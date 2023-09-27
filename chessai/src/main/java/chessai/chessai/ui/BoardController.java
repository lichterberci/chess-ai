package chessai.chessai.ui;

import chessai.chessai.MainApplication;
import chessai.chessai.lib.Board;
import chessai.chessai.lib.Piece;
import chessai.chessai.lib.PieceColor;
import chessai.chessai.lib.Square;
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
//                final int finalFile = file;
//                final int finalRow = row;
//                square.setOnMouseClicked(e -> handleMouseClick(new Square(finalFile, finalRow)));
//                square.setOnMouseDragEntered(e -> handleMouseDragEnter(new Square(finalFile, finalRow)));
//                square.setOnMouseDragExited(e -> handleMouseDragExited(new Square(finalFile, finalRow)));
                board.add(square, file, row);
            }
        }
    }

    public void drawBoard(Board board, boolean fromWhitesPerspective) throws IOException {

        resetBoard();

        for (int i = 0; i < 64; i++) {
            Square square = new Square(i);

            Piece piece = board.get(new Square(i));

            // empty square
            if (piece == null)
                continue;

            String urlString = "pieces/%s%s.png".formatted(
                    piece.getColor() == PieceColor.WHITE ? 'w' : 'b',
                    Character.toUpperCase(piece.getFENChar())
            );

//            System.out.println("Draw " + urlString + " --> " + resourceLoaderClass.getResource(urlString));

            drawPiece(square, MainApplication.class.getResource(urlString), fromWhitesPerspective);
        }
    }

    private void resetBoard() {
        board.getChildren().forEach(square -> ((Pane)square).getChildren().removeAll());
    }

    public void drawPiece (Square square, URL pieceResourceUrl, boolean fromWhitesPerspective) throws IOException {
        drawPiece(square.file(), square.row(), pieceResourceUrl, fromWhitesPerspective);
    }

    public void drawPiece (int file, int row, URL pieceResourceUrl, boolean fromWhitesPerspective) throws IOException {

        if (fromWhitesPerspective)
            row = 7 - row;

        int finalRow = row;
        Pane parentSquare = (Pane) board.getChildren()
                .stream()
                .filter(square -> square.getId().equals("Square_%d_%d".formatted(file, finalRow)))
                .filter(square -> square instanceof Pane)
                .findFirst()
                .orElseThrow(() -> new IllegalStateException("Board does not have square %d %d".formatted(file, finalRow)));


        parentSquare.getChildren().removeAll();

        if (pieceResourceUrl == null)
            return;

        Image image = new Image(
                pieceResourceUrl.openStream()
        );

        ImageView imageView = new ImageView();

        imageView.fitWidthProperty().bind(parentSquare.widthProperty());
        imageView.fitHeightProperty().bind(parentSquare.heightProperty());
        imageView.setSmooth(true);
        imageView.setPreserveRatio(true);

        imageView.setImage(image);

        imageView.setId(MessageFormat.format("Piece_{0}_{1}", file, row));

        parentSquare.getChildren().add(imageView);
    }
}