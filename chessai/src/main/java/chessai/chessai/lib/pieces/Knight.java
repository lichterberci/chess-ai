package chessai.chessai.lib.pieces;

import chessai.chessai.lib.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class Knight extends Piece {
    public Knight (PieceColor color) {
        super(color);
    }
    @Override
    public char getFENChar() {
        return getColor() == PieceColor.WHITE ? 'N' : 'n';
    }

    @Override
    public List<Move> getAllPossibleMoves(Board board) {
        List<Move> moves = new ArrayList<>();

        final int currentFile = getSquare().file();
        final int currentRow = getSquare().row();

        checkSquare(board, moves, currentFile + 2, currentRow - 1);
        checkSquare(board, moves, currentFile + 2, currentRow + 1);
        checkSquare(board, moves, currentFile - 2, currentRow - 1);
        checkSquare(board, moves, currentFile - 2, currentRow + 1);
        checkSquare(board, moves, currentFile + 1, currentRow - 2);
        checkSquare(board, moves, currentFile + 1, currentRow + 2);
        checkSquare(board, moves, currentFile - 1, currentRow - 2);
        checkSquare(board, moves, currentFile - 1, currentRow + 2);

        return moves;
    }

    @Override
    public Piece copy() {
        return new Knight(color);
    }

    private void checkSquare(Board board, List<Move> moves, int file, int row) {

        if (file < 0 || row < 0 || file > 7 || row > 7)
            return;

        Square square = new Square(file, row);

        Optional<PieceColor> color;

        if ((color = board.getColorAtSquare(square)).isPresent()) {

            if (color.get().equals(getColor()))
                return;

            moves.add(new Move(getSquare(),
                    square,
                    null,
                    true,
                    false,
                    SpecialMove.NONE));

            return;
        }

        moves.add(new Move(getSquare(),
                square,
                null,
                false,
                false,
                SpecialMove.NONE));

    }
}

