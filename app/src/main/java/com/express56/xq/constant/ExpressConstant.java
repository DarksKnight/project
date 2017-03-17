package com.express56.xq.constant;

import android.Manifest;
import android.os.Environment;

import com.express56.xq.http.HttpHelper;

import java.io.File;

/**
 * Created by Administrator on 2016/5/18.
 */
public class ExpressConstant {

    /**
     * 拍照后图片本地存储的有效时间
     */
    public static final long ACTIVE_TIME = 3 * 24 * 60 * 60 * 1000;
    public static final String USER_HEAD_PORTRAIT_PIC_URL_KEY = "user_protrait_url";
    public static final String USER_HEAD_PORTRAIT_PIC_LOCAL_PATH_KEY = "user_protrait_local";

    public static final int SEARCH_START_DATE = 1;
    public static final int SEARCH_END_DATE = SEARCH_START_DATE + 1;

    /**
     * 设置页面扫码推广
     */
    public static final int ACTION_ID_SETTING_SCAN = 1;

    /**
     * 是否只在wifi网络环境下上传图片
     */
    public static boolean isOnlyUploadWifi = true;

    public static final float PHOTO_PREVIEW_RATIO = 0.75f;
    public static final float PHOTO_PREVIEW_RATIO_HEIGHT = 0.85f;
    /**
     * 上传尝试次数
     */
    public static final int UPLOAD_TRY_COUNT = 5;
    public static final int MAIN_GRIDVIEW_COLUMN = 4;

    /**
     * 扫描时间间隔
     */
    public static final long SCAN_BARCODE_FREQUENCY = 1000;

    public static final int ANALYZE_SUCCESS = 0;
    public static final int ANALYZE_FAIL = 1;

    /**
     * 未识别列表每次加载数量
     */
    public static final int PAGE_SIZE = 10;

    /**
     * 验证码时间间隔
     */
    public static final long GET_VERIFY_CODE_INTERVAL = 120000;

    /**
     * 上传状态
     */
    public static final int UPLOAD_STATUS_UPLOADING = 1;
    public static final int UPLOAD_STATUS_NORMAL = 0;

    public static int photoNumber = 0;

    /**
     * 检测上传间隔时间 单位：毫秒
     */
    public static final int UPLOAD_CHECK_INTERVAL = 1000;
    public static final int UPLOAD_CHECK_PHOTO_INTERVAL = 3000;

    /**
     * logo 页面动画持续时间
     */
    public static final int RIDING_BIKE_ANIMATION_INTERVAL = 3000;

    /**
     * logo 页面图片文字透明度动画持续时间
     */
    public static final int ALPHA_ANIMATION_INTERVAL = 2000;


    public static final String PROTOCOL_URL = HttpHelper.HTTP + HttpHelper.IP + "/ASBicycle/Uploads/mianze.html";
//    public static final String PROTOCOL_URL = "http://m.baidu.com/";

    public static final String APK_FILE_NAME = "isr_bms.apk";
    public static final String FOLDER_NAME_1 = "express56_temp";
    public static final String ORIGINAL_PHOTO_FOLDER_NAME = "express56_orginal";
    public static final String PHOTO_FOLDER_NAME = "express_compress";

    public static final String COMPRESS_PHOTO_FILE_PATH = Environment.getExternalStorageDirectory().getPath() + File.separator + PHOTO_FOLDER_NAME;
    public static final String ORIGINAL_PHOTO_LOCATION_PATH = Environment.getExternalStorageDirectory().getPath() + File.separator + ORIGINAL_PHOTO_FOLDER_NAME;
    public static final String IMAGE_FILE_LOCATION_FOLDER_PATH = Environment.getExternalStorageDirectory().getPath() + File.separator + FOLDER_NAME_1;
    public static final String IMAGE_FILE_LOCATION_PATH = Environment.getExternalStorageDirectory().getPath() + File.separator + FOLDER_NAME_1 + "/temp.jpg";

    public static final String BIKE_PIC_PATH =
            Environment.getExternalStorageDirectory().getPath() + File.separator + FOLDER_NAME_1 + "/test_portrait_pic.jpg";
    public static final String PROTRAIT_PIC_PATH =
            Environment.getExternalStorageDirectory().getPath() + File.separator + FOLDER_NAME_1 + "/portrait_pic.jpg";

    public static final String BIKE_PIC_LOCAL_PATH_KEY = "bike_pic_local_file_path";
    /**
     * 用户头像尺寸
     */
    public static final int USER_HEAD_PORTRAIT_PIC_SIZE_WIDTH = 600;
    public static final int USER_HEAD_PORTRAIT_PIC_SIZE_HEIGHT = 600;



    /**
     * 地图显示级别
     * <p/>
     * 百度地图缩放级别 以及对应的数值 单位：米
     * 5     10    20     50    100    200    500    1km    2km   5km   10km   20km   25km  50km   100km  200km  500km  1000km  2000km
     * 21.0  20.0  19.0   18.0  17.0   16.0   15.0   14.0   13.0  12.0  11.0   10.0   9.0   8.0    7.0    6.0    5.0    4.0     3.0
     */
    public static final float LEVEL_BIKE_SITE = 16.0f;



    public static final long DOUBLE_CLICK_TIME_INTERVAL = 300;
    public static final long DOUBLE_CLICK_TIME_INTERVAL_2 = 1000;

    public static final int BIKE_STATUS_OK = 0;


    /**
     * 操作类型
     */
    public static final int BIKE_OPTION_TYPE_BIND = 1;
    public static final int BIKE_OPTION_TYPE_LOCK = BIKE_OPTION_TYPE_BIND + 1;
    public static final int BIKE_OPTION_TYPE_UNLOCK = BIKE_OPTION_TYPE_LOCK + 1;

    /**
     * 车辆锁定状态
     */
    public static final int BIKE_PAGE_NOT_LOCK = 1;
    public static final int BIKE_PAGE_HAD_LOCK = BIKE_PAGE_NOT_LOCK + 1;

    public static final int REQUEST_PERMISSION_ID = 100001;
    public static final int REQUEST_PERMISSION_ID_SCAN = 100003;

    public static final int REQUEST_INSTALL_APK_ID = 100002;


    // activity callback
    public static final int REQUEST_CODE_SCAN = 0x0001;

    public static final int REQUEST_CODE_START_PREVIEW = REQUEST_CODE_SCAN + 1;


    //上传图片类型
    /**
     * 车辆图片
     */
    public static final int UPLOAD_TYPE_BIKE = 1;
    /**
     * 用户实名认证图片
     */
    public static final int UPLOAD_TYPE_USER = 2;
    /**
     * 用户头像图片
     */
    public static final int UPLOAD_TYPE_HEAD = 3;

    /**
     * 没有更新
     */
    public static final int UPGRADE_NO = 0;

    /**
     * 版本非强制更新
     */
    public static final int UPGRADE_HAVE = 2;

    /**
     * 版本强制更新
     */
    public static final int UPGRADE_FORCE = 1;


    /**
     * 停车港类型
     */
    public static final int BIKE_SITE_TYPE_NORMAL = 1;


    /**
     * 定位相关权限
     */
    public static final String[] PERMISSIONS_LOCATION = {Manifest.permission.ACCESS_COARSE_LOCATION,
            Manifest.permission.ACCESS_FINE_LOCATION};

    /**
     * 短信相关权限
     */
    public static final String[] PERMISSIONS_SMS = {Manifest.permission.SEND_SMS,
            Manifest.permission.READ_SMS, Manifest.permission.RECEIVE_SMS};

    /**
     * 相机相关权限
     */
    public static final String[] PERMISSIONS_CAMERA = {Manifest.permission.CAMERA};

    /**
     * 数据读写权限
     */
    public static final String[] PERMISSIONS_STORAGE_AND_CAMERA = {Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.CAMERA};
    /**
     * 数据读写权限
     */
    public static final String[] PERMISSIONS_STORAGE = {Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE};


    //读取本地缓存数据标记
    public static final String SHARED_PREFERENCES_READ = "Read";
    public static final String SHARED_PREFERENCES_WRITE = "Write";


    /**
     * 跳转到锁定、解锁车辆界面标记key
     */
    public static final String LOCK_PAGE_TYPE_KEY = "lockPageType";

    /**
     * 绑定、解锁、锁定等操作标记key
     */
    public static final String LOCK_OPTION_RESULT_TYPE_KEY = "result_type";

    /**
     * 账号key
     */
    public static final String ACCOUNT_PHONE_NUMBER_KEY = "phone_number";

    /**
     * 验证码key
     */
    public static final String ACCOUNT_VERIFICATION_CODE_KEY = "verification_code";

    /**
     * 车锁号key
     */
    public static final String BIKE_BLUETOOTH_ID_KEY = "device_id";

    /**
     * 站点名称key
     */
    public static final String BIKE_SITE_NAME_KEY = "bike_site_name";

    /**
     * 车辆锁定状态key
     */
    public static final String BIKE_LOCK_STATUS_KEY = "lock_status";

    /**
     * 信鸽推送的通知
     */
    public static final int NOTIFICATION_ID_PUSH = 100001;

    public static final String PUSH_TYPE = "push_type";//内容类型
    public static final String PUSH_SERIAL_ID = "serial_ID";//内容id
    public static final String PUSH_TIME = "time";//时间
    public static final String PUSH_POSITION = "position";//地点

    /**
     * 推送消息类型：车辆状态异常
     */
    public static final String PUSH_TYPE_ALARM = "1";

    /**
     * 推送消息类型：报警后车辆途径桩点位置
     */
    public static final String PUSH_TYPE_LOCATION = "2";

    /**
     * 未锁定页面
     */
    public static final int VIEW_TYPE_OPENED = 1;

    /**
     * 已锁定页面
     */
    public static final int VIEW_TYPE_LOCKED = VIEW_TYPE_OPENED + 1;

    /**
     * 车辆异常页面
     */
    public static final int VIEW_TYPE_ABNORMAL = VIEW_TYPE_LOCKED + 1;

    /**
     * 车辆报警页面
     */
    public static final int VIEW_TYPE_ALARM = VIEW_TYPE_ABNORMAL + 1;

    /**
     * 车辆使用中页面
     */
    public static final int VIEW_TYPE_USING = VIEW_TYPE_ALARM + 1;

    /**
     * regular expression
     */
    public static final String PHONE_REGEX = "[1]\\d{10}";

    /**
     * login setting
     */
    public static final String LOGIN_SET = "express_login_setting";// 登录设置

    /**
     * 移动距离  单位：米
     */
    public static final Double MOVE_DISTANCE_REQUEST = 3000.0D;

    /**
     * 启动页面停留时间
     */
    public static final long SPLASH_VIEW_DURATION = 0;

    /**
     * 连续两次返回键按下时间间隔
     */
    public static final long EXIT_APP_DURATION = 2000;

    /**
     * apk下载保存路径
     */
    public static final String DOWNLOAD_APK_DIR = Environment.getExternalStorageDirectory() + "/isr_bms";

    /**
     * 上次推送的消息
     */
    public static final String LATEST_PUSH_MSG = "latest_push_msg";

    /**
     * 普通用户
     */
    public static final int USER_TYPE_NORMAL = 2;

    /**
     * 快递员
     */
    public static final int USER_TYPE_COURIER = 3;


    public static final int EXPRESS_TYPE_ALL = 3;

    /**
     * 上传图片类型 寄件
     */
    public static final int EXPRESS_TYPE_SEND = 1;

    /**
     * 上传图片类型 收件
     */
    public static final int EXPRESS_TYPE_RECEIVE = 2;

    public static final String EXPRESS_ORDER_NOT_RELEASE = "1"; //未发布

    public static final String EXPRESS_ORDER_RELEASE = "2";//已发布

    public static final String EXPRESS_ORDER_PAY_FAIL = "3";//付款失败

    public static final String EXPRESS_ORDER_PAY_ING = "4";//付款确认中

    public static final String EXPRESS_ORDER_PAY_COMPLETE = "5";//已付款

    public static final String EXPRESS_ORDER_COMPLETE = "6"; //待评论

    public static final String EXPRESS_ORDER_COMMENT = "7";//已评论

    public static final String EXPRESS_ORDER_PAY_REIMBURSE = "8";//退款申请中

    public static final String EXPRESS_ORDER_PAY_REIMBURSE_COMPLETE = "9";//退款完成

}
