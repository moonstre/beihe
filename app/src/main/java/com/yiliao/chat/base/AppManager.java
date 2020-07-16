package com.yiliao.chat.base;

import android.app.ActivityManager;
import android.app.Application;
import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.support.multidex.MultiDex;

import com.fm.openinstall.OpenInstall;
import com.mob.MobSDK;
import com.tencent.bugly.crashreport.CrashReport;
import com.yiliao.chat.BuildConfig;
import com.yiliao.chat.bean.ChatUserInfo;
import com.yiliao.chat.constant.Constant;
import com.yiliao.chat.helper.SharedPreferenceHelper;
import com.yiliao.chat.util.SPUtils;
import com.yiliao.chat.util.SensitiveWordsUtil;

import cn.jpush.android.api.JPushInterface;
import cn.jpush.im.android.api.JMessageClient;
import cn.tillusory.sdk.TiSDK;

/*
 * Copyright (C) 2018
 * 版权所有
 *
 * 功能描述：自定义Application,用于初始化,储存一些全局变量,如UserInfo
 * 作者：
 * 创建时间：2018/6/27
 *
 * 修改人：
 * 修改描述：
 * 修改日期
 */
public class AppManager extends Application {

    private ChatUserInfo mUserInfo;
    private static AppManager mInstance;
    //判断微信支付是充值VIP 还是充值金币
    private boolean mIsWeChatForVip = false;
    //判断微信登录是绑定提现账号,还是登录页面的微信登录
    private boolean mIsWeChatBindAccount = false;
    //判断是首页微信分享还是
    private boolean mIsMainPageShareQun = false;
    //用于视频接通后提示用户
    public String mVideoStartHint = "";

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        MultiDex.install(base);
    }

    @Override
    public void onCreate() {
        super.onCreate();
        mInstance = this;
        MobSDK.init(this);
        //极光
        JPushInterface.setDebugMode(true);
        JPushInterface.init(this);
        JMessageClient.init(this, false);
        //离线鉴权初始化方法
        if (BuildConfig.beautySDK==1) {
            TiSDK.init(Constant.TI_KEY, this);
        }

        //初始化敏感字
        SensitiveWordsUtil.addData();


        //初始化腾讯bugly
        CrashReport.initCrashReport(this);
        CrashReport.setAppVersion(this, Constant.getVersion(this) + "_" + BuildConfig.FLAVOR + (BuildConfig.DEBUG ? "_debug" : ""));

        //OpenInstall 推广
        if(Constant.showOpeninstall()){
            if (isMainProcess()) {
                OpenInstall.init(this);
            }
        }

        SPUtils.init(this);
    }

    public ChatUserInfo getUserInfo() {
        if (mUserInfo != null) {
            return mUserInfo;
        } else {
            return SharedPreferenceHelper.getAccountInfo(getApplicationContext());
        }
    }

    public void setUserInfo(ChatUserInfo userInfo) {
        this.mUserInfo = userInfo;
    }

    public static AppManager getInstance() {
        return mInstance;
    }

    public void setIsWeChatForVip(boolean isWeChatForVip) {
        mIsWeChatForVip = isWeChatForVip;
    }

    public boolean getIsWeChatForVip() {
        return mIsWeChatForVip;
    }

    public void setIsWeChatBindAccount(boolean isWeChatBindAccount) {
        mIsWeChatBindAccount = isWeChatBindAccount;
    }

    public boolean getIsWeChatBindAccount() {
        return mIsWeChatBindAccount;
    }

    public boolean getIsMainPageShareQun() {
        return mIsMainPageShareQun;
    }

    public void setIsMainPageShareQun(boolean mIsMainPageShareQun) {
        this.mIsMainPageShareQun = mIsMainPageShareQun;
    }

    public boolean isMainProcess() {
        int pid = android.os.Process.myPid();
        ActivityManager activityManager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningAppProcessInfo appProcess : activityManager.getRunningAppProcesses()) {
            if (appProcess.pid == pid) {
                return getApplicationInfo().packageName.equals(appProcess.processName);
            }
        }
        return false;
    }
}
