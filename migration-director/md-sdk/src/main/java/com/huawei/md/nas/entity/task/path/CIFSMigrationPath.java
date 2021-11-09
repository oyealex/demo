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
@Immutable
public class CIFSMigrationPath extends MigrationPath {
    private static final Pattern LOCAL_PATH_PATTERN = Pattern.compile("^[a-zA-Z]:\\\\.*$");

    private static final Pattern NETWORK_PATH_PATTERN = Pattern.compile("^\\\\\\\\(?<host>[^\\\\]+)(?<path>\\\\.*)$");

    public CIFSMigrationPath(@NotNull String host, @NotNull Path path) {
        super(host, path);
    }

    public CIFSMigrationPath(@NotNull String address) {
        super(address);
    }

    @Override
    protected String[] parseAddress(String address) {
        if (LOCAL_PATH_PATTERN.matcher(address).matches()) {
            return new String[]{LOCAL_HOST, address};
        }
        Matcher matcher = NETWORK_PATH_PATTERN.matcher(address);
        if (matcher.matches()) {
            String host = matcher.group("host");
            if (LOCAL_HOST.equals(normalizeHost(host))) {
                throw new InvalidMigrationPathException("CIFS address: " + address + " contains localhost domain");
            }
            return new String[]{host, matcher.group("path")};
        }
        throw new InvalidMigrationPathException("CIFS address: " + address + " is not valid migration path");
    }

    @Override
    public String toString() {
        return isLocalPath() ? path.toString() : ("\\\\" + host + path);
    }
}
