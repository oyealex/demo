package com.oyealex.pipe.assist;

import java.util.function.IntSupplier;
import java.util.function.IntUnaryOperator;
import java.util.function.Supplier;

/**
 * Box
 *
 * @author oyealex
 * @since 2023-05-24
 */
public final class IntBox implements IntSupplier, Supplier<Integer> {
    private int value;

    private IntBox(int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }

    public void setValue(int value) {
        this.value = value;
    }

    public int incrementAndGet() {
        return ++value;
    }

    public int getAndIncrement() {
        return value++;
    }

    public int decrementAndGet() {
        return --value;
    }

    public int getAndDecrement() {
        return value--;
    }

    public int getAndMap(IntUnaryOperator operator) {
        int oldValue = value;
        value = operator.applyAsInt(oldValue);
        return oldValue;
    }

    public int mapAndGet(IntUnaryOperator operator) {
        value = operator.applyAsInt(value);
        return value;
    }

    public int incrementRoundAndGet(int bound) {
        if (++value >= bound) {
            value = 0;
        }
        return value;
    }

    public int getAndIncrementRound(int bound) {
        int oldValue = value;
        if (++value >= bound) {
            value = 0;
        }
        return oldValue;
    }

    public int decrementRoundAndGet(int bound) {
        if (--value < 0) {
            value = bound - 1;
        }
        return value;
    }

    public int getAndDecrementRound(int bound) {
        int oldValue = value;
        if (--value < 0) {
            value = bound - 1;
        }
        return oldValue;
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

    public static IntBox box() {
        return new IntBox(0);
    }

    public static IntBox box(int initValue) {
        return new IntBox(initValue);
    }
}
