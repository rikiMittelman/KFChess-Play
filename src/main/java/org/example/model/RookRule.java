package org.example.model;

public class RookRule implements PieceRule{

    public boolean isValidMove(Board board, int fromRow, int fromCol, int toRow, int toCol) {
        if (fromRow == toRow && fromCol == toCol) {
            return false;
        }

        boolean isRow = (fromRow == toRow);
        boolean isCol = (fromCol == toCol);

        if (!isRow && !isCol) {
            return false;
        }

        if (!board.isPathClear(fromRow, fromCol, toRow, toCol)) {
            return false;
        }

        Piece destinationPiece = board.getPiece(toRow, toCol);
        if (destinationPiece != null) {
            Piece currentPiece = board.getPiece(fromRow, fromCol);
            // אם לצריח שלנו ולכלי ביעד יש אותו צבע - המהלך חסום
            if (currentPiece != null && currentPiece.getColor() == destinationPiece.getColor()) {
                return false;
            }
        }

        return true;
    }
}
