package chessai.chessai.javafxUI.ui;

import chessai.chessai.engine.ChessEngine;
import chessai.chessai.lib.*;
import chessai.chessai.lib.pieces.Queen;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

import java.io.IOException;
import java.text.MessageFormat;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Function;

@Deprecated
public class GameManager {

    private static GameManager instance;

    public static GameManager getInstance() {
        if (instance == null)
            instance = new GameManager();
        return instance;
    }

    public void playGame(Stage stage,
                         PlayerType whitePlayerType,
                         PlayerType blackPlayerType,
                         ChessEngine whiteEngine,
                         ChessEngine blackEngine,
                         String initialPosition) throws IOException, ParseException, InterruptedException {

        FXMLLoader fxmlLoader = new FXMLLoader(MainApplication.class.getResource("MainBoard.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), 800, 800);

        stage.setTitle(MessageFormat.format("{0} vs {1}", whitePlayerType, blackPlayerType));
        stage.setScene(scene);

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
            if (square != null) {
//                boardController.drawPossibleMoveTargets(
//                        boardWrapper.board
//                                .generateLegalMovesUsingBitMapsAndUpdateBitMaps()
//                                .stream()
//                                .map(Move::to)
//                                .toList(),
//                        true,
//                        false);
//                boardController.drawPossibleMoveTargets(
//                        boardWrapper.board
//                                .generateLegalMovesUsingBitMapsAndUpdateBitMaps()
//                                .stream()
//                                .filter(move -> move.from().equals(selectedSquareWrapper.selectedSquare))
//                                .map(Move::to)
//                                .toList(),
//                        true,
//                        true);
                if (boardWrapper.board.colorToMove == PieceColor.WHITE) {
                    boardController.drawPossibleMoveTargets(
                            boardWrapper.board
                                    .whiteKing
                                    .getIndexesOfOnes()
                                    .stream()
                                    .map(Square::new)
                                    .toList(),
                            true,
                            true);
                    boardController.drawPossibleMoveTargets(
                            boardWrapper.board
                                    .whitePieces
                                    .getIndexesOfOnes()
                                    .stream()
                                    .map(Square::new)
                                    .toList(),
                            true,
                            false);
                } else {
                    boardController.drawPossibleMoveTargets(
                            boardWrapper.board
                                    .blackKing
                                    .getIndexesOfOnes()
                                    .stream()
                                    .map(Square::new)
                                    .toList(),
                            true,
                            true);
                    boardController.drawPossibleMoveTargets(
                            boardWrapper.board
                                    .blackPieces
                                    .getIndexesOfOnes()
                                    .stream()
                                    .map(Square::new)
                                    .toList(),
                            true,
                            false);
                }
            } else {
                boardController.drawPossibleMoveTargets(new ArrayList<>(), true, true);
                boardController.drawPossibleMoveTargets(new ArrayList<>(), true, false);
            }
        };

        Function<Square, Boolean> tryToMoveToSquare = (square) -> {

            if (selectedSquareWrapper.selectedSquare == null)
                return false;

            Optional<Move> move = boardWrapper.board.tryToInferMove(selectedSquareWrapper.selectedSquare, square, Queen.class);

            if (move.isEmpty()) {
                selectSquare.accept(null);
                return false;
            }

            if (
                    boardWrapper.board.get(selectedSquareWrapper.selectedSquare) != null && (
                            boardWrapper.board.get(selectedSquareWrapper.selectedSquare).getColor() == PieceColor.WHITE && whitePlayerType != PlayerType.HUMAN
                                    || boardWrapper.board.get(selectedSquareWrapper.selectedSquare).getColor() == PieceColor.BLACK && blackPlayerType != PlayerType.HUMAN
                    )
            ) {
                return false;
            }

            if (!boardWrapper.board.isMoveLegal(move.get())) {
                return false;
            }

            boardWrapper.board = boardWrapper.board.makeMove(move.get());

            System.out.println(boardWrapper.board.colorToMove == PieceColor.WHITE ? "white to move:" : "black to move:");
            System.out.println("white attack map: " + boardWrapper.board.whiteAttackSquares);
            System.out.println("black attack map: " + boardWrapper.board.blackAttackSquares);

            try {
                boardController.drawBoard(boardWrapper.board, true);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }

            selectSquare.accept(null);

            GameState currentGameState = boardWrapper.board.getState();

            if (currentGameState != GameState.PLAYING) {
                endGame(stage, currentGameState);
                return true;
            }

            if (boardWrapper.board.colorToMove == PieceColor.WHITE && whitePlayerType != PlayerType.HUMAN
                    || boardWrapper.board.colorToMove == PieceColor.BLACK && blackPlayerType != PlayerType.HUMAN
            ) {
                var engineMove = (boardWrapper.board.colorToMove == PieceColor.WHITE ? whiteEngine : blackEngine).makeMove(boardWrapper.board, m -> {
                }, () -> false);

                engineMove.ifPresent(value -> boardWrapper.board = boardWrapper.board.makeMove(value));

                try {
                    boardController.drawBoard(boardWrapper.board, true);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }

                GameState currentGameStateAfterEngineMove = boardWrapper.board.getState();

                if (currentGameStateAfterEngineMove != GameState.PLAYING) {
                    endGame(stage, currentGameStateAfterEngineMove);
                    return true;
                }
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

        boardController.addOnMouseClickOnSquareListener(selectOrMoveToSquare);
        boardController.addOnMouseDragEnterIntoSquareListeners(selectSquare);
        boardController.addOnMouseDragExitFromSquareListeners(tryToMoveToSquare::apply);

        if (whitePlayerType == PlayerType.ENGINE) {
            var engineMove = whiteEngine.makeMove(boardWrapper.board, m -> {
            }, () -> false);

            engineMove.ifPresent(move -> boardWrapper.board = boardWrapper.board.makeMove(move));

            try {
                boardController.drawBoard(boardWrapper.board, true);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }

    private void endGame(Stage stage, GameState currentGameState) {

        System.out.println(currentGameState);

        stage.close();
    }
}
