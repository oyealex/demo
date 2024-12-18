package com.oye.common.general.executor.task;

import com.oye.common.general.executor.context.Context;
import com.oye.common.general.executor.exception.TaskExecution;

public interface ParallelTask extends MultiTask {
    int getMaxConcurrency(Context context) throws TaskExecution;
}
