package com.express56.xq.util;

import android.app.AlertDialog.Builder;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.express56.xq.R;

/**
 * DialogUtils
 * 对话框类
 *
 * @author Guofeng Huang
 */
public class DialogUtils {

    /**
     * 默认提示文字的loading 对话框
     *
     * @param context
     * @return
     */
    public static Dialog createLoadingDialog(Context context, boolean cancelable) {
        return createLoadingDialog(context, null, cancelable);
    }

    public static void showLoadingDialog(final Dialog dialog) {
//        if (dialog == null) {
//            dialog = createLoadingDialog(context, null);
//        }
        if (dialog != null && !dialog.isShowing()) {
            dialog.show();
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    if (dialog != null) {
                        dialog.dismiss();
                    }
                }
            }, 40000);
        }
    }

    public static void closeLoadingDialog(Dialog dialog) {
        if (dialog != null) {
            dialog.dismiss();
        }
    }

    /**
     * 得到自定义的progressDialog
     *
     * @param context
     * @param msg     提示文字
     * @return
     */
    public static Dialog createLoadingDialog(Context context, String msg, boolean cancelable) {

        LayoutInflater inflater = LayoutInflater.from(context);
        View v = inflater.inflate(R.layout.loading_dialog, null);// 得到加载view
        LinearLayout layout = (LinearLayout) v.findViewById(R.id.dialog_view);// 加载布局
        // main.xml中的ImageView
        ImageView spaceshipImage = (ImageView) v.findViewById(R.id.img);
        TextView tipTextView = (TextView) v.findViewById(R.id.tipTextView);// 提示文字
        // 加载动画
        Animation hyperspaceJumpAnimation = AnimationUtils.loadAnimation(
                context, R.anim.loading_animation);
        // 使用ImageView显示动画
        spaceshipImage.startAnimation(hyperspaceJumpAnimation);
        if (StringUtils.notEmpty(msg)) {
            tipTextView.setText(msg);// 设置加载信息
        }

        Dialog loadingDialog = new Dialog(context, R.style.loading_dialog);// 创建自定义样式dialog

        loadingDialog.setCancelable(cancelable);
        loadingDialog.setContentView(layout, new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.MATCH_PARENT));// 设置布局
        return loadingDialog;
    }

    /**
     * 弹出"确定"询问窗口
     *
     * @param context
     * @param title    兑换框的标题
     * @param message  对话框提示的内容
     * @param callBack 执行事件的接口
     */
    public static void showDialog(Context context, String title,
                                  String message, final DialogSingleCallBack callBack) {
        Builder builder = new Builder(context);
        builder.setMessage(message);
        builder.setTitle(title);
        builder.setPositiveButton("确认",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        callBack.onClickedOk();
                    }
                });
        builder.create().show();
    }

    /**
     * 弹出"确定/取消"询问窗口
     *
     * @param context
     * @param title    对话框的标题
     * @param message  对话框提示的内容
     * @param callBack 执行事件的接口
     */
    public static void showDialog(Context context, String title,
                                  String message, final DialogDoubleCallBack callBack) {
        Builder builder = new Builder(context);
        builder.setMessage(message);
        builder.setTitle(title);
        builder.setPositiveButton("确认",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        callBack.onClickedOk();
                    }
                });

        builder.setNegativeButton("取消",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        callBack.onClickedCancel();
                    }
                });

        builder.create().show();
    }

    /**
     * 只含一个"确定"事件的接口
     */
    public interface DialogSingleCallBack {
        public void onClickedOk();
    }

    /**
     * 含"确定/取消"事件的接口
     */
    public interface DialogDoubleCallBack {
        public void onClickedOk();

        public void onClickedCancel();
    }
}