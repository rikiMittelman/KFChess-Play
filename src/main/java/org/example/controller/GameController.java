package org.example.controller;
import org.example.engine.GameEngine;
import org.example.model.Position;

public class GameController {
    private final GameEngine gameEngine;
    private final BoardMapper mapper;

    // הקונסטרקטור מקבל את מנוע המשחק ומאתחל את המפר
    public GameController(GameEngine gameEngine) {
        this.gameEngine = gameEngine;
        this.mapper = new BoardMapper();
    }

    /**
     * מקבל פקודת תנועה בפורמט טקסט ומנסה לבצע אותה במנוע
     * @param fromNotation מיקום המקור (למשל "e2")
     * @param toNotation מיקום היעד (למשל "e4")
     * @return true אם המהלך התקבל ונרשם במערכת בזמן אמת, false אחרת
     */
    public boolean handlePlayerMove(String fromNotation, String toNotation) {
        int totalRows = gameEngine.getBoard().getRows();

        // תרגום הטקסט לאובייקטי Position פנימיים
        Position from = mapper.toPosition(fromNotation, totalRows);
        Position to = mapper.toPosition(toNotation, totalRows);

        if (from == null || to == null) {
            return false; // פורמט קלט לא תקין
        }

        // שליחת המהלך לביצוע במנוע המשחק
        return gameEngine.makeMove(from, to);
    }

    /**
     * מעדכן את ציר הזמן של המשחק (נקרא בכל פעם שמתקדם זמן או פולס במערכת)
     */
    public void handleTick() {
        gameEngine.tick();
    }
}
