package com.oyealex.pipe.basis.api;

import com.oyealex.pipe.assist.Tuple;
import com.oyealex.pipe.base.BasePipe;
import com.oyealex.pipe.basis.Pipes;
import com.oyealex.pipe.basis.api.policy.MergePolicy;
import com.oyealex.pipe.basis.api.policy.MergeRemainingPolicy;
import com.oyealex.pipe.basis.api.policy.PartitionPolicy;
import com.oyealex.pipe.basis.functional.LongBiConsumer;
import com.oyealex.pipe.basis.functional.LongBiFunction;
import com.oyealex.pipe.basis.functional.LongBiPredicate;
import com.oyealex.pipe.bi.BiPipe;
import com.oyealex.pipe.tri.TriPipe;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Random;
import java.util.Set;
import java.util.Spliterator;
import java.util.Spliterators;
import java.util.StringJoiner;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.function.BinaryOperator;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.IntFunction;
import java.util.function.Predicate;
import java.util.function.Supplier;
import java.util.function.ToDoubleFunction;
import java.util.function.ToIntFunction;
import java.util.function.ToLongFunction;
import java.util.stream.Stream;

import static com.oyealex.pipe.basis.api.policy.MergePolicy.OURS_FIRST;
import static com.oyealex.pipe.basis.api.policy.MergePolicy.THEIRS_FIRST;
import static com.oyealex.pipe.basis.api.policy.MergeRemainingPolicy.TAKE_REMAINING;
import static com.oyealex.pipe.flag.PipeFlag.EMPTY;
import static com.oyealex.pipe.utils.MiscUtil.isStdIdentify;
import static com.oyealex.pipe.utils.MiscUtil.optimizedReverseOrder;
import static java.util.Comparator.comparing;
import static java.util.Comparator.naturalOrder;
import static java.util.Objects.requireNonNull;
import static java.util.function.Function.identity;

/**
 * 流水线接口
 * <p/>
 * 定义一条<b>串行</b>流水线的接口。
 * <p/>
 * 流水线由单个数据源和多个数据操作组成，数据操作包括非终结操作和终结操作，每个流水线至少包含一个终结操作，
 * 流水线的最终结果由终结操作给出。
 * <p/>
 *
 * <h1>数据源</h1>
 * 数据源有一系列的元素组成，在执行流水线的时候逐个经过流水线定义的每个操作，得到最终结果。
 * <h2>有限数据源</h2>
 * <h2>无限数据源</h2>
 * <p/>
 *
 * <h1>数据操作</h1>
 * 流水线由一个个的节点组成，每个节点记录了需要对流水线中数据执行的操作，在最终执行流水线的时候，
 * 每个节点将记录的操作按顺序对数据执行。
 * <h2>非终结操作</h2>
 * 非终结操作有明显的输入数据和输出数据，输入数据来自流水线上游节点的输出，数据数据会作为输入流入下游节点。
 * <br/>
 * 非终结操作不会对流水线产生任何状态变更，只有最终实际执行流水线的时候起作用。
 * <h2>终结操作</h2>
 * 终结操作没有下游输出数据，但是一般会有一个最终结果（部分API不会产生显式结果，可以认为最终结果为{@code null}，
 * 例如{@link #forEach(Consumer)}）。
 * <br/>
 * 终结操作会启动流水线的数据运算，将数据源的数据<b>串行地</b>逐个经过各个节点地处理，最终流入终结操作产生一个最终结果。
 * <h2>短路优化</h2>
 * 并不是所有情况下数据源中的数据都会经历完整的运算，有些操作会导致流水线短路。
 * <br/>
 * 例如{@link #limit(long)}：
 * <pre>{@code
 *     pipe.limit(10).toList();
 * }</pre>
 * 此流水线将数据源中的前10个元素转为列表，这意味着第10个元素之后的数据没有任何继续遍历的必要了，一旦收集到的数据数量达到要求，
 * 即可触发短路停止流水线的继续执行，因为此时已经得到最终结果了，继续执行流水线并不会改变结果。
 * <br/>
 * 相反，有些操作要求处理完全部数据才能得到最终结果，例如{@link #findLast()}，如果不处理完所有数据是无法知道最后一个元素是什么。
 * <br/>
 * 短路操作是相对的，即一条流水线可能在部分操作上短路，在部分操作上非短路，例如：
 * <pre>{@code
 *     pipe.limit(10).findLast();
 * }</pre>
 * 假设原始数据源包含100个元素，那么第一个操作{@code limit(10)}会在处理了10个元素之后短路，导致后续的90个元素被忽略，
 * 而终结操作{@code findLast()}则不会对这来自第一个操作的10个元素执行短路，只有处理完流入此节点的全部10个元素之后才能得到最终结果。
 * <p/>
 *
 * <h1>线程安全</h1>
 * 由于实际运用中并行流水线很少使用，为了降低实现复杂度并执行更多的针对性优化，并结合本库的核心目标，
 * 在当前实现中流水线的任何API均不是线程安全的，哪怕提供了线程安全的数据源和操作，流水线总是串行执行数据操作。
 * <p/>
 * <i>不排除后续扩展为支持线程安全的可能。</i>
 *
 * @param <E> 数据类型
 * @author oyealex
 * @since 2023-02-09
 */
public interface Pipe<E> extends BasePipe<E, Pipe<E>> {
    /**
     * 根据给定断言保留元素。
     *
     * @param predicate 断言方法，满足断言的元素会保留。
     * @return 新的流水线。
     * @throws NullPointerException 当{@code predicate}为null时抛出。
     * @see #takeIfOrderly(LongBiPredicate)
     * @see #takeWhile(Predicate)
     * @see #dropIf(Predicate)
     * @see #filter(Predicate)
     */
    Pipe<E> takeIf(Predicate<? super E> predicate);

    /**
     * 根据给定断言过滤元素。
     *
     * @param predicate 断言方法，满足断言的元素会被保留。
     * @return 新的流水线。
     * @throws NullPointerException 当{@code predicate}为null时抛出。
     * @apiNote 同 {@link #takeIf(Predicate)}
     * @see #takeIf(Predicate)
     * @see Stream#filter(Predicate)
     */
    default Pipe<E> filter(Predicate<? super E> predicate) {
        return takeIf(predicate);
    }

    /**
     * 根据给定断言保留元素，断言支持访问元素次序。
     * <p/>
     * 大致等同于：
     * <pre>{@code
     * long index = 0L;
     * for (E element : pipeElements) {
     *     if (predicate.test(index++, element)) {
     *         // do something
     *     }
     * }
     * }</pre>
     *
     * @param predicate 断言方法：第一个参数为访问的元素在流水线中的次序，从0开始计算；第二个参数为需要判断是否保留的元素。
     * @return 新的流水线。
     * @throws NullPointerException 当{@code predicate}为null时抛出。
     * @see #takeIf(Predicate)
     * @see #dropIfOrderly(LongBiPredicate)
     */
    Pipe<E> takeIfOrderly(LongBiPredicate<? super E> predicate);

    /**
     * 保留第一个元素，丢弃其他所有元素。
     *
     * @return 最多只含有一个元素的新的流水线。
     * @apiNote 同 {@code limit(1)}。
     * @see #takeFirst(int)
     * @see #takeLast()
     * @see #findFirst()
     * @see #limit(long)
     */
    default Pipe<E> takeFirst() {
        return limit(1L);
    }

    /**
     * 保留前{@code count}个元素，丢弃其他所有元素。
     *
     * @return 包含前 {@code count}个元素的新的流水线。
     * @throws IllegalArgumentException 当需要保留的元素数量小于0时抛出。
     * @apiNote 同 {@link #limit(long)}。
     * @see #takeFirst()
     * @see #takeLast(int)
     * @see #limit(long)
     */
    default Pipe<E> takeFirst(int count) {
        return limit(count);
    }

    /**
     * 保留最后一个元素，丢弃其他所有元素。
     *
     * @return 最多只含有一个元素的新的流水线。
     * @see #takeLast(int)
     * @see #takeFirst()
     * @see #findLast()
     */
    default Pipe<E> takeLast() { // OPT 2023-05-26 00:08 在无限流上调用此方法抛出异常
        return takeLast(1);
    }

    /**
     * 保留最后给定数量的元素。
     *
     * @param count 需要保留的最后几个元素的数量。
     * @return 新的流水线。
     * @throws IllegalArgumentException 当{@code count}为负值时抛出。
     * @see #takeLast()
     * @see #takeFirst(int)
     */
    Pipe<E> takeLast(int count);

    /**
     * 保留元素直到给定的断言首次为{@code False}，丢弃之后的元素。
     * <p/>
     * 大致等同于：
     * <pre>{@code
     * for (E element : pipeElements) {
     *     if (predicate.test(element)) {
     *         // do something
     *     } else {
     *         break;
     *     }
     * }
     * }</pre>
     *
     * @param predicate 断言方法。
     * @return 新的流水线。
     * @throws NullPointerException 当{@code predicate}为null时抛出。
     * @apiNote 此方法支持对流水线执行短路优化，当断言首次返回{@code false}后，流水线剩余的元素允许被短路，
     * 因此{@code predicate}可能无法测试每个元素。
     * @see #takeWhileOrderly(LongBiPredicate)
     * @see #dropWhile(Predicate)
     */
    Pipe<E> takeWhile(Predicate<? super E> predicate);

    /**
     * 保留元素直到给定的断言首次为{@code False}，丢弃之后的元素，断言支持访问{@code long}类型的元素次序。
     * <p/>
     * 大致等同于：
     * <pre>{@code
     * long index = 0L;
     * for (E element : pipeElements) {
     *     if (predicate.test(index++, element)) {
     *         // do something
     *     } else {
     *         break;
     *     }
     * }
     * }</pre>
     *
     * @param predicate 断言方法：第一个参数为访问的元素在流水线中的次序，从0开始计算；第二个参数为判断是否需要保留的元素。
     * @return 新的流水线。
     * @throws NullPointerException 当{@code predicate}为null时抛出。
     * @apiNote 此方法支持对流水线执行短路优化，当断言首次返回{@code false}后，流水线剩余的元素允许被短路，
     * 因此{@code predicate}可能无法测试每个元素。
     * @see #takeWhile(Predicate)
     * @see #dropWhileOrderly(LongBiPredicate)
     */
    Pipe<E> takeWhileOrderly(LongBiPredicate<? super E> predicate);

    /**
     * 根据给定断言的结果丢弃元素。
     *
     * @param predicate 断言方法，满足断言的元素会丢弃。
     * @return 新的流水线。
     * @throws NullPointerException 当{@code predicate}为null时抛出。
     * @see #dropIfOrderly(LongBiPredicate)
     * @see #dropWhile(Predicate)
     * @see #takeIf(Predicate)
     */
    default Pipe<E> dropIf(Predicate<? super E> predicate) {
        return takeIf(requireNonNull(predicate).negate());
    }

    /**
     * 根据给定断言丢弃元素，断言支持访问元素次序。
     * <p/>
     * 大致等同于：
     * <pre>{@code
     * long index = 0L;
     * for (E element : pipeElements) {
     *     if (!predicate.test(index++, element)) {
     *         // do something
     *     }
     * }
     * }</pre>
     *
     * @param predicate 断言方法：第一个参数为访问的元素在流水线中的次序，从0开始计算；第二个参数为需要判断是否丢弃的元素。
     * @return 新的流水线。
     * @throws NullPointerException 当{@code predicate}为null时抛出。
     * @see #dropIf(Predicate)
     * @see #takeIfOrderly(LongBiPredicate)
     */
    default Pipe<E> dropIfOrderly(LongBiPredicate<? super E> predicate) {
        return takeIfOrderly(requireNonNull(predicate).negate());
    }

    /**
     * 丢弃第一个元素，保留剩下的所有元素。
     *
     * @return 丢弃了第一个元素的新的流水线。
     * @apiNote 同 {@code skip(1)}
     * @see #dropFirst(int)
     * @see #takeFirst()
     * @see #skip(long)
     */
    default Pipe<E> dropFirst() {
        return skip(1L);
    }

    /**
     * 丢弃前{@code count}个元素，保留其他所有元素。
     *
     * @return 丢弃了前 {@code count}个元素的新的流水线。
     * @throws IllegalArgumentException 当需要丢弃的元素数量小于0时抛出。
     * @apiNote 同 {@link #skip(long)}。
     * @see #dropFirst()
     * @see #dropLast(int)
     * @see #skip(long)
     */
    default Pipe<E> dropFirst(int count) {
        return skip(count);
    }

    /**
     * 丢弃最后一个元素，保留其他所有元素。
     *
     * @return 丢弃了最后一个元素的新的流水线。
     * @see #dropLast(int)
     * @see #dropFirst()
     */
    default Pipe<E> dropLast() {
        return dropLast(1);
    }

    /**
     * 保留最后给定数量的元素。
     *
     * @param count 需要保留的最后几个元素的数量。
     * @return 新的流水线。
     * @throws IllegalArgumentException 当{@code count}为负值时抛出。
     * @see #dropLast()
     * @see #dropFirst(int)
     */
    Pipe<E> dropLast(int count);

    /**
     * 丢弃元素直到给定的断言首次为{@code False}，保留之后的元素。
     * <p/>
     * 大致等同于：
     * <pre>{@code
     * boolean shouldTake = false;
     * for (E element : pipeElements) {
     *     if (shouldTake || (shouldTake = !predicate.test(element))) {
     *         // do something
     *     }
     * }
     * }</pre>
     *
     * @param predicate 断言方法。
     * @return 新的流水线。
     * @throws NullPointerException 当{@code predicate}为null时抛出。
     * @apiNote 当断言首次返回 {@code false}后，流水线剩余的元素直接流向下游，无需再使用{@code predicate}测试元素，
     * 因此{@code predicate}可能无法测试每个元素。
     * @see #dropWhileOrderly(LongBiPredicate)
     * @see #takeWhile(Predicate)
     */
    Pipe<E> dropWhile(Predicate<? super E> predicate);

    /**
     * 丢弃元素直到给定的断言首次为{@code False}，保留之后的元素。
     * <p/>
     * 大致等同于：
     * <pre>{@code
     * long index = 0L;
     * boolean shouldTake = false;
     * for (E element : pipeElements) {
     *     if (shouldTake || (shouldTake = !predicate.test(index++, element))) {
     *         // do something
     *     }
     * }
     * }</pre>
     *
     * @param predicate 断言方法：第一个参数为访问的元素在流水线中的次序，从0开始计算；第二个参数为判断是否需要保留的元素。
     * @return 新的流水线。
     * @throws NullPointerException 当{@code predicate}为null时抛出。
     * @apiNote 当断言首次返回 {@code false}后，流水线剩余的元素直接流向下游，无需再使用{@code predicate}测试元素，
     * 因此{@code predicate}可能无法测试每个元素。
     * @see #dropWhile(Predicate)
     * @see #takeWhileOrderly(LongBiPredicate)
     */
    Pipe<E> dropWhileOrderly(LongBiPredicate<? super E> predicate);

    /**
     * 丢弃流水线中的{@code null}，仅保留非空的元素。
     *
     * @return 新的流水线
     * @see #dropNullBy(Function)
     */
    Pipe<E> dropNull();

    /**
     * 仅保留按照给定方法映射的结果不为{@code null}的元素。
     * <p/>
     * 大致等同于：
     * <pre>{@code
     * for (E element : pipeElements) {
     *     if (mapper.apply(element) != null) {
     *         // do something
     *     }
     * }
     * }</pre>
     *
     * @param mapper 映射方法
     * @return 新的流水线
     * @throws NullPointerException 当{@code mapper}为null时抛出。
     * @see #dropNull()
     */
    default Pipe<E> dropNullBy(Function<? super E, ?> mapper) {
        requireNonNull(mapper);
        return takeIf(value -> mapper.apply(value) != null);
    }

    /**
     * 将此流水线中的元素映射为其他类型。
     *
     * @param mapper 映射方法
     * @param <R> 新的类型
     * @return 包含新类型元素的流水线
     * @throws NullPointerException 当{@code mapper}为null时抛出
     * @see Stream#map(Function)
     */
    <R> Pipe<R> map(Function<? super E, ? extends R> mapper);

    /**
     * 将流水线中的数据映射为字符串。
     *
     * @return 包含数据字符串的流水线
     */
    default Pipe<String> mapToString() {
        return map(Objects::toString);
    }

    /**
     * 将流水线中的数据映射为字符串。
     *
     * @param nullDefault 当数据为{@code null}时的目标字符串。
     * @return 包含数据字符串的流水线
     */
    default Pipe<String> mapToString(String nullDefault) {
        return map(value -> Objects.toString(value, nullDefault));
    }

    default Pipe<E> mapIf(Predicate<? super E> condition, E replacement) {
        return mapIf(condition, () -> replacement);
    }

    default Pipe<E> mapIf(Predicate<? super E> condition, Supplier<? extends E> replacementSupplier) {
        return mapIf(condition, ignoredValue -> replacementSupplier.get());
    }

    Pipe<E> mapIf(Predicate<? super E> condition, Function<? super E, ? extends E> mapper);

    Pipe<E> mapIf(Function<? super E, Optional<? extends E>> mapper);

    Pipe<E> mapNull(Supplier<? extends E> supplier);

    default Pipe<E> mapNull(E value) {
        requireNonNull(value);
        return mapIf(Objects::isNull, nullValue -> value);
    }

    /**
     * 将此流水线中的元素映射为其他类型，映射方法支持访问元素在流水线中的次序，从0开始计算，使用{@code long}类型的数据表示次序。
     *
     * @param mapper 映射方法：第一个参数为访问的元素在流水线中的次序，从0开始计算；第二个参数为需要映射的元素。
     * @param <R> 新的类型
     * @return 包含新类型元素的流水线
     * @throws NullPointerException 当{@code mapper}为null时抛出
     */
    <R> Pipe<R> mapOrderly(LongBiFunction<? super E, ? extends R> mapper);

    /**
     * 将流水线中的元素映射为int类型。
     *
     * @param mapper 映射方法
     * @return int流水线
     * @throws NullPointerException 当{@code mapper}为null时抛出
     * @see Stream#mapToInt(ToIntFunction)
     */
    IntPipe mapToInt(ToIntFunction<? super E> mapper);

    /**
     * 将流水线中的元素映射为int类型，支持访问元素的次序。
     *
     * @param mapper 映射方法：第一个参数为访问的元素在流水线中的次序，从0开始计算；第二个参数为需要映射的元素。
     * @return int流水线
     * @throws NullPointerException 当{@code mapper}为null时抛出
     * @see Stream#mapToInt(ToIntFunction)
     */
    // OPT 2023-05-10 01:39 添加对应的基于次序的函数式接口
    IntPipe mapToIntOrderly(ToIntFunction<? super E> mapper);

    /**
     * 将流水线中的元素映射为long类型。
     *
     * @param mapper 映射方法
     * @return long流水线
     * @throws NullPointerException 当{@code mapper}为null时抛出
     * @see Stream#mapToLong(ToLongFunction)
     */
    LongPipe mapToLong(ToLongFunction<? super E> mapper);

    /**
     * 将流水线中的元素映射为long类型，支持访问元素的次序。
     *
     * @param mapper 映射方法：第一个参数为访问的元素在流水线中的次序，从0开始计算；第二个参数为需要映射的元素。
     * @return long流水线
     * @throws NullPointerException 当{@code mapper}为null时抛出
     * @see Stream#mapToLong(ToLongFunction)
     */
    // OPT 2023-05-10 01:39 添加对应的基于次序的函数式接口
    LongPipe mapToLongOrderly(ToLongFunction<? super E> mapper);

    /**
     * 将流水线中的元素映射为double类型。
     *
     * @param mapper 映射方法
     * @return double流水线
     * @throws NullPointerException 当{@code mapper}为null时抛出
     * @see Stream#mapToDouble(ToDoubleFunction)
     */
    DoublePipe mapToDouble(ToDoubleFunction<? super E> mapper);

    /**
     * 将流水线中的元素映射为double类型，支持访问元素的次序。
     *
     * @param mapper 映射方法：第一个参数为访问的元素在流水线中的次序，从0开始计算；第二个参数为需要映射的元素。
     * @return double流水线
     * @throws NullPointerException 当{@code mapper}为null时抛出
     * @see Stream#mapToDouble(ToDoubleFunction)
     */
    // OPT 2023-05-10 01:39 添加对应的基于次序的函数式接口
    DoublePipe mapToDoubleOrderly(ToDoubleFunction<? super E> mapper);

    /**
     * 将流水线中的元素映射为新的流水线，并按照次序拼接为一条流水线。
     *
     * @param mapper 映射方法
     * @param <R> 新流水线中的元素类型
     * @return 映射并拼接后的流水线
     * @throws NullPointerException 当{@code mapper}为null时抛出
     * @see Stream#flatMap(Function)
     */
    <R> Pipe<R> flatMap(Function<? super E, ? extends Pipe<? extends R>> mapper);

    default <R> Pipe<R> flatMapCollection(Function<? super E, ? extends Collection<? extends R>> mapper) {
        return map(mapper).flatMap(Pipes::iterable);
    }

    /**
     * 将流水线中的元素映射为新的流水线，并按照次序拼接为一条流水线，支持访问元素的次序。
     *
     * @param mapper 映射方法：第一个参数为访问的元素在流水线中的次序，从0开始计算；第二个参数为需要映射的元素。
     * @param <R> 新流水线中的元素类型
     * @return 映射并拼接后的流水线
     * @throws NullPointerException 当{@code mapper}为null时抛出
     * @see Stream#flatMap(Function)
     */
    <R> Pipe<R> flatMapOrderly(LongBiFunction<? super E, ? extends Pipe<? extends R>> mapper);

    /**
     * 将流水线中的元素映射为新的int流水线，并按照次序拼接为一条int流水线。
     *
     * @param mapper 映射方法
     * @return 映射并拼接后的int流水线
     * @throws NullPointerException 当{@code mapper}为null时抛出
     * @see Stream#flatMapToInt(Function)
     */
    IntPipe flatMapToInt(Function<? super E, ? extends IntPipe> mapper);

    /**
     * 将流水线中的元素映射为新的int流水线，并按照次序拼接为一条int流水线，支持访问元素的次序。
     *
     * @param mapper 映射方法：第一个参数为访问的元素在流水线中的次序，从0开始计算；第二个参数为需要映射的元素。
     * @return 映射并拼接后的int流水线
     * @throws NullPointerException 当{@code mapper}为null时抛出
     * @see Stream#flatMapToInt(Function)
     */
    IntPipe flatMapToIntOrderly(LongBiFunction<? super E, ? extends IntPipe> mapper);

    /**
     * 将流水线中的元素映射为新的long流水线，并按照次序拼接为一条long流水线。
     *
     * @param mapper 映射方法
     * @return 映射并拼接后的long流水线
     * @throws NullPointerException 当{@code mapper}为null时抛出
     * @see Stream#flatMapToLong(Function)
     */
    LongPipe flatMapToLong(Function<? super E, ? extends LongPipe> mapper);

    /**
     * 将流水线中的元素映射为新的long流水线，并按照次序拼接为一条long流水线，支持访问元素的次序。
     *
     * @param mapper 映射方法：第一个参数为访问的元素在流水线中的次序，从0开始计算；第二个参数为需要映射的元素。
     * @return 映射并拼接后的long流水线
     * @throws NullPointerException 当{@code mapper}为null时抛出
     * @see Stream#flatMapToLong(Function)
     */
    LongPipe flatMapToLongOrderly(LongBiFunction<? super E, ? extends LongPipe> mapper);

    /**
     * 将流水线中的元素映射为新的double流水线，并按照次序拼接为一条double流水线。
     *
     * @param mapper 映射方法
     * @return 映射并拼接后的double流水线
     * @throws NullPointerException 当{@code mapper}为null时抛出
     * @see Stream#flatMapToDouble(Function)
     */
    DoublePipe flatMapToDouble(Function<? super E, ? extends DoublePipe> mapper);

    /**
     * 将流水线中的元素映射为新的double流水线，并按照次序拼接为一条double流水线，支持访问元素的次序。
     *
     * @param mapper 映射方法：第一个参数为访问的元素在流水线中的次序，从0开始计算；第二个参数为需要映射的元素。
     * @return 映射并拼接后的double流水线
     * @throws NullPointerException 当{@code mapper}为null时抛出
     * @see Stream#flatMapToDouble(Function)
     */
    DoublePipe flatMapToDoubleOrderly(LongBiFunction<? super E, ? extends DoublePipe> mapper);

    Pipe<Pipe<E>> flatMapSingleton();

    /**
     * 使用给定的映射方法，将此流水线扩展为两元组的流水线，其中两元组的第一个元素仍然为当前流水线中的元素。
     *
     * @param secondMapper 两元组第二个元素的映射方法
     * @param <S> 两元组第二个元素的类型
     * @return 映射后的两元组流水线
     * @throws NullPointerException 当映射方法为null时抛出
     */
    default <S> BiPipe<E, S> extendToTuple(Function<? super E, ? extends S> secondMapper) {
        return extendToTuple(identity(), secondMapper);
    }

    /**
     * 使用给定的映射方法，将此流水线扩展为两元组的流水线。
     *
     * @param firstMapper 两元组第一个元素的映射方法
     * @param secondMapper 两元组第二个元素的映射方法
     * @param <F> 两元组第一个元素的类型
     * @param <S> 两元组第二个元素的类型
     * @return 映射后的两元组流水线
     * @throws NullPointerException 当任意映射方法为null时抛出
     */
    <F, S> BiPipe<F, S> extendToTuple(Function<? super E, ? extends F> firstMapper,
        Function<? super E, ? extends S> secondMapper);

    BiPipe<E, E> pairExtend(boolean keepLastIncompletePair);

    /**
     * 使用给定的映射方法，将此流水线扩展为三元组的流水线。
     *
     * @param firstMapper 三元组第一个元素的映射方法
     * @param secondMapper 三元组第二个元素的映射方法
     * @param thirdMapper 三元组第三个元素的映射方法
     * @param <F> 三元组第一个元素的类型
     * @param <S> 三元组第二个元素的类型
     * @param <T> 三元组第三个元素的类型
     * @return 映射后的三元组流水线
     * @throws NullPointerException 当任意映射方法为null时抛出
     */
    <F, S, T> TriPipe<F, S, T> extendToTriple(Function<? super E, ? extends F> firstMapper,
        Function<? super E, ? extends S> secondMapper, Function<? super E, ? extends T> thirdMapper);

    /**
     * 对流水线中的元素去重，以{@link Object#equals(Object)}为依据。
     *
     * @return 元素去重之后的流水线
     * @see Stream#distinct()
     */
    Pipe<E> distinct();

    /**
     * 对流水线中的元素去重，以给定的映射结果为依据。
     *
     * @param mapper 去重依据的映射方法
     * @param <K> 映射结果的类型
     * @return 元素去重之后的流水线
     * @throws NullPointerException 当{@code mapper}为null时抛出
     */
    <K> Pipe<E> distinctBy(Function<? super E, ? extends K> mapper);

    /**
     * 对流水线中的元素去重，以给定的映射结果为依据，支持访问元素次序。
     *
     * @param mapper 去重依据的映射方法：第一个参数为访问的元素在流水线中的次序，从0开始计算；第二个参数为需要映射的元素。
     * @param <K> 映射结果的类型
     * @return 元素去重之后的流水线
     * @throws NullPointerException 当{@code mapper}为null时抛出
     */
    <K> Pipe<E> distinctByOrderly(LongBiFunction<? super E, ? extends K> mapper);

    /**
     * 对流水线中的元素排序，以默认顺序排序。
     * <p/>
     * 要求元素实现了{@link Comparable}，否则可能在流水线的终结操作中抛出{@link ClassCastException}异常。
     *
     * @return 元素排序后的流水线
     * @apiNote 流水线会针对元素的排序情况进行优化：例如如果元素已经处于自然有序状态，则本次排序会被省略；
     * 或者如果元素已经处于自然逆序状态，则会以相对高效的逆序代替自然排序，
     * 此时每个元素的{@link Comparable#compareTo(Object)}方法调用会被省略。
     * @see Stream#sorted()
     */
    default Pipe<E> sort() {
        return sort(null);
    }

    /**
     * 对流水线中的元素排序，以给定的比较方法排序。
     *
     * @param comparator 元素比较方法
     * @return 元素排序后的流水线
     * @see Stream#sorted(Comparator)
     */
    Pipe<E> sort(Comparator<? super E> comparator);

    /**
     * 对流水线中的元素排序，以默认顺序排序，排序的依据为映射后的结果。
     * <p/>
     * 要求映射后的结果类型{@code R}实现了{@link Comparable}。
     *
     * @param mapper 排序依据的映射方法
     * @param <K> 映射结果的类型
     * @return 元素排序后的流水线
     */
    default <K extends Comparable<? super K>> Pipe<E> sortBy(Function<? super E, ? extends K> mapper) {
        return isStdIdentify(mapper) ? sort() : sort(comparing(mapper));
    }

    /**
     * 对流水线中的元素排序，以给定的比较方法排序，排序的依据为映射后的结果。
     *
     * @param mapper 排序依据的映射方法
     * @param comparator 元素比较方法
     * @param <K> 映射结果的类型
     * @return 元素排序后的流水线
     */
    @SuppressWarnings("unchecked")
    default <K> Pipe<E> sortBy(Function<? super E, ? extends K> mapper, Comparator<? super K> comparator) {
        return isStdIdentify(mapper) ? sort((Comparator<? super E>) comparator) : sort(comparing(mapper, comparator));
    }

    /**
     * 对流水线中的元素排序，以默认顺序排序，排序的依据为映射后的结果，支持访问元素次序。
     * <p/>
     * 要求映射后的结果类型{@code R}实现了{@link Comparable}。
     *
     * @param mapper 排序依据的映射方法：第一个参数为访问的元素在流水线中的次序，从0开始计算；第二个参数为需要映射的元素。
     * @param <K> 映射结果的类型
     * @return 元素排序后的流水线
     */
    default <K extends Comparable<? super K>> Pipe<E> sortByOrderly(LongBiFunction<? super E, ? extends K> mapper) {
        return sortByOrderly(mapper, naturalOrder());
    }

    /**
     * 对流水线中的元素排序，以给定的比较方法排序，排序的依据为映射后的结果。
     *
     * @param mapper 排序依据的映射方法：第一个参数为访问的元素在流水线中的次序，从0开始计算；第二个参数为需要映射的元素。
     * @param comparator 元素比较方法
     * @param <R> 映射结果的类型
     * @return 元素排序后的流水线
     */
    <R> Pipe<E> sortByOrderly(LongBiFunction<? super E, ? extends R> mapper, Comparator<? super R> comparator);

    /**
     * 将流水线中的元素按照当前顺序颠倒。
     *
     * @return 元素顺序颠倒后的流水线
     */
    Pipe<E> reverse();

    /**
     * 随机打乱流水线中的元素。
     *
     * @return 元素顺序被打乱后新的流水线
     */
    default Pipe<E> shuffle() {
        return shuffle(new Random());
    }

    /**
     * 使用给定的随机对象打乱流水线中的元素。
     *
     * @param random 用于随机元素次序的随机对象
     * @return 元素顺序被打乱后新的流水线
     */
    Pipe<E> shuffle(Random random);

    /**
     * 以给定方法访问流水线中的元素。
     *
     * @param consumer 元素访问方法
     * @return 新的流水线
     * @apiNote 不同于经典Stream会优化某些场景下的访问方法调用，Pipe不会主动优化此访问方法。
     * @see Stream#peek(Consumer)
     */
    Pipe<E> peek(Consumer<? super E> consumer);

    /**
     * 以给定方法访问流水线中的元素，访问方法支持访问元素在流水线中的次序。
     *
     * @param consumer 元素访问方法：第一个参数为访问的元素在流水线中的次序，从0开始计算；第二个参数为访问的元素。
     * @return 新的流水线
     * @apiNote 不同于经典Stream会优化某些场景下的访问方法调用，Pipe不会主动优化此访问方法。
     */
    Pipe<E> peekOrderly(LongBiConsumer<? super E> consumer);

    /**
     * 仅保留给定数量的元素。
     *
     * @param size 需要保留的元素
     * @return 新的流水线
     * @throws IllegalArgumentException 当需要保留的元素数量小于0时抛出
     * @see Stream#limit(long)
     */
    Pipe<E> limit(long size);

    /**
     * 跳过指定数量的元素。
     *
     * @param size 需要跳过元素的数量
     * @return 新的流水线
     * @throws IllegalArgumentException 当需要保留的元素数量小于0时抛出
     * @see Stream#skip(long)
     */
    Pipe<E> skip(long size);

    /**
     * 对元素执行切片，即仅保留{@code startInclusive}到{@code endExclusive}之间的元素。
     *
     * @param startInclusive 切片范围起始索引，包含。
     * @param endExclusive 切片范围结束索引，不包含。
     * @return 新的流水线
     */
    Pipe<E> slice(long startInclusive, long endExclusive);

    /**
     * 在流水线头部插入给定的拆分器中的元素。
     *
     * @param spliterator 包含需要插入到头部的元素的拆分器
     * @return 新的流水线
     */
    Pipe<E> prepend(Spliterator<? extends E> spliterator);

    /**
     * 在流水线头部插入给定的迭代器中的元素。
     *
     * @param iterator 包含需要插入到头部的元素的迭代器
     * @return 新的流水线
     */
    default Pipe<E> prepend(Iterator<? extends E> iterator) {
        return prepend(Spliterators.spliteratorUnknownSize(iterator, 0));
    }

    /**
     * 在流水线头部插入给定的流水线中的元素。
     *
     * @param pipe 包含需要插入到头部的元素的流水线
     * @return 新的流水线
     */
    default Pipe<E> prepend(Pipe<? extends E> pipe) {
        return prepend(pipe.toSpliterator());
    }

    /**
     * 在流水线头部插入给定的流中的元素。
     *
     * @param stream 包含需要插入到头部的元素的流
     * @return 新的流水线
     */
    default Pipe<E> prepend(Stream<? extends E> stream) {
        return prepend(stream.spliterator());
    }

    /**
     * 在流水线头部插入给定的元素。
     *
     * @param value 需要插入到头部的元素
     * @return 新的流水线
     */
    Pipe<E> prepend(E value);

    /**
     * 在流水线头部插入给定的数组中的元素。
     *
     * @param values 需要插入到头部的元素
     * @return 新的流水线
     */
    @SuppressWarnings({"unchecked", "varargs"})
    default Pipe<E> prepend(E... values) {
        return prepend(Arrays.spliterator(values));
    }

    /**
     * 在流水线头部插入给定的Map中的键。
     *
     * @param map 包含需要插入到头部的键的Map
     * @return 新的流水线
     */
    default Pipe<E> prependKeys(Map<? extends E, ?> map) {
        return prepend(map.keySet().spliterator());
    }

    /**
     * 在流水线头部插入给定的Map中的值。
     *
     * @param map 包含需要插入到头部的值的Map
     * @return 新的流水线
     */
    default Pipe<E> prependValues(Map<?, ? extends E> map) {
        return prepend(map.values().spliterator());
    }

    /**
     * 在流水线尾部插入给定的拆分器中的元素。
     *
     * @param spliterator 包含需要插入到尾部的元素的拆分器
     * @return 新的流水线
     */
    Pipe<E> append(Spliterator<? extends E> spliterator);

    /**
     * 在流水线尾部插入给定的迭代器中的元素。
     *
     * @param iterator 包含需要插入到尾部的元素的迭代器
     * @return 新的流水线
     */
    default Pipe<E> append(Iterator<? extends E> iterator) {
        return append(Spliterators.spliteratorUnknownSize(iterator, EMPTY));
    }

    /**
     * 在流水线尾部插入给定的流水线中的元素。
     *
     * @param pipe 包含需要插入到尾部的元素的流水线
     * @return 新的流水线
     */
    default Pipe<E> append(Pipe<? extends E> pipe) {
        return append(pipe.toSpliterator());
    }

    /**
     * 在流水线尾部插入给定的流中的元素。
     *
     * @param stream 包含需要插入到尾部的元素的流
     * @return 新的流水线
     */
    default Pipe<E> append(Stream<? extends E> stream) {
        return append(stream.spliterator());
    }

    /**
     * 在流水线尾部插入给定的元素。
     *
     * @param value 需要插入到尾部的元素
     * @return 新的流水线
     */
    Pipe<E> append(E value);

    /**
     * 在流水线尾部插入给定的数组中的元素。
     *
     * @param values 需要插入到尾部的元素
     * @return 新的流水线
     */
    @SuppressWarnings({"unchecked", "varargs"})
    default Pipe<E> append(E... values) {
        return append(Arrays.spliterator(values));
    }

    /**
     * 在流水线尾部插入给定的Map中的键。
     *
     * @param map 包含需要插入到尾部的键的Map
     * @return 新的流水线
     */
    default Pipe<E> appendKeys(Map<? extends E, ?> map) {
        return append(map.keySet().spliterator());
    }

    /**
     * 在流水线尾部插入给定的Map中的值。
     *
     * @param map 包含需要插入到尾部的值的Map
     * @return 新的流水线
     */
    default Pipe<E> appendValues(Map<?, ? extends E> map) {
        return append(map.values().spliterator());
    }

    Pipe<E> disperse(E delimiter); // OPT 2023-05-18 23:15 考虑更多类似的API

    /**
     * 按照给定数量，对元素进行分区，并将分区结果封装为新的流水线。
     * <p/>
     * 根据实际情况，最后一个分区包含的元素数量可能不足给定大小，但不会为空分区。
     *
     * @param size 需要分区的元素数量
     * @return 新的包含已分区元素的流水线
     * @throws IllegalArgumentException 当给定的分区元素数量小于1时抛出
     */
    Pipe<Pipe<E>> partition(int size);

    Pipe<Pipe<E>> partition(Function<? super E, PartitionPolicy> function);

    Pipe<Pipe<E>> partitionOrderly(LongBiFunction<? super E, PartitionPolicy> function);

    /**
     * 按照给定数量，对元素进行分区，并将分区结果封装为列表。
     * <p/>
     * 根据实际情况，最后一个分区包含的元素数量可能不足给定大小，但不会为空分区。
     *
     * @param size 需要分区的元素数量
     * @return 新的包含已分区元素列表的流水线
     * @throws IllegalArgumentException 当给定的分区元素数量小于1时抛出
     * @apiNote 封装的列表不保证可变性，如果明确需要分区列表可修改，请使用{@link #partitionToList(int, Supplier)}。
     */
    default Pipe<List<E>> partitionToList(int size) {
        return partition(size).map(Pipe::toList);
    }

    /**
     * 按照给定数量，对元素进行分区，并将分区结果封装为列表，列表实例由给定的{@link Supplier}提供。
     * <p/>
     * 根据实际情况，最后一个分区包含的元素数量可能不足给定大小，但不会为空分区。
     *
     * @param size 需要分区的元素数量
     * @param listSupplier 用于存储分区元素的列表的构造方法
     * @return 新的包含已分区元素列表的流水线
     * @throws IllegalArgumentException 当给定的分区元素数量小于1时抛出
     */
    default <L extends List<E>> Pipe<List<E>> partitionToList(int size, Supplier<L> listSupplier) {
        requireNonNull(listSupplier);
        return partition(size).map(pipe -> pipe.toList(listSupplier));
    }

    /**
     * 按照给定数量，对元素进行分区，并将分区结果封装为集合。
     * <p/>
     * 根据实际情况，最后一个分区包含的元素数量可能不足给定大小，但不会为空分区。
     *
     * @param size 需要分区的元素数量
     * @return 新的包含已分区元素集合的流水线
     * @throws IllegalArgumentException 当给定的分区元素数量小于1时抛出
     * @apiNote 封装的集合不保证可变性，如果明确需要分区集合可修改，请使用{@link #partitionToSet(int, Supplier)}。
     */
    default Pipe<Set<E>> partitionToSet(int size) {
        return partition(size).map(Pipe::toSet);
    }

    /**
     * 按照给定数量，对元素进行分区，并将分区结果封装为集合，集合实例由给定的{@link Supplier}提供。
     * <p/>
     * 根据实际情况，最后一个分区包含的元素数量可能不足给定大小，但不会为空分区。
     *
     * @param size 需要分区的元素数量
     * @param listSupplier 用于存储分区元素的集合的构造方法
     * @return 新的包含已分区元素集合的流水线
     * @throws IllegalArgumentException 当给定的分区元素数量小于1时抛出
     */
    default <S extends Set<E>> Pipe<Set<E>> partitionToSet(int size, Supplier<S> listSupplier) {
        requireNonNull(listSupplier);
        return partition(size).map(pipe -> pipe.toSet(listSupplier));
    }

    /**
     * 按照给定数量，对元素进行分区，并将分区结果封装为容器，容器实例由给定的{@link Supplier}提供。
     * <p/>
     * 根据实际情况，最后一个分区包含的元素数量可能不足给定大小，但不会为空分区。
     *
     * @param size 需要分区的元素数量
     * @param listSupplier 用于存储分区元素的容器的构造方法
     * @return 新的包含已分区元素容器的流水线
     * @throws IllegalArgumentException 当给定的分区元素数量小于1时抛出
     */
    default <S extends Collection<E>> Pipe<Collection<E>> partitionToCollection(int size, Supplier<S> listSupplier) {
        requireNonNull(listSupplier);
        return partition(size).map(pipe -> pipe.toCollection(listSupplier));
    }

    /**
     * 结合第二条流水线，组合成两元组流水线。
     *
     * @param secondPipe 第二条流水线
     * @param <S> 第二条流水线中的元素类型
     * @return 新的两元组流水线
     * @apiNote 当任意流水线耗尽时，新的双元组流水线即耗尽，哪怕还有剩余元素。
     */
    <S> BiPipe<E, S> combine(Pipe<S> secondPipe);

    default Pipe<E> merge(Pipe<? extends E> pipe, BiFunction<? super E, ? super E, MergePolicy> mergeHandle,
        MergeRemainingPolicy remainingPolicy) {
        return merge(pipe, mergeHandle, (value, policy) -> value, (value, policy) -> value, remainingPolicy);
    }

    /**
     * 将此流水线和另外一个流水线合并为一个新的流水线。
     * <p/>
     * 合并时会将两个流水线中的数据按次序进行比较，由{@code mergeHandle}返回这两个数据的合并策略，
     * 此策略将指导如何从两个流水线中选择元素加入到新的流水线，各种策略的合并详情见{@link MergePolicy}。
     * <p/>
     * 合并时并不要求两条流水线的数据类型相同，但是被选中的数据根据来源不同，会分别经过{@code oursMapper}
     * 和{@code theirsMapper}统一映射为最终的数据类型{@code R}，映射后的数据组成新的流水线。
     * <p/>
     * 由于两条流水线的元素数量可能不一致，或者受合并策略的影响，可能存在一条流水线还有数据待合并而另一条已经耗尽的情况，
     * 此时需要根据{@code remainingPolicy}来决定剩余数据的处理策略，详见{@link MergeRemainingPolicy}。
     *
     * @param pipe 另一条需要合并的流水线。
     * @param mergeHandle 流水线的合并策略。第一个参数为此流水线的数据，第二个参数为另一条流水线的数据，
     * 受{@link MergeRemainingPolicy#MERGE_AS_NULL}和流水线自身数据的影响，这两个参数都可能为{@code null}。
     * 返回值为这两个数据的合并策略，此合并策略<b>必须不能为</b>{@code null}，否则会导致{@link NullPointerException}。
     * @param oursMapper 来自此流水线的数据被选中进入新流水线时的映射方法，第一个参数为被选中的数据，
     * 第二个参数为此数据的合并策略。
     * @param theirsMapper 来自另一条流水线的数据被选中进入新流水线时的映射方法，第一个参数为被选中的数据，
     * 第二个参数为此数据的合并策略。
     * @param remainingPolicy 当一条流水线耗尽时另一条流水线数据的处理策略，
     * 传入{@code null}时等同于{@link MergeRemainingPolicy#TAKE_REMAINING}。
     * @param <T> 另一条流水线的数据类型
     * @param <R> 新流水线的数据类型
     * @return 合并后的新流水线
     * @throws NullPointerException 当{@code mergeHandle}返回{@code null}时抛出。
     * @apiNote 对于 {@code oursMapper}和{@code theirsMapper}而言，如果数据因为
     * {@link MergeRemainingPolicy#TAKE_REMAINING}或{@link MergeRemainingPolicy#TAKE_OURS}
     * 或{@link MergeRemainingPolicy#TAKE_THEIRS}而保留，则对应的第二个参数合并策略为{@link MergePolicy#TAKE_OURS}
     * 或{@link MergePolicy#TAKE_THEIRS}。
     * @see MergePolicy
     * @see MergeRemainingPolicy
     */
    <T, R> Pipe<R> merge(Pipe<? extends T> pipe, BiFunction<? super E, ? super T, MergePolicy> mergeHandle,
        BiFunction<? super E, MergePolicy, ? extends R> oursMapper,
        BiFunction<? super T, MergePolicy, ? extends R> theirsMapper, MergeRemainingPolicy remainingPolicy);

    default Pipe<E> mergeAlternately(Pipe<? extends E> pipe) {
        return mergeAlternately(pipe, TAKE_REMAINING);
    }

    default Pipe<E> mergeAlternately(Pipe<? extends E> pipe, MergeRemainingPolicy remainingPolicy) {
        return merge(pipe, (ignoredOurs, ignoredTheirs) -> OURS_FIRST, remainingPolicy);
    }

    default Pipe<E> mergeAlternatelyTheirsFirst(Pipe<? extends E> pipe) {
        return mergeAlternatelyTheirsFirst(pipe, TAKE_REMAINING);
    }

    default Pipe<E> mergeAlternatelyTheirsFirst(Pipe<? extends E> pipe, MergeRemainingPolicy remainingPolicy) {
        return merge(pipe, (ignoredOurs, ignoredTheirs) -> THEIRS_FIRST, remainingPolicy);
    }

    /**
     * 访问流水线中的每个元素。
     *
     * @param action 访问元素的方法
     * @see Stream#forEach(Consumer)
     */
    void forEach(Consumer<? super E> action);

    default void run() {
        forEach(ignored -> {});
    }

    /**
     * 访问流水线中的每个元素，支持访问元素的次序。
     *
     * @param action 访问元素的方法：第一个参数为访问的元素在流水线中的次序，从0开始计算；第二个参数为访问的元素。
     */
    void forEachOrderly(LongBiConsumer<? super E> action);

    Optional<E> reduce(BinaryOperator<E> operator);

    default E reduce(E initVar, BinaryOperator<E> reducer) {
        return reduce(initVar, (BiFunction<? super E, ? super E, ? extends E>) reducer);
    }

    <R> R reduce(R initVar, BiFunction<? super R, ? super E, ? extends R> reducer);

    default <R> R reduceIdentity(R initVar, BiConsumer<? super R, ? super E> reducer) {
        return reduce(initVar, (result, value) -> {
            reducer.accept(result, value);
            return result;
        });
    }

    default <R> R reduce(R initVar, Function<? super E, ? extends R> mapper, BinaryOperator<R> operator) {
        return reduce(initVar, (result, value) -> operator.apply(result, mapper.apply(value)));
    }

    /**
     * 获取流水线中最小的元素，以自然顺序为比较依据。
     * <p/>
     * 尝试将元素转为{@link Comparable}来进行比较。
     *
     * @return 最小的元素，如果流水线为空则返回空的{@link Optional}
     * @throws ClassCastException 当流水线元素无法转换为{@link Comparable}类型时抛出
     */
    default Optional<E> min() {
        return min(null);
    }

    /**
     * 获取流水线中最小的元素，以给定的比较器为比较依据。
     *
     * @param comparator 比较器
     * @return 最小的元素，如果流水线为空则返回空的{@link Optional}
     * @see Stream#min(Comparator)
     */
    Optional<E> min(Comparator<? super E> comparator);

    default <K extends Comparable<? super K>> Optional<E> minBy(Function<? super E, ? extends K> mapper) {
        return isStdIdentify(mapper) ? min() : min(comparing(mapper));
    }

    @SuppressWarnings("unchecked")
    default <K> Optional<E> minBy(Function<? super E, ? extends K> mapper, Comparator<? super K> comparator) {
        return isStdIdentify(mapper) ? min((Comparator<? super E>) comparator) : min(comparing(mapper, comparator));
    }

    default <K extends Comparable<? super K>> Optional<E> minByOrderly(LongBiFunction<? super E, ? extends K> mapper) {
        return minByOrderly(mapper, naturalOrder());
    }

    <K> Optional<E> minByOrderly(LongBiFunction<? super E, ? extends K> mapper, Comparator<? super K> comparator);

    /**
     * 获取流水线中最大的元素，以自然顺序为比较依据。
     * <p/>
     * 尝试将元素转为{@link Comparable}来进行比较。
     *
     * @return 最大的元素，如果流水线为空则返回空的{@link Optional}
     * @throws ClassCastException 当流水线元素无法转换为{@link Comparable}类型时抛出
     */
    default Optional<E> max() {
        return max(null);
    }

    /**
     * 获取流水线中最大的元素，以给定的比较器为比较依据。
     *
     * @param comparator 比较器
     * @return 最大的元素，如果流水线为空则返回空的{@link Optional}
     * @see Stream#max(Comparator)
     */
    default Optional<E> max(Comparator<? super E> comparator) {
        return min(optimizedReverseOrder(comparator));
    }

    default <K extends Comparable<? super K>> Optional<E> maxBy(Function<? super E, ? extends K> mapper) {
        return isStdIdentify(mapper) ? max() : max(comparing(mapper));
    }

    @SuppressWarnings("unchecked")
    default <K> Optional<E> maxBy(Function<? super E, ? extends K> mapper, Comparator<? super K> comparator) {
        return isStdIdentify(mapper) ? max((Comparator<? super E>) comparator) : max(comparing(mapper, comparator));
    }

    default <K extends Comparable<? super K>> Optional<E> maxByOrderly(LongBiFunction<? super E, ? extends K> mapper) {
        return maxByOrderly(mapper, naturalOrder());
    }

    default <K> Optional<E> maxByOrderly(LongBiFunction<? super E, ? extends K> mapper,
        Comparator<? super K> comparator) {
        return minByOrderly(mapper, optimizedReverseOrder(comparator));
    }

    default Tuple<Optional<E>, Optional<E>> minMax() {
        return minMax(null);
    }

    Tuple<Optional<E>, Optional<E>> minMax(Comparator<? super E> comparator);

    default <K extends Comparable<? super K>> Tuple<Optional<E>, Optional<E>> minMaxBy(
        Function<? super E, ? extends K> mapper) {
        return isStdIdentify(mapper) ? minMax() : minMax(comparing(mapper));
    }

    @SuppressWarnings("unchecked")
    default <K> Tuple<Optional<E>, Optional<E>> minMaxBy(Function<? super E, ? extends K> mapper,
        Comparator<? super K> comparator) {
        return isStdIdentify(mapper) ? minMax((Comparator<? super E>) comparator) :
            minMax(comparing(mapper, comparator));
    }

    default <K extends Comparable<? super K>> Tuple<Optional<E>, Optional<E>> minMaxByOrderly(
        LongBiFunction<? super E, ? extends K> mapper) {
        return minMaxByOrderly(mapper, naturalOrder());
    }

    <K> Tuple<Optional<E>, Optional<E>> minMaxByOrderly(LongBiFunction<? super E, ? extends K> mapper,
        Comparator<? super K> comparator);

    /**
     * 计算当前流水线中元素的数量。
     *
     * @return 当前流水线中元素的数量
     * @see Stream#count()
     */
    long count();

    default boolean isEmpty() {
        return count() == 0L;
    }

    boolean anyMatch(Predicate<? super E> predicate);

    boolean allMatch(Predicate<? super E> predicate);

    default boolean noneMatch(Predicate<? super E> predicate) {
        return !anyMatch(predicate);
    }

    boolean anyNull();

    boolean allNull();

    default boolean noneNull() {
        return !anyNull();
    }

    default <K> boolean anyNullBy(Function<? super E, ? extends K> mapper) {
        requireNonNull(mapper);
        return anyMatch(value -> mapper.apply(value) == null);
    }

    default <K> boolean allNullBy(Function<? super E, ? extends K> mapper) {
        requireNonNull(mapper);
        return allMatch(value -> mapper.apply(value) == null);
    }

    default <K> boolean noneNullBy(Function<? super E, ? extends K> mapper) {
        requireNonNull(mapper);
        return !anyNullBy(mapper);
    }

    /**
     * 获取流水线中的第一个元素。
     *
     * @return 流水线中的第一个元素，如果流水线为空则返回空的{@link Optional}
     * @see Stream#findFirst()
     */
    Optional<E> findFirst();

    default Optional<E> findFirstNonnull() {
        return dropNull().findFirst();
    }

    /**
     * 获取流水线中的最后一个元素。
     *
     * @return 流水线中的最后一个元素，如果流水线为空则返回空的{@link Optional}
     */
    Optional<E> findLast();

    default Optional<E> findLastNonnull() {
        return dropNull().findLast();
    }

    Tuple<Optional<E>, Optional<E>> findFirstLast();

    /**
     * 同{@link #findFirst()}。
     *
     * @return 流水线中的任一元素，如果流水线为空则返回空的{@link Optional}
     * @see Stream#findAny()
     */
    default Optional<E> findAny() {
        return findFirst();
    }

    E[] toArray(IntFunction<E[]> generator);

    default List<E> toList() { // OPT 2023-05-14 00:02 默认可变 or 默认不可变，通过全局配置控制
        return toList(ArrayList::new);
    }

    default <L extends List<E>> List<E> toList(Supplier<L> supplier) {
        return toCollection(supplier);
    }

    default List<E> toUnmodifiableList() {
        return Collections.unmodifiableList(toList());
    }

    default Set<E> toSet() {
        return toSet(HashSet::new);
    }

    default <S extends Set<E>> Set<E> toSet(Supplier<S> supplier) {
        return toCollection(supplier);
    }

    default Set<E> toUnmodifiableSet() {
        return Collections.unmodifiableSet(toSet());
    }

    default <C extends Collection<E>> C toCollection(Supplier<C> supplier) {
        requireNonNull(supplier);
        return reduceIdentity(supplier.get(), Collection::add);
    }

    default <K> Map<K, E> toMap(Function<? super E, ? extends K> keyMapper) {
        return toMap(HashMap::new, keyMapper);
    }

    default <K, M extends Map<K, E>> M toMap(Supplier<M> supplier, Function<? super E, ? extends K> keyMapper) {
        requireNonNull(supplier);
        requireNonNull(keyMapper);
        return reduceIdentity(supplier.get(), (map, value) -> map.put(keyMapper.apply(value), value));
    }

    default <K> Map<K, E> toUnmodifiableMap(Function<? super E, ? extends K> keyMapper) {
        return Collections.unmodifiableMap(toMap(keyMapper));
    }

    <K> BiPipe<K, Pipe<E>> groupAndExtend(Function<? super E, ? extends K> classifier);

    default <K> Pipe<List<E>> groupValues(Function<? super E, ? extends K> classifier) {
        return Pipes.iterable(group(classifier).values());
    }

    default <K> Pipe<Pipe<E>> groupFlatValues(Function<? super E, ? extends K> classifier) {
        return groupValues(classifier).map(Pipes::iterable);
    }

    default <K> Map<K, List<E>> group(Function<? super E, ? extends K> classifier) {
        return group(classifier, HashMap::new);
    }

    default <K, M extends Map<K, List<E>>> M group(Function<? super E, ? extends K> classifier,
        Supplier<? extends M> mapSupplier) {
        return reduceIdentity(mapSupplier.get(),
            (map, value) -> map.computeIfAbsent(classifier.apply(value), key -> new ArrayList<>()).add(value));
    }

    @SuppressWarnings("unchecked")
    default <K, V> Map<K, V> groupAndThen(Function<? super E, ? extends K> classifier,
        BiFunction<K, List<E>, V> finisher) {
        HashMap<K, Object> result = reduceIdentity(new HashMap<>(),
            (map, value) -> ((ArrayList<E>) map.computeIfAbsent(classifier.apply(value),
                key -> new ArrayList<E>())).add(value));
        result.replaceAll((key, list) -> finisher.apply(key, (List<E>) list));
        return (Map<K, V>) result;
    }

    default <K> Map<K, List<E>> groupAndExecute(Function<? super E, ? extends K> classifier,
        BiConsumer<K, List<E>> action) {
        HashMap<K, List<E>> map = group(classifier, HashMap::new);
        map.forEach(action);
        return map;
    }

    default String join(CharSequence delimiter, CharSequence prefix, CharSequence suffix) {
        return reduce(new StringJoiner(delimiter, prefix, suffix),
            (joiner, value) -> joiner.add(Objects.toString(value))).toString();
    }

    default String join(CharSequence delimiter) {
        return join(delimiter, "", "");
    }

    default String join() {
        return join(",", "", "");
    }

    default <U> U chain(Function<? super Pipe<E>, U> function) {
        return function.apply(this);
    }

    default Pipe<E> println() { // DBG 2023-05-20 01:07 调试接口
        return peek(System.out::println);
    }

    default Pipe<E> print() { // DBG 2023-05-20 01:08 调试接口
        return peek(System.out::print);
    }
}
