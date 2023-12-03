import Chess.ChessBoard;
import chess_server_package.Client;
import chess_server_package.MessType;
import chess_server_package.MyListener;

import javax.swing.*;

public class GameWindow extends JFrame{
    private JPanel Content;
    private JPanel _chat;
    private JPanel _board;
    private Client player;

    public GameWindow(Client client,char color){
        super(client.nick+"'s Game");
        player = client;
        System.out.println(color);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        ChessBoard game = new ChessBoard(_board,color);
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
}
