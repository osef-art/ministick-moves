package com.mygdx.moves.world;

import java.util.EnumMap;
import java.util.stream.Stream;

public class FollowUps {
    enum MoveInput {
        NONE,
        JUMP,
        DOWN,
        KICK,
        SIDE,
        PUNCH,
        SIDE_KICK,
        DOWN_KICK,
        SIDE_PUNCH,
        DOWN_PUNCH,
    }

    private final EnumMap<MoveInput, State> states = new EnumMap<>(MoveInput.class);

    public boolean isEmpty() {
        return states.isEmpty();
    }

    public static FollowUps empty() {
        return new FollowUps();
    }

    public Stream<State> stream() {
        return states.values().stream();
    }

    public void isEase() {
        onRelease(State.IDLE);
    }

    public void isAerial() {
        onRelease(State.GET_UP);
    }

    public void onRelease(State state) {
        states.putIfAbsent(MoveInput.NONE, state);
    }

    public FollowUps onSide(State state) {
        states.putIfAbsent(MoveInput.SIDE, state);
        return this;
    }

    public FollowUps onJump(State state) {
        states.putIfAbsent(MoveInput.JUMP, state);
        return this;
    }

    public FollowUps onDown(State state) {
        states.putIfAbsent(MoveInput.DOWN, state);
        return this;
    }

    public FollowUps onKick(State state) {
        states.putIfAbsent(MoveInput.KICK, state);
        return this;
    }

    public FollowUps onPunch(State state) {
        states.putIfAbsent(MoveInput.PUNCH, state);
        return this;
    }

    public FollowUps onSideKick(State state) {
        states.putIfAbsent(MoveInput.SIDE_KICK, state);
        return this;
    }

    public FollowUps onSidePunch(State state) {
        states.putIfAbsent(MoveInput.SIDE_PUNCH, state);
        return this;
    }

    public FollowUps onDownKick(State state) {
        states.putIfAbsent(MoveInput.DOWN_KICK, state);
        return this;
    }

    public FollowUps onDownPunch(State state) {
        states.putIfAbsent(MoveInput.DOWN_PUNCH, state);
        return this;
    }
}
