package org.example.controller;

import org.example.model.Position;

public class BoardMapper {
    // מתרגם מחרוזת כמו "e4" לאובייקט Position פנימי
    // בהנחה שלוח שחמט סטנדרטי הוא 8x8, כאשר 'a' היא העמודה 0 ו-'1' היא השורה התחתונה
    public Position toPosition(String notation, int totalRows) {
        if (notation == null || notation.length() < 2) {
            return null;
        }

        char file = notation.charAt(0); // עמודה: 'a' עד 'h'
        char rank = notation.charAt(1); // שורה: '1' עד '8'

        int col = file - 'a';
        // בג'אווה שורה 0 היא לרוב העליונה, אז נהפוך את האינדקס בהתאם לגודל הלוח
        int row = totalRows - (rank - '0');

        return new Position(row, col);
    }

    // מתרגם אובייקט Position בחזרה למחרוזת מוכרת כמו "e4"
    public String toNotation(Position pos, int totalRows) {
        if (pos == null) return "";

        char file = (char) ('a' + pos.getCol());
        char rank = (char) ('0' + (totalRows - pos.getRow()));

        return "" + file + rank;
    }
}

