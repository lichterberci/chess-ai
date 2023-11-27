package chessai.chessai.lib;


import org.jetbrains.annotations.Nullable;

/**
 * Represents a move in the game.
 *
 * @param fromIndex          the index of the square, the piece is moving from
 * @param toIndex            the index of the square, the piece is moving to
 * @param promotionPieceType the type of piece, this piece wants to promote to (nullable)
 * @param isCapture          if the move is a capture
 * @param isEnPassant        if the move is en passant
 * @param specialMove        special flags of the move
 * @param isCheck            determines if the move is a check (usually this is left at a default false for performance reasons)
 */
public record Move(
        int fromIndex,
        int toIndex,
        @Nullable Class<? extends Piece> promotionPieceType,
        boolean isCapture,
        boolean isEnPassant,
        @Nullable SpecialMove specialMove,
        boolean isCheck
) {
    public Move (Square from, Square to, Class<? extends  Piece> promotionPieceType, boolean isCapture, boolean isEnPassant, SpecialMove specialMove) {
        this(from, to, promotionPieceType, isCapture, isEnPassant, specialMove, false);
    }

    public Move(int from, int to, Class<? extends Piece> promotionPieceType, boolean isCapture, boolean isEnPassant, SpecialMove specialMove) {
        this(from, to, promotionPieceType, isCapture, isEnPassant, specialMove, false);
    }

    public Move(Square from, Square to, Class<? extends Piece> promotionPieceType, boolean isCapture, boolean isEnPassant, SpecialMove specialMove, boolean isCheck) {
        this(from.getIndex(),
                to.getIndex(),
                promotionPieceType,
                isCapture,
                isEnPassant,
                specialMove,
                isCheck
        );
    }

    @Override
    public boolean equals(Object other) {
        if (other == null) return false;
        if (other == this) return true;
        if (!(other instanceof Move otherMove)) return false;
        if (otherMove.toIndex != toIndex) return false;
        if (otherMove.fromIndex != fromIndex) return false;
        if (otherMove.isEnPassant != isEnPassant) return false;
        if (otherMove.isCapture != isCapture) return false;
        return otherMove.specialMove == specialMove;
    }

    public Square from() {
        return new Square(fromIndex);
    }

    public Square to() {
        return new Square(toIndex);
    }

    @Override
    public String toString() {
        return String.format("Move(from=%s, to=%s, isCapture=%s, isEnPassant=%s, specialMove=%s, isCheck=%s)", from(), to(), isCapture, isEnPassant, specialMove, isCheck);
    }

    public String toShortString() {
        return toShortString(false);
    }

    public String toShortString(boolean upperCase) {
        return String.format("%s%s", Square.toString(fromIndex, upperCase), Square.toString(toIndex, upperCase));
    }

    /**
     * Copies the move and sets the promotion type
     *
     * @param pieceType the promotion type of the copy
     * @return a new Move object with the promotion type set
     */
    public Move withPromotionType(Class<? extends Piece> pieceType) {
        return new Move(
                fromIndex,
                toIndex,
                pieceType,
                isCapture,
                isEnPassant,
                specialMove,
                isCheck
        );
    }

    /**
     * Copies the move and sets the isCheck flag of the result
     * @param isCheck the isCheck flag of the result
     * @return a new move object with the given isCheck flag
     */
    public Move withCheck(boolean isCheck) {
        return new Move(
                fromIndex,
                toIndex,
                promotionPieceType,
                isCapture,
                isEnPassant,
                specialMove,
                isCheck
        );
    }
}
