package com.haoye.preanaware.viewer.model;

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
import android.widget.FrameLayout;

/**
 * @brief
 * @detail
 * @see
 * @author Haoye
 * @date 2017-03-31
 */

public class Vernier extends View {
    private DisplayMetrics dm;
    private Paint paint;
    private OnDragListener onDragListener = null;

    public void setOnDragListener(OnDragListener onDragListener) {
        this.onDragListener = onDragListener;
    }

    public Vernier(Context context) {
        this(context, null);
    }

    public Vernier(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public Vernier(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    private void init(Context context) {
        dm = context.getResources().getDisplayMetrics();
        paint = new Paint();
        paint.setColor(Color.WHITE);
        paint.setStrokeWidth(3);
        paint.setAntiAlias(true);
        paint.setStyle(Paint.Style.STROKE);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int min = (int) (100 * dm.density);
        int max = (int) (200 * dm.density);
        int size = dm.widthPixels / 10;
        size = Math.min(Math.max(min, size), max);
        setMeasuredDimension(size, size);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        // clear
        canvas.drawColor(Color.TRANSPARENT, PorterDuff.Mode.DST_OUT);

        int cx = getWidth() / 2;
        int cy = getHeight() / 2;
        paint.setColor(Color.LTGRAY);
        canvas.drawLine(0, cy, getWidth(), cy, paint);
        canvas.drawLine(cx, 0, cx, getHeight(), paint);
        canvas.drawCircle(cx, cy, getWidth()/2 - 2, paint);

        int dt = (int) (10 * dm.density);
        paint.setColor(Color.RED);
        canvas.drawLine(cx-dt, cy,  cx+dt, cy, paint);
        canvas.drawLine(cx, cy-dt,  cx, cy+dt, paint);
    }

    private float oldX;
    private float oldY;

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction()) {
        case MotionEvent.ACTION_DOWN:
            oldX = event.getRawX();
            oldY = event.getRawY();
            break;
        case MotionEvent.ACTION_MOVE:
        case MotionEvent.ACTION_UP:
            float nx = event.getRawX();
            float ny = event.getRawY();
            int ddx = (int) (nx - oldX);
            int ddy = (int) (ny - oldY);
            oldX = nx;
            oldY = ny;
            moveBy(ddx, ddy);
            if (onDragListener != null) {
                onDragListener.onDrop(getCx(), getCy());
            }
            break;
        default:
            break;
        }
        return true;
    }

    public void moveBy(int dx, int dy) {
        FrameLayout.LayoutParams params = (FrameLayout.LayoutParams) getLayoutParams();
        params.leftMargin += dx;
        params.topMargin  += dy;
        this.setLayoutParams(params);
    }

    public int getCx() {
        return getLeft() + getWidth() / 2;
    }

    public int getCy() {
        return getTop() + getHeight() / 2;
    }

    public int getCyBasedBottom() {
        return dm.heightPixels - (getBottom() - getHeight() / 2);
    }

    public interface OnDragListener {
        void onDrop(int x, int y);
    }
}
