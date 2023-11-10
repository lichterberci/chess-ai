package chessai.chessai.engine;

import chessai.chessai.lib.*;
import chessai.chessai.lib.pieces.*;

import java.security.InvalidKeyException;
import java.util.List;
import java.util.Optional;

public class MinimaxEngine extends ChessEngine {

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
				int storedEval = transpositionTable.get(board);
				if (storedEval == -1) {
					System.out.println(Long.toBinaryString(transpositionTable.table[Integer.remainderUnsigned(board.hashCode(), transpositionTable.capacity)]));
				}
				return storedEval;
			} catch (InvalidKeyException e) {
				// we just don't return
				System.err.println("Invalid key!");
			}
		}

		List<Move> possibleLegalMoves = board.getLegalMoves();

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

	private int evaluateOngoingPosition(Board board) {

		final int pawnValue = 100;
		final int knightValue = 300;
		final int bishopValue = 320;
		final int rookValue = 500;
		final int queenValue = 900;

		int result = 0;

		for (int i = 0; i < 64; i++) {

			Piece piece = board.get(i);

			if (piece == null)
				continue;

			int pieceValue = 0;

			if (piece.getClass().equals(Pawn.class)) {
				pieceValue = pawnValue;
			} else if (piece.getClass().equals(Knight.class)) {
				pieceValue = knightValue;
			} else if (piece.getClass().equals(Bishop.class)) {
				pieceValue = bishopValue;
			} else if (piece.getClass().equals(Rook.class)) {
				pieceValue = rookValue;
			} else if (piece.getClass().equals(Queen.class)) {
				pieceValue = queenValue;
			}

			result += piece.getColor() == PieceColor.WHITE ? pieceValue : -pieceValue;
		}

		return result;
	}
}
