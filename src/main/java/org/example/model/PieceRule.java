package org.example.model;

public interface PieceRule {

    boolean isValidMove(Board board, int fromRow, int fromCol, int toRow, int toCol);
}
