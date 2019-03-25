package com.sukaidev.server;

import com.sukaidev.lib.utils.CloseUtils;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.net.SocketTimeoutException;

/**
 * Created by sukaidev on 2019/03/18.
 * �ͻ�����Ϣ�����߳�.
 */
public class ClientThread extends Thread {

    private boolean done = false;
    private final Socket socket; // ��ͻ��˽������ӵ�socket����
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

            // ��ȡ�ͻ�����Ϣ����ת���������ͻ��ˣ�ʵ��������Ч��
            do {
                try {
                    while ((len = inputStream.read(b)) != -1) {
                        String str = new String(b, 0, len);
                        System.out.println(str); // ��ӡ����Ļ�����ڲ���
                        //  ����Ϣ���͵�TCPServer�У����㴦��
                        clientHandlerCallback.onNewMessageArrived(this, str);
                    }
                } catch (SocketTimeoutException e) {
                    //noinspection UnnecessaryContinue
                    continue; // ��timeout
                }
            }
            while (!done);
        } catch (IOException e) {
            if (!done) {
                e.printStackTrace();
                System.out.println("�����쳣�Ͽ�" + e.getMessage());
                exitBySelf();
            }
        } finally {
            exit();
        }
    }

    /**
     * ����Ϣ���͵��ͻ���
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
        System.out.println("�ͻ������˳���" + socket.getInetAddress() + " P:" + socket.getPort());
    }


    /**
     * �����˳�ʱ��Ҫ���ѷ�����Ƴ��ÿͻ���
     */
    private void exitBySelf() {
        exit();
        clientHandlerCallback.onSelfClosed(this);
    }

    public interface ClientHandlerCallback {
        // ����رջص�
        void onSelfClosed(ClientThread handler);

        // �յ���Ϣ�ص�
        void onNewMessageArrived(ClientThread handler, String msg);
    }
}
