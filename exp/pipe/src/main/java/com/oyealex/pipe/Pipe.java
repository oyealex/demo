package com.oyealex.pipe;

import com.oyealex.pipe.annotations.Extended;
import com.oyealex.pipe.functional.IntBiConsumer;
import com.oyealex.pipe.functional.IntBiFunction;
import com.oyealex.pipe.functional.IntBiPredicate;
import com.oyealex.pipe.functional.LongBiPredicate;

import java.util.Collection;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
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
import java.util.stream.Stream;

/**
 * 流水线接口
 *
 * @param <T> 数据类型
 * @author oyealex
 * @since 2023-02-09
 */
public interface Pipe<T> extends AutoCloseable {
    /**
     * 根据给定断言过滤元素。
     *
     * @param predicate 断言方法，满足断言的元素会保留下来。
     * @return 新的流水线
     * @throws NullPointerException 当{@code predicate}为null时抛出
     * @see Stream#filter(Predicate)
     */
    Pipe<T> filter(Predicate<? super T> predicate);

    /**
     * 根据给定断言的否定结果过滤元素。
     *
     * @param predicate 断言方法，不满足断言的元素会保留下来。
     * @return 新的流水线
     * @throws NullPointerException 当{@code predicate}为null时抛出
     * @see Stream#filter(Predicate)
     */
    default Pipe<T> filterReversed(Predicate<? super T> predicate) {
        Objects.requireNonNull(predicate);
        return filter(predicate.negate());
    }

    /**
     * 根据给定断言过滤元素，断言支持访问的元素在流水线中的次序，从0开始计算，使用{@code int}类型的数据表示次序。
     *
     * @param predicate 断言方法：第一个参数为访问的元素在流水线中的次序，从0开始计算；第二个参数为访问的元素。
     * @return 新的流水线
     * @throws NullPointerException 当{@code predicate}为null时抛出
     * @apiNote 如果元素数量超过了 {@link Integer#MAX_VALUE}，则会导致数据溢出为负数，
     * 如果预估数据数量超过此最大值，请使用 {@link #filterEnumeratedLong(LongBiPredicate)}。
     * @see #filterEnumeratedLong(LongBiPredicate)
     */
    @Extended
    Pipe<T> filterEnumerated(IntBiPredicate<? super T> predicate);

    /**
     * 根据给定断言过滤元素，断言支持访问的元素在流水线中的次序，从0开始计算，使用{@code long}类型的数据表示次序。
     *
     * @param predicate 断言方法：第一个参数为访问的元素在流水线中的次序，从0开始计算；第二个参数为访问的元素。
     * @return 新的流水线
     * @throws NullPointerException 当{@code predicate}为null时抛出
     */
    @Extended
    Pipe<T> filterEnumeratedLong(LongBiPredicate<? super T> predicate);

    /**
     * 将此流水线中的元素映射为其他类型。
     *
     * @param mapper 映射方法
     * @param <R> 新的类型
     * @return 包含新类型元素的流水线
     * @throws NullPointerException 当{@code mapper}为null时抛出
     * @see Stream#map(Function)
     */
    default <R> Pipe<R> map(Function<? super T, ? extends R> mapper) {
        throw new UnsupportedOperationException();
    }

    /**
     * 将此流水线中的元素映射为其他类型，映射方法支持访问元素在流水线中的次序，从0开始计算。
     *
     * @param mapper 映射方法：第一个参数为访问的元素在流水线中的次序，从0开始计算；第二个参数为访问的元素。
     * @param <R> 新的类型
     * @return 包含新类型元素的流水线
     * @throws NullPointerException 当{@code mapper}为null时抛出
     */
    @Extended
    default <R> Pipe<R> mapEnumerated(IntBiFunction<? super T, ? extends R> mapper) {
        throw new UnsupportedOperationException();
    }

    /**
     * 将流水线中的元素映射为int类型。
     *
     * @param mapper 映射方法
     * @return int流水线
     * @throws NullPointerException 当{@code mapper}为null时抛出
     * @see Stream#mapToInt(ToIntFunction)
     */
    default IntPipe mapToInt(ToIntFunction<? super T> mapper) {
        throw new UnsupportedOperationException();
    }

    /**
     * 将流水线中的元素映射为long类型。
     *
     * @param mapper 映射方法
     * @return long流水线
     * @throws NullPointerException 当{@code mapper}为null时抛出
     * @see Stream#mapToLong(ToLongFunction)
     */
    default LongPipe mapToLong(ToLongFunction<? super T> mapper) {
        throw new UnsupportedOperationException();
    }

    /**
     * 将流水线中的元素映射为double类型。
     *
     * @param mapper 映射方法
     * @return double流水线
     * @throws NullPointerException 当{@code mapper}为null时抛出
     * @see Stream#mapToDouble(ToDoubleFunction)
     */
    default DoublePipe mapToDouble(ToDoubleFunction<? super T> mapper) {
        throw new UnsupportedOperationException();
    }

    /**
     * 将流水线中的元素映射为新的流水线，并按照次序拼接为一条流水线。
     *
     * @param mapper 映射方法
     * @param <R> 新流水线中的元素类型
     * @return 映射并拼接后的流水线
     * @throws NullPointerException 当{@code mapper}为null时抛出
     * @see Stream#flatMap(Function)
     */
    default <R> Pipe<R> flatMap(Function<? super T, ? extends Pipe<? extends R>> mapper) {
        throw new UnsupportedOperationException();
    }

    /**
     * 将流水线中的元素映射为新的int流水线，并按照次序拼接为一条int流水线。
     *
     * @param mapper 映射方法
     * @return 映射并拼接后的int流水线
     * @throws NullPointerException 当{@code mapper}为null时抛出
     * @see Stream#flatMapToInt(Function)
     */
    default IntPipe flatMapToInt(Function<? super T, ? extends IntPipe> mapper) {
        throw new UnsupportedOperationException();
    }

    /**
     * 将流水线中的元素映射为新的long流水线，并按照次序拼接为一条long流水线。
     *
     * @param mapper 映射方法
     * @return 映射并拼接后的long流水线
     * @throws NullPointerException 当{@code mapper}为null时抛出
     * @see Stream#flatMapToLong(Function)
     */
    default LongPipe flatMapToLong(Function<? super T, ? extends LongPipe> mapper) {
        throw new UnsupportedOperationException();
    }

    /**
     * 将流水线中的元素映射为新的double流水线，并按照次序拼接为一条double流水线。
     *
     * @param mapper 映射方法
     * @return 映射并拼接后的double流水线
     * @throws NullPointerException 当{@code mapper}为null时抛出
     * @see Stream#flatMapToDouble(Function)
     */
    default DoublePipe flatMapToDouble(Function<? super T, ? extends DoublePipe> mapper) {
        throw new UnsupportedOperationException();
    }

    /**
     * 对流水线中的元素去重，以{@link Object#equals(Object)}为依据。
     *
     * @return 元素去重之后的流水线
     * @see Stream#distinct()
     */
    default Pipe<T> distinct() {
        throw new UnsupportedOperationException();
    }

    /**
     * 对流水线中的元素去重，以给定的映射结果为依据。
     *
     * @param mapper 去重依据的映射方法
     * @return 元素去重之后的流水线
     * @throws NullPointerException 当{@code mapper}为null时抛出
     */
    @Extended
    default Pipe<T> distinctBy(Function<? super T, ?> mapper) {
        throw new UnsupportedOperationException();
    }

    /**
     * 对流水线中的元素排序，以默认顺序排序，
     * 要求元素实现了{@link Comparable}，否则可能在流水线的终结操作中抛出{@link ClassCastException}异常。
     *
     * @return 元素排序后的流水线
     * @see Stream#sorted()
     */
    // TODO 2023-04-24 00:19 关注排序稳定性
    default Pipe<T> sorted() {
        throw new UnsupportedOperationException();
    }

    /**
     * 对流水线中的元素排序，以给定的比较方法排序。
     *
     * @param comparator 元素比较方法
     * @return 元素排序后的流水线
     * @see Stream#sorted(Comparator)
     */
    // TODO 2023-04-24 00:19 关注排序稳定性
    default Pipe<T> sorted(Comparator<? super T> comparator) {
        Objects.requireNonNull(comparator);
        throw new UnsupportedOperationException();
    }

    /**
     * 将流水线中的元素按照当前顺序颠倒。
     *
     * @return 元素顺序颠倒后的流水线
     */
    @Extended
    default Pipe<T> reversed() {
        throw new UnsupportedOperationException();
    }

    /**
     * 以给定方法访问流水线中的元素。
     *
     * @param consumer 元素访问方法
     * @return 新的流水线
     * @apiNote 不同于经典Stream会优化某些场景下的访问方法调用，Pipe不会主动优化此访问方法。
     * @see Stream#peek(Consumer)
     */
    default Pipe<T> peek(Consumer<? super T> consumer) {
        if (consumer == null) {
            return this;
        }
        throw new UnsupportedOperationException();
    }

    /**
     * 以给定方法访问流水线中的元素，访问方法支持访问元素在流水线中的次序。
     *
     * @param consumer 元素访问方法：第一个参数为访问的元素在流水线中的次序，从0开始计算；第二个参数为访问的元素。
     * @return 新的流水线
     * @apiNote 不同于经典Stream会优化某些场景下的访问方法调用，Pipe不会主动优化此访问方法。
     */
    @Extended
    default Pipe<T> peekEnumerated(IntBiConsumer<? super T> consumer) {
        throw new UnsupportedOperationException();
    }

    /**
     * 仅保留给定数量的元素。
     *
     * @param size 需要保留的元素
     * @return 新的流水线
     * @throws IllegalArgumentException 当需要保留的元素数量小于0时抛出
     * @see Stream#limit(long)
     */
    Pipe<T> limit(long size);

    /**
     * 跳过指定数量的元素。
     *
     * @param size 需要跳过元素的数量
     * @return 新的流水线
     * @throws IllegalArgumentException 当需要保留的元素数量小于0时抛出
     * @see Stream#skip(long)
     */
    default Pipe<T> skip(long size) {
        if (size < 0) {
            throw new IllegalArgumentException("skip size cannot be negative, size: " + size);
        }
        if (size == 0) {
            return this;
        }
        throw new UnsupportedOperationException();
    }

    /**
     * 在流水线头部插入给定的流水线中的元素。
     *
     * @param pipe 包含需要插入到头部的元素的流水线
     * @return 新的流水线
     */
    @Extended
    default Pipe<T> prepend(Pipe<? extends T> pipe) {
        throw new UnsupportedOperationException();
    }

    /**
     * 在流水线头部插入给定的迭代器中的元素。
     *
     * @param iterator 包含需要插入到头部的元素的迭代器
     * @return 新的流水线
     */
    @Extended
    default Pipe<T> prepend(Iterator<? extends T> iterator) {
        return prepend(Pipes.from(iterator));
    }

    /**
     * 在流水线头部插入给定的流中的元素。
     *
     * @param stream 包含需要插入到头部的元素的流
     * @return 新的流水线
     */
    @Extended
    default Pipe<T> prepend(Stream<? extends T> stream) {
        return prepend(Pipes.from(stream));
    }

    /**
     * 在流水线头部插入给定的数组中的元素。
     *
     * @param values 需要插入到头部的元素
     * @return 新的流水线
     */
    @Extended
    @SuppressWarnings({"unchecked", "varargs"})
    default Pipe<T> prepend(T... values) {
        throw new UnsupportedOperationException();
    }

    /**
     * 在流水线尾部插入给定的流水线中的元素。
     *
     * @param pipe 包含需要插入到尾部的元素的流水线
     * @return 新的流水线
     */
    @Extended
    default Pipe<T> append(Pipe<? extends T> pipe) {
        throw new UnsupportedOperationException();
    }

    /**
     * 在流水线尾部插入给定的迭代器中的元素。
     *
     * @param iterator 包含需要插入到尾部的元素的迭代器
     * @return 新的流水线
     */
    @Extended
    default Pipe<T> append(Iterator<? extends T> iterator) {
        throw new UnsupportedOperationException();
    }

    /**
     * 在流水线尾部插入给定的流中的元素。
     *
     * @param stream 包含需要插入到尾部的元素的流
     * @return 新的流水线
     */
    @Extended
    default Pipe<T> append(Stream<? extends T> stream) {
        return append(Pipes.from(stream));
    }

    /**
     * 在流水线尾部插入给定的数组中的元素。
     *
     * @param values 需要插入到尾部的元素
     * @return 新的流水线
     */
    @Extended
    @SuppressWarnings({"unchecked", "varargs"})
    default Pipe<T> append(T... values) {
        throw new UnsupportedOperationException();
    }

    /**
     * 丢弃元素直到给定的断言首次为{@code True}。
     *
     * @param predicate 断言
     * @return 新的流水线
     */
    @Extended
    default Pipe<T> dropUntil(Predicate<? super T> predicate) {
        throw new UnsupportedOperationException();
    }

    /**
     * 保留元素直到给定的断言首次为{@code True}。
     *
     * @param predicate 断言
     * @return 新的流水线
     */
    @Extended
    default Pipe<T> keepUntil(Predicate<? super T> predicate) {
        throw new UnsupportedOperationException();
    }

    /**
     * 仅保留非空的元素。
     *
     * @return 新的流水线
     */
    @Extended
    default Pipe<T> nonNull() {
        return filter(Objects::nonNull);
    }

    /**
     * 仅保留按照给定映射方法结果非空的元素。
     *
     * @param mapper 映射方法
     * @return 新的流水线
     */
    @Extended
    default Pipe<T> nonNullBy(Function<? super T, ?> mapper) {
        Objects.requireNonNull(mapper);
        return filter(value -> mapper.apply(value) != null);
    }

    /**
     * 按照给定数量，对元素进行分区，并将分区结果封装为新的流水线。
     * <p/>
     * 根据实际情况，最后一个分区包含的元素数量可能不足给定大小，但不会为空分区。
     *
     * @param size 需要分区的元素数量
     * @return 新的包含已分区元素的流水线
     * @throws IllegalArgumentException 当给定的分区元素数量小于1时抛出
     */
    @Extended
    default Pipe<Pipe<T>> partition(int size) {
        throw new UnsupportedOperationException();
    }

    /**
     * 按照给定数量，对元素进行分区，并将分区结果封装为列表。
     * <p/>
     * 根据实际情况，最后一个分区包含的元素数量可能不足给定大小，但不会为空分区。
     *
     * @param size 需要分区的元素数量
     * @return 新的包含已分区元素列表的流水线
     * @throws IllegalArgumentException 当给定的分区元素数量小于1时抛出
     * @apiNote 封装的列表不保证可变性，如果明确需要分区列表可修改，请使用{@link #partitionToList(int, Supplier)}。
     */
    @Extended
    default Pipe<List<T>> partitionToList(int size) {
        if (size < 1) {
            throw new IllegalArgumentException("partition size cannot be less then 1, size: " + size);
        }
        return partition(size).map(Pipe::toList);
    }

    /**
     * 按照给定数量，对元素进行分区，并将分区结果封装为列表，列表实例由给定的{@link Supplier}提供。
     * <p/>
     * 根据实际情况，最后一个分区包含的元素数量可能不足给定大小，但不会为空分区。
     *
     * @param size 需要分区的元素数量
     * @param listSupplier 用于存储分区元素的列表的构造方法
     * @return 新的包含已分区元素列表的流水线
     * @throws IllegalArgumentException 当给定的分区元素数量小于1时抛出
     */
    @Extended
    default <L extends List<T>> Pipe<List<T>> partitionToList(int size, Supplier<L> listSupplier) {
        if (size < 1) {
            throw new IllegalArgumentException("partition size cannot be less then 1, size: " + size);
        }
        return partition(size).map(pipe -> pipe.toList(listSupplier));
    }

    /**
     * 结合第二条流水线，组合成两元组流水线。
     *
     * @param secondPipe 第二条流水线
     * @param <S> 第二条流水线中的元素类型
     * @return 新的两元组流水线
     * @apiNote 当任意流水线耗尽时，新的双元组流水线即耗尽，哪怕还有剩余元素。
     */
    default <S> BiPipe<T, S> combine(Pipe<S> secondPipe) {
        throw new UnsupportedOperationException();
    }

    /**
     * 访问流水线中的每个元素。
     *
     * @param action 访问元素的方法
     * @see Stream#forEach(Consumer)
     */
    default void forEach(Consumer<? super T> action) {
        throw new UnsupportedOperationException();
    }

    /**
     * 访问流水线中的每个元素，支持访问元素的次序。
     *
     * @param action 访问元素的方法：第一个参数为访问的元素在流水线中的次序，从0开始计算；第二个参数为访问的元素。
     */
    @Extended
    default void forEachEnumerated(IntBiConsumer<? super T> action) {
        throw new UnsupportedOperationException();
    }

    default T reduce(T identity, BinaryOperator<T> op) {
        throw new UnsupportedOperationException();
    }

    default Optional<T> reduce(BinaryOperator<T> op) {
        throw new UnsupportedOperationException();
    }

    default <R> R reduce(R identity, Function<? super T, ? extends R> mapper, BinaryOperator<R> op) {
        throw new UnsupportedOperationException();
    }

    /**
     * 获取流水线中最小的元素，以给定的比较器为比较依据。
     *
     * @param comparator 比较器
     * @return 最小的元素，如果流水线为空则返回空的{@link Optional}
     * @see Stream#min(Comparator)
     */
    default Optional<T> min(Comparator<? super T> comparator) {
        throw new UnsupportedOperationException();
    }

    /**
     * 获取流水线中最小的元素，以自然顺序为比较依据。
     * <p/>
     * 尝试将元素转为{@link Comparable}来进行比较。
     *
     * @return 最小的元素，如果流水线为空则返回空的{@link Optional}
     * @throws ClassCastException 当流水线元素无法转换为{@link Comparable}类型时抛出
     */
    @Extended
    default Optional<T> min() {
        throw new UnsupportedOperationException();
    }

    /**
     * 获取流水线中最大的元素，以给定的比较器为比较依据。
     *
     * @param comparator 比较器
     * @return 最大的元素，如果流水线为空则返回空的{@link Optional}
     * @see Stream#max(Comparator)
     */
    default Optional<T> max(Comparator<? super T> comparator) {
        throw new UnsupportedOperationException();
    }

    /**
     * 获取流水线中最大的元素，以自然顺序为比较依据。
     * <p/>
     * 尝试将元素转为{@link Comparable}来进行比较。
     *
     * @return 最大的元素，如果流水线为空则返回空的{@link Optional}
     * @throws ClassCastException 当流水线元素无法转换为{@link Comparable}类型时抛出
     */
    @Extended
    default Optional<T> max() {
        throw new UnsupportedOperationException();
    }

    /**
     * 计算当前流水线中元素的数量。
     *
     * @return 当前流水线中元素的数量
     * @see Stream#count()
     */
    default long count() {
        throw new UnsupportedOperationException();
    }

    default boolean anyMatch(Predicate<? super T> predicate) {
        throw new UnsupportedOperationException();
    }

    default boolean allMatch(Predicate<? super T> predicate) {
        throw new UnsupportedOperationException();
    }

    default boolean noneMatch(Predicate<? super T> predicate) {
        throw new UnsupportedOperationException();
    }

    /**
     * 获取流水线中的第一个元素。
     *
     * @return 流水线中的第一个元素，如果流水线为空则返回空的{@link Optional}
     * @see Stream#findFirst()
     */
    default Optional<T> findFirst() {
        throw new UnsupportedOperationException();
    }

    /**
     * 获取流水线中的最后一个元素。
     *
     * @return 流水线中的最后一个元素，如果流水线为空则返回空的{@link Optional}
     */
    @Extended
    default Optional<T> findLast() {
        throw new UnsupportedOperationException();
    }

    /**
     * 同{@link #findFirst()}。
     *
     * @return 流水线中的任一元素，如果流水线为空则返回空的{@link Optional}
     * @see Stream#findAny()
     */
    default Optional<T> findAny() {
        return findFirst();
    }

    default Iterator<T> iterator() {
        throw new UnsupportedOperationException();
    }

    default <A> A[] toArray(IntFunction<A[]> generator) {
        throw new UnsupportedOperationException();
    }

    @Extended
    default List<T> toList() {
        throw new UnsupportedOperationException();
    }

    @Extended
    default <L extends List<T>> List<T> toList(Supplier<L> listSupplier) {
        throw new UnsupportedOperationException();
    }

    @Extended
    default List<T> toUnmodifiableList() {
        throw new UnsupportedOperationException();
    }

    @Extended
    default Set<T> toSet() {
        throw new UnsupportedOperationException();
    }

    @Extended
    default <S extends Set<T>> Set<T> toSet(Supplier<S> setSupplier) {
        throw new UnsupportedOperationException();
    }

    @Extended
    default Set<T> toUnmodifiableSet() {
        throw new UnsupportedOperationException();
    }

    @Extended
    default <C extends Collection<T>> C toCollection(Supplier<C> collectionSupplier) {
        throw new UnsupportedOperationException();
    }

    @Extended
    default <K> Map<K, T> toMap(Function<? super T, ? extends K> keyMapper) {
        throw new UnsupportedOperationException();
    }

    @Extended
    default <K, M extends Map<K, T>> M toMap(Supplier<M> mapSupplier, Function<? super T, ? extends K> keyMapper) {
        throw new UnsupportedOperationException();
    }

    @Extended
    default <K> Map<K, T> toUnmodifiableMap(Function<? super T, ? extends K> keyMapper) {
        throw new UnsupportedOperationException();
    }

    @Extended
    default <K> Map<K, List<T>> group(Function<? super T, ? extends K> classifier) {
        throw new UnsupportedOperationException();
    }

    @Extended
    default <K, V> Map<K, V> groupAndThen(Function<? super T, ? extends K> classifier, Function<List<T>, V> finisher) {
        throw new UnsupportedOperationException();
    }

    @Extended
    default <K> Map<K, List<T>> groupAndExecute(Function<? super T, ? extends K> classifier,
        BiConsumer<K, List<T>> action) {
        throw new UnsupportedOperationException();
    }

    @Extended
    default String join(CharSequence delimiter, CharSequence prefix, CharSequence suffix) {
        throw new UnsupportedOperationException();
    }

    @Extended
    default String join(CharSequence delimiter) {
        return join(delimiter, "", "");
    }

    @Extended
    default String join() {
        return join("", "", "");
    }

    Pipe<T> onClose(Runnable closeAction);

    @Override
    void close();
}
