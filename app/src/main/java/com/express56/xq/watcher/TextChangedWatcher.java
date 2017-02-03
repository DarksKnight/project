package com.express56.xq.watcher;

import android.text.Editable;
import android.text.TextWatcher;
import android.widget.EditText;

import com.express56.xq.activity.InvokeStaticMethod;

/**
 * Created by SEELE on 2016/9/30.
 */
public class TextChangedWatcher implements TextWatcher
{
    private EditText et;

    public TextChangedWatcher(EditText et)
    {
        this.et = et;
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after)
    {

    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count)
    {
        //输入的类容
        CharSequence input = s.subSequence(start, start + count);
        // 退格
        if (count == 0) return;

        //如果 输入的类容包含有Emoji
        if (InvokeStaticMethod.isEmojiCharacter(input))
        {
            //那么就去掉
            et.setText(InvokeStaticMethod.removeEmoji(s));
        }

        //如果输入的字符超过最大限制,超出的部分 砍掉~
        if (s.length() > 3)
        {
            et.setText(s.subSequence(0, start));
        }
        //最后光标移动到最后 TODO 这里可能会有更好的解决方案
        et.setSelection(et.getText().toString().length());

    }

    @Override
    public void afterTextChanged(Editable s) {

    }
}