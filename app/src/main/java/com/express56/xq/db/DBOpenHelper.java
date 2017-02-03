package com.express56.xq.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.express56.xq.util.LogUtil;

public class DBOpenHelper extends SQLiteOpenHelper {
    private static final String TAG = "DBOpenHelper";// 调试标签
    private static final String DBName = "com.express56.xq.db";

    //*************************************************************************
    // 特别注意：当数据库的结构有变化时候需要手动更新DBVersion(加1)
    //*************************************************************************
    private static final int DBVersion = 10;


    public static final String TB_NAME_STRING = "";

    private static final String experssPhotoTableSql = "CREATE TABLE IF NOT EXISTS " +
            DBConstants.EXPRESS_PHOTO_Table.TABLE_NAME + " (" +
            DBConstants.EXPRESS_PHOTO_Table.EXPRESS_PHOTO_TABLE_ID + " integer primary key autoincrement," +
            DBConstants.EXPRESS_PHOTO_Table.EXPRESS_PHOTO_TABLE_UID + " char(100)," +
            DBConstants.EXPRESS_PHOTO_Table.EXPRESS_PHOTO_TABLE_USER_ID + " char(100)," +
            DBConstants.EXPRESS_PHOTO_Table.EXPRESS_PHOTO_TABLE_PHOTO + " char(200)," +
            DBConstants.EXPRESS_PHOTO_Table.EXPRESS_PHOTO_TABLE_PHOTO_TYPE + " integer," +
            DBConstants.EXPRESS_PHOTO_Table.EXPRESS_PHOTO_TABLE_UPLOAD_STATUS + " integer," +
            DBConstants.EXPRESS_PHOTO_Table.EXPRESS_PHOTO_TABLE_PHOTO_SIZE + " char(50)," +
            DBConstants.EXPRESS_PHOTO_Table.EXPRESS_PHOTO_TABLE_PHOTO_EXPRESS_NO + " char(100)," +
            DBConstants.EXPRESS_PHOTO_Table.EXPRESS_PHOTO_TABLE_SENDER + " char(100)," +
            DBConstants.EXPRESS_PHOTO_Table.EXPRESS_PHOTO_TABLE_RECEIVER + " char(100)," +
            DBConstants.EXPRESS_PHOTO_Table.EXPRESS_PHOTO_TABLE_PHOTO_UPLOAD_FAILURE_COUNT + " integer," +
            DBConstants.EXPRESS_PHOTO_Table.EXPRESS_PHOTO_TABLE_ANALYZE_STATUS + " integer)";

    private static final String experssBarcodeTableSql = "CREATE TABLE IF NOT EXISTS " +
            DBConstants.EXPRESS_BAR_CODE_Table.TABLE_NAME + " (" +
            DBConstants.EXPRESS_BAR_CODE_Table.EXPRESS_BAR_CODE_TABLE_ID + " integer primary key autoincrement," +
            DBConstants.EXPRESS_BAR_CODE_Table.EXPRESS_BAR_CODE_TABLE_BARCODE + " char(100))";
//	private static final String goundTableSql = "CREATE TABLE IF NOT EXISTS "+
//																					DBConstants.Notice_Table.TABLE_NAME+" (" +
//																					DBConstants.Notice_Table.MESSAGE_TABLE_ID+" integer primary key autoincrement," +
//																					DBConstants.Notice_Table.MESSAGE_TABLE_MSGID+" char(20) unique,"+
//																					DBConstants.Notice_Table.MESSAGE_TABLE_UID+" char(20),"+
//																					DBConstants.Notice_Table.MESSAGE_TABLE_NICK+" char(20),"+
//																					DBConstants.Notice_Table.MESSAGE_TABLE_HEAD+" char(200)," +
//																					DBConstants.Notice_Table.MESSAGE_TABLE_TEXT+" char(100), " +
//																					DBConstants.Notice_Table.MESSAGE_TABLE_KIND+" integer," +
//																					DBConstants.Notice_Table.MESSAGE_TABLE_MTYPE+" integer," +
//																					DBConstants.Notice_Table.MESSAGE_TABLE_ATTEND+" integer," +
//																					DBConstants.Notice_Table.MESSAGE_TABLE_FLAG+" integer," +
//																					DBConstants.Notice_Table.MESSAGE_TABLE_REF_ID+" char(20)," +
//																					DBConstants.Notice_Table.MESSAGE_TABLE_TIME+" char(20)," +
//																					DBConstants.Notice_Table.IS_MSG_READED+" char(10))";
//	private static final String attend_blogTableSql = "CREATE TABLE IF NOT EXISTS "+
//																					DBConstants.attend_blog_table.TABLE_NAME+" (" +
//																					DBConstants.attend_blog_table.BLOG_TABLE_ID+" integer primary key autoincrement," +
//																					DBConstants.attend_blog_table.BLOG_TABLE_BID+" integer," +
//																					DBConstants.attend_blog_table.BLOG_TABLE_UID+" char(20),"+
//																					DBConstants.attend_blog_table.BLOG_TABLE_GROUP_ID+" char(20),"+
//																					DBConstants.attend_blog_table.BLOG_TABLE_NICK+" char(20),"+
//																					DBConstants.attend_blog_table.BLOG_TABLE_HEAD+" char(200)," +
//																					DBConstants.attend_blog_table.BLOG_TABLE_TITLE+" char(100)," +
//																					DBConstants.attend_blog_table.BLOG_TABLE_BODY+" char(100)," +
//																					DBConstants.attend_blog_table.BLOG_TABLE_IMAGE+" char(100)," +
//																					DBConstants.attend_blog_table.BLOG_TABLE_VIDEO+" char(100)," +
//																					DBConstants.attend_blog_table.BLOG_TABLE_FTYPE+" integer ,"+
//																					DBConstants.attend_blog_table.BLOG_TABLE_TIME+" char(20),"+
//																					DBConstants.attend_blog_table.BLOG_TABLE_HOT_NUM+" char(100),"+
//																					DBConstants.attend_blog_table.BLOG_TABLE_COMMENT_NUM+" integer,"+
//																					DBConstants.attend_blog_table.BLOG_TABLE_FORWARD_NUM+" integer,"+
//																					DBConstants.attend_blog_table.BLOG_TABLE_FORWARD_UID+" char(20),"+
//																					DBConstants.attend_blog_table.BLOG_TABLE_FORWARD_REASON+" char(100),"+
//																					DBConstants.attend_blog_table.BLOG_TABLE_SEX+" integer,"+
//																    	            DBConstants.attend_blog_table.BLOG_TABLE_PIC_NUM+" integer,"+
//																    	            DBConstants.attend_blog_table.BLOG_TABLE_FROM_BY+" char(30),"+
//																    	            DBConstants.attend_blog_table.BLOG_TABLE_ORIGINAL_AUTHOR_NAME+" char(30),"+
//																    	            DBConstants.attend_blog_table.BLOG_TABLE_CITY+" char(30),"+
//																					DBConstants.attend_blog_table.BLOG_TABLE_FORWARD_NICKNAME+" char(30),"+
//																					DBConstants.attend_blog_table.BLOG_TABLE_AID+" char(20),"+
//																					DBConstants.attend_blog_table.BLOG_TABLE_FORWARD_FTYPE+" integer)";
//	private static final String blogTableSql = "CREATE TABLE IF NOT EXISTS "+
//																					DBConstants.blog_table.TABLE_NAME+" (" +
//																					DBConstants.blog_table.BLOG_TABLE_ID+" integer primary key autoincrement," +
//																					DBConstants.blog_table.BLOG_TABLE_BID+" integer," +
//																					DBConstants.blog_table.BLOG_TABLE_UID+" char(20),"+
//																					DBConstants.blog_table.BLOG_TABLE_GROUP_ID+" char(20),"+
//																					DBConstants.blog_table.BLOG_TABLE_NICK+" char(20),"+
//																					DBConstants.blog_table.BLOG_TABLE_HEAD+" char(200)," +
//																					DBConstants.blog_table.BLOG_TABLE_TITLE+" char(100)," +
//																					DBConstants.blog_table.BLOG_TABLE_BODY+" char(100)," +
//																					DBConstants.blog_table.BLOG_TABLE_IMAGE+" char(100)," +
//																					DBConstants.blog_table.BLOG_TABLE_VIDEO+" char(100)," +
//																					DBConstants.blog_table.BLOG_TABLE_FTYPE+" integer ,"+
//																					DBConstants.blog_table.BLOG_TABLE_TIME+" char(20),"+
//																					DBConstants.blog_table.BLOG_TABLE_HOT_NUM+" char(100),"+
//																					DBConstants.blog_table.BLOG_TABLE_COMMENT_NUM+" integer,"+
//																					DBConstants.blog_table.BLOG_TABLE_FORWARD_NUM+" integer,"+
//																					DBConstants.blog_table.BLOG_TABLE_FORWARD_UID+" char(20),"+
//																					DBConstants.blog_table.BLOG_TABLE_FORWARD_REASON+" char(100),"+
//																					DBConstants.blog_table.BLOG_TABLE_SEX+" integer,"+
//																				    DBConstants.blog_table.BLOG_TABLE_PIC_NUM+" integer,"+
//																				    DBConstants.blog_table.BLOG_TABLE_FROM_BY+" char(30),"+
//																				    DBConstants.blog_table.BLOG_TABLE_ORIGINAL_AUTHOR_NAME+" char(30),"+
//																				    DBConstants.blog_table.BLOG_TABLE_CITY+" char(30),"+
//																					DBConstants.blog_table.BLOG_TABLE_FORWARD_NICKNAME+" char(30),"+
//																					DBConstants.blog_table.BLOG_TABLE_AID+" char(20),"+
//																					DBConstants.blog_table.BLOG_TABLE_FORWARD_FTYPE+" integer)";
//
//	private static final String sortBlogTableSql = "CREATE TABLE IF NOT EXISTS "+
//																					DBConstants.attend_blog_table.SORT_TABLE_NAME+" (" +
//																					DBConstants.attend_blog_table.BLOG_TABLE_ID+" integer primary key autoincrement," +
//																					DBConstants.attend_blog_table.BLOG_TABLE_BID+" integer," +
//																					DBConstants.attend_blog_table.BLOG_TABLE_UID+" char(20),"+
//																					DBConstants.attend_blog_table.BLOG_TABLE_GROUP_ID+" char(20),"+
//																					DBConstants.attend_blog_table.BLOG_TABLE_NICK+" char(20),"+
//																					DBConstants.attend_blog_table.BLOG_TABLE_HEAD+" char(200)," +
//																					DBConstants.attend_blog_table.BLOG_TABLE_TITLE+" char(100)," +
//																					DBConstants.attend_blog_table.BLOG_TABLE_BODY+" char(100)," +
//																					DBConstants.attend_blog_table.BLOG_TABLE_IMAGE+" char(100)," +
//																					DBConstants.attend_blog_table.BLOG_TABLE_VIDEO+" char(100)," +
//																					DBConstants.attend_blog_table.BLOG_TABLE_FTYPE+" integer ,"+
//																					DBConstants.attend_blog_table.BLOG_TABLE_TIME+" char(20),"+
//																					DBConstants.attend_blog_table.BLOG_TABLE_COMMENT_NUM+" integer,"+
//																					DBConstants.attend_blog_table.BLOG_TABLE_FORWARD_NUM+" integer,"+
//																					DBConstants.attend_blog_table.BLOG_TABLE_FORWARD_UID+" char(20),"+
//																					DBConstants.attend_blog_table.BLOG_TABLE_FORWARD_REASON+" char(100),"+
//																					DBConstants.attend_blog_table.BLOG_TABLE_FORWARD_NICKNAME+" char(20),"+
//																					DBConstants.attend_blog_table.BLOG_TABLE_FORWARD_FTYPE+" integer," +
//																					DBConstants.attend_blog_table.BLOG_TABLE_SEX+" integer,"+
//																    	            DBConstants.attend_blog_table.BLOG_TABLE_PIC_NUM+" integer,"+
//																    	            DBConstants.attend_blog_table.BLOG_TABLE_FROM_BY+" char(30),"+
//																    	            DBConstants.attend_blog_table.BLOG_TABLE_ORIGINAL_AUTHOR_NAME+" char(30),"+
//																    	            DBConstants.attend_blog_table.BLOG_TABLE_CITY+" char(30),"+
//																    	            DBConstants.attend_blog_table.BLOG_TABLE_HOT_NUM+" char(100),"+
//																					DBConstants.attend_blog_table.BLOG_TABLE_WHOSEUID+" char(200)," +
//																					DBConstants.attend_blog_table.BLOG_TABLE_AID+" char(20),"+
//																					DBConstants.attend_blog_table.BLOG_TABLE_DATATYPE+" integer)";
//
//	private static final String accountTableSql = "CREATE TABLE IF NOT EXISTS "+
//																					DBConstants.account_table.TABLE_NAME+" (" +
//																					DBConstants.account_table.ACCOUNT_TABLE_ID+" integer primary key autoincrement," +
//																					DBConstants.account_table.ACCOUNT_TABLE_UID+" char(20),"+
//																					DBConstants.account_table.ACCOUNT_TABLE_USERNAME+" char(20),"+
//																					DBConstants.account_table.ACCOUNT_TABLE_NICKNAME+" char(20),"+
//																					DBConstants.account_table.ACCOUNT_TABLE_PASS+" char(20),"+
//																					DBConstants.account_table.ACCOUNT_TABLE_SN+" char(200),"+
//																					DBConstants.account_table.ACCOUNT_TABLE_TOKEN+" char(200),"+
//																					DBConstants.account_table.ACCOUNT_TABLE_SECRET+" char(200),"+
//																					DBConstants.account_table.ACCOUNT_TABLE_TYPE+" integer not null default 0,"+
//																					DBConstants.account_table.ACCOUNT_TABLE_CURRENT+" integer not null default 0)";
//	private static final String OtherUserTableSql = "CREATE TABLE IF NOT EXISTS "+
//																					DBConstants.other_user_table.TABLE_NAME+" (" +
//																					DBConstants.other_user_table.OTHERUSER_TABLE_ID+" integer primary key autoincrement," +
//																					DBConstants.other_user_table.OTHERUSER_TABLE_UID+" char(20),"+
//																					DBConstants.other_user_table.OTHERUSER_TABLE_NICK+" char(20),"+
//																					DBConstants.other_user_table.OTHERUSER_TABLE_HEAD+" char(200)," +
//																					DBConstants.other_user_table.OTHERUSER_TABLE_TAG+" char(100), " +
//																					DBConstants.other_user_table.OTHERUSER_TABLE_WHOSEUID+" char(200)," +
//																					DBConstants.other_user_table.OTHERUSER_TABLE_DATATYPE+" integer," +
//																					DBConstants.other_user_table.OTHERUSER_TABLE_ATTEND+" integer not null default 0)";
//	private static final String CommentTableSql = "CREATE TABLE IF NOT EXISTS "+
//																					DBConstants.comment_table.TABLE_NAME+" (" +
//																					DBConstants.comment_table.COMMENT_TABLE_ID+" integer primary key autoincrement," +
//																					DBConstants.comment_table.COMMENT_TABLE_UID+" char(20),"+
//																					DBConstants.comment_table.COMMENT_TABLE_NICK+" char(20),"+
//																					DBConstants.comment_table.COMMENT_TABLE_CONTENT+" char(100)," +
//																					DBConstants.comment_table.COMMENT_TABLE_FEEDID+" integer," +
//																					DBConstants.comment_table.COMMENT_TABLE_TIME+" char(20))";
//
//
//	private static final String AlbumTableSql = "CREATE TABLE IF NOT EXISTS "+
//	                                                                                DBConstants.album_table.TABLE_NAME + "(" +
//	                                                                                DBConstants.album_table.ALBUM_TABLE_ID + " integer primary key autoincrement," +
//	                                                                                DBConstants.album_table.ALBUM_ID + " text unique," +
//	                                                                                DBConstants.album_table.ALBUM_TAG + " text unique," +
//	                                                                                DBConstants.album_table.ALBUM_COVER + " text," +
//	                                                                                DBConstants.album_table.ALBUM_TOTAL_BLOG +" text)";
//
//	   private static final String MeetTableSql = "CREATE TABLE IF NOT EXISTS "+
//                                                                                   DBConstants.meet_table.TABLE_NAME + "(" +
//                                                                                   DBConstants.meet_table.MEET_TABLE_ID + " integer primary key autoincrement," +
//                                                                                   DBConstants.meet_table.MEET_ID + " text ," +
//                                                                                   DBConstants.meet_table.UID + " text ," +
//                                                                                   DBConstants.meet_table.TITLE + " text ," +
//                                                                                   DBConstants.meet_table.BEGIN_TIME + " text ," +
//                                                                                   DBConstants.meet_table.END_TIME + " text ," +
//                                                                                   DBConstants.meet_table.ACT_AREA + " text ," +
//                                                                                   DBConstants.meet_table.JOIN_NUM + " text ," +
//                                                                                   DBConstants.meet_table.FAV_NUM + " text ," +
//                                                                                   DBConstants.meet_table.NICKNAME + " text ," +
//                                                                                   DBConstants.meet_table.TYPE + " text ," +
//                                                                                   DBConstants.meet_table.STATUS + " text ," +
//                                                                                   DBConstants.meet_table.IMG + " text)" ;


    public DBOpenHelper(Context context) {
        super(context, DBName, null, DBVersion);
    }


    @Override
    public void onCreate(SQLiteDatabase db) {
        LogUtil.i(TAG, "begin onCreate");
        db.beginTransaction();
        try {
            db.execSQL(experssPhotoTableSql);
        } catch (Exception e) {
            LogUtil.e(TAG, "Failed to create the photo table");
        }
        try {
            db.execSQL(experssBarcodeTableSql);
        } catch (Exception e) {
            LogUtil.e(TAG, "Failed to create the barcode table");
        }
//		try {
//			db.execSQL(goundTableSql);
//		} catch (Exception e) {
//			LogUtil.e(TAG, "Failed to create the ground table");
//		}
//		try {
//			db.execSQL(attend_blogTableSql);
//		} catch (Exception e) {
//			LogUtil.e(TAG, "Failed to create the blog table");
//		}
//
//		try {
//			db.execSQL(blogTableSql);
//		} catch (Exception e) {
//			LogUtil.e(TAG, "Failed to create the blog table");
//		}
//
//		try {
//
////		    db.execSQL("CREATE TABLE IF NOT EXISTS sort_blog_table (_id " +
////		    		"integer primary key autoincrement,blogId integer,uid char(200),nickname char(24),headUrl char(200)," +
////		    		"title char(100),body char(100),firstImageUrl char(100),videoUrl char(100)," +
////		    		"ftype integer,time char(20),comment_num integer,forward_num integer," +
////		    		"forward_uid char(20),forward_reason char(100),forward_nickname char(24)," +
////		    		"forward_ftype integer,whoseUid char(200),dataType integer)");
//			db.execSQL(sortBlogTableSql);
//		} catch (Exception e) {
//			LogUtil.e(TAG, "Failed to create the blog table");
//		}
//		try {
//			db.execSQL(accountTableSql);
//		} catch (Exception e) {
//			LogUtil.e(TAG, "Failed to create the userinfo table");
//		}
//		try {
////		    db.execSQL("CREATE TABLE IF NOT EXISTS other_user_table (_id " +
////		    		"integer primary key autoincrement,uid char(20)" +
////		    		",nickname char(24),headUrl char(200),tag char(100),whoseUid char(200)," +
////		    		"dataType integer,isAttend integer not null default 0)");
//			db.execSQL(OtherUserTableSql);
//		} catch (Exception e) {
//			LogUtil.e(TAG, "Failed to create the otheruser table");
//		}
//		try {
//			db.execSQL(CommentTableSql);
//
//		} catch (Exception e) {
//			LogUtil.e(TAG, "Failed to create the comment table");
//		}
//		try {
//			db.execSQL(AlbumTableSql);
//		} catch (Exception e) {
//			LogUtil.e(TAG, "Failed to create the album table");
//		}
//
//		try {
//			db.execSQL(MeetTableSql);
//		} catch (Exception e) {
//			LogUtil.e(TAG, "Failed to create the Meet table");
//		}

        db.setTransactionSuccessful();
        db.endTransaction();
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        LogUtil.e("DBOpenHelper", "onUpgradeonUpgradeonUpgradeonUpgrade");
        LogUtil.d(TAG, "begin to drop all table");
        db.beginTransaction();
        try {
            db.execSQL("DROP TABLE IF EXISTS " + DBConstants.EXPRESS_PHOTO_Table.TABLE_NAME);
        } catch (Exception e) {
            LogUtil.e(TAG, "Failed to create the photo_table");
        }
        try {
            db.execSQL("DROP TABLE IF EXISTS " + DBConstants.EXPRESS_BAR_CODE_Table.TABLE_NAME);
        } catch (Exception e) {
            LogUtil.e(TAG, "Failed to create the barcode_table");
        }
//		try {
//			db.execSQL("DROP TABLE IF EXISTS " + DBConstants.Notice_Table.TABLE_NAME);
//		} catch (Exception e) {
//			LogUtil.e(TAG, "Failed to create the ground_table");
//		}
//		try {
//			db.execSQL("DROP TABLE IF EXISTS " + DBConstants.other_user_table.TABLE_NAME);
//		} catch (Exception e) {
//			LogUtil.e(TAG, "Failed to create the otheruser table");
//		}
//		try {
//			db.execSQL("DROP TABLE IF EXISTS " + DBConstants.attend_blog_table.TABLE_NAME);
//		} catch (Exception e) {
//			LogUtil.e(TAG, "Failed to create the attend blog_table");
//		}
//		try {
//			db.execSQL("DROP TABLE IF EXISTS " + DBConstants.blog_table.SORT_TABLE_NAME);
//		} catch (Exception e) {
//			LogUtil.e(TAG, "Failed to create the blog_table");
//		}
//		try {
//			db.execSQL("DROP TABLE IF EXISTS " + DBConstants.blog_table.TABLE_NAME);
//		} catch (Exception e) {
//			LogUtil.e(TAG, "Failed to create the blog_table");
//		}
//		// 用户表不做升级，保存用户数据
//		try {
////			db.execSQL("DROP TABLE IF EXISTS " + DBConstants.account_table.TABLE_NAME);
//		} catch (Exception e) {
//			LogUtil.e(TAG, "Failed to create the account_table");
//		}
//		try {
//			db.execSQL("DROP TABLE IF EXISTS " + DBConstants.comment_table.TABLE_NAME);
//		} catch (Exception e) {
//			LogUtil.e(TAG, "Failed to create the comment_table");
//		}
//		try {
//			db.execSQL("DROP TABLE IF EXISTS " + DBConstants.album_table.TABLE_NAME);
//		} catch (Exception e) {
//			LogUtil.e(TAG, "Failed to create the album_table");
//		}
//		try {
//			db.execSQL("DROP TABLE IF EXISTS " + DBConstants.meet_table.TABLE_NAME);
//		} catch (Exception e) {
//			LogUtil.e(TAG, "Failed to drop the meet_table");
//		}
        db.setTransactionSuccessful();
        db.endTransaction();
        onCreate(db);

    }

}
