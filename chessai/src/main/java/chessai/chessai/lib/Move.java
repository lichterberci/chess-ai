package chessai.chessai.lib;


import org.jetbrains.annotations.Nullable;

/**
 * Format: <a href="https://www.chessprogramming.org/Encoding_Moves#Extended_Move_Structure">Format</a>
 * @param from
 * @param to
 */
public record Move(Square from, Square to, @Nullable Class<? extends  Piece> promotionPieceType, boolean isCapture, boolean isEnPassant, @Nullable SpecialMove specialMove) {
}
