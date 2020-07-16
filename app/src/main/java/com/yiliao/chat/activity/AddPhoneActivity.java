package com.yiliao.chat.activity;

import android.app.job.JobInfo;
import android.app.job.JobScheduler;
import android.content.ComponentName;
import android.content.Intent;
import android.os.Build;
import android.os.CountDownTimer;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.yiliao.chat.R;
import com.yiliao.chat.base.BaseActivity;
import com.yiliao.chat.base.BaseResponse;
import com.yiliao.chat.bean.ChatUserInfo;
import com.yiliao.chat.constant.ChatApi;
import com.yiliao.chat.constant.Constant;
import com.yiliao.chat.helper.SharedPreferenceHelper;
import com.yiliao.chat.net.AjaxCallback;
import com.yiliao.chat.net.NetCode;
import com.yiliao.chat.socket.ConnectService;
import com.yiliao.chat.socket.WakeupService;
import com.yiliao.chat.util.LogUtil;
import com.yiliao.chat.util.ParamUtil;
import com.yiliao.chat.util.ToastUtil;
import com.yiliao.chat.util.VerifyUtils;
import com.zhy.http.okhttp.OkHttpUtils;

import java.util.HashMap;
import java.util.Map;

import butterknife.BindView;
import butterknife.OnClick;
import cn.jpush.android.api.JPushInterface;
import cn.jpush.im.android.api.JMessageClient;
import cn.jpush.im.api.BasicCallback;
import okhttp3.Call;
import okhttp3.Request;

public class AddPhoneActivity extends BaseActivity {
    @BindView(R.id.ivLoginClose)
    ImageView ivLoginClose;
    @BindView(R.id.tvRegisterTitle)
    TextView tvRegisterTitle;
    //获取验证码
    @BindView(R.id.send_verify_tv)
    TextView mSendVerifyTv;
    @BindView(R.id.mobile_et)
    EditText mMobileEt;
    @BindView(R.id.code_et)
    EditText mCodeEt;

    private CountDownTimer mCountDownTimer;
    private String nickName;
    private String avatar;
    private boolean phoneUpdate;

    @Override
    protected boolean supportFullScreen() {
        return true;
    }

    @Override
    protected View getContentView() {
        return inflate(R.layout.activity_add_phone);
    }

    @Override
    protected void onContentAdded() {
        needHeader(false);

        phoneUpdate = getIntent().getBooleanExtra(Constant.USER_PHONE_UPDATE, false);
        nickName = getIntent().getStringExtra(Constant.NICK_NAME);
        avatar = getIntent().getStringExtra(Constant.MINE_HEAD_URL);

        if (phoneUpdate) {
            ivLoginClose.setVisibility(View.VISIBLE);
        }
    }

    @OnClick({R.id.ivLoginClose, R.id.send_verify_tv, R.id.confirm_tv})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.ivLoginClose:
                finish();
                break;
            case R.id.send_verify_tv: {//获取验证码
                sendSmsVerifyCode();
                break;
            }
            case R.id.confirm_tv: {//确认
                bindPhone();
                break;
            }
        }
    }

    /**
     * 发送短信验证码
     */
    private void sendSmsVerifyCode() {
        String phone = mMobileEt.getText().toString().trim();
        if (TextUtils.isEmpty(phone)) {
            ToastUtil.showToast(getApplicationContext(), R.string.phone_number_null);
            return;
        }
        if (!VerifyUtils.isPhoneNum(phone)) {
            ToastUtil.showToast(getApplicationContext(), R.string.wrong_phone_number);
            return;
        }

        if (VerifyUtils.isPhoneNum(phone)) {
            Map<String, String> paramMap = new HashMap<>();
            paramMap.put("phone", phone);
            paramMap.put("resType", "1");
            OkHttpUtils.post().url(ChatApi.SEND_SMS_CODE)
                    .addParams("param", ParamUtil.getParam(paramMap))
                    .build().execute(new AjaxCallback<BaseResponse>() {
                @Override
                public void onResponse(BaseResponse response, int id) {
                    LogUtil.i("获取短信验证码==--", JSON.toJSONString(response));
                    dismissLoadingDialog();
                    if (response != null && response.m_istatus == NetCode.SUCCESS) {
                        String message = response.m_strMessage;
                        if (!TextUtils.isEmpty(message) && message.contains(getResources().getString(R.string.send_success))) {
                            ToastUtil.showToast(getApplicationContext(), R.string.send_success_des);
                            startCountDown();
                        }
                    } else if (response != null && response.m_istatus == NetCode.FAIL) {
                        String message = response.m_strMessage;
                        if (!TextUtils.isEmpty(message)) {
                            ToastUtil.showToast(getApplicationContext(), message);
                        } else {
                            ToastUtil.showToast(getApplicationContext(), R.string.send_code_fail);
                        }
                    } else {
                        ToastUtil.showToast(getApplicationContext(), R.string.send_code_fail);
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
                    ToastUtil.showToast(getApplicationContext(), R.string.system_error);
                }
            });
        } else {
            Map<String, String> paramMap = new HashMap<>();
            paramMap.put("email", phone);
            paramMap.put("resType", "1");
            OkHttpUtils.post().url(ChatApi.SEND_EMAIL_CODE)
                    .addParams("param", ParamUtil.getParam(paramMap))
                    .build().execute(new AjaxCallback<BaseResponse>() {
                @Override
                public void onResponse(BaseResponse response, int id) {
                    LogUtil.i("获取短信验证码==--", JSON.toJSONString(response));
                    dismissLoadingDialog();
                    if (response != null && response.m_istatus == NetCode.SUCCESS) {
                        String message = response.m_strMessage;
                        if (!TextUtils.isEmpty(message) && message.contains(getResources().getString(R.string.send_success))) {
                            ToastUtil.showToast(getApplicationContext(), R.string.send_success_des);
                            startCountDown();
                        }
                    } else if (response != null && response.m_istatus == NetCode.FAIL) {
                        String message = response.m_strMessage;
                        if (!TextUtils.isEmpty(message)) {
                            ToastUtil.showToast(getApplicationContext(), message);
                        } else {
                            ToastUtil.showToast(getApplicationContext(), R.string.send_code_fail);
                        }
                    } else {
                        ToastUtil.showToast(getApplicationContext(), R.string.send_code_fail);
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
                    ToastUtil.showToast(getApplicationContext(), R.string.system_error);
                }
            });
        }
    }

    /**
     * 开始倒计时
     */
    private void startCountDown() {
        mSendVerifyTv.setClickable(false);
        mCountDownTimer = new CountDownTimer(60000, 1000) {
            @Override
            public void onTick(long l) {
                mSendVerifyTv.setTextColor(getResources().getColor(R.color.black_bbbbbb));
                String text = getResources().getString(R.string.re_send_one) + l / 1000 + getResources().getString(R.string.second);
                mSendVerifyTv.setText(text);
            }

            @Override
            public void onFinish() {
                mSendVerifyTv.setClickable(true);
                mSendVerifyTv.setTextColor(getResources().getColor(R.color.violet_d81aff));
                mSendVerifyTv.setText(R.string.get_code_one);
                if (mCountDownTimer != null) {
                    mCountDownTimer.cancel();
                    mCountDownTimer = null;
                }
            }
        }.start();
    }

    /**
     * 绑定手机号
     */
    private void bindPhone() {
        final String phone = mMobileEt.getText().toString().trim();
        if (TextUtils.isEmpty(phone)) {
            ToastUtil.showToast(getApplicationContext(), R.string.phone_number_null);
            return;
        }
        if (!VerifyUtils.isPhoneNum(phone)) {
            ToastUtil.showToast(getApplicationContext(), R.string.wrong_phone_number);
            return;
        }
        String verifyCode = mCodeEt.getText().toString().trim();
        if (TextUtils.isEmpty(verifyCode)) {
            ToastUtil.showToast(getApplicationContext(), R.string.verify_code_number_null);
            return;
        }

        Map<String, String> paramMap = new HashMap<>();
        paramMap.put("userId", getUserId());
        paramMap.put("phone", phone);
        paramMap.put("smsCode", verifyCode);
        OkHttpUtils.post().url(ChatApi.BIND_PHONE)
                .addParams("param", ParamUtil.getParam(paramMap))
                .build().execute(new AjaxCallback<BaseResponse<ChatUserInfo>>() {
            @Override
            public void onResponse(BaseResponse<ChatUserInfo> response, int id) {
                dismissLoadingDialog();
                if (response != null) {
                    if (response.m_istatus == NetCode.SUCCESS) {
                        ToastUtil.showToastLong(getApplicationContext(), R.string.add_phone_success);
                        ChatUserInfo chatUserInfo = SharedPreferenceHelper.getAccountInfo(getApplicationContext());
                        chatUserInfo.is_phone = 1;
                        SharedPreferenceHelper.saveAccountInfo(getApplicationContext(), chatUserInfo);
                        if (phoneUpdate) {
                            Intent intent = new Intent();
                            intent.putExtra(Constant.PHONE_MODIFY, phone);
                            setResult(RESULT_OK, intent);
                        } else {
                            if (chatUserInfo.t_sex == 0||chatUserInfo.t_sex == 1) {


                                startSocket((ChatUserInfo)(getIntent().getSerializableExtra("userInfo")));
                                loginJIM((ChatUserInfo)(getIntent().getSerializableExtra("userInfo")));
                            } else {
                                Intent intent = new Intent(getApplicationContext(), ChooseGenderActivity.class);
                                intent.putExtra(Constant.NICK_NAME, nickName);
                                intent.putExtra(Constant.MINE_HEAD_URL, avatar);
                                intent.putExtra("userInfo",(ChatUserInfo)(getIntent().getSerializableExtra("userInfo")));
                                startActivity(intent);
                            }
                        }
                        finish();
                    } else {
                        String message = response.m_strMessage;
                        if (!TextUtils.isEmpty(message)) {
                            ToastUtil.showToast(getApplicationContext(), message);
                        } else {
                            ToastUtil.showToast(getApplicationContext(), R.string.add_phone_failed);
                        }
                    }
                } else {
                    ToastUtil.showToast(getApplicationContext(), R.string.add_phone_failed);
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
                ToastUtil.showToast(getApplicationContext(), R.string.system_error);
            }
        });
    }

    /**
     * 开启服务并登陆
     */
    private void startSocket(ChatUserInfo chatUserInfo){
        if (chatUserInfo != null && chatUserInfo.t_id > 0) {
            if (chatUserInfo.t_sex != 2) {//有性别的话 就直接登录socket
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
        if (mCountDownTimer != null) {
            mCountDownTimer.cancel();
            mCountDownTimer = null;
        }
    }

}
