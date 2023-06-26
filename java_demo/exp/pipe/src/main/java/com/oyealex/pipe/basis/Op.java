package com.oyealex.pipe.basis;

import java.util.function.Consumer;

/**
 * 操作接口
 *
 * @author oyealex
 * @since 2023-02-09
 */
interface Op<T> extends Consumer<T> {
    /**
     * 准备好开始接收元素。
     *
     * @param size 元素数量，-1表示未知或无限
     */
    default void begin(long size) {}

    // TODO 2023-05-19 02:05 根据size的上下界做更进一步的优化
    default void begin(long size, long minSize, long maxSize) {
        begin(size);
    }

    @Override
    void accept(T value);

    /**
     * 结束接收元素。
     */
    default void end() {}

    /**
     * 判断是否可以执行短路操作，提前结束数据流。
     *
     * @return {@code true} - 可以提前结束数据流。
     */
    default boolean canShortCircuit() {return false;}
}
