package com.yiliao.chat.activity;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.app.job.JobInfo;
import android.app.job.JobScheduler;
import android.content.Context;
import android.content.Intent;
import android.graphics.Point;
import android.net.Uri;
import android.os.Build;
import android.os.Handler;
import android.os.Message;
import android.provider.Settings;
import android.support.v4.content.FileProvider;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.yiliao.chat.BuildConfig;
import com.yiliao.chat.R;
import com.yiliao.chat.base.AppManager;
import com.yiliao.chat.base.BaseActivity;
import com.yiliao.chat.base.BaseResponse;
import com.yiliao.chat.bean.ChatUserInfo;
import com.yiliao.chat.bean.UpdateBean;
import com.yiliao.chat.constant.ChatApi;
import com.yiliao.chat.constant.Constant;
import com.yiliao.chat.helper.SharedPreferenceHelper;
import com.yiliao.chat.net.AjaxCallback;
import com.yiliao.chat.net.NetCode;
import com.yiliao.chat.socket.ConnectService;
import com.yiliao.chat.socket.WakeupService;
import com.yiliao.chat.util.ActivityManager;
import com.yiliao.chat.util.FileUtil;
import com.yiliao.chat.util.LogUtil;
import com.yiliao.chat.util.MyDataCleanManager;
import com.yiliao.chat.util.ParamUtil;
import com.yiliao.chat.util.ToastUtil;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.FileCallBack;

import java.io.File;
import java.lang.ref.WeakReference;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.OnClick;
import cn.jpush.android.api.JPushInterface;
import cn.jpush.im.android.api.JMessageClient;
import okhttp3.Call;

/*
 * Copyright (C) 2018
 * 版权所有
 *
 * 功能描述：设置页面
 * 作者：
 * 创建时间：2018/6/22
 *
 * 修改人：
 * 修改描述：
 * 修改日期
 */
public class SettingActivity extends BaseActivity {

    @BindView(R.id.check_tv)
    TextView mCheckTv;
    @BindView(R.id.cache_number_tv)
    TextView mCacheNumberTv;
    @BindView(R.id.sound_iv)
    ImageView mSoundIv;
    @BindView(R.id.vibrate_iv)
    ImageView mVibrateIv;



    private MyHandler mHandler = new MyHandler(SettingActivity.this);
    private final int DONE = 1;

    @Override
    protected View getContentView() {
        return inflate(R.layout.activity_setting_layout);
    }

    @Override
    protected void onContentAdded() {
        setTitle(R.string.setting_center);
        initView();
    }

    /**
     * 初始化版本
     */
    private void initView() {
        //消息提示音
        boolean sound = SharedPreferenceHelper.getTipSound(getApplicationContext());
        mSoundIv.setSelected(sound);
        //消息震动
        boolean vibrate = SharedPreferenceHelper.getTipVibrate(getApplicationContext());
        mVibrateIv.setSelected(vibrate);
        //版本
        String originalVersionName = BuildConfig.VERSION_NAME;//现在版本
        if (!TextUtils.isEmpty(originalVersionName)) {
            mCheckTv.setText(originalVersionName);
        }
        //缓存
        try {
            String dataSize = MyDataCleanManager.getTotalCacheSize(getApplicationContext());
            mCacheNumberTv.setText(dataSize);
            mCacheNumberTv.setVisibility(View.VISIBLE);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    @OnClick({R.id.exit_tv, R.id.check_rl,
            R.id.clear_cache_tv, R.id.sound_iv, R.id.vibrate_iv,R.id.blacklist_tv})
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.exit_tv: {//退出
                exit();
                break;
            }
            case R.id.check_rl: {//检查版本
                showLoadingDialog();
                checkUpdate();
                break;
            }
            case R.id.clear_cache_tv: {//清除缓存
                showLoadingDialog();
                clearAppCache();
                break;
            }
            case R.id.sound_iv: {//消息提示音
                if (mSoundIv.isSelected()) {
                    mSoundIv.setSelected(false);
                    SharedPreferenceHelper.saveTipSound(getApplicationContext(), false);
                } else {
                    mSoundIv.setSelected(true);
                    SharedPreferenceHelper.saveTipSound(getApplicationContext(), true);
                }
                break;
            }
            case R.id.vibrate_iv: {//消息震动
                if (mVibrateIv.isSelected()) {
                    mVibrateIv.setSelected(false);
                    SharedPreferenceHelper.saveTipVibrate(getApplicationContext(), false);
                } else {
                    mVibrateIv.setSelected(true);
                    SharedPreferenceHelper.saveTipVibrate(getApplicationContext(), true);
                }
                break;
            }
            case R.id.blacklist_tv://我的黑名单
                startActivity(new Intent(mContext,MyBlackListActivity.class));
                break;
        }
    }

    /**
     * 清除app缓存
     */
    public void clearAppCache() {
        new Thread() {
            @Override
            public void run() {
                try {
                    MyDataCleanManager.clearAllCache(getApplicationContext());
                    mHandler.sendEmptyMessage(DONE);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }.start();
    }

    /**
     * 退出
     */
    private void exit() {
        try {
            showLoadingDialog();
            //调用接口退出
            logoutMyService(getUserId());

            AppManager.getInstance().setUserInfo(null);
            ChatUserInfo chatUserInfo = new ChatUserInfo();
            chatUserInfo.t_sex = -1;
            chatUserInfo.t_id = 0;
            SharedPreferenceHelper.saveAccountInfo(getApplicationContext(), chatUserInfo);

            //关闭service
            finishJob();

            //IM登出
            JMessageClient.logout();

            //极光
            JPushInterface.stopPush(mContext);

            Window window = getWindow();
            if (window != null) {
                window.getDecorView().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        dismissLoadingDialog();
                        Intent intent = new Intent(getApplicationContext(), ScrollLoginActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        startActivity(intent);
                        finish();
                    }
                }, 700);
            } else {
                dismissLoadingDialog();
                Intent intent = new Intent(getApplicationContext(), ScrollLoginActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
                finish();
            }
        } catch (Exception e) {
            e.printStackTrace();
            dismissLoadingDialog();
            Intent intent = new Intent(getApplicationContext(), ScrollLoginActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            finish();
        }
        ActivityManager.getInstance().finishActivity(MainActivity.class);
    }

    private void finishJob() {
        try {
            Intent connect = new Intent(getApplicationContext(), ConnectService.class);
            stopService(connect);

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                Intent job = new Intent(getApplicationContext(), WakeupService.class);
                stopService(job);

                JobScheduler jobScheduler = (JobScheduler) getSystemService(Context.JOB_SCHEDULER_SERVICE);
                if (jobScheduler != null) {
                    List<JobInfo> allPendingJobs = jobScheduler.getAllPendingJobs();
                    if (allPendingJobs.size() > 0) {
                        jobScheduler.cancel(1);
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 调用接口退出
     */
    private void logoutMyService(String userId) {
        Map<String, String> paramMap = new HashMap<>();
        paramMap.put("userId", userId);
        OkHttpUtils.post().url(ChatApi.LOGOUT)
                .addParams("param", ParamUtil.getParam(paramMap))
                .build().execute(new AjaxCallback<BaseResponse>() {
            @Override
            public void onResponse(BaseResponse response, int id) {
                if (response != null && response.m_istatus == NetCode.SUCCESS) {
                    LogUtil.i("登出服务器成功");
                }
            }
        });
    }

    /**
     * 检查更新
     */
    private void checkUpdate() {
        Map<String, String> paramMap = new HashMap<>();
        paramMap.put("userId", getUserId());
        OkHttpUtils.post().url(ChatApi.GET_NEW_VERSION)
                .addParams("param", ParamUtil.getParam(paramMap))
                .build().execute(new AjaxCallback<BaseResponse<UpdateBean>>() {
            @Override
            public void onResponse(BaseResponse<UpdateBean> response, int id) {
                dismissLoadingDialog();
                if (response != null && response.m_istatus == NetCode.SUCCESS) {
                    UpdateBean bean = response.m_object;
                    if (bean != null) {
                        String t_version = bean.t_version;//接口版本
                        if (!TextUtils.isEmpty(t_version)) {
                            String originalVersionName = BuildConfig.VERSION_NAME;//现在版本
                            if (!TextUtils.isEmpty(originalVersionName)) {
                                if (!originalVersionName.equals(t_version)) {
                                    showUpdateDialog(bean);
                                } else {
                                    ToastUtil.showToast(getApplicationContext(), R.string.already_the_latest);
                                }
                            }
                        }
                    }
                }
            }

            @Override
            public void onError(Call call, Exception e, int id) {
                super.onError(call, e, id);
                ToastUtil.showToast(getApplicationContext(), R.string.system_error);
                dismissLoadingDialog();
            }
        });
    }

    /**
     * 显示更新dialog
     */
    private void showUpdateDialog(UpdateBean bean) {
        final Dialog mDialog = new Dialog(SettingActivity.this, R.style.DialogStyle_Dark_Background);
        @SuppressLint("InflateParams")
        View view = LayoutInflater.from(SettingActivity.this).inflate(R.layout.dialog_update_layout, null);
        setUpdateDialogView(view, mDialog, bean);
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
        mDialog.setCancelable(false);
        if (!isFinishing()) {
            mDialog.show();
        }
    }

    private void setUpdateDialogView(View view, final Dialog mDialog, final UpdateBean bean) {
        //描述
        TextView des_tv = view.findViewById(R.id.des_tv);
        String des = bean.t_version_depict;
        if (!TextUtils.isEmpty(des)) {
            des_tv.setText(des);
        }
        //更新
        final TextView update_tv = view.findViewById(R.id.update_tv);
        update_tv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mDialog.setCancelable(false);
                downloadApkFile(bean, mDialog, update_tv);
            }
        });
    }

    /**
     * 下载apk文件
     */
    private void downloadApkFile(UpdateBean bean, final Dialog updateDialog, final TextView updateTv) {
        String downloadUrl = bean.t_download_url;
        if (TextUtils.isEmpty(downloadUrl)) {
            ToastUtil.showToast(getApplicationContext(), R.string.update_fail);
            return;
        }
        File pFile = new File(FileUtil.YCHAT_DIR);
        if (!pFile.exists()) {
            boolean res = pFile.mkdir();
            if (!res) {
                return;
            }
        }
        File file = new File(Constant.UPDATE_DIR);
        if (!file.exists()) {
            boolean res = file.mkdir();
            if (!res) {
                return;
            }
        } else {
            FileUtil.deleteFiles(file.getPath());
        }
        OkHttpUtils.get().url(downloadUrl).build().execute(new FileCallBack(Constant.UPDATE_DIR, Constant.UPDATE_APK_NAME) {
            @Override
            public void onError(Call call, Exception e, int id) {

            }

            @Override
            public void inProgress(float progress, long total, int id) {
                super.inProgress(progress, total, id);
                int res = (int) (progress * 100);
                String content = res + getResources().getString(R.string.percent);
                updateTv.setText(content);
            }

            @Override
            public void onResponse(File response, int id) {
                try {
                    updateDialog.dismiss();
                    if (response != null && response.exists() && response.isFile()) {
                        // 下载成功后，检查8.0安装权限,安装apk
                        checkIsAndroidO(response);
                    } else {
                        ToastUtil.showToast(getApplicationContext(), R.string.update_fail);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    updateDialog.dismiss();
                    ToastUtil.showToast(getApplicationContext(), R.string.update_fail);
                }
            }
        });
    }

    /**
     * 判断是否是8.0,8.0需要处理未知应用来源权限问题,否则直接安装
     */
    private void checkIsAndroidO(File response) {
        if (Build.VERSION.SDK_INT >= 26) {
            boolean b = getPackageManager().canRequestPackageInstalls();
            LogUtil.i("=====未知来源安装权限: " + b);
            if (b) {
                installApk(response);//安装应用的逻辑(写自己的就可以)
            } else {
                showUnkownPermissionDialog();
            }
        } else {
            installApk(response);
        }
    }

    /**
     * 被封号
     */
    private void showUnkownPermissionDialog() {
        final Dialog mDialog = new Dialog(SettingActivity.this, R.style.DialogStyle_Dark_Background);
        @SuppressLint("InflateParams")
        View view = LayoutInflater.from(SettingActivity.this).inflate(R.layout.dialog_set_unkown_permission_layout, null);
        setUnkownDialogView(view, mDialog);
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
    private void setUnkownDialogView(View view, final Dialog mDialog) {
        //取消
        TextView cancel_tv = view.findViewById(R.id.cancel_tv);
        cancel_tv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mDialog.dismiss();
            }
        });
        //设置
        TextView confirm_tv = view.findViewById(R.id.set_tv);
        confirm_tv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (Build.VERSION.SDK_INT >= 26) {
                    //请求安装未知应用来源的权限
                    Intent intent = new Intent(Settings.ACTION_MANAGE_UNKNOWN_APP_SOURCES);
                    startActivityForResult(intent, 10086);
                }
                mDialog.dismiss();
            }
        });
    }

    //普通安装
    private void installApk(File apkFile) {
        Intent intent = new Intent(Intent.ACTION_VIEW);
        //版本在7.0以上是不能直接通过uri访问的
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            // 由于没有在Activity环境下启动Activity,设置下面的标签
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            Uri apkUri = FileProvider.getUriForFile(getApplicationContext(), BuildConfig.fileprovider, apkFile);
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            intent.setDataAndType(apkUri, "application/vnd.android.package-archive");
        } else {
            intent.setDataAndType(Uri.fromFile(apkFile),
                    "application/vnd.android.package-archive");
        }
        startActivity(intent);
    }

    private static class MyHandler extends Handler {
        private WeakReference<SettingActivity> mSettingActivityWeakReference;

        MyHandler(SettingActivity settingActivity) {
            mSettingActivityWeakReference = new WeakReference<>(settingActivity);
        }

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            SettingActivity settingActivity = mSettingActivityWeakReference.get();
            if (settingActivity != null) {
                settingActivity.clearDone();
                settingActivity.dismissLoadingDialog();
            }
        }
    }

    /**
     * 清理完成
     */
    private void clearDone() {
        mCacheNumberTv.setText(getResources().getString(R.string.cache_des));
        ToastUtil.showToast(getApplicationContext(), R.string.clear_done);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mHandler != null) {
            mHandler.removeCallbacksAndMessages(null);
            mHandler = null;
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 10086) {//未知来源权限
            if (Build.VERSION.SDK_INT >= 26) {
                boolean b = getPackageManager().canRequestPackageInstalls();
                File apk = new File(Constant.UPDATE_DIR, Constant.UPDATE_APK_NAME);
                if (apk.exists() && b) {
                    installApk(apk);
                }
            }
        }
    }

}
