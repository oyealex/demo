package com.oyealex.pipe.basis;

import com.oyealex.pipe.PipeTestFixture;
import com.oyealex.pipe.basis.api.Pipe;
import com.oyealex.pipe.basis.functional.LongBiConsumer;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.util.function.Consumer;

import static com.oyealex.pipe.basis.Pipes.list;
import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * 针对流水线其他杂项API的测试。
 *
 * @author oyealex
 * @see Pipe#reverse()
 * @see Pipe#shuffle()
 * @see Pipe#shuffle(Random)
 * @see Pipe#peek(Consumer)
 * @see Pipe#peekOrderly(LongBiConsumer)
 * @since 2023-05-30
 */
class PipeMiscTest extends PipeTestFixture {
    @Test
    @DisplayName("能够正确的逆序元素")
    void should_reverse_elements_rightly() {
        List<String> sample = generateRandomStrList();
        List<String> expected = new ArrayList<>(sample);
        Collections.reverse(expected);
        assertEquals(expected, list(sample).reverse().toList());
    }

    @Test
    @DisplayName("如果一个流水线已经自然有序，逆序之后成为自然逆序，并且对此执行自然逆序排序不会真正执行比较")
    void should_not_do_sort_actually_when_reverse_a_pipe_which_is_already_sorted_reversely() {
    }
}
