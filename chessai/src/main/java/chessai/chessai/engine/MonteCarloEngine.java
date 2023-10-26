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
        Board state;
        TreeNode parent;
        List<TreeNode> children = new ArrayList<>();
        int numSimulationsRanByThisNode = 0;
        int numWins = 0; // +1
        int numDraws = 0; // +0.5
        int numSimulationsRanByParentNode = 0;
        double score;

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

        void updateScore(double explorationParameter) {

            if (numSimulationsRanByThisNode == 0) {
                score = 0;
                return;
            }

            score = ((double) numWins /*+ numDraws / 2.0*/) / numSimulationsRanByThisNode
                    + explorationParameter * Math.sqrt(Math.log(numSimulationsRanByParentNode) / numSimulationsRanByThisNode);
        }
    }

    private Random random;
    private double explorationParameter;
    private int numSimulations;
    private int numNodesToCheck;

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

        if (root.children.isEmpty())
            return Optional.empty();

        List<TreeNode> unexploredNodes = new ArrayList<>(root.children);

        // search

        for (int i = 0; i < numNodesToCheck; i++) {

            // selection

            unexploredNodes.forEach(node -> node.updateScore(explorationParameter));

            if (unexploredNodes.isEmpty())
                break;

            TreeNode selectedNodeToExplore = unexploredNodes.stream()
                    .max(Comparator.comparingDouble(node -> node.score))
                    .get();

            // exploration

            unexploredNodes.remove(selectedNodeToExplore);

            selectedNodeToExplore.generateEmptyChildren();

            unexploredNodes.addAll(selectedNodeToExplore.children);
//
//            System.out.printf(
//                    "exploring node %d - parent: %s - fen: %s -  %n",
//                    i,
//                    selectedNodeToExplore.parent.hashCode(),
//                    selectedNodeToExplore.state.getFENString()
//            );

//             simulation

            AtomicInteger numWinsToAdd = new AtomicInteger();
            AtomicInteger numDrawsToAdd = new AtomicInteger();

//            for (int j = 0; j < numSimulations; j++) {
//
////                System.out.println("    running simulation " + j);
//
//                GameState result = simulate(selectedNodeToExplore.state);
//
//                if (result == GameState.DRAW)
//                    numDrawsToAdd++;
//                    // IMPORTANT: here, we check for the color of the board of the root (aka. simply board)
//                else if (board.colorToMove == PieceColor.WHITE ? result == GameState.WHITE_WIN : result == GameState.BLACK_WIN)
//                    numWinsToAdd++;
//            }

            IntStream.range(0, numSimulations).parallel().forEach(j -> {

                GameState result = simulate(selectedNodeToExplore.state);

                if (result == GameState.DRAW)
                    numDrawsToAdd.getAndIncrement();
                    // IMPORTANT: here, we check for the color of the board of the root (aka. simply board)
                else if (board.colorToMove == PieceColor.WHITE ? result == GameState.WHITE_WIN : result == GameState.BLACK_WIN)
                    numWinsToAdd.getAndIncrement();

            });

            selectedNodeToExplore.numSimulationsRanByThisNode += numSimulations;
            selectedNodeToExplore.numWins = numWinsToAdd.get();
            selectedNodeToExplore.numDraws = numDrawsToAdd.get();

            // backpropagation

            selectedNodeToExplore.children.forEach(child -> child.numSimulationsRanByParentNode += numSimulations);

            TreeNode currentNode = selectedNodeToExplore.parent;

            while (currentNode != null) {

                currentNode.numWins += numWinsToAdd.get();
                currentNode.numDraws += numDrawsToAdd.get();
                currentNode.numSimulationsRanByThisNode += numSimulations;
                if (currentNode.parent != null)
                    currentNode.numSimulationsRanByParentNode += numSimulations;

                currentNode = currentNode.parent;
            }
        }

        // pick the child with the most simulations

        Move result = null;
//        int maxSimulations = -1;
//        double maxScore = Double.MIN_VALUE;
        double bestWinRate = -1;

        for (int i = 0; i < root.children.size(); i++) {

            TreeNode child = root.children.get(i);

            double winRate = (child.numWins + child.numDraws / 2.0) / child.numSimulationsRanByThisNode;

            System.out.println(legalMovesByRoot.get(i).from() + " --> " + legalMovesByRoot.get(i).to() + " ===> " + winRate + " (" + (child.numWins + child.numDraws / 2.0) + "/" + child.numSimulationsRanByThisNode + ")");

            if (Double.isNaN(winRate))
                continue;

            if (winRate > bestWinRate) {
                result = legalMovesByRoot.get(i);
                bestWinRate = winRate;
            }

//            System.out.println(child.score);

//            if (child.score > maxScore) {
//                System.out.println("update to child");
//                result = legalMovesByRoot.get(i);
//                maxScore = child.score;
//            }
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
