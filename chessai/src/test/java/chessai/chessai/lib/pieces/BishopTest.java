package chessai.chessai.lib.pieces;

import chessai.chessai.lib.Board;
import chessai.chessai.lib.Move;
import chessai.chessai.lib.Square;
import org.junit.jupiter.api.Test;

import java.text.ParseException;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;

class BishopTest {

    @Test
    void getAllPossibleMoves() throws ParseException {

        Board board = new Board("8/2k5/2r5/8/4B3/8/2K5/8 w KQkq - 0 1");

        var ref = new Object() {
            Bishop bishop;
        };

        assertDoesNotThrow(() -> ref.bishop = (Bishop) board.get(new Square("e4")));

        List<Move> moves = ref.bishop.getPseudoLegalMoves(board);

        assertEquals(9, moves.size());
    }
}