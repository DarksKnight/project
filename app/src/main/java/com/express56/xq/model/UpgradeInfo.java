package com.express56.xq.model;

import java.io.Serializable;

/**
 * Created by qxpd52 on 16/12/4.
 */

public class UpgradeInfo implements Serializable{
//    "version": "20161115.1.0beta",
//            "isRequire": "1",
//            "remarks": "测试",
//            "downloadPath": "app/android/express.apk"
    public String version = null;

    public int isRequire = 0;

    public String downloadPath = null;

    public String remarks = null;

    @Override
    public String toString() {
        return "AboutInfo->"
                + "|isRequire = " + isRequire
                + "|downloadPath = " + downloadPath
                + "|version = " + version
                + "|remarks = " + remarks
                ;
    }
}
