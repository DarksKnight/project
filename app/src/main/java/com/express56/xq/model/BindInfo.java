package com.express56.xq.model;

/**
 * Created by Administrator on 2016/5/24.
 */
public class BindInfo {
    public String user_id = null;
    public String serial = null;
    public String token = null;
    public String bike_img = null;

    public BindInfo() {

    }

    public BindInfo(String userId, String serial, String token) {
        this.user_id = userId;
        this.serial = serial;
        this.token = token;
    }

    public BindInfo(String userId, String serial, String token, String imgUrl) {
        this.user_id = userId;
        this.serial = serial;
        this.token = token;
        this.bike_img = imgUrl;
    }
}
