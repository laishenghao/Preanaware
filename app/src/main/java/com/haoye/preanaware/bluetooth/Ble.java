package com.haoye.preanaware.bluetooth;

import android.annotation.TargetApi;
import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCallback;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattDescriptor;
import android.bluetooth.BluetoothGattService;
import android.bluetooth.BluetoothProfile;
import android.content.Intent;
import android.os.Build;
import android.support.annotation.NonNull;
import android.util.Log;

import com.haoye.preanaware.utils.Constants;

import java.util.UUID;

/**
 * @brief bluetooth of light energy
 * @detail
 * @see
 * @author Haoye
 * @date 2017/1/21
 */
public class Ble {
    private BluetoothAdapter adapter;
    private Activity         activity;
    private BluetoothGatt    gatt;
    private OnReceivedDataListener onReceivedDataListener;
    private BluetoothGattCharacteristic gattCharacteristic;
//    private boolean isConnected = false;
    private BluetoothDevice device = null;

    public boolean isConnected() {
        return device != null;
    }

    private BluetoothGattCallback gattCallback = new BluetoothGattCallback() {
        @Override
        public void onConnectionStateChange(BluetoothGatt gatt, int status, int newState) {
            if (newState == BluetoothProfile.STATE_CONNECTED) {
                gatt.discoverServices();
            }
            else if (newState == BluetoothProfile.STATE_DISCONNECTED){
                Ble.this.device = null;
                Ble.this.gatt   = null;
            }
        }

        @Override
        public void onServicesDiscovered(BluetoothGatt gatt, int status) {
            if (status == BluetoothGatt.GATT_SUCCESS) {
                BluetoothGattService service = gatt.getService(UUID.fromString(Constants.SERVICE_UUID));
                gattCharacteristic = service.getCharacteristic(UUID.fromString(Constants.CHARACTER_UUID));
                gatt.setCharacteristicNotification(gattCharacteristic, true);

                BluetoothGattDescriptor descriptor = gattCharacteristic.getDescriptor(
                        UUID.fromString(Constants.CLIENT_UUID));
                descriptor.setValue(BluetoothGattDescriptor.ENABLE_NOTIFICATION_VALUE);
                gatt.writeDescriptor(descriptor);
            }
        }

        @Override
        public void onCharacteristicChanged(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic) {
            if (characteristic != null && onReceivedDataListener != null) {
                byte[] bytes = characteristic.getValue();
                onReceivedDataListener.onReceived(bytes);
            }
        }

    };

    public Ble(@NonNull Activity activity) {
        this.activity = activity;
        this.adapter  = BluetoothAdapter.getDefaultAdapter();
    }

    public void setOnReceivedDataListener(OnReceivedDataListener listener) {
        this.onReceivedDataListener = listener;
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

    public void scan() {
        adapter.startDiscovery();
    }

    public void stopScan() {
        adapter.cancelDiscovery();
    }

    public void connect(BluetoothDevice device) {
        if (this.device != null && this.device.equals(device)) {
            return;
        }
        this.device = device;
        this.gatt = device.connectGatt(activity, false, gattCallback);
    }

    public void disconnect() {
        if (gatt != null) {
            gatt.close();
            gatt = null;
        }
    }

    public void write(byte[] data) {
        if (data != null && gatt != null && isConnected() && gattCharacteristic != null) {
            gattCharacteristic.setValue(data);
            gatt.writeCharacteristic(gattCharacteristic);
        }
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public boolean setMTU(int size){
        if ( size > 20 &&Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP){
                return gatt.requestMtu(size);
        }
        return false;
    }

    public interface OnReceivedDataListener {
        void onReceived(byte[] data);
    }

}
