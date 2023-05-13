package com.oyealex.pipe.basis;

import java.util.Random;

import static com.oyealex.pipe.flag.PipeFlag.NOT_REVERSED_SORTED;
import static com.oyealex.pipe.flag.PipeFlag.NOT_SORTED;

/**
 * ShuffleStage
 *
 * @author oyealex
 * @since 2023-05-12
 */
class ShuffleOp<T> extends RefPipe<T, T> {
    private final Random random;

    ShuffleOp(RefPipe<?, ? extends T> prePipe, Random random) {
        super(prePipe, NOT_SORTED | NOT_REVERSED_SORTED);
        this.random = random;
    }

    @Override
    protected Op<T> wrapOp(Op<T> nextOp) {
        return new ChainedOp.ListRepeater<>(nextOp) {
            @Override
            protected void beforeEnd() {
                shuffle();
            }

            private void shuffle() {
                int size = elements.size();
                for (int index = 0; index < size; index++) {
                    int swapIndex = index + random.nextInt(size - index);
                    swap(index, swapIndex);
                }
            }

            private void swap(int index, int swapIndex) {
                T var = elements.get(index);
                elements.set(index, elements.get(swapIndex));
                elements.set(swapIndex, var);
            }
        };
    }
}
