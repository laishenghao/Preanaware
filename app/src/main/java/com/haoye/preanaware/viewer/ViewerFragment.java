package com.haoye.preanaware.viewer;


import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.haoye.preanaware.R;
import com.haoye.preanaware.utils.Constants;
import com.haoye.preanaware.viewer.chart.ChartActivity;
import com.haoye.preanaware.viewer.model.PreanFile;

import java.io.File;
import java.io.IOException;

/**
 * A simple {@link Fragment} subclass.
 */
public class ViewerFragment extends Fragment {
    public static final String DEFAULT_PATH = FileUtil.getPreanFileHomePath();
    private final int          disableColor = Color.parseColor("#5f6264");
    private final int          enableColor  = Color.parseColor("#32B6E6");
    private String             currentPath  = DEFAULT_PATH;
    private View               rootView;
    private TextView           contentTxtV;
    private ListView           fileListView;
    private FileListAdapter    fileListAdapter;
    private TextView           resetBtn;
    private TextView           returnBtn;

    public ViewerFragment() {
        // Required empty public constructor
    }

    private void findViews() {
        rootView     = getActivity().getLayoutInflater().inflate(R.layout.fragment_viewer, null);
        contentTxtV  = (TextView) rootView.findViewById(R.id.contentTxtV);
        fileListView = (ListView) rootView.findViewById(R.id.fileListV);
        resetBtn     = (TextView) rootView.findViewById(R.id.fileListResetBtn);
        returnBtn    = (TextView) rootView.findViewById(R.id.fileListReturnBtn);
    }

    private void initFileListView() {
        fileListAdapter = new FileListAdapter(getContext());
        fileListAdapter.update(currentPath);
        fileListView.setAdapter(fileListAdapter);
        fileListView.setChoiceMode(AbsListView.CHOICE_MODE_SINGLE);
        fileListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                File file = fileListAdapter.getFile(position);
                if (file.isDirectory()) {
                    currentPath = file.getPath();
                    fileListAdapter.update(currentPath);
                    if (currentPath.equals(DEFAULT_PATH)) {
                        resetBtn.setEnabled(false);
                        resetBtn.setTextColor(disableColor);
                    }
                    else {
                        resetBtn.setEnabled(true);
                        resetBtn.setTextColor(enableColor);
                    }
                }
                else {
                    openPreanFileWindow(file.getPath());
                }
            }
        });
    }

    private void initControlButton() {
        resetBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                currentPath = DEFAULT_PATH;
                fileListAdapter.update(currentPath);
                resetBtn.setEnabled(false);
                resetBtn.setTextColor(disableColor);
            }
        });

        returnBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (currentPath.equals("/")) {
                    Toast.makeText(getContext(), "当前目录已是根目录!", Toast.LENGTH_LONG).show();
                }
                else {
                    File file = new File(currentPath);
                    currentPath = file.getParent();
                    if (currentPath == null) {
                        currentPath = file.getPath();
                        return;
                    }
                    fileListAdapter.update(currentPath);
                }
                if (currentPath.equals(DEFAULT_PATH)) {
                    resetBtn.setEnabled(false);
                    resetBtn.setTextColor(disableColor);
                }
                else {
                    resetBtn.setEnabled(true);
                    resetBtn.setTextColor(enableColor);
                }
            }
        });

    }

    private void openPreanFileWindow(String  path) {
        if (path == null) {
            return;
        }
        Intent intent = new Intent(getActivity(), ChartActivity.class);
        intent.putExtra(Constants.OPENING_PREANFILE_PATH_KEY, path);
        getActivity().startActivity(intent);
    }


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        findViews();
        initFileListView();
        initControlButton();
        // test
        displayFileInfo();
    }

    // test
    private void displayFileInfo() {
        String documentPath = FileUtil.getPublicDocumentPath();
        try {
            PreanFile preanFile = new PreanFile(
                    documentPath + "/preanaware_dir_test/ID6001_2013年11月06日14时56分38秒.dat", "r");
            contentTxtV.append(" File Type: " + preanFile.getType());
            contentTxtV.append("\n Created Time: " + preanFile.getCreatedTimeString());
            contentTxtV.append("\n Gauge ID: " + preanFile.getGaugeId());
            contentTxtV.append("\n Interval: " + preanFile.getRecordInterval());
            contentTxtV.append("\n Start Time: " + preanFile.getStartRecordTimeString());
            contentTxtV.append("\n Channel Count: " + preanFile.getRecordChannelCount());
            contentTxtV.append("\n Record Count: " + preanFile.getRecordCount());
            contentTxtV.append("\n File Length: " + preanFile.length());
            contentTxtV.append("\n Up Limit: " + preanFile.getUpLimitAlarmValue());
            contentTxtV.append("\n Voltage: " + preanFile.getVoltage());
            contentTxtV.append("\n Version: " + preanFile.getVersion());
            contentTxtV.append("\n Record 0: " + preanFile.getChannelOneRecord(0));
            contentTxtV.append("\n Record 50: " + preanFile.getChannelOneRecord(50));
            contentTxtV.append("\n Record 100: " + preanFile.getChannelOneRecord(100));
            contentTxtV.append("\n Record 1500: " + preanFile.getChannelOneRecord(1500) + "\n");

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return rootView;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ViewGroup parent = (ViewGroup) rootView.getParent();
        if (parent != null) {
            parent.removeAllViewsInLayout();
        }
    }
}
