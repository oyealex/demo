package com.oyealex.seq;

import com.oyealex.seq.annotations.Extended;
import com.oyealex.seq.functional.IntBiConsumer;

import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
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
public interface Pipe<T> extends BasePipe<Pipe<T>> {
    default Pipe<T> filter(Predicate<? super T> predicate) {
        throw new UnsupportedOperationException();
    }

    default <R> Pipe<R> map(Function<? super T, ? extends R> mapper) {
        throw new UnsupportedOperationException();
    }

    default IntPipe mapToInt(ToIntFunction<? super T> mapper) {
        throw new UnsupportedOperationException();
    }

    default LongPipe mapToLong(ToLongFunction<? super T> mapper) {
        throw new UnsupportedOperationException();
    }

    default DoublePipe mapToDouble(ToDoubleFunction<? super T> mapper) {
        throw new UnsupportedOperationException();
    }

    default <R> Pipe<R> flatMap(Function<? super T, ? extends Pipe<? extends R>> mapper) {
        throw new UnsupportedOperationException();
    }

    default IntPipe flatMapToInt(Function<? super T, ? extends IntPipe> mapper) {
        throw new UnsupportedOperationException();
    }

    default LongPipe flatMapToLong(Function<? super T, ? extends LongPipe> mapper) {
        throw new UnsupportedOperationException();
    }

    default DoublePipe flatMapToDouble(Function<? super T, ? extends DoublePipe> mapper) {
        throw new UnsupportedOperationException();
    }

    default Pipe<T> distinct() {
        throw new UnsupportedOperationException();
    }

    default Pipe<T> sorted() {
        throw new UnsupportedOperationException();
    }

    default Pipe<T> sorted(Comparator<? super T> comparator) {
        Objects.requireNonNull(comparator);
        throw new UnsupportedOperationException();
    }

    @Extended
    default Pipe<T> reversed() {
        throw new UnsupportedOperationException();
    }

    default Pipe<T> peek(Consumer<? super T> action) {
        throw new UnsupportedOperationException();
    }

    @Extended
    default Pipe<T> peekEnumerated(IntBiConsumer<? super T> consumer) {
        throw new UnsupportedOperationException();
    }

    default Pipe<T> limit(long maxSize) {
        throw new UnsupportedOperationException();
    }

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
    default Pipe<List<T>> partition(int size) {
        return flatPartition(size).map(Pipe::toList);
    }

    @Extended
    default Pipe<Pipe<T>> flatPartition(int size) {
        throw new UnsupportedOperationException();
    }

    default void forEach(Consumer<? super T> action) {
        throw new UnsupportedOperationException();
    }

    default void forEachEnumerated(IntBiConsumer<? super T> consumer) {
        throw new UnsupportedOperationException();
    }

    default <A> A[] toArray(IntFunction<A[]> generator) {
        throw new UnsupportedOperationException();
    }

    default T reduce(T identity, BinaryOperator<T> accumulator) {
        throw new UnsupportedOperationException();
    }

    default Optional<T> min(Comparator<? super T> comparator) {
        throw new UnsupportedOperationException();
    }

    default Optional<T> max(Comparator<? super T> comparator) {
        throw new UnsupportedOperationException();
    }

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

    default Optional<T> findFirst() {
        throw new UnsupportedOperationException();
    }

    default Iterator<T> iterator() {
        throw new UnsupportedOperationException();
    }

    @Extended
    default List<T> toList() {
        throw new UnsupportedOperationException();
    }

    default List<T> toUnmodifiableList() {
        throw new UnsupportedOperationException();
    }

    default <K> Map<K, T> toMap(Function<? super T, ? extends K> keyMapper, BinaryOperator<T> selector) {
        throw new UnsupportedOperationException();
    }

    default <K> Map<K, T> toUnmodifiableMap(Function<? super T, ? extends K> keyMapper, BinaryOperator<T> selector) {
        throw new UnsupportedOperationException();
    }

    default <K> Map<K, T> toMapWithNew(Function<? super T, ? extends K> keyMapper) {
        return toMap(keyMapper, (oldOne, newOne) -> newOne);
    }

    default <K> Map<K, T> toUnmodifiableMapWithNew(Function<? super T, ? extends K> keyMapper) {
        return toMap(keyMapper, (oldOne, newOne) -> newOne);
    }

    default <K> Map<K, T> toMapWithOld(Function<? super T, ? extends K> keyMapper) {
        return toMap(keyMapper, (oldOne, newOne) -> newOne);
    }

    default <K> Map<K, T> toUnmodifiableMapWithOld(Function<? super T, ? extends K> keyMapper) {
        return toMap(keyMapper, (oldOne, newOne) -> newOne);
    }

    @Extended
    default <K> Map<K, List<T>> group(Function<? super T, ? extends K> classifier) {
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

    static <T> Pipe<T> empty() {
        // TODO 2023-03-03 01:03
        throw new UnsupportedOperationException();
    }

    @SafeVarargs
    @SuppressWarnings("varargs")
    static <T> Pipe<T> of(T... values) {
        // TODO 2023-03-03 01:03
        throw new UnsupportedOperationException();
    }

    static <T> Pipe<T> iterate(final T seed, final UnaryOperator<T> generator) {
        // TODO 2023-03-03 01:05
        throw new UnsupportedOperationException();
    }

    static <T> Pipe<T> generate(Supplier<T> supplier) {
        // TODO 2023-03-03 01:06
        throw new UnsupportedOperationException();
    }

    @Extended
    @SafeVarargs
    @SuppressWarnings("varargs")
    static <T> Pipe<T> concat(Pipe<? extends T>... pipe) {
        if (pipe == null || pipe.length == 0) {
            return empty();
        }
        // TODO 2023-03-03 01:07
        throw new UnsupportedOperationException();
    }

    @Extended
    static <T> Pipe<T> from(Stream<? extends T> stream) {
        // TODO 2023-03-03 01:24
        throw new UnsupportedOperationException();
    }

    @Extended
    static <T> Pipe<T> from(Iterator<? extends T> iterator) {
        // TODO 2023-03-03 01:24
        throw new UnsupportedOperationException();
    }

    @Extended
    static <T> Pipe<T> from(Iterable<? extends T> iterable) {
        return from(iterable.iterator());
    }
}
