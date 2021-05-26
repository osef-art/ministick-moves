package com.mygdx.moves;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.mygdx.moves.controller.Controller;
import com.mygdx.moves.renderer.ShapeRendererAdaptor;
import com.mygdx.moves.renderer.SpriteRendererAdaptor;
import com.mygdx.moves.world.State;
import com.mygdx.moves.world.World;


public class MainScreen extends ApplicationAdapter {
	public static OrthographicCamera camera;
	public static SpriteRendererAdaptor spra;
	public static ShapeRendererAdaptor sra;
	public static Controller controller;

	private World world;

	@Override
	public void create () {
		State.elaborateMovesAutomata();
		camera = new OrthographicCamera();
		camera.setToOrtho(true);

		spra = new SpriteRendererAdaptor();
		sra = new ShapeRendererAdaptor();
		world = new World();
	}

	@Override
	public void render () {
		Gdx.gl.glClearColor(0, 0, 0, 1);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

		controller.update();
		world.update();
		world.render();
	}
	
	@Override
	public void dispose () {
		spra.dispose();
	}
}
