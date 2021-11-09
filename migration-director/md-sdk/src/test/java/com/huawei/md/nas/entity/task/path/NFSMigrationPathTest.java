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
 * NFSMigrationPath的单测
 *
 * @author oyealex
 * @version 1.0
 * @since 2021-11-10
 */
@EnabledOnOs(OS.LINUX)
class NFSMigrationPathTest {
    @Test
    @DisplayName("能够正确解析正常网络路径")
    void should_parse_normal_network_address_rightly() {
        String address = "100.0.0.1:/share/path";
        NFSMigrationPath path = new NFSMigrationPath(address);
        assertFalse(path.isLocalPath());
        assertEquals(address, path.toString());
        assertEquals(new NFSMigrationPath("host", Paths.get("/share/path")), path);
    }

    @Test
    @DisplayName("能够正确解析正常本地路径")
    void should_parse_normal_local_address_rightly() {
        String address = "/share/path";
        NFSMigrationPath path = new NFSMigrationPath(address);
        assertTrue(path.isLocalPath());
        assertEquals(address, path.toString());
        assertEquals(new NFSMigrationPath("localhost", Paths.get("/share/path")), path);
    }

    @Test
    @DisplayName("解析无效的路径时抛出异常")
    void should_throw_exception_on_invalid_path() {
        assertThrows(InvalidMigrationPathException.class, () -> new NFSMigrationPath("share"));
        assertThrows(InvalidMigrationPathException.class, () -> new NFSMigrationPath("127.0.0.1:share"));
        assertThrows(InvalidMigrationPathException.class, () -> new NFSMigrationPath("\\share"));
    }
}