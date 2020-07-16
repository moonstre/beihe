package com.yiliao.chat.activity;

import android.app.job.JobInfo;
import android.app.job.JobScheduler;
import android.content.ComponentName;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Build;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;

import com.alibaba.fastjson.JSON;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.SimpleTarget;
import com.yiliao.chat.R;
import com.yiliao.chat.base.AppManager;
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
import com.yiliao.chat.util.BitmapUtil;
import com.yiliao.chat.util.FileUtil;
import com.yiliao.chat.util.LogUtil;
import com.yiliao.chat.util.ParamUtil;
import com.yiliao.chat.util.ToastUtil;
import com.zhy.http.okhttp.OkHttpUtils;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import butterknife.BindView;
import butterknife.OnClick;
import cn.jpush.android.api.JPushInterface;
import cn.jpush.im.android.api.JMessageClient;
import cn.jpush.im.android.api.model.UserInfo;
import cn.jpush.im.api.BasicCallback;
import okhttp3.Call;
import okhttp3.Request;

/*
 * Copyright (C) 2018
 * 版权所有
 *
 * 功能描述：选择性别页面
 * 作者：
 * 创建时间：2018/6/22
 *
 * 修改人：
 * 修改描述：
 * 修改日期
 */
public class ChooseGenderActivity extends BaseActivity {

    @BindView(R.id.girl_iv)
    ImageView mGirlIv;
    @BindView(R.id.boy_iv)
    ImageView mBoyIv;

    private final int BOY = 1;
    private final int GIRL = 0;
    private int mSelectGender;

    @Override
    protected View getContentView() {
        return inflate(R.layout.activity_choose_gender_layout);
    }

    @Override
    protected void onContentAdded() {
        setTitle(R.string.choose_gender);
        setRightText(R.string.confirm);
        setBackVisibility(View.GONE);

        String nickName = getIntent().getStringExtra(Constant.NICK_NAME);
        String headUrl = getIntent().getStringExtra(Constant.MINE_HEAD_URL);
        if (!TextUtils.isEmpty(nickName) && !TextUtils.isEmpty(headUrl)) {
            setIMInfo(headUrl, nickName);
        }
    }

    @OnClick({R.id.boy_iv, R.id.girl_iv, R.id.right_text})
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.boy_iv: {//男
                setGenderSelect(BOY);
                break;
            }
            case R.id.girl_iv: {//女
                setGenderSelect(GIRL);
                break;
            }
            case R.id.right_text: {//确定
                chooseGender();
                break;
            }
        }
    }

    /**
     * 设置选中
     */
    private void setGenderSelect(int position) {
        if (position == BOY) {
            if (mBoyIv.isSelected()) {
                return;
            }
            mSelectGender = BOY;
            mBoyIv.setSelected(true);
            mBoyIv.setImageResource(R.drawable.boy);
            mGirlIv.setSelected(false);
            mGirlIv.setImageResource(R.drawable.gril_not);
        } else if (position == GIRL) {
            if (mGirlIv.isSelected()) {
                return;
            }
            mSelectGender = GIRL;
            mGirlIv.setSelected(true);
            mGirlIv.setImageResource(R.drawable.girl);
            mBoyIv.setSelected(false);
            mBoyIv.setImageResource(R.drawable.boy_not);
        }
    }

    /**
     * 选择性别
     */
    private void chooseGender() {
        Map<String, String> paramMap = new HashMap<>();
        paramMap.put("userId", getUserId());
        paramMap.put("sex", String.valueOf(mSelectGender));
        OkHttpUtils.post().url(ChatApi.UPDATE_USER_SEX)
                .addParams("param", ParamUtil.getParam(paramMap))
                .build().execute(new AjaxCallback<BaseResponse<ChatUserInfo>>() {
            @Override
            public void onResponse(BaseResponse<ChatUserInfo> response, int id) {
                LogUtil.i("选择性别: " + JSON.toJSONString(response));
                dismissLoadingDialog();
                if (response != null && response.m_istatus == NetCode.SUCCESS) {
                    ToastUtil.showToast(getApplicationContext(), R.string.choose_success);
                    if (AppManager.getInstance().getUserInfo() != null) {
                        AppManager.getInstance().getUserInfo().t_sex = mSelectGender;
                    }
                    SharedPreferenceHelper.saveGenderInfo(getApplicationContext(), mSelectGender);
                    loginJIM((ChatUserInfo) getIntent().getSerializableExtra("userInfo"));
                    //登录socket
                    startSocket(response.m_object);

                } else {
                    ToastUtil.showToast(getApplicationContext(), R.string.system_error);
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
            if (chatUserInfo.t_sex == 0||chatUserInfo.t_sex ==1) {//有性别的话 就直接登录socket
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
        //极光
        if (JPushInterface.isPushStopped(getApplicationContext())) {
            JPushInterface.resumePush(getApplicationContext());
        }
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
    /**
     * 设置头像  昵称
     */
    private void setIMInfo(String headImg, String nick) {
        UserInfo myUserInfo = JMessageClient.getMyInfo();
        if (myUserInfo != null) {
            //昵称
            String nickName = myUserInfo.getNickname();
            if (TextUtils.isEmpty(nickName) || !nickName.equals(nick)) {
                myUserInfo.setNickname(nick);
                setIMNick(myUserInfo);
            }
            //头像
            String face = myUserInfo.getAvatar();
            if (TextUtils.isEmpty(face) || !face.equals(headImg)) {
                setIMFace(headImg);
            }
        }
    }

    /**
     * 设置Im头像
     */
    private void setIMFace(String faceUrl) {
        //保存到本地
        File pFile = new File(FileUtil.YCHAT_DIR);
        if (!pFile.exists()) {
            pFile.mkdir();
        }
        File dir = new File(Constant.HEAD_AFTER_SHEAR_DIR);
        if (!dir.exists()) {
            dir.mkdir();
        } else {
            FileUtil.deleteFiles(Constant.HEAD_AFTER_SHEAR_DIR);
        }
        final String filePath = Constant.HEAD_AFTER_SHEAR_DIR + System.currentTimeMillis() + ".png";
        Glide.with(this).load(faceUrl).asBitmap().centerCrop().into(new SimpleTarget<Bitmap>() {
            @Override
            public void onResourceReady(Bitmap resource, GlideAnimation<? super Bitmap> glideAnimation) {
                File file = BitmapUtil.saveBitmapAsJpg(resource, filePath);
                if (file != null) {
                    JMessageClient.updateUserAvatar(new File(filePath), new BasicCallback() {
                        @Override
                        public void gotResult(int responseCode, String responseMessage) {
                            if (responseCode == 0) {
                                LogUtil.i("更新头像成功");
                            }
                        }
                    });
                }
            }
        });
    }

    /**
     * 设置Im昵称
     */
    private void setIMNick(UserInfo myUserInfo) {
        //注册时候更新昵称
        JMessageClient.updateMyInfo(UserInfo.Field.nickname, myUserInfo, new BasicCallback() {
            @Override
            public void gotResult(final int status, String desc) {
                if (status == 0) {
                    LogUtil.i("更新极光im昵称成功");
                }
            }
        });
    }

    /**
     * 开启socket
     */
    private void startSocket() {
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
