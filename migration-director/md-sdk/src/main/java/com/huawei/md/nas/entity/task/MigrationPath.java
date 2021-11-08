/*
 * Copyright (c) 2016 - 2021. oyealex. All rights reserved.
 */

package com.huawei.md.nas.entity.task;

import lombok.Getter;
import org.jetbrains.annotations.NotNull;

import java.nio.file.Path;

/**
 * 迁移路径
 *
 * @author oyealex
 * @version 1.0
 * @since 2021-11-09
 */
@Getter
public abstract class MigrationPath {
    /** 默认的本地迁移路径的主机地址 */
    public static final String LOCAL_HOST = "localhost";

    /** 主机地址 */
    private final String host;

    /** 绝对路径 */
    private final Path path;

    public MigrationPath(@NotNull String host, @NotNull Path path) {
        this.host = normalizeHost(host);
        this.path = path;
    }

    public MigrationPath(@NotNull String address) {
        this.host = parseHost(address);
        this.path = parsePath(address);
    }

    protected abstract String parseHost(String address);

    protected abstract Path parsePath(String address);

    private String normalizeHost(String host) {
        return LOCAL_HOST.equalsIgnoreCase(host) || "127.0.0.1".equals(host) ? LOCAL_HOST : host;
    }

    /**
     * 判断此迁移路径是否是网络路径
     *
     * @return 此迁移路径是否是网络路径
     */
    public boolean isNetworkPath() {
        return LOCAL_HOST.equals(host);
    }
}
