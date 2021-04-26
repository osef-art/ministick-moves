package com.mygdx.moves.world;

import com.mygdx.moves.renderer.AnimatedSprite;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

enum State {
        // IDLE
    CLINGING("clinging", 500_000_000, 2),
    STANDING("stand", 100_000_000, 6),
    SQUATTING("squatting", 75_000_000, 8),
    FALLING("falling", 75_000_000, 4),
    WALKING("walking", 50_000_000, 6),
    // TODO: walking ease ?

        // ONE SHOTS

    // MOVES
    WALL_JUMP("walljump", 125_000_000, 4, 0),
    JUMPING("jumping", 100_000_000, 6, 0),
    // EASE
    SPINNING_LOW_KICK_EASE("splkick-e", 25_000_000, 6, 6),
    NEUTRAL_PUNCH_EASE("npunch-e", 25_000_000, 6, 6),
    NEUTRAL_KICK_EASE("nkick-e", 25_000_000, 6, 6),
    SIDE_PUNCH_2_EASE("s2punch-e", 25_000_000, 4, 4),
    BACK_KICK_2_EASE("2backkick-e", 25_000_000, 6, 6),
    BACK_KICK_EASE("backkick-e", 25_000_000, 6, 6),
    GET_UP("getup", 15_000_000, 4, 0),
    SQUAT("squat", 25_000_000, 8, 0),

        // ATTACKS

    // PUNCHES
    NEUTRAL_PUNCH("npunch", 30_000_000, 6, 3, 3, new RelativeHitbox(30, 60, 30, 15, 5, 0)),
    NEUTRAL_PUNCH2("n2punch", 50_000_000, 10, 4, 5, new RelativeHitbox(30, 60, 40, 20, 7.5f, 0)),
    SIDE_PUNCH("npunch", 60_000_000, 6, 3,  3, new RelativeHitbox(30, 60, 40, 20, 5, 0)),
    SIDE_PUNCH_2("s2punch", 50_000_000, 10, 5,  10, new RelativeHitbox(50, 60, 40, 20, 10, 0)),
    DOWN_AIR_PUNCH("dapunch", 50_000_000, 10, 5, 10, new RelativeHitbox(0, 20, 60, 60, 5, 50)),
    AIR_UPPERCUT("auppercut", 50_000_000, 10, 5, 10, new RelativeHitbox(-20, 120, 80, 70, 5, -30)),
    UPPERCUT("uppercut", 40_000_000, 14, 5, 8, new RelativeHitbox(20, 120, 40, 80, 5, -30)),
    UPPERCUT_2("2uppercut", 50_000_000, 12, 3, 6, new RelativeHitbox(20, 120, 60, 60, 5, -30)),
    // KICKS
    NEUTRAL_KICK("nkick", 20_000_000, 12, 8, 8, new RelativeHitbox(30, 30, 30, 30, 2.5f, -5)),
    SIDE_KICK("skick", 40_000_000, 8, 4, 4, new RelativeHitbox(40, 80, 40, 60, 10, 5)),
    LOW_KICK("s2kick", 60_000_000, 8, 2, 2, new RelativeHitbox(30, 40, 40, 20, 5, -10)),
    SPINNING_LOW_KICK("splkick", 35_000_000, 12, 7, 7, new RelativeHitbox(40, 70, 40, 80, 5, -15)),
    BACK_KICK("backkick", 60_000_000, 8, 4, 4, new RelativeHitbox(40, 70, 40, 80, 0, -20)),
    BACK_KICK_2("2backkick", 50_000_000, 8, 4, 8, new RelativeHitbox(20, 50, 50, 20, 10, 0)),
    BACKUP_KICK("backupkick", 40_000_000, 12, 6, 12, new RelativeHitbox(0, 120, 60, 60, 2.5f, -25)),
    AIR_KICK("airkick", 40_000_000, 8, 4, 8, new RelativeHitbox(30, 50, 50, 40, 15, -5)),
    SWEEPER("sweeper", 30_000_000, 10, 5, 6, new RelativeHitbox(30, 10, 40, 20, 2.5f, -10)),
    // TODO: dair kick
    // TODO: ground pound
    ;

    private static class RelativeHitbox {
        private final float x, y, width, height, vx, vy;
        RelativeHitbox(float x, float y, float width, float height, float vx, float vy) {
            this.x = x;
            this.y = y;
            this.width = width;
            this.height = height;
            this.vx = vx;
            this.vy = vy;
        }
    }

    private final RelativeHitbox hitbox;
    private final int comboWindow;
    private final int hitFrame;
    private final int nbFrames;
    private final double speed;
    private final String path;

    State(String path, double speed, int frames) {
        this(path, speed, frames, -1);
    }

    State(String path, double speed, int frames, int window) {
        this(path, speed, frames, -1, window, null);
    }

    State(String path, double speed, int frames, int hitFrame, int window, RelativeHitbox hitbox) {
        this.path = "android/assets/sprites/" + path;
        nbFrames = frames;
        this.comboWindow = window;
        this.hitFrame = hitFrame;
        this.hitbox = hitbox;
        this.speed = speed;
    }

    public boolean isAttack() {
        return hitFrame != -1;
    }

    public AnimatedSprite sprite() {
        return comboWindow == -1
                 ? new AnimatedSprite(path, nbFrames, speed)
                 : AnimatedSprite.oneShot(path, nbFrames, speed);
    }

    public int comboWindow() {
        return comboWindow;
    }

    public int hitFrame() {
        return hitFrame;
    }

    public static List<State> attacks() {
        return Arrays.stream(values())
          .filter(s -> s.hitFrame != -1)
          .collect(Collectors.toList());
    }

    public Hitbox hitbox(Ministick s) {
        return new Hitbox(
          s.position.x + (s.isLookingLeft() ? -hitbox.x - hitbox.width : hitbox.x),
          s.position.y - hitbox.y,
          hitbox.width, hitbox.height,
          hitbox.vx * s.dir(), hitbox.vy
        );
    }
}