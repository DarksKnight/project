package com.express56.xq.model;

import java.io.Serializable;

/**
 * Created by Administrator on 2016/5/20.
 */
public class User implements Serializable {

//    "token": "be4cb025804648d7b6ceefe2816d3aab6zNb16m1zNxqZ36GwV75R2qap7Af5F8cB",
//            "userType": "3",
//            "name": "admin",
//            "wifiUpload": "0"


    /**
     * 缓存起来，除非登出时清除,重新登陆时更新，以后部分请求需要用到
     */
    public String token = null;

    /**
     * 用户类型
     */
    public int userType = 0;

    public int wifiUpload = 0;


    public User() {

    }

    @Override
    public String toString() {
        return "User->"
                + "|token = " + token
                + "|userType = " + userType
                + "|phone = " + phone
                + "|name = " + name
                + "|commonAccount = " + commonAccount
                + "|installmentAccount = " + installmentAccount
                + "|creditAccount = " + creditAccount
                + "|creditLimit = " + creditLimit
                + "|cashAccount = " + cashAccount
                + "|wifiUpload = " + wifiUpload
                + "|userPhoto = " + userPhoto
                ;
    }

    /**
     * 用户手机号码
     */
    public String phone = "";

    public String name = "";

    public String commonAccount = "";

    public String installmentAccount = "";

    public String creditAccount = "";

    public String creditLimit = "";

    public String cashAccount = "";

    /**
     * 头像地址
     */
    public String userPhoto = null;

}
