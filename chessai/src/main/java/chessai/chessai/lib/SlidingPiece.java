package chessai.chessai.lib;

public abstract class SlidingPiece extends Piece {

	protected SlidingPiece(PieceColor color) {
		super(color);
	}

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
		boolean weArePinningOurOwnEnPassantPawn = false;

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
				else if (!canMove) // we hit a second piece of our own
					return;
				else {
					canPin = false;
					canMove = false;
					canAttack = false;
					weArePinningOurOwnEnPassantPawn = true;
				}
			}

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

			// an enemy piece is in the way
			if (otherColorPieces.getBit(squareIndex)) {

				if (canMove) {
					result.isResultCapture().setBitInPlace(squareIndex, true);

					// we hit a piece, we can move no further
					canMove = false;

					// if it is not the king, we cannot attack behind it
					if (!otherColorKing.getBit(squareIndex)) {
						canAttack = false; // we can at least still pin this piece to the king
					} else {
						canPin = false; // we can at least still attack behind the king
						result.checkTrack().orInPlace(checkTrack); // we are giving check
					}
				} else if (canPin && otherColorKing.getBit(squareIndex)) {
					result.pinMap().orInPlace(pinTrace);
					return;
				} else if (weArePinningOurOwnEnPassantPawn && otherColorKing.getBit(squareIndex)) {
					result.isEnPassantTargetUnCapturableBecausePin().setBitInPlace(enPassantTargetIndex, true);
					return;
				} else {
					return;
				}
			}
		}
	}

}
