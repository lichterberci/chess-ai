package chessai.chessai.lib;

import chessai.chessai.lib.pieces.King;
import chessai.chessai.lib.pieces.Pawn;
import chessai.chessai.lib.pieces.Rook;
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

    public Board(Board other) {
        this(other.squares,
                other.colorToMove,
                other.canBlackCastleKingSide,
                other.canBlackCastleQueenSide,
                other.canWhiteCastleKingSide,
                other.canWhiteCastleQueenSide,
                other.enPassantTarget,
                other.fullMoveClock,
                other.halfMoveCounter);
    }
    public Board (Piece[] squares,
                  PieceColor colorToMove,
                  boolean canBlackCastleKingSide,
                  boolean canBlackCastleQueenSide,
                  boolean canWhiteCastleKingSide,
                  boolean canWhiteCastleQueenSide,
                  Square enPassantTarget,
                  int fullMoveClock,
                  int halfMoveCounter
    ) {
        this.squares = squares;
        this.colorToMove = colorToMove;
        this.canBlackCastleKingSide = canBlackCastleKingSide;
        this.canBlackCastleQueenSide = canBlackCastleQueenSide;
        this.canWhiteCastleKingSide = canWhiteCastleKingSide;
        this.canWhiteCastleQueenSide = canWhiteCastleQueenSide;
        this.enPassantTarget = enPassantTarget;
        this.fullMoveClock = fullMoveClock;
        this.halfMoveCounter = halfMoveCounter;
    }
    public Board (String fenString) throws ParseException {
        setFromFENString(fenString);
    }
    public Piece get (@NotNull Square square) {
        return squares[square.getIndex()];
    }
    public Board move (Square from, Square to) {
        Piece movingPiece = get(from);

        if (movingPiece == null)
            return new Board(this);

        Board result = new Board(this);

        Piece[] newSquares = squares.clone();
        newSquares[to.getIndex()] = newSquares[from.getIndex()];

        result.squares = newSquares;

        if (movingPiece.getColor() == PieceColor.WHITE) {
            if (movingPiece instanceof King) {
                result.canWhiteCastleQueenSide = false;
                result.canWhiteCastleKingSide = false;
            }
            else if (movingPiece instanceof Rook) {
                if (from.equals(new Square("a1")))
                    result.canWhiteCastleQueenSide = false;
                else if (from.equals(new Square("h1")))
                    result.canWhiteCastleKingSide = false;
            }
        } else {
            if (movingPiece instanceof King) {
                result.canBlackCastleQueenSide = false;
                result.canBlackCastleKingSide = false;
            }
            else if (movingPiece instanceof Rook) {
                if (from.equals(new Square("a8")))
                    result.canBlackCastleQueenSide = false;
                else if (from.equals(new Square("h8")))
                    result.canBlackCastleQueenSide = false;
            }
        }

        // en passant target detection
        if (movingPiece instanceof Pawn && Math.abs(from.row() - to.row()) == 2)
            result.enPassantTarget = new Square(from.file(), (from.row() + to.row()) / 2);

        result.colorToMove = colorToMove == PieceColor.WHITE ? PieceColor.BLACK : PieceColor.WHITE;

        // move counts
        if (get(to) != null || movingPiece instanceof Pawn) {
            result.halfMoveCounter = 0;
            result.fullMoveClock = 0;
        } else {
            result.halfMoveCounter++;
            if (result.halfMoveCounter % 2 == 0)
                result.fullMoveClock++;
        }

        return result;
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


