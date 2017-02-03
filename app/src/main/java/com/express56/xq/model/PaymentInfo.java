package com.express56.xq.model;

import java.io.Serializable;

/**
 * Created by SEELE on 2016/9/20.
 */
public class PaymentInfo implements Serializable {

    /**
     * 车辆编号
     */
    public String ble_name = "";

    /**
     * 起点出发时间
     */
    public String start_time = "";

    /**
     * 到达终点时间
     */
    public String end_time = "";

    /**
     * 起点名称
     */
    public String start_site_name = "";

    /**
     * 终点名称
     */
    public String end_site_name = "";

    /**
     * 用时
     */
    public int rental_time = 0;

    /**
     * 评价
     */
//    public String comment = "";

    /**
     * 订单号
     */
    public String out_trade_no = "";

    /**
     * 商品道具名称
     */
    public String subject = "租车服务";

    /**
     * 订单描述
     */
    public String body = "租车产生的费用";

    /**
     * 订单价格
     */
    public String allpay = "";

    public String remark = "";

    /**
     * 支付方式
     */
    public String pay_method = "";

    /**
     * 校区名称
     */
    public String school_name = "";


}
