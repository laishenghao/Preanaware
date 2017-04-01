package com.haoye.preanaware.transmit;

import android.content.Context;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.haoye.preanaware.R;
import com.haoye.preanaware.bluetooth.Ble;
import com.haoye.preanaware.transmit.model.Message;
import com.haoye.preanaware.utils.Constants;

/**
 * @brief
 * @detail
 * @see
 * @author Haoye
 * @date 2017/2/25
 */

public class BottomBar implements View.OnClickListener{
    private Context      context;
    private Ble          ble;
    private View         rootView;
    private LinearLayout msgBarWrap;
    private LinearLayout ctrlBarWrap;
    private EditText     editText;
    private MessageListAdapter adapter;
//    private TextView

    public BottomBar(Context context, View rootView, MessageListAdapter adapter, Ble ble) {
        this.context  = context;
        this.rootView = rootView;
        this.adapter  = adapter;
        this.ble      = ble;
    }

    public void init() {
        initToggleButton();
        initAddOrSendButton();
        initRegulateTimeButton();
        initModifyParameter();
    }

    private void initToggleButton() {
        msgBarWrap  = (LinearLayout) rootView.findViewById(R.id.msgBarWrap);
        ctrlBarWrap = (LinearLayout) rootView.findViewById(R.id.ctrlBarWrap);

        ImageView msgBarToggleTxtV     = (ImageView) rootView.findViewById(R.id.msgBarToggleBtn);
        ImageView controlBarToggleTxtV = (ImageView) rootView.findViewById(R.id.controlBarToggleBtn);
        msgBarToggleTxtV.setOnClickListener(this);
        controlBarToggleTxtV.setOnClickListener(this);

    }

    private void initAddOrSendButton() {
        editText  = (EditText) rootView.findViewById(R.id.sendEditText);
        TextView addOrSendBtn = (TextView) rootView.findViewById(R.id.addOrSendBtn);
        addOrSendBtn.setOnClickListener(this);
    }

    private void initRegulateTimeButton() {
        TextView textView = (TextView) rootView.findViewById(R.id.regulateTimeTxtV);
        textView.setOnClickListener(this);
    }

    private void initModifyParameter() {
        TextView textView = (TextView) rootView.findViewById(R.id.modifyParameterTxtV);
        textView.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.msgBarToggleBtn:
            case R.id.controlBarToggleBtn:
                onToggleBtnClick();
                break;
            case R.id.addOrSendBtn:
                onAddOrSendBtnClick();
                break;
            case R.id.regulateTimeTxtV:
                onRegulateTimeBtnClick();
                break;
            case R.id.modifyParameterTxtV:
                onModifyParameterBtnClick();
                break;
            default:
                break;
        }
    }

    private void onAddOrSendBtnClick() {
        // TODO: 2017/2/26  add the "add" function

        String str = editText.getText().toString();
        if (str.length() <= 0) {
            Toast.makeText(context, "请输入要发送的信息！", Toast.LENGTH_LONG).show();
            editText.setText("");
        }
        else if (ble.isEnabled() && ble.isConnected()) {
            ble.write(str.getBytes());
            adapter.addMessage(new Message(str, Message.TYPE_SEND_TEXT));
            editText.setText("");
        }
        else {
            Toast.makeText(context, "当前蓝牙不可用或未连接！", Toast.LENGTH_LONG).show();
        }
    }

    private void onToggleBtnClick() {
        if (msgBarWrap.getVisibility() != View.VISIBLE) {
            msgBarWrap.setVisibility(View.VISIBLE);
            ctrlBarWrap.setVisibility(View.GONE);
        }
        else {
            msgBarWrap.setVisibility(View.GONE);
            ctrlBarWrap.setVisibility(View.VISIBLE);
            // hide softInputForm
            hideSoftInputForm();
        }
    }

    private void hideSoftInputForm() {
        InputMethodManager manager =
                (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
        manager.hideSoftInputFromWindow(editText.getWindowToken(), 0);
    }

    private void onRegulateTimeBtnClick() {
        Message msg = new Message(Constants.COMMAND_REGULATE_TIME_DISPLAY, Message.TYPE_SEND_TEXT);
        adapter.addMessage(msg);
        ble.write(Constants.COMMAND_REGULATE_TIME_TRANSMIT.getBytes());
    }

    private void onModifyParameterBtnClick() {
        Message msg = new Message(Constants.COMMAND_OBTAIN_DATA_DISPLAY, Message.TYPE_SEND_TEXT);
        adapter.addMessage(msg);
        ble.write(Constants.COMMAND_OBTAIN_DATA_TRANSMIT.getBytes());
    }

}
