package com.mygdx.moves.world;

public class RelativeHitbox {
    private final float x, y, width, height, vx, vy;

    RelativeHitbox(float x, float y, float width, float height, float vx, float vy) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
        this.vx = vx;
        this.vy = vy;
    }

    public float x() {
        return x;
    }

    public float y() {
        return y;
    }

    public float width() {
        return width;
    }

    public float height() {
        return height;
    }

    public float vx() {
        return vx;
    }

    public float vy() {
        return vy;
    }
}