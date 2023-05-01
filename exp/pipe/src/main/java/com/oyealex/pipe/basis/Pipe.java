package com.oyealex.pipe.basis;

import com.oyealex.pipe.annotations.Extended;
import com.oyealex.pipe.basis.functional.*;
import com.oyealex.pipe.bi.BiPipe;
import com.oyealex.pipe.tri.TriPipe;

import java.util.*;
import java.util.function.*;
import java.util.stream.Stream;

import static java.util.Objects.requireNonNull;

/**
 * 流水线接口
 *
 * @param <E> 数据类型
 * @author oyealex
 * @since 2023-02-09
 */
public interface Pipe<E> extends AutoCloseable {
    /**
     * 根据给定断言过滤元素。
     *
     * @param predicate 断言方法，满足断言的元素会保留下来。
     * @return 新的流水线
     * @throws NullPointerException 当{@code predicate}为null时抛出
     * @see Stream#filter(Predicate)
     */
    Pipe<E> filter(Predicate<? super E> predicate);

    /**
     * 根据给定断言的否定结果过滤元素。
     *
     * @param predicate 断言方法，不满足断言的元素会保留下来。
     * @return 新的流水线
     * @throws NullPointerException 当{@code predicate}为null时抛出
     * @see Stream#filter(Predicate)
     */
    default Pipe<E> filterReversed(Predicate<? super E> predicate) {
        requireNonNull(predicate);
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
    Pipe<E> filterEnumerated(IntBiPredicate<? super E> predicate);

    /**
     * 根据给定断言过滤元素，断言支持访问的元素在流水线中的次序，从0开始计算，使用{@code long}类型的数据表示次序。
     *
     * @param predicate 断言方法：第一个参数为访问的元素在流水线中的次序，从0开始计算；第二个参数为访问的元素。
     * @return 新的流水线
     * @throws NullPointerException 当{@code predicate}为null时抛出
     */
    @Extended
    Pipe<E> filterEnumeratedLong(LongBiPredicate<? super E> predicate);

    /**
     * 将此流水线中的元素映射为其他类型。
     *
     * @param mapper 映射方法
     * @param <R> 新的类型
     * @return 包含新类型元素的流水线
     * @throws NullPointerException 当{@code mapper}为null时抛出
     * @see Stream#map(Function)
     */
    <R> Pipe<R> map(Function<? super E, ? extends R> mapper);

    /**
     * 将此流水线中的元素映射为其他类型，映射方法支持访问元素在流水线中的次序，从0开始计算，使用{@code int}类型的数据表示次序。
     *
     * @param mapper 映射方法：第一个参数为访问的元素在流水线中的次序，从0开始计算；第二个参数为访问的元素。
     * @param <R> 新的类型
     * @return 包含新类型元素的流水线
     * @throws NullPointerException 当{@code mapper}为null时抛出
     * @apiNote 如果元素数量超过了 {@link Integer#MAX_VALUE}，则会导致数据溢出为负数，
     * 如果预估数据数量超过此最大值，请使用 {@link #mapEnumeratedLong(LongBiFunction)}。
     */
    @Extended
    <R> Pipe<R> mapEnumerated(IntBiFunction<? super E, ? extends R> mapper);

    /**
     * 将此流水线中的元素映射为其他类型，映射方法支持访问元素在流水线中的次序，从0开始计算，使用{@code long}类型的数据表示次序。
     *
     * @param mapper 映射方法：第一个参数为访问的元素在流水线中的次序，从0开始计算；第二个参数为访问的元素。
     * @param <R> 新的类型
     * @return 包含新类型元素的流水线
     * @throws NullPointerException 当{@code mapper}为null时抛出
     */
    @Extended
    <R> Pipe<R> mapEnumeratedLong(LongBiFunction<? super E, ? extends R> mapper);

    /**
     * 将流水线中的元素映射为int类型。
     *
     * @param mapper 映射方法
     * @return int流水线
     * @throws NullPointerException 当{@code mapper}为null时抛出
     * @see Stream#mapToInt(ToIntFunction)
     */
    default IntPipe mapToInt(ToIntFunction<? super E> mapper) {
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
    default LongPipe mapToLong(ToLongFunction<? super E> mapper) {
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
    default DoublePipe mapToDouble(ToDoubleFunction<? super E> mapper) {
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
    <R> Pipe<R> flatMap(Function<? super E, ? extends Pipe<? extends R>> mapper);

    /**
     * 将流水线中的元素映射为新的int流水线，并按照次序拼接为一条int流水线。
     *
     * @param mapper 映射方法
     * @return 映射并拼接后的int流水线
     * @throws NullPointerException 当{@code mapper}为null时抛出
     * @see Stream#flatMapToInt(Function)
     */
    default IntPipe flatMapToInt(Function<? super E, ? extends IntPipe> mapper) {
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
    default LongPipe flatMapToLong(Function<? super E, ? extends LongPipe> mapper) {
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
    default DoublePipe flatMapToDouble(Function<? super E, ? extends DoublePipe> mapper) {
        throw new UnsupportedOperationException();
    }

    /**
     * 使用给定的映射方法，将此流水线扩展为两元组的流水线。
     *
     * @param firstMapper 两元组第一个元素的映射方法
     * @param secondMapper 两元组第二个元素的映射方法
     * @param <F> 两元组第一个元素的类型
     * @param <S> 两元组第二个元素的类型
     * @return 映射后的两元组流水线
     * @throws NullPointerException 当任意映射方法为null时抛出
     */
    default <F, S> BiPipe<F, S> extend(Function<? super E, ? extends F> firstMapper,
            Function<? super E, ? extends S> secondMapper) {
        throw new UnsupportedOperationException();
    }

    /**
     * 使用给定的映射方法，将此流水线扩展为三元组的流水线。
     *
     * @param firstMapper 三元组第一个元素的映射方法
     * @param secondMapper 三元组第二个元素的映射方法
     * @param thirdMapper 三元组第三个元素的映射方法
     * @param <F> 三元组第一个元素的类型
     * @param <S> 三元组第二个元素的类型
     * @param <T> 三元组第三个元素的类型
     * @return 映射后的三元组流水线
     * @throws NullPointerException 当任意映射方法为null时抛出
     */
    default <F, S, T> TriPipe<F, S, T> extendTriple(Function<? super E, ? extends F> firstMapper,
            Function<? super E, ? extends S> secondMapper, Function<? super E, ? extends T> thirdMapper) {
        throw new UnsupportedOperationException();
    }

    /**
     * 使用给定的映射方法，将此流水线扩展为两元组的流水线，其中两元组的第一个元素仍然为当前流水线中的元素。
     *
     * @param secondMapper 两元组第二个元素的映射方法
     * @param <S> 两元组第二个元素的类型
     * @return 映射后的两元组流水线
     * @throws NullPointerException 当映射方法为null时抛出
     */
    default <S> BiPipe<E, S> extendSelf(Function<? super E, ? extends S> secondMapper) {
        throw new UnsupportedOperationException();

    }

    /**
     * 对流水线中的元素去重，以{@link Object#equals(Object)}为依据。
     *
     * @return 元素去重之后的流水线
     * @see Stream#distinct()
     */
    default Pipe<E> distinct() {
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
    <R> Pipe<E> distinctKeyed(Function<? super E, ? extends R> mapper);

    /**
     * 对流水线中的元素排序，以默认顺序排序，
     * 要求元素实现了{@link Comparable}，否则可能在流水线的终结操作中抛出{@link ClassCastException}异常。
     *
     * @return 元素排序后的流水线
     * @see Stream#sorted()
     */
    // TODO 2023-04-24 00:19 关注排序稳定性
    default Pipe<E> sorted() {
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
    default Pipe<E> sorted(Comparator<? super E> comparator) {
        requireNonNull(comparator);
        throw new UnsupportedOperationException();
    }

    /**
     * 将流水线中的元素按照当前顺序颠倒。
     *
     * @return 元素顺序颠倒后的流水线
     */
    @Extended
    default Pipe<E> reversed() {
        throw new UnsupportedOperationException();
    }

    /**
     * 随机打乱流水线中的元素。
     *
     * @return 元素顺序被打乱后新的流水线
     */
    default Pipe<E> shuffle() {
        throw new UnsupportedOperationException();
    }

    /**
     * 使用给定的随机对象打乱流水线中的元素。
     *
     * @param random 用于随机元素次序的随机对象
     * @return 元素顺序被打乱后新的流水线
     */
    default Pipe<E> shuffle(Random random) {
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
    default Pipe<E> peek(Consumer<? super E> consumer) {
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
    default Pipe<E> peekEnumerated(IntBiConsumer<? super E> consumer) {
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
    Pipe<E> limit(long size);

    /**
     * 跳过指定数量的元素。
     *
     * @param size 需要跳过元素的数量
     * @return 新的流水线
     * @throws IllegalArgumentException 当需要保留的元素数量小于0时抛出
     * @see Stream#skip(long)
     */
    Pipe<E> skip(long size);

    /**
     * 在流水线头部插入给定的流水线中的元素。
     *
     * @param pipe 包含需要插入到头部的元素的流水线
     * @return 新的流水线
     */
    @Extended
    default Pipe<E> prepend(Pipe<? extends E> pipe) {
        throw new UnsupportedOperationException();
    }

    /**
     * 在流水线头部插入给定的迭代器中的元素。
     *
     * @param iterator 包含需要插入到头部的元素的迭代器
     * @return 新的流水线
     */
    @Extended
    Pipe<E> prepend(Iterator<? extends E> iterator);

    /**
     * 在流水线头部插入给定的可迭代对象中的元素。
     *
     * @param iterable 包含需要插入到头部的元素的可迭代对象
     * @return 新的流水线
     */
    @Extended
    default Pipe<E> prepend(Iterable<? extends E> iterable) {
        return prepend(iterable.iterator());
    }


    /**
     * 在流水线头部插入给定的流中的元素。
     *
     * @param stream 包含需要插入到头部的元素的流
     * @return 新的流水线
     */
    @Extended
    default Pipe<E> prepend(Stream<? extends E> stream) {
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
    default Pipe<E> prepend(E... values) {
        return prepend(Misc.arrayIterator(values));
    }

    /**
     * 在流水线尾部插入给定的流水线中的元素。
     *
     * @param pipe 包含需要插入到尾部的元素的流水线
     * @return 新的流水线
     */
    @Extended
    default Pipe<E> append(Pipe<? extends E> pipe) {
        throw new UnsupportedOperationException();
    }

    /**
     * 在流水线尾部插入给定的可迭代对象中的元素。
     *
     * @param iterable 包含需要插入到尾部的元素的可迭代对象
     * @return 新的流水线
     */
    @Extended
    default Pipe<E> append(Iterable<? extends E> iterable) {
        return append(iterable.iterator());
    }


    /**
     * 在流水线尾部插入给定的迭代器中的元素。
     *
     * @param iterator 包含需要插入到尾部的元素的迭代器
     * @return 新的流水线
     */
    @Extended
    default Pipe<E> append(Iterator<? extends E> iterator) {
        throw new UnsupportedOperationException();
    }

    /**
     * 在流水线尾部插入给定的流中的元素。
     *
     * @param stream 包含需要插入到尾部的元素的流
     * @return 新的流水线
     */
    @Extended
    default Pipe<E> append(Stream<? extends E> stream) {
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
    default Pipe<E> append(E... values) {
        return append(Misc.arrayIterator(values));
    }

    /**
     * 丢弃元素直到给定的断言首次为{@code True}。
     *
     * @param predicate 断言
     * @return 新的流水线
     */
    @Extended
    default Pipe<E> dropUntil(Predicate<? super E> predicate) {
        throw new UnsupportedOperationException();
    }

    /**
     * 保留元素直到给定的断言首次为{@code True}。
     *
     * @param predicate 断言
     * @return 新的流水线
     */
    @Extended
    default Pipe<E> keepUntil(Predicate<? super E> predicate) {
        throw new UnsupportedOperationException();
    }

    /**
     * 仅保留非空的元素。
     *
     * @return 新的流水线
     */
    @Extended
    default Pipe<E> nonNull() {
        return filter(Objects::nonNull);
    }

    /**
     * 仅保留按照给定映射方法结果非空的元素。
     *
     * @param mapper 映射方法
     * @return 新的流水线
     */
    @Extended
    default Pipe<E> nonNullBy(Function<? super E, ?> mapper) {
        requireNonNull(mapper);
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
    default Pipe<Pipe<E>> partition(int size) {
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
    default Pipe<List<E>> partitionToList(int size) {
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
    default <L extends List<E>> Pipe<List<E>> partitionToList(int size, Supplier<L> listSupplier) {
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
    default <S> BiPipe<E, S> combine(Pipe<S> secondPipe) {
        throw new UnsupportedOperationException();
    }

    /**
     * 访问流水线中的每个元素。
     *
     * @param action 访问元素的方法
     * @see Stream#forEach(Consumer)
     */
    void forEach(Consumer<? super E> action);

    /**
     * 访问流水线中的每个元素，支持访问元素的次序。
     *
     * @param action 访问元素的方法：第一个参数为访问的元素在流水线中的次序，从0开始计算；第二个参数为访问的元素。
     */
    @Extended
    default void forEachEnumerated(IntBiConsumer<? super E> action) {
        throw new UnsupportedOperationException();
    }

    default E reduce(E identity, BinaryOperator<E> op) {
        throw new UnsupportedOperationException();
    }

    default Optional<E> reduce(BinaryOperator<E> op) {
        throw new UnsupportedOperationException();
    }

    default <R> R reduce(R identity, Function<? super E, ? extends R> mapper, BinaryOperator<R> op) {
        throw new UnsupportedOperationException();
    }

    /**
     * 获取流水线中最小的元素，以给定的比较器为比较依据。
     *
     * @param comparator 比较器
     * @return 最小的元素，如果流水线为空则返回空的{@link Optional}
     * @see Stream#min(Comparator)
     */
    default Optional<E> min(Comparator<? super E> comparator) {
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
    default Optional<E> min() {
        throw new UnsupportedOperationException();
    }

    /**
     * 获取流水线中最大的元素，以给定的比较器为比较依据。
     *
     * @param comparator 比较器
     * @return 最大的元素，如果流水线为空则返回空的{@link Optional}
     * @see Stream#max(Comparator)
     */
    default Optional<E> max(Comparator<? super E> comparator) {
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
    default Optional<E> max() {
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

    default boolean anyMatch(Predicate<? super E> predicate) {
        throw new UnsupportedOperationException();
    }

    default boolean allMatch(Predicate<? super E> predicate) {
        throw new UnsupportedOperationException();
    }

    default boolean noneMatch(Predicate<? super E> predicate) {
        throw new UnsupportedOperationException();
    }

    /**
     * 获取流水线中的第一个元素。
     *
     * @return 流水线中的第一个元素，如果流水线为空则返回空的{@link Optional}
     * @see Stream#findFirst()
     */
    default Optional<E> findFirst() {
        throw new UnsupportedOperationException();
    }

    /**
     * 获取流水线中的最后一个元素。
     *
     * @return 流水线中的最后一个元素，如果流水线为空则返回空的{@link Optional}
     */
    @Extended
    default Optional<E> findLast() {
        throw new UnsupportedOperationException();
    }

    /**
     * 同{@link #findFirst()}。
     *
     * @return 流水线中的任一元素，如果流水线为空则返回空的{@link Optional}
     * @see Stream#findAny()
     */
    default Optional<E> findAny() {
        return findFirst();
    }

    default Iterator<E> iterator() {
        throw new UnsupportedOperationException();
    }

    default <A> A[] toArray(IntFunction<A[]> generator) {
        throw new UnsupportedOperationException();
    }

    @Extended
    default List<E> toList() {
        throw new UnsupportedOperationException();
    }

    @Extended
    default <L extends List<E>> List<E> toList(Supplier<L> listSupplier) {
        throw new UnsupportedOperationException();
    }

    @Extended
    default List<E> toUnmodifiableList() {
        throw new UnsupportedOperationException();
    }

    @Extended
    default Set<E> toSet() {
        throw new UnsupportedOperationException();
    }

    @Extended
    default <S extends Set<E>> Set<E> toSet(Supplier<S> setSupplier) {
        throw new UnsupportedOperationException();
    }

    @Extended
    default Set<E> toUnmodifiableSet() {
        throw new UnsupportedOperationException();
    }

    @Extended
    default <C extends Collection<E>> C toCollection(Supplier<C> collectionSupplier) {
        throw new UnsupportedOperationException();
    }

    @Extended
    default <K> Map<K, E> toMap(Function<? super E, ? extends K> keyMapper) {
        throw new UnsupportedOperationException();
    }

    @Extended
    default <K, M extends Map<K, E>> M toMap(Supplier<M> mapSupplier, Function<? super E, ? extends K> keyMapper) {
        throw new UnsupportedOperationException();
    }

    @Extended
    default <K> Map<K, E> toUnmodifiableMap(Function<? super E, ? extends K> keyMapper) {
        throw new UnsupportedOperationException();
    }

    @Extended
    default <K> Map<K, List<E>> group(Function<? super E, ? extends K> classifier) {
        throw new UnsupportedOperationException();
    }

    @Extended
    default <K, V> Map<K, V> groupAndThen(Function<? super E, ? extends K> classifier, Function<List<E>, V> finisher) {
        throw new UnsupportedOperationException();
    }

    @Extended
    default <K> Map<K, List<E>> groupAndExecute(Function<? super E, ? extends K> classifier,
            BiConsumer<K, List<E>> action) {
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

    Pipe<E> onClose(Runnable closeAction);

    @Override
    default void close() {
    }
}
