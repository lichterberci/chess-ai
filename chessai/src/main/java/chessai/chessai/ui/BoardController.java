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
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

public class BoardController {

    @FXML
    private GridPane board;
    @FXML
    private AnchorPane boardHolder;

    private List<Consumer<Square>> onMouseClickOnSquareListeners;
    private List<Consumer<Square>> onMouseDragEnterIntoSquareListeners;
    private List<Consumer<Square>> onMouseDragExitFromSquareListeners;

    @FXML
    private void initialize() {
        boardHolder.widthProperty().addListener((observable, oldValue, newValue) -> resizeBoard(newValue.doubleValue(), boardHolder.getHeight()));
        boardHolder.heightProperty().addListener((observable, oldValue, newValue) -> resizeBoard(boardHolder.getWidth(), newValue.doubleValue()));
        onMouseClickOnSquareListeners = new ArrayList<>();
        onMouseDragEnterIntoSquareListeners = new ArrayList<>();
        onMouseDragExitFromSquareListeners = new ArrayList<>();
    }

    private void handleMouseClickOnSquare(Square square) {
        onMouseClickOnSquareListeners.forEach(listener -> listener.accept(square));
    }

    private void handleMouseDragEnterIntoSquare(Square square) {
        onMouseDragEnterIntoSquareListeners.forEach(listener -> listener.accept(square));
    }

    private void handleMouseDragExitedOutFromSquare(Square square) {
        System.out.println("Handle mouse click");
        onMouseDragExitFromSquareListeners.forEach(listener -> listener.accept(square));
    }

    public void addOnMouseClickOnSquareListener(Consumer<Square> listener) {
        onMouseClickOnSquareListeners.add(listener);
    }

    public void addOnMouseDragEnterIntoSquareListeners(Consumer<Square> listener) {
        onMouseDragEnterIntoSquareListeners.add(listener);
    }

    public void addOnMouseDragExitFromSquareListeners(Consumer<Square> listener) {
        onMouseDragExitFromSquareListeners.add(listener);
    }

    public void removeOnMouseClickOnSquareListener(Consumer<Square> listener) {
        onMouseClickOnSquareListeners.remove(listener);
    }

    public void removeOnMouseDragEnterIntoSquareListeners(Consumer<Square> listener) {
        onMouseDragEnterIntoSquareListeners.remove(listener);
    }

    public void removeOnMouseDragExitFromSquareListeners(Consumer<Square> listener) {
        onMouseDragExitFromSquareListeners.remove(listener);
    }

    public void removeAllOnMouseClickOnSquareListeners() {
        onMouseClickOnSquareListeners.clear();
    }

    public void removeAllOnMouseDragEnterIntoSquareListenerss() {
        onMouseDragEnterIntoSquareListeners.clear();
    }

    public void removeAllOnMouseDragExitFromSquareListenerss() {
        onMouseDragExitFromSquareListeners.clear();
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
                square.setBackground(Background.fill((file + row) % 2 == (whiteIsAtBottom ? 0 : 1) ? blackColor : whiteColor));
                square.setId(MessageFormat.format("Square_{0}_{1}", file, row));
                final int finalFile = file;
                final int finalRow = 7 - row;
                square.setOnMouseClicked(e -> handleMouseClickOnSquare(new Square(finalFile, finalRow)));
                square.setOnMouseDragEntered(e -> handleMouseDragEnterIntoSquare(new Square(finalFile, finalRow)));
                square.setOnMouseDragExited(e -> handleMouseDragExitedOutFromSquare(new Square(finalFile, finalRow)));
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
        board.getChildren().forEach(square -> ((Pane) square).getChildren().clear());
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

    public void drawSelection(Square selectedSquare, boolean fromWhitesPerspective) {

        board.getChildren().forEach(square -> {
            ((Pane) square).getChildren().removeAll(((Pane) square).getChildren().filtered(child -> child.getId().equals("selection")));
        });

        if (selectedSquare == null)
            return;

        int row = selectedSquare.row();

        if (fromWhitesPerspective)
            row = 7 - selectedSquare.row();

        int finalRow = row;

        Pane parentSquare = (Pane) board.getChildren()
                .stream()
                .filter(square -> square.getId().equals("Square_%d_%d".formatted(selectedSquare.file(), finalRow)))
                .filter(square -> square instanceof Pane)
                .findFirst()
                .orElseThrow(() -> new IllegalStateException("Board does not have square %d %d".formatted(selectedSquare.file(), finalRow)));

        Pane selectionPane = new Pane();

        selectionPane.setBackground(Background.fill(Color.RED));
        selectionPane.setId("selection");
        selectionPane.setPrefWidth(200);
        selectionPane.setPrefHeight(200);

        parentSquare.getChildren().add(selectionPane);
        selectionPane.toBack();
    }
}