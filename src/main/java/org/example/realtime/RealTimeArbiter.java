package org.example.realtime;

import org.example.model.Piece;
import org.example.model.Position;

import java.util.ArrayList;
import java.util.List;

public class RealTimeArbiter {

    private final List<Motion> activeMotions;

    public RealTimeArbiter() {

        activeMotions =
                new ArrayList<>();
    }

    public void addMotion(
            Motion motion) {

        if (motion != null) {
            activeMotions.add(motion);
        }
    }

    public void removeMotion(
            Motion motion) {

        activeMotions.remove(motion);
    }

    public void removeMotionsForPiece(
            Piece piece) {

        activeMotions.removeIf(
                motion ->
                        motion.getPiece() == piece
        );
    }

    public boolean isSourceBlocked(
            Position position) {

        if (position == null) {
            return false;
        }

        for (Motion motion : activeMotions) {

            if (samePosition(
                    motion.getFrom(),
                    position)) {

                return true;
            }
        }

        return false;
    }

    public boolean hasMotionForPiece(
            Piece piece) {

        for (Motion motion : activeMotions) {

            if (motion.getPiece() == piece) {
                return true;
            }
        }

        return false;
    }

    public List<Motion> updateTime(
            long currentTimeMillis) {

        List<Motion> arrivedMotions =
                new ArrayList<>();

        activeMotions.removeIf(
                motion -> {

                    if (currentTimeMillis
                            >= motion.getArrivalTimeMillis()) {

                        arrivedMotions.add(motion);
                        return true;
                    }

                    return false;
                }
        );

        return arrivedMotions;
    }

    public List<Motion> getActiveMotions() {

        return new ArrayList<>(
                activeMotions
        );
    }

    private boolean samePosition(
            Position first,
            Position second) {

        if (first == null || second == null) {
            return false;
        }

        return first.getRow() == second.getRow()
                && first.getCol() == second.getCol();
    }
}