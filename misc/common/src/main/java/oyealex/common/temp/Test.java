/*
 * Copyright (c) 2020. oyealex. All rights reserved.
 */

package oyealex.common.temp;

import com.oyealex.thread.Threads;
import lombok.extern.slf4j.Slf4j;

/**
 * 临时测试类
 *
 * @author oyealex
 * @version 1.0
 * @since 2020/10/19
 */
@Slf4j
public class Test {
    public static void main(String[] args) {
        Threads.sleepUninterruptedly(1000L);
    }
}
