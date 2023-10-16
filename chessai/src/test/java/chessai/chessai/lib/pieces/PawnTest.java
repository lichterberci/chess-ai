package chessai.chessai.lib.pieces;

import chessai.chessai.lib.Board;
import chessai.chessai.lib.Square;
import org.junit.jupiter.api.Test;

import java.text.ParseException;

import static org.junit.jupiter.api.Assertions.assertEquals;

class PawnTest {

    @Test
    void getAllPseudoLegalMoves() throws ParseException {

        Board board = new Board("2k2n2/1p2P2N/2b4P/pP1r4/3P2P1/3p4/K1R2P1p/6BN w KQkq a6 0 1");

        assertEquals(2, board.get(new Square("f2")).getPseudoLegalMoves(board).size());
        assertEquals(1, board.get(new Square("g4")).getPseudoLegalMoves(board).size());
        assertEquals(0, board.get(new Square("h6")).getPseudoLegalMoves(board).size());
        assertEquals(8, board.get(new Square("e7")).getPseudoLegalMoves(board).size());
        assertEquals(3, board.get(new Square("b5")).getPseudoLegalMoves(board).size());
        assertEquals(0, board.get(new Square("d4")).getPseudoLegalMoves(board).size());

        assertEquals(1, board.get(new Square("a5")).getPseudoLegalMoves(board).size());
        assertEquals(2, board.get(new Square("d3")).getPseudoLegalMoves(board).size());
        assertEquals(4, board.get(new Square("h2")).getPseudoLegalMoves(board).size());
        assertEquals(1, board.get(new Square("b7")).getPseudoLegalMoves(board).size());

    }
}