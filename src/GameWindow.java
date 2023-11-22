import Chess.ChessBoard;

import javax.swing.*;

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

    public static void main(String[] args){
        new GameWindow();
    }
}
