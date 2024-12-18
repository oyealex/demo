package com.oye.common.general.executor.task;

import com.oye.common.general.executor.context.Context;
import com.oye.common.general.executor.exception.TaskExecution;
import com.oye.common.general.executor.result.Result;

public interface StepTask extends Task {
    void beforeExecution(Context context) throws TaskExecution;

    Result afterExecution(Context context, Result result) throws TaskExecution;
}
