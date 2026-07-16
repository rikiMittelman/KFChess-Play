package org.example.model;

import java.util.Objects;

public final class Position {
    private final int row;
    private final int col;

    public Position(int row, int col) {
        this.row = row;
        this.col = col;
    }

    public int getRow() { return row; }
    public int getCol() { return col; }

    // פונקציית השוואה מודרנית ובטוחה
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        // המרה מפורשת שעובדת בכל גרסאות ג'אווה
        Position position = (Position) o;
        return this.row == position.row && this.col == position.col;
    }
    // ייצור קוד גיבוב ייחודי המבוסס על השורה והעמודה
    @Override
    public int hashCode() {
        return Objects.hash(row, col);
    }

    // הדפסה נוחה לצורכי בדיקה ודיבאג
    @Override
    public String toString() {
        return "(" + row + "," + col + ")";
    }

}
