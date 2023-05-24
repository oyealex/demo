package com.oyealex.pipe.assist;

import java.util.Objects;
import java.util.Optional;
import java.util.function.Supplier;

/**
 * Box
 *
 * @author oyealex
 * @since 2023-05-24
 */
public final class Box<T> implements Supplier<T> {
    private T value;

    public Box(T value) {
        this.value = value;
    }

    public Box() {
    }

    public T getValue() {
        return value;
    }

    public void setValue(T value) {
        this.value = value;
    }

    public Optional<T> unwrap() {
        return Optional.ofNullable(value);
    }

    @Override
    public T get() {
        return getValue();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Box<?> box = (Box<?>) o;
        return Objects.equals(value, box.value);
    }

    @Override
    public int hashCode() {
        return Objects.hash(value);
    }

    @Override
    public String toString() {
        return "Box(" + value + ")";
    }
}
