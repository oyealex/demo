package com.oyealex.pipe.basis;

import com.oyealex.pipe.basis.api.policy.MergePolicy;
import com.oyealex.pipe.basis.api.policy.MergeRemainingPolicy;

import java.util.Spliterator;
import java.util.function.BiFunction;

import static com.oyealex.pipe.basis.api.policy.MergePolicy.DROP_THEIRS;
import static com.oyealex.pipe.basis.api.policy.MergePolicy.OURS_FIRST;
import static com.oyealex.pipe.basis.api.policy.MergePolicy.PREFER_OURS;
import static com.oyealex.pipe.basis.api.policy.MergePolicy.PREFER_THEIRS;
import static com.oyealex.pipe.basis.api.policy.MergePolicy.TAKE_OURS;
import static com.oyealex.pipe.basis.api.policy.MergePolicy.TAKE_THEIRS;
import static com.oyealex.pipe.basis.api.policy.MergePolicy.THEIRS_FIRST;
import static java.util.Objects.requireNonNull;

/**
 * MergeOp
 *
 * @author oyealex
 * @since 2023-05-17
 */
class MergeOp<OURS, THEIRS, RESULT> extends ChainedOp.ShortCircuitRecorded<OURS, RESULT> {
    private final BiFunction<? super OURS, ? super THEIRS, MergePolicy> mergeHandle;

    private final BiFunction<? super OURS, MergePolicy, ? extends RESULT> oursMapper;

    private final BiFunction<? super THEIRS, MergePolicy, ? extends RESULT> theirsMapper;

    private final MergeRemainingPolicy remainingPolicy;

    private Spliterator<? extends THEIRS> split;

    private THEIRS theirs;

    private boolean theirsReady = false;

    private static final int THEIRS_STATUS = 0x11;

    private static final int THEIRS_MERGED = 0x01;

    private static final int THEIRS_DROPPED = 0x00;

    private static final int THEIRS_REMAINED = 0x10;

    private static final int OURS_STATUS = THEIRS_STATUS << 2;

    private static final int OURS_MERGED = THEIRS_MERGED << 2;

    private static final int OURS_DROPPED = THEIRS_DROPPED << 2;

    private static final int OURS_REMAINED = THEIRS_REMAINED << 2;

    MergeOp(Op<? super RESULT> nextOp, Spliterator<? extends THEIRS> split,
        BiFunction<? super OURS, ? super THEIRS, MergePolicy> mergeHandle,
        BiFunction<? super OURS, MergePolicy, ? extends RESULT> oursMapper,
        BiFunction<? super THEIRS, MergePolicy, ? extends RESULT> theirsMapper, MergeRemainingPolicy remainingPolicy) {
        super(nextOp);
        this.mergeHandle = mergeHandle;
        this.oursMapper = oursMapper;
        this.theirsMapper = theirsMapper;
        this.remainingPolicy = remainingPolicy;
        this.split = split;
    }

    private void takeNext(THEIRS value) {
        theirs = value;
        theirsReady = true;
    }

    @Override
    public void begin(long size) {
        nextOp.begin(-1);
    }

    @Override
    public void accept(OURS ours) {
        for (int mergeStatus = OURS_REMAINED | THEIRS_REMAINED; (mergeStatus & OURS_STATUS) == OURS_REMAINED; ) {
            mergeStatus = prepareTheirs() ? mergePair(ours, theirs) : mergeRemainingOurs(ours);
        }
    }

    @Override
    public void end() {
        for (int mergeStatus = OURS_REMAINED | THEIRS_REMAINED; (mergeStatus & THEIRS_STATUS) == THEIRS_REMAINED; ) {
            if (prepareTheirs()) {

            } else {
                
            }
        }
        split = null;
        nextOp.end();
    }

    private boolean prepareTheirs() {
        if (!theirsReady) {
            takeTheirsNext();
        }
        return theirsReady;
    }

    private int mergePair(OURS ours, THEIRS theirs) {
        switch (requireNonNull(mergeHandle.apply(ours, theirs))) {
            case TAKE_OURS:
                nextOp.accept(oursMapper.apply(ours, TAKE_OURS));
                dropTheirs();
                return OURS_MERGED | THEIRS_DROPPED;
            case TAKE_THEIRS:
                nextOp.accept(theirsMapper.apply(theirs, TAKE_THEIRS));
                dropTheirs();
                return OURS_DROPPED | THEIRS_MERGED;
            case PREFER_OURS:
                nextOp.accept(oursMapper.apply(ours, PREFER_OURS));
                return OURS_MERGED | THEIRS_REMAINED;
            case PREFER_THEIRS:
                nextOp.accept(theirsMapper.apply(theirs, PREFER_THEIRS));
                dropTheirs();
                return OURS_REMAINED | THEIRS_MERGED;
            case OURS_FIRST:
                nextOp.accept(oursMapper.apply(ours, OURS_FIRST));
                nextOp.accept(theirsMapper.apply(theirs, OURS_FIRST));
                dropTheirs();
                return OURS_MERGED | THEIRS_MERGED;
            case THEIRS_FIRST:
                nextOp.accept(theirsMapper.apply(theirs, THEIRS_FIRST));
                nextOp.accept(oursMapper.apply(ours, THEIRS_FIRST));
                dropTheirs();
                return OURS_MERGED | THEIRS_MERGED;
            case DROP_OURS:
                return OURS_DROPPED | THEIRS_REMAINED;
            case DROP_THEIRS:
                dropTheirs();
                return OURS_REMAINED | THEIRS_DROPPED;
            case DROP_BOTH:
                dropTheirs();
                return OURS_DROPPED | THEIRS_DROPPED;
            default:
                return OURS_REMAINED | THEIRS_REMAINED;
        }
    }

    private int mergeRemainingOurs(OURS ours) {
        switch (remainingPolicy) {
            case MERGE_AS_NULL:
                theirs = null;
                theirsReady = true;
                return OURS_REMAINED | THEIRS_REMAINED;
            case TAKE_REMAINING:
            case TAKE_OURS:
                nextOp.accept(oursMapper.apply(ours, TAKE_OURS));
                return OURS_MERGED | THEIRS_DROPPED;
            case TAKE_THEIRS:
            case DROP:
                theirsReady = false;
                return OURS_DROPPED | THEIRS_DROPPED;
            default:
                return OURS_REMAINED | THEIRS_REMAINED;
        }
    }

    private int mergeRemainingTheirs() {
        switch (remainingPolicy) {
            case MERGE_AS_NULL:
                return mergePair(null, theirs);
            case TAKE_REMAINING:
            case TAKE_THEIRS:
                nextOp.accept(theirsMapper.apply(theirs, TAKE_THEIRS));
                dropTheirs();
                return OURS_DROPPED | THEIRS_MERGED;
            case TAKE_OURS:
            case DROP:
                theirsReady = false;
                return OURS_DROPPED | THEIRS_DROPPED;
            default:
                return OURS_REMAINED | THEIRS_REMAINED;
        }
    }

    private void merge(OURS ours) {
        for (MergePolicy policy = requireNonNull(mergeHandle.apply(ours, theirs));
             policy != null && !shouldShortCircuit(); policy = merge(ours, policy)) {
            // noop
        }
    }

    private MergePolicy merge(OURS ours, MergePolicy policy) {
        switch (policy) {
            case TAKE_OURS:
                nextOp.accept(oursMapper.apply(ours, policy));
                dropTheirs();
                break;
            case TAKE_THEIRS:
                nextOp.accept(theirsMapper.apply(theirs, policy));
                dropTheirs();
                break;
            case PREFER_OURS:
                nextOp.accept(oursMapper.apply(ours, policy));
                break;
            case OURS_FIRST:
                nextOp.accept(oursMapper.apply(ours, policy));
                nextOp.accept(theirsMapper.apply(theirs, policy));
                dropTheirs();
                break;
            case THEIRS_FIRST:
                nextOp.accept(theirsMapper.apply(theirs, policy));
                nextOp.accept(oursMapper.apply(ours, policy));
                dropTheirs();
                break;
            case DROP_OURS:
                break;
            case DROP_BOTH:
                dropTheirs();
                break;
            case PREFER_THEIRS:
                return mergeOursWhenPreferTheirs(ours);
            case DROP_THEIRS:
                return mergeOursWhenDropTheirs(ours);
        }
        return null;
    }

    private MergePolicy mergeOursWhenPreferTheirs(OURS ours) {
        MergePolicy policy = PREFER_THEIRS;
        do {
            nextOp.accept(theirsMapper.apply(theirs, policy));
            dropTheirs();
        } while (!shouldShortCircuit() && PREFER_THEIRS.equals(policy = mergeHandle.apply(ours, theirs)) &&
            takeTheirsNext());
        return requireNonNull(policy);
    }

    private MergePolicy mergeOursWhenDropTheirs(OURS ours) {
        MergePolicy policy = DROP_THEIRS;
        do {
            dropTheirs();
        } while (!shouldShortCircuit() && DROP_THEIRS.equals(policy = mergeHandle.apply(ours, theirs)) &&
            takeTheirsNext());
        return requireNonNull(policy);
    }

    private void dropTheirs() {
        theirs = null;
        theirsReady = false;
    }

    private boolean takeTheirsNext() {
        split.tryAdvance(this::takeNext);
        return theirsReady;
    }

    private void mergeOursWhenTheirsExhausted(OURS ours) {
        if (shouldShortCircuit()) {
            return;
        }
        switch (remainingPolicy) {
            case MERGE_AS_NULL:
                theirs = null;
                theirsReady = true;
                merge(ours);
                break;
            case TAKE_REMAINING:
            case TAKE_OURS:
                nextOp.accept(oursMapper.apply(ours, MergePolicy.TAKE_OURS));
                break;
            case TAKE_THEIRS:
            case DROP:
                dropTheirs();
                break;
        }
    }

    private void mergeTheirsWhenOursExhausted() {
        if (shouldShortCircuit() || (!theirsReady && !takeTheirsNext())) {
            return;
        }
        do {
            switch (remainingPolicy) {
                case MERGE_AS_NULL:
                    merge(null);
                    break;
                case TAKE_REMAINING:
                case TAKE_THEIRS:
                    nextOp.accept(theirsMapper.apply(theirs, MergePolicy.TAKE_THEIRS));
                    dropTheirs();
                case TAKE_OURS:
                case DROP:
                    break;
            }
        } while (!shouldShortCircuit() && takeTheirsNext());
    }
}

