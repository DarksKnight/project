package com.express56.xq.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;

import com.express56.xq.util.LogUtil;

import java.util.ArrayList;

/**
 * 条形码上传
 */
public class BarcodeDataHelper {
    private static final String TAG = BarcodeDataHelper.class.getSimpleName();// 调试标签

    private DBOpenHelper dbOpenHelper = null;
    private SQLiteDatabase mSQLiteDatabase = null;
    private Context context = null;

    public BarcodeDataHelper(Context context) {
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

    /**
     * 存入条形码
     * @param barcode
     */
    public void insertBarcode(String barcode) {
        mSQLiteDatabase.beginTransaction();
        ContentValues values = new ContentValues();
        values.put(DBConstants.EXPRESS_BAR_CODE_Table.EXPRESS_BAR_CODE_TABLE_BARCODE, barcode);
        mSQLiteDatabase.insert(DBConstants.EXPRESS_BAR_CODE_Table.TABLE_NAME, "", values);
        mSQLiteDatabase.setTransactionSuccessful();
        mSQLiteDatabase.endTransaction();
    }

    /**
     * 获取当前表中所有的条形码字符串
     * @return
     */
    public ArrayList<String> getAll() {
        String SELECT_SQL = "SELECT * FROM " + DBConstants.EXPRESS_BAR_CODE_Table.TABLE_NAME;
        ArrayList<String> list = new ArrayList<String>();
        String barcode = null;
        Cursor mCursor = mSQLiteDatabase.rawQuery(SELECT_SQL, null);
        if (mCursor != null) {
            if (mCursor.moveToFirst()) {//just need to query one time
                //cur.moveToFirst()让游标指向第一行，如果游标指向第一行，则返回true
                do {
                    barcode = mCursor.getString(mCursor.getColumnIndex(DBConstants.EXPRESS_BAR_CODE_Table.EXPRESS_BAR_CODE_TABLE_BARCODE));
                    list.add(barcode);
                } while (mCursor.moveToNext());
                        /*游标移动到下一行，如果游标已经到达结果集中的最后，即没有行可以移动时，则返回false*/
                //其他可能移动的是 moveToPrevious() 和moveToFirst()方法
            }
        }
        if (mCursor != null) {
            mCursor.close();
        }
        return list;
    }

    /**
     * 根据barcode删除记录
     *
     * @param list
     */
    public void deleteByBarcode(ArrayList<String> list) {
        for (int i = 0; i < list.size(); i++) {
            if (mSQLiteDatabase != null) {
                mSQLiteDatabase.delete(DBConstants.EXPRESS_BAR_CODE_Table.TABLE_NAME,
                        DBConstants.EXPRESS_BAR_CODE_Table.EXPRESS_BAR_CODE_TABLE_BARCODE + " = '" + list.get(i) + "'", null);
            }
        }
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
