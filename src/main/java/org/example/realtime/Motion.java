package org.example.realtime;

import org.example.model.Piece;
import org.example.model.Position;

public class Motion {

    private final Piece piece;
    private final Position from;
    private final Position to;

    private final long startTimeMillis;
    private final long arrivalTimeMillis;

    public Motion(
            Piece piece,
            Position from,
            Position to,
            long startTimeMillis,
            long arrivalTimeMillis) {

        this.piece = piece;
        this.from = from;
        this.to = to;
        this.startTimeMillis = startTimeMillis;
        this.arrivalTimeMillis = arrivalTimeMillis;
    }

    public Piece getPiece() {
        return piece;
    }

    public Position getFrom() {
        return from;
    }

    public Position getTo() {
        return to;
    }

    public long getStartTimeMillis() {
        return startTimeMillis;
    }

    public long getArrivalTimeMillis() {
        return arrivalTimeMillis;
    }

    public boolean isJump() {

        return from.getRow() == to.getRow()
                && from.getCol() == to.getCol();
    }
}