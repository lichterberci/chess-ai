package chessai.chessai.lib.pieces;

import chessai.chessai.lib.Board;
import chessai.chessai.lib.Move;
import chessai.chessai.lib.Square;
import org.junit.jupiter.api.Test;

import java.text.ParseException;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;

class KingTest {

    @Test
    void getAllPossibleMoves() throws ParseException {

        Board board = new Board("1q6/2kb4/2RR4/8/4Q3/8/2K3r1/8 w - - 0 1");

        var ref = new Object() {
            King king;
        };

        assertDoesNotThrow(() -> ref.king = (King) board.get(new Square("c7")));

        List<Move> moves = ref.king.getPseudoLegalMoves(board);

        assertEquals(6, moves.size());
    }
    @Test
    void getAllPossibleMovesWithCastle() throws ParseException {

        Board board = new Board("r3k2r/4p3/8/8/8/8/2K5/8 w KQkq - 0 1");

        var ref = new Object() {
            King king;
        };

        assertDoesNotThrow(() -> ref.king = (King) board.get(new Square("e8")));

        List<Move> moves = ref.king.getPseudoLegalMoves(board);

        assertEquals(6, moves.size());
    }
}