package com.express56.xq.model;

/**
 * Created by Administrator on 2016/5/17.
 * 锁车记录对象
 */
public class LockRecord {

    /**
     * 车辆编号
     */
    public String bikeNumber = null;

    /**
     * 车辆状态
     */
    public String bikeStatus = null;

    /**
     * 监控时间
     */
    public String monitorTime = null;

    public LockRecord() {

    }

    public LockRecord(String bikeNumber, String bikeStaus, String monitorTime) {
        this.bikeNumber = bikeNumber;
        this.bikeStatus = bikeStaus;
        this.monitorTime = monitorTime;
    }

    @Override
    public String toString() {
        return "bikeNumber: " + bikeNumber + "|bikeStatus: " + bikeStatus + "|monitorTime: " + monitorTime;
    }
}
