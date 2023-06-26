package com.oyealex.pipe.basis;

import com.oyealex.pipe.PipeTestFixture;
import com.oyealex.pipe.assist.IntBox;
import com.oyealex.pipe.functional.LongBiFunction;
import com.oyealex.pipe.policy.PartitionPolicy;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Vector;
import java.util.function.Function;
import java.util.function.Supplier;

import static com.oyealex.pipe.basis.Pipe.list;
import static com.oyealex.pipe.policy.PartitionPolicy.IN;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrowsExactly;

/**
 * 针对流水线分区系列API的测试
 *
 * @author oyealex
 * @see Pipe#partition(int)
 * @see Pipe#partition(Function)
 * @see Pipe#partitionOrderly(LongBiFunction)
 * @see Pipe#partitionToList(int)
 * @see Pipe#partitionToList(Function)
 * @see Pipe#partitionToList(int, Supplier)
 * @see Pipe#partitionToList(Function, Supplier)
 * @see Pipe#partitionToSet(int)
 * @see Pipe#partitionToSet(Function)
 * @see Pipe#partitionToSet(int, Supplier)
 * @see Pipe#partitionToSet(Function, Supplier)
 * @see Pipe#partitionToCollection(int, Supplier)
 * @see Pipe#partitionToCollection(Function, Supplier)
 * @since 2023-06-05
 */
class PipePartitionTest extends PipeTestFixture {
    @Test
    @DisplayName("能够根据固定大小正确对流水线元素分区")
    void should_partition_elements_by_fixed_size_rightly() {
        List<String> sample = genRandomStrList();
        int size = sample.size();
        assertAll(() -> assertEquals(partition(sample, 5), list(sample).partition(5).chain(this::toList)),
            () -> assertEquals(partition(sample, 1), list(sample).partition(1).chain(this::toList)),
            () -> assertEquals(partition(sample, size), list(sample).partition(size).chain(this::toList)),
            () -> assertEquals(partition(sample, size + 1), list(sample).partition(size + 1).chain(this::toList)));
    }

    @Test
    @DisplayName("能够正确根据策略对元素分区")
    void should_partition_elements_by_policy_rightly() {
        List<String> sample = genRandomStrList();
        assertAll(() -> assertEquals(partition(sample, getPolicyRoundly(), ArrayList::new),
                list(sample).partition(getPolicyRoundly()).chain(this::toList)),
            () -> assertEquals(partition(sample, getPolicyRoundly(), ArrayList::new),
                list(sample).partitionOrderly((order, ignored) -> PartitionPolicy.values()[(int) order % 3])
                    .chain(this::toList)));
    }

    @Test
    @DisplayName("能够根据固定大小正确将元素分区为容器对象")
    void should_partition_elements_to_containers_by_fixed_size_rightly() {
        List<String> sample = genRandomStrList();
        int size = 5;
        assertAll(() -> assertEquals(partition(sample, size), list(sample).partitionToList(size).toList()),
            () -> assertEquals(partition(sample, getPolicyRoundly(), ArrayList::new),
                list(sample).partitionToList(getPolicyRoundly()).toList()),
            () -> assertEqualsWithType(partition(sample, size, LinkedList::new),
                list(sample).partitionToList(size, LinkedList::new).toList()),
            () -> assertEqualsWithType(partition(sample, getPolicyRoundly(), LinkedList::new),
                list(sample).partitionToList(getPolicyRoundly(), LinkedList::new).toList()),
            () -> assertEquals(partition(sample, size, HashSet::new), list(sample).partitionToSet(size).toList()),
            () -> assertEquals(partition(sample, getPolicyRoundly(), HashSet::new),
                list(sample).partitionToSet(getPolicyRoundly()).toList()),
            () -> assertEqualsWithType(partition(sample, size, HashSet::new),
                list(sample).partitionToSet(size, HashSet::new).toList()),
            () -> assertEqualsWithType(partition(sample, getPolicyRoundly(), HashSet::new),
                list(sample).partitionToSet(getPolicyRoundly(), HashSet::new).toList()),
            () -> assertEqualsWithType(partition(sample, size, Vector::new),
                list(sample).partitionToCollection(size, Vector::new).toList()),
            () -> assertEqualsWithType(partition(sample, getPolicyRoundly(), Vector::new),
                list(sample).partitionToCollection(getPolicyRoundly(), Vector::new).toList()));
    }

    private Function<String, PartitionPolicy> getPolicyRoundly() {
        IntBox counter = IntBox.box(0);
        return ignored -> PartitionPolicy.values()[counter.getAndIncrementRound(3)];
    }

    private <T> List<List<T>> partition(List<T> list, int size) {
        return partition(list, size, ArrayList::new);
    }

    private <T, C extends Collection<T>> List<C> partition(List<T> list, int size, Supplier<C> supplier) {
        int index = 0;
        List<C> partitions = new ArrayList<>(list.size() / size + 1);
        while (index < list.size()) {
            C partition = supplier.get();
            for (int i = index; i < index + size && i < list.size(); i++) {
                partition.add(list.get(i));
            }
            partitions.add(partition);
            index += size;
        }
        return partitions;
    }

    private <T> List<List<T>> partition(List<T> list, int... sizes) {
        int index = 0;
        List<List<T>> partitions = new ArrayList<>(sizes.length + 1);
        for (int i = 0; i < sizes.length && index < list.size(); i++) {
            partitions.add(list.subList(index, Math.min(index + sizes[i], list.size())));
            index += sizes[i];
        }
        if (index < list.size()) {
            partitions.add(list.subList(index, list.size()));
        }
        return partitions;
    }

    private <T, C extends Collection<T>> List<C> partition(List<T> list, Function<T, PartitionPolicy> function,
        Supplier<C> supplier) {
        List<C> partitions = new ArrayList<>();
        C currentPartition = null;
        for (T value : list) {
            switch (function.apply(value)) {
                case BEGIN:
                    if (currentPartition != null) {
                        partitions.add(currentPartition);
                    }
                    currentPartition = supplier.get();
                    currentPartition.add(value);
                    break;
                case IN:
                    if (currentPartition == null) {
                        currentPartition = supplier.get();
                    }
                    currentPartition.add(value);
                    break;
                case END:
                    if (currentPartition == null) {
                        currentPartition = supplier.get();
                    }
                    currentPartition.add(value);
                    partitions.add(currentPartition);
                    currentPartition = null;
            }
        }
        if (currentPartition != null) {
            partitions.add(currentPartition);
        }
        return partitions;
    }

    private <T> List<List<T>> toList(Pipe<Pipe<T>> pipe) {
        return pipe.map(Pipe::toList).toList();
    }

    // exception test

    @Test
    @DisplayName("当分区大小参数无效时抛出异常")
    void should_throw_exception_when_partition_size_is_invalid() {
        assertAll(
            () -> assertThrowsExactly(IllegalArgumentException.class, () -> infiniteRandomStrPipe().partition(-1)),
            () -> assertThrowsExactly(IllegalArgumentException.class, () -> infiniteRandomStrPipe().partition(0)),
            () -> assertThrowsExactly(IllegalArgumentException.class,
                () -> infiniteRandomStrPipe().partitionToList(-1)),
            () -> assertThrowsExactly(IllegalArgumentException.class, () -> infiniteRandomStrPipe().partitionToList(0)),
            () -> assertThrowsExactly(IllegalArgumentException.class,
                () -> infiniteRandomStrPipe().partitionToList(-1, ArrayList::new)),
            () -> assertThrowsExactly(IllegalArgumentException.class,
                () -> infiniteRandomStrPipe().partitionToList(0, ArrayList::new)),
            () -> assertThrowsExactly(IllegalArgumentException.class, () -> infiniteRandomStrPipe().partitionToSet(-1)),
            () -> assertThrowsExactly(IllegalArgumentException.class, () -> infiniteRandomStrPipe().partitionToSet(0)),
            () -> assertThrowsExactly(IllegalArgumentException.class,
                () -> infiniteRandomStrPipe().partitionToSet(-1, HashSet::new)),
            () -> assertThrowsExactly(IllegalArgumentException.class,
                () -> infiniteRandomStrPipe().partitionToSet(0, HashSet::new)),
            () -> assertThrowsExactly(IllegalArgumentException.class,
                () -> infiniteRandomStrPipe().partitionToCollection(-1, ArrayList::new)),
            () -> assertThrowsExactly(IllegalArgumentException.class,
                () -> infiniteRandomStrPipe().partitionToCollection(0, ArrayList::new)));
    }

    @Test
    @DisplayName("当不能为null的参数为null时抛出异常")
    void should_throw_exception_when_required_non_null_param_is_null() {
        assertAll(() -> assertThrowsExactly(NullPointerException.class, () -> infiniteRandomStrPipe().partition(null)),
            () -> assertThrowsExactly(NullPointerException.class, () -> infiniteRandomStrPipe().partitionOrderly(null)),
            () -> assertThrowsExactly(NullPointerException.class, () -> infiniteRandomStrPipe().partitionToList(null)),
            () -> assertThrowsExactly(NullPointerException.class,
                () -> infiniteRandomStrPipe().partitionToList(5, null)),
            () -> assertThrowsExactly(NullPointerException.class,
                () -> infiniteRandomStrPipe().partitionToList(ignored -> IN, null)),
            () -> assertThrowsExactly(NullPointerException.class,
                () -> infiniteRandomStrPipe().partitionToList(null, ArrayList::new)),
            () -> assertThrowsExactly(NullPointerException.class, () -> infiniteRandomStrPipe().partitionToSet(null)),
            () -> assertThrowsExactly(NullPointerException.class,
                () -> infiniteRandomStrPipe().partitionToSet(5, null)),
            () -> assertThrowsExactly(NullPointerException.class,
                () -> infiniteRandomStrPipe().partitionToSet(ignored -> IN, null)),
            () -> assertThrowsExactly(NullPointerException.class,
                () -> infiniteRandomStrPipe().partitionToSet(null, HashSet::new)),
            () -> assertThrowsExactly(NullPointerException.class,
                () -> infiniteRandomStrPipe().partitionToCollection(5, null)),
            () -> assertThrowsExactly(NullPointerException.class,
                () -> infiniteRandomStrPipe().partitionToCollection(null, ArrayList::new)));
    }

    @Test
    @DisplayName("当分区策略方法返回null时，在流水线运行期间抛出异常")
    void should_throw_exception_when_policy_function_return_null_on_pipe_running() {
        Function<String, PartitionPolicy> function = ignored -> null;
        assertAll(() -> assertThrowsExactly(NullPointerException.class,
                () -> infiniteRandomStrPipe().partition(function).run()),
            () -> assertThrowsExactly(NullPointerException.class,
                () -> infiniteRandomStrPipe().partitionOrderly((order, ignored) -> null).run()),
            () -> assertThrowsExactly(NullPointerException.class,
                () -> infiniteRandomStrPipe().partitionToList(function).run()),
            () -> assertThrowsExactly(NullPointerException.class,
                () -> infiniteRandomStrPipe().partitionToList(function, ArrayList::new).run()),
            () -> assertThrowsExactly(NullPointerException.class,
                () -> infiniteRandomStrPipe().partitionToSet(function).run()),
            () -> assertThrowsExactly(NullPointerException.class,
                () -> infiniteRandomStrPipe().partitionToSet(function, HashSet::new).run()),
            () -> assertThrowsExactly(NullPointerException.class,
                () -> infiniteRandomStrPipe().partitionToCollection(function, HashSet::new).run()));
    }
}
