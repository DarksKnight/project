package com.express56.xq.receiver;

import android.content.Context;
import android.content.Intent;

import com.express56.xq.activity.PlaceOrderEditActivity;
import com.express56.xq.util.LogUtil;
import com.tencent.android.tpush.XGPushBaseReceiver;
import com.tencent.android.tpush.XGPushClickedResult;
import com.tencent.android.tpush.XGPushRegisterResult;
import com.tencent.android.tpush.XGPushShowedResult;
import com.tencent.android.tpush.XGPushTextMessage;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Iterator;

/**
 * Created by Administrator on 2016/5/31.
 */
public class MsgPushReceiver extends XGPushBaseReceiver {

    private static final String TAG = MsgPushReceiver.class.getSimpleName();

    private void show(Context context, String text) {
        Intent intent = new Intent(context, PlaceOrderEditActivity.class);
        context.startActivity(intent);
    }

    // 通知展示
    @Override
    public void onNotifactionShowedResult(Context context,
                                          XGPushShowedResult notifiShowedRlt) {
    }

    @Override
    public void onUnregisterResult(Context context, int errorCode) {
    }

    @Override
    public void onSetTagResult(Context context, int errorCode, String tagName) {
    }

    @Override
    public void onDeleteTagResult(Context context, int errorCode, String tagName) {
    }

    // 通知点击回调 actionType=1为该消息被清除，actionType=0为该消息被点击
    @Override
    public void onNotifactionClickedResult(Context context,
                                           XGPushClickedResult message) {
        if (context == null || message == null) {
            return;
        }
        String text = "";
        LogUtil.d(TAG, "action type : " + message.getActionType());
        if (message.getActionType() == XGPushClickedResult.NOTIFACTION_OPEN_TYPE) {
            // 通知在通知栏被点击啦。。。。。
            // APP自己处理点击的相关动作
            // 这个动作可以在activity的onResume也能监听，请看第3点相关内容
            text = "通知被打开 :" + message;
        }
        // 获取自定义key-value
        String customContent = message.getCustomContent();
        if (customContent != null && customContent.length() != 0) {
            try {
                JSONObject obj = new JSONObject(customContent);
                // key1为前台配置的key
                for(Iterator it = obj.keys();it.hasNext();) {
                    String key = it.next().toString();
                    LogUtil.d(TAG, "key : " + key);
                    LogUtil.d(TAG, "str value : " + obj.getString(key));
                }
                if (!obj.isNull("key")) {
                    String value = obj.getString("key");
                    LogUtil.d(TAG, "get custom value:" + value);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        // APP自主处理的过程。。。
        LogUtil.d(TAG, text);
        show(context, text);
    }

    @Override
    public void onRegisterResult(Context context, int errorCode,
                                 XGPushRegisterResult message) {
    }

    // 消息透传
    @Override
    public void onTextMessage(Context context, XGPushTextMessage message) {
    }

    private void createNotification(Context context, XGPushTextMessage message, JSONObject messageJson) {
    }

    private void dealWithPushMessage(Context context, JSONObject messageJson) {
    }

    private void putMessage(JSONObject messageJson, Intent mainIntent) {

    }
}
