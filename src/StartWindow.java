import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class StartWindow extends JFrame {
    private JPanel Background;
    private JPanel Start;
    private JPanel Register;
    private JPanel Login;
    private JPasswordField _login;
    private JPasswordField _username;
    private JPasswordField _passwordSign;
    private JPasswordField _passwordLog;
    private JCheckBox _remember;
    private JButton _buttonLog;
    private JButton _buttonSign;
    private JLabel TEST_1;

    public StartWindow() {
        super("TEST_1 Log In");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setContentPane(Start);
        setSize(600,400);
        setVisible(true);
        _buttonLog.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                loginPerformed();
                new GameWindow();
                dispose();
            }
        });
    }
    public void loginPerformed() {

    }
}
