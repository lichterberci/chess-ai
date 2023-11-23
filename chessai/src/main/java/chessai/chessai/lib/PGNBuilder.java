package chessai.chessai.lib;

import java.time.LocalDate;
import java.util.LinkedList;
import java.util.List;

public class PGNBuilder {

	private static final String SITE_NAME = "Budapest";
	private static final String EVENT_NAME = "Challange";
	private Board board;
	private final String whiteName;
	private final String blackName;
	private List<String> moveStrings;
	private GameState result = GameState.PLAYING;

	public PGNBuilder(Board board, String whiteName, String blackName) {
		this.board = board;
		this.whiteName = whiteName;
		this.blackName = blackName;
		this.moveStrings = new LinkedList<>();
	}

	public void addMove(Move move) {
		this.board = board.makeMove(move);
	}

	public void setResult(GameState result) {
		this.result = result;
	}

	public String buildString() {
		StringBuilder sb = new StringBuilder();

		sb.append("[Event \"%s\"]".formatted(EVENT_NAME));
		sb.append("[Site \"%s\"]".formatted(SITE_NAME));
		sb.append("[Date \"%s\"]".formatted(LocalDate.now().toString()));
		sb.append("[White \"%s\"]".formatted(whiteName));
		sb.append("[Black \"%s\"]".formatted(blackName));
		sb.append("[Result \"%s\"]".formatted(switch (result) {
			case WHITE_WIN -> "1-0";
			case BLACK_WIN -> "0-1";
			case DRAW -> "½-½";
			case PLAYING -> "ongoing";
		}));

		for (int i = 0; i < moveStrings.size(); i++) {
			sb.append("%d.%s ".formatted(i + 1, moveStrings.get(i)));
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
