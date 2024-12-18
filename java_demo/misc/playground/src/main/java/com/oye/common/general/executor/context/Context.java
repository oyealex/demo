package com.oye.common.general.executor.context;

import com.oye.common.general.executor.progress.Progress;
import com.oye.common.general.executor.task.Task;

import java.io.Closeable;
import java.util.Optional;

/** 上下文接口，用于管理任务的执行状态，和提供一般的访问执行流程的API。 */
public interface Context extends Closeable {
    <K, V> Optional<V> get(K key);

    <K, V> Optional<V> set(K key, V value);

    <K, V> Optional<V> read(K key);

    <K, V> Optional<V> write(K key, V value);

    Optional<Task> getRunningTask();

    Progress getProgress();

    Progress getTotalProgress();
}
