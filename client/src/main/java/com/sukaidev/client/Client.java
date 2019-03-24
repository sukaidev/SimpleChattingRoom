package com.sukaidev.client;

import com.sukaidev.lib.Constants;
import com.sukaidev.lib.utils.CloseUtils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.net.Inet4Address;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketTimeoutException;

/**
 * Created by sukaidev on 2019/03/19.
 * 客户端消息处理类.
 */
public class Client {

    private final Socket socket;
    private final ReadHandler readHandler;
    private final PrintStream printStream;

    public Client(Socket socket, ReadHandler readHandler) throws IOException {
        this.socket = socket;
        this.readHandler = readHandler;
        this.printStream = new PrintStream(socket.getOutputStream());
    }

    public void exit() {
        readHandler.exit();
        CloseUtils.close(printStream, socket);
        System.out.println("客户端已退出~");
    }

    public void send(String msg) {
        printStream.println(msg);
    }

    public static Client start(OnReadHandlerListener listener) throws IOException {
        Socket socket = new Socket();

        socket.setSoTimeout(3000);

        socket.connect(new InetSocketAddress(Inet4Address.getByName(Constants.ADDRESS_TCP_SERVER), Constants.PORT_TCP_SERVER), 3000);

        System.out.println("已发起服务器连接，并进入后续流程~");
        System.out.println("客户端信息：" + socket.getLocalAddress() + " P:" + socket.getLocalPort());
        System.out.println("服务器信息：" + socket.getInetAddress() + " P:" + socket.getPort());

        try {
            ReadHandler readHandler = new ReadHandler(socket.getInputStream(), listener);
            readHandler.start();

            return new Client(socket, readHandler);
        } catch (Exception e) {
            System.out.println("异常关闭");
        }
        return null;
    }


    static class ReadHandler extends Thread {

        private boolean done = false;
        private final InputStream inputStream;
        private final OnReadHandlerListener listener;

        ReadHandler(InputStream is, OnReadHandlerListener listener) {
            this.inputStream = is;
            this.listener = listener;
        }

        @Override
        public void run() {
            super.run();

            try {
                // 得到输入流，用于接收数据
                BufferedReader socketInput = new BufferedReader(new InputStreamReader(inputStream));

                do {
                    String str;
                    try {
                        // 客户端拿到一条数据
                        str = socketInput.readLine();
                    } catch (SocketTimeoutException e) {
                        continue;
                    }
                    if (str == null) {
                        System.out.println("连接已关闭，无法读取数据");
                        break;
                    }
                    // 打印到屏幕，并处理数据
                    System.out.println(str);
                    listener.onReceive(str);
                } while (!done);

                socketInput.close();

            } catch (Exception e) {
                if (!done) {
                    System.out.println("连接异常断开" + e.getMessage());
                }
            } finally {
                // 连接关闭
                CloseUtils.close(inputStream);
            }
        }

        void exit() {
            done = true;
            CloseUtils.close(inputStream);
        }
    }

    public interface OnReadHandlerListener {
        void onReceive(String message);
    }

}