/*
 * Copyright (c) 2021. oyealex. All rights reserved.
 */

package com.oye.common.leetcode;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.HashSet;
import java.util.Set;

/**
 * UnionFindSet
 *
 * @author oyealex
 * @version 1.0
 * @since 2021-07-18
 */
public class UnionFindSet extends LeetcodeBase {
    // https://leetcode-cn.com/problems/longest-consecutive-sequence/
    @Test
    public void no_128_longest_consecutive_sequence() {
        Assertions.assertEquals(4, longestConsecutive_set(new int[]{100, 4, 200, 1, 3, 2}));
        Assertions.assertEquals(9, longestConsecutive_set(new int[]{0, 3, 7, 2, 5, 8, 4, 6, 0, 1}));
    }

    /*
     * 使用集合来优化查询，对于每个数字，如果存在更小的数字，才开始自增扫描，同时记录最大深度
     * 由于只从最小的数字开始扫描，因此复杂度降低到线性
     */
    int longestConsecutive_set(int[] nums) {
        if (nums.length == 0) {
            return 0;
        }
        Set<Integer> set = new HashSet<>();
        for (int num : nums) {
            set.add(num);
        }

        int longest = 1;
        for (int num : nums) {
            if (!set.contains(num - 1)) {
                int currentLength = 1;
                while (set.contains(++num)) {
                    currentLength++;
                    longest = Math.max(longest, currentLength);
                }
            }
        }
        return longest;
    }

    int longestConsecutive_ufs(int[] nums) {
        return -1;
    }
}
