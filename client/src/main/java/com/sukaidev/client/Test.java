package com.sukaidev.client;

import java.io.IOException;

/**
 * Created by sukaidev on 2019/03/24.
 */
public class Test implements Client.OnReadHandlerListener {

    public static void main(String[] args) throws IOException {
        new Test().start();
    }

    private void start() {
        try {
            Client client = Client.start(this);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }



    @Override
    public void onReceive(String message) {

    }
}
