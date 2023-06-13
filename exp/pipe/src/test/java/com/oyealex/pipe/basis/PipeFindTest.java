package com.oyealex.pipe.basis;

import com.oyealex.pipe.PipeTestFixture;
import com.oyealex.pipe.assist.Tuple;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Optional;

import static com.oyealex.pipe.basis.Pipe.empty;
import static com.oyealex.pipe.basis.Pipe.list;
import static java.util.Optional.of;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrowsExactly;

/**
 * 针对流水线搜索系列API的测试。
 *
 * @author oyealex
 * @see Pipe#findFirst()
 * @see Pipe#findFirstNonnull()
 * @see Pipe#findLast()
 * @see Pipe#findLastNonnull()
 * @see Pipe#findFirstLast()
 * @see Pipe#findAny()
 * @since 2023-06-13
 */
class PipeFindTest extends PipeTestFixture {
    @Test
    @DisplayName("能够正确找到流水线第一个或最后一个元素")
    void should_find_first_or_last_rightly() {
        List<String> sample = generateRandomStrList();
        String first = sample.get(0);
        String last = sample.get(sample.size() - 1);
        assertAll(
            () -> assertEquals(of(first), list(sample).findFirst()),
            () -> assertEquals(of(first), list(sample).findFirstNonnull()),
            () -> assertEquals(of(first), list(sample).prepend((String) null).findFirstNonnull()),
            () -> assertEquals(of(last), list(sample).findLast()),
            () -> assertEquals(of(last), list(sample).findLastNonnull()),
            () -> assertEquals(of(last), list(sample).append((String) null).findLastNonnull()),
            () -> assertEquals(Tuple.of(of(first), of(last)), list(sample).findFirstLast()),
            () -> assertEquals(of(first), list(sample).findAny())
        );
    }
    // optimization test

    // exception test
}
