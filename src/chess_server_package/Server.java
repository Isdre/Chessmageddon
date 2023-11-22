package chess_server_package;

import java.net.*;
import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Objects;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.sql.*;

/**
 * Klasa serwer obsługuje stawianie serwera, komunikację z użytkownikiem, tworzenie oraz łączenie użytkowników w grach.
 */
public class Server implements Runnable {
    private final ArrayList<ConnectionHandler> connections;
    private ServerSocket server;
    private boolean done;
    private ExecutorService pool;
    private final ArrayList<Game> games = new ArrayList<>();
    private DataBaseConnection database;

    /**
     * tworzy obiekt klasy chess_server_package.Server.
     */
    public Server() {
        connections = new ArrayList<>();
        done = false;
    }

    /**
     * Funkcja run() ustawia parametry serwera oraz go uruchamia.
     */
    @Override
    public void run() {
        try {
            database = new DataBaseConnection();
            server = new ServerSocket(9999);
            pool = Executors.newCachedThreadPool();
            System.out.println("chess_server_package.Server is running");
            while(!done) {
                Socket client = server.accept();
                ConnectionHandler handler = new ConnectionHandler(client);
                connections.add(handler);
                pool.execute(handler);
            }
        } catch (IOException e) {
            shutdown();
        }
    }
    private void broadcast(String message) {
        for(ConnectionHandler ch : connections) {
            if(ch != null) {
                ch.systemMessage(message);
            }
        }
    }
    private void shutdown() {
        try {
            done = true;
            if(!server.isClosed()) {
                server.close();
            }
            for(ConnectionHandler ch : connections) {
                ch.shutdown();
            }
        } catch(IOException e) {
            System.out.println(e.getMessage());
        }
    }

    private ConnectionHandler findUser(String nickname) {
        for(ConnectionHandler ch : connections) {
            if(Objects.equals(ch.nickname, nickname)) {
                return ch;
            }
        }
        System.out.println("Nie znaleziono uzytkownika: " + nickname);
        return null;
    }

    /**
     * Klasa ConnectionHandler obsługuje połączenie serwera z konkretnym użytkownikiem.
     */
    public class ConnectionHandler implements Runnable {
        /**
         * Obsługuje połączenie serwera z użytkonikiem
         */
        private final Socket client;
        /**
         * obsługuje wiadomości przychodzące od użytkownika
         */
        private BufferedReader in;
        /**
         * obsługuje wysyłanie wiadomości do użytkownika.
         */
        private PrintWriter out;
        /**
         * nazwa użytkownika
         */
        public String nickname = "BRAK IMIENIA";
        /**
         * przechowuje informacje o grze użytkownika
         */
        private Game game = null;
        /**
         * jeśli gracz zostanie zaproszony do gry, gracz zapraszający zostanie tu zapisany
         */
        private String inviter = null;

        /**
         * Tworzy obiekt klasy ConnectionHandler
         * @param client przyjmuje socket użytkownika.
         */
        public ConnectionHandler(Socket client) {
            this.client = client;
        }

        /**
         * Obsługuje początek połączenia, logowanie lub rejestrację.
         * @throws IOException wyjątek.
         */
        public void beginConnection() throws IOException {
            String inMessage;
            while((inMessage = in.readLine()) != null) {
                System.out.println(inMessage);
                if(inMessage.startsWith("/login")) {
                    String[] tmp = inMessage.split(" ");
                    String name = tmp[1];
                    String password = tmp[2];
                    boolean right = login(name, password);
                    if(right) {
                        sendMessage("Right");
                        break;
                    }
                    else {
                        sendMessage("Wrong");
                    }
                }
                else if(inMessage.startsWith("/register")) {
                    String[] tmp = inMessage.split(" ");
                    String name = tmp[1];
                    String password = tmp[2];
                    boolean right = register(name, password);
                    if (right) {
                        sendMessage("Right");
                        break;
                    }
                    else {
                        sendMessage("Wrong");
                    }
                }
            }
            if(in.readLine() == null) {
                return;
            }
            System.out.println(nickname + " connected!    Online: " + connections.size());
            broadcast(nickname + " joined!");
//            systemMessage("Please enter your nickname: ");
//            String supposedName = in.readLine();
//            while(findUser(supposedName) != null) {
//                systemMessage("Please choose another nickname: ");
//                supposedName = in.readLine();
//            }
//            nickname = supposedName;
//            System.out.println(nickname + " connected!    Online: " + connections.size());
//            broadcast(nickname + " joined!");
        }

        /**
         * Obsługuje rejestrację użytkownika.
         * @param n nazwa
         * @param p hasło
         * @return czy udało się zarejestrować
         */
        public boolean register(String n, String p) {
            if(database.correctRegister(n, p)) {
                nickname = n;
                return true;
            }
            return false;

        }

        /**
         * Obsługuje logowanie użytkownika.
         * @param n nazwa
         * @param p hasło
         * @return czy udalo się zalogować
         */
        public boolean login(String n, String p) {
            if(database.correctLogin(n, p)) {
                for(ConnectionHandler c : connections) {
                    if(c.nickname.equals(n)) {
                        System.out.println("Already logged in");
                        return false;
                    }
                }
                nickname = n;
                return true;
            }
            return false;

        }


//        /**
//         * Obsługuje zmianę hasła.
//         * @param newPassword nowe hasło
//         */
//        public void changePassword(String newPassword) {
//            return;
//            //TOD changePassword
//        }
//
//        /**
//         * Obsługuje zmianę nazwy.
//         * @param newName nowa nazwa
//         */
//        public void changeName(String newName) {
//            return;
//            //TOD changeName
//        }

        /**
         * uruchamia ConnectionHanler-a.
         */
        @Override
        public void run() {
            try {
                out = new PrintWriter(client.getOutputStream(), true);
                in = new BufferedReader(new InputStreamReader(client.getInputStream()));
                beginConnection();
                String message;
                while((message = in.readLine()) != null) {
                    if(message.startsWith("/quit")) {
                        String leftName = nickname;
                        broadcast(nickname + " left");
                        shutdown();
                        System.out.println(leftName + " left.    Online: " + connections.size());
                        break;
                    }
                    else if(game == null) {
                        outsideGame(message);
                    }
                    else {
                        inGame(message);
                    }
//                    if(message.startsWith("/nick")) {
//                        String[] messageSplit = message.split(" ", 2);
//                        if(messageSplit.length == 2) {
//                            System.out.println("SERVER: " + nickname + " changed nickname to " + messageSplit[1]);
//                            out.println("SERVER: Successfully changed nickname to: " + messageSplit[1]);
//                            broadcast("SERVER: " + nickname + " changed nickname to " + messageSplit[1]);
//                            nickname = messageSplit[1];
//                        }
//                        else {
//                            out.println("SERVER: No nickname provided.");
//                        }
//                    }
//                    else if(message.startsWith("/quit")) {
//                        String leftName = nickname;
//                        broadcast("SERVER: " + nickname + " left");
//                        shutdown();
//                        System.out.println("SERVER: " + leftName + " left.    Online: " + connections.size());
//                        break;
//                    }
//                    else {
//                        broadcast(nickname + ": " + message);
//                    }
                }
            } catch(IOException e) {
                shutdown();
            }
        }

        /**
         * obsługuje polecenia poza grą.
         * @param message polecenie.
         */
        public void outsideGame(String message) {
            System.out.println(message);
            if(message.startsWith("/playWith")) {
                playWith(extractName(message));
            }
            else if(message.startsWith("/playersOnline")) {
                playersOnline();
            }
            else if(message.startsWith("/confirm")) {
                confirm();
            }
            else if(message.startsWith("/reject")) {
                reject();
            }
            else if(message.startsWith("/getPlayerGames")) {
                sendPlayerGamesMessage(nickname);
            }
            else if(message.startsWith("/getAllPlayersStats")) {
                sendAllPlayersStatistics();
            }
            else if(message.startsWith("/getPlayerStats")) {
                sendPlayerStatistics(nickname);
            }
//            else if(message.startsWith("/getGame")) {
//                getGame();
//            }
        }

        /**
         * Obsługuje polecenia w grze.
         * @param message polecenie
         */
        public void inGame(String message) {
            if(message.startsWith("M")) {
                messageOpponent(message);
            }
            else if (message.startsWith("X")) {
                makeMove(message);
            }
            else if(message.startsWith("E")) {
                endGame(1);
            }
        }

        /**
         * Konczy gre. Jesli 1 to gracz 1, 2 to gracz 2, 0 to remis.
         * @param winner kto wygral
         */
        public void endGame(int winner) {
            Game tmp = new Game(game.players[0], game.players[1]);
            if(winner == 1) {
                tmp.players[0].systemMessage("You won");
                tmp.players[1].systemMessage("You lost");
            }
            else if(winner == 2) {
                tmp.players[0].systemMessage("You lost");
                tmp.players[1].systemMessage("You won");
            }
            else if(winner == 0) {
                tmp.players[0].systemMessage("Draw");
                tmp.players[1].systemMessage("Draw");
            }
            tmp.players[0].systemMessage("endOfGame");
            tmp.players[0].game = null;
            tmp.players[1].systemMessage("endOfGame");
            tmp.players[1].game = null;
            Server.this.games.remove(this);
        }

        /**
         * Wysyła do przeciwnika. Nie zaleca się używać.
         * @param message wiadomość
         */
        public void sendToOpponent(String message) {
            if (game.players[0] == this) {
                game.players[1].sendMessage(message);
            } else {
                game.players[0].sendMessage(message);
            }
        }

        /**
         * Wysyła wiadomość do przeciwnika.
         * @param message wiadomość
         */
        public void messageOpponent(String message) {
            sendToOpponent("M" + nickname + ": " + message.substring(1));
        }

        /**
         * obsługuje przesyłanie ruchów pomiędzy graczami.
         * @param move ruch.
         */
        public void makeMove(String move) {
            System.out.println("(" + nickname + ") Move: " + move);
            sendToOpponent(move);
        }

        /**
         * Pomocnicza funkcja do poleceń tekstowych.
         * @param message polecenie
         * @return nazwa gracza
         */
        public String extractName(String message) {
            String[] messageSplit = message.split(" ", 2);
            if(messageSplit.length >= 2) {
                return messageSplit[1];
            }
            return null;
        }

        /**
         * Obsługuje połączenie się z innym użytkownikiem w grze.
         * @param nick nazwa gracza z którym gramy.
         */
        public void playWith(String nick) {
            if(nick != null && findUser(nick) != null) {
                ConnectionHandler ch = findUser(nick);
                if(Objects.equals(ch.nickname, nickname)) {
                    systemMessage("Wanna play with yourself ?!");
                    return;
                }
                if(ch.game == null) {
                    ch.systemMessage("Wanna play with: " + nickname + "?");
                    ch.inviter = nickname;
                    ch.inviteMessage(nickname);
                }
                else { systemMessage("Player already in another game"); }
            }
            else { systemMessage("There is no such player."); }
        }

        /**
         * Wysyła do użytkownika informację o grazcach Online.
         */
        public void playersOnline() {
            String tmp = "";
            for(ConnectionHandler ch : connections) {
                tmp += ch.nickname + (ch.game == null ? "" : " inGame") + ",";
            }
            sendMessage("P" + tmp);
        }

        /**
         * Obsługuje potwierdzenie zaproszenia.
         */
        public void confirm() {
            if(inviter != null) {
                ConnectionHandler ch = findUser(inviter);
                if(ch.game != null) {
                    systemMessage("someone else accepted earlier");
                    inviter=null;
                    return;
                }
                game = new Game(this, ch);
                ch.game = game;
                System.out.println("New game: " + game.players[0].nickname + " " + game.players[1].nickname);
                systemMessage("In game with: " + game.players[1].nickname);
                ch.systemMessage("In game with: " + game.players[0].nickname);
                ch.confirmMessage(inviter);
                games.add(game); //na koniec if-a
            }
        }

        /**
         * obsługuje odrzucenie zaproszenia.
         */
        public void reject() {
            if(inviter != null) {
                ConnectionHandler ch = findUser(inviter);
                ch.systemMessage("Invitation rejected");
                ch.rejectMessage("unimportant");
                inviter = null;
            }
        }
//        public void getGame() {
//            System.out.println("Tak");
//            ArrayList<ArrayList<String>> tmp = new ArrayList<>();
//            tmp.add(new ArrayList<String>());
//            tmp.add(new ArrayList<String>());
//            tmp.add(new ArrayList<String>());
//            tmp.get(0).add("uno");
//            tmp.get(0).add("duo");
//            tmp.get(1).add("ter");
//            tmp.get(1).add("kwar");
//            tmp.get(2).add(("kwin"));
//            tmp.get(2).add("sek");
//            StringBuilder str = new StringBuilder();
//            for (ArrayList<String> x: tmp) {
//                for (String y: x) {
//                    str.append(y).append(",");
//                }
//                str.append("]");
//            }
//            System.out.println(str);
//            systemMessage("|" + str);
//        }

        /**
         * wysyła wiadomość do użytkownika
         * @param message wiadomość
         */
        public void sendMessage(String message) {
            out.println(message);
        }

        /**
         * wysyła wiadomość systemową do użytkownika
         * @param message wiadomość
         */
        public void systemMessage(String message) {
            sendMessage("S" + message);
        }

        /**
         * Wysyła wiadomość potwierdzającą zaproszenie do użytkownika
         * @param message wiadomość
         */
        public void confirmMessage(String message) {
            sendMessage("C" + message);
        }
        /**
         * Wysyła wiadomość odrzucającą zaproszenie do użytkownika
         * @param message wiadomość
         */
        public void rejectMessage(String message) {sendMessage("R" + message);}
        public void inviteMessage(String nick) {sendMessage("I" + nick);}
        /**
         * Wysyła do użytkownika informacje o partiach gracza.
         * @param name imię gracza
         */
        public void sendPlayerGamesMessage(String name) {
            String games = database.getPlayerGamesMessage(name);
            sendMessage("P" + games);
            return;
        }

        /**
         * Wysyła do użytkownika informacje o statystykach wszystkich graczy.
         */
        public void sendAllPlayersStatistics() {
            sendMessage("P" + database.getAllPlayersStatistics());
            return;
        }

        /**
         * Wysyła do użytkownika informacje o statystykach gracza.
         * @param name nazwa gracza.
         */
        public void sendPlayerStatistics(String name) {
            sendMessage("P" + database.getPlayerStatistics(nickname));
            return;
        }

        /**
         * zamyka połączenie z serwerem.
         */

        public void shutdown() {
            try {
                in.close();
                out.close();
                if(!client.isClosed()) {
                    client.close();
                }
                connections.remove(this);
            } catch (IOException e) {
                System.out.println(e.getMessage());
            }
        }
    }

    /**
     * Przechowuje informacje o grze.
     */
    private class Game implements Serializable{
        ConnectionHandler[] players = new ConnectionHandler[2];
        Game(ConnectionHandler ch1, ConnectionHandler ch2) {
            players[0] = ch1;
            players[1] = ch2;
        }
    }

    /**
     * uruchamia serwer.
     * @param args .
     */
    public static void main(String[] args) {
        Server server = new Server();
        server.run();
    }
}
