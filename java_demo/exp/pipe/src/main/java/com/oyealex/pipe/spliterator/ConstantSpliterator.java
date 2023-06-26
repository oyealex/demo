package com.oyealex.pipe.spliterator;

import java.util.Comparator;
import java.util.Spliterator;
import java.util.function.Consumer;

/**
 * 单例拆分器
 *
 * @author oyealex
 * @since 2023-05-13
 */
class ConstantSpliterator<T> implements Spliterator<T> {
    private T constant;

    private int count;

    ConstantSpliterator(T constant, int count) {
        this.constant = constant;
        this.count = count;
    }

    @Override
    public boolean tryAdvance(Consumer<? super T> action) {
        if (count <= 0) {
            return false;
        }
        T value = constant;
        if (--count <= 0) {
            constant = null;
        }
        action.accept(value);
        return true;
    }

    @Override
    public Spliterator<T> trySplit() {
        return null;
    }

    @Override
    public long estimateSize() {
        return count;
    }

    @Override
    public int characteristics() {
        return Spliterator.SIZED | Spliterator.SORTED | Spliterator.ORDERED | Spliterator.SUBSIZED |
            (constant == null ? 0 : Spliterator.NONNULL);
    }

    @Override
    public Comparator<? super T> getComparator() {
        // 自然有序
        return null;
    }
}
