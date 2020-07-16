package com.yiliao.chat.util;

import android.content.Context;
import android.content.res.AssetManager;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

/*
 * Copyright (C) 2018
 * 版权所有
 *
 * 功能描述：本地json 的 util
 * 作者：
 * 创建时间：2018/7/3
 *
 * 修改人：
 * 修改描述：
 * 修改日期
 */
public class JsonUtil {

    /**
     * 得到json文件中的内容
     */
    public static String getJson(Context context, String fileName) {
        StringBuilder stringBuilder = new StringBuilder();
        //获得assets资源管理器
        AssetManager assetManager = context.getAssets();
        //使用IO流读取json文件内容
        try {
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(
                    assetManager.open(fileName), "utf-8"));
            String line;
            while ((line = bufferedReader.readLine()) != null) {
                stringBuilder.append(line);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return stringBuilder.toString();
    }

    /**
     * 转换成对象
     */

}
