package chessai.chessai.engine;

import chessai.chessai.lib.*;
import chessai.chessai.lib.pieces.*;

import java.security.InvalidKeyException;
import java.util.*;

public class MinimaxEngine extends ChessEngine {

	private static final float POSITION_MAP_WEIGHT = 10f;
	private final int maxDepth;
	private final TranspositionTable transpositionTable;
	private final int[][] historicalBestMovesCount;

	public MinimaxEngine(int maxDepth) {
		this.maxDepth = maxDepth;
		this.transpositionTable = new TranspositionTable(1_000_000_000);
		historicalBestMovesCount = new int[64][64];
	}

	@Override
	public Optional<Move> makeMove(Board board) {

		Optional<Move> bestMove = Optional.empty();

		for (int i = 0; i < maxDepth; i++) {
			bestMove = search(board, i);
		}

		return bestMove;
	}

	public Optional<Move> search(Board board, int depth) {
		transpositionTable.clear();

		List<Move> possiblyImmutableLegalMoves = board.getLegalMoves();

		if (possiblyImmutableLegalMoves.isEmpty())
			return Optional.empty();

		if (possiblyImmutableLegalMoves.size() == 1)
			return Optional.of(possiblyImmutableLegalMoves.get(0));
//
//		ArrayList<Board> boards = new ArrayList<>(possiblyImmutableLegalMoves.size());
//
//		possiblyImmutableLegalMoves.forEach(move -> boards.add(board.makeMove(move)));

//		List<Move> possibleLegalMoves = board.withIsCheckSet(possiblyImmutableLegalMoves, boards);
		List<Move> possibleLegalMoves = new ArrayList<>(possiblyImmutableLegalMoves);

		sortMovesInPlace(possibleLegalMoves);

		int indexOfBestMove = 0;
		int bestEval = board.colorToMove == PieceColor.WHITE ? Integer.MIN_VALUE : Integer.MAX_VALUE;

		int alpha = Integer.MIN_VALUE;
		int beta = Integer.MAX_VALUE;

		for (int i = 0; i < possibleLegalMoves.size(); i++) {
			Move move = possibleLegalMoves.get(i);

			int currentEval = evaluateState(
//					boards.get(i),
					board.makeMove(possibleLegalMoves.get(i)),
					depth,
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

		System.out.println("best at depth " + depth + ": " + possibleLegalMoves.get(indexOfBestMove) + " (" + bestEval + ")");

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

		List<Move> possiblyImmutableLegalMoves = board.getLegalMoves();

//		ArrayList<Board> boards = new ArrayList<>(possiblyImmutableLegalMoves.size());

//		possiblyImmutableLegalMoves.forEach(move -> boards.add(board.makeMove(move)));

//		List<Move> possibleLegalMoves = board.withIsCheckSet(possiblyImmutableLegalMoves, boards);
		List<Move> possibleLegalMoves = new ArrayList<>(possiblyImmutableLegalMoves);

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

		int fromIndexOfBestMove = 0;
		int toIndexOfBestMove = 0;

		for (Move possibleLegalMove : possibleLegalMoves) {

			int currentEval = evaluateState(
//                    boards.get(i),
					board.makeMove(possibleLegalMove),
					depthRemaining - 1,
					!isMaximizingPlayer,
					alpha,
					beta
			);

			if (isMaximizingPlayer) {
				if (currentEval > bestEval) {
					bestEval = currentEval;
					fromIndexOfBestMove = possibleLegalMove.fromIndex();
					toIndexOfBestMove = possibleLegalMove.toIndex();
				}
				alpha = Math.max(alpha, bestEval);
			} else {
				if (currentEval < bestEval) {
					bestEval = currentEval;
					fromIndexOfBestMove = possibleLegalMove.fromIndex();
					toIndexOfBestMove = possibleLegalMove.toIndex();
				}
				beta = Math.min(beta, bestEval);
			}

			if (beta <= alpha) {
				break;
			}
		}

		historicalBestMovesCount[fromIndexOfBestMove][toIndexOfBestMove]++;

		return bestEval;
	}

	private void sortMovesInPlace(List<Move> possibleLegalMoves) {

		if (possibleLegalMoves == null || possibleLegalMoves.isEmpty() || possibleLegalMoves.size() == 1)
			return;

		Comparator<Move> moveComparator = (move1, move2) -> {

			if (move1.isCapture() && !move2.isCapture())
				return -1;
			if (move2.isCapture())
				return 1;

			if (move1.promotionPieceType() != null && move2.promotionPieceType() == null)
				return -1;
			if (move2.promotionPieceType() != null)
				return 1;

			if (move1.isCheck() && !move2.isCheck())
				return -1;
			if (move2.isCheck())
				return 1;

			int historicalScoreOfMove1 = historicalBestMovesCount[move1.fromIndex()][move1.toIndex()];
			int historicalScoreOfMove2 = historicalBestMovesCount[move2.fromIndex()][move2.toIndex()];

			return historicalScoreOfMove2 - historicalScoreOfMove1;
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

			final int maxValue = Arrays.stream(positionMap).max().getAsInt();

			// we normalize the values
			final float valueFromPositionMap = (float) positionMap[positionMapIndex] / maxValue;

			pieceValue += (int) Math.floor(POSITION_MAP_WEIGHT * valueFromPositionMap);

			result += isPieceWhite ? pieceValue : -pieceValue;
		}

		return result;
	}
}
