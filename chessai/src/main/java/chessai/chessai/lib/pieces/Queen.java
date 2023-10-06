package chessai.chessai.lib.pieces;

import chessai.chessai.lib.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class Queen extends Piece {
    public Queen (PieceColor color) {
        super(color);
    }
    @Override
    public char getFENChar() {
        return getColor() == PieceColor.WHITE ? 'Q' : 'q';
    }

    @Override
    public List<Move> getAllPossibleMoves(Board board) {
        final int currentFile = getSquare().file();
        final int currentRow = getSquare().row();

        List<Move> moves = new ArrayList<>();

        for (int i = currentFile - 1; i >= 0; i--) {
            final Square square = new Square(i, currentRow);

            if (determineWhetherItCanMoveToSquare(board, moves, square))
                break;
        }

        for (int i = currentFile + 1; i <= 7; i++) {
            final Square square = new Square(i, currentRow);

            if (determineWhetherItCanMoveToSquare(board, moves, square))
                break;
        }

        for (int i = currentRow - 1; i >= 0; i--) {
            final Square square = new Square(currentFile, i);

            if (determineWhetherItCanMoveToSquare(board, moves, square))
                break;
        }

        for (int i = currentRow + 1; i <= 7; i++) {
            final Square square = new Square(currentFile, i);

            if (determineWhetherItCanMoveToSquare(board, moves, square))
                break;
        }

        // up, right
        for (int i = 1; i < Math.min(8 - currentFile, 8 - currentRow); i++) {
            final Square square = new Square(currentFile + i, currentRow + i);

            if (determineWhetherItCanMoveToSquare(board, moves, square))
                break;
        }

        // up, left
        for (int i = 1; i < Math.min(currentFile + 1, 8 - currentRow); i++) {
            final Square square = new Square(currentFile - i, currentRow + i);

            if (determineWhetherItCanMoveToSquare(board, moves, square))
                break;
        }

        // down, left
        for (int i = 1; i < Math.min(currentFile + 1, currentRow + 1); i++) {
            final Square square = new Square(currentFile - i, currentRow - i);

            if (determineWhetherItCanMoveToSquare(board, moves, square))
                break;
        }

        // down, right
        for (int i = 1; i < Math.min(8 - currentFile, currentRow + 1); i++) {
            final Square square = new Square(currentFile + i, currentRow - i);

            if (determineWhetherItCanMoveToSquare(board, moves, square))
                break;
        }

        return moves;
    }

    @Override
    public Piece copy() {
        return new Queen(color);
    }

    /**
     * @param board the board in which me want to move
     * @param moves the list of moves that we amend
     * @param square the square we want to look at
     * @return whether we terminate the current loop
     */
    private boolean determineWhetherItCanMoveToSquare(Board board, List<Move> moves, Square square) {
        Optional<PieceColor> color;

        if ((color = board.getColorAtSquare(square)).isPresent()) {

            if (color.get().equals(getColor()))
                return true;

            moves.add(new Move(getSquare(),
                    square,
                    null,
                    true,
                    false,
                    SpecialMove.NONE));

            return true;
        }

        moves.add(new Move(getSquare(),
                square,
                null,
                false,
                false,
                SpecialMove.NONE));

        return false;
    }
}

