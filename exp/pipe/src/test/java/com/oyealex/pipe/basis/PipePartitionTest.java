package com.oyealex.pipe.basis;

import com.oyealex.pipe.PipeTestFixture;
import com.oyealex.pipe.functional.LongBiFunction;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;
import java.util.function.Supplier;

import static com.oyealex.pipe.basis.Pipe.list;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * 针对流水线分区系列API的测试
 *
 * @author oyealex
 * @see Pipe#partition(int)
 * @see Pipe#partition(Function)
 * @see Pipe#partitionOrderly(LongBiFunction)
 * @see Pipe#partitionToList(int)
 * @see Pipe#partitionToList(int, Supplier)
 * @see Pipe#partitionToSet(int)
 * @see Pipe#partitionToSet(int, Supplier)
 * @see Pipe#partitionToCollection(int, Supplier)
 * @since 2023-06-05
 */
class PipePartitionTest extends PipeTestFixture {
    @Test
    @DisplayName("能够根据固定大小正确对流水线元素分区")
    void should_partition_elements_by_fixed_size_rightly() {
        List<String> sample = generateRandomStrList();
        int size = sample.size();
        assertAll(() -> assertEquals(partition(sample, 5), list(sample).partition(5).map(Pipe::toList).toList()),
            () -> assertEquals(partition(sample, 1), list(sample).partition(1).map(Pipe::toList).toList()),
            () -> assertEquals(partition(sample, size), list(sample).partition(size).map(Pipe::toList).toList()),
            () -> assertEquals(partition(sample, size + 1),
                list(sample).partition(size + 1).map(Pipe::toList).toList()));
    }

    @Test
    @DisplayName("能够正确根据策略对元素分区")
    void should_partition_elements_by_policy_rightly() {

    }

    private <T> List<List<T>> partition(List<T> list, int size) {
        int index = 0;
        List<List<T>> partitions = new ArrayList<>(list.size() / size + 1);
        while (index < list.size()) {
            partitions.add(list.subList(index, Math.min(index + size, list.size())));
            index += size;
        }
        return partitions;
    }

    // exception test
}
