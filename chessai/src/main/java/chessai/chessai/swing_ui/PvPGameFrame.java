package chessai.chessai.swing_ui;

import chessai.chessai.lib.*;
import chessai.chessai.lib.pieces.Queen;

import javax.swing.*;
import java.awt.*;
import java.text.ParseException;
import java.util.Collections;
import java.util.function.Consumer;

public class PvPGameFrame extends JFrame {

	private final BoardPanel boardPanel;
	private transient Board board;
	private transient Square selectedPiece;

	public PvPGameFrame() {
		this("rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR w KQkq - 0 1");
	}

	public PvPGameFrame(String startFen) {
		super("Human vs human");

		this.setLayout(new BorderLayout());
		this.setResizable(false);
		this.setDefaultCloseOperation(DISPOSE_ON_CLOSE);

		try {
			board = new Board(startFen);
		} catch (ParseException e) {
			System.err.println(e.getMessage());
			throw new RuntimeException(e);
		}

		boardPanel = new BoardPanel(
				true,
				100
		);

		this.setSize(new Dimension(800, 800));

		boardPanel.drawPosition(board);
		boardPanel.drawLayer("selectedSquare",
				Settings.getInstance().getSelectedPieceBackgroundColor(),
				Collections.emptyList(),
				3);
		boardPanel.validate();
		boardPanel.repaint();

		boardPanel.addOnSquareClickListeners(this::onSquareClick);

		this.add(boardPanel, BorderLayout.CENTER);

		boardPanel.setVisible(true);
	}

	private void makeMove(Move move) {

		boardPanel.playMoveSound(new BoardPanel.MoveSoundType(move.isCapture(),
				board.withIsCheckSet(move).isCheck(),
				move.promotionPieceType() != null,
				move.specialMove() == SpecialMove.KING_SIDE_CASTLE || move.specialMove() == SpecialMove.QUEEN_SIDE_CASTLE));

		board = board.makeMove(move);

		boardPanel.drawPosition(board);
		boardPanel.validate();
		boardPanel.repaint();

//		boardPanel.repaint();

		if (board.getState() != GameState.PLAYING)
			gameEndedAction(board.getState());
	}

	private void gameEndedAction(GameState endState) {

		GameEndedDialog gameEndedDialog = new GameEndedDialog(this, endState);
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
		boardPanel.drawLayer("selectedSquare",
				Settings.getInstance().getSelectedPieceBackgroundColor(),
				Collections.singletonList(square),
				3);
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
