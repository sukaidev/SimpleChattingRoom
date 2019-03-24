package com.sukaidev.simplechattingroom.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.LinearLayoutCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.sukaidev.simplechattingroom.R;
import com.sukaidev.simplechattingroom.bean.MessageInfo;
import com.sukaidev.simplechattingroom.view.ProfilePhotoView;

import java.util.ArrayList;

/**
 * Created by sukaidev on 2019/03/15.
 */
public class MsgAdapter extends RecyclerView.Adapter<MsgAdapter.MsgViewHolder> {

    private static final int TYPE_LEFT = 0;
    private static final int TYPE_RIGHT = 1;

    private LinearLayoutCompat leftLayout;
    private LinearLayoutCompat rightLayout;

    private Context context;
    private ArrayList<MessageInfo> data;

    public MsgAdapter(Context context, ArrayList<MessageInfo> data) {
        this.context = context;
        this.data = data;
    }


    @NonNull
    @Override
    public MsgViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {
        if (viewType == TYPE_LEFT) {
            leftLayout = (LinearLayoutCompat) LayoutInflater.from(context).inflate(R.layout.msg_left, viewGroup);
            return new MsgViewHolder(leftLayout);
        } else {
            rightLayout = (LinearLayoutCompat) LayoutInflater.from(context).inflate(R.layout.msg_right, viewGroup);
            return new MsgViewHolder(rightLayout);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull MsgViewHolder msgViewHolder, int position) {
        MessageInfo msg = data.get(position);
        if (msg.getType() == TYPE_LEFT) {
            msgViewHolder.profilePhoto.setImageResource(R.drawable.profile_photo_01);
            msgViewHolder.userName.setText(msg.getUser());
            msgViewHolder.content.setText(msg.getContent());
        } else {
            msgViewHolder.profilePhoto.setImageResource(R.drawable.profile_photo_00);
            msgViewHolder.content.setText(msg.getContent());
        }
    }

    @Override
    public int getItemCount() {
        if (data != null) {
            return data.size();
        }
        return 0;
    }


    class MsgViewHolder extends RecyclerView.ViewHolder {

        private ProfilePhotoView profilePhoto;
        private TextView userName;
        private TextView content;

        MsgViewHolder(@NonNull View itemView) {
            super(itemView);
            if (itemView == leftLayout) {
                profilePhoto = itemView.findViewById(R.id.left_photo_view);
                userName = itemView.findViewById(R.id.user_name);
                content = itemView.findViewById(R.id.left_msg);
            } else {
                content = itemView.findViewById(R.id.right_msg);
                profilePhoto = itemView.findViewById(R.id.right_photo_view);
            }
        }
    }
}
