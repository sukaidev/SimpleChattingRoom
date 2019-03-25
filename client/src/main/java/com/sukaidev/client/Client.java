package com.sukaidev.client;

import com.sukaidev.lib.Constants;
import com.sukaidev.lib.utils.CloseUtils;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Inet4Address;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * Created by sukaidev on 2019/03/19.
 * �ͻ�������.
 */
public class Client {

    private final Socket socket;
    private final ReadHandler readHandler;
    private final WriteHandler writeHandler;

    private Client(Socket socket, ReadHandler readHandler, WriteHandler writeHandler) {
        this.socket = socket;
        this.readHandler = readHandler;
        this.writeHandler = writeHandler;
    }

    /**
     * ������Ϣ�������
     * @param msg
     */
    public void send(String msg) {
        writeHandler.send(msg);
    }

    /**
     * �ͻ����������.
     */
    public static Client start(OnReadHandlerListener readHandlerListener, OnWriteHandlerListener writeHandlerListener) throws IOException {
        Socket socket = new Socket();

        socket.setSoTimeout(3000);

        socket.connect(new InetSocketAddress(Inet4Address.getByName(Constants.ADDRESS_TCP_SERVER), Constants.PORT_TCP_SERVER), 3000);

        System.out.println("�ѷ�����������ӣ��������������~");
        System.out.println("�ͻ�����Ϣ��" + socket.getLocalAddress() + " P:" + socket.getLocalPort());
        System.out.println("��������Ϣ��" + socket.getInetAddress() + " P:" + socket.getPort());

        try {
            ReadHandler readHandler = new ReadHandler(socket.getInputStream(), readHandlerListener);
            readHandler.start();

            WriteHandler writeHandler = new WriteHandler(socket.getOutputStream(), writeHandlerListener);

            return new Client(socket, readHandler, writeHandler);
        } catch (Exception e) {
            System.out.println("�쳣�ر�");
        }
        return null;
    }

    public void exit() {
        readHandler.exit();
        writeHandler.exit();
        CloseUtils.close(socket);
        System.out.println("�ͻ������˳�~");
    }

    /**
     * �ӷ���˶�ȡ��Ϣ.
     */
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
                int len;
                byte[] b = new byte[1024 * 3];

                do {
                    try {
                        while ((len = inputStream.read(b)) != -1) {
                            String str = new String(b, 0, len);
                            // �ص�onReceive������app�յ���Ϣ
                            listener.onReceive(str);
                        }
                    } catch (SocketTimeoutException e) {
                        //noinspection UnnecessaryContinue
                        continue;
                    }
                } while (!done);

            } catch (Exception e) {
                if (!done) {
                    e.printStackTrace();
                    System.out.println("�����쳣�Ͽ�" + e.getMessage());
                }
            } finally {
                exit();
            }
        }

        void exit() {
            done = true;
            CloseUtils.close(inputStream);
        }
    }

    /**
     * ������Ϣ�������.
     */
    static class WriteHandler {
        private boolean done = false;
        private final OutputStream outputStream;
        private final OnWriteHandlerListener listener;
        private final ExecutorService executor;

        WriteHandler(OutputStream os, OnWriteHandlerListener listener) {
            this.outputStream = os;
            this.listener = listener;
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
                        outputStream.write(str.getBytes());
                        // �ص�onSend������app��Ϣ���ͳɹ�
                        listener.onSend(str);
                    } catch (Exception ignored) {
                    }
                }
            });
        }

        void exit() {
            done = true;
            executor.shutdownNow();
            CloseUtils.close(outputStream);
        }
    }

    public interface OnReadHandlerListener {
        void onReceive(String message);
    }

    public interface OnWriteHandlerListener {
        void onSend(String message);
    }

}