package chessai.chessai.lib.pieces;

import chessai.chessai.lib.Board;
import chessai.chessai.lib.Move;
import chessai.chessai.lib.Square;
import org.junit.jupiter.api.Test;

import java.text.ParseException;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;

class QueenTest {

    @Test
    void getAllPossibleMoves() throws ParseException {

        Board board = new Board("8/2k5/4b3/8/4Q3/8/2K3r1/4R3 w KQkq - 0 1");

        var ref = new Object() {
            Queen queen;
        };

        assertDoesNotThrow(() -> ref.queen = (Queen) board.get(new Square("e4")));

        List<Move> moves = ref.queen.getPseudoLegalMoves(board);

        assertEquals(21, moves.size());

    }
}