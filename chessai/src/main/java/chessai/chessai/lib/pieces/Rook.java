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

        return result;

    }

    private static void slide(int currentFile, int currentRow, BitMap otherColorPieces, BitMap sameColorPieces, BitMap otherColorKing, MoveResult result, int fileOffset, int rowOffset) {

        final int currentSquareIndex = Square.getIndex(currentFile, currentRow);

        boolean onlyAttacks = false;

        for (int i = 0; i < 8; i++) {

            final int squareIndex = currentSquareIndex + (fileOffset + rowOffset * 8);

            // we moved out of the board
            if (squareIndex == -1)
                return;

            // out friendly piece is in the way
            if (sameColorPieces.getBit(squareIndex))
                return;

            // we can attack this square
            result.attackTargetsWithoutEnemyKingOnBoard().setBitInPlace(squareIndex, true);

            // if we are not behind the king, we can move there
            if (!onlyAttacks)
                result.moveTargets().setBitInPlace(squareIndex, true);

            // an enemy piece is in the way
            if (otherColorPieces.getBit(squareIndex)) {

                result.moveTargets().setBitInPlace(squareIndex, true);
                result.isResultCapture().setBitInPlace(squareIndex, true);

                // if it is not the king, we return
                if (!otherColorKing.getBit(squareIndex))
                    return;

                // if it way the enemy king, we can attack behind him as well
                onlyAttacks = true;
            }
        }
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

