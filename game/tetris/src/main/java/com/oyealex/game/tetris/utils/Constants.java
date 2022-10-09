/*
 * Copyright (c) 2021. oyealex. All rights reserved.
 */

package com.oyealex.game.tetris.utils;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.jetbrains.annotations.Contract;

/**
 * Constants
 *
 * @author oyealex
 * @version 1.0
 * @since 2021-08-17
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class Constants {
    /** 窗口标题 */
    public static final String TITLE = "Tetris";

    /** 网格横向数量 */
    public static final int GRID_ROW = 20;

    /** 网格纵向数量 */
    public static final int GRID_COL = 10;

    /** 最大行索引 */
    public static final int GRID_MAX_ROW = GRID_ROW - 1;

    /** 最大列索引 */
    public static final int GRID_MAX_COL = GRID_COL - 1;

    /** 单元格大小 */
    public static final int BRICK_SIZE = 24;

    /** 网格分割线宽度 */
    public static final int GRID_LINE_WIDTH = 1;

    /** 网格边界宽度 */
    public static final int GRID_BORDER_WIDTH = 3;

    /** 网格单元大小 */
    public static final int GRID_BRICK_SIZE = BRICK_SIZE + GRID_LINE_WIDTH;

    /** 网格像素宽度 */
    public static final int GRID_PIX_WIDTH = GRID_COL * (BRICK_SIZE + GRID_LINE_WIDTH) + GRID_LINE_WIDTH;

    /** 网格像素高度 */
    public static final int GRID_PIX_HEIGHT = GRID_ROW * (BRICK_SIZE + GRID_LINE_WIDTH) + GRID_LINE_WIDTH;

    /** 窗口宽度 */
    public static final int WINDOW_WIDTH = GRID_PIX_WIDTH + GRID_BORDER_WIDTH * 2;

    /** 窗口高度 */
    public static final int WINDOW_HEIGHT = GRID_PIX_HEIGHT + GRID_BORDER_WIDTH * 2;

    /** 视窗宽度 */
    public static final int VIEW_PORT_WIDTH = WINDOW_WIDTH;

    /** 视窗高度 */
    public static final int VIEW_PORT_HEIGHT = WINDOW_HEIGHT;

    /** 颜色方块数量 */
    public static final int BRICK_COLORS_AMOUNT = 6;

    /** 指示方块数量 */
    public static final int TARGET_BRICKS_AMOUNT = 4;

    /**
     * 将网格横坐标转换为屏幕横坐标
     *
     * @param gridX 网格横坐标
     * @return 屏幕横坐标
     */
    @Contract(pure = true)
    public static int toScreenX(int gridX) {
        return gridX * GRID_BRICK_SIZE + GRID_BORDER_WIDTH + 1;
    }

    /**
     * 将网格纵坐标转换为屏幕纵坐标
     *
     * @param gridY 网格纵坐标
     * @return 屏幕纵坐标
     */
    @Contract(pure = true)
    public static int toScreenY(int gridY) {
        return gridY * GRID_BRICK_SIZE + GRID_BORDER_WIDTH + 1;
    }
}
