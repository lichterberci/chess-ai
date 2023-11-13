package chessai.chessai.engine;

import chessai.chessai.lib.*;
import chessai.chessai.lib.pieces.*;

import java.security.InvalidKeyException;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;

public class MinimaxEngine extends ChessEngine {

	private static final float POSITION_MAP_WEIGHT = 0.3f;
	final int maxDepth;
	private final TranspositionTable transpositionTable;

	public MinimaxEngine(int maxDepth) {
		this.maxDepth = maxDepth;
		this.transpositionTable = new TranspositionTable(1_000_000_000);
	}

	@Override
	public Optional<Move> makeMove(Board board) {

		transpositionTable.clear();

		List<Move> possibleLegalMoves = board.getLegalMoves();

		if (possibleLegalMoves.isEmpty())
			return Optional.empty();

		if (possibleLegalMoves.size() == 1)
			return Optional.of(possibleLegalMoves.get(0));

		sortMovesInPlace(possibleLegalMoves);

		int indexOfBestMove = 0;
		int bestEval = board.colorToMove == PieceColor.WHITE ? Integer.MIN_VALUE : Integer.MAX_VALUE;

		int alpha = Integer.MIN_VALUE;
		int beta = Integer.MAX_VALUE;

		for (int i = 0; i < possibleLegalMoves.size(); i++) {
			Move move = possibleLegalMoves.get(i);

			int currentEval = evaluateState(
					board.makeMove(move),
					maxDepth,
					board.colorToMove == PieceColor.BLACK,
					alpha,
					beta
			);

			System.out.println(move + " --> " + currentEval);

			if (board.colorToMove == PieceColor.WHITE) {

				if (bestEval < currentEval) {
					bestEval = currentEval;
					indexOfBestMove = i;
				}

				alpha = Math.max(alpha, bestEval);
			} else {

				if (bestEval > currentEval) {
					bestEval = currentEval;
					indexOfBestMove = i;
				}

				beta = Math.min(beta, bestEval);
			}

			if (beta <= alpha)
				break;
		}

		System.out.println("best: " + possibleLegalMoves.get(indexOfBestMove) + " (" + bestEval + ")");

		return Optional.of(possibleLegalMoves.get(indexOfBestMove));
	}

	private int evaluateState(Board board, int depthRemaining, boolean isMaximizingPlayer, int alpha, int beta) {

		if (transpositionTable.contains(board)) {
			try {
				return transpositionTable.get(board);
			} catch (InvalidKeyException e) {
				// we just don't return
				System.err.println("Invalid key!");
			}
		}

		List<Move> possibleLegalMoves = board.getLegalMoves();

		sortMovesInPlace(possibleLegalMoves);

		GameState state = board.getState();

		if (state != GameState.PLAYING || depthRemaining <= 0) {

			int result = switch (state) {
				case WHITE_WIN -> Integer.MAX_VALUE;
				case BLACK_WIN -> Integer.MIN_VALUE;
				case DRAW -> 0;
				case PLAYING -> evaluateOngoingPosition(board);
			};

			transpositionTable.put(board, result);

			return result;
		}

		if (possibleLegalMoves.isEmpty())
			throw new IllegalStateException("There has to be at least one legal move!");

		int bestEval = isMaximizingPlayer ? Integer.MIN_VALUE : Integer.MAX_VALUE;

		for (Move move : possibleLegalMoves) {

			int currentEval = evaluateState(
					board.makeMove(move),
					depthRemaining - 1,
					!isMaximizingPlayer,
					alpha,
					beta
			);

			if (isMaximizingPlayer) {
				bestEval = Math.max(currentEval, bestEval);
				alpha = Math.max(alpha, bestEval);
			} else {
				bestEval = Math.min(currentEval, bestEval);
				beta = Math.min(beta, bestEval);
			}

			if (beta <= alpha) {
				break;
			}
		}

		return bestEval;
	}

	private void sortMovesInPlace(List<Move> possibleLegalMoves) {

		if (possibleLegalMoves == null || possibleLegalMoves.isEmpty() || possibleLegalMoves.size() == 1)
			return;

		Comparator<Move> moveComparator = (move1, move2) -> {

			if (move1.isCapture())
				return -1;
			if (move2.isCapture())
				return 1;

			if (move1.promotionPieceType() != null)
				return -1;
			if (move2.promotionPieceType() != null)
				return 1;

			return 0;
		};

		try {
			possibleLegalMoves.sort(moveComparator);
		} catch (UnsupportedOperationException e) {
			throw new RuntimeException(e);
		}

	}

	private int evaluateOngoingPosition(Board board) {

		final int pawnValue = 100;
		final int knightValue = 300;
		final int bishopValue = 320;
		final int rookValue = 500;
		final int queenValue = 900;

		int result = 0;


		// note: all of these maps are for white (for black, we look at them from the other side)

		final int[] pawnPositionMap = new int[]{
				0, 0, 0, 0, 0, 0, 0, 0,
				60, 60, 60, 60, 60, 60, 60, 60,
				50, 50, 50, 50, 50, 50, 50, 50,
				35, 40, 40, 40, 40, 40, 40, 35,
				20, 20, 30, 35, 35, 30, 20, 20,
				10, 15, 20, 15, 15, 20, 15, 10,
				20, 20, 10, 10, 10, 10, 20, 20,
				0, 0, 0, 0, 0, 0, 0, 0
		};

		final int[] knightPositionMap = new int[]{
				10, 10, 10, 10, 10, 10, 10, 10,
				10, 30, 30, 30, 30, 30, 30, 10,
				10, 30, 40, 40, 40, 40, 30, 10,
				10, 30, 40, 50, 50, 40, 30, 10,
				10, 30, 40, 50, 50, 40, 30, 10,
				10, 30, 40, 40, 40, 40, 30, 10,
				10, 30, 30, 30, 30, 30, 30, 10,
				10, 10, 10, 10, 10, 10, 10, 10
		};

		final int[] bishopPositionMap = new int[]{
				20, 10, 10, 10, 10, 10, 10, 20,
				10, 20, 30, 30, 30, 30, 20, 10,
				10, 30, 40, 40, 40, 40, 30, 10,
				10, 40, 45, 50, 50, 45, 40, 10,
				10, 40, 45, 50, 50, 45, 40, 10,
				20, 30, 40, 45, 45, 40, 30, 20,
				20, 50, 30, 30, 30, 30, 40, 20,
				20, 10, 10, 10, 10, 10, 10, 20
		};

		final int[] rookPositionMap = new int[]{
				40, 45, 45, 45, 45, 45, 45, 40,
				50, 50, 50, 60, 60, 50, 50, 50,
				20, 30, 40, 40, 40, 40, 30, 20,
				10, 40, 45, 50, 50, 45, 40, 10,
				10, 40, 45, 50, 50, 45, 40, 10,
				10, 30, 40, 45, 45, 40, 30, 10,
				10, 10, 20, 30, 30, 20, 10, 10,
				20, 20, 30, 40, 40, 30, 20, 20
		};

		final int[] queenPositionMap = new int[]{
				40, 45, 45, 45, 45, 45, 45, 40,
				50, 50, 50, 60, 60, 50, 50, 50,
				20, 30, 40, 40, 40, 40, 30, 20,
				10, 40, 45, 50, 50, 45, 40, 10,
				10, 40, 45, 50, 50, 45, 40, 10,
				10, 30, 40, 45, 45, 40, 30, 10,
				10, 10, 20, 40, 40, 20, 10, 10,
				20, 20, 30, 40, 40, 30, 20, 20
		};

		final int[] kingPositionMap = new int[]{
				0, 0, 0, 0, 0, 0, 0, 0,
				0, 0, 0, 0, 0, 0, 0, 0,
				0, 0, 0, 0, 0, 0, 0, 0,
				0, 0, 0, 0, 0, 0, 0, 0,
				0, 0, 0, 0, 0, 0, 0, 0,
				0, 0, 0, 0, 0, 0, 0, 0,
				20, 20, 10, 10, 10, 10, 20, 20,
				30, 30, 10, 10, 10, 10, 30, 30,
		};

		for (int i = 0; i < 64; i++) {

			Piece piece = board.get(i);

			if (piece == null)
				continue;

			boolean isPieceWhite = piece.getColor() == PieceColor.WHITE;

			int pieceValue = 0;

			int[] positionMap = kingPositionMap;

			if (piece.getClass().equals(Pawn.class)) {
				pieceValue = pawnValue;
				positionMap = pawnPositionMap;
			} else if (piece.getClass().equals(Knight.class)) {
				pieceValue = knightValue;
				positionMap = knightPositionMap;
			} else if (piece.getClass().equals(Bishop.class)) {
				pieceValue = bishopValue;
				positionMap = bishopPositionMap;
			} else if (piece.getClass().equals(Rook.class)) {
				pieceValue = rookValue;
				positionMap = rookPositionMap;
			} else if (piece.getClass().equals(Queen.class)) {
				pieceValue = queenValue;
				positionMap = queenPositionMap;
			}

			final int positionMapIndex = isPieceWhite ? i : Square.getIndex(i % 8, 7 - i / 8);

			pieceValue += (int) Math.floor(POSITION_MAP_WEIGHT * positionMap[positionMapIndex]);

			result += isPieceWhite ? pieceValue : -pieceValue;
		}

		return result;
	}
}
