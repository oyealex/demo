package com.oyealex.pipe;

import com.oyealex.pipe.annotations.Extended;

import java.util.Iterator;
import java.util.Objects;
import java.util.function.Supplier;
import java.util.function.UnaryOperator;
import java.util.stream.Stream;

/**
 * 流水线的辅助工具类
 *
 * @author oyealex
 * @since 2023-04-28
 */
public class Pipes {
    private Pipes() {
        throw new IllegalStateException("no instance available");
    }

    /**
     * 构造一个空的流水线实例。
     *
     * @param <T> 流水线元素类型
     * @return 空的流水线实例
     * @see Stream#empty()
     */
    public static <T> Pipe<T> empty() {
        return new AbstractPipe.PipeHead<>(Misc.emptyIterator());
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
        return new AbstractPipe.PipeHead<>(new Iterator<T>() {
            private int index = 0;

            @Override
            public boolean hasNext() {
                return index < values.length;
            }

            @Override
            public T next() {
                return values[index++];
            }
        });
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
        Objects.requireNonNull(generator);
        return new AbstractPipe.PipeHead<>(new Iterator<T>() {
            private T previous = seed;

            private boolean started = false;

            @Override
            public boolean hasNext() {
                return true;
            }

            @Override
            public T next() {
                T res;
                if (started) {
                    res = generator.apply(previous);
                } else {
                    res = seed;
                    started = true;
                }
                previous = res;
                return res;
            }
        });
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
        Objects.requireNonNull(supplier);
        return new AbstractPipe.PipeHead<>(new Iterator<T>() {
            @Override
            public boolean hasNext() {
                return true;
            }

            @Override
            public T next() {
                return supplier.get();
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
    @Extended
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
    @Extended
    public static <T> Pipe<T> from(Stream<? extends T> stream) {
        return new AbstractPipe.PipeHead<>(stream.iterator());
    }

    /**
     * 从给定的迭代器对象创建流水线实例，流水线的元素从迭代器中获取。
     *
     * @param iterator 用于获取元素的迭代器
     * @param <T> 元素类型
     * @return 新的流水线
     */
    @Extended
    public static <T> Pipe<T> from(Iterator<? extends T> iterator) {
        return new AbstractPipe.PipeHead<>(iterator);
    }

    /**
     * 从给定的可迭代对象中创建流水线实例，流水线的元素从可迭代对象的迭代器中获取。
     *
     * @param iterable 可迭代对象
     * @param <T> 元素类型
     * @return 新的流水线
     */
    @Extended
    public static <T> Pipe<T> from(Iterable<? extends T> iterable) {
        return from(iterable.iterator());
    }
}
