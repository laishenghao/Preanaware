package com.haoye.preanaware.transmit.model;

import com.haoye.preanaware.bluetooth.Ble;
import com.haoye.preanaware.utils.Converter;
import com.haoye.preanaware.viewer.FileUtil;
import com.haoye.preanaware.viewer.model.PreanFileManager;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;

/**
 * @brief
 * @detail
 * @see
 * @author Haoye
 * @date 2017/2/26
 */

public class MessageReceiver implements Ble.OnReceivedDataListener{
    private int type = Message.TYPE_RECV_TEXT;
    private int receivedCount = -1;
    private int totalPackage = 0;
    private ArrayList<byte[]> buffer = new ArrayList<>();
    private OnTextReceiveListener textReceiveListener = null;
    private OnFileReceiveListener fileReceiveListener = null;
    private FileOutputStream tempOutputStream = null;
    private File tempFile = null;

    public MessageReceiver() {

    }

    public void setTextReceiveListener(OnTextReceiveListener textReceiveListener) {
        this.textReceiveListener = textReceiveListener;
    }

    public void setFileReceiveListener(OnFileReceiveListener fileReceiveListener) {
        this.fileReceiveListener = fileReceiveListener;
    }

    public void handleMessage(byte[] data) {
        receivedCount++;
        if (receivedCount == 0) {
            handleFirstPackage(data);
        }
        else {
            if (type == Message.TYPE_RECV_TEXT) {
                handleTextPackage(data);
            }
            else {
                handleFilePackage(data);
            }
        }
    }

    private void handleFirstPackage(byte[] data) {
        this.type     = Converter.bytesToInt(new byte[]{data[0]});
        byte[] bytes  = {data[1], data[2], data[3], data[4]};
        totalPackage  = Converter.bytesToInt(bytes);
        buffer.clear();

        if (this.type == Message.TYPE_RECV_FILE) {
            oldPercent = 0;
            tempFile = new File(FileUtil.getTempFileDefaultPath() + "/" + System.currentTimeMillis() + ".DAT");
            try {
                tempOutputStream = new FileOutputStream(tempFile, true);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
            // callback
            if (fileReceiveListener != null) {
                fileReceiveListener.onStart("正在传输文件...", totalPackage * 20);
            }
        }
    }

    private void handleTextPackage(byte[] data) {
        buffer.add(data);
        if (receivedCount == totalPackage) {
            if (textReceiveListener != null) {
                String text = Converter.bytesToString(buffer);
                textReceiveListener.onReceived(text);
            }
            buffer.clear();
            receivedCount = -1;
        }
    }

    private int oldPercent = 0;
    private void handleFilePackage(byte[] data) {
        buffer.add(data);
        int newPercent = receivedCount * 100 / totalPackage;
        if (newPercent - oldPercent >= 5 && fileReceiveListener != null) {
            fileReceiveListener.onReceive(newPercent);
            oldPercent = newPercent;
        }

        // on finish
        if (receivedCount == totalPackage) {
            try {
                for (byte[] item : buffer) {
                    tempOutputStream.write(item);
                }
                tempOutputStream.flush();
                tempOutputStream.close();
                tempOutputStream = null;
                String desPath = PreanFileManager.restore(tempFile.getPath());
                // callback
                if (fileReceiveListener != null) {
                    fileReceiveListener.onReceived(desPath);
                }
                tempFile.delete();
                tempFile = null;
            } catch (IOException e) {
                if (fileReceiveListener != null) {
                    fileReceiveListener.onError("传输或存储失败!");
                }
                e.printStackTrace();
            }
            receivedCount = -1;
            oldPercent = 0;
            buffer.clear();
        }
    }

    @Override
    public void onReceived(byte[] data) {
        handleMessage(data);
    }

    public interface OnTextReceiveListener {
        void onReceived(String msg);
    }

    public interface OnFileReceiveListener {
        void onStart(String title, int len);

        void onReceive(int progress);

        void onError(String info);

        void onReceived(String path);
    }


}
