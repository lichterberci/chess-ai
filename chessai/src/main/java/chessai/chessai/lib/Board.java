package chessai.chessai.lib;

import org.jetbrains.annotations.NotNull;

import java.text.ParseException;
import java.util.List;

public class Board {

    /**
     * 0 = A8
     * 1 = B8
     * ...
     * 8 = A7
     * ...
     * 63 = H1
     * */
    Piece[] squares;
    PieceColor colorToMove;
    boolean canBlackCastleKingSide;
    boolean canBlackCastleQueenSide;
    boolean canWhiteCastleKingSide = true;
    boolean canWhiteCastleQueenSide;
    Square enPassantTarget;
    int fullMoveClock;
    int halfMoveCounter;

    public Board (String fenString) throws ParseException {
        setFromFENString(fenString);
    }
    public Piece get (@NotNull Square square) {
        return squares[square.getIndex()];
    }
    public void setFromFENString(@NotNull String fenString) throws ParseException {

        squares = new Piece[64];

        String[] fenStringParts = fenString.split(" ");

        if (fenStringParts.length != 6)
            throw new ParseException("FEN string has invalid number of parts!", 0);

        // board part

        int numSquaresDone = 0;
        for (int i = 0; numSquaresDone < 64; i++) {

            char c = fenStringParts[0].charAt(i);

            if (c == '/') {

                if (numSquaresDone % 8 != 0)
                    throw new ParseException("Slash is at the wrong place!", i);

                continue;
            }

            if (c > '0' && c <= '8') {
                numSquaresDone += (c - '0');

                continue;
            }

            if (Character.isAlphabetic(c)) {
                try {
                    squares[numSquaresDone++] = PieceFactory.generateFromChar(c);
                } catch (IllegalStateException e) {
                    ParseException newException =  new ParseException("Piece not recognized!", i);
                    newException.initCause(e);
                    throw newException;
                }

                continue;
            }

            throw new ParseException("Invalid character in FEN string: " + c, i);
        }

        if (numSquaresDone != 64)
            throw new ParseException("FEN does not describe all 64 squares!", fenStringParts[0].length());

        // next player to move

        if (!List.of('w', 'b').contains(Character.toLowerCase(fenStringParts[1].charAt(0))))
            throw new ParseException("Invalid side!", 4);

        colorToMove = fenStringParts[1].charAt(0) == 'w' ? PieceColor.WHITE : PieceColor.BLACK;

        // castling rights

        canBlackCastleKingSide = false;
        canWhiteCastleKingSide = false;
        canBlackCastleQueenSide = false;
        canWhiteCastleQueenSide = false;

        for (int i = 0; i < fenStringParts[2].length(); i++) {

            char c = fenStringParts[2].charAt(i);

            if (c == '-')
                break;

            switch (c) {
                case 'K' -> canWhiteCastleKingSide = true;
                case 'k' -> canBlackCastleKingSide = true;
                case 'q' -> canBlackCastleQueenSide = true;
                case 'Q' -> canWhiteCastleQueenSide = true;
                default -> throw new ParseException("Invalid castling right character!", 2);
            }
        }

        // en passant

        enPassantTarget = fenStringParts[3].charAt(0) == '-' ? null : new Square(fenStringParts[3]);

        // half moves

        halfMoveCounter = Integer.parseInt(fenStringParts[4]);
        fullMoveClock = Integer.parseInt(fenStringParts[5]);
    }
    public String getFENString () {
        StringBuilder sb = new StringBuilder(40);

        sb.append(getFENPositionString());

        sb.append(' ');

        sb.append(colorToMove == PieceColor.WHITE ? 'w' : 'b');

        sb.append(' ');

        if (canWhiteCastleKingSide)
            sb.append('K');
        if (canWhiteCastleQueenSide)
            sb.append('Q');
        if (canBlackCastleKingSide)
            sb.append('k');
        if (canBlackCastleQueenSide)
            sb.append('q');

        if (!canWhiteCastleQueenSide && !canWhiteCastleKingSide && !canBlackCastleKingSide && !canBlackCastleQueenSide)
            sb.append('-');

        sb.append(' ');

        if (enPassantTarget != null)
            sb.append(enPassantTarget);
        else
            sb.append('-');

        sb.append(' ');

        sb.append(halfMoveCounter);

        sb.append(' ');

        sb.append(fullMoveClock);

        return sb.toString();

    }
    public String getFENPositionString () {
        StringBuilder sb = new StringBuilder(32);

        int emptySquaresCount = 0;
        for (int i = 0; i < 64; i++) {
            Piece piece = squares[i];

            if (piece == null) {
                emptySquaresCount++;

                if (i % 8 == 7) {
                    sb.append(emptySquaresCount);

                    if (i != 63)
                        sb.append('/');

                    emptySquaresCount = 0;
                }

                continue;
            }

            if (emptySquaresCount > 0) {
                sb.append(emptySquaresCount);
            }

            emptySquaresCount = 0;
            sb.append(squares[i].getFENChar());

            if (i % 8 == 7 && i != 63)
                sb.append('/');
        }

        return sb.toString();
    }
}


