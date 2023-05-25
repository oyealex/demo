package com.oyealex.pipe.basis;

import com.oyealex.pipe.PipeTestFixture;
import com.oyealex.pipe.basis.api.Pipe;
import com.oyealex.pipe.basis.api.policy.MergeRemainingPolicy;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;

import java.util.List;
import java.util.function.BiFunction;

import static com.oyealex.pipe.basis.api.policy.MergeRemainingPolicy.DROP;
import static com.oyealex.pipe.basis.api.policy.MergeRemainingPolicy.MERGE_AS_NULL;
import static com.oyealex.pipe.basis.api.policy.MergeRemainingPolicy.TAKE_OURS;
import static com.oyealex.pipe.basis.api.policy.MergeRemainingPolicy.TAKE_REMAINING;
import static com.oyealex.pipe.basis.api.policy.MergeRemainingPolicy.TAKE_THEIRS;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.params.ParameterizedTest.ARGUMENTS_PLACEHOLDER;

/**
 * PipeMergeTest
 *
 * @author oyealex
 * @see Pipe#mergeAlternately(Pipe)
 * @see Pipe#mergeAlternately(Pipe, MergeRemainingPolicy)
 * @see Pipe#mergeAlternatelyTheirsFirst(Pipe)
 * @see Pipe#mergeAlternatelyTheirsFirst(Pipe, MergeRemainingPolicy)
 * @see Pipe#merge(Pipe, BiFunction, MergeRemainingPolicy)
 * @see Pipe#merge(Pipe, BiFunction, BiFunction, BiFunction, MergeRemainingPolicy)
 * @since 2023-05-24
 */
class PipeMergeTest extends PipeTestFixture {
    @Test
    @DisplayName("能够正确交替合并两个流水线")
    void should_merge_alternately_rightly() {
        List<String> res = infiniteEvenIntegerStrPipe().mergeAlternately(infiniteOddIntegerStrPipe()).limit(10).toList();
        assertEquals(infiniteIntegerStrPipe().limit(10).toList(), res);
    }

    @Test
    @DisplayName("当剩余数据合并策略为 MERGE_AS_NULL 时能够正确交替合并两个流水线，并将缺少的数据作为null参与合并")
    void should_merge_alternately_with_MERGE_AS_NULL_remaining_policy() {
        System.out.println(prefixedIntegerStrPipe("A").limit(2).mergeAlternately(prefixedIntegerStrPipe("B").limit(4), MERGE_AS_NULL).mapNull("_").println().limit(1).toList());
        System.out.println(prefixedIntegerStrPipe("A").limit(4).mergeAlternately(prefixedIntegerStrPipe("B").limit(2), MERGE_AS_NULL).mapNull("_").println().limit(1).toList());
        System.out.println();
        System.out.println(prefixedIntegerStrPipe("A").limit(2).mergeAlternately(prefixedIntegerStrPipe("B").limit(4), TAKE_OURS).mapNull("_").println().limit(1).toList());
        System.out.println(prefixedIntegerStrPipe("A").limit(4).mergeAlternately(prefixedIntegerStrPipe("B").limit(2), TAKE_OURS).mapNull("_").println().limit(1).toList());
        System.out.println();
        System.out.println(prefixedIntegerStrPipe("A").limit(2).mergeAlternately(prefixedIntegerStrPipe("B").limit(4), TAKE_THEIRS).mapNull("_").println().limit(1).toList());
        System.out.println(prefixedIntegerStrPipe("A").limit(4).mergeAlternately(prefixedIntegerStrPipe("B").limit(2), TAKE_THEIRS).mapNull("_").println().limit(1).toList());
        System.out.println();
        System.out.println(prefixedIntegerStrPipe("A").limit(2).mergeAlternately(prefixedIntegerStrPipe("B").limit(4), TAKE_REMAINING).mapNull("_").println().limit(1).toList());
        System.out.println(prefixedIntegerStrPipe("A").limit(4).mergeAlternately(prefixedIntegerStrPipe("B").limit(2), TAKE_REMAINING).mapNull("_").println().limit(1).toList());
        System.out.println();
        System.out.println(prefixedIntegerStrPipe("A").limit(2).mergeAlternately(prefixedIntegerStrPipe("B").limit(4), DROP).mapNull("_").println().limit(1).toList());
        System.out.println(prefixedIntegerStrPipe("A").limit(4).mergeAlternately(prefixedIntegerStrPipe("B").limit(2), DROP).mapNull("_").println().limit(1).toList());
    }

    @ParameterizedTest(name = "当剩余数据合并策略为" + ARGUMENTS_PLACEHOLDER + "时能够正确交替合并两个流水线")
    @EnumSource(MergeRemainingPolicy.class)
    @DisplayName("当存在剩余数据时能够正确交替合并两个流水线")
    void should_merge_alternately_rightly_when_(MergeRemainingPolicy remainingPolicy) {

    }

    @Test
    @DisplayName("能够正确交替合并两个流水线，另外的流水线优先")
    void should_merge_alternately_theirs_first_rightly() {
        List<String> res = infiniteOddIntegerStrPipe().mergeAlternatelyTheirsFirst(infiniteEvenIntegerStrPipe()).limit(10).toList();
        assertEquals(infiniteIntegerStrPipe().limit(10).toList(), res);
    }
}
