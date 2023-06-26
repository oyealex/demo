package com.oyealex.pipe.basis;

import com.oyealex.pipe.assist.Tuple;
import com.oyealex.pipe.flag.PipeFlag;

import java.util.Iterator;
import java.util.Spliterator;

import static com.oyealex.pipe.flag.PipeFlag.SHORT_CIRCUIT;

/**
 * RefBiPipe
 *
 * @author oyealex
 * @since 2023-06-24
 */
abstract class RefBiPipe<FI, SI, FO, SO> implements BiPipe<FO, SO> {
    /** 整条流水线的头节点，元素类型未知，非{@code null}。 */
    private final RefBiPipe<?, ?, ?, ?> headPipe;

    /** 此节点的前置节点，当且仅当此节点为头节点时为{@code null}。 */
    private final RefBiPipe<?, ?, ? extends FI, ? extends SI> prePipe;

    /** 流水线标记 */
    final int flag; // MK 2023-05-12 23:04 final标记很重要，如果后续开发移除final，则需要重新审视所有使用flag字段的地方

    RefBiPipe(int flag) {
        this.headPipe = this;
        this.prePipe = null;
        this.flag = flag;
    }

    /**
     * 以给定的流水线为上游，构造一个新的流水线。
     *
     * @param prePipe 上游流水线
     * @param opFlag 操作标记
     */
    RefBiPipe(RefBiPipe<?, ?, ? extends FI, ? extends SI> prePipe, int opFlag) {
        this.headPipe = prePipe.headPipe;
        this.prePipe = prePipe;
        this.flag = PipeFlag.combine(prePipe.flag, opFlag);
    }

    /**
     * 获取流水线的数据源，此数据源来自头节点。
     *
     * @return 流水线的数据源
     * @apiNote 此方法仅允许调用一次。
     * @implNote 仅头节点可以重写此方法。
     */
    protected Spliterator<?> takeDataSource() {
        return headPipe.takeDataSource();
    }

    /**
     * 将当前节点的操作和下游节点的操作封装为一个操作，此操作接受的元素为上游节点的输出元素。
     *
     * @param nextOp 下游节点的操作
     * @return 封装之后的操作
     */
    protected abstract Op<Tuple<FI, SI>> wrapOp(Op<Tuple<FO, SO>> nextOp);

    @SuppressWarnings("unchecked")
    private <R> R evaluate(TerminalOp<Tuple<FO, SO>, R> terminalOp) {
        driveData((Spliterator<Object>) headPipe.takeDataSource(), terminalOp);
        return terminalOp.get();
    }

    /**
     * 以给定的数据源{@code dataSource}驱动执行当前流水线中定义的所有元素操作，并以给定的{@code tailOp}作为最终的结尾操作。
     *
     * @param dataSource 数据源
     * @param tailOp 结尾操作
     * @param <OP> 结尾操作类型
     */
    <OP extends TerminalOp<Tuple<FO, SO>, ?>> void driveData(Spliterator<Object> dataSource, OP tailOp) {
        Op<Object> wrappedOp = wrapAllOp(tailOp);
        wrappedOp.begin(dataSource.getExactSizeIfKnown());
        if (SHORT_CIRCUIT.isSet(flag | tailOp.getOpFlag())) {
            // 如果允许短路，则尝试短路遍历
            do {/*noop*/} while (!wrappedOp.canShortCircuit() && dataSource.tryAdvance(wrappedOp));
        } else {
            // 否则直接执行全量遍历
            dataSource.forEachRemaining(wrappedOp);
        }
        wrappedOp.end();
    }

    /**
     * 以给定的操作作为流水线尾部操作，将整条流水线的所有节点的操作按顺序封装为一个操作。
     *
     * @param tailOp 流水线尾部操作
     * @return 封装了所有流水线节点操作的操作方法
     */
    @SuppressWarnings("unchecked")
    Op<Object> wrapAllOp(Op<Tuple<FO, SO>> tailOp) {
        Op<?> wrappedOp = tailOp;
        for (@SuppressWarnings("rawtypes") RefBiPipe pipe = this; pipe.prePipe != null; pipe = pipe.prePipe) {
            // 从尾部到头部，逐级逆向封装
            wrappedOp = pipe.wrapOp(wrappedOp);
        }
        return (Op<Object>) wrappedOp;
    }

    protected boolean isFlagSet(PipeFlag pipeFlag) {
        return pipeFlag.isSet(flag);
    }

    @Override
    public Spliterator<Tuple<FO, SO>> toSpliterator() {
        return null;
    }

    @Override
    public Iterator<Tuple<FO, SO>> toIterator() {
        return null;
    }

    @Override
    public BiPipe<FO, SO> onClose(Runnable closeAction) {
        return null;
    }

    @Override
    public void close() {

    }
}
