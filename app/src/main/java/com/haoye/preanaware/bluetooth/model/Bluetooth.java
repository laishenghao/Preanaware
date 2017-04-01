package com.haoye.preanaware.bluetooth.model;

import android.bluetooth.BluetoothDevice;

/**
 * @author Haoye
 * @brief
 * @detail
 * @date 2017/1/21
 * @see
 */

public interface Bluetooth {

    boolean isSupported();

    boolean isEnabled();

    void invokeOpenActivity();

    boolean open();

    void close();

    void scan();

    void cancelScan();

    void connect(BluetoothDevice device);

    void disconnect();

    void write(byte[] data);

    void setOnReceivedDataListener(OnReceivedDataListener onReceivedDataListener);



    interface OnReceivedDataListener {
        void onReceived(byte[] data);
    }
}
