package com.express56.xq.receiver;

import android.app.ActivityManager;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.NotificationCompat;
import android.text.TextUtils;
import android.widget.Toast;

import com.express56.xq.R;
import com.express56.xq.activity.MainActivity;
import com.express56.xq.constant.ExpressConstant;
import com.express56.xq.util.DeviceConfigUtil;
import com.express56.xq.util.LogUtil;
import com.express56.xq.util.SharedPreUtils;
import com.tencent.android.tpush.XGPushBaseReceiver;
import com.tencent.android.tpush.XGPushClickedResult;
import com.tencent.android.tpush.XGPushRegisterResult;
import com.tencent.android.tpush.XGPushShowedResult;
import com.tencent.android.tpush.XGPushTextMessage;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

/**
 * Created by Administrator on 2016/5/31.
 */
public class MsgPushReceiver extends XGPushBaseReceiver {

    private static final String TAG = MsgPushReceiver.class.getSimpleName();

    private void show(Context context, String text) {
//		Toast.makeText(context, text, Toast.LENGTH_SHORT).show();
    }

    // 通知展示
    @Override
    public void onNotifactionShowedResult(Context context,
                                          XGPushShowedResult notifiShowedRlt) {
        if (context == null || notifiShowedRlt == null) {
            return;
        }
        show(context, "您有1条新消息, " + "通知被展示 ， " + notifiShowedRlt.toString());
    }

    @Override
    public void onUnregisterResult(Context context, int errorCode) {
        if (context == null) {
            return;
        }
        String text = "";
        if (errorCode == XGPushBaseReceiver.SUCCESS) {
            text = "反注册成功";
        } else {
            text = "反注册失败" + errorCode;
        }
        LogUtil.d(TAG, text);
        show(context, text);

    }

    @Override
    public void onSetTagResult(Context context, int errorCode, String tagName) {
        if (context == null) {
            return;
        }
        String text = "";
        if (errorCode == XGPushBaseReceiver.SUCCESS) {
            text = "\"" + tagName + "\"设置成功";
        } else {
            text = "\"" + tagName + "\"设置失败,错误码：" + errorCode;
        }
        LogUtil.d(TAG, text);
        show(context, text);

    }

    @Override
    public void onDeleteTagResult(Context context, int errorCode, String tagName) {
        if (context == null) {
            return;
        }
        String text = "";
        if (errorCode == XGPushBaseReceiver.SUCCESS) {
            text = "\"" + tagName + "\"删除成功";
        } else {
            text = "\"" + tagName + "\"删除失败,错误码：" + errorCode;
        }
        LogUtil.d(TAG, text);
        show(context, text);

    }

    // 通知点击回调 actionType=1为该消息被清除，actionType=0为该消息被点击
    @Override
    public void onNotifactionClickedResult(Context context,
                                           XGPushClickedResult message) {
        if (context == null || message == null) {
            return;
        }
        String text = "";
        if (message.getActionType() == XGPushClickedResult.NOTIFACTION_CLICKED_TYPE) {
            // 通知在通知栏被点击啦。。。。。
            // APP自己处理点击的相关动作
            // 这个动作可以在activity的onResume也能监听，请看第3点相关内容
            text = "通知被打开 :" + message;
        } else if (message.getActionType() == XGPushClickedResult.NOTIFACTION_DELETED_TYPE) {
            // 通知被清除啦。。。。
            // APP自己处理通知被清除后的相关动作
            text = "通知被清除 :" + message;
        }
        Toast.makeText(context, "广播接收到通知被点击:" + message.toString(),
                Toast.LENGTH_SHORT).show();
        // 获取自定义key-value
        String customContent = message.getCustomContent();
        if (customContent != null && customContent.length() != 0) {
            try {
                JSONObject obj = new JSONObject(customContent);
                // key1为前台配置的key
                if (!obj.isNull("key")) {
                    String value = obj.getString("key");
                    LogUtil.d(TAG, "get custom value:" + value);
                }
                // ...
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
        // TODO Auto-generated method stub
        if (context == null || message == null) {
            return;
        }
        String text = "";
        if (errorCode == XGPushBaseReceiver.SUCCESS) {
            text = message + "注册成功";
            // 在这里拿token
            String token = message.getToken();
        } else {
            text = message + "注册失败，错误码：" + errorCode;
        }
        LogUtil.d(TAG, text);
        show(context, text);
    }

    // 消息透传
    @Override
    public void onTextMessage(Context context, XGPushTextMessage message) {
        SharedPreUtils sp = new SharedPreUtils(context);
        // TODO Auto-generated method stub
        String text = "收到消息:" + message.toString();
        // 获取自定义key-value
        String customContent = message.getCustomContent();
        LogUtil.d(TAG, "get custom :" + customContent);
        if (customContent != null && customContent.length() != 0) {
            try {
                JSONObject obj = new JSONObject(customContent);
                String lastContent = sp.getString(ExpressConstant.LATEST_PUSH_MSG);//上次的推送消息
                if (!TextUtils.isEmpty(lastContent)) {
                    JSONObject lastObj = new JSONObject(lastContent);
                    if (obj.has("type") && obj.has("site")
                            && obj.getString("type").equals(lastObj.getString("type"))
                            && obj.getString("site").equals((lastObj.getString("site")))) {
                        //同一种推送类型 同一个桩点 不能连续推送两次
                        LogUtil.d(TAG, "同一种推送类型 同一个桩点 不能连续推送两次");
                    } else {
                        createNotification(context, message, obj);
                    }
                } else {
                    createNotification(context, message, obj);
                }
//                LogUtil.d(TAG, "get custom :" + customContent);
                sp.setString("latest_push_msg", customContent);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        // APP自主处理消息的过程...
        LogUtil.d(TAG, text);
        show(context, text);
    }

    private void createNotification(Context context, XGPushTextMessage message, JSONObject messageJson) {
        //定义NotificationManager
        String ns = Context.NOTIFICATION_SERVICE;
        NotificationManager mNotificationManager = (NotificationManager) context.getSystemService(ns);
        //定义通知栏展现的内容信息
        int icon = R.mipmap.ic_launcher;
        long when = System.currentTimeMillis();

        //定义下拉通知栏时要展现的内容信息
        CharSequence contentTitle = message.getTitle();
        CharSequence contentText = message.getContent();

        Notification notification = new NotificationCompat.Builder(context)
                .setSmallIcon(icon)
                .setContentTitle(contentTitle)
                .setContentText(contentText)
                .setWhen(when)
                .setDefaults(Notification.DEFAULT_SOUND)
                .build();

        notification.flags |= Notification.FLAG_AUTO_CANCEL;
        Intent notificationIntent = null;

        if (MainActivity.mainActivity != null) {
            ActivityManager manager = (ActivityManager)context.getSystemService(Context.ACTIVITY_SERVICE) ;
            List<ActivityManager.RunningTaskInfo> runningTaskInfos = manager.getRunningTasks(1) ;
            if(runningTaskInfos != null) {
                if (messageJson != null) {
//                    notificationIntent = new Intent(context, PushMsgActivity.class);
                    notificationIntent = new Intent(context, MainActivity.class);
                }
            }
        } else {
            //重新启动app
            LogUtil.i(TAG, "the app process is dead");
//            notificationIntent = new Intent(context, TheLogoActivity.class);
        }
        putMessage(messageJson, notificationIntent);
        PendingIntent contentIntent = PendingIntent.getActivity(context, 0, notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT);
//        notification.setLatestEventInfo(context, contentTitle, contentText, contentIntent);
        notification.contentIntent = contentIntent;

        //用mNotificationManager的notify方法通知用户生成标题栏消息通知
        mNotificationManager.notify(ExpressConstant.NOTIFICATION_ID_PUSH, notification);
    }

    private void dealWithPushMessage(Context context, JSONObject messageJson) {
        LogUtil.i("NotificationReceiver", "dealWithPushMessage");

        //判断app进程是否存活
//        if(DeviceConfigUtil.isAppRunning(context, context.getPackageName())){
        if(DeviceConfigUtil.isAppInBackground(context) || DeviceConfigUtil.isAppInForeground(context)){
            //如果存活的话，就直接启动DetailActivity，但要考虑一种情况，就是app的进程虽然仍然在
            //但Task栈已经空了，比如用户点击Back键退出应用，但进程还没有被系统回收，如果直接启动
            //DetailActivity,再按Back键就不会返回MainActivity了。所以在启动
            //DetailActivity前，要先启动MainActivity。
            LogUtil.i(TAG, "the app process is alive");

            ActivityManager manager = (ActivityManager)context.getSystemService(Context.ACTIVITY_SERVICE);
            List<ActivityManager.RunningTaskInfo> runningTaskInfos = manager.getRunningTasks(1);
            if(runningTaskInfos != null) {
                String baseActivityName = (runningTaskInfos.get(0).baseActivity).toString();
                String topActivityName = (runningTaskInfos.get(0).topActivity).toString();
                Intent targetIntent = null;
                //将MainAtivity的launchMode设置成SingleTask, 或者在下面flag中加上Intent.FLAG_CLEAR_TOP,
                //如果Task栈中有MainActivity的实例，就会把它移到栈顶，把在它之上的Activity都清理出栈，
                //如果Task栈不存在MainActivity实例，则在栈顶创建
                if (baseActivityName != null && baseActivityName.contains(MainActivity.class.getSimpleName())
                        && topActivityName != null && topActivityName.contains(MainActivity.class.getSimpleName())){
                    LogUtil.i(TAG, "start Main");
                    targetIntent = new Intent(context, MainActivity.class);
                    targetIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    putMessage(messageJson, targetIntent);
                    context.startActivity(targetIntent);
                } else {
                    LogUtil.i(TAG, "the app process is others");
                    return;
                }

            }
        } else {
            //如果app进程已经被杀死，先重新启动app，将DetailActivity的启动参数传入Intent中，参数经过
            //SplashActivity传入MainActivity，此时app的初始化已经完成，在MainActivity中就可以根据传入             //参数跳转到DetailActivity中去了
            LogUtil.i(TAG, "the app process is dead");
            Intent launchIntent = context.getPackageManager().getLaunchIntentForPackage(context.getPackageName());
            launchIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED);
            putMessage(messageJson, launchIntent);
            context.startActivity(launchIntent);
        }
    }

    private void putMessage(JSONObject messageJson, Intent mainIntent) {
        if (messageJson != null && !messageJson.isNull("site")) {
            try {
                mainIntent.putExtra(ExpressConstant.PUSH_TYPE, messageJson.getString("type"));
                mainIntent.putExtra(ExpressConstant.PUSH_POSITION, messageJson.getString("site"));
                mainIntent.putExtra(ExpressConstant.PUSH_TIME, messageJson.getString("date"));
                mainIntent.putExtra(ExpressConstant.PUSH_SERIAL_ID, messageJson.getString("serial_id"));
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }
}
