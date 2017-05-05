package com.haoye.preanaware.viewer.chart;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;

/**
 * @author Haoye
 * @brief
 * @detail
 * @date 2017-05-05
 * @see
 */

public class VerticalAxis extends View {
    private LineChart lineChart = null;

    public VerticalAxis(Context context) {
        super(context);
    }

    public VerticalAxis(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public VerticalAxis(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        if (lineChart != null) {
            int w = getResources().getDisplayMetrics().widthPixels;
            setMeasuredDimension(w, lineChart.getHeight());
        }
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        Log.i("VerticalAxis", "onSizeChanged -- Height: " + h);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        drawAxis(canvas);
        canvas.restore();
    }

    private int dw = 1;
    public void resize(LineChart lineChart) {
        this.lineChart = lineChart;
        ViewGroup.LayoutParams params = getLayoutParams();
        dw = -dw;
        params.width = getWidth() + dw;
        params.height = lineChart.getHeight();
        this.setLayoutParams(params);
        invalidate();
        requestLayout();
        Log.e("VerticalAxis", "resize Height: " + params.height);
        Log.e("VerticalAxis", "view height: " + getHeight());
    }

    private void drawAxis(Canvas canvas) {
        if (lineChart == null) {
            return;
        }
        canvas.drawColor(Color.TRANSPARENT, PorterDuff.Mode.DST_OUT);

        int axisMargin = lineChart.getAxisMargin();
        int endY = getHeight() - axisMargin;
        int yAxisStartNumber = lineChart.getyAxisStartNumber();
        int gridHeight = lineChart.getGridHeight();
        int yValuePerGrid = lineChart.getyValuePerGrid();
        int textCoordX = 20;
        Paint paint = new Paint();
        paint.setColor(Color.GRAY);
        paint.setTextSize(40);
        int gridCnt = lineChart.getyGridCount();
        for (int y = endY, cnt = 0, coordY = yAxisStartNumber;
             cnt <= gridCnt;
             cnt++, y -= gridHeight, coordY += yValuePerGrid) {
            canvas.drawText("" + coordY, textCoordX, y, paint);
        }
    }

}
