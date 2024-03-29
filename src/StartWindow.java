import chess_server_package.Client;
import chess_server_package.MessType;
import chess_server_package.MyListener;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.IOException;

/**
 * Okno logowania
 */
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
    private JLabel label;
    private JLabel _loginError;
    private JLabel _signError;

    public StartWindow() {
        super("Chessmageddon");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setContentPane(Start);
        setSize(600,400);
        setVisible(true);
        player = new Client(this);
        _buttonLog.addActionListener(this);
        _buttonSign.addActionListener(this);
        Thread t = new Thread(player);
        t.start();
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                player.quit();
                super.windowClosing(e);
            }
        });
    }

    /**
     * Wykonuje się, gdy użytkownik spróbuje się zalogować lub zarejestrować
     * Wyświetla komunikat błędu dla logowania oraz rejestracji
     */
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

    /**
     * Logowanie gracza
     * @return czy się powiodło
     */
    public boolean loginPerformed() {
        try {
            return  player.login(String.valueOf(_login.getPassword()), String.valueOf(_passwordLog.getPassword()));
        } catch (IOException ex) {
            throw new RuntimeException(ex);
        }
    }

    /**
     * Rejestracja gracza
     * @return czy się powiodło
     */
    public boolean signPerformed() {
        try {
            if (String.valueOf(_username.getPassword()).equals("") || String.valueOf(_passwordSign.getPassword()).equals("")) return false;
            return  player.register(String.valueOf(_username.getPassword()), String.valueOf(_passwordSign.getPassword()));
        } catch (IOException ex) {
            throw new RuntimeException(ex);
        }
    }

    public void performed(String message, MessType type){};
}
