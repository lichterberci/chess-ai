package chessai.chessai.lib.pieces;

import chessai.chessai.lib.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class King extends Piece {

    private static final int MOVE_TARGET_START_FILE = 6;
    private static final int MOVE_TARGET_START_ROW = 1;
    private static final BitMap MOVE_TARGET_MAP = new BitMap(
            "00000000" +
                    "00000000" +
                    "00000000" +
                    "00000000" +
                    "00000000" +
                    "00000111" +
                    "00000101" +
                    "00000111"
    );

    public King (PieceColor color) {
        super(color);
    }
    @Override
    public char getFENChar() {
        return getColor() == PieceColor.WHITE ? 'K' : 'k';
    }
    @Override
    public List<Move> getPseudoLegalMoves(Board board) {

        List<Move> moves = new ArrayList<>();

        for (int f = Math.max(getSquare().file() - 1, 0); f <= Math.min(getSquare().file() + 1, 7); f++) {
            for (int r = Math.max(getSquare().row() - 1, 0); r <= Math.min(getSquare().row() + 1, 7); r++) {

                Square square = new Square(f, r);

                Optional<PieceColor> color = board.getColorAtSquare(square);

                if (color.isPresent()) {

                    if (color.get().equals(getColor()))
                        continue;

                    moves.add(new Move(getSquare(),
                            square,
                            null,
                            true,
                            false,
                            SpecialMove.NONE));

                    continue;
                }

                moves.add(new Move(getSquare(),
                        square,
                        null,
                        false,
                        false,
                        SpecialMove.NONE));

            }
        }

        if (getColor() == PieceColor.WHITE) {
            if (board.canWhiteCastleKingSide
                    && board.get(new Square("g1")) == null
                    && board.get(new Square("f1")) == null
            ) {
                moves.add(new Move(
                        getSquare(),
                        new Square("g1"),
                        null,
                        false,
                        false,
                        SpecialMove.KING_SIDE_CASTLE
                ));
            }
            if (board.canWhiteCastleQueenSide
                    && board.get(new Square("b1")) == null
                    && board.get(new Square("c1")) == null
                    && board.get(new Square("d1")) == null
            ) {
                moves.add(new Move(
                        getSquare(),
                        new Square("c1"),
                        null,
                        false,
                        false,
                        SpecialMove.QUEEN_SIDE_CASTLE
                ));
            }
        } else {
            if (board.canBlackCastleQueenSide
                    && board.get(new Square("g8")) == null
                    && board.get(new Square("f8")) == null
            ) {
                moves.add(new Move(
                        getSquare(),
                        new Square("g8"),
                        null,
                        false,
                        false,
                        SpecialMove.KING_SIDE_CASTLE
                ));
            }
            if (board.canBlackCastleQueenSide
                    && board.get(new Square("b8")) == null
                    && board.get(new Square("c8")) == null
                    && board.get(new Square("d8")) == null
            ) {
                moves.add(new Move(
                        getSquare(),
                        new Square("c8"),
                        null,
                        false,
                        false,
                        SpecialMove.QUEEN_SIDE_CASTLE
                ));
            }
        }

        return moves;
    }

    @Override
    public MoveResult getPseudoLegalMovesAsBitMaps(Board board) {
        final int currentFile = getSquare().file();
        final int currentRow = getSquare().row();

        BitMap otherColorPieces = color == PieceColor.WHITE ? board.blackPieces : board.whitePieces;
        BitMap sameColorPieces = color == PieceColor.BLACK ? board.blackPieces : board.whitePieces;

        MoveResult result = new MoveResult();

        BitMap offsetMoveMap = MOVE_TARGET_MAP.shift((currentFile - MOVE_TARGET_START_FILE), (currentRow - MOVE_TARGET_START_ROW));

        result.moveTargets().orInPlace(offsetMoveMap.and(sameColorPieces.invert()));
        result.isResultCapture().orInPlace(offsetMoveMap.and(sameColorPieces.invert()).and(otherColorPieces));
        result.attackTargetsWithoutEnemyKingOnBoard().orInPlace(offsetMoveMap);

        // castling

        BitMap piecesOnBoard = board.whitePieces.or(board.blackPieces);

        if (color == PieceColor.WHITE) {

            // white, king-side
            if (board.canWhiteCastleKingSide
                    && !piecesOnBoard.getBit(Square.getIndex("F1"))
                    && !piecesOnBoard.getBit(Square.getIndex("G1"))
            ) {
                result.moveTargets().setBitInPlace(Square.getIndex("G1"), true);
                result.isResultKingSideCastle().setBitInPlace(Square.getIndex("G1"), true);
            }

            // white, queen-side
            if (board.canWhiteCastleQueenSide
                    && !piecesOnBoard.getBit(Square.getIndex("B1"))
                    && !piecesOnBoard.getBit(Square.getIndex("C1"))
                    && !piecesOnBoard.getBit(Square.getIndex("D1"))
            ) {
                result.moveTargets().setBitInPlace(Square.getIndex("C1"), true);
                result.isResultQueenSideCastle().setBitInPlace(Square.getIndex("C1"), true);
            }

        } else {

            // black, king-side
            if (board.canBlackCastleKingSide
                    && !piecesOnBoard.getBit(Square.getIndex("F8"))
                    && !piecesOnBoard.getBit(Square.getIndex("G8"))
            ) {
                result.moveTargets().setBitInPlace(Square.getIndex("G8"), true);
                result.isResultKingSideCastle().setBitInPlace(Square.getIndex("G8"), true);
            }

            // white, queen-side
            if (board.canBlackCastleQueenSide
                    && !piecesOnBoard.getBit(Square.getIndex("B8"))
                    && !piecesOnBoard.getBit(Square.getIndex("C8"))
                    && !piecesOnBoard.getBit(Square.getIndex("D8"))
            ) {
                result.moveTargets().setBitInPlace(Square.getIndex("C8"), true);
                result.isResultQueenSideCastle().setBitInPlace(Square.getIndex("C8"), true);
            }

        }

        return result;
    }

    @Override
    public Piece copy() {
        return new King(color);
    }
}

