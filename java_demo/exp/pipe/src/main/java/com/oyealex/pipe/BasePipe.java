package com.oyealex.pipe;

import java.util.Iterator;
import java.util.Spliterator;

/**
 * 基础流水线接口
 * <p/>
 * 定义流水线的通用方法。
 *
 * @author oyealex
 * @since 2023-05-23
 */
public interface BasePipe<T, P extends BasePipe<T, P>> extends AutoCloseable {
    /**
     * 将流水线转为拆分器。
     * <p/>
     * 此方法会终结流水线，返回的拆分器会包含此流水线的标准{@linkplain Spliterator#characteristics() 特征}。
     *
     * @return 包含了流水线元素的拆分器。
     */
    Spliterator<T> toSpliterator();

    /**
     * 将流水线转为迭代器。
     * <p/>
     * 此方法会终结流水线。
     *
     * @return 包含了流水线元素的迭代器。
     */
    Iterator<T> toIterator();

    /**
     * 在流水线结束时执行给定方法。
     * <p/>
     * 给定的方法只有在执行{@link #close()}方法时才会被执行。多次设置的关闭方法会保证在{@link #close()}调用时一定被执行。
     *
     * @param closeAction 关闭时执行的方法。
     * @return 流水线自身。
     */
    P onClose(Runnable closeAction);

    @Override
    void close();
}
