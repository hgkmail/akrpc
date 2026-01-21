package io.github.akrpc.common.utils;

import java.util.zip.CRC32;

/**
 * CRC32 工具类
 *
 * @author Kim Huang
 * @version 1.0
 * @since 2026/1/11
 */
public class CRC32Utils {

    private CRC32Utils() {}

    public static long getValue(byte[] data) {
        CRC32 crc32 = new CRC32();
        crc32.update(data);
        return crc32.getValue();
    }

}
