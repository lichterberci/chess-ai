package chessai.chessai.lib.pieces;

import chessai.chessai.lib.*;

/**
 * Represents a rook on the board
 */
public class Rook extends SlidingPiece {
    public Rook(PieceColor color) {
        super(color);
    }

    @Override
    public char getFENChar() {
        return getColor() == PieceColor.WHITE ? 'R' : 'r';
    }

    @Override
    public MoveResult getPseudoLegalMoves(Board board) {

        final int currentFile = getSquare().file();
        final int currentRow = getSquare().row();

        BitMap otherColorPieces = color == PieceColor.WHITE ? board.blackPieces : board.whitePieces;
        BitMap sameColorPieces = color == PieceColor.BLACK ? board.blackPieces : board.whitePieces;
        BitMap otherColorKing = color == PieceColor.WHITE ? board.blackKing : board.whiteKing;

        MoveResult result = new MoveResult();

        BitMap enPassantPawn = new BitMap(0);

        if (board.enPassantTarget != null) {
            int enPassantPawnSquareIndex = board.colorToMove == PieceColor.WHITE ?
                    board.enPassantTarget.getIndex() + 8 // prev move was by black
                    : board.enPassantTarget.getIndex() - 8; // prev move was by white
            enPassantPawn.setBitInPlace(enPassantPawnSquareIndex, true);
        }

        int enPassantTargetIndex = board.enPassantTarget != null ? board.enPassantTarget.getIndex() : -1;

        slide(currentFile, currentRow, otherColorPieces, sameColorPieces, enPassantPawn, enPassantTargetIndex, otherColorKing, result, 1, 0);
        slide(currentFile, currentRow, otherColorPieces, sameColorPieces, enPassantPawn, enPassantTargetIndex, otherColorKing, result, -1, 0);
        slide(currentFile, currentRow, otherColorPieces, sameColorPieces, enPassantPawn, enPassantTargetIndex, otherColorKing, result, 0, 1);
        slide(currentFile, currentRow, otherColorPieces, sameColorPieces, enPassantPawn, enPassantTargetIndex, otherColorKing, result, 0, -1);

        return result;

    }

    @Override
    public Piece copy() {
        return new Rook(color);
    }

}

