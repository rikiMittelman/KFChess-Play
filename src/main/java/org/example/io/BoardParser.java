package org.example.io;
import org.example.model.Board;
import org.example.model.Piece;
import org.example.model.Position;

import java.util.HashSet;
import java.util.List;

public class BoardParser {
    private static final HashSet<String> VALID_TOKENS = new HashSet<>();

    // אתחול רשימת התווים החוקיים בלוח
    static {
        VALID_TOKENS.add(".");
        char[] colors = {'w', 'b'};
        char[] types = {'K', 'Q', 'R', 'B', 'N', 'P'};
        for (char color : colors) {
            for (char type : types) {
                VALID_TOKENS.add("" + color + type);
            }
        }
    }

    public Board parse(List<String> rows) {
        int height = rows.size();
        if (height == 0) {
            return new Board(0, 0);
        }

        // קביעת הרוחב לפי השורה הראשונה
        String[] firstLineTokens = rows.get(0).split(" ");
        int width = firstLineTokens.length;

        Board board = new Board(height, width);

        for (int i = 0; i < height; i++) {
            String[] lineTokens = rows.get(i).split(" ");

            // בדיקת התאמת רוחב השורה (איטרציה 1 - ROW_WIDTH_MISMATCH)
            if (lineTokens.length != width) {
                System.out.println("ERROR ROW_WIDTH_MISMATCH");
                System.exit(0);
            }

            for (int j = 0; j < width; j++) {
                String token = lineTokens[j];

                // בדיקת תקינות הכלי (איטרציה 1 - UNKNOWN_TOKEN)
                if (!VALID_TOKENS.contains(token)) {
                    System.out.println("ERROR UNKNOWN_TOKEN");
                    System.exit(0);
                }

                // אם המשבצת אינה ריקה, ניצור כלי ונשים אותו בלוח
                if (!token.equals(".")) {
                    char color = token.charAt(0);
                    char type = token.charAt(1);
                    Position pos = new Position(i, j);
                    board.setPieceAt(pos, new Piece(color, type));
                }
            }
        }
        return board;
    }
}
