package com.haoye.preanaware.viewer.model;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.ScrollView;

/**
 * @brief
 * @detail
 * @see
 * @author Haoye
 * @date 2017-04-01
 */

public class ChartVerticalScrollView extends ScrollView {
    private OnChangedListener onChangedListener = null;
    private int sTop = 0;

    public ChartVerticalScrollView(Context context) {
        this(context, null);
    }

    public ChartVerticalScrollView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public ChartVerticalScrollView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public int getsTop() {
        return sTop;
    }

    @Override
    protected void onScrollChanged(int l, int t, int oldl, int oldt) {
        sTop = t;
        super.onScrollChanged(l, t, oldl, oldt);
        if (onChangedListener != null) {
            onChangedListener.onChanged(t);
        }
    }

    public void setOnChangedListener(OnChangedListener onChangedListener) {
        this.onChangedListener = onChangedListener;
    }

    public interface OnChangedListener {
        void onChanged(int top);
    }
}
