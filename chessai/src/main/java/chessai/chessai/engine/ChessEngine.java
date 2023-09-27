package chessai.chessai.engine;

import chessai.chessai.lib.Board;
import chessai.chessai.lib.Move;

import java.util.Optional;

public abstract class ChessEngine {

    public abstract Optional<Move> makeMove(Board board);

}
