package chessai.chessai.swing_ui;

import chessai.chessai.lib.Board;
import chessai.chessai.lib.Move;
import chessai.chessai.lib.Square;
import chessai.chessai.lib.pieces.Queen;

import javax.swing.*;
import java.awt.*;
import java.text.ParseException;

public class PvPGameFrame extends JFrame {

	private final BoardPanel boardPanel;
	private transient Board board;
	private transient Square selectedPiece;

	public PvPGameFrame() {

		try {
			board = new Board("r1bqkbnr/pppp1pp1/2n1p2p/8/2BPP3/5Q2/PPP2PPP/RNB1K1NR b KQkq - 0 1");
		} catch (ParseException e) {
			throw new RuntimeException(e);
		}

		boardPanel = new BoardPanel(
				new Color(237, 214, 179, 255),
				new Color(179, 134, 98, 255),
				new Color(255, 28, 28, 147),
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

		boardPanel.selectSquare(null);

		boardPanel.repaint();
	}

	private boolean tryToMakeMove(Move move) {

		if (move == null)
			return false;

		if (!board.isMoveLegal(move))
			return false;

		makeMove(move);

		return true;
	}

	private void onSquareClick(Square square) {

		if (selectedPiece == null
				&& board.get(square) != null
				&& board.get(square).getColor() == board.colorToMove
		) {
			selectPieceOnBoard(square);
			return;
		}

		if (selectedPiece == null)
			return;

		// TODO: add promotion piece selection
		final boolean couldMakeMove = tryToMakeMove(board.tryToInferMove(selectedPiece, square, Queen.class).orElse(null));

		if (couldMakeMove) {
			selectPieceOnBoard(null);
			return;
		}

		if (board.get(square) != null && board.get(square).getColor() == board.colorToMove)
			selectPieceOnBoard(square); // clicked on another friendly piece
		else
			selectPieceOnBoard(null); // clicked on a random square
	}

	private void selectPieceOnBoard(Square square) {
		selectedPiece = square;
		boardPanel.selectSquare(square);
		boardPanel.repaint();
	}

	private void onSquareDragStart(Square square) {

		System.out.println("Drag start " + square);

		selectedPiece = square;
	}

	private void onSquareDragEnd(Square square) {

		System.out.println("Drag end " + square);

		// TODO: add promotion piece selection
		tryToMakeMove(board.tryToInferMove(selectedPiece, square, Queen.class).orElse(null));
	}
}
