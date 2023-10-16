package chessai.chessai.lib.pieces;

import chessai.chessai.lib.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class Rook extends SlidingPiece {
    public Rook(PieceColor color) {
        super(color);
    }

    @Override
    public char getFENChar() {
        return getColor() == PieceColor.WHITE ? 'R' : 'r';
    }

    @Override
    public List<Move> getPseudoLegalMoves(Board board) {
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

        return moves;
    }

    @Override
    public MoveResult getPseudoLegalMovesAsBitMaps(Board board) {

        final int currentFile = getSquare().file();
        final int currentRow = getSquare().row();

        BitMap otherColorPieces = color == PieceColor.WHITE ? board.blackPieces : board.whitePieces;
        BitMap sameColorPieces = color == PieceColor.BLACK ? board.blackPieces : board.whitePieces;
        BitMap otherColorKing = color == PieceColor.WHITE ? board.blackKing : board.whiteKing;

        MoveResult result = new MoveResult();

        slide(currentFile, currentRow, otherColorPieces, sameColorPieces, otherColorKing, result, 1, 0);
        slide(currentFile, currentRow, otherColorPieces, sameColorPieces, otherColorKing, result, -1, 0);
        slide(currentFile, currentRow, otherColorPieces, sameColorPieces, otherColorKing, result, 0, 1);
        slide(currentFile, currentRow, otherColorPieces, sameColorPieces, otherColorKing, result, 0, -1);

        return result;

    }

    @Override
    public Piece copy() {
        return new Rook(color);
    }

    /**
     * @param board   the board in which me want to move
     * @param moves   the list of moves that we amend
     * @param _square the _square we want to look at
     * @return whether we terminate the current loop
     */
    private boolean determineWhetherItCanMoveToSquare(Board board, List<Move> moves, Square _square) {

        Optional<PieceColor> color = board.getColorAtSquare(_square);

        if (color.isPresent()) {

            if (color.get().equals(getColor()))
                return true;

            moves.add(new Move(square,
                    _square,
                    null,
                    true,
                    false,
                    SpecialMove.NONE));

            return true;
        }

        moves.add(new Move(square,
                _square,
                null,
                false,
                false,
                SpecialMove.NONE));

        return false;
    }
}

