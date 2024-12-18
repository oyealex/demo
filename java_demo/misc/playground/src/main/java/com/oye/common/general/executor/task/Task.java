package com.oye.common.general.executor.task;

import com.oye.common.general.executor.context.Context;
import com.oye.common.general.executor.exception.TaskExecution;
import com.oye.common.general.executor.result.Result;

/** 任务接口 */
public interface Task {
    /**
     * 获取任务名称。
     *
     * @return 任务名称。不能为空。
     */
    String getName();

    /**
     * 执行任务，得到结果，或抛出异常。
     *
     * @param context 执行上下文。
     * @return 结果。
     * @throws TaskExecution 当执行异常时抛出。
     */
    Result execute(Context context) throws TaskExecution;
}
