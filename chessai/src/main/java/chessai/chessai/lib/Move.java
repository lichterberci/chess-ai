package chessai.chessai.lib;


import org.jetbrains.annotations.Nullable;

public record Move(
        int fromIndex,
        int toIndex,
        @Nullable Class<? extends Piece> promotionPieceType,
        boolean isCapture,
        boolean isEnPassant,
        @Nullable SpecialMove specialMove
) {
    public Move (Square from, Square to, Class<? extends  Piece> promotionPieceType, boolean isCapture, boolean isEnPassant, SpecialMove specialMove) {
        this(from.getIndex(),
                to.getIndex(),
                promotionPieceType,
                isCapture,
                isEnPassant,
                specialMove
        );
    }

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
}
