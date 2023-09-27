package chessai.chessai.lib;


import org.jetbrains.annotations.Nullable;

import java.util.Objects;

public record Move(Square from, Square to, @Nullable Class<? extends  Piece> promotionPieceType, boolean isCapture, boolean isEnPassant, @Nullable SpecialMove specialMove) {
    public Move (Square from, Square to, Class<? extends  Piece> promotionPieceType, boolean isCapture, boolean isEnPassant, SpecialMove specialMove) {
        this.from = from.copy();
        this.to = to.copy();
        this.promotionPieceType = promotionPieceType;
        this.isCapture = isCapture;
        this.isEnPassant = isEnPassant;
        this.specialMove = specialMove;
    }

    public boolean equals(Object other) {
        if (other == null) return false;
        if (other == this) return true;
        if (!(other instanceof Move otherMove)) return false;
        if (!Objects.equals(otherMove.to(), to)) return false;
        if (!Objects.equals(otherMove.from(), from)) return false;
        if (otherMove.isEnPassant != isEnPassant) return false;
        if (otherMove.isCapture != isCapture) return false;
        return otherMove.specialMove == specialMove;
    }
}
