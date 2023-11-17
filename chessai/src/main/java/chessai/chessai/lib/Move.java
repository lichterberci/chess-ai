package chessai.chessai.lib;


import org.jetbrains.annotations.Nullable;

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
