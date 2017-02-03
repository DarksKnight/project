package com.express56.xq.model;

import java.io.Serializable;

/**
 * Created by SEELE on 2016/9/19.
 */
public class TripInfo implements Serializable{
    /**
     * 骑行开始时间
     * //            "start_time": "2016-09-19 13:44:57",
     */
    public String start_time = null;

    /**
     * 骑行起点名称
     */
    public String start_site_name = null;

    /**
     * 骑行终点名称
     */
    public String end_site_name = null;

    /**
     * 支付状态 是否支付
     */
    public int pay_status = 0;

    /**
     *  评价状态 是否已经评价
     */
    public int remarkstatus = 0;

    /**
     *  费用
     */
    public float payment = 0.0f;

    /**
     * 应付金额
     */
    public float should_pay = 0.0f;

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

    public String end_time = null;

    /**
     * 起点停车港名称
     */
    public String sitename = null;

    public String start_point = null;

    public String end_point = null;

    public int start_site_id = 0;
    public String remark = "";



}
