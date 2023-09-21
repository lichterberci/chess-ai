package chessai.chessai;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.Objects;

public class MainApplication extends Application {
    @Override
    public void start(Stage stage) throws IOException {

        FXMLLoader fxmlLoader = new FXMLLoader(MainApplication.class.getResource("MainBoard.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), 800, 800);

        BoardController boardController = fxmlLoader.getController();

        boardController.colorBoard(true, Color.valueOf("#EEE"), Color.valueOf("#003366"));
        boardController.drawPiece(0, 0, Objects.requireNonNull(getClass().getResource("pieces/bR.png")));
        boardController.drawPiece(1, 0, Objects.requireNonNull(getClass().getResource("pieces/bN.png")));
        boardController.drawPiece(2, 0, Objects.requireNonNull(getClass().getResource("pieces/bB.png")));
        boardController.drawPiece(0, 7, Objects.requireNonNull(getClass().getResource("pieces/wR.png")));
        boardController.drawPiece(1, 7, Objects.requireNonNull(getClass().getResource("pieces/wN.png")));
        boardController.drawPiece(2, 7, Objects.requireNonNull(getClass().getResource("pieces/wB.png")));

        stage.setTitle("BOTond");
        stage.setScene(scene);
        stage.show();
    }

    public static void main(String[] args)
    {
        launch();
    }
}