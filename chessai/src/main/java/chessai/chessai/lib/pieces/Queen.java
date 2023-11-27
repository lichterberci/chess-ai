package chessai.chessai.lib.pieces;

import chessai.chessai.lib.*;

import java.util.List;
import java.util.Optional;

/**
 * Represents a queen on the board
 */
public class Queen extends SlidingPiece {
	public Queen(PieceColor color) {
		super(color);
	}

	@Override
	public char getFENChar() {
		return getColor() == PieceColor.WHITE ? 'Q' : 'q';
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
                    board.enPassantTarget.getIndex() - 8 // prev move was by black
                    : board.enPassantTarget.getIndex() + 8; // prev move was by white
            enPassantPawn.setBitInPlace(enPassantPawnSquareIndex, true);
        }

        int enPassantTargetIndex = board.enPassantTarget != null ? board.enPassantTarget.getIndex() : -1;

        slide(currentFile, currentRow, otherColorPieces, sameColorPieces, enPassantPawn, enPassantTargetIndex, otherColorKing, result, 1, 0);
        slide(currentFile, currentRow, otherColorPieces, sameColorPieces, enPassantPawn, enPassantTargetIndex, otherColorKing, result, -1, 0);
        slide(currentFile, currentRow, otherColorPieces, sameColorPieces, enPassantPawn, enPassantTargetIndex, otherColorKing, result, 0, 1);
        slide(currentFile, currentRow, otherColorPieces, sameColorPieces, enPassantPawn, enPassantTargetIndex, otherColorKing, result, 0, -1);
        slide(currentFile, currentRow, otherColorPieces, sameColorPieces, enPassantPawn, enPassantTargetIndex, otherColorKing, result, 1, 1);
        slide(currentFile, currentRow, otherColorPieces, sameColorPieces, enPassantPawn, enPassantTargetIndex, otherColorKing, result, -1, 1);
        slide(currentFile, currentRow, otherColorPieces, sameColorPieces, enPassantPawn, enPassantTargetIndex, otherColorKing, result, 1, -1);
        slide(currentFile, currentRow, otherColorPieces, sameColorPieces, enPassantPawn, enPassantTargetIndex, otherColorKing, result, -1, -1);

	    return result;
    }

    @Override
    public Piece copy() {
        return new Queen(color);
    }

    /**
     * @param board  the board in which me want to move
     * @param moves  the list of moves that we amend
     * @param square the square we want to look at
     * @return whether we terminate the current loop
     */
    private boolean determineWhetherItCanMoveToSquare(Board board, List<Move> moves, Square square) {
        Optional<PieceColor> color;

        if ((color = board.getColorAtSquare(square)).isPresent()) {

            if (color.get().equals(getColor()))
                return true;

            moves.add(new Move(getSquare(),
                    square,
                    null,
                    true,
                    false,
                    SpecialMove.NONE));

            return true;
        }

        moves.add(new Move(getSquare(),
                square,
                null,
                false,
                false,
                SpecialMove.NONE));

        return false;
    }
}

