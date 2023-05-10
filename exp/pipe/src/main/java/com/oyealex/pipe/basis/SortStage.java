package com.oyealex.pipe.basis;

import com.oyealex.pipe.basis.functional.LongBiFunction;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import static com.oyealex.pipe.flag.PipeFlag.IS_SORTED;
import static com.oyealex.pipe.flag.PipeFlag.NOT_REVERSED_SORTED;
import static com.oyealex.pipe.flag.PipeFlag.NOT_SORTED;
import static com.oyealex.pipe.utils.CheckUtil.checkArraySize;
import static java.util.Objects.requireNonNullElse;
import static java.util.Objects.requireNonNullElseGet;

/**
 * SortStage
 *
 * @author oyealex
 * @since 2023-05-11
 */
abstract class SortStage<T> extends RefPipe<T, T> {
    private SortStage(RefPipe<?, ? extends T> prePipe, int opFlag) {
        super(prePipe, opFlag);
    }

    static class Normal<T> extends SortStage<T> {
        private final Comparator<? super T> comparator;

        @SuppressWarnings("unchecked")
        Normal(RefPipe<?, ? extends T> prePipe, Comparator<? super T> comparator) {
            super(prePipe, comparator == null ? IS_SORTED | NOT_REVERSED_SORTED : NOT_SORTED | NOT_REVERSED_SORTED);
            this.comparator = requireNonNullElseGet(comparator,
                () -> (Comparator<? super T>) Comparator.naturalOrder());
        }

        @Override
        protected Op<T> wrapOp(Op<T> nextOp) {
            return new ChainedOp.CollectedOp<>(nextOp) {
                @Override
                public void end() {
                    elements.sort(comparator);
                    // 收集并处理完元素之后开始执行后续操作
                    nextOp.begin(elements.size());
                    if (isShortCircuitRequested) {
                        for (T var : elements) {
                            if (nextOp.canShortCircuit()) {
                                break;
                            }
                            nextOp.accept(var);
                        }
                    } else {
                        elements.forEach(nextOp);
                    }
                    nextOp.end();
                    elements = null;
                }
            };
        }
    }

    static class Orderly<T, K> extends SortStage<T> {
        private final Comparator<? super K> comparator;

        private final LongBiFunction<? super T, ? extends K> mapper;

        Orderly(RefPipe<?, ? extends T> prePipe, Comparator<? super K> comparator,
            LongBiFunction<? super T, ? extends K> mapper) {
            super(prePipe, comparator == null ? IS_SORTED | NOT_REVERSED_SORTED : NOT_SORTED | NOT_REVERSED_SORTED);
            @SuppressWarnings("unchecked") Comparator<? super K> actualComparator = requireNonNullElse(comparator,
                (Comparator<? super K>) Comparator.naturalOrder());
            this.comparator = actualComparator;
            this.mapper = mapper;
        }

        @Override
        protected Op<T> wrapOp(Op<T> nextOp) {
            return new ChainedOp.NonShortCircuitOp<>(nextOp) {
                private List<Object[]> elements;

                private int index = 0; // 排序操作无法处理超过数组最大数量的元素，所以次序字段使用int即可

                @Override
                public void begin(long size) {
                    checkArraySize(size);
                    elements = size >= 0 ? new ArrayList<>((int) size) : new ArrayList<>();
                }

                @Override
                @SuppressWarnings("unchecked")
                public void end() {
                    elements.sort(Comparator.comparing(wrap -> (K) wrap[0], comparator));
                    // 收集并处理完元素之后开始执行后续操作
                    nextOp.begin(elements.size());
                    if (isShortCircuitRequested) {
                        for (Object[] var : elements) {
                            if (nextOp.canShortCircuit()) {
                                break;
                            }
                            nextOp.accept((T) var[1]);
                        }
                    } else {
                        elements.forEach(var -> nextOp.accept((T) var[1]));
                    }
                    nextOp.end();
                    elements = null;
                }

                @Override
                public void accept(T var) {
                    elements.add(new Object[]{mapper.apply(index++, var), var});
                }
            };
        }
    }
}
