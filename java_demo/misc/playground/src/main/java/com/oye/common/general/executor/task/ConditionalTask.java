package com.oye.common.general.executor.task;

import com.oye.common.general.executor.context.Context;
import com.oye.common.general.executor.exception.TaskExecution;
import com.oye.common.general.executor.result.Result;

/** 支持条件的任务 */
public interface ConditionalTask extends Task {
    boolean match(Result result, Context context) throws TaskExecution;
}
