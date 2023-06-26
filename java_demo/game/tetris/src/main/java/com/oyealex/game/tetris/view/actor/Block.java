/*
 * Copyright (c) 2021. oyealex. All rights reserved.
 */

package com.oyealex.game.tetris.view.actor;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.oyealex.game.tetris.model.Brick;
import com.oyealex.game.tetris.utils.Resources;
import lombok.RequiredArgsConstructor;

import java.util.LinkedList;

import static com.oyealex.game.tetris.utils.Constants.toScreenX;
import static com.oyealex.game.tetris.utils.Constants.toScreenY;

/**
 * Block
 *
 * @author oyealex
 * @version 1.0
 * @since 2021-08-19
 */
@RequiredArgsConstructor
public abstract class Block extends Actor {
    protected final LinkedList<Brick> bricks = new LinkedList<>();

    @Override
    public void draw(Batch batch, float parentAlpha) {
        for (Brick brick : bricks) {
            batch.draw(Resources.BRICKS[brick.color.getIndex()], toScreenX(brick.x), toScreenY(brick.y));
        }
    }
}
