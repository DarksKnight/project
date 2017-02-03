package com.express56.xq.model;

import java.io.Serializable;

/**
 * 报警桩点信息
 * Created by SEELE on 2016/8/24.
 */
public class AlarmSite implements Serializable{

    public String alarmtime = "";

    public String sitename = "";

    public String gps_point = "";

    public double lat = 0D;

    public double lon = 0D;

}
