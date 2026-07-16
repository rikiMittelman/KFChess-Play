package org.example.rules;
import org.example.model.Board;
import org.example.model.Position;

public class BishopRules implements PieceRules {
    @Override
    public boolean isValidMove(Position from, Position to, Board board) {
        int dRow = Math.abs(to.getRow() - from.getRow());
        int dCol = Math.abs(to.getCol() - from.getCol());

        // רץ נע רק באלכסון (המרחק בשורות שווה למרחק בעמודות)
        return dRow == dCol;
    }
}
