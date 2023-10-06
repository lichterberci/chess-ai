package chessai.chessai.engine.mates;

import chessai.chessai.engine.ChessEngine;
import chessai.chessai.engine.MonteCarloEngine;
import chessai.chessai.lib.Board;
import chessai.chessai.lib.Move;
import chessai.chessai.lib.Square;
import org.junit.jupiter.api.Test;

import java.text.ParseException;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class MateInTwoTest {

    @Test
    void findMateInTwo() throws ParseException {

        ChessEngine engine = new MonteCarloEngine(0, Math.sqrt(2), 40, 30);

        Board board = new Board("6k1/p2rR1p1/1p1r1p1R/3P4/4QPq1/1P6/P5PK/8 w - - 1 1");

        Optional<Move> firstMove = engine.makeMove(board);

        assertTrue(firstMove.isPresent());

        assertEquals(new Square("e7"), firstMove.get().from());
        assertEquals(new Square("e8"), firstMove.get().to());
    }

}
