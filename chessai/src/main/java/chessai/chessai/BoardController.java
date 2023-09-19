package chessai.chessai;

import javafx.fxml.FXML;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Background;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;

import java.io.IOException;
import java.net.URL;
import java.text.MessageFormat;

public class BoardController {
    @FXML
    private GridPane board;
    @FXML
    private AnchorPane boardHolder;

    @FXML
    private void initialize() {
        boardHolder.widthProperty().addListener((observable, oldValue, newValue) -> resizeBoard(newValue.doubleValue(), boardHolder.getHeight()));
        boardHolder.heightProperty().addListener((observable, oldValue, newValue) -> resizeBoard(boardHolder.getWidth(), newValue.doubleValue()));
    }

    private void resizeBoard (double width, double height) {

        final double size = Math.min(width, height);

        System.out.printf("resize to (%s, %s) --> %s%n", width, height, size);

        final double squareSize = size / 8.0;

        board.getColumnConstraints().forEach(columnConstraints -> columnConstraints.setPrefWidth(squareSize));
        board.getRowConstraints().forEach(rowConstraints -> rowConstraints.setPrefHeight(squareSize));
    }

    public void colorBoard(boolean whiteIsAtBottom, Color whiteColor, Color blackColor) {

        board.getChildren().removeAll();

        for (int file = 0; file < 8; file++) {
            for (int row = 0; row < 8; row++) {
                Pane square = new Pane();
                square.setBackground(Background.fill((file + row) % 2 == (whiteIsAtBottom ? 1 : 0) ? blackColor : whiteColor));
                square.setId(MessageFormat.format("Square_{0}_{1}", file, row));
                board.add(square, file, row);
            }
        }
    }
    public void drawPiece (int file, int row, URL pieceResourceUrl) throws IOException {

        board.getChildren()
                .stream()
                .filter(square -> square.getId().equals("Square_%d_%d".formatted(file, row)))
                .filter(square -> square instanceof Pane)
                .forEach(square -> ((Pane) square).getChildren().removeAll());

        Image imageOfSet = new Image(pieceResourceUrl.openStream());

        ImageView imageView = new ImageView();

        imageView.setImage(imageOfSet);

        imageView.setId(MessageFormat.format("Piece_{0}_{1}", file, row));

        board.getChildren()
                .stream()
                .filter(child -> child.getId().equals("Square_%d_%d".formatted(file, row)))
                .filter(square -> square instanceof Pane)
                .forEach(square -> ((Pane)square).getChildren().add(imageView));
    }
}