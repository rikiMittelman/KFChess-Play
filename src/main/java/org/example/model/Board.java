package org.example.model;

public class Board {
    private final Piece[][] grid;
    private final int rows;
    private final int cols;

    // קונסטרקטור המקבל את מימדי הלוח ויוצר את המערך הפנימי
    public Board(int rows, int cols) {
        this.rows = rows;
        this.cols = cols;
        this.grid = new Piece[rows][cols];
    }

    // החזרת מספר השורות בלוח
    public int getRows() {
        return rows;
    }

    // החזרת מספר העמודות בלוח
    public int getCols() {
        return cols;
    }

    // פונקציה פנימית לבדיקה האם המיקום חוקי ונמצא בתוך גבולות הלוח
    public boolean isValidPosition(Position pos) {
        if (pos == null) return false;
        return pos.getRow() >= 0 && pos.getRow() < rows &&
                pos.getCol() >= 0 && pos.getCol() < cols;
    }

    // שליפת כלי ממיקום מסוים - מחזירה null אם המשבצת ריקה או מחוץ ללוח
    public Piece getPieceAt(Position pos) {
        if (!isValidPosition(pos)) {
            return null;
        }
        return grid[pos.getRow()][pos.getCol()];
    }

    // השמת כלי במיקום מסוים
    public void setPieceAt(Position pos, Piece piece) {
        if (isValidPosition(pos)) {
            grid[pos.getRow()][pos.getCol()] = piece;
        }
    }

    // הסרת כלי ממיקום מסוים (פינוי המשבצת)
    public void removePieceAt(Position pos) {
        if (isValidPosition(pos)) {
            grid[pos.getRow()][pos.getCol()] = null;
        }
    }
}
