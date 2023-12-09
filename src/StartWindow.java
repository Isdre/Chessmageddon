import chess_server_package.Client;
import chess_server_package.MessType;
import chess_server_package.MyListener;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;


public class StartWindow extends JFrame implements ActionListener, MyListener {
    private Client player;
    private JPanel Background;
    private JPanel Start;
    private JPanel Register;
    private JPanel Login;
    //Log In
    private JPasswordField _login;
    private JPasswordField _passwordLog;
    //Sign In
    private JPasswordField _username;
    private JPasswordField _passwordSign;
    private JButton _buttonLog;
    private JButton _buttonSign;
    private JLabel TEST_1;
    private JLabel _loginError;
    private JLabel _signError;

    public StartWindow() {
        super("TEST_2");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setContentPane(Start);
        setSize(600,400);
        setVisible(true);
        player = new Client(this);
        _buttonLog.addActionListener(this);
        _buttonSign.addActionListener(this);
        Thread t = new Thread(player);
        t.start();
    }

    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == _buttonLog){
            if (!loginPerformed()) {
                _loginError.setText("Incorrect login or password");
                return;
            }
        } else if (e.getSource() == _buttonSign) {
            if (!signPerformed()) {
                _signError.setText("Username and password can't be empty");
                return;
            }
        }
        new UserWindow(player);
        dispose();
    }

    public boolean loginPerformed() {
        try {
            return  player.login(String.valueOf(_login.getPassword()), String.valueOf(_passwordLog.getPassword()));
        } catch (IOException ex) {
            throw new RuntimeException(ex);
        }
    }

    public boolean signPerformed() {
        try {
            if (String.valueOf(_username.getPassword()) == "" || String.valueOf(_passwordSign.getPassword()) == "") return false;
            return  player.register(String.valueOf(_username.getPassword()), String.valueOf(_passwordSign.getPassword()));
        } catch (IOException ex) {
            throw new RuntimeException(ex);
        }
    }

    public void performed(String message, MessType type){};
}
