package com.express56.xq.http;

/**
 * 接口：网络请求消息ID
 */
public class RequestID {

    /**
     * get validate code
     */
    public static final int REQ_GET_VALIDATE_CODE = 10001;

    /**
     * get validate code for register
     */
    public static final int REQ_GET_VALIDATE_CODE_REGISTER = REQ_GET_VALIDATE_CODE + 1;

    /**
     * get userinfo
     */
    public static final int REQ_GET_USERINFO = REQ_GET_VALIDATE_CODE_REGISTER + 1;

    /**
     * get userinfo click navigation bar
     */
    public static final int REQ_GET_USERINFO_CLICK = REQ_GET_USERINFO + 1;

    /**
     * get validate code for forget pwd
     */
    public static final int REQ_GET_VALIDATE_CODE_FORGET_PWD = REQ_GET_USERINFO_CLICK + 1;

    /**
     * get validate code for forget pwd
     */
    public static final int REQ_POST_RESET_PWD = REQ_GET_VALIDATE_CODE_FORGET_PWD + 1;

    /**
     * modify pwd
     */
    public static final int REQ_POST_MODIFY_PWD = REQ_POST_RESET_PWD + 1;

    /**
     * login
     */
    public static final int REQ_LOGIN = REQ_POST_MODIFY_PWD + 1;

    /**
     * auto login
     */
    public static final int REQ_AUT0_LOGIN = REQ_LOGIN + 1;


    /**
     * 退出登录
     */
    public static final int REQ_LOGOUT = REQ_AUT0_LOGIN + 1;

    /**
     * 查询
     */
    public static final int REQ_SEARCH = REQ_LOGOUT + 1;

    /**
     * 版本更新检查
     */
    public static final int REQ_UPDATE_VERSION_CHECK = REQ_SEARCH + 1;

    /**
     * 新版本APK下载
     */
    public static final int REQ_DOWNLOAD_APK = REQ_UPDATE_VERSION_CHECK + 1;

    /**
     * 手动上传图片
     */
    public static final int REQ_UPLOAD_PICTURE = REQ_DOWNLOAD_APK + 1;

    /**
     * 自动上传图片
     */
    public static final int REQ_UPLOAD_PICTURE_AUTO = REQ_UPLOAD_PICTURE + 1;

    /**
     * 上传条形码
     */
    public static final int REQ_UPLOAD_BARCODES = REQ_UPLOAD_PICTURE_AUTO + 1;

    /**
     * 下载图片
     */
    public static final int REQ_DOWNLOAD_PICTURE = REQ_UPLOAD_BARCODES + 1;
    /**
     * 下载图片 个人资料界面
     */
    public static final int REQ_DOWNLOAD_PICTURE_PERSONAL_VIEW = REQ_DOWNLOAD_PICTURE + 1;

    /**
     * 用户注册接口
     */
    public static final int REQ_REGISTER = REQ_DOWNLOAD_PICTURE_PERSONAL_VIEW + 1;

    /**
     * 更新是否只在wifi环境下上传图片
     */
    public static final int REQ_UPDATE_ISWIFIUPLOAD_FLAG = REQ_REGISTER + 1;

    /**
     * 获取设置信息
     */
    public static final int REQ_GET_SETTING_INFO = REQ_UPDATE_ISWIFIUPLOAD_FLAG + 1;

    /**
     * 关于
     */
    public static final int REQ_GET_ABOUT_US = REQ_GET_SETTING_INFO + 1;

    public static final int REQ_GET_SETTING_INFO_CLICK = REQ_GET_ABOUT_US + 1;

    public static final int REQ_GET_ADVERTIZEMENTS = REQ_GET_SETTING_INFO_CLICK + 1;

    public static final int REQ_SETTING_SCAN = REQ_GET_ADVERTIZEMENTS + 1;

    public static final int REQ_GET_AREA_PRICE = REQ_SETTING_SCAN + 1;

    public static final int REQ_GET_AREA_PRICE_SAVE = REQ_GET_AREA_PRICE + 1;

    public static final int REQ_GET_AREA = REQ_GET_AREA_PRICE_SAVE + 1;

    public static final int REQ_GET_AREA_EDIT = REQ_GET_AREA + 1;

    public static final int REQ_GET_ORDER_INFO = REQ_GET_AREA_EDIT + 1;

    public static final int REQ_PAY_SUCCESS = REQ_GET_ORDER_INFO + 1;

    public static final int REQ_GET_ORDER_LIST = REQ_PAY_SUCCESS + 1;

    public static final int REQ_GET_EXPRESS_COMPANY = REQ_GET_ORDER_LIST + 1;

    public static final int REQ_SAVE_ORDER = REQ_GET_EXPRESS_COMPANY + 1;

    public static final int REQ_GET_ORDER = REQ_SAVE_ORDER + 1;

    public static final int REQ_GET_RECEIVING_ORDER = REQ_GET_ORDER + 1;

    public static final int REQ_OPEN_PUSH = REQ_GET_RECEIVING_ORDER + 1;

    public static final int REQ_CLOSE_PUSH = REQ_OPEN_PUSH + 1;

    public static final int REQ_GET_EXPRESS = REQ_CLOSE_PUSH + 1;

    public static final int REQ_SAVE_COMPANY = REQ_GET_EXPRESS + 1;

    public static final int REQ_GET_USER_MONEY = REQ_SAVE_COMPANY + 1;

    public static final int REQ_SAVE_RECEIVING_ORDER = REQ_GET_USER_MONEY + 1;

    public static final int REQ_GET_QUOTATION_LIST = REQ_SAVE_RECEIVING_ORDER + 1;

    public static final int REQ_ORDER_CANCEL = REQ_GET_QUOTATION_LIST + 1;

    public static final int REQ_GET_RECHARGE_LIST = REQ_ORDER_CANCEL + 1;
}
