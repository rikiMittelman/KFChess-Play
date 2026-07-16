package org.example.model;

public class BoardInitializer {

    public static Board createInitialBoard() {

        Board board = new Board(8, 8);

        // ===== שחורים =====
        board.setPieceAt(new Position(0,0), new Piece('b','R'));
        board.setPieceAt(new Position(0,1), new Piece('b','N'));
        board.setPieceAt(new Position(0,2), new Piece('b','B'));
        board.setPieceAt(new Position(0,3), new Piece('b','Q'));
        board.setPieceAt(new Position(0,4), new Piece('b','K'));
        board.setPieceAt(new Position(0,5), new Piece('b','B'));
        board.setPieceAt(new Position(0,6), new Piece('b','N'));
        board.setPieceAt(new Position(0,7), new Piece('b','R'));

        for (int col = 0; col < 8; col++) {
            board.setPieceAt(new Position(1,col), new Piece('b','P'));
        }

        // ===== לבנים =====
        for (int col = 0; col < 8; col++) {
            board.setPieceAt(new Position(6,col), new Piece('w','P'));
        }

        board.setPieceAt(new Position(7,0), new Piece('w','R'));
        board.setPieceAt(new Position(7,1), new Piece('w','N'));
        board.setPieceAt(new Position(7,2), new Piece('w','B'));
        board.setPieceAt(new Position(7,3), new Piece('w','Q'));
        board.setPieceAt(new Position(7,4), new Piece('w','K'));
        board.setPieceAt(new Position(7,5), new Piece('w','B'));
        board.setPieceAt(new Position(7,6), new Piece('w','N'));
        board.setPieceAt(new Position(7,7), new Piece('w','R'));

        return board;
    }
}