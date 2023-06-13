package com.oyealex.pipe.basis;

import com.oyealex.pipe.PipeTestFixture;
import com.oyealex.pipe.functional.LongBiFunction;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Comparator;
import java.util.List;
import java.util.function.Function;

import static com.oyealex.pipe.assist.Tuple.of;
import static com.oyealex.pipe.basis.Pipe.empty;
import static com.oyealex.pipe.basis.Pipe.list;
import static java.util.Comparator.comparingInt;
import static java.util.Comparator.naturalOrder;
import static java.util.Comparator.reverseOrder;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrowsExactly;

/**
 * 针对流水线求极值系列API的测试
 *
 * @author oyealex
 * @see Pipe#min()
 * @see Pipe#min(Comparator)
 * @see Pipe#minBy(Function)
 * @see Pipe#minBy(Function, Comparator)
 * @see Pipe#minByOrderly(LongBiFunction)
 * @see Pipe#minByOrderly(LongBiFunction, Comparator)
 * @see Pipe#max()
 * @see Pipe#max(Comparator)
 * @see Pipe#maxBy(Function)
 * @see Pipe#maxBy(Function, Comparator)
 * @see Pipe#maxByOrderly(LongBiFunction)
 * @see Pipe#maxByOrderly(LongBiFunction, Comparator)
 * @see Pipe#minMax()
 * @see Pipe#minMax(Comparator)
 * @see Pipe#minMaxBy(Function)
 * @see Pipe#minMaxBy(Function, Comparator)
 * @see Pipe#minMaxByOrderly(LongBiFunction)
 * @see Pipe#minMaxByOrderly(LongBiFunction, Comparator)
 * @since 2023-06-05
 */
class PipeMinMaxTest extends PipeTestFixture {
    @Test
    @DisplayName("能够正确获流水线最小元素")
    void should_get_the_min_element_rightly() {
        List<String> sample = generateRandomStrList();
        RecordedCounter<String> counter1 = new RecordedCounter<>();
        RecordedCounter<String> counter2 = new RecordedCounter<>();
        assertAll(() -> assertEquals(sample.stream().min(naturalOrder()), list(sample).min()),
            () -> assertEquals(sample.stream().min(naturalOrder()), list(sample).min(null)),
            () -> assertEquals(sample.stream().min(comparingInt(String::length)), list(sample).minBy(String::length)),
            () -> assertEquals(sample.stream().max(comparingInt(String::length)),
                list(sample).minBy(String::length, reverseOrder())),
            () -> assertEquals(sample.stream().min(comparingInt(value -> value.length() + counter1.getOrder(value))),
                list(sample).minByOrderly((order, value) -> value.length() + order)),
            () -> assertEquals(sample.stream().max(comparingInt(value -> value.length() + counter2.getOrder(value))),
                // MK 2023-06-14 02:06 偶现失败
                list(sample).minByOrderly((order, value) -> value.length() + order, reverseOrder())));
    }

    @Test
    @DisplayName("能够正确获流水线最大元素")
    void should_get_the_max_element_rightly() {
        List<String> sample = generateRandomStrList();
        RecordedCounter<String> counter1 = new RecordedCounter<>();
        RecordedCounter<String> counter2 = new RecordedCounter<>();
        assertAll(() -> assertEquals(sample.stream().max(naturalOrder()), list(sample).max()),
            () -> assertEquals(sample.stream().max(naturalOrder()), list(sample).max(null)),
            () -> assertEquals(sample.stream().max(comparingInt(String::length)), list(sample).maxBy(String::length)),
            () -> assertEquals(sample.stream().min(comparingInt(String::length)),
                list(sample).maxBy(String::length, reverseOrder())),
            () -> assertEquals(sample.stream().max(comparingInt(value -> value.length() + counter1.getOrder(value))),
                list(sample).maxByOrderly((order, value) -> value.length() + order)),
            () -> assertEquals(sample.stream().min(comparingInt(value -> value.length() + counter2.getOrder(value))),
                list(sample).maxByOrderly((order, value) -> value.length() + order, reverseOrder())));
    }

    @Test
    @DisplayName("能够正确获流水线最下和最大元素")
    void should_get_min_and_max_elements_rightly() {
        List<String> sample = generateRandomStrList();
        RecordedCounter<String> counter11 = new RecordedCounter<>();
        RecordedCounter<String> counter12 = new RecordedCounter<>();
        RecordedCounter<String> counter21 = new RecordedCounter<>();
        RecordedCounter<String> counter22 = new RecordedCounter<>();
        assertAll(() -> assertEquals(of(sample.stream().min(naturalOrder()), sample.stream().max(naturalOrder())),
                list(sample).minMax()),
            () -> assertEquals(of(sample.stream().min(naturalOrder()), sample.stream().max(naturalOrder())),
                list(sample).minMax(null)), () -> assertEquals(of(sample.stream().min(comparingInt(String::length)),
                sample.stream().max(comparingInt(String::length))), list(sample).minMaxBy(String::length)),
            () -> assertEquals(of(sample.stream().max(comparingInt(String::length)),
                    sample.stream().min(comparingInt(String::length))),
                list(sample).minMaxBy(String::length, reverseOrder())), () -> assertEquals(
                of(sample.stream().min(comparingInt(value -> value.length() + counter11.getOrder(value))),
                    sample.stream().max(comparingInt(value -> value.length() + counter12.getOrder(value)))),
                list(sample).minMaxByOrderly((order, value) -> value.length() + order)), () -> assertEquals(
                of(sample.stream().max(comparingInt(value -> value.length() + counter21.getOrder(value))),
                    sample.stream().min(comparingInt(value -> value.length() + counter22.getOrder(value)))),
                list(sample).minMaxByOrderly((order, value) -> value.length() + order, reverseOrder())));
    }

    // exception test

    @Test
    @DisplayName("当不能为null的参数为null时抛出异常")
    void should_throw_exception_when_required_non_null_param_is_null() {
        assertAll(() -> assertThrowsExactly(NullPointerException.class, () -> empty().minBy(null)),
            () -> assertThrowsExactly(NullPointerException.class, () -> empty().minBy(null, naturalOrder())),
            () -> assertThrowsExactly(NullPointerException.class, () -> empty().minByOrderly(null)),
            () -> assertThrowsExactly(NullPointerException.class, () -> empty().minByOrderly(null, naturalOrder())),
            () -> assertThrowsExactly(NullPointerException.class, () -> empty().maxBy(null)),
            () -> assertThrowsExactly(NullPointerException.class, () -> empty().maxBy(null, naturalOrder())),
            () -> assertThrowsExactly(NullPointerException.class, () -> empty().maxByOrderly(null)),
            () -> assertThrowsExactly(NullPointerException.class, () -> empty().maxByOrderly(null, naturalOrder())),
            () -> assertThrowsExactly(NullPointerException.class, () -> empty().minMaxBy(null)),
            () -> assertThrowsExactly(NullPointerException.class, () -> empty().minMaxBy(null, naturalOrder())),
            () -> assertThrowsExactly(NullPointerException.class, () -> empty().minMaxByOrderly(null)),
            () -> assertThrowsExactly(NullPointerException.class, () -> empty().minMaxByOrderly(null, naturalOrder())));
    }

    @Test
    @DisplayName("如果元素没有实现Comparable接口，则在尝试使用自然顺序获取最大或最小值时抛出异常")
    void should_throw_exception_when_get_min_or_max_element_by_natural_order_and_elements_are_not_comparable() {
        assertAll(() -> assertThrowsExactly(ClassCastException.class, () -> generateUnComparablePipe().min()),
            () -> assertThrowsExactly(ClassCastException.class, () -> generateUnComparablePipe().min(null)),
            () -> assertThrowsExactly(ClassCastException.class, () -> generateUnComparablePipe().max()),
            () -> assertThrowsExactly(ClassCastException.class, () -> generateUnComparablePipe().max(null)),
            () -> assertThrowsExactly(ClassCastException.class, () -> generateUnComparablePipe().minMax()),
            () -> assertThrowsExactly(ClassCastException.class, () -> generateUnComparablePipe().minMax(null)));
    }
}
