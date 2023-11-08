package chessai.chessai.engine;

import chessai.chessai.lib.*;
import chessai.chessai.lib.pieces.*;

import java.util.List;
import java.util.Optional;

public class MinimaxEngine extends ChessEngine {

	final int maxDepth;

	public MinimaxEngine(int maxDepth) {
		this.maxDepth = maxDepth;
	}

	@Override
	public Optional<Move> makeMove(Board board) {

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

			if (alpha <= beta)
				break;
		}

		return Optional.of(possibleLegalMoves.get(indexOfBestMove));
	}

	private int evaluateState(Board board, int depthRemaining, boolean isMaximizingPlayer, int alpha, int beta) {

		List<Move> possibleLegalMoves = board.getLegalMoves();

		GameState state = board.getState();

		if (state == GameState.WHITE_WIN) {
			return Integer.MAX_VALUE;
		} else if (state == GameState.BLACK_WIN) {
			return Integer.MIN_VALUE;
		} else if (state == GameState.DRAW) {
			return 0;
		}

		if (depthRemaining == 0) {
			return evaluateOngoingPosition(board);
		}

		if (possibleLegalMoves.isEmpty())
			throw new IllegalStateException("There has to be at least one legal move!");

		int bestEval = isMaximizingPlayer ? Integer.MIN_VALUE : Integer.MAX_VALUE;

		for (int i = 0; i < possibleLegalMoves.size(); i++) {
			Move move = possibleLegalMoves.get(i);

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

		final boolean isWhiteToMove = board.colorToMove == PieceColor.WHITE;

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
