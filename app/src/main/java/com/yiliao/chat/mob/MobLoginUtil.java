package com.yiliao.chat.mob;

import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;

import com.yiliao.chat.R;
import com.yiliao.chat.util.ToastUtil;

import java.util.HashMap;

import cn.sharesdk.facebook.Facebook;
import cn.sharesdk.framework.Platform;
import cn.sharesdk.framework.PlatformActionListener;
import cn.sharesdk.framework.PlatformDb;
import cn.sharesdk.framework.ShareSDK;
import cn.sharesdk.tencent.qq.QQ;
import cn.sharesdk.wechat.friends.Wechat;

/**
 * Created by cxf on 2017/8/29.
 * mob登录
 */

public class MobLoginUtil {

    private static final int CODE_SUCCESS = 200;//成功
    private static final int CODE_ERROR = 300;//失败
    private static final int CODE_CANCEL = 400;//取消

    private PlatformActionListener mPlatformActionListener;
    private Handler mHandler;
    private MobCallback mMobCallback;


    public MobLoginUtil() {
        mPlatformActionListener = new PlatformActionListener() {

            @Override
            public void onComplete(Platform platform, int i, HashMap<String, Object> hashMap) {
                if (mHandler != null) {
                    Message msg = Message.obtain();
                    msg.what = CODE_SUCCESS;
                    msg.obj = platform;
                    mHandler.sendMessage(msg);
                }
            }

            @Override
            public void onError(Platform platform, int i, Throwable throwable) {
                if (mHandler != null) {
                    mHandler.sendEmptyMessage(CODE_ERROR);
                }
            }

            @Override
            public void onCancel(Platform platform, int i) {
                if (mHandler != null) {
                    mHandler.sendEmptyMessage(CODE_CANCEL);
                }
            }
        };
        mHandler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                if (mMobCallback == null) {
                    return;
                }
                switch (msg.what) {
                    case CODE_SUCCESS:
                        Platform platform = (Platform) msg.obj;
                        if (platform == null) {
                            return;
                        }
                        PlatformDb platDB = platform.getDb();
                        if (platDB == null) {
                            return;
                        }
                        final LoginData data = new LoginData();
                        data.setNickName(platDB.getUserName());
                        data.setAvatar(platDB.getUserIcon());
                        String platformName = platDB.getPlatformNname();
                        if (platformName.equals(Facebook.NAME)) {
                            data.setOpenID(platDB.getUserId());
                            data.setType(MobConst.Type.FACEBOOK);
                        } else if (platformName.equals(Wechat.NAME)) {
                            data.setOpenID(platDB.get("unionid"));
                            data.setType(MobConst.Type.WX);
                        } else if (platformName.equals(QQ.NAME)) {
                            data.setType(MobConst.Type.QQ);
//                            if (AppConfig.QQ_LOGIN_WITH_PC) {
//                                HttpUtil.getQQLoginUnionID(platDB.getToken(), new CommonCallback<String>() {
//                                    @Override
//                                    public void callback(String unionID) {
//                                        data.setOpenID(unionID);
//                                        mMobCallback.onSuccess(data);
//                                        ToastUtil.show(R.string.login_auth_success);
//                                        mMobCallback.onFinish();
//                                    }
//                                });
//                                return;
//                            } else {
                                data.setOpenID(platDB.getUserId());
//                            }
                        }
                        mMobCallback.onSuccess(data);
                        ToastUtil.show(R.string.login_auth_success);
                        break;
                    case CODE_ERROR:
                        mMobCallback.onError();
                        ToastUtil.show(R.string.login_auth_failure);
                        break;
                    case CODE_CANCEL:
                        mMobCallback.onCancel();
                        ToastUtil.show(R.string.login_auth_cancle);
                        break;
                }
                mMobCallback.onFinish();
                mMobCallback = null;
            }
        };
    }

    public void execute(String platType, MobCallback callback) {
        if (TextUtils.isEmpty(platType) || callback == null) {
            return;
        }
        String platName = MobConst.MAP.get(platType);
        if (TextUtils.isEmpty(platName)) {
            return;
        }
        mMobCallback = callback;
        try {
            Platform platform = ShareSDK.getPlatform(platName);
            platform.setPlatformActionListener(mPlatformActionListener);
            platform.SSOSetting(false);
            platform.removeAccount(true);
            platform.showUser(null);
        } catch (Exception e) {
            e.printStackTrace();
            callback.onError();
        }
    }


    public void release() {
        mMobCallback = null;
        if (mHandler != null) {
            mHandler.removeCallbacksAndMessages(null);
            mHandler = null;
        }
    }

}
