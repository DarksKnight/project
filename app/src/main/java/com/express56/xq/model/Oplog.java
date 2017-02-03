package com.express56.xq.model;

/**
 * Created by Administrator on 2016/5/20.
 * 记录信息类
 */
public class Oplog {
    public String btId = null;
    public String createdAt = null;
    public String opTime = null;
    public String siteId = null;
    public String type = null;
    public String updateAt = null;
    public String opId = null;

    public Oplog() {

    }

    public Oplog(String btId, String createdAt, String opTime,
                 String siteId, String type, String updateAt, String opId) {
        this.btId = btId;
        this.createdAt = createdAt;
        this.opTime = opTime;
        this.siteId = siteId;
        this.type = type;
        this.updateAt = updateAt;
        this.opId = opId;
    }

    @Override
    public String toString() {
        return "this.btId" + btId
                + " |this.createdAt" + createdAt
                + " |this.opTime" + opTime
                + " |this.siteId" + siteId
                + " |this.type" + type
                + " |this.updateAt" + updateAt
                + " |this.opId" + opId;
    }
}
