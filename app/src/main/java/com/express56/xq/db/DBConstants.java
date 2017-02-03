package com.express56.xq.db;

import android.database.Cursor;

//import com.javgame.wansha.R.string;

/**
 * 接口：wansha 数据库表相关信息
 *
 * @author wangxy
 */
public class DBConstants {

    public static final class EXPRESS_PHOTO_Table {
        public static final String TABLE_NAME = "express_photo_table";// table name
        public final static String EXPRESS_PHOTO_TABLE_ID = "_id"; //数据库自动生成字段 用来对应 list跳转的 position
        public final static String EXPRESS_PHOTO_TABLE_UID = "uid";
        public final static String EXPRESS_PHOTO_TABLE_PHOTO = "express_img";
        public final static String EXPRESS_PHOTO_TABLE_PHOTO_TYPE= "expressType";//图片类型
        public final static String EXPRESS_PHOTO_TABLE_PHOTO_UPLOAD_FAILURE_COUNT = "upload_fail_count";//图片上传图片失败次数
        public final static String EXPRESS_PHOTO_TABLE_PHOTO_EXPRESS_NO = "expressNo";//图片上的条码
        public final static String EXPRESS_PHOTO_TABLE_ANALYZE_STATUS = "analyze_status";//条码解析是否成功 0 - 解析成功  1- 解析失败
        public final static String EXPRESS_PHOTO_TABLE_RECEIVER = "receiver";//接收者
        public final static String EXPRESS_PHOTO_TABLE_SENDER = "sender";//发送者
        public final static String EXPRESS_PHOTO_TABLE_UPLOAD_STATUS = "uploading";//上传状态  0 - 未上传  1 - 上传中
        public final static String EXPRESS_PHOTO_TABLE_PHOTO_SIZE = "photo_size";//图片大小  单位 KB
        public static final String EXPRESS_PHOTO_TABLE_USER_ID = "user_id"; //用户名
    }

    public static final class EXPRESS_BAR_CODE_Table {
        public static final String TABLE_NAME = "express_barcode_table";// table name
        public final static String EXPRESS_BAR_CODE_TABLE_ID = "_id"; //数据库自动生成字段 用来对应 list跳转的 position
        public final static String EXPRESS_BAR_CODE_TABLE_BARCODE = "barcode";//条形码
    }

//    public static final class Notice_Table {
//        /* ground_table表名及字段 */
//        public static final String TABLE_NAME = "ground_table";// table name
//        public final static String MESSAGE_TABLE_ID = "_id"; //数据库自动生成字段 用来对应 list跳转的 position
//        public final static String MESSAGE_TABLE_MSGID = "msgId";
//        public final static String MESSAGE_TABLE_UID = "uid";
//        public final static String MESSAGE_TABLE_NICK = "nickname";
//        public final static String MESSAGE_TABLE_HEAD = "headUrl";
//        public final static String MESSAGE_TABLE_TEXT = "text";
//        public final static String MESSAGE_TABLE_KIND = "kind";
//        public final static String MESSAGE_TABLE_MTYPE = "mType";
//        public final static String MESSAGE_TABLE_ATTEND = "is_attend";
//        public final static String MESSAGE_TABLE_FLAG = "flag";
//        public final static String MESSAGE_TABLE_REF_ID = "ref_id";
//        public final static String MESSAGE_TABLE_TIME = "time";
//        public final static String IS_MSG_READED = "is_msg_readed";
//    }
//
//    public static final class attend_blog_table {
//        /* 关注栏的blog_table表名及字段 */
//        public static final String TABLE_NAME = "attend_blog_table";// table name
//        public static final String SORT_TABLE_NAME = "sort_blog_table";//某类博客表
//        public final static String BLOG_TABLE_ID = "_id";//数据库自动生成字段 用来对应 list跳转的 position
//        public final static String BLOG_TABLE_TYPE = "type";//区分是自己的博客还是收藏的博客
//        public final static String BLOG_TABLE_BID = "blogId";
//        public final static String BLOG_TABLE_UID = "uid";
//        public final static String BLOG_TABLE_GROUP_ID = "group_id";
//        public final static String BLOG_TABLE_NICK = "nickname";
//        public final static String BLOG_TABLE_HEAD = "headUrl";
//        public final static String BLOG_TABLE_TITLE = "title";
//        public final static String BLOG_TABLE_BODY = "body";
//        public final static String BLOG_TABLE_IMAGE = "firstImageUrl";
//        public final static String BLOG_TABLE_VIDEO = "videoUrl";
//        public final static String BLOG_TABLE_FTYPE = "ftype";
//        public final static String BLOG_TABLE_TIME = "time";
//        public final static String BLOG_TABLE_HOT_NUM = "hot_num";//人气
//        public final static String BLOG_TABLE_COMMENT_NUM = "comment_num";
//        public final static String BLOG_TABLE_FORWARD_NUM = "forward_num";
//        public final static String BLOG_TABLE_FORWARD_UID = "forward_uid";
//        public final static String BLOG_TABLE_FORWARD_REASON = "forward_reason";
//        public final static String BLOG_TABLE_FORWARD_NICKNAME = "forward_nickname";
//        public final static String BLOG_TABLE_FORWARD_FTYPE = "forward_ftype";
//        public final static String BLOG_TABLE_WHOSEUID = "whoseUid";
//        public final static String BLOG_TABLE_DATATYPE = "dataType";
//        public final static String BLOG_TABLE_SEX = "sex";
//        public final static String BLOG_TABLE_PIC_NUM = "pic_num";
//        public final static String BLOG_TABLE_FROM_BY = "from_by";
//        public final static String BLOG_TABLE_ORIGINAL_AUTHOR_NAME = "original_name";
//        public final static String BLOG_TABLE_CITY = "city";
//        public final static String BLOG_TABLE_AID = "aid";//活动id
//    }
//
//    public static final class blog_table {
//        /* 非关注栏的blog_table表名及字段 */
//        public static final String TABLE_NAME = "blog_table";// table name
//        public static final String SORT_TABLE_NAME = "sort_blog_table";//某类博客表
//        public final static String BLOG_TABLE_ID = "_id";//数据库自动生成字段 用来对应 list跳转的 position
//        public final static String BLOG_TABLE_TYPE = "type";//区分是自己的博客还是收藏的博客
//        public final static String BLOG_TABLE_BID = "blogId";
//        public final static String BLOG_TABLE_UID = "uid";
//        public final static String BLOG_TABLE_GROUP_ID = "group_id";
//        public final static String BLOG_TABLE_NICK = "nickname";
//        public final static String BLOG_TABLE_HEAD = "headUrl";
//        public final static String BLOG_TABLE_TITLE = "title";
//        public final static String BLOG_TABLE_BODY = "body";
//        public final static String BLOG_TABLE_IMAGE = "firstImageUrl";
//        public final static String BLOG_TABLE_VIDEO = "videoUrl";
//        public final static String BLOG_TABLE_FTYPE = "ftype";
//        public final static String BLOG_TABLE_TIME = "time";
//        public final static String BLOG_TABLE_HOT_NUM = "hot_num";//人气
//        public final static String BLOG_TABLE_COMMENT_NUM = "comment_num";
//        public final static String BLOG_TABLE_FORWARD_NUM = "forward_num";
//        public final static String BLOG_TABLE_FORWARD_UID = "forward_uid";
//        public final static String BLOG_TABLE_FORWARD_REASON = "forward_reason";
//        public final static String BLOG_TABLE_FORWARD_NICKNAME = "forward_nickname";
//        public final static String BLOG_TABLE_FORWARD_FTYPE = "forward_ftype";
//        public final static String BLOG_TABLE_WHOSEUID = "whoseUid";
//        public final static String BLOG_TABLE_DATATYPE = "dataType";
//        public final static String BLOG_TABLE_SEX = "sex";
//        public final static String BLOG_TABLE_PIC_NUM = "pic_num";
//        public final static String BLOG_TABLE_FROM_BY = "from_by";
//        public final static String BLOG_TABLE_ORIGINAL_AUTHOR_NAME = "original_name";
//        public final static String BLOG_TABLE_CITY = "city";
//        public final static String BLOG_TABLE_AID = "aid";//活动id
//    }
//
//    public static final class account_table {
//        /* account_table表名及字段 */
//        public static final String TABLE_NAME = "account_table";// table name
//        public final static String ACCOUNT_TABLE_ID = "_id";//数据库自动生成字段 用来对应 list跳转的 position
//        public final static String ACCOUNT_TABLE_UID = "uid";
//        public final static String ACCOUNT_TABLE_USERNAME = "username";
//        public final static String ACCOUNT_TABLE_NICKNAME = "nickname";
//        public final static String ACCOUNT_TABLE_PASS = "password";
//        public final static String ACCOUNT_TABLE_SN = "sn";
//        public final static String ACCOUNT_TABLE_TOKEN = "token";
//        public final static String ACCOUNT_TABLE_SECRET = "secret";
//        public final static String ACCOUNT_TABLE_TYPE = "type";
//        public final static String ACCOUNT_TABLE_CURRENT = "isCurrent";//是否是上次 登录用户 默认采用此id
//    }
//
//    public static final class other_user_table {
//        /* other_user_table表名及字段 */
//        public static final String TABLE_NAME = "other_user_table";// table name
//        public final static String OTHERUSER_TABLE_ID = "_id";//数据库自动生成字段 用来对应 list跳转的 position
//        public final static String OTHERUSER_TABLE_TYPE = "type";//区分是关注还是粉丝列表
//        public final static String OTHERUSER_TABLE_UID = "uid";
//        public final static String OTHERUSER_TABLE_NICK = "nickname";
//        public final static String OTHERUSER_TABLE_HEAD = "headUrl";
//        public final static String OTHERUSER_TABLE_TAG = "tag";
//        public final static String OTHERUSER_TABLE_WHOSEUID = "whoseUid";
//        public final static String OTHERUSER_TABLE_DATATYPE = "dataType";
//        public final static String OTHERUSER_TABLE_ATTEND = "isAttend";
//    }
//
//    public static final class comment_table {
//        /* comment_table表名及字段 */
//        public static final String TABLE_NAME = "comment_table";// table name
//        public final static String COMMENT_TABLE_ID = "_id";//数据库自动生成字段 用来对应 list跳转的 position
//        public final static String COMMENT_TABLE_UID = "uid";
//        public final static String COMMENT_TABLE_NICK = "nickname";
//        public final static String COMMENT_TABLE_CONTENT = "content";
//        public final static String COMMENT_TABLE_FEEDID = "feedId";
//        public final static String COMMENT_TABLE_TIME = "time";
//    }
//
//    public static final class album_table {
//        /* album_table表名及字段 */
//        public static final String TABLE_NAME = "album_table";
//        public static final String ALBUM_TABLE_ID = "_id";
//        public static final String ALBUM_ID = "album_id";  //专辑id
//        public static final String ALBUM_TAG = "album_tag";     //专辑标签
//        public static final String ALBUM_COVER = "album_cover";  //专辑封面
//        public static final String ALBUM_TOTAL_BLOG = "album_total_blog";
//    }
//
//
//    public static final class meet_table {
//        /* meet_table表名及字段 */
//        public static final String TABLE_NAME = "meet_table";
//        public static final String MEET_TABLE_ID = "_id";
//        public static final String MEET_ID = "id"; // 服务器端数据 Meet ID
//        public static final String UID = "uid";  //创建者
//        public static final String TITLE = "title";     //标题
//        public static final String BEGIN_TIME = "begin_time";  //开始时间
//        public static final String END_TIME = "end_time";  //结束时间
//        public static final String ACT_AREA = "act_area";  //活动地点
//        public static final String JOIN_NUM = "join_num";  //参加人数
//        public static final String FAV_NUM = "fav_num";  //感兴趣人数
//        public static final String NICKNAME = "nickname";  //感兴趣人数
//        public static final String TYPE = "type";  //感兴趣人数
//        public static final String STATUS = "status";  //感兴趣人数
//        public static final String IMG = "img";  //图片地址
//    }

    /**
     * FunName:				getInt
     * Description :			根据当前域名返回对应行对应列的int值
     *
     * @param： Cursor cursor 是当前info在 list中的位置
     * String field_name 对应表的域名（列名）
     * @return： int：返回对应域名的int值，失败返回-1
     * @Author： wangxy
     * @Create Date:：	2011-10-19
     */
    public static int getIntFromCursor(Cursor cursor, String tableColumnName) {
        return cursor.getInt(cursor.getColumnIndex(tableColumnName));
    }

    /**
     * FunName:				getString
     * Description :			根据当前域名返回对应行对应列的String值
     *
     * @param： Cursor cursor 是当前info在 list中的位置
     * String field_name 对应表的域名（列名）
     * @return： String：返回对应域名的String值，失败返回null
     * @Author： wangxy
     * @Create Date:：	2011-10-19
     */
    public static String getStringFromCursor(Cursor cursor, String tableColumnName) {
        return cursor.getString(cursor.getColumnIndex(tableColumnName));
    }

}
