package com.yiliao.chat.activity;

import android.app.job.JobInfo;
import android.app.job.JobScheduler;
import android.content.ComponentName;
import android.content.Intent;
import android.os.Build;
import android.text.InputFilter;
import android.text.InputType;
import android.text.TextUtils;
import android.text.method.DigitsKeyListener;
import android.view.KeyEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.yiliao.chat.R;
import com.yiliao.chat.base.AppManager;
import com.yiliao.chat.base.BaseActivity;
import com.yiliao.chat.base.BaseResponse;
import com.yiliao.chat.bean.ChatUserInfo;
import com.yiliao.chat.constant.ChatApi;
import com.yiliao.chat.constant.Constant;
import com.yiliao.chat.event.LoginEvent;
import com.yiliao.chat.helper.SharedPreferenceHelper;
import com.yiliao.chat.net.AjaxCallback;
import com.yiliao.chat.net.NetCode;
import com.yiliao.chat.socket.ConnectService;
import com.yiliao.chat.socket.WakeupService;
import com.yiliao.chat.util.LogUtil;
import com.yiliao.chat.util.ParamUtil;
import com.yiliao.chat.util.ToastUtil;
import com.zhy.http.okhttp.OkHttpUtils;

import org.greenrobot.eventbus.EventBus;

import java.util.HashMap;
import java.util.Map;

import butterknife.BindView;
import butterknife.OnClick;
import cn.jpush.android.api.JPushInterface;
import cn.jpush.im.android.api.JMessageClient;
import cn.jpush.im.api.BasicCallback;
import okhttp3.Call;
import okhttp3.Request;

/*
 * Copyright (C) 2018
 * 版权所有
 *
 * 功能描述：登录页面
 * 作者：
 * 创建时间：2018/6/22
 *
 * 修改人：
 * 修改描述：
 * 修改日期
 */
public class PhoneLoginActivity extends BaseActivity {
    @BindView(R.id.mobile_et)
    EditText mMobileEt;
    @BindView(R.id.code_et)
    EditText mCodeEt;

    @Override
    protected View getContentView() {
        return inflate(R.layout.activity_login_layout);
    }

    @Override
    protected void onContentAdded() {
        needHeader(false);
        mCodeEt.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if (keyCode == KeyEvent.KEYCODE_ENTER && event.getAction() == KeyEvent.ACTION_DOWN) {
                    requestAccountLogin();
                    return true;
                }
                return false;
            }
        });

        if (SharedPreferenceHelper.getLoginTypeEmail(PhoneLoginActivity.this)) {
            mMobileEt.setFilters(new InputFilter[]{new InputFilter.LengthFilter(50)});
            mMobileEt.setHint(R.string.please_input_phone_login_or_email);
        } else {
            mMobileEt.setKeyListener(DigitsKeyListener.getInstance("0123456789"));
            mMobileEt.setInputType(InputType.TYPE_CLASS_NUMBER);
        }
    }

    @Override
    protected boolean supportFullScreen() {
        return true;
    }

    @OnClick({R.id.ivLoginClose, R.id.login_tv, R.id.forget_tv})
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.ivLoginClose:
                finish();
                break;
            case R.id.login_tv: {//登录
                requestAccountLogin();
                break;
            }
            case R.id.forget_tv: {//忘记密码
                int joinTypeForget = 1;//忘记密码
                Intent intent = new Intent(getApplicationContext(), RegisterActivity.class);
                intent.putExtra(Constant.JOIN_TYPE, joinTypeForget);
                startActivity(intent);
//                finish();
                break;
            }
        }
    }

    /**
     * 账号密码登录
     */
    private void requestAccountLogin() {
        final String phone = mMobileEt.getText().toString().trim();
        if (TextUtils.isEmpty(phone)) {
            if (SharedPreferenceHelper.getLoginTypeEmail(PhoneLoginActivity.this)) {
                ToastUtil.showToast(getApplicationContext(), R.string.phone_number_or_email_null);
            } else {
                ToastUtil.showToast(getApplicationContext(), R.string.phone_number_null);
            }
            return;
        }
        //密码
        String password = mCodeEt.getText().toString().trim();
        if (TextUtils.isEmpty(password)) {
            ToastUtil.showToast(getApplicationContext(), R.string.please_input_password);
            return;
        }
        if (password.length() < 6 || password.length() > 16) {
            ToastUtil.showToast(getApplicationContext(), R.string.length_wrong);
            return;
        }

        Map<String, String> paramMap = new HashMap<>();
        paramMap.put("phone", phone);
        paramMap.put("password", password);
        OkHttpUtils.post().url(ChatApi.USER_LOGIN)
                .addParams("param", ParamUtil.getParam(paramMap))
                .build().execute(new AjaxCallback<BaseResponse<ChatUserInfo>>() {
            @Override
            public void onResponse(BaseResponse<ChatUserInfo> response, int id) {
                dismissLoadingDialog();
                if (response != null) {
                    if (response.m_istatus == NetCode.SUCCESS) {
                        ChatUserInfo userInfo = response.m_object;
                        if (userInfo != null) {
                            userInfo.phone = phone;
                            userInfo.is_phone = 1;
                            userInfo.logintype = "phone";
                            AppManager.getInstance().setUserInfo(userInfo);
                            SharedPreferenceHelper.saveAccountInfo(getApplicationContext(), userInfo);

                            ToastUtil.showToast(getApplicationContext(), R.string.login_success);
                            if (userInfo.t_sex == 0||userInfo.t_sex==1) {

                                loginSocket(userInfo);
                            } else {
                                Intent intent = new Intent(getApplicationContext(), ChooseGenderActivity.class);
                                intent.putExtra("userInfo", userInfo);
                                startActivity(intent);
                                EventBus.getDefault().post(new LoginEvent());
                                finish();

                            }

                        } else {
                            if (!TextUtils.isEmpty(response.m_strMessage)) {
                                ToastUtil.showToast(getApplicationContext(), response.m_strMessage);
                            } else {
                                ToastUtil.showToast(getApplicationContext(), R.string.login_fail);
                            }
                        }
                    } else if (response.m_istatus == -1) {//被封号
                        String message = response.m_strMessage;
                        if (!TextUtils.isEmpty(message)) {
                            ToastUtil.showToast(getApplicationContext(), message);
                        } else {
                            ToastUtil.showToast(getApplicationContext(), R.string.login_fail);
                        }
                    } else if (response.m_istatus == -200) {//7天内已经登陆过其他账号
                        ToastUtil.showToast(getApplicationContext(), R.string.seven_days);
                    } else {
                        if (!TextUtils.isEmpty(response.m_strMessage)) {
                            ToastUtil.showToast(getApplicationContext(), response.m_strMessage);
                        } else {
                            ToastUtil.showToast(getApplicationContext(), R.string.login_fail);
                        }
                    }
                } else {
                    ToastUtil.showToast(getApplicationContext(), R.string.login_fail);
                }
            }

            @Override
            public void onBefore(Request request, int id) {
                super.onBefore(request, id);
                showLoadingDialog();
            }

            @Override
            public void onError(Call call, Exception e, int id) {
                super.onError(call, e, id);
                dismissLoadingDialog();
            }
        });

    }

    /**
     * 开启服务并登陆
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
            loginJIM(chatUserInfo);

            //极光
            if (JPushInterface.isPushStopped(getApplicationContext())) {
                JPushInterface.resumePush(getApplicationContext());
            }
        }
    }

    /**
     * 登录Im
     */
    private void loginJIM(final ChatUserInfo chatUserInfo) {
        //检测账号是否登陆
        //登录
        JMessageClient.login(String.valueOf(10000 + chatUserInfo.t_id), String.valueOf(10000 + chatUserInfo.t_id),
                new BasicCallback() {
                    @Override
                    public void gotResult(int i, String s) {
                        if (i == 0) {
                            LogUtil.i("极光im登录成功");
                            Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                            startActivity(intent);
                            EventBus.getDefault().post(new LoginEvent());
                            finish();
                        }else if (i == 801003) {
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
                                                                    Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                                                                    startActivity(intent);
                                                                    EventBus.getDefault().post(new LoginEvent());
                                                                    finish();
                                                                }
                                                            }
                                                        });
                                            }
                                        }
                                    });

                        } else {
                            LogUtil.i("极光im登录失败:  " + i + "描述: " + s);
                        }

                    }
                });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

}
