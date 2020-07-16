package com.yiliao.chat.util;


import android.annotation.TargetApi;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Build;
import android.text.TextUtils;
import android.util.DisplayMetrics;

import java.util.Locale;

/**
 * SharedPreferences工具类
 */
public final class SPUtils {

    private static SharedPreferences sp;
    private static SharedPreferences.Editor editor;
    /**
     * 保存在手机里面的文件名
     */
    private static final String FILE_NAME = "config";

    public static void init(Context context) {
        sp = context.getSharedPreferences(FILE_NAME, Context.MODE_PRIVATE);
        editor = sp.edit();
    }

    /**
     * 保存数据的方法
     *
     * @param key
     * @param value
     */
    public static void set(String key, Object value) {
        String type = value.getClass().getSimpleName();
        if ("String".equals(type)) {
            editor.putString(key, (String) value);
        } else if ("Integer".equals(type)) {
            editor.putInt(key, (Integer) value);
        } else if ("Boolean".equals(type)) {
            editor.putBoolean(key, (Boolean) value);
        } else if ("Float".equals(type)) {
            editor.putFloat(key, (Float) value);
        } else if ("Long".equals(type)) {
            editor.putLong(key, (Long) value);
        }
        editor.commit();
    }


    /**
     * 获取数据的方法
     *
     * @param key
     * @param defaultValue
     * @return
     */
    public static Object get(String key, Object defaultValue) {
        String type = defaultValue.getClass().getSimpleName();
        if ("String".equals(type)) {
            return sp.getString(key, (String) defaultValue);
        } else if ("Integer".equals(type)) {
            return sp.getInt(key, (Integer) defaultValue);
        } else if ("Boolean".equals(type)) {
            return sp.getBoolean(key, (Boolean) defaultValue);
        } else if ("Float".equals(type)) {
            return sp.getFloat(key, (Float) defaultValue);
        } else if ("Long".equals(type)) {
            return sp.getLong(key, (Long) defaultValue);
        }
        return null;
    }

    public static String getString(String key) {
        return sp.getString(key, "");
    }

    public static Boolean getBoolean(String key) {
        return sp.getBoolean(key, false);
    }

    public static void clear() {
        editor.clear();
        editor.commit();
    }

    public static Context selectLanguage(Context context, String language) {
        Context updateContext;
        //设置语言类型
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            updateContext = createConfigurationResources(context, language);
        } else {
            applyLanguage(context, language);
            updateContext = context;
        }
        //保存设置语言的类型
        set("language", language);
        return updateContext;
    }

    @TargetApi(Build.VERSION_CODES.N)
    private static Context createConfigurationResources(Context context, String language) {
        //设置语言类型
        Resources resources = context.getResources();
        Configuration configuration = resources.getConfiguration();
        Locale locale = null;
        switch (language) {
            case "en":
                locale = Locale.ENGLISH;
                break;

            case "zh":
                locale = Locale.SIMPLIFIED_CHINESE;
                break;

            case "tw":
                // 中文繁体
                locale = Locale.TRADITIONAL_CHINESE;
                break;

            case "ja":
                // 日语
                locale = Locale.JAPAN;
                break;

            default:
                locale = Locale.getDefault();
                break;
        }
        configuration.setLocale(locale);
        return context.createConfigurationContext(configuration);
    }

    public static Locale getLocal(Context context, String language){
        Locale locale = null;
        switch (language) {
            case "en":
                locale = Locale.ENGLISH;
                break;

            case "zh":
                locale = Locale.SIMPLIFIED_CHINESE;
                break;

            case "tw":
                // 中文繁体
                locale = Locale.TRADITIONAL_CHINESE;
                break;

            case "ja":
                // 日语
                locale = Locale.JAPAN;
                break;

            default:
                locale = Locale.getDefault();
                break;
        }
        return locale;
    };

    private static void applyLanguage(Context context, String language) {
        //设置语言类型
        Resources resources = context.getResources();
        Configuration configuration = resources.getConfiguration();
        DisplayMetrics displayMetrics = resources.getDisplayMetrics();
        Locale locale = null;
        switch (language) {
            case "en":
                locale = Locale.ENGLISH;
                break;

            case "zh":
                locale = Locale.SIMPLIFIED_CHINESE;
                break;

            case "tw":
                // 中文繁体
                locale = Locale.TRADITIONAL_CHINESE;
                break;

            case "ja":
                // 日语
                locale = Locale.JAPAN;
                break;

            default:
                locale = Locale.getDefault();
                break;
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            // apply locale
            configuration.setLocale(locale);
        } else {
            // updateConfiguration
            configuration.locale = locale;
            DisplayMetrics dm = resources.getDisplayMetrics();
            resources.updateConfiguration(configuration, dm);
        }
    }

    public static Context updateLanguage(Context context) {
        String curLanguage = getString("language");
        if (null == curLanguage || TextUtils.isEmpty(curLanguage)) {
            curLanguage = "zh";
        }
        return selectLanguage(context, curLanguage);
    }
}
