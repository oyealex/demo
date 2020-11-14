/*
 * Copyright (c) 2021. oyealex. All rights reserved.
 */

package com.oyealex.debug.time.recorder;

import lombok.Getter;
import lombok.ToString;

/** 时间戳记录，包含时间戳、记录名称和距离上个时间戳经过的时间 */
@Getter
@ToString
class StampRecord {
    /** 开始记录名称 */
    static final String START_NAME = "!!START!!";

    /** 结束记录名称 */
    static final String STOP_NAME = "!!STOP!!";

    /** 匿名记录名称 */
    static final String ANONYMOUS_NAME = "!!ANONYMOUS!!";

    /** 记录名称 */
    private final String name;

    /** 记录纳秒时间戳 */
    private final long stamp;

    /** 距离上一个记录经过的纳秒时间 */
    private final long elapsed;

    /**
     * 以给定名称和当前时间戳创建一个时间戳记录，经过的纳秒时间为0
     *
     * @param name 记录名称
     */
    public StampRecord(String name) {
        this.name = name;
        this.stamp = System.nanoTime();
        this.elapsed = 0L;
    }

    /**
     * 以给定的名称和当前时间创建一个时间戳记录，经过的纳秒时间为当前时间到给定前置记录的时间戳之间的时间
     *
     * @param name      记录时间
     * @param preRecord 前置时间戳
     */
    public StampRecord(String name, StampRecord preRecord) {
        this.name = name;
        this.stamp = System.nanoTime();
        this.elapsed = this.stamp - preRecord.stamp;
    }

    /**
     * 判断是否是命名记录，即 非匿名记录
     *
     * @return 是否是命名记录
     */
    public boolean isNamedRecord() {
        return !ANONYMOUS_NAME.equals(name);
    }
}
