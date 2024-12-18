package com.oye.common;

import com.google.common.io.MoreFiles;
import com.google.common.io.RecursiveDeleteOption;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.file.PathUtils;

import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;

/**
 * Main
 *
 * @author oyealex
 * @since 2023-04-24
 */
public class Main {
    public static void main(String[] args) throws IOException {
        // MoreFiles.deleteRecursively(Paths.get("E:\\.tp\\test"), RecursiveDeleteOption.ALLOW_INSECURE);
        FileUtils.deleteDirectory(new File("E:\\.tp\\test"));
    }
}
