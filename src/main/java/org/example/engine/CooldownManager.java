package org.example.engine;

import org.example.model.Piece;

import java.util.IdentityHashMap;
import java.util.Map;

public class CooldownManager {

    private final Map<Piece, Long> cooldownEndTimes;

    public CooldownManager() {

        cooldownEndTimes =
                new IdentityHashMap<>();
    }

    public void startCooldown(
            Piece piece,
            long currentTime,
            long duration) {

        cooldownEndTimes.put(
                piece,
                currentTime + duration
        );
    }

    public boolean isCoolingDown(
            Piece piece) {

        if (piece == null) {
            return false;
        }

        Long endTime =
                cooldownEndTimes.get(piece);

        if (endTime == null) {
            return false;
        }

        if (System.currentTimeMillis()
                >= endTime) {

            cooldownEndTimes.remove(piece);
            return false;
        }

        return true;
    }

    public void remove(
            Piece piece) {

        cooldownEndTimes.remove(piece);
    }

    public void update(
            long currentTime) {

        cooldownEndTimes
                .entrySet()
                .removeIf(
                        entry ->
                                currentTime
                                        >= entry.getValue()
                );
    }

    public void clear() {

        cooldownEndTimes.clear();
    }
}