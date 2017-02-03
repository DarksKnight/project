package com.express56.xq.model;

/**
 * 更新车辆状态操作对象
 * Created by Administrator on 2016/5/24.
 */
public class LockOpInfo {
    public String phone;

    public String status;

    public String bikeSite;

    public LockOpInfo() {

    }

    public LockOpInfo(String phone, String status, String bikeSite) {
        this.phone = phone;
        this.status = status;
        this.bikeSite = bikeSite;
    }
}
