package org.example.engine;

import org.example.model.Board;
import org.example.model.Piece;
import org.example.model.Position;
import org.example.realtime.Motion;

import java.util.ArrayList;
import java.util.List;

public class JumpManager {

    private static final long HISTORY_KEEP_MS =
            10_000;

    private final List<Motion> jumpHistory;

    public JumpManager() {

        jumpHistory =
                new ArrayList<>();
    }

    public void addJump(
            Motion jumpMotion) {

        jumpHistory.add(jumpMotion);
    }

    public Motion findProtectingJump(
            Motion movingMotion,
            Board board) {

        Position destination =
                movingMotion.getTo();

        for (Motion jumpMotion : jumpHistory) {

            if (!jumpMotion.isJump()) {
                continue;
            }

            if (!samePosition(
                    jumpMotion.getTo(),
                    destination)) {

                continue;
            }

            Piece defender =
                    jumpMotion.getPiece();

            Piece attacker =
                    movingMotion.getPiece();

            if (defender.getColor()
                    == attacker.getColor()) {

                continue;
            }

            if (board.getPieceAt(destination)
                    != defender) {

                continue;
            }

            boolean overlaps =
                    jumpMotion.getStartTimeMillis()
                            <= movingMotion
                            .getArrivalTimeMillis()
                            &&
                            jumpMotion.getArrivalTimeMillis()
                                    >= movingMotion
                                    .getStartTimeMillis();

            if (overlaps) {
                return jumpMotion;
            }
        }

        return null;
    }

    public void remove(
            Motion jumpMotion) {

        jumpHistory.remove(jumpMotion);
    }

    public void removeForPiece(
            Piece piece) {

        jumpHistory.removeIf(
                jump ->
                        jump.getPiece() == piece
        );
    }

    public void update(
            long currentTime) {

        jumpHistory.removeIf(
                jump ->
                        currentTime
                                - jump.getStartTimeMillis()
                                > HISTORY_KEEP_MS
        );
    }

    public void clear() {

        jumpHistory.clear();
    }

    private boolean samePosition(
            Position first,
            Position second) {

        return first != null
                && second != null
                && first.getRow()
                == second.getRow()
                && first.getCol()
                == second.getCol();
    }
}