package chessai.chessai.ui;

import chessai.chessai.MainApplication;
import chessai.chessai.lib.Board;
import chessai.chessai.lib.GameState;
import chessai.chessai.lib.Move;
import chessai.chessai.lib.Square;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

import java.io.IOException;
import java.text.MessageFormat;
import java.text.ParseException;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Function;

public class GameManager {

    private static GameManager instance;

    public static GameManager getInstance() {
        if (instance == null)
            instance = new GameManager();
        return instance;
    }

    public void playGame(Stage stage, PlayerType whitePlayerType, PlayerType blackPlayerType) throws IOException, ParseException {

        FXMLLoader fxmlLoader = new FXMLLoader(MainApplication.class.getResource("MainBoard.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), 800, 800);

        stage.setTitle(MessageFormat.format("{0} vs {1}", whitePlayerType, blackPlayerType));
        stage.setScene(scene);

        String initialPosition = "rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR w KQkq - 0 1";

        BoardController boardController = fxmlLoader.getController();

        boardController.colorBoard(true, Color.rgb(0, 100, 140), Color.rgb(255, 255, 255));

        var boardWrapper = new Object() {
            Board board = new Board(initialPosition);
        };

        boardController.drawBoard(boardWrapper.board, true);

        stage.show();

        var selectedSquareWrapper = new Object() {
            Square selectedSquare = null;
        };


        Consumer<Square> selectSquare = (square) -> {
            selectedSquareWrapper.selectedSquare = square;
            boardController.drawSelection(square, true);
        };

        Function<Square, Boolean> tryToMoveToSquare = (square) -> {

            System.out.println(selectedSquareWrapper.selectedSquare + " --> " + square);

            if (selectedSquareWrapper.selectedSquare == null)
                return false;

            Optional<Move> move = boardWrapper.board.tryToInferMove(selectedSquareWrapper.selectedSquare, square, null);

            if (move.isEmpty()) {
                selectSquare.accept(null);
                return false;
            }

            if (!boardWrapper.board.isMovePossibleAndLegal(move.get())) {
                return false;
            }

            System.out.println("Make move!");

            boardWrapper.board = boardWrapper.board.move(move.get());

            try {
                boardController.drawBoard(boardWrapper.board, true);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }

            selectSquare.accept(null);

            GameState currentGameState = boardWrapper.board.getState();

            if (currentGameState != GameState.PLAYING) {

                endGame(stage, currentGameState);
            }

            return true;
        };

        Consumer<Square> selectOrMoveToSquare = (square) -> {

            if (selectedSquareWrapper.selectedSquare == null) {
                selectSquare.accept(square);
                return;
            }

            boolean moved = tryToMoveToSquare.apply(square);

            selectSquare.accept(moved ? null : square);
        };
//
//        boardController.removeAllOnMouseDragEnterIntoSquareListenerss();
//        boardController.addOnMouseDragEnterIntoSquareListeners(selectSquare);
//
//        boardController.removeAllOnMouseDragExitFromSquareListenerss();
//        boardController.addOnMouseDragExitFromSquareListeners(tryToMoveToSquare);

        boardController.removeAllOnMouseClickOnSquareListeners();
        boardController.addOnMouseClickOnSquareListener(selectOrMoveToSquare);

    }

    private void endGame(Stage stage, GameState currentGameState) {

        System.out.println(currentGameState);

        stage.close();
    }
}
