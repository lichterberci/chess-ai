package chessai.chessai.lib.pieces;

import chessai.chessai.lib.Board;
import chessai.chessai.lib.MoveResult;
import chessai.chessai.lib.Square;
import org.junit.jupiter.api.Test;

import java.text.ParseException;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;

class RookTest {


    @Test
    void getPseudoLegalMoves() throws ParseException {

        Board board = new Board("8/2K5/5N2/8/8/8/2k2r2/8 b - - 0 1");

        var ref = new Object() {
            Rook rook;
        };

        assertDoesNotThrow(() -> ref.rook = (Rook) board.get(new Square("f2")));

        MoveResult result = ref.rook.getPseudoLegalMoves(board);

        assertEquals(9, result.moveTargets().getIndexesOfOnes().size());
    }
}