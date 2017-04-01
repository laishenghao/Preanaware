package com.haoye.preanaware.viewer.chart;

import android.graphics.Color;
import android.view.View;
import android.widget.TextView;

import com.haoye.preanaware.R;

/**
 * @brief
 * @detail
 * @see
 * @author Haoye
 * @date 2017-03-04
 */

public class ChartControlBar implements View.OnClickListener{
    private ChartActivity chartActivity;
    private LineChart lineChart;
    private View rootView;
    private TextView exitBtn;
    private TextView resetBtn;
    private TextView zoomInXBtn;
    private TextView zoomOutXBtn;
    private TextView zoomInYBtn;
    private TextView zoomOutYBtn;

    private float totalScaleX = 1.0f;
    private float totalScaleY = 1.0f;
    private int disableColor;
    private int enableColor;

    public ChartControlBar(ChartActivity chartActivity, View chartControlBar, LineChart lineChart) {
        this.chartActivity = chartActivity;
        this.rootView = chartControlBar;
        this.lineChart = lineChart;
    }

    public void init() {
        exitBtn     = (TextView) rootView.findViewById(R.id.exitChartBtn);
        resetBtn    = (TextView) rootView.findViewById(R.id.resetChartBtn);
        zoomInXBtn  = (TextView) rootView.findViewById(R.id.zoomInChartXBtn);
        zoomOutXBtn = (TextView) rootView.findViewById(R.id.zoomOutChartXBtn);
        zoomInYBtn  = (TextView) rootView.findViewById(R.id.zoomInChartYBtn);
        zoomOutYBtn = (TextView) rootView.findViewById(R.id.zoomOutChartYBtn);

        exitBtn.setOnClickListener(this);
        resetBtn.setOnClickListener(this);
        zoomInXBtn.setOnClickListener(this);
        zoomOutXBtn.setOnClickListener(this);
        zoomInYBtn.setOnClickListener(this);
        zoomOutYBtn.setOnClickListener(this);

        disableColor = Color.parseColor("#5f6264");
        enableColor  = Color.parseColor("#32B6E6");
        disable(resetBtn);
        disable(zoomOutXBtn);
        disable(zoomOutYBtn);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
        case R.id.exitChartBtn:
            exitChart();
            break;
        case R.id.resetChartBtn:
            resetChart();
            break;
        case R.id.zoomInChartXBtn:
            zoomInChartX();
            break;
        case R.id.zoomOutChartXBtn:
            zoomOutChartX();
            break;
        case R.id.zoomInChartYBtn:
            zoomInChartY();
            break;
        case R.id.zoomOutChartYBtn:
            zoomOutChartY();
            break;
        default:
            break;
        }
    }

    private void enable(TextView btn) {
        btn.setEnabled(true);
        btn.setTextColor(enableColor);
    }

    private void disable(TextView btn) {
        btn.setEnabled(false);
        btn.setTextColor(disableColor);
    }

    private void zoomOutChartX() {
        float scale = 5.0f / 6.0f;
        lineChart.scaleX(scale);
        totalScaleX *= scale;

        enable(zoomInXBtn);
        if (totalScaleX < 1.001f) {
            disable(zoomOutXBtn);
            totalScaleX = 1.0f;
            if (totalScaleY < 1.001f) {
                disable(resetBtn);
            }
        }
    }

    private void zoomInChartX() {
        float scale = 6.0f / 5.0f;
        lineChart.scaleX(scale);
        totalScaleX *= scale;

        enable(resetBtn);
        enable(zoomOutXBtn);
        if (totalScaleX > 4.99f) {
            disable(zoomInXBtn);
        }
    }

    private void zoomOutChartY() {
        float scale = 5.0f / 6.0f;
        lineChart.scaleY(scale);
        totalScaleY *= scale;

        enable(zoomInYBtn);
        if (totalScaleY < 1.001f) {
            disable(zoomOutYBtn);
            totalScaleY = 1.0f;
            if (totalScaleX < 1.001f) {
                disable(resetBtn);
            }
        }
    }

    private void zoomInChartY() {
        float scale = 6.0f / 5.0f;
        lineChart.scaleY(scale);
        totalScaleY *= scale;

        enable(resetBtn);
        enable(zoomOutYBtn);
        if (totalScaleY > 4.99f) {
            disable(zoomInYBtn);
        }
    }

    private void resetChart() {
        enable(zoomInXBtn);
        enable(zoomInYBtn);
        disable(resetBtn);
        disable(zoomOutXBtn);
        disable(zoomOutYBtn);
        totalScaleX = 1.0f;
        totalScaleY = 1.0f;

        lineChart.reset();
    }

    private void exitChart() {
        chartActivity.finish();
    }

}
