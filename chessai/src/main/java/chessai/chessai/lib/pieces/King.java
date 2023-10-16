package chessai.chessai.lib.pieces;

import chessai.chessai.lib.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class King extends Piece {
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
        return null;
    }

    @Override
    public Piece copy() {
        return new King(color);
    }
}

