package com.express56.xq.model;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * 报警追踪详细信息类
 * Created by SEELE on 2016/8/24.
 */
public class AlarmBikeRouteInfo implements Serializable{

    public String bikename = "";

    public String bike_img = "";

    /**
     * 用于报警桩点文字绘制
     */
    public ArrayList<AlarmSite> alarmList = null;

    /**
     * 用于地图上停车点的绘制
     */
    public ArrayList<AlarmSite> mapAlarmList = null;

    public AlarmBikeRouteInfo() {

    }

    public AlarmBikeRouteInfo(String bikename, String bikeimg, ArrayList<AlarmSite> alarmList, ArrayList<AlarmSite> mapAlarmList) {
        this.bikename = bikename;
        this.bike_img = bikeimg;
        this.alarmList = alarmList;
        this.mapAlarmList = mapAlarmList;
    }
}
