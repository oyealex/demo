package com.oyealex.pipe.basis;

import com.oyealex.pipe.basis.functional.LongBiConsumer;
import com.oyealex.pipe.basis.functional.LongBiFunction;
import com.oyealex.pipe.basis.functional.LongBiPredicate;
import com.oyealex.pipe.utils.Tuple;

import java.util.Comparator;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;

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
            public void accept(T var) {
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
            public void accept(T var) {
                if (predicate.test(var)) {
                    nextOp.accept(var);
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
            public void accept(T var) {
                if (predicate.test(index++, var)) {
                    nextOp.accept(var);
                }
            }
        };
    }

    public static <T> TerminalOp<T, Void> forEachOp(Consumer<? super T> action) {
        return new TerminalOp<>() {
            @Override
            public void accept(T var) {
                action.accept(var);
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
            public void accept(T var) {
                nextOp.accept(mapper.apply(index++, var));
            }
        };
    }

    public static <T> TerminalOp<T, Void> forEachOrderlyOp(LongBiConsumer<? super T> action) {
        return new TerminalOp<>() {
            private long index = 0L;

            @Override
            public void accept(T var) {
                action.accept(index++, var);
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
            public void accept(T var) {
                consumer.accept(var);
                nextOp.accept(var);
            }
        };
    }

    public static <T> Op<T> peekOrderlyOp(Op<T> nextOp, LongBiConsumer<? super T> consumer) {
        return new ChainedOp.Orderly<>(nextOp) {
            @Override
            public void accept(T var) {
                consumer.accept(index++, var);
                nextOp.accept(var);
            }
        };
    }

    public static <T> TerminalOp<T, Optional<T>> minTerminalOp(Comparator<? super T> comparator) {
        return new TerminalOp.KeepSingle<>() {
            @Override
            public void accept(T var) {
                if (result == null || comparator.compare(result, var) > 0) {
                    result = var;
                }
            }
        };
    }

    public static <T, K> TerminalOp<T, Optional<Tuple<K, T>>> minByOrderlyTerminalOp(
        LongBiFunction<? super T, ? extends K> mapper, Comparator<? super K> comparator) {
        return new TerminalOp.KeepSingle<>() {
            private long index = 0L;

            @Override
            public void accept(T var) {
                K key = mapper.apply(index++, var);
                if (result == null || comparator.compare(result.first, key) > 0) {
                    result = new Tuple<>(key, var);
                }
            }
        };
    }

    public static <T> TerminalOp<T, Optional<T>> findFirstTerminalOp() {
        return new TerminalOp.FindSingle<>() {
            @Override
            public void accept(T var) {
                result = var;
                found = true;
            }
        };
    }

    public static <T> TerminalOp<T, Optional<T>> findLastTerminalOp() {
        return new TerminalOp.KeepSingle<>() {
            @Override
            public void accept(T var) {
                result = var;
            }
        };
    }
}
