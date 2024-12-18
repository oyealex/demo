package com.oye.common.general.executor.task;

import com.oye.common.general.executor.context.Context;
import com.oye.common.general.executor.exception.TaskExecution;

import java.util.List;

/** 支持嵌套多个子任务 */
public interface MultiTask extends Task {
    List<SubTask> getSubTasksOnce(Context context) throws TaskExecution;
}
