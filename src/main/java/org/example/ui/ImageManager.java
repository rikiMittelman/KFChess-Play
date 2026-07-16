package org.example.ui;

import org.example.Img;
import org.example.model.Piece;

import java.awt.*;
import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class ImageManager {

    private final Map<String, PieceImages> images = new HashMap<>();

    private final int pieceWidth;
    private final int pieceHeight;

    public ImageManager(int pieceWidth, int pieceHeight) {

        this.pieceWidth = pieceWidth;
        this.pieceHeight = pieceHeight;

        loadAllImages();
    }

    private void loadAllImages() {

        String[] colors = {"white", "black"};

        String[] pieces = {
                "king",
                "queen",
                "rook",
                "bishop",
                "knight",
                "pawn"
        };

        for (String color : colors) {
            for (String piece : pieces) {
                loadPiece(color, piece);
            }
        }
    }

    private void loadPiece(String color, String piece) {

        PieceImages pieceImages = new PieceImages();

        for (AnimationState state : AnimationState.values()) {

            ArrayList<Img> frames = loadFrames(color, piece, state);

            pieceImages.put(state, frames);

        }

        images.put(color + "_" + piece, pieceImages);
    }

    private ArrayList<Img> loadFrames(String color,
                                      String piece,
                                      AnimationState state) {

        ArrayList<Img> frames = new ArrayList<>();

        int frame = 1;

        while (true) {

            String path =
                    "src/main/images/"
                            + color + "/"
                            + piece
                            + "/states/"
                            + state.name().toLowerCase()
                            + "/sprites/"
                            + frame
                            + ".png";

            File file = new File(path);

            if (!file.exists()) {
                break;
            }

            Img img = new Img().read(
                    path,
                    new Dimension(pieceWidth, pieceHeight),
                    true,
                    null);

            frames.add(img);

            frame++;
        }

        pieceImagesDebug(color, piece, state, frames.size());

        return frames;
    }

    private void pieceImagesDebug(String color,
                                  String piece,
                                  AnimationState state,
                                  int count) {

        System.out.println(
                color + " "
                        + piece + " "
                        + state + " -> "
                        + count + " frames");
    }

    public Img getImage(Piece piece,
                        AnimationState state,
                        int frameIndex) {

        if (piece == null)
            return null;

        String color =
                piece.getColor() == 'w'
                        ? "white"
                        : "black";

        String type;

        switch (piece.getType()) {

            case 'K':
                type = "king";
                break;

            case 'Q':
                type = "queen";
                break;

            case 'R':
                type = "rook";
                break;

            case 'B':
                type = "bishop";
                break;

            case 'N':
                type = "knight";
                break;

            case 'P':
                type = "pawn";
                break;

            default:
                return null;
        }

        PieceImages pieceImages =
                images.get(color + "_" + type);

        if (pieceImages == null)
            return null;

        ArrayList<Img> frames =
                pieceImages.get(state);

        if (frames == null || frames.isEmpty())
            return null;

        frameIndex = frameIndex % frames.size();

        return frames.get(frameIndex);
    }
    public int getFrameCount(Piece piece, AnimationState state) {

        if (piece == null || state == null) {
            return 0;
        }

        String color = piece.getColor() == 'w'
                ? "white"
                : "black";

        String type;

        switch (piece.getType()) {
            case 'K':
                type = "king";
                break;
            case 'Q':
                type = "queen";
                break;
            case 'R':
                type = "rook";
                break;
            case 'B':
                type = "bishop";
                break;
            case 'N':
                type = "knight";
                break;
            case 'P':
                type = "pawn";
                break;
            default:
                return 0;
        }

        PieceImages pieceImages = images.get(color + "_" + type);

        if (pieceImages == null) {
            return 0;
        }

        java.util.ArrayList<Img> frames = pieceImages.get(state);

        if (frames == null) {
            return 0;
        }

        return frames.size();
    }
}