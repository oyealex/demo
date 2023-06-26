package com.oyealex.pipe.basis;

import com.oyealex.pipe.BasePipe;
import com.oyealex.pipe.flag.PipeFlag;

import java.util.Spliterator;

import static com.oyealex.pipe.flag.PipeFlag.SHORT_CIRCUIT;

/**
 * AbstractRefPipe
 *
 * @author oyealex
 * @since 2023-06-24
 */
abstract class AbstractRefPipe<IN, OUT, P extends BasePipe<OUT, P>, PP extends AbstractRefPipe<IN, OUT, P, PP>>
    implements BasePipe<OUT, P> {
    /** 整条流水线的头节点，元素类型未知，非{@code null}。 */
    protected final PP headPipe;

    /** 此节点的前置节点，当且仅当此节点为头节点时为{@code null}。 */
    protected final PP prePipe;

    final int flag;

    AbstractRefPipe(int flag) {
        this.headPipe = (PP) this;
        this.prePipe = null;
        this.flag = flag;
    }

    /**
     * 以给定的流水线为上游，构造一个新的流水线。
     *
     * @param prePipe 上游流水线
     * @param opFlag 操作标记
     */
    AbstractRefPipe(PP prePipe, int opFlag) {
        this.headPipe = prePipe.headPipe;
        this.prePipe = prePipe;
        this.flag = combineFlag(prePipe, opFlag);
    }

    protected int combineFlag(PP prePipe, int opFlag) {
        return PipeFlag.combine(prePipe.flag, opFlag);
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
    protected abstract Op<IN> wrapOp(Op<OUT> nextOp);

    @SuppressWarnings("unchecked")
    private <R> R evaluate(TerminalOp<OUT, R> terminalOp) {
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
    <OP extends TerminalOp<OUT, ?>> void driveData(Spliterator<Object> dataSource, OP tailOp) {
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
    Op<Object> wrapAllOp(Op<OUT> tailOp) {
        Op<?> wrappedOp = tailOp;
        for (PP pipe = (PP) this; pipe.prePipe != null; pipe = pipe.prePipe) {
            // 从尾部到头部，逐级逆向封装
            wrappedOp = pipe.wrapOp((Op<OUT>) wrappedOp);
        }
        return (Op<Object>) wrappedOp;
    }
}
