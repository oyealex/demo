package com.oyealex.seq;

import java.util.function.Consumer;

/**
 * 操作接口，
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

    /**
     * 判断是否拒绝接收更多的元素
     *
     * @return true 拒绝接收更多元素
     */
    default boolean refuseAccept() {return false;}
}
