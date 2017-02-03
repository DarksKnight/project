package com.express56.xq.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;

import com.express56.xq.constant.ExpressConstant;
import com.express56.xq.model.ExpressPhoto;
import com.express56.xq.util.LogUtil;
import com.express56.xq.util.StringUtils;

import java.util.ArrayList;

/**
 * 图片上传
 */
public class PhotoDataHelper {
    private static final String TAG = PhotoDataHelper.class.getSimpleName();// 调试标签

    private DBOpenHelper dbOpenHelper = null;
    private SQLiteDatabase mSQLiteDatabase = null;
    private Context context = null;

////    public String getPhoto_express_no() {
////        return photo_express_no;
////    }
//
//    private String photo_express_no;
//
////    public String getPhoto_id() {
////        return photo_id;
////    }
//
//    /**
//     * 最近一次取出photo的id
//     */
//    private String photo_id = null;
//
////    public int getPhoto_type() {
////        return photo_type;
////    }
//
//    private int photo_type;


    public PhotoDataHelper(Context context) {
        this.context = context;
    }

    /**
     * FunName:				open
     * Description :			open DB，should be used before operating DB
     *
     * @param： none
     * @return： none
     * @Author： wangxy
     * @Create Date:：	2011-10-19
     */
    public void open() {
        if (null == dbOpenHelper) {
            dbOpenHelper = new DBOpenHelper(context);
        }

        try {
            mSQLiteDatabase = dbOpenHelper.getWritableDatabase();
        } catch (SQLiteException ex) {
            mSQLiteDatabase = dbOpenHelper.getReadableDatabase();
        }
    }

    /**
     * FunName:				close
     * Description :			open DB，should be used after operating DB
     *
     * @param： none
     * @return： none
     * @Author： wangxy
     * @Create Date:：	2011-10-19
     */
    public void close() {
        LogUtil.d(TAG, "closeDatabase");
        mSQLiteDatabase.close();
        if (null != dbOpenHelper) {
            dbOpenHelper.close();
            dbOpenHelper = null;
        }
    }

    public boolean deleteAllData() {
        LogUtil.d(TAG, "deleteAllData");
        try {
            mSQLiteDatabase.execSQL(
                    "delete from " + DBConstants.EXPRESS_PHOTO_Table.TABLE_NAME);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

//    public boolean addBlogList(final ArrayList<Blog> list) {
//        LogUtil.d(TAG, "begin to add blog list");
//        mSQLiteDatabase.beginTransaction();
//        int count = list.size();
//        int i = 0;
//        while (0 != count) {
//            Blog blog = (Blog) list.get(i);
//            this.addBlogInfo(blog);
//            i++;
//            --count;
//        }
//
//        mSQLiteDatabase.setTransactionSuccessful();
//        mSQLiteDatabase.endTransaction();
//
//        return true;
//    }

    public boolean insertPhoto(String filePath, String username, int expressType, String express_No, String sender, String receiver, String size, int analyze_Status) {
        mSQLiteDatabase.beginTransaction();
        ContentValues values = new ContentValues();
//        final ByteArrayOutputStream os = new ByteArrayOutputStream();
//        bitmap.compress(Bitmap.CompressFormat.PNG, 100, os);
        values.put(DBConstants.EXPRESS_PHOTO_Table.EXPRESS_PHOTO_TABLE_UID, String.valueOf(System.currentTimeMillis()));
//        values.put(DBConstants.EXPRESS_PHOTO_Table.EXPRESS_PHOTO_TABLE_PHOTO, os.toByteArray());
        values.put(DBConstants.EXPRESS_PHOTO_Table.EXPRESS_PHOTO_TABLE_PHOTO, filePath);
        values.put(DBConstants.EXPRESS_PHOTO_Table.EXPRESS_PHOTO_TABLE_PHOTO_TYPE, expressType);//  1 寄件图片   2 签收图片
        values.put(DBConstants.EXPRESS_PHOTO_Table.EXPRESS_PHOTO_TABLE_PHOTO_EXPRESS_NO, express_No);//  条码
        values.put(DBConstants.EXPRESS_PHOTO_Table.EXPRESS_PHOTO_TABLE_USER_ID, username);//  用户名
        values.put(DBConstants.EXPRESS_PHOTO_Table.EXPRESS_PHOTO_TABLE_SENDER, sender);//  寄件人电话
        values.put(DBConstants.EXPRESS_PHOTO_Table.EXPRESS_PHOTO_TABLE_UPLOAD_STATUS, ExpressConstant.UPLOAD_STATUS_NORMAL);//  上传状态 0 未上传
        values.put(DBConstants.EXPRESS_PHOTO_Table.EXPRESS_PHOTO_TABLE_PHOTO_SIZE, size);//  图片大小 的字符串 包含 单位 KB
        values.put(DBConstants.EXPRESS_PHOTO_Table.EXPRESS_PHOTO_TABLE_RECEIVER, receiver);//  收件人电话
        values.put(DBConstants.EXPRESS_PHOTO_Table.EXPRESS_PHOTO_TABLE_ANALYZE_STATUS, analyze_Status);//  解析状态
        mSQLiteDatabase.insert(DBConstants.EXPRESS_PHOTO_Table.TABLE_NAME, "", values);
        mSQLiteDatabase.setTransactionSuccessful();
        mSQLiteDatabase.endTransaction();
//        try {
//            os.close();
//            return true;
//        } catch (IOException e) {
//            e.printStackTrace();
//            return false;
//        }
        return true;
    }

    public int getUploadFailureCount(String photo_id) {
        if (StringUtils.isEmpty(photo_id)) {
            return 0;
        }
        LogUtil.d(TAG, "updateUploadFailTime id=" + " =" + photo_id);
        String SELECT_SQL = "SELECT * FROM " + DBConstants.EXPRESS_PHOTO_Table.TABLE_NAME
                + " WHERE " + DBConstants.EXPRESS_PHOTO_Table.EXPRESS_PHOTO_TABLE_UID + " = " + photo_id;
        Cursor mCursor = mSQLiteDatabase.rawQuery(SELECT_SQL, null);
        int lastCount = 0;
        if (mCursor != null) {
            if (mCursor.moveToFirst()) {//just need to query one time
                lastCount = mCursor.getInt(mCursor.getColumnIndex(DBConstants.EXPRESS_PHOTO_Table.EXPRESS_PHOTO_TABLE_PHOTO_UPLOAD_FAILURE_COUNT));
            }
        }
        if (mCursor != null) {
            mCursor.close();
        }
        return lastCount;
    }

    /**
     * 更新某张图片上传失败次数 执行一次+1
     */
    public boolean updateUploadFailTime(String photo_id) {
        if (StringUtils.isEmpty(photo_id)) {
            return false;
        }
//        LogUtil.d(TAG,"updateUploadFailTime id="+" =" + photo_id);
//        String SELECT_SQL = "SELECT * FROM " + DBConstants.EXPRESS_PHOTO_Table.TABLE_NAME
//                + "WHERE " + DBConstants.EXPRESS_PHOTO_Table.EXPRESS_PHOTO_TABLE_UID + " = " + photo_id;
//        Cursor mCursor = mSQLiteDatabase.rawQuery(SELECT_SQL, null);
//        int lastCount = 0;
//        if (mCursor != null) {
//            if (mCursor.moveToFirst()) {//just need to query one time
//                lastCount = mCursor.getInt(mCursor.getColumnIndex(DBConstants.EXPRESS_PHOTO_Table.EXPRESS_PHOTO_TABLE_PHOTO_UPLOAD_FAILURE_COUNT));
//            }
//        }
//        if (mCursor != null) {
//            mCursor.close();
//        }
        int lastCount = getUploadFailureCount(photo_id);
        ContentValues values = new ContentValues();
        values.put(DBConstants.EXPRESS_PHOTO_Table.EXPRESS_PHOTO_TABLE_PHOTO_UPLOAD_FAILURE_COUNT, lastCount + 1);
        try {
            mSQLiteDatabase.update(DBConstants.EXPRESS_PHOTO_Table.TABLE_NAME, values, DBConstants.EXPRESS_PHOTO_Table.EXPRESS_PHOTO_TABLE_UID + "=" + photo_id, null);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * 获取对象
     *
     * @return
     */
    public ExpressPhoto getExpressPhoto(String username) {
        ExpressPhoto expressPhoto = new ExpressPhoto();
        String SELECT_SQL = "SELECT * FROM " + DBConstants.EXPRESS_PHOTO_Table.TABLE_NAME
                + " WHERE " + DBConstants.EXPRESS_PHOTO_Table.EXPRESS_PHOTO_TABLE_ANALYZE_STATUS + " = " + ExpressConstant.ANALYZE_SUCCESS
                + " AND " + DBConstants.EXPRESS_PHOTO_Table.EXPRESS_PHOTO_TABLE_USER_ID + " = " + username;
        Cursor mCursor = mSQLiteDatabase.rawQuery(SELECT_SQL, null);
        if (mCursor != null) {
            if (mCursor.moveToFirst()) {//just need to query one time
//                expressPhoto.photoFilePath = mCursor.getBlob(mCursor.getColumnIndex(DBConstants.EXPRESS_PHOTO_Table.EXPRESS_PHOTO_TABLE_PHOTO));//取出图片
                expressPhoto.photoFilePath = mCursor.getString(mCursor.getColumnIndex(DBConstants.EXPRESS_PHOTO_Table.EXPRESS_PHOTO_TABLE_PHOTO));//取出图片
                expressPhoto.photo_id = mCursor.getString(mCursor.getColumnIndex(DBConstants.EXPRESS_PHOTO_Table.EXPRESS_PHOTO_TABLE_UID));
                expressPhoto.photo_type = mCursor.getInt(mCursor.getColumnIndex(DBConstants.EXPRESS_PHOTO_Table.EXPRESS_PHOTO_TABLE_PHOTO_TYPE));
                expressPhoto.photo_express_no = mCursor.getString(mCursor.getColumnIndex(DBConstants.EXPRESS_PHOTO_Table.EXPRESS_PHOTO_TABLE_PHOTO_EXPRESS_NO));
                expressPhoto.photo_analyze_status = mCursor.getInt(mCursor.getColumnIndex(DBConstants.EXPRESS_PHOTO_Table.EXPRESS_PHOTO_TABLE_ANALYZE_STATUS));
                expressPhoto.sender = mCursor.getString(mCursor.getColumnIndex(DBConstants.EXPRESS_PHOTO_Table.EXPRESS_PHOTO_TABLE_SENDER));
                expressPhoto.size = mCursor.getString(mCursor.getColumnIndex(DBConstants.EXPRESS_PHOTO_Table.EXPRESS_PHOTO_TABLE_PHOTO_SIZE));
                expressPhoto.upload_status = mCursor.getInt(mCursor.getColumnIndex(DBConstants.EXPRESS_PHOTO_Table.EXPRESS_PHOTO_TABLE_UPLOAD_STATUS));
                expressPhoto.receiver = mCursor.getString(mCursor.getColumnIndex(DBConstants.EXPRESS_PHOTO_Table.EXPRESS_PHOTO_TABLE_RECEIVER));
            }
        }
//        if (expressPhoto != null && !StringUtils.isEmpty(expressPhoto.photo_id)
//                && System.currentTimeMillis() - Long.parseLong(expressPhoto.photo_id) > 3 * 60 * 1000) {
        if (expressPhoto != null && !StringUtils.isEmpty(expressPhoto.photo_id)
                && System.currentTimeMillis() - Long.parseLong(expressPhoto.photo_id) > ExpressConstant.ACTIVE_TIME) {
            deletePhotoByID(expressPhoto.photo_id, username);
            expressPhoto = null;
        }
        if (mCursor != null) {
            mCursor.close();
        }
        return expressPhoto;
    }

    /**
     * 根据uid 查询记录 to modify express_no
     *
     * @return
     */
    public ExpressPhoto getExpressPhotoByUID(String uid, String username) {
        ExpressPhoto expressPhoto = new ExpressPhoto();
        String SELECT_SQL = "SELECT * FROM " + DBConstants.EXPRESS_PHOTO_Table.TABLE_NAME
                + " WHERE " + DBConstants.EXPRESS_PHOTO_Table.EXPRESS_PHOTO_TABLE_UID + " = " + uid
                + " AND " + DBConstants.EXPRESS_PHOTO_Table.EXPRESS_PHOTO_TABLE_USER_ID + " = " + username;
        ;
        Cursor mCursor = mSQLiteDatabase.rawQuery(SELECT_SQL, null);
        if (mCursor != null) {
            if (mCursor.moveToFirst()) {//just need to query one time
                expressPhoto.photoFilePath = mCursor.getString(mCursor.getColumnIndex(DBConstants.EXPRESS_PHOTO_Table.EXPRESS_PHOTO_TABLE_PHOTO));//取出图片
                expressPhoto.photo_id = mCursor.getString(mCursor.getColumnIndex(DBConstants.EXPRESS_PHOTO_Table.EXPRESS_PHOTO_TABLE_UID));
                expressPhoto.photo_type = mCursor.getInt(mCursor.getColumnIndex(DBConstants.EXPRESS_PHOTO_Table.EXPRESS_PHOTO_TABLE_PHOTO_TYPE));
                expressPhoto.photo_express_no = mCursor.getString(mCursor.getColumnIndex(DBConstants.EXPRESS_PHOTO_Table.EXPRESS_PHOTO_TABLE_PHOTO_EXPRESS_NO));
                expressPhoto.photo_analyze_status = mCursor.getInt(mCursor.getColumnIndex(DBConstants.EXPRESS_PHOTO_Table.EXPRESS_PHOTO_TABLE_ANALYZE_STATUS));
                expressPhoto.sender = mCursor.getString(mCursor.getColumnIndex(DBConstants.EXPRESS_PHOTO_Table.EXPRESS_PHOTO_TABLE_SENDER));
                expressPhoto.receiver = mCursor.getString(mCursor.getColumnIndex(DBConstants.EXPRESS_PHOTO_Table.EXPRESS_PHOTO_TABLE_RECEIVER));
                expressPhoto.upload_status = mCursor.getInt(mCursor.getColumnIndex(DBConstants.EXPRESS_PHOTO_Table.EXPRESS_PHOTO_TABLE_UPLOAD_STATUS));
                expressPhoto.size = mCursor.getString(mCursor.getColumnIndex(DBConstants.EXPRESS_PHOTO_Table.EXPRESS_PHOTO_TABLE_PHOTO_SIZE));
            }
        }
        if (mCursor != null) {
            mCursor.close();
        }
        return expressPhoto;

    }

    /**
     * 维护单号
     *
     * @param uid
     */
    public boolean updateExpressPhotoExpressNoByUID(String uid, String express_no) {
//        String SELECT_SQL = "UPDATE " + DBConstants.EXPRESS_PHOTO_Table.TABLE_NAME
//                + " SET " + DBConstants.EXPRESS_PHOTO_Table.EXPRESS_PHOTO_TABLE_PHOTO_EXPRESS_NO + " = " + express_no
//                + " WHERE " + DBConstants.EXPRESS_PHOTO_Table.EXPRESS_PHOTO_TABLE_UID +" = " + uid;
//        Cursor mCursor = mSQLiteDatabase.update(SELECT_SQL, null);

        ContentValues values = new ContentValues();
        values.put(DBConstants.EXPRESS_PHOTO_Table.EXPRESS_PHOTO_TABLE_PHOTO_EXPRESS_NO, express_no);
        values.put(DBConstants.EXPRESS_PHOTO_Table.EXPRESS_PHOTO_TABLE_ANALYZE_STATUS, ExpressConstant.ANALYZE_SUCCESS);
        try {
            mSQLiteDatabase.update(DBConstants.EXPRESS_PHOTO_Table.TABLE_NAME, values, DBConstants.EXPRESS_PHOTO_Table.EXPRESS_PHOTO_TABLE_UID + "=" + uid, null);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * 取第一页数据
     *
     * @return
     */
    public ArrayList<ExpressPhoto> getAnlayzeFailExpressPhoto(String username) {
        return getAnlayzeFailExpressPhoto(1, username);
    }

    /**
     * 查询所有需要维护的数据
     *
     * @return
     */
    public ArrayList<ExpressPhoto> getAnlayzeFailExpressPhoto(int pageIndex, String username) {
        //select * from users order by id limit 10 offset 0;//offset代表从第几条记录“之后“开始查询，limit表明查询多少条结果
        ArrayList<ExpressPhoto> expressPhotos = new ArrayList<ExpressPhoto>();
        String[] selectionArgs = new String[]{ExpressConstant.ANALYZE_FAIL + ""};
        //boolean distinct, String table, String[] columns, String selection, String[] selectionArgs, String groupBy, String having, String orderBy, String limit
        Cursor mCursor = mSQLiteDatabase.query(false,
                DBConstants.EXPRESS_PHOTO_Table.TABLE_NAME, null,
                DBConstants.EXPRESS_PHOTO_Table.EXPRESS_PHOTO_TABLE_ANALYZE_STATUS + " = " + ExpressConstant.ANALYZE_FAIL
                + " AND " + DBConstants.EXPRESS_PHOTO_Table.EXPRESS_PHOTO_TABLE_UPLOAD_STATUS + " = " + ExpressConstant.UPLOAD_STATUS_NORMAL
//                        + " LIMIT " + ExpressConstant.PAGE_SIZE + " OFFSET " + (pageIndex - 1)* ExpressConstant.PAGE_SIZE
//                        + " ORDER BY " + DBConstants.EXPRESS_PHOTO_Table.EXPRESS_PHOTO_TABLE_UID + " ASC"
                , null, null, null,
                DBConstants.EXPRESS_PHOTO_Table.EXPRESS_PHOTO_TABLE_UID + " ASC", (pageIndex - 1) * ExpressConstant.PAGE_SIZE + "," + ExpressConstant.PAGE_SIZE);
        if (mCursor != null) {
            while (mCursor.moveToNext()) {
                ExpressPhoto expressPhoto = new ExpressPhoto();
                expressPhoto.photoFilePath = mCursor.getString(mCursor.getColumnIndex(DBConstants.EXPRESS_PHOTO_Table.EXPRESS_PHOTO_TABLE_PHOTO));//取出图片
                expressPhoto.photo_id = mCursor.getString(mCursor.getColumnIndex(DBConstants.EXPRESS_PHOTO_Table.EXPRESS_PHOTO_TABLE_UID));
                expressPhoto.photo_type = mCursor.getInt(mCursor.getColumnIndex(DBConstants.EXPRESS_PHOTO_Table.EXPRESS_PHOTO_TABLE_PHOTO_TYPE));
                expressPhoto.photo_express_no = mCursor.getString(mCursor.getColumnIndex(DBConstants.EXPRESS_PHOTO_Table.EXPRESS_PHOTO_TABLE_PHOTO_EXPRESS_NO));
                expressPhoto.photo_analyze_status = mCursor.getInt(mCursor.getColumnIndex(DBConstants.EXPRESS_PHOTO_Table.EXPRESS_PHOTO_TABLE_ANALYZE_STATUS));
                expressPhoto.sender = mCursor.getString(mCursor.getColumnIndex(DBConstants.EXPRESS_PHOTO_Table.EXPRESS_PHOTO_TABLE_SENDER));
                expressPhoto.receiver = mCursor.getString(mCursor.getColumnIndex(DBConstants.EXPRESS_PHOTO_Table.EXPRESS_PHOTO_TABLE_RECEIVER));
                expressPhoto.upload_status = mCursor.getInt(mCursor.getColumnIndex(DBConstants.EXPRESS_PHOTO_Table.EXPRESS_PHOTO_TABLE_UPLOAD_STATUS));
                expressPhoto.size = mCursor.getString(mCursor.getColumnIndex(DBConstants.EXPRESS_PHOTO_Table.EXPRESS_PHOTO_TABLE_PHOTO_SIZE));
                if (expressPhoto != null && !StringUtils.isEmpty(expressPhoto.photo_id)
                        && System.currentTimeMillis() - Long.parseLong(expressPhoto.photo_id) > ExpressConstant.ACTIVE_TIME) {
                    deletePhotoByID(expressPhoto.photo_id, username);
                } else {
                    expressPhotos.add(expressPhoto);
                }
            }
            if (mCursor != null) {
                mCursor.close();
            }
        }

        return expressPhotos;

    }

    /**
     * 查询所有数据
     *
     * @return
     */
    public ArrayList<ExpressPhoto> getAllExpressPhoto(int pageIndex, String username) {
        //select * from users order by id limit 10 offset 0;//offset代表从第几条记录“之后“开始查询，limit表明查询多少条结果
        ArrayList<ExpressPhoto> expressPhotos = new ArrayList<ExpressPhoto>();
        String[] selectionArgs = new String[]{ExpressConstant.ANALYZE_FAIL + ""};
        //boolean distinct, String table, String[] columns, String selection, String[] selectionArgs, String groupBy, String having, String orderBy, String limit
        Cursor mCursor = mSQLiteDatabase.query(false,
                DBConstants.EXPRESS_PHOTO_Table.TABLE_NAME, null,
                DBConstants.EXPRESS_PHOTO_Table.EXPRESS_PHOTO_TABLE_USER_ID + " = ?"
                        + " AND " + DBConstants.EXPRESS_PHOTO_Table.EXPRESS_PHOTO_TABLE_UPLOAD_STATUS + " = ?"
                , new String[]{username, String.valueOf(ExpressConstant.UPLOAD_STATUS_NORMAL)}, null, null,
                DBConstants.EXPRESS_PHOTO_Table.EXPRESS_PHOTO_TABLE_UID + " ASC", (pageIndex - 1) * ExpressConstant.PAGE_SIZE + "," + ExpressConstant.PAGE_SIZE);
        if (mCursor != null) {
            while (mCursor.moveToNext()) {
                ExpressPhoto expressPhoto = new ExpressPhoto();
                expressPhoto.photoFilePath = mCursor.getString(mCursor.getColumnIndex(DBConstants.EXPRESS_PHOTO_Table.EXPRESS_PHOTO_TABLE_PHOTO));//取出图片
                expressPhoto.photo_id = mCursor.getString(mCursor.getColumnIndex(DBConstants.EXPRESS_PHOTO_Table.EXPRESS_PHOTO_TABLE_UID));
                expressPhoto.photo_type = mCursor.getInt(mCursor.getColumnIndex(DBConstants.EXPRESS_PHOTO_Table.EXPRESS_PHOTO_TABLE_PHOTO_TYPE));
                expressPhoto.photo_express_no = mCursor.getString(mCursor.getColumnIndex(DBConstants.EXPRESS_PHOTO_Table.EXPRESS_PHOTO_TABLE_PHOTO_EXPRESS_NO));
                expressPhoto.photo_analyze_status = mCursor.getInt(mCursor.getColumnIndex(DBConstants.EXPRESS_PHOTO_Table.EXPRESS_PHOTO_TABLE_ANALYZE_STATUS));
                expressPhoto.sender = mCursor.getString(mCursor.getColumnIndex(DBConstants.EXPRESS_PHOTO_Table.EXPRESS_PHOTO_TABLE_SENDER));
                expressPhoto.receiver = mCursor.getString(mCursor.getColumnIndex(DBConstants.EXPRESS_PHOTO_Table.EXPRESS_PHOTO_TABLE_RECEIVER));
                expressPhoto.upload_status = mCursor.getInt(mCursor.getColumnIndex(DBConstants.EXPRESS_PHOTO_Table.EXPRESS_PHOTO_TABLE_UPLOAD_STATUS));
                expressPhoto.size = mCursor.getString(mCursor.getColumnIndex(DBConstants.EXPRESS_PHOTO_Table.EXPRESS_PHOTO_TABLE_PHOTO_SIZE));
                if (expressPhoto != null && !StringUtils.isEmpty(expressPhoto.photo_id)
                        && System.currentTimeMillis() - Long.parseLong(expressPhoto.photo_id) > ExpressConstant.ACTIVE_TIME) {
                    deletePhotoByID(expressPhoto.photo_id, username);
                } else {
                    expressPhotos.add(expressPhoto);
                }
            }
            if (mCursor != null) {
                mCursor.close();
            }
        }

        return expressPhotos;

    }

    /**
     * 更新状态
     *
     * @param photo_id
     * @return
     */
    public boolean updatePhotoUploadStatus(String photo_id, String username, int upload_status ) {
        ContentValues values = new ContentValues();
        values.put(DBConstants.EXPRESS_PHOTO_Table.EXPRESS_PHOTO_TABLE_UPLOAD_STATUS, upload_status);
        try {
            mSQLiteDatabase.update(DBConstants.EXPRESS_PHOTO_Table.TABLE_NAME, values,
                    DBConstants.EXPRESS_PHOTO_Table.EXPRESS_PHOTO_TABLE_UID + " = ? AND "
                            + DBConstants.EXPRESS_PHOTO_Table.EXPRESS_PHOTO_TABLE_USER_ID + " = ?", new String[]{photo_id, username});
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * 查询状态
     *
     * @param photo_id
     * @return
     */
    public boolean isPhotoUploading(String photo_id, String username) {
        String SELECT_SQL = "SELECT * FROM " + DBConstants.EXPRESS_PHOTO_Table.TABLE_NAME
                + " WHERE " + DBConstants.EXPRESS_PHOTO_Table.EXPRESS_PHOTO_TABLE_UID + " = " + photo_id
                + " AND " + DBConstants.EXPRESS_PHOTO_Table.EXPRESS_PHOTO_TABLE_USER_ID + " = " + username;
        Cursor mCursor = mSQLiteDatabase.rawQuery(SELECT_SQL, null);
        int upload_status = ExpressConstant.UPLOAD_STATUS_NORMAL;
        if (mCursor != null) {
            if (mCursor.moveToFirst()) {//just need to query one time
                upload_status = mCursor.getInt(mCursor.getColumnIndex(DBConstants.EXPRESS_PHOTO_Table.EXPRESS_PHOTO_TABLE_UPLOAD_STATUS));
            }
        }
        if (mCursor != null) {
            mCursor.close();
        }
        if (upload_status == ExpressConstant.UPLOAD_STATUS_UPLOADING) {
            return true;
        } else {
            return false;
        }
    }


    /**
     * 根据id删除记录
     *
     * @param photo_id
     */
    public void deletePhotoByID(String photo_id, String username) {
        mSQLiteDatabase.delete(DBConstants.EXPRESS_PHOTO_Table.TABLE_NAME,
                DBConstants.EXPRESS_PHOTO_Table.EXPRESS_PHOTO_TABLE_UID + " = " + photo_id
                    + " AND " + DBConstants.EXPRESS_PHOTO_Table.EXPRESS_PHOTO_TABLE_USER_ID + " = " + username, null);
    }

//    public void insertAlbums(List<Album> list){
//        mSQLiteDatabase.beginTransaction();
//        for(Album album : list){
//            ContentValues values = new ContentValues();
//            values.put(DBConstants.album_table.ALBUM_ID, album.getAlbumId());
//            values.put(DBConstants.album_table.ALBUM_TAG, album.getAlbumTag());
//            values.put(DBConstants.album_table.ALBUM_COVER, album.getAlbumCover());
//            values.put(DBConstants.album_table.ALBUM_TOTAL_BLOG, album.getAlbumTotal());
//            try{
//                mSQLiteDatabase.insertOrThrow(DBConstants.album_table.TABLE_NAME, "", values);
//            }catch (SQLException e) {
//                LogUtil.d(TAG, "insert album failed");
//                LogUtil.w(TAG, e);
//                ContentValues value = new ContentValues();
//                value.put(DBConstants.album_table.ALBUM_COVER, album.getAlbumCover());
//                value.put(DBConstants.album_table.ALBUM_TOTAL_BLOG, album.getAlbumTotal());
//                try{
//                    LogUtil.d(TAG, "update album");
//                    mSQLiteDatabase.update(DBConstants.album_table.TABLE_NAME,
//                            values, DBConstants.album_table.ALBUM_ID + "="
//                                    + album.getAlbumId(), null);
//                }catch (SQLException e1) {
//                    LogUtil.w(TAG, e1 );
//                }
//
//            }
//
//        }
//        mSQLiteDatabase.setTransactionSuccessful();
//        mSQLiteDatabase.endTransaction();
//
//    }

//
//    public boolean addBlogInfo(final Blog blog) {
//        LogUtil.d(TAG, "begin to add blog info");
//        try {
//            mSQLiteDatabase.execSQL(
//                    "insert into " + DBConstants.blog_table.TABLE_NAME + " (" +
//                            DBConstants.blog_table.BLOG_TABLE_BID + "," +
//                            DBConstants.blog_table.BLOG_TABLE_UID + "," +
//                            DBConstants.blog_table.BLOG_TABLE_GROUP_ID + "," +
//                            DBConstants.blog_table.BLOG_TABLE_NICK + "," +
//                            DBConstants.blog_table.BLOG_TABLE_HEAD + "," +
//                            DBConstants.blog_table.BLOG_TABLE_TITLE + "," +
//                            DBConstants.blog_table.BLOG_TABLE_BODY + "," +
//                            DBConstants.blog_table.BLOG_TABLE_IMAGE + "," +
//                            DBConstants.blog_table.BLOG_TABLE_VIDEO + "," +
//                            DBConstants.blog_table.BLOG_TABLE_FTYPE + "," +
//                            DBConstants.blog_table.BLOG_TABLE_TIME + "," +
//                            DBConstants.blog_table.BLOG_TABLE_HOT_NUM + "," +
//                            DBConstants.blog_table.BLOG_TABLE_COMMENT_NUM + "," +
//                            DBConstants.blog_table.BLOG_TABLE_FORWARD_NUM + "," +
//                            DBConstants.blog_table.BLOG_TABLE_FORWARD_UID + "," +
//                            DBConstants.blog_table.BLOG_TABLE_FORWARD_REASON + "," +
//                            DBConstants.blog_table.BLOG_TABLE_SEX + "," +
//                            DBConstants.blog_table.BLOG_TABLE_PIC_NUM + "," +
//                            DBConstants.blog_table.BLOG_TABLE_FROM_BY + "," +
//                            DBConstants.blog_table.BLOG_TABLE_ORIGINAL_AUTHOR_NAME + "," +
//                            DBConstants.blog_table.BLOG_TABLE_CITY + "," +
//                            DBConstants.blog_table.BLOG_TABLE_FORWARD_NICKNAME + "," +
//                            DBConstants.blog_table.BLOG_TABLE_AID + "," +
//                            DBConstants.blog_table.BLOG_TABLE_FORWARD_FTYPE + ") " +
//                            "values(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)",
//                    new Object[]{blog.id, blog.uid, blog.group_id, blog.nickname, blog.avatar, blog.title, blog.content,
//                            blog.first_img_url, blog.videoUrl, blog.ftype, blog.time, blog.hot_num, blog.comment_num, blog.forward_num
//                            , blog.forward_feed_id, blog.forward_reason, blog.sex, blog.picNum, blog.fromBy, blog.originalNickname, blog.city
//                            , blog.forward_nickname, blog.aid, blog.forward_ftype});
//            return true;
//
//        } catch (Exception e) {
//            e.printStackTrace();
//            return false;
//        }
//    }
//
//    public Cursor getCursor() {
//        String sql = "select * from " + DBConstants.blog_table.TABLE_NAME;
//        Cursor cursor = mSQLiteDatabase.rawQuery(sql, null);
//        return cursor;
//    }
//
//    /**
//     * FunName:				getInt
//     * Description :			根据当前域名返回对应行对应列的int值
//     *
//     * @param： Cursor cursor 是当前bolg info在 list中的位置
//     * String field_name 对应表的域名（列名）
//     * @return： int：返回对应域名的int值，失败返回-1
//     * @Author： wangxy
//     * @Create Date:：	2011-10-19
//     */
//    public int getInt(final Cursor cursor, final String field_name) {
//        int i = -1;
//        i = cursor.getInt(cursor.getColumnIndexOrThrow(field_name));
//        return i;
//    }
//
//    /**
//     * FunName:				getStringofCursorByFieldName
//     * Description :			根据当前域名返回对应行对应列的string值
//     *
//     * @param： Cursor cursor 是当前blog info在 list中的位置
//     * String field_name 对应表的域名（列名）
//     * @return： String：返回对应域名的String值，失败返回null
//     * @Author： wangxy
//     * @Create Date:：	2011-10-19
//     */
//    public String getString(final Cursor cursor, final String field_name) {
//        String string = null;
//        string = cursor.getString(cursor.getColumnIndexOrThrow(field_name));
//        return string;
//    }
//
//
//    /**
//     * FunName:				getBlogIdbyCursor
//     * Description :			根据当前blog的位置得到blog id
//     *
//     * @param： int position 是当前blog在 list中的位置
//     * @return： int：返回 blog id,无此记录则返回0；
//     * @Author： wangxy
//     * @Create Date:：	2011-10-17
//     */
//    public int getBlogIdbyCursor(final int position) {
//        LogUtil.d(TAG, "getBlogIdbyCursor:position" + " =" + String.valueOf(position));
//        String[] columns = new String[]{DBConstants.blog_table.BLOG_TABLE_BID};
//        try {
//            Cursor cursor = mSQLiteDatabase.query(DBConstants.blog_table.TABLE_NAME, columns,
//                    DBConstants.blog_table.BLOG_TABLE_ID + " = " + position, null, null, null, null);
//            int blog_id = cursor.getInt(0);
//            return blog_id;
//
//        } catch (Exception e) {
//            e.printStackTrace();
//            return 0;
//        }
//
//    }
//
//    /**
//     * FunName:				getUidbyCursor
//     * Description :			根据当前blog的位置得到blog uid
//     *
//     * @param： int position 是当前blog在 list中的位置
//     * @return： String：返回blog uid，无此记录返回null；
//     * @Author： wangxy
//     * @Create Date:：	2011-10-17
//     */
//    public String getUidbyCursor(final int position) {
//        LogUtil.d(TAG, "getUidbyCursor postition" + " =" + String.valueOf(position));
//        String[] columns = new String[]{DBConstants.blog_table.BLOG_TABLE_UID};
//        LogUtil.d(TAG, "getBlogUidbyCursor:postition" + " =" + String.valueOf(position));
//        try {
//            Cursor cursor = mSQLiteDatabase.query(DBConstants.blog_table.TABLE_NAME, columns,
//                    DBConstants.blog_table.BLOG_TABLE_ID + " = " + position, null, null, null, null);
//            LogUtil.d(TAG, "getBlogUidbyCursor:postition" + " =" + String.valueOf(position));
//            cursor.moveToFirst();
//            String blog_uid = cursor.getString(0);
//            cursor.close();
//            return blog_uid;
//
//        } catch (Exception e) {
//            e.printStackTrace();
//            return null;
//        }
//
//    }
//
//    /**
//     * 根据博客id得到活动id
//     *
//     * @param feedId
//     * @return
//     */
//    public String getAidByBlogId(String feedId) {
//        LogUtil.d(TAG, "get aid by  feedId");
//        String aid = null;
//        String[] columns = new String[]{DBConstants.blog_table.BLOG_TABLE_AID};
//        try {
//            Cursor cursor = mSQLiteDatabase.query(DBConstants.blog_table.TABLE_NAME, columns,
//                    DBConstants.blog_table.BLOG_TABLE_BID + " = " + feedId, null, null, null, null);
//            cursor.moveToFirst();
//            if (cursor.getCount() > 0) {
//                aid = cursor.getString(0);
//            }
//            cursor.close();
//            return aid;
//        } catch (Exception e) {
//            e.printStackTrace();
//            return null;
//        }
//    }
//
//    /**
//     * FunName:				deleteblog
//     * Description :			根据当前blog feed ID delete blog
//     *
//     * @param： String bid 是当前blog的feed_id
//     * @return： boolean：返回成功或失败；
//     * @Author： wangxy
//     * @Create Date:：	2011-10-17
//     */
//    public boolean deleteBlog(final String bid) {
//        LogUtil.d(TAG, "deleteblog");
//        mSQLiteDatabase.delete(DBConstants.blog_table.TABLE_NAME,
//                DBConstants.blog_table.BLOG_TABLE_BID + " = " + bid, null);
//        return true;
//    }
//
//    /**
//     * FunName:				addBlogHot
//     * Description :			根据当前blog feed ID对其 人气加1
//     *
//     * @param： String bid 是当前blog的feed_id
//     * @return： none
//     * @Author： wangxy
//     * @Create Date:：	2011-11-07
//     */
//    public void addBlogHot(final String bid) {
//        LogUtil.d(TAG, "addBlogHot =");
//        String[] columns = new String[]{DBConstants.blog_table.BLOG_TABLE_HOT_NUM};
//        String hotNum = null;
//        int i = 0;
//        try {
//            Cursor cursor = mSQLiteDatabase.query(DBConstants.blog_table.TABLE_NAME, columns,
//                    DBConstants.blog_table.BLOG_TABLE_BID + " = " + bid, null, null, null, null);
//            if (cursor.moveToFirst()) {
//                hotNum = cursor.getString(0);
//                cursor.close();
//                LogUtil.d(TAG, "hotNum =" + String.valueOf(hotNum));
//            }
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//        try {
//            i = Integer.parseInt(hotNum);
//        } catch (NumberFormatException NFE) {
//            LogUtil.d(TAG, context.getResources().getString(R.string.format_err));
//        }
//
//        ContentValues values = new ContentValues();
//        values.put(DBConstants.blog_table.BLOG_TABLE_HOT_NUM, String.valueOf(i + 1));
//        try {
//            mSQLiteDatabase.update(DBConstants.blog_table.TABLE_NAME, values, DBConstants.blog_table.BLOG_TABLE_BID + "=" + bid, null);
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//    }
//
//    /**
//     * FunName:				addBlogHot
//     * Description :			根据当前blog feed ID对其 人气加1
//     *
//     * @param： String bid 是当前blog的feed_id
//     * @return： none
//     * @Author： wangxy
//     * @Create Date:：	2011-11-07
//     */
//    public void reduceBlogHot(final String bid) {
//        LogUtil.d(TAG, "addBlogHot =");
//        String[] columns = new String[]{DBConstants.blog_table.BLOG_TABLE_HOT_NUM};
//        String hotNum = null;
//        int i = 0;
//        try {
//            Cursor cursor = mSQLiteDatabase.query(DBConstants.blog_table.TABLE_NAME, columns,
//                    DBConstants.blog_table.BLOG_TABLE_BID + " = " + bid, null, null, null, null);
//            if (cursor.moveToFirst()) {
//                hotNum = cursor.getString(0);
//                cursor.close();
//                LogUtil.d(TAG, "hotNum =" + String.valueOf(hotNum));
//            }
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//        try {
//            i = Integer.parseInt(hotNum);
//        } catch (NumberFormatException NFE) {
//            LogUtil.d(TAG, context.getResources().getString(R.string.format_err));
//        }
//
//        ContentValues values = new ContentValues();
//        values.put(DBConstants.blog_table.BLOG_TABLE_HOT_NUM, String.valueOf(i - 1));
//        try {
//            mSQLiteDatabase.update(DBConstants.blog_table.TABLE_NAME, values, DBConstants.blog_table.BLOG_TABLE_BID + "=" + bid, null);
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//    }
//
//    /**
//     * FunName:				getLastBlogInfo
//     * Description :			得到当前表中的最后一条数据信息
//     *
//     * @param：
//     * @return： String[]：返回最后一条blog的 id and time；
//     * @Author： wangxy
//     * @Create Date:：	2011-10-17
//     */
//    public String[] getLastBlogInfo() {
//        LogUtil.d(TAG, "getLastBlogInfo");
//        String[] strings = new String[2];
//        String[] columns = new String[]{DBConstants.blog_table.BLOG_TABLE_BID, DBConstants.blog_table.BLOG_TABLE_TIME};
//        try {
//            Cursor cursor = mSQLiteDatabase.query(DBConstants.blog_table.TABLE_NAME, columns,
//                    null, null, null, null, null);
//            if (null == cursor)
//                LogUtil.d(TAG, "getlastblog = null");
//            else
//                LogUtil.d(TAG, "getlastblog != null");
//            if (cursor != null && cursor.moveToLast()) {
//                strings[0] = cursor.getString(0);
//                strings[1] = cursor.getString(1);
//                cursor.close();
//                return strings;
//            }
//
//        } catch (Exception e) {
//            e.printStackTrace();
//            return null;
//        }
//        return null;
//    }

}
