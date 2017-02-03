package com.express56.xq.model;

import java.io.Serializable;

/**
 * Created by qxpd52 on 16/12/4.
 */

public class AboutInfo implements Serializable{
/*    "name": "苏州某某科技公司",
            "mobile": "15114521562,0512-68886666",
            "rights": "Copyright @2014-2016 All Rights Reserved",
            "weixin": "test0001",
            "email": "15114521562,0512-68886666"*/

    public String name = null;

    public String mobile = null;

    public String rights = null;

    public String weixin = null;

    public String email = null;

    @Override
    public String toString() {
        return "AboutInfo->"
                + "|mobile = " + mobile
                + "|rights = " + rights
                + "|name = " + name
                + "|weixin = " + weixin
                + "|email = " + email
                ;
    }
}
