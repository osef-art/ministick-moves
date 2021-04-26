package com.mygdx.moves.world;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.Rectangle;
import com.mygdx.moves.controller.Controller;
import com.mygdx.moves.renderer.Animated;

import java.util.ArrayList;

import static com.mygdx.moves.MainScreen.controller;
import static com.mygdx.moves.MainScreen.sra;

public class World implements Animated {
    private final Ministick ministick = new Ministick();
    private static final ArrayList<Hitbox> hitboxes = new ArrayList<>();
    public static final Rectangle leftWall = new Rectangle(-25, 0, 50, 480);
    public static final Rectangle rightWall = new Rectangle(480-25, 0, 50, 480);
    public static final Rectangle ground = new Rectangle(-25, 480-25, 580, 150);
    private final Object box = new Object(280, 240, 50, 50, 0.25f);

    public World() {
        controller = new Controller(ministick);
        Gdx.input.setInputProcessor(controller);
    }

    public static void addHitbox(Hitbox hitbox) {
        hitboxes.add(hitbox);
    }

    @Override
    public void update() {
        hitboxes.forEach(h -> {
            if (!h.hasExpired() && h.hits(box)) {
                h.hit(box);
                h.expire();
            }
        });
        hitboxes.removeIf(Hitbox::isOver);
        ministick.update();
        box.update();
    }

    @Override
    public void render() {
        sra.drawRect(ground.x, ground.y - 5, ground.width, ground.height, Color.GRAY);
        sra.drawRect(leftWall, Color.GRAY);
        sra.drawRect(rightWall, Color.GRAY);
        hitboxes.forEach(Hitbox::render);
        ministick.render();
        box.render();
    }
}
