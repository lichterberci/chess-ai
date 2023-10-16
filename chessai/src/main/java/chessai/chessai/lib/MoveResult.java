package chessai.chessai.lib;

public record MoveResult(
		BitMap moveTargets,
		BitMap isResultCapture,
		BitMap attackTargetsWithoutEnemyKingOnBoard,
		BitMap isResultPromotion,
		BitMap isResultDoublePawnMove,
		BitMap isResultKingSideCastle,
		BitMap isResultQueenSideCastle
) {

	public MoveResult() {
		this(new BitMap(0),
				new BitMap(0),
				new BitMap(0),
				new BitMap(0),
				new BitMap(0),
				new BitMap(0),
				new BitMap(0));
	}

}
