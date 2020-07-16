package com.yiliao.chat.activity;

import android.Manifest;
import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.graphics.BitmapFactory;
import android.graphics.Point;
import android.media.AudioAttributes;
import android.media.AudioManager;
import android.media.SoundPool;
import android.net.Uri;
import android.os.Build;
import android.os.Handler;
import android.os.Vibrator;
import android.provider.Settings;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.content.FileProvider;
import android.support.v4.view.ViewPager;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationClient;
import com.amap.api.location.AMapLocationClientOption;
import com.amap.api.location.AMapLocationListener;
import com.bumptech.glide.Glide;
import com.bumptech.glide.GlideBuilder;
import com.bumptech.glide.RequestManager;
import com.faceunity.wrapper.faceunity;
import com.fm.openinstall.OpenInstall;
import com.fm.openinstall.listener.AppInstallAdapter;
import com.fm.openinstall.model.AppData;
import com.yalantis.ucrop.UCrop;
import com.yiliao.chat.BuildConfig;
import com.yiliao.chat.R;
import com.yiliao.chat.base.BaseActivity;
import com.yiliao.chat.base.BaseResponse;
import com.yiliao.chat.bean.CompanyInviteBean;
import com.yiliao.chat.bean.DownloadBean;
import com.yiliao.chat.bean.RedCountBean;
import com.yiliao.chat.bean.ScrollGraphBean;
import com.yiliao.chat.bean.UnReadBean;
import com.yiliao.chat.bean.UnReadMessageBean;
import com.yiliao.chat.bean.UpdateBean;
import com.yiliao.chat.constant.ChatApi;
import com.yiliao.chat.constant.Constant;
import com.yiliao.chat.fragment.ActiveFragment;
import com.yiliao.chat.fragment.HomeOneFragment;
import com.yiliao.chat.fragment.LiveFragment;
import com.yiliao.chat.fragment.MessageFragment;
import com.yiliao.chat.fragment.MineFragment;
import com.yiliao.chat.fragment.VideoFragment;
import com.yiliao.chat.fragment.ZBLiveFragment;
import com.yiliao.chat.fragment.ZBMineFragment;
import com.yiliao.chat.fulive.authpack;
import com.yiliao.chat.helper.SharedPreferenceHelper;
import com.yiliao.chat.mob.MobCallback;
import com.yiliao.chat.mob.MobConst;
import com.yiliao.chat.mob.MobShareUtil;
import com.yiliao.chat.mob.ShareData;
import com.yiliao.chat.net.AjaxCallback;
import com.yiliao.chat.net.NetCode;
import com.yiliao.chat.util.DevicesUtil;
import com.yiliao.chat.util.FileUtil;
import com.yiliao.chat.util.LogUtil;
import com.yiliao.chat.util.ParamUtil;
import com.yiliao.chat.util.SPUtils;
import com.yiliao.chat.util.ToastUtil;
import com.zhihu.matisse.Matisse;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.FileCallBack;

import org.json.JSONException;

import java.io.File;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.OnClick;
import cn.jpush.android.api.JPushInterface;
import cn.jpush.im.android.api.JMessageClient;
import cn.jpush.im.android.api.content.TextContent;
import cn.jpush.im.android.api.enums.ConversationType;
import cn.jpush.im.android.api.event.MessageEvent;
import cn.jpush.im.android.api.event.OfflineMessageEvent;
import cn.jpush.im.android.api.model.Message;
import de.hdodenhof.circleimageview.CircleImageView;
import ezy.assist.compat.SettingsCompat;
import okhttp3.Call;

/*
 * Copyright (C) 2018
 * 版权所有
 *
 * 功能描述：主页
 * 作者：
 * 创建时间：2018/6/14
 *
 * 修改人：
 * 修改描述：
 * 修改日期
 */
public class MainActivity extends BaseActivity {
    @BindView(R.id.roll_view)
    LinearLayout mRoolView;
    @BindView(R.id.roll_head)
    CircleImageView mRoolHead;
    @BindView(R.id.roll_id)
    TextView mRoolId;
    @BindView(R.id.roll_type)
    TextView mRoolType;
    @BindView(R.id.roll_git)
    TextView mRoolGit;
    @BindView(R.id.roll_yuan)
    TextView mRoolYuan;
    @BindView(R.id.content_vp)
    ViewPager mContentVp;
    //首页
    @BindView(R.id.home_tv)
    View mHomeTv;
    //关注
    @BindView(R.id.focus_tv)
    View mFocusTv;
    //消息
    @BindView(R.id.message_tv)
    View mMessageTv;
    @BindView(R.id.red_count_tv)
    TextView mRedCountTv;
    //我的
    @BindView(R.id.mine_tv)
    View mMineTv;
    @BindView(R.id.red_small_iv)
    ImageView mRedSmallIv;
    @BindView(R.id.active_red_hot_iv)
    ImageView mActiveRedHotIv;
    @BindView(R.id.quick_ll)
    View mQuickLl;


    @BindView(R.id.bottom_ll)
    LinearLayout bottom_ll;

    //动态
    private ActiveFragment mActiveFragment;
    //我的相关成员
    private MineFragment mMineFragment;
    //消息
    private MessageFragment mMessageFragment;
    private ZBMineFragment mzbMineFragment;

    //短视频
    VideoFragment mVideoFragment;
    Handler mHandler=new Handler();
    //接收关闭分享广播
    private MyCloseBroadcastReceiver mMyBroadcastReceiver;
    private Dialog mThreeQunDialog;
    private SoundPool mSoundPool;//新消息声音
    private int mSoundId;
    private int mSystemMessageCount;

    @Override
    protected View getContentView() {
        return inflate(R.layout.activity_main);
    }

    @Override
    protected boolean isImmersionBarEnabled() {
        return true;
    }

    @Override
    protected void onContentAdded() {
        needHeader(false);
        checkPermission();
        checkFloatPermission();
        initViewPager();
        checkUpdate();

        if (!Constant.isNewUser()) {
            checkNewUser();
        }

        setJPushAlias();
        startLocation();
        getQQNumber();

        //相芯初始化
        try {
            InputStream v3 = getAssets().open("v3.bundle");
            byte[] v3Data = new byte[v3.available()];
            v3.read(v3Data);
            v3.close();
            int num = faceunity.fuSetup(v3Data, authpack.A());
        } catch (Exception e) {
            e.printStackTrace();
        }

        if (Constant.displayInviteCode()) {
            mHandler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    InviteCode();
                }
            },2000);

        }


    }

    /**
     * 设置极光推送别名
     */
    private void setJPushAlias() {
        String saveAlisa = SharedPreferenceHelper.getJPushAlias(mContext);
        if (TextUtils.isEmpty(saveAlisa) || !saveAlisa.equals(getUserId())) {
            JPushInterface.setAlias(mContext, 1, getUserId());
        }
    }

    /**
     * 初始化viewPager
     */
    @SuppressLint("ResourceAsColor")
    private void initViewPager() {


        mMyBroadcastReceiver = new MyCloseBroadcastReceiver();
        IntentFilter filter = new IntentFilter(Constant.QUN_SHARE_QUN_CLOSE);
        registerReceiver(mMyBroadcastReceiver, filter);

        final List<Fragment> list = new ArrayList<>();
        final HomeOneFragment mHomeFragment = new HomeOneFragment();
        ZBLiveFragment zbliveFragment = new ZBLiveFragment();
        LiveFragment liveFragment = new LiveFragment();




        VideoFragment videoFragment = new VideoFragment();
        videoFragment.setShowTitle(true);
        if (Constant.hideActiveFragment()) {

        } else {
            mActiveFragment = new ActiveFragment();
        }
        mMessageFragment = new MessageFragment();
        mMineFragment = new MineFragment();
        mzbMineFragment = new ZBMineFragment();

        list.add(mHomeFragment);
        if (Constant.hideActiveFragment()) {
            list.add(videoFragment);
        } else {
            list.add(mActiveFragment);
        }

        if (Constant.showMainVideo()) {
            if (Constant.displaylive()) {
                list.add(zbliveFragment);
            } else {
                list.add(videoFragment);
            }
        }


        list.add(mMessageFragment);

        if (Constant.displayMine()) {
            list.add(mzbMineFragment);
        } else {
            list.add(mMineFragment);
        }

        mContentVp.setAdapter(new FragmentStatePagerAdapter(getSupportFragmentManager()) {
            @Override
            public Fragment getItem(int position) {
                return list.get(position);
            }

            @Override
            public int getCount() {
                return list.size();
            }
        });
        mContentVp.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                switch (position) {
                    case 0:
                        switchHome(position, true);
                        break;
                    case 1:
                        switchFocus(position, true);
                        break;
                    case 2:
                        if (Constant.showMainVideo()) {
                            switchLive(position, true);
                        } else {
                            switchMessage(position, true);
                        }
                        break;
                    case 3:
                        if (Constant.showMainVideo()) {
                            switchMessage(position, true);
                        } else {
                            switchMine(position, true);
                            if (mzbMineFragment!=null){
                                mzbMineFragment.getCompanyInvite();
                                if (Constant.hideHomeNearAndNew()){
                                    mzbMineFragment.getInfo();
                                }
                            }
                            getRedPacketCount();
                        }
                        break;
                    case 4:
                        switchMine(position, true);
                        if (mzbMineFragment!=null){
                            mzbMineFragment.getCompanyInvite();
                            if (Constant.hideHomeNearAndNew()){
                                mzbMineFragment.getInfo();
                            }
                        }
                        getRedPacketCount();
                        break;
                    default:
                        break;
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
        mContentVp.setOffscreenPageLimit(list.size());
        switchHome(0, false);
        getScrollGraph();
        Calendar calendar=Calendar.getInstance();
        if (TextUtils.isEmpty(SPUtils.getString(getUserId()+"_"+"date"))){
            showNotAdultDialog();
        }else {
            if (!TextUtils.equals(SPUtils.getString(getUserId()+"_"+"date"),""+calendar.get(Calendar.YEAR)+calendar.get(Calendar.MONTH)+calendar.get(Calendar.DAY_OF_MONTH))){
                showNotAdultDialog();
            };
        }

    }

    @OnClick({R.id.home_ll, R.id.focus_ll, R.id.message_ll, R.id.mine_ll, R.id.quick_ll})
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.home_ll: {//首页
                switchHome(0, false);
                break;
            }
            case R.id.focus_ll: {//关注
                switchFocus(1, false);
                break;
            }
            case R.id.quick_ll: {//直播
                //直播
//                if (Constant.showMainVideo()) {
//                    switchLive(2, false);
//                } else {
//                    //1 主播 0 用户
//                    if (getUserRole() == 1) {//主播
//                        //如果是主播,获取房间号,加入房间预览视频
//                        getRoomId();
//                    } else {//用户
//                        Intent intent = new Intent(mContext, UserViewQuickActivity.class);
//                        mContext.startActivity(intent);
//                    }
//                }
                Intent intent = new Intent(mContext, RankActivity.class);
                startActivity(intent);
                break;
            }
            case R.id.message_ll: {//消息
                if (Constant.showMainVideo()) {
                    switchMessage(3, false);
                } else {
                    switchMessage(2, false);
                }
                break;
            }
            case R.id.mine_ll: {//我的
                if (Constant.showMainVideo()) {
                    switchMine(4, false);
                } else {
                    switchMine(3, false);
                }
                break;
            }
        }
    }

    /**
     * 初始化导航状态
     */
    private void initNavigation() {
        mHomeTv.setSelected(false);
        mFocusTv.setSelected(false);
        mMessageTv.setSelected(false);
        mMineTv.setSelected(false);
    }

    private void switchHome(int position, boolean fromViewPager) {
        if (mHomeTv.isSelected()) {
            return;
        }
        mImmersionBar.statusBarDarkFont(false).init();
        initNavigation();
        if (!fromViewPager) {
            mContentVp.setCurrentItem(position);
        }
        mHomeTv.setSelected(true);
        mFocusTv.setSelected(false);
        mMessageTv.setSelected(false);
        mMineTv.setSelected(false);
    }

    private void switchFocus(int position, boolean fromViewPager) {
        if (mFocusTv.isSelected()) {
            return;
        }
        mImmersionBar.statusBarDarkFont(false).init();
        initNavigation();
        if (!fromViewPager) {
            mContentVp.setCurrentItem(position);
        }
        mFocusTv.setSelected(true);
        mHomeTv.setSelected(false);
        mMessageTv.setSelected(false);
        mMineTv.setSelected(false);
    }

    private void switchMessage(int position, boolean fromViewPager) {
        if (mMessageTv.isSelected()) {
            return;
        }
        mImmersionBar.statusBarDarkFont(false).init();
        initNavigation();
        if (!fromViewPager) {
            mContentVp.setCurrentItem(position);
        }
        mMessageTv.setSelected(true);
        mHomeTv.setSelected(false);
        mFocusTv.setSelected(false);
        mMineTv.setSelected(false);
    }

    private void switchMine(int position, boolean fromViewPager) {
        if (mMineTv.isSelected()) {
            return;
        }
        mImmersionBar.statusBarDarkFont(true).init();
        initNavigation();
        if (!fromViewPager) {
            mContentVp.setCurrentItem(position);
        }
        mMineTv.setSelected(true);
        mHomeTv.setSelected(false);
        mFocusTv.setSelected(false);
        mMessageTv.setSelected(false);
    }

    private void switchLive(int position, boolean fromViewPager) {

        mImmersionBar.statusBarDarkFont(false).init();
        initNavigation();
        if (!fromViewPager) {
            mContentVp.setCurrentItem(position);
        }
        mMineTv.setSelected(false);
        mHomeTv.setSelected(false);
        mFocusTv.setSelected(false);
        mMessageTv.setSelected(false);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            if (requestCode == Constant.REQUEST_CODE_CHOOSE) {//图库选择
                List<Uri> mSelectedUris = Matisse.obtainResult(data);
                LogUtil.i("==--", "头像selected: " + mSelectedUris);
                if (mSelectedUris != null && mSelectedUris.size() > 0) {
                    try {
                        Uri uri = mSelectedUris.get(0);
                        String filePath = FileUtil.getPathAbove19(this, uri);
                        if (!TextUtils.isEmpty(filePath)) {
                            File file = new File(filePath);
                            LogUtil.i("==--", "file大小: " + file.length() / 1024);
                            //直接裁剪
                            Uri pictureUri = FileUtil.getUriAdjust24(getBaseContext(), file);
                            cutWithUCrop(pictureUri);
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            } else if (requestCode == Constant.REQUEST_CODE_SHOOT_PICTURE) {//拍照
                File file = new File(FileUtil.YCHAT_DIR + "shoot/", "shoot_temp.jpg");
                Uri pictureUri = FileUtil.getUriAdjust24(getBaseContext(), file);
                cutWithUCrop(pictureUri);
            } else if (requestCode == UCrop.REQUEST_CROP) {
                Uri resultUri = UCrop.getOutput(data);
                if (Constant.displayMine()) {
                    if (resultUri != null && mzbMineFragment != null) {
                        mzbMineFragment.showHeadImage(resultUri);
                    }
                } else {
                    if (resultUri != null && mMineFragment != null) {
                        mMineFragment.showHeadImage(resultUri);
                    }
                }

            } else if (requestCode == Constant.REQUEST_ALBUM_IMAGE_AND_VIDEO) {//动态发布请求matisee相册,用于从ActiveFragment进,返回到MainActivity
                List<Uri> mSelectedUris = Matisse.obtainResult(data);
                if (mSelectedUris != null && mSelectedUris.size() > 0) {
                    LogUtil.i("动态相册选择的: " + mSelectedUris.toString());
                    if (checkUri(mSelectedUris)) {//判断通过
                        Uri fileUri = mSelectedUris.get(0);
                        if (fileUri != null) {
                            //跳转到发布页面
                            int album = 2;//拍摄
                            Intent intent = new Intent(getApplicationContext(), PostActiveActivity.class);
                            intent.putExtra(Constant.POST_FROM, album);
                            //如果是视频
                            if (fileUri.toString().contains("video")) {
                                intent.putExtra(Constant.PASS_TYPE, Constant.TYPE_VIDEO);
                            } else {
                                //如果是图片
                                intent.putExtra(Constant.PASS_TYPE, Constant.TYPE_IMAGE);
                            }
                            intent.putExtra(Constant.POST_FILE_URI, fileUri.toString());
                            startActivity(intent);
                        }
                    }
                }
            }
        } else if (requestCode == 10086) {//未知来源权限
            if (Build.VERSION.SDK_INT >= 26) {
                boolean b = getPackageManager().canRequestPackageInstalls();
                File apk = new File(Constant.UPDATE_DIR, Constant.UPDATE_APK_NAME);
                if (apk.exists() && b) {
                    installApk(apk);
                }
            }
        }
    }

    /**
     * 检查选择的
     */
    private boolean checkUri(List<Uri> mSelectedUris) {
        //判断文件是否无效
        Uri uri = mSelectedUris.get(0);
        if (!checkUriFileExist(uri)) {
            ToastUtil.showToast(getApplicationContext(), R.string.file_invalidate);
            return false;
        }
        return true;
    }

    /**
     * 判断文件
     */
    private boolean checkUriFileExist(Uri uri) {
        if (uri != null) {
            String filePath = FileUtil.getPathAbove19(getApplicationContext(), uri);
            if (!TextUtils.isEmpty(filePath)) {
                File file = new File(filePath);
                if (file.exists()) {
                    return true;
                } else {
                    LogUtil.i("文件不存在: " + uri.toString());
                    return false;
                }
            }
        }
        return false;
    }

    /**
     * 使用u crop裁剪
     */
    private void cutWithUCrop(Uri sourceUri) {
        //计算 图片裁剪的大小
        int overWidth = (DevicesUtil.dp2px(mContext, 80));
        int overHeight = (DevicesUtil.dp2px(mContext, 80));
        File file = new File(Constant.HEAD_AFTER_SHEAR_DIR);
        if (!file.exists()) {
            boolean res = file.mkdir();
            if (!res) {
                return;
            }
        } else {
            FileUtil.deleteFiles(file.getPath());
        }
        String filePath = file.getPath() + File.separator + System.currentTimeMillis() + ".png";
        UCrop.of(sourceUri, Uri.fromFile(new File(filePath)))
                .withAspectRatio(1, 1)
                .withMaxResultSize(overWidth, overHeight)
                .start(this);
    }

    protected void checkPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            List<String> permissions = new ArrayList<>();
            if (PackageManager.PERMISSION_GRANTED != ActivityCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                permissions.add(Manifest.permission.WRITE_EXTERNAL_STORAGE);
            }
            if (PackageManager.PERMISSION_GRANTED != ActivityCompat.checkSelfPermission(this, Manifest.permission.CAMERA)) {
                permissions.add(Manifest.permission.CAMERA);
            }
            if (PackageManager.PERMISSION_GRANTED != ActivityCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO)) {
                permissions.add(Manifest.permission.RECORD_AUDIO);
            }
            if (PackageManager.PERMISSION_GRANTED != ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_PHONE_STATE)) {
                permissions.add(Manifest.permission.READ_PHONE_STATE);
            }
            if (PackageManager.PERMISSION_GRANTED != ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE)) {
                permissions.add(Manifest.permission.READ_EXTERNAL_STORAGE);
            }
            //定位
            if (PackageManager.PERMISSION_GRANTED != ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)) {
                permissions.add(Manifest.permission.ACCESS_FINE_LOCATION);
            }
            if (permissions.size() != 0) {
                ActivityCompat.requestPermissions(this, permissions.toArray(new String[0]), 100);
            }
        }
    }

    /**
     * 离线消息
     */
    public void onEventMainThread(OfflineMessageEvent event) {
        List<Message> messageList = event.getOfflineMessageList();
        LogUtil.i("离线消息:  " + messageList.size());
        if (messageList.size() > 0) {
            dealMessageCount(0);
        }
    }

    /**
     * 在线消息
     */
    public void onEventMainThread(MessageEvent event) {
        Message message = event.getMessage();
        if (message.getTargetType() == ConversationType.single) {
            switch (message.getContentType()) {
                case text: {//文本
                    String content = ((TextContent) message.getContent()).getText();
                    LogUtil.i("新的文本消息: " + content);
                    try {
                        dealMessageCount(mSystemMessageCount);
                        playMusicAndVibrate();
                        if (mMessageFragment != null && !isFinishing()) {
                            if (mMessageFragment.mHaveFirstVisible) {
                                mMessageFragment.getConversation();
                            }
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    break;
                }
                case custom: {//礼物
                    try {
                        dealMessageCount(mSystemMessageCount);
                        playMusicAndVibrate();
                        if (mMessageFragment != null && !isFinishing()) {
                            if (mMessageFragment.mHaveFirstVisible) {
                                mMessageFragment.getConversation();
                            }
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    break;
                }
            }
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        try {
            //注册sdk的event用于接收各种event事件
            JMessageClient.registerEventReceiver(MainActivity.this);
            dealUnReadCount();
            getRedPacketCount();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 处理未读消息
     */
    private void dealUnReadCount() {
        Map<String, String> paramMap = new HashMap<>();
        paramMap.put("userId", getUserId());
        OkHttpUtils.post().url(ChatApi.GET_UN_READ_MESSAGE)
                .addParams("param", ParamUtil.getParam(paramMap))
                .build().execute(new AjaxCallback<BaseResponse<UnReadBean<UnReadMessageBean>>>() {
            @Override
            public void onResponse(BaseResponse<UnReadBean<UnReadMessageBean>> response, int id) {
                if (response != null && response.m_istatus == NetCode.SUCCESS) {
                    UnReadBean<UnReadMessageBean> bean = response.m_object;
                    if (bean != null) {
                        mSystemMessageCount = bean.totalCount;
                        dealMessageCount(bean.totalCount);
                        if (mMessageFragment != null && !isFinishing()) {
                            mMessageFragment.loadSystemMessage(bean);
                        }
                    } else {
                        dealMessageCount(0);
                    }
                } else {
                    dealMessageCount(0);
                }
            }

            @Override
            public void onError(Call call, Exception e, int id) {
                super.onError(call, e, id);
                dealMessageCount(0);
            }
        });
    }

    /**
     * 处理消息总数
     */
    private void dealMessageCount(int systemCount) {
        int count;
        if(systemCount!=0){
             count =   systemCount;
        }else {
             count = JMessageClient.getAllUnReadMsgCount() + systemCount;
        }

        LogUtil.i("未读消息count1: " + JMessageClient.getAllUnReadMsgCount());
        if (mRedCountTv != null && !isFinishing()) {
            LogUtil.i("未读消息count: " + count);
            if (count > 0) {
                if (count <= 99) {
                    mRedCountTv.setText(String.valueOf(count));
                    mRedCountTv.setBackgroundResource(R.drawable.shape_unread_count_text_back);
                } else {
                    mRedCountTv.setText(getResources().getString(R.string.nine_nine));
                    mRedCountTv.setBackgroundResource(R.drawable.shape_unread_count_nine_nine_text_back);
                }
                mRedCountTv.setVisibility(View.VISIBLE);
            } else {
                mRedCountTv.setVisibility(View.GONE);
            }
        }
    }

    public void resetRedPot() {
        if (mRedCountTv != null) {
            if (mRedCountTv.getVisibility() == View.VISIBLE) {
                mRedCountTv.setVisibility(View.GONE);
            }
        }
    }

    /**
     * 获取用户收未拆开红包统计
     */
    private void getRedPacketCount() {
        Map<String, String> paramMap = new HashMap<>();
        paramMap.put("userId", getUserId());
        OkHttpUtils.post().url(ChatApi.GET_RED_PACKET_COUNT)
                .addParams("param", ParamUtil.getParam(paramMap))
                .build().execute(new AjaxCallback<BaseResponse<RedCountBean>>() {
            @Override
            public void onResponse(BaseResponse<RedCountBean> response, int id) {
                if (isFinishing()) {
                    return;
                }
                if (response != null && response.m_istatus == NetCode.SUCCESS) {
                    RedCountBean bean = response.m_object;
                    if (bean != null) {
                        int total = bean.total;
                        if (Constant.displayMine()) {
                            if (mzbMineFragment != null) {
                                mzbMineFragment.showRedPack(total);
                            }
                        } else {
                            if (mMineFragment != null) {
                                mMineFragment.showRedPack(total);
                            }
                        }

                        if (total > 0) {
                            if (mRedSmallIv != null) {
                                mRedSmallIv.setVisibility(View.VISIBLE);
                            }
                        } else {
                            if (mRedSmallIv != null) {
                                mRedSmallIv.setVisibility(View.INVISIBLE);
                            }
                        }
                    } else {
                        if (mRedSmallIv != null) {
                            mRedSmallIv.setVisibility(View.INVISIBLE);
                        }
                    }
                } else {
                    mRedSmallIv.setVisibility(View.INVISIBLE);
                }
            }

            @Override
            public void onError(Call call, Exception e, int id) {
                super.onError(call, e, id);
                if (isFinishing()) {
                    return;
                }
                mRedSmallIv.setVisibility(View.INVISIBLE);
            }
        });
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        //判断用户是否点击了“返回键”
        if (keyCode == KeyEvent.KEYCODE_BACK) {
//            moveTaskToBack(false);
//            ActivityManager.getInstance().exit();
            Intent home = new Intent(Intent.ACTION_MAIN);
            home.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            home.addCategory(Intent.CATEGORY_HOME);
            startActivity(home);
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    /**
     * 检查悬浮窗权限
     */
    private void checkFloatPermission() {
        try {
            boolean result = SettingsCompat.canDrawOverlays(MainActivity.this);
            if (!result) {
                showRequestPermissionDialog();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 被封号
     */
    private void showNotAdultDialog() {
        final Dialog mDialog = new Dialog(MainActivity.this, R.style.DialogStyle_Dark_Background);
        @SuppressLint("InflateParams")
        View view = LayoutInflater.from(MainActivity.this).inflate(R.layout.dialog_not_adult, null);
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
        view.findViewById(R.id.enter).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Calendar calendar=Calendar.getInstance();
                SPUtils.set(getUserId()+"_"+"date",""+calendar.get(Calendar.YEAR)+calendar.get(Calendar.MONTH)+calendar.get(Calendar.DAY_OF_MONTH));
                mDialog.dismiss();
            }
        });
        view.findViewById(R.id.know).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Calendar calendar=Calendar.getInstance();
                SPUtils.set(getUserId()+"_"+"date",""+calendar.get(Calendar.YEAR)+calendar.get(Calendar.MONTH)+calendar.get(Calendar.DAY_OF_MONTH));
                mDialog.dismiss();

            }
        });
    }

    /**
     * 被封号
     */
    private void showRequestPermissionDialog() {
        final Dialog mDialog = new Dialog(MainActivity.this, R.style.DialogStyle_Dark_Background);
        @SuppressLint("InflateParams")
        View view = LayoutInflater.from(MainActivity.this).inflate(R.layout.dialog_set_permission_layout, null);
        setDialogView(view, mDialog);
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
    private void setDialogView(View view, final Dialog mDialog) {
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
                try {
                    SettingsCompat.manageDrawOverlays(MainActivity.this);
                } catch (Exception e) {
                    e.printStackTrace();
                    // 跳转到系统设置修改权限设置页，暂时只支持 Android 6.0+
                    try {
                        SettingsCompat.manageWriteSettings(MainActivity.this);
                    } catch (Exception e1) {
                        e1.printStackTrace();
                    }
                }
                mDialog.dismiss();
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
                if (response != null && response.m_istatus == NetCode.SUCCESS) {
                    UpdateBean bean = response.m_object;
                    if (bean != null) {
                        String t_version = bean.t_version;//接口版本
                        if (!TextUtils.isEmpty(t_version)) {
                            String originalVersionName = BuildConfig.VERSION_NAME;//现在版本
                            if (!TextUtils.isEmpty(originalVersionName) && !originalVersionName.equals(t_version)) {
                                showUpdateDialog(bean);
                            }
                        }
                    }
                }
            }
        });
    }

    /**
     * 显示更新dialog
     */
    private void showUpdateDialog(UpdateBean bean) {
        final Dialog mDialog = new Dialog(MainActivity.this, R.style.DialogStyle_Dark_Background);
        @SuppressLint("InflateParams")
        View view = LayoutInflater.from(MainActivity.this).inflate(R.layout.dialog_update_layout, null);
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
        //版本
        TextView title_tv = view.findViewById(R.id.title_tv);
        String version = bean.t_version;
        String content;
        if (!TextUtils.isEmpty(version)) {
            content = getResources().getString(R.string.new_version_des_one) + version;
        } else {
            content = getString(R.string.new_version_des);
        }
        title_tv.setText(content);
        //更新
        final TextView update_tv = view.findViewById(R.id.update_tv);
        update_tv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                mDialog.setCancelable(false);
//                update_tv.setEnabled(false);
//                downloadApkFile(bean, mDialog, update_tv);
                Intent intent = new Intent();
                intent.setAction("android.intent.action.VIEW");
                Uri content_url = Uri.parse(bean.t_download_url);
                intent.setData(content_url);
                startActivity(intent);
            }
        });
        //不能正常升级
        TextView click_tv = view.findViewById(R.id.click_tv);
        click_tv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getDownloadUrl();
                mDialog.dismiss();
            }
        });
    }

    /**
     * 获取下载链接
     */
    private void getDownloadUrl() {
        Map<String, String> paramMap = new HashMap<>();
        paramMap.put("userId", getUserId());
        OkHttpUtils.post().url(ChatApi.GET_DOLOAD_URL)
                .addParams("param", ParamUtil.getParam(paramMap))
                .build().execute(new AjaxCallback<BaseResponse<DownloadBean>>() {
            @Override
            public void onResponse(BaseResponse<DownloadBean> response, int id) {
                if (response != null && response.m_istatus == NetCode.SUCCESS) {
                    DownloadBean bean = response.m_object;
                    if (bean != null && !TextUtils.isEmpty(bean.t_android_download)) {
                        Intent intent = new Intent();
                        intent.setAction("android.intent.action.VIEW");
                        Uri content_url = Uri.parse(bean.t_android_download);
                        intent.setData(content_url);
                        startActivity(intent);
                    } else {
                        ToastUtil.showToast(getApplicationContext(), R.string.get_download_url_fail);
                    }
                } else {
                    ToastUtil.showToast(getApplicationContext(), R.string.get_download_url_fail);
                }
            }

            @Override
            public void onError(Call call, Exception e, int id) {
                super.onError(call, e, id);
                ToastUtil.showToast(getApplicationContext(), R.string.get_download_url_fail);
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
            updateTv.setEnabled(true);
            return;
        }
        File pFile = new File(FileUtil.YCHAT_DIR);
        if (!pFile.exists()) {
            boolean res = pFile.mkdir();
            if (!res) {
                updateTv.setEnabled(true);
                return;
            }
        }
        File file = new File(Constant.UPDATE_DIR);
        if (!file.exists()) {
            boolean res = file.mkdir();
            if (!res) {
                updateTv.setEnabled(true);
                return;
            }
        } else {
            FileUtil.deleteFiles(file.getPath());
        }
        OkHttpUtils.get().url(downloadUrl).build().execute(new FileCallBack(Constant.UPDATE_DIR, Constant.UPDATE_APK_NAME) {
            @Override
            public void onError(Call call, Exception e, int id) {
                updateTv.setEnabled(true);
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
        final Dialog mDialog = new Dialog(MainActivity.this, R.style.DialogStyle_Dark_Background);
        @SuppressLint("InflateParams")
        View view = LayoutInflater.from(MainActivity.this).inflate(R.layout.dialog_set_unkown_permission_layout, null);
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

    /**
     * 判断是不是新用户
     */
    private void checkNewUser() {
        Map<String, String> paramMap = new HashMap<>();
        paramMap.put("userId", getUserId());
        OkHttpUtils.post().url(ChatApi.GET_USER_NEW)
                .addParams("param", ParamUtil.getParam(paramMap))
                .build().execute(new AjaxCallback<BaseResponse<Integer>>() {
            @Override
            public void onResponse(BaseResponse<Integer> response, int id) {
                if (response != null && response.m_istatus == NetCode.SUCCESS) {
                    Integer m_object = response.m_object;
                    if (m_object == -1) {//0.不是新用户 1.是新用户
                        showNewUserDialog();
                    }
                }
            }
        });
    }

    /**
     * 显示新用户dialog
     */
    private void showNewUserDialog() {
        final Dialog mDialog = new Dialog(MainActivity.this, R.style.DialogStyle_Dark_Background);
        @SuppressLint("InflateParams")
        View view = LayoutInflater.from(MainActivity.this).inflate(R.layout.dialog_new_user_red_pack_layout, null);
        setNewUserDialogView(view, mDialog);
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

    private void setNewUserDialogView(View view, final Dialog mDialog) {
        //取消
        ImageView close_iv = view.findViewById(R.id.close_iv);
        close_iv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mDialog.dismiss();
            }
        });
        //立即领取
        ImageView get_now_iv = view.findViewById(R.id.get_now_iv);
        get_now_iv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showThreeQunDialog();
                mDialog.dismiss();
            }
        });
    }

    /**
     * 显示分享到3个群dialog
     */
    private void showThreeQunDialog() {
        if (mThreeQunDialog == null) {
            mThreeQunDialog = new Dialog(MainActivity.this, R.style.DialogStyle_Dark_Background);
            @SuppressLint("InflateParams")
            View view = LayoutInflater.from(MainActivity.this).inflate(R.layout.dialog_wechat_qun_share_layout, null);
            setThreeQunDialogView(view, mThreeQunDialog);
            mThreeQunDialog.setContentView(view);
            Point outSize = new Point();
            getWindowManager().getDefaultDisplay().getSize(outSize);
            Window window = mThreeQunDialog.getWindow();
            if (window != null) {
                WindowManager.LayoutParams params = window.getAttributes();
                params.width = outSize.x;
                window.setGravity(Gravity.CENTER); // 此处可以设置dialog显示的位置
            }
            mThreeQunDialog.setCanceledOnTouchOutside(false);
            if (!isFinishing()) {
                mThreeQunDialog.show();
            }
        }
    }

    private void setThreeQunDialogView(View view, final Dialog mDialog) {
        //取消
        ImageView back_iv = view.findViewById(R.id.back_iv);
        back_iv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mDialog.dismiss();
            }
        });
        //立即分享
        TextView share_qun_tv = view.findViewById(R.id.share_qun_tv);
        share_qun_tv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                shareToWeChatCircle(MobConst.Type.WX);
            }
        });
    }

    /**
     * 分享到微信
     */
    private void shareToWeChatCircle(String platType) {
        MobShareUtil mobShareUtil = new MobShareUtil();
        ShareData data = new ShareData();
        data.setTitle(getResources().getString(R.string.share_qun_title));
        data.setDes(getResources().getString(R.string.share_qun_des));
        data.setImgData(BitmapFactory.decodeResource(getResources(), R.mipmap.logo));
        String webUrl = ChatApi.JUMP_GAME + getUserId();
        data.setWebUrl(webUrl);
        mobShareUtil.execute(platType, data, new MobCallback() {
            @Override
            public void onSuccess(Object data) {
                ToastUtil.showToast(getApplicationContext(), R.string.share_success);
            }

            @Override
            public void onError() {
//                ToastUtil.showToast(getApplicationContext(), R.string.share_fail);
            }

            @Override
            public void onCancel() {
//                ToastUtil.showToast(getApplicationContext(), R.string.share_cancel);
            }

            @Override
            public void onFinish() {

            }
        });
    }

    class MyCloseBroadcastReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            try {
                String action = intent.getAction();
                if (!TextUtils.isEmpty(action) && action.equals(Constant.QUN_SHARE_QUN_CLOSE)) {//关闭分享页面
                    if (mThreeQunDialog != null) {
                        mThreeQunDialog.dismiss();
                        mThreeQunDialog = null;
                    }
                    if (mMyBroadcastReceiver != null) {
                        unregisterReceiver(mMyBroadcastReceiver);
                        mMyBroadcastReceiver = null;
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        try {
            if (mSoundPool != null) {
                mSoundPool.release();
                mSoundPool = null;
            }
            if (mMyBroadcastReceiver != null) {
                unregisterReceiver(mMyBroadcastReceiver);
            }
            mHandler.removeCallbacksAndMessages(null);
            //注销消息接收
            JMessageClient.unRegisterEventReceiver(MainActivity.this);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 开始定位
     */
    private void startLocation() {
        //检查权限
        if (ActivityCompat.checkSelfPermission(getApplicationContext(),
                Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(getApplicationContext(),
                        Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            LogUtil.i("地理位置没有权限:");
            return;
        }

        //声明AMapLocationClient类对象
        AMapLocationClient mLocationClient = new AMapLocationClient(getApplicationContext());
        AMapLocationClientOption mLocationOption = new AMapLocationClientOption();
        mLocationOption.setLocationMode(AMapLocationClientOption.AMapLocationMode.Hight_Accuracy);
        mLocationOption.setOnceLocation(true);
        mLocationClient.setLocationListener(new AMapLocationListener() {
            @Override
            public void onLocationChanged(AMapLocation aMapLocation) {
                if (aMapLocation != null) {
                    if (aMapLocation.getErrorCode() == 0) {//成功
                        //保存在本地
                        double lat = aMapLocation.getLatitude();
                        double lng = aMapLocation.getLongitude();
                        String city =aMapLocation.getCity();

                        if (lat > 0 && lng > 0) {
                            SharedPreferenceHelper.saveCode(getApplicationContext(), String.valueOf(lat),
                                    String.valueOf(lng),city);
                            uploadCode(lat, lng);
                        }
                    } else {//失败
                        LogUtil.i("定位失败 :" + aMapLocation.getErrorCode() + ", errInfo:"
                                + aMapLocation.getErrorInfo());
                    }
                }
            }
        });
        mLocationClient.setLocationOption(mLocationOption);
        mLocationClient.startLocation();
    }

    /**
     * 上传坐标
     */
    private void uploadCode(double lat, double lng) {
        Map<String, String> paramMap = new HashMap<>();
        paramMap.put("userId", getUserId());
        paramMap.put("lat", String.valueOf(lat));
        paramMap.put("lng", String.valueOf(lng));
        OkHttpUtils.post().url(ChatApi.UPLOAD_COORDINATE)
                .addParams("param", ParamUtil.getParam(paramMap))
                .build().execute(new AjaxCallback<BaseResponse>() {
            @Override
            public void onResponse(BaseResponse response, int id) {
                if (response != null && response.m_istatus == NetCode.SUCCESS) {
                    LogUtil.i("上传坐标成功");
                }
            }
        });
    }

    /**
     * 播放音频 震动
     */
    private void playMusicAndVibrate() {
        try {
            //获取是否开启声音
            boolean sound = SharedPreferenceHelper.getTipSound(getApplicationContext());
            if (sound) {
                if (mSoundPool == null) {
                    //初始化SoundPool
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                        AudioAttributes aab = new AudioAttributes.Builder()
                                .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                                .setUsage(AudioAttributes.USAGE_MEDIA)
                                .build();
                        mSoundPool = new SoundPool.Builder()
                                .setMaxStreams(5)
                                .setAudioAttributes(aab)
                                .build();
                    } else {
                        mSoundPool = new SoundPool(5, AudioManager.STREAM_MUSIC, 8);
                    }
                    //设置资源加载监听
                    mSoundPool.setOnLoadCompleteListener(new SoundPool.OnLoadCompleteListener() {
                        @Override
                        public void onLoadComplete(SoundPool soundPool, int sampleId, int status) {
                            if (mSoundPool != null && mSoundId > 0) {
                                mSoundPool.play(mSoundId, 1, 1, 0, 0, 1);
                            }
                        }
                    });
                    //加载deep 音频文件
                    mSoundId = mSoundPool.load(getApplicationContext(), R.raw.new_message, 1);
                } else {
                    if (mSoundId > 0) {
                        mSoundPool.play(mSoundId, 1, 1, 0, 0, 1);
                    }
                }
            }
            //消息提示震动
            boolean vibrate = SharedPreferenceHelper.getTipVibrate(getApplicationContext());
            if (vibrate) {
                Vibrator vibrator = (Vibrator) getApplicationContext().getSystemService(VIBRATOR_SERVICE);
                if (vibrator != null) {
                    vibrator.vibrate(400);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 动态新品类
     */
    @Override
    protected void onActiveNewComment() {
        if (mActiveRedHotIv != null && mActiveRedHotIv.getVisibility() != View.VISIBLE) {
            mActiveRedHotIv.setVisibility(View.VISIBLE);
        }
        if (mActiveFragment != null && mActiveFragment.mHaveFirstVisible) {
            mActiveFragment.getNewCommentCount();
        }
    }

    /**
     * 清除动态红点
     */
    public void clearActiveRedPot() {
        if (mActiveRedHotIv != null && mActiveRedHotIv.getVisibility() == View.VISIBLE) {
            mActiveRedHotIv.setVisibility(View.GONE);
        }
    }

    /**
     * 清除红包图标
     */
    public void clearRed() {
        if (mRedSmallIv != null) {
            mRedSmallIv.setVisibility(View.INVISIBLE);
        }
    }

    /**
     * 获取QQ客服号
     */
    private void getQQNumber() {
        Map<String, String> paramMap = new HashMap<>();
        paramMap.put("userId", getUserId());
        String encodeStr = ParamUtil.getParam(paramMap);
        OkHttpUtils.post().url(ChatApi.GET_SERVICE_QQ)
                .addParams("param", encodeStr)
                .build().execute(new AjaxCallback<BaseResponse<String>>() {
            @Override
            public void onResponse(BaseResponse<String> response, int id) {
                if (response != null && response.m_istatus == NetCode.SUCCESS) {
                    if (!TextUtils.isEmpty(response.m_object)) {
                        String mQQNumber = response.m_object;
                        LogUtil.i("QQ客服: " + mQQNumber);
                        String saveQQ = SharedPreferenceHelper.getQQ(getApplicationContext());
                        if (!TextUtils.isEmpty(mQQNumber) && !saveQQ.equals(mQQNumber)) {
                            SharedPreferenceHelper.saveQQ(getApplicationContext(), mQQNumber);
                        }
                    }
                }
            }
        });
    }

    /**
     * 速配
     */
    private void getRoomId() {
        Map<String, String> paramMap = new HashMap<>();
        paramMap.put("userId", getUserId());
        OkHttpUtils.post().url(ChatApi.GET_SPEED_DATING_ROOM)
                .addParams("param", ParamUtil.getParam(paramMap))
                .build().execute(new AjaxCallback<BaseResponse<Integer>>() {
            @Override
            public void onResponse(BaseResponse<Integer> response, int id) {
                if (response != null && response.m_istatus == NetCode.SUCCESS) {
                    Integer roomId = response.m_object;
                    if (roomId > 0) {
                        Intent intent = new Intent(mContext, QuickVideoChatActivity.class);
                        intent.putExtra(Constant.ROOM_ID, roomId);
                        intent.putExtra(Constant.FROM_TYPE, Constant.FROM_ACTOR);
                        mContext.startActivity(intent);
                    }
                }
            }
        });
    }

    public void InviteCode() {
            if (SharedPreferenceHelper.getAccountInfo(mContext).is_referee.equals("0")) {
                    OpenInstall.getInstall(new AppInstallAdapter() {
                        @Override
                        public void onInstall(AppData appData) {
                            String bindData = appData.getData();
                            JSONObject obj = JSON.parseObject(bindData);
                            if (obj != null) {
                                final String refereeId = obj.getString("code");
                                if (!TextUtils.isEmpty(refereeId) && !refereeId.equals(String.valueOf(Integer.parseInt(getUserId()) + 10000))) {
                                    Map<String, String> paramMap = new HashMap<>();
                                    paramMap.put("userId", getUserId());
                                    paramMap.put("refereeId", refereeId);
                                    OkHttpUtils.post().url(ChatApi.OPENINSTALL)
                                            .addParams("param", ParamUtil.getParam(paramMap))
                                            .build().execute(new AjaxCallback<BaseResponse>() {
                                        @Override
                                        public void onResponse(BaseResponse response, int id) {
                                            if (response != null&&response.m_istatus==NetCode.SUCCESS) {//	-3：当前用户已绑定推广人 -2：当前推广人不存在 -1：推广人不能是自己 0.程序异常 1:绑定成功

                                            }
                                        }
                                    });

                                }
                            }
                        }
                    });

            }

    }


    /**
     * 显示输入邀请码弹窗
     */
    private void showInputInviteCodeDialog() {
        final Dialog mDialog = new Dialog(mContext, R.style.DialogStyle_Dark_Background);
        @SuppressLint("InflateParams")
        View view = LayoutInflater.from(mContext).inflate(R.layout.dialog_input_invite_code_layout, null);
        setInviteCodeView(view, mDialog);
        mDialog.setContentView(view);
        Point outSize = new Point();
        getWindowManager().getDefaultDisplay().getSize(outSize);
        Window window = mDialog.getWindow();
        if (window != null) {
            WindowManager.LayoutParams params = window.getAttributes();
            params.width = outSize.x;
            window.setGravity(Gravity.CENTER); // 此处可以设置dialog显示的位置
        }
        mDialog.setCanceledOnTouchOutside(true);
        if (!isFinishing()) {
            mDialog.show();
        }
    }

    //ID号
    private int mIdCardNnmber;

    private void setInviteCodeView(View view, final Dialog mDialog) {

        mIdCardNnmber = Integer.parseInt(getUserId()) + 10000;
        //邀请码
        TextView code_tv = view.findViewById(R.id.code_tv);
        if (mIdCardNnmber > 0) {
            code_tv.setText(String.valueOf(mIdCardNnmber));
        }
        //复制
        TextView copy_tv = view.findViewById(R.id.copy_tv);
        copy_tv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mIdCardNnmber > 0) {
                    //获取剪贴板管理器
                    ClipboardManager cm = (ClipboardManager) mContext.getSystemService(Context.CLIPBOARD_SERVICE);
                    // 创建普通字符型ClipData
                    ClipData mClipData = ClipData.newPlainText("Label", String.valueOf(mIdCardNnmber));
                    // 将ClipData内容放到系统剪贴板里。
                    if (cm != null) {
                        cm.setPrimaryClip(mClipData);
                        ToastUtil.showToast(mContext, R.string.copy_success);
                    }
                }
                mDialog.dismiss();
            }
        });
        //输入邀请框
        final EditText code_et = view.findViewById(R.id.code_et);
        //确认
        TextView confirm_tv = view.findViewById(R.id.confirm_tv);
        confirm_tv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String code = code_et.getText().toString().trim();
                if (TextUtils.isEmpty(code)) {
                    ToastUtil.showToast(mContext, R.string.please_input_code);
                    return;
                }
                if (Integer.parseInt(code) <= 0) {
                    ToastUtil.showToast(mContext, R.string.please_input_right_code);
                    return;
                }
                bindCode(code);
                mDialog.dismiss();
            }
        });
    }

    @Override
    protected void recive_red(String content) {
        super.recive_red(content);
        if (mRedSmallIv != null) {
            try {
                org.json.JSONObject jsonObject1=new org.json.JSONObject(content);
                String redCount=jsonObject1.getString("redCount");
                if (mMineFragment!=null){
                    mMineFragment.setRedNum(redCount);
                }
                mRedSmallIv.setVisibility(View.VISIBLE);
            } catch (JSONException e) {
                e.printStackTrace();
            }

        }

    }

    @Override
    protected void indive_guild(String content) {
        super.indive_guild(content);
        try {
            org.json.JSONObject js=new org.json.JSONObject(content);
            String t_admin_name=js.getString("t_admin_name");
            String t_guild_name=js.getString("t_guild_name");
            int t_id=js.getInt("t_id");
            String show = t_admin_name + mContext.getResources().getString(R.string.invite_you)
                    + t_guild_name + mContext.getResources().getString(R.string.company);
            if (mzbMineFragment!=null){
                mzbMineFragment.showCompanyInviteDialog(show,t_id);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    /**
     * 手动绑定推广人
     */
    private void bindCode(String code) {
        Map<String, String> paramMap = new HashMap<>();
        paramMap.put("userId", getUserId());
        paramMap.put("idCard", code);
        OkHttpUtils.post().url(ChatApi.UNITE_ID_CARD)
                .addParams("param", ParamUtil.getParam(paramMap))
                .build().execute(new AjaxCallback<BaseResponse>() {
            @Override
            public void onResponse(BaseResponse response, int id) {
                if (response != null) {//	-3：当前用户已绑定推广人 -2：当前推广人不存在 -1：推广人不能是自己 0.程序异常 1:绑定成功
                    if (response.m_istatus == NetCode.SUCCESS) {
                        ToastUtil.showToast(mContext, R.string.bind_success);
                    } else if (response.m_istatus == -3) {
                        ToastUtil.showToast(mContext, R.string.have_bind);
                    } else if (response.m_istatus == -2) {
                        ToastUtil.showToast(mContext, R.string.bind_man_not_exist);
                    } else if (response.m_istatus == -1) {
                        ToastUtil.showToast(mContext, R.string.can_not_bind_yourself);
                    } else {
                        ToastUtil.showToast(mContext, R.string.bind_fail);
                    }
                } else {
                    ToastUtil.showToast(mContext, R.string.bind_fail);
                }
            }
        });
    }
    private  int second;
    ArrayList<ScrollGraphBean.ScrollGraphData> scrollDatas;
    /**
     * 轮播
     */
    private void getScrollGraph() {
        Map<String, String> paramMap = new HashMap<>();
        paramMap.put("userId", getUserId());
        OkHttpUtils.post().url(ChatApi.SCROLL_GRAPH)
                .addParams("param", ParamUtil.getParam(paramMap))
                .build().execute(new AjaxCallback<BaseResponse<ScrollGraphBean>>() {
            @Override
            public void onResponse(BaseResponse<ScrollGraphBean> response, int id) {
                if (response != null && response.m_istatus == NetCode.SUCCESS) {
                    second=response.m_object.seconds;
                    if (response.m_object.scrollgraph!=null&&response.m_object.scrollgraph.size()>0){
                        scrollDatas=response.m_object.scrollgraph;
                        i=0;
                        playAnimation();
                    }else {
                        i=0;
                        playAnimation();
                    }


                }
            }
        });
    }
    /**
     * 播放动画
     */
    int i=0;
    private void playAnimation() {
        //---动画一：实现向上移动并逐渐显示
//        mRoolView.setVisibility(View.VISIBLE);
        final ObjectAnimator move = ObjectAnimator.ofFloat(mRoolView, "translationX", getWindowManager().getDefaultDisplay().getWidth() , 0);
//        final ObjectAnimator animatInOut = ObjectAnimator.ofFloat(rollView, "alpha", 0f, 1f);

        AnimatorSet animationSet = new AnimatorSet();
        animationSet.play(move);
        animationSet.setDuration(5000);
        animationSet.start();

        //---动画二：停顿一段时间，实现向上移动并逐渐消失
        //---(注：)
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                ObjectAnimator move2 = ObjectAnimator.ofFloat(mRoolView, "translationX", 0,-getWindowManager().getDefaultDisplay().getWidth());
//                ObjectAnimator animatInOut2 = ObjectAnimator.ofFloat(rollView, "alpha", 1f, 0f, 0f);

                AnimatorSet animationSet2 = new AnimatorSet();
                animationSet2.play(move2);
                animationSet2.setDuration(1000);
                animationSet2.addListener(new Animator.AnimatorListener() {
                    @Override
                    public void onAnimationStart(Animator animation) {

                    }

                    /**
                     * 实现循环滚动...
                     * @param animation
                     */
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        i++;
                        //如果滚动到最后一条，则重新由第一条开始滚动...
                        if (mRoolView==null)return;
                        mRoolView.setVisibility(View.VISIBLE);
                        if (scrollDatas==null)return;
                        if (i >= scrollDatas.size()) {
                           getScrollGraph();
                        }else {
                            GlideBuilder builder=new GlideBuilder(MainActivity.this);
                            Glide.with(MainActivity.this).load(scrollDatas.get(i).t_handImg).placeholder(R.mipmap.logo).error(R.mipmap.logo).into(mRoolHead);
                            mRoolId.setText(scrollDatas.get(i).t_userid);
                            if (scrollDatas.get(i).t_type.equals("1")){
                                mRoolType.setText("送出了");
                                mRoolGit.setText(scrollDatas.get(i).t_giftname);
                                mRoolYuan.setVisibility(View.GONE);

                            }else {
                                mRoolType.setText("充值了");
                                mRoolGit.setText(scrollDatas.get(i).t_money);
                                mRoolYuan.setVisibility(View.VISIBLE);
                            };
                            mRoolId.setText(scrollDatas.get(i).t_userid);
                            playAnimation();
                        }
                    }

                    @Override
                    public void onAnimationCancel(Animator animation) {

                    }

                    @Override
                    public void onAnimationRepeat(Animator animation) {

                    }
                });
                animationSet2.start();
            }
        }, 10000);
    }
}
