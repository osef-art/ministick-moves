package com.mygdx.moves.world;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.Vector2;
import com.mygdx.moves.renderer.Animated;

import static com.mygdx.moves.MainScreen.sra;
import static com.mygdx.moves.world.World.*;
import static com.mygdx.moves.world.World.leftWall;

public class Object implements Animated {
    final float weight;
    final Vector2 acceleration;
    final Vector2 position;
    final Vector2 size;

    public Object(float x, float y, float width, float height, float weight) {
        acceleration = new Vector2(0, 0);
        position = new Vector2(x, y);
        size = new Vector2(width, height);
        this.weight = weight;
    }

    boolean isLanded() {
        return acceleration.y == 0;
    }

    boolean isFalling() {
        return acceleration.y > 0;
    }

    void addX(int value) {
        position.add(value, 0);
    }

    void setYAcc(float value) {
        acceleration.y = value;
    }

    void addAcc(float x, float y) {
        acceleration.add(x, y);
    }

    void applyGravityAndFriction() {
        if (-0.5 < acceleration.y && acceleration.y < 0) {
            acceleration.y = 1;
        }
        acceleration.x *= 0.9;
        acceleration.y *= isFalling() ? 1.1 : 0.9;
        acceleration.y += 0.5f * weight;
    }

    void cling() {
        if (position.x <= leftWall.x + size.x / 2 + leftWall.width) {
            acceleration.x = Math.abs(acceleration.x) * 2;
            position.x = leftWall.x + size.x / 2 + leftWall.width;
        }
        if (position.x >= rightWall.x - size.x / 2) {
            acceleration.x = -Math.abs(acceleration.x) * 2;
            position.x = rightWall.x - size.x / 2;
        }
    }

    void land() {
        if (position.y >= ground.y) {
            position.y = ground.y;
            acceleration.y = -Math.abs(acceleration.y) * 0.25f;
        }
    }

    @Override
    public void render() {
        sra.drawRect(position.x - size.x / 2, position.y - size.y, size.x, size.y, Color.LIGHT_GRAY);
    }

    @Override
    public void update() {
        position.x += acceleration.x;
        position.y += acceleration.y;
        applyGravityAndFriction();
        cling();
        land();
    }
}
