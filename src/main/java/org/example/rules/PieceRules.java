package org.example.rules;

import org.example.model.Board;
import org.example.model.Position;

public interface PieceRules {

    boolean isValidMove(Position from, Position to, Board board);
}
