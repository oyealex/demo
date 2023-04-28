package com.oyealex.pipe.basis.op;

import java.util.function.Consumer;

/**
 * 操作接口
 *
 * @author oyealex
 * @since 2023-02-09
 */
public interface Op<T> extends Consumer<T> {
    /**
     * 准备好开始接收元素
     *
     * @param size 元素数量，-1表示未知或无限
     */
    default void begin(long size) {}

    /**
     * 结束接收元素
     */
    default void end() {}

    @Override
    void accept(T t);

    /**
     * 判断是否需要取消后续循环
     *
     * @return true 需要取消后续循环
     */
    default boolean cancellationRequested() {return false;}
}
