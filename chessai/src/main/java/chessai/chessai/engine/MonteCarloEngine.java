package chessai.chessai.engine;

import chessai.chessai.lib.*;
import chessai.chessai.lib.pieces.*;

import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.BooleanSupplier;
import java.util.function.Consumer;
import java.util.stream.IntStream;

public class MonteCarloEngine extends ChessEngine {

    private static final int NODES_TO_SEARCH_BETWEEN_UPDATES = 10;

    private static class TreeNode {
        private final Board state;
        private final TreeNode parent;
        private List<TreeNode> children = new ArrayList<>();
        private int numSimulationsRanByThisNode = 0;
        private int numWins = 0; // +1
        private int numDraws = 0; // +0.5

        TreeNode(Board state, TreeNode parent, Move move) {
            this.state = state;
            this.parent = parent;
        }

        List<Move> generateEmptyChildren() {

            List<Move> legalMoves = state.getLegalMoves();

            this.children = legalMoves
                    .stream()
                    .map(move -> new TreeNode(state.makeMove(move), this, move))
                    .toList();

            return legalMoves;
        }

        public double getScore(double explorationParameter) {

            if (numSimulationsRanByThisNode == 0) {
                return Double.POSITIVE_INFINITY;
            }

            final int numSimulationsRanByTheParentNode = parent != null ? parent.numSimulationsRanByThisNode : 0;

            return (numWins + numDraws * 0.5) / numSimulationsRanByThisNode
                    + explorationParameter * Math.sqrt(Math.log(numSimulationsRanByTheParentNode) / numSimulationsRanByThisNode);
        }
    }

    private static final int PAWN_VALUE = 100;
    private static final int KNIGHT_VALUE = 300;
    private static final int BISHOP_VALUE = 320;
    private static final int ROOK_VALUE = 500;
    private static final int QUEEN_VALUE = 900;
    private final Random random;
    private final double explorationParameter;
    private final int numSimulations;
    private final int numNodesToCheck;

    public MonteCarloEngine(int seed, double explorationParameter, int numSimulations, int numNodesToCheck) {
        random = new Random(seed);
        this.explorationParameter = explorationParameter;
        this.numSimulations = numSimulations;
        this.numNodesToCheck = numNodesToCheck;
    }

    @Override
    public Optional<EvaluatedMove> makeMove(Board board, Consumer<Optional<EvaluatedMove>> callbackAfterEachDepth, BooleanSupplier isCancelled) {

        TreeNode root = new TreeNode(board, null, null);
        List<Move> legalMovesByRoot = root.generateEmptyChildren();

        // no moves
        if (root.children.isEmpty())
            return Optional.empty();

        // forced move
        if (root.children.size() == 1) {
            Move result = legalMovesByRoot.get(0);
            return Optional.of(new EvaluatedMove(result, 0));
        }

//        List<TreeNode> unexploredNodes = new ArrayList<>(root.children);

        // search

        for (int i = 0; i < numNodesToCheck && !isCancelled.getAsBoolean(); i++) {

            // selection

            TreeNode traversingNode = root;

            // we traverse down the tree, always choosing the child with the highest score
            while (!traversingNode.children.isEmpty()) {

                Optional<TreeNode> nextNode = traversingNode.children.stream()
                        .max(Comparator.comparingDouble(node -> node.getScore(explorationParameter)));

                if (nextNode.isEmpty())
                    break;

                traversingNode = nextNode.get();
            }

            final TreeNode selectedNodeToExplore = traversingNode;

            // exploration

            selectedNodeToExplore.generateEmptyChildren();

            // simulation

            AtomicInteger numWinsToAdd = new AtomicInteger();
            AtomicInteger numDrawsToAdd = new AtomicInteger();
//
//            int numWinsToAdd = 0;
//            int numDrawsToAdd = 0;

            TreeNode backTraversingCurrentNode;

            if (selectedNodeToExplore.state.getState() == GameState.PLAYING) {

                // we select 1 child, and play from there

                TreeNode selectedChild = selectedNodeToExplore.children.get(random.nextInt(selectedNodeToExplore.children.size()));

                IntStream.range(0, numSimulations).parallel().forEach(j -> {

                    GameState result = simulate(selectedChild.state);

                    if (result == GameState.DRAW)
                        numDrawsToAdd.getAndIncrement();
                        // IMPORTANT: here, we check for the color of the board of the root (aka. simply board)
                    else if (board.colorToMove == PieceColor.WHITE ? result == GameState.WHITE_WIN : result == GameState.BLACK_WIN)
                        numWinsToAdd.getAndIncrement();

                });

                // we start the back propagation from the child node

                selectedChild.numSimulationsRanByThisNode += numSimulations;
                selectedChild.numWins += numWinsToAdd.get();
                selectedChild.numDraws += numDrawsToAdd.get();

                backTraversingCurrentNode = selectedChild.parent;
            } else {

                // our selected node is in a terminal state

                switch (selectedNodeToExplore.state.getState()) {
                    case WHITE_WIN ->
                            numWinsToAdd.getAndAdd(board.colorToMove == PieceColor.WHITE ? numSimulations : 0);
                    case BLACK_WIN ->
                            numWinsToAdd.getAndAdd(board.colorToMove == PieceColor.BLACK ? numSimulations : 0);
                    case DRAW -> numDrawsToAdd.getAndAdd(numSimulations);
                    default -> throw new IllegalArgumentException("Unexpected value for game state!");
                }

                // we start the back propagation from our selected node

                selectedNodeToExplore.numSimulationsRanByThisNode += numSimulations;
                selectedNodeToExplore.numWins += numWinsToAdd.get();
                selectedNodeToExplore.numDraws += numDrawsToAdd.get();

                backTraversingCurrentNode = selectedNodeToExplore.parent;
            }

//            for (int j = 0; j < numSimulations; j++) {
//
//                GameState result = simulate(new Board(selectedNodeToExplore.state));
//
//                if (result == GameState.DRAW)
//                    numDrawsToAdd += 1;
//                    // IMPORTANT: here, we check for the color of the board of the root (aka. simply board)
//                else if (board.colorToMove == PieceColor.WHITE ? result == GameState.WHITE_WIN : result == GameState.BLACK_WIN)
//                    numWinsToAdd += 1;
//
//            }


            // backpropagation

            while (backTraversingCurrentNode != null) {

                backTraversingCurrentNode.numWins += numWinsToAdd.get();
                backTraversingCurrentNode.numDraws += numDrawsToAdd.get();
                backTraversingCurrentNode.numSimulationsRanByThisNode += numSimulations;

                backTraversingCurrentNode = backTraversingCurrentNode.parent;
            }

            // update best move, if necessary

            if (i % NODES_TO_SEARCH_BETWEEN_UPDATES == 1) {
                Move result = null;
                int resultEval = 0;
                int maxSimulations = -1;

                for (int j = 0; j < root.children.size(); j++) {
                    TreeNode child = root.children.get(j);

                    if (child.numSimulationsRanByThisNode > maxSimulations) {
                        maxSimulations = child.numSimulationsRanByThisNode;
                        result = legalMovesByRoot.get(j);
                        double winRate = (child.numWins + child.numDraws / 2.0f) / child.numSimulationsRanByThisNode;
                        resultEval = (int) (Math.pow(winRate * 2 - 1, 3) * Integer.MAX_VALUE);
                        if (child.state.colorToMove == PieceColor.WHITE)
                            resultEval = -resultEval;
                    }
                }

                if (result == null) {
                    callbackAfterEachDepth.accept(Optional.empty());
                }

                callbackAfterEachDepth.accept(Optional.of(new EvaluatedMove(result, resultEval)));
            }
        }

        // pick the child with the most simulations

	    Move result = null;
        int resultEval = 0;
	    int maxSimulations = -1;
//        double maxScore = Double.MIN_VALUE;
//        double bestWinRate = -1;

        for (int i = 0; i < root.children.size(); i++) {

	        TreeNode child = root.children.get(i);

//            double winRate = (child.numWins + child.numDraws / 2.0) / child.numSimulationsRanByThisNode;

//	        System.out.println(legalMovesByRoot.get(i).from() + " --> " + legalMovesByRoot.get(i).to() + " ===>  (" + (child.numWins + child.numDraws / 2.0) + "/" + child.numSimulationsRanByThisNode + ")");
//
//            if (Double.isNaN(winRate))
//                continue;

//            if (winRate > bestWinRate) {
//                result = legalMovesByRoot.get(i);
//                bestWinRate = winRate;
//            }

//            System.out.println(child.score);

//            if (child.score > maxScore) {
//                System.out.println("update to child");
//                result = legalMovesByRoot.get(i);
//                maxScore = child.score;
//            }

	        if (child.numSimulationsRanByThisNode > maxSimulations) {
		        maxSimulations = child.numSimulationsRanByThisNode;
		        result = legalMovesByRoot.get(i);
                double winRate = (child.numWins + child.numDraws / 2.0f) / child.numSimulationsRanByThisNode;
                resultEval = (int) (Math.pow(winRate * 2 - 1, 3) * Integer.MAX_VALUE);
                if (child.state.colorToMove == PieceColor.WHITE)
                    resultEval = -resultEval;
	        }
        }

        if (result == null) {
//            System.out.println("empty move");
            return Optional.empty();
        }

//        System.out.println("move: " + result.from() + " --> " + result.to());

        return Optional.of(new EvaluatedMove(result, resultEval));
    }

    private GameState simulate(Board board) {

        Board playingBoard = new Board(board);

        while (playingBoard.getState() == GameState.PLAYING) {

            List<Move> legalMoves = playingBoard.getLegalMoves();

            if (legalMoves.isEmpty())
                throw new IllegalStateException("There are no legal moves, but the state is PLAYING!");

//            Move move = legalMoves.get(random.nextInt(legalMoves.size()));

            Move move = getBiasedRandomMove(playingBoard, legalMoves, random);

//            System.out.println("        " + playingBoard.getFENString() + " ::: " + playingBoard.get(move.from()).getFENChar() + " " + move.from() + " -> " + move.to());

            playingBoard = playingBoard.makeMove(move);
        }

        return playingBoard.getState();
    }

    private Move getBiasedRandomMove(Board board, List<Move> moves, Random random) {

        List<Double> moveWeights = moves.stream().map((Move move) -> getMoveWeight(board, move)).toList();

        double sumWeight = moveWeights.stream().mapToDouble(x -> x).sum();

        double currentWeightsSum = 0;

        double randomValue = random.nextDouble(sumWeight);

        for (int i = 0; i < moves.size(); i++) {

            if (currentWeightsSum + moveWeights.get(i) > randomValue)
                return moves.get(i);

            currentWeightsSum += moveWeights.get(i);
        }

        return moves.get(moves.size() - 1);
    }

    private double getMoveWeight(Board board, Move move) {

        double result = 1;

        Piece movingPiece = board.get(move.fromIndex());

        if (move.isCapture()) {

            int capturedIndex = move.toIndex();
            if (move.isEnPassant())
                capturedIndex += board.colorToMove == PieceColor.WHITE ? 8 : -8;

            Piece capturedPiece = board.get(capturedIndex);

            int valueDifference = getPieceValue(capturedPiece.getClass()) - getPieceValue(movingPiece.getClass());

            result += 4 * valueDifference;
        }

        if (move.isCheck())
            result += 10;

        if (move.promotionPieceType() != null)
            result += 20;

        return Math.max(result, 1);
    }

    private int getPieceValue(Class<? extends Piece> pieceType) {
        if (pieceType.equals(Pawn.class))
            return PAWN_VALUE;
        else if (pieceType.equals(Knight.class))
            return KNIGHT_VALUE;
        else if (pieceType.equals(Bishop.class))
            return BISHOP_VALUE;
        else if (pieceType.equals(Rook.class))
            return ROOK_VALUE;
        else if (pieceType.equals(Queen.class))
            return QUEEN_VALUE;
        else
            return 0;
    }
}
