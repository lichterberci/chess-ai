package chessai.chessai.lib;

import chessai.chessai.lib.pieces.*;
import org.jetbrains.annotations.NotNull;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;

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
                other.blackAttackSquares);
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
                 BitMap whitePieces,
                 BitMap blackPieces,
                 BitMap whiteKing,
                 BitMap blackKing,
                 BitMap whiteAttackSquares,
                 BitMap blackAttackSquares) {

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

        if (whiteAttackSquares == null || blackAttackSquares == null) {
            generateAttackMapsForBothSides();
        }

        return color == PieceColor.WHITE ? blackAttackSquares.and(whiteKing).isNonZero() : whiteAttackSquares.and(blackKing).isNonZero();
    }

    private void generateAttackMapsForBothSides() {
        whiteAttackSquares = new BitMap(0);
        blackAttackSquares = new BitMap(0);

        for (int index : whitePieces.getIndexesOfOnes()) {
            Piece whitePiece = squares[index];

            MoveResult moveResult = whitePiece.getPseudoLegalMovesAsBitMaps(this);

            whiteAttackSquares.orInPlace(moveResult.attackTargetsWhilePretendingTheEnemyKingIsNotThere());
        }

        for (int index : blackPieces.getIndexesOfOnes()) {
            Piece blackPiece = squares[index];

            MoveResult moveResult = blackPiece.getPseudoLegalMovesAsBitMaps(this);

            blackAttackSquares.orInPlace(moveResult.attackTargetsWhilePretendingTheEnemyKingIsNotThere());
        }
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

        boolean hasMoves = !generateLegalMovesUsingBitMapsAndUpdateBitMaps().isEmpty();

        if (hasMoves)
            return GameState.PLAYING;

        boolean isKingInCheck = isKingInCheck(colorToMove);

        if (!isKingInCheck)
            return GameState.DRAW;

        return colorToMove == PieceColor.WHITE ? GameState.BLACK_WIN : GameState.WHITE_WIN;
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
        return generateLegalMovesUsingBitMapsAndUpdateBitMaps(false);
    }

    public List<Move> generateLegalMovesUsingBitMapsAndUpdateBitMaps(boolean onlyGenerateEnemyAttackMaps) {

        BitMap enemyPieces;
        BitMap checkTrackForOurKing;
        BitMap enemyDoubleAttackSquares = new BitMap(0);
        BitMap pinMapForOurPieces;
        BitMap ourPieces;
        BitMap ourKing;
        BitMap enemyPiecesGivingCheck = new BitMap(0);
        BitMap uncapturableEnPassantTarget = new BitMap(0);
        BitMap enemyAttackSquares = new BitMap(0);

        if (colorToMove == PieceColor.WHITE) {
            enemyPieces = blackPieces;
            ourPieces = whitePieces;
            ourKing = whiteKing;
            checkTrackForOurKing = new BitMap(0);
            pinMapForOurPieces = new BitMap(0);
        } else {
            enemyPieces = whitePieces;
            ourPieces = blackPieces;
            ourKing = blackKing;
            checkTrackForOurKing = new BitMap(0);
            pinMapForOurPieces = new BitMap(0);
        }

        // generate moves for the other side, so we can determine whether we are in or will be in check
        for (int index : enemyPieces.getIndexesOfOnes()) {
            Piece enemyPiece = squares[index];

            MoveResult moveResult = enemyPiece.getPseudoLegalMovesAsBitMaps(this);

            checkTrackForOurKing.orInPlace(moveResult.checkTrack());
            enemyDoubleAttackSquares.orInPlace(enemyAttackSquares.and(moveResult.attackTargetsWhilePretendingTheEnemyKingIsNotThere()));
            enemyAttackSquares.orInPlace(moveResult.attackTargetsWhilePretendingTheEnemyKingIsNotThere());
            pinMapForOurPieces.orInPlace(moveResult.pinMap());
            uncapturableEnPassantTarget.orInPlace(moveResult.isEnPassantTargetUnCapturableBecausePin());

            if (moveResult.attackTargetsWhilePretendingTheEnemyKingIsNotThere().and(ourKing).isNonZero())
                enemyPiecesGivingCheck.setBitInPlace(index, true);
        }

        if (colorToMove == PieceColor.WHITE)
            blackAttackSquares = enemyAttackSquares;
        else
            whiteAttackSquares = enemyAttackSquares;

        if (onlyGenerateEnemyAttackMaps)
            return new ArrayList<>();

        // double check
        if (enemyDoubleAttackSquares.and(ourKing).isNonZero()) {
            System.out.printf("%s is in double check%n", colorToMove);
            return generateMovesForDoubleCheckSituation(ourKing, enemyAttackSquares);
        }

        List<Move> result = new LinkedList<>();

        // we are in check, so only moves are
        // - with the king
        // - blocking
        // - capturing the piece giving check
        if (enemyAttackSquares.and(ourKing).isNonZero()) {
            System.out.printf("%s is in check%n", colorToMove);
            generateMovesForSingleCheckSituation(ourPieces,
                    enemyAttackSquares,
                    result,
                    checkTrackForOurKing,
                    enemyPiecesGivingCheck,
                    uncapturableEnPassantTarget,
                    ourKing);
        } else {
            System.out.printf("%s has a general situation%n", colorToMove);
            // general situation
            generateMovesForGeneralSituation(pinMapForOurPieces,
                    ourPieces,
                    ourKing,
                    uncapturableEnPassantTarget,
                    enemyAttackSquares,
                    result);
        }

        return result;
    }

    private void generateMovesForGeneralSituation(BitMap pinMapForOurPieces,
                                                  BitMap ourPieces,
                                                  BitMap ourKing,
                                                  BitMap uncapturableEnPassantTarget,
                                                  BitMap enemyAttackSquares,
                                                  List<Move> result) {
        for (int ourPieceIndex : ourPieces.getIndexesOfOnes()) {
            Piece ourPiece = squares[ourPieceIndex];

            if (ourKing.getBit(ourPieceIndex)) {
                generateKingMovesForGeneralSituation(ourPieceIndex,
                        enemyAttackSquares,
                        result);
            } else {
                generateNonKingMovesForGeneralSituation(ourPieceIndex,
                        pinMapForOurPieces,
                        ourPiece,
                        result,
                        uncapturableEnPassantTarget);
            }
        }
    }

    private void generateNonKingMovesForGeneralSituation(int ourPieceIndex,
                                                         BitMap pinMapForOurPieces,
                                                         Piece ourPiece,
                                                         List<Move> result,
                                                         BitMap uncapturableEnPassantTarget) {
        MoveResult moveResult = squares[ourPieceIndex].getPseudoLegalMovesAsBitMaps(this);

        BitMap validMoveSquares = pinMapForOurPieces.getBit(ourPieceIndex) ?
                moveResult.moveTargets().and(pinMapForOurPieces)
                : moveResult.moveTargets();

        generateNonKingMovesForWithGivenMoveMap(ourPieceIndex,
                ourPiece,
                result,
                uncapturableEnPassantTarget,
                moveResult,
                validMoveSquares);
    }

    private void generateNonKingMovesForWithGivenMoveMap(int ourPieceIndex,
                                                         Piece ourPiece,
                                                         List<Move> result,
                                                         BitMap uncapturableEnPassantTarget,
                                                         MoveResult moveResult,
                                                         BitMap validMoveSquares) {
        for (int index : validMoveSquares.getIndexesOfOnes()) {

            if (!(ourPiece instanceof Pawn)) {
                result.add(new Move(
                        ourPieceIndex,
                        index,
                        null,
                        moveResult.isResultCapture().getBit(index),
                        false,
                        SpecialMove.NONE
                ));
                continue;
            }

            if (moveResult.isResultPromotion().getBit(index)) {
                result.add(new Move(
                        ourPieceIndex,
                        index,
                        Knight.class,
                        moveResult.isResultCapture().getBit(index),
                        false,
                        SpecialMove.NONE
                ));
                result.add(new Move(
                        ourPieceIndex,
                        index,
                        Bishop.class,
                        moveResult.isResultCapture().getBit(index),
                        false,
                        SpecialMove.NONE
                ));
                result.add(new Move(
                        ourPieceIndex,
                        index,
                        Rook.class,
                        moveResult.isResultCapture().getBit(index),
                        false,
                        SpecialMove.NONE
                ));
                result.add(new Move(
                        ourPieceIndex,
                        index,
                        Queen.class,
                        moveResult.isResultCapture().getBit(index),
                        false,
                        SpecialMove.NONE
                ));
                continue;
            }

            if (
                    moveResult.isResultEnPassant().getBit(index)
                            && !uncapturableEnPassantTarget.getBit(index)
            ) {
                result.add(new Move(
                        ourPieceIndex,
                        index,
                        null,
                        true,
                        true,
                        SpecialMove.NONE
                ));
                continue;
            }

            result.add(new Move(
                    ourPieceIndex,
                    index,
                    null,
                    moveResult.isResultCapture().getBit(index),
                    false,
                    moveResult.isResultDoublePawnMove().getBit(index) ? SpecialMove.DOUBLE_PAWN_PUSH : SpecialMove.NONE
            ));

        }
    }

    private void generateKingMovesForGeneralSituation(int ourPieceIndex,
                                                      BitMap enemyAttackSquares,
                                                      List<Move> result) {
        MoveResult moveResult;
        moveResult = squares[ourPieceIndex].getPseudoLegalMovesAsBitMaps(this);

        // we cannot move into another check, or castle from a check
        BitMap normalMoves = moveResult.moveTargets()
                .and(enemyAttackSquares.invert())
                .and(moveResult.isResultKingSideCastle().invert())
                .and(moveResult.isResultQueenSideCastle().invert());

        // normal moves
        result.addAll(normalMoves
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

        // castling
        BitMap kingSideCastlingMoves = moveResult.moveTargets().and(moveResult.isResultKingSideCastle());
        BitMap queenSideCastlingMoves = moveResult.moveTargets().and(moveResult.isResultQueenSideCastle());

        // white king side
        if (colorToMove == PieceColor.WHITE
                && !enemyAttackSquares.getBit(Square.getIndex("F1"))
                && !enemyAttackSquares.getBit(Square.getIndex("G1"))
                && kingSideCastlingMoves.isNonZero()
        ) {
            result.add(new Move(
                    ourPieceIndex,
                    Square.getIndex("G1"),
                    null,
                    false,
                    false,
                    SpecialMove.KING_SIDE_CASTLE
            ));
        }

        // black king side
        if (colorToMove == PieceColor.BLACK
                && !enemyAttackSquares.getBit(Square.getIndex("F8"))
                && !enemyAttackSquares.getBit(Square.getIndex("G8"))
                && kingSideCastlingMoves.isNonZero()
        ) {
            result.add(new Move(
                    ourPieceIndex,
                    Square.getIndex("G8"),
                    null,
                    false,
                    false,
                    SpecialMove.KING_SIDE_CASTLE
            ));
        }


        // white queen side
        if (colorToMove == PieceColor.WHITE
                && !enemyAttackSquares.getBit(Square.getIndex("C1"))
                && !enemyAttackSquares.getBit(Square.getIndex("D1"))
                && queenSideCastlingMoves.isNonZero()
        ) {
            result.add(new Move(
                    ourPieceIndex,
                    Square.getIndex("C1"),
                    null,
                    false,
                    false,
                    SpecialMove.QUEEN_SIDE_CASTLE
            ));
        }

        // black queen side
        if (colorToMove == PieceColor.BLACK
                && !enemyAttackSquares.getBit(Square.getIndex("C8"))
                && !enemyAttackSquares.getBit(Square.getIndex("D8"))
                && queenSideCastlingMoves.isNonZero()
        ) {
            result.add(new Move(
                    ourPieceIndex,
                    Square.getIndex("C8"),
                    null,
                    false,
                    false,
                    SpecialMove.QUEEN_SIDE_CASTLE
            ));
        }
    }

    private void generateMovesForSingleCheckSituation(BitMap ourPieces,
                                                      BitMap enemyAttackSquares,
                                                      List<Move> result,
                                                      BitMap checkTrackForOurKing,
                                                      BitMap enemyPiecesGivingCheck,
                                                      BitMap uncapturableEnPassantTarget, BitMap ourKing) {
        for (int ourPieceIndex : ourPieces.getIndexesOfOnes()) {
            Piece ourPiece = squares[ourPieceIndex];

            if (ourKing.getBit(ourPieceIndex)) {
                // we can capture, or run away
                generateKingMovesForSingleCheckSituation(enemyAttackSquares,
                        result,
                        ourPieceIndex);
            } else {
                // we can block or capture
                generateBlockingOrCapturingMovesForSingleCheckSituation(result,
                        checkTrackForOurKing,
                        enemyPiecesGivingCheck,
                        ourPieceIndex,
                        ourPiece,
                        uncapturableEnPassantTarget);
            }

        }
    }

    private void generateKingMovesForSingleCheckSituation(BitMap enemyAttackSquares, List<Move> result, int ourPieceIndex) {
        MoveResult moveResult = squares[ourPieceIndex].getPseudoLegalMovesAsBitMaps(this);

        // we cannot move into another check, or castle from a check
        BitMap validMoves = moveResult.moveTargets()
                .and(enemyAttackSquares.invert())
                .and(moveResult.isResultKingSideCastle().invert())
                .and(moveResult.isResultQueenSideCastle().invert());

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

    private void generateBlockingOrCapturingMovesForSingleCheckSituation(List<Move> result,
                                                                         BitMap checkTrackForOurKing,
                                                                         BitMap enemyPiecesGivingCheck,
                                                                         int ourPieceIndex,
                                                                         Piece ourPiece,
                                                                         BitMap uncapturableEnPassantTarget) {
        MoveResult moveResult = squares[ourPieceIndex].getPseudoLegalMovesAsBitMaps(this);

        BitMap validMoves = moveResult.moveTargets().and(checkTrackForOurKing.or(enemyPiecesGivingCheck));

        generateNonKingMovesForWithGivenMoveMap(ourPieceIndex, ourPiece, result, uncapturableEnPassantTarget, moveResult, validMoves);
    }

    @NotNull
    private List<Move> generateMovesForDoubleCheckSituation(BitMap ourKing, BitMap enemyAttackSquares) {
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

    public boolean isMoveLegal(Move move) {
        return generateLegalMovesUsingBitMapsAndUpdateBitMaps()
                .stream()
                .filter(otherMove -> otherMove.equals(move))
                .count() == 1;
    }

    public Board makeMove(Move move) {

        Square from = move.from();
        Square to = move.to();

        Piece movingPiece = squares[from.getIndex()];

        if (movingPiece == null)
            return new Board(this);

        Board result = new Board(this);
        result.enPassantTarget = null;

        result.squares = getSquaresAfterMove(move, result, to);

        updateBitMapsForMoves(move, result);

        updateCastlingRightsForMove(move, movingPiece, result, from);

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

    private void updateCastlingRightsForMove(Move move, Piece movingPiece, Board result, Square from) {
        if (movingPiece.getColor() == PieceColor.WHITE) {
            if (movingPiece instanceof King) {
                result.canWhiteCastleQueenSide = false;
                result.canWhiteCastleKingSide = false;

                // update king bitmap

                result.whiteKing = whiteKing.setBit(move.fromIndex(), false).setBit(move.toIndex(), true);

            } else if (movingPiece instanceof Rook) {
                if (from.getIndex() == Square.getIndex("a1"))
                    result.canWhiteCastleQueenSide = false;
                else if (from.getIndex() == Square.getIndex("h1"))
                    result.canWhiteCastleKingSide = false;
            }
        } else {
            if (movingPiece instanceof King) {
                result.canBlackCastleQueenSide = false;
                result.canBlackCastleKingSide = false;

                // update king position in king bitmap
                result.blackKing = blackKing.setBit(move.fromIndex(), false).setBit(move.toIndex(), true);

            } else if (movingPiece instanceof Rook) {
                if (from.getIndex() == Square.getIndex("a8"))
                    result.canBlackCastleQueenSide = false;
                else if (from.getIndex() == Square.getIndex("h8"))
                    result.canBlackCastleKingSide = false;
            }
        }

        if (move.isCapture() && (squares[move.to().getIndex()] instanceof Rook)) {
            if (move.to().getIndex() == Square.getIndex("a1"))
                    result.canWhiteCastleQueenSide = false;
            if (move.to().getIndex() == Square.getIndex("a8"))
                    result.canBlackCastleQueenSide = false;
            if (move.to().getIndex() == Square.getIndex("h1"))
                    result.canWhiteCastleKingSide = false;
            if (move.to().getIndex() == Square.getIndex("h8"))
                    result.canBlackCastleKingSide = false;

        }
    }

    private void updateBitMapsForMoves(Move move, Board result) {

        if (colorToMove == PieceColor.WHITE) {

            result.whitePieces.setBitInPlace(move.fromIndex(), false);
            result.whitePieces.setBitInPlace(move.toIndex(), true);

            if (move.isEnPassant())
                result.blackPieces.setBitInPlace(move.toIndex() - 8, false);
            else if (move.isCapture())
                result.blackPieces.setBitInPlace(move.toIndex(), false);

            if (move.specialMove() == SpecialMove.KING_SIDE_CASTLE) {
                result.whitePieces.setBitInPlace(Square.getIndex("H1"), false);
                result.whitePieces.setBitInPlace(Square.getIndex("F1"), true);
            } else if (move.specialMove() == SpecialMove.QUEEN_SIDE_CASTLE) {
                result.whitePieces.setBitInPlace(Square.getIndex("A1"), false);
                result.whitePieces.setBitInPlace(Square.getIndex("D1"), true);
            }

        } else {

            result.blackPieces.setBitInPlace(move.fromIndex(), false);
            result.blackPieces.setBitInPlace(move.toIndex(), true);

            if (move.isEnPassant())
                result.whitePieces.setBitInPlace(move.toIndex(), false);
            else if (move.isCapture())
                result.whiteAttackSquares.setBitInPlace(move.toIndex() + 8, false);

            if (move.specialMove() == SpecialMove.KING_SIDE_CASTLE) {
                result.blackPieces.setBitInPlace(Square.getIndex("H8"), false);
                result.blackPieces.setBitInPlace(Square.getIndex("F8"), true);
            } else if (move.specialMove() == SpecialMove.QUEEN_SIDE_CASTLE) {
                result.blackPieces.setBitInPlace(Square.getIndex("A8"), false);
                result.blackPieces.setBitInPlace(Square.getIndex("D8"), true);
            }
        }

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
                newSquares[Square.getIndex("d1")] = newSquares[Square.getIndex("a1")];
                newSquares[Square.getIndex("d1")].setSquare(new Square("d1"));
                newSquares[Square.getIndex("c1")] = newSquares[Square.getIndex("e1")];
                newSquares[Square.getIndex("e1")].setSquare(new Square("e1"));
                newSquares[Square.getIndex("e1")] = null;
                newSquares[Square.getIndex("a1")] = null;
            } else {
                newSquares[Square.getIndex("d8")] = newSquares[Square.getIndex("a8")];
                newSquares[Square.getIndex("d8")].setSquare(new Square("d8"));
                newSquares[Square.getIndex("c8")] = newSquares[Square.getIndex("e8")];
                newSquares[Square.getIndex("c8")].setSquare(new Square("c8"));
                newSquares[Square.getIndex("e8")] = null;
                newSquares[Square.getIndex("a8")] = null;
            }
        } else if (move.specialMove() == SpecialMove.KING_SIDE_CASTLE) {
            if (colorToMove == PieceColor.WHITE) {
                newSquares[Square.getIndex("f1")] = newSquares[Square.getIndex("h1")];
                newSquares[Square.getIndex("f1")].setSquare(new Square("f1"));
                newSquares[Square.getIndex("g1")] = newSquares[Square.getIndex("e1")];
                newSquares[Square.getIndex("g1")].setSquare(new Square("g1"));
                newSquares[Square.getIndex("e1")] = null;
                newSquares[Square.getIndex("h1")] = null;
            } else {
                newSquares[Square.getIndex("f8")] = newSquares[Square.getIndex("h8")];
                newSquares[Square.getIndex("f8")].setSquare(new Square("f8"));
                newSquares[Square.getIndex("g8")] = newSquares[Square.getIndex("e8")];
                newSquares[Square.getIndex("g8")].setSquare(new Square("g8"));
                newSquares[Square.getIndex("e8")] = null;
                newSquares[Square.getIndex("h8")] = null;
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

    @Deprecated(forRemoval = true)
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
                default -> System.out.printf("WARNING: '%s' is an unexpected castling character!%n", c);
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


