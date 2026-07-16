package org.example.rules;

import org.example.model.Board;
import org.example.model.Piece;
import org.example.model.Position;

import java.util.HashMap;
import java.util.Map;

public class RuleEngine {
    private final Map<Character, PieceRules> strategies = new HashMap<>();

    public RuleEngine() {
        // רישום כל אסטרטגיות הכלים במערכת
        strategies.put('K', new KingRules());
        strategies.put('Q', new QueenRules());
        strategies.put('R', new RookRules());
        strategies.put('B', new BishopRules());
        strategies.put('N', new KnightRules());
        strategies.put('P', new PawnRules());
    }

    // מתודה המאפשרת למשתמשים להגדיר כלים מותאמים אישית בעתיד (דרישה 2 של שמואל!)
    public void registerStrategy(char pieceType, PieceRules rule) {
        strategies.put(pieceType, rule);
    }

    public boolean validateMove(Position from, Position to, Board board) {
        // 1. בדיקה שהמיקומים חוקיים בתוך גבולות הלוח
        if (!board.isValidPosition(from) || !board.isValidPosition(to)) return false;
        if (from.equals(to)) return false;

        // 2. בדיקה שקיים כלי במיקום המקור
        Piece movingPiece = board.getPieceAt(from);
        if (movingPiece == null) return false;

        // 3. בדיקה שלא מנסים לאכול כלי מאותו הצבע
        Piece targetPiece = board.getPieceAt(to);
        if (targetPiece != null && targetPiece.getColor() == movingPiece.getColor()) {
            return false;
        }

        // 4. שליפת חוק התנועה הספציפי של הכלי והפעלתו
        PieceRules rule = strategies.get(movingPiece.getType());
        if (rule == null) return false;

        if (!rule.isValidMove(from, to, board)) return false;

        // 5. בדיקת חסימות בדרך עבור כלים שנעים למרחק (צריח, רץ, מלכה)
        char type = movingPiece.getType();
        if (type == 'R' || type == 'B' || type == 'Q') {
            if (!isPathClear(from, to, board)) return false;
        }

        return true;
    }

    // פונקציית עזר פרטית לבדיקה שהמסלול בין המקור ליעד נקי מכלים
    private boolean isPathClear(Position from, Position to, Board board) {
        int rowStep = Integer.compare(to.getRow(), from.getRow());
        int colStep = Integer.compare(to.getCol(), from.getCol());

        int currentRow = from.getRow() + rowStep;
        int currentCol = from.getCol() + colStep;

        while (currentRow != to.getRow() || currentCol != to.getCol()) {
            Position currentPos = new Position(currentRow, currentCol);
            if (board.getPieceAt(currentPos) != null) {
                return false; // נמצא כלי חוסם בדרך
            }
            currentRow += rowStep;
            currentCol += colStep;
        }
        return true;
    }
}
