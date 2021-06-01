package com.mygdx.moves.world;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.Rectangle;
import com.mygdx.moves.controller.InputHandler;
import com.mygdx.moves.renderer.Animated;

import java.util.ArrayList;

import static com.mygdx.moves.MainScreen.inputHandler;
import static com.mygdx.moves.MainScreen.sra;

public class World implements Animated {
    private final Ministick ministick = new Ministick();
    private final static float wallWidth = 50;
    private static final ArrayList<Hitbox> hitboxes = new ArrayList<>();
    public static final Rectangle leftWall = new Rectangle( - wallWidth / 2, 0, wallWidth, Gdx.graphics.getHeight());
    public static final Rectangle rightWall = new Rectangle(Gdx.graphics.getHeight() - wallWidth / 2, 0, wallWidth, Gdx.graphics.getHeight());
    public static final Rectangle ground = new Rectangle( - wallWidth / 2, Gdx.graphics.getHeight() - wallWidth / 2, Gdx.graphics.getWidth(), 100);
    private final Object box = new Object(280, 240, 50, 50, 0.25f);

    public World() {
        inputHandler = new InputHandler(ministick);
        Gdx.input.setInputProcessor(inputHandler);
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
//        hitboxes.forEach(Hitbox::render);
        ministick.render();
        box.render();
    }
}
