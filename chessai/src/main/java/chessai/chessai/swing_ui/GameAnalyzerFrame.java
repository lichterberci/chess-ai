package chessai.chessai.swing_ui;

import chessai.chessai.lib.Board;
import chessai.chessai.lib.Move;
import chessai.chessai.lib.PGNReader;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.text.ParseException;
import java.util.List;

public class GameAnalyzerFrame extends JFrame {

    private final transient List<Board> boards;
    private final transient List<Move> moves;
    private transient int currentBoardIndex;
    private final transient BoardPanel boardPanel;

    public GameAnalyzerFrame(String pgnString) throws ParseException {
        super("Game analyzer");

        this.setLayout(new BorderLayout());
        this.setResizable(false);
        this.setDefaultCloseOperation(DISPOSE_ON_CLOSE);

        PGNReader pgnReader = new PGNReader(pgnString);

        boards = pgnReader.getBoards();
        moves = pgnReader.getMoves();

        boardPanel = new BoardPanel(
                true,
                100
        );
        boardPanel.setVisible(true);
        this.add(boardPanel, BorderLayout.CENTER);

        this.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                GameAnalyzerFrame.this.keyPressed(e);
            }
        });

        drawBoard(0);
    }

    private void keyPressed(KeyEvent e) {

        if (e.getKeyCode() == KeyEvent.VK_RIGHT || e.getKeyCode() == KeyEvent.VK_UP)
            drawBoard(currentBoardIndex + 1);
        if (e.getKeyCode() == KeyEvent.VK_LEFT || e.getKeyCode() == KeyEvent.VK_DOWN)
            drawBoard(currentBoardIndex - 1);
        if (e.getKeyCode() == KeyEvent.VK_PAGE_UP || e.getKeyCode() == KeyEvent.VK_END)
            return;
    }

    private void drawBoard(int index) {
        if (index < 0 || index >= boards.size())
            return;

        currentBoardIndex = index;

        boardPanel.drawPosition(boards.get(index));

        boardPanel.validate();
        boardPanel.repaint();
    }
}
