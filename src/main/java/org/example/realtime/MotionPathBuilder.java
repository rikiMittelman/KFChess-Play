package org.example.realtime;

import org.example.model.Piece;
import org.example.model.Position;

import java.util.ArrayList;
import java.util.List;

public final class MotionPathBuilder {

    private MotionPathBuilder() {
    }

    public static List<Position> buildPath(
            Piece piece,
            Position from,
            Position to) {

        List<Position> path =
                new ArrayList<>();

        path.add(from);

        /*
         * פרש אינו עובר דרך משבצות הביניים.
         * מבחינת ההתנגשות הוא קופץ ישירות ליעד.
         */
        if (piece.getType() == 'N') {

            path.add(to);
            return path;
        }

        int rowDirection =
                Integer.compare(
                        to.getRow(),
                        from.getRow()
                );

        int colDirection =
                Integer.compare(
                        to.getCol(),
                        from.getCol()
                );

        int currentRow =
                from.getRow();

        int currentCol =
                from.getCol();

        while (currentRow != to.getRow()
                || currentCol != to.getCol()) {

            currentRow += rowDirection;
            currentCol += colDirection;

            path.add(
                    new Position(
                            currentRow,
                            currentCol
                    )
            );
        }

        return path;
    }
}