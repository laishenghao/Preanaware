package com.haoye.preanaware.transmit;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;
import java.util.ArrayList;

import com.haoye.preanaware.R;
import com.haoye.preanaware.transmit.model.Message;


/**
 * @brief
 * @detail
 * @see BaseAdapter
 * @author Haoye
 * @date 2017/2/26
 */

public class MessageListAdapter extends BaseAdapter{
    /**
     * layout inflater
     */
    private LayoutInflater inflater;
    /**
     * device list
     */
    private ArrayList<Message> msgList = new ArrayList<>();

    public MessageListAdapter(Context context) {
        inflater = LayoutInflater.from(context);
    }

    public Message getMessage(int index) {
        return msgList.get(index);
    }

    public void addMessage(Message message) {
        if (message != null) {
            msgList.add(message);
            this.notifyDataSetChanged();
        }
    }

    public void removeMessage(int index) {
        msgList.remove(index);
        this.notifyDataSetChanged();
    }

    public void removeMessage(Message message) {
        msgList.remove(message);
        this.notifyDataSetChanged();
    }

    public void clear() {
        msgList.clear();
        this.notifyDataSetChanged();
    }


    @Override
    public int getCount() {
        return msgList.size();
    }

    @Override
    public Object getItem(int position) {
        return msgList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        Message message = msgList.get(position);
        if (message.isSendMsg()) {
            convertView       = inflater.inflate(R.layout.msg_list_item_send, null);
            TextView textView = (TextView) convertView.findViewById(R.id.itemMsgSendTxtV);
            textView.setText(message.getText());
        }
        else {
            convertView       = inflater.inflate(R.layout.msg_list_item_recv, null);
            TextView textView = (TextView) convertView.findViewById(R.id.itemMsgRecvTxtV);
            textView.setText(message.getText());
        }
        return convertView;
    }

//    private class Holder {
//        TextView textView;
//    }
}
