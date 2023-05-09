package com.oyealex.pipe.basis;

import com.oyealex.pipe.flag.PipeFlag;

import java.util.ArrayDeque;
import java.util.Comparator;
import java.util.Queue;
import java.util.Spliterator;
import java.util.function.Consumer;

import static com.oyealex.pipe.flag.PipeFlag.SPLIT_MASK;
import static java.util.Objects.requireNonNull;

/**
 * 将流水线中的元素包装为拆分器。
 * <p/>
 * <h2>基本原理</h2>
 * 需要保证从拆分器流出的元素都是经过原流水线各个节点处理过的元素，
 * 但是流水线的元素处理驱动方式未对外部暴露，有两种驱动方式：
 * <ol>
 *     <li/>如果流水线中包含可短路的操作，则执行可短路的{@link Spliterator#tryAdvance(Consumer)}方法逐个驱动流水线中的元素
 *     <li/>如果流水线不包含可短路的操作，则执行{@link Spliterator#forEachRemaining(Consumer)}方法批量驱动流水线中的元素
 * </ol>
 * 因此对应的需要采用不同的方式处理从流水线流出的元素：
 * <ol>
 *     <li/>短路流水线：将逐个流出的元素缓存起来，然后通过包装拆分器逐个处理
 *     <li/>非短路流水线：直接批量处理从流水线中流出的元素
 * </ol>
 *
 * @author oyealex
 * @since 2023-05-08
 */
class PipeSpliterator<OUT> implements Spliterator<OUT> {
    /** 被包装的流水线 */
    private final ReferencePipe<?, OUT> pipe;

    /** 被包装流水线的数据源 */
    private final Spliterator<Object> split;

    /** 用于缓存短路遍历元素的队列 */
    private Queue<OUT> cachedQueue;

    /** 已经封装了流水线全部节点操作，并最终将元素流入缓存队列的操作方法 */
    private Op<Object> wrappedOfferOp;

    /** 是否已经完整遍历 */
    private boolean isTravelledFully = false;

    PipeSpliterator(ReferencePipe<?, OUT> pipe, Spliterator<Object> split) {
        this.pipe = pipe;
        this.split = split;
    }

    private boolean fillQueue() {
        while (cachedQueue.isEmpty()) {
            if (wrappedOfferOp.cancellationRequested() || !split.tryAdvance(wrappedOfferOp)) {
                if (isTravelledFully) {
                    return false;
                } else {
                    wrappedOfferOp.end();
                    isTravelledFully = true;
                }
            }
        }
        return true;
    }

    private boolean advance() {
        if (cachedQueue == null) {
            if (isTravelledFully) {
                return false;
            }
            cachedQueue = new ArrayDeque<>();
            wrappedOfferOp = pipe.wrapAllOp(cachedQueue::offer);
            wrappedOfferOp.begin(split.getExactSizeIfKnown());
            return fillQueue();
        } else {
            return !cachedQueue.isEmpty() || fillQueue();
        }
    }

    @Override
    public boolean tryAdvance(Consumer<? super OUT> action) {
        requireNonNull(action);
        boolean hasNext = advance();
        if (hasNext) {
            action.accept(cachedQueue.poll());
        }
        return hasNext;
    }

    @Override
    public void forEachRemaining(Consumer<? super OUT> action) {
        if (cachedQueue == null && !isTravelledFully) {
            pipe.processData(split, action::accept);
            isTravelledFully = true;
        } else {
            do {/*noop*/} while (tryAdvance(action));
        }
    }

    @Override
    public Spliterator<OUT> trySplit() {
        // can not split anymore
        return null;
    }

    @Override
    public long estimateSize() {
        return split.estimateSize();
    }

    @Override
    public long getExactSizeIfKnown() {
        return PipeFlag.SIZED.isSet(pipe.flag) ? split.getExactSizeIfKnown() : -1;
    }

    @Override
    public int characteristics() {
        int characteristics = pipe.flag & SPLIT_MASK;
        if ((characteristics & SIZED) == SIZED) {
            characteristics &= ~(SIZED | SUBSIZED);
        }
        return characteristics;
    }

    @Override
    public Comparator<? super OUT> getComparator() {
        if (hasCharacteristics(Spliterator.ORDERED)) {
            return null;
        }
        throw new IllegalStateException();
    }
}
