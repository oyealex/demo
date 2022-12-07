package com.oye.common.prototype.multi.level.pipeline.stream;

import org.jetbrains.annotations.Nullable;

import java.util.Collection;
import java.util.concurrent.ExecutorService;
import java.util.stream.Stream;

/**
 * 支持单个维度的数据访问流水线
 *
 * @param <T1> 数据类型
 * @author oyealex
 * @since 2022-12-07
 */
public class L1Pipeline<T1> extends PipelineBase {
    private final Collection<? extends Visitor<T1>> visitors;

    private final StreamPump<T1> streamPump;

    public L1Pipeline(@Nullable ExecutorService executor, Collection<? extends Visitor<T1>> visitors,
        StreamPump<T1> streamPump) {
        super(executor);
        this.visitors = visitors;
        this.streamPump = streamPump;
    }

    @Override
    public void visitAll() {
        finishStream(visitedStream());
    }

    /**
     * 获取已经访问第一个维度数据的流
     *
     * @return 已经访问的数据流
     */
    Stream<T1> visitedStream() {
        return streamPump.pump().peek(this::visitorsVisitData);
    }

    private void visitorsVisitData(T1 data) {
        finishAllVisitTask(visitors.stream().map(visitor -> () -> visitor.visit(data)));
    }

    /**
     * 访问单维度数据的接口
     *
     * @param <T1> 数据类型
     */
    public interface Visitor<T1> {
        /**
         * 访问单维度数据
         *
         * @param data 数据
         */
        default void visit(T1 data) {}
    }

    /**
     * 数据流创建接口
     *
     * @param <T1> 数据类型
     */
    public interface StreamPump<T1> {
        /**
         * 创建一个新的数据流
         *
         * @return 数据流
         */
        Stream<T1> pump();
    }
}
