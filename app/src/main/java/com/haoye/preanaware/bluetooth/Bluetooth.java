package com.haoye.preanaware.bluetooth;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCharacteristic;
import android.content.Intent;
import android.support.annotation.NonNull;

import com.haoye.preanaware.utils.Constants;

/**
 * @brief
 * @detail
 * @see
 * @author Haoye
 * @date 2017/1/17
 */
public class Bluetooth {
    private BluetoothAdapter adapter;
    private Activity activity;

    public Bluetooth(@NonNull Activity activity) {
        this.activity = activity;
        this.adapter  = BluetoothAdapter.getDefaultAdapter();
    }

    public boolean isSupported() {
        return adapter != null;
    }

    public boolean isEnabled() {
        return isSupported() && adapter.isEnabled();
    }

    public void invokeOpenActivity() {
        if (isSupported() && !isEnabled()) {
            Intent intent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            activity.startActivityForResult(intent, Constants.BLUETOOTH_REQUEST_ENABLE_CODE);
        }
    }

    public boolean open() {
        if (isSupported()) {
            return adapter.enable();
        }
        return false;
    }

    public void close() {
        if (isEnabled()) {
            adapter.disable();
        }
    }

    public boolean scan() {
        return adapter.startDiscovery();
    }

    public void connect(BluetoothDevice device) {

    }

//    private class ConnectThread extends Thread {
//        private BluetoothSocket  socket;
//        private BluetoothDevice  device;
//        private BluetoothAdapter adapter;
//
//        ConnectThread(BluetoothDevice device, BluetoothAdapter adapter) {
//            this.device  = device;
//            this.adapter = adapter;
//            try {
//                socket = device.createRfcommSocketToServiceRecord(UUID.fromString(Constants.BLUETOOTH_UUID));
//            } catch (IOException e) {
//                e.printStackTrace();
//            }
//        }
//
//        @Override
//        public void run() {
////            adapter.cancelDiscovery();
//
//            try {
//                if (device.getBondState() != BluetoothDevice.BOND_BONDED) {
//                    Method m = BluetoothDevice.class.getMethod("createBond");
//                    m.invoke(device);
//                }
//                socket.connect();
//                new TransmitThread(socket).start();
//            } catch (IOException e) {
//                try {
//                    socket.close();
//                } catch (IOException e1) {
//                    e1.printStackTrace();
//                }
//            } catch (Exception e) {
//                e.printStackTrace();
//            }
////            byte[] data = new byte[]{'a', 'b', 'c', 'd'};
////            try {
////                socket.getOutputStream().write(data);
////            } catch (IOException e) {
////                e.printStackTrace();
////            }
//        }
//    }
//
//    private class TransmitThread extends Thread {
//        private BluetoothSocket socket;
//        private InputStream     inStream;
//        private OutputStream    outStream;
//
//        TransmitThread(BluetoothSocket socket) {
//            this.socket = socket;
//            try {
//                this.inStream = socket.getInputStream();
//                this.outStream = socket.getOutputStream();
//            }
//            catch (Exception e) {
//                e.printStackTrace();
//            }
//        }
//
//        @Override
//        public void run() {
//            byte[] buffer = new byte[1024];
//            int cnt = 0;
//            while (true) {
//                try {
//                    cnt = inStream.read(buffer);
//                    //todo:
//                } catch (IOException e) {
//                    e.printStackTrace();
//                }
//            }
//        }
//
//        public void write(byte[] data) {
//            try {
//                outStream.write(data);
//            } catch (IOException e) {
//                e.printStackTrace();
//            }
//        }
//
//        public void cancel() {
//            try {
//                socket.close();
//            } catch (IOException e) {
//                e.printStackTrace();
//            }
//        }
//    }
}
