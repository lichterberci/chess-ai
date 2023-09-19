package chessai.chessai;

import javafx.fxml.FXML;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;

public class BoardController {
    @FXML
    private GridPane board;

    public void colorBoard(boolean whiteIsAtBottom, Color whiteColor, Color blackColor) {

        board.getChildren().removeAll();

        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                Pane square = new Pane();
                square.setBackground(Background.fill((i + j) % 2 == (whiteIsAtBottom ? 1 : 0) ? blackColor : whiteColor));
                board.add(square, i, j);
            }
        }
    }
}