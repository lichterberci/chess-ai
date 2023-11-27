package chessai.chessai.swing_ui;

import chessai.chessai.engine.ChessEngine;
import chessai.chessai.engine.EvaluatedMove;
import chessai.chessai.lib.Board;
import chessai.chessai.lib.Move;
import chessai.chessai.lib.PGNReader;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.ExecutionException;

public class GameAnalyzerFrame extends JFrame {

    private final transient List<Board> boards;
    private final transient List<Move> moves;
    private final transient ChessEngine engine;
    private transient int currentBoardIndex;
    private final transient BoardPanel boardPanel;
    private transient SwingWorker<Optional<EvaluatedMove>, Optional<EvaluatedMove>> engineMoveCalculatorWorker;
    private final transient EvalBar evalBar;

    public GameAnalyzerFrame(String pgnString, ChessEngine engine) throws ParseException {
        super("Game analyzer");

        this.engine = engine;

//        this.setLayout(new FlowLayout(FlowLayout.LEFT, 0, 0));
        this.setLayout(new BorderLayout());
        this.setResizable(false);
        this.setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        this.setSize(820, 800);

        PGNReader pgnReader = new PGNReader(pgnString);

        boards = new ArrayList<>(pgnReader.getBoards());
        moves = new ArrayList<>(pgnReader.getMoves());

        evalBar = new EvalBar();
        evalBar.setMinimumSize(new Dimension(20, 800));
        this.add(evalBar, BorderLayout.WEST);

        boardPanel = new BoardPanel(
                true,
                100
        );
        boardPanel.setVisible(true);
        this.add(boardPanel, BorderLayout.CENTER);


        this.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                GameAnalyzerFrame.this.keyPressed(e);
            }
        });

        drawBoard(0);

        validate();
        pack();
    }

    private void keyPressed(KeyEvent e) {
        if (e.getKeyCode() == KeyEvent.VK_RIGHT || e.getKeyCode() == KeyEvent.VK_UP)
            drawBoard(currentBoardIndex + 1);
        if (e.getKeyCode() == KeyEvent.VK_LEFT || e.getKeyCode() == KeyEvent.VK_DOWN)
            drawBoard(currentBoardIndex - 1);
        if (e.getKeyCode() == KeyEvent.VK_PAGE_UP || e.getKeyCode() == KeyEvent.VK_END)
            drawBoard(boards.size() - 1);
        if (e.getKeyCode() == KeyEvent.VK_PAGE_DOWN || e.getKeyCode() == KeyEvent.VK_HOME)
            drawBoard(0);
    }

    private void drawBoard(int index) {
        if (index < 0 || index >= boards.size())
            return;

        currentBoardIndex = index;

        boardPanel.drawPosition(boards.get(index));

        if (index > 0) {
            boardPanel.drawLayer("moveHighlight",
                    Settings.getInstance().getMoveHighlightColor(),
                    List.of(moves.get(index - 1).from(), moves.get(index - 1).to()),
                    1);
        } else {
            boardPanel.drawLayer("moveHighlight",
                    Settings.getInstance().getMoveHighlightColor(),
                    Collections.emptyList(),
                    1);
        }

        boardPanel.validate();
        boardPanel.repaint();

        evalBar.setEval(0);

        if (index < boards.size() - 1)
            calculateBestMoves();
        else
            GameAnalyzerFrame.this.boardPanel.drawLayer("engineMove",
                    Settings.getInstance().getSelectedPieceBackgroundColor(),
                    Collections.emptyList(),
                    4);
    }

    private void calculateBestMoves() {

        if (this.engineMoveCalculatorWorker != null)
            this.engineMoveCalculatorWorker.cancel(true);

        this.engineMoveCalculatorWorker = new SwingWorker<>() {

            @Override
            protected Optional<EvaluatedMove> doInBackground() {
                return GameAnalyzerFrame.this.engine.makeMove(
                        GameAnalyzerFrame.this.boards.get(GameAnalyzerFrame.this.currentBoardIndex),
                        optMove -> {
                            if (isCancelled())
                                return;

                            if (optMove.isEmpty())
                                return;

                            if (optMove.get().eval().isPresent())
                                GameAnalyzerFrame.this.evalBar.setEval(optMove.get().eval().get());

                            GameAnalyzerFrame.this.boardPanel.drawLayer("engineMove",
                                    Settings.getInstance().getSelectedPieceBackgroundColor(),
                                    List.of(optMove.get().move().from(), optMove.get().move().to()),
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
                    GameAnalyzerFrame.this.dispose();
                }

                if (result.isEmpty())
                    return;

                if (result.get().eval().isPresent())
                    GameAnalyzerFrame.this.evalBar.setEval(result.get().eval().get());

                GameAnalyzerFrame.this.boardPanel.drawLayer("engineMove",
                        Settings.getInstance().getSelectedPieceBackgroundColor(),
                        List.of(result.get().move().from(), result.get().move().to()),
                        4);
            }
        };

        engineMoveCalculatorWorker.execute();
    }
}
