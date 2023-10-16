package chessai.chessai.lib.pieces;

import chessai.chessai.lib.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class Pawn extends Piece {
    public Pawn (PieceColor color) {
        super(color);
    }
    @Override
    public char getFENChar() {
        return getColor() == PieceColor.WHITE ? 'P' : 'p';
    }

    @Override
    public List<Move> getPseudoLegalMoves(Board board) {
        List<Move> moves = new ArrayList<>();

        if (getColor() == PieceColor.WHITE) {

            if (square.row() <= 5) {
                if (board.get(new Square(square.file(), square.row() + 1)) == null)
                    moves.add(new Move(
                            square,
                            new Square(square.file(), square.row() + 1),
                            null,
                            false,
                            false,
                            SpecialMove.NONE
                    ));

                if (square.file() < 7 && Optional.ofNullable(
                            board.get(new Square(square.file() + 1, square.row() + 1))
                    ).map(Piece::getColor)
                    .orElse(PieceColor.WHITE) == PieceColor.BLACK
                )
                    moves.add(new Move(
                            square,
                            new Square(square.file() + 1, square.row() + 1),
                            null,
                            true,
                            false,
                            SpecialMove.NONE
                    ));

                if (square.file() > 0 && Optional.ofNullable(
                        board.get(new Square(square.file() - 1, square.row() + 1))
                    ).map(Piece::getColor)
                    .orElse(PieceColor.WHITE) == PieceColor.BLACK
                )
                    moves.add(new Move(
                            square,
                            new Square(square.file() - 1, square.row() + 1),
                            null,
                            true,
                            false,
                            SpecialMove.NONE
                    ));
            }

            if (square.row() == 4 && board.enPassantTarget != null
                    && (Math.abs(board.enPassantTarget.file() - square.file()) == 1)) {
                moves.add(new Move(
                        square,
                        board.enPassantTarget,
                        null,
                        true,
                        true,
                        SpecialMove.NONE
                ));

            }

            if (square.row() == 1
                    && (board.get(new Square(square.file(), square.row() + 1)) == null && board.get(new Square(square.file(), square.row() + 2)) == null)) {
                moves.add(new Move(
                        square,
                        new Square(square.file(), square.row() + 2),
                        null,
                        false,
                        false,
                        SpecialMove.DOUBLE_PAWN_PUSH
                ));

            }

            if (square.row() == 6) {

                if (board.get(new Square(square.file(), 7)) == null)
                    addPromotionMoves(moves, new Square(square.file(), square.row() + 1), false);

                if (square.file() < 7 && Optional.ofNullable(
                                board.get(new Square(square.file() + 1, square.row() + 1))
                        ).map(Piece::getColor)
                        .orElse(PieceColor.WHITE) == PieceColor.BLACK
                )
                    addPromotionMoves(moves, new Square(square.file() + 1, square.row() + 1), true);

                if (square.file() > 0 && Optional.ofNullable(
                                board.get(new Square(square.file() - 1, square.row() + 1))
                        ).map(Piece::getColor)
                        .orElse(PieceColor.WHITE) == PieceColor.BLACK
                )
                    addPromotionMoves(moves, new Square(square.file() - 1, square.row() + 1), true);

            }

        } else {

            if (square.row() >= 2) {
                if (board.get(new Square(square.file(), square.row() - 1)) == null)
                    moves.add(new Move(
                            square,
                            new Square(square.file(), square.row() - 1),
                            null,
                            false,
                            false,
                            SpecialMove.NONE
                    ));

                if (Optional.ofNullable(
                                board.get(new Square(square.file() + 1, square.row() - 1))
                        ).map(Piece::getColor)
                        .orElse(PieceColor.BLACK) == PieceColor.WHITE
                )
                    moves.add(new Move(
                            square,
                            new Square(square.file() + 1, square.row() - 1),
                            null,
                            true,
                            false,
                            SpecialMove.NONE
                    ));

                if (Optional.ofNullable(
                                board.get(new Square(square.file() - 1, square.row() - 1))
                        ).map(Piece::getColor)
                        .orElse(PieceColor.BLACK) == PieceColor.WHITE
                )
                    moves.add(new Move(
                            square,
                            new Square(square.file() - 1, square.row() - 1),
                            null,
                            true,
                            false,
                            SpecialMove.NONE
                    ));
            }

            if (square.row() == 3 && board.enPassantTarget != null
                    && (Math.abs(board.enPassantTarget.file() - square.file()) == 1)) {
                moves.add(new Move(
                        square,
                        board.enPassantTarget,
                        null,
                        true,
                        true,
                        SpecialMove.NONE
                ));

            }

            if (square.row() == 6
                    && (board.get(new Square(square.file(), square.row() - 1)) == null && board.get(new Square(square.file(), square.row() - 2)) == null)) {
                moves.add(new Move(
                        square,
                        new Square(square.file(), square.row() - 2),
                        null,
                        false,
                        false,
                        SpecialMove.DOUBLE_PAWN_PUSH
                ));

            }

            if (square.row() == 1) {

                if (board.get(new Square(square.file(), 0)) == null)
                    addPromotionMoves(moves, new Square(square.file(), square.row() - 1), false);

                if (square.file() < 7 && Optional.ofNullable(
                                board.get(new Square(square.file() + 1, square.row() - 1))
                        ).map(Piece::getColor)
                        .orElse(PieceColor.BLACK) == PieceColor.WHITE
                )
                    addPromotionMoves(moves, new Square(square.file() + 1, square.row() - 1), true);

                if (square.file() > 0 && Optional.ofNullable(
                                board.get(new Square(square.file() - 1, square.row() - 1))
                        ).map(Piece::getColor)
                        .orElse(PieceColor.BLACK) == PieceColor.WHITE
                )
                    addPromotionMoves(moves, new Square(square.file() - 1, square.row() - 1), true);

            }

        }

        return moves;
    }

    @Override
    public MoveResult getPseudoLegalMovesAsBitMaps(Board board) {
        return null;
    }

    @Override
    public Piece copy() {
        return new Pawn(color);
    }

    private void addPromotionMoves(List<Move> moves, Square toSquare, boolean isCapture) {

        if (toSquare.file() < 0 || toSquare.file() > 7)
            return;

        moves.add(new Move(
                square,
                toSquare,
                Queen.class,
                isCapture,
                false,
                SpecialMove.NONE
        ));

        moves.add(new Move(
                square,
                toSquare,
                Rook.class,
                isCapture,
                false,
                SpecialMove.NONE
        ));

        moves.add(new Move(
                square,
                toSquare,
                Bishop.class,
                isCapture,
                false,
                SpecialMove.NONE
        ));

        moves.add(new Move(
                square,
                toSquare,
                Knight.class,
                isCapture,
                false,
                SpecialMove.NONE
        ));
    }
}
