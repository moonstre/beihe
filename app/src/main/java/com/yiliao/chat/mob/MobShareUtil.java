package com.yiliao.chat.mob;

import android.text.TextUtils;

import com.yiliao.chat.BuildConfig;
import com.yiliao.chat.R;
import com.yiliao.chat.base.AppManager;
import com.yiliao.chat.util.WordUtil;

import java.util.HashMap;

import cn.sharesdk.framework.Platform;
import cn.sharesdk.framework.PlatformActionListener;
import cn.sharesdk.onekeyshare.OnekeyShare;

/**
 * Created by cxf on 2017/8/29.
 * Mob 分享
 */

public class MobShareUtil {

    private PlatformActionListener mPlatformActionListener;
    private MobCallback mMobCallback;


    public MobShareUtil() {
        mPlatformActionListener = new PlatformActionListener() {

            @Override
            public void onComplete(Platform platform, int i, HashMap<String, Object> hashMap) {
                if (mMobCallback != null) {
                    mMobCallback.onSuccess(null);
                    mMobCallback.onFinish();
                    mMobCallback = null;
                }
            }

            @Override
            public void onError(Platform platform, int i, Throwable throwable) {
                if (mMobCallback != null) {
                    mMobCallback.onError();
                    mMobCallback.onFinish();
                    mMobCallback = null;
                }
            }

            @Override
            public void onCancel(Platform platform, int i) {
                if (mMobCallback != null) {
                    mMobCallback.onCancel();
                    mMobCallback.onFinish();
                    mMobCallback = null;
                }
            }
        };
    }

    public void execute(String platType, ShareData data, MobCallback callback) {
        if (TextUtils.isEmpty(platType) || data == null) {
            return;
        }
        String platName = MobConst.MAP.get(platType);
        if (TextUtils.isEmpty(platName)) {
            return;
        }
        mMobCallback = callback;
        OnekeyShare oks = new OnekeyShare();
        oks.disableSSOWhenAuthorize();//设置一个总开关，用于在分享前若需要授权，则禁用sso功能
        oks.setPlatform(platName);
        oks.setSilent(true);//是否直接分享
        if (!TextUtils.isEmpty(data.getImgPath())) {
            oks.setImagePath(data.getImgPath());
        } else {
            oks.setSite(WordUtil.getString(R.string.app_name));//site是分享此内容的网站名称，仅在QQ空间使用，否则可以不提供
            oks.setSiteUrl(BuildConfig.hostAddress);//siteUrl是分享此内容的网站地址，仅在QQ空间使用，否则可以不提供
            oks.setTitle(data.getTitle());
            oks.setText(data.getDes());
            if (data.getImgData() != null) {
                oks.setImageData(data.getImgData());
            } else {
                oks.setImageUrl(data.getImgUrl());
            }
            String webUrl = data.getWebUrl();
            oks.setUrl(webUrl);
            oks.setSiteUrl(webUrl);
            oks.setTitleUrl(webUrl);
        }
        oks.setCallback(mPlatformActionListener);
        oks.show(AppManager.getInstance());
    }

    public void release() {
        mMobCallback = null;
    }

}
