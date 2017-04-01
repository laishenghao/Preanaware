package com.haoye.preanaware.bluetooth;


import android.bluetooth.*;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Switch;
import android.widget.Toast;

import com.haoye.preanaware.R;
import com.haoye.preanaware.utils.Constants;

/**
 * @brief bluetooth fragment
 * @detail
 * @see Fragment
 * @author Haoye
 * @date 2017/1/16
 */
public class BluetoothFragment extends Fragment {
    private View              rootView;
    private Switch            bluetoothSwView;
    private ImageView         scanImgV;
    private ListView          deviceListView;
    private DeviceListAdapter deviceListAdapter;
    private Ble               ble;

    private BroadcastReceiver bluetoothStateReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent != null) {
                switch (intent.getAction()) {
                case BluetoothAdapter.ACTION_STATE_CHANGED:
                    invokeBluetoothSwitchView();
                    if (!bluetoothSwView.isChecked()) {
                        deviceListAdapter.clear();
                    }
                    break;
                case BluetoothAdapter.ACTION_DISCOVERY_STARTED:
                    deviceListAdapter.clear();
                    Toast.makeText(getContext(), "扫描开始...", Toast.LENGTH_SHORT).show();
                    break;
                case BluetoothAdapter.ACTION_DISCOVERY_FINISHED:
                    Toast.makeText(getContext(), "扫描结束...", Toast.LENGTH_SHORT).show();
                    break;
                case BluetoothDevice.ACTION_FOUND:
                    BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                    deviceListAdapter.addDevice(device);
                    break;
                case BluetoothAdapter.ACTION_CONNECTION_STATE_CHANGED:
                    if (deviceListAdapter.getCurConnectedIndex() >= 0) {
                        deviceListAdapter.setCurConnectedIndex(-1);
                    }
                    break;
                }
            }
        }
    };

    public static BluetoothFragment create(Ble bluetooth) {
        BluetoothFragment fragment = new BluetoothFragment();
        fragment.ble = bluetooth;
        return fragment;
    }

    public BluetoothFragment() {
        // Required empty public constructor
    }

    /**
     * executed in <code>onCreate</code> function
     */
    private void findView() {
        rootView        = getActivity().getLayoutInflater().inflate(R.layout.fragment_bluetooth, null);
        bluetoothSwView = (Switch) rootView.findViewById(R.id.bluetoothToggleSw);
        deviceListView  = (ListView) rootView.findViewById(R.id.deviceListV);
        scanImgV        = (ImageView) rootView.findViewById(R.id.scanImgV);
    }

    private void initBroadcastReceiver() {
        IntentFilter filter = new IntentFilter();
        filter.addAction(BluetoothAdapter.ACTION_STATE_CHANGED);
        filter.addAction(BluetoothAdapter.ACTION_CONNECTION_STATE_CHANGED);
        filter.addAction(BluetoothAdapter.ACTION_DISCOVERY_STARTED);
        filter.addAction(BluetoothAdapter.ACTION_DISCOVERY_FINISHED);
        filter.addAction(BluetoothDevice.ACTION_FOUND);
        getActivity().registerReceiver(bluetoothStateReceiver, filter);
    }

    private void initDeviceList() {
        deviceListAdapter = new DeviceListAdapter(getContext());
        deviceListView.setAdapter(deviceListAdapter);
        deviceListView.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
        deviceListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                ble.connect(deviceListAdapter.getDevice(position));
                deviceListAdapter.setCurConnectedIndex(position);
                ble.stopScan();
            }
        });
    }

    private void initScanBtn() {
        scanImgV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (ble.isEnabled()) {
                    ble.scan();
                }
                else {
                    Toast.makeText(getContext(), "蓝牙不可用或未开启！", Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    private void initBluetoothSwitchView() {
        bluetoothSwView.setChecked(ble.isEnabled());
        bluetoothSwView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (bluetoothSwView.isChecked()) {
                    if (!ble.open()) {
                        ble.invokeOpenActivity();
                    }
                }
                else {
                    ble.close();
                    deviceListAdapter.clear();
                }
            }
        });
    }

    public void invokeBluetoothSwitchView() {
        bluetoothSwView.setChecked(ble.isEnabled());
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        findView();
        initDeviceList();
        initBluetoothSwitchView();
        initScanBtn();
        initBroadcastReceiver();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return rootView;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ViewGroup parent = (ViewGroup) rootView.getParent();
        if (parent != null) {
            parent.removeAllViewsInLayout();
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        getActivity().unregisterReceiver(bluetoothStateReceiver);
    }
}
