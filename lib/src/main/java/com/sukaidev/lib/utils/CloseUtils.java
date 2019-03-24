package com.sukaidev.lib.utils;

import java.io.Closeable;
import java.io.IOException;

/**
 * Created by sukaidev on 2019/03/18.
 */
public class CloseUtils {

    public static void close(Closeable... closeables) {
        if (closeables == null) {
            return;
        }
        for (Closeable closeable : closeables) {
            try {
                closeable.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
