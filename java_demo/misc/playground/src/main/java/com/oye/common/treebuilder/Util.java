/*
 * Copyright (c) 2021. oyealex. All rights reserved.
 */

package com.oye.common.treebuilder;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.io.BufferedOutputStream;
import java.io.Closeable;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Random;

/**
 * @author oye
 * @since 2020-07-02 23:54:33
 */
@Slf4j
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class Util implements Constants {
    private static final byte[] BUFFER = new byte[4 * Constants.KB];

    static {
        new Random().nextBytes(BUFFER);
    }

    public static void closeSilently(Closeable... closeables) {
        if (isBlank(closeables)) {
            return;
        }

        for (Closeable closeable : closeables) {
            try {
                closeable.close();
            } catch (IOException ignored) {
            }
        }
    }

    public static boolean createFolders(String path) {
        try {
            File pathFile = new File(path).getCanonicalFile();
            return pathFile.exists() && !pathFile.isFile() || pathFile.mkdirs();
        } catch (IOException e) {
            log.error("create folder {} failed, ", path, e);
            return false;
        }
    }

    public static boolean fillFile(File file, long size) {
        BufferedOutputStream ops = null;
        try {
            ops = new BufferedOutputStream(new FileOutputStream(file));
            write2OutputStream(ops, size);
            return true;
        } catch (IOException e) {
            log.error("fill file {} with size {} failed, ", file, size, e);
            return false;
        } finally {
            closeSilently(ops);
        }
    }

    public static boolean isBlank(Object[] objs) {
        return objs == null || objs.length == 0;
    }

    private static void write2OutputStream(OutputStream ops, long size) throws IOException {
        long writtenSize = 0L;
        int size2Write;
        while (writtenSize < size) {
            size2Write = Math.min(BUFFER.length, (int) (size - writtenSize));
            ops.write(BUFFER, 0, size2Write);
            ops.flush();
            writtenSize += size2Write;
        }
    }
}
