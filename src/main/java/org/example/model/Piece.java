package org.example.model;

public class Piece {
    private  final char color;
    private  final char type;
    private  final PieceRule pieceRule;

    public Piece(char color, char type, PieceRule pieceRule) {
        this.color = color;
        this.type = type;
        this.pieceRule = pieceRule;
    }
    public char getColor() {
        return color;
    }
    public char getType() {
        return type;
    }
    public boolean isValidMove(Board board, int fromRow, int fromCol, int toRow, int toCol) {
        if (pieceRule == null) {
            return false;
        }
        return pieceRule.isValidMove(board, fromRow, fromCol, toRow, toCol);
    }
    @Override
    public String toString() {
        return "" + color + type;
    }
}
