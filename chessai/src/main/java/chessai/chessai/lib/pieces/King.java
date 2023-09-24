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
    public List<Move> getAllPossibleMoves(Board board) {

        List<Move> moves = new ArrayList<>();

        for (int f = Math.max(getSquare().file() - 1, 0); f <= Math.min(getSquare().file() + 1, 7); f++) {
            for (int r = Math.max(getSquare().row() - 1, 0); r <= Math.min(getSquare().row() + 1, 7); r++) {

                Square square = new Square(f, r);

                Optional<PieceColor> color;

                if ((color = board.getColorAtSquare(square)).isPresent()) {

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

        return moves;
    }
}

