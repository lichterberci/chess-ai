package chessai.chessai.lib;

public abstract class SlidingPiece extends Piece {

	protected SlidingPiece(PieceColor color) {
		super(color);
	}

	protected static void slide(int currentFile, int currentRow, BitMap otherColorPieces, BitMap sameColorPieces, BitMap otherColorKing, MoveResult result, int fileOffset, int rowOffset) {

		final int currentSquareIndex = Square.getIndex(currentFile, currentRow);

		boolean onlyAttacks = false;

		for (int i = 0; i < 8; i++) {

			final int squareIndex = currentSquareIndex + (fileOffset + rowOffset * 8);

			// we moved out of the board
			if (squareIndex == -1)
				return;

			// out friendly piece is in the way
			if (sameColorPieces.getBit(squareIndex))
				return;

			// we can attack this square
			result.attackTargetsWithoutEnemyKingOnBoard().setBitInPlace(squareIndex, true);

			// if we are not behind the king, we can move there
			if (!onlyAttacks)
				result.moveTargets().setBitInPlace(squareIndex, true);

			// an enemy piece is in the way
			if (otherColorPieces.getBit(squareIndex)) {

				result.moveTargets().setBitInPlace(squareIndex, true);
				result.isResultCapture().setBitInPlace(squareIndex, true);

				// if it is not the king, we return
				if (!otherColorKing.getBit(squareIndex))
					return;

				// if it way the enemy king, we can attack behind him as well
				onlyAttacks = true;
			}
		}
	}

}
