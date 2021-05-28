package com.mygdx.moves.world;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.mygdx.moves.controller.Controller;
import com.mygdx.moves.renderer.AnimatedSprite;
import com.mygdx.moves.sound.SoundStream;

import java.util.Arrays;

import static com.mygdx.moves.MainScreen.controller;
import static com.mygdx.moves.MainScreen.spra;
import static com.mygdx.moves.world.World.*;

public class Ministick extends Object {
    private final SoundStream stream = new SoundStream();
    private final Sprite star = new Sprite(new Texture("android/assets/icons/star.png"));
    private AnimatedSprite sprite;
    private boolean lookingLeft;
    private State state;

    public Ministick() {
        super(240, ground.y,120, 120, 1);
        setState(State.IDLE);
        star.flip(false, true);
    }

    public boolean isLookingLeft() {
        return lookingLeft;
    }

    private boolean stateIs(State... states) {
        return Arrays.stream(states).anyMatch(s -> s == state);
    }

    private boolean stateIsNot(State... states) {
        return Arrays.stream(states).noneMatch(s -> s == state);
    }

    private void setState(State state) {
        if (state == this.state) return;
        this.state = state;
        sprite = state.sprite();
    }

    int dir() {
        return lookingLeft ? -1 : 1;
    }

    void addX(int value) {
        super.addX(value * dir());
    }

    private void addXAcc(float x) {
        super.addAcc(x * dir(), 0);
    }

    @Override
    void addAcc(float x, float y) {
        super.addAcc(x * dir(), y);
    }

    // moves

    private void turnBack(boolean left) {
        if (lookingLeft == left) return;
        lookingLeft = left;
    }

    private void move(boolean left) {
        if (stateIs(State.CLINGING)) return;
        if (!state.isAttack() && stateIsNot(State.WALL_JUMP)) turnBack(left);

        switch (state) {
            case FALLING:
            case WALL_JUMP:
            case AIR_SMASH:
            case ACTIVE_FALLING:
                super.addAcc((left ? -1 : 1) * 0.5f, 0);
                break;
            case JUMP:
            case AIR_KICK:
            case RUNNING:
            case AIR_UPPERCUT:
                super.addAcc((left ? -1 : 1), 0);
                break;
        }
    }

    public void moveLeft() {
        move(true);
    }

    public void moveRight() {
        move(false);
    }

    public void walk() {
        if (stateIs(State.IDLE)) setState(State.RUNNING);
    }

    public void stopWalking() {
        if (stateIs(State.RUNNING)) setState(State.IDLE);
    }

    public void jump() {
        if (!sprite.hasExceeded(state.comboWindow())) return;

        switch (state) {
            case JUMP:
            case FALLING:
            case WALL_JUMP:
            case ACTIVE_FALLING:
                return;
            case CLINGING:
                stream.play("jump");
                setState(State.WALL_JUMP);
                lookingLeft = !lookingLeft;
                addXAcc(15);
                addX((int) size.x / 4);
                setYAcc(-20);
                break;
            default:
                stream.play("jump");
                addAcc(0, -30);
                setState(State.JUMP);
        }
    }

    public void squat() {
        if (!sprite.hasExceeded(state.comboWindow())) return;
        switch (state) {
            case IDLE:
            case RUNNING:
                setState(State.SQUAT);
        }
    }

    public void getUp() {
        if (!sprite.hasExceeded(state.comboWindow())) return;
        if (state == State.SQUATTING) {
            setState(State.GET_UP);
        }
    }

    // attacks

    public void punch() {
        if (!sprite.hasExceeded(state.comboWindow())) return;

        switch (state) {
            case JUMP:
            case WALL_JUMP:
            case ACTIVE_FALLING:
                setYAcc(-20);
                setState(State.AIR_UPPERCUT);
                break;
            case LOW_KICK:
            case ROTATING_KICK:
                setState(State.REVERSE_PUNCH);
                break;
            case REVERSE_PUNCH:
                setState(State.ENHANCED_PUNCH);
                break;
            case IDLE:
            case SIDE_KICK:
                setState(State.PUNCH);
                break;
            case MID_KICK:
                setState(State.SLIDING_UPPERCUT);
        }
        stream.play("whoosh");
    }

    public void leftSidePunch() {
        sidePunch(true);
    }

    public void rightSidePunch() {
        sidePunch(false);
    }

    private void sidePunch(boolean left) {
        if (!sprite.hasExceeded(state.comboWindow())) return;
        stream.play("whoosh");
        turnBack(left);

        switch (state) {
            case JUMP:
            case WALL_JUMP:
            case ACTIVE_FALLING:
                setYAcc(-20);
                setState(State.AIR_UPPERCUT);
                break;
            case RUNNING:
            case IDLE:
            case SIDE_KICK:
            case REVERSE_PUNCH:
                setState(State.ENHANCED_PUNCH);
                break;
            case ENHANCED_PUNCH:
                setState(State.DOUBLE_PUNCH);
                break;
            case MID_KICK:
                setState(State.SLIDING_UPPERCUT);
                break;
            case LOW_KICK:
            case ROTATING_KICK:
                setState(State.REVERSE_PUNCH);
                break;
        }
    }

    public void downPunch() {
        if (!sprite.hasExceeded(state.comboWindow())) return;
        stream.play("whoosh");

        switch (state) {
            case JUMP:
            case WALL_JUMP:
                setState(State.AIR_SMASH);
                setYAcc(isFalling() ? -15 : -10);
                break;
            case SQUAT:
            case SWEEPER:
            case SQUATTING:
                setState(State.UPPERCUT);
        }
    }

    public void kick() {
        if (!sprite.hasExceeded(state.comboWindow())) return;
        stream.play("whoosh");

        switch (state) {
            case JUMP:
            case WALL_JUMP:
                addAcc(0, -5);
                setState(State.AIR_KICK);
                break;
            case ENHANCED_PUNCH:
                setState(State.BACKUP_KICK);
                break;
            case BACKUP_KICK:
                setState(State.BACKUP_KICK_2);
                break;
            case IDLE:
                setState(State.LOW_KICK);
                break;
            case SIDE_KICK:
                addXAcc(5);
                setState(State.BACK_KICK);
        }
    }

    public void leftSideKick() {
        sideKick(true);
    }

    public void rightSideKick() {
        sideKick(false);
    }

    private void sideKick(boolean left) {
        if (!sprite.hasExceeded(state.comboWindow())) return;
        stream.play("whoosh");
        turnBack(left);

        switch (state) {
            case JUMP:
            case WALL_JUMP:
                addAcc(0, -5);
                setState(State.AIR_KICK);
                break;
            case ENHANCED_PUNCH:
                setState(State.MID_KICK);
                break;
            case RUNNING:
            case IDLE:
                setState(State.SIDE_KICK);
        }
    }

    public void downKick() {
        if (!sprite.hasExceeded(state.comboWindow())) return;
        stream.play("whoosh");

        switch (state) {
            case SQUAT:
            case IDLE:
            case SQUATTING:
                addXAcc(1);
                setState(State.SWEEPER);
                break;
            case ENHANCED_PUNCH:
                addAcc(2.5f, -10);
                setState(State.ROTATING_KICK);
        }
    }

    private void addHitbox(Hitbox hitbox) {
        World.addHitbox(hitbox);
    }


    // update

    void applyGravityAndFriction() {
        if (-0.5 < acceleration.y && acceleration.y < 0) {
            acceleration.y = 1;
        }
        acceleration.x *= 0.9;
        acceleration.y *= isFalling() ? 1.01/*1.1*/ : 0.9;
        acceleration.y += (stateIs(State.CLINGING) ? 0.1f : 0.5f) * weight;
    }

    void cling() {
        if (position.x < leftWall.x + size.x / 4 + leftWall.width) {
            if (position.y < ground.y && stateIs(State.JUMP, State.FALLING, State.ACTIVE_FALLING)) setState(State.CLINGING);
            position.x = leftWall.x + size.x / 4 + leftWall.width;
        }
        if (position.x > rightWall.x - size.x / 4) {
            if (position.y < ground.y && stateIs(State.JUMP, State.FALLING, State.ACTIVE_FALLING)) setState(State.CLINGING);
            position.x = rightWall.x - size.x / 4;
        }
    }

    void land() {
        if (position.y >= ground.y) {
            position.y = ground.y;
            acceleration.y = -Math.abs(acceleration.y) * 0.25f;

            if (acceleration.y < 0.5) {
                acceleration.y = 0;
            }

            if (stateIs(State.JUMP, State.FALLING, State.ACTIVE_FALLING, State.AIR_KICK, State.AIR_UPPERCUT)) {
                setState(State.GET_UP);
            } else if (stateIs(State.CLINGING)) {
                lookingLeft = !lookingLeft;
                addX(5);
                setState(State.GET_UP);
            }
        }
    }

    private void triggerHitbox() {
        State.attacks().forEach(s -> {
            if (state == s && sprite.frame() == s.hitFrame()) {
                addHitbox(s.hitbox(this));
            }
        });
    }

    private void updateFrame() {
        switch (state) {
            case MID_KICK:
            case SIDE_KICK:
            case ENHANCED_PUNCH:
            case REVERSE_PUNCH:
                if (sprite.frame() == 3) addXAcc(5);
                break;

            case DOUBLE_PUNCH:
                if (sprite.frame() == 4) {
                    addX(25);
                    addXAcc(7.5f);
                }
                break;

            case BACKUP_KICK:
                if (sprite.frame() == 3) addXAcc(1);
                break;

            case UPPERCUT:
            case SLIDING_UPPERCUT:
                if (sprite.frame() == 3) addAcc(5, -3);
                break;

            case BACKUP_KICK_2:
                if (sprite.frame() == 4) addAcc(0, -10);
                break;
        }
    }

    private void updateState() {
        if (!sprite.isOver()) return;

        switch (state) {
            case LOW_KICK:
                setState(State.LOW_KICK_EASE);
                break;
            case ENHANCED_PUNCH:
            case PUNCH:
                setState(State.PUNCH_EASE);
                break;
            case DOUBLE_PUNCH:
                setState(State.DOUBLE_PUNCH_EASE);
                break;
            case BACKUP_KICK:
                setState(State.BACKUP_KICK_EASE);
                break;
            case ROTATING_KICK:
                setState(State.ROTATING_KICK_EASE);
                break;
            case BACK_KICK:
                setState(State.BACK_KICK_EASE);
                break;

            case WALL_JUMP:
                cling();
                setState(isLanded() ? State.GET_UP : State.ACTIVE_FALLING);
                break;
            case JUMP:
            case AIR_UPPERCUT:
            case AIR_SMASH:
                cling();
                setState(isLanded() ? State.GET_UP : State.FALLING);
                break;
            case SQUAT:
            case SWEEPER:
                setState(controller.isPressed(Controller.Key.DOWN_KEY) ? State.SQUATTING : State.GET_UP);
                break;
            case IDLE:
                setState(controller.isPressed(Controller.Key.UP_KEY) ? State.JUMP : State.IDLE);
                break;
            case BACKUP_KICK_2:
                addX(25);
                setState(landingState());
                break;
            case UPPERCUT:
                addX(15);
                setState(landingState());
                break;
            case SLIDING_UPPERCUT:
                addX(25);
            default:
                setState(landingState());
        }
    }

    private State landingState() {
        if (controller.isPressed(Controller.Key.DOWN_KEY)) {
            return State.SQUAT;
        }
        else if (controller.isPressed(Controller.Key.LEFT_KEY, Controller.Key.RIGHT_KEY)) {
            return State.RUNNING;
        }
        return State.IDLE;
    }

    @Override
    public void update() {
        super.update();
        if (sprite.updateIfPossible()) {
            updateFrame();
            triggerHitbox();
        }
        updateState();
    }

    @Override
    public void render() {
        sprite.render(position.x - size.x / 2, position.y - size.y, size.x, size.y, lookingLeft);

        if (sprite.hasExceeded(state.comboWindow())) {
            spra.render(star, position.x - 10, position.y + 5, 20, 20);
        }
    }
}
