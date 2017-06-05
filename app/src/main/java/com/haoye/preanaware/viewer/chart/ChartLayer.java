package com.haoye.preanaware.viewer.chart;

import android.app.Activity;
import android.content.Context;
import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.haoye.preanaware.R;
import com.haoye.preanaware.viewer.model.ChartHorizontalScrollView;
import com.haoye.preanaware.viewer.model.ChartVerticalScrollView;
import com.haoye.preanaware.viewer.model.PreanFile;
import com.haoye.preanaware.viewer.model.Vernier;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Formatter;

/**
 * @author Haoye
 * @brief
 * @detail
 * @date 2017-05-13
 * @see
 */

public class ChartLayer {
    private Activity activity;

    private LineChart lineChart;
    private VerticalAxis verticalAxis;
    private View viewModeCtrlView;
    private View chartCtrlBar;
    private ChartHorizontalScrollView horizontalScrollView;
    private ChartVerticalScrollView verticalScrollView;
    private TextView timeTxtV;
    private TextView pressureTxtV;
    private Vernier vernier;
    private EditText indexEditText;
    private TextView indexConfirmBtn;

    private PreanFile preanFile;
    private int intervalMs = 0;
    private long basicTimeMillis;
    private int denominator = 1;
    private int oldIndex = 0;

    private void toggleSwitchView() {
        int visibility = viewModeCtrlView.getVisibility();
        if (visibility == View.VISIBLE) {
            viewModeCtrlView.setVisibility(View.GONE);
            chartCtrlBar.setVisibility(View.GONE);
        } else {
            viewModeCtrlView.setVisibility(View.VISIBLE);
            chartCtrlBar.setVisibility(View.VISIBLE);
        }
    }

    private ChartLayer(Activity activity, PreanFile preanFile) {
        this.activity = activity;
        this.preanFile = preanFile;
    }

    public static ChartLayer create(Activity activity, PreanFile preanFile) {
        ChartLayer chartLayer = new ChartLayer(activity, preanFile);
        chartLayer.init();
        return chartLayer;
    }

    private void init() {
        initPreanFileParams();
        findViews();
        initLineChart();
        initControlBar();
        initScrollViews();
        initVernier();
        initIndex();
    }

    private void initPreanFileParams() {
        if (preanFile != null) {
            basicTimeMillis = preanFile.getStartRecordTime().getTimeInMillis();
            intervalMs = preanFile.getRecordInterval() * 1000;
            int cnt = preanFile.getChannel1DecimalCount();
            for (int i = 0; i < cnt; i++) {
                denominator *= 10;
            }
        }
    }

    private void findViews() {
        lineChart            = (LineChart) activity.findViewById(R.id.lineChart);
        verticalAxis         = (VerticalAxis) activity.findViewById(R.id.verticalAxis);
        viewModeCtrlView     = activity.findViewById(R.id.viewMode_switchWrap);
        chartCtrlBar = activity.findViewById(R.id.chart_controlbar_wrap);
        horizontalScrollView = (ChartHorizontalScrollView) activity.findViewById(R.id.horizontalScrollView);
        verticalScrollView   = (ChartVerticalScrollView) activity.findViewById(R.id.verticalScrollView);
        timeTxtV             = (TextView) activity.findViewById(R.id.vernierTimeTxtV);
        pressureTxtV         = (TextView) activity.findViewById(R.id.vernierPressureTxtV);
        vernier              = (Vernier) activity.findViewById(R.id.vernier);
        indexEditText        = (EditText) activity.findViewById(R.id.indexEditText);
        indexConfirmBtn      = (TextView) activity.findViewById(R.id.indexConfirmBtn);
    }

    private void initLineChart() {
        if (preanFile != null) {
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    int[] values = preanFile.getChannelOneRecords();
                    lineChart.setyValues(values);
                }
            }, 200);

        }

        // Set up the user interaction to manually show or hide the system UI.
        lineChart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                toggleSwitchView();
            }
        });
        lineChart.setOnChangedListener(new LineChart.OnChangedListener() {
            @Override
            public void onChanged(int w, int h) {
                updateChartDisplayInfo();
                verticalAxis.resize(lineChart);
            }
        });
        verticalAxis.resize(lineChart);
    }

    private void initControlBar() {
        new ChartControlBar((ChartActivity)activity, chartCtrlBar, lineChart).init();
    }

    private void initScrollViews() {
        verticalScrollView.setOnChangedListener(new ChartVerticalScrollView.OnChangedListener() {
            @Override
            public void onChanged(int top) {
                updateChartDisplayInfo();
            }
        });
        horizontalScrollView.setOnChangedListener(new ChartHorizontalScrollView.OnChangedListener() {
            @Override
            public void onChanged(int left) {
                updateChartDisplayInfo();
            }
        });
    }

    private void initVernier() {
        vernier.setOnDragListener(new Vernier.OnDragListener() {
            @Override
            public void onDrop(int x, int y) {
                updateChartDisplayInfo();
            }
        });
    }

    private void initIndex() {
        indexConfirmBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                scrollByIndex();
                indexConfirmBtn.setVisibility(View.GONE);
            }
        });

        indexEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                String input = s.toString().trim();
                checkInputIndex(input);
            }
        });
    }

    private boolean toastFlag = true;
    private void checkInputIndex(String input) {
        final String tip =  "数值应在 0 到 " + lineChart.getDotCount() + " 之间!";
        if (input.isEmpty()) {
            indexConfirmBtn.setVisibility(View.GONE);
            return;
        }
        int index;
        try {
            index = Integer.valueOf(input);
        }
        catch (NumberFormatException e) {
            indexConfirmBtn.setVisibility(View.GONE);
            Toast.makeText(activity, tip, Toast.LENGTH_LONG).show();
            return;
        }
        if (index < 0 || index >= lineChart.getDotCount()) {
            indexConfirmBtn.setVisibility(View.GONE);
            if (toastFlag) {
                toastFlag = false;
                Toast.makeText(activity, tip, Toast.LENGTH_LONG).show();
            }
            indexEditText.setText(String.valueOf(lineChart.getDotCount() - 1));
        }
        else if (oldIndex != index) {
            indexConfirmBtn.setVisibility(View.VISIBLE);
        }
    }

    private void scrollByIndex() {
        String input = indexEditText.getText().toString().trim();
        int index = Integer.valueOf(input);
        index = Math.max(0, Math.min(index, lineChart.getDotCount()));

        int w = index * lineChart.getGridWidth()  + lineChart.getAxisMargin();
        int vernierX = vernier.getCx();
        int hScrollLeft = w - vernierX;
        horizontalScrollView.scrollTo(hScrollLeft, 0);

        int err = hScrollLeft - horizontalScrollView.getsLeft();
        if (err != 0) {
            vernier.moveBy(hScrollLeft, 0);
        }
        updateChartDisplayInfo();
        indexEditText.setText(String.valueOf(index));
        indexEditText.setSelection(indexEditText.getText().length());
    }

    private void updateChartDisplayInfo() {
        indexConfirmBtn.setVisibility(View.GONE);
        hideSoftInputForm();

        int hScrollLeft = horizontalScrollView.getsLeft();
        int vScrollTop  = verticalScrollView.getsTop();
        int vernierX    = vernier.getCx();
        int vernierY    = vernier.getCy();
        if (hScrollLeft < 0 || vernierX < 0 || vScrollTop < 0 || vernierY < 0) {
            return;
        }
        int y = vScrollTop + vernierY;
        int pressureValue = lineChart.getPressureBasedY(y);

        int w = hScrollLeft + vernierX - lineChart.getAxisMargin();
        int index = w / lineChart.getGridWidth();
        if (index < 0) {
            return;
        }
        oldIndex = index;
        indexEditText.setText(String.valueOf(index));
        indexEditText.setSelection(indexEditText.getText().length());
        indexEditText.setHint("0 — " + lineChart.getDotCount());

        int dt = index * intervalMs;
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(basicTimeMillis + dt);
        SimpleDateFormat format = new SimpleDateFormat("时间：yyyy-MM-dd HH:mm:ss");
        String time = format.format(calendar.getTime());
        timeTxtV.setText(time);
        String pressure = new Formatter().format("压力：%.2f", pressureValue * 1.0 / denominator).toString() + " kPa";
        pressureTxtV.setText(pressure);
    }

    private void hideSoftInputForm() {
        View view = activity.getCurrentFocus();
        if (view != null) {
            InputMethodManager manager =
                    (InputMethodManager) activity.getSystemService(Context.INPUT_METHOD_SERVICE);
            manager.hideSoftInputFromWindow(view.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
        }
    }
}
