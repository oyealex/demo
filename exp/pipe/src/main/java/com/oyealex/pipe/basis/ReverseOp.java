package com.oyealex.pipe.basis;

import static com.oyealex.pipe.flag.PipeFlag.IS_REVERSED_SORTED;
import static com.oyealex.pipe.flag.PipeFlag.IS_SORTED;
import static com.oyealex.pipe.flag.PipeFlag.NO_FLAG;
import static com.oyealex.pipe.flag.PipeFlag.NOT_REVERSED_SORTED;
import static com.oyealex.pipe.flag.PipeFlag.NOT_SORTED;
import static com.oyealex.pipe.flag.PipeFlag.REVERSED_SORTED;
import static com.oyealex.pipe.flag.PipeFlag.SORTED;

/**
 * ReverseStage
 *
 * @author oyealex
 * @since 2023-05-11
 */
class ReverseOp<T> extends RefPipe<T, T> {
    ReverseOp(RefPipe<?, ? extends T> prePipe) {
        super(prePipe, getOpFlag(prePipe));
    }

    @Override
    protected Op<T> wrapOp(Op<T> nextOp) {
        return new ChainedOp.ToList<>(nextOp) {
            @Override
            public void end() {
                nextOp.begin(elements.size());
                if (isShortCircuitRequested) {
                    for (int i = elements.size() - 1; i >= 0 && !nextOp.canShortCircuit(); i--) {
                        nextOp.accept(elements.get(i));
                    }
                } else {
                    for (int i = elements.size() - 1; i >= 0; i--) {
                        nextOp.accept(elements.get(i));
                    }
                }
                nextOp.end();
                elements = null;
            }
        };
    }

    private static int getOpFlag(RefPipe<?, ?> prePipe) {
        int opFlag = NO_FLAG;
        if (prePipe.isFlagSet(SORTED)) { // 如果已自然有序，则标记自然逆序
            opFlag |= NOT_SORTED | IS_REVERSED_SORTED;
        }
        if (prePipe.isFlagSet(REVERSED_SORTED)) { // 如果已自然逆序，则标记自然有序
            opFlag |= IS_SORTED | NOT_REVERSED_SORTED;
        }
        return opFlag;
    }
}
