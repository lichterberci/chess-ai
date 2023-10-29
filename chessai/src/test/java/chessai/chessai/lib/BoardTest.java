package chessai.chessai.lib;

import chessai.chessai.lib.pieces.King;
import chessai.chessai.lib.pieces.Pawn;
import chessai.chessai.lib.pieces.Queen;
import chessai.chessai.lib.pieces.Rook;
import org.junit.jupiter.api.Test;
import org.opentest4j.TestAbortedException;

import java.text.ParseException;
import java.util.Optional;

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
    void setFromFENString() throws ParseException {
        assertDoesNotThrow(() -> new Board("rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR w KQkq - 0 1"));
        assertDoesNotThrow(() -> new Board("rnbqkbnr/pp1ppppp/8/2p5/4P3/8/PPPP1PPP/RNBQKBNR w KQkq c6 0 2"));
        assertDoesNotThrow(() -> new Board("8/2B2k2/6p1/5P1p/6p1/p1p3PP/6P1/1n2K2R w K - 0 1"));
        assertThrows(ParseException.class, () -> new Board(""));
        assertThrows(ParseException.class, () -> new Board("     "));
        assertThrows(ParseException.class, () -> new Board("rnbqkbnr/pp1ppppp/8/2p5/4P3/5N2/PPPP1PPP/RNBQKB1R"));
        assertThrows(ParseException.class, () -> new Board("rnbqkbnr/pp1ppppp/8/2p5/3P3/5N2/PPPP1PPP/RNBQKB1R b KQkq - 1 2"));

        Board board = new Board("rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR w KQkq - 0 1");
        for (int i = 0; i < 64; i++) {
            Piece piece = board.get(new Square(i));
            if (piece == null) continue;
            assertEquals(i, piece.getSquare().getIndex());
        }
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
        assertEquals(GameState.DRAW, new Board("8/8/8/2q5/K1k5/8/8/8 w - - 0 1").getState());
    }

    @Test
    void move () throws ParseException {

        Board board = new Board("r3k2r/8/8/8/8/8/8/R3K2R w KQkq - 0 1");

        // O - O

        Board boardAfterShortCastle = board.makeMove(new Move(new Square("e1"), new Square("g1"), null, false, false, SpecialMove.KING_SIDE_CASTLE));

        Piece kingAfterCastle = boardAfterShortCastle.get(new Square("g1"));
        Piece rookAfterCastle = boardAfterShortCastle.get(new Square("f1"));

        assertNotNull(kingAfterCastle);
        assertNotNull(rookAfterCastle);
        assertEquals(6, kingAfterCastle.getSquare().file());
        assertEquals(5, rookAfterCastle.getSquare().file());

        // O - O - O

        Board boardAfterLongCastle = board.makeMove(new Move(new Square("e1"), new Square("c1"), null, false, false, SpecialMove.QUEEN_SIDE_CASTLE));

        Piece kingAfterLongCastle = boardAfterLongCastle.get(new Square("c1"));
        Piece rookAfterLongCastle = boardAfterLongCastle.get(new Square("d1"));

        assertNotNull(kingAfterLongCastle);
        assertNotNull(rookAfterLongCastle);
        assertEquals(2, kingAfterLongCastle.getSquare().file());
        assertEquals(3, rookAfterLongCastle.getSquare().file());
    }

    @Test
    void enPassantPin() throws ParseException {
        // pinned en passant
        Board pinnedEnPassantBoard = new Board("4k3/8/8/2KPp2r/8/8/8/8 w - e6 0 1");

        long legalMovesFromD5 = pinnedEnPassantBoard.getLegalMoves()
                .stream().filter(move -> move.fromIndex() == Square.getIndex("d5"))
                .count();

        assertEquals(1, legalMovesFromD5);
    }

    @Test
    void castlingIsDoneCorrecly() throws ParseException {
        Board boardWithWhiteToMove = new Board("r3k2r/8/8/8/8/8/8/R3K2R w KQkq - 0 1");

        Optional<Move> whiteShortCastle = boardWithWhiteToMove.getLegalMoves().stream()
                .filter(move -> move.specialMove() == SpecialMove.KING_SIDE_CASTLE).findFirst();

        assertTrue(whiteShortCastle.isPresent());

        Board boardAfterWhiteShortCastle = boardWithWhiteToMove.makeMove(whiteShortCastle.get());

        assertNull(boardAfterWhiteShortCastle.get(new Square("e1")));
        assertNull(boardAfterWhiteShortCastle.get(new Square("h1")));
        assertInstanceOf(King.class, boardAfterWhiteShortCastle.get(new Square("g1")));
        assertInstanceOf(Rook.class, boardAfterWhiteShortCastle.get(new Square("f1")));

        Optional<Move> whiteLongCastle = boardWithWhiteToMove.getLegalMoves().stream()
                .filter(move -> move.specialMove() == SpecialMove.QUEEN_SIDE_CASTLE).findFirst();

        assertTrue(whiteLongCastle.isPresent());

        Board boardAfterWhiteLongCastle = boardWithWhiteToMove.makeMove(whiteLongCastle.get());

        assertNull(boardAfterWhiteLongCastle.get(new Square("a1")));
        assertNull(boardAfterWhiteLongCastle.get(new Square("b1")));
        assertNull(boardAfterWhiteLongCastle.get(new Square("e1")));
        assertInstanceOf(King.class, boardAfterWhiteLongCastle.get(new Square("c1")));
        assertInstanceOf(Rook.class, boardAfterWhiteLongCastle.get(new Square("d1")));

        Board boardWithBlackToMove = new Board("r3k2r/8/8/8/8/8/8/R3K2R b KQkq - 0 1");

        Optional<Move> blackShortCastle = boardWithBlackToMove.getLegalMoves().stream()
                .filter(move -> move.specialMove() == SpecialMove.KING_SIDE_CASTLE).findFirst();

        assertTrue(blackShortCastle.isPresent());

        Board boardAfterBlackShortCastle = boardWithBlackToMove.makeMove(blackShortCastle.get());

        assertNull(boardAfterBlackShortCastle.get(new Square("e8")));
        assertNull(boardAfterBlackShortCastle.get(new Square("h8")));
        assertInstanceOf(King.class, boardAfterBlackShortCastle.get(new Square("g8")));
        assertInstanceOf(Rook.class, boardAfterBlackShortCastle.get(new Square("f8")));

        Optional<Move> blackLongCastle = boardWithBlackToMove.getLegalMoves().stream()
                .filter(move -> move.specialMove() == SpecialMove.QUEEN_SIDE_CASTLE).findFirst();

        assertTrue(blackLongCastle.isPresent());

        Board boardAfterBlackLongCastle = boardWithBlackToMove.makeMove(blackLongCastle.get());

        assertNull(boardAfterBlackLongCastle.get(new Square("a8")));
        assertNull(boardAfterBlackLongCastle.get(new Square("b8")));
        assertNull(boardAfterBlackLongCastle.get(new Square("e8")));
        assertInstanceOf(King.class, boardAfterBlackLongCastle.get(new Square("c8")));
        assertInstanceOf(Rook.class, boardAfterBlackLongCastle.get(new Square("d8")));
    }

    @Test
    void cannotCastleFromAndThroughCheckOrTroughOwnPiece() throws ParseException {

        Board fromCheckBoard = new Board("1k2r3/8/8/8/8/8/3P1P2/R3KR1R w KQ - 0 1");

        assertEquals(1, fromCheckBoard.getLegalMoves().size());

        Board throughCheckBoard = new Board("1k6/8/8/8/2r5/8/3PPP2/R3KR1R w KQ - 0 1");

        assertEquals(1, throughCheckBoard.getLegalMoves().stream().filter(move -> move.from().equals(new Square("e1"))).count());

        Board throughCheckBoard2 = new Board("1k6/8/8/8/3r4/8/4PP2/R3KR1R w KQ - 0 1");

        assertEquals(0, throughCheckBoard2.getLegalMoves().stream().filter(move -> move.from().equals(new Square("e1"))).count());

        Board throughOwnPieceBoard = new Board("1k6/8/8/8/2r5/8/PPPPPP2/R1B1KR1R w KQ - 0 1");

        assertEquals(1, throughOwnPieceBoard.getLegalMoves().stream().filter(move -> move.from().equals(new Square("e1"))).count());
    }

    @Test
    void canFindLegalMovesWithoutThrowing() throws ParseException {

        Board board = new Board("r1bqkbnr/pppp1pp1/2n1p2p/8/2BPP3/5Q2/PPP2PPP/RNB1K1NR b KQkq - 0 1");

        assertDoesNotThrow(() -> board.getLegalMoves());
    }
}