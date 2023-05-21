package com.oyealex.pipe.basis;

import com.oyealex.pipe.basis.api.Pipe;
import com.oyealex.pipe.spliterator.MoreSpliterators;
import com.oyealex.pipe.utils.NoInstance;

import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Spliterator;
import java.util.Spliterators;
import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.function.Supplier;
import java.util.function.UnaryOperator;
import java.util.stream.Stream;

import static java.lang.Long.MAX_VALUE;
import static java.util.Objects.requireNonNull;
import static java.util.Spliterator.IMMUTABLE;
import static java.util.Spliterator.ORDERED;

/**
 * 流水线的辅助工具类
 *
 * @author oyealex
 * @since 2023-04-28
 */
public final class Pipes extends NoInstance { // TODO 2023-05-21 19:05 添加自定义Flag的支持，用于指导生成流水线的优化
    /**
     * 构造一个空的流水线实例。
     *
     * @param <T> 流水线元素类型
     * @return 空的流水线实例
     * @see Stream#empty()
     */
    public static <T> Pipe<T> empty() {
        return new PipeHead<>(Spliterators.emptySpliterator());
    }

    /**
     * 从给定的拆分器中构建新的流水线实例。
     *
     * @param spliterator 拆分器
     * @param <T> 拆分器中的元素类型
     * @return 新的流水线实例
     */
    public static <T> Pipe<T> spliterator(Spliterator<? extends T> spliterator) {
        return new PipeHead<>(spliterator);
    }

    public static <T> Pipe<T> singleton(T singleton) {
        return spliterator(MoreSpliterators.singleton(singleton));
    }

    /**
     * 根据给定的元素构造一个新的流水线实例。
     *
     * @param values 包含在流水线中的元素
     * @param <T> 元素类型
     * @return 新的流水线实例
     */
    @SafeVarargs
    @SuppressWarnings("varargs")
    public static <T> Pipe<T> of(T... values) {
        if (values.length == 0) {
            return empty();
        }
        if (values.length == 1) {
            return singleton(values[0]);
        }
        return spliterator(Arrays.spliterator(values));
    }

    public static <T> Pipe<T> constant(T constant, int count) {
        if (count < 0) {
            throw new IllegalArgumentException("Pipe length cannot be negative: " + count);
        }
        if (count == 0) {
            return empty();
        }
        if (count == 1) {
            return singleton(constant);
        }
        return spliterator(MoreSpliterators.constant(constant, count));
    }

    public static <T> Pipe<T> optional(T value) {
        return value == null ? empty() : singleton(value);
    }

    public static <T> Pipe<T> keys(Map<T, ?> map) {
        throw new UnsupportedOperationException();
    }

    public static <T, V> Pipe<T> keys(Map<T, V> map, Predicate<? super V> valuePredicate) {
        throw new UnsupportedOperationException();
    }

    public static <T> Pipe<T> values(Map<?, T> map) {
        throw new UnsupportedOperationException();
    }

    public static <T, K> Pipe<T> values(Map<K, T> map, Predicate<? super K> keyPredicate) {
        throw new UnsupportedOperationException();
    }

    /**
     * 创建一个含有无限元素的流水线，元素由{@code supplier}持续提供。
     *
     * @param supplier 提供元素的生成器
     * @param <T> 元素类型
     * @return 无限长度的流水线
     * @see Stream#generate(Supplier)
     */
    public static <T> Pipe<T> generate(Supplier<? extends T> supplier) {
        requireNonNull(supplier);
        return new PipeHead<>(new Spliterators.AbstractSpliterator<>(MAX_VALUE, IMMUTABLE) {
            @Override
            public boolean tryAdvance(Consumer<? super T> action) {
                requireNonNull(action).accept(supplier.get());
                return true;
            }
        });
    }

    /**
     * 拼接给定的流水线，每个流水线中的元素按次序处理。
     *
     * @param pipes 需要拼接的流水线
     * @param <T> 元素类型
     * @return 拼接后的流水线
     */
    @SafeVarargs
    @SuppressWarnings("varargs")
    public static <T> Pipe<T> concat(Pipe<? extends T>... pipes) {
        if (pipes == null || pipes.length == 0) {
            return empty();
        }
        Pipe<T> resPipe = empty();
        for (Pipe<? extends T> pipe : pipes) {
            resPipe = resPipe.append(pipe);
        }
        return resPipe;
    }

    /**
     * 创建一个新的流水线，从给定的{@link Stream}实例中获取元素。
     *
     * @param stream 需要获取元素的流实例
     * @param <T> 元素类型
     * @return 新的流水线
     * @apiNote 此方法通过调用 {@link Stream#iterator()}方法获取流元素的迭代器来组装流水线，此操作会终结给定的流。
     */
    public static <T> Pipe<T> stream(Stream<? extends T> stream) {
        return new PipeHead<>(stream.spliterator());
    }

    /**
     * 创建一个含有无限元素的流水线，元素的生成以给定的种子{@code seed}为基础，每个元素都是对上一个元素应用{@code generator}
     * 生成，即使生成的结果为{@code null}也会被认为是有效元素。
     *
     * @param seed 初始种子元素，第0个元素
     * @param generator 后续元素的生成器，以前一个元素为参数
     * @param <T> 元素类型
     * @return 无限长度的流水线
     * @see Stream#iterate(Object, UnaryOperator)
     */
    public static <T> Pipe<T> iterate(final T seed, final UnaryOperator<T> generator) {
        requireNonNull(generator);
        return new PipeHead<>(new Spliterators.AbstractSpliterator<>(MAX_VALUE, ORDERED | IMMUTABLE) {
            private T previous;

            private boolean started;

            @Override
            public boolean tryAdvance(Consumer<? super T> action) {
                requireNonNull(action);
                T value;
                if (started) {
                    value = generator.apply(previous);
                } else {
                    value = seed;
                    started = true;
                }
                action.accept(previous = value);
                return true;
            }
        });
    }

    /**
     * 从给定的迭代器对象创建流水线实例，流水线的元素从迭代器中获取。
     *
     * @param iterator 用于获取元素的迭代器
     * @param <T> 元素类型
     * @return 新的流水线
     */
    public static <T> Pipe<T> iterator(Iterator<? extends T> iterator) {
        return new PipeHead<>(Spliterators.spliteratorUnknownSize(iterator, 0));
    }

    /**
     * 从给定的可迭代对象中创建流水线实例，流水线的元素从可迭代对象的迭代器中获取。
     *
     * @param iterable 可迭代对象
     * @param <T> 元素类型
     * @return 新的流水线
     */
    public static <T> Pipe<T> iterable(Iterable<? extends T> iterable) {
        return iterator(iterable.iterator());
    }

    public static <T> Pipe<T> list(List<? extends T> list) {
        return list == null || list.isEmpty() ? empty() : spliterator(list.spliterator());
    }

    public static <T> Pipe<T> set(Set<? extends T> set) {
        return set == null || set.isEmpty() ? empty() : spliterator(set.spliterator());
    }

    public static <T> Pipe<T> collection(Collection<? extends T> collection) {
        return collection == null || collection.isEmpty() ? empty() : spliterator(collection.spliterator());
    }
}
