package chessai.chessai.lib.pieces;

import chessai.chessai.lib.BitMap;
import chessai.chessai.lib.Board;
import chessai.chessai.lib.MoveResult;
import chessai.chessai.lib.Square;
import org.junit.jupiter.api.Test;

import java.text.ParseException;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;

class QueenTest {


    @Test
    void testThatPinsAreWorkingProperly() throws ParseException {

        Board board = new Board("4k3/8/4b3/8/4Q3/8/2K3r1/4R3 w E - 0 1");

        var ref = new Object() {
            Queen queen;
        };

        assertDoesNotThrow(() -> ref.queen = (Queen) board.get(new Square("e4")));

        MoveResult result = ref.queen.getPseudoLegalMoves(board);

        assertEquals(21, result.moveTargets().getIndexesOfOnes().size());

        assertEquals(new BitMap(
                "00001000" +
                        "00001000" +
                        "00001000" +
                        "00001000" +
                        "00001000" +
                        "00000000" +
                        "00000000" +
                        "00000000"
        ), result.pinMap());

    }

    @Test
    void testThatAttackMapsWorkProperly() throws ParseException {

        Board board = new Board("8/8/2k5/8/4Q3/8/2K3r1/4R3 w E - 0 1");

        var ref = new Object() {
            Queen queen;
        };

        assertDoesNotThrow(() -> ref.queen = (Queen) board.get(new Square("e4")));

        MoveResult result = ref.queen.getPseudoLegalMoves(board);

        assertEquals(21, result.moveTargets().getIndexesOfOnes().size());

        assertEquals(new BitMap(
                "10001000" +
                        "01001001" +
                        "00101010" +
                        "00011100" +
                        "11110111" +
                        "00011100" +
                        "00101010" +
                        "00001000"
        ).getData(), result.attackTargetsWhilePretendingTheEnemyKingIsNotThere().getData());

    }
}