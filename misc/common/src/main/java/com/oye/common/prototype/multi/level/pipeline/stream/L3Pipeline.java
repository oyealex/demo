package com.oye.common.prototype.multi.level.pipeline.stream;

import org.jetbrains.annotations.Nullable;

import java.util.Collection;
import java.util.concurrent.ExecutorService;
import java.util.stream.Stream;

/**
 * 支持三维度的数据访问流水线
 * <p/>
 * 以DFS方式访问三个维度的数据流，不同的{@link Visitor}之间支持并行或穿行访问
 *
 * @param <T1> 第一个维度的数据类型
 * @param <T2> 第二个维度的数据类型
 * @param <T3> 第三个维度的数据类型
 * @author oyealex
 * @since 2022-12-07
 */
public class L3Pipeline<T1, T2, T3> extends PipelineBase {
    private final L2Pipeline<T1, T2> prePipeline;

    private final Collection<? extends Visitor<T1, T2, T3>> visitors;

    private final StreamPump<T1, T2, T3> streamStarter;

    public L3Pipeline(@Nullable ExecutorService executor, Collection<? extends Visitor<T1, T2, T3>> visitors,
        StreamPump<T1, T2, T3> streamStarter) {
        super(executor);
        this.prePipeline = new L2Pipeline<>(executor, visitors, streamStarter);
        this.visitors = visitors;
        this.streamStarter = streamStarter;
    }

    @Override
    public void visitAll() {
        visitedStream().map(data2Stream -> data2Stream.getStream().map(DataStream::getStream))
            .forEach(data3Stream -> data3Stream.forEach(super::finishStream));
    }

    /**
     * 获取三个维度都已访问的数据流
     *
     * @return 三个维度都已访问的数据流
     */
    Stream<DataStream<T1, DataStream<T2, T3>>> visitedStream() {
        return prePipeline.visitedStream().map(this::visitedStream);
    }

    private DataStream<T1, DataStream<T2, T3>> visitedStream(DataStream<T1, T2> dataStream) {
        T1 data1 = dataStream.getData();
        return new DataStream<>(data1, dataStream.getStream().map(data2 -> new DataStream<>(data2,
            streamStarter.start(data1, data2).peek(data3 -> visitorsVisitData(data1, data2, data3)))));
    }

    private void visitorsVisitData(T1 data1, T2 data2, T3 data3) {
        finishAllVisitTask(visitors.stream().map(visitor -> () -> visitor.visit(data1, data2, data3)));
    }

    /**
     * 三维度数据访问接口，兼容一、二维度的数据访问
     *
     * @param <T1> 第一个维度的数据类型
     * @param <T2> 第二个维度的数据类型
     * @param <T3> 第三个维度的数据类型
     */
    public interface Visitor<T1, T2, T3> extends L2Pipeline.Visitor<T1, T2> {
        /**
         * 访问三维度数据
         *
         * @param data1 第一个维度的数据
         * @param data2 第二个维度的数据
         * @param data3 第三个维度的数据
         */
        default void visit(T1 data1, T2 data2, T3 data3) {}
    }

    /**
     * 三维度数据创建接口
     *
     * @param <T1> 第一个维度的数据类型
     * @param <T2> 第二个维度的数据类型
     * @param <T3> 第三个维度的数据类型
     */
    public interface StreamPump<T1, T2, T3> extends L2Pipeline.StreamPump<T1, T2> {
        /**
         * 创建一个新的第三维度数据流
         *
         * @param data1 第一个维度的数据
         * @param data2 第二个维度的数据
         * @return 第三维度的数据流
         */
        Stream<T3> start(T1 data1, T2 data2);
    }
}
