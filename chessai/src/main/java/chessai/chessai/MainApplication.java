package chessai.chessai;

import chessai.chessai.lib.Board;
import chessai.chessai.ui.BoardController;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

import java.io.IOException;
import java.text.ParseException;

public class MainApplication extends Application {
    @Override
    public void start(Stage stage) throws IOException {

        FXMLLoader fxmlLoader = new FXMLLoader(MainApplication.class.getResource("MainBoard.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), 800, 800);

        BoardController boardController = fxmlLoader.getController();

        boardController.colorBoard(true, Color.rgb(255, 230, 210), Color.rgb(163, 68, 10));

//        String startingPosition = "rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR w KQkq - 0 1";

        String initialPosition = "8/4npk1/5p1p/1Q5P/1p4P1/4r3/7q/3K1R2 b - - 1 49";

        try {
            boardController.drawBoard(new Board(initialPosition), getClass(), true);
        } catch (ParseException e) {
            System.out.println("Cannot draw board!");
        }

        stage.setTitle("BOTond");
        stage.setScene(scene);
        stage.show();
    }

    public static void main(String[] args)
    {
        launch();
    }
}