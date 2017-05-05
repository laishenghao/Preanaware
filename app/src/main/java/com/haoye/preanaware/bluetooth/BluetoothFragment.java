package com.haoye.preanaware.bluetooth;


import android.Manifest;
import android.bluetooth.*;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.haoye.preanaware.R;
import com.haoye.preanaware.viewer.FileUtil;
import com.haoye.preanaware.viewer.model.PreanFile;

import java.io.File;
import java.util.Calendar;

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
    private TextView          msgListTxtV;

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
        msgListTxtV     = (TextView) rootView.findViewById(R.id.msgListTxtV);
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
                BluetoothDevice device = deviceListAdapter.getDevice(position);
                byte[] command = createTransmitCommend(device.getName());
                ble.setHandshake(command);
                ble.connect(device);
                deviceListAdapter.setCurConnectedIndex(position);
                ble.stopScan();
            }
        });
    }

    private void initFileReceiver() {
        FileReceiver receiver = new FileReceiver(ble);
        receiver.setInfoDisplayer(new FileReceiver.ReceiverInfoDisplayer() {
            @Override
            public void onReceive(int length) {

            }

            @Override
            public void onReceived(String info) {
                addMessageThroughUiThread(info);
            }
        });
    }

    private void addMessageThroughUiThread(final String info) {
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                msgListTxtV.append("\n" + info);
            }
        });
    }

    private static byte[] createTransmitCommend(String deviceName) {
        Calendar calendar = Calendar.getInstance();

        byte[] commend = new byte[20];
        commend[0] = 'R';
        commend[1] = 'D';

        // start copy time
        long startCopyTime = getLatestFileTime(deviceName);
        if (startCopyTime != 0) {
            calendar.setTimeInMillis(startCopyTime);
            calendarToBytes(calendar, commend, 2);
        }

        // regulate time
        long regulateTime = System.currentTimeMillis();
        calendar.setTimeInMillis(regulateTime);
        calendarToBytes(calendar, commend, 14);

        return commend;
    }

    private static void calendarToBytes(Calendar calendar, byte[] bytes, int offset) {
        bytes[offset + 0] = (byte)(calendar.get(Calendar.YEAR) - 2000);
        bytes[offset + 1] = (byte)(calendar.get(Calendar.MONTH) + 1);
        bytes[offset + 2] = (byte)(calendar.get(Calendar.DAY_OF_MONTH));
        bytes[offset + 3] = (byte)(calendar.get(Calendar.HOUR_OF_DAY));
        bytes[offset + 4] = (byte)(calendar.get(Calendar.MINUTE));
        bytes[offset + 5] = (byte)(calendar.get(Calendar.SECOND));
    }

    private static long getLatestFileTime(String deviceName) {
        long time = 0;
        String path = FileUtil.getPreanFileHomePath() + "/ID_" + deviceName.substring(2, 6);
        File folder = new File(path);
        if (!folder.exists() || !folder.isDirectory()) {
            return time;
        }

        File[] files = new File(path).listFiles();
        if (files != null && files.length > 0) {
            for (File file : files) {
                PreanFile preanFile = PreanFile.create(file.getPath());
                if (preanFile == null) {
                    continue;
                }
                Calendar calendar = preanFile.getStartRecordTime();
                if (calendar == null) {
                    continue;
                }
                long nTime = calendar.getTimeInMillis()
                        + 1000 * ((preanFile.getRecordCount() - 1) * preanFile.getRecordInterval() + 1);
                if ( nTime> time) {
                    time = nTime;
                }
            }
        }
        return time;
    }

    private void initScanBtn() {
        scanImgV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (ble.isEnabled()) {
                    checkLocationPermission();
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

    private void checkLocationPermission() {
        if (Build.VERSION.SDK_INT >= 23) {
            int checkCallPhonePermission = ContextCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_COARSE_LOCATION);
            if(checkCallPhonePermission != PackageManager.PERMISSION_GRANTED){
                if(ActivityCompat.shouldShowRequestPermissionRationale(getActivity(), Manifest.permission.ACCESS_COARSE_LOCATION))
                    Toast.makeText(getContext(),R.string.ble_need_location, Toast.LENGTH_LONG).show();

                ActivityCompat.requestPermissions(getActivity() ,new String[]{Manifest.permission.ACCESS_COARSE_LOCATION},1);
            }
        }
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
        initFileReceiver();
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
