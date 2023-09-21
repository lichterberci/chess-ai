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
}