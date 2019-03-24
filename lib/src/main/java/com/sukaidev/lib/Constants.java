package com.sukaidev.lib;

/**
 * Created by sukaidev on 2019/03/18.
 */
public class Constants {

    // 公用消息头，用于消息验证
    public static byte[] HEADER = new byte[]{3, 1, 5, 4, 9, 5, 2, 7};

    // 服务器局域网IP地址
    public static final String ADDRESS_TCP_SERVER = "192.168.1.236";

    // 固化服务器TCP连接端口号
    public static final int PORT_TCP_SERVER = 30301;


}
