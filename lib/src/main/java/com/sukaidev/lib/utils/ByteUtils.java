package com.sukaidev.lib.utils;

/**
 * Created by sukaidev on 2019/03/18.
 * �ֽڹ�����.
 */
public class ByteUtils {

    /**
     * �ֽ�����ת��Ϊ��������
     *
     * @param b
     * @return
     */
    public static int byteArrayToInt(byte[] b) {
        return b[3] & 0xFF | (b[2] & 0xFF) << 8 | (b[1] & 0xFF) << 16 | (b[0] & 0xFF) << 24;
    }

    /**
     * ��������ת��Ϊ�ֽ�����
     *
     * @param a
     * @return
     */
    public static byte[] intToByteArray(int a) {
        return new byte[]{(byte) ((a >> 24) & 0xFF), (byte) ((a >> 16) & 0xFF), (byte) ((a >> 8) & 0xFF),
                (byte) (a & 0xFF),};
    }

    /**
     * �ж�����ʱ�����Э��ͷ
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
