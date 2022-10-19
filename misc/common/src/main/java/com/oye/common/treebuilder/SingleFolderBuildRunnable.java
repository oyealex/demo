/*
 * Copyright (c) 2021. oyealex. All rights reserved.
 */

package com.oye.common.treebuilder;

import com.google.common.base.Stopwatch;
import lombok.extern.slf4j.Slf4j;

import java.io.File;
import java.util.concurrent.TimeUnit;

/**
 * @author oye
 * @since 2020-07-02 23:38:05
 */
@Slf4j
public class SingleFolderBuildRunnable implements Runnable {
    private final SingleFolderBuildParam buildParam;

    private final SingleFolderBuildContext context;

    public SingleFolderBuildRunnable(SingleFolderBuildParam buildParam, SingleFolderBuildContext context) {
        this.buildParam = buildParam;
        this.context = context;
    }

    @Override
    public void run() {
        if (!Util.createFolders(buildParam.getBasePath())) {
            log.error("create base path {} failed", buildParam.getBasePath());
            return;
        }

        final int END_FILE_NO = buildParam.getStartIndex() + buildParam.getFileAmount();
        final long FILE_SIZE = buildParam.getFileSize();
        int nextFileNo = buildParam.getStartIndex();
        File nextFile;
        log.info("start to fill file with param: {}", buildParam);
        Stopwatch stopwatch = Stopwatch.createStarted();
        while ((nextFile = generateNextFile(nextFileNo++, END_FILE_NO)) != null) {
            if (Util.fillFile(nextFile, FILE_SIZE)) {
                context.addActualSize(FILE_SIZE);
                context.addActualAmount();
            }
        }
        stopwatch.stop();
        context.addCostTime(stopwatch.elapsed(TimeUnit.NANOSECONDS));
        context.finishOneTask();
    }

    private File generateNextFile(int nextFileNo, int endFileNo) {
        return nextFileNo >= endFileNo ? null :
            new File(buildParam.getBasePath(), buildParam.getFilePrefix() + nextFileNo);
    }
}
