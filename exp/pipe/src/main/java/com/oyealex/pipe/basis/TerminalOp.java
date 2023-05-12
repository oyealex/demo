package com.oyealex.pipe.basis;

import com.oyealex.pipe.flag.PipeFlag;

import java.util.Objects;
import java.util.function.Consumer;
import java.util.function.Supplier;

/**
 * 终结操作，定义流水线地最终操作，并能够获取流水线的最终返回值。
 *
 * @author oyealex
 * @since 2023-04-27
 */
interface TerminalOp<IN, R> extends Op<IN>, Supplier<R> {
    /**
     * 获取此终结操作的标记。
     *
     * @return 终结操作的标记
     * @implNote 默认返回的标记为 {@link PipeFlag#NOTHING}，
     * 在当前实现中，如果覆写此方法则唯一可能返回的值为{@link PipeFlag#IS_SHORT_CIRCUIT}。
     */
    default int getOpFlag() {
        return PipeFlag.NOTHING;
    }

    static <IN> TerminalOp<IN, Void> wrap(Consumer<? super IN> action) {
        Objects.requireNonNull(action);
        return new TerminalOp<>() {
            @Override
            public void accept(IN var) {
                action.accept(var);
            }

            @Override
            public Void get() {
                return null;
            }
        };
    }
}
