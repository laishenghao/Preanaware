package com.haoye.preanaware.main;

import android.content.Context;
import android.os.Process;
import android.view.ContextThemeWrapper;
import android.view.MenuItem;
import android.view.View;
import android.widget.PopupMenu;

import com.haoye.preanaware.R;

/**
 * @brief
 * @detail
 * @see
 * @author Haoye
 * @date 2017/2/24
 */

public class MoreMenu {
    private PopupMenu menu;

    public MoreMenu(Context context, View anchor) {
        Context wrapContext = new ContextThemeWrapper(context, R.style.PopupMenu);
        this.menu = new PopupMenu(wrapContext, anchor);
        initMenu();
    }

    private void initMenu() {
        menu.inflate(R.menu.menu_more);
        menu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                switch (item.getItemId()) {
                case R.id.menuHelp:
                    onHelp();
                    return true;
                case R.id.menuAbout:
                    onAbout();
                    return true;
                case R.id.menuExit:
                    onExit();
                    return true;
                default:
                    return false;
                }
            }
        });
    }

    public void show() {
        menu.show();
    }

    private void onHelp() {
        // TODO: 2017/2/26
    }

    private void onAbout() {
        // TODO: 2017/2/24
    }

    private void onExit() {
        android.os.Process.killProcess(Process.myPid());
    }


}
