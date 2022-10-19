/*
 * Copyright (c) 2021. oyealex. All rights reserved.
 */

package com.oye.common.treebuilder;

import lombok.extern.slf4j.Slf4j;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.LongAdder;

/**
 * @author oye
 * @since 2020-07-02 23:40:19
 */
@Slf4j
public class SingleFolderBuildContext {
    private final int parallel;

    private final SingleFolderBuildParam buildParam;

    private final ExecutorService executorService;

    private final AtomicInteger runningTask;

    private final LongAdder costTime;

    private final LongAdder fileSize;

    private final LongAdder fileAmount;

    public SingleFolderBuildContext(int parallel, SingleFolderBuildParam buildParam) {
        this.parallel = parallel;
        this.buildParam = buildParam;

        executorService = Executors.newFixedThreadPool(this.parallel);
        runningTask = new AtomicInteger(0);
        costTime = new LongAdder();
        fileSize = new LongAdder();
        fileAmount = new LongAdder();
    }

    public void startBuild() {
        for (SingleFolderBuildParam subParam : createSubParams()) {
            runningTask.incrementAndGet();
            executorService.submit(new SingleFolderBuildRunnable(subParam, this));
        }
        executorService.shutdown();
    }

    public List<SingleFolderBuildParam> createSubParams() {
        List<SingleFolderBuildParam> subParams = new ArrayList<>(parallel);
        final int subParamFileAmount = buildParam.getFileAmount() / parallel;
        for (int i = 0; i < parallel; i++) {
            subParams.add(new SingleFolderBuildParam(
                buildParam.getBasePath() +
                    File.separator +
                    buildParam.getSubFolderPrefix() + (i + buildParam.getSubFolderStartIndex()),
                buildParam.getFilePrefix(),
                "",
                0,
                buildParam.getFileSize(),
                i + 1 == parallel ? buildParam.getFileAmount() - subParamFileAmount * i : subParamFileAmount,
                i * subParamFileAmount + buildParam.getStartIndex()));
        }

        return subParams;
    }

    public void addCostTime(long costTimeNano) {
        costTime.add(costTimeNano);
    }

    public void addActualSize(long size) {
        fileSize.add(size);
    }

    public void addActualAmount() {
        fileAmount.increment();
    }

    public void finishOneTask() {
        if (runningTask.decrementAndGet() <= 0) {
            final long cost = costTime.sum() / parallel;
            final long actualSize = fileSize.sum();
            final long actualAmount = fileAmount.sum();
            log.info("finish building, build file {} with {} MB, avg cost time: {} mill, {} sec, avg speed: {} MB/SEC",
                actualAmount,
                actualSize / Constants.MB,
                TimeUnit.NANOSECONDS.toMillis(cost),
                TimeUnit.NANOSECONDS.toSeconds(cost),
                actualSize * actualAmount / TimeUnit.NANOSECONDS.toSeconds(cost) / Constants.MB);
        }
    }
}
