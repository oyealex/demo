package com.oyealex.pipe.basis;

import com.oyealex.pipe.basis.functional.LongBiFunction;
import com.oyealex.pipe.utils.Tuple;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import static com.oyealex.pipe.flag.PipeFlag.IS_SORTED;
import static com.oyealex.pipe.flag.PipeFlag.NOT_REVERSED_SORTED;
import static com.oyealex.pipe.flag.PipeFlag.NOT_SORTED;
import static com.oyealex.pipe.utils.MiscUtil.checkArraySize;
import static com.oyealex.pipe.utils.MiscUtil.isStdNaturalOrder;

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

    static class Normal<T> extends SortOp<T> {
        private final Comparator<? super T> comparator;

        @SuppressWarnings("unchecked")
        Normal(RefPipe<?, ? extends T> prePipe, Comparator<? super T> comparator) {
            super(prePipe,
                isStdNaturalOrder(comparator) ? IS_SORTED | NOT_REVERSED_SORTED : NOT_SORTED | NOT_REVERSED_SORTED);
            this.comparator = comparator;
        }

        @Override
        protected Op<T> wrapOp(Op<T> nextOp) {
            return new ChainedOp.ListRepeater<>(nextOp) {
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
            super(prePipe,
                isStdNaturalOrder(comparator) ? IS_SORTED | NOT_REVERSED_SORTED : NOT_SORTED | NOT_REVERSED_SORTED);
            this.comparator = comparator;
            this.mapper = mapper;
        }

        @Override
        protected Op<T> wrapOp(Op<T> nextOp) {
            return new ChainedOp.NonShortCircuit<>(nextOp) {
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
                        for (Tuple<K, T> var : elements) {
                            if (nextOp.canShortCircuit()) {
                                break;
                            }
                            nextOp.accept(var.second);
                        }
                    } else {
                        elements.forEach(var -> nextOp.accept(var.second));
                    }
                    nextOp.end();
                    elements = null;
                }

                @Override
                public void accept(T var) {
                    elements.add(new Tuple<>(mapper.apply(index++, var), var));
                }
            };
        }
    }
}
