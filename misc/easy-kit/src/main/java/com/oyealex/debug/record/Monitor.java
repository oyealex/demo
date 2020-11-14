/*
 * Copyright (c) 2021. oyealex. All rights reserved.
 */

package com.oyealex.debug.record;

import java.util.concurrent.atomic.AtomicLong;

/**
 * Monitor
 *
 * @author oyealex
 * @version 1.0
 * @since 2021/4/21
 */
public class Monitor {
    private final AtomicLong accumulatedCount = new AtomicLong(0L);

    private final AtomicLong accumulatedSum = new AtomicLong(0L);

    private final AtomicLong accumulatedMin = new AtomicLong(Long.MAX_VALUE);

    private final AtomicLong accumulatedMax = new AtomicLong(Long.MIN_VALUE);

    private final AtomicLong count = new AtomicLong(0L);

    private final AtomicLong sum = new AtomicLong(0L);

    private final AtomicLong min = new AtomicLong(Long.MAX_VALUE);

    private final AtomicLong max = new AtomicLong(Long.MIN_VALUE);

    public Monitor incrementCount() {
        count.incrementAndGet();
        return this;
    }

    public Monitor accept(long value) {
        sum.addAndGet(value);
        count.incrementAndGet();
        updateMin(min, value);
        updateMax(max, value);
        return this;
    }

    public Monitor setValue(long value) {
        while (true) {
            long currValue = sum.get();
            if (currValue == value || sum.compareAndSet(currValue, value)) {
                return this;
            }
        }
    }

    public long sum() {
        return sum.get();
    }

    public long count() {
        return count.get();
    }

    public long min() {
        return min.get();
    }

    public long max() {
        return max.get();
    }

    public double average() {
        long currCount = count.get();
        return currCount == 0 ? 0.0 : sum.get() * 1.0 / currCount;
    }

    public Monitor accumulateAndReset() {
        accumulatedSum.addAndGet(sum.getAndSet(0L));
        accumulatedCount.addAndGet(count.getAndSet(0L));
        updateMin(accumulatedMin, min.getAndSet(Long.MAX_VALUE));
        updateMax(accumulatedMax, max.getAndSet(Long.MIN_VALUE));
        return this;
    }

    public long accumulatedSum() {
        return accumulatedSum.get();
    }

    public long accumulatedCount() {
        return accumulatedCount.get();
    }

    public long accumulatedMin() {
        return accumulatedMin.get();
    }

    public long accumulatedMax() {
        return accumulatedMax.get();
    }

    public double accumulatedAverage() {
        long currCount = accumulatedCount.get();
        return currCount == 0 ? 0.0 : accumulatedSum.get() * 1.0 / currCount;
    }

    private void updateMin(AtomicLong min, long value) {
        while (true) {
            long currMin = min.get();
            if (currMin <= value || min.compareAndSet(currMin, value)) {
                return;
            }
        }
    }

    private void updateMax(AtomicLong max, long value) {
        while (true) {
            long currMax = max.get();
            if (currMax >= value || max.compareAndSet(currMax, value)) {
                return;
            }
        }
    }
}
