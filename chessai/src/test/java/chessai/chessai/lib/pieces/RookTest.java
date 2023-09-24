package chessai.chessai.lib.pieces;

import chessai.chessai.lib.Board;
import chessai.chessai.lib.Move;
import chessai.chessai.lib.Piece;
import chessai.chessai.lib.Square;
import org.junit.jupiter.api.Test;

import java.text.ParseException;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class RookTest {

    @Test
    void getAllPossibleMoves() throws ParseException {

        Board board = new Board("8/2K5/5N2/8/8/8/2k2r2/8 b - - 0 1");

        var ref = new Object() {
            Rook rook;
        };

        assertDoesNotThrow(() -> ref.rook = (Rook) board.get(new Square("f2")));

        List<Move> moves = ref.rook.getAllPossibleMoves(board);

        assertEquals(9, moves.size());
    }
}