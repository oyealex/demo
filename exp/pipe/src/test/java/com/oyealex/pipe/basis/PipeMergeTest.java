package com.oyealex.pipe.basis;

import com.oyealex.pipe.PipeTestBase;
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
class PipeMergeTest extends PipeTestBase {
    @Test
    @DisplayName("能够正确交替合并两个流水线")
    void should_merge_alternately_rightly() {
        List<String> res = evenStrPipe().mergeAlternately(oddStrPipe()).limit(10).toList();
        assertEquals(seqStrPipe().limit(10).toList(), res);
    }

    @Test
    @DisplayName("当剩余数据合并策略为 MERGE_AS_NULL 时能够正确交替合并两个流水线，并将缺少的数据作为null参与合并")
    void should_merge_alternately_with_MERGE_AS_NULL_remaining_policy() {
        System.out.println(evenStrPipe().limit(2).mergeAlternately(oddStrPipe().limit(4), MERGE_AS_NULL).mapNull("_").toList());
        System.out.println(evenStrPipe().limit(4).mergeAlternately(oddStrPipe().limit(2), MERGE_AS_NULL).mapNull("_").toList());
        System.out.println();
        System.out.println(evenStrPipe().limit(2).mergeAlternately(oddStrPipe().limit(4), TAKE_OURS).mapNull("_").toList());
        System.out.println(evenStrPipe().limit(4).mergeAlternately(oddStrPipe().limit(2), TAKE_OURS).mapNull("_").toList());
        System.out.println();
        System.out.println(evenStrPipe().limit(2).mergeAlternately(oddStrPipe().limit(4), TAKE_THEIRS).mapNull("_").toList());
        System.out.println(evenStrPipe().limit(4).mergeAlternately(oddStrPipe().limit(2), TAKE_THEIRS).mapNull("_").toList());
        System.out.println();
        System.out.println(evenStrPipe().limit(2).mergeAlternately(oddStrPipe().limit(4), TAKE_REMAINING).mapNull("_").toList());
        System.out.println(evenStrPipe().limit(4).mergeAlternately(oddStrPipe().limit(2), TAKE_REMAINING).mapNull("_").toList());
        System.out.println();
        System.out.println(evenStrPipe().limit(2).mergeAlternately(oddStrPipe().limit(4), DROP).mapNull("_").toList());
        System.out.println(evenStrPipe().limit(4).mergeAlternately(oddStrPipe().limit(2), DROP).mapNull("_").toList());
    }

    @ParameterizedTest(name = "当剩余数据合并策略为" + ARGUMENTS_PLACEHOLDER + "时能够正确交替合并两个流水线")
    @EnumSource(MergeRemainingPolicy.class)
    @DisplayName("当存在剩余数据时能够正确交替合并两个流水线")
    void should_merge_alternately_rightly_when_(MergeRemainingPolicy remainingPolicy) {

    }

    @Test
    @DisplayName("能够正确交替合并两个流水线，另外的流水线优先")
    void should_merge_alternately_theirs_first_rightly() {
        List<String> res = oddStrPipe().mergeAlternatelyTheirsFirst(evenStrPipe()).limit(10).toList();
        assertEquals(seqStrPipe().limit(10).toList(), res);
    }
}
