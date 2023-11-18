package chess_server_package;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class Window implements ActionListener, MyListener {
    private JFrame frame;
    JPanel panel;
    JButton lb;
    JButton rb;
    JButton pl;
    JTextField name;
    JTextField pass;
    JPanel panel1;
    JLabel label;
    Client c;
    public Window() {
        inicjalize();
    }

    public void inicjalize() {
        c = new Client(this);
        Thread t = new Thread(c);
        t.start();
        frame = new JFrame();
        this.frame.setTitle("Button");
        this.frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        this.frame.setSize(400, 300);
        this.frame.setResizable(true);
        this.frame.setLocationRelativeTo(null);

        panel = new JPanel();
        panel.setLayout(new GridLayout(10, 1));
        panel.setBackground(Color.BLACK);

        name = new JTextField();
        name.setColumns(10);
        panel.add(name);

        pass = new JTextField();
        pass.setColumns(10);
        panel.add(pass);

        lb = new JButton("login");
        lb.setBackground(Color.WHITE);
        lb.addActionListener(this);
        panel.add(lb);

        rb = new JButton("register");
        rb.setBackground(Color.WHITE);
        rb.addActionListener(this);
        panel.add(rb);

        pl = new JButton("playersOnline");
        pl.setBackground(Color.WHITE);
        pl.addActionListener(this);
        panel.add(pl);


        panel1 = new JPanel();
        panel1.setLayout(new FlowLayout(FlowLayout.CENTER, 20, 20));
        panel1.setBackground(Color.DARK_GRAY);

        label = new JLabel("");
        label.setForeground(Color.BLACK);
        label.setFont(new Font("Dialog", Font.PLAIN, 20));
        panel1.add(label);

        frame.add(panel, BorderLayout.WEST);
        frame.add(panel1, BorderLayout.CENTER);


        this.frame.setVisible(true);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        try {
            if(e.getSource() == lb) {
                label.setText(String.valueOf(c.login(name.getText(), pass.getText())));
            }
            if(e.getSource() == rb) {
                label.setText(String.valueOf(c.register(name.getText(), pass.getText())));
            }
            if(e.getSource() == pl) {
                System.out.println(c.playersOnline());
            }
        } catch(Exception ex) {
            System.out.println("BRUH");
        }

    }

    public static void main(String[] str) {
        Window w = new Window();
    }

    @Override
    public void performed(String message, MessType type) {
    return;
    }
}
