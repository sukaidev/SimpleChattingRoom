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
public class TCPServer implements ClientHandler.ClientHandlerCallback {
    private final int port;
    private ClientListener mListener;
    private List<ClientHandler> clientList = new ArrayList<>();
    private final ThreadPoolExecutor forwardingThreadPool; // ת�����̳߳�

    TCPServer(int port) {
        this.port = port;
        this.forwardingThreadPool = new ThreadPoolExecutor(1, 1, 0, TimeUnit.MILLISECONDS, new ArrayBlockingQueue<Runnable>(100));
    }

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

    public void stop() {
        if (mListener != null) {
            mListener.exit();
        }
        synchronized (this) {
            for (ClientHandler clientHandler : clientList) {
                clientHandler.exit();
            }
            clientList.clear();
        }

        // ֹͣ�̳߳�
        forwardingThreadPool.shutdownNow();
    }

    synchronized void broadcast(String str) {
        for (ClientHandler clientHandler : clientList) {
            clientHandler.send(str);
        }
    }

    @Override
    public synchronized void onSelfClosed(ClientHandler handler) {
        clientList.remove(handler);
    }

    @Override
    public void onNewMessageArrived(final ClientHandler handler, final String msg) {
        // ��ӡ����Ļ
        System.out.println("Received-" + handler.getClientInfo() + ":" + msg);

        forwardingThreadPool.execute(new Runnable() {
            @Override
            public void run() {
                synchronized (this) {
                    for (ClientHandler clientHandler : clientList) {
                        if (clientHandler.equals(handler)) {
                            // �����Լ�
                            continue;
                        }
                        // �������ͻ��˷�����Ϣ
                        clientHandler.send(msg);
                    }
                }
            }
        });
    }

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
                    continue;
                }
                try {
                    // �ͻ��˹����첽�߳�
                    ClientHandler clientHandler = new ClientHandler(client, TCPServer.this);
                    clientHandler.readAndPrint();
                    synchronized (TCPServer.this) {
                        clientList.add(clientHandler);
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
