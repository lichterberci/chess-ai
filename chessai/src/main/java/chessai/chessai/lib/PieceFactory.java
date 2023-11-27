package chessai.chessai.lib;

import chessai.chessai.lib.pieces.*;

/**
 * This is used during parsing to create pieces.
 */
public class PieceFactory {

    /**
     * Creates a new piece, determined by the FEN character
     *
     * @param fenChar the FEN character that represents the piece
     * @return a new piece isntance
     */
    public static Piece generateFromChar (char fenChar) {
        return switch (fenChar) {
            case 'P' -> new Pawn(PieceColor.WHITE);
            case 'p' -> new Pawn(PieceColor.BLACK);
            case 'N' -> new Knight(PieceColor.WHITE);
            case 'n' -> new Knight(PieceColor.BLACK);
            case 'B' -> new Bishop(PieceColor.WHITE);
            case 'b' -> new Bishop(PieceColor.BLACK);
            case 'R' -> new Rook(PieceColor.WHITE);
            case 'r' -> new Rook(PieceColor.BLACK);
            case 'Q' -> new Queen(PieceColor.WHITE);
            case 'q' -> new Queen(PieceColor.BLACK);
            case 'K' -> new King(PieceColor.WHITE);
            case 'k' -> new King(PieceColor.BLACK);
            default -> throw new IllegalStateException("Unexpected value: " + fenChar);
        };
    }

}
