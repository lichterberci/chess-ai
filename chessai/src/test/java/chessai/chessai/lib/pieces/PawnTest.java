package chessai.chessai.lib.pieces;

import chessai.chessai.lib.Board;
import chessai.chessai.lib.Square;
import org.junit.jupiter.api.Test;

import java.text.ParseException;
import static org.junit.jupiter.api.Assertions.*;

class PawnTest {

    @Test
    void getAllPossibleMovesWithCastle() throws ParseException {

        Board board = new Board("2k2n2/1p2P2N/2b4P/pP1r4/3P2P1/3p4/K1R2P1p/6BN w KQkq a6 0 1");

        assertEquals(2, board.get(new Square("f2")).getAllPossibleMoves(board).size());
        assertEquals(1, board.get(new Square("g4")).getAllPossibleMoves(board).size());
        assertEquals(0, board.get(new Square("h6")).getAllPossibleMoves(board).size());
        assertEquals(8, board.get(new Square("e7")).getAllPossibleMoves(board).size());
        assertEquals(3, board.get(new Square("b5")).getAllPossibleMoves(board).size());
        assertEquals(0, board.get(new Square("d4")).getAllPossibleMoves(board).size());

        assertEquals(1, board.get(new Square("a5")).getAllPossibleMoves(board).size());
        assertEquals(2, board.get(new Square("d3")).getAllPossibleMoves(board).size());
        assertEquals(4, board.get(new Square("h2")).getAllPossibleMoves(board).size());
        assertEquals(1, board.get(new Square("b7")).getAllPossibleMoves(board).size());

    }
}