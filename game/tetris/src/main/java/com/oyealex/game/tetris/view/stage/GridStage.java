/*
 * Copyright (c) 2021. oyealex. All rights reserved.
 */

package com.oyealex.game.tetris.view.stage;

import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.oyealex.game.tetris.view.actor.BlockActor;
import com.oyealex.game.tetris.view.actor.DiscreteBlock;
import com.oyealex.game.tetris.view.actor.GridActor;
import com.oyealex.game.tetris.view.screen.TetrisScreen;

/**
 * GridStage
 *
 * @author oyealex
 * @version 1.0
 * @since 2021-08-19
 */
public class GridStage extends Stage {
    public GridStage(TetrisScreen screen) {
        super(screen.getViewport(), screen.getBatch());
        addListener(new InputListener());
        addActor(new GridActor());
        addActor(new BlockActor());
        addActor(new DiscreteBlock());
    }

    private final class InputHandler extends InputListener {
        @Override
        public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
            return true;
        }
    }
}
