package com.express56.xq.util;


import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * StringUtils
 * 字符串工具类
 */
public class StringUtils {

    /**
     * 剔除字符串中的所有空格
     */
    public static String replaceBlank(String str) {
        Pattern p = Pattern.compile("//s*|/t|/r|/n");
//		String str="I am a, I am Hello ok, /n new line ffdsa!";
//        System.out.println("before:" + str);
        Matcher m = p.matcher(str);
        String after = m.replaceAll("");
//        System.out.println("after:" + after);
        return after;
    }

    /**
     * 判断字符串是否有值
     *
     * @param str
     * @return
     */
    public static boolean notEmpty(String str) {
        if (str == null || str.trim().equals("")) {
            return false;
        }
        return true;
    }

    /**
     * 判断字符串是否有值
     *
     * @param str
     * @return
     */
    public static boolean isEmpty(String str) {
        if (str == null || str.trim().equals("")) {
            return true;
        }
        return false;
    }

    /**
     * 通过标签解析字符串 (比如：<tag>values</tag>)
     *
     * @param src
     * @param tag
     * @return
     */
    public static String parseString(String src, String tag) {
        String start = "<" + tag + ">";
        String end = "</" + tag + ">";
        String values = "";
        try {
            values = src.substring(src.indexOf(start) + start.length(), src.indexOf(end));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return values;
    }

    /**
     * 返回字符串里中文字或者全角字符的个数
     *
     * @param s
     * @return
     */
    public static int getChineseNum(String s) {

        int num = 0;
        char[] myChar = s.toCharArray();
        for (int i = 0; i < myChar.length; i++) {
            if ((char) (byte) myChar[i] != myChar[i]) {
                num++;
            }
        }
        return num;
    }


    /**
     * @param content
     * @return
     * @method getCharacterNum
     * @description 获取一段字符串的字符个数（包含中英文，一个中文算2个字符）
     * @author huanggf
     * @since Nov 15, 2011
     */
    public static int getCharacterNum(final String content) {
        if (null == content || "".equals(content)) {
            return 0;
        } else {
            return (content.length() + getChineseNum(content));
        }
    }


    /**
     * @param content
     * @return
     * @method splitWithComma
     * @description 把一个用空格、逗号分隔的字符串统一换成英文的逗号分隔（包含去掉空白多余的分隔）
     * @author huanggf
     * @since Nov 16, 2011
     */
    public static String splitWithComma(String content) {
        if (content == null || "".equals(content)) {
            return null;
        }
        LogUtil.d("StringUtils", "原始字符:= " + content);
        // 把“，”换成“ ”
        content = content.replace("，", " ");
        LogUtil.d("StringUtils", "把“，”换成“ ”content=" + content);
        // 把","换成“ ”
        content = content.replace(",", " ");
        LogUtil.d("StringUtils", "把“,”换成“ ”content=" + content);

        String temp_content = "";
        // 去掉多余空格
        String[] v1 = content.split(" ");
        for (int i = 0; i < v1.length; i++) {
            if (!"".equals(v1[i])) {
                if ("".equals(temp_content)) {
                    temp_content = temp_content + v1[i];
                } else {
                    temp_content = temp_content + " " + v1[i];
                }
            }
        }
        LogUtil.d("StringUtils", "去掉多余空格temp_content=" + temp_content);
        // 把空格换成“,”
        temp_content = temp_content.replace(" ", ",");
        LogUtil.d("StringUtils", "把空格换成“,”temp_content=" + temp_content);
        return temp_content;
    }

    /**
     * @param size
     * @return
     * @method isSplitedPartLongerThan
     * @description 用英文逗号分割之后的每部分字符的个数是否大于某个值
     * @author huanggf
     * @since Nov 15, 2011
     */
    public static boolean isSplitedPartLongerThan(final String s, final int size) {
        if (s == null || "".equals(s)) {
            return false;
        }
        LogUtil.d("StringUtils", "原始字符:= " + s);
        // 按照英文“,”来分割，看是否有大于8个字符的
        String[] v = s.split(",");
        for (int k = 0; k < v.length; k++) {
            LogUtil.d("StringUtils", "v[" + k + "]= " + v[k] + "  >>>>length= " + getCharacterNum(v[k]));
            if (getCharacterNum(v[k]) > size) {
                return true;
            }
        }
        return false;
    }

}
