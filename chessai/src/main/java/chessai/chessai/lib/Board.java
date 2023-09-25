package chessai.chessai.lib;

import chessai.chessai.lib.pieces.King;
import chessai.chessai.lib.pieces.Pawn;
import chessai.chessai.lib.pieces.Rook;
import org.jetbrains.annotations.NotNull;

import java.text.ParseException;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

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
    public PieceColor colorToMove;
    public boolean canBlackCastleKingSide;
    public boolean canBlackCastleQueenSide;
    public boolean canWhiteCastleKingSide;
    public boolean canWhiteCastleQueenSide;
    public Square enPassantTarget;
    public int fullMoveClock;
    public int halfMoveCounter;

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
    public Optional<PieceColor> getColorAtSquare (Square square) { return Optional.ofNullable(squares[square.getIndex()]).map(Piece::getColor); }
    public Optional<PieceColor> getColorAtSquare (int index) { return Optional.ofNullable(squares[index]).map(Piece::getColor); }
    public Optional<PieceColor> getColorAtSquare (int file, int row) { return Optional.ofNullable(squares[file + (7 - row) * 8]).map(Piece::getColor); }
    public boolean isKingInCheck(PieceColor color) {

        int kingSquareIndex = Arrays.stream(squares)
                .filter(Objects::nonNull)
                .filter(piece -> piece.getColor() == color)
                .findFirst(piece -> (piece instanceof King) == true)
                .orElseThrow(() -> new IllegalStateException("There is no king on side " + color))
                .getSquare()
                .getIndex();

        for (Piece piece : squares) {

            if (piece == null)
                return;

            if (piece.getColor() == color)
                continue;

            if (piece instanceof King)
                continue;

            if (piece
                .getAllPossibleMoves(this)
                .stream()
                .filter(Move::isCapture)
                .anyMatch(move -> move.to().getIndex() == kingSquareIndex)
            ) {
                return true;
            }
        }

        return  false;
    }
    public Board move (Move move) {

        Square from = move.from();
        Square to = move.to();

        Piece movingPiece = get(from);

        if (movingPiece == null)
            return new Board(this);

        Board result = new Board(this);

        Piece[] newSquares = squares.clone();

        if (move.specialMove() == SpecialMove.NONE || move.specialMove() == SpecialMove.DOUBLE_PAWN_PUSH) {

            if (move.isEnPassant()) {
                newSquares[enPassantTarget.getIndex()] = squares[from.getIndex()];

                if (colorToMove == PieceColor.WHITE) {
                    newSquares[new Square(enPassantTarget.file(), enPassantTarget.row() - 1).getIndex()] = null;
                } else {
                    newSquares[new Square(enPassantTarget.file(), enPassantTarget.row() + 1).getIndex()] = null;
                }
            } else {
                newSquares[to.getIndex()] = squares[from.getIndex()];
            }

            newSquares[from.getIndex()] = null;
        }
        else if (move.specialMove() == SpecialMove.QUEEN_SIDE_CASTLE) {
            if (colorToMove == PieceColor.WHITE) {
                newSquares[new Square("d1").getIndex()] = squares[new Square("a1").getIndex()];
                newSquares[new Square("c1").getIndex()] = squares[new Square("e1").getIndex()];
                newSquares[new Square("e1").getIndex()] = null;
                newSquares[new Square("a1").getIndex()] = null;
            }
            else {
                newSquares[new Square("d8").getIndex()] = squares[new Square("a8").getIndex()];
                newSquares[new Square("c8").getIndex()] = squares[new Square("e8").getIndex()];
                newSquares[new Square("e8").getIndex()] = null;
                newSquares[new Square("a8").getIndex()] = null;
            }
        }
        else if (move.specialMove() == SpecialMove.KING_SIDE_CASTLE) {
            if (colorToMove == PieceColor.WHITE) {
                newSquares[new Square("f1").getIndex()] = squares[new Square("h1").getIndex()];
                newSquares[new Square("g1").getIndex()] = squares[new Square("e1").getIndex()];
                newSquares[new Square("e1").getIndex()] = null;
                newSquares[new Square("h1").getIndex()] = null;
            }
            else {
                newSquares[new Square("f8").getIndex()] = squares[new Square("h8").getIndex()];
                newSquares[new Square("g8").getIndex()] = squares[new Square("e8").getIndex()];
                newSquares[new Square("e8").getIndex()] = null;
                newSquares[new Square("h8").getIndex()] = null;
            }
        }

        if (move.promotionPieceType() != null) {
            try {
                newSquares[to.getIndex()] = move.promotionPieceType().getConstructor(PieceColor.class).newInstance(colorToMove);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }

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
        if (move.specialMove() == SpecialMove.DOUBLE_PAWN_PUSH)
            result.enPassantTarget = new Square(from.file(), (from.row() + to.row()) / 2);

        result.colorToMove = colorToMove == PieceColor.WHITE ? PieceColor.BLACK : PieceColor.WHITE;

        // move counts
        if (move.isCapture() || movingPiece instanceof Pawn) {
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
                    Piece newPiece = squares[numSquaresDone++] = PieceFactory.generateFromChar(c);
                    newPiece.setSquare(new Square(numSquaresDone - 1));
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


