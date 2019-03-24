package com.sukaidev.server;

import com.sukaidev.lib.utils.CloseUtils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintStream;
import java.net.Socket;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * Created by sukaidev on 2019/03/18.
 */
public class ClientHandler {
    private final Socket socket;
    private final ClientReadHandler readHandler;
    private final ClientWriteHandler writeHandler;
    private final ClientHandlerCallback clientHandlerCallback;
    private final String clientInfo;

    public ClientHandler(Socket socket, ClientHandlerCallback closeNotify) throws IOException {
        this.socket = socket;
        this.clientHandlerCallback = closeNotify;
        this.readHandler = new ClientReadHandler(socket.getInputStream());
        this.writeHandler = new ClientWriteHandler(socket.getOutputStream());
        this.clientInfo = "A[" + socket.getInetAddress().getHostAddress() + "] P[" + socket.getPort() + "] ";
        System.out.println("新客户端连接：" + clientInfo);
    }

    public String getClientInfo() {
        return clientInfo;
    }

    public void exit() {
        readHandler.exit();
        writeHandler.exit();
        CloseUtils.close(socket);
        System.out.println("客户端已退出：" + socket.getInetAddress() + " P:" + socket.getPort());
    }

    public void send(String str) {
        writeHandler.send(str);
    }

    public void readAndPrint() {
        readHandler.start();
    }

    private void exitBySelf() {
        exit();
        clientHandlerCallback.onSelfClosed(this);
    }

    public interface ClientHandlerCallback {
        // 自身关闭回调
        void onSelfClosed(ClientHandler handler);

        // 收到消息回调
        void onNewMessageArrived(ClientHandler handler, String msg);
    }

    class ClientReadHandler extends Thread {

        private boolean done = false;
        private final InputStream inputStream;

        ClientReadHandler(InputStream is) {
            this.inputStream = is;
        }

        @Override
        public void run() {
            super.run();

            try {
                // 得到输入流，用于接收数据
                BufferedReader socketInput = new BufferedReader(new InputStreamReader(inputStream));

                do {
                    // 客户端拿到一条数据
                    String str = socketInput.readLine();
                    if (str == null) {
                        System.out.println("客户端已无法读取数据！");
                        exitBySelf();
                        break;
                    }
                    // 打印到屏幕。并回送数据长度
                    System.out.println(str);
                    clientHandlerCallback.onNewMessageArrived(ClientHandler.this, str);
                } while (!done);

                socketInput.close();

            } catch (Exception e) {
                if (!done) {
                    System.out.println("连接异常断开" + e.getMessage());
                    exitBySelf();
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

    class ClientWriteHandler extends Thread {

        private boolean done = false;
        private final PrintStream printStream;
        private final ExecutorService executor;

        ClientWriteHandler(OutputStream outputStream) {
            this.printStream = new PrintStream(outputStream);
            this.executor = new ThreadPoolExecutor(1, 1, 0L, TimeUnit.MILLISECONDS,
                    new ArrayBlockingQueue<Runnable>(100));
        }

        void send(final String str) {
            if (done) {
                return;
            }
            executor.execute(new Runnable() {
                @Override
                public void run() {
                    if (done) {
                        return;
                    }
                    try {
                        printStream.println(str);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });
        }

        void exit() {
            done = true;
            CloseUtils.close(printStream);
        }
    }
}
