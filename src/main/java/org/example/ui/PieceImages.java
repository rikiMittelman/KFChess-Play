package org.example.ui;

import org.example.Img;

import java.util.ArrayList;
import java.util.EnumMap;
import java.util.Map;

public class PieceImages {

    private final Map<AnimationState, ArrayList<Img>> animations =
            new EnumMap<>(AnimationState.class);

    public void put(AnimationState state,
                    ArrayList<Img> frames) {

        animations.put(state, frames);
    }

    public ArrayList<Img> get(AnimationState state) {

        return animations.get(state);
    }
}