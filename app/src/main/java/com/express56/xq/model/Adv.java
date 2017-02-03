package com.express56.xq.model;

/**
 * Created by qxpd52 on 16/12/6.
 */

public class Adv {
//    {
//        "image": "ads/ads1.jpg",
//            "url": "http://www.baidu.com"
//    },
//    {
//        "image": "ads/ads2.jpg",
//            "url": "http://www.qq.com"
//    },
//    {
//        "image": "ads/ads3.jpg",
//            "url": "http://www.tmall.com"
//    }
    public String image = null;

    public String url = null;

    public String getImageLink() {
        return url;
    }

    public String getImageUrl() {
        return image;
    }
}
