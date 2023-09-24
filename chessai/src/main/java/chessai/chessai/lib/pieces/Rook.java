package chessai.chessai.lib.pieces;

import chessai.chessai.lib.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class Rook extends Piece {
    public Rook (PieceColor color) {
        super(color);
    }
    @Override
    public char getFENChar() {
        return getColor() == PieceColor.WHITE ? 'R' : 'r';
    }

    @Override
    public List<Move> getAllPossibleMoves(Board board) {
        final int currentFile = getSquare().file();
        final int currentRow = getSquare().row();

        List<Move> moves = new ArrayList<>();

        for (int i = currentFile + 1; i < 8; i++) {
            final Square square = new Square(i, currentRow);

            if (determineWhetherItCanMoveToSquare(board, moves, square))
                break;
        }

        for (int i = 7; i > currentFile; i--) {
            final Square square = new Square(i, currentRow);

            if (determineWhetherItCanMoveToSquare(board, moves, square))
                break;
        }

        for (int i = currentRow + 1; i < 8; i++) {
            final Square square = new Square(currentFile, i);

            if (determineWhetherItCanMoveToSquare(board, moves, square))
                break;
        }

        for (int i = 7; i > currentRow; i--) {
            final Square square = new Square(currentFile, i);

            if (determineWhetherItCanMoveToSquare(board, moves, square))
                break;
        }

        return moves;
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

