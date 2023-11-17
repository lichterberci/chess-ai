package chessai.chessai.engine;

import chessai.chessai.lib.*;
import chessai.chessai.lib.pieces.*;

import java.security.InvalidKeyException;
import java.util.*;

public class MinimaxEngine extends ChessEngine {

	private static final float POSITION_MAP_WEIGHT = 10f;
	private static final float ATTACK_SQUARE_WEIGHT = 5f;
	private final int maxDepth;
	private final TranspositionTable transpositionTable;
	private final int[][] historicalBestMovesCount;
	/**
	 * @implSpec <a href="https://www.chessprogramming.org/Triangular_PV-Table">chess programming wiki page</a>
	 */
	private Move[] pvTable;
	private Move[] prevPvTable;
	private int currentMaxDepth;

	public MinimaxEngine(int maxDepth) {
		this(maxDepth, 1_000_000_000);
	}

	public MinimaxEngine(int maxDepth, int transpositionTableCapacityInBytes) {
		this.maxDepth = maxDepth;
		this.transpositionTable = new TranspositionTable(transpositionTableCapacityInBytes);
		historicalBestMovesCount = new int[64][64];
	}

	@Override
	public Optional<Move> makeMove(Board board) {

		Optional<Move> bestMove = Optional.empty();

		for (int i = 1; i <= maxDepth; i++) {
			transpositionTable.clear();
			currentMaxDepth = i;
			bestMove = search(board, i);
		}

		return bestMove;
	}

	public Optional<Move> search(Board board, int depth) {

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
		int pvTablSize = Math.ceilDiv((maxDepth + 1) * (maxDepth + 2), 2);
		prevPvTable = pvTable != null ? pvTable : new Move[pvTablSize];
		pvTable = new Move[pvTablSize];

		List<Move> possibleLegalMoves = new ArrayList<>(possiblyImmutableLegalMoves);

		sortMovesInPlace(possibleLegalMoves, 0);

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
					beta,
					0
			);

			if (depth == maxDepth)
				System.out.printf("%s --> %d%n", move.toShortString(), currentEval);

			if (board.colorToMove == PieceColor.WHITE) {

				if (bestEval < currentEval) {
					bestEval = currentEval;
					indexOfBestMove = i;
				}

				if (bestEval > alpha) {
					alpha = bestEval;
				}
			} else {

				if (bestEval > currentEval) {
					bestEval = currentEval;
					indexOfBestMove = i;
				}

				if (bestEval < beta) {
					beta = bestEval;
				}
			}

			if (beta <= alpha)
				break;
		}

		if (depth == maxDepth)
			System.out.printf("best at depth %d: %s (%d)%n", depth, possibleLegalMoves.get(indexOfBestMove), bestEval);

		return Optional.of(possibleLegalMoves.get(indexOfBestMove));
	}

	private int evaluateState(Board board,
							  int depthRemaining,
							  boolean isMaximizingPlayer,
							  int alpha,
							  int beta,
							  int pvIndex) {

		if (transpositionTable.contains(board)) {
			try {
				return transpositionTable.get(board);
			} catch (InvalidKeyException e) {
				// we just don't return
				System.err.println("Invalid key!");
			}
		}

		pvTable[pvIndex] = null;

		int pvNextIndex = pvIndex + depthRemaining;

		List<Move> possiblyImmutableLegalMoves = board.getLegalMoves();

//		ArrayList<Board> boards = new ArrayList<>(possiblyImmutableLegalMoves.size());

//		possiblyImmutableLegalMoves.forEach(move -> boards.add(board.makeMove(move)));

//		List<Move> possibleLegalMoves = board.withIsCheckSet(possiblyImmutableLegalMoves, boards);
		List<Move> possibleLegalMoves = new ArrayList<>(possiblyImmutableLegalMoves);

		sortMovesInPlace(possibleLegalMoves, pvIndex);

		GameState state = board.getState();

		if (state != GameState.PLAYING || depthRemaining <= 0) {

			int result = switch (state) {
				case WHITE_WIN -> Integer.MAX_VALUE - currentMaxDepth;
				case BLACK_WIN -> Integer.MIN_VALUE + currentMaxDepth;
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

		for (Move move : possibleLegalMoves) {

			int currentEval = evaluateState(
//                    boards.get(i),
					board.makeMove(move),
					depthRemaining - 1,
					!isMaximizingPlayer,
					alpha,
					beta,
					pvNextIndex
			);

			if (isMaximizingPlayer) {
				if (currentEval > bestEval) {
					bestEval = currentEval;
					pvTable[pvIndex] = move;
					copyPvTableSegment(pvIndex + 1, pvNextIndex, depthRemaining + 1);
					fromIndexOfBestMove = move.fromIndex();
					toIndexOfBestMove = move.toIndex();
				}
				alpha = Math.max(alpha, bestEval);
			} else {
				if (currentEval < bestEval) {
					bestEval = currentEval;
					pvTable[pvIndex] = move;
					copyPvTableSegment(pvIndex + 1, pvNextIndex, depthRemaining + 1);
					fromIndexOfBestMove = move.fromIndex();
					toIndexOfBestMove = move.toIndex();
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

	private void copyPvTableSegment(int toIndex, int fromIndex, int length) {
		System.arraycopy(pvTable, fromIndex, pvTable, toIndex, length);
	}

	private void sortMovesInPlace(List<Move> possibleLegalMoves, int pvIndex) {

		if (possibleLegalMoves == null || possibleLegalMoves.isEmpty() || possibleLegalMoves.size() == 1)
			return;

		Comparator<Move> moveComparator = (move1, move2) -> {

			if (move1.equals(prevPvTable[pvIndex]) && !move2.equals(prevPvTable[pvIndex]))
				return -1;
			if (move2.equals(prevPvTable[pvIndex]))
				return 1;

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

			final int historicalScoreOfMove1 = historicalBestMovesCount[move1.fromIndex()][move1.toIndex()];
			final int historicalScoreOfMove2 = historicalBestMovesCount[move2.fromIndex()][move2.toIndex()];

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
				50, 50, 50, 55, 55, 50, 50, 50,
				35, 40, 40, 50, 50, 40, 40, 35,
				20, 20, 30, 45, 45, 30, 20, 20,
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

		if (board.whiteAttackSquares == null || board.blackAttackSquares == null)
			board.generateAttackSquare();

		int attackSquareSumDifference = 0;

		for (int i = 0; i < 64; i++) {
			attackSquareSumDifference += board.whiteAttackSquares.getBit(i) ? 1 : 0;
			attackSquareSumDifference -= board.blackAttackSquares.getBit(i) ? 1 : 0;
		}

		result += ATTACK_SQUARE_WEIGHT * attackSquareSumDifference;

		return result;
	}
}
