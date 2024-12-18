package com.oye.common.general.executor.task;

public interface SubTask extends Task {
    Task getParent();
}
