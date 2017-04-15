package com.haoye.preanaware.viewer.model;

import com.haoye.preanaware.viewer.FileUtil;

import java.io.File;
import java.io.IOException;

/**
 * @author Haoye
 * @brief
 * @detail
 * @date 2017-03-17
 * @see
 */

public class PreanFileManager {

    public static String restore(String sourcePath) {
        PreanFile tempPrean = PreanFile.create(sourcePath);
        if (tempPrean == null) {
            return null;
        }
        String folder = FileUtil.getPreanFileHomePath() +  "/ID_" + tempPrean.getGaugeId();
        if (!FileUtil.createDir(folder)) {
            return null;
        }

        String desPath = folder + "/" + tempPrean.getDefaultName();
        try {
            tempPrean.close();
            File temp = new File(sourcePath);
            if (temp.renameTo(new File(desPath))) {
                return desPath;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }


}
