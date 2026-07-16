package org.example.model;

public final class Piece {
    private final char color; // 'w' או 'b'
    private final char type;  // 'K', 'Q', 'R', 'B', 'N', 'P'

    // קונסטרקטור לבניית הכלי
    public Piece(char color, char type) {
        this.color = color;
        this.type = type;
    }

    public char getColor() {
        return color;
    }

    public char getType() {
        return type;
    }

    // מחזיר ייצוג כמו "wP" או "bQ" בשביל ההדפסה והתקשורת
    public String getRepresentation() {
        return String.valueOf(color) + type;
    }
}
