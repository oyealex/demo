package com.oyealex.pipe.bi;

import com.oyealex.pipe.assist.Tuple;
import com.oyealex.pipe.base.BasePipe;

/**
 * 支持两元组的流水线
 *
 * @param <F> 第一个元素类型
 * @param <S> 第二个元素类型
 * @author oyealex
 * @since 2023-03-04
 */
public interface BiPipe<F, S> extends BasePipe<Tuple<F, S>, BiPipe<F, S>> {
}
