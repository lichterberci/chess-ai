package chessai.chessai.lib.pieces;

import chessai.chessai.lib.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class Bishop extends Piece {
    public Bishop (PieceColor color) {
        super(color);
    }
    @Override
    public char getFENChar() {
        return getColor() == PieceColor.WHITE ? 'B' : 'b';
    }

    @Override
    public List<Move> getPseudoLegalMoves(Board board) {
        final int currentFile = getSquare().file();
        final int currentRow = getSquare().row();

        List<Move> moves = new ArrayList<>();


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
    public MoveResult getPseudoLegalMovesAsBitMaps(Board board) {
        return null;
    }

    @Override
    public Piece copy() {
        return new Bishop(color);
    }

    /**
     * @param board the board in which me want to move
     * @param moves the list of moves that we amend
     * @param square the square we want to look at
     * @return whether we terminate the current loop
     */
    private boolean determineWhetherItCanMoveToSquare(Board board, List<Move> moves, Square square) {

        Optional<PieceColor> color = board.getColorAtSquare(square);

        if (color.isPresent()) {

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

