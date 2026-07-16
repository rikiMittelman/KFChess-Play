package org.example.rules;

import org.example.model.Board;
import org.example.model.Position;

public class RookRules implements PieceRules {
    @Override
    public boolean isValidMove(Position from, Position to, Board board) {
        int dRow = Math.abs(to.getRow() - from.getRow());
        int dCol = Math.abs(to.getCol() - from.getCol());

        // צריח נע רק בקו ישר (שורה או עמודה)
        return dRow == 0 || dCol == 0;
    }

}
