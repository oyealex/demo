package com.oyealex.pipe;

import com.oyealex.pipe.annotations.Classical;
import com.oyealex.pipe.annotations.Extended;
import com.oyealex.pipe.functional.IntBiConsumer;
import com.oyealex.pipe.functional.IntBiFunction;
import com.oyealex.pipe.functional.IntBiPredicate;

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
import java.util.function.UnaryOperator;
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
     * @param predicate 断言方法，满足断言的元素会保留下来
     * @return 新的流水线
     * @throws NullPointerException 当{@code predicate}为null时抛出
     * @see Stream#filter(Predicate)
     */
    @Classical
    default Pipe<T> filter(Predicate<? super T> predicate) {
        throw new UnsupportedOperationException();
    }

    /**
     * 根据给定断言过滤元素，断言支持访问的元素在流水线中的次序，从0开始计算。
     *
     * @param predicate 断言方法：第一个参数为访问的元素在流水线中的次序，从0开始计算；第二个参数为访问的元素
     * @return 新的流水线
     * @throws NullPointerException 当{@code predicate}为null时抛出
     */
    @Extended
    default Pipe<T> filterEnumerated(IntBiPredicate<? super T> predicate) {
        throw new UnsupportedOperationException();
    }

    /**
     * 将此流水线中的元素映射为其他类型。
     *
     * @param mapper 映射方法
     * @param <R>    新的类型
     * @return 包含新类型元素的流水线
     * @throws NullPointerException 当{@code mapper}为null时抛出
     * @see Stream#map(Function)
     */
    @Classical
    default <R> Pipe<R> map(Function<? super T, ? extends R> mapper) {
        throw new UnsupportedOperationException();
    }

    /**
     * 将此流水线中的元素映射为其他类型，映射方法支持访问元素在流水线中的次序，从0开始计算。
     *
     * @param mapper 映射方法：第一个参数为访问的元素在流水线中的次序，从0开始计算；第二个参数为访问的元素
     * @param <R>    新的类型
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
    @Classical
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
    @Classical
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
    @Classical
    default DoublePipe mapToDouble(ToDoubleFunction<? super T> mapper) {
        throw new UnsupportedOperationException();
    }

    /**
     * 将流水线中的元素映射为新的流水线，并按照次序拼接为一条流水线。
     *
     * @param mapper 映射方法
     * @param <R>    新流水线中的元素类型
     * @return 映射并拼接后的流水线
     * @throws NullPointerException 当{@code mapper}为null时抛出
     * @see Stream#flatMap(Function)
     */
    @Classical
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
    @Classical
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
    @Classical
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
    @Classical
    default DoublePipe flatMapToDouble(Function<? super T, ? extends DoublePipe> mapper) {
        throw new UnsupportedOperationException();
    }

    /**
     * 对流水线中的元素去重，以{@link Object#equals(Object)}为依据。
     *
     * @return 元素去重之后的流水线
     * @see Stream#distinct()
     */
    @Classical
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
    default Pipe<T> distinct(Function<? super T, ?> mapper) {
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
    @Classical
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
    @Classical
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
    @Classical
    default Pipe<T> peek(Consumer<? super T> consumer) {
        if (consumer == null) {
            return this;
        }
        throw new UnsupportedOperationException();
    }

    /**
     * 以给定方法访问流水线中的元素，访问方法支持访问元素在流水线中的次序。
     *
     * @param consumer 元素访问方法：第一个参数为访问的元素在流水线中的次序，从0开始计算；第二个参数为访问的元素
     * @return 新的流水线
     * @apiNote 不同于经典Stream会优化某些场景下的访问方法调用，Pipe不会主动优化此访问方法。
     */
    @Extended
    default Pipe<T> peekEnumerated(IntBiConsumer<? super T> consumer) {
        throw new UnsupportedOperationException();
    }

    @Classical
    default Pipe<T> limit(long maxSize) {
        throw new UnsupportedOperationException();
    }

    @Classical
    default Pipe<T> skip(long n) {
        throw new UnsupportedOperationException();
    }

    @Extended
    @SuppressWarnings({"unchecked", "varargs"})
    default Pipe<T> prepend(T... values) {
        throw new UnsupportedOperationException();
    }

    @Extended
    default Pipe<T> prepend(Iterator<? extends T> iterator) {
        throw new UnsupportedOperationException();
    }

    @Extended
    default Pipe<T> prepend(Pipe<? extends T> pipe) {
        throw new UnsupportedOperationException();
    }

    @Extended
    default Pipe<T> prepend(Stream<? extends T> stream) {
        return prepend(Pipe.from(stream));
    }

    @Extended
    @SuppressWarnings({"unchecked", "varargs"})
    default Pipe<T> append(T... values) {
        throw new UnsupportedOperationException();
    }

    @Extended
    default Pipe<T> append(Iterator<? extends T> iterator) {
        throw new UnsupportedOperationException();
    }

    @Extended
    default Pipe<T> append(Pipe<? extends T> pipe) {
        throw new UnsupportedOperationException();
    }

    @Extended
    default Pipe<T> append(Stream<? extends T> stream) {
        return append(Pipe.from(stream));
    }

    @Extended
    default Pipe<T> dropUntil(Predicate<? super T> predicate) {
        throw new UnsupportedOperationException();
    }

    @Extended
    default Pipe<T> keepUntil(Predicate<? super T> predicate) {
        throw new UnsupportedOperationException();
    }

    @Extended
    default Pipe<T> nonNull() {
        return filter(Objects::nonNull);
    }

    @Extended
    default Pipe<T> nonNull(Function<? super T, ?> mapper) {
        Objects.requireNonNull(mapper);
        return filter(value -> mapper.apply(value) != null);
    }

    @Extended
    default Pipe<List<T>> partition(int size) {
        return flatPartition(size).map(Pipe::toList);
    }

    @Extended
    default <R> Pipe<R> partitionAndThen(int size, Function<List<T>, R> finisher) {
        throw new UnsupportedOperationException();
    }

    @Extended
    default Pipe<List<T>> partitionAndExecute(int size, Consumer<List<T>> action) {
        throw new UnsupportedOperationException();
    }

    @Extended
    default Pipe<Pipe<T>> flatPartition(int size) {
        throw new UnsupportedOperationException();
    }

    @Classical
    default void forEach(Consumer<? super T> action) {
        throw new UnsupportedOperationException();
    }

    @Classical
    default void forEachEnumerated(IntBiConsumer<? super T> consumer) {
        throw new UnsupportedOperationException();
    }

    @Classical
    default T reduce(T identity, BinaryOperator<T> op) {
        throw new UnsupportedOperationException();
    }

    @Classical
    default Optional<T> reduce(BinaryOperator<T> op) {
        throw new UnsupportedOperationException();
    }

    @Classical
    default <R> R reduce(R identity, Function<? super T, ? extends R> mapper, BinaryOperator<R> op) {
        throw new UnsupportedOperationException();
    }

    @Classical
    default Optional<T> min(Comparator<? super T> comparator) {
        throw new UnsupportedOperationException();
    }

    @Classical
    default Optional<T> max(Comparator<? super T> comparator) {
        throw new UnsupportedOperationException();
    }

    @Classical
    default long count() {
        throw new UnsupportedOperationException();
    }

    @Classical
    default boolean anyMatch(Predicate<? super T> predicate) {
        throw new UnsupportedOperationException();
    }

    @Classical
    default boolean allMatch(Predicate<? super T> predicate) {
        throw new UnsupportedOperationException();
    }

    @Classical
    default boolean noneMatch(Predicate<? super T> predicate) {
        throw new UnsupportedOperationException();
    }

    @Classical
    default Optional<T> findFirst() {
        throw new UnsupportedOperationException();
    }

    @Classical
    default Optional<T> findAny() {
        return findFirst();
    }

    @Classical
    default Iterator<T> iterator() {
        throw new UnsupportedOperationException();
    }

    @Classical
    default <A> A[] toArray(IntFunction<A[]> generator) {
        throw new UnsupportedOperationException();
    }

    @Extended
    default List<T> toList() {
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
    default Set<T> toUnmodifiableSet() {
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
    default <K, M extends Map<K, T>> M toSizedMap(IntFunction<M> mapSupplier,
        Function<? super T, ? extends K> keyMapper) {
        throw new UnsupportedOperationException();
    }

    @Extended
    default <K> Map<K, T> toUnmodifiableMap(Function<? super T, ? extends K> keyMapper) {
        throw new UnsupportedOperationException();
    }

    @Extended
    default <R extends Collection<T>> R toCollection(Supplier<R> collectionSupplier) {
        throw new UnsupportedOperationException();
    }

    @Extended
    default <R extends Collection<T>> R toSizedCollection(IntFunction<R> collectionSupplier) {
        throw new UnsupportedOperationException();
    }

    @Extended
    default <K> Map<K, List<T>> group(Function<? super T, ? extends K> classifier) {
        throw new UnsupportedOperationException();
    }

    @Extended
    default <K, R> Map<K, R> groupAndThen(Function<? super T, ? extends K> classifier, Function<List<T>, R> finisher) {
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

    @Classical
    Pipe<T> onClose(Runnable closeAction);

    @Override
    void close();

    @Classical

    static <T> Pipe<T> empty() {
        throw new UnsupportedOperationException();
    }

    @SafeVarargs
    @SuppressWarnings("varargs")
    static <T> Pipe<T> of(T... values) {
        throw new UnsupportedOperationException();
    }

    static <T> Pipe<T> iterate(final T seed, final UnaryOperator<T> generator) {
        throw new UnsupportedOperationException();
    }

    static <T> Pipe<T> generate(Supplier<T> supplier) {
        throw new UnsupportedOperationException();
    }

    @Extended
    @SafeVarargs
    @SuppressWarnings("varargs")
    static <T> Pipe<T> concat(Pipe<? extends T>... pipe) {
        if (pipe == null || pipe.length == 0) {
            return empty();
        }
        throw new UnsupportedOperationException();
    }

    @Extended
    static <T> Pipe<T> from(Stream<? extends T> stream) {
        throw new UnsupportedOperationException();
    }

    @Extended
    static <T> Pipe<T> from(Iterator<? extends T> iterator) {
        throw new UnsupportedOperationException();
    }

    @Extended
    static <T> Pipe<T> from(Iterable<? extends T> iterable) {
        return from(iterable.iterator());
    }
}
