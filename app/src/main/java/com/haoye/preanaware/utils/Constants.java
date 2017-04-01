package com.haoye.preanaware.utils;

/**
 * @brief constants
 * @detail
 * @see
 * @author Haoye
 * @date 2017/1/20
 */
public final class Constants {
    public final static int BLUETOOTH_REQUEST_ENABLE_CODE = 1;

    public final static String BLUETOOTH_UUID = "e5b152ed-6b46-09e9-4678-665e9a972cbc";

    public final static String SERVICE_UUID   = "0000fee0-0000-1000-8000-00805f9b34fb";
    public final static String CLIENT_UUID    = "00002902-0000-1000-8000-00805f9b34fb";
    public final static String CHARACTER_UUID = "0000fee1-0000-1000-8000-00805f9b34fb";

    public final static String ACTION_BLUETOOTH_RECEIVED_DATA = "com.haoye.bluetooth.received_data";

    public static final String OPENING_PREANFILE_PATH_KEY = "opening_preanfile_path_key";

    public static final String COMMAND_REGULATE_TIME_TRANSMIT = "[CMD]RegulateTime";
    public static final String COMMAND_REGULATE_TIME_DISPLAY  = "[指令] 校正时间\n(功能测试)";
    public static final String COMMAND_OBTAIN_DATA_TRANSMIT   = "[CMD]ObtainData";
    public static final String COMMAND_OBTAIN_DATA_DISPLAY    = "[指令] 抄表\n(功能测试)";
}
