package com.oyealex.pipe.flag;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static com.oyealex.pipe.flag.PipeFlag.IS_DISTINCT;
import static com.oyealex.pipe.flag.PipeFlag.IS_SIZED;
import static com.oyealex.pipe.flag.PipeFlag.IS_SORTED;
import static com.oyealex.pipe.flag.PipeFlag.NOT_DISTINCT;
import static com.oyealex.pipe.flag.PipeFlag.NOT_SIZED;
import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * PipeFlagTest
 *
 * @author oyealex
 * @since 2023-05-05
 */
class PipeFlagTest {
    @Test
    @DisplayName("当设置标记时能够正确组合标记")
    void should_combine_flags_rightly_when_set_flag() {
        int flat = PipeFlag.combine(IS_DISTINCT | IS_SORTED | NOT_SIZED, IS_SIZED);
        assertEquals(IS_DISTINCT | IS_SORTED | IS_SIZED, flat);
    }

    @Test
    @DisplayName("当取消标记时能够正确组合标记")
    void should_combine_flags_rightly_when_clear_flag() {
        int flat = PipeFlag.combine(IS_DISTINCT | IS_SORTED | NOT_SIZED, NOT_DISTINCT);
        assertEquals(NOT_DISTINCT | IS_SORTED | NOT_SIZED, flat);
    }

    @Test
    @DisplayName("当添加设置标记时能够正确组合标记")
    void should_combine_flags_rightly_when_add_and_set_flag() {
        int flat = PipeFlag.combine(IS_DISTINCT | IS_SORTED, IS_SIZED);
        assertEquals(IS_DISTINCT | IS_SORTED | IS_SIZED, flat);
    }

    @Test
    @DisplayName("当添加取消标记时能够正确组合标记")
    void should_combine_flags_rightly_when_add_and_clear_flag() {
        int flat = PipeFlag.combine(IS_SORTED | NOT_SIZED, NOT_DISTINCT);
        assertEquals(NOT_DISTINCT | IS_SORTED | NOT_SIZED, flat);
    }

    @Test
    @DisplayName("当把原标记和空标记结合时得到原标记自身")
    void should_get_source_flag_self_when_combine_with_no_flag() {
        int flat = PipeFlag.combine(IS_DISTINCT | IS_SORTED | NOT_SIZED, 0);
        assertEquals(IS_DISTINCT | IS_SORTED | NOT_SIZED, flat);
    }
}