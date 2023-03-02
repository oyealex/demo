package com.oyealex.seq;

import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
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
public interface Pipe<T> extends BasePipe<T, Pipe<T>> {
    /* 继承的非终结操作 */

    Pipe<T> filter(Predicate<? super T> predicate);

    <R> Pipe<R> map(Function<? super T, ? extends R> mapper);

    IntPipe mapToInt(ToIntFunction<? super T> mapper);

    LongPipe mapToLong(ToLongFunction<? super T> mapper);

    DoublePipe mapToDouble(ToDoubleFunction<? super T> mapper);

    <R> Pipe<R> flatMap(Function<? super T, ? extends Pipe<? extends R>> mapper);

    IntPipe flatMapToInt(Function<? super T, ? extends IntPipe> mapper);

    LongPipe flatMapToLong(Function<? super T, ? extends LongPipe> mapper);

    DoublePipe flatMapToDouble(Function<? super T, ? extends DoublePipe> mapper);

    Pipe<T> distinct();

    Pipe<T> sorted();

    Pipe<T> peek(Consumer<? super T> action);

    Pipe<T> limit(long maxSize);

    Pipe<T> skip(long n);

    /* 扩展的非终结操作 */

    @SuppressWarnings({"unchecked", "varargs"})
    Pipe<T> prepend(T... values);

    Pipe<T> prepend(Iterator<? extends T> iterator);

    Pipe<T> prepend(Pipe<? extends T> pipe);

    Pipe<T> prepend(Stream<? extends T> stream);

    @SuppressWarnings({"unchecked", "varargs"})
    Pipe<T> append(T... values);

    Pipe<T> append(Iterator<? extends T> iterator);

    Pipe<T> append(Pipe<? extends T> pipe);

    Pipe<T> append(Stream<? extends T> stream);

    default Pipe<T> nonNull() {
        return filter(Objects::nonNull);
    }

    /* 继承的终结操作 */

    void forEach(Consumer<? super T> action);

    <A> A[] toArray(IntFunction<A[]> generator);

    T reduce(T identity, BinaryOperator<T> accumulator);

    Optional<T> min(Comparator<? super T> comparator);

    Optional<T> max(Comparator<? super T> comparator);

    long count();

    boolean anyMatch(Predicate<? super T> predicate);

    boolean allMatch(Predicate<? super T> predicate);

    boolean noneMatch(Predicate<? super T> predicate);

    Optional<T> findFirst();

    /* 扩展的终结操作 */

    List<T> toList();

    /* 继承的其他接口 */

    static <T> Pipe<T> empty() {
        // TODO 2023-03-03 01:03
        return null;
    }

    @SafeVarargs
    @SuppressWarnings("varargs")
    static <T> Pipe<T> of(T... values) {
        // TODO 2023-03-03 01:03
        return null;
    }

    static <T> Pipe<T> iterate(final T seed, final UnaryOperator<T> generator) {
        // TODO 2023-03-03 01:05
        return null;
    }

    static <T> Pipe<T> generate(Supplier<T> supplier) {
        // TODO 2023-03-03 01:06
        return null;
    }

    /* 扩展的其他接口 */

    @SafeVarargs
    @SuppressWarnings("varargs")
    static <T> Pipe<T> concat(Pipe<? extends T>... pipe) {
        if (pipe == null || pipe.length == 0) {
            return empty();
        }
        // TODO 2023-03-03 01:07
        return null;
    }

    static <T> Pipe<T> from(Stream<? extends T> stream) {
        // TODO 2023-03-03 01:24
        return null;
    }

    static <T> Pipe<T> from(Iterator<? extends T> iterator) {
        // TODO 2023-03-03 01:24
        return null;
    }
}
