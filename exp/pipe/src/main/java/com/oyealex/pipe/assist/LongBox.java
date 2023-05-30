package com.oyealex.pipe.assist;

import java.util.function.LongSupplier;
import java.util.function.Supplier;

/**
 * Box
 *
 * @author oyealex
 * @since 2023-05-24
 */
public final class LongBox implements LongSupplier, Supplier<Long> {
    private long value;

    public LongBox(long value) {
        this.value = value;
    }

    public long getValue() {
        return value;
    }

    public void setValue(long value) {
        this.value = value;
    }

    public long incrementAndGet() {
        return ++value;
    }

    public long getAndIncrement() {
        return value++;
    }

    public long decrementAndGet() {
        return --value;
    }

    public long getAndDecrement() {
        return value--;
    }

    @Override
    public Long get() {
        return getValue();
    }

    @Override
    public long getAsLong() {
        return getValue();
    }

    @Override
    public int hashCode() {
        return Long.hashCode(value);
    }

    @Override
    public String toString() {
        return "LongBox(" + value + ")";
    }

    public static LongBox box() {
        return new LongBox(0);
    }

    public static LongBox box(long initValue) {
        return new LongBox(initValue);
    }
}
