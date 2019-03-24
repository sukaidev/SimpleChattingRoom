package com.sukaidev.simplechattingroom.activities;

import android.support.design.widget.TextInputEditText;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.sukaidev.simplechattingroom.R;


public class MainActivity extends AppCompatActivity {

    @SuppressWarnings("FieldCanBeLocal")
    private Button mBtnJoin;
    private TextInputEditText mEditTxt;

    private AlertDialog mDialog;
//    private OnClientConnectedListener mListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.hide();
        }


        mBtnJoin = findViewById(R.id.join_chat);
        mEditTxt = findViewById(R.id.nick_name);

        mDialog = new AlertDialog.Builder(this)
                .setCancelable(false)
                .setMessage("正在加入聊天室...")
                .setTitle("Loading").create();

        mBtnJoin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                joinChat();
            }
        });
    }

    @SuppressWarnings("ConstantConditions")
    private void joinChat() {
        mDialog.show();
        String name = mEditTxt.getText().toString();
        if (!name.equals("")) {
            mDialog.dismiss();
        } else {
            mDialog.dismiss();
            mEditTxt.setError("请输入昵称！");
        }
    }


/*    public interface OnClientConnectedListener {
        void startChat(ServerInfo info);
    }

    public void setOnClientConnectedListener(OnClientConnectedListener listener) {
        this.mListener = listener;
    }*/
}
