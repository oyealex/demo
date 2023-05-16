package com.oyealex.pipe.basis;

import com.oyealex.pipe.basis.api.Pipe;
import com.oyealex.pipe.basis.api.policy.MergePolicy;
import com.oyealex.pipe.basis.api.policy.MergeRemainingPolicy;

import java.util.Spliterator;
import java.util.function.BiFunction;

import static com.oyealex.pipe.basis.api.policy.MergePolicy.DROP_THEIRS;
import static com.oyealex.pipe.basis.api.policy.MergePolicy.PREFER_THEIRS;
import static com.oyealex.pipe.flag.PipeFlag.NOTHING;
import static java.util.Objects.requireNonNull;

/**
 * MergeOp
 *
 * @author oyealex
 * @since 2023-05-17
 */
class MergeOp {
    static class Homogeneous<T> extends RefPipe<T, T> {
        private final Pipe<? extends T> pipe;

        private final BiFunction<? super T, ? super T, MergePolicy> mergeHandle;

        private final MergeRemainingPolicy remainingPolicy;

        Homogeneous(RefPipe<?, ? extends T> prePipe, Pipe<? extends T> pipe,
            BiFunction<? super T, ? super T, MergePolicy> mergeHandle, MergeRemainingPolicy remainingPolicy) {
            super(prePipe, NOTHING);
            this.pipe = pipe;
            this.mergeHandle = mergeHandle;
            this.remainingPolicy = remainingPolicy;
        }

        @Override
        protected Op<T> wrapOp(Op<T> nextOp) {
            return new ChainedOp.ShortCircuitRecorded<>(nextOp) {
                private Spliterator<? extends T> split = pipe.toSpliterator();

                private T theirs;

                private boolean theirsReady = false;

                private void takeNext(T value) {
                    theirs = value;
                    theirsReady = true;
                }

                @Override
                public void accept(T ours) {
                    if (!theirsReady) {
                        if (!takeNextTheirs()) {
                            if (onTheirsExhausted(ours)) {
                                merge(ours);
                            }
                            return;
                        }
                    }
                    merge(ours);
                }

                @Override
                public void end() {
                    onOursExhausted();
                    split = null;
                }

                private void merge(T ours) {
                    for (MergePolicy policy = requireNonNull(mergeHandle.apply(ours, theirs)); policy != null;
                         policy = mergePair(ours, policy)) {
                        // noop
                    }
                }

                private MergePolicy mergePair(T ours, MergePolicy policy) {
                    switch (policy) {
                        case SELECT_OURS:
                            if (!needShortCircuit()) {
                                nextOp.accept(ours);
                            }
                            invalidTheirs();
                            break;
                        case SELECT_THEIRS:
                            if (!needShortCircuit()) {
                                nextOp.accept(theirs);
                            }
                            invalidTheirs();
                            break;
                        case PREFER_OURS:
                            if (!needShortCircuit()) {
                                nextOp.accept(ours);
                            }
                            break;
                        case OURS_FIRST:
                            if (!needShortCircuit()) {
                                nextOp.accept(ours);
                            }
                            if (!needShortCircuit()) {
                                nextOp.accept(theirs);
                            }
                            invalidTheirs();
                            break;
                        case THEIRS_FIRST:
                            if (!needShortCircuit()) {
                                nextOp.accept(theirs);
                            }
                            if (!needShortCircuit()) {
                                nextOp.accept(ours);
                            }
                            invalidTheirs();
                            break;
                        case DROP_OURS:
                            break;
                        case DROP_BOTH:
                            invalidTheirs();
                            break;
                        case PREFER_THEIRS:
                            return onPreferTheirs(ours);
                        case DROP_THEIRS:
                            return onDropTheirs(ours);
                    }
                    return null;
                }

                private MergePolicy onPreferTheirs(T ours) {
                    MergePolicy policy = PREFER_THEIRS;
                    do {
                        if (!needShortCircuit()) {
                            nextOp.accept(theirs);
                        }
                        invalidTheirs();
                    } while (takeNextTheirs() && PREFER_THEIRS.equals(policy = mergeHandle.apply(ours, theirs)));
                    return requireNonNull(policy);
                }

                private MergePolicy onDropTheirs(T ours) {
                    MergePolicy policy = DROP_THEIRS;
                    do {
                        invalidTheirs();
                    } while (takeNextTheirs() && DROP_THEIRS.equals(policy = mergeHandle.apply(ours, theirs)));
                    return requireNonNull(policy);
                }

                private void invalidTheirs() {
                    theirs = null;
                    theirsReady = false;
                }

                private boolean takeNextTheirs() {
                    split.tryAdvance(this::takeNext);
                    return theirsReady;
                }

                private boolean onTheirsExhausted(T ours) {
                    switch (remainingPolicy) {
                        case MERGE_AS_NULL:
                            theirs = null;
                            theirsReady = true;
                            return true;
                        case SELECT_REMAINING:
                        case SELECT_OURS:
                            if (!needShortCircuit()) {
                                nextOp.accept(ours);
                            }
                            break;
                        case DROP:
                        case SELECT_THEIRS:
                            break;
                    }
                    return false;
                }

                private void onOursExhausted() {
                    if (!theirsReady) {
                        if (!takeNextTheirs()) {
                            return;
                        }
                    }
                    do {
                        switch (remainingPolicy) {
                            case MERGE_AS_NULL:
                                merge(null);
                                break;
                            case SELECT_REMAINING:
                            case SELECT_THEIRS:
                                if (!needShortCircuit()) {
                                    nextOp.accept(theirs);
                                }
                                invalidTheirs();
                            case SELECT_OURS:
                            case DROP:
                                break;
                        }
                    } while (takeNextTheirs());
                }
            };
        }
    }
}
