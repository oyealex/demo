package com.oye.common.prototype.multi.level.pipeline.stream;

import org.jetbrains.annotations.Nullable;

import java.util.Collection;
import java.util.concurrent.ExecutorService;
import java.util.stream.Stream;

/**
 * 支持双维度的数据访问流水线
 * <p/>
 * 以DFS方式访问两个维度的数据流，不同的{@link Visitor}之间支持并行或穿行访问
 *
 * @param <T1> 第一个维度的数据类型
 * @param <T2> 第二个维度的数据类型
 * @author oyealex
 * @since 2022-12-07
 */
public class L2Pipeline<T1, T2> extends PipelineBase {
    private final L1Pipeline<T1> prePipeline;

    private final Collection<? extends Visitor<T1, T2>> visitors;

    private final StreamPump<T1, T2> streamStarter;

    public L2Pipeline(@Nullable ExecutorService executor, Collection<? extends Visitor<T1, T2>> visitors,
        StreamPump<T1, T2> streamStarter) {
        super(executor);
        this.visitors = visitors;
        this.prePipeline = new L1Pipeline<>(executor, visitors, streamStarter);
        this.streamStarter = streamStarter;
    }

    @Override
    public void visitAll() {
        visitedStream().map(DataStream::getStream).forEach(super::finishStream);
    }

    /**
     * 获取两个维度都已访问的数据流
     *
     * @return 两个维度都已访问的数据流
     */
    Stream<DataStream<T1, T2>> visitedStream() {
        return prePipeline.visitedStream().map(this::visitedStream);
    }

    private DataStream<T1, T2> visitedStream(T1 data1) {
        return new DataStream<>(data1, streamStarter.start(data1).peek(data2 -> visitorsVisitData(data1, data2)));
    }

    private void visitorsVisitData(T1 data1, T2 data2) {
        finishAllVisitTask(visitors.stream().map(visitor -> () -> visitor.visit(data1, data2)));
    }

    /**
     * 双维度数据访问接口，兼容单维度数据访问接口
     *
     * @param <T1> 第一个维度的数据类型
     * @param <T2> 第二个维度的数据类型
     */
    public interface Visitor<T1, T2> extends L1Pipeline.Visitor<T1> {
        /**
         * 访问双维度数据
         *
         * @param data1 第一个维度的数据
         * @param data2 第二个维度的数据
         */
        default void visit(T1 data1, T2 data2) {}
    }

    /**
     * 双维度数据流创建接口
     *
     * @param <T1> 第一个维度的数据类型
     * @param <T2> 第二个维度的数据类型
     */
    public interface StreamPump<T1, T2> extends L1Pipeline.StreamPump<T1> {
        /**
         * 创建一个新的第二维度数据流
         *
         * @param data1 第一个维度的数据
         * @return 第二维度的数据流
         */
        Stream<T2> start(T1 data1);
    }
}
