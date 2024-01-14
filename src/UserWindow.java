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
    private JPanel onlinePlayers;

    public UserWindow(Client client){
        super("Chessmageddon");
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
                player.confirm('B');
                System.out.println("Rozpocznij grę");
                new GameWindow(player,'W');
                dispose();
            }
        } );
        _rejectInv.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                player.reject();
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

    /**
     * Odpowiada za przyjmowanie wiadomości z serwera
     * Przyjmownaie/odpowiadanie na zaproszenia
     */
    @Override
    public void performed(String message, MessType type) {
        switch (type) {
            case INVITED:
                    _nickInv.setText(message);
                    inv.setVisible(true);
                    pack();
                    break;
            case REJECT:
                System.out.println("Odrzucone zaproszenie");
                break;
            case CONFIRM:
                System.out.println("Aakceptowane zaproszenie");
                System.out.println(message);
                new GameWindow(player,'B');
                dispose();
                break;
            default: break;
        }
    }
}
