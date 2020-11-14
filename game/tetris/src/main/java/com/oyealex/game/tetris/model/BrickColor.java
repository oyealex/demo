/*
 * Copyright (c) 2016 - 2021. oyealex. All rights reserved.
 */

package com.oyealex.game.tetris.model;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.Random;

/**
 * BrickColor
 *
 * @author oyealex
 * @version 1.0
 * @since 2021-09-14
 */
@Getter
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public enum BrickColor {
    BLUE(0),
    GREEN(1),
    PURPLE(2),
    YELLOW(3),
    ORANGE(4),
    GRAY(5),
    MARK(6),
    ;

    private final int index;

    public static final int MAX_NORMAL_BLOCK_COLOR_INDEX_EXCLUSIVE = 5;

    private static final Random RANDOM = new Random();

    private static final BrickColor[] COLORS = values();

    /**
     * 获取随机普通颜色
     *
     * @return 随机普通颜色
     */
    public static BrickColor randomNormalColor() {
        return COLORS[RANDOM.nextInt(MAX_NORMAL_BLOCK_COLOR_INDEX_EXCLUSIVE)];
    }
}
