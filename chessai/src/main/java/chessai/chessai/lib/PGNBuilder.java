package chessai.chessai.lib;

import chessai.chessai.lib.pieces.*;

import java.time.LocalDate;
import java.util.LinkedList;
import java.util.List;

/**
 * This is a class with which PGN output can be constructed.
 */
public class PGNBuilder {

	private static final String SITE_NAME = "Budapest";
	private static final String EVENT_NAME = "Challange";
	private Board board;
	private final String whiteName;
	private final String blackName;
	private final List<String> movesInAlgebraicNotation;
	private final String initialFen;
	private GameState result = GameState.PLAYING;

	public PGNBuilder(Board board, String whiteName, String blackName, String initialFen) {
		this.board = board;
		this.whiteName = whiteName;
		this.blackName = blackName;
		this.movesInAlgebraicNotation = new LinkedList<>();
		this.initialFen = initialFen;
	}

	/**
	 * Registers a move
	 *
	 * @param move the move
	 */
	public void addMove(Move move) {

		var nextBoard = board.makeMove(move);

		Move moveWithCheckSet = board.withIsCheckSet(move, nextBoard);

		String moveString;

		if (move.specialMove() == SpecialMove.QUEEN_SIDE_CASTLE)
			moveString = "O-O-O";
		else if (move.specialMove() == SpecialMove.KING_SIDE_CASTLE)
			moveString = "O-O";
		else {
			char pieceChar = board.get(move.fromIndex()).getFENChar();

			List<Move> otherLegalMoves = board.getLegalMoves().stream().filter(m -> !m.equals(move)).toList();

			final boolean canAnotherPieceMoveThereFromTheSameFile = otherLegalMoves.stream()
					.anyMatch(otherMove ->
							board.get(otherMove.fromIndex()).getClass().equals(board.get(move.fromIndex()).getClass())
									&& otherMove.from().file() == move.from().file()
									&& otherMove.toIndex() == move.toIndex()
					);

			final boolean canAnotherPieceMoveThereFromADifferentFile = otherLegalMoves.stream()
					.anyMatch(otherMove ->
							board.get(otherMove.fromIndex()).getClass().equals(board.get(move.fromIndex()).getClass())
									&& otherMove.from().file() != move.from().file()
									&& otherMove.toIndex() == move.toIndex()
					);

			final boolean isPawn = board.get(move.fromIndex()) instanceof Pawn;

			if (isPawn && move.promotionPieceType() != null) {

				char promotionChar;

				if (move.promotionPieceType().equals(Queen.class))
					promotionChar = 'Q';
				else if (move.promotionPieceType().equals(Rook.class))
					promotionChar = 'R';
				else if (move.promotionPieceType().equals(Knight.class))
					promotionChar = 'N';
				else if (move.promotionPieceType().equals(Bishop.class))
					promotionChar = 'B';
				else
					throw new IllegalStateException("Invalid promotion class!");

				moveString = (move.isCapture() ? ((char) (move.from().file() + 'a') + "x") : "") + Square.toString(move.toIndex(), false) + "=" + promotionChar;
			} else if (isPawn && move.isCapture()) {

				moveString = (char) (move.from().file() + 'a') + "x" + Square.toString(move.toIndex(), false);

			} else if (isPawn) {

				moveString = Square.toString(move.toIndex(), false);

			} else if (canAnotherPieceMoveThereFromTheSameFile && canAnotherPieceMoveThereFromADifferentFile) {

				moveString = pieceChar + Square.toString(move.fromIndex(), false) + (move.isCapture() ? "x" : "") + Square.toString(move.toIndex(), false);

			} else if (canAnotherPieceMoveThereFromADifferentFile) {

				moveString = pieceChar + String.valueOf((char) (move.from().file() + 'a')) + (move.isCapture() ? "x" : "") + Square.toString(move.toIndex(), false);

			} else if (canAnotherPieceMoveThereFromTheSameFile) {

				moveString = pieceChar + ((char) ((7 - move.from().row()) + '0')) + (move.isCapture() ? "x" : "") + Square.toString(move.toIndex(), false);

			} else {

				moveString = pieceChar + (move.isCapture() ? "x" : "") + Square.toString(move.toIndex(), false);

			}
		}

		GameState stateAfterMove = nextBoard.getState();

		if (stateAfterMove != GameState.PLAYING && stateAfterMove != GameState.DRAW)
			moveString += "#";
		else if (moveWithCheckSet.isCheck())
			moveString += "+";

		movesInAlgebraicNotation.add(moveString);

		this.board = nextBoard;
	}

	/**
	 * The result of the game can be set
	 * @param result the result
	 */
	public void setResult(GameState result) {
		this.result = result;
	}

	/**
	 * Builds the PGN output
	 * @return the PGN as a string
	 */
	public String buildString() {
		StringBuilder sb = new StringBuilder();

		sb.append("[Event \"%s\"]%n".formatted(EVENT_NAME));
		sb.append("[Site \"%s\"]%n".formatted(SITE_NAME));
		sb.append("[Date \"%s\"]%n".formatted(LocalDate.now().toString()));
		sb.append("[FEN \"%s\"]%n".formatted(initialFen));
		sb.append("[White \"%s\"]%n".formatted(whiteName));
		sb.append("[Black \"%s\"]%n".formatted(blackName));
		sb.append("[Result \"%s\"]%n".formatted(switch (result) {
			case WHITE_WIN -> "1-0";
			case BLACK_WIN -> "0-1";
			case DRAW -> "½-½";
			case PLAYING -> "ongoing";
		}));

		for (int i = 0; i < movesInAlgebraicNotation.size(); i++) {
			if (i % 2 == 0)
				sb.append("%d. ".formatted((i + 1) / 2 + 1));

			sb.append("%s ".formatted(movesInAlgebraicNotation.get(i)));
		}

		if (result != GameState.PLAYING)
			sb.append(switch (result) {
				case WHITE_WIN -> "1-0";
				case BLACK_WIN -> "0-1";
				case DRAW -> "½-½";
				default -> throw new IllegalStateException("Cannot be ongoing!");
			});

		return sb.toString();
	}
}
