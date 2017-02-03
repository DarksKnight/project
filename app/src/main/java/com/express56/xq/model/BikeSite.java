package com.express56.xq.model;

import java.io.Serializable;

/**
 * Created by Administrator on 2016/5/20.
 * 停车点位对象
 */
public class BikeSite implements Serializable {

    public int id = 0;

    public int school_id = 0;

    public String name = null;

    public String description = null;

    public float rent_charge = 0.0f;

    public float return_charge = 0.0f;

    public String gps_point = "";

    public int radius = 0;

    public int bike_count = 0;

    public int available_count = 0;

    public float distance = 0;

    /**
     *  1:普通 2: 防盗 3:租车
     */
    public int type = 0;

    public BikeSite() {

    }

    @Override
    public String toString() {
        return "available_bikes = " + available_count
                + "|bike_count = " + bike_count
                + "|school_id = " + school_id
                + "|return_charge = " + return_charge
                + "|description = " + description
                + "|rent_charge = " + rent_charge
                + "|gps_point = " + gps_point
                + "|name = " + name
                + "|type = " + type
                + "|distance = " + distance
                + "|radius = " + radius
                + "|userId = " + id;
    }
}
