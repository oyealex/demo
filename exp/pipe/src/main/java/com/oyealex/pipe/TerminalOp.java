package com.oyealex.pipe;

import java.util.function.Supplier;

/**
 * TerminalOp
 *
 * @author oyealex
 * @since 2023-04-27
 */
interface TerminalOp<IN, R> extends Op<IN>, Supplier<R> {}
