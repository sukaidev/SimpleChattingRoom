package com.sukaidev.simplechattingroom.activities;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatButton;
import android.support.v7.widget.AppCompatEditText;
import android.support.v7.widget.LinearLayoutCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
import com.sukaidev.client.Client;
import com.sukaidev.simplechattingroom.R;
import com.sukaidev.simplechattingroom.adapter.MsgAdapter;
import com.sukaidev.simplechattingroom.bean.Msg;

import java.io.IOException;
import java.lang.ref.WeakReference;
import java.util.ArrayList;

/**
 * Created by sukaidev on 2019/03/18.
 * 聊天室.
 */
public class RoomActivity extends AppCompatActivity implements Client.OnReadHandlerListener, Client.OnWriteHandlerListener {

    private RecyclerView mRecyclerView;
    private MsgAdapter mAdapter;
    private AppCompatEditText mEdtContent;
    private AppCompatButton mBtnSend;

    private ArrayList<Msg> mData = new ArrayList<>();

    private String mName;

    private int mProfileId;

    private Client mClient;

    private final MainHandler mHandler = new MainHandler(this);

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat_room);

        init();

    }

    private void init() {

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.hide();
        }

        mName = getIntent().getStringExtra("name");
        mProfileId = getIntent().getIntExtra("profileId", R.drawable.profile_photo_00);

        mRecyclerView = findViewById(R.id.recycler_view);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutCompat.VERTICAL, false));
        mAdapter = new MsgAdapter(this, mData);
        mRecyclerView.setAdapter(mAdapter);

        mEdtContent = findViewById(R.id.message_edit);
        mBtnSend = findViewById(R.id.send_message);

        // 主线程无法使用网络
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    final Client client = Client.start(RoomActivity.this, RoomActivity.this);
                    if (client == null) {
                        mHandler.sendEmptyMessage(-1);
                    } else {
                        // 利用Handler将client对象发回主线程
                        Message message = Message.obtain();
                        message.obj = client;
                        message.what = 0;
                        mHandler.sendMessage(message);
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }).start();

        mBtnSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mClient == null) {
                    Toast.makeText(RoomActivity.this, "未连接到服务器！", Toast.LENGTH_LONG).show();
                } else {
                    String content = mEdtContent.getText().toString();
                    if (content.equals("")) {
                        Toast.makeText(RoomActivity.this, "请填写消息内容！", Toast.LENGTH_SHORT).show();
                    } else {
                        final Msg msg = new Msg(Msg.TYPE_SENT, mName, mProfileId, content);
                        final String json = JSON.toJSONString(msg);
                        mClient.send(json);
                        mHandler.sendEmptyMessage(1);
                    }
                }
            }
        });

    }

    // 消息发送成功回调
    @Override
    public void onSend(String message) {
        Msg msg = JSON.parseObject(message, Msg.class);
        msg.setType(Msg.TYPE_SENT);
        Message sendMessage = Message.obtain();
        sendMessage.obj = msg;
        sendMessage.what = 2;
        mHandler.sendMessage(sendMessage);
    }

    // 接收到消息回调
    @Override
    public void onReceive(String message) {
        Msg msg = JSON.parseObject(message, Msg.class);
        msg.setType(Msg.TYPE_RECEIVED);
        Message sendMessage = Message.obtain();
        sendMessage.obj = msg;
        sendMessage.what = 2;
        mHandler.sendMessage(sendMessage);
    }

    private void setClient(final Client client) {
        this.mClient = client;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        if (mClient != null) {
            mClient.exit();
        }
    }

    static class MainHandler extends Handler {

        private final WeakReference<RoomActivity> mActivity;

        MainHandler(RoomActivity activity) {
            this.mActivity = new WeakReference<>(activity);
        }

        @Override
        public void handleMessage(Message msg) {
            RoomActivity activity = mActivity.get();
            if (activity != null) {
                switch (msg.what) {
                    case -1:
                        // 客户端为空
                        Toast.makeText(activity, "连接服务器失败！", Toast.LENGTH_LONG).show();
                        activity.finish();
                        break;
                    case 0:
                        // 客户端成功连接到服务端，保存Client对象
                        activity.setClient((Client) msg.obj);
                        break;
                    case 1:
                        // 成功发送消息，清空发送框
                        activity.mEdtContent.setText("");
                        break;
                    case 2:
                        // 发送或接收到消息，进行界面刷新
                        activity.mData.add((Msg) msg.obj);
                        activity.mAdapter.notifyItemInserted(activity.mData.size() - 1);
                        break;
                    default:
                        break;
                }
            }
        }
    }
}
