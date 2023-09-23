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

        Optional<PieceColor> color;

        for (int i = currentFile; i < 8; i++) {
            if ((color = board.getColorAtSquare(currentFile + i, currentRow)).isPresent()) {

                if (color.get().equals(getColor()))
                    break;

                moves.add(new Move(getSquare(),
                        new Square(currentFile + i, currentRow),
                        null,
                        true,
                        false,
                        SpecialMove.NONE));

                break;
            }


            moves.add(new Move(getSquare(),
                    new Square(currentFile + i, currentRow),
                    null,
                    false,
                    false,
                    SpecialMove.NONE));
        }
        return moves;
    }
}

