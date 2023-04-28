package com.oyealex.pipe.basis.op;

import java.util.function.Supplier;

/**
 * TerminalOp
 *
 * @author oyealex
 * @since 2023-04-27
 */
public interface TerminalOp<IN, R> extends Op<IN>, Supplier<R> {}
