package com.express56.xq.model;

import java.io.Serializable;
import java.util.LinkedList;

/**
 * Created by bojoy-sdk2 on 2017/2/22.
 */

public class CompanyInfo implements Serializable {

    public String areaCode = "";

    public String areaName = "";

    public LinkedList<CompanyItemInfo> companys = null;
}
