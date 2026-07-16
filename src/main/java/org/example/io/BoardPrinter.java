package org.example.io;

import org.example.model.Board;
import org.example.model.Piece;
import org.example.model.Position;

public class BoardPrinter {
    public void print(Board board) {
        int rows = board.getRows();
        int cols = board.getCols();

        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                Position pos = new Position(i, j);
                Piece piece = board.getPieceAt(pos);

                if (piece == null) {
                    System.out.print(".");
                } else {
                    System.out.print(piece.getRepresentation());
                }

                // הדפסת רווח בין המשבצות, אך לא בסוף השורה
                if (j < cols - 1) {
                    System.out.print(" ");
                }
            }
            System.out.println();
        }
    }
}
