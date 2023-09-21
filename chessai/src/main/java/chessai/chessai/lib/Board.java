package chessai.chessai.lib;

import java.security.InvalidParameterException;
import java.util.Collection;
import java.util.Collections;

public class Board {

    /**
     * 0 = A1
     * 1 = A2
     * ...
     * 8 = B1
     * ...
     * 63 = H8
     *
     * */
    Piece[] pieces;

    public Board (Collection<Piece> pieces) {
        this.pieces = new Piece[64];
        pieces.forEach(piece -> this.pieces[piece.getSquare().toIndex()] = piece);
    }

}
