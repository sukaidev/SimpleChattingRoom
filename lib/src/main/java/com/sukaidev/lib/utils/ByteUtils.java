package com.sukaidev.lib.utils;

/**
 * Created by sukaidev on 2019/03/18.
 * 字节工具类.
 */
public class ByteUtils {

    /**
     * 字节数组转化为整型数据
     *
     * @param b
     * @return
     */
    public static int byteArrayToInt(byte[] b) {
        return b[3] & 0xFF | (b[2] & 0xFF) << 8 | (b[1] & 0xFF) << 16 | (b[0] & 0xFF) << 24;
    }

    /**
     * 整型数据转化为字节数组
     *
     * @param a
     * @return
     */
    public static byte[] intToByteArray(int a) {
        return new byte[]{(byte) ((a >> 24) & 0xFF), (byte) ((a >> 16) & 0xFF), (byte) ((a >> 8) & 0xFF),
                (byte) (a & 0xFF),};
    }

    /**
     * 判断数据时候包含协议头
     *
     * @param data
     * @param head
     * @return
     */
    public static boolean startsWith(byte[] data, byte[] head) {
        if (data == null || data.length == 0)
            return false;
        int i = 0;
        int length = head.length;
        while (i != length) {
            if (head[i] != data[i]) {
                return false;
            }
            i++;
        }
        return true;
    }
}
