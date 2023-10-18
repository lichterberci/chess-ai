package chessai.chessai.lib;

public record MoveResult(
		BitMap moveTargets,
		BitMap isResultCapture,
		BitMap attackTargetsWhilePretendingTheEnemyKingIsNotThere,
		BitMap isResultPromotion,
		BitMap isResultEnPassant,
		BitMap isResultDoublePawnMove,
		BitMap isResultKingSideCastle,
		BitMap isResultQueenSideCastle,
		BitMap pinMap,
		BitMap checkTrack
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
				new BitMap(0),
				new BitMap(0));
	}

}
