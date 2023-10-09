package chessai.chessai.engine.mates;

import chessai.chessai.engine.ChessEngine;
import chessai.chessai.engine.MonteCarloEngine;
import chessai.chessai.lib.Board;
import chessai.chessai.lib.GameState;
import chessai.chessai.lib.Move;
import org.junit.jupiter.api.Test;

import java.text.ParseException;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class MateInTwoTest {

    @Test
    void findMateInTwo() throws ParseException {

        ChessEngine engine = new MonteCarloEngine(0, 1000, 10, 300);

        Board board1 = new Board("6k1/p2rR1p1/1p1r1p1R/3P4/4QPq1/1P6/P5PK/8 w - - 1 1");

        Optional<Move> move1 = engine.makeMove(board1);

        assertTrue(move1.isPresent());

//        assertEquals(new Square("h6"), firstMove.get().from());
//        assertEquals(new Square("h8"), firstMove.get().to());

        Board board2 = board1.move(move1.get());

        Optional<Move> move2 = engine.makeMove(board2);

        assertTrue(move2.isPresent());

        Board board3 = board2.move(move2.get());

        Optional<Move> move3 = engine.makeMove(board3);

        assertTrue(move3.isPresent());

        Board board4 = board3.move(move3.get());

        assertEquals(GameState.WHITE_WIN, board4.getState());
    }

}
