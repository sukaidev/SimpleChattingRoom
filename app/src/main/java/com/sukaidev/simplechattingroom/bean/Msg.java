package com.sukaidev.simplechattingroom.bean;

/**
 * Created by sukaidev on 2019/03/18.
 * 消息实体类.
 */
public class Msg {

    public static final int TYPE_RECEIVED = 0;

    public static final int TYPE_SENT = 1;

    private int type; // 消息类型
    private String user;  //  用户名
    private int profilePhotoId;
    private String content;  //  消息主体

    // 必须，用于fastJson解析
    public Msg() {
        super();
    }

    public Msg(int type, String user, int profilePhotoId, String content) {
        this.content = content;
        this.user = user;
        this.profilePhotoId = profilePhotoId;
        this.type = type;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public int getProfilePhotoId() {
        return profilePhotoId;
    }

    public void setProfilePhotoId(int profilePhotoId) {
        this.profilePhotoId = profilePhotoId;
    }
}
