package chessai.chessai.engine;

import chessai.chessai.lib.Move;

import java.util.Optional;

/**
 * A move with optional evaluation attached to it.
 *
 * @param move The move
 * @param eval The evaluation (if it is set)
 */
public record EvaluatedMove(Move move, Optional<Integer> eval) {
}
