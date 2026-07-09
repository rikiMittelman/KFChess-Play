package org.example.model;

public class Board {
    private final Piece[][] grid;
    private final int rows;
    private final int cols;
    public Board(int row, int col) {
        this.rows = row;
        this.cols = col;
        this.grid = new Piece[rows][cols];
    }
    public boolean isValidPosition(int row, int col) {
        return row >= 0 && row < rows && col >= 0 && col < cols;
    }
    public Piece getPiece(int row, int col) {
        if(!isValidPosition(row, col)) {
            return null;
        }
        return grid[row][col];
    }
    public boolean isEmpty(int row, int col) {
        return isValidPosition(row, col) && grid[row][col]==null;
    }
    public void setPiece(int row, int col, Piece piece) {
        if(isValidPosition(row, col)) {
            grid[row][col] = piece;

        }
    }
    public void movePiece(int fromRow, int fromCol, int toRow, int toCol) {
        Piece  piece = getPiece(fromRow, fromCol);
        setPiece(fromRow, fromCol, piece);
        setPiece(toRow, toCol, null);
    }
    public void print() {
        for (int i = 0; i < rows; i++) {
            StringBuilder rowStr = new StringBuilder();
            for (int j = 0; j < cols; j++) {
                if (grid[i][j] == null) {
                    rowStr.append(".");
                } else {
                    // כאן ה-toString() שכתבנו בתוך Piece יופעל אוטומטית (למשל "wR")
                    rowStr.append(grid[i][j].toString());
                }

                // מוסיפים רווח בין המשבצות, אבל לא בסוף השורה
                if (j < cols - 1) {
                    rowStr.append(" ");
                }
            }
            System.out.println(rowStr);
        }
    }
    public boolean isPathClear(int fromRow, int fromCol, int toRow, int toCol) {
        // Integer.compare מחזיר 1, 1- או 0, וזה נותן לנו את כיוון הצעד (הדלתא)
        int deltaRow = Integer.compare(toRow, fromRow);
        int deltaCol = Integer.compare(toCol, fromCol);

        int currentRow = fromRow + deltaRow;
        int currentCol = fromCol + deltaCol;

        // עוברים משבצת-משבצת בדרך עד שמגיעים ליעד
        while (currentRow != toRow || currentCol != toCol) {
            if (!isEmpty(currentRow, currentCol)) {
                return false; // נמצא כלי חוסם!
            }
            currentRow += deltaRow;
            currentCol += deltaCol;
        }
        return true;
    }
}
