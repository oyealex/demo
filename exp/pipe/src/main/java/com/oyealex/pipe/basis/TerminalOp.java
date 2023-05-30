package com.oyealex.pipe.basis;

import com.oyealex.pipe.flag.PipeFlag;
import com.oyealex.pipe.assist.Tuple;

import java.util.Objects;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Supplier;

import static java.util.Optional.empty;
import static java.util.Optional.of;

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
     * @implNote 默认返回的标记为 {@link PipeFlag#EMPTY}，
     * 在当前实现中，如果覆写此方法则唯一可能返回的值为{@link PipeFlag#IS_SHORT_CIRCUIT}。
     */
    default int getOpFlag() {
        return PipeFlag.EMPTY;
    }

    static <T> TerminalOp<T, Void> wrap(Consumer<? super T> action) {
        Objects.requireNonNull(action);
        return new TerminalOp<T, Void>() {
            @Override
            public void accept(T value) {
                action.accept(value);
            }

            @Override
            public Void get() {
                return null;
            }
        };
    }

    abstract class Find<T, R> implements TerminalOp<T, R> {
        protected R result;

        Find(R initVar) {
            this.result = initVar;
        }

        @Override
        public R get() {
            return result;
        }
    }

    abstract class FindOpt<T, R> implements TerminalOp<T, Optional<R>> {
        protected R result;

        protected boolean found = false;

        @Override
        public Optional<R> get() {
            return found ? of(result) : empty();
        }
    }

    abstract class FindTupleOpt<T, F, S> implements TerminalOp<T, Tuple<Optional<F>, Optional<S>>> {
        protected F first;

        protected S second;

        protected boolean foundFirst = false;

        protected boolean foundSecond = false;

        @Override
        public Tuple<Optional<F>, Optional<S>> get() {
            return Tuple.of(foundFirst ? of(first) : empty(), foundSecond ? of(second) : empty());
        }
    }

    abstract class FindOptOnce<T, R> extends FindOpt<T, R> {
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
