package com.express56.xq.sms;

import android.app.Activity;
import android.database.ContentObserver;
import android.database.Cursor;
import android.net.Uri;
import android.os.Handler;
import android.widget.EditText;

import com.express56.xq.util.LogUtil;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by SEELE on 2016/8/15.
 */
public class SmsContent extends ContentObserver {

    public static final String SMS_URI_INBOX = "content://sms/inbox";
    private Activity activity = null;
    private String smsContent = "";
    private EditText verifyText = null;

    public SmsContent(Activity activity, Handler handler, EditText verifyText) {
        super(handler);
        this.activity = activity;
        this.verifyText = verifyText;
    }

    @Override
    public void onChange(boolean selfChange) {
        super.onChange(selfChange);
        Cursor cursor = null;// 光标
        // 读取收件箱中指定号码的短信
        cursor = activity.getContentResolver().query(Uri.parse(SMS_URI_INBOX), new String[]{"_id", "address", "body", "read"}, "address=? and read=?",
                new String[]{"5554", "0"}, "date desc");
        if (cursor != null) {// 如果短信为未读模式
            cursor.moveToFirst();
            if (cursor.moveToFirst()) {
                String smsbody = cursor.getString(cursor.getColumnIndex("body"));
                System.out.println("smsbody=======================" + smsbody);
                String regEx = "[^0-9]";
                Pattern p = Pattern.compile(regEx);
                Matcher m = p.matcher(smsbody.toString());
                smsContent = m.replaceAll("").trim().toString();
                verifyText.setText(smsContent);
            }
        }
    }

    //invoke
//    SmsContent content = new SmsContent(LoginActivity.this, new Handler(), verifyText);
//    // 注册短信变化监听
//    this.getContentResolver().registerContentObserver(Uri.parse("content://sms/"), true, content);  // need unregister







    //    getContentResolver().registerContentObserver(Uri.parse("content://sms"), true, c);
    //    getContentResolver().unregisterContentObserver(c);

//    ContentObserver c = new ContentObserver(han) {
//        @Override
//        public void onChange(boolean selfChange) {
//            super.onChange(selfChange);
//            han.sendEmptyMessage(0);
//        }
//    };


    Handler han = new Handler() {
        @SuppressWarnings("deprecation")
        public void handleMessage(android.os.Message msg) {
            String codestr = null;
            try {
                codestr = getSMSValidateCode(new Activity());
               // code.setText(codestr);
               // requestcode();
            } catch (Exception e) {
                LogUtil.e("yung", "验证码提取失败:" + codestr);
            }
        }

        ;
    };

    public static String getSMSValidateCode(Activity c) {
        Uri uri = Uri.parse("content://sms/inbox");
        String[] projection = new String[]{"address", "person", "body"};
        String selection = " address='" + "发送验证码的短信号码" + "' ";
        String[] selectionArgs = new String[]{};
        String sortOrder = "date desc";
        @SuppressWarnings("deprecation")
        Cursor cur = c.managedQuery(uri, projection, selection, selectionArgs,
                sortOrder);
        if (cur != null && cur.getCount() > 0) {
            cur.moveToFirst();
            String body = cur.getString(cur.getColumnIndex("body")).replaceAll(
                    "\n", " ");
            cur.close();
            return getSMSValidateCode(body, 6);
        }
        cur.close();
        return null;
    }


    /**
     * 从短信字符窜提取验证码
     *
     * @param body      短信内容
     * @param YZMLENGTH 验证码的长度 一般6位或者4位
     * @return 接取出来的验证码
     */
    public static String getSMSValidateCode(String body, int YZMLENGTH) {
        // 首先([a-zA-Z0-9]{YZMLENGTH})是得到一个连续的六位数字字母组合
        // (?<![a-zA-Z0-9])负向断言([0-9]{YZMLENGTH})前面不能有数字
        // (?![a-zA-Z0-9])断言([0-9]{YZMLENGTH})后面不能有数字出现
        //Pattern p = Pattern.compile("(?<![0-9])([0-9]{" + YZMLENGTH+ "})(?![0-9])");
        Pattern p = Pattern
                .compile("(?<![a-zA-Z0-9])([a-zA-Z0-9]{" + YZMLENGTH + "})(?![a-zA-Z0-9])");
        Matcher m = p.matcher(body);
        if (m.find()) {
            System.out.println(m.group());
            return m.group(0);
        }
        return null;
    }


}