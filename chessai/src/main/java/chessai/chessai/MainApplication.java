package chessai.chessai;

import chessai.chessai.engine.ChessEngine;
import chessai.chessai.engine.MonteCarloEngine;
import chessai.chessai.ui.GameManager;
import chessai.chessai.ui.PlayerType;
import javafx.application.Application;
import javafx.stage.Stage;

import java.io.IOException;
import java.text.ParseException;

public class MainApplication extends Application {
    @Override
    public void start(Stage stage) throws IOException, ParseException, InterruptedException {

        GameManager gameManager = GameManager.getInstance();

        ChessEngine engine = new MonteCarloEngine(0, 0, 80, 50);

        gameManager.playGame(stage, PlayerType.ENGINE, PlayerType.HUMAN, engine, null);

    }

    public static void main(String[] args)
    {
        launch();
    }
}