package com.sukaidev.server;

import com.sukaidev.lib.Constants;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

/**
 * Created by sukaidev on 2019/03/18.
 * ��������������.
 */
public class Server {


    public static void main(String[] args) {
        TCPServer server = new TCPServer(Constants.PORT_TCP_SERVER);
        server.start();
 /*       if (server.start()) {
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(System.in));
            String str;
            while (true) {
                try {
                    str = bufferedReader.readLine();
                    server.broadcast(str);
                } catch (IOException e) {
                    e.printStackTrace();
                    break;
                }
            }
        } else {
            System.out.println("Start tcp server failed!");
        }*/

    }

}
