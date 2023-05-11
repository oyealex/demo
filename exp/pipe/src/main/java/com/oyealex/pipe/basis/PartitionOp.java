package com.oyealex.pipe.basis;

import com.oyealex.pipe.basis.api.Pipe;

import java.util.ArrayList;
import java.util.List;

/**
 * PartitionOp
 *
 * @author oyealex
 * @since 2023-05-03
 */
@Deprecated
class PartitionOp<IN> extends ChainedOp<IN, Pipe<IN>> {
    private final int size;

    private List<IN> partition;

    PartitionOp(Op<Pipe<IN>> op, int size) {
        super(op);
        this.size = size;
    }

    @Override
    public void accept(IN in) {
        if (partition == null) {
            partition = new ArrayList<>(size);
        }
        partition.add(in);
        tryPartition();
    }

    private void tryPartition() {
        if (partition.size() >= size) {
            nextOp.accept(Pipes.from(partition));
            partition = null;
        }
    }

    @Override
    public void end() {
        if (partition != null) {
            tryPartition();
        }
        nextOp.end();
    }
}
