import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;

public class Stats implements Runnable {
    JFrame frame = new JFrame();

    GridLayout gridLayout = new GridLayout(1, 1);
    JPanel panel = new JPanel(gridLayout);
    JTable table = new JTable();
    JScrollPane scrollPane;
    ArrayList<ArrayList<String>> data;
    Stats(ArrayList<ArrayList<String>> d) {
        data = d;
    }
    public void showStats() {
        String[][] fin = new String[data.size()][data.get(0).size()];
        for(int i=0; i<data.size(); i++) {
            fin[i] = data.get(i).toArray(fin[i]);
        }
        table = new JTable(fin, fin[0]);
    }

    @Override
    public void run() {
        showStats();
        panel.add(table);
        scrollPane = new JScrollPane(table);
        frame.add(scrollPane);
        scrollPane.setVisible(true);
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        frame.pack();
        frame.setSize(800, 400);
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }
}
