package chessai.chessai.swing_ui;

import chessai.chessai.lib.Board;

import javax.swing.*;
import java.awt.*;
import java.io.IOException;
import java.text.ParseException;

public class Menu {

    private JFrame window;

    public Menu() {

        window = new JFrame();
        window.setTitle("Chess AI");
        window.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);

        JButton playVsEngineBtn = new JButton("Play vs engine");
        playVsEngineBtn.addActionListener(e -> {
            BoardPanel board = new BoardPanel(Color.WHITE, Color.BLACK, true, 100);
            board.setVisible(true);
            try {
                board.drawPosition(new Board("4R2k/p2r2p1/1p1r1p2/3P4/4QPq1/1P6/P5PK/8 b - - 2 2"));
            } catch (IOException | ParseException ex) {
                throw new RuntimeException(ex);
            }
            window.add(board);
            window.pack();
            window.setLocationRelativeTo(null);
        });

        window.add(playVsEngineBtn);

        window.pack();
        window.setLocationRelativeTo(null);
    }

    public void show() {
        window.setVisible(true);
    }
}
