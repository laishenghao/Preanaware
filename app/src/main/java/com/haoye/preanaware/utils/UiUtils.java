package com.haoye.preanaware.utils;

import android.content.Context;
import android.util.DisplayMetrics;
import android.view.WindowManager;

/**
 * Created by Haoye on 2016/2/5.
 * Copyright © 2016 Haoye All Rights Reserved
 */
public class UiUtils {

    private static DisplayMetrics dm = null;

    public static DisplayMetrics getDisplayMetrics(Context context) {
        if (dm == null) {
            dm = new DisplayMetrics();
            ((WindowManager)context.getSystemService(Context.WINDOW_SERVICE))
                    .getDefaultDisplay()
                    .getMetrics(dm);
        }
        return dm;
    }

    public static int getScreenWidthPixels(Context context) {
        return getDisplayMetrics(context).widthPixels;
    }

    public static int getScreenHeightPixels(Context context) {
        return getDisplayMetrics(context).heightPixels;
    }

    public static int dipToPx(Context context, int dip) {
        return (int) (dip * getScreenDensity(context) + 0.5f);
    }

    public static int dipTopx(int dip, float density) {
        return (int) (dip * density + 0.5f);

    }

    public static  float getScreenDensity(Context context) {
        return getDisplayMetrics(context).density;

    }
}