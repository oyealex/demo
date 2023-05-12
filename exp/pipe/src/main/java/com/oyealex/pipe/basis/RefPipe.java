package com.oyealex.pipe.basis;

import com.oyealex.pipe.basis.api.DoublePipe;
import com.oyealex.pipe.basis.api.IntPipe;
import com.oyealex.pipe.basis.api.LongPipe;
import com.oyealex.pipe.basis.api.Pipe;
import com.oyealex.pipe.basis.functional.LongBiConsumer;
import com.oyealex.pipe.basis.functional.LongBiFunction;
import com.oyealex.pipe.basis.functional.LongBiPredicate;
import com.oyealex.pipe.bi.BiPipe;
import com.oyealex.pipe.flag.PipeFlag;
import com.oyealex.pipe.spliterator.ConcatSpliterator;
import com.oyealex.pipe.tri.TriPipe;

import java.util.Collection;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Random;
import java.util.Set;
import java.util.Spliterator;
import java.util.function.BiConsumer;
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
import static com.oyealex.pipe.basis.Pipes.pipe;
import static com.oyealex.pipe.flag.PipeFlag.DISTINCT;
import static com.oyealex.pipe.flag.PipeFlag.NOTHING;
import static com.oyealex.pipe.flag.PipeFlag.NOT_DISTINCT;
import static com.oyealex.pipe.flag.PipeFlag.NOT_REVERSED_SORTED;
import static com.oyealex.pipe.flag.PipeFlag.NOT_SIZED;
import static com.oyealex.pipe.flag.PipeFlag.NOT_SORTED;
import static com.oyealex.pipe.flag.PipeFlag.REVERSED_SORTED;
import static com.oyealex.pipe.flag.PipeFlag.SHORT_CIRCUIT;
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
        processData((Spliterator<Object>) headPipe.takeDataSource(), terminalOp);
        return terminalOp.get();
    }

    /**
     * 以给定的数据源{@code dataSource}驱动执行当前流水线中定义的所有元素操作，并以给定的{@code tailOp}作为最终的结尾操作。
     *
     * @param dataSource 数据源
     * @param tailOp 结尾操作
     * @param <OP> 结尾操作类型
     */
    <OP extends TerminalOp<OUT, ?>> void processData(Spliterator<Object> dataSource, OP tailOp) {
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
    public Pipe<OUT> keepIf(Predicate<? super OUT> predicate) {
        requireNonNull(predicate);
        return new RefPipe<>(this, NOT_SIZED) {
            @Override
            protected Op<OUT> wrapOp(Op<OUT> nextOp) {
                return SimpleOps.keepIfOp(nextOp, predicate);
            }
        };
    }

    @Override
    public Pipe<OUT> keepIfOrderly(LongBiPredicate<? super OUT> predicate) {
        requireNonNull(predicate);
        return new RefPipe<>(this, NOT_SIZED) {
            @Override
            protected Op<OUT> wrapOp(Op<OUT> nextOp) {
                return SimpleOps.keepIfOrderlyOp(nextOp, predicate);
            }
        };
    }

    @Override
    public Pipe<OUT> keepWhile(Predicate<? super OUT> predicate) {
        return new WhileStage.KeepWhile<>(this, requireNonNull(predicate));
    }

    @Override
    public Pipe<OUT> keepWhileOrderly(LongBiPredicate<? super OUT> predicate) {
        return new WhileStage.KeepWhileOrderly<>(this, requireNonNull(predicate));
    }

    @Override
    public Pipe<OUT> dropWhile(Predicate<? super OUT> predicate) {
        return new WhileStage.DropWhile<>(this, requireNonNull(predicate));
    }

    @Override
    public Pipe<OUT> dropWhileOrderly(LongBiPredicate<? super OUT> predicate) {
        return new WhileStage.DropWhileOrderly<>(this, requireNonNull(predicate));
    }

    @Override
    public <R> Pipe<R> map(Function<? super OUT, ? extends R> mapper) {
        requireNonNull(mapper);
        return new RefPipe<>(this, NOT_SORTED | NOT_REVERSED_SORTED | NOT_DISTINCT) {
            @Override
            protected Op<OUT> wrapOp(Op<R> nextOp) {
                return SimpleOps.mapOp(nextOp, mapper);
            }
        };
    }

    @Override
    public <R> Pipe<R> mapOrderly(LongBiFunction<? super OUT, ? extends R> mapper) {
        requireNonNull(mapper);
        return new RefPipe<>(this, NOT_SORTED | NOT_REVERSED_SORTED | NOT_DISTINCT) {
            @Override
            protected Op<OUT> wrapOp(Op<R> nextOp) {
                return SimpleOps.mapOrderlyOp(nextOp, mapper);
            }
        };
    }

    @Override
    public IntPipe mapToInt(ToIntFunction<? super OUT> mapper) {
        throw new UnsupportedOperationException();
    }

    @Override
    public IntPipe mapToIntOrderly(ToIntFunction<? super OUT> mapper) {
        throw new UnsupportedOperationException();
    }

    @Override
    public LongPipe mapToLong(ToLongFunction<? super OUT> mapper) {
        throw new UnsupportedOperationException();
    }

    @Override
    public LongPipe mapToLongOrderly(ToLongFunction<? super OUT> mapper) {
        throw new UnsupportedOperationException();
    }

    @Override
    public DoublePipe mapToDouble(ToDoubleFunction<? super OUT> mapper) {
        throw new UnsupportedOperationException();
    }

    @Override
    public DoublePipe mapToDoubleOrderly(ToDoubleFunction<? super OUT> mapper) {
        throw new UnsupportedOperationException();
    }

    @Override
    public <R> Pipe<R> flatMap(Function<? super OUT, ? extends Pipe<? extends R>> mapper) {
        return new FlatMapStage.Normal<>(this, requireNonNull(mapper));
    }

    @Override
    public <R> Pipe<R> flatMapOrderly(LongBiFunction<? super OUT, ? extends Pipe<? extends R>> mapper) {
        return new FlatMapStage.Orderly<>(this, requireNonNull(mapper));
    }

    @Override
    public IntPipe flatMapToInt(Function<? super OUT, ? extends IntPipe> mapper) {
        throw new UnsupportedOperationException();
    }

    @Override
    public IntPipe flatMapToIntOrderly(LongBiFunction<? super OUT, ? extends IntPipe> mapper) {
        throw new UnsupportedOperationException();
    }

    @Override
    public LongPipe flatMapToLong(Function<? super OUT, ? extends LongPipe> mapper) {
        throw new UnsupportedOperationException();
    }

    @Override
    public LongPipe flatMapToLongOrderly(LongBiFunction<? super OUT, ? extends LongPipe> mapper) {
        throw new UnsupportedOperationException();
    }

    @Override
    public DoublePipe flatMapToDouble(Function<? super OUT, ? extends DoublePipe> mapper) {
        throw new UnsupportedOperationException();
    }

    @Override
    public DoublePipe flatMapToDoubleOrderly(LongBiFunction<? super OUT, ? extends DoublePipe> mapper) {
        throw new UnsupportedOperationException();
    }

    @Override
    public Pipe<Pipe<OUT>> flatMapSingleton() {
        return map(var -> pipe(SimpleSpliterators.singleton(var)));
    }

    @Override
    public <F, S> BiPipe<F, S> extendToTuple(Function<? super OUT, ? extends F> firstMapper,
        Function<? super OUT, ? extends S> secondMapper) {
        throw new UnsupportedOperationException();
    }

    @Override
    public <F, S, T> TriPipe<F, S, T> extendToTriple(Function<? super OUT, ? extends F> firstMapper,
        Function<? super OUT, ? extends S> secondMapper, Function<? super OUT, ? extends T> thirdMapper) {
        throw new UnsupportedOperationException();
    }

    @Override
    public Pipe<OUT> distinct() {
        if (isFlagSet(DISTINCT)) {
            return this;
        }
        return isFlagSet(SORTED) || isFlagSet(REVERSED_SORTED) ? new DistinctStage.NaturalSorted<>(this) :
            new DistinctStage.Normal<>(this);
    }

    @Override
    public <R> Pipe<OUT> distinctBy(Function<? super OUT, ? extends R> mapper) {
        return new DistinctStage.NormalKeyed<>(this, requireNonNull(mapper));
    }

    @Override
    public <R> Pipe<OUT> distinctByOrderly(LongBiFunction<? super OUT, ? extends R> mapper) {
        return new DistinctStage.OrderlyKeyed<>(this, requireNonNull(mapper));
    }

    @Override
    public Pipe<OUT> sort() {
        if (isFlagSet(SORTED)) { // 已经自然有序，可以省略本次排序
            return this;
        }
        if (isFlagSet(REVERSED_SORTED)) { // 已经自然逆序，可以直接逆序
            return reverse();
        }
        return new SortStage.Normal<>(this, null);
    }

    @Override
    public Pipe<OUT> sort(Comparator<? super OUT> comparator) {
        return comparator == null ? sort() : new SortStage.Normal<>(this, comparator);
    }

    @Override
    public <R extends Comparable<? super R>> Pipe<OUT> sortByOrderly(LongBiFunction<? super OUT, ? extends R> mapper) {
        return new SortStage.Orderly<>(this, Comparator.naturalOrder(), mapper);
    }

    @Override
    public <R> Pipe<OUT> sortByOrderly(LongBiFunction<? super OUT, ? extends R> mapper,
        Comparator<? super R> comparator) {
        return new SortStage.Orderly<>(this, comparator, mapper);
    }

    @Override
    public Pipe<OUT> reverse() {
        return new ReverseStage<>(this);
    }

    @Override
    public Pipe<OUT> shuffle(Random random) {
        return new ShuffleStage<>(this, requireNonNull(random));
    }

    @Override
    public Pipe<OUT> peek(Consumer<? super OUT> consumer) {
        requireNonNull(consumer);
        return new RefPipe<>(this, NOTHING) {
            @Override
            protected Op<OUT> wrapOp(Op<OUT> nextOp) {
                return SimpleOps.peekOp(nextOp, consumer);
            }
        };
    }

    @Override
    public Pipe<OUT> peekOrderly(LongBiConsumer<? super OUT> consumer) {
        requireNonNull(consumer);
        return new RefPipe<>(this, NOTHING) {
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
        return new SliceStage<>(this, 0, size);
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
        return new SliceStage<>(this, size, MAX_VALUE);
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
        return new SliceStage<>(this, startInclusive, endExclusive - startInclusive);
    }

    @Override
    public Pipe<OUT> prepend(Spliterator<? extends OUT> spliterator) {
        requireNonNull(spliterator);
        @SuppressWarnings("unchecked") ConcatSpliterator<OUT, Spliterator<OUT>> finalSpliterator
            = new ConcatSpliterator<>((Spliterator<OUT>) spliterator, toSpliterator());
        Pipe<OUT> pipe = pipe(finalSpliterator);
        return pipe.onClose(this::close);
    }

    @Override
    public Pipe<OUT> append(Spliterator<? extends OUT> spliterator) {
        requireNonNull(spliterator);
        @SuppressWarnings("unchecked") ConcatSpliterator<OUT, Spliterator<OUT>> finalSpliterator
            = new ConcatSpliterator<>(toSpliterator(), (Spliterator<OUT>) spliterator);
        Pipe<OUT> pipe = pipe(finalSpliterator);
        return pipe.onClose(this::close);
    }

    @Override
    public Pipe<Pipe<OUT>> partition(int size) {
        if (size < 1) {
            throw new IllegalArgumentException("partition size cannot be less then 1, size: " + size);
        }
        return size == 1 ? flatMapSingleton() : new PartitionStage<>(this, size);
    }

    @Override
    public <S> BiPipe<OUT, S> combine(Pipe<S> secondPipe) {
        throw new UnsupportedOperationException();
    }

    @Override
    public <T, R> Pipe<R> merge(Pipe<T> pipe) {
        throw new UnsupportedOperationException();
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
    public OUT reduce(OUT identity, BinaryOperator<OUT> op) {
        throw new UnsupportedOperationException();
    }

    @Override
    public Optional<OUT> reduce(BinaryOperator<OUT> op) {
        return Optional.empty();
    }

    @Override
    public <R> R reduce(R identity, Function<? super OUT, ? extends R> mapper, BinaryOperator<R> op) {
        throw new UnsupportedOperationException();
    }

    @Override
    public Optional<OUT> min() {
        throw new UnsupportedOperationException();
    }

    @Override
    public Optional<OUT> min(Comparator<? super OUT> comparator) {
        throw new UnsupportedOperationException();
    }

    @Override
    public Optional<OUT> max() {
        throw new UnsupportedOperationException();
    }

    @Override
    public Optional<OUT> max(Comparator<? super OUT> comparator) {
        throw new UnsupportedOperationException();
    }

    @Override
    public long count() {
        return evaluate(SimpleOps.countOp());
    }

    @Override
    public boolean anyMatch(Predicate<? super OUT> predicate) {
        return false;
    }

    @Override
    public boolean allMatch(Predicate<? super OUT> predicate) {
        return false;
    }

    @Override
    public boolean noneMatch(Predicate<? super OUT> predicate) {
        return false;
    }

    @Override
    public Optional<OUT> findFirst() {
        throw new UnsupportedOperationException();
    }

    @Override
    public Optional<OUT> findLast() {
        throw new UnsupportedOperationException();
    }

    @Override
    public Iterator<OUT> iterator() {
        throw new UnsupportedOperationException();
    }

    @Override
    public <A> A[] toArray(IntFunction<A[]> generator) {
        throw new UnsupportedOperationException();
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
    public <L extends List<OUT>> List<OUT> toList(Supplier<L> listSupplier) {
        throw new UnsupportedOperationException();
    }

    @Override
    public List<OUT> toUnmodifiableList() {
        throw new UnsupportedOperationException();
    }

    @Override
    public Set<OUT> toSet() {
        throw new UnsupportedOperationException();
    }

    @Override
    public <S extends Set<OUT>> Set<OUT> toSet(Supplier<S> setSupplier) {
        throw new UnsupportedOperationException();
    }

    @Override
    public Set<OUT> toUnmodifiableSet() {
        throw new UnsupportedOperationException();
    }

    @Override
    public <C extends Collection<OUT>> C toCollection(Supplier<C> collectionSupplier) {
        throw new UnsupportedOperationException();
    }

    @Override
    public <K> Map<K, OUT> toMap(Function<? super OUT, ? extends K> keyMapper) {
        throw new UnsupportedOperationException();
    }

    @Override
    public <K, M extends Map<K, OUT>> M toMap(Supplier<M> mapSupplier, Function<? super OUT, ? extends K> keyMapper) {
        throw new UnsupportedOperationException();
    }

    @Override
    public <K> Map<K, OUT> toUnmodifiableMap(Function<? super OUT, ? extends K> keyMapper) {
        throw new UnsupportedOperationException();
    }

    @Override
    public <K> Map<K, List<OUT>> group(Function<? super OUT, ? extends K> classifier) {
        throw new UnsupportedOperationException();
    }

    @Override
    public <K, V> Map<K, V> groupAndThen(Function<? super OUT, ? extends K> classifier,
        Function<List<OUT>, V> finisher) {
        throw new UnsupportedOperationException();
    }

    @Override
    public <K> Map<K, List<OUT>> groupAndExecute(Function<? super OUT, ? extends K> classifier,
        BiConsumer<K, List<OUT>> action) {
        throw new UnsupportedOperationException();
    }

    @Override
    public String join(CharSequence delimiter, CharSequence prefix, CharSequence suffix) {
        throw new UnsupportedOperationException();
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
