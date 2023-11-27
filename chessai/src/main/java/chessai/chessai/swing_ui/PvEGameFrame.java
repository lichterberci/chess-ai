package chessai.chessai.swing_ui;

import chessai.chessai.engine.ChessEngine;
import chessai.chessai.engine.EvaluatedMove;
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

/**
 * The frame, containing the controller for the person versus engine use-case.
 */
@SuppressWarnings("ALL")
public class PvEGameFrame extends JFrame {

	private final BoardPanel boardPanel;
	private transient Board board;
	private transient Square selectedPiece;
	private final transient ChessEngine engine;
	private final transient boolean isPlayerWhite;
	private transient SwingWorker<Optional<EvaluatedMove>, Optional<EvaluatedMove>> engineMoveCalculatorWorker;
    private final transient Optional<Integer> availableTimeInMillisForEngine;
	private final transient PGNBuilder pgnBuilder;

	public PvEGameFrame(ChessEngine engine, boolean isPlayerWhite, Optional<Integer> availableTimeInMillisForEngine) {
		this("rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR w KQkq - 0 1", engine, isPlayerWhite, availableTimeInMillisForEngine);
	}

	public PvEGameFrame(String startFen, ChessEngine engine, boolean isPlayerWhite, Optional<Integer> availableTimeInMillisForEngine) {
		super("Human vs engine");

		this.engine = engine;
		this.isPlayerWhite = isPlayerWhite;
		this.availableTimeInMillisForEngine = availableTimeInMillisForEngine;

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

        if (!isPlayerWhite)
            calculateEngineMoveAndMakMoveAfterwards();

        pgnBuilder = new PGNBuilder(board, isPlayerWhite ? "Player" : "Engine", isPlayerWhite ? "Engine" : "Player", startFen);
	}

	private void makeMove(Move move) {

		if (move == null) {
			throw new RuntimeException("Move is null!");
		}

		pgnBuilder.addMove(move);

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

			Optional<Move> currentBestMove = Optional.empty();

			@Override
			protected Optional<EvaluatedMove> doInBackground() {
				return PvEGameFrame.this.engine.makeMove(
						PvEGameFrame.this.board,
						optMove -> currentBestMove = optMove.map(EvaluatedMove::move),
						this::isCancelled
				);
			}

			@Override
			protected void done() {
				// this is a callback to run after the thread finishes execution

				if (isCancelled()) {
					PvEGameFrame.this.makeMove(currentBestMove.orElse(null));
					return;
				}

				Optional<Move> result;

				try {
					result = this.get().map(EvaluatedMove::move);
				} catch (InterruptedException | ExecutionException e) {
					throw new RuntimeException(e);
				}

				if (result.isEmpty()) {
					System.err.println("The engine returned with an empty result");
					PvEGameFrame.this.dispose();
				}

				PvEGameFrame.this.makeMove(result.orElse(null));
			}
		};

		if (availableTimeInMillisForEngine.isPresent()) {
			new SwingWorker<Void, Void>() {
				int availableTimeInMillis = PvEGameFrame.this.availableTimeInMillisForEngine.get();

				@Override
				protected Void doInBackground() throws Exception {

					long startTime = System.currentTimeMillis();

					while (System.currentTimeMillis() < startTime + availableTimeInMillis) {
						Thread.sleep(100);
					}

					PvEGameFrame.this.engineMoveCalculatorWorker.cancel(true);

					return null;
				}
			}.execute();
		}

		engineMoveCalculatorWorker.execute();
	}

	private void gameEndedAction(GameState endState) {

		pgnBuilder.setResult(endState);
		GameEndedDialog gameEndedDialog = new GameEndedDialog(this, endState, pgnBuilder.buildString());
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
