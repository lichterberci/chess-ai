package chessai.chessai.engine;

import chessai.chessai.lib.Board;
import chessai.chessai.lib.Move;

import java.util.List;
import java.util.Optional;
import java.util.Random;

public class RandomEngine extends ChessEngine {

    @Override
    public Optional<Move> makeMove(Board board) {
        List<Move> moves = board.getLegalMoves();

        if (moves.isEmpty())
            return Optional.empty();

        Random random = new Random(System.currentTimeMillis());

        return Optional.of(moves.get(random.nextInt(moves.size())));
    }
}
