package chessai.chessai.lib;

import chessai.chessai.lib.pieces.King;
import chessai.chessai.lib.pieces.Pawn;
import chessai.chessai.lib.pieces.Queen;
import chessai.chessai.lib.pieces.Rook;
import org.junit.jupiter.api.Test;
import org.opentest4j.TestAbortedException;

import java.text.ParseException;

import static org.junit.jupiter.api.Assertions.*;

class BoardTest {

    @Test
    void get() {
        try {
            Board board = new Board("rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR w KQkq - 0 1");

            assertEquals(board.get(new Square("a1")).getClass(), Rook.class);
            assertEquals(board.get(new Square("a7")).getClass(), Pawn.class);
            assertEquals(board.get(new Square("e1")).getClass(), King.class);
            assertEquals(board.get(new Square("d1")).getClass(), Queen.class);
        } catch (ParseException e) {
            throw new TestAbortedException();
        }
    }

    @Test
    void setFromFENString() {
        assertDoesNotThrow(() -> new Board("rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR w KQkq - 0 1"));
        assertDoesNotThrow(() -> new Board("rnbqkbnr/pp1ppppp/8/2p5/4P3/8/PPPP1PPP/RNBQKBNR w KQkq c6 0 2"));
        assertDoesNotThrow(() -> new Board("8/2B2k2/6p1/5P1p/6p1/p1p3PP/6P1/1n2K2R w K - 0 1"));
        assertThrows(ParseException.class, () -> new Board(""));
        assertThrows(ParseException.class, () -> new Board("     "));
        assertThrows(ParseException.class, () -> new Board("rnbqkbnr/pp1ppppp/8/2p5/4P3/5N2/PPPP1PPP/RNBQKB1R"));
        assertThrows(ParseException.class, () -> new Board("rnbqkbnr/pp1ppppp/8/2p5/3P3/5N2/PPPP1PPP/RNBQKB1R b KQkq - 1 2"));
    }

    @Test
    void getFENString() {
        try {
            String fenString = new Board("rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR w KQkq - 0 1").getFENString();
            assertEquals("rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR w KQkq - 0 1", fenString);
        } catch (ParseException e) {
            throw new TestAbortedException();
        }

        try {
            String fenString = new Board("8/2B2k2/6p1/5P1p/6p1/p1p3PP/6P1/1n2K2R w K - 0 1").getFENString();
            assertEquals("8/2B2k2/6p1/5P1p/6p1/p1p3PP/6P1/1n2K2R w K - 0 1", fenString);
        } catch (ParseException e) {
            throw new TestAbortedException();
        }

        try {
            String fenString = new Board("rnbqkbnr/pp1ppppp/8/2p5/4P3/5N2/PPPP1PPP/RNBQKB1R b KQkq - 1 2").getFENString();
            assertEquals("rnbqkbnr/pp1ppppp/8/2p5/4P3/5N2/PPPP1PPP/RNBQKB1R b KQkq - 1 2", fenString);
        } catch (ParseException e) {
            throw new TestAbortedException();
        }
    }

    @Test
    void isKingInCheck() throws ParseException {

        Board board = new Board("8/3k4/8/8/2n5/1PP5/1K6/8 b - - 0 1");

        assertTrue(board.isKingInCheck(PieceColor.WHITE));
        assertFalse(board.isKingInCheck(PieceColor.BLACK));
    }

    @Test
    void getState() throws ParseException {

        assertEquals(GameState.WHITE_WIN, new Board("8/8/8/8/kQK5/8/8/8 b - - 0 1").getState());
        assertEquals(GameState.DRAW, new Board("8/8/8/8/8/1Q6/2K5/k7 b - - 0 1").getState());
        assertEquals(GameState.PLAYING, new Board("8/8/8/8/8/1Q6/2K5/k7 w - - 0 1").getState());

    }

    @Test
    void move () throws ParseException {

        Board board = new Board("2k5/3p4/8/8/8/8/4P3/2K5 w - - 0 1");

        // e4

        board = board.move(new Move(new Square("e2"), new Square("e4"), null, false, false, SpecialMove.DOUBLE_PAWN_PUSH));

        Piece pawnOnE4 = board.get(new Square("e4"));

        assertNotNull(pawnOnE4);
        assertEquals(3, pawnOnE4.getSquare().row());

        // kc7

        board = board.move(new Move(new Square("c8"), new Square("c7"), null, false, false, SpecialMove.NONE));

        Piece kingOnC7 = board.get(new Square("c7"));

        assertNotNull(kingOnC7);
        assertEquals(6, kingOnC7.getSquare().row());

        // e5

        board = board.move(new Move(new Square("e4"), new Square("e5"), null, false, false, SpecialMove.NONE));

        Piece pawnOnE5 = board.get(new Square("e5"));

        assertNotNull(pawnOnE5);
        assertEquals(4, pawnOnE5.getSquare().row());

        // d5

        board = board.move(new Move(new Square("d7"), new Square("d5"), null, false, false, SpecialMove.DOUBLE_PAWN_PUSH));

        Piece pawnOnD5 = board.get(new Square("d5"));

        assertNotNull(pawnOnD5);
        assertEquals(4, pawnOnD5.getSquare().row());

        // exd4

        board = board.move(new Move(new Square("e5"), new Square("d4"), null, true, true, SpecialMove.NONE));

        Piece pawnAfterEnPassant = board.get(new Square("d6"));

        assertNotNull(pawnAfterEnPassant);
        assertEquals(5, pawnAfterEnPassant.getSquare().row());
    }
}