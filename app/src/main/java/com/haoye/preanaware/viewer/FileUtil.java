package com.haoye.preanaware.viewer;

import android.os.Environment;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * @brief
 * @detail
 * @see
 * @author Haoye
 * @date 2017/2/11
 */

public class FileUtil {

    public static boolean isExternalStorageWritable() {
        String state = Environment.getExternalStorageState();
        return Environment.MEDIA_MOUNTED.equals(state);
    }

    public static boolean isExternalStorageReadable() {
        String state = Environment.getExternalStorageState();
        return Environment.MEDIA_MOUNTED.equals(state) ||
                Environment.MEDIA_MOUNTED_READ_ONLY.equals(state);
    }

    public static String getPublicDocumentPath() {
        String path = "";
        if (isExternalStorageReadable()) {
            path = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS).getPath();
        }
        return path;
    }

    public static String getDefaultSdCardPath() {
        String path = "";
        File file = new File(Environment.getExternalStorageDirectory(), "_test.txt");
        if (file.exists()) {
            path = file.getAbsolutePath();
        }
        return path;
    }

    public static String getPreanFileHomePath() {
        String docPath = getPublicDocumentPath();
        String temp = docPath + "/" + "压力数据";
        if (createDir(temp)) {
            return temp;
        }
        return docPath;
    }

    public static String getTempFileDefaultPath() {
        String docPath = getPublicDocumentPath();
        String temp = docPath + "/" + "temp";
        if (createDir(temp)) {
            return temp;
        }
        return docPath;
    }

    public static boolean createDir(String path) {
        File file = new File(path);
        return file.exists() && file.isDirectory()
                || file.mkdir();
    }

    public static byte[] readFile(String path) {
        try {
            File file = new File(path);
            FileInputStream stream = new FileInputStream(file);
            byte[] bytes = new byte[stream.available()];
            stream.read(bytes);
            stream.close();
            return bytes;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static int pump(InputStream in, byte[] out) {
        int total = 0;
        try {
            while (total < out.length) {
                int read = in.read(out, total, out.length - total);
                if (read == -1) {
                    break;
                }
                total += read;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return total;
    }

    public static boolean saveFile(String path, byte[] buffer) {
        File file = new File(path);
        try {
            FileOutputStream stream = new FileOutputStream(file);
            stream.write(buffer);
            stream.flush();
            stream.close();
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

    public static boolean cutAndPaste(String srcPath, String desPath) {

        return true;
    }

    public static String bytesToString(byte[] data) {
        StringBuilder builder = new StringBuilder(data.length);
        for (byte b : data) {
            builder.append((char)b);
        }
        return builder.toString();
    }


}
