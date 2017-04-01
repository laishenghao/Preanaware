package com.haoye.preanaware.utils;

import java.util.ArrayList;

/**
 * @author Haoye
 * @brief
 * @detail
 * @date 2017/2/26
 * @see
 */

public class Converter {

    public static String bytesToString(byte[] data) {
        StringBuilder builder = new StringBuilder(data.length);
        for (byte b : data) {
            builder.append((char)b);
        }
        return builder.toString();
    }

    public static String bytesToString(ArrayList<byte[]> data) {
        StringBuilder builder = new StringBuilder(data.size() * 20);
        for (byte[] item : data) {
            String str = bytesToString(item);
            builder.append(str);
        }
        return builder.toString();
    }

    /**
     * 低位在前，高位在后
     * @param bytes
     * @return result
     */
    public static int bytesToInt(byte[] bytes) {
        int result = 0;
        for (int i = bytes.length - 1; i >= 0; i--) {
            int temp = (0x00ff & bytes[i]);
            result = (result<<8) + temp;
        }
        return result;
    }
}
