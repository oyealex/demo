package com.oyealex.pipe.basis;

import com.oyealex.pipe.assist.Tuple;
import com.oyealex.pipe.basis.functional.LongBiFunction;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import static com.oyealex.pipe.flag.PipeFlag.IS_REVERSED_SORTED;
import static com.oyealex.pipe.flag.PipeFlag.IS_SORTED;
import static com.oyealex.pipe.flag.PipeFlag.NOT_REVERSED_SORTED;
import static com.oyealex.pipe.flag.PipeFlag.NOT_SORTED;
import static com.oyealex.pipe.utils.MiscUtil.checkArraySize;
import static com.oyealex.pipe.utils.MiscUtil.isStdNaturalOrder;
import static com.oyealex.pipe.utils.MiscUtil.isStdReverseOrder;

/**
 * SortStage
 *
 * @author oyealex
 * @since 2023-05-11
 */
abstract class SortOp<T> extends RefPipe<T, T> {
    private SortOp(RefPipe<?, ? extends T> prePipe, int opFlag) {
        super(prePipe, opFlag);
    }

    private static <T> int parseOpFlag(Comparator<? super T> comparator) {
        if (isStdNaturalOrder(comparator)) {
            return IS_SORTED | NOT_REVERSED_SORTED;
        } else if (isStdReverseOrder(comparator)) {
            return NOT_SORTED | IS_REVERSED_SORTED;
        }
        return NOT_SORTED | NOT_REVERSED_SORTED;
    }

    static class Normal<T> extends SortOp<T> {
        private final Comparator<? super T> comparator;

        Normal(RefPipe<?, ? extends T> prePipe, Comparator<? super T> comparator) {
            super(prePipe, parseOpFlag(comparator));
            this.comparator = comparator;
        }

        @Override
        protected Op<T> wrapOp(Op<T> nextOp) {
            return new ChainedOp.ListRepeater<T>(nextOp) {
                @Override
                protected void beforeEnd() {
                    elements.sort(comparator);
                }
            };
        }
    }

    static class Orderly<T, K> extends SortOp<T> {
        private final Comparator<? super K> comparator;

        private final LongBiFunction<? super T, ? extends K> mapper;

        Orderly(RefPipe<?, ? extends T> prePipe, Comparator<? super K> comparator,
            LongBiFunction<? super T, ? extends K> mapper) {
            super(prePipe, parseOpFlag(comparator));
            this.comparator = comparator;
            this.mapper = mapper;
        }

        @Override
        protected Op<T> wrapOp(Op<T> nextOp) {
            return new ChainedOp.NonShortCircuit<T, T>(nextOp) {
                private List<Tuple<K, T>> elements;

                private int index = 0; // 排序操作无法处理超过数组最大数量的元素，所以次序字段使用int即可

                @Override
                public void begin(long size) {
                    checkArraySize(size);
                    elements = size >= 0 ? new ArrayList<>((int) size) : new ArrayList<>();
                }

                @Override
                public void end() {
                    elements.sort(Comparator.comparing(wrap -> wrap.first, comparator));
                    // 收集并处理完元素之后开始执行后续操作
                    nextOp.begin(elements.size());
                    if (isShortCircuitRequested) {
                        for (Tuple<K, T> value : elements) {
                            if (nextOp.canShortCircuit()) {
                                break;
                            }
                            nextOp.accept(value.second);
                        }
                    } else {
                        elements.forEach(value -> nextOp.accept(value.second));
                    }
                    nextOp.end();
                    elements = null;
                }

                @Override
                public void accept(T value) {
                    elements.add(Tuple.of(mapper.apply(index++, value), value));
                }
            };
        }
    }
}
