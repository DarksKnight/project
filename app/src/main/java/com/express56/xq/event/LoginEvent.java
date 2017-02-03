package com.express56.xq.event;

/**
 * Created by qzy on 2016/6/7.
 * <p/>
 * 登录事件
 */
public class LoginEvent extends BikeEvent {

    public String userId = null;

    public LoginEvent(String userId) {
        this.userId = userId;
    }
}
