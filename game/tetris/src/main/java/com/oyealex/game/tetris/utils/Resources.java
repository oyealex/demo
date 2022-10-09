/*
 * Copyright (c) 2021. oyealex. All rights reserved.
 */

package com.oyealex.game.tetris.utils;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import static com.oyealex.game.tetris.utils.Constants.BRICK_COLORS_AMOUNT;
import static com.oyealex.game.tetris.utils.Constants.BRICK_SIZE;
import static com.oyealex.game.tetris.utils.Constants.TARGET_BRICKS_AMOUNT;
import static com.oyealex.game.tetris.utils.Constants.WINDOW_HEIGHT;
import static com.oyealex.game.tetris.utils.Constants.WINDOW_WIDTH;

/**
 * Resources
 *
 * @author oyealex
 * @version 1.0
 * @since 2021-08-21
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class Resources {
    /** 材质资源 */
    public static final Texture ASSERT = new Texture(Gdx.files.internal("assert.png"));

    /** 网格资源 */
    public static final TextureRegion GRID =
        new TextureRegion(ASSERT, 0, BRICK_SIZE, WINDOW_WIDTH, WINDOW_HEIGHT);

    /** 砖块资源 */
    public static final TextureRegion[] BRICKS =
        new TextureRegion(ASSERT, 0, 0, BRICK_SIZE * BRICK_COLORS_AMOUNT, BRICK_SIZE).split(BRICK_SIZE, BRICK_SIZE)[0];

    /** 目标砖块资源动画 */
    public static final Animation<TextureRegion> TARGET_BRICK_ANIM = new Animation<>(1.0f,
        new TextureRegion(ASSERT, BRICK_SIZE * BRICK_COLORS_AMOUNT, 0, BRICK_SIZE * TARGET_BRICKS_AMOUNT, BRICK_SIZE)
            .split(BRICK_SIZE, BRICK_SIZE)[0]);
}
