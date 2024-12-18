package com.oye.common.general.executor.task;

import com.oye.common.general.executor.context.Context;
import com.oye.common.general.executor.exception.TaskExecution;

/** 普通Python脚本任务 */
public interface PlainPythonScriptTask {
    String getEntryMethod(Context context) throws TaskExecution;
}
