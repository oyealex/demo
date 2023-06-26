package com.oyealex.pipe.flag;

import com.oyealex.pipe.PipeTestFixture;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Comparator;
import java.util.Spliterator;
import java.util.Spliterators;
import java.util.function.Consumer;
import java.util.function.Function;

import static com.oyealex.pipe.flag.PipeFlag.IS_DISTINCT;
import static com.oyealex.pipe.flag.PipeFlag.IS_NONNULL;
import static com.oyealex.pipe.flag.PipeFlag.IS_REVERSED_SORTED;
import static com.oyealex.pipe.flag.PipeFlag.IS_SHORT_CIRCUIT;
import static com.oyealex.pipe.flag.PipeFlag.IS_SIZED;
import static com.oyealex.pipe.flag.PipeFlag.IS_SORTED;
import static com.oyealex.pipe.flag.PipeFlag.NOT_DISTINCT;
import static com.oyealex.pipe.flag.PipeFlag.NOT_SIZED;
import static com.oyealex.pipe.flag.PipeFlag.NOT_SORTED;
import static com.oyealex.pipe.flag.PipeFlag.combine;
import static com.oyealex.pipe.flag.PipeFlag.fromSpliterator;
import static com.oyealex.pipe.flag.PipeFlag.toReadablePipeFlag;
import static com.oyealex.pipe.flag.PipeFlag.toReadableSplitCharacteristics;
import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * 针对流水线标记的测试。
 *
 * @author oyealex
 * @see PipeFlag
 * @since 2023-05-05
 */
class PipeFlagTest extends PipeTestFixture {
    @Test
    @DisplayName("当设置标记时能够正确组合标记")
    void should_combine_flags_rightly_when_set_flag() {
        int flag = combine(IS_DISTINCT | IS_SORTED | NOT_SIZED, IS_SIZED);
        assertEquals(IS_DISTINCT | IS_SORTED | IS_SIZED, flag, generateFailedMsg(flag));
    }

    @Test
    @DisplayName("当取消标记时能够正确组合标记")
    void should_combine_flags_rightly_when_clear_flag() {
        int flag = combine(IS_DISTINCT | IS_SORTED | NOT_SIZED, NOT_DISTINCT);
        assertEquals(NOT_DISTINCT | IS_SORTED | NOT_SIZED, flag, generateFailedMsg(flag));
    }

    @Test
    @DisplayName("当添加设置标记时能够正确组合标记")
    void should_combine_flags_rightly_when_add_and_set_flag() {
        int flag = combine(IS_DISTINCT | IS_SORTED, IS_SIZED);
        assertEquals(IS_DISTINCT | IS_SORTED | IS_SIZED, flag, generateFailedMsg(flag));
    }

    @Test
    @DisplayName("当添加取消标记时能够正确组合标记")
    void should_combine_flags_rightly_when_add_and_clear_flag() {
        int flag = combine(IS_SORTED | NOT_SIZED, NOT_DISTINCT);
        assertEquals(NOT_DISTINCT | IS_SORTED | NOT_SIZED, flag, generateFailedMsg(flag));
    }

    @Test
    @DisplayName("当把原标记和空标记结合时得到原标记自身")
    void should_get_source_flag_self_when_combine_with_no_flag() {
        int flag = combine(IS_DISTINCT | IS_SORTED | NOT_SIZED, 0);
        assertEquals(IS_DISTINCT | IS_SORTED | NOT_SIZED, flag, generateFailedMsg(flag));
    }

    @Test
    @DisplayName("能够正确地将拆分器的特征值转换为流水线标记")
    void should_convert_split_characteristics_to_pipe_flag_rightly() {
        Spliterator<String> split = new Split(Spliterator.IMMUTABLE | Spliterator.DISTINCT | Spliterator.NONNULL,
            false);
        assertEquals(IS_DISTINCT | IS_NONNULL, fromSpliterator(split),
            generateFailedMsgFromSplit(split.characteristics()));
    }

    @Test
    @DisplayName("如果拆分器已排序并且返回了非空的比较器，则转换后的流水线标记应当标记为NOT_SORTED")
    void should_convert_split_character_to_flag_with_NOT_SORTED_when_split_is_ORDERED_and_return_non_null_comparator() {
        Spliterator<String> split = new Split(
            Spliterator.IMMUTABLE | Spliterator.DISTINCT | Spliterator.NONNULL | Spliterator.SORTED, false);
        assertEquals(IS_DISTINCT | IS_NONNULL | NOT_SORTED, fromSpliterator(split),
            generateFailedMsgFromSplit(split.characteristics()));
    }

    @Test
    @DisplayName("如果拆分器已排序并且返回了空的比较器，则转换后的流水线标记应当标记为IS_SORTED")
    void should_convert_split_character_to_flag_with_IS_SORTED_when_split_is_ORDERED_and_return_null_comparator() {
        Spliterator<String> split = new Split(
            Spliterator.CONCURRENT | Spliterator.DISTINCT | Spliterator.NONNULL | Spliterator.SORTED, true);
        assertEquals(IS_DISTINCT | IS_NONNULL | IS_SORTED, fromSpliterator(split),
            generateFailedMsgFromSplit(split.characteristics()));
    }

    private static class Split extends Spliterators.AbstractSpliterator<String> {
        private final boolean isNaturalSorted;

        Split(int additionalCharacteristics, boolean isNaturalSorted) {
            super(0, additionalCharacteristics);
            this.isNaturalSorted = isNaturalSorted;
        }

        @Override
        public boolean tryAdvance(Consumer<? super String> action) {
            return false;
        }

        @Override
        public Comparator<? super String> getComparator() {
            if ((Spliterator.SORTED & characteristics()) == Spliterator.SORTED) {
                return isNaturalSorted ? null : Comparator.comparing(Function.identity());
            }
            throw new IllegalStateException();
        }
    }

    @Test
    void smoke() {
        int all = IS_DISTINCT | IS_SORTED | IS_SIZED | IS_NONNULL | IS_SHORT_CIRCUIT | IS_REVERSED_SORTED;
        System.out.println(Integer.toBinaryString(all));
        System.out.println(Integer.toBinaryString(all << 16 | all));
        System.out.println(Integer.toBinaryString(Integer.MAX_VALUE));
        System.out.println(Long.toBinaryString(Long.MAX_VALUE));
    }

    private String generateFailedMsg(int flag) {
        return "actual pipe flags are: " + toReadablePipeFlag(flag);
    }

    private String generateFailedMsgFromSplit(int characteristics) {
        return "actual spliterator characteristics are: " + toReadableSplitCharacteristics(characteristics);
    }
}