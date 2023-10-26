package chessai.chessai.lib.pieces;

import chessai.chessai.lib.*;

public class Pawn extends Piece {
    public Pawn (PieceColor color) {
        super(color);
    }
    @Override
    public char getFENChar() {
        return getColor() == PieceColor.WHITE ? 'P' : 'p';
    }

    @Override
    public MoveResult getPseudoLegalMoves(Board board) {

        final int currentFile = getSquare().file();
        final int currentRow = getSquare().row();

        BitMap otherColorPieces = color == PieceColor.WHITE ? board.blackPieces : board.whitePieces;
        BitMap sameColorPieces = color == PieceColor.BLACK ? board.blackPieces : board.whitePieces;

        int rowOffset = color == PieceColor.WHITE ? 1 : -1;

        MoveResult result = new MoveResult();

        final int oneMoveForwardIndex = Square.getIndex(currentFile, currentRow + rowOffset);
        final int twoMoveForwardIndex = Square.getIndex(currentFile, currentRow + 2 * rowOffset);
        final int attackToRightIndex = Square.getIndex(currentFile + 1, currentRow + rowOffset);
        final int attackToLeftIndex = Square.getIndex(currentFile - 1, currentRow + rowOffset);

        final int enPassantTargetIndex = board.enPassantTarget != null ? board.enPassantTarget.getIndex() : -1;

        // single move forward
        result.moveTargets().setBitInPlace(
                oneMoveForwardIndex,
                !otherColorPieces.getBit(oneMoveForwardIndex) && !sameColorPieces.getBit(oneMoveForwardIndex)
        );

        // double move
        if ((
                color == PieceColor.WHITE ? currentRow == 1 : currentRow == 6 // we are on the correct row
        ) &&
                !otherColorPieces.getBit(oneMoveForwardIndex) // nothing in front
                && !sameColorPieces.getBit(oneMoveForwardIndex)
                && !otherColorPieces.getBit(twoMoveForwardIndex) // nothing one square further
                && !sameColorPieces.getBit(twoMoveForwardIndex)
        ) {
            result.moveTargets().setBitInPlace(twoMoveForwardIndex, true);
            result.isResultDoublePawnMove().setBitInPlace(twoMoveForwardIndex, true);
        }

        // promotion from forward move (can also occur from capture)
        if (color == PieceColor.WHITE ? currentRow == 6 : currentRow == 1)
            result.isResultPromotion().setBitInPlace(oneMoveForwardIndex, true);

        BitMap targetableSquaresForCapture = otherColorPieces.copy();

        if (enPassantTargetIndex != -1)
            targetableSquaresForCapture.setBitInPlace(enPassantTargetIndex, true);

        captureAndAttackIfRelevant(currentRow, result, attackToLeftIndex, enPassantTargetIndex, targetableSquaresForCapture);
        captureAndAttackIfRelevant(currentRow, result, attackToRightIndex, enPassantTargetIndex, targetableSquaresForCapture);

        return result;
    }

    private void captureAndAttackIfRelevant(int currentRow,
                                            MoveResult result,
                                            int attackIndex,
                                            int enPassantTargetIndex,
                                            BitMap targetableSquaresForCapture) {
        if (attackIndex != -1) {
            result.attackTargetsWhilePretendingTheEnemyKingIsNotThere().setBitInPlace(attackIndex, true);
            result.isResultCapture().setBitInPlace(attackIndex, targetableSquaresForCapture.getBit(attackIndex));
            result.moveTargets().setBitInPlace(attackIndex, targetableSquaresForCapture.getBit(attackIndex));
            result.isResultEnPassant().setBitInPlace(attackIndex, attackIndex == enPassantTargetIndex);
            result.isResultPromotion().setBitInPlace(attackIndex, color == PieceColor.WHITE ? currentRow == 6 : currentRow == 1);
        }
    }

    @Override
    public Piece copy() {
        return new Pawn(color);
    }

}
