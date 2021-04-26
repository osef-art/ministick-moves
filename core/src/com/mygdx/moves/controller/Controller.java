package com.mygdx.moves.controller;

import com.badlogic.gdx.InputProcessor;
import com.mygdx.moves.world.Ministick;

import java.util.*;
import java.util.stream.IntStream;

public class Controller implements InputProcessor {
    public enum Key {
        LEFT_KEY(21, 45),
        RIGHT_KEY(22, 32),
        UP_KEY(19, 54),
        DOWN_KEY(20, 47),
        PUNCH_KEY(31, 40),
        KICK_KEY(50, 41),
        ;

        private final List<Integer> keys = new ArrayList<>();

        Key(int code, int alt) {
            keys.add(code);
            keys.add(alt);
        }

        static Optional<Key> ofCode(int code) {
            return Arrays.stream(values()).filter(k -> k.keys.contains(code)).findFirst();
        }
    }
    private final Ministick actor;
    private final HashMap<Integer, Boolean> pressed = new HashMap<>();

    public Controller(Ministick ministick) {
        this.actor = ministick;

        IntStream.range(0, 100)
          .forEach(n -> pressed.put(n, false));
    }

    @Override
    public boolean keyDown (int keycode) {
        // System.out.println(keycode);
        pressed.put(keycode, true);

        // GAME
        if (keycode == 29 || keycode == 131) { // Q, ESC
            System.exit(0);
        }

        // MOVES
        Key.ofCode(keycode).ifPresent(k -> {
            switch (k) {
                case LEFT_KEY:
                case RIGHT_KEY:
                    actor.walk();
                    break;
                case UP_KEY:
                    actor.jump();
                    break;
                case DOWN_KEY:
                    actor.squat();
                    break;
                case PUNCH_KEY:
                    if (isPressed(Key.DOWN_KEY)) actor.downPunch();
                    else if (isPressed(Key.LEFT_KEY)) actor.leftSidePunch();
                    else if (isPressed(Key.RIGHT_KEY)) actor.rightSidePunch();
                    else actor.punch();
                    break;
                case KICK_KEY:
                    if (isPressed(Key.DOWN_KEY)) actor.downKick();
                    else if (isPressed(Key.LEFT_KEY)) actor.leftSideKick();
                    else if (isPressed(Key.RIGHT_KEY)) actor.rightSideKick();
                    else actor.kick();
                    break;
           }
        });
        return true;
    }

    @Override
    public boolean keyUp (int keycode) {
        pressed.put(keycode, false);

        Key.ofCode(keycode).ifPresent(k -> {
            switch (k) {
                case LEFT_KEY:
                case RIGHT_KEY:
                    actor.stopWalking();
                    break;
                case DOWN_KEY: // down
                    actor.getUp();
                    break;
            }
        });
        return false;
    }

    public boolean isPressed(Key ... keys) {
        return Arrays.stream(keys).anyMatch(key -> key.keys.stream().anyMatch(pressed::get));
    }

    @Override
    public boolean keyTyped (char character) {
        return false;
    }

    @Override
    public boolean touchDown (int x, int y, int pointer, int button) {
        return false;
    }

    @Override
    public boolean touchUp (int x, int y, int pointer, int button) {
        return false;
    }

    @Override
    public boolean touchDragged (int x, int y, int pointer) {
        return false;
    }

    @Override
    public boolean mouseMoved(int screenX, int screenY) {
        return false;
    }

    @Override
    public boolean scrolled (int amount) {
        return false;
    }

    public void update() {
        if (isPressed(Key.LEFT_KEY)) {
            actor.moveLeft();
        }
        else if (isPressed(Key.RIGHT_KEY)) {
            actor.moveRight();
        }
    }
}
