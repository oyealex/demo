package com.oyealex.pipe.basis;

import com.oyealex.pipe.basis.functional.LongBiFunction;
import com.oyealex.pipe.basis.policy.PartitionPolicy;

import java.util.ArrayList;
import java.util.List;
import java.util.Spliterators;
import java.util.function.Function;

import static com.oyealex.pipe.basis.Pipe.list;
import static com.oyealex.pipe.basis.Pipe.spliterator;
import static com.oyealex.pipe.flag.PipeFlag.NOT_SIZED;
import static com.oyealex.pipe.flag.PipeFlag.toSpliteratorFlag;

/**
 * PartitionOp
 *
 * @author oyealex
 * @since 2023-05-03
 */
abstract class PartitionOp<T> extends RefPipe<T, Pipe<T>> {
    protected final int partitionSpliteratorFlag;

    private PartitionOp(RefPipe<?, ? extends T> prePipe) {
        super(prePipe, NOT_SIZED);
        this.partitionSpliteratorFlag = toSpliteratorFlag(flag);
    }

    static class Sized<T> extends PartitionOp<T> {
        private final int partitionSize;

        Sized(RefPipe<?, ? extends T> prePipe, int size) {
            super(prePipe);
            this.partitionSize = size;
        }

        @Override
        protected Op<T> wrapOp(Op<Pipe<T>> nextOp) {
            // OPT 2023-05-12 22:59 空间优先 or 时间优先策略，根据全局配置动态选择
            return new ChainedOp.ShortCircuitRecorded<T, Pipe<T>>(nextOp) {
                private T[] partition;

                private int index;

                @Override
                public void begin(long size) {
                    if (size == 0) { // 不会有数据流入
                        nextOp.begin(0);
                        return;
                    }
                    prepareNewPartition();
                    nextOp.begin(size > 0 ? size / partitionSize + (size % partitionSize == 0 ? 0 : 1) : -1);
                }

                @Override
                public void accept(T value) {
                    partition[index++] = value;
                    if (index >= partitionSize) {
                        if (shouldShortCircuit()) {
                            // 如果短路则不传递元素，但是还是会重置索引
                            index = 0;
                            return;
                        }
                        consumerPartition();
                    }
                }

                @Override
                public void end() {
                    if (index > 0) {
                        nextOp.accept(
                            spliterator(Spliterators.spliterator(partition, 0, index, partitionSpliteratorFlag)));
                    }
                    partition = null;
                }

                private void consumerPartition() {
                    int length = index;
                    T[] elements = prepareNewPartition();
                    nextOp.accept(spliterator(Spliterators.spliterator(elements, 0, length, partitionSpliteratorFlag)));
                }

                private T[] prepareNewPartition() {
                    T[] previous = partition;
                    @SuppressWarnings("unchecked") T[] array = (T[]) new Object[partitionSize];
                    partition = array;
                    index = 0;
                    return previous;
                }
            };
        }
    }

    static class Policy<T> extends Conditional<T> {
        private final Function<? super T, PartitionPolicy> function;

        Policy(RefPipe<?, ? extends T> prePipe, Function<? super T, PartitionPolicy> function) {
            super(prePipe);
            this.function = function;
        }

        @Override
        protected PartitionPolicy getPolicy(T value) {
            return function.apply(value);
        }
    }

    static class PolicyOrderly<T> extends Conditional<T> {
        private final LongBiFunction<? super T, PartitionPolicy> function;

        private long index = 0L;

        PolicyOrderly(RefPipe<?, ? extends T> prePipe, LongBiFunction<? super T, PartitionPolicy> function) {
            super(prePipe);
            this.function = function;
        }

        @Override
        protected PartitionPolicy getPolicy(T value) {
            return function.apply(index++, value);
        }
    }

    private abstract static class Conditional<T> extends PartitionOp<T> {
        private Conditional(RefPipe<?, ? extends T> prePipe) {
            super(prePipe);
        }

        @Override
        protected Op<T> wrapOp(Op<Pipe<T>> nextOp) {
            return new ChainedOp.ShortCircuitRecorded<T, Pipe<T>>(nextOp) {
                private List<T> partition;

                @Override
                public void begin(long size) {
                    partition = new ArrayList<>();
                    nextOp.begin(size);
                }

                @Override
                public void accept(T value) {
                    switch (getPolicy(value)) {
                        case BEGIN: // 当前分区结束
                            completeCurrentPartition();
                        case IN: // 添加数据到新分区
                            ensurePartition();
                            partition.add(value);
                            break;
                        case END: // 添加数据，当前分区结束
                            ensurePartition();
                            partition.add(value);
                            completeCurrentPartition();
                            break;
                    }
                }

                @Override
                public void end() {
                    if (partition != null && !partition.isEmpty() && !shouldShortCircuit()) {
                        nextOp.accept(list(partition));
                    }
                    partition = null;
                    nextOp.end();
                }

                private void ensurePartition() {
                    if (partition == null) {
                        partition = new ArrayList<>();
                    }
                }

                private void completeCurrentPartition() {
                    List<T> endPartition = partition;
                    partition = null;
                    if (!shouldShortCircuit()) {
                        nextOp.accept(list(endPartition));
                    }
                }
            };
        }

        protected abstract PartitionPolicy getPolicy(T value);
    }
}
