package chessai.chessai.engine;

import chessai.chessai.lib.Move;

import java.util.Optional;

public record EvaluatedMove(Move move, Optional<Integer> eval) {
}
