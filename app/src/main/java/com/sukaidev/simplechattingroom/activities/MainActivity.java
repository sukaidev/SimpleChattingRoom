package com.sukaidev.simplechattingroom.activities;

import android.content.Intent;
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

        mBtnJoin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                joinChat();
            }
        });
    }

    @SuppressWarnings("ConstantConditions")
    private void joinChat() {
        String name = mEditTxt.getText().toString();
        if (!name.equals("")) {
            Intent intent = new Intent(MainActivity.this, RoomActivity.class);
            intent.putExtra("name", name);
            startActivity(intent);
        } else {
            mEditTxt.setError("请输入昵称！");
        }
    }

}
