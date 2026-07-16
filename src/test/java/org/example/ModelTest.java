package org.example;
// אלו הייבואים (Imports) של הכלים של JUnit והמודל שלך
import org.example.model.Board;
import org.example.model.Piece;
import org.example.model.Position;
import org.example.rules.RookRules;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
/*
public class ModelTest {

    @Test
    public void testRookValidMoveOnEmptyBoard() {
        // 1. Arrange - הכנה
        Board board = new Board(8, 8);
        RookRules pieceRule = new RookRules();
        Piece whiteRook = new Piece('w', 'R');

        // נשים את הצריח בפינה (0,0)
        board.setPiece(new Position( 0, 0), whiteRook);

        // 2. Act - הפעולה (נשאל את הכלי האם מהלך ישר ל- (0,5) הוא חוקי)
        boolean canMove = whiteRook.isValidMove(board, 0, 0, 0, 5);

        // 3. Assert - וידוא (אנחנו מצפים שזה יהיה חוקי, כלומר true)
        assertTrue(canMove, "הצריח אמור לזוז בקו ישר על לוח ריק!");
    }
}

*/