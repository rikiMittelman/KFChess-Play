package org.example.rules;

import org.example.model.Board;
import org.example.model.Piece;
import org.example.model.Position;

public class PawnRules implements PieceRules{
    @Override
    public boolean isValidMove(Position from, Position to, Board board) {
        Piece pawn = board.getPieceAt(from);
        if (pawn == null) return false;

        char color = pawn.getColor();
        // קביעת כיוון התנועה: לבן נע למעלה (מינוס בשורות), שחור נע למטה (פלוס בשורות)
        int direction = (color == 'w') ? -1 : 1;

        int rowDiff = to.getRow() - from.getRow();
        int dCol = Math.abs(to.getCol() - from.getCol());

        // 1. צעד אחד קדימה למשבצת ריקה
        if (rowDiff == direction && dCol == 0) {
            return board.getPieceAt(to) == null;
        }

        // 2. צעד כפול קדימה משורת ההתחלה
        int startRow = (color == 'w') ? board.getRows() - 2 : 1;
        if (rowDiff == 2 * direction && from.getRow() == startRow && dCol == 0) {
            // נוודא שגם משבצת היעד וגם המשבצת שבדרך ריקות
            Position intermediate = new Position(from.getRow() + direction, from.getCol());
            return board.getPieceAt(to) == null && board.getPieceAt(intermediate) == null;
        }

        // 3. אכילה באלכסון (צעד אחד קדימה ואחד הצידה) למשבצת שאינה ריקה
        if (rowDiff == direction && dCol == 1) {
            return board.getPieceAt(to) != null;
        }

        return false;
    }
}
