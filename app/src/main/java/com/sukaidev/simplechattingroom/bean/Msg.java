package com.sukaidev.simplechattingroom.bean;

/**
 * Created by sukaidev on 2019/03/18.
 */
public class Msg {

    public static final int TYPE_RECEIVED = 0;

    public static final int TYPE_SENT = 1;

    private String content;

    private String name;

    private int type;

    public Msg(int type, String name, String content) {
        this.content = content;
        this.name = name;
        this.type = type;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }
}
