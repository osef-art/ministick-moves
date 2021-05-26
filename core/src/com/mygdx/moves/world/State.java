package com.mygdx.moves.world;

import java.util.EnumMap;

public class State {
    private final StateRecord current;
    private final EnumMap<MoveInput, StateRecord> followUps = new EnumMap<>(MoveInput.class);

    public State(StateRecord current) {
        this.current = current;
    }
}
