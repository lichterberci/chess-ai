package chessai.chessai.swing_ui;

import chessai.chessai.lib.Board;
import chessai.chessai.lib.Move;
import chessai.chessai.lib.Square;
import chessai.chessai.lib.pieces.Queen;

import javax.swing.*;
import java.awt.*;
import java.text.ParseException;
import java.util.Optional;

public class PvPGameFrame extends JFrame {

	private final BoardPanel boardPanel;
	private Board board;
	private Square selectedSquare;

	public PvPGameFrame() {

		try {
			board = new Board("r1bqkbnr/pppp1pp1/2n1p2p/8/2BPP3/5Q2/PPP2PPP/RNB1K1NR b KQkq - 0 1");
		} catch (ParseException e) {
			throw new RuntimeException(e);
		}

		boardPanel = new BoardPanel(
				new Color(237, 214, 179, 255),
				new Color(179, 134, 98, 255),
				false,
				100
		);

		boardPanel.drawPosition(board);
		boardPanel.repaint();

		boardPanel.addOnSquareClickListeners(this::onSquareClick);
//		boardPanel.addOnSquareDragStartListeners(this::onSquareDragStart);
//		boardPanel.addOnSquareDragEndListeners(this::onSquareDragEnd);

		this.add(boardPanel);

		boardPanel.setVisible(true);
	}

	private void makeMove(Move move) {

		board = board.makeMove(move);

		boardPanel.drawPosition(board);

		boardPanel.repaint();
	}

	private void tryToMakeMove(Optional<Move> move) {

		if (move.isEmpty())
			return;

		if (!board.isMoveLegal(move.get()))
			return;

		makeMove(move.get());
	}

	private void onSquareClick(Square square) {

		System.out.println("Click " + square);

		if (selectedSquare == null) {
			selectedSquare = square;
			return;
		}

		if (board.get(selectedSquare) == null) {
			// we previously selected an empty square
			selectedSquare = square;
			return;
		}


		if (board.get(selectedSquare).getColor() != board.colorToMove) {
			// we previously selected a piece of the opposite color
			selectedSquare = square;
			return;
		}

		// TODO: add promotion piece selection
		tryToMakeMove(board.tryToInferMove(selectedSquare, square, Queen.class));

		selectedSquare = null;
	}

	private void onSquareDragStart(Square square) {

		System.out.println("Drag start " + square);

		selectedSquare = square;

	}

	private void onSquareDragEnd(Square square) {

		System.out.println("Drag end " + square);

		// TODO: add promotion piece selection
		tryToMakeMove(board.tryToInferMove(selectedSquare, square, Queen.class));
	}
}
