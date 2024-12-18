package com.oye.common.general.executor.task;

import com.oye.common.general.executor.context.Context;
import com.oye.common.general.executor.exception.TaskExecution;

import java.nio.file.Path;
import java.util.List;

/** 基于Python脚本的任务 */
public interface PythonScriptTask extends ScriptTask {
    List<Path> getClassPaths(Context context) throws TaskExecution;
}
