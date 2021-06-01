package com.mygdx.moves.world;

import com.mygdx.moves.renderer.AnimatedSprite;

import java.util.*;
import java.util.stream.Collectors;

public enum State {
        // IDLE
    IDLE("stand", 100_000_000, 6),
    CLINGING("clinging", 500_000_000, 2),
    RUNNING("walking", 50_000_000, 6),
    FALLING("falling", 75_000_000, 4),
    ACTIVE_FALLING("falling", 100_000_000, 1), // tmp
    SQUATTING("squatting", 75_000_000, 8),
    // TODO: running ease ?

    // ONE SHOTS

    // MOVES
    WALL_JUMP("walljump", 125_000_000, 4, 0),
    JUMP("jumping", 100_000_000, 6, 0),

    // EASE
    ROTATING_KICK_EASE("splkick-e", 25_000_000, 6, true),
    DOUBLE_PUNCH_EASE("s2punch-e", 25_000_000, 4, true),
    BACKUP_KICK_EASE("backkick-e", 25_000_000, 6, true),
    BACK_KICK_EASE("2backkick-e", 25_000_000, 6, true),
    LOW_KICK_EASE("nkick-e", 25_000_000, 6, true),
    PUNCH_EASE("npunch-e", 25_000_000, 6, true),
    GET_UP("getup", 30_000_000, 4, 0),
    SQUAT("squat", 25_000_000, 8, 0),
    // TODO: SLIDE

        // ATTACKS

    // PUNCHES
    PUNCH("npunch", 30_000_000, 6, 3, 3, new RelativeHitbox(30, 60, 30, 15, 5, 0)),
    REVERSE_PUNCH("n2punch", 50_000_000, 10, 4, 5, new RelativeHitbox(30, 60, 40, 20, 7.5f, 0)),
    ENHANCED_PUNCH("npunch", 75_000_000, 6, 3,  3, new RelativeHitbox(30, 60, 40, 20, 5, 0)),
    DOUBLE_PUNCH("s2punch", 50_000_000, 10, 5,  10, new RelativeHitbox(50, 60, 40, 20, 10, 0)),
    AIR_SMASH("dapunch", 50_000_000, 10, 5, 10, new RelativeHitbox(0, 20, 60, 60, 5, 50)),
    AIR_UPPERCUT("auppercut", 40_000_000, 10, 5, 10, new RelativeHitbox(-20, 120, 80, 70, 5, -30)),
    UPPERCUT("uppercut", 40_000_000, 14, 5, 8, new RelativeHitbox(20, 120, 40, 80, 5, -30)),
    SLIDING_UPPERCUT("2uppercut", 50_000_000, 12, 3, 6, new RelativeHitbox(20, 120, 60, 60, 5, -30)),
    // TODO: SLAM
    // TODO: HALT
    // TODO: HIGH PUNCH

    // KICKS
    LOW_KICK("nkick", 20_000_000, 12, 8, 8, new RelativeHitbox(30, 30, 30, 30, 2.5f, -5)),
    SIDE_KICK("skick", 40_000_000, 8, 4, 4, new RelativeHitbox(40, 80, 40, 60, 10, 5)),
    MID_KICK("s2kick", 60_000_000, 8, 2, 2, new RelativeHitbox(30, 40, 40, 20, 5, -10)),
    BACK_KICK("2backkick", 50_000_000, 8, 4, 8, new RelativeHitbox(20, 50, 50, 20, 10, 0)),
    ROTATING_KICK("splkick", 35_000_000, 12, 7, 7, new RelativeHitbox(40, 70, 40, 80, 5, -15)),
    BACKUP_KICK("backkick", 60_000_000, 8, 4, 4, new RelativeHitbox(40, 70, 40, 80, 0, -20)),
    BACKUP_KICK_2("backupkick", 40_000_000, 12, 6, 12, new RelativeHitbox(0, 120, 60, 60, 2.5f, -25)),
    AIR_KICK("airkick", 40_000_000, 8, 4, 8, new RelativeHitbox(30, 50, 50, 40, 15, -5)),
    SWEEPER("sweeper", 30_000_000, 10, 5, 6, new RelativeHitbox(30, 10, 40, 20, 2.5f, -10)),
    // TODO: SPINNING AIR KICK
    // TODO: GROUND_POUND
    ;

    private final FollowUps followUps = FollowUps.empty();
    private final RelativeHitbox hitbox;
    private final int comboWindow;
    private final int hitFrame;
    private final int nbFrames;
    private final double speed;
    private final String path;

    State(String path, double speed, int frames) {
        this(path, speed, frames, -1);
    }

    State(String path, double speed, int frames, boolean easing) {
        this(path, speed, frames, -1, frames, null);
    }

    State(String path, double speed, int frames, int window) {
        this(path, speed, frames, -1, window, null);
    }

    State(String path, double speed, int frames, int hitFrame, int window, RelativeHitbox hitbox) {
        nbFrames = frames;
        this.path = "android/assets/sprites/" + path;
        this.comboWindow = window;
        this.hitFrame = hitFrame;
        this.hitbox = hitbox;
        this.speed = speed;
    }

    public static void elaborateMovesAutomata() {
        // IDLE
        IDLE.followUps
          .onSide(RUNNING)
          .onDown(SQUAT)
          .onJump(JUMP)
          .onPunch(PUNCH)
          .onKick(LOW_KICK)
          .onSidePunch(ENHANCED_PUNCH)
          .onSideKick(SIDE_KICK)
        ;

        CLINGING.followUps
          .onJump(WALL_JUMP)
          .onRelease(ACTIVE_FALLING)
        ;

        RUNNING.followUps
          .onDown(SQUAT)
          .onJump(JUMP)
          .onSidePunch(ENHANCED_PUNCH)
          .onSideKick(SIDE_KICK)
          .onRelease(IDLE) // tmp
        ;

        SQUATTING.followUps
          .onDownPunch(UPPERCUT)
          .onDownKick(SWEEPER)
          .onJump(JUMP)
          .onRelease(GET_UP)
        ;

        ACTIVE_FALLING.followUps
          .onKick(AIR_KICK)
          .onPunch(AIR_UPPERCUT)
          .onDownPunch(AIR_SMASH)
          .isAerial()
        ;

        FALLING.followUps.onRelease(GET_UP);

        // MOVES
        SQUAT.followUps
          .onDownPunch(UPPERCUT)
          .onDownKick(SWEEPER)
          .onDown(SQUATTING)
          .onRelease(GET_UP)
        ;

        JUMP.followUps
          .onKick(AIR_KICK)
          .onPunch(AIR_UPPERCUT)
          .onDownPunch(AIR_SMASH)
          .isAerial()
        ;

        WALL_JUMP.followUps
          .onKick(AIR_KICK)
          .onPunch(AIR_UPPERCUT)
          .onDownPunch(AIR_SMASH)
          .isAerial()
        ;

        // PUNCHES
        PUNCH.followUps.onRelease(PUNCH_EASE);

        DOUBLE_PUNCH.followUps.onRelease(DOUBLE_PUNCH_EASE);

        REVERSE_PUNCH.followUps
          .onSidePunch(ENHANCED_PUNCH)
          .onRelease(PUNCH_EASE)
        ;

        ENHANCED_PUNCH.followUps
          .onSidePunch(DOUBLE_PUNCH)
          .onKick(BACKUP_KICK)
          .onSideKick(MID_KICK)
          .onDownKick(ROTATING_KICK)
          .onRelease(PUNCH_EASE)
        ;

        SLIDING_UPPERCUT.followUps.onRelease(IDLE);

        UPPERCUT.followUps.onRelease(IDLE);

        AIR_UPPERCUT.followUps.isAerial();

        AIR_SMASH.followUps.isAerial();

        // KICKS
        LOW_KICK.followUps
          .onPunch(REVERSE_PUNCH)
          .onSidePunch(REVERSE_PUNCH)
          .onRelease(LOW_KICK_EASE)
        ;

        SIDE_KICK.followUps
          .onSideKick(BACK_KICK)
          .onRelease(IDLE)
        ;

        MID_KICK.followUps
          .onPunch(SLIDING_UPPERCUT)
          .onDownPunch(SLIDING_UPPERCUT)
          .onRelease(IDLE)
        ;

        BACK_KICK.followUps.onRelease(BACK_KICK_EASE);

        ROTATING_KICK.followUps.onRelease(ROTATING_KICK_EASE);

        BACKUP_KICK.followUps
          .onKick(BACKUP_KICK_2)
          .onDownKick(ROTATING_KICK)
          .onRelease(BACKUP_KICK_EASE)
        ;

        BACKUP_KICK_2.followUps.onRelease(IDLE);

        SWEEPER.followUps
          .onDown(SQUATTING)
          .onDownPunch(UPPERCUT)
          .onRelease(GET_UP)
        ;

        AIR_KICK.followUps.isAerial();

        // EASE
        ROTATING_KICK_EASE.followUps.isEase();

        DOUBLE_PUNCH_EASE.followUps.isEase();

        BACKUP_KICK_EASE.followUps.isEase();

        BACK_KICK_EASE.followUps.isEase();

        LOW_KICK_EASE.followUps.isEase();

        PUNCH_EASE.followUps.isEase();

        GET_UP.followUps
          .onSide(RUNNING)
          .onDown(SQUAT)
          .onJump(JUMP)
          .isEase()
        ;

        checkAutomataValidity();
    }

    private static void checkAutomataValidity() {
        Arrays.stream(State.values())
          .filter(s -> s.followUps.isEmpty())
          .findFirst()
          .ifPresent(s -> {
              throw new IllegalStateException(s + " is not used !");
          });

        Set<State> accessibleStates = Arrays.stream(State.values())
                                        .flatMap((state -> state.followUps.stream()))
                                        .collect(Collectors.toSet());

        Arrays.stream(State.values())
          .filter(s -> !accessibleStates.contains(s) && !Objects.equals(CLINGING, s))
          .findFirst()
          .ifPresent(s -> {
              throw new IllegalStateException(s + " is not accessible by any state !");
          });
    }

    public boolean isAttack() {
        return hitFrame != -1;
    }

    public boolean isEasing() {
        return nbFrames == comboWindow;
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
          s.position.x + (s.isLookingLeft() ? -hitbox.x() - hitbox.width() : hitbox.x()),
          s.position.y - hitbox.y(),
          hitbox.width(), hitbox.height(),
          hitbox.vx() * s.dir(), hitbox.vy()
        );
    }
    public Optional<State> getStateFollowing(FollowUps.MoveInput move) {
        return Optional.ofNullable(followUps.on(move));
    }
}