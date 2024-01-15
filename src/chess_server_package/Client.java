package chess_server_package;

import java.net.*;
import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Objects;

/**
 * Klasa chess_server_package.Client obsługuje połączenie z serverem, wysyłanie i otrzymywanie wiadomości
 * Zainicjuj obiekt "chess_server_package.Client(MESSAGE_RECEIVER)" oraz wykonaj metode {@link #run()}.
 * "MESSAGE_RECEIVER" to twoj obiekt odbierajacy wiadomosci. Musi implementować {@link MyListener}
 * i zawierac {@link MyListener#performed(String, MessType)} ktory przujmuje wiadomosc i jej typ.
 * Zobacz rowniez: {@link MessType}.
 */
public class Client implements Runnable {
    private Socket client;
    public BufferedReader in;
    public PrintWriter out;
    private  boolean done = false;
    private String board = " "; 
    /**
     * aplikacja słuchająca, implementująca {@link MyListener}
     */
    public MyListener listener;
    private boolean loggedin;
    private boolean transfer;
    private String messageContainer = "";
    public String nick;
    /**
     * Informuje czy podłączono się do serwera
     * @return zwraca true jeśli podłączono i false w przeciwnym wypadku
     */
    public boolean outExist() {
        return out != null;
    }

    /**
     * Tworzy objekt typu {@link Client}
     * @param obj obiekt nasłuchujący wiadomości z serwera.
     */
    public Client(MyListener obj) {
        listener = obj;
    }

    /**
     * zamyka połączenie z serverem
     */
    public void shutdown() {
        done = true;
        try {
            in.close();
            out.close();
            if(!client.isClosed()) {
                client.close();
            }
        } catch (IOException e) {
            
        }
    }


    /**
     * Klasa TestInput obsługuje wejście z konsoli. Służy do testowania programu z poziomu konsoli. Nie należy używać w końcowym programie.
     */
    class TestInput implements Runnable {
        /**
         * Implementuje metodę run() z {@link Runnable}
         */

        @Override
        public void run() {
            try {
                BufferedReader inReader = new BufferedReader(new InputStreamReader(System.in));
                while (true) {
                    String tmp = inReader.readLine();
                    if(tmp.equals("/quit")) {
                        inReader.close();
                        send(tmp);
                        break;
                    }
                    send(tmp);
                }

            } catch (IOException e) {
                shutdown();
            }
        }
    }


    /**
     * Wysyla wiadomosc na serwer. Nie uzywac jesli mozesz skorzystac z: <p>
     * {@link #playWith(String)},
     * {@link #playersOnline()},
     * {@link #confirm(char)},
     * {@link #reject()},
     * {@link #messageOpponent(String)},
     * {@link #makeMove(String)}
     * @param message wiadomosc
     */
    public void send(String message) {
        if(message.equals("/quit")) {
            out.println(message);
            shutdown();
        }
        else {
            out.println(message);
        }
    }

    /**
     * Łączy z serwerem oraz tworzy wątki do obsługi konsoli. Przyjmuje dane z serwera. Implementuje metodę run z Runnable.
     */
    public void run() {
        try {
            client = new Socket("127.0.0.1", 9999);
            out = new PrintWriter(client.getOutputStream(), true);
            in = new BufferedReader(new InputStreamReader(client.getInputStream()));

            TestInput testInput = new TestInput();
            Thread t1 = new Thread(testInput);
            t1.start();

            String inMessage;
            while(!loggedin) {
                System.out.print("");
            }

            while((inMessage = in.readLine()) != null) {
                messageContainer = inMessage;
                if(inMessage.startsWith("S")) {
                    receiveSystemMessage(inMessage.substring(1));
                }
                else if (inMessage.startsWith("X")) {
                    receiveMove(inMessage.substring(1));
                }
                else if (inMessage.startsWith("M")) {
                    receiveOpponentsMessage(inMessage.substring(1));
                }
                else if(inMessage.startsWith("C")) {
                    confirmed(inMessage.substring(1));
                }
                else if(inMessage.startsWith("R")) {
                    rejected(inMessage.substring(1));
                }
                else if(inMessage.startsWith("P")) {
                    transfer = true;
                }
                else if(inMessage.startsWith("I")) {
                    invited(inMessage.substring(1));
                }
                else if(inMessage.startsWith("E")) {
                    endGame(inMessage.substring(1));
                }
                else {
                    System.out.println("UNHANDLED: " + inMessage);
                }

            }
        } catch (IOException e) {
            System.out.println("Brak polaczenia");
        }
    }

    /**
     * Kończy połączenie z serwerem.
     */
    public void quit() {
        try {
            send("/quit");
            shutdown();
        } catch (Exception e) {

        }

    }

    private void confirmed(String message) {
        listener.performed(message, MessType.CONFIRM);
    }
    private void rejected(String message) {
        listener.performed(message, MessType.REJECT);
    }

    /**
     * Przyjmuje wiadomosc systemowa
     * @param message wiadomosc
     */
    private void receiveSystemMessage(String message) {

        listener.performed(message, MessType.SYSTEM_MESSAGE);
    }

    /**
     * przyjmuje wiadomosc od przeciwnika. Tylko podczas gry (mam nadzieje)
     * @param message wiadomosc
     */
    private void receiveOpponentsMessage(String message) {

        listener.performed(message, MessType.OPPONENT_MESSAGE);
    }

    /**
     * przyjmuje ruch
     * @param move ruch
     */
    private void receiveMove(String move) {
        board = move;

        listener.performed(board, MessType.MOVE);

    }
    private void invited(String nick) {
        listener.performed(nick, MessType.INVITED);
    }
    private void endGame(String winner) {
        listener.performed(winner, MessType.GAME_ENDED);
    }

    /**
     * Wysyła zapytanie przez serwer do gracza o nazwie nick czy chce grać.
     * @param nick nazwa gracza
     */
    public void playWith(String nick) {
        send("/playWith " + nick);
    }

    /**
     * Wysyła zapytanie do serwera o listę aktywnych graczy
     * @return tablica graczy (jesli w grze, z dopiskiem inGame)
     */
    public ArrayList<String> playersOnline() {
        send("/playersOnline");
        while(!transfer) {
            System.out.print("");
        }
        transfer=false;
        ArrayList<String> players = new ArrayList<>();
        String[] tmp1 = messageContainer.substring(1).split(",");
        players.addAll(Arrays.asList(tmp1));
        return players;
    }

    /**
     * Jesli gracz zostal zaproszony, informuje serwer, że przyjmuje zaproszenie tym samym dolaczajac siebie i drugiego gracza do gry. Nie wykonuj akcji w innym wypadku.
     * @param color kolor
     */
    public void confirm(char color) {
        send("/confirm " + color);
    }

    /**
     * Informuje serwer, że odrzuca zaproszenie, jesli zaproszony. Nie wykonuje akcji w innym wypadku.
     */
    public void reject() {
        send("/reject");
    }

    /**
     * Wysyła oponentowi wiadomosc. Dziala tylko podczas gry.
     * @param message wiadomość
     */
    public void messageOpponent(String message) {
        send("M" + message);
    }

    /**
     * Wysyla ruch gracza do przeciwnika.
     * @param move String zawierająy planszę.
     */
    public void makeMove(String move) {
        board = move;
        send("X" + board);
    }

    /**
     * Wysyła wiadomość o końcu gry i zwycięztwie.
     * @param winner zwycięzca: jeśli 1 to ty, jeśli 2 to przeciwnik, jeśli 0 to remis
     */
    public void whoWin(int winner) {
        send("E" + winner);
    }

    /**
     * Wysyła zapytanie do serwera o listę gier gracza. Zwraca listę gier gracza w kolejności chronologicznej w postaci listy list.
     * Kolumny: data, przeciwnik, zwycięzca
     * @return lista gier gracza
     */
    public ArrayList<ArrayList<String>> getPlayerGames() {
        send("/getPlayerGames");
        while(!transfer) {
            System.out.print("");
        }
        transfer=false;
        String[] tmp = messageContainer.substring(1).split("/");
        ArrayList<ArrayList<String>> games = new ArrayList<>();
        for(String i : tmp) {
            ArrayList<String> tmp2 = new ArrayList<>();
            tmp2.addAll(Arrays.asList(i.split(",")));
            games.add(tmp2);
        }
        return games;
    }

    /**
     * Wysyła zapytanie do serwera o statystyki wszystkich graczy. Zwraca statystyki wszystkich graczy w postaci listy list.
     * Kolumny: liczba gier, liczba wygranych, liczba przegranych, liczba remisów, data utworzenia konta.
     * @return lista list statystyk wszystkich graczy.
     */
    public ArrayList<ArrayList<String>> getAllPlayersStatistics() {
        send("/getAllPlayersStats");
        while(!transfer) {
            System.out.print("");
        }
        transfer=false;
        String[] tmp = messageContainer.substring(1).split("/");
        ArrayList<ArrayList<String>> stats = new ArrayList<>();
        for(String i : tmp) {
            ArrayList<String> tmp2 = new ArrayList<>();
            tmp2.addAll(Arrays.asList(i.split(",")));
            stats.add(tmp2);
        }
        return stats;
    }

    /**
     * Wysyła zapytanie do serwera o statystyki gracza. Zwraca statyst gracza w postaci listy.
     * Kolumny: liczba gier, liczba wygranych, liczba przegranych, liczba remisów, data utworzenia konta.
     * @return lista statystyk gracza.
     */
    public ArrayList<ArrayList<String>> getPlayerStatistics() {
        send("/getPlayerStats");
        while(!transfer) {
            System.out.print("");
        }
        transfer=false;
        String[] tmp = messageContainer.substring(1).split("/");
        ArrayList<ArrayList<String>> stats = new ArrayList<>();
        for(String i : tmp) {
            ArrayList<String> tmp2 = new ArrayList<>();
            tmp2.addAll(Arrays.asList(i.split(",")));
            stats.add(tmp2);
        }
        return stats;
    }

    /**
     * Obsługuje logowanie się użytkownika.
     * @param name nazwa
     * @param password hasło
     * @throws IOException wyjątek
     * @return czy zalogowany
     */
    public boolean login(String name, String password) throws IOException {
        if(Objects.equals(name, "") || Objects.equals(password, "")) {
            return false;
        }
        String tmp = "/login " + name + " " + password;
        send(tmp);
        String inMessage;
        while((inMessage = in.readLine()) != null && !inMessage.equals("Wrong") && !inMessage.equals("Right")) {
            System.out.print("");
        }
        assert inMessage != null;
        nick = name;
        if(inMessage.equals("Right")) {
            loggedin = true;
            return true;
        }
        else {
            return false;
        }
    }

    /**
     * Obsługuje rejestrację użytkownika.
     *
     * @param name     nazwa
     * @param password hasło
     * @throws IOException wyjątek
     * @return czy się powiodło
     */
    public boolean register(String name, String password) throws IOException {
        if(Objects.equals(name, "") || Objects.equals(password, "")) {
            return false;
        }
        String tmp = "/register " + name + " " + password;
        send(tmp);
        String inMessage;
        while((inMessage = in.readLine()) != null && !inMessage.startsWith("Right") && !inMessage.equals("Wrong")) {
            System.out.println("registering in: " + inMessage);
        }
        assert inMessage != null;
        nick = name;
        return inMessage.equals("Right");
    }
}
