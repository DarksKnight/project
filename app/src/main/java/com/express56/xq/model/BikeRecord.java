package com.express56.xq.model;

/**
 * 添加车辆信息对象
 * Created by Administrator on 2016/5/24.
 */
public class BikeRecord {
    public String btSerial = null;

    public String status = null;

    public String bikeSite = null;

    public BikeRecord() {

    }

    public BikeRecord(String btSerial, String status, String bikeSite) {
        this.btSerial = btSerial;
        this.status = status;
        this.bikeSite = bikeSite;
    }

}
