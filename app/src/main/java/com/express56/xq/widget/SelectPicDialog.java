package com.express56.xq.widget;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;

import com.express56.xq.R;

/**
 * 普通的对话框
 */
public class SelectPicDialog extends Dialog implements View.OnClickListener{

    private int offsetY;
    private Context context;

    private ClickListenerInterface clickListenerInterface;

    public interface ClickListenerInterface {
        public void selectFromAlbum();

        public void selectFromCamera();

        public void selectCancel();
    }

    public SelectPicDialog(Context context) {
        super(context, R.style.dialog);
        this.context = context;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        init();
    }

    private void init() {
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.select_pic_dialog, null);
        setContentView(view);

        TextView btn_Album = (TextView) view.findViewById(R.id.select_from_album);
        btn_Album.setOnClickListener(this);
        TextView btn_Camera = (TextView) view.findViewById(R.id.select_from_camera);
        btn_Camera.setOnClickListener(this);
        TextView btn_Cancel = (TextView) view.findViewById(R.id.select_cancel);
        btn_Cancel.setOnClickListener(this);

        Window dialogWindow = getWindow();
        WindowManager.LayoutParams lp = dialogWindow.getAttributes();
        DisplayMetrics d = context.getResources().getDisplayMetrics(); // 获取屏幕宽、高用
        lp.width = (int) (d.widthPixels * 0.9); // 宽度设置为屏幕的0.9
        int x = lp.x;
        int y = (int) (lp.y + offsetY * d.scaledDensity);
        int t = lp.y -= y;
        dialogWindow.setAttributes(lp);

    }

    public void setClickListener(ClickListenerInterface clickListenerInterface) {
        this.clickListenerInterface = clickListenerInterface;
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        switch (id) {
            case R.id.select_from_album:
                clickListenerInterface.selectFromAlbum();
                break;
            case R.id.select_from_camera:
                clickListenerInterface.selectFromCamera();
                break;
            case R.id.select_cancel:
                clickListenerInterface.selectCancel();
                dismiss();
                break;
            default:
                break;
        }
    }

//    private class ClickListener implements View.OnClickListener {
//
//        @Override
//        public void onClick(View v) {
//            int id = v.getId();
//            switch (id) {
//                case R.id.confirm:
//                    clickListenerInterface.doConfirm();
//                    break;
//                case R.id.cancel:
//                    clickListenerInterface.doCancel();
//                    break;
//            }
//        }
//    }

}
