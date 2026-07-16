package org.example.rules;

import org.example.model.Board;
import org.example.model.Position;

public class KingRules implements PieceRules{
    @Override
    public boolean isValidMove(Position from, Position to, Board board) {
        int dRow = Math.abs(to.getRow() - from.getRow());
        int dCol = Math.abs(to.getCol() - from.getCol());

        // מלך זז לכל היותר משבצת אחת לכל כיוון
        return dRow <= 1 && dCol <= 1;
    }
}
