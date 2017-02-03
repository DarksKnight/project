package com.express56.xq.model;

import java.io.Serializable;

/**
 * Created by Administrator on 2016/5/20.
 */
public class PersonalInfo implements Serializable {
    //    "name": "admin",
//            "phone": "1111111111",
//            "userPhoto": "user\\2016-12-03\\705e11b1b33c48be9656eefc4821c3ea.jpg",
//            "commonAccount": 0.00,
//            "installmentAccount": 0.00,
//            "creditAccount": 195.00,
//            "creditLimit": 200.00,
//            "cashAccount": 0.00



    public PersonalInfo() {

    }

    @Override
    public String toString() {
        return "PersonalInfo->"
                + "|phone = " + phone
                + "|name = " + name
                + "|commonAccount = " + commonAccount
                + "|installmentAccount = " + installmentAccount
                + "|creditAccount = " + creditAccount
                + "|creditLimit = " + creditLimit
                + "|cashAccount = " + cashAccount
                + "|userPhoto = " + userPhoto
                + "|introduced = " + introduced
                ;
    }

    /**
     * 用户手机号码
     */
    public String phone = "";

    public String name = "";

    public String commonAccount = "";

    public String installmentAccount = "";

    public String creditAccount = "";

    public String creditLimit = "";

    public String cashAccount = "";

    public boolean introduced = true;



    /**
     * 头像地址
     */
    public String userPhoto = null;

}
