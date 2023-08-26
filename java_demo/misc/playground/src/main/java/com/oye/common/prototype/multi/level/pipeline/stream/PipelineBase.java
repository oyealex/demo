package com.oye.common.prototype.multi.level.pipeline.stream;

import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.Nullable;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.stream.Stream;

import static java.util.concurrent.CompletableFuture.allOf;
import static java.util.concurrent.CompletableFuture.runAsync;

/**
 * 基础的数据访问流水线，提供一些公用的能力复用
 *
 * @author oyealex
 * @since 2022-12-07
 */
@RequiredArgsConstructor
abstract class PipelineBase {
    /** 用于支持不同访问者之间并行访问同一份数据，如果置为null，则以串行方式访问 */
    @Nullable
    private final ExecutorService executor;

    /**
     * 以串行或并行的方式完成所有数据访问任务
     *
     * @param tasks 数据访问任务
     */
    protected final void finishAllVisitTask(Stream<Runnable> tasks) {
        if (executor == null) {
            tasks.forEach(Runnable::run);
        } else {
            allOf(tasks.map(task -> runAsync(task, executor)).toArray(CompletableFuture[]::new)).join();
        }
    }

    /**
     * 终结数据流
     *
     * @param stream 数据流
     */
    protected final void finishStream(Stream<?> stream) {
        stream.forEach(data -> {});
    }

    /**
     * 按照预定的方式完成所有数据访问
     */
    public abstract void visitAll();
}
