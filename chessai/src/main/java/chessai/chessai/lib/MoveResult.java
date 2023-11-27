package chessai.chessai.lib;

/**
 * This is a helper class that is returned by the piece when generating the pseudo legal moves.
 *
 * @param moveTargets                                        the squares, this piece can move to
 * @param isResultCapture                                    the squares, this piece can capture
 * @param attackTargetsWhilePretendingTheEnemyKingIsNotThere the squares that this piece can attack (had the enemy king not been there)
 * @param isResultPromotion                                  the squares, onto which a move would result in a promotion
 * @param isResultEnPassant                                  the squares, onto which a move would result in an en passant
 * @param isResultDoublePawnMove                             the squares, onto which a move would result in a double pawn push
 * @param isResultKingSideCastle                             the squares, onto which a move would result in a king side castle
 * @param isResultQueenSideCastle                            the squares, onto which a move would result in a queen side castle
 * @param pinMap                                             the piece, this piece pins
 * @param checkTrack                                         the track of the check this piece causes
 * @param isEnPassantTargetUnCapturableBecausePin            the squares that are uncapturable en passant targets
 */
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
		BitMap checkTrack,
		BitMap isEnPassantTargetUnCapturableBecausePin
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
				new BitMap(0),
				new BitMap(0));
	}

}
