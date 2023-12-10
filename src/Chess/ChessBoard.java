package Chess;

import chess_server_package.Client;
import com.chess.engine.board.Board;
import com.chess.engine.board.BoardUtils;
import com.chess.engine.board.Move;
import com.chess.engine.board.MoveTransition;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class ChessBoard {
    private JPanel board;
    private Client _player;
    private JButton[][] chessBoardSquares = new JButton[8][8];
    private String previousS;
    private JButton previousB;
    private Board logicBoard;
    private Dimension BlockSize = new Dimension(60,60);
    private char playerColor;
    private Icon holdPiece = null;
    public boolean yourTurn = false;
    private String[] cordsC = {"a","b","c","d","e","f","g","h"};

    public ChessBoard(JPanel board, char c, Board boardL, Client p) {
        _player = p;
        this.logicBoard = boardL;
        this.playerColor = c;
        previousS = "";
        previousB = null;
        String color;
        String typ;
        String buttonName;
        Insets buttonMargin = new Insets(0, 0, 0, 0);
        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                buttonName = Integer.toString(i+1)+Integer.toString(j+1);
                if (c == 'W') buttonName = Integer.toString(8-i)+Integer.toString(j+1);
                JButton b = new JButton();
                b.setName(buttonName);
                //System.out.println(b.getName());
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
        //SPRAWDŹ CZY JEST JEGO KOLEJ
        if (!yourTurn) return;
        //WYKONAJ RUCH
        if(previousS == ""){
            previousS = b.getName();
            previousB = b;
            //System.out.println(previous);
            holdPiece = b.getIcon();
        } else {
            //WEŹ WSPÓŁRZĘDNE
            String y1 = previousS.substring(0,1);
            String x1 = cordsC[Integer.parseInt(previousS.substring(1))-1];
            String y2 = b.getName().substring(0,1);
            String x2 = cordsC[Integer.parseInt(b.getName().substring(1))-1];
            System.out.println(x1+y1+"->"+x2+y2);
            //SPRÓBUJ WYKONAĆ RUCH
            final MoveTransition moveTransition = logicBoard.currentPlayer().makeMove(Move.MoveFactory.createMove(logicBoard, BoardUtils.INSTANCE.getCoordinateAtPosition(x1+y1),
                    BoardUtils.INSTANCE.getCoordinateAtPosition(x2+y2)));
            //JEŚLI SIĘ UDAŁO WYŚLIJ GO
            if (moveTransition.getMoveStatus().isDone()) {
                //previous.setIcon(null);
                logicBoard = moveTransition.getToBoard();
                b.setIcon(holdPiece);
                previousB.setIcon(null);
                yourTurn = false;
                //WYŚLIJ GO
                _player.makeMove(x1+y1+x2+y2);
            }
            previousS = "";
            previousB = null;
            holdPiece = null;
        }
    }

    /*
    * Wykonanie ruchu przeciwnika
    * @return "InGame", "DRAW", "LOST" zależnie od skutku ruchu
     */
    public String opponentMove(String from, String to) {
        String x1 = from.substring(0,1);
        String y1 = from.substring(1);
        String x2 = to.substring(0,1);
        String y2 = to.substring(1);
        //System.out.println(x1+" "+y1+"->"+x2+" "+y2);
        int i1 = 0;
        int i2 = 0;
        int j = 0;
        for (String c: cordsC) {
            if (c.equals(x1)) i1 = j;
            if (c.equals(x2)) i2 = j;
            j++;
        }

        int j1 = Integer.parseInt(y1);
        int j2 = Integer.parseInt(y2);
        if (this.playerColor == 'W') {
            j1 = 8 - j1;
            j2 = 8 - j2;
        } else {
            j1-=1;
            j2-=1;
        }
        previousB = chessBoardSquares[j1][i1];
        System.out.println(previousB.getName());
        JButton b = chessBoardSquares[j2][i2];
        System.out.println(b.getName());
        holdPiece = previousB.getIcon();
        System.out.println(x1+y1+"->"+x2+y2);
        final MoveTransition moveTransition = logicBoard.currentPlayer().makeMove(Move.MoveFactory.createMove(logicBoard, BoardUtils.INSTANCE.getCoordinateAtPosition(from),
                BoardUtils.INSTANCE.getCoordinateAtPosition(to)));

        if (moveTransition.getMoveStatus().isDone()) {
            logicBoard = moveTransition.getToBoard();
            b.setIcon(holdPiece);
            previousB.setIcon(null);
            //SPRAWDŹ CZY KONIEC GRY
            if (logicBoard.currentPlayer().isInCheckMate()) return "LOST";
            else if (logicBoard.currentPlayer().isCastled()) return "DRAW";
        }
        previousS = "";
        previousB = null;
        holdPiece = null;
        yourTurn = true;
        return "InGame";
    }
}