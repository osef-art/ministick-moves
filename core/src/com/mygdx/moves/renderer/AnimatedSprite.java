package com.mygdx.moves.renderer;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.mygdx.moves.time.Timer;

import java.nio.file.Path;
import java.util.Random;

import static com.mygdx.moves.MainScreen.spra;


public class AnimatedSprite implements Animated, NonStatic {
    private final Timer updateTimer;
    private final Sprite[] rightSprites;
    private final Sprite[] leftSprites;
    private final boolean looping;
    private final int nbFrames;
    private Path path;
    private int frame;

    public AnimatedSprite(String path, int frames, double speed) {
        this(path, new Random().nextInt(frames), frames, speed, true);
    }

    public AnimatedSprite(String path, int startingFrame, int frames, double speed, boolean looping) {
        this.looping = looping;
        updateTimer = new Timer(speed);
        rightSprites = new Sprite[frames];
        leftSprites = new Sprite[frames];
        frame = startingFrame;
        nbFrames = frames;

        updatePath(path);
    }

    public int frame() {
        return frame;
    }

    public static AnimatedSprite oneShot(String path, int frame, double speed) {
        return new AnimatedSprite(path, 0, frame, speed, false);
    }

    private Sprite currentSprite(boolean left) {
        return left ? leftSprites[frame] : rightSprites[frame];
    }

    public void updatePath(String path) {
        this.path = Path.of(path);
        updateSprites();
    }

    private void updateSprites() {
        for (int i = 0; i < nbFrames; i++) {
            var lSprite = new Sprite(new Texture(path.toString() + (nbFrames >= 9 && i < 10 ? "0" : "") + i + ".png"));
            var rSprite = new Sprite(new Texture(path.toString() + (nbFrames >= 9 && i < 10 ? "0" : "") + i + ".png"));
            rSprite.flip(false, true);
            lSprite.flip(true, true);
            rightSprites[i] = rSprite;
            leftSprites[i] = lSprite;
        }
    }

    @Override
    public void update() {
        if (isAtLastFrame() && !looping) return;
        if (updateTimer.resetIfExceeds()) frame = (frame + 1) % nbFrames;
    }

    public boolean updateIfPossible() {
        if (isAtLastFrame() && !looping) return false;
        if (updateTimer.resetIfExceeds()) {
            frame = (frame + 1) % nbFrames;
            return true;
        }
        return false;
    }

    @Override
    public void render() {
        spra.render(currentSprite(false));
    }

    @Override
    public void render(float x, float y, float width, float height) {
        spra.render(currentSprite(false), x, y, width, height);
    }

    public void render(float x, float y, float width, float height, boolean left) {
        spra.render(currentSprite(left), x, y, width, height);
    }

    private boolean isAtLastFrame() {
        return frame == nbFrames - 1;
    }

    public boolean isOver() {
        return isAtLastFrame() && updateTimer.isExceeded();
    }

    public boolean hasExceeded(int frame) {
        return this.frame >= frame;
    }
}
