package org.example.rules;

import org.example.model.Board;
import org.example.model.Position;

public class QueenRules implements PieceRules{
    private final RookRules rookRules = new RookRules();
    private final BishopRules bishopRules = new BishopRules();

    @Override
    public boolean isValidMove(Position from, Position to, Board board) {
        // מלכה יכולה לזוז או כמו צריח או כמו רץ
        return rookRules.isValidMove(from, to, board) ||
                bishopRules.isValidMove(from, to, board);
    }
}
