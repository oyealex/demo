package com.oyealex.pipe.basis;

import com.oyealex.pipe.BasePipe;
import com.oyealex.pipe.annotations.Todo;
import com.oyealex.pipe.assist.Tuple;
import com.oyealex.pipe.bi.BiPipe;
import com.oyealex.pipe.flag.PipeFlag;
import com.oyealex.pipe.functional.LongBiConsumer;
import com.oyealex.pipe.functional.LongBiFunction;
import com.oyealex.pipe.functional.LongBiPredicate;
import com.oyealex.pipe.policy.MergePolicy;
import com.oyealex.pipe.policy.MergeRemainingPolicy;
import com.oyealex.pipe.policy.PartitionPolicy;
import com.oyealex.pipe.spliterator.MoreSpliterators;
import com.oyealex.pipe.utils.MiscUtil;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
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
import java.util.function.UnaryOperator;
import java.util.stream.Stream;

import static com.oyealex.pipe.policy.MergePolicy.OURS_FIRST;
import static com.oyealex.pipe.policy.MergePolicy.THEIRS_FIRST;
import static com.oyealex.pipe.policy.MergeRemainingPolicy.TAKE_REMAINING;
import static com.oyealex.pipe.utils.MiscUtil.isStdIdentify;
import static com.oyealex.pipe.utils.MiscUtil.naturalOrderIfNull;
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
     * @throws NullPointerException 当{@code predicate}为{@code null}时抛出。
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
     * @throws NullPointerException 当{@code predicate}为{@code null}时抛出。
     * @apiNote 此方法等价于 {@link #takeIf(Predicate)}，为了方便从{@link Stream#filter(Predicate)}迁移而存在。
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
     * for (E element : getPipeElements()) {
     *     if (predicate.test(index++, element)) {
     *         doSomething(element);
     *     }
     * }
     * }</pre>
     *
     * @param predicate 断言方法：第一个参数为访问的元素在流水线中的次序，从0开始计算；第二个参数为需要判断是否保留的元素。
     * @return 新的流水线。
     * @throws NullPointerException 当{@code predicate}为{@code null}时抛出。
     * @see #takeIf(Predicate)
     * @see #dropIfOrderly(LongBiPredicate)
     */
    Pipe<E> takeIfOrderly(LongBiPredicate<? super E> predicate);

    /**
     * 保留第一个元素，丢弃其他所有元素。
     *
     * @return 最多只含有第一个元素的新的流水线。
     * @apiNote 此方法等价于 {@code limit(1)}，作为更接近自然语言表达方式的版本而存在。
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
     * @return 最多包含前 {@code count}个元素的新的流水线。
     * @throws IllegalArgumentException 当需要保留的元素数量小于0时抛出。
     * @apiNote 此方法等价于 {@code limit(count)}，作为更接近自然语言表达方式的版本而存在。
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
     * @return 最多只含有最后一个元素的新的流水线。
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
     * @return 最多包含后 {@code count}个元素的新的流水线。
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
     * for (E element : getPipeElements()) {
     *     if (predicate.test(element)) {
     *         doSomething(element);
     *     } else {
     *         break;
     *     }
     * }
     * }</pre>
     *
     * @param predicate 断言方法。
     * @return 新的流水线。
     * @throws NullPointerException 当{@code predicate}为{@code null}时抛出。
     * @apiNote 此方法为了方便从 {@code Stream.takeWhile(predicate)}（JDK8更高版本）迁移而存在。
     * @implNote 此方法支持对流水线执行短路优化，当断言首次返回{@code false}后，流水线剩余的元素允许被短路，
     * 因此{@code predicate}可能无法对每个元素调用。
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
     * for (E element : getPipeElements()) {
     *     if (predicate.test(index++, element)) {
     *         doSomething(element);
     *     } else {
     *         break;
     *     }
     * }
     * }</pre>
     *
     * @param predicate 断言方法：第一个参数为访问的元素在流水线中的次序，从0开始计算；第二个参数为判断是否需要保留的元素。
     * @return 新的流水线。
     * @throws NullPointerException 当{@code predicate}为{@code null}时抛出。
     * @implNote 此方法支持对流水线执行短路优化，当断言首次返回{@code false}后，流水线剩余的元素允许被短路，
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
     * @throws NullPointerException 当{@code predicate}为{@code null}时抛出。
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
     * for (E element : getPipeElements()) {
     *     if (!predicate.test(index++, element)) {
     *         doSomething(element);
     *     }
     * }
     * }</pre>
     *
     * @param predicate 断言方法：第一个参数为访问的元素在流水线中的次序，从0开始计算；第二个参数为需要判断是否丢弃的元素。
     * @return 新的流水线。
     * @throws NullPointerException 当{@code predicate}为{@code null}时抛出。
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
     * @apiNote 此方法等价于 {@code skip(1)}，作为更接近自然语言表达方式的版本而存在。
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
     * @apiNote 此方法等价于 {@code skip(count)}，作为更接近自然语言表达方式的版本而存在。
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
     * 丢弃最后给定数量的元素。
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
     * for (E element : getPipeElements()) {
     *     if (shouldTake || (shouldTake = !predicate.test(element))) {
     *         doSomething(element);
     *     }
     * }
     * }</pre>
     *
     * @param predicate 断言方法。
     * @return 新的流水线。
     * @throws NullPointerException 当{@code predicate}为{@code null}时抛出。
     * @implNote 当断言首次返回 {@code false}后，流水线剩余的元素直接流向下游，无需再使用{@code predicate}测试元素，
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
     * for (E element : getPipeElements()) {
     *     if (shouldTake || (shouldTake = !predicate.test(index++, element))) {
     *         doSomething(element);
     *     }
     * }
     * }</pre>
     *
     * @param predicate 断言方法：第一个参数为访问的元素在流水线中的次序，从0开始计算；第二个参数为判断是否需要保留的元素。
     * @return 新的流水线。
     * @throws NullPointerException 当{@code predicate}为{@code null}时抛出。
     * @implNote 当断言首次返回 {@code false}后，流水线剩余的元素直接流向下游，无需再使用{@code predicate}测试元素，
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
     * for (E element : getPipeElements()) {
     *     if (mapper.apply(element) != null) {
     *         doSomething(element);
     *     }
     * }
     * }</pre>
     *
     * @param mapper 映射方法
     * @return 新的流水线
     * @throws NullPointerException 当{@code mapper}为{@code null}时抛出。
     * @see #dropNull()
     */
    default Pipe<E> dropNullBy(Function<? super E, ?> mapper) {
        requireNonNull(mapper);
        return takeIf(value -> mapper.apply(value) != null);
    }

    /**
     * 将此流水线中的元素映射为新的值。
     *
     * @param mapper 映射方法。
     * @param <R> 新的类型。
     * @return 包含新类型元素的流水线。
     * @throws NullPointerException 当{@code mapper}为{@code null}时抛出。
     * @see Stream#map(Function)
     */
    <R> Pipe<R> map(Function<? super E, ? extends R> mapper);

    /**
     * 将此流水线中的元素映射为新的值，映射方法支持访问元素在流水线中的次序，从0开始计算，使用{@code long}类型的数据表示次序。
     * <p/>
     * 大致等同于：
     * <pre>{@code
     * long index = 0L;
     * for (E element : getPipeElements()) {
     *     doSomething(mapper.apply(index++, element));
     * }
     * }</pre>
     *
     * @param mapper 映射方法：第一个参数为访问的元素在流水线中的次序，从0开始计算；第二个参数为需要映射的元素。
     * @param <R> 新的类型。
     * @return 包含新类型元素的流水线
     * @throws NullPointerException 当{@code mapper}为{@code null}时抛出
     */
    <R> Pipe<R> mapOrderly(LongBiFunction<? super E, ? extends R> mapper);

    /**
     * 将满足{@code condition}条件的元素映射为{@code replacement}。
     * <p/>
     * 大致等同于：
     * <pre>{@code
     * for (E element : getPipeElements()) {
     *     doSomething(condition.test(element) ? replacement : element);
     * }
     * }</pre>
     *
     * @param condition 对元素执行映射的条件。
     * @param replacement 满足条件的元素需要映射为的对象。
     * @return 新的流水线。
     * @throws NullPointerException 当{@code condition}为{@code null}时抛出。
     * @see #mapIf(Predicate, Function)
     * @see #mapIf(Predicate, Supplier)
     * @see #mapIf(Function)
     */
    default Pipe<E> mapIf(Predicate<? super E> condition, E replacement) {
        return mapIf(condition, () -> replacement);
    }

    /**
     * 将满足{@code condition}条件的元素映射为{@code replacementSupplier}提供的值。
     * <p/>
     * 大致等同于：
     * <pre>{@code
     * for (E element : getPipeElements()) {
     *     doSomething(condition.test(element) ? replacementSupplier.get() : element);
     * }
     * }</pre>
     *
     * @param condition 对元素执行映射的条件。
     * @param replacementSupplier 满足条件的元素需要映射为的对象的获取方法。
     * @return 新的流水线。
     * @throws NullPointerException 当{@code condition}或{@code replacementSupplier}为{@code null}时抛出。
     * @see #mapIf(Predicate, Object)
     * @see #mapIf(Predicate, Function)
     * @see #mapIf(Function)
     */
    default Pipe<E> mapIf(Predicate<? super E> condition, Supplier<? extends E> replacementSupplier) {
        requireNonNull(replacementSupplier);
        return mapIf(condition, ignoredValue -> replacementSupplier.get());
    }

    /**
     * 将满足{@code condition}条件的元素使用{@code mapper}映射。
     * <p/>
     * 大致等同于：
     * <pre>{@code
     * for (E element : getPipeElements()) {
     *     doSomething(condition.test(element) ? mapper.apply(element) : element);
     * }
     * }</pre>
     *
     * @param condition 对元素执行映射的条件。
     * @param mapper 满足条件的元素的映射方法。
     * @return 新的流水线。
     * @throws NullPointerException 当{@code condition}或{@code mapper}为{@code null}时抛出。
     * @see #mapIf(Predicate, Object)
     * @see #mapIf(Predicate, Supplier)
     * @see #mapIf(Function)
     */
    Pipe<E> mapIf(Predicate<? super E> condition, Function<? super E, ? extends E> mapper);

    /**
     * 将元素通过{@code mapper}映射，如果结果值{@link Optional}不为空，则将对应元素映射为其值，否则维持原始值。
     * <p/>
     * 大致等同于：
     * <pre>{@code
     * for (E element : getPipeElements()) {
     *     doSomething(mapper.apply(element).orElse(element));
     * }
     * }</pre>
     *
     * @param mapper 满足条件的元素需要映射为的对象。
     * @param <R> 一个{@link Optional}类型，值类型为{@code E}或其子类
     * @return 新的流水线。
     * @throws NullPointerException 当{@code mapper}为{@code null}时抛出。
     * @see #mapIf(Predicate, Object)
     * @see #mapIf(Predicate, Function)
     * @see #mapIf(Predicate, Supplier)
     */
    <R extends Optional<? extends E>> Pipe<E> mapIf(Function<? super E, R> mapper);

    /**
     * 使用{@link Objects#toString(Object)}将流水线中的数据映射为字符串。
     *
     * @return 包含数据字符串的流水线。
     * @see #mapToString(String)
     */
    default Pipe<String> mapToString() {
        return map(Objects::toString);
    }

    /**
     * 将流水线中的数据映射为字符串。
     * <p/>
     * 大致等同于：
     * <pre>{@code
     * for (E element : getPipeElements()) {
     *     doSomething(element == null ? nullDefault : element.toString());
     * }
     * }</pre>
     *
     * @param nullDefault 当数据为{@code null}时的目标字符串。
     * @return 包含数据字符串的流水线。
     * @see #mapToString()
     */
    default Pipe<String> mapToString(String nullDefault) {
        return map(value -> Objects.toString(value, nullDefault));
    }

    /**
     * 将空值{@code null}映射为给定的值。
     * <p/>
     * 大致等同于：
     * <pre>{@code
     * for (E element : getPipeElements()) {
     *     doSomething(element == null ? value : element);
     * }
     * }</pre>
     *
     * @param value {@code null}需要映射为的对象，不可为{@code null}。
     * @return 不包含任何 {@code null}的新的流水线。
     * @throws NullPointerException 当{@code value}为{@code null}时抛出。
     * @see #mapNull(Supplier)
     */
    default Pipe<E> mapNull(E value) {
        requireNonNull(value);
        return mapNull(() -> value);
    }

    /**
     * 将空值{@code null}映射为给定{@code supplier}提供的值。
     * <p/>
     * 大致等同于：
     * <pre>{@code
     * for (E element : getPipeElements()) {
     *     if (element == null) {
     *         E newValue = supplier.get();
     *         if (newValue == null) {
     *             throw new NullPointerException();
     *         } else {
     *             doSomething(newValue);
     *         }
     *     } else {
     *         doSomething(element);
     *     }
     * }
     * }</pre>
     *
     * @param supplier 映射对象的获取方法，获取的对象不可为{@code null}。
     * @return 不包含任何 {@code null}的新的流水线。
     * @throws NullPointerException 当{@code supplier}为{@code null}时抛出。
     * @implNote 在流水线运行期间 {@code supplier}产生的任何空值，均会导致抛出{@link NullPointerException}。
     * @see #mapNull(Object)
     */
    Pipe<E> mapNull(Supplier<? extends E> supplier);

    /**
     * 将流水线中的元素映射为int类型。
     *
     * @param mapper 映射方法
     * @return int流水线
     * @throws NullPointerException 当{@code mapper}为{@code null}时抛出
     * @see Stream#mapToInt(ToIntFunction)
     */
    @Todo
    IntPipe mapToInt(ToIntFunction<? super E> mapper);

    /**
     * 将流水线中的元素映射为int类型，支持访问元素的次序。
     *
     * @param mapper 映射方法：第一个参数为访问的元素在流水线中的次序，从0开始计算；第二个参数为需要映射的元素。
     * @return int流水线
     * @throws NullPointerException 当{@code mapper}为{@code null}时抛出
     * @see Stream#mapToInt(ToIntFunction)
     */
    // OPT 2023-05-10 01:39 添加对应的基于次序的函数式接口
    @Todo
    IntPipe mapToIntOrderly(ToIntFunction<? super E> mapper);

    /**
     * 将流水线中的元素映射为long类型。
     *
     * @param mapper 映射方法
     * @return long流水线
     * @throws NullPointerException 当{@code mapper}为{@code null}时抛出
     * @see Stream#mapToLong(ToLongFunction)
     */
    @Todo
    LongPipe mapToLong(ToLongFunction<? super E> mapper);

    /**
     * 将流水线中的元素映射为long类型，支持访问元素的次序。
     *
     * @param mapper 映射方法：第一个参数为访问的元素在流水线中的次序，从0开始计算；第二个参数为需要映射的元素。
     * @return long流水线
     * @throws NullPointerException 当{@code mapper}为{@code null}时抛出
     * @see Stream#mapToLong(ToLongFunction)
     */
    // OPT 2023-05-10 01:39 添加对应的基于次序的函数式接口
    @Todo
    LongPipe mapToLongOrderly(ToLongFunction<? super E> mapper);

    /**
     * 将流水线中的元素映射为double类型。
     *
     * @param mapper 映射方法
     * @return double流水线
     * @throws NullPointerException 当{@code mapper}为{@code null}时抛出
     * @see Stream#mapToDouble(ToDoubleFunction)
     */
    @Todo
    DoublePipe mapToDouble(ToDoubleFunction<? super E> mapper);

    /**
     * 将流水线中的元素映射为double类型，支持访问元素的次序。
     *
     * @param mapper 映射方法：第一个参数为访问的元素在流水线中的次序，从0开始计算；第二个参数为需要映射的元素。
     * @return double流水线
     * @throws NullPointerException 当{@code mapper}为{@code null}时抛出
     * @see Stream#mapToDouble(ToDoubleFunction)
     */
    // OPT 2023-05-10 01:39 添加对应的基于次序的函数式接口
    @Todo
    DoublePipe mapToDoubleOrderly(ToDoubleFunction<? super E> mapper);

    /**
     * 将流水线中的元素映射为新的流水线，并按照次序拼接为一条流水线。
     * <p/>
     * 大致等同于：
     * <pre>{@code
     * for (E element : getPipeElements()) {
     *     for (R newElement : getPipeElements(mapper.apply(element))) {
     *         doSomething(newElement);
     *     }
     * }
     * }</pre>
     *
     * @param mapper 映射方法。
     * @param <R> 新流水线中的元素类型。
     * @return 映射并拼接后的流水线。
     * @throws NullPointerException 当{@code mapper}为{@code null}时抛出。
     * @see Stream#flatMap(Function)
     * @see #flatMapOrderly(LongBiFunction)
     * @see #flatMapCollection(Function)
     */
    <R> Pipe<R> flatMap(Function<? super E, ? extends Pipe<? extends R>> mapper);

    /**
     * 将流水线中的元素映射为新的流水线，并按照次序拼接为一条流水线，支持访问元素的次序。
     * <p/>
     * 大致等同于：
     * <pre>{@code
     * long index = 0L;
     * for (E element : getPipeElements()) {
     *     for (R newElement : getPipeElements(mapper.apply(index++, element))) {
     *         doSomething(newElement);
     *     }
     * }
     * }</pre>
     *
     * @param mapper 映射方法：第一个参数为访问的元素在流水线中的次序，从0开始计算；第二个参数为需要映射的元素。
     * @param <R> 新流水线中的元素类型。
     * @return 映射并拼接后的流水线。
     * @throws NullPointerException 当{@code mapper}为{@code null}时抛出。
     * @see #flatMap(Function)
     * @see #flatMapCollection(Function)
     */
    <R> Pipe<R> flatMapOrderly(LongBiFunction<? super E, ? extends Pipe<? extends R>> mapper);

    /**
     * 将流水线中的元素映射为容器，并将容器中的元素按照次序拼接为一条流水线。
     * <p/>
     * 大致等同于：
     * <pre>{@code
     * for (E element : getPipeElements()) {
     *     for (R newElement : mapper.apply(element)) {
     *         doSomething(newElement);
     *     }
     * }
     * }</pre>
     *
     * @param mapper 映射方法。
     * @param <R> 新流水线中的元素类型。
     * @return 映射并拼接后的流水线。
     * @throws NullPointerException 当{@code mapper}为{@code null}时抛出。
     * @implNote 由于无法确定具体的 {@link Collection}类型，流水线无法进行更进一步的优化，如果有必要请使用其他显式构造流水线的
     * 方法映射并拼接流水线。
     * @see #flatMap(Function)
     * @see #flatMapOrderly(LongBiFunction)
     */
    default <R> Pipe<R> flatMapCollection(Function<? super E, ? extends Collection<? extends R>> mapper) {
        return map(mapper).flatMap(Pipe::collection);
    }

    /**
     * 将流水线中的每个元素包装为一个个流水线，这些流水线仅包含一个元素。
     *
     * @return 新的流水线。
     * @apiNote 此方法通常在需要对单个元素使用流水线的方法时使用。
     */
    Pipe<Pipe<E>> flatMapSingleton(); // OPT 2023-05-27 00:50 完善注释中的使用示例

    /**
     * 将流水线中的元素映射为新的int流水线，并按照次序拼接为一条int流水线。
     *
     * @param mapper 映射方法
     * @return 映射并拼接后的int流水线
     * @throws NullPointerException 当{@code mapper}为{@code null}时抛出
     * @see Stream#flatMapToInt(Function)
     */
    @Todo
    IntPipe flatMapToInt(Function<? super E, ? extends IntPipe> mapper);

    /**
     * 将流水线中的元素映射为新的int流水线，并按照次序拼接为一条int流水线，支持访问元素的次序。
     *
     * @param mapper 映射方法：第一个参数为访问的元素在流水线中的次序，从0开始计算；第二个参数为需要映射的元素。
     * @return 映射并拼接后的int流水线
     * @throws NullPointerException 当{@code mapper}为{@code null}时抛出
     * @see Stream#flatMapToInt(Function)
     */
    @Todo
    IntPipe flatMapToIntOrderly(LongBiFunction<? super E, ? extends IntPipe> mapper);

    /**
     * 将流水线中的元素映射为新的long流水线，并按照次序拼接为一条long流水线。
     *
     * @param mapper 映射方法
     * @return 映射并拼接后的long流水线
     * @throws NullPointerException 当{@code mapper}为{@code null}时抛出
     * @see Stream#flatMapToLong(Function)
     */
    @Todo
    LongPipe flatMapToLong(Function<? super E, ? extends LongPipe> mapper);

    /**
     * 将流水线中的元素映射为新的long流水线，并按照次序拼接为一条long流水线，支持访问元素的次序。
     *
     * @param mapper 映射方法：第一个参数为访问的元素在流水线中的次序，从0开始计算；第二个参数为需要映射的元素。
     * @return 映射并拼接后的long流水线
     * @throws NullPointerException 当{@code mapper}为{@code null}时抛出
     * @see Stream#flatMapToLong(Function)
     */
    @Todo
    LongPipe flatMapToLongOrderly(LongBiFunction<? super E, ? extends LongPipe> mapper);

    /**
     * 将流水线中的元素映射为新的double流水线，并按照次序拼接为一条double流水线。
     *
     * @param mapper 映射方法
     * @return 映射并拼接后的double流水线
     * @throws NullPointerException 当{@code mapper}为{@code null}时抛出
     * @see Stream#flatMapToDouble(Function)
     */
    @Todo
    DoublePipe flatMapToDouble(Function<? super E, ? extends DoublePipe> mapper);

    /**
     * 将流水线中的元素映射为新的double流水线，并按照次序拼接为一条double流水线，支持访问元素的次序。
     *
     * @param mapper 映射方法：第一个参数为访问的元素在流水线中的次序，从0开始计算；第二个参数为需要映射的元素。
     * @return 映射并拼接后的double流水线
     * @throws NullPointerException 当{@code mapper}为{@code null}时抛出
     * @see Stream#flatMapToDouble(Function)
     */
    @Todo
    DoublePipe flatMapToDoubleOrderly(LongBiFunction<? super E, ? extends DoublePipe> mapper);

    /**
     * 使用给定的映射方法，将此流水线扩展为两元组的流水线，其中两元组的第一个元素仍然为当前流水线中的元素。
     *
     * @param secondMapper 两元组第二个元素的映射方法
     * @param <S> 两元组第二个元素的类型
     * @return 映射后的两元组流水线
     * @throws NullPointerException 当映射方法为{@code null}时抛出
     */
    @Todo
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
     * @throws NullPointerException 当任意映射方法为{@code null}时抛出
     */
    @Todo
    <F, S> BiPipe<F, S> extendToTuple(Function<? super E, ? extends F> firstMapper,
        Function<? super E, ? extends S> secondMapper);

    /**
     * 将流水线中的元素逐对扩展为两元组。
     *
     * @param keepLastIncompletePair 是否保留最后一个不完整的元组。
     * @return 扩展后的两元组流水线。
     */
    @Todo
    BiPipe<E, E> pairExtend(boolean keepLastIncompletePair);

    /**
     * 对流水线中的元素去重，以{@link Object#equals(Object)}为依据。
     *
     * @return 元素去重之后的流水线。
     * @see Stream#distinct()
     * @see #distinctBy(Function)
     */
    Pipe<E> distinct();

    /**
     * 对流水线中的元素去重，以给定的映射结果为依据。
     * <p/>
     * 大致等同于：
     * <pre>{@code
     * Set<K> seen = new HashSet<>();
     * for (E element : getPipeElements()) {
     *     K key = mapper.apply(element);
     *     if (!seen.contains(key)) {
     *         doSomething(element);
     *     } else {
     *         seen.add(key);
     *     }
     * }
     * }</pre>
     *
     * @param mapper 去重依据的映射方法。
     * @param <K> 映射结果的类型。
     * @return 元素按照映射结果去重之后的流水线。
     * @throws NullPointerException 当{@code mapper}为{@code null}时抛出。
     * @see #distinct()
     * @see #distinctByOrderly(LongBiFunction)
     */
    <K> Pipe<E> distinctBy(Function<? super E, ? extends K> mapper);

    /**
     * 对流水线中的元素去重，以给定的映射结果为依据，支持访问元素次序。
     * <p/>
     * 大致等同于：
     * <pre>{@code
     * long index = 0L;
     * Set<K> seen = new HashSet<>();
     * for (E element : getPipeElements()) {
     *     K key = mapper.apply(index++, element);
     *     if (!seen.contains(key)) {
     *         doSomething(element);
     *     } else {
     *         seen.add(key);
     *     }
     * }
     * }</pre>
     *
     * @param mapper 去重依据的映射方法：第一个参数为访问的元素在流水线中的次序，从0开始计算；第二个参数为需要映射的元素。
     * @param <K> 映射结果的类型。
     * @return 元素按照映射结果去重之后的流水线。
     * @throws NullPointerException 当{@code mapper}为{@code null}时抛出。
     * @see #distinctBy(Function)
     */
    <K> Pipe<E> distinctByOrderly(LongBiFunction<? super E, ? extends K> mapper);

    /**
     * 对流水线中的元素排序，以自然顺序排序。
     * <p/>
     * 要求元素实现了{@link Comparable}，否则会在流水线的终结操作中抛出{@link ClassCastException}异常。
     *
     * @return 元素自然有序的流水线。
     * @apiNote 此接口通常在元素实现了 {@link Comparable}时作为{@code sort(null)}或{@code sort(Comparator.naturalOrder())}
     * 的代替使用。
     * @implNote 流水线支持针对元素的排序情况进行优化：例如如果元素已经处于自然有序状态，则本次排序会被省略；
     * 或者如果元素已经处于自然逆序状态，则会以相对高效的逆序{@link #reverse()}代替自然排序，此时每个元素的
     * {@link Comparable#compareTo(Object)}方法调用会被省略。
     * @see Stream#sorted()
     * @see #sortReversely()
     * @see #sort(Comparator)
     * @see #reverse()
     */
    default Pipe<E> sort() {
        return sort(null);
    }

    /**
     * 对流水线中的元素排序，以自然逆序排序。
     * <p/>
     * 要求元素实现了{@link Comparable}，否则会在流水线的终结操作中抛出{@link ClassCastException}异常。
     *
     * @return 元素自然逆序的流水线。
     * @apiNote 此接口通常在元素实现了 {@link Comparable}时作为{@code sort(null)}或{@code sort(Comparator.reverseOrder())}
     * 的代替使用。
     * @implNote 流水线允许针对元素的排序情况进行优化：例如如果元素已经处于自然有序状态，则本次排序会被省略；
     * 或者如果元素已经处于自然逆序状态，则会以相对高效的逆序{@link #reverse()}代替自然排序，此时每个元素的
     * {@link Comparable#compareTo(Object)}方法调用会被省略。
     * @see #sort()
     * @see #reverse()
     */
    default Pipe<E> sortReversely() {
        return sort(MiscUtil.optimizedReverseOrder(null));
    }

    /**
     * 对流水线中的元素排序，以给定的比较方法排序。
     * <p/>
     * 如果元素已经实现了 {@link Comparable}接口，流水线允许对某些排序场景进行优化：<br/>
     * 当期望以自然顺序排序时，推荐传入{@link Comparator#naturalOrder()}作为比较器，或者使用{@link #sort()}方法；<br/>
     * 当期望以自然逆序排序时，推荐传入{@link Comparator#reverseOrder()}作为比较器，或者使用{@link #sortReversely()}方法。
     *
     * @param comparator 元素比较器，如果比较器为{@code null}则默认以{@link Comparator#naturalOrder()}作为比较器，
     * 此时如果元素没有实现{@link Comparable}接口则会在流水线终结操作中抛出{@link ClassCastException}异常。
     * @return 元素按照给定比较器排序后的流水线。
     * @see Stream#sorted(Comparator)
     * @see #sort()
     * @see #sortReversely()
     */
    Pipe<E> sort(Comparator<? super E> comparator);

    /**
     * 对流水线中的元素排序，以给定映射后的结果的自然顺序排序。
     * <p/>
     * 要求映射后的结果类型{@code R}实现了{@link Comparable}。
     *
     * @param mapper 排序依据的映射方法。
     * @param <K> 映射结果的类型。
     * @return 元素按照映射结果自然顺序排序后的流水线。
     * @throws NullPointerException 当{@code mapper}为{@code null}时抛出。
     * @implNote 如果 {@code mapper}为{@link Function#identity()}则此方法等同于{@code sort()}。
     * @see #sortBy(Function, Comparator)
     * @see #sortBy(Function)
     */
    default <K extends Comparable<? super K>> Pipe<E> sortBy(Function<? super E, ? extends K> mapper) {
        return isStdIdentify(mapper) ? sort() : sort(comparing(mapper));
    }

    /**
     * 对流水线中的元素排序，以给定的比较器对映射之后的结果排序顺序为准。
     *
     * @param mapper 排序依据的映射方法。
     * @param comparator 元素比较器，如果比较器为{@code null}则默认以{@link Comparator#naturalOrder()}作为比较器。
     * @param <K> 映射结果的类型。
     * @return 元素排序后的流水线。
     * @throws NullPointerException 当{@code mapper}为{@code null}时抛出。
     * @implNote 如果 {@code mapper}为{@link Function#identity()}则此方法等同于{@code sort(comparator)}。
     * @see #sortBy(Function)
     * @see #sortByOrderly(LongBiFunction, Comparator)
     */
    @SuppressWarnings("unchecked")
    default <K> Pipe<E> sortBy(Function<? super E, ? extends K> mapper, Comparator<? super K> comparator) {
        return isStdIdentify(mapper) ? sort((Comparator<? super E>) comparator) :
            sort(comparing(mapper, naturalOrderIfNull(comparator)));
    }

    /**
     * 对流水线中的元素排序，以给定的映射方法映射后的结果自然顺序为准，支持访问元素次序。
     * <p/>
     * 要求映射后的结果类型{@code R}实现了{@link Comparable}。
     *
     * @param mapper 排序依据的映射方法：第一个参数为访问的元素在流水线中的次序，从0开始计算；第二个参数为需要映射的元素。
     * @param <K> 映射结果的类型。
     * @return 元素排序后的流水线。
     * @throws NullPointerException 当{@code mapper}为{@code null}时抛出。
     * @see #sortBy(Function, Comparator)
     * @see #sortBy(Function)
     */
    default <K extends Comparable<? super K>> Pipe<E> sortByOrderly(LongBiFunction<? super E, ? extends K> mapper) {
        return sortByOrderly(mapper, naturalOrder());
    }

    /**
     * 对流水线中的元素排序，以给定的比较方法排序，排序的依据为映射后的结果。
     *
     * @param mapper 排序依据的映射方法：第一个参数为访问的元素在流水线中的次序，从0开始计算；第二个参数为需要映射的元素。
     * @param comparator 元素比较器，如果比较器为{@code null}则默认以{@link Comparator#naturalOrder()}作为比较器。
     * @param <R> 映射结果的类型。
     * @return 元素排序后的流水线。
     * @throws NullPointerException 当{@code mapper}为{@code null}时抛出。
     * @see #sortBy(Function, Comparator)
     * @see #sortByOrderly(LongBiFunction)
     */
    <R> Pipe<E> sortByOrderly(LongBiFunction<? super E, ? extends R> mapper, Comparator<? super R> comparator);

    /**
     * 将被选中的元素置于流水线的头部，选中的元素和未选中的元素各自相对位置保持不变。
     * <p/>
     * 一个示意：
     * <pre><code>
     *  select: A B C
     *          before                      after
     * ┌─────────────────────┐     ┌─────────────────────┐
     * │ ... 1 A 2 B 3 C ... │ ==> │ A B C ... 1 2 3 ... │
     * └─────────────────────┘     └─────────────────────┘
     * </code></pre>
     *
     * @param select 需要置于流水线头部的元素的选择方法。
     * @return 新的流水线。
     * @throws NullPointerException 当{@code select}为{@code null}时抛出。
     * @see #selectedLast(Predicate)
     */
    Pipe<E> selectedFirst(Predicate<? super E> select);

    /**
     * 将被选中的元素置于流水线的尾部，选中的元素和未选中的元素各自相对位置保持不变。
     * <p/>
     * 一个示意：
     * <pre><code>
     *  select: A B C
     *          before                      after
     * ┌─────────────────────┐     ┌─────────────────────┐
     * │ ... 1 A 2 B 3 C ... │ ==> │ ... 1 2 3 ... A B C │
     * └─────────────────────┘     └─────────────────────┘
     * </code></pre>
     *
     * @param select 需要置于流水线尾部的元素的选择方法。
     * @return 新的流水线。
     * @throws NullPointerException 当{@code select}为{@code null}时抛出。
     * @see #selectedFirst(Predicate)
     */
    Pipe<E> selectedLast(Predicate<? super E> select);

    /**
     * 将流水线中的{@code null}置于流水线头部，其他元素之间的相对位置保持不变。
     *
     * @return 新的流水线。
     * @see #nullsLast()
     * @see #nullsFirstBy(Function)
     */
    Pipe<E> nullsFirst();

    /**
     * 将流水线中根据给定方法映射后结果为{@code null}的元素置于流水线头部，需要移动的元素和不需要移动的元素相对位置保持不变。
     *
     * @param mapper 映射方法。
     * @return 新的流水线。
     * @throws NullPointerException 当{@code mapper}为{@code null}时抛出。
     * @implNote 如果 {@code mapper}为{@link Function#identity()}则此方法等同于{@code nullsFirst()}。
     * @see #nullsFirst()
     * @see #nullsLastBy(Function)
     */
    default Pipe<E> nullsFirstBy(Function<? super E, ?> mapper) {
        requireNonNull(mapper);
        return isStdIdentify(mapper) ? nullsFirst() : selectedFirst(value -> mapper.apply(value) == null);
    }

    /**
     * 将流水线中的{@code null}置于流水线末尾，其他元素之间的相对位置保持不变。
     *
     * @return 新的流水线。
     * @see #nullsFirst()
     * @see #nullsLastBy(Function)
     */
    Pipe<E> nullsLast();

    /**
     * 将流水线中根据给定方法映射后结果为{@code null}的元素置于流水线尾部，需要移动的元素和不需要移动的元素相对位置保持不变。
     *
     * @param mapper 映射方法。
     * @return 新的流水线。
     * @throws NullPointerException 当{@code mapper}为{@code null}时抛出。
     * @implNote 如果 {@code mapper}为{@link Function#identity()}则此方法等同于{@code nullsLast()}。
     * @see #nullsLast()
     * @see #nullsFirstBy(Function)
     */
    default Pipe<E> nullsLastBy(Function<? super E, ?> mapper) {
        requireNonNull(mapper);
        return isStdIdentify(mapper) ? nullsLast() : selectedLast(value -> mapper.apply(value) == null);
    }

    /**
     * 将流水线中的元素按照当前顺序颠倒。
     *
     * @return 元素顺序颠倒后的流水线。
     * @apiNote 此方法会用于代替某些场景下的操作，例如对一个已经自然有序的流水线执行自然逆序排序，或已经自然逆序的流水线
     * 执行自然有序排序，其中后者执行的排序操作允许被优化为逆序操作，以优化掉多余的{@link Comparable#compareTo(Object)}
     * 方法调用。
     * <p/>
     * <b>注意：此类优化暂未考虑对排序稳定性的影响，后续可能添加对此类优化的开关控制。</b>
     * @implNote 颠倒操作会对处于排序状态的流水线产生影响：会导致已经自然有序的流水线被认定为自然逆序，进而可以优化后续的
     * 自然逆序排序操作；还会导致已经自然逆序的流水线被认定为自然有序，进而可以优化后续的自然有序排序操作。
     */
    Pipe<E> reverse();

    /**
     * 随机打乱流水线中的元素。
     * <p/>
     * 使用的随机数非安全随机数，如果对随机有要求，请使用{@link #shuffle(Random)}。
     *
     * @return 元素顺序被打乱后新的流水线。
     * @see #shuffle(Random)
     */
    default Pipe<E> shuffle() {
        return shuffle(new Random());
    }

    /**
     * 使用给定的随机数生成器打乱流水线中的元素。
     *
     * @param random 用于随机元素次序的随机数生成器。
     * @return 元素顺序被打乱后新的流水线。
     * @throws NullPointerException 当{@code random}为{@code null}时抛出。
     * @see #shuffle()
     */
    Pipe<E> shuffle(Random random);

    /**
     * 以给定方法访问流水线中的元素。
     * <p/>
     * 如果需要以终结地方式对每个元素执行操作，请使用{@link #forEach(Consumer)}。
     *
     * @param consumer 元素访问方法。
     * @return 新的流水线。
     * @throws NullPointerException 当{@code consumer}为{@code null}时抛出。
     * @implNote 不同于经典Stream会优化某些场景下的访问方法调用，Pipe不会优化此访问方法。
     * @see Stream#peek(Consumer)
     * @see #forEach(Consumer)
     */
    Pipe<E> peek(Consumer<? super E> consumer);

    /**
     * 以给定方法访问流水线中的元素，访问方法支持访问元素在流水线中的次序。
     *
     * @param consumer 元素访问方法：第一个参数为访问的元素在流水线中的次序，从0开始计算；第二个参数为访问的元素。
     * @return 新的流水线。
     * @throws NullPointerException 当{@code consumer}为{@code null}时抛出。
     * @see #peek(Consumer)
     */
    Pipe<E> peekOrderly(LongBiConsumer<? super E> consumer);

    /**
     * 跳过指定数量的元素。
     * <p/>
     * {@code pipe.skip(0)}没有任何效果，等同于{@code pipe}自身；{@code pipe.skip(Long.MAX_VALUE)}得到空的流水线。
     *
     * @param size 需要跳过元素数量。
     * @return 新的流水线。
     * @throws IllegalArgumentException 当需要保留的元素数量小于0时抛出。
     * @see Stream#skip(long)
     * @see #skip(long, Predicate)
     * @see #limit(long)
     * @see #slice(long, long)
     */
    Pipe<E> skip(long size);

    /**
     * 持续跳过元素，直到已经跳过的满足给定断言的元素数量达到给定数量。当跳过的元素数量满足要求后，剩余元素不会再执行断言方法。
     * <p/>
     * {@code pipe.skip(0, predicate)}没有任何效果，等同于{@code pipe}自身；{@code pipe.skip(Long.MAX_VALUE, predicate)}
     * 得到空的流水线。
     *
     * @param size 需要跳过元素数量。
     * @param predicate 满足此条件的元素才会被计入跳过数量。
     * @return 新的流水线。
     * @throws IllegalArgumentException 当需要保留的元素数量小于0时抛出。
     * @throws NullPointerException 当断言方法{@code predicate}为{@code null}时抛出。
     * @see Stream#skip(long)
     * @see #skip(long)
     * @see #limit(long, Predicate)
     * @see #slice(long, long, Predicate)
     */
    Pipe<E> skip(long size, Predicate<? super E> predicate);

    /**
     * 仅保留给定数量的元素，丢弃剩余的元素。
     * <p/>
     * {@code pipe.limit(0)}得到空的流水线；{@code pipe.limit(Long.MAX_VALUE)}没有任何效果，等同于{@code pipe}自身。
     *
     * @param size 需要保留的元素数量。
     * @return 新的流水线。
     * @throws IllegalArgumentException 当需要保留的元素数量小于0时抛出。
     * @see Stream#limit(long)
     * @see #limit(long, Predicate)
     * @see #skip(long)
     * @see #slice(long, long)
     */
    Pipe<E> limit(long size);

    /**
     * 持续保留元素，直到已经保留的满足给定断言的元素数量达到给定数量，丢弃剩余的元素。
     * <p/>
     * {@code pipe.limit(0, predicate)}得到空的流水线；{@code pipe.limit(Long.MAX_VALUE, predicate)}没有任何效果，
     * 等同于{@code pipe}自身。
     *
     * @param size 需要保留的元素数量。
     * @param predicate 满足此条件的元素才会被计入保留数量。
     * @return 新的流水线。
     * @throws IllegalArgumentException 当需要保留的元素数量小于0时抛出。
     * @throws NullPointerException 当断言方法{@code predicate}为{@code null}时抛出。
     * @see #limit(long)
     * @see #skip(long, Predicate)
     * @see #slice(long, long, Predicate)
     */
    Pipe<E> limit(long size, Predicate<? super E> predicate);

    /**
     * 对元素执行切片，仅保留{@code startInclusive}到{@code endExclusive}之间的元素。
     * <p/>
     * 等同于：
     * <pre>{@code
     * pipe.skip(startInclusive).limit(endInclusive - startInclusive)
     * }</pre>
     * <p/>
     * {@code pipe.slice(0, Long.MAX_VALUE)}不会产生任何效果，等同于{@code pipe}自身。
     *
     * @param startInclusive 切片范围起始索引，包含。
     * @param endExclusive 切片范围结束索引，不包含。
     * @return 新的流水线。
     * @throws IllegalArgumentException 当起始索引小于0、结束索引小于0或起始索引大于结束索引时抛出。
     * @see #skip(long)
     * @see #limit(long)
     * @see #slice(long, long, Predicate)
     */
    Pipe<E> slice(long startInclusive, long endExclusive);

    /**
     * 对元素执行切片，仅保留{@code startInclusive}到{@code endExclusive}之间的元素，仅对满足断言的元素做数量统计。
     * <p/>
     * 等同于：
     * <pre>{@code
     * pipe.skip(startInclusive, predicate).limit(endInclusive - startInclusive, predicate)
     * }</pre>
     * <p/>
     * {@code pipe.slice(0, Long.MAX_VALUE, predicate)}不会产生任何效果，等同于{@code pipe}自身。
     * <p/>
     * 一个示意：
     * <pre><code>
     * predicate select: A B C D E
     * ┌─────────────────────────┐  slice(1, 4, select)  ┌───────────┐
     * │ 1 A 2 B 3 C 4 D 5 E ... │ ====================> │ B 3 C 4 D │
     * └─────────────────────────┘                       └───────────┘
     * </code></pre>
     *
     * @param startInclusive 切片范围起始索引，包含。
     * @param endExclusive 切片范围结束索引，不包含。
     * @param predicate 满足此条件的元素才会被计入数量。
     * @return 新的流水线。
     * @throws IllegalArgumentException 当起始索引小于0、结束索引小于0或起始索引大于结束索引时抛出。
     * @throws NullPointerException 当断言方法{@code predicate}为{@code null}时抛出。
     * @see #skip(long)
     * @see #limit(long)
     * @see #slice(long, long, Predicate)
     */
    Pipe<E> slice(long startInclusive, long endExclusive, Predicate<? super E> predicate);

    /**
     * 在流水线头部插入给定的拆分器中的元素。
     *
     * @param spliterator 包含需要插入到头部的元素的拆分器。
     * @return 新的流水线。
     * @throws NullPointerException 当拆分器{@code spliterator}为{@code null}时抛出。
     */
    Pipe<E> prepend(Spliterator<? extends E> spliterator);

    /**
     * 在流水线头部插入给定的流水线中的元素。
     * <p/>
     * 此方法会对{@code pipe}执行终结操作。
     *
     * @param pipe 包含需要插入到头部的元素的流水线。
     * @return 新的流水线。
     * @throws NullPointerException 当流水线{@code pipe}为{@code null}时抛出。
     */
    default Pipe<E> prepend(Pipe<? extends E> pipe) {
        return prepend(requireNonNull(pipe).toSpliterator());
    }

    /**
     * 在流水线头部插入给定的流中的元素。
     * <p/>
     * 此方法会对{@code stream}执行终结操作。
     *
     * @param stream 包含需要插入到头部的元素的流。
     * @return 新的流水线。
     * @throws NullPointerException 当流{@code stream}为{@code null}时抛出。
     */
    default Pipe<E> prepend(Stream<? extends E> stream) {
        return prepend(requireNonNull(stream).spliterator());
    }

    /**
     * 在流水线头部插入给定的数组中的元素。
     *
     * @param values 需要插入到头部的元素。
     * @return 新的流水线。
     * @throws NullPointerException 当数组{@code values}为{@code null}时抛出。
     */
    @SuppressWarnings({"unchecked", "varargs"})
    default Pipe<E> prepend(E... values) {
        return prepend(Arrays.spliterator(requireNonNull(values)));
    }

    /**
     * 在流水线头部插入给定的元素。
     *
     * @param value 需要插入到头部的元素。
     * @return 新的流水线。
     */
    Pipe<E> prepend(E value);

    /**
     * 在流水线头部插入给定的Map中的键。
     *
     * @param map 包含需要插入到头部的键的Map。
     * @return 新的流水线。
     * @throws NullPointerException 当{@code map}为{@code null}时抛出。
     * @see #prependKeys(Map, Predicate)
     * @see #appendKeys(Map)
     */
    default Pipe<E> prependKeys(Map<? extends E, ?> map) {
        return prepend(requireNonNull(map).keySet().spliterator());
    }

    /**
     * 在流水线头部插入给定的Map中的键。
     * <p/>
     * 只有符合要求的值的键才会插入流水线。
     *
     * @param map 包含需要插入到头部的键的Map。
     * @param valuePredicate 值筛选方法。
     * @return 新的流水线。
     * @throws NullPointerException 当{@code map}或筛选方法{@code valuePredicate}为{@code null}时抛出。
     * @see #prependKeys(Map)
     * @see #appendKeys(Map, Predicate)
     */
    default <V> Pipe<E> prependKeys(Map<? extends E, ? extends V> map, Predicate<? super V> valuePredicate) {
        return prepend(keys(requireNonNull(map), requireNonNull(valuePredicate)));
    }

    /**
     * 在流水线头部插入给定的Map中的值。
     *
     * @param map 包含需要插入到头部的值的Map。
     * @return 新的流水线。
     * @throws NullPointerException 当{@code map}为{@code null}时抛出。
     * @see #prependValues(Map, Predicate)
     * @see #appendValues(Map)
     */
    default Pipe<E> prependValues(Map<?, ? extends E> map) {
        return prepend(requireNonNull(map).values().spliterator());
    }

    /**
     * 在流水线头部插入给定的Map中的值。
     * <p/>
     * 只有符合要求的键的值才会插入流水线。
     *
     * @param map 包含需要插入到头部的值的Map。
     * @param keyPredicate 键筛选方法。
     * @return 新的流水线。
     * @throws NullPointerException 当{@code map}或筛选方法{@code keyPredicate}为{@code null}时抛出。
     * @see #prependValues(Map)
     * @see #appendValues(Map, Predicate)
     */
    default <K> Pipe<E> prependValues(Map<? extends K, ? extends E> map, Predicate<? super K> keyPredicate) {
        return prepend(values(requireNonNull(map), requireNonNull(keyPredicate)));
    }

    /**
     * 在流水线尾部插入给定的拆分器中的元素。
     *
     * @param spliterator 包含需要插入到尾部的元素的拆分器。
     * @return 新的流水线。
     * @throws NullPointerException 当拆分器{@code spliterator}为{@code null}时抛出。
     */
    Pipe<E> append(Spliterator<? extends E> spliterator);

    /**
     * 在流水线尾部插入给定的流水线中的元素。
     * <p/>
     * 此方法会对{@code pipe}执行终结操作。
     *
     * @param pipe 包含需要插入到尾部的元素的流水线。
     * @return 新的流水线。
     * @throws NullPointerException 当流水线{@code pipe}为{@code null}时抛出。
     */
    default Pipe<E> append(Pipe<? extends E> pipe) {
        return append(requireNonNull(pipe).toSpliterator());
    }

    /**
     * 在流水线尾部插入给定的流中的元素。
     * <p/>
     * 此方法会对{@code stream}执行终结操作。
     *
     * @param stream 包含需要插入到尾部的元素的流。
     * @return 新的流水线。
     * @throws NullPointerException 当流{@code stream}为{@code null}时抛出。
     */
    default Pipe<E> append(Stream<? extends E> stream) {
        return append(requireNonNull(stream).spliterator());
    }

    /**
     * 在流水线尾部插入给定的数组中的元素。
     *
     * @param values 需要插入到尾部的元素。
     * @return 新的流水线。
     * @throws NullPointerException 当数组{@code values}为{@code null}时抛出。
     */
    @SuppressWarnings({"unchecked", "varargs"})
    default Pipe<E> append(E... values) {
        return append(Arrays.spliterator(requireNonNull(values)));
    }

    /**
     * 在流水线尾部插入给定的元素。
     *
     * @param value 需要插入到尾部的元素。
     * @return 新的流水线。
     */
    Pipe<E> append(E value);

    /**
     * 在流水线尾部插入给定的Map中的键。
     *
     * @param map 包含需要插入到尾部的键的Map。
     * @return 新的流水线。
     * @throws NullPointerException 当{@code map}为{@code null}时抛出。
     * @see #appendKeys(Map, Predicate)
     * @see #prependKeys(Map)
     */
    default Pipe<E> appendKeys(Map<? extends E, ?> map) {
        return append(requireNonNull(map).keySet().spliterator());
    }

    /**
     * 在流水线尾部插入给定的Map中的键。
     * <p/>
     * 只有符合要求的值的键才会插入流水线。
     *
     * @param map 包含需要插入到尾部的键的Map。
     * @param valuePredicate 值筛选方法。
     * @return 新的流水线。
     * @throws NullPointerException 当{@code map}或筛选方法{@code valuePredicate}为{@code null}时抛出。
     * @see #appendKeys(Map)
     * @see #prependKeys(Map, Predicate)
     */
    default <V> Pipe<E> appendKeys(Map<? extends E, ? extends V> map, Predicate<? super V> valuePredicate) {
        return append(keys(requireNonNull(map), requireNonNull(valuePredicate)));
    }

    /**
     * 在流水线尾部插入给定的Map中的值。
     *
     * @param map 包含需要插入到尾部的值的Map。
     * @return 新的流水线。
     * @throws NullPointerException 当{@code map}为{@code null}时抛出。
     * @see #appendValues(Map, Predicate)
     * @see #prependValues(Map)
     */
    default Pipe<E> appendValues(Map<?, ? extends E> map) {
        return append(requireNonNull(map).values().spliterator());
    }

    /**
     * 在流水线尾部插入给定的Map中的值。
     * <p/>
     * 只有符合要求的键的值才会插入流水线。
     *
     * @param map 包含需要插入到尾部的值的Map。
     * @param keyPredicate 键筛选方法。
     * @return 新的流水线。
     * @throws NullPointerException 当{@code map}或筛选方法{@code keyPredicate}为{@code null}时抛出。
     * @see #appendValues(Map)
     * @see #prependValues(Map, Predicate)
     */
    default <K> Pipe<E> appendValues(Map<? extends K, ? extends E> map, Predicate<? super K> keyPredicate) {
        return append(values(requireNonNull(map), requireNonNull(keyPredicate)));
    }

    /**
     * 使用给定的数据分隔流水线中的元素。
     * <p/>
     * 一个示意：
     * <pre><code>
     * ┌─────────────────────┐  disperse("X")  ┌─────────────────────────────────┐
     * │ 1 2 3 4 ... 5 6 7 8 │ ==============> │ 1 X 2 X 3 X 4 ... 5 X 6 X 7 X 8 │
     * └─────────────────────┘                 └─────────────────────────────────┘
     * </code></pre>
     *
     * @param delimiter 分隔数据。
     * @return 新的流水线。
     */
    Pipe<E> disperse(E delimiter); // OPT 2023-05-18 23:15 考虑更多类似的API

    /**
     * 根据固定数量对元素进行分区，并将分区结果封装为新的流水线。
     * <p/>
     * 根据实际情况，最后一个分区包含的元素数量可能不足给定大小，但不会为空分区。
     *
     * @param size 需要分区的元素数量。
     * @return 新的包含已分区元素的流水线。
     * @throws IllegalArgumentException 当给定的分区元素数量小于1时抛出。
     * @see #partition(Function)
     */
    Pipe<Pipe<E>> partition(int size); // OPT 2023-06-07 22:14 添加支持Supplier<Integer>类型参数的API

    /**
     * 根据{@link PartitionPolicy}策略对元素进行分区，并将分区结果封装为新的流水线。
     *
     * @param function 分区策略方法，返回的分区策略不能为{@code null}。
     * @return 新的包含分区元素的流水线。
     * @throws NullPointerException 当分区策略方法{@code function}为{@code null}时抛出。
     * @implNote 如果给定的分区策略方法 {@code function}返回{@code null}，会导致流水线运行期间抛出
     * {@link NullPointerException}异常。
     * @see #partition(int)
     * @see #partitionOrderly(LongBiFunction)
     */
    Pipe<Pipe<E>> partition(Function<? super E, PartitionPolicy> function);

    /**
     * 根据{@link PartitionPolicy}策略对元素进行分区，并将分区结果封装为新的流水线，支持访问元素的次序。
     *
     * @param function 分区策略方法，返回的分区策略不能为{@code null}，第一个参数为元素的次序，第二个参数为访问的元素。
     * @return 新的包含分区元素的流水线。
     * @throws NullPointerException 当分区策略方法{@code function}为{@code null}时抛出。
     * @implNote 如果给定的分区策略方法 {@code function}返回{@code null}，会导致流水线运行期间抛出
     * {@link NullPointerException}异常。
     * @see #partition(int)
     * @see #partition(Function)
     */
    Pipe<Pipe<E>> partitionOrderly(LongBiFunction<? super E, PartitionPolicy> function);

    /**
     * 按照给定数量，对元素进行分区，并将分区结果封装为列表。
     * <p/>
     * 根据实际情况，最后一个分区包含的元素数量可能不足给定大小，但不会为空分区。
     *
     * @param size 需要分区的元素数量。
     * @return 新的包含已分区元素列表的流水线。
     * @throws IllegalArgumentException 当给定的分区元素数量小于1时抛出。
     * @implNote 封装的列表不保证可变性，如果明确需要分区列表可修改，请使用{@link #partitionToList(int, Supplier)}。
     * @see #partitionToList(int, Supplier)
     * @see #partitionToList(Function)
     */
    default Pipe<List<E>> partitionToList(int size) {
        return partition(size).map(Pipe::toList);
    }

    /**
     * 根据{@link PartitionPolicy}策略对元素进行分区，并将分区结果封装为列表。
     *
     * @param function 分区策略方法，返回的分区策略不能为{@code null}。
     * @return 新的包含已分区元素列表的流水线。
     * @throws NullPointerException 当分区策略方法{@code function}为{@code null}时抛出。
     * @implNote 封装的列表不保证可变性，如果明确需要分区列表可修改，请使用{@link #partitionToList(Function, Supplier)}。
     * 如果给定的分区策略方法{@code function}返回{@code null}，会导致流水线运行期间抛出{@link NullPointerException}异常。
     * @see #partitionToList(Function, Supplier)
     * @see #partitionToList(int)
     */
    default Pipe<List<E>> partitionToList(Function<? super E, PartitionPolicy> function) {
        return partition(function).map(Pipe::toList);
    }

    /**
     * 按照给定数量，对元素进行分区，并将分区结果封装为列表，列表由给定的{@link Supplier}提供。
     * <p/>
     * 根据实际情况，最后一个分区包含的元素数量可能不足给定大小，但不会为空分区。
     *
     * @param size 需要分区的元素数量。
     * @param listSupplier 用于存储分区元素的列表的构造方法。
     * @return 新的包含已分区元素列表的流水线。
     * @throws IllegalArgumentException 当给定的分区元素数量小于1时抛出。
     * @throws NullPointerException 当列表构建方法{@code listSupplier}为{@code null}时抛出。
     * @see #partitionToList(int)
     * @see #partitionToList(Function, Supplier)
     */
    default <L extends List<E>> Pipe<List<E>> partitionToList(int size, Supplier<L> listSupplier) {
        requireNonNull(listSupplier);
        return partition(size).map(pipe -> pipe.toList(listSupplier));
    }

    /**
     * 根据{@link PartitionPolicy}策略对元素进行分区，并将分区结果封装为列表，列表由给定的{@link Supplier}提供。
     *
     * @param function 分区策略方法，返回的分区策略不能为{@code null}。
     * @param listSupplier 用于存储分区元素的列表的构造方法。
     * @return 新的包含已分区元素列表的流水线。
     * @throws NullPointerException 当分区策略方法{@code function}或列表构建方法{@code listSupplier}为{@code null}时抛出。
     * @implNote 如果给定的分区策略方法{@code function}返回{@code null}，会导致流水线运行期间抛出
     * {@link NullPointerException}异常。
     * @see #partitionToList(Function)
     * @see #partitionToList(int, Supplier)
     */
    default <L extends List<E>> Pipe<List<E>> partitionToList(Function<? super E, PartitionPolicy> function,
        Supplier<L> listSupplier) {
        requireNonNull(listSupplier);
        return partition(function).map(pipe -> pipe.toList(listSupplier));
    }

    /**
     * 按照给定数量，对元素进行分区，并将分区结果封装为集合。
     * <p/>
     * 根据实际情况，最后一个分区包含的元素数量可能不足给定大小，但不会为空分区。
     *
     * @param size 需要分区的元素数量。
     * @return 新的包含已分区元素集合的流水线。
     * @throws IllegalArgumentException 当给定的分区元素数量小于1时抛出。
     * @implNote 封装的集合不保证可变性，如果明确需要分区集合可修改，请使用{@link #partitionToSet(int, Supplier)}。
     * @see #partitionToSet(int, Supplier)
     * @see #partitionToSet(Function)
     */
    default Pipe<Set<E>> partitionToSet(int size) {
        return partition(size).map(Pipe::toSet);
    }

    /**
     * 根据{@link PartitionPolicy}策略对元素进行分区，并将分区结果封装为集合。
     *
     * @param function 分区策略方法，返回的分区策略不能为{@code null}。
     * @return 新的包含已分区元素集合的流水线。
     * @throws NullPointerException 当分区策略方法{@code function}为{@code null}时抛出。
     * @implNote 封装的集合不保证可变性，如果明确需要分区列表可修改，请使用{@link #partitionToSet(Function, Supplier)}。
     * 如果给定的分区策略方法{@code function}返回{@code null}，会导致流水线运行期间抛出{@link NullPointerException}异常。
     * @see #partitionToSet(Function, Supplier)
     * @see #partitionToSet(int)
     */
    default Pipe<Set<E>> partitionToSet(Function<? super E, PartitionPolicy> function) {
        return partition(function).map(Pipe::toSet);
    }

    /**
     * 按照给定数量，对元素进行分区，并将分区结果封装为集合，集合由给定的{@link Supplier}提供。
     * <p/>
     * 根据实际情况，最后一个分区包含的元素数量可能不足给定大小，但不会为空分区。
     *
     * @param size 需要分区的元素数量。
     * @param setSupplier 用于存储分区元素的集合的构造方法。
     * @return 新的包含已分区元素集合的流水线。
     * @throws IllegalArgumentException 当给定的分区元素数量小于1时抛出。
     * @throws NullPointerException 当集合构建方法{@code setSupplier}为{@code null}时抛出。
     * @see #partitionToSet(int)
     * @see #partitionToSet(Function, Supplier)
     */
    default <S extends Set<E>> Pipe<Set<E>> partitionToSet(int size, Supplier<S> setSupplier) {
        requireNonNull(setSupplier);
        return partition(size).map(pipe -> pipe.toSet(setSupplier));
    }

    /**
     * 根据{@link PartitionPolicy}策略对元素进行分区，并将分区结果封装为集合，集合由给定的{@link Supplier}提供。
     *
     * @param function 分区策略方法，返回的分区策略不能为{@code null}。
     * @param setSupplier 用于存储分区元素的集合的构造方法。
     * @return 新的包含已分区元素集合的流水线。
     * @throws NullPointerException 当分区策略方法{@code function}或集合构建方法{@code setSupplier}为{@code null}时抛出。
     * @implNote 如果给定的分区策略方法{@code function}返回{@code null}，会导致流水线运行期间抛出
     * {@link NullPointerException}异常。
     * @see #partitionToSet(int, Supplier)
     * @see #partitionToSet(Function)
     */
    default <S extends Set<E>> Pipe<Set<E>> partitionToSet(Function<? super E, PartitionPolicy> function,
        Supplier<S> setSupplier) {
        requireNonNull(setSupplier);
        return partition(function).map(pipe -> pipe.toSet(setSupplier));
    }

    /**
     * 按照给定数量，对元素进行分区，并将分区结果封装为容器，容器由给定的{@link Supplier}提供。
     * <p/>
     * 根据实际情况，最后一个分区包含的元素数量可能不足给定大小，但不会为空分区。
     *
     * @param size 需要分区的元素数量。
     * @param collectionSupplier 用于存储分区元素的容器的构造方法。
     * @return 新的包含已分区元素容器的流水线。
     * @throws IllegalArgumentException 当给定的分区元素数量小于1时抛出。
     * @throws NullPointerException 当容器构建方法{@code collectionSupplier}为{@code null}时抛出。
     * @see #partitionToCollection(Function, Supplier)
     */
    default <S extends Collection<E>> Pipe<Collection<E>> partitionToCollection(int size,
        Supplier<S> collectionSupplier) {
        requireNonNull(collectionSupplier);
        return partition(size).map(pipe -> pipe.toCollection(collectionSupplier));
    }

    /**
     * 根据{@link PartitionPolicy}策略对元素进行分区，并将分区结果封装为容器，容器由给定的{@link Supplier}提供。
     *
     * @param function 分区策略方法，返回的分区策略不能为{@code null}。
     * @param collectionSupplier 用于存储分区元素的容器的构造方法。
     * @return 新的包含已分区元素容器的流水线。
     * @throws NullPointerException 当分区策略方法{@code function}或容器构建方法{@code setSupplier}为{@code null}时抛出。
     * @implNote 如果给定的分区策略方法{@code function}返回{@code null}，会导致流水线运行期间抛出
     * {@link NullPointerException}异常。
     * @see #partitionToCollection(int, Supplier)
     */
    default <S extends Collection<E>> Pipe<Collection<E>> partitionToCollection(
        Function<? super E, PartitionPolicy> function, Supplier<S> collectionSupplier) {
        requireNonNull(collectionSupplier);
        return partition(function).map(pipe -> pipe.toCollection(collectionSupplier));
    }

    /**
     * 结合第二条流水线，组合成两元组流水线。
     *
     * @param secondPipe 第二条流水线
     * @param <S> 第二条流水线中的元素类型
     * @return 新的两元组流水线
     * @apiNote 当任意流水线耗尽时，新的双元组流水线即耗尽，哪怕还有剩余元素。
     */
    @Todo
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

    default <K> Map<K, E> toMapKeyed(Function<? super E, ? extends K> keyMapper) {
        return toMapKeyed(HashMap::new, keyMapper);
    }

    default <V> Map<E, V> toMapValued(Function<? super E, ? extends V> valueMapper) {
        return toMapValued(HashMap::new, valueMapper);
    }

    default <K, V> Map<K, V> toMap(Function<? super E, ? extends K> keyMapper,
        Function<? super E, ? extends V> valueMapper) {
        return toMap(HashMap::new, keyMapper, valueMapper);
    }

    default <K, M extends Map<K, E>> M toMapKeyed(Supplier<M> supplier, Function<? super E, ? extends K> keyMapper) {
        requireNonNull(supplier);
        requireNonNull(keyMapper);
        return reduceIdentity(supplier.get(), (map, value) -> map.put(keyMapper.apply(value), value));
    }

    default <V, M extends Map<E, V>> M toMapValued(Supplier<M> supplier, Function<? super E, ? extends V> valueMapper) {
        requireNonNull(supplier);
        requireNonNull(valueMapper);
        return reduceIdentity(supplier.get(), (map, value) -> map.put(value, valueMapper.apply(value)));
    }

    default <K, V, M extends Map<K, V>> M toMap(Supplier<M> supplier, Function<? super E, ? extends K> keyMapper,
        Function<? super E, ? extends V> valueMapper) {
        requireNonNull(supplier);
        requireNonNull(keyMapper);
        requireNonNull(valueMapper);
        return reduceIdentity(supplier.get(),
            (map, value) -> map.put(keyMapper.apply(value), valueMapper.apply(value)));
    }

    default <K> Map<K, E> toUnmodifiableMapKeyed(Function<? super E, ? extends K> keyMapper) {
        return Collections.unmodifiableMap(toMapKeyed(keyMapper));
    }

    default <V> Map<E, V> toUnmodifiableMapValued(Function<? super E, ? extends V> valueMapper) {
        return Collections.unmodifiableMap(toMapValued(valueMapper));
    }

    default <K, V> Map<K, V> toUnmodifiableMap(Function<? super E, ? extends K> keyMapper,
        Function<? super E, ? extends V> valueMapper) {
        return Collections.unmodifiableMap(toMap(keyMapper, valueMapper));
    }

    <K> BiPipe<K, Pipe<E>> groupAndExtend(Function<? super E, ? extends K> classifier);

    default <K> Pipe<List<E>> groupValues(Function<? super E, ? extends K> classifier) {
        Collection<List<E>> values = group(classifier).values();
        return spliterator(Spliterators.spliterator(values.iterator(), values.size(), 0));
    }

    default <K> Pipe<Pipe<E>> groupFlatValues(Function<? super E, ? extends K> classifier) {
        return groupValues(classifier).map(Pipe::list);
    }

    default <K> Map<K, List<E>> group(Function<? super E, ? extends K> classifier) {
        return group(classifier, HashMap::new);
    }

    default <K, M extends Map<K, List<E>>> M group(Function<? super E, ? extends K> classifier,
        Supplier<? extends M> mapSupplier) {
        requireNonNull(classifier);
        requireNonNull(mapSupplier);
        return reduceIdentity(mapSupplier.get(),
            (map, value) -> map.computeIfAbsent(classifier.apply(value), key -> new ArrayList<>()).add(value));
    }

    @SuppressWarnings("unchecked")
    default <K, V> Map<K, V> groupAndThen(Function<? super E, ? extends K> classifier,
        BiFunction<K, List<E>, V> finisher) {
        requireNonNull(classifier);
        requireNonNull(finisher);
        HashMap<K, Object> result = reduceIdentity(new HashMap<>(),
            (map, value) -> ((ArrayList<E>) map.computeIfAbsent(classifier.apply(value),
                key -> new ArrayList<E>())).add(value));
        result.replaceAll((key, list) -> finisher.apply(key, (List<E>) list));
        return (Map<K, V>) result;
    }

    default <K> Map<K, Long> groupAndCount(Function<? super E, ? extends K> classifier) {
        requireNonNull(classifier);
        return reduceIdentity(new HashMap<>(),
            (map, value) -> map.compute(classifier.apply(value), (key, count) -> count == null ? 1L : count + 1));
    }

    default <K> Map<K, List<E>> groupAndExecute(Function<? super E, ? extends K> classifier,
        BiConsumer<K, List<E>> action) {
        HashMap<K, List<E>> map = group(classifier, HashMap::new);
        map.forEach(action);
        return map;
    }

    /**
     * 使用给定的分隔符、前缀和后缀将流水线拼接为一个字符串。
     * <p/>
     * 使用{@link Objects#toString(Object)}作为流水线转换为字符串的方法。
     *
     * @param delimiter 分隔符。
     * @param prefix 前缀。
     * @param suffix 后缀。
     * @return 拼接后的字符串。
     * @see #join(CharSequence)
     * @see #join()
     */
    default String join(CharSequence delimiter, CharSequence prefix, CharSequence suffix) {
        return reduce(new StringJoiner(delimiter, prefix, suffix),
            (joiner, value) -> joiner.add(Objects.toString(value))).toString();
    }

    /**
     * 使用给定的分隔符将流水线拼接为一个字符串。
     * <p/>
     * 使用{@link Objects#toString(Object)}作为流水线转换为字符串的方法。
     *
     * @param delimiter 分隔符。
     * @return 拼接后的字符串。
     * @see #join(CharSequence, CharSequence, CharSequence)
     * @see #join()
     */
    default String join(CharSequence delimiter) {
        return join(delimiter, "", "");
    }

    /**
     * 使用英文逗号分隔符将流水线拼接为一个字符串。
     * <p/>
     * 使用{@link Objects#toString(Object)}作为流水线转换为字符串的方法。
     *
     * @return 拼接后的字符串。
     * @see #join(CharSequence, CharSequence, CharSequence)
     * @see #join(CharSequence)
     */
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

    /**
     * 获取空的流水线实例，此流水线不包含任何数据。
     *
     * @param <T> 数据类型。
     * @return 空的流水线。
     */
    static <T> Pipe<T> empty() {
        return new PipeHead<>(Spliterators.emptySpliterator());
    }

    /**
     * 从给定的拆分器中构建新的流水线。
     *
     * @param spliterator 拆分器。
     * @param <T> 拆分器中的元素类型。
     * @return 新的流水线。
     * @throws NullPointerException 当{@code spliterator}为{@code null}时抛出。
     * @see #spliterator(Spliterator, int)
     */
    static <T> Pipe<T> spliterator(Spliterator<? extends T> spliterator) {
        return new PipeHead<>(requireNonNull(spliterator));
    }

    /**
     * 从给定的拆分器中构建新的流水线实例，支持额外的数据标记。
     *
     * @param spliterator 拆分器。
     * @param extraFlag 额外的数据标记。
     * @param <T> 拆分器中的元素类型。
     * @return 新的流水线。
     * @throws NullPointerException 当{@code spliterator}为{@code null}时抛出。
     * @apiNote 此方法仅限于充分了解给定拆分器中元素分布时使用，其{@code extraFlag}参数会用于指导流水线优化，如果给定的标记有误
     * 可能误导流水线的优化操作，进而影响结果正确性，当不确定此标记是否设置正确时请优先使用{@link #spliterator(Spliterator)}。
     * @see PipeFlag
     * @see #spliterator(Spliterator)
     */
    static <T> Pipe<T> spliterator(Spliterator<? extends T> spliterator, int extraFlag) {
        return new PipeHead<>(requireNonNull(spliterator), extraFlag);
    }

    /**
     * 获取仅包含单个给定元素的流水线。
     *
     * @param singleton 数据。
     * @param <T> 数据类型。
     * @return 流水线。
     * @see #optional(Object)
     */
    static <T> Pipe<T> singleton(T singleton) {
        return spliterator(MoreSpliterators.singleton(singleton));
    }

    /**
     * 从给定的单个数据生成流水线，如果给定的数据不为{@code null}，则生成单例流水线，否则生成空流水线。
     *
     * @param value 数据，可能为{@code null}。
     * @param <T> 数据类型。
     * @return 单例流水线或空流水线。
     * @see #singleton(Object)
     * @see #empty()
     */
    static <T> Pipe<T> optional(T value) {
        return value == null ? empty() : singleton(value);
    }

    /**
     * 根据给定的元素构造一个新的流水线。
     *
     * @param values 包含在流水线中的元素。
     * @param <T> 元素类型。
     * @return 新的流水线。
     * @see #of(int, Object[])
     */
    @SafeVarargs
    @SuppressWarnings("varargs")
    static <T> Pipe<T> of(T... values) {
        if (values.length == 0) {
            return empty();
        }
        if (values.length == 1) {
            return singleton(values[0]);
        }
        return spliterator(Arrays.spliterator(values));
    }

    /**
     * 根据给定的元素构造一个新的流水线实例，支持额外的数据标记。
     *
     * @param values 包含在流水线中的元素。
     * @param <T> 元素类型。
     * @return 新的流水线。
     * @apiNote 此方法仅限于充分了解给定元素分布时使用，其{@code extraFlag}参数会用于指导流水线优化，如果给定的标记有误
     * 可能误导流水线的优化操作，进而影响结果正确性，当不确定此标记是否设置正确时请优先使用{@link #of(Object[])}。
     * @see PipeFlag
     * @see #of(Object[])
     */
    @SafeVarargs
    @SuppressWarnings("varargs")
    static <T> Pipe<T> of(int extraFlag, T... values) { // MK 2023-06-05 23:54 考虑是否保留此API
        if (values.length == 0) {
            return empty();
        }
        if (values.length == 1) {
            return singleton(values[0]);
        }
        return spliterator(Arrays.spliterator(values));
    }

    /**
     * 根据常量生成一个新的流水线实例，常量的数量由{@code count}指定。
     * <p/>
     * 即：将给定的常量数据{@code constant}重复{@code count}次组成流水线。
     *
     * @param constant 常量数据。
     * @param count 数量。
     * @param <T> 常量数据类型。
     * @return 新的流水线。
     * @throws IllegalArgumentException 当常量数量{@code count}为负数时抛出。
     */
    static <T> Pipe<T> constant(T constant, int count) {
        if (count < 0) {
            throw new IllegalArgumentException("Pipe length cannot be negative: " + count);
        }
        if (count == 0) {
            return empty();
        }
        if (count == 1) {
            return singleton(constant);
        }
        return spliterator(MoreSpliterators.constant(constant, count));
    }

    /**
     * 从给定的映射的键中生成流水线。
     *
     * @param map 映射。
     * @param <T> 映射的键的类型
     * @return 新的流水线。
     * @throws NullPointerException 当{@code map}为{@code null}时抛出。
     * @see #keys(Map, Predicate)
     * @see #values(Map)
     */
    static <T> Pipe<T> keys(Map<? extends T, ?> map) {
        return spliterator(requireNonNull(map).keySet().spliterator());
    }

    /**
     * 从给定的映射的键中生成流水线，仅使用其中对应值满足给定断言的键。
     *
     * @param map 映射。
     * @param valuePredicate 值筛选方法。
     * @param <T> 映射的键的类型。
     * @param <V> 映射的值的类型。
     * @return 新的流水线。
     * @throws NullPointerException 当{@code map}或{@code valuePredicate}为{@code null}时抛出。
     * @see #keys(Map)
     * @see #values(Map, Predicate)
     */
    static <T, V> Pipe<T> keys(Map<? extends T, ? extends V> map, Predicate<? super V> valuePredicate) {
        // OPT 2023-06-05 23:23 使用BiPipe优化
        return spliterator(requireNonNull(map).entrySet().spliterator()).filter(
            entry -> valuePredicate.test(entry.getValue())).map(Map.Entry::getKey);
    }

    /**
     * 从给定的映射的值中生成流水线。
     *
     * @param map 映射。
     * @param <T> 映射的值的类型
     * @return 新的流水线。
     * @throws NullPointerException 当{@code map}为{@code null}时抛出。
     * @see #values(Map, Predicate)
     * @see #keys(Map)
     */
    static <T> Pipe<T> values(Map<?, ? extends T> map) {
        return spliterator(requireNonNull(map).values().spliterator());
    }

    /**
     * 从给定的映射的值中生成流水线，仅使用其中对应键满足给定断言的值。
     *
     * @param map 映射。
     * @param keyPredicate 值筛选方法。
     * @param <T> 映射的值的类型。
     * @param <K> 映射的键的类型。
     * @return 新的流水线。
     * @throws NullPointerException 当{@code map}或{@code keyPredicate}为{@code null}时抛出。
     * @see #values(Map)
     * @see #keys(Map, Predicate)
     */
    static <T, K> Pipe<T> values(Map<? extends K, ? extends T> map, Predicate<? super K> keyPredicate) {
        // OPT 2023-06-05 23:23 使用BiPipe优化
        return spliterator(requireNonNull(map).entrySet().spliterator()).filter(
            entry -> keyPredicate.test(entry.getKey())).map(Map.Entry::getValue);
    }

    /**
     * 创建一个含有无限元素的流水线，元素由{@code supplier}持续提供。
     *
     * @param supplier 提供元素的生成器。
     * @param <T> 元素类型。
     * @return 无限长度的流水线。
     * @throws NullPointerException 当{@code supplier}为{@code null}时抛出。
     * @see Stream#generate(Supplier)
     */
    static <T> Pipe<T> generate(Supplier<? extends T> supplier) {
        return spliterator(MoreSpliterators.generate(requireNonNull(supplier)));
    }

    /**
     * 拼接给定的流水线，每个流水线中的元素按次序处理。
     *
     * @param pipes 需要拼接的流水线。
     * @param <T> 元素类型。
     * @return 拼接后的流水线。
     * @throws NullPointerException 当流水线{@code pipes}存在{@code null}时抛出。
     * @see #append(Pipe)
     */
    @SafeVarargs
    @SuppressWarnings("varargs")
    static <T> Pipe<T> concat(Pipe<? extends T>... pipes) {
        if (pipes == null || pipes.length == 0) {
            return empty();
        }
        Pipe<T> resPipe = empty();
        for (Pipe<? extends T> pipe : pipes) {
            resPipe = resPipe.append(pipe);
        }
        return resPipe;
    }

    /**
     * 创建一个新的流水线，从给定的{@link Stream}实例中获取元素。
     * <p/>
     * 用于转换标准流。
     *
     * @param stream 需要获取元素的流。
     * @param <T> 元素类型。
     * @return 新的流水线。
     * @throws NullPointerException 当流{@code stream}为{@code null}时抛出。
     * @implNote 此方法通过调用 {@link Stream#iterator()}方法获取流元素的迭代器来组装流水线，此操作会终结给定的流。
     * @see Stream#iterator()
     */
    static <T> Pipe<T> stream(Stream<? extends T> stream) {
        return spliterator(requireNonNull(stream).spliterator());
    }

    /**
     * 创建一个含有无限元素的流水线，元素的生成以给定的种子{@code seed}为基础，每个元素都是对上一个元素应用{@code generator}
     * 生成，即使生成的结果为{@code null}也会被认为是有效元素。
     * <p/>
     * 大致等同于：
     * <pre>{@code
     * T element = seed;
     * do {
     *     doSomething(element);
     *     element = generator.apply(element);
     * } while (true)
     * }</pre>
     *
     * @param seed 初始种子元素，第0个元素。
     * @param generator 后续元素的生成器，以前一个元素为参数。
     * @param <T> 元素类型。
     * @return 无限长度的流水线。
     * @throws NullPointerException 当迭代器{@code generator}为{@code null}时抛出。
     * @see Stream#iterate(Object, UnaryOperator)
     */
    static <T> Pipe<T> iterate(final T seed, final UnaryOperator<T> generator) {
        return spliterator(MoreSpliterators.iterate(seed, requireNonNull(generator)));
    }

    /**
     * 从给定的列表中创建流水线。
     * <p/>
     * 如果给定列表为{@code null}则返回空流水线。
     *
     * @param list 列表。
     * @param <T> 列表中的元素类型。
     * @return 新的流水线。
     * @see #list(List, int)
     * @see #set(Set)
     * @see #collection(Collection)
     */
    static <T> Pipe<T> list(List<? extends T> list) {
        return list == null || list.isEmpty() ? empty() : spliterator(list.spliterator());
    }

    /**
     * 从给定的列表中创建流水线，支持额外的数据标记。
     * <p/>
     * 如果给定列表为{@code null}则返回空流水线。
     *
     * @param list 列表。
     * @param extraFlag 额外的数据标记。
     * @param <T> 列表中的元素类型。
     * @return 新的流水线。
     * @apiNote 此方法仅限于充分了解给定列表中元素分布时使用，其{@code extraFlag}参数会用于指导流水线优化，如果给定的标记有误
     * 可能误导流水线的优化操作，进而影响结果正确性，当不确定此标记是否设置正确时请优先使用{@link #list(List)}。
     * @see PipeFlag
     * @see #list(List)
     * @see #set(Set)
     * @see #collection(Collection)
     */
    static <T> Pipe<T> list(List<? extends T> list, int extraFlag) {
        return list == null || list.isEmpty() ? empty() : spliterator(list.spliterator(), extraFlag);
    }

    /**
     * 从给定的集合中创建流水线。
     * <p/>
     * 如果给定集合为{@code null}则返回空流水线。
     *
     * @param set 集合。
     * @param <T> 集合中的元素类型。
     * @return 新的流水线。
     * @see #set(Set, int)
     * @see #list(List)
     * @see #collection(Collection)
     */
    static <T> Pipe<T> set(Set<? extends T> set) {
        return set == null || set.isEmpty() ? empty() : spliterator(set.spliterator());
    }

    /**
     * 从给定的集合中创建流水线，支持额外的数据标记。
     * <p/>
     * 如果给定集合为{@code null}则返回空流水线。
     *
     * @param set 集合。
     * @param extraFlag 额外的数据标记。
     * @param <T> 集合中的元素类型。
     * @return 新的流水线。
     * @apiNote 此方法仅限于充分了解给定集合中元素分布时使用，其{@code extraFlag}参数会用于指导流水线优化，如果给定的标记有误
     * 可能误导流水线的优化操作，进而影响结果正确性，当不确定此标记是否设置正确时请优先使用{@link #set(Set)}。
     * @see PipeFlag
     * @see #set(Set)
     * @see #list(List)
     * @see #collection(Collection)
     */
    static <T> Pipe<T> set(Set<? extends T> set, int extraFlag) {
        return set == null || set.isEmpty() ? empty() : spliterator(set.spliterator(), extraFlag);
    }

    /**
     * 从给定的容器中创建流水线。
     * <p/>
     * 如果给定容器为{@code null}则返回空流水线。
     *
     * @param collection 容器。
     * @param <T> 容器中的元素类型。
     * @return 新的流水线。
     * @see #collection(Collection, int)
     * @see #list(List)
     * @see #set(Set)
     */
    static <T> Pipe<T> collection(Collection<? extends T> collection) {
        return collection == null || collection.isEmpty() ? empty() : spliterator(collection.spliterator());
    }

    /**
     * 从给定的容器中创建流水线，支持额外的数据标记。
     * <p/>
     * 如果给定容器为{@code null}则返回空流水线。
     *
     * @param collection 容器。
     * @param extraFlag 额外的数据标记。
     * @param <T> 容器中的元素类型。
     * @return 新的流水线。
     * @apiNote 此方法仅限于充分了解给定容器中元素分布时使用，其{@code extraFlag}参数会用于指导流水线优化，如果给定的标记有误
     * 可能误导流水线的优化操作，进而影响结果正确性，当不确定此标记是否设置正确时请优先使用{@link #collection(Collection)}。
     * @see PipeFlag
     * @see #collection(Collection)
     * @see #list(List)
     * @see #set(Set)
     */
    static <T> Pipe<T> collection(Collection<? extends T> collection, int extraFlag) {
        return collection == null || collection.isEmpty() ? empty() : spliterator(collection.spliterator(), extraFlag);
    }
}
