package chessai.chessai.lib;

public record MoveResult(
		BitMap moveTargets,
		BitMap isResultCapture,
		BitMap attackTargetsWithoutEnemyKingOnBoard,
		BitMap isResultPromotion,
		BitMap squaresWherePromotionTakesPlace,
		BitMap isResultDoublePawnMove,
		BitMap isResultKingSideCastle,
		BitMap isResultQueenSideCastle) {
}
