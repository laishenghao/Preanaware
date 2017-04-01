package com.haoye.preanaware.main;

import android.content.Context;
import android.content.Intent;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;

import java.util.ArrayList;

import com.haoye.preanaware.R;
import com.haoye.preanaware.bluetooth.BluetoothFragment;
import com.haoye.preanaware.bluetooth.Ble;
import com.haoye.preanaware.transmit.TransmitFragment;
import com.haoye.preanaware.utils.Constants;
import com.haoye.preanaware.viewer.ViewerFragment;

/**
 * @author Haoye
 * @brief
 * @detail
 * @date 2017/1/16
 * @see AppCompatActivity
 */
public class MainActivity extends AppCompatActivity {
    /**
     * bluetooth
     */
    private Ble bluetooth = new Ble(this);
    /**
     * custom titlebar
     */
    private Titlebar titlebar;
    /**
     * view pager
     */
    private ViewPager viewPager;
    /**
     * fragments
     */
    private BluetoothFragment bluetoothFragment = BluetoothFragment.create(bluetooth);
    private TransmitFragment transmitFragment = TransmitFragment.create(bluetooth);
    private ViewerFragment viewerFragment = new ViewerFragment();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        init();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case Constants.BLUETOOTH_REQUEST_ENABLE_CODE:
                bluetoothFragment.invokeBluetoothSwitchView();
                break;
        }
    }

    private void init() {
        findView();
        initTitlebar();
        initViewPager();
    }

    private void findView() {
        titlebar = (Titlebar) findViewById(R.id.titlebar);
        viewPager = (ViewPager) findViewById(R.id.viewPager);
    }

    private void initTitlebar() {
        titlebar.setOnItemClickListener(new Titlebar.OnItemMenuClickListener() {
            @Override
            public void onItemClick(int index) {
                if (index >= 0 && index < viewPager.getChildCount()) {
                    viewPager.setCurrentItem(index);
                }
            }
        });
    }

    private void initViewPager() {
        ArrayList<Fragment> fragments = new ArrayList<>(3);
        fragments.add(bluetoothFragment);
        fragments.add(transmitFragment);
        fragments.add(viewerFragment);
        viewPager.setAdapter(new MainPagerAdapter(getSupportFragmentManager(), fragments));
        viewPager.setOffscreenPageLimit(3);
        viewPager.setCurrentItem(0);
        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageSelected(int position) {
                hideSoftInputForm();
                titlebar.setSelectedIndex(position);
            }

            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                hideSoftInputForm();
            }

            @Override
            public void onPageScrollStateChanged(int state) {
                hideSoftInputForm();
            }
        });
    }


    @Override
    public void onBackPressed() {
        moveTaskToBack(true);
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
