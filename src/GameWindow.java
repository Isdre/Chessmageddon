import Chess.ChessBoard;
import chess_server_package.Client;
import chess_server_package.MessType;
import chess_server_package.MyListener;
import com.chess.engine.board.Board;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

/**
 * Okno gry
 */
public class GameWindow extends JFrame implements MyListener, ActionListener {
    private GameWindow instance;
    private JPanel Content;
    private JPanel _chat;
    private JPanel _board;
    private JTextField messenger;
    private JTextArea textArea;
    private JButton giveUpButton;
    private Client player;
    private ChessBoard gameBoardFront;

    public GameWindow(Client client,char color){
        super(client.nick+"'s Game");
        instance = this;
        player = client;
        player.listener = this;
        System.out.println(color);
        textArea.setForeground(Color.LIGHT_GRAY);
        messenger.setForeground(Color.LIGHT_GRAY);
        messenger.addActionListener(this);
        giveUpButton.addActionListener(this);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        final Board gameBoardEngine = Board.createStandardBoard();
        gameBoardFront = new ChessBoard(_board,color,gameBoardEngine,player);
        if (color == 'W') gameBoardFront.yourTurn = true;
        pack();
        setContentPane(Content);
        setSize(900,600);
        setVisible(true);

        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                player.whoWin(2);
                player.quit();
                super.windowClosing(e);
            }
        });
    }

    /**
     * pomocnicza. Do sprawdzania wyglądu okna gry
     */
    public static void main(String[] args){
        new GameWindow(new Client(new MyListener() {
            @Override
            public void performed(String message, MessType type) {
                System.out.println("sadsada");
            }
        }), 'W');
    }

    /**
     * Odpowiada za przyjmowanie wiadomości z serwera
     * (przyjmuje ruch przeciwnika, w przypadku końca gry wysyła komunikat do serwera)
     */
    @Override
    public void performed(String message, MessType type) {
        switch (type) {
            case MOVE:
                //System.out.println(message);
                String x = gameBoardFront.opponentMove(message.substring(0,2),message.substring(2));
                if(x == "LOST") {
                    player.whoWin(2);
                }
                else if(x== "DRAW") {
                    player.whoWin(0);
                }
                break;
            case OPPONENT_MESSAGE:
                textArea.append(message+"\n");
                break;
            case GAME_ENDED:
                System.out.println(message);
                //Komunikat końca meczu
                JDialog jd = new JDialog();
                jd.addWindowListener(new WindowAdapter() {
                    @Override
                    public void windowClosed(WindowEvent e) {
                        new UserWindow(player);
                        dispose();
                        instance.dispose();
                        super.windowClosed(e);
                    }
                    @Override
                    public void windowClosing(WindowEvent e) {
                        new UserWindow(player);
                        dispose();
                        instance.dispose();
                        super.windowClosed(e);
                    }
                });
                jd.setPreferredSize(new Dimension(100, 80));
                jd.getContentPane().setLayout(new GridBagLayout());
                String end_mess = "KONIEC! ";
                if (message.equals(player.nick)) end_mess += "WYGRAŁEŚ!";
                else if (message.equals("DRAW")) end_mess += "REMIS!";
                else end_mess += "PRZEGRAŁEŚ!";
                jd.getContentPane().setLayout(new BorderLayout());
                JLabel endLabel = new JLabel(end_mess);
                jd.getContentPane().add(endLabel,BorderLayout.NORTH);
                JButton end = new JButton();
                end.addActionListener(new ActionListener() {
                    public void actionPerformed(ActionEvent e) {
//                        new UserWindow(player);
                        jd.dispose();
                        dispose();
                    }
                } );
                jd.add(end,BorderLayout.SOUTH);
                jd.pack();

                jd.setLocationRelativeTo(Content);
                jd.setVisible(true);
                break;
            default: break;
        }
    }

    /**
     * Wpisywanie tekstu do czatu oraz poddawanie się
     */
    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == giveUpButton) {
            player.whoWin(2);
        }
        else {
            textArea.append(player.nick + ":" + messenger.getText() + "\n");
            player.messageOpponent(messenger.getText());
            messenger.setText("");
        }
    }
}
