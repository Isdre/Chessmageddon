import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import Chess.ChessBoard;

public class GameWindow extends JFrame{
    private JPanel Content;
    private JPanel _chat;
    private JPanel _board;

    public GameWindow(){
        super("TEST_1 Game");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        ChessBoard game = new ChessBoard(_board);
        pack();
        setContentPane(Content);
        setSize(800,600);
        setVisible(true);
    }
}
