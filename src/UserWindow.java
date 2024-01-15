import chess_server_package.Client;
import chess_server_package.MessType;
import chess_server_package.MyListener;

import javax.swing.*;
import javax.swing.border.EtchedBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

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
    private JPanel stats;
    private JButton GetPlayerGames;
    private JButton GetPlayerStatistics;
    private JButton getAllPlayersStatistics;

    public UserWindow(Client client){
        super("Chessmageddon");
        player = client;
        player.listener = this;
        UserInterface.setBorder(BorderFactory.createTitledBorder(BorderFactory.createEtchedBorder(EtchedBorder.RAISED,Color.BLACK,Color.BLACK), player.nick));
        //Przycisk odpowiedzialny za zapraszanie
        _invite.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                if (_nickOpponent.getText() == "") return;
                player.playWith(_nickOpponent.getText());
            }
        } );
        //Przycisk odpowiedzialny za akceptowanie zaproszeń
        _acceptInv.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                player.confirm('B');
                System.out.println("Rozpocznij grę");
                new GameWindow(player,'W');
                dispose();
            }
        } );
        //Przycisk odpowiedzialny za odrzucanie zaproszeń
        _rejectInv.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                player.reject();
                inv.setVisible(false);
                pack();
            }
        } );
        GetPlayerGames.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                Stats s = new Stats(player.getPlayerGames());
                Thread t = new Thread(s);
                t.start();
            }
        });
        GetPlayerStatistics.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                Stats s = new Stats(player.getPlayerStatistics());
                Thread t = new Thread(s);
                t.start();
            }
        });
        getAllPlayersStatistics.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                Stats s = new Stats(player.getAllPlayersStatistics());
                Thread t = new Thread(s);
                t.start();
            }
        });
        //getPlayerStatistics lub getPlayerGames()

        System.out.println(player.getPlayerGames());
        /*
        String[] columnNames = {"Kolumna 1", "Kolumna 2", "Kolumna 3","Kolumna 1", "Kolumna 2"};
        ArrayList<ArrayList<String>> data = player.getPlayerGames();
        Object[][] dataArray = new Object[data.size()][];
        for (int i = 0; i < data.size(); i++) {
            ArrayList<String> row = data.get(i);
            dataArray[i] = row.toArray(new Object[0]);
        }

        DefaultTableModel model = new DefaultTableModel(dataArray, columnNames);
        JTable playerGames = new JTable(model);
        JScrollPane scrollPane = new JScrollPane(playerGames);
        stats.add(scrollPane);
        */
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        pack();
        setContentPane(Content);
        setSize(470,400);
        setVisible(true);

        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                player.quit();
                super.windowClosing(e);
            }
        });
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
