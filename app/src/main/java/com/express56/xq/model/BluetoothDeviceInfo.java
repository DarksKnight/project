package com.express56.xq.model;

/**
 * Created by Administrator on 2016/5/19.
 * 蓝牙设备资料
 */
public class BluetoothDeviceInfo {
    // {"btId":"1","btSerial":"abc0001","createdAt":"2016-05-15T22:00:15+08:00","siteId":"1","status":"3","updatedAt":"2016-05-19T22:00:18+08:00"}
    public String btId = null;
    public String btSerial = null;
    public String createdAt = null;
    public String siteId = null;
    public String status = null;
    public String updatedAt = null;

    @Override
    public String toString() {
        return "|btId:" + btId
                + "|btSerial:" + btSerial
                + "|createdAt:" + createdAt
                + "|siteId:" + siteId
                + "|status:" + status
                + "|updatedAt:" + updatedAt;
    }
}
