package com.haoye.preanaware.viewer.chart;

import android.app.Activity;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.view.View;
import android.widget.TextView;

import com.haoye.preanaware.R;

/**
 * @brief
 * @detail
 * @see
 * @author Haoye
 * @date 2017-06-05
 */
public class LayerManager implements View.OnClickListener{
    private Activity activity;
    private View chartLayer;
    private View listLayer;
    private TextView chartTab;
    private TextView listTab;

    public LayerManager(@NonNull Activity activity) {
        this.activity = activity;
    }

    public void init() {
        chartLayer = activity.findViewById(R.id.viewer_chart_layer_wrap);
        listLayer  = activity.findViewById(R.id.viewer_list_layer_wrap);
        chartTab   = (TextView) activity.findViewById(R.id.viewMode_chart);
        listTab    = (TextView) activity.findViewById(R.id.viewMode_list);

        chartTab.setOnClickListener(this);
        listTab.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
        case R.id.viewMode_chart:
            chartLayer.setVisibility(View.VISIBLE);
            listLayer.setVisibility(View.GONE);
            chartTab.setTextColor(Color.parseColor("#32B6E6"));
            listTab.setTextColor(Color.parseColor("#e9f3f9"));
            break;
        case R.id.viewMode_list:
            chartLayer.setVisibility(View.GONE);
            listLayer.setVisibility(View.VISIBLE);
            chartTab.setTextColor(Color.parseColor("#e9f3f9"));
            listTab.setTextColor(Color.parseColor("#32B6E6"));
            break;
        default:
            break;
        }
    }
}
