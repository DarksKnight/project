package com.express56.xq.model;

import java.io.Serializable;

/**
 * 校区信息类
 * Created by qzy on 2016/6/21.
 */
public class SchoolInfo implements Serializable{
    //                    "userId": 2,
//                        "name": "苏州大学",
//                        "areacode": "test",
//                        "gps_point": "120.736933, 31.278583",
//                        "site_count": 5,
//                        "bike_count": 100,
//                        "time_change": 1,
//                        "refresh_date": "2016-06-20T17:21:22+08:00"
    public int id = 0;

    public String name = "";

    public String areacode = "";

    public String gps_point = "";

    public int site_count = 0;

    public int bike_count = 0;

    public int time_charge = 0;

    public String refresh_date = "";

}
