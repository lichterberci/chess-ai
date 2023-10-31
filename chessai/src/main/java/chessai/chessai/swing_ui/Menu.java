package chessai.chessai.swing_ui;

import chessai.chessai.lib.Board;

import javax.swing.*;
import java.awt.*;
import java.text.ParseException;

public class Menu {

    private JFrame window;

    public Menu() {

        window = new JFrame();
        window.setTitle("Chess AI");
        window.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);

        JButton playVsEngineBtn = new JButton("Play vs engine");
        playVsEngineBtn.addActionListener(e -> {
	        BoardPanel board = new BoardPanel(
			        new Color(237, 214, 179, 255),
			        new Color(179, 134, 98, 255),
			        false,
			        100);
	        board.setVisible(true);
	        try {
		        board.drawPosition(new Board("r1bqkbnr/pppp1pp1/2n1p2p/8/2BPP3/5Q2/PPP2PPP/RNB1K1NR b KQkq - 0 1"));
		        board.drawPosition(new Board("rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR w KQkq - 0 1"));
	        } catch (ParseException ex) {
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
