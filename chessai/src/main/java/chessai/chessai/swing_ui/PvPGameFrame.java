package chessai.chessai.swing_ui;

import chessai.chessai.lib.*;
import chessai.chessai.lib.pieces.Queen;

import javax.swing.*;
import java.awt.*;
import java.text.ParseException;
import java.util.function.Consumer;

public class PvPGameFrame extends JFrame {

	private final BoardPanel boardPanel;
	private transient Board board;
	private transient Square selectedPiece;

	public PvPGameFrame() {
		super();

		this.setDefaultCloseOperation(DISPOSE_ON_CLOSE);

		try {
//			board = new Board("r1bqkbnr/pppp1pp1/2n1p2p/8/2BPP3/5Q2/PPP2PPP/RNB1K1NR b KQkq - 0 1");
			board = new Board("k7/4P3/K7/8/6n1/6N1/2p5/8 w - - 0 1");
		} catch (ParseException e) {
			throw new RuntimeException(e);
		}

		boardPanel = new BoardPanel(
				new Color(237, 214, 179, 255),
				new Color(179, 134, 98, 255),
				new Color(255, 28, 28, 147),
				true,
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

		if (board.getState() != GameState.PLAYING)
			gameEndedAction(board.getState());
	}

	private void gameEndedAction(GameState endState) {

		Dialog gameEndedDialog = new Dialog(this, "Game ended", true);

		gameEndedDialog.setModalityType(Dialog.ModalityType.MODELESS);
		gameEndedDialog.setLayout(new GridLayout(2, 1, 0, 0));

		String message = switch (endState) {
			case WHITE_WIN -> "White won!";
			case BLACK_WIN -> "Black won!";
			case DRAW -> "Draw!";
			case PLAYING -> throw new IllegalStateException("State cannot be playing!");
		};

		JLabel messageLabel = new JLabel(message);

		messageLabel.setHorizontalTextPosition(SwingConstants.CENTER);
		messageLabel.setVerticalTextPosition(SwingConstants.CENTER);
		messageLabel.setVerticalAlignment(SwingConstants.CENTER);
		messageLabel.setHorizontalAlignment(SwingConstants.CENTER);

		gameEndedDialog.add(messageLabel);

		JPanel buttonHolderPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 5));

		JButton backToBoardBtn = new JButton("Back to board");
		backToBoardBtn.addActionListener(e -> gameEndedDialog.dispose());
		backToBoardBtn.setHorizontalAlignment(SwingConstants.CENTER);

		JButton closeBoardBtn = new JButton("Close board");
		closeBoardBtn.addActionListener(e -> {
			gameEndedDialog.dispose();
			this.dispose();
		});
		closeBoardBtn.setHorizontalAlignment(SwingConstants.CENTER);

		buttonHolderPanel.add(backToBoardBtn);
		buttonHolderPanel.add(closeBoardBtn);

		gameEndedDialog.add(buttonHolderPanel);

		gameEndedDialog.pack();

		gameEndedDialog.setLocationRelativeTo(null);

		gameEndedDialog.setVisible(true);

	}

	private boolean tryToMakeMove(Move move) {

		if (move == null)
			return false;

		if (!board.isMoveLegal(move))
			return false;

		Consumer<Class<? extends Piece>> makeMoveWithPromotionPieceType = pieceType -> makeMove(move.withPromotionType(pieceType));

		if (board.shouldMoveBePromotion(move)) {

			var promotionPopupMenu = new PromotionPopupMenu(makeMoveWithPromotionPieceType, board.colorToMove);

			promotionPopupMenu.show(this,
					MouseInfo.getPointerInfo().getLocation().x - this.getX(),
					MouseInfo.getPointerInfo().getLocation().y - this.getY());
		} else {
			makeMoveWithPromotionPieceType.accept(null);
		}

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

		tryToMakeMove(board.tryToInferMove(selectedPiece, square, Queen.class).orElse(null));
	}
}
