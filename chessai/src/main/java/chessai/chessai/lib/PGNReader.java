package chessai.chessai.lib;

import chessai.chessai.lib.pieces.Bishop;
import chessai.chessai.lib.pieces.Knight;
import chessai.chessai.lib.pieces.Queen;
import chessai.chessai.lib.pieces.Rook;

import java.text.ParseException;
import java.util.LinkedList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * This class parses PGN string and reads the moves and boards (positions).
 */
public class PGNReader {

    private static final String DEFAULT_POSITION_FEN = "rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR w KQkq - 0 1";
    private final List<Board> boards;
    private final List<Move> moves;
    private GameState endResult;

    public PGNReader() {
        boards = new LinkedList<>();
        moves = new LinkedList<>();
        endResult = GameState.PLAYING;
    }

    public PGNReader(String input) throws ParseException {
        this();
        this.parseString(input);
    }

    public List<Move> getMoves() {
        return moves;
    }

    public List<Board> getBoards() {
        return boards;
    }

    public GameState getEndResult() {
        return endResult;
    }

    /**
     * Parses the string as a PGN
     *
     * @param input the input PGN string
     * @throws ParseException if the input is malformed
     */
    public void parseString(String input) throws ParseException {

        Pattern fieldPattern = Pattern.compile("(?<Field>\\[.+\".+\"])");
        Pattern fieldNameValuePattern = Pattern.compile("\\[(?<FieldName>.+)\"(?<FieldValue>.+)\"]");

        Matcher fieldsMatcher = fieldPattern.matcher(input);

        String startingFen = DEFAULT_POSITION_FEN;

        if (!fieldsMatcher.find())
            throw new ParseException(input, 0);

        for (int i = 0; i < fieldsMatcher.groupCount(); i++) {
            String fieldString = fieldsMatcher.group(i + 1);

            Matcher fieldNameValueMatcher = fieldNameValuePattern.matcher(fieldString);

            if (!fieldNameValueMatcher.find())
                throw new ParseException(input, input.indexOf(fieldString));

            String fieldName = fieldNameValueMatcher.group("FieldName").trim();
            String fieldValue = fieldNameValueMatcher.group("FieldValue").trim();

            System.out.println("field: " + fieldName + " - " + fieldValue);

            switch (fieldName) {
                case "FEN" -> startingFen = fieldValue;
                case "Result" -> endResult = switch (fieldValue) {
                    case "1-0" -> GameState.WHITE_WIN;
                    case "0-1" -> GameState.BLACK_WIN;
                    case "1/2-1/2" -> GameState.DRAW;
                    case "½-½" -> GameState.DRAW;
                    default -> GameState.PLAYING;
                };
                default -> System.out.printf("Not handling field: %s%n", fieldName);
            }
        }

        Board board = new Board(startingFen);

        String nonFieldInput = input.substring(input.lastIndexOf(']') + 1);

        Pattern movesInRoundPattern = Pattern.compile("(?<move1>[\\d\\w=\\-+#]+)\\s+(?<move2>[\\d\\w=\\-+#]+)?");

        boards.add(new Board(board));

        for (String round : nonFieldInput.split("\\s*\\d+\\.\\s*")) {

            if (round.trim().isEmpty())
                continue;

            Matcher roundInfoMatcher = movesInRoundPattern.matcher(round);

            if (!roundInfoMatcher.find())
                throw new ParseException(round, 0);

            if (Pattern.compile("^\\s*[01/2½]+-[01/2½]+\\s*$").matcher(roundInfoMatcher.group("move1")).matches())
                continue;

            Move whiteMove = parseMove(board, roundInfoMatcher.group("move1").trim());

            moves.add(whiteMove);

            board = board.makeMove(whiteMove);

            boards.add(new Board(board));

            if (roundInfoMatcher.group("move2") != null) {

                if (Pattern.compile("^\\s*[01/2½]+-[01/2½]+\\s*$").matcher(roundInfoMatcher.group("move2")).matches())
                    continue;

                Move blackMove = parseMove(board, roundInfoMatcher.group("move2").trim());

                moves.add(blackMove);

                board = board.makeMove(blackMove);

                boards.add(new Board(board));
            }
        }
    }

    private Move parseMove(Board board, String completeMoveString) throws ParseException {

        // we don't care about the check and checkmate signs right now
        final String moveString = completeMoveString.replace("#", "").replace("+", "").trim();

        if (moveString.contains("=")) {
            // it is a promotion

            String promotionString = moveString.split("=")[1];

            Class<? extends Piece> promotionPieceType = switch (promotionString) {
                case "Q" -> Queen.class;
                case "R" -> Rook.class;
                case "B" -> Bishop.class;
                case "K" -> Knight.class;
                default -> throw new ParseException(moveString, moveString.indexOf(promotionString));
            };

            String[] captureParts = moveString.split("=")[0].split("x");

            if (captureParts.length == 1) {
                // there is no capture

                Square toSquare = new Square(captureParts[0]);
                Square fromSquare = new Square(toSquare.file(), toSquare.row() + (board.colorToMove == PieceColor.WHITE ? -1 : 1));

                return new Move(fromSquare, toSquare, promotionPieceType, false, false, SpecialMove.NONE);
            } else {

                int fromFile = captureParts[0].charAt(0) - 'a';
                int fromRow = board.colorToMove == PieceColor.WHITE ? 6 : 1;

                Square fromSquare = new Square(fromFile, fromRow);
                Square toSquare = new Square(captureParts[1]);

                return new Move(fromSquare, toSquare, promotionPieceType, true, false, SpecialMove.NONE);
            }
        }

        if (Pattern.compile("^[a-h][2-7]$").matcher(moveString).matches()) {
            // simple pawn move

            Square toSquare = new Square(moveString);

            boolean isDoublePush = board.get(new Square(toSquare.file(), toSquare.row() + (board.colorToMove == PieceColor.WHITE ? -1 : 1))) == null;

            int rowAbsoluteDifference = isDoublePush ? 2 : 1;

            int signedRowDiff = board.colorToMove == PieceColor.WHITE ? -rowAbsoluteDifference : rowAbsoluteDifference;

            Square fromSquare = new Square(toSquare.file(), toSquare.row() + signedRowDiff);

            return new Move(fromSquare,
                    toSquare,
                    null,
                    false,
                    false,
                    isDoublePush ? SpecialMove.DOUBLE_PAWN_PUSH : SpecialMove.NONE);
        }

        if (Pattern.compile("^[a-h]x[a-h][2-7]$").matcher(moveString).matches()) {
            // pawn capture

            String[] captureParts = moveString.split("x");

            Square toSquare = new Square(captureParts[1]);

            int fromFile = captureParts[0].charAt(0) - 'a';
            int fromRow = toSquare.row() + (board.colorToMove == PieceColor.WHITE ? -1 : 1);

            Square fromSquare = new Square(fromFile, fromRow);

            boolean isEnPassant = toSquare.equals(board.enPassantTarget);

            return new Move(
                    fromSquare,
                    toSquare,
                    null,
                    true,
                    isEnPassant,
                    SpecialMove.NONE
            );
        }

        if (Pattern.compile("^[KNBQRknbqr][a-h][1-8]$").matcher(moveString).matches()) {
            // simple move

            Square toSquare = new Square(moveString.substring(1, 3));

            return board.getLegalMoves().stream()
                    .filter(m -> m.toIndex() == toSquare.getIndex())
                    .filter(m -> board.get(m.fromIndex()) != null && Character.toUpperCase(board.get(m.fromIndex()).getFENChar()) == Character.toUpperCase(moveString.charAt(0)))
                    .findFirst()
                    .orElseThrow(() -> new ParseException(moveString, 0));
        }

        if (Pattern.compile("^[KNBQRknbqr][a-h][a-h][1-8]$").matcher(moveString).matches()) {
            // simple move

            Square toSquare = new Square(moveString.substring(2, 4));

            int fromFile = moveString.charAt(1) - 'a';

            return board.getLegalMoves().stream()
                    .filter(m -> m.toIndex() == toSquare.getIndex())
                    .filter(m -> m.from().file() == fromFile)
                    .filter(m -> board.get(m.fromIndex()) != null && Character.toUpperCase(board.get(m.fromIndex()).getFENChar()) == Character.toUpperCase(moveString.charAt(0)))
                    .findFirst()
                    .orElseThrow(() -> new ParseException(moveString, 0));
        }

        if (Pattern.compile("^[KNBQRknbqr][1-8][a-h][1-8]$").matcher(moveString).matches()) {
            // simple move

            Square toSquare = new Square(moveString.substring(2, 4));

            int fromRow = moveString.charAt(1) - '1';

            return board.getLegalMoves().stream()
                    .filter(m -> m.toIndex() == toSquare.getIndex())
                    .filter(m -> m.from().row() == fromRow)
                    .filter(m -> board.get(m.fromIndex()) != null && Character.toUpperCase(board.get(m.fromIndex()).getFENChar()) == Character.toUpperCase(moveString.charAt(0)))
                    .findFirst()
                    .orElseThrow(() -> new ParseException(moveString, 0));
        }

        if (Pattern.compile("^[KNBQRknbqr][a-h][1-8][a-h][1-8]$").matcher(moveString).matches()) {
            // simple move

            Square toSquare = new Square(moveString.substring(3, 5));

            int fromFile = moveString.charAt(1) - 'a';
            int fromRow = moveString.charAt(2) - '1';

            return board.getLegalMoves().stream()
                    .filter(m -> m.toIndex() == toSquare.getIndex())
                    .filter(m -> m.from().file() == fromFile)
                    .filter(m -> m.from().row() == fromRow)
                    .filter(m -> board.get(m.fromIndex()) != null && Character.toUpperCase(board.get(m.fromIndex()).getFENChar()) == Character.toUpperCase(moveString.charAt(0)))
                    .findFirst()
                    .orElseThrow(() -> new ParseException(moveString, 0));
        }

        // from now on, only captures and castling remains

        if (Pattern.compile("^[KNBQRknbqr]x[a-h][1-8]$").matcher(moveString).matches()) {
            // simple move

            Square toSquare = new Square(moveString.substring(2, 4));

            return board.getLegalMoves().stream()
                    .filter(m -> m.toIndex() == toSquare.getIndex())
                    .filter(m -> board.get(m.fromIndex()) != null && Character.toUpperCase(board.get(m.fromIndex()).getFENChar()) == Character.toUpperCase(moveString.charAt(0)))
                    .findFirst()
                    .orElseThrow(() -> new ParseException(moveString, 0));
        }

        if (Pattern.compile("^[KNBQRknbqr][a-h]x[a-h][1-8]$").matcher(moveString).matches()) {
            // simple move

            Square toSquare = new Square(moveString.substring(3, 5));

            int fromFile = moveString.charAt(1) - 'a';

            return board.getLegalMoves().stream()
                    .filter(m -> m.toIndex() == toSquare.getIndex())
                    .filter(m -> m.from().file() == fromFile)
                    .filter(m -> board.get(m.fromIndex()) != null && Character.toUpperCase(board.get(m.fromIndex()).getFENChar()) == Character.toUpperCase(moveString.charAt(0)))
                    .findFirst()
                    .orElseThrow(() -> new ParseException(moveString, 0));
        }

        if (Pattern.compile("^[KNBQRknbqr][1-8]x[a-h][1-8]$").matcher(moveString).matches()) {
            // simple move

            Square toSquare = new Square(moveString.substring(3, 5));

            int fromRow = moveString.charAt(1) - '1';

            return board.getLegalMoves().stream()
                    .filter(m -> m.toIndex() == toSquare.getIndex())
                    .filter(m -> m.from().row() == fromRow)
                    .filter(m -> board.get(m.fromIndex()) != null && Character.toUpperCase(board.get(m.fromIndex()).getFENChar()) == Character.toUpperCase(moveString.charAt(0)))
                    .findFirst()
                    .orElseThrow(() -> new ParseException(moveString, 0));
        }

        if (Pattern.compile("^[KNBQRknbqr][a-h][1-8]x[a-h][1-8]$").matcher(moveString).matches()) {
            // simple move

            Square toSquare = new Square(moveString.substring(4, 6));

            int fromFile = moveString.charAt(1) - 'a';
            int fromRow = moveString.charAt(2) - '1';

            return board.getLegalMoves().stream()
                    .filter(m -> m.toIndex() == toSquare.getIndex())
                    .filter(m -> m.from().file() == fromFile)
                    .filter(m -> m.from().row() == fromRow)
                    .filter(m -> board.get(m.fromIndex()) != null && Character.toUpperCase(board.get(m.fromIndex()).getFENChar()) == Character.toUpperCase(moveString.charAt(0)))
                    .findFirst()
                    .orElseThrow(() -> new ParseException(moveString, 0));
        }

        if (moveString.equals("O-O-O")) {
            // long castle

            Square fromSquare = board.colorToMove == PieceColor.WHITE ? new Square("e1") : new Square("e8");
            Square toSquare = board.colorToMove == PieceColor.WHITE ? new Square("c1") : new Square("c8");

            return new Move(fromSquare, toSquare, null, false, false, SpecialMove.QUEEN_SIDE_CASTLE);
        }

        if (moveString.equals("O-O")) {
            // long castle

            Square fromSquare = board.colorToMove == PieceColor.WHITE ? new Square("e1") : new Square("e8");
            Square toSquare = board.colorToMove == PieceColor.WHITE ? new Square("g1") : new Square("g8");

            return new Move(fromSquare, toSquare, null, false, false, SpecialMove.KING_SIDE_CASTLE);
        }

        throw new ParseException(moveString, 0);
    }
}
