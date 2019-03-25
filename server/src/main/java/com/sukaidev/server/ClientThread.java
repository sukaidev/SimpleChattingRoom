package com.sukaidev.server;

import com.sukaidev.lib.utils.CloseUtils;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.net.SocketTimeoutException;

/**
 * Created by sukaidev on 2019/03/18.
 * 客户端消息处理线程.
 */
public class ClientThread extends Thread {

    private boolean done = false;
    private final Socket socket; // 与客户端建立连接的socket对象
    private final InputStream inputStream;
    private final OutputStream outputStream;
    private final ClientHandlerCallback clientHandlerCallback;

    ClientThread(Socket socket, ClientHandlerCallback closeNotify) throws IOException {
        this.socket = socket;
        this.clientHandlerCallback = closeNotify;
        this.inputStream = socket.getInputStream();
        this.outputStream = socket.getOutputStream();
    }

    @Override
    public void run() {
        super.run();
        try {
            int len;
            byte[] b = new byte[1024 * 3];

            // 读取客户端消息，并转发到其他客户端，实现聊天室效果
            do {
                try {
                    while ((len = inputStream.read(b)) != -1) {
                        String str = new String(b, 0, len);
                        System.out.println(str); // 打印到屏幕，用于测试
                        //  将消息发送到TCPServer中，方便处理
                        clientHandlerCallback.onNewMessageArrived(this, str);
                    }
                } catch (SocketTimeoutException e) {
                    //noinspection UnnecessaryContinue
                    continue; // 防timeout
                }
            }
            while (!done);
        } catch (IOException e) {
            if (!done) {
                e.printStackTrace();
                System.out.println("连接异常断开" + e.getMessage());
                exitBySelf();
            }
        } finally {
            exit();
        }
    }

    /**
     * 将消息发送到客户端
     * @param str
     */
    void send(String str) {
        try {
            outputStream.write(str.getBytes());
        } catch (IOException ignored) {
        }
    }


    void exit() {
        done = false;
        CloseUtils.close(socket);
        System.out.println("客户端已退出：" + socket.getInetAddress() + " P:" + socket.getPort());
    }


    /**
     * 自身退出时需要提醒服务端移除该客户端
     */
    private void exitBySelf() {
        exit();
        clientHandlerCallback.onSelfClosed(this);
    }

    public interface ClientHandlerCallback {
        // 自身关闭回调
        void onSelfClosed(ClientThread handler);

        // 收到消息回调
        void onNewMessageArrived(ClientThread handler, String msg);
    }
}
