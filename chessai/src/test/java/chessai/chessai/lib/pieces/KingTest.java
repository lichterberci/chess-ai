package chessai.chessai.lib.pieces;

import chessai.chessai.lib.Board;
import chessai.chessai.lib.MoveResult;
import chessai.chessai.lib.Square;
import org.junit.jupiter.api.Test;

import java.text.ParseException;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;

class KingTest {

    @Test
    void getPseudoLegalMoves() throws ParseException {

        Board board = new Board("1q6/2kb4/2RR4/8/4Q3/8/2K3r1/8 w - - 0 1");

        var ref = new Object() {
            King king;
        };

        assertDoesNotThrow(() -> ref.king = (King) board.get(new Square("c7")));

        MoveResult result = ref.king.getPseudoLegalMoves(board);

        assertEquals(6, result.moveTargets().getIndexesOfOnes().size());

    }
}