/*
 * Copyright (c) 2016 - 2021. oyealex. All rights reserved.
 */

package com.oyealex.game.tetris.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

/**
 * Shape
 *
 * @author oyealex
 * @version 1.0
 * @since 2021-09-14
 */
@Slf4j
@Getter
@Setter
@AllArgsConstructor
@RequiredArgsConstructor
public class Shape {
    private final String name;

    private final int[][] offsets;

    private Shape next;

    public int getBrickCount() {
        return offsets[0].length;
    }
}
