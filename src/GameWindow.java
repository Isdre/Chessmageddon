import Chess.ChessBoard;
import chess_server_package.Client;
import chess_server_package.MessType;
import chess_server_package.MyListener;
import com.chess.engine.board.Board;

import javax.swing.*;

public class GameWindow extends JFrame implements MyListener{
    private JPanel Content;
    private JPanel _chat;
    private JPanel _board;
    private JTextField messenger;
    private JTextArea textArea;
    private Client player;
    private ChessBoard gameBoardFront;

    public GameWindow(Client client,char color){
        super(client.nick+"'s Game");
        player = client;
        player.listener = this;
        System.out.println(color);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        final Board gameBoardEngine = Board.createStandardBoard();
        gameBoardFront = new ChessBoard(_board,color,gameBoardEngine,player);
        if (color == 'W') gameBoardFront.yourTurn = true;
        pack();
        setContentPane(Content);
        setSize(800,600);
        setVisible(true);
    }

    public static void main(String[] args){
        new GameWindow(new Client(new MyListener() {
            @Override
            public void performed(String message, MessType type) {
                System.out.println("sadsada");
            }
        }), 'W');
    }

    @Override
    public void performed(String message, MessType type) {
        switch (type) {
            case MOVE:
                //System.out.println(message);
                String x = gameBoardFront.opponentMove(message.substring(0,2),message.substring(2));
                if(x == "LOST") player.whoWin(2);
                else if(x== "DRAW") player.whoWin(0);
                break;
            case OPPONENT_MESSAGE:
                System.out.println(message);
                break;
            case GAME_ENDED:
                System.out.println(message);
                break;
            default: break;
        }
    }
}
