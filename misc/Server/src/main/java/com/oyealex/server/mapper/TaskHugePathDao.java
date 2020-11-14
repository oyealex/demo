/*
 * Copyright (c) 2020. oyealex. All rights reserved.
 */

package com.oyealex.server.mapper;

import com.oyealex.server.entity.TaskHugePath;

import java.util.Optional;

/**
 * 大目录Dao层
 *
 * @author oyealex
 * @version 1.0
 * @since 2020/8/25
 */
public interface TaskHugePathDao {
    /**
     * 插入一条大目录记录
     *
     * @param taskHugePath 大目录记录
     */
    void insert(TaskHugePath taskHugePath);

    /**
     * 根据任务id查询大目录记录
     *
     * @param taskId 任务id
     */
    Optional<TaskHugePath> selectByTaskId(String taskId);
}
