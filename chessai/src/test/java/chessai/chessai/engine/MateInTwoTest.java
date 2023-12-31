package chessai.chessai.engine;

import chessai.chessai.lib.Board;
import chessai.chessai.lib.GameState;
import chessai.chessai.lib.Move;
import chessai.chessai.lib.Square;
import org.junit.jupiter.api.Test;

import java.text.ParseException;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class MateInTwoTest {

    @Test
    void findMateInTwo() throws ParseException {

        ChessEngine engine = new MinimaxEngine(4, 100_000);
//        ChessEngine engine = new MonteCarloEngine(0, 1.4142, 150, 150d);

        Board board1 = new Board("6k1/p2rR1p1/1p1r1p1R/3P4/4QPq1/1P6/P5PK/8 w - - 1 1");

        Optional<Move> move1 = engine.makeMove(board1);

        assertTrue(move1.isPresent());

        assertEquals(Square.getIndex("h6"), move1.get().fromIndex());
        assertEquals(Square.getIndex("h8"), move1.get().toIndex());

        Board board2 = board1.makeMove(move1.get());

        Optional<Move> move2 = engine.makeMove(board2);

        assertTrue(move2.isPresent());

        Board board3 = board2.makeMove(move2.get());

        Optional<Move> move3 = engine.makeMove(board3);

        assertTrue(move3.isPresent());

        Board board4 = board3.makeMove(move3.get());

        assertEquals(GameState.WHITE_WIN, board4.getState());
    }

}
