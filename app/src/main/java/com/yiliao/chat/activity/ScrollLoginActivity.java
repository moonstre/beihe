package com.yiliao.chat.activity;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.app.job.JobInfo;
import android.app.job.JobScheduler;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.graphics.Point;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.yiliao.chat.R;
import com.yiliao.chat.adapter.LoginTypeAdapter;
import com.yiliao.chat.adapter.SplashAdapter;
import com.yiliao.chat.base.AppManager;
import com.yiliao.chat.base.BaseActivity;
import com.yiliao.chat.base.BaseResponse;
import com.yiliao.chat.bean.ChatUserInfo;
import com.yiliao.chat.bean.LoginBean;
import com.yiliao.chat.constant.ChatApi;
import com.yiliao.chat.constant.Constant;
import com.yiliao.chat.dialog.DialogUitl;
import com.yiliao.chat.event.LoginEvent;
import com.yiliao.chat.helper.SharedPreferenceHelper;
import com.yiliao.chat.listener.OnItemClickListener;
import com.yiliao.chat.mob.LoginData;
import com.yiliao.chat.mob.MobBean;
import com.yiliao.chat.mob.MobCallback;
import com.yiliao.chat.mob.MobConst;
import com.yiliao.chat.mob.MobLoginUtil;
import com.yiliao.chat.net.AjaxCallback;
import com.yiliao.chat.net.NetCode;
import com.yiliao.chat.socket.ConnectService;
import com.yiliao.chat.socket.WakeupService;
import com.yiliao.chat.util.LogUtil;
import com.yiliao.chat.util.ParamUtil;
import com.yiliao.chat.util.SystemUtil;
import com.yiliao.chat.util.ToastUtil;
import com.yiliao.chat.view.ScrollLinearLayoutManager;
import com.zhy.http.okhttp.OkHttpUtils;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.OnClick;
import cn.jpush.android.api.JPushInterface;
import cn.jpush.im.android.api.JMessageClient;
import cn.jpush.im.api.BasicCallback;
import okhttp3.Call;

import static com.yiliao.chat.mob.MobConst.Type.FACEBOOK;
import static com.yiliao.chat.mob.MobConst.Type.QQ;
import static com.yiliao.chat.mob.MobConst.Type.WX;

/*
 * Copyright (C) 2018
 * 版权所有
 *
 * 功能描述：背景长图滑动登录页面
 * 作者：
 * 创建时间：2018/6/22
 *
 * 修改人：
 * 修改描述：
 * 修改日期
 */
public class ScrollLoginActivity extends BaseActivity {

    @BindView(R.id.content_rv)
    RecyclerView mContentRv;
    @BindView(R.id.recyclerView)
    RecyclerView mRecyclerView;

    @BindView(R.id.agree_tv)
    TextView agree_tv;
    @BindView(R.id.register_tv)
    TextView register_tv;
    @BindView(R.id.ivLoginLogo)
    ImageView ivLoginLogo;
    @BindView(R.id.text_gone)
    TextView text_gone;
    @BindView(R.id.layoutBottom)
    LinearLayout layoutBottom;
    private LoginTypeAdapter mLoginTypeAdapter;

    //接收关闭activity广播
    private MyLoginBroadcastReceiver mMyBroadcastReceiver;

    @Override
    protected View getContentView() {
        return inflate(R.layout.activity_scroll_login_layout);
    }

    @Override
    protected boolean supportFullScreen() {
        return true;
    }

    @Override
    protected void onContentAdded() {
        needHeader(false);
        if (Constant.hideHomeNearAndNew()){
//            ivLoginLogo.setVisibility(View.GONE);
            text_gone.setVisibility(View.GONE);
        }

        initStart();
    }

    /**
     * 初始化QQ 微信
     */
    private void initStart() {
        if (Constant.hideHomeNearAndNew()){
            register_tv.setTextColor(Color.parseColor("#FFFFFF"));
            layoutBottom.setVisibility(View.GONE);
        }
        mMyBroadcastReceiver = new MyLoginBroadcastReceiver();
        IntentFilter filter = new IntentFilter();
        filter.addAction(Constant.FINISH_LOGIN_PAGE);
        filter.addAction(Constant.BEEN_CLOSE_LOGIN_PAGE);
        registerReceiver(mMyBroadcastReceiver, filter);

        boolean beenClose = getIntent().getBooleanExtra(Constant.BEEN_CLOSE, false);
        if (beenClose) {
            String beenCloseDes = getIntent().getStringExtra(Constant.BEEN_CLOSE_DES);
            if (!TextUtils.isEmpty(beenCloseDes)) {
                showBeenCloseDialog(beenCloseDes);
            } else {
                String des = getResources().getString(R.string.been_suspend);
                showBeenCloseDialog(des);
            }
        }

        if (getIntent().getBooleanExtra(Constant.ACCOUNT_OUT, false)) {
            DialogUitl.showAccountOutDialog(mContext);
        }

        mContentRv.setAdapter(new SplashAdapter());
        mContentRv.setLayoutManager(new ScrollLinearLayoutManager(ScrollLoginActivity.this,
                LinearLayoutManager.HORIZONTAL));
        if (!Constant.hideHomeNearAndNew()){

            //smoothScrollToPosition滚动到某个位置（有滚动效果）
            mContentRv.smoothScrollToPosition(Integer.MAX_VALUE / 2);
        }
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(mContext, LinearLayoutManager.HORIZONTAL, false));
        mLoginTypeAdapter = new LoginTypeAdapter(mContext);
        mLoginTypeAdapter.setOnItemClickListener(new OnItemClickListener<LoginBean>() {
            @Override
            public void onItemClick(LoginBean bean, int position) {
                switch (bean.getType()) {
                    case 1: {
                        MobBean mobBean = new MobBean();
                        mobBean.setType(MobConst.Type.WX);
                        mobLoginGrant(mobBean);
                    }
                    break;
                    case 2: {
                        MobBean mobBean = new MobBean();
                        mobBean.setType(MobConst.Type.QQ);
                        mobLoginGrant(mobBean);
                    }
                    break;
                    case 3:
                        break;
                    case 4: {
                        MobBean mobBean = new MobBean();
                        mobBean.setType(FACEBOOK);
                        mobLoginGrant(mobBean);
                    }
                    break;
                }
            }
        });
        mRecyclerView.setAdapter(mLoginTypeAdapter);
        SharedPreferenceHelper.saveLoginTypeEmail(ScrollLoginActivity.this, false);
        intLoginUI();
    }

    @OnClick({R.id.phone_tv, R.id.agree_tv, R.id.register_tv})
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.phone_tv: {//手机号
                Intent intent = new Intent(getApplicationContext(), PhoneLoginActivity.class);
                startActivity(intent);
//                finish();
                break;
            }
            case R.id.agree_tv: {//用户协议
                Intent intent = new Intent(getApplicationContext(), CommonWebViewActivity.class);
                intent.putExtra(Constant.TITLE, getResources().getString(R.string.agree_detail));
                intent.putExtra(Constant.URL, "file:///android_asset/agree.html");
                startActivity(intent);
                break;
            }
            case R.id.register_tv: {
                int joinTypeRegister = 0;//注册
                Intent intent = new Intent(getApplicationContext(), RegisterActivity.class);
                intent.putExtra(Constant.JOIN_TYPE, joinTypeRegister);
                startActivity(intent);
                break;
            }
        }
    }

    /**
     * mob第三方授权
     */
    private void mobLoginGrant(MobBean bean) {
        MobLoginUtil mLoginUtil = new MobLoginUtil();
        mLoginUtil.execute(bean.getType(), new MobCallback() {
            @Override
            public void onSuccess(Object data) {
                if (data != null) {
                    LoginData loginData = (LoginData) data;
                    if (loginData.getType().equals(FACEBOOK)) {
//                        loginFacebookWay(loginData);
                    } else if (loginData.getType().equals(WX)) {
                        loginWxWay(loginData);
                    } else if (loginData.getType().equals(QQ)) {
//                        loginQQWay(loginData);
                    }
                }
            }

            @Override
            public void onError() {

            }

            @Override
            public void onCancel() {

            }

            @Override
            public void onFinish() {
//                        if (dialog != null) {
//                            dialog.dismiss();
//                        }
            }
        });
    }

//    private void loginFacebookWay(final LoginData loginData) {
//        //用于师徒
//        String t_system_version = "Android " + SystemUtil.getSystemVersion();
//        String deviceNumber = SystemUtil.getOnlyOneId(getApplicationContext());
//
//        Map<String, String> paramMap = new HashMap<>();
//        paramMap.put("openId", loginData.getOpenID());
//        paramMap.put("nickName", loginData.getNickName());
//        paramMap.put("handImg", loginData.getAvatar());
//        paramMap.put("city", "");
//        paramMap.put("t_phone_type", "Android");
//        paramMap.put("t_system_version", TextUtils.isEmpty(t_system_version) ? "" : t_system_version);
//        paramMap.put("deviceNumber", deviceNumber);
//        OkHttpUtils.post().url(ChatApi.FACEBOOK_LOGIN)
//                .addParams("param", ParamUtil.getParam(paramMap))
//                .build().execute(new AjaxCallback<BaseResponse<ChatUserInfo>>() {
//            @Override
//            public void onResponse(BaseResponse<ChatUserInfo> response, int id) {
//                dismissLoadingDialog();
//                if (response != null) {
//                    if (response.m_istatus == NetCode.SUCCESS) {
//                        ChatUserInfo userInfo = response.m_object;
//                        if (userInfo != null) {
//                            userInfo.nickName = loginData.getNickName();
//                            userInfo.headUrl = loginData.getNickName();
//                            AppManager.getInstance().setUserInfo(userInfo);
//                            SharedPreferenceHelper.saveAccountInfo(getApplicationContext(), userInfo);
////                            loginSocket(userInfo);
//                            ToastUtil.showToast(getApplicationContext(), R.string.login_success);
//
//                            if (userInfo.is_phone == 0) {
//                                Intent intent = new Intent(getApplicationContext(), AddPhoneActivity.class);
//                                intent.putExtra(Constant.NICK_NAME, loginData.getNickName());
//                                intent.putExtra(Constant.MINE_HEAD_URL, loginData.getAvatar());
//                                startActivity(intent);
//                            } else if (userInfo.t_sex == 2) {//没有性别
//                                Intent intent = new Intent(getApplicationContext(), ChooseGenderActivity.class);
//                                intent.putExtra(Constant.NICK_NAME, loginData.getNickName());
//                                intent.putExtra(Constant.MINE_HEAD_URL, loginData.getNickName());
//                                startActivity(intent);
//                            } else {
//                                startSocket(userInfo);
//
//                            }
//                            finish();
//                        } else {
//                            if (!TextUtils.isEmpty(response.m_strMessage)) {
//                                ToastUtil.showToast(getApplicationContext(), response.m_strMessage);
//                            } else {
//                                ToastUtil.showToast(getApplicationContext(), R.string.login_fail);
//                            }
//                        }
//                    } else if (response.m_istatus == -1) {//被封号
//                        String message = response.m_strMessage;
//                        if (!TextUtils.isEmpty(message)) {
//                            showBeenCloseDialog(message);
//                        } else {
//                            ToastUtil.showToast(getApplicationContext(), R.string.login_fail);
//                        }
//                    } else if (response.m_istatus == -200) {//7天内已经登陆过其他账号
//                        ToastUtil.showToast(getApplicationContext(), R.string.seven_days);
//                    } else {
//                        if (!TextUtils.isEmpty(response.m_strMessage)) {
//                            ToastUtil.showToast(getApplicationContext(), response.m_strMessage);
//                        } else {
//                            ToastUtil.showToast(getApplicationContext(), R.string.login_fail);
//                        }
//                    }
//                } else {
//                    ToastUtil.showToast(getApplicationContext(), R.string.login_fail);
//                }
//            }
//
//            @Override
//            public void onError(Call call, Exception e, int id) {
//                super.onError(call, e, id);
//                dismissLoadingDialog();
//                ToastUtil.showToast(getApplicationContext(), R.string.login_fail);
//            }
//        });
//    }

    /**
     * 调用自己的api 进行微信登录
     */
    private void loginWxWay(final LoginData loginData) {
        //用于师徒
        String t_system_version = "Android " + SystemUtil.getSystemVersion();
        String deviceNumber = SystemUtil.getOnlyOneId(getApplicationContext());

        Map<String, String> paramMap = new HashMap<>();
        paramMap.put("openId", loginData.getOpenID());
        paramMap.put("nickName", loginData.getNickName());
        paramMap.put("handImg", loginData.getAvatar());
        paramMap.put("city", "");
        paramMap.put("t_phone_type", "Android");
        paramMap.put("t_system_version", TextUtils.isEmpty(t_system_version) ? "" : t_system_version);
        paramMap.put("deviceNumber", deviceNumber);
        OkHttpUtils.post().url(ChatApi.WE_CHAT_LOGIN)
                .addParams("param", ParamUtil.getParam(paramMap))
                .build().execute(new AjaxCallback<BaseResponse<ChatUserInfo>>() {
            @Override
            public void onResponse(BaseResponse<ChatUserInfo> response, int id) {
                if (response != null) {
                    if (response.m_istatus == NetCode.SUCCESS) {
                        ChatUserInfo userInfo = response.m_object;
                        if (userInfo != null) {
                            userInfo.nickName = loginData.getNickName();
                            userInfo.headUrl = loginData.getAvatar();
                            userInfo.logintype="wx";
                            AppManager.getInstance().setUserInfo(userInfo);
                            SharedPreferenceHelper.saveAccountInfo(getApplicationContext(), userInfo);

                            ToastUtil.showToast(getApplicationContext(), R.string.login_success);
                            if (userInfo.is_phone == 0) {
                                Intent intent = new Intent(getApplicationContext(), AddPhoneActivity.class);
                                intent.putExtra(Constant.NICK_NAME, loginData.getNickName());
                                intent.putExtra(Constant.MINE_HEAD_URL, loginData.getAvatar());
                                intent.putExtra("userInfo",userInfo);
                                startActivity(intent);
                            } else {
                                if (userInfo.t_sex == 0||userInfo.t_sex==1) {
                                    startSocket(userInfo);
                                    loginJIM(userInfo);
                                } else {
                                    Intent intent = new Intent(getApplicationContext(), ChooseGenderActivity.class);
                                    intent.putExtra(Constant.NICK_NAME, loginData.getNickName());
                                    intent.putExtra(Constant.MINE_HEAD_URL, loginData.getAvatar());
                                    intent.putExtra("userInfo",userInfo);
                                    startActivity(intent);
                                    finish();
                                }
                            }

                        } else {
                            if (!TextUtils.isEmpty(response.m_strMessage)) {
                                ToastUtil.showToast(getApplicationContext(), response.m_strMessage);
                            } else {
                                ToastUtil.showToast(getApplicationContext(), R.string.login_fail);
                            }
                            finish();
                        }
                    } else if (response.m_istatus == -1) {//被封号
                        String message = response.m_strMessage;
                        Intent intent = new Intent(Constant.BEEN_CLOSE_LOGIN_PAGE);
                        intent.putExtra(Constant.BEEN_CLOSE, message);
                        sendBroadcast(intent);
//                        finish();
                    } else if (response.m_istatus == -200) {//7天内已经登陆过其他账号
                        ToastUtil.showToast(getApplicationContext(), R.string.seven_days);
                        finish();
                    } else {
                        ToastUtil.showToast(getApplicationContext(), R.string.login_fail);
                        finish();
                    }
                } else {
                    ToastUtil.showToast(getApplicationContext(), R.string.login_fail);
                    finish();
                }
            }

            @Override
            public void onError(Call call, Exception e, int id) {
                super.onError(call, e, id);
                ToastUtil.showToast(getApplicationContext(), R.string.login_fail);
                finish();
            }
        });
    }

    /**
     * 调用自己的api 进行qq登录
     */
//    private void loginQQWay(final LoginData loginData) {
//        //用于师徒
//        String t_system_version = "Android " + SystemUtil.getSystemVersion();
//        String deviceNumber = SystemUtil.getOnlyOneId(getApplicationContext());
//
//        Map<String, String> paramMap = new HashMap<>();
//        paramMap.put("openId", loginData.getOpenID());
//        paramMap.put("nickName", loginData.getNickName());
//        paramMap.put("handImg", loginData.getAvatar());
//        paramMap.put("city", "");
//        paramMap.put("t_phone_type", "Android");
//        paramMap.put("t_system_version", TextUtils.isEmpty(t_system_version) ? "" : t_system_version);
//        paramMap.put("deviceNumber", deviceNumber);
//        OkHttpUtils.post().url(ChatApi.QQ_LOGIN)
//                .addParams("param", ParamUtil.getParam(paramMap))
//                .build().execute(new AjaxCallback<BaseResponse<ChatUserInfo>>() {
//            @Override
//            public void onResponse(BaseResponse<ChatUserInfo> response, int id) {
//                dismissLoadingDialog();
//                if (response != null) {
//                    if (response.m_istatus == NetCode.SUCCESS) {
//                        ChatUserInfo userInfo = response.m_object;
//                        if (userInfo != null) {
//                            userInfo.nickName = loginData.getNickName();
//                            userInfo.headUrl = loginData.getAvatar();
//                            AppManager.getInstance().setUserInfo(userInfo);
//                            SharedPreferenceHelper.saveAccountInfo(getApplicationContext(), userInfo);
//                            loginSocket(userInfo);
//                            ToastUtil.showToast(getApplicationContext(), R.string.login_success);
//
//                            if (userInfo.is_phone == 0) {
//                                Intent intent = new Intent(getApplicationContext(), AddPhoneActivity.class);
//                                intent.putExtra(Constant.NICK_NAME, loginData.getNickName());
//                                intent.putExtra(Constant.MINE_HEAD_URL, loginData.getAvatar());
//                                startActivity(intent);
//                            } else if (userInfo.t_sex == 2) {//没有性别
//                                Intent intent = new Intent(getApplicationContext(), ChooseGenderActivity.class);
//                                intent.putExtra(Constant.NICK_NAME, loginData.getNickName());
//                                intent.putExtra(Constant.MINE_HEAD_URL, loginData.getAvatar());
//                                startActivity(intent);
//                            } else {
//                                Intent intent = new Intent(getApplicationContext(), MainActivity.class);
//                                startActivity(intent);
//                            }
//                            finish();
//                        } else {
//                            if (!TextUtils.isEmpty(response.m_strMessage)) {
//                                ToastUtil.showToast(getApplicationContext(), response.m_strMessage);
//                            } else {
//                                ToastUtil.showToast(getApplicationContext(), R.string.login_fail);
//                            }
//                        }
//                    } else if (response.m_istatus == -1) {//被封号
//                        String message = response.m_strMessage;
//                        if (!TextUtils.isEmpty(message)) {
//                            showBeenCloseDialog(message);
//                        } else {
//                            ToastUtil.showToast(getApplicationContext(), R.string.login_fail);
//                        }
//                    } else if (response.m_istatus == -200) {//7天内已经登陆过其他账号
//                        ToastUtil.showToast(getApplicationContext(), R.string.seven_days);
//                    } else {
//                        if (!TextUtils.isEmpty(response.m_strMessage)) {
//                            ToastUtil.showToast(getApplicationContext(), response.m_strMessage);
//                        } else {
//                            ToastUtil.showToast(getApplicationContext(), R.string.login_fail);
//                        }
//                    }
//                } else {
//                    ToastUtil.showToast(getApplicationContext(), R.string.login_fail);
//                }
//            }
//
//            @Override
//            public void onError(Call call, Exception e, int id) {
//                super.onError(call, e, id);
//                dismissLoadingDialog();
//                ToastUtil.showToast(getApplicationContext(), R.string.login_fail);
//            }
//        });
//    }

    class MyLoginBroadcastReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (!TextUtils.isEmpty(action) && action.equals(Constant.BEEN_CLOSE_LOGIN_PAGE)) {//被封号
                String message = intent.getStringExtra(Constant.BEEN_CLOSE);
                showBeenCloseDialog(message);
            } else {
                finish();
            }
        }
    }

    /**
     * 被封号
     */
    private void showBeenCloseDialog(String des) {
        final Dialog mDialog = new Dialog(mContext, R.style.DialogStyle_Dark_Background);
        @SuppressLint("InflateParams")
        View view = LayoutInflater.from(mContext).inflate(R.layout.dialog_been_close_layout, null);
        setDialogView(view, mDialog, des);
        mDialog.setContentView(view);
        Point outSize = new Point();
        getWindowManager().getDefaultDisplay().getSize(outSize);
        Window window = mDialog.getWindow();
        if (window != null) {
            WindowManager.LayoutParams params = window.getAttributes();
            params.width = outSize.x;
            window.setGravity(Gravity.CENTER); // 此处可以设置dialog显示的位置
        }
        mDialog.setCanceledOnTouchOutside(false);
        if (!isFinishing()) {
            mDialog.show();
        }
    }

    /**
     * 设置查看微信号提醒view
     */
    private void setDialogView(View view, final Dialog mDialog, String des) {
        //描述
        TextView see_des_tv = view.findViewById(R.id.des_tv);
        see_des_tv.setText(des);
        //取消
        TextView cancel_tv = view.findViewById(R.id.cancel_tv);
        cancel_tv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mDialog.dismiss();
            }
        });
        //查看规则
        TextView confirm_tv = view.findViewById(R.id.confirm_tv);
        confirm_tv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), CommonWebViewActivity.class);
                intent.putExtra(Constant.TITLE, getResources().getString(R.string.agree_detail));
                intent.putExtra(Constant.URL, "file:///android_asset/agree.html");
                startActivity(intent);
                mDialog.dismiss();
            }
        });
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EventBus.getDefault().register(this);
    }

    @Override
    protected void onDestroy() {
        if (mMyBroadcastReceiver != null) {
            unregisterReceiver(mMyBroadcastReceiver);
        }
        EventBus.getDefault().unregister(this);
        super.onDestroy();
    }
    /**
     * 开启服务并登陆
     */
    private void startSocket(ChatUserInfo chatUserInfo){
        if (chatUserInfo != null && chatUserInfo.t_id > 0) {
            if (chatUserInfo.t_sex == 0||chatUserInfo.t_sex == 1) {//有性别的话 就直接登录socket
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


//    private void loginSocket(ChatUserInfo chatUserInfo) {
//
//    }

    /**
     * 登录Im
     */
    private void loginJIM(final ChatUserInfo chatUserInfo) {
        //极光
        if (JPushInterface.isPushStopped(getApplicationContext())) {
            JPushInterface.resumePush(getApplicationContext());
        }
        //检测账号是否登陆
        cn.jpush.im.android.api.model.UserInfo myInfo = JMessageClient.getMyInfo();
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
                            Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                            startActivity(intent);
                            finish();
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
                                                Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                                                startActivity(intent);
                                                finish();
                                            }
                                        });
                            }
                        }
                    });
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(LoginEvent event) {
        finish();
    }

    private void intLoginUI() {
        String loginType = SharedPreferenceHelper.getLoginType(ScrollLoginActivity.this);
        if (!TextUtils.isEmpty(loginType)) {
            List<String> list = Arrays.asList(loginType.split(","));
            List<LoginBean> listBeans = new ArrayList<>();
            for (String item : list) {
                switch (item) {
                    case "1": {
                        LoginBean bean = new LoginBean();
                        bean.setType(1);
                        bean.setIcon(R.mipmap.icon_login_wechat);
                        listBeans.add(bean);
                    }
                    break;
                    case "2": {
                        LoginBean bean = new LoginBean();
                        bean.setType(2);
                        bean.setIcon(R.mipmap.icon_login_qq);
                        listBeans.add(bean);
                    }
                    break;
                    case "3": {
                        LoginBean bean = new LoginBean();
                        bean.setType(3);
                        bean.setIcon(R.mipmap.icon_login_whats);
                        listBeans.add(bean);
                    }
                    break;
                    case "4": {
                        LoginBean bean = new LoginBean();
                        bean.setType(4);
                        bean.setIcon(R.mipmap.icon_login_face);
                        listBeans.add(bean);
                    }
                    break;
                    case "5": {
                        SharedPreferenceHelper.saveLoginTypeEmail(ScrollLoginActivity.this, true);
                    }
                    break;
                }
            }
            mLoginTypeAdapter.setItems(listBeans);
        }
    }
}
