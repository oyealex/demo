/*
 * Copyright (c) 2016 - 2021. oyealex. All rights reserved.
 */

package com.huawei.md.nas.entity.task;

import org.jetbrains.annotations.NotNull;

import java.nio.file.Path;

/**
 * NFS版本的迁移路径
 * <br/>
 * 示例
 * <pre><code>
 * 10.0.0.1:/share/path
 * /root/download
 * localhost:/tmp(等价于/tmp)
 * </code></pre>
 *
 * @author oyealex
 * @version 1.0
 * @since 2021-11-09
 */
public class NFSMigrationPath extends MigrationPath {
    public NFSMigrationPath(@NotNull String host, @NotNull Path path) {
        super(host, path);
    }

    public NFSMigrationPath(@NotNull String address) {
        super(address);
    }

    @Override
    protected String parseHost(String address) {
        return null; // TODO 20211109010643
    }

    @Override
    protected Path parsePath(String address) {
        return null; // TODO 20211109010646
    }
}
