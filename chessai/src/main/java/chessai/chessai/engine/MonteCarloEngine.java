package chessai.chessai.engine;

import chessai.chessai.lib.Board;
import chessai.chessai.lib.GameState;
import chessai.chessai.lib.Move;
import chessai.chessai.lib.PieceColor;

import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.IntStream;

public class MonteCarloEngine extends ChessEngine {

    private static class TreeNode {
        private final Board state;
        private final TreeNode parent;
        private List<TreeNode> children = new ArrayList<>();
        private int numSimulationsRanByThisNode = 0;
        private int numWins = 0; // +1
        private int numDraws = 0; // +0.5

        TreeNode(Board state, TreeNode parent) {
            this.state = state;
            this.parent = parent;
        }

        List<Move> generateEmptyChildren() {

            List<Move> legalMoves = state.getLegalMoves();

            this.children = legalMoves
                    .stream()
                    .map(move -> new TreeNode(state.makeMove(move), this))
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
    public Optional<Move> makeMove(Board board) {

        TreeNode root = new TreeNode(board, null);
        List<Move> legalMovesByRoot = root.generateEmptyChildren();

        // no moves
        if (root.children.isEmpty())
            return Optional.empty();

        // forced move
        if (root.children.size() == 1) {
            Move result = legalMovesByRoot.get(0);
            System.out.printf("Forced move: %s ---> %s%n", result.from(), result.to());
            return Optional.of(result);
        }

//        List<TreeNode> unexploredNodes = new ArrayList<>(root.children);

        // search

        for (int i = 0; i < numNodesToCheck; i++) {

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

            IntStream.range(0, numSimulations).parallel().forEach(j -> {

                GameState result = simulate(selectedNodeToExplore.state);

                if (result == GameState.DRAW)
                    numDrawsToAdd.getAndIncrement();
                    // IMPORTANT: here, we check for the color of the board of the root (aka. simply board)
                else if (board.colorToMove == PieceColor.WHITE ? result == GameState.WHITE_WIN : result == GameState.BLACK_WIN)
                    numWinsToAdd.getAndIncrement();

            });

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

            selectedNodeToExplore.numSimulationsRanByThisNode += numSimulations;
            selectedNodeToExplore.numWins = numWinsToAdd.get();
            selectedNodeToExplore.numDraws = numDrawsToAdd.get();

            // backpropagation

            TreeNode backTraversingCurrentNode = selectedNodeToExplore.parent;

            while (backTraversingCurrentNode != null) {

                backTraversingCurrentNode.numWins += numWinsToAdd.get();
                backTraversingCurrentNode.numDraws += numDrawsToAdd.get();
                backTraversingCurrentNode.numSimulationsRanByThisNode += numSimulations;

                backTraversingCurrentNode = backTraversingCurrentNode.parent;
            }
        }

        // pick the child with the most simulations

	    Move result = null;
	    int maxSimulations = -1;
//        double maxScore = Double.MIN_VALUE;
//        double bestWinRate = -1;

        for (int i = 0; i < root.children.size(); i++) {

	        TreeNode child = root.children.get(i);

//            double winRate = (child.numWins + child.numDraws / 2.0) / child.numSimulationsRanByThisNode;

	        System.out.println(legalMovesByRoot.get(i).from() + " --> " + legalMovesByRoot.get(i).to() + " ===>  (" + (child.numWins + child.numDraws / 2.0) + "/" + child.numSimulationsRanByThisNode + ")");
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
	        }
        }

        if (result == null) {
            System.out.println("empty move");
            return Optional.empty();
        }

        System.out.println("move: " + result.from() + " --> " + result.to());

        return Optional.of(result);
    }

    private GameState simulate(Board board) {

        Board playingBoard = new Board(board);

        while (playingBoard.getState() == GameState.PLAYING) {

            List<Move> legalMoves = playingBoard.getLegalMoves();

            if (legalMoves.isEmpty())
                throw new IllegalStateException("There are no legal moves, but the state is PLAYING!");

            Move move = legalMoves.get(random.nextInt(legalMoves.size()));

//            System.out.println("        " + playingBoard.getFENString() + " ::: " + playingBoard.get(move.from()).getFENChar() + " " + move.from() + " -> " + move.to());

            playingBoard = playingBoard.makeMove(move);
        }

        return playingBoard.getState();
    }
}
