/*
 * Copyright (c) 2016 - 2021. oyealex. All rights reserved.
 */

package com.huawei.md.nas.entity.task.path;

import com.huawei.md.nas.exception.InvalidMigrationPathException;
import org.jetbrains.annotations.NotNull;

import javax.annotation.concurrent.Immutable;
import java.nio.file.Path;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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
@Immutable
public class NFSMigrationPath extends MigrationPath {
    private static final Pattern LOCAL_PATH_PATTERN = Pattern.compile("^/.*$");

    private static final Pattern NETWORK_PATH_PATTERN = Pattern.compile("^(?<host>[^:/]+):(?<path>/.*$)");

    public NFSMigrationPath(@NotNull String host, @NotNull Path path) {
        super(host, path);
    }

    public NFSMigrationPath(@NotNull String address) {
        super(address);
    }

    @Override
    protected String[] parseAddress(String address) {
        if (LOCAL_PATH_PATTERN.matcher(address).matches()) {
            return new String[]{LOCAL_HOST, address};
        }
        Matcher matcher = NETWORK_PATH_PATTERN.matcher(address);
        if (matcher.matches()) {
            return new String[]{matcher.group("host"), matcher.group("path")};
        }
        throw new InvalidMigrationPathException("NFS address: " + address + " is not valid migration path");
    }

    @Override
    public String toString() {
        return isLocalPath() ? path.toString() : (host + ":" + path);
    }
}
