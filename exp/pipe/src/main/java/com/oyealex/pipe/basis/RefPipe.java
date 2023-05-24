package com.oyealex.pipe.basis;

import com.oyealex.pipe.assist.Tuple;
import com.oyealex.pipe.basis.api.DoublePipe;
import com.oyealex.pipe.basis.api.IntPipe;
import com.oyealex.pipe.basis.api.LongPipe;
import com.oyealex.pipe.basis.api.Pipe;
import com.oyealex.pipe.basis.api.policy.MergePolicy;
import com.oyealex.pipe.basis.api.policy.MergeRemainingPolicy;
import com.oyealex.pipe.basis.api.policy.PartitionPolicy;
import com.oyealex.pipe.basis.functional.LongBiConsumer;
import com.oyealex.pipe.basis.functional.LongBiFunction;
import com.oyealex.pipe.basis.functional.LongBiPredicate;
import com.oyealex.pipe.bi.BiPipe;
import com.oyealex.pipe.flag.PipeFlag;
import com.oyealex.pipe.spliterator.MoreSpliterators;
import com.oyealex.pipe.tri.TriPipe;

import java.util.Comparator;
import java.util.Iterator;
import java.util.Objects;
import java.util.Optional;
import java.util.Random;
import java.util.Spliterator;
import java.util.Spliterators;
import java.util.function.BiFunction;
import java.util.function.BinaryOperator;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.IntFunction;
import java.util.function.Predicate;
import java.util.function.Supplier;
import java.util.function.ToDoubleFunction;
import java.util.function.ToIntFunction;
import java.util.function.ToLongFunction;

import static com.oyealex.pipe.basis.Pipes.empty;
import static com.oyealex.pipe.basis.Pipes.spliterator;
import static com.oyealex.pipe.basis.api.policy.MergeRemainingPolicy.TAKE_REMAINING;
import static com.oyealex.pipe.flag.PipeFlag.DISTINCT;
import static com.oyealex.pipe.flag.PipeFlag.EMPTY;
import static com.oyealex.pipe.flag.PipeFlag.IS_NONNULL;
import static com.oyealex.pipe.flag.PipeFlag.NONNULL;
import static com.oyealex.pipe.flag.PipeFlag.NOT_DISTINCT;
import static com.oyealex.pipe.flag.PipeFlag.NOT_NONNULL;
import static com.oyealex.pipe.flag.PipeFlag.NOT_REVERSED_SORTED;
import static com.oyealex.pipe.flag.PipeFlag.NOT_SIZED;
import static com.oyealex.pipe.flag.PipeFlag.NOT_SORTED;
import static com.oyealex.pipe.flag.PipeFlag.REVERSED_SORTED;
import static com.oyealex.pipe.flag.PipeFlag.SHORT_CIRCUIT;
import static com.oyealex.pipe.flag.PipeFlag.SORTED;
import static com.oyealex.pipe.utils.MiscUtil.isStdNaturalOrder;
import static com.oyealex.pipe.utils.MiscUtil.isStdReverseOrder;
import static com.oyealex.pipe.utils.MiscUtil.naturalOrderIfNull;
import static java.lang.Long.MAX_VALUE;
import static java.util.Objects.requireNonNull;

/**
 * 基于引用的抽象流水线实现
 * <br/>
 * 此实现为引用类型的数据流水线提供支持。
 * <p/>
 * 一个典型的流水线：
 * <pre>{@code
 *     Pipes.of("This", "is", "an", "example", "of", "Pipe")
 *         .takeIf(word -> word.length() < 2)
 *         .sort()
 *         .limit(2)
 *         .toList();
 * }</pre>
 * 构造得到流水线的结构：
 * <pre><code>
 *     ┌─────┬─────────────────────┬────────────────┬────────────────┬────────────────────┐
 *     ↓     │                     │                │                │                    │
 *      Head                    takeIf            sort             limit               toList
 *  ┌──────────┐             ┌──────────┐     ┌──────────┐     ┌──────────┐     ┌──────────────────┐
 *  │ PipeHead │ ←────────── │ takeIfOp │ ←── │  SortOp  │ ←── │  SliceOp │ ←── │ reduceTerminalOp │
 *  └──────────┘             └──────────┘     └──────────┘     └──────────┘     └──────────────────┘
 *        │                        │                │                │                    │
 *        │                     wrapOp           wrapOp           wrapOp                  │
 *        ↓                        ↓                ↓                ↓                    ↓
 * ╭─────────────╮ driveData ╭──────────╮     ╭──────────╮     ╭──────────╮        ╭────────────╮ get ╔══════╗
 * │ Spliterator │ ────────→ │    Op    │ ──→ │    Op    │ ──→ │    Op    │ ─────→ │ TerminalOp │ ──→ ║ list ║
 * ╰─────────────╯           ╰──────────╯     ╰──────────╯     ╰──────────╯        ╰────────────╯     ╚══════╝
 * </code></pre>
 * 运行示意图：
 * <pre><code>
 * ┌─────────┐     ┌──────────┐      ┌────────┐             ┌────────┐  ┌──────────────────┐
 * │driveData│     │Op(takeIf)│      │Op(sort)│             │Op(sort)│  │TerminalOp(toList)│
 * └─────────┘     └──────────┘      └────────┘             └────────┘  └──────────────────┘
 *      ┆                ┆                ┆                     ┆                ┆
 *     ┌─┐              ┌─┐              ┌─┐                   ┌─┐              ┌─┐
 *     │ │ ═══begin═══▶ │ │ ═══begin═══▶ │ │ ══╗       ╔═════▶ │ │ ═══begin═══▶ │ │ ═════╗
 *     │ │ ◀───void──── │ │ ◀─────┐      │ │   ║       ║       │ │ ◀────void─── │ │   new list
 *     └─┘              └─┘       │      └─┘  new      ║       └─┘              └─┘      ║
 *      ┆                ┆      void      ┆   list     ║        ┆                ┆       ⇓
 *      ┆                ┆        │      ╭─╮   ║     begin      ┆                ┆      ╭─╮
 *      ┆                ┆        └───── │░│ ◀═╝       ║        ┆                ┆      │░│
 *      ┆                ┆               │░│           ║        ┆                ┆      │░│
 *      ┆                ┆          list │░│ ◀═════╗   ║        ┆                ┆      │░│ list
 *      ┆                ┆               │░│ ◀═╗   ║   ║        ┆                ┆      │░│
 *      ┆                ┆               ╰─╯   ║   ║   ║        ┆                ┆      ╰─╯
 *      ┆                ┆                ┆   add  ║   ║        ┆                ┆       ⇑
 *      ┆             accept           accept  ║   ║   ║        ┆                ┆       ║
 *     ╭─╮ Spliterator  ┌─┐              ┌─┐   ║   ║   ║       ╭─╮              ╭─╮     add
 *     │ │   forEach    │ │              │ │   ║   ║   ║       │ │              │ │      ║
 *     │ │ ═══════════▶ │ │ ═══════════▶ │ │ ══╝   ║   ║   ╔═▶ │ │ ═══════════▶ │ │ ═════╝
 *     │ │    accept    │ │    accept    │ │       ║   ║   ║   │ │    accept    │ │
 *     ╰─╯              └─┘              └─┘     sort  ║   ║   ╰─╯              ╰─╯
 *      ┆                ┆                ┆        ║   ║   ║    ┆                ┆
 *     ╭─╮              ╭─╮              ╭─╮       ║   ║   ║   ╭─╮              ╭─╮
 *     │ │ ════end════▶ │ │ ════end════▶ │ │ ══════╝   ║   ║   │ │ ═════end═══▶ │ │
 *     │ │              │ │              │ │ ══════════╝   ║   │ │              │ │
 *     │ │              │ │              │ │ ══forEach═════╝   │ │              │ │
 *     │ │              │ │              │ │ ═══════end══════▶ │ │              │ │
 *     │ │ ◀───void──── │ │ ◀───void──── │ │ ◀──────void────── │ │ ◀────void─── │ │
 *     ╰─╯              ╰─╯              ╰─╯                   ╰─╯              ╰─╯
 * </code></pre>
 *
 * @author oyealex
 * @see Pipe
 * @see com.oyealex.pipe.basis
 * @since 2023-03-04
 */
// TODO 2023-05-06 22:43 关注流水线的重复消费问题，参见 java.util.stream.AbstractPipeline.linkedOrConsumed
abstract class RefPipe<IN, OUT> implements Pipe<OUT> {
    /** 整条流水线的头节点，元素类型未知，非{@code null}。 */
    private final RefPipe<?, ?> headPipe;

    /** 此节点的前置节点，当且仅当此节点为头节点时为{@code null}。 */
    private final RefPipe<?, ? extends IN> prePipe;

    /** 流水线标记 */
    final int flag; // MK 2023-05-12 23:04 final标记很重要，如果后续开发移除final，则需要重新审视所有使用flag字段的地方

    RefPipe(int flag) {
        this.headPipe = this;
        this.prePipe = null;
        this.flag = flag;
    }

    /**
     * 以给定的流水线为上游，构造一个新的流水线。
     *
     * @param prePipe 上游流水线
     * @param opFlag 操作标记
     */
    RefPipe(RefPipe<?, ? extends IN> prePipe, int opFlag) {
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
        driveData((Spliterator<Object>) headPipe.takeDataSource(), terminalOp);
        return terminalOp.get();
    }

    /**
     * 以给定的数据源{@code dataSource}驱动执行当前流水线中定义的所有元素操作，并以给定的{@code tailOp}作为最终的结尾操作。
     *
     * @param dataSource 数据源
     * @param tailOp 结尾操作
     * @param <OP> 结尾操作类型
     */
    <OP extends TerminalOp<OUT, ?>> void driveData(Spliterator<Object> dataSource, OP tailOp) {
        Op<Object> wrappedOp = wrapAllOp(tailOp);
        wrappedOp.begin(dataSource.getExactSizeIfKnown());
        if (SHORT_CIRCUIT.isSet(flag | tailOp.getOpFlag())) {
            // 如果允许短路，则尝试短路遍历
            do {/*noop*/} while (!wrappedOp.canShortCircuit() && dataSource.tryAdvance(wrappedOp));
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
        for (@SuppressWarnings("rawtypes") RefPipe pipe = this; pipe.prePipe != null; pipe = pipe.prePipe) {
            // 从尾部到头部，逐级逆向封装
            wrappedOp = pipe.wrapOp(wrappedOp);
        }
        return (Op<Object>) wrappedOp;
    }

    protected boolean isFlagSet(PipeFlag pipeFlag) {
        return pipeFlag.isSet(flag);
    }

    @Override
    public Pipe<OUT> takeIf(Predicate<? super OUT> predicate) {
        requireNonNull(predicate);
        return new RefPipe<OUT, OUT>(this, NOT_SIZED) {
            @Override
            protected Op<OUT> wrapOp(Op<OUT> nextOp) {
                return SimpleOps.takeIfOp(nextOp, predicate);
            }
        };
    }

    @Override
    public Pipe<OUT> takeIfOrderly(LongBiPredicate<? super OUT> predicate) {
        requireNonNull(predicate);
        return new RefPipe<OUT, OUT>(this, NOT_SIZED) {
            @Override
            protected Op<OUT> wrapOp(Op<OUT> nextOp) {
                return SimpleOps.takeIfOrderlyOp(nextOp, predicate);
            }
        };
    }

    @Override
    public Pipe<OUT> takeLast(int count) {
        if (count < 0) {
            throw new IllegalArgumentException("The count to take last is at least 0: " + count);
        }
        if (count == 0) {
            return empty();
        }
        return new TakeOrDropLastOp<>(this, true, count);
    }

    @Override
    public Pipe<OUT> dropLast(int count) {
        if (count < 0) {
            throw new IllegalArgumentException("The count to drop last is at least 0: " + count);
        }
        if (count == 0) {
            return this;
        }
        return new TakeOrDropLastOp<>(this, false, count);
    }

    @Override
    public Pipe<OUT> takeWhile(Predicate<? super OUT> predicate) {
        return new WhileOp.TakeWhile<>(this, requireNonNull(predicate));
    }

    @Override
    public Pipe<OUT> takeWhileOrderly(LongBiPredicate<? super OUT> predicate) {
        return new WhileOp.TakeWhileOrderly<>(this, requireNonNull(predicate));
    }

    @Override
    public Pipe<OUT> dropWhile(Predicate<? super OUT> predicate) {
        return new WhileOp.DropWhile<>(this, requireNonNull(predicate));
    }

    @Override
    public Pipe<OUT> dropWhileOrderly(LongBiPredicate<? super OUT> predicate) {
        return new WhileOp.DropWhileOrderly<>(this, requireNonNull(predicate));
    }

    @Override
    public Pipe<OUT> dropNull() {
        if (isFlagSet(NONNULL)) {
            return this;
        }
        return new RefPipe<OUT, OUT>(this, NOT_SIZED | IS_NONNULL) {
            @Override
            protected Op<OUT> wrapOp(Op<OUT> nextOp) {
                return SimpleOps.takeIfOp(nextOp, Objects::nonNull);
            }
        };
    }

    @Override
    public <R> Pipe<R> map(Function<? super OUT, ? extends R> mapper) {
        requireNonNull(mapper);
        return new RefPipe<OUT, R>(this, NOT_SORTED | NOT_REVERSED_SORTED | NOT_DISTINCT | NOT_NONNULL) {
            @Override
            protected Op<OUT> wrapOp(Op<R> nextOp) {
                return SimpleOps.mapOp(nextOp, mapper);
            }
        };
    }

    @Override
    public Pipe<OUT> mapIf(Predicate<? super OUT> condition, Function<? super OUT, ? extends OUT> mapper) {
        requireNonNull(condition);
        requireNonNull(mapper);
        return new RefPipe<OUT, OUT>(this, NOT_SORTED | NOT_REVERSED_SORTED | NOT_DISTINCT | NOT_NONNULL) {
            @Override
            protected Op<OUT> wrapOp(Op<OUT> nextOp) {
                return SimpleOps.mapIfOp(nextOp, condition, mapper);
            }
        };
    }

    @Override
    public Pipe<OUT> mapIf(Function<? super OUT, Optional<? extends OUT>> mapper) {
        requireNonNull(mapper);
        return new RefPipe<OUT, OUT>(this, NOT_SORTED | NOT_REVERSED_SORTED | NOT_DISTINCT) {
            @Override
            protected Op<OUT> wrapOp(Op<OUT> nextOp) {
                return SimpleOps.mapIfOp(nextOp, mapper);
            }
        };
    }

    @Override
    public Pipe<OUT> mapNull(Supplier<? extends OUT> supplier) {
        requireNonNull(supplier);
        return new RefPipe<OUT, OUT>(this, NOT_SORTED | NOT_REVERSED_SORTED | NOT_DISTINCT | IS_NONNULL) {
            @Override
            protected Op<OUT> wrapOp(Op<OUT> nextOp) {
                return SimpleOps.mapNullOp(nextOp, supplier);
            }
        };
    }

    @Override
    public <R> Pipe<R> mapOrderly(LongBiFunction<? super OUT, ? extends R> mapper) {
        requireNonNull(mapper);
        return new RefPipe<OUT, R>(this, NOT_SORTED | NOT_REVERSED_SORTED | NOT_DISTINCT | NOT_NONNULL) {
            @Override
            protected Op<OUT> wrapOp(Op<R> nextOp) {
                return SimpleOps.mapOrderlyOp(nextOp, mapper);
            }
        };
    }

    @Override
    public IntPipe mapToInt(ToIntFunction<? super OUT> mapper) {
        throw new IllegalStateException();
    }

    @Override
    public IntPipe mapToIntOrderly(ToIntFunction<? super OUT> mapper) {
        throw new IllegalStateException();
    }

    @Override
    public LongPipe mapToLong(ToLongFunction<? super OUT> mapper) {
        throw new IllegalStateException();
    }

    @Override
    public LongPipe mapToLongOrderly(ToLongFunction<? super OUT> mapper) {
        throw new IllegalStateException();
    }

    @Override
    public DoublePipe mapToDouble(ToDoubleFunction<? super OUT> mapper) {
        throw new IllegalStateException();
    }

    @Override
    public DoublePipe mapToDoubleOrderly(ToDoubleFunction<? super OUT> mapper) {
        throw new IllegalStateException();
    }

    @Override
    public <R> Pipe<R> flatMap(Function<? super OUT, ? extends Pipe<? extends R>> mapper) {
        return new FlatMapOp.Normal<>(this, requireNonNull(mapper));
    }

    @Override
    public <R> Pipe<R> flatMapOrderly(LongBiFunction<? super OUT, ? extends Pipe<? extends R>> mapper) {
        return new FlatMapOp.Orderly<>(this, requireNonNull(mapper));
    }

    @Override
    public IntPipe flatMapToInt(Function<? super OUT, ? extends IntPipe> mapper) {
        throw new IllegalStateException();
    }

    @Override
    public IntPipe flatMapToIntOrderly(LongBiFunction<? super OUT, ? extends IntPipe> mapper) {
        throw new IllegalStateException();
    }

    @Override
    public LongPipe flatMapToLong(Function<? super OUT, ? extends LongPipe> mapper) {
        throw new IllegalStateException();
    }

    @Override
    public LongPipe flatMapToLongOrderly(LongBiFunction<? super OUT, ? extends LongPipe> mapper) {
        throw new IllegalStateException();
    }

    @Override
    public DoublePipe flatMapToDouble(Function<? super OUT, ? extends DoublePipe> mapper) {
        throw new IllegalStateException();
    }

    @Override
    public DoublePipe flatMapToDoubleOrderly(LongBiFunction<? super OUT, ? extends DoublePipe> mapper) {
        throw new IllegalStateException();
    }

    @Override
    public Pipe<Pipe<OUT>> flatMapSingleton() {
        return map(value -> spliterator(MoreSpliterators.singleton(value)));
    }

    @Override
    public <F, S> BiPipe<F, S> extendToTuple(Function<? super OUT, ? extends F> firstMapper,
        Function<? super OUT, ? extends S> secondMapper) {
        throw new IllegalStateException();
    }

    @Override
    public BiPipe<OUT, OUT> pairExtend(boolean keepLastIncompletePair) {
        throw new IllegalStateException();
    }

    @Override
    public <F, S, T> TriPipe<F, S, T> extendToTriple(Function<? super OUT, ? extends F> firstMapper,
        Function<? super OUT, ? extends S> secondMapper, Function<? super OUT, ? extends T> thirdMapper) {
        throw new IllegalStateException();
    }

    @Override
    public Pipe<OUT> distinct() {
        if (isFlagSet(DISTINCT)) {
            return this;
        }
        return isFlagSet(SORTED) || isFlagSet(REVERSED_SORTED) ? new DistinctOp.NaturalSorted<>(this) :
            new DistinctOp.Normal<>(this);
    }

    @Override
    public <K> Pipe<OUT> distinctBy(Function<? super OUT, ? extends K> mapper) {
        return new DistinctOp.NormalKeyed<>(this, requireNonNull(mapper));
    }

    @Override
    public <K> Pipe<OUT> distinctByOrderly(LongBiFunction<? super OUT, ? extends K> mapper) {
        return new DistinctOp.OrderlyKeyed<>(this, requireNonNull(mapper));
    }

    @Override
    public Pipe<OUT> sort(Comparator<? super OUT> comparator) { // TODO 2023-05-13 00:21 处理null值问题
        if ((isStdNaturalOrder(comparator) && isFlagSet(SORTED) ||
            (isStdReverseOrder(comparator) && isFlagSet(REVERSED_SORTED)))) {
            return this;
        }
        if ((isStdNaturalOrder(comparator) && isFlagSet(REVERSED_SORTED) ||
            (isStdReverseOrder(comparator) && isFlagSet(SORTED)))) {
            return reverse();
        }
        return new SortOp.Normal<>(this, naturalOrderIfNull(comparator));
    }

    @Override
    public <R> Pipe<OUT> sortByOrderly(LongBiFunction<? super OUT, ? extends R> mapper,
        Comparator<? super R> comparator) {
        requireNonNull(mapper);
        return new SortOp.Orderly<>(this, naturalOrderIfNull(comparator), mapper);
    }

    @Override
    public Pipe<OUT> reverse() {
        return new ReverseOp<>(this);
    }

    @Override
    public Pipe<OUT> shuffle(Random random) {
        return new ShuffleOp<>(this, requireNonNull(random));
    }

    @Override
    public Pipe<OUT> peek(Consumer<? super OUT> consumer) {
        requireNonNull(consumer);
        return new RefPipe<OUT, OUT>(this, EMPTY) {
            @Override
            protected Op<OUT> wrapOp(Op<OUT> nextOp) {
                return SimpleOps.peekOp(nextOp, consumer);
            }
        };
    }

    @Override
    public Pipe<OUT> peekOrderly(LongBiConsumer<? super OUT> consumer) {
        requireNonNull(consumer);
        return new RefPipe<OUT, OUT>(this, EMPTY) {
            @Override
            protected Op<OUT> wrapOp(Op<OUT> nextOp) {
                return SimpleOps.peekOrderlyOp(nextOp, consumer);
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
        return new SliceOp<>(this, 0, size);
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
        return new SliceOp<>(this, size, MAX_VALUE);
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
        return new SliceOp<>(this, startInclusive, endExclusive - startInclusive);
    }

    @Override
    public Pipe<OUT> prepend(Spliterator<? extends OUT> spliterator) {
        requireNonNull(spliterator);
        @SuppressWarnings("unchecked") Spliterator<OUT> finalSpliterator = MoreSpliterators.concat(
            (Spliterator<OUT>) spliterator, toSpliterator());
        Pipe<OUT> pipe = spliterator(finalSpliterator);
        return pipe.onClose(this::close);
    }

    @Override
    public Pipe<OUT> prepend(OUT value) {
        return prepend(MoreSpliterators.singleton(value));
    }

    @Override
    public Pipe<OUT> append(Spliterator<? extends OUT> spliterator) {
        requireNonNull(spliterator);
        @SuppressWarnings("unchecked") Spliterator<OUT> finalSpliterator = MoreSpliterators.concat(toSpliterator(),
            (Spliterator<OUT>) spliterator);
        Pipe<OUT> pipe = spliterator(finalSpliterator);
        return pipe.onClose(this::close);
    }

    @Override
    public Pipe<OUT> append(OUT value) {
        return append(MoreSpliterators.singleton(value));
    }

    @Override
    public Pipe<OUT> disperse(OUT delimiter) {
        return new RefPipe<OUT, OUT>(this,
            NOT_SIZED | NOT_SORTED | NOT_REVERSED_SORTED | NOT_DISTINCT | (delimiter == null ? NOT_NONNULL : EMPTY)) {
            @Override
            protected Op<OUT> wrapOp(Op<OUT> nextOp) {
                return SimpleOps.disperse(nextOp, delimiter);
            }
        };
    }

    @Override
    public Pipe<Pipe<OUT>> partition(int size) {
        if (size < 1) {
            throw new IllegalArgumentException("partition size cannot be less then 1, size: " + size);
        }
        return size == 1 ? flatMapSingleton() : new PartitionOp.Sized<>(this, size);
    }

    @Override
    public Pipe<Pipe<OUT>> partition(Function<? super OUT, PartitionPolicy> function) {
        return new PartitionOp.Policy<>(this, function);
    }

    @Override
    public Pipe<Pipe<OUT>> partitionOrderly(LongBiFunction<? super OUT, PartitionPolicy> function) {
        return new PartitionOp.PolicyOrderly<>(this, function);
    }

    @Override
    public <S> BiPipe<OUT, S> combine(Pipe<S> secondPipe) {
        requireNonNull(secondPipe);
        throw new IllegalStateException();
    }

    @Override
    public <T, R> Pipe<R> merge(Pipe<? extends T> pipe, BiFunction<? super OUT, ? super T, MergePolicy> mergeHandle,
        BiFunction<? super OUT, MergePolicy, ? extends R> oursMapper,
        BiFunction<? super T, MergePolicy, ? extends R> theirsMapper, MergeRemainingPolicy remainingPolicy) {
        onClose(pipe::close);
        return new RefPipe<OUT, R>(this, NOT_SORTED | NOT_DISTINCT | NOT_SIZED | NOT_NONNULL | NOT_REVERSED_SORTED) {
            @Override
            protected Op<OUT> wrapOp(Op<R> nextOp) {
                return new MergeOp<>(nextOp, requireNonNull(pipe).toSpliterator(), requireNonNull(mergeHandle),
                    requireNonNull(oursMapper), requireNonNull(theirsMapper),
                    remainingPolicy == null ? TAKE_REMAINING : remainingPolicy);
            }
        };
    }

    @Override
    public void forEach(Consumer<? super OUT> action) {
        requireNonNull(action);
        evaluate(SimpleOps.forEachOp(action));
    }

    @Override
    public void forEachOrderly(LongBiConsumer<? super OUT> action) {
        requireNonNull(action);
        evaluate(SimpleOps.forEachOrderlyOp(action));
    }

    @Override
    public Optional<OUT> reduce(BinaryOperator<OUT> operator) {
        return evaluate(SimpleOps.reduceTerminalOp(operator));
    }

    @Override
    public <R> R reduce(R initVar, BiFunction<? super R, ? super OUT, ? extends R> reducer) {
        return evaluate(SimpleOps.reduceTerminalOp(initVar, reducer));
    }

    @Override
    public Optional<OUT> min(Comparator<? super OUT> comparator) { // TODO 2023-05-13 00:21 处理null值问题
        if (isStdNaturalOrder(comparator) && isFlagSet(SORTED) ||
            isStdReverseOrder(comparator) && isFlagSet(REVERSED_SORTED)) {
            return findFirst();
        }
        if (isStdNaturalOrder(comparator) && isFlagSet(REVERSED_SORTED) ||
            isStdReverseOrder(comparator) && isFlagSet(SORTED)) {
            return findLast();
        }
        return evaluate(SimpleOps.minTerminalOp(naturalOrderIfNull(comparator)));
    }

    @Override
    public <K> Optional<OUT> minByOrderly(LongBiFunction<? super OUT, ? extends K> mapper,
        Comparator<? super K> comparator) {
        requireNonNull(mapper);
        return evaluate(SimpleOps.minByOrderlyTerminalOp(mapper, naturalOrderIfNull(comparator))).map(
            result -> result.second);
    }

    @Override
    public Tuple<Optional<OUT>, Optional<OUT>> minMax(Comparator<? super OUT> comparator) {
        if (isStdNaturalOrder(comparator) && isFlagSet(SORTED) ||
            isStdReverseOrder(comparator) && isFlagSet(REVERSED_SORTED)) {
            return findFirstLast();
        }
        if (isStdNaturalOrder(comparator) && isFlagSet(REVERSED_SORTED) ||
            isStdReverseOrder(comparator) && isFlagSet(SORTED)) {
            return findFirstLast().swap();
        }
        return evaluate(SimpleOps.minMaxTerminalOp(naturalOrderIfNull(comparator)));
    }

    @Override
    public <K> Tuple<Optional<OUT>, Optional<OUT>> minMaxByOrderly(LongBiFunction<? super OUT, ? extends K> mapper,
        Comparator<? super K> comparator) {
        requireNonNull(mapper);
        return evaluate(SimpleOps.minMaxByOrderlyTerminalOp(mapper, naturalOrderIfNull(comparator))).map(
            first -> first.map(Tuple::getSecond), second -> second.map(Tuple::getSecond));
    }

    @Override
    public long count() {
        return evaluate(SimpleOps.countOp());
    }

    @Override
    public boolean anyMatch(Predicate<? super OUT> predicate) {
        requireNonNull(predicate);
        return evaluate(SimpleOps.anyMatchTerminalOp(predicate));
    }

    @Override
    public boolean allMatch(Predicate<? super OUT> predicate) {
        requireNonNull(predicate);
        return evaluate(SimpleOps.allMatchTerminalOp(predicate));
    }

    @Override
    public boolean anyNull() {
        if (isFlagSet(NONNULL)) {
            return false;
        }
        return anyMatch(Objects::isNull);
    }

    @Override
    public boolean allNull() {
        if (isFlagSet(NONNULL)) {
            return false;
        }
        return allMatch(Objects::isNull);
    }

    @Override
    public Optional<OUT> findFirst() {
        return evaluate(SimpleOps.findFirstTerminalOp());
    }

    @Override
    public Optional<OUT> findLast() {
        return evaluate(SimpleOps.findLastTerminalOp());
    }

    @Override
    public Tuple<Optional<OUT>, Optional<OUT>> findFirstLast() {
        return evaluate(SimpleOps.findFirstLastTerminalOp());
    }

    @Override
    public Iterator<OUT> toIterator() {
        return Spliterators.iterator(toSpliterator());
    }

    @Override
    public OUT[] toArray(IntFunction<OUT[]> generator) {
        return evaluate(new ToArrayTerminalOp<>(generator));
    }

    @Override
    @SuppressWarnings("unchecked")
    public Spliterator<OUT> toSpliterator() {
        return this == headPipe ? (Spliterator<OUT>) headPipe.takeDataSource() :
            new PipeSpliterator<>(this, (Spliterator<Object>) headPipe.takeDataSource());
    }

    @Override
    public <K> BiPipe<K, Pipe<OUT>> groupAndExtend(Function<? super OUT, ? extends K> classifier) {
        throw new IllegalStateException();
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

    @Override
    public Pipe<OUT> debug() {
        return new RefPipe<OUT, OUT>(this, EMPTY) {
            @Override
            protected Op<OUT> wrapOp(Op<OUT> nextOp) {
                return new ChainedOp<OUT, OUT>(nextOp) {
                    @Override
                    public void begin(long size) {
                        super.begin(size);
                    }

                    @Override
                    public void end() {
                        super.end();
                    }

                    @Override
                    public void accept(OUT value) {
                        nextOp.accept(value);
                    }

                    @Override
                    public boolean canShortCircuit() {
                        return super.canShortCircuit();
                    }
                };
            }
        };
    }
}
