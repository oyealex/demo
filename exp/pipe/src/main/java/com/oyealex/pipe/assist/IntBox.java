package com.oyealex.pipe.assist;

import java.util.function.IntSupplier;
import java.util.function.Supplier;

/**
 * Box
 *
 * @author oyealex
 * @since 2023-05-24
 */
public final class IntBox implements IntSupplier, Supplier<Integer> {
    private int value;

    public IntBox(int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }

    public void setValue(int value) {
        this.value = value;
    }

    @Override
    public Integer get() {
        return getValue();
    }

    @Override
    public int getAsInt() {
        return getValue();
    }

    @Override
    public int hashCode() {
        return Integer.hashCode(value);
    }

    @Override
    public String toString() {
        return "IntBox(" + value + ")";
    }
}
