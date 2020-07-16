package com.yiliao.chat.activity;

import android.app.job.JobInfo;
import android.app.job.JobScheduler;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;

import com.wushuangtech.wstechapi.TTTRtcEngine;
import com.yiliao.chat.BuildConfig;
import com.yiliao.chat.R;
import com.yiliao.chat.base.AppManager;
import com.yiliao.chat.base.BaseActivity;
import com.yiliao.chat.base.BaseResponse;
import com.yiliao.chat.bean.ChatUserInfo;
import com.yiliao.chat.bean.LoginTypeBean;
import com.yiliao.chat.bean.UserCenterBean;
import com.yiliao.chat.constant.ChatApi;
import com.yiliao.chat.helper.SharedPreferenceHelper;
import com.yiliao.chat.net.AjaxCallback;
import com.yiliao.chat.net.NetCode;
import com.yiliao.chat.socket.ConnectService;
import com.yiliao.chat.socket.WakeupService;
import com.yiliao.chat.util.DevicesUtil;
import com.yiliao.chat.util.LogUtil;
import com.yiliao.chat.util.ParamUtil;
import com.yiliao.chat.util.SystemUtil;
import com.yiliao.chat.util.ToastUtil;
import com.zhy.http.okhttp.OkHttpUtils;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.util.HashMap;
import java.util.Map;

import cn.jpush.android.api.JPushInterface;
import cn.jpush.im.android.api.JMessageClient;
import cn.jpush.im.android.api.model.UserInfo;
import cn.jpush.im.api.BasicCallback;
import okhttp3.Call;

import static com.wushuangtech.library.Constants.CHANNEL_PROFILE_LIVE_BROADCASTING;

/*
 * Copyright (C) 2018
 * 版权所有
 *
 * 功能描述：启动页面
 * 作者：
 * 创建时间：2018/6/14
 *
 * 修改人：
 * 修改描述：
 * 修改日期
 */
public class SplashActivity extends BaseActivity {

    private boolean mHasLightSensor = true;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //在小米手机上 判断是否导航页被销毁，没有销毁直接跳过
        if ((getIntent().getFlags() & Intent.FLAG_ACTIVITY_BROUGHT_TO_FRONT) != 0) {
            //结束你的activity
            finish();
            return;
        }
        Log.i("aaa", "MD5:" + getSignMd5Str());
        Log.i("aaa", "sha1:" + getAppSignSha1(getApplicationContext()));
    }

    /**
     * MD5加密
     *
     * @param byteStr 需要加密的内容
     * @return 返回 byteStr的md5值
     */
    public static String encryptionMD5(byte[] byteStr) {
        MessageDigest messageDigest = null;
        StringBuffer md5StrBuff = new StringBuffer();
        try {
            messageDigest = MessageDigest.getInstance("MD5");
            messageDigest.reset();
            messageDigest.update(byteStr);
            byte[] byteArray = messageDigest.digest();
//            return Base64.encodeToString(byteArray,Base64.NO_WRAP);
            for (int i = 0; i < byteArray.length; i++) {
                if (Integer.toHexString(0xFF & byteArray[i]).length() == 1) {
                    md5StrBuff.append("0").append(Integer.toHexString(0xFF & byteArray[i]));
                } else {
                    md5StrBuff.append(Integer.toHexString(0xFF & byteArray[i]));
                }
            }
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return md5StrBuff.toString();
    }

    /**
     * 获取app签名md5值,与“keytool -list -keystore D:\Desktop\app_key”‘keytool -printcert     *file D:\Desktop\CERT.RSA’获取的md5值一样
     */
    public String getSignMd5Str() {
        try {
            PackageInfo packageInfo = getPackageManager().getPackageInfo(
                    getPackageName(), PackageManager.GET_SIGNATURES);
            Signature[] signs = packageInfo.signatures;
            Signature sign = signs[0];
            String signStr = encryptionMD5(sign.toByteArray());
            return signStr;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return "";
    }

    /**
     * 获得app 的sha1值 *
     *
     * @param context
     * @return
     */
    public static String getAppSignSha1(Context context) {
        try {
            PackageManager packageManager = context.getPackageManager();
            PackageInfo packageInfo = packageManager.getPackageInfo(context.getPackageName(), PackageManager.GET_SIGNATURES);
// X509证书，X.509是一种非常通用的证书格式
            Signature[] signs = packageInfo.signatures;
            Signature sign = signs[0];
            CertificateFactory certFactory = CertificateFactory.getInstance("X.509");
            X509Certificate cert = (X509Certificate) certFactory.generateCertificate(new ByteArrayInputStream(sign.toByteArray())); // md5
            MessageDigest md = MessageDigest.getInstance("SHA1");
            // 获得公钥
            byte[] b = md.digest(cert.getEncoded());
            return byte2HexFormatted(b);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }


    /**
     * 将获取到得编码进行16进制转换
     *
     * @param arr
     * @return
     */
    private static String byte2HexFormatted(byte[] arr) {
        StringBuilder str = new StringBuilder(arr.length * 2);
        for (int i = 0; i < arr.length; i++) {
            String h = Integer.toHexString(arr[i]);
            int l = h.length();
            if (l == 1)
                h = "0" + h;
            if (l > 2)
                h = h.substring(l - 2, l);
            str.append(h.toUpperCase());
            if (i < (arr.length - 1))
                str.append(':');
        }
        return str.toString();
    }

    @Override
    protected View getContentView() {

        return inflate(R.layout.activity_splash_layout);
    }

    @Override
    protected boolean supportFullScreen() {
        return true;
    }

    @Override
    protected void onContentAdded() {
        needHeader(false);
        String deviceNumber = SystemUtil.getOnlyOneId(getApplicationContext());
        LogUtil.i("设备标识: " + deviceNumber);
        checkEme();
        initTIM();
        loginSocket(SharedPreferenceHelper.getAccountInfo(getApplicationContext()));
        getLoginType();

    }

    /**
     * 检测模拟器
     */
    private void checkEme() {
        if (DevicesUtil.notHasLightSensorManager(getApplicationContext())) {
            mHasLightSensor = false;
        }
    }

    /**
     * 开启服务并登陆socket
     */
    private void loginSocket(ChatUserInfo chatUserInfo) {
        if (chatUserInfo != null && chatUserInfo.t_id > 0) {
            if (chatUserInfo.t_sex ==0||chatUserInfo.t_sex ==1) {//有性别的话 就直接登录socket
                Intent intent = new Intent(getApplicationContext(), ConnectService.class);
                startService(intent);

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    JobScheduler jobScheduler = (JobScheduler) getSystemService(JOB_SCHEDULER_SERVICE);
                    JobInfo jobInfo = new JobInfo.Builder(1, new ComponentName(getPackageName(),
                            WakeupService.class.getName()))
                            .setPeriodic(5 * 60 * 1000L)
                            .setRequiredNetworkType(JobInfo.NETWORK_TYPE_ANY)
                            .build();
                    if (jobScheduler != null) {
                        jobScheduler.schedule(jobInfo);
                    }
                }
            }

            //登录Im
            loginJIM(chatUserInfo);

            //极光
            if (JPushInterface.isPushStopped(getApplicationContext())) {
                JPushInterface.resumePush(getApplicationContext());
            }

            //获取个人信息
            getInfo(chatUserInfo.t_id);

            //更新登录时间
            updateLoginTime(chatUserInfo.t_id);

            goNext();
        } else {

            goNext();
        }
    }

    /**
     * 获取个人中心信息
     */
    private void getInfo(final int userId) {
        Map<String, String> paramMap = new HashMap<>();
        paramMap.put("userId", String.valueOf(userId));
        OkHttpUtils.post().url(ChatApi.INDEX)
                .addParams("param", ParamUtil.getParam(paramMap))
                .build().execute(new AjaxCallback<BaseResponse<UserCenterBean>>() {
            @Override
            public void onResponse(BaseResponse<UserCenterBean> response, int id) {
                if (response != null && response.m_istatus == NetCode.SUCCESS) {
                    UserCenterBean bean = response.m_object;
                    if (bean != null) {
                        ChatUserInfo info = new ChatUserInfo();
                        info.t_id = userId;
                        info.nickName = bean.nickName;
                        info.headUrl = bean.handImg;
                        info.gold = bean.amount;
                        info.t_is_vip = bean.t_is_vip;
                        info.t_role = bean.t_role;
                        info.t_sex = bean.t_sex;
                        info.t_token = bean.t_token;
                        AppManager.getInstance().setUserInfo(info);
                        //保存
                        SharedPreferenceHelper.saveUserVip(getApplicationContext(), bean.t_is_vip);
                        SharedPreferenceHelper.saveUserExtreme(getApplicationContext(), bean.t_is_extreme);
                        SharedPreferenceHelper.saveRoleInfo(getApplicationContext(), bean.t_role);
                    }
                }
            }
        });
    }

    /**
     * 初始化IM
     */
    private void initTIM() {
        //三体
        TTTRtcEngine engine = TTTRtcEngine.create(getApplicationContext(), BuildConfig.tttAppId, true, null);
        engine.setChannelProfile(CHANNEL_PROFILE_LIVE_BROADCASTING);
    }

    /**
     * 登录Im
     */
    private void loginJIM(final ChatUserInfo chatUserInfo) {
        //检测账号是否登陆
        UserInfo myInfo = JMessageClient.getMyInfo();
        if (myInfo != null && !TextUtils.isEmpty(myInfo.getUserName())) {
            //登录
            JMessageClient.login(myInfo.getUserName(), String.valueOf(10000 + chatUserInfo.t_id),
                    new BasicCallback() {
                        @Override
                        public void gotResult(int i, String s) {
                            if (i == 0) {
                                LogUtil.i("极光im登录成功");
                            } else {
                                LogUtil.i("极光im登录失败:  " + i + "描述: " + s);
                            }
                        }
                    });
        } else {
            JMessageClient.register(String.valueOf(10000 + chatUserInfo.t_id), String.valueOf(10000 + chatUserInfo.t_id),
                    new BasicCallback() {
                        @Override
                        public void gotResult(int i, String s) {
                            if (i == 0 || i == 898001) {
                                LogUtil.i("极光注册成功");
                                //登录
                                JMessageClient.login(String.valueOf(10000 + chatUserInfo.t_id), String.valueOf(10000 + chatUserInfo.t_id),
                                        new BasicCallback() {
                                            @Override
                                            public void gotResult(int i, String s) {
                                                if (i == 0) {
                                                    LogUtil.i("极光im登录成功");
                                                }
                                            }
                                        });
                            }
                        }
                    });
        }
    }

    /**
     * 更新登录时间
     */
    private void updateLoginTime(int userId) {
        Map<String, String> paramMap = new HashMap<>();
        paramMap.put("userId", String.valueOf(userId));
        OkHttpUtils.post().url(ChatApi.UP_LOGIN_TIME)
                .addParams("param", ParamUtil.getParam(paramMap))
                .build().execute(new AjaxCallback<BaseResponse>() {
            @Override
            public void onResponse(BaseResponse response, int id) {
                if (response != null && response.m_istatus == NetCode.SUCCESS) {
                    LogUtil.i("更新登录时间成功");
                }
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        try {
            TTTRtcEngine.destroy();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 获取配制
     */
    private void getLoginType() {
        OkHttpUtils.post().url(ChatApi.GET_LOGIN_TYPE).build().execute(new AjaxCallback<BaseResponse<LoginTypeBean>>() {
            @Override
            public void onResponse(BaseResponse<LoginTypeBean> response, int id) {
                if (response != null) {
                    if (response.m_istatus == NetCode.SUCCESS) {
                        LoginTypeBean loginTypeBean = response.m_object;
                        if (loginTypeBean != null) {
                            SharedPreferenceHelper.saveLoginType(SplashActivity.this, loginTypeBean.t_login_type);
                            SharedPreferenceHelper.saveShareType(SplashActivity.this, loginTypeBean.t_share_type);
                            SharedPreferenceHelper.savePayType(SplashActivity.this, loginTypeBean.t_pay_type);
                        }
                    }
                }
            }

            @Override
            public void onError(Call call, Exception e, int id) {
                super.onError(call, e, id);

            }
        });
    }

    private void goNext() {
        final ChatUserInfo chatUserInfo = SharedPreferenceHelper.getAccountInfo(getApplicationContext());
        getWindow().getDecorView().postDelayed(new Runnable() {
            @Override
            public void run() {
                if (mHasLightSensor) {
                    if (chatUserInfo != null && chatUserInfo.t_id > 0) {
                        if (chatUserInfo.is_phone == 0) {
                            ToastUtil.show("您还未绑定手机号，请重新登录");
                            startActivity(new Intent(getApplicationContext(), ScrollLoginActivity.class));
                        } else {
                            if (chatUserInfo.t_sex == 0 || chatUserInfo.t_sex == 1) {//还没有选择性别
                                Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                                startActivity(intent);
                                finish();
                            } else {
                                Intent intent = new Intent(getApplicationContext(), ChooseGenderActivity.class);
                                intent.putExtra("userInfo", chatUserInfo);
                                startActivity(intent);
                            }
                        }
                    } else {
                        startActivity(new Intent(getApplicationContext(), ScrollLoginActivity.class));
                    }
                }
                finish();
            }
        }, 2000);
    }
}
