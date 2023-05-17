package com.oyealex.pipe.basis;

import com.oyealex.pipe.basis.functional.LongBiConsumer;
import com.oyealex.pipe.basis.functional.LongBiFunction;
import com.oyealex.pipe.basis.functional.LongBiPredicate;
import com.oyealex.pipe.utils.Tuple;

import java.util.Comparator;
import java.util.Optional;
import java.util.function.BiFunction;
import java.util.function.BinaryOperator;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;

import static com.oyealex.pipe.flag.PipeFlag.IS_SHORT_CIRCUIT;

/**
 * Ops
 *
 * @author oyealex
 * @since 2023-04-28
 */
final class SimpleOps {
    private SimpleOps() {
        throw new IllegalStateException("no instance available");
    }

    public static <T> TerminalOp<T, Long> countOp() {
        return new TerminalOp.Orderly<>() {
            @Override
            public void accept(T value) {
                index++;
            }

            @Override
            public Long get() {
                return index;
            }
        };
    }

    public static <T> Op<T> keepIfOp(Op<T> nextOp, Predicate<? super T> predicate) {
        return new ChainedOp<>(nextOp) {
            @Override
            public void begin(long size) {
                nextOp.begin(-1);
            }

            @Override
            public void accept(T value) {
                if (predicate.test(value)) {
                    nextOp.accept(value);
                }
            }
        };
    }

    public static <T> Op<T> keepIfOrderlyOp(Op<T> nextOp, LongBiPredicate<? super T> predicate) {
        return new ChainedOp.Orderly<>(nextOp) {
            @Override
            public void begin(long size) {
                nextOp.begin(-1);
            }

            @Override
            public void accept(T value) {
                if (predicate.test(index++, value)) {
                    nextOp.accept(value);
                }
            }
        };
    }

    public static <T> TerminalOp<T, Void> forEachOp(Consumer<? super T> action) {
        return new TerminalOp<>() {
            @Override
            public void accept(T value) {
                action.accept(value);
            }

            @Override
            public Void get() {
                return null;
            }
        };
    }

    public static <IN, OUT> ChainedOp<IN, OUT> mapOp(Op<OUT> nextOp, Function<? super IN, ? extends OUT> mapper) {
        return new ChainedOp<>(nextOp) {
            @Override
            public void accept(IN in) {
                nextOp.accept(mapper.apply(in));
            }
        };
    }

    public static <T, R> Op<T> mapOrderlyOp(Op<R> nextOp, LongBiFunction<? super T, ? extends R> mapper) {
        return new ChainedOp.Orderly<>(nextOp) {
            @Override
            public void accept(T value) {
                nextOp.accept(mapper.apply(index++, value));
            }
        };
    }

    public static <T> TerminalOp<T, Void> forEachOrderlyOp(LongBiConsumer<? super T> action) {
        return new TerminalOp<>() {
            private long index = 0L;

            @Override
            public void accept(T value) {
                action.accept(index++, value);
            }

            @Override
            public Void get() {
                return null;
            }
        };
    }

    public static <T> Op<T> peekOp(Op<T> nextOp, Consumer<? super T> consumer) {
        return new ChainedOp<>(nextOp) {
            @Override
            public void accept(T value) {
                consumer.accept(value);
                nextOp.accept(value);
            }
        };
    }

    public static <T> Op<T> peekOrderlyOp(Op<T> nextOp, LongBiConsumer<? super T> consumer) {
        return new ChainedOp.Orderly<>(nextOp) {
            @Override
            public void accept(T value) {
                consumer.accept(index++, value);
                nextOp.accept(value);
            }
        };
    }

    public static <T> TerminalOp<T, Optional<T>> minTerminalOp(Comparator<? super T> comparator) {
        return new TerminalOp.FindOpt<>() {
            @Override
            public void accept(T value) {
                if (found) {
                    if (comparator.compare(result, value) > 0) {
                        result = value;
                    }
                } else {
                    result = value;
                    found = true;
                }
            }
        };
    }

    public static <T, K> TerminalOp<T, Optional<Tuple<K, T>>> minByOrderlyTerminalOp(
        LongBiFunction<? super T, ? extends K> mapper, Comparator<? super K> comparator) {
        return new TerminalOp.FindOpt<>() {
            private long index = 0L;

            @Override
            public void accept(T value) {
                K key = mapper.apply(index++, value);
                if (found) {
                    if (comparator.compare(result.first, key) > 0) {
                        result = new Tuple<>(key, value);
                    }
                } else {
                    result = new Tuple<>(key, value);
                    found = true;
                }
            }
        };
    }

    public static <T> TerminalOp<T, Optional<T>> findFirstTerminalOp() {
        return new TerminalOp.FindOptOnce<>() {
            @Override
            public void accept(T value) {
                result = value;
                found = true;
            }
        };
    }

    public static <T> TerminalOp<T, Optional<T>> findLastTerminalOp() {
        return new TerminalOp.FindOpt<>() {
            @Override
            public void accept(T value) {
                result = value;
                found = true;
            }
        };
    }

    public static <T> TerminalOp<T, Optional<T>> reduceTerminalOp(BinaryOperator<T> operator) {
        return new TerminalOp.FindOpt<>() {
            @Override
            public void accept(T value) {
                if (found) {
                    result = operator.apply(result, value);
                } else {
                    found = true;
                    result = value;
                }
            }
        };
    }

    public static <T, R> TerminalOp<T, R> reduceTerminalOp(R initVar,
        BiFunction<? super R, ? super T, ? extends R> reducer) {
        return new TerminalOp.Find<>(initVar) {
            @Override
            public void accept(T value) {
                result = reducer.apply(result, value);
            }
        };
    }

    public static <T> TerminalOp<T, Boolean> anyMatchTerminalOp(Predicate<? super T> predicate) {
        return new TerminalOp.Find<>(Boolean.FALSE) {
            @Override
            public void accept(T value) {
                result = predicate.test(value);
            }

            @Override
            public boolean canShortCircuit() {
                return result;
            }

            @Override
            public int getOpFlag() {
                return IS_SHORT_CIRCUIT;
            }
        };
    }

    public static <T> TerminalOp<T, Boolean> allMatchTerminalOp(Predicate<? super T> predicate) {
        return new TerminalOp.Find<>(Boolean.TRUE) {
            @Override
            public void accept(T value) {
                result = predicate.test(value);
            }

            @Override
            public boolean canShortCircuit() {
                return !result;
            }

            @Override
            public int getOpFlag() {
                return IS_SHORT_CIRCUIT;
            }
        };
    }
}
