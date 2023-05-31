package com.oyealex.pipe.basis;

import com.oyealex.pipe.assist.Tuple;
import com.oyealex.pipe.basis.functional.LongBiConsumer;
import com.oyealex.pipe.basis.functional.LongBiFunction;
import com.oyealex.pipe.basis.functional.LongBiPredicate;
import com.oyealex.pipe.utils.NoInstance;

import java.util.Comparator;
import java.util.Optional;
import java.util.function.BiFunction;
import java.util.function.BinaryOperator;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;

import static com.oyealex.pipe.flag.PipeFlag.IS_SHORT_CIRCUIT;
import static java.util.Objects.requireNonNull;

/**
 * Ops
 *
 * @author oyealex
 * @since 2023-04-28
 */
final class SimpleOps extends NoInstance {
    public static <T> TerminalOp<T, Long> countOp() {
        return new TerminalOp.Orderly<T, Long>() {
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

    public static <T> Op<T> takeIfOp(Op<T> nextOp, Predicate<? super T> predicate) {
        return new ChainedOp<T, T>(nextOp) {
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

    public static <T> Op<T> takeIfOrderlyOp(Op<T> nextOp, LongBiPredicate<? super T> predicate) {
        return new ChainedOp.Orderly<T, T>(nextOp) {
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
        return new TerminalOp<T, Void>() {
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
        return new ChainedOp<IN, OUT>(nextOp) {
            @Override
            public void accept(IN in) {
                nextOp.accept(mapper.apply(in));
            }
        };
    }

    public static <T, R> Op<T> mapOrderlyOp(Op<R> nextOp, LongBiFunction<? super T, ? extends R> mapper) {
        return new ChainedOp.Orderly<T, R>(nextOp) {
            @Override
            public void accept(T value) {
                nextOp.accept(mapper.apply(index++, value));
            }
        };
    }

    public static <T> TerminalOp<T, Void> forEachOrderlyOp(LongBiConsumer<? super T> action) {
        return new TerminalOp<T, Void>() {
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
        return new ChainedOp<T, T>(nextOp) {
            @Override
            public void accept(T value) {
                consumer.accept(value);
                nextOp.accept(value);
            }
        };
    }

    public static <T> Op<T> peekOrderlyOp(Op<T> nextOp, LongBiConsumer<? super T> consumer) {
        return new ChainedOp.Orderly<T, T>(nextOp) {
            @Override
            public void accept(T value) {
                consumer.accept(index++, value);
                nextOp.accept(value);
            }
        };
    }

    public static <T> TerminalOp<T, Optional<T>> minTerminalOp(Comparator<? super T> comparator) {
        return new TerminalOp.FindOpt<T, T>() {
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
        return new TerminalOp.FindOpt<T, Tuple<K, T>>() {
            private long index = 0L;

            @Override
            public void accept(T value) {
                K key = mapper.apply(index++, value);
                if (found) {
                    if (comparator.compare(result.first, key) > 0) {
                        result = Tuple.of(key, value);
                    }
                } else {
                    result = Tuple.of(key, value);
                    found = true;
                }
            }
        };
    }

    public static <T> TerminalOp<T, Optional<T>> findFirstTerminalOp() {
        return new TerminalOp.FindOptOnce<T, T>() {
            @Override
            public void accept(T value) {
                result = value;
                found = true;
            }
        };
    }

    public static <T> TerminalOp<T, Optional<T>> findLastTerminalOp() {
        return new TerminalOp.FindOpt<T, T>() {
            @Override
            public void accept(T value) {
                result = value;
                found = true;
            }
        };
    }

    public static <T> TerminalOp<T, Optional<T>> reduceTerminalOp(BinaryOperator<T> operator) {
        return new TerminalOp.FindOpt<T, T>() {
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
        return new TerminalOp.Find<T, R>(initVar) {
            @Override
            public void accept(T value) {
                result = reducer.apply(result, value);
            }
        };
    }

    public static <T> TerminalOp<T, Boolean> anyMatchTerminalOp(Predicate<? super T> predicate) {
        return new TerminalOp.Find<T, Boolean>(Boolean.FALSE) {
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
        return new TerminalOp.Find<T, Boolean>(Boolean.TRUE) {
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

    public static <T> Op<T> mapIfOp(Op<T> nextOp, Predicate<? super T> condition,
        Function<? super T, ? extends T> mapper) {
        return new ChainedOp<T, T>(nextOp) {
            @Override
            public void accept(T value) {
                nextOp.accept(condition.test(value) ? mapper.apply(value) : value);
            }
        };
    }

    public static <T, R extends Optional<? extends T>> Op<T> mapIfOp(Op<T> nextOp, Function<? super T, R> mapper) {
        return new ChainedOp<T, T>(nextOp) {
            @Override
            public void accept(T value) {
                Optional<? extends T> opt = mapper.apply(value);
                nextOp.accept(opt.isPresent() ? opt.get() : value);
            }
        };
    }

    public static <T> Op<T> mapNullOp(Op<T> nextOp, Supplier<? extends T> supplier) {
        return new ChainedOp<T, T>(nextOp) {
            @Override
            public void accept(T value) {
                nextOp.accept(value == null ? requireNonNull(supplier.get()) : value);
            }
        };
    }

    public static <T> Op<T> disperse(Op<T> nextOp, T delimiter) {
        return new ChainedOp.ShortCircuitRecorded<T, T>(nextOp) {
            private boolean seen = false;

            @Override
            public void accept(T value) {
                if (seen) {
                    nextOp.accept(delimiter);
                    if (shouldShortCircuit()) {
                        return;
                    }
                } else {
                    seen = true;
                }
                nextOp.accept(value);
            }
        };
    }

    public static <T> TerminalOp<T, Tuple<Optional<T>, Optional<T>>> minMaxTerminalOp(
        Comparator<? super T> comparator) {
        return new TerminalOp.FindTupleOpt<T, T, T>() {
            @Override
            public void accept(T value) {
                if (foundFirst) {
                    if (comparator.compare(first, value) > 0) {
                        first = value;
                    }
                } else {
                    first = value;
                    foundFirst = true;
                }
                if (foundSecond) {
                    if (comparator.compare(second, value) < 0) {
                        second = value;
                    }
                } else {
                    second = value;
                    foundSecond = true;
                }
            }
        };
    }

    public static <T, K> TerminalOp<T, Tuple<Optional<Tuple<K, T>>, Optional<Tuple<K, T>>>> minMaxByOrderlyTerminalOp(
        LongBiFunction<? super T, ? extends K> mapper, Comparator<? super K> comparator) {
        return new TerminalOp.FindTupleOpt<T, Tuple<K, T>, Tuple<K, T>>() {
            private long index = 0L;

            @Override
            public void accept(T value) {
                K key = mapper.apply(index++, value);
                if (foundSecond) {
                    if (comparator.compare(second.first, key) < 0) {
                        second = Tuple.of(key, value);
                    }
                } else {
                    second = Tuple.of(key, value);
                    foundSecond = true;
                }
                if (foundFirst) {
                    if (comparator.compare(first.first, key) > 0) {
                        first = Tuple.of(key, value);
                    }
                } else {
                    first = Tuple.of(key, value);
                    foundFirst = true;
                }
            }
        };
    }

    public static <T> TerminalOp<T, Tuple<Optional<T>, Optional<T>>> findFirstLastTerminalOp() {
        return new TerminalOp.FindTupleOpt<T, T, T>() {
            @Override
            public void accept(T value) {
                second = value;
                foundSecond = true;
                if (!foundFirst) {
                    first = value;
                    foundFirst = true;
                }
            }
        };
    }

    public static <T> Op<T> nullsLastOp(Op<T> nextOp) {
        return new ChainedOp<T, T>(nextOp) {
            private int nullCount = 0;

            @Override
            public void accept(T value) {
                if (value == null) {
                    nullCount++;
                } else {
                    nextOp.accept(value);
                }
            }

            @Override
            public void end() {
                int count = nullCount;
                for (int i = 0; i < count; i++) {
                    nextOp.accept(null);
                }
                nextOp.end();
            }
        };
    }

    public static <T> Op<T> nullsFirstOp(Op<T> nextOp) {
        return new ChainedOp.ToList<T, T>(nextOp) {
            private int nullCount = 0;

            @Override
            public void accept(T value) {
                if (value == null) {
                    nullCount++;
                } else {
                    super.accept(value);
                }
            }

            @Override
            public void end() {
                int count = nullCount;
                nextOp.begin(elements.size() + count);
                if (isShortCircuitRequested) {
                    for (int i = 0; i < count && !nextOp.canShortCircuit(); i++) {
                        nextOp.accept(null);
                    }
                    for (T value : elements) {
                        if (nextOp.canShortCircuit()) {
                            break;
                        }
                        nextOp.accept(value);
                    }
                } else {
                    for (int i = 0; i < count; i++) {
                        nextOp.accept(null);
                    }
                    elements.forEach(nextOp);
                }
                nextOp.end();
                elements = null;
            }
        };
    }
}
