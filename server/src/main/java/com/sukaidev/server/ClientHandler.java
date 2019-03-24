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
        System.out.println("�¿ͻ������ӣ�" + clientInfo);
    }

    public String getClientInfo() {
        return clientInfo;
    }

    public void exit() {
        readHandler.exit();
        writeHandler.exit();
        CloseUtils.close(socket);
        System.out.println("�ͻ������˳���" + socket.getInetAddress() + " P:" + socket.getPort());
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
        // ����رջص�
        void onSelfClosed(ClientHandler handler);

        // �յ���Ϣ�ص�
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
                // �õ������������ڽ�������
                BufferedReader socketInput = new BufferedReader(new InputStreamReader(inputStream));

                do {
                    // �ͻ����õ�һ������
                    String str = socketInput.readLine();
                    if (str == null) {
                        System.out.println("�ͻ������޷���ȡ���ݣ�");
                        exitBySelf();
                        break;
                    }
                    // ��ӡ����Ļ�����������ݳ���
                    System.out.println(str);
                    clientHandlerCallback.onNewMessageArrived(ClientHandler.this, str);
                } while (!done);

                socketInput.close();

            } catch (Exception e) {
                if (!done) {
                    System.out.println("�����쳣�Ͽ�" + e.getMessage());
                    exitBySelf();
                }
            } finally {
                // ���ӹر�
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
