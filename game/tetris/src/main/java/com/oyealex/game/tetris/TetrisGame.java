/*
 * Copyright (c) 2021. oyealex. All rights reserved.
 */

package com.oyealex.game.tetris;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3Application;
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3ApplicationConfiguration;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.utils.viewport.ExtendViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.oyealex.game.tetris.view.screen.TetrisScreen;
import com.oyealex.game.tetris.utils.Resources;
import lombok.Getter;
import org.jetbrains.annotations.NotNull;

import static com.oyealex.game.tetris.utils.Constants.TITLE;
import static com.oyealex.game.tetris.utils.Constants.VIEW_PORT_HEIGHT;
import static com.oyealex.game.tetris.utils.Constants.VIEW_PORT_WIDTH;
import static com.oyealex.game.tetris.utils.Constants.WINDOW_HEIGHT;
import static com.oyealex.game.tetris.utils.Constants.WINDOW_WIDTH;

/**
 * TetrisMain
 *
 * @author oyealex
 * @version 1.0
 * @since 2021-08-12
 */
@Getter
public class TetrisGame extends Game {
    /** 摄像机 */
    private Camera camera;

    /** 视窗 */
    private Viewport viewport;

    /** 批量绘制 */
    private Batch batch;

    /** 游戏屏 */
    private TetrisScreen tetrisScreen;

    public static void main(String[] args) {
        new Lwjgl3Application(new TetrisGame(), buildConfig());
    }

    @NotNull
    private static Lwjgl3ApplicationConfiguration buildConfig() {
        Lwjgl3ApplicationConfiguration config = new Lwjgl3ApplicationConfiguration();
        config.setTitle(TITLE);
        config.setWindowedMode(WINDOW_WIDTH, WINDOW_HEIGHT);
        config.setWindowIcon("icon.png");
        config.setResizable(false);
        return config;
    }

    @Override
    public void create() {
        camera = new OrthographicCamera();
        viewport = new ExtendViewport(VIEW_PORT_WIDTH, VIEW_PORT_HEIGHT, camera);
        batch = new SpriteBatch();
        tetrisScreen = new TetrisScreen(this);
        setScreen(tetrisScreen);
    }

    @Override
    public void dispose() {
        super.dispose();
        batch.dispose();
        tetrisScreen.dispose();
        Resources.ASSERT.dispose();
    }
}
