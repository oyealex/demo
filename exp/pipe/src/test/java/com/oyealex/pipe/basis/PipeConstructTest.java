package com.oyealex.pipe.basis;

import com.oyealex.pipe.PipeTestFixture;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Spliterator;
import java.util.function.Predicate;
import java.util.function.Supplier;
import java.util.function.UnaryOperator;
import java.util.stream.Stream;

import static com.oyealex.pipe.basis.Pipe.empty;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertThrowsExactly;

/**
 * 针对流水线构造系列API的测试。
 *
 * @author oyealex
 * @see Pipe#empty()
 * @see Pipe#spliterator(Spliterator)
 * @see Pipe#spliterator(Spliterator, int)
 * @see Pipe#singleton(Object)
 * @see Pipe#optional(Object)
 * @see Pipe#of(Object[])
 * @see Pipe#of(int, Object[])
 * @see Pipe#constant(Object, int)
 * @see Pipe#keys(Map)
 * @see Pipe#keys(Map, Predicate)
 * @see Pipe#values(Map)
 * @see Pipe#values(Map, Predicate)
 * @see Pipe#generate(Supplier)
 * @see Pipe#concat(Pipe[])
 * @see Pipe#stream(Stream)
 * @see Pipe#iterate(Object, UnaryOperator)
 * @see Pipe#list(List)
 * @see Pipe#list(List, int)
 * @see Pipe#set(Set)
 * @see Pipe#set(Set, int)
 * @see Pipe#collection(Collection)
 * @see Pipe#collection(Collection, int)
 * @since 2023-06-15
 */
class PipeConstructTest extends PipeTestFixture {
    // TODO 2023-06-15 01:16 continue
    // optimization test

    // exception test

    @Test
    @DisplayName("当不能为null的参数为null时抛出异常")
    void should_throw_exception_when_required_non_null_param_is_null() {
        assertAll(() -> assertThrowsExactly(NullPointerException.class, () -> empty().peek(null)));
    }
}
