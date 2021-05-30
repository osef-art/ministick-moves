package com.mygdx.moves.world;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.mygdx.moves.controller.InputHandler;
import com.mygdx.moves.renderer.AnimatedSprite;
import com.mygdx.moves.sound.SoundStream;

import java.util.Arrays;

import static com.mygdx.moves.MainScreen.inputHandler;
import static com.mygdx.moves.MainScreen.spra;
import static com.mygdx.moves.world.World.*;

public class Ministick extends Object {
    private final SoundStream stream = new SoundStream();
    private final Sprite star = new Sprite(new Texture("android/assets/icons/star.png"));
    private AnimatedSprite sprite;
    private boolean lookingLeft;
    private boolean jumpMode;
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

    private void setToFollowingStateOn(FollowUps.MoveInput move) {
        if (sprite.hasExceeded(state.comboWindow()))
            state.getStateOn(move).ifPresent(this::setState);
    }

    private void setToFollowingStateOn(FollowUps.MoveInput move, String soundName) {
        setToFollowingStateOn(move);
        stream.play(soundName);
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
        if (stateIs(State.CLINGING) || state.isEasing()) return;
        if (lookingLeft == left) return;
        lookingLeft = left;
    }

    private void moveX(boolean left) {
        if (stateIs(State.CLINGING)) return;

        if (!state.isAttack() && stateIsNot(State.WALL_JUMP, State.FALLING)) {
            turnBack(left);
        }

        switch (state) {
            case WALL_JUMP:
            case AIR_SMASH:
                super.addAcc((left ? -1 : 1) * 0.5f, 0);
                break;
            case FALLING:
            case ACTIVE_FALLING:
                super.addAcc((left ? -1 : 1) * 0.4f, 0);
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
        moveX(true);
    }

    public void moveRight() {
        moveX(false);
    }

    public void startRunning() {
        if (stateIs(State.IDLE)) setState(State.RUNNING);
    }

    public void stopRunning() {
        if (stateIs(State.RUNNING)) setState(State.IDLE);
    }

    public void startJumping() {
        jumpMode = true;
        jump();
    }

    public void stopJumping() {
        jumpMode = false;
    }

    private void jump() {
        if (stateIs(State.JUMP, State.WALL_JUMP)) return;
        setToFollowingStateOn(FollowUps.MoveInput.JUMP, "jump");

        switch (state) {
            case WALL_JUMP:
                lookingLeft = !lookingLeft;
                setYAcc(-20);
                addXAcc(15);
                addX((int) size.x / 4);
                break;
            case JUMP:
                addAcc(0, -30);
        }
    }

    public void squat() {
        setToFollowingStateOn(FollowUps.MoveInput.DOWN);
    }

    public void getUp() {
        if (state == State.SQUATTING) setState(State.GET_UP);
    }

    // attacks

    public void punch() {
        setToFollowingStateOn(FollowUps.MoveInput.PUNCH, "whoosh");

        if (stateIs(State.AIR_UPPERCUT)) {
            setYAcc(-20);
        }
    }

    private void sidePunch(boolean left) {
        turnBack(left);
        setToFollowingStateOn(FollowUps.MoveInput.SIDE_PUNCH, "whoosh");

        if (stateIs(State.AIR_UPPERCUT)) {
            setYAcc(-20);
        }
    }

    public void downPunch() {
        setToFollowingStateOn(FollowUps.MoveInput.DOWN_PUNCH, "whoosh");

        if (stateIs(State.AIR_SMASH)) {
            setYAcc(isFalling() ? -15 : -10);
        }
    }

    public void kick() {
        setToFollowingStateOn(FollowUps.MoveInput.KICK, "whoosh");

        switch (state) {
            case AIR_KICK:
                addAcc(0, -5);
                break;
            case BACK_KICK:
                addXAcc(5);
        }
    }

    private void sideKick(boolean left) {
        turnBack(left);
        setToFollowingStateOn(FollowUps.MoveInput.SIDE_KICK, "whoosh");

        if (stateIs(State.AIR_KICK)) {
            addAcc(0, -5);
        }
    }

    public void downKick() {
        setToFollowingStateOn(FollowUps.MoveInput.DOWN_KICK, "whoosh");

        switch (state) {
            case SWEEPER:
                addXAcc(1);
                break;
            case ROTATING_KICK:
                addAcc(2.5f, -10);
        }
    }

    public void leftSidePunch() {
        sidePunch(true);
    }

    public void rightSidePunch() {
        sidePunch(false);
    }

    public void leftSideKick() {
        sideKick(true);
    }

    public void rightSideKick() {
        sideKick(false);
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
        acceleration.y *= isFalling() ? 1.05 : 0.9;
        acceleration.y += (stateIs(State.CLINGING) ? 0.00f : 0.5f) * weight;
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
                setState(inputHandler.isPressed(InputHandler.Key.DOWN_KEY) ? State.SQUATTING : State.GET_UP);
                break;
            case IDLE:
                setState(inputHandler.isPressed(InputHandler.Key.UP_KEY) ? State.JUMP : State.IDLE);
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
        if (inputHandler.isPressed(InputHandler.Key.DOWN_KEY)) {
            return State.SQUAT;
        }
        else if (inputHandler.isPressed(InputHandler.Key.LEFT_KEY, InputHandler.Key.RIGHT_KEY)) {
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
