package chessai.chessai;

import chessai.chessai.ui.GameManager;
import chessai.chessai.ui.PlayerType;
import javafx.application.Application;
import javafx.stage.Stage;

import java.io.IOException;
import java.text.ParseException;

public class MainApplication extends Application {
    @Override
    public void start(Stage stage) throws IOException, ParseException {

        GameManager gameManager = GameManager.getInstance();

        gameManager.startGame(stage, PlayerType.HUMAN, PlayerType.HUMAN);

    }

    public static void main(String[] args)
    {
        launch();
    }
}