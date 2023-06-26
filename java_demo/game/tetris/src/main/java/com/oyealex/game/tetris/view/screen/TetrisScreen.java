/*
 * Copyright (c) 2021. oyealex. All rights reserved.
 */

package com.oyealex.game.tetris.view.screen;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.oyealex.game.tetris.TetrisGame;
import com.oyealex.game.tetris.view.stage.GridStage;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

/**
 * 游戏屏
 * <br/>
 * 管理游戏过程相关的角色和资源。
 *
 * @author oyealex
 * @version 1.0
 * @since 2021-08-12
 */
@Getter
@Slf4j
public class TetrisScreen extends ScreenAdapter {
    /** 批量绘制，来自Game */
    private final Batch batch;

    /** 视窗 */
    private final Viewport viewport;

    /** 网格舞台 */
    private final GridStage stage;

    public TetrisScreen(TetrisGame game) {
        this.batch = game.getBatch();
        this.viewport = game.getViewport();
        this.stage = new GridStage(this);
    }

    @Override
    public void show() {
        Gdx.input.setInputProcessor(stage);
    }

    @Override
    public void render(float delta) {
        stage.act();
        stage.draw();
    }

    @Override
    public void dispose() {
        stage.dispose();
    }
}
