package com.oyealex.pipe;

import java.util.ArrayList;
import java.util.List;

/**
 * Collectors
 *
 * @author oyealex
 * @since 2023-04-27
 */
class CollectorUtil {
    private CollectorUtil() {
        throw new IllegalStateException("unavailable");
    }

    public static <T> TerminalOp<T, List<T>> makeToListTerminalOp() {
        return new ToListTerminalOp<>();
    }

    private static class ToListTerminalOp<T> implements TerminalOp<T, List<T>> {
        private List<T> list;

        @Override
        public void begin(long size) {
            if (size > 0) {
                list = new ArrayList<>((int) size);
            } else {
                list = new ArrayList<>();
            }
        }

        @Override
        public void accept(T t) {
            list.add(t);
        }

        @Override
        public List<T> get() {
            return list;
        }
    }
}
