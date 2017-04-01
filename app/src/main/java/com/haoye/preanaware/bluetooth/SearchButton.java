package com.haoye.preanaware.bluetooth;

import android.content.Context;
import android.graphics.Canvas;
import android.util.AttributeSet;
import android.view.View;

/**
 * @brief custom button for scanning bluetooth
 * @detail
 * @see View
 * @author Haoye
 * @date 2017/1/30
 */
public class SearchButton extends View{

    public SearchButton(Context context) {
        super(context);
    }

    public SearchButton(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public SearchButton(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
//        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int widthMode = MeasureSpec.getMode(widthMeasureSpec);
        int widthSize = MeasureSpec.getSize(widthMeasureSpec);
        int heightMode = MeasureSpec.getMode(heightMeasureSpec);
        int heightSize = MeasureSpec.getSize(heightMeasureSpec);
        int width = 100;
        int height = 100;
        if (widthMode == MeasureSpec.EXACTLY) {
            width = widthSize;
        }
        else {
//            mTitlePaint.setTextSize(mTitleFontSize);
//            mTitlePaint.getTextBounds(mTitleText, 0, mTitleText.length(), mTitleRect);
//            float textWidth = mTitleRect.width();
//            int desired = (int) (getPaddingLeft() + textWidth + getPaddingRight());
//            width = desired;
        }

        if (heightMode == MeasureSpec.EXACTLY) {
            height = heightSize;
        }
        else {
//            mTitlePaint.setTextSize(mTitleFontSize);
//            mTitlePaint.getTextBounds(mTitleText, 0, mTitleText.length(), mTitleRect);
//            float textHeight = mTitleRect.height();
//            int desired = (int) (getPaddingTop() + textHeight + getPaddingBottom());
//            height = desired;
        }

        setMeasuredDimension(width, height);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
    }

}
