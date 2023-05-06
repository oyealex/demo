package com.oyealex.pipe.spliterator;

import java.util.Comparator;
import java.util.Spliterator;
import java.util.function.Consumer;

/**
 * 将两个拆分器拼接起来。
 *
 * @author oyealex
 * @since 2023-05-06
 */
public class ConcatSpliterator<T, SPLIT extends Spliterator<T>> implements Spliterator<T> {
    /** 首先使用的拆分器 */
    private SPLIT head;

    /** 随后使用的拆分器 */
    private SPLIT tail;

    /** 标记在拆分之前，此组合拆分器是否无边界 */
    private final boolean isUnSizedBeforeSplit;

    public ConcatSpliterator(SPLIT head, SPLIT tail) {
        this.head = head;
        this.tail = tail;
        this.isUnSizedBeforeSplit = head.estimateSize() + tail.estimateSize() < 0;
    }

    @Override
    @SuppressWarnings("unchecked")
    public SPLIT trySplit() {
        if (head != null) {
            SPLIT result = head;
            head = null;
            return result;
        }
        if (tail != null) {
            return (SPLIT) tail.trySplit();
        }
        return null;
    }

    @Override
    public boolean tryAdvance(Consumer<? super T> consumer) {
        if (head != null) {
            if (head.tryAdvance(consumer)) {
                return true;
            } else {
                head = null;
            }
        }
        if (tail != null) {
            if (tail.tryAdvance(consumer)) {
                return true;
            } else {
                tail = null;
            }
        }
        return false;
    }

    @Override
    public void forEachRemaining(Consumer<? super T> consumer) {
        if (head != null) {
            head.forEachRemaining(consumer);
        }
        if (tail != null) {
            tail.forEachRemaining(consumer);
        }
    }

    @Override
    public long estimateSize() {
        if (head != null) {
            long size = head.estimateSize() + tail.estimateSize();
            return (size >= 0) ? size : Long.MAX_VALUE;
        }
        if (tail != null) {
            return tail.estimateSize();
        }
        return 0L;
    }

    @Override
    public int characteristics() {
        if (head != null) {
            return head.characteristics() & tail.characteristics() &
                ~(DISTINCT | SORTED | (isUnSizedBeforeSplit ? SIZED | SUBSIZED : 0));
        }
        if (tail != null) {
            return tail.characteristics();
        }
        return 0;
    }

    @Override
    public Comparator<? super T> getComparator() {
        if (head != null) {
            throw new IllegalStateException();
        }
        return tail.getComparator();
    }
}
