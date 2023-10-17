package chessai.chessai.lib.pieces;

import chessai.chessai.lib.Board;
import chessai.chessai.lib.MoveResult;
import chessai.chessai.lib.Square;
import org.junit.jupiter.api.Test;

import java.text.ParseException;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;

class KnightTest {

    @Test
    void getPseudoLegalMovesAsBitMaps() throws ParseException {

        Board board = new Board("4k3/8/8/5r2/2b5/4N3/2K5/5q2 w KQkq - 0 1");

        var ref = new Object() {
            Knight knight;
        };

        assertDoesNotThrow(() -> ref.knight = (Knight) board.get(new Square("e3")));

        MoveResult result = ref.knight.getPseudoLegalMovesAsBitMaps(board);

        assertEquals(7, result.moveTargets().getIndexesOfOnes().size());
    }
}