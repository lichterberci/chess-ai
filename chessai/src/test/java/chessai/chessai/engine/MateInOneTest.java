package chessai.chessai.engine;

import chessai.chessai.lib.Board;
import chessai.chessai.lib.GameState;
import org.junit.jupiter.api.Test;

import java.text.ParseException;

import static org.testng.AssertJUnit.assertEquals;
import static org.testng.AssertJUnit.assertTrue;

class MateInOneTest {

	@Test
	void mateInOne() throws ParseException {

		Board board = new Board("7k/p2rR1p1/1p1r1p2/3P4/4QPq1/1P6/P5PK/8 w - - 1 1");

		ChessEngine engine = new MonteCarloEngine(0, 1.4142, 50, 50);

		var move = engine.makeMove(board);

		assertTrue(move.isPresent());

		Board boardAfterMove = board.makeMove(move.get());

		assertEquals(GameState.WHITE_WIN, boardAfterMove.getState());
	}

}
