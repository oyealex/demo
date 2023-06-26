/*
 * Copyright (c) 2021. oyealex. All rights reserved.
 */

package com.oyealex.game.tetris.model;

import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.ToString;

/**
 * Brick
 *
 * @author oyealex
 * @version 1.0
 * @since 2021-08-19
 */
@ToString
@AllArgsConstructor
@RequiredArgsConstructor
public final class Brick {
    public int x;

    public int y;

    public final BrickColor color;

    public Brick copy() {
        return new Brick(x, y, color);
    }
}
