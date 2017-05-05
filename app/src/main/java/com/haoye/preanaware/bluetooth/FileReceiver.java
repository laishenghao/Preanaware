package com.haoye.preanaware.bluetooth;

import android.util.Log;

import com.haoye.preanaware.utils.Converter;
import com.haoye.preanaware.viewer.FileUtil;
import com.haoye.preanaware.viewer.model.PreanFileManager;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;

/**
 * @brief
 * @detail
 * @see
 * @author Haoye
 * @date 2017-04-25
 */

public class FileReceiver implements Ble.OnReceivedDataListener {
    private static final String TAG = "FileReceiver";
    private Ble ble;
    private OutputStream outputStream = null;
    private File file = null;
    private ReceiverInfoDisplayer infoDisplayer;
    private int pkgLength = 0;
    private int receivedCnt = 0;
    private byte[] buffer = new byte[256];

    public void setInfoDisplayer(ReceiverInfoDisplayer infoDisplayer) {
        this.infoDisplayer = infoDisplayer;
    }

    public FileReceiver(Ble ble) {
        this.ble = ble;
        ble.setOnReceivedDataListener(this);
    }

    @Override
    public void onReceived(byte[] data) {
        if (receivedCnt == 0 && data[0] == 0x5a) {
            onPackageStart(data);
        }
        else if (receivedCnt > 0){
            System.arraycopy(data, 0, buffer, receivedCnt, data.length);
            receivedCnt += data.length;
            if (buffer[pkgLength] == 0xaa) {
                writeBufferToFile();
                resetPackageInfo();
                ble.write(new byte[]{0x5a});
            }
            else if (receivedCnt > pkgLength) {
                ble.write(new byte[]{0x7a});
            }
        }
    }

    private void resetPackageInfo() {
        receivedCnt = 0;
        pkgLength = 0;
    }

    private void onPackageStart(byte[] data) {
        pkgLength = 0x00ff & data[1];
        buffer[pkgLength] = 0;
        System.arraycopy(data, 2, buffer, receivedCnt, data.length-2);
        receivedCnt += data.length - 2;
    }

    private void writeBufferToFile() {
        byte[] head = new byte[6];
        System.arraycopy(buffer, 0, head, 0, head.length);
        String temp = Converter.bytesToString(head);
        if (temp.equals("BLE1.0")) {
            resetOutputStream();
            saveOldFile();
            file = new File(FileUtil.getTempFileDefaultPath() + "/" + System.currentTimeMillis() + ".DAT");
            try {
                outputStream = new FileOutputStream(file);
            } catch (FileNotFoundException e) {
                Log.e(TAG, "create outputStream error");
            }
        }

        try {
            outputStream.write(buffer, 0, pkgLength);
        } catch (IOException e) {
            Log.e(TAG, "outputStream write error");
        }
    }

    private void saveOldFile() {
        if (file != null && file.exists()) {
            String path = PreanFileManager.restore(file.getPath());
            if (path == null) {
                Log.e(TAG, "file restore error");
                return;
            }
            int last = path.lastIndexOf("/");
            if (infoDisplayer != null) {
                infoDisplayer.onReceived(path.substring(last, path.length()));
            }
            file.delete();
            file = null;
        }
    }

    private void resetOutputStream() {
        if (outputStream != null) {
            try {
                outputStream.flush();
                outputStream.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
            outputStream = null;
        }
    }

    public interface ReceiverInfoDisplayer {
        void onReceive(int length);

        void onReceived(String info);
    }

}