package com.haoye.preanaware.main;

import android.content.Context;
import android.graphics.Color;
import android.util.AttributeSet;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * @brief
 * @detail
 * @see LinearLayout
 * @author Haoye
 * @date 2017/1/17
 */
public class Titlebar extends LinearLayout {

    /**
     * selected index
     */
    private int selectedIndex = 0;
    /**
     * item menu list
     */
    private ArrayList<TextView> items = new ArrayList<>(3);
    /**
     * item menu click listener
     */
    private OnItemMenuClickListener itemListener;
    /**
     * click listener of the child views
     */
    private View.OnClickListener viewListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            for (int index = 0; index < items.size(); index++) {
                if (v == items.get(index)) {
                    setSelectedIndex(index);
                    break;
                }
            }
            if (itemListener != null) {
                itemListener.onItemClick(selectedIndex);
            }
        }
    };

    public Titlebar(Context context) {
        super(context);
    }

    public Titlebar(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public Titlebar(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        initItemMenuView();
        initMoreMenu();
    }

    /**
     * init the item menu view, must be executed after inflation
     */
    private void initItemMenuView() {
        int cnt = getChildCount();
        for (int i = 1; i < cnt - 1; i++) {
            View v = this.getChildAt(i);
            v.setOnClickListener(viewListener);
            items.add((TextView)v);
        }
        if (items.size() > 0) {
            resetItemsColor();
        }
    }

    private void initMoreMenu() {
        TextView more = (TextView) getChildAt(getChildCount()-1);
        more.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                new MoreMenu(getContext(), v).show();
            }
        });
    }

    public void setSelectedIndex(int index) {
        if (index >= 0 && index < items.size() && index != selectedIndex) {
            this.selectedIndex = index;
            resetItemsColor();
        }
    }

    public int getSelectedIndex() {
        return this.selectedIndex;
    }

    private void resetItemsColor() {
        for (TextView v : items) {
            v.setTextColor(Color.parseColor("#aabbcc"));
        }
        items.get(selectedIndex).setTextColor(Color.parseColor("#FF32B6E6"));
    }

    public void setOnItemClickListener(OnItemMenuClickListener listener) {
        this.itemListener = listener;
    }

    public interface OnItemMenuClickListener {
        void onItemClick(int index);
    }

}
