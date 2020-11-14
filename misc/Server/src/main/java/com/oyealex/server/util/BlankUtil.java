package com.oyealex.server.util;

import java.util.List;
import java.util.Map;

/**
 * @author oye
 * @since 2020-05-17 09:53:24
 */
public class BlankUtil {
    private BlankUtil() {
    }

    public static boolean isBlank(String string) {
        return string == null || string.trim().isEmpty();
    }

    public static boolean isBlank(List<?> list) {
        return list == null || list.isEmpty();
    }

    public static boolean isBlank(Map<?, ?> map) {
        return map == null || map.isEmpty();
    }

    public static boolean isAnyBlank(String... strings) {
        if (strings == null || strings.length == 0) {
            return true;
        }
        for (String string : strings) {
            if (isBlank(string)) {
                return true;
            }
        }
        return false;
    }
}
