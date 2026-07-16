package org.example.rules;

import org.example.model.Board;
import org.example.model.Position;

public class KnightRules implements PieceRules{
    public boolean isValidMove(Position from, Position to, Board board) {
        int dRow = Math.abs(to.getRow() - from.getRow());
        int dCol = Math.abs(to.getCol() - from.getCol());

        // פרש נע בצורת L (2 ו-1, או 1 ו-2)
        return (dRow == 2 && dCol == 1) || (dRow == 1 && dCol == 2);
    }
}
