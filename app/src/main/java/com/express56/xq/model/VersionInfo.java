package com.express56.xq.model;

/**
 * Created by SEELE on 2016/7/15.
 */
public class VersionInfo {
    //            {"success":true,"result":{"versionCode":1,"versionName":"0.0.1","mustUpdate":1,"versionUrl":""},"error":null,"unAuthorizedRequest":false}

    /**
     * 版本号
     */
    public int versionCode = 0;

    /**
     * 版本名称
     */
    public String versionName = "";

    /**
     *  1 - 没有新版本  2 - 可选升级  3 - 强制升级
     */
    public int upgrade = 1;

    /**
     *   新版本apk下载地址
     */
    public String versionUrl = "";

}
