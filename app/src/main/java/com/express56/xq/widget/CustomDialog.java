package com.express56.xq.widget;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.express56.xq.R;
import com.express56.xq.util.StringUtils;


/**
 * 普通的对话框
 */
public class CustomDialog extends Dialog {

    private String photoUid;
    /**
     * 租车地点
     */
    private String placeName;

    /**
     * 租车日期
     */
    private String date;

    /**
     * 用车时间
     */
    private int hour = 0;
    private int minute = 0;

    private String description;

    /**
     * dialog type
     */
    private int dialog_type = 0;

    /**
     * 租车确认
     */
    public static final int DIALOG_RENT_BIKE_CONFIRM = 1;

    /**
     * 支付类型选择
     */
    public static final int DIALOG_PAY_TYPE_SELECT = DIALOG_RENT_BIKE_CONFIRM + 1;

    /**
     * 拍照类型选择
     */
    public static final int DIALOG_PHOTO_TYPE_SELECT = DIALOG_PAY_TYPE_SELECT + 1;

    /**
     * 分享
     */
    public static final int DIALOG_SHARE = DIALOG_PAY_TYPE_SELECT + 1;

    /**
     * 填写单号
     */
    public static final int DIALOG_INPUT_EXPRESS_NO = DIALOG_SHARE + 1;

    private int offsetY;

    private Context context;

    private String title;

    private String confirmBtnText;

    private String cancelBtnText;

    private String promptStr;

    private String blename;

    private ClickListenerInterface clickListenerInterface;
    private ClickInputListenerInterface clickInputListenerInterface;

    private ClickItemListenerInterface clickItemListenerInterface;

    private int lineCount;
    private String express_no;
    private EditText editText;

    public void setRentBikeTime(String rentBikeTime) {
        this.rentBikeTime = rentBikeTime;
    }

    private String rentBikeTime = null;

    public void setPwdDialog(boolean pwdDialog) {
        isPwdDialog = pwdDialog;
    }

    private boolean isPwdDialog = false;

    public interface ClickListenerInterface {

        public void doConfirm();

        public void doCancel();
    }

    public interface ClickInputListenerInterface {

        public void doConfirm(String uid, String express_no);

        public void doCancel();
    }

    public interface ClickItemListenerInterface {

        public void clickFirst();

        public void clickSecond();
    }

//    public CustomDialog(Context context, String title, String confirmBtnText,
//                        String cancelBtnText, String promptStr) {
//        super(context, R.style.dialog);
//        this.context = context;
//        this.title = title;
//        this.confirmBtnText = confirmBtnText;
//        this.cancelBtnText = cancelBtnText;
//        this.promptStr = promptStr;
//        this.setCancelable(false);
//
//    }

    public CustomDialog(Context context, String promptStr, String confirmBtnText, String cancelBtnText, int offsetY) {
        super(context, R.style.dialog);
        this.context = context;
        this.confirmBtnText = confirmBtnText;
        this.cancelBtnText = cancelBtnText;
        this.promptStr = promptStr;
        this.offsetY = offsetY;
        this.setCancelable(false);
    }

    /**
     * 租车密码显示
     *
     * @param context
     * @param title
     * @param promptStr
     * @param confirmBtnText
     */
    public CustomDialog(Context context, String title, String promptStr, String confirmBtnText) {
        super(context, R.style.dialog);
        this.context = context;
        this.title = title;
        this.confirmBtnText = confirmBtnText;
        this.promptStr = promptStr;
        this.setCancelable(false);

    }

    /**
     * 支付类型选择 调用  目前两种支付方式
     *
     * @param context
     */
    public CustomDialog(Context context, int dialog_type) {
        super(context, R.style.dialog);
        this.context = context;
        this.dialog_type = dialog_type;
        this.setCancelable(true);
        this.offsetY = 60;
    }

    /**
     * 租车确认对话框
     *
     * @param context
     * @param blename
     * @param description
     */
    public CustomDialog(Context context, String blename, String description, int dialog_type) {
        super(context, R.style.dialog);
        this.context = context;
        this.blename = blename;
        this.description = description;
        this.dialog_type = dialog_type;
        offsetY = 60;
        this.setCancelable(false);

    }

    /**
     * 分享对话框
     *
     * @param context
     * @param placeName
     * @param hour
     * @param minute
     * @param date
     * @param dialog_type
     */
    public CustomDialog(Context context, String placeName, int hour, int minute, String date, int dialog_type) {
        super(context, R.style.dialog);
        this.context = context;
        this.hour = hour;
        this.minute = minute;
        this.placeName = placeName;
        this.date = date;
        this.dialog_type = dialog_type;
        offsetY = 60;
        this.setCancelable(false);

    }

    /**
     * 提交单号
     *
     * @param context
     * @param photoUid
     * @param dialog_type
     */
    public CustomDialog(Context context, String photoUid, int dialog_type) {
        super(context, R.style.dialog);
        this.context = context;
        this.photoUid = photoUid;
        this.dialog_type = dialog_type;
        offsetY = 60;
        this.setCancelable(false);

    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        init();
    }

    private void init() {
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = null;
        if (isPwdDialog) {
            view = inflater.inflate(R.layout.dialog_show_pwd, null);
            TextView textView_rent_time = (TextView) view.findViewById(R.id.textview_str_3);
            textView_rent_time.setText(rentBikeTime);
        } else {
            switch (dialog_type) {
                case DIALOG_RENT_BIKE_CONFIRM:
//                    view = inflater.inflate(R.layout.dialog_rent_bike_confirm, null);
                    break;
                case DIALOG_PHOTO_TYPE_SELECT:
                    view = inflater.inflate(R.layout.dialog_photo_type_select, null);
                    break;
                case DIALOG_INPUT_EXPRESS_NO:
                    view = inflater.inflate(R.layout.dialog_with_input, null);
                    break;
                default:
                    view = inflater.inflate(R.layout.dialog_custom, null);
                    break;
            }
        }
        setView(view, dialog_type);

        Window dialogWindow = getWindow();
        WindowManager.LayoutParams lp = dialogWindow.getAttributes();
        DisplayMetrics d = context.getResources().getDisplayMetrics(); // 获取屏幕宽、高用
        if (isPwdDialog) {
            lp.width = (int) (d.widthPixels * 2 / 3); // 密码提示 ：宽度设置为屏幕的 2/3
        } else {
            lp.width = (int) (d.widthPixels * 0.7); // 宽度设置为屏幕的0.9
        }
        int x = lp.x;
        int y = (int) (lp.y + offsetY * d.scaledDensity);
        int t = lp.y -= y;
        dialogWindow.setAttributes(lp);


    }

    private void setView(View view, int dialog_type) {
        setContentView(view);

        switch (dialog_type) {
            case DIALOG_RENT_BIKE_CONFIRM:
//                setRentBikeConfirmDialog(view);
                break;
            case DIALOG_PHOTO_TYPE_SELECT:
                setPayTypeSelectDialog(view);
                break;
            case DIALOG_INPUT_EXPRESS_NO:
                setInputView(view);
                break;
            default:
                setDefaultView(view);
                break;
        }

    }

    private void setInputView(View view) {
        view.findViewById(R.id.input_confirm).setOnClickListener(new clickListener());
        view.findViewById(R.id.input_cancel).setOnClickListener(new clickListener());

        editText = (EditText) view.findViewById(R.id.editText_express_no);
    }

    /**
     * 支付选择对话框
     */
    private void setPayTypeSelectDialog(View view) {
        view.findViewById(R.id.dialog_deposit_pay_alipay_item).setOnClickListener(new clickListener());
        view.findViewById(R.id.dialog_deposit_pay_weixinPay_item).setOnClickListener(new clickListener());
    }

//    private void setRentBikeConfirmDialog(View view) {
//        TextView tvConfirm = (TextView) view.findViewById(R.id.confirm);
//        tvConfirm.setOnClickListener(new clickListener());
//        tvConfirm.setLayoutParams(new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1.0f));
//
//        TextView tvCancel = (TextView) view.findViewById(R.id.cancel);
//        tvCancel.setOnClickListener(new clickListener());
//        tvCancel.setLayoutParams(new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1.0f));
//        TextView textView_bleName = (TextView) view.findViewById(R.id.textView_blename);
//        TextView textView_description = (TextView) view.findViewById(R.id.textview_description);
//        textView_bleName.setText(blename);
//        textView_description.setText(description);
//    }

    private void setDefaultView(View view) {
        TextView tvConfirm = (TextView) view.findViewById(R.id.confirm);
        tvConfirm.setOnClickListener(new clickListener());
        tvConfirm.setLayoutParams(new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1.0f));

        TextView tvCancel = (TextView) view.findViewById(R.id.cancel);
        tvCancel.setOnClickListener(new clickListener());
        tvCancel.setLayoutParams(new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1.0f));
        tvConfirm.setText(confirmBtnText);
        if (StringUtils.isEmpty(cancelBtnText)) {
            tvCancel.setVisibility(View.GONE);
        } else {
            tvCancel.setText(cancelBtnText);
        }
        final TextView tvPrompt = (TextView) view.findViewById(R.id.prompt_str_part);
        tvPrompt.setText(promptStr);

        ViewTreeObserver vto = tvPrompt.getViewTreeObserver();
        vto.addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {
            @Override
            public boolean onPreDraw() {
                lineCount = tvPrompt.getLineCount();
//                System.out.println(lineCount);
                return true;
            }
        });
        if (lineCount > 1) {
            tvPrompt.setGravity(Gravity.LEFT);
        }
    }

//    private int measureTextViewHeight(String text, int textSize, int deviceWidth) {
//        TextView textView = new TextView(getContext());
//        textView.setText(text);
//        textView.setPromptTextSize(TypedValue.COMPLEX_UNIT_PX, textSize);
//        int widthMeasureSpec = MeasureSpec.makeMeasureSpec(deviceWidth, MeasureSpec.AT_MOST);
//        int heightMeasureSpec = MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED);
//        textView.measure(widthMeasureSpec, heightMeasureSpec);
//        return textView.getMeasuredHeight();
//    }

    public void setClickListener(ClickListenerInterface clickListenerInterface) {
        this.clickListenerInterface = clickListenerInterface;
    }

    public void setClickListener(ClickInputListenerInterface clickInputListenerInterface) {
        this.clickInputListenerInterface = clickInputListenerInterface;
    }

    public void setClickListener(ClickItemListenerInterface clickItemListenerInterface) {
        this.clickItemListenerInterface = clickItemListenerInterface;
    }

    private class clickListener implements View.OnClickListener {

        @Override
        public void onClick(View v) {
            int id = v.getId();
            switch (id) {
                case R.id.confirm:
                    clickListenerInterface.doConfirm();
                    break;
                case R.id.cancel:
                    clickListenerInterface.doCancel();
                    break;
                case R.id.dialog_deposit_pay_alipay_item:
                    clickItemListenerInterface.clickFirst();
                    break;
                case R.id.dialog_deposit_pay_weixinPay_item:
                    clickItemListenerInterface.clickSecond();
                    break;
                case R.id.input_confirm:
                    express_no = editText.getText().toString().trim();
                    if (StringUtils.isEmpty(photoUid) || StringUtils.isEmpty(express_no)) return;
                    clickInputListenerInterface.doConfirm(photoUid, express_no);
                    break;
                case R.id.input_cancel:
                    clickInputListenerInterface.doCancel();
                    break;
                default:
                    break;
            }
        }
    }

}
