package chessai.chessai.lib;

/**
 * This is a type of piece (namely: rook, bishop, queen)
 */
public abstract class SlidingPiece extends Piece {

	protected SlidingPiece(PieceColor color) {
		super(color);
	}

	/**
	 * This is used during move generation to calculate the pseudo-legal moves
	 */
	protected static void slide(int currentFile,
	                            int currentRow,
	                            BitMap otherColorPieces,
	                            BitMap sameColorPieces,
	                            BitMap enPassantPawn,
	                            int enPassantTargetIndex,
	                            BitMap otherColorKing,
	                            MoveResult result,
	                            int fileOffset,
	                            int rowOffset) {

		boolean canMove = true;
		boolean canPin = true;
		boolean canAttack = true;
		/*
		 * En passant pinning:
		 * -------------------
		 * Only occurs, when two pawns are on the same rank
		 * and one of them has just moved 2 squares (and thus can be captured via en passant).
		 * We also need a king of the color of the capturing pawn and a rook or queen to stare
		 * down this row. The order of the pawns is not important.
		 * If an en passant move would occur, both pawns would disappear from this row,
		 * revealing an attack.
		 *
		 * We track the kings, the en passant pawn (the one that moved 2 squares) and ourself.
		 * We need an order of
		 * Rook/Queen - our en passant pawn - enemy piece - enemy king
		 * or
		 * Rook/Queen - enemy piece - our en passant pawn - enemy king
		 *
		 * We need to realized that it doesn't matter, what kind of enemy piece it is, as long as it is not a king.
		 * If it is a pawn, tha case is trivial, otherwise there is no pawn to capture via en passant.
		 *
		 * For implementing this check, we can track whether we have passed our en passant pawn and an enemy piece before
		 * arriving at the enemy king.
		 * */
		boolean haveWePassedAnEnemyPieceForEnPassantPin = false;
		boolean haveWePassedOurEnPassantPawnForEnPassantPin = false;

		// this will be or-d to the pin map
		// we add the current position to the pin map, so we will know that by capturing this piece, we can eliminate the pin
		BitMap pinTrace = new BitMap(0).setBit(Square.getIndex(currentFile, currentRow), true);

		BitMap checkTrack = new BitMap(0).setBit(Square.getIndex(currentFile, currentRow), true);

		for (int i = 1; i < 8; i++) {

			final int squareIndex = Square.getIndex(currentFile + i * fileOffset, currentRow + i * rowOffset);

			// we moved out of the board
			if (squareIndex == -1)
				return;

			// our friendly piece is in the way
			if (sameColorPieces.getBit(squareIndex)) {

				// we are defending this square from the enemy king, if we can
				if (canAttack)
					result.attackTargetsWhilePretendingTheEnemyKingIsNotThere().setBitInPlace(squareIndex, true);

				if (enPassantPawn.isZero()) // no en passants the previous move
					return;
				else if (!enPassantPawn.getBit(squareIndex)) // there was an en passant but not here
					return;
				else if (!canMove && haveWePassedOurEnPassantPawnForEnPassantPin) // we hit a second piece of our own
					return;
				else {
					// there was an en passant here, and it was ours
					canPin = false;
					canMove = false;
					canAttack = false;
					haveWePassedOurEnPassantPawnForEnPassantPin = true;
				}
			}

			handleNonPieceInteractions(result, canMove, canPin, canAttack, pinTrace, checkTrack, squareIndex);

			// an enemy piece is in the way
			if (otherColorPieces.getBit(squareIndex)) {

				if (canMove) {
					result.isResultCapture().setBitInPlace(squareIndex, true);

					// we hit a piece, we can move no further
					canMove = false;

					// if it is the king, we can attack behind it
					if (otherColorKing.getBit(squareIndex)) {
						canPin = false; // we can at least still attack behind the king
						result.checkTrack().orInPlace(checkTrack); // we are giving check
					} else {
						canAttack = false; // we can at least still pin this piece to the king
						haveWePassedAnEnemyPieceForEnPassantPin = true;
					}
				} else if (canPin && otherColorKing.getBit(squareIndex)) {
					result.pinMap().orInPlace(pinTrace);
					return;
				} else if (
						haveWePassedOurEnPassantPawnForEnPassantPin
								&& haveWePassedAnEnemyPieceForEnPassantPin
								&& otherColorKing.getBit(squareIndex)
				) {
					result.isEnPassantTargetUnCapturableBecausePin().setBitInPlace(enPassantTargetIndex, true);
					return;
				} else if (
						haveWePassedOurEnPassantPawnForEnPassantPin
								&& !otherColorKing.getBit(squareIndex)
								&& !haveWePassedAnEnemyPieceForEnPassantPin
				) {
					// we have passed our pawn and we are passing an enemy (not king) piece
					haveWePassedAnEnemyPieceForEnPassantPin = true;
				} else {
					return;
				}
			}
		}
	}

	private static void handleNonPieceInteractions(MoveResult result, boolean canMove, boolean canPin, boolean canAttack, BitMap pinTrace, BitMap checkTrack, int squareIndex) {
		// we can attack this square
		if (canAttack)
			result.attackTargetsWhilePretendingTheEnemyKingIsNotThere().setBitInPlace(squareIndex, true);

		// we are at least able to pin
		if (canPin)
			pinTrace.setBitInPlace(squareIndex, true);

		// if we are not behind the king, we can move there
		if (canMove) {
			result.moveTargets().setBitInPlace(squareIndex, true);
			checkTrack.setBitInPlace(squareIndex, true);
		}
	}

}
