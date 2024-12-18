package com.oye.common.general.executor.result;

import com.oye.common.general.executor.result.state.ResultState;

import java.time.LocalDateTime;
import java.util.Optional;

/** 任务执行结果 */
public interface Result {
    Optional<LocalDateTime> getStartTime();

    Optional<LocalDateTime> getCompleteTime();

    ResultState getState();

    <T> Optional<T> getData();

    Optional<Throwable> getCause();
}
