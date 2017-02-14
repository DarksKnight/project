package com.express56.xq.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;

import com.express56.xq.R;

/**
 * Created by bojoy-sdk2 on 2017/2/13.
 */

public class WeightChoose extends LinearLayout implements View.OnClickListener {

    private Button btnMinus = null;
    private Button btnPlus = null;
    private EditText etNumber = null;
    private double weight = 1;

    public WeightChoose(Context context) {
        this(context, null);
    }

    public WeightChoose(Context context, AttributeSet attrs) {
        super(context, attrs);

        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.layout_weight_choose, this, false);
        addView(view);

        initView(context);
    }

    private void initView(Context context) {
        btnMinus = (Button)findViewById(R.id.btn_weight_choose_minus);
        btnPlus = (Button)findViewById(R.id.btn_weight_choose_plus);
        etNumber = (EditText)findViewById(R.id.et_weight_choose);

        btnMinus.setOnClickListener(this);
        btnPlus.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_weight_choose_minus:
                if(weight <= 0) {
                    return;
                }
                weight = Float.parseFloat(etNumber.getText().toString()) - 0.5;
                etNumber.setText(weight + "");
                break;
            case R.id.btn_weight_choose_plus:
                weight = Float.parseFloat(etNumber.getText().toString()) + 0.5;
                etNumber.setText(weight + "");
                break;
            default:
                break;
        }
    }

    public double getWeight() {
        return weight;
    }
}
