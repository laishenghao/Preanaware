package com.haoye.preanaware.viewer.chart;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.haoye.preanaware.R;
import com.haoye.preanaware.viewer.model.PreanFile;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Formatter;

/**
 * @brief
 * @detail
 * @see
 * @author Haoye
 * @date 2017-05-13
 */

public class ListLayer {
    private Activity  activity;
    private PreanFile preanFile;
    private int[]     data;
    private int intervalMs = 0;
    private long basicTimeMillis = 0;
    private int denominator = 1;

    private ListLayer(Activity activity, PreanFile preanFile) {
        this.activity = activity;
        this.preanFile = preanFile;
    }

    public static ListLayer create(Activity activity, PreanFile preanFile) {
        ListLayer listLayer = new ListLayer(activity, preanFile);
        listLayer.init();
        return listLayer;
    }

    private void init() {
        initPreanData();
        initListView();
    }

    private void initPreanData() {
        if (preanFile != null) {
            data = preanFile.getChannelOneRecords();
            basicTimeMillis = preanFile.getStartRecordTime().getTimeInMillis();
            intervalMs = preanFile.getRecordInterval() * 1000;
            int cnt = preanFile.getChannel1DecimalCount();
            for (int i = 0; i < cnt; i++) {
                denominator *= 10;
            }
        }
    }

    private void initListView() {
        ListView listView = (ListView) activity.findViewById(R.id.preanDataListV);
        listView.setAdapter(new PreanListAdapter(activity));
        listView.setHeaderDividersEnabled(true);
    }

    private class PreanListAdapter extends BaseAdapter {
        private LayoutInflater inflater;

        public PreanListAdapter(Context context) {
            this.inflater = LayoutInflater.from(context);
        }

        @Override
        public int getCount() {
            if (data != null) {
                return data.length;
            }
            return 0;
        }

        @Override
        public Object getItem(int position) {
            if (data != null && position >= 0 && position < data.length) {
                return data[position];
            }
            return null;
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            convertView = inflater.inflate(R.layout.prean_data_list_item, null);
            TextView indexTxtV = (TextView) convertView.findViewById(R.id.itemIndex);
            indexTxtV.setText(String.valueOf(position + 1));

            TextView valueTxtV = (TextView) convertView.findViewById(R.id.itemValue);
            String pressure = new Formatter().format("%.2f", data[position] * 1.0 / denominator).toString() + " kPa";
            valueTxtV.setText(pressure);

            TextView timeTxtV = (TextView) convertView.findViewById(R.id.itemTime);
            int dt = position * intervalMs;
            Calendar calendar = Calendar.getInstance();
            calendar.setTimeInMillis(basicTimeMillis + dt);
            SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            String time = format.format(calendar.getTime());
            timeTxtV.setText(time);

            return convertView;
        }
    }
}
