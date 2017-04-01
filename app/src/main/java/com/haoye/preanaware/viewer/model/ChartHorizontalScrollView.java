package com.haoye.preanaware.viewer.model;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.HorizontalScrollView;

/**
 * @brief
 * @detail
 * @see
 * @author Haoye
 * @date 2017-03-31
 */

public class ChartHorizontalScrollView extends HorizontalScrollView {
    private OnChangedListener onChangedListener = null;
    private int sLeft = 0;

    public ChartHorizontalScrollView(Context context) {
        this(context, null);
    }

    public ChartHorizontalScrollView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public ChartHorizontalScrollView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public int getsLeft() {
        return sLeft;
    }

    @Override
    protected void onScrollChanged(int l, int t, int oldl, int oldt) {
        super.onScrollChanged(l, t, oldl, oldt);
        sLeft = l;
        if (onChangedListener != null) {
            onChangedListener.onChanged(l);
        }
    }

    public void setOnChangedListener(OnChangedListener onChangedListener) {
        this.onChangedListener = onChangedListener;
    }

    public interface OnChangedListener {
        void onChanged(int left);
    }
}
