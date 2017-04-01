package com.haoye.preanaware.bluetooth;

import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.haoye.preanaware.R;

import java.util.ArrayList;

/**
 * @brief device list adapter
 * @detail
 * @see BaseAdapter
 * @author Haoye
 * @date 2017/1/18
 */
public class DeviceListAdapter extends BaseAdapter {
    /**
     * layout inflater
     */
    private LayoutInflater inflater;
    /**
     * device list
     */
    private ArrayList<BluetoothDevice> deviceList = new ArrayList<>();

    private int curConnectedIndex = -1;

    public DeviceListAdapter(Context context) {
        inflater = LayoutInflater.from(context);
    }

    public int getCurConnectedIndex() {
        return curConnectedIndex;
    }

    public void setCurConnectedIndex(int curConnectedIndex) {
        this.curConnectedIndex = curConnectedIndex;
        this.notifyDataSetChanged();
    }

    public BluetoothDevice getDevice(int index) {
        return deviceList.get(index);
    }

    public void addDevice(BluetoothDevice device) {
        if (device != null && !deviceList.contains(device)) {
            deviceList.add(device);
            this.notifyDataSetChanged();
        }
    }

    public void removeDevice(int index) {
        deviceList.remove(index);
        this.notifyDataSetChanged();
    }

    public void removeDevice(BluetoothDevice device) {
        deviceList.remove(device);
        this.notifyDataSetChanged();
    }

    public void clear() {
        deviceList.clear();
        this.notifyDataSetChanged();
        curConnectedIndex = -1;
    }


    @Override
    public int getCount() {
        return deviceList.size();
    }

    @Override
    public Object getItem(int position) {
        return deviceList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ItemView itemView;
        if (convertView == null) {
            convertView      = inflater.inflate(R.layout.device_list_item, null);
            itemView         = new ItemView();
            itemView.name    = (TextView) convertView.findViewById(R.id.itemDeviceNameTxtV);
            itemView.address = (TextView) convertView.findViewById(R.id.itemDeviceAddressTxtV);
            convertView.setTag(itemView);
        }
        else {
            itemView = (ItemView) convertView.getTag();
        }

        BluetoothDevice device = deviceList.get(position);
        String name = device.getName();
        if (name == null || name.trim().length() == 0) {
            name = "<unknown>";
        }
        itemView.name.setText(name);
        itemView.address.setText(device.getAddress());
        if (position == curConnectedIndex) {
            itemView.name.setTextColor(Color.parseColor("#0066ff"));
            itemView.address.setTextColor(Color.parseColor("#0066ff"));
        }
        else {
            itemView.name.setTextColor(Color.parseColor("#334455"));
            itemView.address.setTextColor(Color.parseColor("#334455"));
        }

        return convertView;
    }

    private class ItemView {
        TextView name;
        TextView address;
    }
}
