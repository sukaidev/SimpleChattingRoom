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
 * TCP�����ṩ��.
 */
public class TCPServer implements ClientThread.ClientHandlerCallback {
    private final int port;     // ����˶˿�
    private ClientListener mListener;  // ���Ӽ���
    private List<ClientThread> clientList = new ArrayList<>();  //�ͻ��˼���
    private final ThreadPoolExecutor forwardingThreadPool; // ת�����̳߳�

    TCPServer(int port) {
        this.port = port;
        this.forwardingThreadPool = new ThreadPoolExecutor(1, 1, 0, TimeUnit.MILLISECONDS, new ArrayBlockingQueue<Runnable>(100));
    }

    /**
     * ������������.
     * @return �����ɹ�����true
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
     * �������ֹ���.
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

        // ֹͣ�̳߳�
        forwardingThreadPool.shutdownNow();
    }

    /**
     * �����������������͹㲥�����ڲ���
     */
    synchronized void broadcast(String str) {
        for (ClientThread clientThread : clientList) {
            clientThread.send(str);
        }
    }

    // �ͻ��˹رջص�
    @Override
    public synchronized void onSelfClosed(ClientThread handler) {
        clientList.remove(handler);
    }

    // ��Ϣת���ص�
    @Override
    public void onNewMessageArrived(final ClientThread handler, final String msg) {
        // ���ֻ��һ���ͻ�������
        if (clientList.size() == 1){
            return;
        }
        forwardingThreadPool.execute(new Runnable() {
                @Override
            public void run() {
                synchronized (this) {
                    for (ClientThread clientThread : clientList) {
                        if (clientThread.equals(handler)) {
                            // �����Լ�
                            continue;
                        }
                        // �������ͻ��˷�����Ϣ
                        clientThread.send(msg);
                    }
                }
            }
        });
    }

    /**
     * ������
     */
    private class ClientListener extends Thread {
        private ServerSocket server;
        private boolean done = false;

        private ClientListener(int port) throws IOException {
            server = new ServerSocket(port);
            System.out.println("��������Ϣ��" + server.getInetAddress() + " P:" + server.getLocalPort());
        }

        @Override
        public void run() {
            super.run();

            System.out.println("������׼������~");
            // �ȴ��ͻ�������
            do {
                Socket client;
                try {
                    client = server.accept();
                } catch (IOException e) {
                    // ��timeout
                    continue;
                }
                try {
                    // �ͻ��˹����첽�߳�
                    ClientThread clientThread = new ClientThread(client, TCPServer.this);
                    clientThread.start();
                    synchronized (TCPServer.this) {
                        // ���ͻ��˼��뼯����
                        clientList.add(clientThread);
                    }
                } catch (IOException e) {
                    System.out.println("�ͻ��������쳣��" + e.getMessage());
                }
            } while (!done);

            System.out.println("�������ѹرգ�");
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
