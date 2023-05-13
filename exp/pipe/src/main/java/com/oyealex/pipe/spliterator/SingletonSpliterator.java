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
public class SingletonSpliterator<T> implements Spliterator<T> {
    private T singleton;

    private boolean consumed = false;

    public SingletonSpliterator(T singleton) {
        this.singleton = singleton;
    }

    @Override
    public boolean tryAdvance(Consumer<? super T> action) {
        if (consumed) {
            return false;
        }
        T var = singleton;
        singleton = null;
        consumed = true;
        action.accept(var);
        return true;
    }

    @Override
    public Spliterator<T> trySplit() {
        return null;
    }

    @Override
    public long estimateSize() {
        return singleton == null ? 0 : 1;
    }

    @Override
    public int characteristics() {
        return Spliterator.SIZED | Spliterator.SORTED | Spliterator.DISTINCT | Spliterator.ORDERED |
            Spliterator.SUBSIZED | (singleton == null ? 0 : Spliterator.NONNULL);
    }

    @Override
    public Comparator<? super T> getComparator() {
        // 自然有序
        return null;
    }
}
