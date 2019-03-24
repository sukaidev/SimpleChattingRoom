package com.sukaidev.simplechattingroom.activities;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatButton;
import android.support.v7.widget.AppCompatEditText;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.sukaidev.simplechattingroom.R;
import com.sukaidev.simplechattingroom.adapter.MsgAdapter;
import com.sukaidev.simplechattingroom.bean.MessageInfo;

import java.util.ArrayList;

/**
 * Created by sukaidev on 2019/03/18.
 */
public class RoomActivity extends AppCompatActivity {

    private RecyclerView mRecyclerView;
    private MsgAdapter mAdapter;
    private AppCompatEditText mMessage;
    private AppCompatButton mBtnSend;

    private ArrayList<MessageInfo> mData = new ArrayList<>();

    private String mName;

    private boolean isExit = false;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat_room);

        init();
    }

    private void init() {
        mName = getIntent().getStringExtra("NickName");

        mRecyclerView = findViewById(R.id.recycler_view);
        mMessage = findViewById(R.id.message_edit);
        mBtnSend = findViewById(R.id.send_message);

        mAdapter = new MsgAdapter(this, mData);
        mRecyclerView.setAdapter(mAdapter);


        mBtnSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
    }

}
