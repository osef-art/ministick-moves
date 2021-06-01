package com.mygdx.moves.world;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.mygdx.moves.renderer.AnimatedSprite;
import com.mygdx.moves.sound.SoundStream;

import java.util.Arrays;

import static com.mygdx.moves.MainScreen.spra;
import static com.mygdx.moves.world.World.*;

public class Ministick extends Object {
    private final SoundStream stream = new SoundStream();
    private final Sprite star = new Sprite(new Texture("android/assets/icons/star.png"));
    private AnimatedSprite sprite;
    private boolean lookingLeft;
    private boolean goingDown;
    private boolean jumping;
    private boolean moving;
    private State state;

    public Ministick() {
        super(240, ground.y,120, 120, 1);
        setState(State.IDLE);
        star.flip(false, true);
    }

    public boolean isLookingLeft() {
        return lookingLeft;
    }

    private boolean stateIs(State ... states) {
        return Arrays.stream(states).anyMatch(s -> s == state);
    }

    private boolean stateIsNot(State ... states) {
        return Arrays.stream(states).noneMatch(s -> s == state);
    }

    private void setState(State state) {
        if (state == this.state) return;
        this.state = state;
        sprite = state.sprite();
    }

    private boolean setToStateFollowing(FollowUps.MoveInput move) {
        if (state.getStateFollowing(move).isPresent()) {
            setState(state.getStateFollowing(move).get());
            return true;
        }
        state.getStateFollowing(FollowUps.MoveInput.NONE).ifPresent(this::setState);
        return false;
    }

    private boolean setToStateFollowingIfOver(FollowUps.MoveInput move) {
        return sprite.hasExceeded(state.comboWindow()) && setToStateFollowing(move);
    }

    private boolean setToStateFollowingIfOver(FollowUps.MoveInput move, String soundName) {
        if (setToStateFollowingIfOver(move)) {
            stream.play(soundName);
            return true;
        }
        return false;
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

    private void jump() {
        if (stateIs(State.JUMP, State.WALL_JUMP)) return;
        setToStateFollowingIfOver(FollowUps.MoveInput.JUMP, "jump");

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

    public void moveLeft() {
        moveX(true);
    }

    public void moveRight() {
        moveX(false);
    }

    public void startRunning() {
        moving = true;
        if (stateIs(State.IDLE)) setState(State.RUNNING);
    }

    public void stopRunning() {
        moving = false;
        if (stateIs(State.RUNNING)) setState(State.IDLE);
    }

    public void startJumping() {
        jumping = true;
        jump();
    }

    public void stopJumping() {
        jumping = false;
    }

    public void startGoingDown() {
        goingDown = true;
        setToStateFollowingIfOver(FollowUps.MoveInput.DOWN);
    }

    public void stopGoingDown() {
        goingDown = false;
        if (stateIs(State.SQUATTING)) setState(State.GET_UP);
    }


    // attacks

    public void punch() {
        if (!setToStateFollowingIfOver(FollowUps.MoveInput.PUNCH, "whoosh")) return;

        // if state has changed
        if (stateIs(State.AIR_UPPERCUT)) {
            setYAcc(-20);
        }
    }

    private void sidePunch(boolean left) {
        turnBack(left);
        setToStateFollowingIfOver(FollowUps.MoveInput.SIDE_PUNCH, "whoosh");

        if (stateIs(State.AIR_UPPERCUT)) {
            setYAcc(-20);
        }
    }

    public void downPunch() {
        setToStateFollowingIfOver(FollowUps.MoveInput.DOWN_PUNCH, "whoosh");

        if (stateIs(State.AIR_SMASH)) {
            setYAcc(isFalling() ? -15 : -10);
        }
    }

    public void kick() {
        setToStateFollowingIfOver(FollowUps.MoveInput.KICK, "whoosh");

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
        setToStateFollowingIfOver(FollowUps.MoveInput.SIDE_KICK, "whoosh");

        if (stateIs(State.AIR_KICK))
            addAcc(0, -5);
    }

    public void downKick() {
        setToStateFollowingIfOver(FollowUps.MoveInput.DOWN_KICK, "whoosh");

        switch (state) {
            case SWEEPER:
                addXAcc(1);
                break;
            case ROTATING_KICK:
                addAcc(2.5f, -0);
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
                setState(goingDown ? State.SQUAT : State.GET_UP);
                if (jumping) jump();
            } else if (stateIs(State.CLINGING)) {
                lookingLeft = !lookingLeft;
                addX(5);
                setState(State.GET_UP);
            }
        }
    }

    private void triggerHitbox() {
        State.attacks().forEach(s -> {
            if (stateIs(s) && sprite.frame() == s.hitFrame()) {
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

        adjustPosition();
        setToStateFollowing(currentInput());

        if (state == State.IDLE) {
            if (moving) startRunning();
        }
    }

    private void adjustPosition() {
        switch (state) {
            case BACKUP_KICK_2:
                addX(25);
                break;
            case UPPERCUT:
            case SLIDING_UPPERCUT:
                addX(15);
                break;
        }
    }

    private FollowUps.MoveInput currentInput() {
        if (goingDown) return FollowUps.MoveInput.DOWN;
        if (jumping) return FollowUps.MoveInput.JUMP;
        return FollowUps.MoveInput.NONE;
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
