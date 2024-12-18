package com.oye.common.general.executor.task;

import com.oye.common.general.executor.context.Context;
import com.oye.common.general.executor.exception.TaskExecution;

import java.nio.file.Path;

/** 基于脚本的任务 */
public interface ScriptTask extends Task {
    Path getScriptPath(Context context) throws TaskExecution;
}
