package com.ljh.photoselector.util;

import java.util.UUID;

/**
 * Created by ljh on 2016/3/10.
 */
public class StringUtil {
    /**
     * 获取32位uuid
     *
     * @return
     */
    public static String get32UUID() {
        return UUID.randomUUID().toString().replace("-", "");
    }

    /**
     * 获取35位uuid
     *
     * @return
     */
    public static String get36UUID() {
        return UUID.randomUUID().toString();
    }
}
