package com.haoye.preanaware.viewer.chart;

import android.annotation.SuppressLint;
import android.content.Context;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.haoye.preanaware.R;
import com.haoye.preanaware.utils.Constants;
import com.haoye.preanaware.viewer.model.ChartHorizontalScrollView;
import com.haoye.preanaware.viewer.model.ChartVerticalScrollView;
import com.haoye.preanaware.viewer.model.PreanFile;
import com.haoye.preanaware.viewer.model.Vernier;

import java.util.Calendar;
import java.util.Formatter;

/**
 * An example full-screen activity that shows and hides the system UI (i.e.
 * status bar and navigation/system bar) with user interaction.
 */
public class ChartActivity extends AppCompatActivity {
    /**
     * Whether or not the system UI should be auto-hidden after
     * {@link #AUTO_HIDE_DELAY_MILLIS} milliseconds.
     */
    private static final boolean AUTO_HIDE = true;

    /**
     * If {@link #AUTO_HIDE} is set, the number of milliseconds to wait after
     * user interaction before hiding the system UI.
     */
    private static final int AUTO_HIDE_DELAY_MILLIS = 3000;

    /**
     * Some older devices needs a small delay between UI widget updates
     * and a change of the status and navigation bar.
     */
    private static final int UI_ANIMATION_DELAY = 100;
    private final Handler mHideHandler = new Handler();
    private final Runnable mHidePart2Runnable = new Runnable() {
        @SuppressLint("InlinedApi")
        @Override
        public void run() {
            // Delayed removal of status and navigation bar

            // Note that some of these constants are new as of API 16 (Jelly Bean)
            // and API 19 (KitKat). It is safe to use them, as they are inlined
            // at compile-time and do nothing on earlier devices.
            lineChart.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LOW_PROFILE
                    | View.SYSTEM_UI_FLAG_FULLSCREEN
                    | View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                    | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                    | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                    | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION);
        }
    };
    private final Runnable mShowPart2Runnable = new Runnable() {
        @Override
        public void run() {
            mControlsView.setVisibility(View.VISIBLE);
            // Delayed display of UI elements
            ActionBar actionBar = getSupportActionBar();
            if (actionBar != null) {
                actionBar.show();
            }
        }
    };
    private boolean mVisible;
    private final Runnable mHideRunnable = new Runnable() {
        @Override
        public void run() {
            hide();
        }
    };
    /**
     * Touch listener to use for in-layout UI controls to delay hiding the
     * system UI. This is to prevent the jarring behavior of controls going away
     * while interacting with activity UI.
     */
//    private final View.OnClickListener mDelayHideTouchListener = new View.OnClickListener() {
//        @Override
//        public void onClick(View view) {
//            if (AUTO_HIDE) {
//                delayedHide(AUTO_HIDE_DELAY_MILLIS);
//            }
//        }
//    };

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);

        // Trigger the initial hide() shortly after the activity has been
        // created, to briefly hint to the user that UI controls
        // are available.
        delayedHide(100);
    }

    private void toggle() {
        if (mVisible) {
            hide();
        } else {
            show();
        }
    }

    private void hide() {
        // Hide UI first
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.hide();
        }
        mControlsView.setVisibility(View.GONE);
        mVisible = false;

        // Schedule a runnable to remove the status and navigation bar after a delay
        mHideHandler.removeCallbacks(mShowPart2Runnable);
        mHideHandler.postDelayed(mHidePart2Runnable, UI_ANIMATION_DELAY);
    }

    @SuppressLint("InlinedApi")
    private void show() {
        // Show the system bar
        lineChart.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION);
        mVisible = true;

        // Schedule a runnable to display UI elements after a delay
        mHideHandler.removeCallbacks(mHidePart2Runnable);
        mHideHandler.postDelayed(mShowPart2Runnable, UI_ANIMATION_DELAY);
    }

    /**
     * Schedules a call to hide() in [delay] milliseconds, canceling any
     * previously scheduled calls.
     */
    private void delayedHide(int delayMillis) {
        mHideHandler.removeCallbacks(mHideRunnable);
        mHideHandler.postDelayed(mHideRunnable, delayMillis);
    }

//============================================================================================
    private LineChart lineChart;
    private View mControlsView;
    private ChartHorizontalScrollView horizontalScrollView;
    private ChartVerticalScrollView   verticalScrollView;
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chart);

        init();
    }

    private void init() {
        initPreanFile();
        findViews();
        initLineChart();
        initControlBar();
        initScrollViews();
        initVernier();
        initIndex();
    }

    private void initPreanFile() {
        String path = getIntent().getStringExtra(Constants.OPENING_PREANFILE_PATH_KEY);
        preanFile = PreanFile.create(path);
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
        lineChart            = (LineChart) findViewById(R.id.lineChart);
        mControlsView        = findViewById(R.id.fullscreen_content_controls);
        horizontalScrollView = (ChartHorizontalScrollView) findViewById(R.id.horizontalScrollView);
        verticalScrollView   = (ChartVerticalScrollView) findViewById(R.id.verticalScrollView);
        timeTxtV             = (TextView) findViewById(R.id.vernierTimeTxtV);
        pressureTxtV         = (TextView) findViewById(R.id.vernierPressureTxtV);
        vernier              = (Vernier) findViewById(R.id.vernier);
        indexEditText        = (EditText) findViewById(R.id.indexEditText);
        indexConfirmBtn      = (TextView) findViewById(R.id.indexConfirmBtn);
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
                toggle();
            }
        });
        lineChart.setOnChangedListener(new LineChart.OnChangedListener() {
            @Override
            public void onChanged(int w, int h) {
                updateChartDisplayInfo();
            }
        });
    }

    private void initControlBar() {
        mVisible = true;
        new ChartControlBar(this, mControlsView, lineChart).init();
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
            Toast.makeText(ChartActivity.this, tip, Toast.LENGTH_LONG).show();
            return;
        }
        if (index < 0 || index >= lineChart.getDotCount()) {
            indexConfirmBtn.setVisibility(View.GONE);
            Toast.makeText(ChartActivity.this, tip, Toast.LENGTH_LONG).show();
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
        String time = "时间：" + calendar.get(Calendar.YEAR)
                    + "-" + calendar.get(Calendar.MONTH)
                    + "-" + calendar.get(Calendar.DATE)
                    + "  " + calendar.get(Calendar.HOUR)
                    + ":" + calendar.get(Calendar.MINUTE)
                    + ":" + calendar.get(Calendar.SECOND);
        timeTxtV.setText(time);
        String pressure = "压力：" + new Formatter().format("%.2f", pressureValue * 1.0 / denominator).toString() + " kPa";
        pressureTxtV.setText(pressure);
    }

    private void hideSoftInputForm() {
        View view = getCurrentFocus();
        if (view != null) {
            InputMethodManager manager =
                    (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            manager.hideSoftInputFromWindow(view.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
        }
    }

}
