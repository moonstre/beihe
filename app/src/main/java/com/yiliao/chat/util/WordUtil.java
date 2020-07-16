package com.yiliao.chat.util;

import android.content.res.Resources;
import android.text.Html;
import android.text.Spanned;

import com.yiliao.chat.base.AppManager;

public class WordUtil {
    private static Resources sResources;

    static {
        sResources = AppManager.getInstance().getResources();
    }

    public static String getString(int res) {
        return sResources.getString(res);
    }

    /**
     * 为字符串添加颜色
     *
     * @param color #FFFFFF
     */
    public static String strAddColor(String str, String color) {
        return "<font color=\"" + color + "\">" + str + "</font>";
    }

    /**
     * 将String转化为Spanned对象
     */
    public static Spanned strToSpanned(String str) {
        return Html.fromHtml(str);
    }
}
