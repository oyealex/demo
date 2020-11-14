/*
 * Copyright (c) 2016 - 2021. oyealex. All rights reserved.
 */

package com.oyealex.container;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

/**
 * int类型的二维点
 *
 * @author oyealex
 * @version 1.0
 * @since 2021-09-11
 */
@AllArgsConstructor
@NoArgsConstructor
public final class IntPoint {
    public int x;

    public int y;

    @Override
    public boolean equals(Object other) {
        if (this == other) return true;
        if (other == null || getClass() != other.getClass()) return false;
        IntPoint intPoint = (IntPoint) other;
        return x == intPoint.x && y == intPoint.y;
    }

    @Override
    public int hashCode() {
        return (31 + x) * 31 + y;
    }

    @Override
    public String toString() {
        return "(" + x + "," + y + ")";
    }
}
