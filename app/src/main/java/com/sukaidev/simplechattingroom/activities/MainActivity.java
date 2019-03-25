package com.sukaidev.simplechattingroom.activities;

import android.content.Intent;
import android.support.design.widget.TextInputEditText;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.sukaidev.simplechattingroom.R;
import com.sukaidev.simplechattingroom.view.ProfilePhotoView;

import java.util.ArrayList;
import java.util.List;


public class MainActivity extends AppCompatActivity {

    @SuppressWarnings("FieldCanBeLocal")
    private Button mBtnJoin;
    private TextInputEditText mEditTxt;

    private List<Integer> mProfilePhotos = new ArrayList<>();
    private ProfilePhotoView mProfileView;
    private int index = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.hide();
        }
        init();
        setListener();
    }

    private void init() {

        mBtnJoin = findViewById(R.id.join_chat);
        mEditTxt = findViewById(R.id.nick_name);
        mProfileView = findViewById(R.id.profile_photo);


        if (mProfilePhotos.size() == 0) {
            mProfilePhotos.add(R.drawable.profile_photo_00);
            mProfilePhotos.add(R.drawable.profile_photo_01);
            mProfilePhotos.add(R.drawable.profile_photo_02);
            mProfilePhotos.add(R.drawable.profile_photo_03);
            mProfilePhotos.add(R.drawable.profile_photo_04);
        }
    }

    private void setListener() {

        mProfileView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (index < mProfilePhotos.size() - 1) {
                    mProfileView.setImageResource(mProfilePhotos.get(++index));
                } else {
                    index = 0;
                    mProfileView.setImageResource(mProfilePhotos.get(index));
                }
            }
        });

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
            intent.putExtra("profileId", mProfileView.getResId());
            startActivity(intent);
        } else {
            mEditTxt.setError("请输入昵称！");
        }
    }

}
