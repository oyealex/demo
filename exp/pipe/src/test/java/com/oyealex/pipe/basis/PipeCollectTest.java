package com.oyealex.pipe.basis;

import com.oyealex.pipe.PipeTestFixture;
import com.oyealex.pipe.assist.IntBox;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Vector;
import java.util.function.Function;
import java.util.function.IntFunction;
import java.util.function.Supplier;

import static com.oyealex.pipe.basis.Pipe.constant;
import static com.oyealex.pipe.basis.Pipe.list;
import static com.oyealex.pipe.basis.Pipe.set;
import static java.util.Arrays.asList;
import static java.util.function.Function.identity;
import static java.util.stream.Collectors.toMap;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertThrowsExactly;

/**
 * 针对流水线收集元素到容器系列API的测试
 *
 * @author oyealex
 * @see Pipe#toSpliterator()
 * @see Pipe#toIterator()
 * @see Pipe#toArray(IntFunction)
 * @see Pipe#toList()
 * @see Pipe#toList(Supplier)
 * @see Pipe#toUnmodifiableList()
 * @see Pipe#toSet()
 * @see Pipe#toSet(Supplier)
 * @see Pipe#toUnmodifiableSet()
 * @see Pipe#toCollection(Supplier)
 * @see Pipe#toMapKeyed(Function)
 * @see Pipe#toMapKeyed(Function, Supplier)
 * @see Pipe#toMapValued(Function)
 * @see Pipe#toMapValued(Function, Supplier)
 * @see Pipe#toMap(Function, Function)
 * @see Pipe#toMap(Function, Function, Supplier)
 * @see Pipe#toUnmodifiableMapKeyed(Function)
 * @see Pipe#toUnmodifiableMapValued(Function)
 * @see Pipe#toUnmodifiableMap(Function, Function)
 * @since 2023-06-05
 */
class PipeCollectTest extends PipeTestFixture {
    @Test
    @DisplayName("能够正确将流水线元素收集到拆分器中")
    void should_collect_pipe_elements_to_spliterator_rightly() {
        List<String> sample = genRandomStrList();
        List<String> actual = new ArrayList<>(sample.size());
        list(sample).toSpliterator().forEachRemaining(actual::add);
        assertEquals(sample, actual);
    }

    @Test
    @DisplayName("能够正确将流水线元素收集到迭代器中")
    void should_collect_pipe_elements_to_iterator_rightly() {
        List<String> sample = genRandomStrList();
        List<String> actual = new ArrayList<>(sample.size());
        list(sample).toIterator().forEachRemaining(actual::add);
        assertEquals(sample, actual);
    }

    @Test
    @DisplayName("能够正确将流水线元素收集到数组中")
    void should_collect_pipe_elements_to_array_rightly() {
        List<String> sample = genRandomStrList();
        assertEquals(sample, asList(list(sample).toArray(String[]::new)));
    }

    @Test
    @DisplayName("能够正确将流水线元素收集到列表中")
    void should_collect_pipe_elements_to_list_rightly() {
        List<String> sample = genRandomStrList();
        assertAll(() -> assertEquals(sample, list(sample).toList()),
            () -> assertEquals(sample, list(sample).toList(ArrayList::new)),
            () -> assertEquals(ArrayList.class, list(sample).toList(ArrayList::new).getClass()),
            () -> assertEquals(sample, list(sample).toUnmodifiableList()),
            () -> assertThrows(UnsupportedOperationException.class, () -> list(sample).toUnmodifiableList().clear()));
    }

    @Test
    @DisplayName("能够正确将流水线元素收集到集合中")
    void should_collect_pipe_elements_to_set_rightly() {
        HashSet<String> sample = new HashSet<>(genRandomStrList());
        assertAll(() -> assertEquals(sample, set(sample).toSet()),
            () -> assertEquals(sample, set(sample).toSet(HashSet::new)),
            () -> assertEquals(HashSet.class, set(sample).toSet(HashSet::new).getClass()),
            () -> assertEquals(sample, set(sample).toUnmodifiableSet()),
            () -> assertThrows(UnsupportedOperationException.class, () -> set(sample).toUnmodifiableSet().clear()));
    }

    @Test
    @DisplayName("能够正确将流水线元素收集到容器中")
    void should_collect_pipe_elements_to_collection_rightly() {
        List<String> sample = genRandomStrList();
        assertAll(() -> assertEquals(new Vector<>(sample), list(sample).toCollection(Vector::new)),
            () -> assertEquals(Vector.class, list(sample).toCollection(Vector::new).getClass()));
    }

    @Test
    @DisplayName("能够正确将流水线元素收集到map中")
    void should_collect_pipe_elements_to_map_rightly() {
        List<String> sample = infiniteRandomStrPipe().distinct().limit(NORMAL_SIZE).toList();
        Function<String, String> lower = String::toLowerCase;
        Function<String, String> upper = String::toUpperCase;
        assertAll(() -> assertEquals(collectToMap(sample, lower, identity()), list(sample).toMapKeyed(lower)),
            () -> assertEquals(collectToMap(sample, lower, identity()), list(sample).toMapKeyed(lower, HashMap::new)),
            () -> assertEquals(HashMap.class, list(sample).toMapKeyed(lower, HashMap::new).getClass()),
            () -> assertEquals(collectToMap(sample, identity(), lower), list(sample).toMapValued(lower)),
            () -> assertEquals(collectToMap(sample, identity(), lower), list(sample).toMapValued(lower, HashMap::new)),
            () -> assertEquals(HashMap.class, list(sample).toMapValued(lower, HashMap::new).getClass()),
            () -> assertEquals(collectToMap(sample, lower, upper), list(sample).toMap(lower, upper)),
            () -> assertEquals(collectToMap(sample, lower, upper), list(sample).toMap(lower, upper, HashMap::new)),
            () -> assertEquals(HashMap.class, list(sample).toMap(lower, upper, HashMap::new).getClass()),
            () -> assertEquals(collectToMap(sample, lower, identity()), list(sample).toUnmodifiableMapKeyed(lower)),
            () -> assertThrows(UnsupportedOperationException.class,
                () -> list(sample).toUnmodifiableMapKeyed(lower).clear()),
            () -> assertEquals(collectToMap(sample, identity(), lower), list(sample).toUnmodifiableMapValued(lower)),
            () -> assertThrows(UnsupportedOperationException.class,
                () -> list(sample).toUnmodifiableMapValued(lower).clear()),
            () -> assertEquals(collectToMap(sample, lower, upper), list(sample).toUnmodifiableMap(lower, upper)),
            () -> assertThrows(UnsupportedOperationException.class,
                () -> list(sample).toUnmodifiableMap(lower, upper).clear()));
    }

    private <T, K, V> Map<K, V> collectToMap(Collection<T> collection, Function<? super T, ? extends K> keyMapper,
        Function<? super T, ? extends V> valueMapper) {
        return collection.stream().collect(toMap(keyMapper, valueMapper));
    }

    @Test
    @DisplayName("收集流水线元素到map中，重新映射键，如果映射的键会重复，则最终的map仅包含较后的元素")
    void should_retain_latter_one_when_collect_pipe_elements_to_map_with_mapped_duplicated_keys() {
        List<String> sample = genRandomStrList();
        Map<String, String> map = list(sample).toMapKeyed(ignored -> SOME_STR);
        assertEquals(sample.get(sample.size() - 1), map.get(SOME_STR));
    }

    @Test
    @DisplayName("收集流水线元素到map中，重新映射值，如果键会重复，则最终的map仅包含较后的元素")
    void should_retain_latter_one_when_collect_pipe_elements_to_map_with_duplicated_keys() {
        IntBox counter = IntBox.box();
        Map<String, Integer> map = constant(SOME_STR, 10).toMapValued(ignored -> counter.incrementAndGet());
        assertEquals(counter.get(), map.get(SOME_STR));
    }

    // exception test

    @Test
    @DisplayName("当不能为null的参数为null时抛出异常")
    void should_throw_exception_when_required_non_null_param_is_null() {
        assertAll(() -> assertThrowsExactly(NullPointerException.class, () -> infiniteIntegerPipe().toArray(null)),
            () -> assertThrowsExactly(NullPointerException.class, () -> infiniteIntegerPipe().toList(null)),
            () -> assertThrowsExactly(NullPointerException.class, () -> infiniteIntegerPipe().toSet(null)),
            () -> assertThrowsExactly(NullPointerException.class, () -> infiniteIntegerPipe().toCollection(null)),
            () -> assertThrowsExactly(NullPointerException.class, () -> infiniteIntegerPipe().toMapKeyed(null)),
            () -> assertThrowsExactly(NullPointerException.class,
                () -> infiniteIntegerPipe().toMapKeyed(null, HashMap::new)),
            () -> assertThrowsExactly(NullPointerException.class,
                () -> infiniteIntegerPipe().toMapKeyed(identity(), null)),
            () -> assertThrowsExactly(NullPointerException.class, () -> infiniteIntegerPipe().toMapValued(null)),
            () -> assertThrowsExactly(NullPointerException.class,
                () -> infiniteIntegerPipe().toMapValued(null, HashMap::new)),
            () -> assertThrowsExactly(NullPointerException.class,
                () -> infiniteIntegerPipe().toMapValued(identity(), null)),
            () -> assertThrowsExactly(NullPointerException.class, () -> infiniteIntegerPipe().toMap(null, identity())),
            () -> assertThrowsExactly(NullPointerException.class, () -> infiniteIntegerPipe().toMap(identity(), null)),
            () -> assertThrowsExactly(NullPointerException.class,
                () -> infiniteIntegerPipe().toMap(null, identity(), HashMap::new)),
            () -> assertThrowsExactly(NullPointerException.class,
                () -> infiniteIntegerPipe().toMap(identity(), null, HashMap::new)),
            () -> assertThrowsExactly(NullPointerException.class,
                () -> infiniteIntegerPipe().toMap(identity(), identity(), null)),
            () -> assertThrowsExactly(NullPointerException.class,
                () -> infiniteIntegerPipe().toUnmodifiableMapKeyed(null)),
            () -> assertThrowsExactly(NullPointerException.class,
                () -> infiniteIntegerPipe().toUnmodifiableMapValued(null)),
            () -> assertThrowsExactly(NullPointerException.class,
                () -> infiniteIntegerPipe().toUnmodifiableMap(null, identity())),
            () -> assertThrowsExactly(NullPointerException.class,
                () -> infiniteIntegerPipe().toUnmodifiableMap(identity(), null)));
    }
}
