package chessai.chessai.javafxUI.ui;

import chessai.chessai.engine.ChessEngine;
import chessai.chessai.engine.MonteCarloEngine;
import javafx.application.Application;
import javafx.stage.Stage;

import java.io.IOException;
import java.text.ParseException;

@Deprecated
public class MainApplication extends Application {
    @Override
    public void start(Stage stage) throws IOException, ParseException, InterruptedException {

        GameManager gameManager = GameManager.getInstance();

        String initialPosition = "rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR w KQkq - 0 1";
//        String initialPosition = "6k1/p2rR1p1/1p1r1p1R/3P4/4QPq1/1P6/P5PK/8 w - - 1 0";

        ChessEngine engine = new MonteCarloEngine(0, 1.41, 30, 300);
//        ChessEngine engine = new RandomEngine();

        gameManager.playGame(stage, PlayerType.HUMAN, PlayerType.ENGINE, null, engine, initialPosition);

    }

    public static void main(String[] args)
    {
        launch();
    }
}