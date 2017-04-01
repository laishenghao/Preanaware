package com.haoye.preanaware.transmit.model;

/**
 * @brief message
 * @detail
 * @see
 * @author Haoye
 * @date 2017/2/26
 */
public class Message {
    public static final int TYPE_RECV_TEXT = 0;
    public static final int TYPE_RECV_FILE = 1;
    public static final int TYPE_SEND_TEXT = 2;
    public static final int TYPE_SEND_FILE = 3;

    private String text = "";
    private int    type = TYPE_SEND_TEXT;

    public Message() {

    }

    public Message(String text, int type) {
        this.text = text;
        this.type = type;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public boolean isFile() {
        return type == TYPE_RECV_FILE || type == TYPE_SEND_FILE;
    }

    public boolean isSendMsg() {
        return type == TYPE_SEND_TEXT || type == TYPE_SEND_FILE;
    }

    public boolean isRecvMsg() {
        return !isSendMsg();
    }

    public int length() {
        return text.length();
    }
}
