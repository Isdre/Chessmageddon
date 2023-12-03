package Chess;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class ChessBoard {
    private JPanel board;
    private JButton[][] chessBoardSquares = new JButton[8][8];
    private Dimension BlockSize = new Dimension(60,60);

    private Icon holdPiece = null;

    public ChessBoard(JPanel board, char c) {
        if (board == null) return;
        String color;
        String typ;
        Insets buttonMargin = new Insets(0, 0, 0, 0);
        for (int i = 0; i < chessBoardSquares.length; i++) {
            for (int j = 0; j < chessBoardSquares[i].length; j++) {
                JButton b = new JButton();
                b.setHorizontalTextPosition(SwingConstants.CENTER);
                b.setVerticalTextPosition(SwingConstants.CENTER);
                b.setMinimumSize(BlockSize);
                b.setPreferredSize(BlockSize);
                b.setMaximumSize(BlockSize);
                if ((i <= 1) || (i >= 6)) {
                    if (i <= 1) {
                        if(c=='B') color = "White";
                        else color = "Black";
                    }
                    else {
                        if(c=='B') color = "Black";
                        else color = "White";
                    }
                    if (i == 1 || i == 6) typ = "R";
                    else {
                        if (j == 0 || j == 7) typ = "T";
                        else if (j == 1 || j == 6) typ = "K";
                        else if (j == 2 || j == 5) typ = "B";
                        else if ((j == 3)) typ = "Q";
                        else typ = "King";
                    }
                    Icon image = new ImageIcon("images/Chess_Pieces/" + color + "_" + typ + ".png");
                    b.setIcon(image);
                }

                b.setMargin(buttonMargin);
                if ((i+j) % 2 == 1) {
                    if(c=='B') b.setBackground(Color.LIGHT_GRAY);
                    else b.setBackground(Color.DARK_GRAY);
                } else {
                    if(c=='B') b.setBackground(Color.DARK_GRAY);
                    else b.setBackground(Color.LIGHT_GRAY);
                }
                b.addActionListener(new ActionListener() {
                    public void actionPerformed(ActionEvent e) {
                        onClickField(b,e);
                    }
                } );
                chessBoardSquares[i][j] = b;
                board.add(b,i*8 + j);
            }
        }
        board.validate();
        board.repaint();
    }

    private void onClickField(JButton b, ActionEvent e) {
        if (holdPiece == null) {
            holdPiece = b.getIcon();
            b.setIcon(null);
        } else {
            b.setIcon(holdPiece);
            holdPiece = null;
        }
    }
}