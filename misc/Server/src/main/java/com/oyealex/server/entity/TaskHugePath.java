/*
 * Copyright (c) 2020. oyealex. All rights reserved.
 */

package com.oyealex.server.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.util.List;

/**
 * 大目录对象
 *
 * @author oyealex
 * @version 1.0
 * @since 2020/8/25
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Accessors(chain = true)
public class TaskHugePath implements Serializable {
    private String id;

    private String taskId;

    private List<HugePath> hugePaths;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Accessors(chain = true)
    public static class HugePath implements Serializable {
        private String path;

        private long cost;
    }
}
