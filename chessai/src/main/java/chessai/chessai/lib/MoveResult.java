package chessai.chessai.lib;

public record MoveResult(
		BitMap moveTargets,
		BitMap isResultCapture,
		BitMap attackTargetsWithoutEnemyKingOnBoard,
		BitMap isResultPromotion,
		BitMap isResultEnPassant,
		BitMap isResultDoublePawnMove,
		BitMap isResultKingSideCastle,
		BitMap isResultQueenSideCastle,
		BitMap pinMap
) {

	public MoveResult() {
		this(new BitMap(0),
				new BitMap(0),
				new BitMap(0),
				new BitMap(0),
				new BitMap(0),
				new BitMap(0),
				new BitMap(0),
				new BitMap(0),
				new BitMap(0));
	}

}
