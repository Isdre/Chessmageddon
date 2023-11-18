package chess_server_package;

import java.net.*;
import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Objects;

// TODO: other
//uncomment message receivers,
// uncomment main,
// ustawienie początkowe szachów

/**
 * Klasa chess_server_package.Client obsługuje połączenie z serverem, wysyłanie i otrzymywanie wiadomości
 * Zainicjuj obiekt "chess_server_package.Client(MESSAGE_RECEIVER)" oraz wykonaj metode {@link #run()}.
 * "MESSAGE_RECEIVER" to twoj obiekt odbierajacy wiadomosci. Musi implementować {@link MyListener}
 * i zawierac {@link MyListener#performed(String, MessType)} ktory przujmuje wiadomosc i jej typ.
 * Zobacz rowniez: {@link MessType}.
 * Przykladowe uzycie: {@link Test}
 */
public class Client implements Runnable {
    private Socket client;
    private BufferedReader in;
    private PrintWriter out;
    private  boolean done = false;
    private String board = " "; //-----------------------------------------ustawienie poczatkowe szachow
    private final MyListener listener;
    private boolean loggedin;
    private boolean transfer;
    private String messageContainer = "";

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

    public void shutdown() {
        done = true;
        try {
            in.close();
            out.close();
            if(!client.isClosed()) {
                client.close();
            }
        } catch (IOException e) {
            //ignore
        }
    }

//    class InputHandler implements Runnable {
//        @Override
//        public void run() {
//            while (!done) {
//                while (input == null) {
//                    Thread.onSpinWait();
//                }
//                String message = input;
//                input = null;
//                if(message.equals("/quit")) {
//                    out.println(message);
//                    shutdown();
//                    break;
//                }
//                else {
//                    out.println(message);
//                }
//            }
//        }
//    }

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

//    public static void main(String[] args) {
//        chess_server_package.Client client = new chess_server_package.Client();
//        client.run();
//    }


    /**
     * Wysyla wiadomosc na serwer. Nie uzywac jesli mozesz skorzystac z: <p>
     * {@link #playWith(String)},
     * {@link #playersOnline()},
     * {@link #confirm()},
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

//            InputHandler inHandler = new InputHandler();
//            Thread t = new Thread(inHandler);
//            t.start();

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
                else {
                    System.out.println("UNHANDLED: " + inMessage);
                }

            }
        } catch (IOException e) {
            // unlucky
        }
    }

    /**
     * Kończy połączenie z serwerem.
     */
    public void quit() {
        send("/quit");
        shutdown();
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
//        System.out.println("System: " + message);
        listener.performed(message, MessType.SYSTEM_MESSAGE);
    }

    /**
     * przyjmuje wiadomosc od przeciwnika. Tylko podczas gry (mam nadzieje)
     * @param message wiadomosc
     */
    private void receiveOpponentsMessage(String message) {
//        System.out.println("Message: " + message);
        listener.performed(message, MessType.OPPONENT_MESSAGE);
    }

    /**
     * przyjmuje ruch
     * @param move ruch
     */
    private void receiveMove(String move) {
        board = move;
//        System.out.println("Move: " + board);
        listener.performed(board, MessType.MOVE);

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
     * @throws IOException wyjątek
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
     */
    public void confirm() {
        send("/confirm");
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
        System.out.println(tmp);
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
    public ArrayList<String> getPlayerStatistics() {
        send("/getPlayerStats");
        while(!transfer) {
            System.out.print("");
        }
        transfer=false;
        String[] tmp = messageContainer.substring(1).split(",");
        ArrayList<String> stats = new ArrayList<>();
        stats.addAll(Arrays.asList(tmp));
        return stats;
    }

    /**
     * Obsługuje logowanie się użytkownika.
     * @param name nazwa
     * @param password hasło
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
            System.out.println("loging in: " + inMessage);
        }
        assert inMessage != null;
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
        return inMessage.equals("Right");
    }

//    /**
//     * Obsługuje zmianę hasła użytkownika
//     * @param name nazwa
//     * @param oldPassword stare hasło
//     * @param newPassword nowe hasło
//     */
//    public void changePassword(String name, String oldPassword, String newPassword) {
//        //TOD changePassword
//    }
//
//    /**
//     * Obsługuje zmianę nazwy użytkownika
//     * @param newName nowa nazwa
//     */
//    public void changeName(String newName) {
//        //TOD changeName
//    }


//    public void getGame() {
//        send("/getGame");
//            String inMessage = "";
//            try {
//                while((inMessage = in.readLine()) != null && !inMessage.startsWith("S|")) {
//                    System.out.println("?? " + inMessage);
//                }
//                System.out.println(inMessage);
//                String s = inMessage.substring(2);
//                ArrayList<String> s1 = new ArrayList<>(Arrays.asList(s.split("]")));
//                System.out.println(s1);
//                ArrayList<ArrayList<String>> s2 = new ArrayList<>();
//                for(String x: s1) {
//                    s2.add(new ArrayList<String>(Arrays.asList(x.split(","))));
//                }
//                System.out.println(s2);
//            } catch (Exception e) {
//
//            }
//    }
}
