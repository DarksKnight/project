package com.express56.xq.model;

import java.io.Serializable;
import java.util.List;

/**
 * Created by bojoy-sdk2 on 2017/2/20.
 */

public class ReceivingOrderInfo implements Serializable {

    public String pushFlag = "";

    public String areaName = "";

    public String companyName = "";

    public List<MyExpressInfo> orders = null;
}
