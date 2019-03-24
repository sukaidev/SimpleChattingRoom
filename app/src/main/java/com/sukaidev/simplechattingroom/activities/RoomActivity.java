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
import android.util.Log;
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
 */
public class RoomActivity extends AppCompatActivity implements Client.OnReadHandlerListener {

    private RecyclerView mRecyclerView;
    private MsgAdapter mAdapter;
    private AppCompatEditText mEdtContent;
    private AppCompatButton mBtnSend;

    private ArrayList<Msg> mData = new ArrayList<>();

    private String mName;

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

        mRecyclerView = findViewById(R.id.recycler_view);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutCompat.VERTICAL, false));
        mAdapter = new MsgAdapter(this, mData);
        mRecyclerView.setAdapter(mAdapter);

        mEdtContent = findViewById(R.id.message_edit);
        mBtnSend = findViewById(R.id.send_message);

        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    final Client client = Client.start(RoomActivity.this);
                    if (client == null) {
                        mHandler.sendEmptyMessage(-1);
                    } else {
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
                        Toast.makeText(activity, "连接服务器失败！", Toast.LENGTH_LONG).show();
                        activity.finish();
                        break;
                    case 0:
                        activity.initButton((Client) msg.obj);
                        break;
                    case 1:
                        activity.mEdtContent.setText("");
                        break;
                    default:
                        break;
                }
            }
        }
    }

    private void initButton(final Client client) {

        mClient = client;

        mBtnSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String content = mEdtContent.getText().toString();
                if (content.equals("")) {
                    Toast.makeText(RoomActivity.this, "请填写消息内容！", Toast.LENGTH_SHORT).show();
                } else {
                    final Msg msg = new Msg(Msg.TYPE_SENT, mName, content);
                    mData.add(msg);
                    mAdapter.notifyItemInserted(mData.size() - 1);
                    final String json = JSON.toJSONString(msg);
                    Log.d("button", json);
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            client.send(json);
                            mHandler.sendEmptyMessage(1);
                        }
                    }).start();
                }
            }
        });
    }

    @Override
    public void onReceive(String message) {
        Log.d("onReceive", message);
        Msg msg = JSON.parseObject(message, Msg.class);
        msg.setType(Msg.TYPE_RECEIVED);
        mData.add(msg);
        mAdapter.notifyItemInserted(mData.size() - 1);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        if (mClient != null) {
            mClient.exit();
        }
    }
}
