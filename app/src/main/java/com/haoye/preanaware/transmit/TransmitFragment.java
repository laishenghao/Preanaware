package com.haoye.preanaware.transmit;


import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import com.haoye.preanaware.R;
import com.haoye.preanaware.bluetooth.Ble;
import com.haoye.preanaware.transmit.model.Message;
import com.haoye.preanaware.transmit.model.MessageReceiver;
import com.haoye.preanaware.utils.Converter;
import com.haoye.preanaware.viewer.FileUtil;


/**
 * A simple {@link Fragment} subclass.
 */
public class TransmitFragment extends Fragment {
    private View               rootView;
    private Ble                ble;
    private ListView           msgListView;
    private MessageListAdapter msgListAdapter;
    private MessageReceiver    msgReceiver;

    public TransmitFragment() {
        // Required empty public constructor
    }

    public static TransmitFragment create(Ble ble) {
        TransmitFragment fragment = new TransmitFragment();
        fragment.ble = ble;
        return fragment;
    }

    private void findViews() {
        rootView    = getActivity().getLayoutInflater().inflate(R.layout.fragment_transmit, null);
        msgListView = (ListView) rootView.findViewById(R.id.msgListV);
    }

    private void initBottomBar() {
        BottomBar bottomBar = new BottomBar(getContext(), rootView, msgListAdapter, ble);
        bottomBar.init();
    }

    private void initMessageList() {
        msgListAdapter = new MessageListAdapter(getContext());
        msgListView.setAdapter(msgListAdapter);
        msgListView.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
        msgListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Message msg = msgListAdapter.getMessage(position);
                if (msg.isFile()) {
                    openFile(msg.getText());
                }
            }
        });
        msgListView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, final int position, long id) {
                AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                builder.setItems(new CharSequence[]{"删除此条", "清空消息", "取消"},
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                switch (which) {
                                case 0:
                                    msgListAdapter.removeMessage(position);
                                    break;
                                case 1:
                                    msgListAdapter.clear();
                                    break;
                                default:
//                                        Toast.makeText(getContext(), "此功能尚未实现！", Toast.LENGTH_SHORT).show();
                                    break;
                                }
                            }
                        });
                builder.show();
                return true;
            }
        });
    }

    public void openFile(String name) {
        // TODO: 2017/2/26
    }

    private Message progressMessage;
    private void initMsgReceiver() {
        msgReceiver = new MessageReceiver();
        ble.setOnReceivedDataListener(msgReceiver);

        msgReceiver.setTextReceiveListener(new MessageReceiver.OnTextReceiveListener() {
            @Override
            public void onReceived(final String msg) {
                addMessageThroughUiThread(msg, Message.TYPE_RECV_TEXT);
            }
        });

        msgReceiver.setFileReceiveListener(new MessageReceiver.OnFileReceiveListener() {
            @Override
            public void onStart(String title, int len) {
                addMessageThroughUiThread(title + len, Message.TYPE_RECV_TEXT);
//                getActivity().runOnUiThread(new Runnable() {
//                    @Override
//                    public void run() {
//                        progressMessage = new Message("传输已完成 0%", Message.TYPE_RECV_TEXT);
//                        msgListAdapter.addMessage(progressMessage);
//                    }
//                });
            }

            @Override
            public void onReceive(final int progress) {
//                getActivity().runOnUiThread(new Runnable() {
//                    @Override
//                    public void run() {
//                        progressMessage.setText("传输已完成 " + progress + "%");
//                        msgListAdapter.removeMessage(progressMessage);
//                        msgListAdapter.notifyDataSetChanged();
//                        msgListAdapter.addMessage(progressMessage);
//                        msgListAdapter.notifyDataSetChanged();
//                    }
//                });
            }

            @Override
            public void onError(String info) {
                addMessageThroughUiThread(info, Message.TYPE_RECV_TEXT);
            }

            @Override
            public void onReceived(final String path) {
                addMessageThroughUiThread(path, Message.TYPE_RECV_FILE);
            }
        });
    }

    private void addMessageThroughUiThread(final String info, final int type) {
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Message message = new Message(info, type);
                msgListAdapter.addMessage(message);
            }
        });
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        findViews();
        initMessageList();
        initMsgReceiver();
        initBottomBar();
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

    @Override
    public void onDestroy() {
        super.onDestroy();
    }
}
