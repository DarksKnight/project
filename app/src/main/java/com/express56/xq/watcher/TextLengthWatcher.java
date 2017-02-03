package com.express56.xq.watcher;

import android.content.Context;
import android.text.Editable;
import android.text.Selection;
import android.text.TextWatcher;
import android.widget.EditText;

import com.express56.xq.R;
import com.express56.xq.util.StringUtils;
import com.express56.xq.widget.ToastUtil;

/**
 * Created by SEELE on 2016/10/6.
 */
public class TextLengthWatcher implements TextWatcher {

    private int maxLen = 0;
    private EditText editText = null;
    private Context context = null;


    public TextLengthWatcher(int maxLen, EditText editText, Context context) {
        this.maxLen = maxLen;
        this.editText = editText;
        this.context = context;

    }

    public void afterTextChanged(Editable arg0) {
        // TODO Auto-generated method stub

    }

    public void beforeTextChanged(CharSequence arg0, int arg1, int arg2,
                                  int arg3) {
        // TODO Auto-generated method stub

    }

    public void onTextChanged(CharSequence arg0, int arg1, int arg2, int arg3) {
        // TODO Auto-generated method stub
        Editable editable = editText.getText();
        int len = editable.length();
        if (StringUtils.getCharacterNum(editable.toString()) > maxLen) {
            ToastUtil.showMessage(context, context.getString(R.string.str_authentication_info_label_nickname_length_prompt));
            int selEndIndex = Selection.getSelectionEnd(editable);
            String str = editable.toString();
            //截取新字符串
            String newStr = str.substring(0, editable.length() - 1);
            editText.setText(newStr);
            editable = editText.getText();

            //新字符串的长度
            int newLen = editable.length();
            //旧光标位置超过字符串长度
            if(selEndIndex > newLen)
            {
                selEndIndex = editable.length();
            }
            //设置新光标所在的位置
            Selection.setSelection(editable, selEndIndex);
        }

    }

}