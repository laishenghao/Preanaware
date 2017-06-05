package com.haoye.preanaware.viewer.chart;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.haoye.preanaware.R;
import com.haoye.preanaware.utils.Constants;
import com.haoye.preanaware.viewer.model.PreanFile;

/**
 * An example full-screen activity that shows and hides the system UI (i.e.
 * status bar and navigation/system bar) with user interaction.
 */
public class ChartActivity extends AppCompatActivity {
    private PreanFile preanFile;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chart);

        init();
    }

    private void init() {
        initPreanFile();
        ChartLayer.create(this, preanFile);
        ListLayer.create(this, preanFile);
        LayerManager layerManager = new LayerManager(this);
        layerManager.init();
    }

    private void initPreanFile() {
        String path = getIntent().getStringExtra(Constants.OPENING_PREANFILE_PATH_KEY);
        preanFile = PreanFile.create(path);
    }

}
