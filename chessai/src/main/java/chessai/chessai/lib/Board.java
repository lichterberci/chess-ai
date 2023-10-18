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
    public BitMap whitePieces;
    public BitMap blackPieces;
    public BitMap whiteKing;
    public BitMap blackKing;
    public BitMap whiteAttackSquares;
    public BitMap blackAttackSquares;
    public BitMap whiteDoubleAttackSquares;
    public BitMap blackDoubleAttackSquares;
    public BitMap pinMapForTheWhitePieces;
    public BitMap pinMapForTheBlackPieces;
    public BitMap checkTrackForWhiteKing;
    public BitMap checkTrackForBlackKing;

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
                other.previousPositions,
                other.whitePieces,
                other.blackPieces,
                other.whiteKing,
                other.blackKing,
                other.whiteAttackSquares,
                other.blackAttackSquares,
                other.whiteDoubleAttackSquares,
                other.blackDoubleAttackSquares,
                other.pinMapForTheWhitePieces,
                other.pinMapForTheBlackPieces,
                other.checkTrackForWhiteKing,
                other.checkTrackForBlackKing);
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
                 List<String> previousPositions,
                 BitMap whitePieces, BitMap blackPieces, BitMap whiteKing, BitMap blackKing, BitMap whiteAttackSquares, BitMap blackAttackSquares, BitMap whiteDoubleAttackSquares, BitMap blackDoubleAttackSquares, BitMap pinMapForTheWhitePieces, BitMap pinMapForTheBlackPieces, BitMap checkTrackForWhiteKing, BitMap checkTrackForBlackKing) {

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
        this.whitePieces = whitePieces;
        this.blackPieces = blackPieces;
        this.whiteKing = whiteKing;
        this.blackKing = blackKing;
        this.whiteAttackSquares = whiteAttackSquares;
        this.blackAttackSquares = blackAttackSquares;
        this.whiteDoubleAttackSquares = whiteDoubleAttackSquares;
        this.blackDoubleAttackSquares = blackDoubleAttackSquares;
        this.pinMapForTheWhitePieces = pinMapForTheWhitePieces;
        this.pinMapForTheBlackPieces = pinMapForTheBlackPieces;
        this.checkTrackForWhiteKing = checkTrackForWhiteKing;
        this.checkTrackForBlackKing = checkTrackForBlackKing;
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
        if (whiteAttackSquares != null && blackAttackSquares != null) {
            if (color == PieceColor.WHITE) {
                return blackAttackSquares.and(whiteKing).isNonZero();
            } else {
                return whiteAttackSquares.and(blackKing).isNonZero();
            }
        }

        int kingIndex = color == PieceColor.WHITE ? whiteKing.getIndexesOfOnes().get(0) : blackKing.getIndexesOfOnes().get(0);

        return isKingInCheck(color, kingIndex);
    }

    public boolean isKingInCheck(PieceColor color, int kingSquareIndex) {

        if (whiteAttackSquares != null && blackAttackSquares != null) {
            if (color == PieceColor.WHITE) {
                return blackAttackSquares.getBit(kingSquareIndex);
            } else {
                return whiteAttackSquares.getBit(kingSquareIndex);
            }
        }

        for (Piece piece : squares) {

            if (piece == null)
                continue;

            if (piece.getColor() == color)
                continue;

            if (piece
                    .getPseudoLegalMoves(this)
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
        LinkedList<Piece> piecesWithOppositeColor = new LinkedList<>();

        for (Piece square : squares) {
            if (square != null) {
                if (square.getColor() == colorToMove) {
                    piecesWithRightColor.add(square);
                } else {
                    piecesWithOppositeColor.add(square);
                }
            }
        }

        int numRightColorPieces = piecesWithRightColor.size();
        int numOppositeColorPieces = piecesWithOppositeColor.size();

        if (numRightColorPieces == 1 && numOppositeColorPieces == 1)
            return GameState.DRAW;

        boolean canRightWin = canRightColorWin(numRightColorPieces, piecesWithRightColor);
        boolean canOppositeWin = canOppositeColorWin(numOppositeColorPieces, piecesWithOppositeColor);

        if (!canRightWin && !canOppositeWin)
            return GameState.DRAW;

        List<Move> moves = new LinkedList<>();

        for (Piece piece : piecesWithRightColor) {

            var possibleMoves = piece.getPseudoLegalMoves(this);

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

    private static boolean canRightColorWin(int numRightColorPieces, LinkedList<Piece> piecesWithRightColor) {
        boolean canRightWin = true;
        if (numRightColorPieces == 2) {
            for (Piece rightPiece : piecesWithRightColor)
                if (rightPiece instanceof Bishop || rightPiece instanceof Knight) {
                    canRightWin = false;
                    break;
                }
        }
        return canRightWin;
    }

    private static boolean canOppositeColorWin(int numOppositeColorPieces, LinkedList<Piece> piecesWithOppositeColor) {
        boolean canOppositeWin = true;
        if (numOppositeColorPieces == 2) {
            for (Piece oppositePiece : piecesWithOppositeColor)
                if (oppositePiece instanceof Bishop || oppositePiece instanceof Knight) {
                    canOppositeWin = false;
                    break;
                }
        }
        return canOppositeWin;
    }

    public List<Move> generateLegalMovesUsingBitMapsAndUpdateBitMaps() {

        BitMap enemyPieces;
        BitMap checkTrackForOurKing;
        BitMap enemyAttackSquares;
        BitMap enemyDoubleAttackSquares;
        BitMap pinMapForOurPieces;
        BitMap ourPieces;
        BitMap ourKing;
        BitMap enemyPiecesGivingCheck = new BitMap(0);

        if (colorToMove == PieceColor.WHITE) {
            enemyPieces = blackPieces;
            ourPieces = whitePieces;
            ourKing = whiteKing;
            checkTrackForOurKing = checkTrackForWhiteKing = new BitMap(0);
            enemyAttackSquares = blackAttackSquares = new BitMap(0);
            enemyDoubleAttackSquares = blackDoubleAttackSquares = new BitMap(0);
            pinMapForOurPieces = pinMapForTheWhitePieces = new BitMap(0);
        } else {
            enemyPieces = whitePieces;
            ourPieces = blackPieces;
            ourKing = blackKing;
            checkTrackForOurKing = checkTrackForBlackKing = new BitMap(0);
            enemyAttackSquares = whiteAttackSquares = new BitMap(0);
            enemyDoubleAttackSquares = whiteDoubleAttackSquares = new BitMap(0);
            pinMapForOurPieces = pinMapForTheBlackPieces = new BitMap(0);
        }

        // generate moves for the other side, so we can determine whether we are in or will be in check
        for (int index : enemyPieces.getIndexesOfOnes()) {
            Piece enemyPiece = squares[index];

            MoveResult moveResult = enemyPiece.getPseudoLegalMovesAsBitMaps(this);

            checkTrackForOurKing.orInPlace(moveResult.checkTrack());
            enemyDoubleAttackSquares.orInPlace(enemyAttackSquares.or(moveResult.attackTargetsWhilePretendingTheEnemyKingIsNotThere()));
            enemyAttackSquares.orInPlace(moveResult.attackTargetsWhilePretendingTheEnemyKingIsNotThere());
            pinMapForOurPieces.orInPlace(moveResult.pinMap());

            if (moveResult.attackTargetsWhilePretendingTheEnemyKingIsNotThere().and(ourKing).isNonZero())
                enemyPiecesGivingCheck.setBitInPlace(index, true);
        }

        // double check
        if (enemyDoubleAttackSquares.and(ourKing).isNonZero()) {

            final int ourKingIndex = ourKing.getIndexesOfOnes().get(0);

            // only the king can move
            MoveResult moveResult = squares[ourKingIndex].getPseudoLegalMovesAsBitMaps(this);

            BitMap validMoves = moveResult.moveTargets().and(enemyAttackSquares.invert());

            return validMoves
                    .getIndexesOfOnes()
                    .stream()
                    .map(index -> new Move(
                            ourKingIndex,
                            index,
                            null,
                            moveResult.isResultCapture().getBit(index),
                            false,
                            SpecialMove.NONE
                    ))
                    .toList();
        }

        List<Move> result = new LinkedList<>();

        // we are in check, so only moves are
        // - with the king
        // - blocking
        // - capturing the piece giving check
        if (enemyAttackSquares.and(ourKing).isNonZero()) {

            for (int ourPieceIndex : ourPieces.getIndexesOfOnes()) {
                Piece ourPiece = squares[ourPieceIndex];

                if (ourPiece instanceof King) {

                    // we can capture, or run away

                    MoveResult moveResult = squares[ourPieceIndex].getPseudoLegalMovesAsBitMaps(this);

                    BitMap validMoves = moveResult.moveTargets().and(enemyAttackSquares.invert());

                    result.addAll(validMoves
                            .getIndexesOfOnes()
                            .stream()
                            .map(index -> new Move(
                                    ourPieceIndex,
                                    index,
                                    null,
                                    moveResult.isResultCapture().getBit(index),
                                    false,
                                    SpecialMove.NONE
                            ))
                            .toList());

                } else {

                    // we can block or capture

                    MoveResult moveResult = squares[ourPieceIndex].getPseudoLegalMovesAsBitMaps(this);

                    BitMap validMoves = moveResult.moveTargets().and(checkTrackForOurKing);

                    result.addAll(validMoves
                            .getIndexesOfOnes()
                            .stream()
                            .map(index -> new Move(
                                    ourPieceIndex,
                                    index,
                                    null,
                                    moveResult.isResultCapture().getBit(index),
                                    false,
                                    SpecialMove.NONE
                            ))
                            .toList());
                }

            }

        }


        return result;
    }
    public boolean isMoveLegal(Move _move) {
        Board boardAfterMove = makeMove(_move);

        Piece movingPiece = squares[_move.from().getIndex()];

        if (!(movingPiece instanceof King)) {
            return !boardAfterMove.isKingInCheck(colorToMove);
        }

        King blackKingOnBoard = (King) Arrays.stream(boardAfterMove.squares)
                .filter(Objects::nonNull)
                .filter(piece -> piece.getColor() == PieceColor.BLACK)
                .filter(King.class::isInstance)
                .findFirst().orElse(null);

        King whiteKingOnBoard = (King) Arrays.stream(boardAfterMove.squares)
                .filter(Objects::nonNull)
                .filter(piece -> piece.getColor() == PieceColor.WHITE)
                .filter(King.class::isInstance)
                .findFirst().orElse(null);

        if (whiteKingOnBoard == null || blackKingOnBoard == null)
            return false;

        int kingFileDistance = Math.abs(whiteKingOnBoard.getSquare().file() - blackKingOnBoard.getSquare().file());
        int kingRowDistance = Math.abs(whiteKingOnBoard.getSquare().row() - blackKingOnBoard.getSquare().row());

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

    public Board makeMove(Move move) {

        Square from = move.from();
        Square to = move.to();

        Piece movingPiece = get(from);

        if (movingPiece == null)
            return new Board(this);

        Board result = new Board(this);
        result.enPassantTarget = null;

        result.squares = getSquaresAfterMove(move, result, to);

        if (movingPiece.getColor() == PieceColor.WHITE) {
            if (movingPiece instanceof King) {
                result.canWhiteCastleQueenSide = false;
                result.canWhiteCastleKingSide = false;

                // update king bitmap

                result.whiteKing = whiteKing.setBit(move.fromIndex(), false).setBit(move.toIndex(), true);

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

                // update king position in king bitmap
                result.blackKing = blackKing.setBit(move.fromIndex(), false).setBit(move.toIndex(), true);

            } else if (movingPiece instanceof Rook) {
                if (from.equals(new Square("a8")))
                    result.canBlackCastleQueenSide = false;
                else if (from.equals(new Square("h8")))
                    result.canBlackCastleKingSide = false;
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

    private Piece[] getSquaresAfterMove(Move move, Board result, Square to) {
        Piece[] newSquares = result.squares;

        if (move.specialMove() == SpecialMove.NONE || move.specialMove() == SpecialMove.DOUBLE_PAWN_PUSH) {

            if (move.isEnPassant()) {
                newSquares[enPassantTarget.getIndex()] = newSquares[move.fromIndex()];
                newSquares[enPassantTarget.getIndex()].setSquare(enPassantTarget);

                if (colorToMove == PieceColor.WHITE) {
                    newSquares[new Square(enPassantTarget.file(), enPassantTarget.row() - 1).getIndex()] = null;
                } else {
                    newSquares[new Square(enPassantTarget.file(), enPassantTarget.row() + 1).getIndex()] = null;
                }
            } else {
                newSquares[move.toIndex()] = newSquares[move.fromIndex()];
                newSquares[move.toIndex()].setSquare(to.copy());
            }

            newSquares[move.fromIndex()] = null;
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
                newSquares[move.toIndex()] = move.promotionPieceType().getConstructor(PieceColor.class).newInstance(colorToMove);
                newSquares[move.toIndex()].setSquare(to.copy());
            } catch (Exception e) {
                throw new IllegalStateException(e);
            }
        }
        return newSquares;
    }

    public boolean isMovePossibleAndLegal(Move move) {
        if (get(move.from()) == null)
            return false;

        if (get(move.from()).getPseudoLegalMoves(this).stream().noneMatch(possibleMove -> possibleMove.equals(move)))
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

        return Optional.of(new Move(from, to, promotedPieceType, isPotentiallyNonEnPassantCapture || isPotentiallyEnPassant, isPotentiallyEnPassant, specialType));
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

        List<Move> pseudoLegalMoves = new LinkedList<>();

        for (Piece piece : squares) {
            if (piece != null && piece.getColor() == colorToMove) {
                pseudoLegalMoves.addAll(piece.getPseudoLegalMoves(this));
            }
        }

        List<Move> legalMoves = new LinkedList<>();

        for (Move move : pseudoLegalMoves) {
            if (isMoveLegal(move)) {
                legalMoves.add(move);
            }
        }

        return legalMoves;
    }

    public void setFromFENString(@NotNull String fenString) throws ParseException {

        squares = new Piece[64];
        whitePieces = new BitMap(0);
        blackPieces = new BitMap(0);
        whiteKing = new BitMap(0);
        blackKing = new BitMap(0);

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

                    if (Character.isUpperCase(c)) {
                        // white
                        whitePieces = whitePieces.setBit(numSquaresDone - 1, true);

                        if (c == 'K')
                            whiteKing = whiteKing.setBit(numSquaresDone - 1, true);
                    } else {
                        blackPieces = blackPieces.setBit(numSquaresDone - 1, true);

                        if (c == 'k')
                            blackKing = blackKing.setBit(numSquaresDone - 1, true);
                    }
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


