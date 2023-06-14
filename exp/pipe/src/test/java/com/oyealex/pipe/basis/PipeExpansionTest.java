package com.oyealex.pipe.basis;

import com.oyealex.pipe.PipeTestFixture;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Spliterator;
import java.util.function.Predicate;
import java.util.stream.Stream;

import static com.oyealex.pipe.basis.Pipe.list;
import static com.oyealex.pipe.basis.Pipe.set;
import static com.oyealex.pipe.policy.PartitionPolicy.BEGIN;
import static com.oyealex.pipe.policy.PartitionPolicy.IN;
import static java.util.Collections.emptyMap;
import static java.util.Collections.singletonList;
import static java.util.function.Function.identity;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrowsExactly;

/**
 * 针对流水线扩容系列API的测试
 *
 * @author oyealex
 * @see Pipe#prepend(Spliterator)
 * @see Pipe#prepend(Pipe)
 * @see Pipe#prepend(Stream)
 * @see Pipe#prepend(Object[])
 * @see Pipe#prepend(Object)
 * @see Pipe#prependKeys(Map)
 * @see Pipe#prependKeys(Map, Predicate)
 * @see Pipe#prependValues(Map)
 * @see Pipe#prependValues(Map, Predicate)
 * @see Pipe#append(Spliterator)
 * @see Pipe#append(Pipe)
 * @see Pipe#append(Stream)
 * @see Pipe#append(Object[])
 * @see Pipe#append(Object)
 * @see Pipe#appendKeys(Map)
 * @see Pipe#appendKeys(Map, Predicate)
 * @see Pipe#appendValues(Map)
 * @see Pipe#appendValues(Map, Predicate)
 * @since 2023-06-05
 */
class PipeExpansionTest extends PipeTestFixture {
    @Test
    @DisplayName("能够正确将各种对象中的元素插入到流水线头部或尾部")
    void should_add_elements_from_some_objects_into_the_head_or_tail_of_pipe_rightly() {
        List<String> sample = genOddIntegerStrWithNullsList();
        List<String> expansion = genOddIntegerStrWithNullsList();
        List<String> prepended = addAll(expansion, sample);
        List<String> appended = addAll(sample, expansion);
        assertAll(() -> assertEquals(prepended, list(sample).prepend(expansion.spliterator()).toList()),
            () -> assertEquals(appended, list(sample).append(expansion.spliterator()).toList()),
            () -> assertEquals(prepended, list(sample).prepend(list(expansion)).toList()),
            () -> assertEquals(appended, list(sample).append(list(expansion)).toList()),
            () -> assertEquals(prepended, list(sample).prepend(expansion.stream()).toList()),
            () -> assertEquals(appended, list(sample).append(expansion.stream()).toList()),
            () -> assertEquals(prepended, list(sample).prepend(expansion.toArray(new String[0])).toList()),
            () -> assertEquals(appended, list(sample).append(expansion.toArray(new String[0])).toList()));
    }

    @Test
    @DisplayName("能够正确将元素插入到流水线头部或尾部")
    void should_add_element_into_the_head_or_tail_of_pipe_rightly() {
        List<String> sample = genOddIntegerStrWithNullsList();
        String element = "element";
        assertAll(() -> assertEquals(addAll(singletonList(null), sample), list(sample).prepend((String) null).toList()),
            () -> assertEquals(addAll(sample, singletonList(null)), list(sample).append((String) null).toList()),
            () -> assertEquals(addAll(singletonList(element), sample), list(sample).prepend(element).toList()),
            () -> assertEquals(addAll(sample, singletonList(element)), list(sample).append(element).toList()));
    }

    @Test
    @DisplayName("能够正确将map的key插入到流水线头部或尾部")
    void should_add_the_keys_of_map_into_the_head_or_tail_of_pipe_rightly() {
        Map<String, String> map = list(genRandomStrList()).toMap(String::toLowerCase, String::toUpperCase);
        List<String> sample = genRandomStrList();
        List<String> prepended = expandAndSortPartially(map.keySet(), sample, 0);
        List<String> appended = expandAndSortPartially(sample, map.keySet(), 1);
        assertAll(() -> assertEquals(prepended,
                list(sample).prependKeys(map).chain(pipe -> sortPartially(pipe, map.size(), 0)).toList()),
            () -> assertEquals(appended,
                list(sample).appendKeys(map).chain(pipe -> sortPartially(pipe, sample.size(), 1)).toList()));
    }

    @Test
    @DisplayName("能够正确将map的key插入到流水线头部或尾部")
    void should_add_the_keys_of_map_into_the_head_or_tail_of_pipe_rightly_with_value_predicate() {
        Map<String, String> map = list(genRandomStrList()).toMap(String::toLowerCase, String::toUpperCase);
        List<String> sample = genRandomStrList();
        Predicate<String> predicate = v -> v.length() > 3;
        List<String> selectedKeys = set(map.entrySet()).takeIf(entry -> predicate.test(entry.getValue()))
            .map(Map.Entry::getKey)
            .toList();
        List<String> prepended = expandAndSortPartially(selectedKeys, sample, 0);
        List<String> appended = expandAndSortPartially(sample, selectedKeys, 1);
        assertAll(() -> assertEquals(prepended, list(sample).prependKeys(map, predicate)
            .chain(pipe -> sortPartially(pipe, selectedKeys.size(), 0))
            .toList()), () -> assertEquals(appended,
            list(sample).appendKeys(map, predicate).chain(pipe -> sortPartially(pipe, sample.size(), 1)).toList()));
    }

    @Test
    @DisplayName("能够正确将map的value插入到流水线头部或尾部")
    void should_add_the_values_of_map_into_the_head_or_tail_of_pipe_rightly() {
        Map<String, String> map = list(genRandomStrList()).toMap(String::toLowerCase, String::toUpperCase);
        List<String> sample = genRandomStrList();
        List<String> prepended = expandAndSortPartially(map.values(), sample, 0);
        List<String> appended = expandAndSortPartially(sample, map.values(), 1);
        assertAll(() -> assertEquals(prepended,
                list(sample).prependValues(map).chain(pipe -> sortPartially(pipe, map.size(), 0)).toList()),
            () -> assertEquals(appended,
                list(sample).appendValues(map).chain(pipe -> sortPartially(pipe, sample.size(), 1)).toList()));
    }

    @Test
    @DisplayName("能够正确将map的value插入到流水线头部或尾部")
    void should_add_the_values_of_map_into_the_head_or_tail_of_pipe_rightly_with_value_predicate() {
        Map<String, String> map = list(genRandomStrList()).toMap(String::toLowerCase, String::toUpperCase);
        List<String> sample = genRandomStrList();
        Predicate<String> predicate = v -> v.length() > 3;
        List<String> selectedKeys = set(map.entrySet()).takeIf(entry -> predicate.test(entry.getKey()))
            .map(Map.Entry::getValue)
            .toList();
        List<String> prepended = expandAndSortPartially(selectedKeys, sample, 0);
        List<String> appended = expandAndSortPartially(sample, selectedKeys, 1);
        assertAll(() -> assertEquals(prepended, list(sample).prependValues(map, predicate)
            .chain(pipe -> sortPartially(pipe, selectedKeys.size(), 0))
            .toList()), () -> assertEquals(appended,
            list(sample).appendValues(map, predicate).chain(pipe -> sortPartially(pipe, sample.size(), 1)).toList()));
    }

    private <T> List<T> expandAndSortPartially(Collection<T> first, Collection<T> second, int sortPartIndex) {
        return list(addAll(first, second)).chain(pipe -> sortPartially(pipe, first.size(), sortPartIndex)).toList();
    }

    private <T> Pipe<T> sortPartially(Pipe<T> pipe, int partSize, long partIndex) {
        // 由于map的keySet或values无序，为了能够验证结果需要对这一部分数据排序
        return pipe.partitionOrderly((order, ignored) -> order == partSize ? BEGIN : IN)
            .mapOrderly((order, partition) -> order == partIndex ? partition.sort() : partition)
            .flatMap(identity());
    }

    // exception test

    @Test
    @DisplayName("当不能为null的参数为null时抛出异常")
    void should_throw_exception_when_required_non_null_param_is_null() {
        assertAll(() -> assertThrowsExactly(NullPointerException.class,
                () -> infiniteRandomStrPipe().prepend((Spliterator<String>) null)),
            () -> assertThrowsExactly(NullPointerException.class,
                () -> infiniteRandomStrPipe().prepend((Pipe<String>) null)),
            () -> assertThrowsExactly(NullPointerException.class,
                () -> infiniteRandomStrPipe().prepend((Stream<String>) null)),
            () -> assertThrowsExactly(NullPointerException.class,
                () -> infiniteRandomStrPipe().prepend((String[]) null)),
            () -> assertThrowsExactly(NullPointerException.class, () -> infiniteRandomStrPipe().prependKeys(null)),
            () -> assertThrowsExactly(NullPointerException.class,
                () -> infiniteRandomStrPipe().prependKeys(null, ignored -> true)),
            () -> assertThrowsExactly(NullPointerException.class,
                () -> infiniteRandomStrPipe().prependKeys(emptyMap(), null)),
            () -> assertThrowsExactly(NullPointerException.class, () -> infiniteRandomStrPipe().prependValues(null)),
            () -> assertThrowsExactly(NullPointerException.class,
                () -> infiniteRandomStrPipe().prependValues(null, ignored -> true)),
            () -> assertThrowsExactly(NullPointerException.class,
                () -> infiniteRandomStrPipe().prependValues(emptyMap(), null)),
            () -> assertThrowsExactly(NullPointerException.class,
                () -> infiniteRandomStrPipe().append((Spliterator<String>) null)),
            () -> assertThrowsExactly(NullPointerException.class,
                () -> infiniteRandomStrPipe().append((Pipe<String>) null)),
            () -> assertThrowsExactly(NullPointerException.class,
                () -> infiniteRandomStrPipe().append((Stream<String>) null)),
            () -> assertThrowsExactly(NullPointerException.class,
                () -> infiniteRandomStrPipe().append((String[]) null)),
            () -> assertThrowsExactly(NullPointerException.class, () -> infiniteRandomStrPipe().appendKeys(null)),
            () -> assertThrowsExactly(NullPointerException.class,
                () -> infiniteRandomStrPipe().appendKeys(null, ignored -> true)),
            () -> assertThrowsExactly(NullPointerException.class,
                () -> infiniteRandomStrPipe().appendKeys(emptyMap(), null)),
            () -> assertThrowsExactly(NullPointerException.class, () -> infiniteRandomStrPipe().appendValues(null)),
            () -> assertThrowsExactly(NullPointerException.class,
                () -> infiniteRandomStrPipe().appendValues(null, ignored -> true)),
            () -> assertThrowsExactly(NullPointerException.class,
                () -> infiniteRandomStrPipe().appendValues(emptyMap(), null)));
    }
}
