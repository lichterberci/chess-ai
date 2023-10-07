package chessai.chessai.lib;

import chessai.chessai.lib.pieces.*;
import org.jetbrains.annotations.NotNull;

import java.text.ParseException;
import java.util.*;

public class Board {

    /**
     * 0 = A8
     * 1 = B8
     * ...
     * 8 = A7
     * ...
     * 63 = H1
     */
    Piece[] squares;
    public PieceColor colorToMove;
    public boolean canBlackCastleKingSide;
    public boolean canBlackCastleQueenSide;
    public boolean canWhiteCastleKingSide;
    public boolean canWhiteCastleQueenSide;
    public Square enPassantTarget;
    public int fullMoveClock;
    public int halfMoveCounter;
    public List<String> previousPositions;

    public Board(Board other) {
        this(other.squares,
                other.colorToMove,
                other.canBlackCastleKingSide,
                other.canBlackCastleQueenSide,
                other.canWhiteCastleKingSide,
                other.canWhiteCastleQueenSide,
                other.enPassantTarget,
                other.fullMoveClock,
                other.halfMoveCounter,
                other.previousPositions);
    }

    public Board(Piece[] squares,
                 PieceColor colorToMove,
                 boolean canBlackCastleKingSide,
                 boolean canBlackCastleQueenSide,
                 boolean canWhiteCastleKingSide,
                 boolean canWhiteCastleQueenSide,
                 Square enPassantTarget,
                 int fullMoveClock,
                 int halfMoveCounter,
                 List<String> previousPositions
    ) {
        this.squares = new Piece[64];

        for (int i = 0; i < 64; i++) {
            if (squares[i] == null)
                continue;
            try {
//                this.squares[i] = squares[i].getClass().getConstructor(PieceColor.class).newInstance(squares[i].getColor());
                this.squares[i] = squares[i].copy();
            } catch (Exception e) {
                System.err.println(e.getMessage());
            }
            this.squares[i].setSquare(new Square(i));
        }

        this.colorToMove = colorToMove;
        this.canBlackCastleKingSide = canBlackCastleKingSide;
        this.canBlackCastleQueenSide = canBlackCastleQueenSide;
        this.canWhiteCastleKingSide = canWhiteCastleKingSide;
        this.canWhiteCastleQueenSide = canWhiteCastleQueenSide;
        this.enPassantTarget = enPassantTarget;
        this.fullMoveClock = fullMoveClock;
        this.halfMoveCounter = halfMoveCounter;
        this.previousPositions = new ArrayList<>();
        if (previousPositions != null)
            this.previousPositions.addAll(previousPositions);
    }

    public Board(String fenString) throws ParseException {
        setFromFENString(fenString);
    }

    public Piece get(@NotNull Square square) {
        return squares[square.getIndex()];
    }

    public Optional<PieceColor> getColorAtSquare(Square square) {
        if (squares[square.getIndex()] == null)
            return Optional.empty();
        return Optional.of(squares[square.getIndex()].color);
    }

    public Optional<PieceColor> getColorAtSquare(int index) {
        return Optional.ofNullable(squares[index]).map(Piece::getColor);
    }

    public Optional<PieceColor> getColorAtSquare(int file, int row) {
        return Optional.ofNullable(squares[file + (7 - row) * 8]).map(Piece::getColor);
    }

    public boolean isKingInCheck(PieceColor color) {

        Optional<Piece> found = Optional.empty();
        for (Piece piece : squares) {
            if (piece != null) {
                if (piece.getColor() == color) {
                    if ((piece instanceof King)) {
                        found = Optional.of(piece);
                        break;
                    }
                }
            }
        }

        int kingSquareIndex = found
                .orElseThrow(() -> new IllegalStateException("There is no king on side " + color))
                .getSquare()
                .getIndex();

        return isKingInCheck(color, kingSquareIndex);
    }

    public boolean isKingInCheck(PieceColor color, int kingSquareIndex) {

        for (Piece piece : squares) {

            if (piece == null)
                continue;

            if (piece.getColor() == color)
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

        return false;
    }

    public GameState getState() {

        if (halfMoveCounter >= 100)
            return GameState.DRAW;

        LinkedList<Piece> piecesWithRightColor = new LinkedList<>();
        for (Piece square : squares) {
            if (square != null) {
                if (square.getColor() == colorToMove) {
                    piecesWithRightColor.add(square);
                }
            }
        }

        LinkedList<Piece> piecesWithOppositeColor = new LinkedList<>();
        for (Piece square : squares) {
            if (square != null) {
                if (square.getColor() != colorToMove) {
                    piecesWithOppositeColor.add(square);
                }
            }
        }

        int numRightColorPieces = piecesWithRightColor.size();
        int numOppositeColorPieces = piecesWithOppositeColor.size();

        if (numRightColorPieces == 1 && numOppositeColorPieces == 1)
            return GameState.DRAW;

        boolean canRightWin = true;
        boolean canOppositeWin = true;

        if (numOppositeColorPieces == 2) {
            for (Piece oppositePiece : piecesWithOppositeColor)
                if (oppositePiece instanceof Bishop || oppositePiece instanceof Knight) {
                    canOppositeWin = false;
                    break;
                }
        }

        if (numRightColorPieces == 2) {
            for (Piece rightPiece : piecesWithRightColor)
                if (rightPiece instanceof Bishop || rightPiece instanceof Knight) {
                    canRightWin = false;
                    break;
                }
        }

        if (!canRightWin && !canOppositeWin)
            return GameState.DRAW;

//                .map(piece -> piece
//                                .getAllPossibleMoves(this)
//                                .stream()
//                                .filter(this::isMoveLegal)
//                                .collect(Collectors.toList())
//                )
//                .flatMap(List::stream)
//                .toList();

        List<Move> moves = new LinkedList<>();

        for (Piece piece : piecesWithRightColor) {

            var possibleMoves = piece.getAllPossibleMoves(this);

            for (Move move : possibleMoves) {

                if (isMoveLegal(move)) {
                    moves.add(move);
                }
            }
        }

        boolean hasMoves = !moves.isEmpty();

        if (hasMoves)
            return GameState.PLAYING;

        boolean isKingInCheck = isKingInCheck(colorToMove);

        return isKingInCheck ? (colorToMove == PieceColor.WHITE ? GameState.BLACK_WIN : GameState.WHITE_WIN) : GameState.DRAW;
    }

    public boolean isMoveLegal(Move _move) {
        Board boardAfterMove = move(_move);

        Piece movingPiece = squares[_move.from().getIndex()];

        if (!(movingPiece instanceof King)) {
            return !boardAfterMove.isKingInCheck(colorToMove);
        }

        King blackKing = (King) Arrays.stream(boardAfterMove.squares)
                .filter(Objects::nonNull)
                .filter(piece -> piece.getColor() == PieceColor.BLACK)
                .filter(piece -> (piece instanceof King))
                .findFirst().orElse(null);

        King whiteKing = (King) Arrays.stream(boardAfterMove.squares)
                .filter(Objects::nonNull)
                .filter(piece -> piece.getColor() == PieceColor.WHITE)
                .filter(piece -> (piece instanceof King))
                .findFirst().orElse(null);

        if (whiteKing == null || blackKing == null)
            return false;

        int kingFileDistance = Math.abs(whiteKing.getSquare().file() - blackKing.getSquare().file());
        int kingRowDistance = Math.abs(whiteKing.getSquare().row() - blackKing.getSquare().row());

        boolean isKingInCheck = boardAfterMove.isKingInCheck(colorToMove);

        // check for checks when castling

        if (colorToMove == PieceColor.WHITE) {
            if (_move.specialMove() == SpecialMove.KING_SIDE_CASTLE
                    && (
                    isKingInCheck(colorToMove, new Square("e1").getIndex())
                            || isKingInCheck(colorToMove, new Square("f1").getIndex())
                            || isKingInCheck(colorToMove, new Square("g1").getIndex())
            )
            ) {
                return false;
            }

            if (_move.specialMove() == SpecialMove.QUEEN_SIDE_CASTLE
                    && (
                    isKingInCheck(colorToMove, new Square("c1").getIndex())
                            || isKingInCheck(colorToMove, new Square("d1").getIndex())
                            || isKingInCheck(colorToMove, new Square("e1").getIndex())
            )
            ) {
                return false;
            }
        }
        if (colorToMove == PieceColor.BLACK) {
            if (_move.specialMove() == SpecialMove.KING_SIDE_CASTLE
                    && (
                    isKingInCheck(colorToMove, new Square("e8").getIndex())
                            || isKingInCheck(colorToMove, new Square("f8").getIndex())
                            || isKingInCheck(colorToMove, new Square("g8").getIndex())
            )
            ) {
                return false;
            }

            if (_move.specialMove() == SpecialMove.QUEEN_SIDE_CASTLE
                    && (
                    isKingInCheck(colorToMove, new Square("c8").getIndex())
                            || isKingInCheck(colorToMove, new Square("d8").getIndex())
                            || isKingInCheck(colorToMove, new Square("e8").getIndex())
            )
            ) {
                return false;
            }
        }

        return !isKingInCheck && (kingFileDistance > 1 || kingRowDistance > 1);

    }

    public Board move(Move move) {

        Square from = move.from();
        Square to = move.to();

        Piece movingPiece = get(from);

        if (movingPiece == null)
            return new Board(this);

        Board result = new Board(this);
        result.enPassantTarget = null;

        Piece[] newSquares = result.squares;

        if (move.specialMove() == SpecialMove.NONE || move.specialMove() == SpecialMove.DOUBLE_PAWN_PUSH) {

            if (move.isEnPassant()) {
                newSquares[enPassantTarget.getIndex()] = newSquares[from.getIndex()];
                newSquares[enPassantTarget.getIndex()].setSquare(enPassantTarget);

                if (colorToMove == PieceColor.WHITE) {
                    newSquares[new Square(enPassantTarget.file(), enPassantTarget.row() - 1).getIndex()] = null;
                } else {
                    newSquares[new Square(enPassantTarget.file(), enPassantTarget.row() + 1).getIndex()] = null;
                }
            } else {
                newSquares[to.getIndex()] = newSquares[from.getIndex()];
                newSquares[to.getIndex()].setSquare(to.copy());
            }

            newSquares[from.getIndex()] = null;
        } else if (move.specialMove() == SpecialMove.QUEEN_SIDE_CASTLE) {
            if (colorToMove == PieceColor.WHITE) {
                newSquares[new Square("d1").getIndex()] = newSquares[new Square("a1").getIndex()];
                newSquares[new Square("d1").getIndex()].setSquare(new Square("d1"));
                newSquares[new Square("c1").getIndex()] = newSquares[new Square("e1").getIndex()];
                newSquares[new Square("e1").getIndex()].setSquare(new Square("e1"));
                newSquares[new Square("e1").getIndex()] = null;
                newSquares[new Square("a1").getIndex()] = null;
            } else {
                newSquares[new Square("d8").getIndex()] = newSquares[new Square("a8").getIndex()];
                newSquares[new Square("d8").getIndex()].setSquare(new Square("d8"));
                newSquares[new Square("c8").getIndex()] = newSquares[new Square("e8").getIndex()];
                newSquares[new Square("c8").getIndex()].setSquare(new Square("c8"));
                newSquares[new Square("e8").getIndex()] = null;
                newSquares[new Square("a8").getIndex()] = null;
            }
        } else if (move.specialMove() == SpecialMove.KING_SIDE_CASTLE) {
            if (colorToMove == PieceColor.WHITE) {
                newSquares[new Square("f1").getIndex()] = newSquares[new Square("h1").getIndex()];
                newSquares[new Square("f1").getIndex()].setSquare(new Square("f1"));
                newSquares[new Square("g1").getIndex()] = newSquares[new Square("e1").getIndex()];
                newSquares[new Square("g1").getIndex()].setSquare(new Square("g1"));
                newSquares[new Square("e1").getIndex()] = null;
                newSquares[new Square("h1").getIndex()] = null;
            } else {
                newSquares[new Square("f8").getIndex()] = newSquares[new Square("h8").getIndex()];
                newSquares[new Square("f8").getIndex()].setSquare(new Square("f8"));
                newSquares[new Square("g8").getIndex()] = newSquares[new Square("e8").getIndex()];
                newSquares[new Square("g8").getIndex()].setSquare(new Square("g8"));
                newSquares[new Square("e8").getIndex()] = null;
                newSquares[new Square("h8").getIndex()] = null;
            }
        }

        if (move.promotionPieceType() != null) {
            try {
                newSquares[to.getIndex()] = move.promotionPieceType().getConstructor(PieceColor.class).newInstance(colorToMove);
                newSquares[to.getIndex()].setSquare(to.copy());
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }

        result.squares = newSquares;

        if (movingPiece.getColor() == PieceColor.WHITE) {
            if (movingPiece instanceof King) {
                result.canWhiteCastleQueenSide = false;
                result.canWhiteCastleKingSide = false;
            } else if (movingPiece instanceof Rook) {
                if (from.equals(new Square("a1")))
                    result.canWhiteCastleQueenSide = false;
                else if (from.equals(new Square("h1")))
                    result.canWhiteCastleKingSide = false;
            }
        } else {
            if (movingPiece instanceof King) {
                result.canBlackCastleQueenSide = false;
                result.canBlackCastleKingSide = false;
            } else if (movingPiece instanceof Rook) {
                if (from.equals(new Square("a8")))
                    result.canBlackCastleQueenSide = false;
                else if (from.equals(new Square("h8")))
                    result.canBlackCastleQueenSide = false;
            }
        }

        if (move.isCapture()) {

            // if we capture a rook, we cant castle with it

            if (squares[move.to().getIndex()] instanceof Rook) {
                if (move.to().equals(new Square("a1")))
                    result.canWhiteCastleQueenSide = false;
                if (move.to().equals(new Square("a8")))
                    result.canBlackCastleQueenSide = false;
                if (move.to().equals(new Square("h1")))
                    result.canWhiteCastleKingSide = false;
                if (move.to().equals(new Square("h8")))
                    result.canBlackCastleKingSide = false;
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

    public boolean isMovePossibleAndLegal(Move move) {
        if (get(move.from()) == null)
            return false;

        if (get(move.from()).getAllPossibleMoves(this).stream().noneMatch(possibleMove -> possibleMove.equals(move)))
            return false;

        return isMoveLegal(move);
    }

    public Optional<Move> tryToInferMove(Square from, Square to, Class<? extends Piece> promotedPieceType) {

        if (from.getIndex() == to.getIndex())
            return Optional.empty();

        Piece movingPiece = get(from);

        Piece pieceOnDestinationSquare = get(to);

        if (movingPiece == null || movingPiece.getColor() != colorToMove)
            return Optional.empty();

        boolean isPotentiallyEnPassant = movingPiece instanceof Pawn && pieceOnDestinationSquare == null && Math.abs(from.file() - to.file()) == 1;

        boolean isPotentiallyNonEnPassantCapture = pieceOnDestinationSquare != null && pieceOnDestinationSquare.color != colorToMove;

        SpecialMove specialType = getInferredSpecialType(from, to, movingPiece);

        return Optional.of(new Move(from, to, promotedPieceType, isPotentiallyNonEnPassantCapture | isPotentiallyEnPassant, isPotentiallyEnPassant, specialType));
    }

    @NotNull
    private SpecialMove getInferredSpecialType(Square from, Square to, Piece movingPiece) {
        boolean isPotentialKingSideCastle = colorToMove == PieceColor.WHITE ? (
                from.equals(new Square("e1")) && to.equals(new Square("g1"))
        ) : (
                from.equals(new Square("e8")) && to.equals(new Square("g8"))
        );

        boolean isPotentialQueenSideCastle = colorToMove == PieceColor.WHITE ? (
                from.equals(new Square("e1")) && to.equals(new Square("c1"))
        ) : (
                from.equals(new Square("e8")) && to.equals(new Square("c8"))
        );

        boolean isPotentialDoublePawnMove = movingPiece instanceof Pawn && Math.abs(from.row() - to.row()) == 2;

        SpecialMove specialType = SpecialMove.NONE;

        if (isPotentialDoublePawnMove)
            specialType = SpecialMove.DOUBLE_PAWN_PUSH;
        if (isPotentialKingSideCastle)
            specialType = SpecialMove.KING_SIDE_CASTLE;
        if (isPotentialQueenSideCastle)
            specialType = SpecialMove.QUEEN_SIDE_CASTLE;
        return specialType;
    }

    public List<Move> getLegalMoves() {

        List<Move> legalMoves = new ArrayList<>();
        for (Piece piece : squares) {
            if (piece != null) {
                if (piece.getColor() == colorToMove) {
                    List<Move> moves = piece.getAllPossibleMoves(this);
                    for (Move move : moves) {
                        if (isMoveLegal(move)) {
                            legalMoves.add(move);
                        }
                    }
                }
            }
        }
        return legalMoves;
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
                    ParseException newException = new ParseException("Piece not recognized!", i);
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

    public String getFENString() {
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

    public String getFENPositionString() {
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


