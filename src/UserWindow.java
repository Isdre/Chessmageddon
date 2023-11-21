import chess_server_package.Client;

import javax.swing.*;

public class UserWindow extends JFrame{
    private Client player;
    private JPanel Content;
    private JTextField textField1;
    private JButton button1;
    private JPanel test;
    private JButton button2;
    private JButton button3;
    private JPanel UserInterface;

    public UserWindow(Client client){
        super("TEST_1 Game");
        player = client;
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        pack();
        setContentPane(Content);
        setSize(800,600);
        setVisible(true);
    }
}
