package chessai.chessai.engine;

import chessai.chessai.lib.Board;
import chessai.chessai.lib.Move;

import java.util.Optional;
import java.util.function.BooleanSupplier;
import java.util.function.Consumer;

/**
 * An abstract class that represents a chess engine (that can determine the best move in a given position)
 */
public abstract class ChessEngine {

    /**
     * Determines the best move in a given position
     *
     * @param board the position
     * @return the best move if there is any (and optionally the eval of that move)
     */
    public Optional<Move> makeMove(Board board) {
        return makeMove(board,
                optMove -> {
                },
                () -> false)
                .map(EvaluatedMove::move);
    }

    /**
     * Determines the best move in a given position
     *
     * @param board                  the position
     * @param callbackAfterEachDepth a callback that is called after some iterations / time that consumes the best move thus far
     * @param isCancelled            supplies whether the engine has more time to calculate or not
     * @return the best move if there is any (and optionally the eval of that move)
     */
    public abstract Optional<EvaluatedMove> makeMove(Board board, Consumer<Optional<EvaluatedMove>> callbackAfterEachDepth, BooleanSupplier isCancelled);

}
