package chessai.chessai.swing_ui;

import chessai.chessai.engine.ChessEngine;
import chessai.chessai.engine.EvaluatedMove;
import chessai.chessai.lib.Board;

import javax.swing.*;
import java.awt.*;
import java.text.ParseException;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.ExecutionException;

/**
 * The frame containing the controller for the puzzle solving use-case.
 */
public class PuzzleSolverFrame extends JFrame {
	private final transient EvalBar evalBar;
	private final transient BoardPanel boardPanel;
	private transient SwingWorker<Optional<EvaluatedMove>, Optional<EvaluatedMove>> engineMoveCalculatorWorker;

	public PuzzleSolverFrame(String fen, ChessEngine engine) throws ParseException {
		super("Puzzle solver");

		this.setLayout(new BorderLayout());
		this.setResizable(false);
		this.setDefaultCloseOperation(DISPOSE_ON_CLOSE);
		this.setSize(820, 800);

		evalBar = new EvalBar();
		evalBar.setMinimumSize(new Dimension(20, 800));
		evalBar.setVisible(true);
		this.add(evalBar, BorderLayout.WEST);

		Board board = new Board(fen);

		this.boardPanel = new BoardPanel(true, 100, board);

		this.add(boardPanel, BorderLayout.CENTER);

		solvePuzzle(engine, board);
	}

	private void solvePuzzle(ChessEngine engine, Board board) {
		engineMoveCalculatorWorker = new SwingWorker<Optional<EvaluatedMove>, Optional<EvaluatedMove>>() {
			@Override
			protected Optional<EvaluatedMove> doInBackground() {
				return engine.makeMove(
						board,
						optMove -> {
							if (isCancelled())
								return;

							if (optMove.isEmpty())
								return;

							if (optMove.get().eval().isPresent())
								PuzzleSolverFrame.this.evalBar.setEval(optMove.get().eval().get());

							PuzzleSolverFrame.this.boardPanel.drawLayer("engineMove",
									Settings.getInstance().getSelectedPieceBackgroundColor(),
									java.util.List.of(optMove.get().move().from(), optMove.get().move().to()),
									4);
						},
						this::isCancelled
				);
			}

			@Override
			protected void done() {
				// this is a callback to run after the thread finishes execution

				if (isCancelled())
					return;

				Optional<EvaluatedMove> result;

				try {
					result = this.get();
				} catch (InterruptedException | ExecutionException e) {
					throw new RuntimeException(e);
				}

				if (result.isEmpty()) {
					System.err.println("The engine returned with an empty result");
					PuzzleSolverFrame.this.dispose();
				}

				if (result.isEmpty())
					return;

				if (result.get().eval().isPresent())
					PuzzleSolverFrame.this.evalBar.setEval(result.get().eval().get());

				PuzzleSolverFrame.this.boardPanel.drawLayer("engineMove",
						Settings.getInstance().getSelectedPieceBackgroundColor(),
						List.of(result.get().move().from(), result.get().move().to()),
						4);
			}
		};

		engineMoveCalculatorWorker.execute();

	}

}
