package com.haoye.preanaware.viewer;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.haoye.preanaware.R;

import java.io.File;
import java.io.FileFilter;
import java.util.ArrayList;

/**
 * @brief
 * @detail
 * @see
 * @author Haoye
 * @date 2017-03-01
 */

public class FileListAdapter extends BaseAdapter{
    private LayoutInflater inflater;
    private ArrayList<File> fileList = new ArrayList<>();

    public FileListAdapter(Context context) {
        this.inflater = LayoutInflater.from(context);
    }

    @Override
    public int getCount() {
        return fileList.size();
    }

    public void clear() {
        fileList.clear();
        notifyDataSetChanged();
    }


    public void fastUpdate(ArrayList<File> dirs, ArrayList<File> files) {
        this.clear();
        this.fileList.addAll(dirs);
        this.fileList.addAll(files);
        notifyDataSetChanged();
    }

    public void update(String folderPath) {
        File[] files = new File(folderPath).listFiles(new FileFilter() {
            @Override
            public boolean accept(File pathname) {
                return pathname.isDirectory()
                        || pathname.getName().endsWith(".DAT")
                        || pathname.getName().endsWith(".dat");
            }
        });
        ArrayList<File> dirsList = new ArrayList<>();
        ArrayList<File> fileList = new ArrayList<>();
        for (File file : files) {
            if (file.isDirectory()) {
                dirsList.add(file);
            }
            else {
                fileList.add(file);
            }
        }

        fastUpdate(dirsList, fileList);
    }

    public File getFile(int index) {
        if (index < 0 || index >= fileList.size()) {
            return null;
        }
        return fileList.get(index);
    }

    @Override
    public Object getItem(int position) {
        if (fileList == null || position >= fileList.size()) {
            return null;
        }
        return fileList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        File file = fileList.get(position);
        if (file.isDirectory()) {
            convertView = inflater.inflate(R.layout.file_list_item_folder, null);
            TextView textView = (TextView) convertView.findViewById(R.id.itemFolderNameTxtV);
            textView.setText(file.getName());
        }
        else {
            convertView = inflater.inflate(R.layout.file_list_item_file, null);
            TextView textView = (TextView) convertView.findViewById(R.id.itemFileNameTxtV);
            textView.setText(file.getName());
        }
        return convertView;
    }


}
