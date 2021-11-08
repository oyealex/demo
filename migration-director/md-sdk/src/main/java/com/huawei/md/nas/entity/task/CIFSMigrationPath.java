/*
 * Copyright (c) 2016 - 2021. oyealex. All rights reserved.
 */

package com.huawei.md.nas.entity.task;

import org.jetbrains.annotations.NotNull;

import java.nio.file.Path;

/**
 * CIFS版本二点迁移路径
 * <br/>
 * 示例
 * <pre><code>
 * \\100.0.0.1\share
 * \\localhost\share\path(无效路径)
 * \\127.0.0.1\share\path(无效路径)
 * D:\project\blog
 * </code></pre>
 *
 * @author oyealex
 * @version 1.0
 * @since 2021-11-09
 */
public class CIFSMigrationPath extends MigrationPath {
    public CIFSMigrationPath(@NotNull String host, @NotNull Path path) {
        super(host, path);
    }

    public CIFSMigrationPath(@NotNull String address) {
        super(address);
    }

    @Override
    protected String parseHost(String address) {
        return null; // TODO 20211109010929
    }

    @Override
    protected Path parsePath(String address) {
        return null; // TODO 20211109010931
    }
}
