/*
 * Copyright (c) 2021. oyealex. All rights reserved.
 */

package com.oyealex.game.tetris.view.actor;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.oyealex.game.tetris.utils.Resources;

/**
 * GridActor
 *
 * @author oyealex
 * @version 1.0
 * @since 2021-08-21
 */
public class GridActor extends Actor {
    @Override
    public void draw(Batch batch, float parentAlpha) {
        batch.draw(Resources.GRID, 0, 0);
    }
}
