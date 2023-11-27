package chessai.chessai.engine;

import chessai.chessai.lib.Board;
import chessai.chessai.lib.Move;

import java.util.List;
import java.util.Optional;
import java.util.Random;
import java.util.function.BooleanSupplier;
import java.util.function.Consumer;

public class RandomEngine extends ChessEngine {

    @Override
    public Optional<EvaluatedMove> makeMove(Board board, Consumer<Optional<EvaluatedMove>> callbackAfterEachDepth, BooleanSupplier isCancelled) {
        List<Move> moves = board.getLegalMoves();

        if (moves.isEmpty())
            return Optional.empty();

        Random random = new Random(System.currentTimeMillis());

        return Optional.of(new EvaluatedMove(moves.get(random.nextInt(moves.size())), Optional.empty()));
    }
}
