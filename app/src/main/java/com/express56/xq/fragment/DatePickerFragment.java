package com.express56.xq.fragment;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;

import com.express56.xq.constant.ExpressConstant;
import com.express56.xq.util.DateUtil;
import com.express56.xq.util.LogUtil;
import com.express56.xq.widget.ToastUtil;

import java.util.Calendar;
import java.util.Date;

/**
 * Created by qxpd52 on 16/12/2.
 */

public class DatePickerFragment extends DialogFragment implements
        DatePickerDialog.OnDateSetListener {

    private ImageView view;

    public void setDate(Date date) {
        this.date = date;
    }

    public void setContext(Context context) {
        this.context = context;
    }

    private Context context = null;

    public void setDateType(int dateType) {
        this.dateType = dateType;
    }

    public void setLatestDate(Date latestDate) {
        this.latestDate = latestDate;
    }

    private Date latestDate = null;

    /**
     * 日期类型  1： 开始日期  2： 结束日期
     */
    private int dateType = 0;

    /**
     * 待比较的时间
     */
    private Date date = null;

    public void setEditText(EditText editText) {
        this.editText = editText;
    }

    private EditText editText = null;

    public DatePickerFragment() {

    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        int year = 0;
        int month = 0;
        int day = 0;
        Calendar calendar = Calendar.getInstance();
        if (latestDate == null) {
            year = calendar.get(Calendar.YEAR);
            month = calendar.get(Calendar.MONTH);
            day = calendar.get(Calendar.DAY_OF_MONTH);
        } else {
            calendar.setTime(latestDate);
            year = calendar.get(Calendar.YEAR);
            month = calendar.get(Calendar.MONTH);
            day = calendar.get(Calendar.DAY_OF_MONTH);
        }
        return new DatePickerDialog(getActivity(), this, year, month, day);
    }

    @Override
    public void onDateSet(DatePicker view, int year, int month, int day) {
        LogUtil.d("OnDateSet", "select year:" + year + ";month:" + month + ";day:" + day);
        String targetStr = null;
        if (day < 10) {
            targetStr = Integer.toString(year)
                    + "-" + Integer.toString((month + 1))
                    + "-" + "0" + day;
        } else {
            targetStr = Integer.toString(year)
                    + "-" + Integer.toString((month + 1))
                    + "-" + Integer.toString(day);
        }
        Date selectDate = DateUtil.str2Date(targetStr, DateUtil.FORMAT_DATE);
        if (date != null) {
            if (dateType == ExpressConstant.SEARCH_START_DATE) {//开始时间 必须早于结束时间
                if (selectDate.getTime() > date.getTime()) {
                    ToastUtil.showMessage(context, "开始时间不能晚于结束时间", true);
                    return;
                }
            } else if (dateType == ExpressConstant.SEARCH_END_DATE) {// 结束时间必须要晚于开始时间
                if (selectDate.getTime() < date.getTime()) {
                    ToastUtil.showMessage(context, "结束时间不能早于开始时间", true);
                    return;
                }
            }
        }
        this.editText.setText(targetStr);
        if (this.view != null) {
            this.view.setVisibility(View.VISIBLE);
        }
    }


    public void setView(ImageView view) {
        this.view = view;
    }
}