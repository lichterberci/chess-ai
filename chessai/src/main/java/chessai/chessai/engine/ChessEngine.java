package chessai.chessai.engine;

import chessai.chessai.lib.Board;
import chessai.chessai.lib.Move;

import java.util.Optional;
import java.util.function.BooleanSupplier;
import java.util.function.Consumer;

public abstract class ChessEngine {

    public Optional<Move> makeMove(Board board) {
        return makeMove(board,
                optMove -> {
                },
                () -> false);
    }

    public abstract Optional<Move> makeMove(Board board, Consumer<Optional<Move>> callbackAfterEachDepth, BooleanSupplier isCancelled);

}
