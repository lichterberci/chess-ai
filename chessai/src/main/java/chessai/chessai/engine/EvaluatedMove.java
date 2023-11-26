package chessai.chessai.engine;

import chessai.chessai.lib.Move;

public record EvaluatedMove(Move move, Integer eval) {
}
