package com.express56.xq.model;

/**
 * 根据手机号码获取用户车辆停靠站点的信息
 * Created by Administrator on 2016/5/25.
 */
public class UserSite {
    public String btSn = null;
    public String phone = null;
    public String siteName = null;
    public String userId = null;

    public UserSite(){

    }

    public UserSite(String btSn, String phone, String siteName, String userId) {
        this.btSn = btSn;
        this.phone = phone;
        this.siteName = siteName;
        this.userId = userId;
    }

}