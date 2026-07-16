package org.example.ui;

public interface MoveListener {

    /**
     * נקרא כאשר מהלך התקבל על ידי מנוע המשחק.
     *
     * @param color צבע הכלי: 'w' או 'b'
     * @param move  תיאור המהלך, לדוגמה e2-e4
     */
    void onMove(char color, String move);
}