import chess_server_package.Client;
import chess_server_package.MessType;
import chess_server_package.MyListener;

import javax.swing.*;
import javax.swing.border.EtchedBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class UserWindow extends JFrame implements MyListener {
    private Client player;
    private JPanel Content;
    private JPanel UserInterface;
    private JTextField _nickOpponent;
    private JButton _invite;
    private JPanel inv;
    private JButton _acceptInv;
    private JButton _rejectInv;
    private JLabel _nickInv;

    public UserWindow(Client client){
        super("TEST_1 Game");
        player = client;
        player.listener = this;
        UserInterface.setBorder(BorderFactory.createTitledBorder(BorderFactory.createEtchedBorder(EtchedBorder.RAISED,Color.BLACK,Color.BLACK), player.nick));
        _invite.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                if (_nickOpponent.getText() == "") return;
                player.playWith(_nickOpponent.getText());
            }
        } );
        _acceptInv.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                System.out.println("Rozpocznij grę");
            }
        } );
        _rejectInv.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                inv.setVisible(false);
                pack();
            }
        } );
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        pack();
        setContentPane(Content);
        setSize(470,400);
        setVisible(true);
    }

    @Override
    public void performed(String message, MessType type) {
        switch (type) {
            case INVITED:
                    _nickInv.setText(message);
                    inv.setVisible(true);
                    pack();
                    break;
            default: break;
        }
    }
}
