package com.mygdx.moves.world;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.mygdx.moves.renderer.Renderable;
import com.mygdx.moves.sound.SoundStream;
import com.mygdx.moves.time.Timer;

import static com.mygdx.moves.MainScreen.sra;

public class Hitbox implements Renderable {
    private final SoundStream stream = new SoundStream();
    private final Timer timer = new Timer(200_000_000);
    private final Rectangle zone;
    private final Vector2 vector;
    private boolean expired;

    public Hitbox(float x, float y, float width, float height, float vx, float vy) {
        this.zone = new Rectangle(x, y, width, height);
        vector = new Vector2(vx, vy) ;
    }

    public boolean isOver() {
        return timer.isExceeded();
    }

    @Override
    public void render() {
        sra.drawRect(zone, Color.CORAL);
    }

    public boolean hasExpired() {
        return expired;
    }

    public boolean hits(Object object) {
        return zone.overlaps(new Rectangle(
          object.position.x - object.size.x / 2,
          object.position.y - object.size.y,
          object.size.x, object.size.y
        ));
    }

    public void expire() {
        expired = true;
    }

    public void hit(Object object) {
        stream.play("hit");
        object.acceleration.add(vector);
    }

}
