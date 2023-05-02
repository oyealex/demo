package com.oyealex.pipe.basis.op;

import java.util.function.Supplier;

/**
 * 终结操作，定义流水线地最终操作，并能够获取流水线的最终返回值。
 *
 * @author oyealex
 * @since 2023-04-27
 */
public interface TerminalOp<IN, R> extends Op<IN>, Supplier<R> {}
