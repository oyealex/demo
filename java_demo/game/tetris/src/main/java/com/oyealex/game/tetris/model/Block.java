/*
 * Copyright (c) 2016 - 2021. oyealex. All rights reserved.
 */

package com.oyealex.game.tetris.model;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * Block
 *
 * @author oyealex
 * @version 1.0
 * @since 2021-09-14
 */
@Getter
@RequiredArgsConstructor
public final class Block {
    private final Brick base;

    private final Brick[] bricks;

    private Shape shape;

    /**
     * 创建一个新的图块
     *
     * @param base  图块的基础位置
     * @param shape 图块的图形
     */
    public Block(Brick base, Shape shape) {
        this.base = base;
        this.shape = shape;
        this.bricks = initBricks();
        updateBricksPosition();
    }

    private Brick[] initBricks() {
        Brick[] result = new Brick[shape.getBrickCount()];
        result[0] = base;
        for (int i = 1; i < result.length; i++) {
            result[0] = base.copy();
        }
        return result;
    }

    /** 变换为形状的下一个类型 */
    public void transform() {
        shape = shape.getNext();
    }

    /** 以当前的基础位置和形状为参考，更新其他方块的位置 */
    public void updateBricksPosition() {
        int[][] offsets = shape.getOffsets();
        for (int i = 1; i < bricks.length; i++) {
            bricks[i].x = offsets[0][i] + base.x;
            bricks[i].y = offsets[1][i] + base.y;
        }
    }
}
