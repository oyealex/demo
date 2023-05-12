package com.oyealex.pipe.basis;

import com.oyealex.pipe.flag.PipeFlag;

import java.util.Objects;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Supplier;

/**
 * 终结操作，定义流水线地最终操作，并能够获取流水线的最终返回值。
 *
 * @author oyealex
 * @since 2023-04-27
 */
interface TerminalOp<T, R> extends Op<T>, Supplier<R> {
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

    static <T> TerminalOp<T, Void> wrap(Consumer<? super T> action) {
        Objects.requireNonNull(action);
        return new TerminalOp<>() {
            @Override
            public void accept(T var) {
                action.accept(var);
            }

            @Override
            public Void get() {
                return null;
            }
        };
    }

    abstract class KeepSingle<T, R> implements TerminalOp<T, Optional<R>> {
        protected R result;

        @Override
        public Optional<R> get() {
            return Optional.ofNullable(result);
        }
    }

    abstract class FindSingle<T, R> extends KeepSingle<T, R> {
        protected boolean found = false;

        @Override
        public boolean canShortCircuit() {
            return found;
        }

        @Override
        public int getOpFlag() {
            return PipeFlag.IS_SHORT_CIRCUIT;
        }
    }

    abstract class Orderly<T, R> implements TerminalOp<T, R> {
        protected long index = 0L;
    }
}
