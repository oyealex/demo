package com.oyealex.pipe.basis;

import com.oyealex.pipe.basis.api.Pipe;

import static com.oyealex.pipe.basis.Pipes.pipe;
import static com.oyealex.pipe.flag.PipeFlag.NOT_SIZED;
import static com.oyealex.pipe.flag.PipeFlag.toSpliteratorFlag;
import static java.util.Spliterators.spliterator;

/**
 * PartitionOp
 *
 * @author oyealex
 * @since 2023-05-03
 */
class PartitionOp<T> extends RefPipe<T, Pipe<T>> {
    private final int partitionSize;

    private final int partitionSpliteratorFlag;

    PartitionOp(RefPipe<?, ? extends T> prePipe, int size) {
        super(prePipe, NOT_SIZED);
        this.partitionSize = size;
        this.partitionSpliteratorFlag = toSpliteratorFlag(flag);
    }

    @Override
    protected Op<T> wrapOp(Op<Pipe<T>> nextOp) {
        // OPT 2023-05-12 22:59 空间优先 or 时间优先策略，根据全局配置动态选择
        return new ChainedOp.ShortCircuitRecorded<>(nextOp) {
            private T[] partition;

            private int nextVarIndex;

            @Override
            public void begin(long size) {
                if (size == 0) { // 不会有数据流入
                    nextOp.begin(0);
                    return;
                }
                prepareNewPartition();
                nextOp.begin(size >= 0 ? size / partitionSize + (size % partitionSize == 0 ? 0 : 1) : -1);
            }

            @Override
            public void accept(T var) {
                partition[nextVarIndex++] = var;
                if (nextVarIndex >= partitionSize) {
                    if (isShortCircuitRequested) { // 可能短路
                        if (nextOp.canShortCircuit()) {
                            // 如果短路则不传递元素，但是还是会重置索引
                            nextVarIndex = 0;
                            return;
                        }
                    }
                    consumerPartition();
                }
            }

            @Override
            public void end() {
                if (nextVarIndex > 0) {
                    nextOp.accept(pipe(spliterator(partition, 0, nextVarIndex, partitionSpliteratorFlag)));
                }
                partition = null;
                nextVarIndex = 0;
            }

            private void consumerPartition() {
                int length = nextVarIndex;
                T[] elements = prepareNewPartition();
                nextOp.accept(pipe(spliterator(elements, 0, length, partitionSpliteratorFlag)));
            }

            private T[] prepareNewPartition() {
                T[] previous = partition;
                @SuppressWarnings("unchecked") T[] array = (T[]) new Object[partitionSize];
                partition = array;
                nextVarIndex = 0;
                return previous;
            }
        };
    }
}
