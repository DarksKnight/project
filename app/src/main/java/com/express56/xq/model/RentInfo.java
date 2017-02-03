package com.express56.xq.model;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by SEELE on 2016/9/19.
 */
public class RentInfo implements Serializable{

    /**
     * 纬度
     */
    public double lat = 0.0D;

    /**
     * 经度
     */
    public double lon = 0.0D;

    /**
     * 纬度
     */
    public double lat_end = 0.0D;

    /**
     * 经度
     */
    public double lon_end = 0.0D;

    /**
     * 订单编号
     */
    public String out_trade_no = null;

    /**
     * 车辆编号
     */
    public String ble_name = null;

    /**
     * 起点停车港名称
     */
    public String start_site_name = null;

    /**
     * 骑行开始时间
     * "start_time": "2016-09-19 13:44:57",
     */
    public String start_time = null;

    /**
     * 骑行终点名称
     */
    public String end_site_name = null;

    /**
     * 车锁密码
     */
    public String pwd = null;

    public ArrayList<BikeSite> bikesiteList = null;

    public RentInfo() {

    }


}
