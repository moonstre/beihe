package com.yiliao.chat.util;

import android.content.Context;
import android.widget.Toast;

import com.yiliao.chat.base.AppManager;

/*
 * Copyright (C) 2018
 * 版权所有
 *
 * 功能描述：Toast工具类
 * 作者：
 * 创建时间：2018/6/14
 *
 * 修改人：
 * 修改描述：
 * 修改日期
 */
public class ToastUtil {

    public static void show(int res) {
        try {
            Toast.makeText(AppManager.getInstance(), AppManager.getInstance().getResources().getString(res), Toast.LENGTH_SHORT).show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void show(String text) {
        try {
            Toast.makeText(AppManager.getInstance(), text, Toast.LENGTH_SHORT).show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void showToast(Context context, String text) {
        try {
            Toast.makeText(context.getApplicationContext(), text, Toast.LENGTH_SHORT).show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void showToast(Context context, int resourceId) {
        try {
            Toast.makeText(context.getApplicationContext(), resourceId, Toast.LENGTH_SHORT).show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void showToastLong(Context context, int resourceId) {
        try {
            Toast.makeText(context.getApplicationContext(), resourceId, Toast.LENGTH_LONG).show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
