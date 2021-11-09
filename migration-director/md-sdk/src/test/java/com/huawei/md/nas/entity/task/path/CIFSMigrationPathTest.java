package com.huawei.md.nas.entity.task.path;

import com.huawei.md.nas.exception.InvalidMigrationPathException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.EnabledOnOs;
import org.junit.jupiter.api.condition.OS;

import java.nio.file.Paths;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * CIFSMigrationPath的单测
 *
 * @author oyealex
 * @version 1.0
 * @since 2021-11-09
 */
@EnabledOnOs(OS.WINDOWS)
class CIFSMigrationPathTest {
    @Test
    @DisplayName("能够正确解析正常网络路径")
    void should_parse_normal_network_address_rightly() {
        String address = "\\\\host\\share\\path";
        CIFSMigrationPath path = new CIFSMigrationPath(address);
        assertFalse(path.isLocalPath());
        assertEquals(address, path.toString());
        assertEquals(new CIFSMigrationPath("host", Paths.get("\\share\\path")), path);
    }

    @Test
    @DisplayName("能够正确解析正常本地路径")
    void should_parse_normal_local_address_rightly() {
        String address = "C:\\share\\path";
        CIFSMigrationPath path = new CIFSMigrationPath(address);
        assertTrue(path.isLocalPath());
        assertEquals(address, path.toString());
        assertEquals(new CIFSMigrationPath("localhost", Paths.get("C:\\share\\path")), path);
    }

    @Test
    @DisplayName("解析无效的路径时抛出异常")
    void should_throw_exception_on_invalid_path() {
        assertThrows(InvalidMigrationPathException.class, () -> new CIFSMigrationPath("\\\\localhost\\share"));
        assertThrows(InvalidMigrationPathException.class, () -> new CIFSMigrationPath("\\\\127.0.0.1\\share"));
        assertThrows(InvalidMigrationPathException.class, () -> new CIFSMigrationPath("\\share"));
    }
}