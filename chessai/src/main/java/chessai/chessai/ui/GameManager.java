package chessai.chessai.ui;

import chessai.chessai.MainApplication;
import chessai.chessai.lib.Board;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

import java.io.IOException;
import java.text.MessageFormat;
import java.text.ParseException;

public class GameManager {

    private static GameManager instance;

    public static GameManager getInstance() {
        if (instance == null)
            instance = new GameManager();
        return instance;
    }

    public void startGame(Stage stage, PlayerType whitePlayerType, PlayerType blackPlayerType) throws IOException, ParseException {

        String initialPosition = "rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR w KQkq - 0 1";

        FXMLLoader fxmlLoader = new FXMLLoader(MainApplication.class.getResource("MainBoard.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), 800, 800);

        BoardController boardController = fxmlLoader.getController();

        boardController.colorBoard(true, Color.rgb(0, 100, 140), Color.rgb(255, 255, 255));

        Board board = new Board(initialPosition);

        boardController.drawBoard(board, true);

        stage.setTitle(MessageFormat.format("{0} vs {1}", whitePlayerType, blackPlayerType));
        stage.setScene(scene);
        stage.show();

    }
}
