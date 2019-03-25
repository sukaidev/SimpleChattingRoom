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
import com.sukaidev.simplechattingroom.bean.Msg;
import com.sukaidev.simplechattingroom.view.ProfilePhotoView;

import java.util.ArrayList;

/**
 * Created by sukaidev on 2019/03/15.
 */
public class MsgAdapter extends RecyclerView.Adapter<MsgAdapter.MsgViewHolder> {

    private static final int TYPE_LEFT = Msg.TYPE_RECEIVED;
    private static final int TYPE_RIGHT = Msg.TYPE_SENT;

    private View leftLayout;
    private View rightLayout;

    private Context context;
    private ArrayList<Msg> data;


    public MsgAdapter(Context context, ArrayList<Msg> data) {
        this.context = context;
        this.data = data;
    }


    @NonNull
    @Override
    public MsgViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {
        if (viewType == TYPE_LEFT) {
            leftLayout = LayoutInflater.from(context).inflate(R.layout.msg_left, viewGroup, false);
            return new MsgViewHolder(leftLayout);
        } else {
            rightLayout = LayoutInflater.from(context).inflate(R.layout.msg_right, viewGroup, false);
            return new MsgViewHolder(rightLayout);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull MsgViewHolder msgViewHolder, int position) {
        Msg msg = data.get(position);
        if (msg.getType() == TYPE_RIGHT) {
            msgViewHolder.profilePhoto.setImageResource(R.drawable.profile_photo_00);
            msgViewHolder.content.setText(msg.getContent());
        } else {
            msgViewHolder.profilePhoto.setImageResource(R.drawable.profile_photo_01);
            msgViewHolder.userName.setText(msg.getName());
            msgViewHolder.content.setText(msg.getContent());
        }
    }

    @Override
    public int getItemCount() {
        if (data != null) {
            return data.size();
        }
        return -1;
    }

    @Override
    public int getItemViewType(int position) {
        Msg msg = data.get(position);
        if (msg != null) {
            if (msg.getType() == Msg.TYPE_SENT) {
                return TYPE_RIGHT;
            } else {
                return TYPE_LEFT;
            }
        }
        return TYPE_RIGHT;
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
