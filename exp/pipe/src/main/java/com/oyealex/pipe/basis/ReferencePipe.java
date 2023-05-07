package com.oyealex.pipe.basis;

import com.oyealex.pipe.basis.functional.IntBiConsumer;
import com.oyealex.pipe.basis.functional.LongBiConsumer;
import com.oyealex.pipe.basis.functional.LongBiFunction;
import com.oyealex.pipe.basis.functional.LongBiPredicate;
import com.oyealex.pipe.basis.op.Op;
import com.oyealex.pipe.basis.op.Ops;
import com.oyealex.pipe.basis.op.TerminalOp;
import com.oyealex.pipe.flag.PipeFlag;
import com.oyealex.pipe.spliterator.ConcatSpliterator;

import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.Spliterator;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;

import static com.oyealex.pipe.basis.Pipes.empty;
import static com.oyealex.pipe.flag.PipeFlag.IS_DISTINCT;
import static com.oyealex.pipe.flag.PipeFlag.IS_REVERSED_SORTED;
import static com.oyealex.pipe.flag.PipeFlag.IS_SHORT_CIRCUIT;
import static com.oyealex.pipe.flag.PipeFlag.IS_SORTED;
import static com.oyealex.pipe.flag.PipeFlag.NOTHING;
import static com.oyealex.pipe.flag.PipeFlag.NOT_DISTINCT;
import static com.oyealex.pipe.flag.PipeFlag.NOT_REVERSED_SORTED;
import static com.oyealex.pipe.flag.PipeFlag.NOT_SIZED;
import static com.oyealex.pipe.flag.PipeFlag.NOT_SORTED;
import static com.oyealex.pipe.flag.PipeFlag.REVERSED_SORTED;
import static com.oyealex.pipe.flag.PipeFlag.SORTED;
import static java.lang.Long.MAX_VALUE;
import static java.util.Objects.requireNonNull;

/**
 * 基于引用的抽象流水线
 *
 * @author oyealex
 * @since 2023-03-04
 */
// TODO 2023-05-06 22:43 关注流水线的重复消费问题，参见 java.util.stream.AbstractPipeline.linkedOrConsumed
abstract class ReferencePipe<IN, OUT> implements Pipe<OUT> {
    /** 整条流水线的头节点，元素类型未知，非{@code null}。 */
    private final ReferencePipe<?, ?> headPipe;

    /** 此节点的前置节点，当且仅当此节点为头节点时为{@code null}。 */
    private final ReferencePipe<?, ? extends IN> prePipe;

    /** 流水线标记 */
    final int flag;

    ReferencePipe(int flag) {
        this.headPipe = this;
        this.prePipe = null;
        this.flag = flag;
    }

    ReferencePipe(ReferencePipe<?, ? extends IN> prePipe, int opFlag) {
        this.headPipe = prePipe.headPipe;
        this.prePipe = prePipe;
        this.flag = PipeFlag.combine(prePipe.flag, opFlag);
    }

    /**
     * 获取流水线的数据源，此数据源来自头节点。
     *
     * @return 流水线的数据源
     * @apiNote 此方法仅允许调用一次。
     * @implNote 仅头节点可以重写此方法。
     */
    protected Spliterator<?> takeDataSource() {
        return headPipe.takeDataSource();
    }

    /**
     * 将当前节点的操作和下游节点的操作封装为一个操作，此操作接受的元素为上游节点的输出元素。
     *
     * @param nextOp 下游节点的操作
     * @return 封装之后的操作
     */
    protected abstract Op<IN> wrapOp(Op<OUT> nextOp);

    @SuppressWarnings("unchecked")
    private <R> R evaluate(TerminalOp<OUT, R> terminalOp) {
        processData((Spliterator<Object>) headPipe.takeDataSource(), terminalOp);
        return terminalOp.get();
    }

    <OP extends Op<OUT>> void processData(Spliterator<Object> dataSource, OP tailOp) {
        Op<Object> wrappedOp = wrapAllOp(tailOp);
        wrappedOp.begin(dataSource.getExactSizeIfKnown());
        if (PipeFlag.SHORT_CIRCUIT.isSet(flag)) {
            // 如果允许短路，则尝试短路遍历
            do {/*noop*/} while (!wrappedOp.cancellationRequested() && dataSource.tryAdvance(wrappedOp));
        } else {
            // 否则直接执行全量遍历
            dataSource.forEachRemaining(wrappedOp);
        }
        wrappedOp.end();
    }

    /**
     * 以给定的操作作为流水线尾部操作，将整条流水线的所有节点的操作按顺序封装为一个操作。
     *
     * @param tailOp 流水线尾部操作
     * @return 封装了所有流水线节点操作的操作方法
     */
    @SuppressWarnings("unchecked")
    Op<Object> wrapAllOp(Op<OUT> tailOp) {
        Op<?> wrappedOp = tailOp;
        for (@SuppressWarnings("rawtypes") ReferencePipe pipe = this; pipe.prePipe != null; pipe = pipe.prePipe) {
            // 从尾部到头部，逐级逆向封装
            wrappedOp = pipe.wrapOp(wrappedOp);
        }
        return (Op<Object>) wrappedOp;
    }

    @Override
    public Pipe<OUT> keepIf(Predicate<? super OUT> predicate) {
        requireNonNull(predicate);
        return new ReferencePipe<>(this, NOT_SIZED) {
            @Override
            protected Op<OUT> wrapOp(Op<OUT> nextOp) {
                return Ops.filterOp(nextOp, predicate);
            }
        };
    }

    @Override
    public Pipe<OUT> keepWhile(Predicate<? super OUT> predicate) {
        return new ReferencePipe<>(this, NOTHING) {
            @Override
            protected Op<OUT> wrapOp(Op<OUT> nextOp) {
                return Ops.keepOrDropWhileOp(nextOp, true, predicate);
            }
        };
    }

    @Override
    public Pipe<OUT> keepWhileEnumerated(LongBiPredicate<? super OUT> predicate) {
        return new ReferencePipe<>(this, NOTHING) {
            @Override
            protected Op<OUT> wrapOp(Op<OUT> nextOp) {
                return Ops.keepOrDropWhileEnumeratedOp(nextOp, true, predicate);
            }
        };
    }

    @Override
    public Pipe<OUT> dropWhile(Predicate<? super OUT> predicate) {
        return new ReferencePipe<>(this, NOTHING) {
            @Override
            protected Op<OUT> wrapOp(Op<OUT> nextOp) {
                return Ops.keepOrDropWhileOp(nextOp, false, predicate);
            }
        };
    }

    @Override
    public Pipe<OUT> dropWhileEnumerated(LongBiPredicate<? super OUT> predicate) {
        return new ReferencePipe<>(this, NOTHING) {
            @Override
            protected Op<OUT> wrapOp(Op<OUT> nextOp) {
                return Ops.keepOrDropWhileEnumeratedOp(nextOp, false, predicate);
            }
        };
    }

    @Override
    public Pipe<OUT> filterEnumerated(LongBiPredicate<? super OUT> predicate) {
        requireNonNull(predicate);
        return new ReferencePipe<>(this, NOT_SIZED) {
            @Override
            protected Op<OUT> wrapOp(Op<OUT> nextOp) {
                return Ops.filterEnumeratedOp(nextOp, predicate);
            }
        };
    }

    @Override
    public <R> Pipe<R> map(Function<? super OUT, ? extends R> mapper) {
        requireNonNull(mapper);
        return new ReferencePipe<>(this, NOT_SORTED | NOT_REVERSED_SORTED | NOT_DISTINCT) {
            @Override
            protected Op<OUT> wrapOp(Op<R> nextOp) {
                return Ops.mapOp(nextOp, mapper);
            }
        };
    }

    @Override
    public <R> Pipe<R> mapEnumerated(LongBiFunction<? super OUT, ? extends R> mapper) {
        requireNonNull(mapper);
        return new ReferencePipe<>(this, NOT_SORTED | NOT_REVERSED_SORTED | NOT_DISTINCT) {
            @Override
            protected Op<OUT> wrapOp(Op<R> nextOp) {
                return Ops.mapEnumeratedOp(nextOp, mapper);
            }
        };
    }

    @Override
    public <R> Pipe<R> flatMap(Function<? super OUT, ? extends Pipe<? extends R>> mapper) {
        requireNonNull(mapper);
        return new ReferencePipe<>(this, NOT_SORTED | NOT_REVERSED_SORTED | NOT_DISTINCT | NOT_SIZED) {
            @Override
            protected Op<OUT> wrapOp(Op<R> nextOp) {
                return Ops.flatMapOP(nextOp, mapper);
            }
        };
    }

    @Override
    public Pipe<OUT> distinct() {
        return new ReferencePipe<>(this, IS_DISTINCT | NOT_SIZED) {
            @Override
            protected Op<OUT> wrapOp(Op<OUT> nextOp) {
                return Ops.distinctOp(nextOp);
            }
        };
    }

    @Override
    public <R> Pipe<OUT> distinctBy(Function<? super OUT, ? extends R> mapper) {
        requireNonNull(mapper);
        return new ReferencePipe<>(this, NOT_DISTINCT | NOT_SIZED) {
            @Override
            protected Op<OUT> wrapOp(Op<OUT> nextOp) {
                return Ops.distinctByOp(nextOp, mapper);
            }
        };
    }

    @Override
    public Pipe<OUT> sort() {
        if (PipeFlag.SORTED.isSet(flag)) {
            return this; // 已经排序了，无需再次排序
        }
        return new ReferencePipe<>(this, IS_SORTED | NOT_REVERSED_SORTED) {
            @Override
            protected Op<OUT> wrapOp(Op<OUT> nextOp) {
                return Ops.sortOp(nextOp, null);
            }
        };
    }

    @Override
    public Pipe<OUT> sort(Comparator<? super OUT> comparator) {
        requireNonNull(comparator);
        return new ReferencePipe<>(this, NOT_SORTED | NOT_REVERSED_SORTED) {
            @Override
            protected Op<OUT> wrapOp(Op<OUT> nextOp) {
                return Ops.sortOp(nextOp, comparator);
            }
        };
    }

    @Override
    public <R extends Comparable<? super R>> Pipe<OUT> sortBy(Function<? super OUT, ? extends R> mapper) {
        requireNonNull(mapper);
        return new ReferencePipe<>(this, NOT_SORTED | NOT_REVERSED_SORTED) {
            @Override
            protected Op<OUT> wrapOp(Op<OUT> nextOp) {
                return Ops.sortOp(nextOp, Comparator.comparing(mapper));
            }
        };
    }

    @Override
    public <R> Pipe<OUT> sortBy(Function<? super OUT, ? extends R> mapper, Comparator<? super R> comparator) {
        requireNonNull(mapper);
        return new ReferencePipe<>(this, NOT_SORTED | NOT_REVERSED_SORTED) {
            @Override
            protected Op<OUT> wrapOp(Op<OUT> nextOp) {
                return Ops.sortOp(nextOp, Comparator.comparing(mapper, comparator));
            }
        };
    }

    @Override
    public Pipe<OUT> reverse() {
        int opFlag = NOTHING;
        if (SORTED.isSet(flag)) {
            // 如果已排序，则标记逆排序
            opFlag |= NOT_SORTED | IS_REVERSED_SORTED;
        }
        if (REVERSED_SORTED.isSet(flag)) {
            // 如果已逆排序，则标记排序
            opFlag |= IS_SORTED | NOT_REVERSED_SORTED;
        }
        return new ReferencePipe<>(this, opFlag) {
            @Override
            protected Op<OUT> wrapOp(Op<OUT> nextOp) {
                // TODO 2023-05-08 02:02
                return null;
            }
        };
    }

    @Override
    public Pipe<OUT> peek(Consumer<? super OUT> consumer) {
        requireNonNull(consumer);
        return new ReferencePipe<>(this, NOTHING) {
            @Override
            protected Op<OUT> wrapOp(Op<OUT> nextOp) {
                return Ops.peekOp(nextOp, consumer);
            }
        };
    }

    @Override
    public Pipe<OUT> peekEnumerated(LongBiConsumer<? super OUT> consumer) {
        requireNonNull(consumer);
        return new ReferencePipe<>(this, NOTHING) {
            @Override
            protected Op<OUT> wrapOp(Op<OUT> nextOp) {
                return Ops.peekEnumeratedOp(nextOp, consumer);
            }
        };
    }

    @Override
    public Pipe<OUT> limit(long size) {
        if (size < 0) {
            throw new IllegalArgumentException("limit size cannot be negative, size: " + size);
        }
        if (size == 0) {
            return empty();
        }
        if (size == MAX_VALUE) {
            return this;
        }
        return new ReferencePipe<>(this, NOT_SIZED | IS_SHORT_CIRCUIT) {
            @Override
            protected Op<OUT> wrapOp(Op<OUT> nextOp) {
                return Ops.sliceOp(nextOp, 0, size);
            }
        };
    }

    @Override
    public Pipe<OUT> skip(long size) {
        if (size < 0) {
            throw new IllegalArgumentException("skip size cannot be negative, size: " + size);
        }
        if (size == 0) {
            return this;
        }
        if (size == MAX_VALUE) {
            return empty();
        }
        return new ReferencePipe<>(this, NOT_SIZED) {
            @Override
            protected Op<OUT> wrapOp(Op<OUT> nextOp) {
                return Ops.sliceOp(nextOp, size, MAX_VALUE);
            }
        };
    }

    @Override
    public Pipe<OUT> slice(long startInclusive, long endExclusive) {
        if (startInclusive < 0 || endExclusive < 0 || startInclusive > endExclusive) {
            throw new IllegalArgumentException("invalid slice bound: [" + startInclusive + ", " + endExclusive + ")");
        }
        if (startInclusive == endExclusive) {
            return empty();
        }
        if (startInclusive == 0 && endExclusive == MAX_VALUE) {
            return this;
        }
        return new ReferencePipe<>(this, NOT_SIZED | IS_SHORT_CIRCUIT) {
            @Override
            protected Op<OUT> wrapOp(Op<OUT> nextOp) {
                return Ops.sliceOp(nextOp, startInclusive, endExclusive - startInclusive);
            }
        };
    }

    @Override
    public Pipe<OUT> prepend(Spliterator<? extends OUT> spliterator) {
        requireNonNull(spliterator);
        @SuppressWarnings("unchecked") ConcatSpliterator<OUT, Spliterator<OUT>> finalSpliterator
            = new ConcatSpliterator<>((Spliterator<OUT>) spliterator, toSpliterator());
        Pipe<OUT> pipe = Pipes.pipe(finalSpliterator);
        return pipe.onClose(this::close);
    }

    @Override
    public Pipe<OUT> append(Spliterator<? extends OUT> spliterator) {
        requireNonNull(spliterator);
        @SuppressWarnings("unchecked") ConcatSpliterator<OUT, Spliterator<OUT>> finalSpliterator
            = new ConcatSpliterator<>(toSpliterator(), (Spliterator<OUT>) spliterator);
        Pipe<OUT> pipe = Pipes.pipe(finalSpliterator);
        return pipe.onClose(this::close);
    }

    @Override
    public Pipe<Pipe<OUT>> partition(int size) {
        if (size < 1) {
            throw new IllegalArgumentException("partition size cannot be less then 1, size: " + size);
        }
        return new ReferencePipe<>(this, NOTHING) {
            @Override
            protected Op<OUT> wrapOp(Op<Pipe<OUT>> nextOp) {
                return Ops.partitionOp(nextOp, size);
            }
        };
    }

    @Override
    public void forEach(Consumer<? super OUT> action) {
        requireNonNull(action);
        evaluate(Ops.forEachOp(action));
    }

    @Override
    public void forEachEnumerated(LongBiConsumer<? super OUT> action) {
        requireNonNull(action);
        evaluate(Ops.forEachEnumeratedOp(action));
    }

    @Override
    public Optional<OUT> min() {
        return evaluate(Ops.minMaxOp(true, null));
    }

    @Override
    public Optional<OUT> min(Comparator<? super OUT> comparator) {
        return evaluate(Ops.minMaxOp(true, comparator));
    }

    @Override
    public Optional<OUT> max() {
        return evaluate(Ops.minMaxOp(false, null));
    }

    @Override
    public Optional<OUT> max(Comparator<? super OUT> comparator) {
        return evaluate(Ops.minMaxOp(false, comparator));
    }

    @Override
    public long count() {
        return evaluate(Ops.countOp());
    }

    @Override
    public Optional<OUT> findFirst() {
        return Pipe.super.findFirst();
    }

    @Override
    public Optional<OUT> findLast() {
        return Pipe.super.findLast();
    }

    @Override
    public Optional<OUT> findAny() {
        return Pipe.super.findAny();
    }

    @Override
    @SuppressWarnings("unchecked")
    public Spliterator<OUT> toSpliterator() {
        return this == headPipe ? (Spliterator<OUT>) headPipe.takeDataSource() :
            new PipeSpliterator<>(this, (Spliterator<Object>) headPipe.takeDataSource());
    }

    @Override
    public List<OUT> toList() {
        return evaluate(Containers.makeToListTerminalOp());
    }

    @Override
    public Pipe<OUT> onClose(Runnable closeAction) {
        headPipe.onClose(requireNonNull(closeAction));
        return this;
    }

    @Override
    public void close() {
        headPipe.close();
    }
}
