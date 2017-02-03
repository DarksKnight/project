package com.express56.xq.model;

import java.io.Serializable;

/**
 * Created by SEELE on 2016/6/25.
 */
public class BoundBikeInfo implements Serializable{
//    {"battery":23,"bike_status":1,"bikesite_id":3,"bikesite_name":"桩点3","ble_name":"test","ble_serial":"12341","ble_type":1,"id":1,"insite_status":1,"lock_status":2,"user_id":2,"vlock_status":1}
    /**
     *  蓝牙序列号
     */
    public String ble_serial = "";

    /**
     * 蓝牙设备名称
     */
    public String ble_name = "";

    /**
     * 蓝牙类型 1追踪器，2智能锁
     */
    public int ble_type = 0;

    /**
     * 智能锁 1锁闭，2锁开，3异常
     */
    public int lock_status = 0;

    /**
     * 出租 1可租用，2出租中
     */
    public int bike_status = 0;

    /**
     * 追踪器 1：锁  2：开  3:异常
     */
    public int vlock_status = 0;

    /**
     * 当前所在的位置的GPS
     */
//    public String position = "";

    /**
     *  电量
     */
    public int battery = 0;

    /**
     * 用户id
     */
    public int user_id = 0;

    /**
     * 停车港id
     */
    public int bikesite_id = 0;

    /**
     *  2：不在港   1:在港
     */
    public int insite_status = 0;

    /**
     * 桩点名称
     */
    public String bikesite_name = "";

    /**
     *
     */
    public int id = 0;

    public String bike_img = "";

}
