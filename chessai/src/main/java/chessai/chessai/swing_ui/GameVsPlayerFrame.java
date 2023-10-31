package chessai.chessai.swing_ui;

import chessai.chessai.lib.Board;

import javax.swing.*;
import java.awt.*;
import java.text.ParseException;

public class GameVsPlayerFrame extends JFrame {

	final BoardPanel boardPanel;

	public GameVsPlayerFrame() {

		try {
			boardPanel = new BoardPanel(
					new Color(237, 214, 179, 255),
					new Color(179, 134, 98, 255),
					false,
					100,
					new Board("r1bqkbnr/pppp1pp1/2n1p2p/8/2BPP3/5Q2/PPP2PPP/RNB1K1NR b KQkq - 0 1")
			);
		} catch (ParseException e) {
			throw new RuntimeException(e);
		}

	}
}
