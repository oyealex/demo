/*
 * Copyright (c) 2016 - 2021. oyealex. All rights reserved.
 */

package com.huawei.md.nas.entity.task.path;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import org.jetbrains.annotations.NotNull;

import javax.annotation.concurrent.Immutable;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * 迁移路径
 *
 * @author oyealex
 * @version 1.0
 * @since 2021-11-09
 */
@Getter
@Immutable
@EqualsAndHashCode
public abstract class MigrationPath {
    /** 默认的本地迁移路径的主机地址 */
    public static final String LOCAL_HOST = "localhost";

    /** 主机地址 */
    protected final String host;

    /** 绝对路径 */
    protected final Path path;

    public MigrationPath(@NotNull String host, @NotNull Path path) {
        this.host = normalizeHost(host);
        this.path = path;
    }

    public MigrationPath(@NotNull String address) {
        String[] parsed = parseAddress(address);
        this.host = normalizeHost(parsed[0]);
        this.path = Paths.get(parsed[1]);
    }

    protected abstract String[] parseAddress(String address);

    @Override
    public abstract String toString();

    protected final String normalizeHost(String host) {
        return LOCAL_HOST.equalsIgnoreCase(host) || "127.0.0.1".equals(host) ? LOCAL_HOST : host;
    }

    /**
     * 判断此迁移路径是否是本地路径
     *
     * @return 此迁移路径是否是本地路径
     */
    public boolean isLocalPath() {
        return LOCAL_HOST.equals(host);
    }
}
