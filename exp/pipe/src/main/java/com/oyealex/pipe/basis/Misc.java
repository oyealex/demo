package com.oyealex.pipe.basis;

import java.util.Iterator;

/**
 * 一些杂项
 *
 * @author oyealex
 * @since 2023-04-28
 */
class Misc {
    private Misc() {
        throw new IllegalStateException("no instance available");
    }

    static Runnable composeAction(Runnable action, Runnable anotherAction) {
        return () -> {
            try {
                action.run();
            } catch (Throwable throwable) {
                try {
                    anotherAction.run();
                } catch (Throwable anotherThrowable) {
                    try {
                        throwable.addSuppressed(anotherThrowable);
                    } catch (Throwable ignore) {
                    }
                }
                throw throwable;
            }
            anotherAction.run();
        };
    }

    public static <E> Iterator<? extends E> arrayIterator(E[] values) {
        return new Iterator<>() {
            private int nextIndex = 0;

            @Override
            public boolean hasNext() {
                return nextIndex < values.length;
            }

            @Override
            public E next() {
                return values[nextIndex++];
            }
        };
    }
}
