package com.haoye.preanaware.viewer.chart;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;

/**
 * @brief
 * @detail
 * @see View
 * @date 2017-03-01
 * @author Haoye
 */
public class LineChart extends View {
    private static final float MAX_SCALE_VALUE = 4f;
    private static final float MIN_SCALE_VALUE = 1f;

    private int axisMargin;
    private DisplayMetrics dm;
    private int[] yValues;
    private int[] yDrawValues;
    private int   xAxisPadding = 0;
    private int   yAxisPadding = 0;
    private int   yAxisStartNumber;
    private int   yValuePerGrid;
    private int   gridWidth;
    private int   gridHeight;
    private int   xGridCount;
    private int   yGridCount;
    private OnChangedListener onChangedListener = null;

    public void setOnChangedListener(OnChangedListener onChangedListener) {
        this.onChangedListener = onChangedListener;
    }

    public LineChart(Context context) {
        this(context, null);
    }

    public LineChart(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public LineChart(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        dm = getResources().getDisplayMetrics();
        yGridCount = 10;
        xGridCount = 20;
        yAxisStartNumber = 0;
        yValuePerGrid    = 10;
        gridWidth  = dip2px(1);
        gridHeight = dip2px(50);
        axisMargin = dip2px(70);
    }

    public int getAxisMargin() {
        return axisMargin;
    }

    public int getPressureBasedY(int y) {
        int range      = getHeight()- axisMargin - y;
        int total      = range * yValuePerGrid;
        int rangeValue = total / gridHeight + (total % gridHeight) * 2 / gridHeight;
        return yAxisStartNumber + rangeValue;
    }

    public int getGridWidth() {
        return gridWidth;
    }

    public int getxGridCount() {
        return xGridCount;
    }

    public void setxGridCount(int xGridCount) {
        this.xGridCount = xGridCount;
    }

    public int getyGridCount() {
        return yGridCount;
    }

    public void setyGridCount(int yGridCount) {
        this.yGridCount = yGridCount;
    }

    public int getxAxisPadding() {
        return xAxisPadding;
    }

    public void setxAxisPadding(int xAxisPadding) {
        this.xAxisPadding = xAxisPadding;
    }

    public int getyAxisPadding() {
        return yAxisPadding;
    }

    public void setyAxisPadding(int yAxisPadding) {
        this.yAxisPadding = dip2px(yAxisPadding);
    }

    public int getyAxisStartNumber() {
        return yAxisStartNumber;
    }

    public int getGridHeight() {
        return gridHeight;
    }

    public int getyValuePerGrid() {
        return yValuePerGrid;
    }

    public void setyValues(int[] yValues) {
        this.yValues = yValues;
//        this.yAxisStartNumber = computeyAxisStartNumber(yValues);
//        this.yGridCount = computeyGridCount(yValues);
        resetGridWidth(yValues.length);
        computeyGridValues(yValues);
        this.yDrawValues = realToDraw(yValues);
        resizeByYValues();
    }

    private void resetGridWidth(final int dataCount) {
        int len;
        do {
            len = gridWidth * dataCount;
            if (len > dm.widthPixels / 5) {
                gridWidth *= 2;
            }
            else if (len > dm.widthPixels / 10){
                gridWidth *= 5;
            }
            else {
                gridWidth *= 10;
            }
        } while (len < dm.widthPixels);
    }

    private void resizeByYValues() {
        if (yValues != null && xGridCount < yValues.length) {
            xGridCount = yValues.length;
        }

        int padding = 2 * axisMargin;
        int width   = gridWidth * xGridCount + padding;
        int height  = gridHeight * yGridCount + padding;

        ViewGroup.LayoutParams params = getLayoutParams();
        params.width = width;
        params.height = height;
        setLayoutParams(params);
    }

    private int[] realToDraw(int[] reals) {
        int[] draws = new int[reals.length];
        float ratio = gridHeight * 1.0f / yValuePerGrid;
        for (int i = 0; i < reals.length; i++) {
            draws[i] = (int) ((reals[i] - yAxisStartNumber) * ratio);
        }
        return draws;
    }

    private void computeyGridValues(int[] yValues) {
        // start number
        int minValue = yValues[0];
        for (int i : yValues) {
            if (i < minValue) {
                minValue = i;
            }
        }
//        yAxisStartNumber = minValue / 10 * 10 - 20;


        // yValuePerGrid 、 end number(grid count)
        int maxValue = yValues[0];
        for (int i : yValues) {
            if (i > maxValue) {
                maxValue = i;
            }
        }
        int range = maxValue - minValue;

        int cnt = range / yValuePerGrid + 2;
        while (cnt < 8 && yValuePerGrid > 1) {
            yValuePerGrid >>= 1;
            cnt = range / yValuePerGrid + 2;
        }

        while (cnt > 16) {
            if (cnt >= 100) {
                yValuePerGrid *= 10;
                cnt = range / yValuePerGrid + 2;
            }
            else if (cnt >= 50) {
                yValuePerGrid *= 5;
                cnt = range / yValuePerGrid + 2;
            }
            else {
                yValuePerGrid *= 2;
                cnt = range / yValuePerGrid + 2;
            }
        }

        yGridCount = Math.max(cnt, 10);

        yAxisStartNumber = minValue / yValuePerGrid * yValuePerGrid - yValuePerGrid;
        if (minValue >= 0 && yAxisStartNumber < 0) {
            yAxisStartNumber = 0;
        }

    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int padding = 2 * axisMargin;
        int width   = gridWidth * xGridCount + padding;
        int height  = gridHeight * yGridCount + padding;
        setMeasuredDimension(width, height);
    }

    private void drawAxis(Canvas canvas) {
        canvas.drawColor(Color.TRANSPARENT, PorterDuff.Mode.DST_OUT);
        Paint paint = new Paint();
        paint.setColor(Color.GRAY);
        paint.setTextSize(40);
//        String testText = "0000";
//        Rect textRect = new Rect();
//        paint.getTextBounds(testText, 0, testText.length(), textRect);
        int endX = getWidth() - axisMargin;
        int endY = getHeight() - axisMargin;


        int gridW = gridWidth * 50;
        int textCoordY = getHeight() - axisMargin * 2 / 3;
        for (int x = axisMargin, coordX = 0; x < endX; x += gridW, coordX += 50) {
            canvas.drawLine(x, axisMargin, x, endY, paint);
            canvas.drawText("" + coordX, x, textCoordY, paint);
        }

        for (int y = endY, coordY = yAxisStartNumber, cnt = 0;
             cnt <=  yGridCount;
             y -= gridHeight, coordY += yValuePerGrid, cnt++) {
            canvas.drawLine(axisMargin, y, endX, y, paint);
        }

    }

    private void drawLineChart(Canvas canvas) {
        if (yValues == null || yValues.length == 0
         || yDrawValues == null || yDrawValues.length != yValues.length) {
            return;
        }

        Paint paint = new Paint();
        paint.setColor(Color.CYAN);
//        paint.setTextSize(40);
        paint.setStrokeWidth(3);

        int endY = getHeight() - axisMargin;

        for (int i = 0, x = axisMargin; i < yDrawValues.length-1; i++, x += gridWidth) {
            canvas.drawLine(x, endY-yDrawValues[i], x+gridWidth, endY-yDrawValues[i+1], paint);
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        canvas.save();
        drawAxis(canvas);
        drawLineChart(canvas);
        canvas.restore();
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        if (onChangedListener != null) {
            onChangedListener.onChanged(w, h);
            Log.e("LineChart", "onSizeChanged -- Height: " + h);
        }
    }

    public int getDotCount() {
        if (yValues == null) {
            return 0;
        }
        return yValues.length;
    }

    public int dip2px(int pixel) {
        return (int) (pixel * dm.density + 0.5);
    }

    public void scale(float scale) {
        gridWidth  = (int) (scale * gridWidth + 0.5);
        gridHeight = (int) (scale * gridHeight + 0.5);
        if (yDrawValues != null) {
            for (int i = 0; i < yDrawValues.length; i++) {
                yDrawValues[i] = (int) (scale * yDrawValues[i]);
            }
        }

        ViewGroup.LayoutParams params = getLayoutParams();
        params.width = gridWidth * xGridCount;
        params.height = gridHeight * yGridCount;
        setLayoutParams(params);
    }

    public void scaleX(float scale) {
        gridWidth  = (int) (scale * gridWidth + 0.5);
        ViewGroup.LayoutParams params = getLayoutParams();
        params.width = gridWidth * xGridCount;
        setLayoutParams(params);
    }

    public void scaleY(float scale) {
        gridHeight = (int) (scale * gridHeight + 0.5);
        if (yDrawValues != null) {
            for (int i = 0; i < yDrawValues.length; i++) {
                yDrawValues[i] = (int) (scale * yDrawValues[i]);
            }
        }

        ViewGroup.LayoutParams params = getLayoutParams();
        params.height = gridHeight * yGridCount;
        setLayoutParams(params);
    }

    public void reset() {
        gridWidth  = dip2px(1);
        gridHeight = dip2px(50);
        axisMargin = dip2px(70);
        this.yDrawValues = realToDraw(yValues);
        resizeByYValues();
        invalidate();
    }

    public interface OnChangedListener {
        void onChanged(int w, int h);
    }
}
