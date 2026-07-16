package org.example.engine;

import org.example.model.Piece;

public class ScoreManager {

    private int whiteScore;
    private int blackScore;

    public ScoreManager() {
        whiteScore = 0;
        blackScore = 0;
    }

    public void addCaptureScore(
            Piece attacker,
            Piece capturedPiece) {

        int points =
                getPieceValue(capturedPiece);

        if (attacker.getColor() == 'w') {
            whiteScore += points;
        } else {
            blackScore += points;
        }
    }

    private int getPieceValue(
            Piece piece) {

        switch (piece.getType()) {

            case 'P':
                return 1;

            case 'N':
            case 'B':
                return 3;

            case 'R':
                return 5;

            case 'Q':
                return 9;

            default:
                return 0;
        }
    }

    public int getWhiteScore() {
        return whiteScore;
    }

    public int getBlackScore() {
        return blackScore;
    }
}