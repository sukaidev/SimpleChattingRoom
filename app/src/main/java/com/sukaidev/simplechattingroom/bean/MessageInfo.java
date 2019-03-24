package com.sukaidev.simplechattingroom.bean;

/**
 * Created by sukaidev on 2019/03/18.
 */
public class MessageInfo {

    public static final int TYPE_RECEIVED = 0;

    public static final int TYPE_SENT = 1;

    private String content;

    private String user;

    private int type;

    public MessageInfo(String content, String user, int type) {
        this.content = content;
        this.user = user;
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
}
