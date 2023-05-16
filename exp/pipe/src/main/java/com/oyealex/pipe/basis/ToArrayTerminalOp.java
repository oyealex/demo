package com.oyealex.pipe.basis;

import com.oyealex.pipe.utils.MiscUtil;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.IntFunction;

import static com.oyealex.pipe.utils.MiscUtil.MAX_ARRAY_LENGTH;
import static java.lang.Integer.SIZE;
import static java.lang.Integer.numberOfLeadingZeros;

/**
 * ToArrayTerminalOp
 *
 * @author oyealex
 * @since 2023-05-16
 */
class ToArrayTerminalOp<T> implements TerminalOp<T, T[]> {
    private static final int MIN_CAPACITY_SHIFT = 4;

    private static final int MAX_CAPACITY_SHIFT = SIZE - numberOfLeadingZeros(MAX_ARRAY_LENGTH / 2);

    private final IntFunction<T[]> arrayFactory;

    private List<T[]> fullArrays;

    private T[] array;

    private int index;

    private int fullLength;

    ToArrayTerminalOp(IntFunction<T[]> arrayFactory) {
        this.arrayFactory = arrayFactory;
    }

    @Override
    public void begin(long size) {
        MiscUtil.checkArraySize(size);
        array = arrayFactory.apply(size >= 0 ? (int) size : calcArrayCapacity(0));
        fullArrays = new ArrayList<>();
        fullArrays.add(array);
        index = 0;
        fullLength = 0;
    }

    private int calcArrayCapacity(int arrayIndex) {
        return 1 <<
            (arrayIndex <= 1 ? MIN_CAPACITY_SHIFT : Math.min(MIN_CAPACITY_SHIFT + arrayIndex - 1, MAX_CAPACITY_SHIFT));
    }

    @Override
    public void accept(T value) {
        if (index >= array.length) {
            prepareNewArray();
        }
        array[index++] = value;
    }

    private void prepareNewArray() {
        fullLength += array.length;
        if (fullLength >= MAX_ARRAY_LENGTH || fullLength <= 0) {
            throw new IllegalArgumentException("Pipe size exceeds max array size");
        }
        array = arrayFactory.apply(calcArrayCapacity(fullArrays.size()));
        fullArrays.add(array);
        index = 0;
    }

    @Override
    public T[] get() {
        if (fullArrays.size() == 1) {
            T[] result = fullArrays.get(0);
            if (index == result.length) {
                return result;
            } else {
                return Arrays.copyOf(result, index);
            }
        }
        T[] result = arrayFactory.apply(fullLength);
        int resultIndex = 0;
        for (int i = 0; i < fullArrays.size() - 1; i++) {
            T[] part = fullArrays.get(i);
            System.arraycopy(part, 0, result, resultIndex, part.length);
            resultIndex += part.length;
        }
        System.arraycopy(array, 0, result, resultIndex, index);
        return result;
    }
}
