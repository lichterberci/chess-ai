package chessai.chessai.swing_ui;

import chessai.chessai.engine.ChessEngine;
import chessai.chessai.lib.*;
import chessai.chessai.lib.pieces.Queen;

import javax.swing.*;
import java.awt.*;
import java.text.ParseException;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.ExecutionException;
import java.util.function.Consumer;

public class PvEGameFrame extends JFrame {

	private final BoardPanel boardPanel;
	private transient Board board;
	private transient Square selectedPiece;
	private final transient ChessEngine engine;
	private final transient boolean isPlayerWhite;
	private transient SwingWorker<Optional<Move>, Optional<Move>> engineMoveCalculatorWorker;

	public PvEGameFrame(ChessEngine engine, boolean isPlayerWhite) {
		this("rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR w KQkq - 0 1", engine, isPlayerWhite);
	}

	public PvEGameFrame(String startFen, ChessEngine engine, boolean isPlayerWhite) {
		super("Human vs engine");

		this.engine = engine;
		this.isPlayerWhite = isPlayerWhite;

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

				isPlayerWhite,
				100
		);

		boardPanel.drawPosition(board);
		boardPanel.drawLayer("selectedSquare",
				Settings.getInstance().getSelectedPieceBackgroundColor(),
				Collections.emptyList(),
				3);
		boardPanel.validate();
		boardPanel.repaint();

		boardPanel.addOnSquareClickListeners(this::onSquareClick);

//		boardPanel.setBorder(BorderFactory.createLineBorder(Color.BLACK, 200));

		this.add(boardPanel, BorderLayout.CENTER);

		boardPanel.setVisible(true);
	}

	private void makeMove(Move move) {

		board = board.makeMove(move);

		boardPanel.playMoveSound(new BoardPanel.MoveSoundType(
				move.isCapture(),
				board.isKingInCheck(board.colorToMove),
				move.promotionPieceType() != null,
				move.specialMove() == SpecialMove.KING_SIDE_CASTLE || move.specialMove() == SpecialMove.QUEEN_SIDE_CASTLE)
		);

		boardPanel.drawPosition(board);
		boardPanel.validate();
		boardPanel.repaint();

		boardPanel.drawLayer("moveHighlight", Settings.getInstance().getMoveHighlightColor(), List.of(move.from(), move.to()), 2);

		if (board.getState() != GameState.PLAYING) {
			gameEndedAction(board.getState());
			return;
		}

		if (board.colorToMove == PieceColor.WHITE ^ isPlayerWhite) {
			calculateEngineMoveAndMakMoveAfterwards();
		}
		// else: the human makes the move
	}

	private void calculateEngineMoveAndMakMoveAfterwards() {
		this.engineMoveCalculatorWorker = new SwingWorker<>() {
			@Override
			protected Optional<Move> doInBackground() {
				return PvEGameFrame.this.engine.makeMove(PvEGameFrame.this.board);
			}

			@Override
			protected void done() {
				// this is a callback to run after tge thread finishes execution

				Optional<Move> result;

				try {
					result = this.get();
				} catch (InterruptedException | ExecutionException e) {
					throw new RuntimeException(e);
				}

				if (result.isEmpty()) {
					System.err.println("The engine returned with an empty result");
					PvEGameFrame.this.dispose();
				}

				PvEGameFrame.this.makeMove(result.get());
			}
		};

		engineMoveCalculatorWorker.execute();
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

		if (board.colorToMove == PieceColor.WHITE ^ isPlayerWhite)
			return;

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
