package chessai.chessai.lib.pieces;

import chessai.chessai.lib.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class Knight extends Piece {

    private static final int MOVE_TARGET_START_FILE = 5;
    private static final int MOVE_TARGET_START_ROW = 2;
    private static BitMap moveTargetMap;

    static {
        moveTargetMap = new BitMap(
                "00000000" +
                        "00000000" +
                        "00000000" +
                        "00001010" +
                        "00010001" +
                        "00000000" +
                        "00010001" +
                        "00001010"
        );
    }

    public Knight(PieceColor color) {
        super(color);
    }

    @Override
    public char getFENChar() {
        return getColor() == PieceColor.WHITE ? 'N' : 'n';
    }

    @Override
    public List<Move> getPseudoLegalMoves(Board board) {
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
    public MoveResult getPseudoLegalMovesAsBitMaps(Board board) {
        final int currentFile = getSquare().file();
        final int currentRow = getSquare().row();

        BitMap otherColorPieces = color == PieceColor.WHITE ? board.blackPieces : board.whitePieces;
        BitMap sameColorPieces = color == PieceColor.BLACK ? board.blackPieces : board.whitePieces;

        MoveResult result = new MoveResult();

        BitMap offsetMoveMap = moveTargetMap.shift(-(currentFile - MOVE_TARGET_START_FILE), -(currentRow - MOVE_TARGET_START_ROW));

        result.moveTargets().orInPlace(offsetMoveMap.xor(sameColorPieces));
        result.isResultCapture().orInPlace(offsetMoveMap.and(otherColorPieces));
        result.attackTargetsWithoutEnemyKingOnBoard().orInPlace(offsetMoveMap);

        return result;
    }

    @Override
    public Piece copy() {
        return new Knight(color);
    }

    private void checkSquare(Board board, List<Move> moves, int file, int row) {

        if (file < 0 || row < 0 || file > 7 || row > 7)
            return;

        Square square = new Square(file, row);

        Optional<PieceColor> color = board.getColorAtSquare(square);

        if (color.isPresent()) {

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

