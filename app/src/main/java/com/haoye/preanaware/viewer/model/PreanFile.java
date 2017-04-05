package com.haoye.preanaware.viewer.model;

import android.annotation.SuppressLint;
import android.util.Log;

import com.haoye.preanaware.viewer.FileUtil;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.Calendar;
import java.util.Date;

/**
 * @brief
 * @detail
 * @see RandomAccessFile
 * @author Haoye
 * @date 2017/2/24
 */

public class PreanFile extends RandomAccessFile {
    public static final String FILE_TYPE_USB10  = "USB1.0";
    public static final String FILE_TYPE_NRF10  = "nRF1.0";
    public static final String FILE_TYPE_USMS10 = "USMS1.0";
    public static final String FILE_TYPE_BLE10  = "BLE1.0";

    public static final int OFFSET_TYPE = 0;

    private PreanFile(String path) throws FileNotFoundException {
        this(path, "r");
    }

    public static PreanFile create(String path) {
        PreanFile file;
        try {
            file = new PreanFile(path);
            if (FILE_TYPE_USB10.equals(file.getType())
             || FILE_TYPE_NRF10.equals(file.getType())
             || FILE_TYPE_BLE10.equals(file.getType())
             || FILE_TYPE_USMS10.equals(file.getType())) {
                return file;
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        return null;
    }

    public PreanFile(String name, String mode) throws FileNotFoundException {
        super(name, mode);
    }

    public PreanFile(File file, String mode) throws FileNotFoundException {
        super(file, mode);
    }

    public String getDefaultName() {
        return "ID" + getGaugeId()
                + "_" + getStartRecordTimeCnString()
                + ".DAT";
    }


    public String getType() {
        return getString(0, 8);
    }

    public Calendar getCreatedTime() {
        Calendar calendar = Calendar.getInstance();
        try {
            this.seek(8);
            int year  = readByte() + 2000;
            int month = readByte();
            int date  = readByte();
            int hour  = readByte();
            int min   = readByte();
            int sec   = readByte();
            calendar.set(year, month, date, hour, min, sec);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return calendar;
    }

    public String getCreatedTimeString() {
        String time = "";
        try {
            this.seek(8);
            int year  = readByte() + 2000;
            int month = readByte();
            int date  = readByte();
            int hour  = readByte();
            int min   = readByte();
            int sec   = readByte();
            time = year
                 + "-" + month
                 + "-" + date
                 + " " + hour
                 + ":" + min
                 + ":" + sec;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return time;
    }

    @SuppressLint("DefaultLocale")
    public String getGaugeId() {
        String idStr = "";
        try {
            this.seek(14);
            int id = readHexShort();
            String format = "%04d";
            idStr = String.format(format, id);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return idStr;
    }

    public int getRecordInterval() {
        return getHexValue(16, 2);
    }

    public int getRecordChannelCount() {
        return getHexValue(18, 1);
    }

    public String getStartRecordDate() {
        String time = "";
        try {
            this.seek(19);
            int year  = readByte() + 2000;
            int month = readByte();
            int date  = readByte();
            time = year
                 + "-" + month
                 + "-" + date;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return time;
    }

    public Calendar getStartRecordTime() {
        Calendar calendar = Calendar.getInstance();
        try {
            this.seek(19);
            int year  = readByte() + 2000;
            int month = readByte();
            int date  = readByte();
            int hour  = readByte();
            int min   = readByte();
            int sec   = readByte();
            calendar.set(year, month, date, hour, min, sec);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return calendar;
    }

    public int getChannel1DecimalCount() {
        int cnt = 0;
        try {
            seek(56);
            cnt = 0xff & readByte();
        }
        catch (IOException e) {
            e.printStackTrace();
        }
        return cnt;
    }

    public String getStartRecordTimeString() {
        String time = "";
        try {
            this.seek(19);
            int year  = readByte() + 2000;
            int month = readByte();
            int date  = readByte();
            int hour  = readByte();
            int min   = readByte();
            int sec   = readByte();
            time = year
                 + "-" + month
                 + "-" + date
                 + " " + hour
                 + ":" + min
                 + ":" + sec;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return time;
    }

    public String getStartRecordTimeCnString() {
        String time = "";
        try {
            this.seek(19);
            int year  = readByte() + 2000;
            int month = readByte();
            int date  = readByte();
            int hour  = readByte();
            int min   = readByte();
            int sec   = readByte();
            time = year  + " 年 "
                 + month + " 月 "
                 + date  + " 日 "
                 + hour  + " 时 "
                 + min   + " 分 "
                 + sec   + " 秒";
        } catch (IOException e) {
            e.printStackTrace();
        }
        return time;
    }

    public int getRecordCount() {
        return getHexValue(25, 4);
    }

    public int getChannelOneRecord(int index) {
        int value = -1;
        int channel = getRecordChannelCount();

        try {
            if (channel == 1) {
                seek(128 + index * 2);
                value = readHexShort();
            }
            else if (channel == 2) {
                seek(128 + index * 4);
                value = readHexShort();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return value;
    }

    public int[] getChannelOneRecords() {
        int[] values = new int[getRecordCount()];
        int channel = getRecordChannelCount();
        if (channel == 1) {
            try {
                seek(128);
                for (int i = 0; i < values.length; i++) {
                    values[i] = readHexShort();
                }

            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        else {
            try {
                seek(128);
                for (int i = 0; i < values.length; i++) {
                    skipBytes(2);
                    values[i] = readHexShort();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return values;
    }

    public int getChannelTwoRecord(int index) {
        int value = -1;
        int channel = getRecordChannelCount();
        if (channel == 2) {
            try {
                seek(128 + 2 + index * 4);
                value = readHexShort();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        return value;
    }

    public double[] getSampleValues() {
        double[] values = new double[6];
        try {
            seek(56);
            final int n = readByte();
            int denominator = 1;
            for (int i = 0; i < n; i++) {
                denominator *= 10;
            }

            seek(32);
            for (int i = 0; i < values.length; i++) {
                values[i] = readHexShort() * 1.0 / denominator;
                skipBytes(2);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return values;
    }

    public double[] getSettingValues() {
        double[] values = new double[6];
        try {
            seek(56);
            final int n = readByte();
            int denominator = 1;
            for (int i = 0; i < n; i++) {
                denominator *= 10;
            }

            seek(32);
            for (int i = 0; i < values.length; i++) {
                skipBytes(2);
                values[i] = readHexShort() * 1.0 / denominator;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return values;
    }

    public int getUpLimitAlarmValue() {
        return getHexValue(57, 2);
    }

    public double getVoltage() {
        return getHexValue(64, 2) / 100.0;
    }

    public String getVersion() {
        return getString(66, 6);
    }

    public int getHexValue(int offset, int count) {
        int result = -1;
        try {
            byte[] bytes = new byte[count];
            int realCount;
            do {
                seek(offset);
                realCount = read(bytes);
            } while (realCount != -1 && realCount != bytes.length);
            int temp = 0;
            for (int i = bytes.length - 1; i >= 0; i--) {
                temp = (temp<<8) + (0xff & bytes[i]);
            }
            result = temp;

        } catch (IOException e) {
            e.printStackTrace();
        }
        return result;
    }

    public short readHexShort() throws IOException {
        int b1 = 0xff & readByte();
        int b2 = 0xff & readByte();
        return (short) ((b2 << 8) + b1);
    }

    public int readHexInt() throws IOException {
        int b1 = 0xff & readByte();
        int b2 = 0xff & readByte();
        int b3 = 0xff & readByte();
        int b4 = 0xff & readByte();
        return (b4 << 24) + (b3 << 16) + (b2 << 8) + b1;
    }

    public String getString(int offset, int len) {
        String str = "";
        try {
            this.seek(offset);
            byte[] bytes = new byte[len];
            this.read(bytes);
            str = FileUtil.bytesToString(bytes);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return str.trim();
    }
}
