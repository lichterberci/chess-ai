package chessai.chessai;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

import java.io.IOException;

public class MainApplication extends Application {
    @Override
    public void start(Stage stage) throws IOException {

        FXMLLoader fxmlLoader = new FXMLLoader(MainApplication.class.getResource("MainBoard.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), 800, 800);

        BoardController boardController = fxmlLoader.getController();

        boardController.colorBoard(true, Color.TRANSPARENT, Color.TRANSPARENT);
        boardController.drawPiece(2, 2, MainApplication.class.getResource("MainPieceSet.png").getPath());

        stage.setTitle("GézaBot");
        stage.setScene(scene);
        stage.show();
    }

    public static void main(String[] args) {
        launch();
    }
}