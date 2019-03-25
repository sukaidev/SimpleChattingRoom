package com.sukaidev.client;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

/**
 * Created by sukaidev on 2019/03/24.
 * �ͻ��˲�����.
 */
public class Test implements Client.OnReadHandlerListener, Client.OnWriteHandlerListener {

    public static void main(String[] args) throws IOException {
        new Test().start();
    }

    private void start() {
        try {
            Client client = Client.start(this, this);
            write(client);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void write(Client client) throws IOException {
        // ��������������
        InputStream in = System.in;
        BufferedReader input = new BufferedReader(new InputStreamReader(in));
        do {
            // ���̶�һ�о�дһ�е�������
            String str = input.readLine();
            client.send(str);

            if ("00bye00".equalsIgnoreCase(str)) {
                break;
            }
        } while (true);
    }


    @Override
    public void onReceive(String message) {

    }

    @Override
    public void onSend(String message) {

    }
}
