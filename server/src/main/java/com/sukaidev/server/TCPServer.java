package com.sukaidev.server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * Created by sukaidev on 2019/03/18.
 * TCP服务提供类.
 */
public class TCPServer implements ClientThread.ClientHandlerCallback {
    private final int port;     // 服务端端口
    private ClientListener mListener;  // 连接监听
    private List<ClientThread> clientList = new ArrayList<>();  //客户端集合
    private final ThreadPoolExecutor forwardingThreadPool; // 转发单线程池

    TCPServer(int port) {
        this.port = port;
        this.forwardingThreadPool = new ThreadPoolExecutor(1, 1, 0, TimeUnit.MILLISECONDS, new ArrayBlockingQueue<Runnable>(100));
    }

    /**
     * 服务端启动入口.
     * @return 启动成功返回true
     */
    boolean start() {

        try {
            ClientListener listener = new ClientListener(port);
            mListener = listener;
            listener.start();
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

    /**
     * 服务端终止入口.
     */
    public void stop() {
        if (mListener != null) {
            mListener.exit();
        }
        synchronized (this) {
            for (ClientThread clientThread : clientList) {
                clientThread.exit();
            }
            clientList.clear();
        }

        // 停止线程池
        forwardingThreadPool.shutdownNow();
    }

    /**
     * 向所有连接主机发送广播，用于测试
     */
    synchronized void broadcast(String str) {
        for (ClientThread clientThread : clientList) {
            clientThread.send(str);
        }
    }

    // 客户端关闭回调
    @Override
    public synchronized void onSelfClosed(ClientThread handler) {
        clientList.remove(handler);
    }

    // 消息转发回调
    @Override
    public void onNewMessageArrived(final ClientThread handler, final String msg) {
        // 如果只有一个客户端在线
        if (clientList.size() == 1){
            return;
        }
        forwardingThreadPool.execute(new Runnable() {
                @Override
            public void run() {
                synchronized (this) {
                    for (ClientThread clientThread : clientList) {
                        if (clientThread.equals(handler)) {
                            // 跳过自己
                            continue;
                        }
                        // 对其他客户端发送信息
                        clientThread.send(msg);
                    }
                }
            }
        });
    }

    /**
     * 连接类
     */
    private class ClientListener extends Thread {
        private ServerSocket server;
        private boolean done = false;

        private ClientListener(int port) throws IOException {
            server = new ServerSocket(port);
            System.out.println("服务器信息：" + server.getInetAddress() + " P:" + server.getLocalPort());
        }

        @Override
        public void run() {
            super.run();

            System.out.println("服务器准备就绪~");
            // 等待客户端连接
            do {
                Socket client;
                try {
                    client = server.accept();
                } catch (IOException e) {
                    // 防timeout
                    continue;
                }
                try {
                    // 客户端构建异步线程
                    ClientThread clientThread = new ClientThread(client, TCPServer.this);
                    clientThread.start();
                    synchronized (TCPServer.this) {
                        // 将客户端加入集合中
                        clientList.add(clientThread);
                    }
                } catch (IOException e) {
                    System.out.println("客户端连接异常：" + e.getMessage());
                }
            } while (!done);

            System.out.println("服务器已关闭！");
        }

        void exit() {
            done = true;
            try {
                server.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
