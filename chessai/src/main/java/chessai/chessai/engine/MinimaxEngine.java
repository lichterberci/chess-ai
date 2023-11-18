package chessai.chessai.engine;

import chessai.chessai.lib.*;
import chessai.chessai.lib.pieces.*;

import java.security.InvalidKeyException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.function.BooleanSupplier;
import java.util.function.Consumer;

public class MinimaxEngine extends ChessEngine {

	private static final float POSITION_MAP_WEIGHT = 10f;
	private static final float ATTACK_SQUARE_WEIGHT = 5f;
	private static final int PAWN_VALUE = 100;
	private static final int KNIGHT_VALUE = 300;
	private static final int BISHOP_VALUE = 320;
	private static final int ROOK_VALUE = 500;
	private static final int QUEEN_VALUE = 900;
	// note: all of these maps are for white (for black, we look at them from the other side)
	private static final int[] PAWN_POSITION_MAP = new int[]{
			0, 0, 0, 0, 0, 0, 0, 0,
			60, 60, 60, 60, 60, 60, 60, 60,
			50, 50, 50, 55, 55, 50, 50, 50,
			35, 40, 40, 50, 50, 40, 40, 35,
			20, 20, 30, 45, 45, 30, 20, 20,
			10, 15, 20, 15, 15, 20, 15, 10,
			20, 20, 10, 10, 10, 10, 20, 20,
			0, 0, 0, 0, 0, 0, 0, 0
	};
	private static final int[] KNIGHT_POSITION_MAP = new int[]{
			10, 10, 10, 10, 10, 10, 10, 10,
			10, 30, 30, 30, 30, 30, 30, 10,
			10, 30, 40, 40, 40, 40, 30, 10,
			10, 30, 40, 50, 50, 40, 30, 10,
			10, 30, 40, 50, 50, 40, 30, 10,
			10, 30, 40, 40, 40, 40, 30, 10,
			10, 30, 30, 30, 30, 30, 30, 10,
			10, 10, 10, 10, 10, 10, 10, 10
	};
	private static final int[] BISHOP_POSITION_MAP = new int[]{
			20, 10, 10, 10, 10, 10, 10, 20,
			10, 20, 30, 30, 30, 30, 20, 10,
			10, 30, 40, 40, 40, 40, 30, 10,
			10, 40, 45, 50, 50, 45, 40, 10,
			10, 40, 45, 50, 50, 45, 40, 10,
			20, 30, 40, 45, 45, 40, 30, 20,
			20, 50, 30, 30, 30, 30, 40, 20,
			20, 10, 10, 10, 10, 10, 10, 20
	};
	private static final int[] ROOK_POSITION_MAP = new int[]{
			40, 45, 45, 45, 45, 45, 45, 40,
			50, 50, 50, 60, 60, 50, 50, 50,
			20, 30, 40, 40, 40, 40, 30, 20,
			10, 40, 45, 50, 50, 45, 40, 10,
			10, 40, 45, 50, 50, 45, 40, 10,
			10, 30, 40, 45, 45, 40, 30, 10,
			10, 10, 20, 30, 30, 20, 10, 10,
			20, 20, 30, 40, 40, 30, 20, 20
	};
	private static final int[] QUEEN_POSITION_MAP = new int[]{
			40, 45, 45, 45, 45, 45, 45, 40,
			50, 50, 50, 60, 60, 50, 50, 50,
			20, 30, 40, 40, 40, 40, 30, 20,
			10, 40, 45, 50, 50, 45, 40, 10,
			10, 40, 45, 50, 50, 45, 40, 10,
			10, 30, 40, 45, 45, 40, 30, 10,
			10, 10, 20, 40, 40, 20, 10, 10,
			20, 20, 30, 40, 40, 30, 20, 20
	};
	private static final int[] KING_POSITION_MAP = new int[]{
			0, 0, 0, 0, 0, 0, 0, 0,
			0, 0, 0, 0, 0, 0, 0, 0,
			0, 0, 0, 0, 0, 0, 0, 0,
			0, 0, 0, 0, 0, 0, 0, 0,
			0, 0, 0, 0, 0, 0, 0, 0,
			0, 0, 0, 0, 0, 0, 0, 0,
			20, 20, 10, 10, 10, 10, 20, 20,
			30, 30, 10, 10, 10, 10, 30, 30,
	};
	private static final int MAX_ADDITIONAL_DEPTH = 4;
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
	public Optional<Move> makeMove(Board board, Consumer<Optional<Move>> callbackAfterEachDepth, BooleanSupplier isCancelled) {

		Optional<Move> bestMove = Optional.empty();

		for (int i = 1; i <= maxDepth && !isCancelled.getAsBoolean(); i++) {
			transpositionTable.clear();
			currentMaxDepth = i;
			bestMove = search(board, i, isCancelled);
			callbackAfterEachDepth.accept(bestMove);
		}

		return bestMove;
	}

	public Optional<Move> search(Board board, int depth, BooleanSupplier isCancelled) {

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
		int pvTablSize = Math.ceilDiv((maxDepth + MAX_ADDITIONAL_DEPTH + 1) * (maxDepth + MAX_ADDITIONAL_DEPTH + 2), 2);
		prevPvTable = pvTable != null ? pvTable : new Move[pvTablSize];
		pvTable = new Move[pvTablSize];

		List<Move> possibleLegalMoves = new ArrayList<>(possiblyImmutableLegalMoves);

		sortMovesInPlace(possibleLegalMoves, 0, board);

		int indexOfBestMove = 0;
		int bestEval = board.colorToMove == PieceColor.WHITE ? Integer.MIN_VALUE : Integer.MAX_VALUE;

		int alpha = Integer.MIN_VALUE;
		int beta = Integer.MAX_VALUE;

		for (int i = 0; i < possibleLegalMoves.size() && !isCancelled.getAsBoolean(); i++) {
			Move move = possibleLegalMoves.get(i);

			int currentEval = evaluateState(
//					boards.get(i),
					board.makeMove(possibleLegalMoves.get(i)),
					0,
					0,
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
							  int depth,
							  int additionalDepth,
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

		int pvNextIndex = pvIndex + currentMaxDepth + MAX_ADDITIONAL_DEPTH - depth - additionalDepth;

		List<Move> possiblyImmutableLegalMoves = board.getLegalMoves();

//		ArrayList<Board> boards = new ArrayList<>(possiblyImmutableLegalMoves.size());

//		possiblyImmutableLegalMoves.forEach(move -> boards.add(board.makeMove(move)));

//		List<Move> possibleLegalMoves = board.withIsCheckSet(possiblyImmutableLegalMoves, boards);
		List<Move> possibleLegalMoves = new ArrayList<>(possiblyImmutableLegalMoves);

		sortMovesInPlace(possibleLegalMoves, pvIndex, board);

		GameState state = board.getState();

		if ((state != GameState.PLAYING || depth >= currentMaxDepth) && !(board.isKingInCheck(board.colorToMove) && additionalDepth < MAX_ADDITIONAL_DEPTH && state == GameState.PLAYING)) {

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
					Math.min(currentMaxDepth, depth + 1),
					depth < currentMaxDepth ? 0 : additionalDepth + 1,
					!isMaximizingPlayer,
					alpha,
					beta,
					pvNextIndex
			);

			if (isMaximizingPlayer) {
				if (currentEval > bestEval) {
					bestEval = currentEval;
					pvTable[pvIndex] = move;
					copyPvTableSegment(pvIndex + 1, pvNextIndex, currentMaxDepth + MAX_ADDITIONAL_DEPTH - depth - additionalDepth - 1);
					fromIndexOfBestMove = move.fromIndex();
					toIndexOfBestMove = move.toIndex();
				}
				alpha = Math.max(alpha, bestEval);
			} else {
				if (currentEval < bestEval) {
					bestEval = currentEval;
					pvTable[pvIndex] = move;
					copyPvTableSegment(pvIndex + 1, pvNextIndex, currentMaxDepth + MAX_ADDITIONAL_DEPTH - depth - additionalDepth - 1);
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

	private void sortMovesInPlace(List<Move> possibleLegalMoves, int pvIndex, Board board) {

		if (possibleLegalMoves == null || possibleLegalMoves.isEmpty() || possibleLegalMoves.size() == 1)
			return;

		Comparator<Move> moveComparator = (move1, move2) -> {

			Move bestMoveAtThisPly = prevPvTable[pvIndex];

			if (move1.equals(bestMoveAtThisPly) && !move2.equals(bestMoveAtThisPly))
				return -1;
			if (move2.equals(bestMoveAtThisPly))
				return 1;

			if (move1.promotionPieceType() != null && move2.promotionPieceType() == null)
				return -1;
			if (move2.promotionPieceType() != null)
				return 1;

			if (move1.isCapture() && move2.isCapture()) {

				// we consider higher value captures first

				Piece capturingPiece1 = board.get(move1.fromIndex());
				Piece capturingPiece2 = board.get(move2.fromIndex());

				int capturedIndex1 = move1.toIndex();
				if (move1.isEnPassant())
					capturedIndex1 += board.colorToMove == PieceColor.WHITE ? 8 : -8;

				int capturedIndex2 = move2.toIndex();
				if (move2.isEnPassant())
					capturedIndex2 += board.colorToMove == PieceColor.WHITE ? 8 : -8;

				Piece capturedPiece1 = board.get(capturedIndex1);
				Piece capturedPiece2 = board.get(capturedIndex2);

				int valueDifferenceOfMove1 = getPieceValue(capturedPiece1.getClass()) - getPieceValue(capturingPiece1.getClass());
				int valueDifferenceOfMove2 = getPieceValue(capturedPiece2.getClass()) - getPieceValue(capturingPiece2.getClass());

				return valueDifferenceOfMove2 - valueDifferenceOfMove1;
			} else if (move1.isCapture())
				return -1;
			else if (move2.isCapture())
				return 1;

			if (move1.isCheck() && !move2.isCheck())
				return -1;
			if (move2.isCheck())
				return 1;

			final int historicalScoreOfMove1 = historicalBestMovesCount[move1.fromIndex()][move1.toIndex()];
			final int historicalScoreOfMove2 = historicalBestMovesCount[move2.fromIndex()][move2.toIndex()];

			int historicalScoreDifference = historicalScoreOfMove2 - historicalScoreOfMove1;

			if (historicalScoreDifference != 0)
				return historicalScoreDifference;

			return getPieceValue(board.get(move2.fromIndex()).getClass()) - getPieceValue(board.get(move1.fromIndex()).getClass());
		};

		try {
			possibleLegalMoves.sort(moveComparator);
		} catch (UnsupportedOperationException e) {
			throw new RuntimeException(e);
		}

	}

	private int getPieceValue(Class<? extends Piece> pieceType) {
		if (pieceType.equals(Pawn.class))
			return PAWN_VALUE;
		else if (pieceType.equals(Knight.class))
			return KNIGHT_VALUE;
		else if (pieceType.equals(Bishop.class))
			return BISHOP_VALUE;
		else if (pieceType.equals(Rook.class))
			return ROOK_VALUE;
		else if (pieceType.equals(Queen.class))
			return QUEEN_VALUE;
		else
			return 0;
	}

	private int evaluateOngoingPosition(Board board) {

		int result = 0;

		for (int i = 0; i < 64; i++) {

			Piece piece = board.get(i);

			if (piece == null)
				continue;

			boolean isPieceWhite = piece.getColor() == PieceColor.WHITE;

			int pieceValue = 0;

			int[] positionMap = KING_POSITION_MAP;

			if (piece.getClass().equals(Pawn.class)) {
				pieceValue = PAWN_VALUE;
				positionMap = PAWN_POSITION_MAP;
			} else if (piece.getClass().equals(Knight.class)) {
				pieceValue = KNIGHT_VALUE;
				positionMap = KNIGHT_POSITION_MAP;
			} else if (piece.getClass().equals(Bishop.class)) {
				pieceValue = BISHOP_VALUE;
				positionMap = BISHOP_POSITION_MAP;
			} else if (piece.getClass().equals(Rook.class)) {
				pieceValue = ROOK_VALUE;
				positionMap = ROOK_POSITION_MAP;
			} else if (piece.getClass().equals(Queen.class)) {
				pieceValue = QUEEN_VALUE;
				positionMap = QUEEN_POSITION_MAP;
			}

			final int positionMapIndex = isPieceWhite ? i : Square.getIndex(i % 8, 7 - i / 8);


			int maxValue = Integer.MIN_VALUE;

			for (int j = 0; j < 64; j++) {
				if (positionMap[j] > maxValue)
					maxValue = positionMap[j];
			}

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

		result += (int) (ATTACK_SQUARE_WEIGHT * attackSquareSumDifference);

		return result;
	}
}
