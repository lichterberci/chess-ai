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

        assertEquals(2, board.get(new Square("f2")).getPseudoLegalMovesAsBitMaps(board).moveTargets().getIndexesOfOnes().size());
        assertEquals(1, board.get(new Square("g4")).getPseudoLegalMovesAsBitMaps(board).moveTargets().getIndexesOfOnes().size());
        assertEquals(0, board.get(new Square("h6")).getPseudoLegalMovesAsBitMaps(board).moveTargets().getIndexesOfOnes().size());
        assertEquals(2, board.get(new Square("e7")).getPseudoLegalMovesAsBitMaps(board).moveTargets().getIndexesOfOnes().size());
        assertEquals(3, board.get(new Square("b5")).getPseudoLegalMovesAsBitMaps(board).moveTargets().getIndexesOfOnes().size());
        assertEquals(0, board.get(new Square("d4")).getPseudoLegalMovesAsBitMaps(board).moveTargets().getIndexesOfOnes().size());
        assertEquals(2, board.get(new Square("d3")).getPseudoLegalMovesAsBitMaps(board).moveTargets().getIndexesOfOnes().size());
        assertEquals(1, board.get(new Square("h2")).getPseudoLegalMovesAsBitMaps(board).moveTargets().getIndexesOfOnes().size());
    }
}