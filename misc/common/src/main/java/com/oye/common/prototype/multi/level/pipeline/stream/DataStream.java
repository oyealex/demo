package com.oye.common.prototype.multi.level.pipeline.stream;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.stream.Stream;

/**
 * 数据和次级数据流的组合
 *
 * @param <D> 数据类型
 * @param <S> 次级数据流的数据类型
 * @author oyealex
 * @since 2022-12-07
 */
@Getter
@RequiredArgsConstructor(access = AccessLevel.PACKAGE)
class DataStream<D, S> {
    private final D data;

    private final Stream<S> stream;
}
