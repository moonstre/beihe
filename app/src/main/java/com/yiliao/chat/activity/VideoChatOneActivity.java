package com.yiliao.chat.activity;

import android.Manifest;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.annotation.SuppressLint;
import android.app.AlarmManager;
import android.app.Dialog;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Point;
import android.media.MediaPlayer;
import android.os.Build;
import android.os.CountDownTimer;
import android.os.Handler;
import android.support.constraint.ConstraintLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.OrientationHelper;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.OvershootInterpolator;
import android.view.animation.TranslateAnimation;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.bumptech.glide.Glide;
import com.bumptech.glide.gifdecoder.GifDecoder;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.bumptech.glide.load.resource.gif.GifDrawable;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.GlideDrawableImageViewTarget;
import com.bumptech.glide.request.target.Target;
import com.faceunity.wrapper.faceunity;
import com.opensource.svgaplayer.SVGACallback;
import com.opensource.svgaplayer.SVGAImageView;
import com.opensource.svgaplayer.SVGAParser;
import com.opensource.svgaplayer.SVGAVideoEntity;
import com.wushuangtech.bean.TTTVideoFrame;
import com.wushuangtech.bean.VideoCompositingLayout;
import com.wushuangtech.library.Constants;
import com.wushuangtech.wstechapi.TTTRtcEngine;
import com.wushuangtech.wstechapi.TTTRtcEngineEventHandler;
import com.wushuangtech.wstechapi.model.PublisherConfiguration;
import com.wushuangtech.wstechapi.model.VideoCanvas;
import com.yiliao.chat.BuildConfig;
import com.yiliao.chat.R;
import com.yiliao.chat.adapter.GiftViewPagerRecyclerAdapter;
import com.yiliao.chat.adapter.GoldGridRecyclerAdapter;
import com.yiliao.chat.adapter.VideoChatTextRecyclerAdapter;
import com.yiliao.chat.base.BaseActivity;
import com.yiliao.chat.base.BaseListResponse;
import com.yiliao.chat.base.BaseResponse;
import com.yiliao.chat.bean.BalanceBean;
import com.yiliao.chat.bean.CustomMessageBean;
import com.yiliao.chat.bean.GiftBean;
import com.yiliao.chat.bean.GoldBean;
import com.yiliao.chat.bean.PersonBean;
import com.yiliao.chat.bean.UserGoldBean;
import com.yiliao.chat.constant.ChatApi;
import com.yiliao.chat.constant.Constant;
import com.yiliao.chat.fulive.BeautificationParams;
import com.yiliao.chat.fulive.FuliveSaveBean;
import com.yiliao.chat.fulive.FuliveUtil;
import com.yiliao.chat.gif.CommonCallback;
import com.yiliao.chat.gif.GifCacheUtil;
import com.yiliao.chat.helper.ImageLoadHelper;
import com.yiliao.chat.helper.SharedPreferenceHelper;
import com.yiliao.chat.layoutmanager.ViewPagerLayoutManager;
import com.yiliao.chat.listener.OnViewPagerListener;
import com.yiliao.chat.net.AjaxCallback;
import com.yiliao.chat.net.NetCode;
import com.yiliao.chat.util.DevicesUtil;
import com.yiliao.chat.util.LogUtil;
import com.yiliao.chat.util.ParamUtil;
import com.yiliao.chat.util.TimeUtil;
import com.yiliao.chat.util.ToastUtil;
import com.yiliao.chat.view.SpreadView;
import com.zhy.http.okhttp.OkHttpUtils;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

import butterknife.BindView;
import butterknife.OnClick;
import cn.jpush.im.android.api.JMessageClient;
import cn.jpush.im.android.api.content.CustomContent;
import cn.jpush.im.android.api.content.TextContent;
import cn.jpush.im.android.api.enums.ContentType;
import cn.jpush.im.android.api.enums.ConversationType;
import cn.jpush.im.android.api.event.MessageEvent;
import cn.jpush.im.android.api.model.Conversation;
import cn.jpush.im.android.api.model.Message;
import cn.jpush.im.android.api.model.UserInfo;
import cn.jpush.im.android.api.options.MessageSendingOptions;
import cn.jpush.im.api.BasicCallback;
import cn.tillusory.sdk.TiSDKManager;
import cn.tillusory.sdk.bean.TiRotation;
import okhttp3.Call;

import static com.wushuangtech.library.Constants.CHANNEL_PROFILE_COMMUNICATION;
import static com.wushuangtech.library.Constants.CHANNEL_PROFILE_LIVE_BROADCASTING;
import static com.wushuangtech.library.Constants.CLIENT_ROLE_ANCHOR;
import static com.wushuangtech.library.Constants.CLIENT_ROLE_BROADCASTER;
import static com.wushuangtech.library.Constants.ERROR_ENTER_ROOM_BAD_VERSION;
import static com.wushuangtech.library.Constants.ERROR_ENTER_ROOM_TIMEOUT;
import static com.wushuangtech.library.Constants.ERROR_ENTER_ROOM_UNKNOW;
import static com.wushuangtech.library.Constants.ERROR_ENTER_ROOM_VERIFY_FAILED;
import static com.wushuangtech.library.Constants.LOG_FILTER_OFF;

/*
 * Copyright (C) 2018
 * 版权所有
 *
 * 功能描述：视频接通页面一 使用三体云
 * 作者：
 * 创建时间：2018/10/29
 *
 * 修改人：
 * 修改描述：
 * 修改日期
 */
public class VideoChatOneActivity extends BaseActivity {

    //规则
    @BindView(R.id.rule_ll)
    View mRuleLl;
    //头像
    @BindView(R.id.head_ll)
    View mHeadLl;
    @BindView(R.id.head_iv)
    ImageView mHeadIv;
    @BindView(R.id.spreadView)
    SpreadView mSpreadView;
    //昵称
    @BindView(R.id.name_tv)
    TextView mNameTv;
    //计时
    @BindView(R.id.des_tv)
    TextView mDesTv;
    //操作按钮部分
    @BindView(R.id.close_micro_iv)
    ImageView mCloseMicroIv;
    //礼物相关
    @BindView(R.id.gift_ll)
    LinearLayout mGiftLl;
    @BindView(R.id.gift_head_iv)
    ImageView mGiftHeadIv;
    @BindView(R.id.gift_des_tv)
    TextView mGiftDesTv;
    @BindView(R.id.gift_iv)
    ImageView mGiftIv;
    @BindView(R.id.gift_number_tv)
    TextView mGiftNumberTv;
    //远端
    @BindView(R.id.remote_fl)
    ConstraintLayout mRemoteFl;
    @BindView(R.id.content_fl)
    ConstraintLayout mContentFl;
    //关闭摄像头
    @BindView(R.id.close_video_iv)
    ImageView mCloseVideoIv;
    //本地视图
    @BindView(R.id.big_cover_black_v)
    View mBigCoverBlackV;
    @BindView(R.id.small_cover_black_v)
    View mSmallCoverBlackV;
    //已关闭提示
    @BindView(R.id.have_off_camera_tv)
    TextView mHaveOffCameraTv;
    //文字消息部分
    @BindView(R.id.input_ll)
    LinearLayout mInputLl;
    @BindView(R.id.input_et)
    EditText mInputEt;
    @BindView(R.id.text_list_rv)
    RecyclerView mTextListRv;
    //呼叫  接通后
    @BindView(R.id.little_ll)
    LinearLayout mLittleLl;
    @BindView(R.id.hang_up_tv)
    TextView mHangUpTv;
    //呼叫时用户端操作摄像头
    @BindView(R.id.camera_ll)
    LinearLayout mCameraLl;
    @BindView(R.id.camera_iv)
    ImageView mCameraIv;
    @BindView(R.id.camera_tv)
    TextView mCameraTv;
    @BindView(R.id.message_iv)
    ImageView ivMessage;
    @BindView(R.id.layoutGold)
    LinearLayout layoutGold;
    @BindView(R.id.tvGoldTime)
    TextView tvGoldTime;
    @BindView(R.id.tvGoldConsume)
    TextView tvGoldConsume;
    @BindView(R.id.tvGoldReward)
    TextView tvGoldReward;
    @BindView(R.id.tvGold)
    TextView tvGold;
    @BindView(R.id.ll_gold_num)
    LinearLayout ll_gold_num;
    @BindView(R.id.enter_room_gif)
    ImageView enter_room_gif;
    @BindView(R.id.gift_svga)
    SVGAImageView gift_svga;
    @BindView(R.id.reward_iv)
    ImageView reward_iv;
    private UserGoldBean mUserGoldBean;//对方余额对象

    private int mRoomId;
    private int mFromType;//是用户发起聊天,还是主播接通聊天
    private int mActorId;//主播id(之后改成了对方ID)
    private MediaPlayer mPlayer;
    //礼物相关
    private List<GiftBean> mGiftBeans = new ArrayList<>();
    private int mMyGoldNumber;
    //计时器
    private Handler mHandler = new Handler();
    private long mCurrentSecond = 0;//当前毫秒数
    //通知主播  我送礼物了

    //是否挂断了
    private boolean mHaveHangUp = false;
    //是用户进来的时候
    //private String mActorNickName = "";
    //记录单次动画时间内用户赠送的礼物
    private int mSingleTimeSendGiftCount = 0;
    //30秒计时器 自动挂断
    private CountDownTimer mAutoHangUpTimer;

    //视频聊天相关
    private TTTRtcEngine mTttRtcEngine;
    private boolean mHaveUserJoin = false;
    private SurfaceView mLocalSurfaceView;
    private SurfaceView mRemoteSurfaceView;
    private boolean mHaveSwitchBigToSmall = false;//已经从大图切到小图
    private boolean mUserSelfMute = false;//用户端自己mute了
    private boolean mUserHaveMute = false;//主播端  用户mute了
    //文字聊天
    private VideoChatTextRecyclerAdapter mTextRecyclerAdapter;
    //美颜
    private TiSDKManager mTiSDKManager;

    // 句柄索引
    private static final int ITEM_ARRAYS_FACE_BEAUTY_INDEX = 0;

    // 句柄数量
    private static final int ITEM_ARRAYS_COUNT = 12;
    //美颜和其他道具的handle数组
    private volatile int[] mItemsArray = new int[ITEM_ARRAYS_COUNT];
    private int mFrameId = 0;

    private final Timer timer = new Timer();
    private TimerTask task;
    Handler handler = new Handler() {
        @Override
        public void handleMessage(android.os.Message msg) {
//            Log.i("aaa","tvGoldConsume"+(Float.parseFloat(tvGoldConsume.getText().toString()) + mUserGoldBean.t_video_gold) + "");
            tvGoldConsume.setText((Float.parseFloat(tvGoldConsume.getText().toString()) + mUserGoldBean.t_video_gold) + "");
            float recharge_money = Float.parseFloat(tvGold.getText().toString());
            recharge_money = recharge_money - mUserGoldBean.t_video_gold;
//            Log.i("aaa","recharge_money"+recharge_money+"----t_video_gold"+mUserGoldBean.t_video_gold);
            if (recharge_money <= 0) {
                tvGold.setText("0.0");
                if (!mHaveHangUp){
                    ToastUtil.show("对方余额不足");
                }

            } else {
                tvGold.setText(recharge_money + "");
            }
            super.handleMessage(msg);
        }
    };

    @Override
    protected View getContentView() {
        return inflate(R.layout.activity_video_chat_one_layout);
    }

    @Override
    protected boolean supportFullScreen() {
        // 禁止截屏
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_SECURE);

        //禁止录屏
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_SECURE, WindowManager.LayoutParams.FLAG_SECURE);


        return true;
    }

    @Override
    protected void onContentAdded() {
        checkPermission();
        needHeader(false);

        mFromType = getIntent().getIntExtra(Constant.FROM_TYPE, Constant.FROM_USER);
        mRoomId = getIntent().getIntExtra(Constant.ROOM_ID, 0);
        mActorId = getIntent().getIntExtra(Constant.ACTOR_ID, 0);//对方ID
        if (mFromType == 1) {
            reward_iv.setVisibility(View.VISIBLE);
        }
        if (Constant.hideChatOnTelephone()) {
            ivMessage.setVisibility(View.GONE);
        }
        initViewShow();
        initTextChat();
        //注册sdk的event用于接收各种event事件
        JMessageClient.registerEventReceiver(this);
        initHelper();
        dealCrash();
        //获取礼物列表
        getGiftList();
        //获取对方信息
        getActorInfo(mActorId);

        if (Constant.showGoldOnChat() && getUserRole() == 1) {
            getUserGold(mActorId);
        }

        //avga动态图回调
        gift_svga.setCallback(new SVGACallback() {
            @Override
            public void onPause() {

            }

            @Override
            public void onFinished() {

                gift_svga.setVisibility(View.GONE);
            }

            @Override
            public void onRepeat() {

            }

            @Override
            public void onStep(int i, double v) {

            }
        });
    }

    /**
     * 初始化界面显示
     */
    private void initViewShow() {
        if (mFromType == Constant.FROM_USER) {//是用户进来创建房间,预览视频
            initAutoCountTimer();
            if (Constant.hideHomeNearAndNew()) {
                mRuleLl.setVisibility(View.GONE);
            } else {
                mRuleLl.setVisibility(View.VISIBLE);
            }
            mHeadLl.setVisibility(View.VISIBLE);
            mDesTv.setText(getResources().getString(R.string.apply_chat));

            //用户进来创建房间,默认显示操作摄像头布局, 显示呼叫的时候布局
            mCameraLl.setVisibility(View.VISIBLE);
            mUserSelfMute = SharedPreferenceHelper.getMute(getApplicationContext());
            if (!mUserSelfMute) {//缓存中摄像头是关闭的
                mCameraIv.setSelected(false);
                mCameraTv.setText(getResources().getString(R.string.off_camera));
                mCloseVideoIv.setSelected(false);
            } else {
                mCameraIv.setSelected(true);
                mCameraTv.setText(getResources().getString(R.string.open_camera));
                mCloseVideoIv.setSelected(true);
            }
        } else if (mFromType == Constant.FROM_ACTOR) {//是主播进来,加入房间
            closeView();
            //主播加入进来不显示操作摄像头,隐藏呼叫时布局
            if (Constant.hideHomeNearAndNew()) {
                mRuleLl.setVisibility(View.GONE);
            }
            mHangUpTv.setVisibility(View.GONE);
            mLittleLl.setVisibility(View.VISIBLE);
            mCloseVideoIv.setVisibility(View.GONE);
            //是主播进来的时候  用户的头像 昵称
        } else if (mFromType == Constant.FROM_ACTOR_INVITE) {//是主播邀请用户进来,创建房间
            if (Constant.hideHomeNearAndNew()) {
                mRuleLl.setVisibility(View.GONE);
            } else {
                mRuleLl.setVisibility(View.VISIBLE);
            }
            mHeadLl.setVisibility(View.VISIBLE);
            mDesTv.setText(getResources().getString(R.string.apply_chat));
            //主播进来不显示操作摄像头,隐藏呼叫时布局,显示小布局按钮,根据布局默认 不用做任何操作
            mCloseVideoIv.setVisibility(View.GONE);

            //是主播邀请进来的时候  顶部用户信息和礼物布局用户的头像 昵称

            initAutoCountTimer();
        } else if (mFromType == Constant.FROM_USER_JOIN) {//是用户受主播邀请加入房间
            closeView();
            //设置礼物相关布局

            //用户受主播邀请加入房间, 显示小按钮, 隐藏呼叫时的挂断 操作摄像头布局
            mCameraLl.setVisibility(View.GONE);
            mLittleLl.setVisibility(View.VISIBLE);
            mHangUpTv.setVisibility(View.GONE);
            mUserSelfMute = SharedPreferenceHelper.getMute(getApplicationContext());
            if (!mUserSelfMute) {//mute 为true,缓存中摄像头是关闭的
                mCloseVideoIv.setSelected(false);
            } else {
                mCloseVideoIv.setSelected(true);
            }
        }
    }

    /**
     * 初始化文字聊天
     */
    private void initTextChat() {
        mTextRecyclerAdapter = new VideoChatTextRecyclerAdapter(this);
        LinearLayoutManager manager = new LinearLayoutManager(this);
        mTextListRv.setLayoutManager(manager);
        mTextListRv.setAdapter(mTextRecyclerAdapter);
        mInputEt.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if (keyCode == android.view.KeyEvent.KEYCODE_ENTER
                        && event.getAction() == android.view.KeyEvent.ACTION_DOWN) {
                    sendTextMessage();
                    return true;
                }
                return false;
            }
        });
        mCloseMicroIv.setSelected(false);
    }

    /**
     * 自动挂断
     */
    private void initAutoCountTimer() {
        if (mAutoHangUpTimer == null) {
            mAutoHangUpTimer = new CountDownTimer((long) (30 * 1000), 1000) {

                @Override
                public void onTick(long millisUntilFinished) {
                }

                @Override
                public void onFinish() {
                    ToastUtil.showToast(getApplication(), R.string.no_response);
                    hangUp(mRoomId+"",2+"");
                }
            };
            mAutoHangUpTimer.start();
        }
    }

    /**
     * 取消timer
     */
    private void cancelAutoTimer() {
        if (mAutoHangUpTimer != null) {
            mAutoHangUpTimer.cancel();
            mAutoHangUpTimer = null;
        }
    }

    /**
     * 关闭View
     */
    private void closeView() {
        mRuleLl.setVisibility(View.GONE);
        mHeadLl.setVisibility(View.GONE);

        if (mSpreadView != null) {
            mSpreadView.stopAnim();
            mSpreadView = null;
        }
    }

    /**
     * 播放音频
     */
    private void playMusic() {
        try {
            if (mPlayer == null) {
                mPlayer = MediaPlayer.create(getApplicationContext(), R.raw.call_come);
                mPlayer.setLooping(true);
                mPlayer.start();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 初始化helper
     */
    private void initHelper() {
        //生成默认参数渲染器
        if (BuildConfig.beautySDK == 1) {
            mTiSDKManager = new TiSDKManager();
        } else if (BuildConfig.beautySDK == 2) {
            int itemBeauty = FuliveUtil.loadItem(mContext, Constant.BUNDLE_FACE_BEAUTIFICATION);
            if (itemBeauty <= 0) {
                return;
            }
            mItemsArray[ITEM_ARRAYS_FACE_BEAUTY_INDEX] = itemBeauty;

            initXX();
        }
        mTttRtcEngine = TTTRtcEngine.create(getApplicationContext(), BuildConfig.tttAppId, false,
                new TTTRtcEngineEventHandler() {
                    @Override
                    public void onError(int errorType) {

                        super.onError(errorType);
                        LogUtil.i("回调onError用户加入: errorType = " + errorType);

                        if (errorType == ERROR_ENTER_ROOM_TIMEOUT) {
                            LogUtil.i("超时，10秒未收到服务器返回结果");
                        } else if (errorType == ERROR_ENTER_ROOM_UNKNOW) {
                            LogUtil.i("无法连接服务器");
                        } else if (errorType == ERROR_ENTER_ROOM_VERIFY_FAILED) {
                            LogUtil.i("验证码错误");
                        } else if (errorType == ERROR_ENTER_ROOM_BAD_VERSION) {
                            LogUtil.i("版本错误");
                        } else if (errorType == 6) {
                            LogUtil.i("该直播间不存在");
                        }
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                if (!isFinishing()) {
                                    ToastUtil.showToast(getApplicationContext(), R.string.connect_fail);
                                }
                            }
                        });
                        if (mFromType == Constant.FROM_USER || mFromType == Constant.FROM_ACTOR_INVITE) {
                            cancelAutoTimer();
                        }
                        if (mTttRtcEngine != null) {
                            mTttRtcEngine.leaveChannel();
                            mTttRtcEngine = null;
                            finish();
                        }
                    }

                    @Override
                    public void onReconnectServerFailed() {
                        super.onReconnectServerFailed();
                        LogUtil.i("回调onReconnectServerFailed用户加入: channel = ");

                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                if (!isFinishing()) {
                                    ToastUtil.showToast(getApplicationContext(), R.string.connect_fail);
                                }
                            }
                        });
                        if (mFromType == Constant.FROM_USER || mFromType == Constant.FROM_ACTOR_INVITE) {
                            cancelAutoTimer();
                        }
                        if (mTttRtcEngine != null) {
                            mTttRtcEngine.leaveChannel();
                            mTttRtcEngine = null;
                            finish();
                        }
                    }

                    @Override
                    public void onJoinChannelSuccess(String channel, long uid) {
                        super.onJoinChannelSuccess(channel, uid);
                        LogUtil.i("回调onJoinChannelSuccess用户加入: channel = " + channel + "  uid: " + uid);

                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                //播放声音
                                if (mFromType == Constant.FROM_USER || mFromType == Constant.FROM_ACTOR_INVITE) {
                                    playMusic();
                                }
                                uploadSelfVideo();
                            }
                        });
                    }

                    @Override
                    public void onUserJoined(final long nUserId, int identity) {
                        super.onUserJoined(nUserId, identity);
                        LogUtil.i("回调onUserJoined用户加入: nUserId = " + nUserId + "  identity: " + identity);
                        if (!mHaveUserJoin) {
                            mHaveUserJoin = true;
                            mHandler.postDelayed(mTimeRunnable, 1000);
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    resetFulive();

                                    if (mFromType == Constant.FROM_USER || mFromType == Constant.FROM_USER_JOIN) {//用户端开始计时
                                        startCountTime();
                                        if (!mUserSelfMute) {
                                            mBigCoverBlackV.setVisibility(View.VISIBLE);
                                            mTttRtcEngine.enableLocalVideo(mUserSelfMute);
                                        }
                                    }
                                    //显示小按钮  隐藏呼叫
                                    mLittleLl.setVisibility(View.VISIBLE);
                                    mCameraLl.setVisibility(View.GONE);
                                    mHangUpTv.setVisibility(View.GONE);

                                    mRemoteSurfaceView = mTttRtcEngine.CreateRendererView(getApplicationContext());
                                    mTttRtcEngine.setupRemoteVideo(new VideoCanvas(nUserId, Constants.RENDER_MODE_HIDDEN, mRemoteSurfaceView));

                                    switchBigAndSmall(!mHaveSwitchBigToSmall);

                                    //如果ttt角色是主播,推流
                                    if (mFromType == Constant.FROM_USER || mFromType == Constant.FROM_ACTOR_INVITE) {
                                        //关闭头像闪烁
                                        closeView();
                                        //取消30秒
                                        cancelAutoTimer();
                                        //关闭头像闪烁
                                        if (mPlayer != null) {
                                            mPlayer.stop();
                                            mPlayer = null;
                                        }
                                    }

                                    // 发送SEI
                                    VideoCompositingLayout layout = new VideoCompositingLayout();
                                    layout.regions = buildRemoteLayoutLocation();
                                    TTTRtcEngine.getInstance().setVideoCompositingLayout(layout);
                                }
                            });
                        }
                    }

                    @Override
                    public void onUserOffline(long nUserId, int reason) {
                        super.onUserOffline(nUserId, reason);
                        LogUtil.i("回调onUserOffline用户离线" + nUserId);
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                if (!isFinishing()) {
                                    if (mTttRtcEngine != null && !mHaveHangUp) {
                                        mHaveHangUp = true;
                                        ToastUtil.showToast(getApplicationContext(), R.string.have_hang_up);
                                        mTttRtcEngine.leaveChannel();
                                        mTttRtcEngine = null;
                                        finish();
                                    }
                                }
                            }
                        });
                    }

                    public void onUserEnableVideo(final long uid, final boolean muted) {
                        super.onUserEnableVideo(uid, muted);
                        LogUtil.i("回调onUserEnableVideo改变: UID " + uid + "  muted:  " + muted);
                        //主播监听对方
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                //处理主播端情况
                                if (mFromType == Constant.FROM_ACTOR || mFromType == Constant.FROM_ACTOR_INVITE) {
                                    if (uid != Long.parseLong(getUserId())) {
                                        mUserHaveMute = muted;
                                        if (mHaveSwitchBigToSmall) {//主播端大图显示的用户
                                            if (muted) {
                                                mBigCoverBlackV.setVisibility(View.VISIBLE);
                                                mSmallCoverBlackV.setVisibility(View.GONE);
                                                mHaveOffCameraTv.setVisibility(View.VISIBLE);
                                            } else {
                                                mRemoteSurfaceView.setVisibility(View.VISIBLE);
                                                mBigCoverBlackV.setVisibility(View.GONE);
                                                mSmallCoverBlackV.setVisibility(View.GONE);
                                                mHaveOffCameraTv.setVisibility(View.GONE);
                                            }
                                        } else {//主播端小图显示的用户
                                            if (muted) {
                                                mRemoteSurfaceView.setVisibility(View.INVISIBLE);
                                                mSmallCoverBlackV.setVisibility(View.VISIBLE);
                                                mBigCoverBlackV.setVisibility(View.GONE);
                                                mHaveOffCameraTv.setVisibility(View.GONE);
                                            } else {
                                                mRemoteSurfaceView.setVisibility(View.VISIBLE);
                                                mBigCoverBlackV.setVisibility(View.GONE);
                                                mSmallCoverBlackV.setVisibility(View.GONE);
                                                mHaveOffCameraTv.setVisibility(View.GONE);
                                            }
                                        }
                                    }
                                }
                            }
                        });
                    }

                    @Override
                    public void onLocalVideoFrameCaptured(TTTVideoFrame frame) {
//                        LogUtil.i("回调onLocalVideoFrameCaptured改变: frame " + frame );

                        if (BuildConfig.beautySDK == 0) {
                            super.onLocalVideoFrameCaptured(frame);
                        }
                        if (BuildConfig.beautySDK == 2) {
                            frame.textureID = faceunity.fuRenderToTexture(frame.textureID, frame.stride, frame.height, mFrameId++, mItemsArray, 0);
                        }
                        if (mTiSDKManager != null) {
                            frame.textureID = mTiSDKManager.renderTexture2D(frame.textureID, frame.stride,
                                    frame.height, TiRotation.CLOCKWISE_ROTATION_0, true);
                        }
                    }

                });
        if (mTttRtcEngine != null) {
            mTttRtcEngine.setLogFilter(LOG_FILTER_OFF);
            mTttRtcEngine.enableVideo();
            mTttRtcEngine.enableLocalVideo(true);
            mTttRtcEngine.setChannelProfile(CHANNEL_PROFILE_LIVE_BROADCASTING);
            mTttRtcEngine.enableCrossRoom(true);
            mTttRtcEngine.muteLocalAudioStream(false);
            //推流地址
//            String pushUrl = BuildConfig.tttPushAddress + getUserId() + "/" + String.valueOf(mRoomId);
            String pushUrl = BuildConfig.tttPushAddress + getUserId();
            LogUtil.i("推流地址: " + pushUrl);
            PublisherConfiguration configuration = new PublisherConfiguration();
            configuration.setPushUrl(pushUrl);
            mTttRtcEngine.configPublisher(configuration);
            mTttRtcEngine.setClientRole(CLIENT_ROLE_ANCHOR);
//            if (mFromType==Constant.FROM_USER||mFromType==Constant.FROM_ACTOR_INVITE){
//                mTttRtcEngine.setClientRole(CLIENT_ROLE_ANCHOR);
//            }else {
//                mTttRtcEngine.setClientRole(CLIENT_ROLE_BROADCASTER);
//
//            }
            mTttRtcEngine.joinChannel("", String.valueOf(mRoomId), Integer.parseInt(getUserId()));
        }
    }

    /**
     * 上传自己的流
     */
    private void uploadSelfVideo() {
        try {
            mLocalSurfaceView = mTttRtcEngine.CreateRendererView(this);
            mTttRtcEngine.setupLocalVideo(new VideoCanvas(Integer.parseInt(getUserId()),
                    Constants.RENDER_MODE_HIDDEN, mLocalSurfaceView), getRequestedOrientation());
            mContentFl.addView(mLocalSurfaceView);
            SurfaceHolder holder = mLocalSurfaceView.getHolder();
            holder.addCallback(new SurfaceHolder.Callback() {
                @Override
                public void surfaceCreated(SurfaceHolder holder) {

                }

                @Override
                public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {

                }

                @Override
                public void surfaceDestroyed(SurfaceHolder holder) {
                    if (mTiSDKManager != null) {
                        mTiSDKManager.destroy();
                    }
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 切换大小视图
     *
     * @param isBigToSmall true 本地视图是大图
     */
    private void switchBigAndSmall(boolean isBigToSmall) {
        resetFulive();
        try {
            mContentFl.removeAllViews();
            mRemoteFl.removeAllViews();
            if (isBigToSmall) {
                mHaveSwitchBigToSmall = true;
                mRemoteFl.addView(mLocalSurfaceView);
                mContentFl.addView(mRemoteSurfaceView);

                mLocalSurfaceView.setZOrderMediaOverlay(true);
                mLocalSurfaceView.setZOrderOnTop(true);
                mRemoteSurfaceView.setZOrderMediaOverlay(false);
                mRemoteSurfaceView.setZOrderOnTop(false);

                //用户切换
                //用户自己是在小图, 如果mute暂停了,小图就显示黑
                if (mFromType == Constant.FROM_USER || mFromType == Constant.FROM_USER_JOIN) {
                    if (!mUserSelfMute) {
                        mLocalSurfaceView.setVisibility(View.INVISIBLE);
                        mSmallCoverBlackV.setVisibility(View.VISIBLE);
                        mBigCoverBlackV.setVisibility(View.GONE);
                    }
                    //主播切换
                } else if (mFromType == Constant.FROM_ACTOR || mFromType == Constant.FROM_ACTOR_INVITE) {
                    if (mUserHaveMute) {
                        mBigCoverBlackV.setVisibility(View.VISIBLE);
                        mSmallCoverBlackV.setVisibility(View.GONE);
                        mHaveOffCameraTv.setVisibility(View.VISIBLE);
                    }
                }
            } else {
                mHaveSwitchBigToSmall = false;
                mRemoteFl.addView(mRemoteSurfaceView);
                mContentFl.addView(mLocalSurfaceView);

                mLocalSurfaceView.setZOrderMediaOverlay(false);
                mLocalSurfaceView.setZOrderOnTop(false);
                mRemoteSurfaceView.setZOrderMediaOverlay(true);
                mRemoteSurfaceView.setZOrderOnTop(true);

                //用户自己是在大图, 如果mute暂停了,大图就显示黑
                //用户切换
                if (mFromType == Constant.FROM_USER || mFromType == Constant.FROM_USER_JOIN) {
                    if (!mUserSelfMute) {
                        mBigCoverBlackV.setVisibility(View.VISIBLE);
                        mSmallCoverBlackV.setVisibility(View.GONE);
                    }
                } else if (mFromType == Constant.FROM_ACTOR || mFromType == Constant.FROM_ACTOR_INVITE) {
                    if (mUserHaveMute) {
                        mRemoteSurfaceView.setVisibility(View.INVISIBLE);
                        mSmallCoverBlackV.setVisibility(View.VISIBLE);
                        mBigCoverBlackV.setVisibility(View.GONE);
                        mHaveOffCameraTv.setVisibility(View.GONE);
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            mRemoteFl.setEnabled(true);
        }
    }

    /**
     * 开始本地计时
     */
    private Runnable mTimeRunnable = new Runnable() {
        @Override
        public void run() {
            mCurrentSecond = mCurrentSecond + 1000;
            mDesTv.setText(TimeUtil.getFormatHMS(mCurrentSecond));
            tvGoldTime.setText(TimeUtil.getFormatHMS(mCurrentSecond));
            //递归调用本run able对象，实现每隔一秒一次执行任务
            mHandler.postDelayed(this, 1000);
        }
    };

    /**
     * 开始计时
     */
    private void startCountTime() {
        Map<String, String> paramMap = new HashMap<>();
        paramMap.put("anthorId", String.valueOf(mActorId));
        paramMap.put("userId", getUserId());
        paramMap.put("roomId", String.valueOf(mRoomId));
        OkHttpUtils.post().url(ChatApi.VIDEO_CHAT_BIGIN_TIMING)
                .addParams("param", ParamUtil.getParam(paramMap))
                .build().execute(new AjaxCallback<BaseResponse>() {
            @Override
            public void onResponse(BaseResponse response, int id) {
                if (response != null && response.m_istatus == NetCode.SUCCESS) {
                    LogUtil.i("开始计时成功");
                } else {
                    ToastUtil.showToast(getApplicationContext(), R.string.please_retry);
                }
            }
        });
    }

    /**
     * 获取主播的信息
     */
    private void getActorInfo(int actorId) {
        Map<String, String> paramMap = new HashMap<>();
        paramMap.put("userId", String.valueOf(actorId));
        OkHttpUtils.post().url(ChatApi.GET_PERSONAL_DATA)
                .addParams("param", ParamUtil.getParam(paramMap))
                .build().execute(new AjaxCallback<BaseResponse<PersonBean>>() {
            @Override
            public void onResponse(BaseResponse<PersonBean> response, int id) {
                if (response != null && response.m_istatus == NetCode.SUCCESS) {
                    PersonBean bean = response.m_object;
                    if (bean != null) {
                        //昵称
                        String nickName = bean.t_nickName;
                        if (!TextUtils.isEmpty(nickName)) {
                            //设置呼叫时,对方昵称
                            mNameTv.setText(nickName);
                        }
                    }
                }
            }
        });
    }

    @Override
    protected void onDestroy() {
        faceunity.fuDestroyAllItems();
        faceunity.fuOnDeviceLost();
        faceunity.fuDone();
        try {
            if (mTiSDKManager != null) {
                mTiSDKManager.destroy();
            }
            if (mTimeRunnable != null) {
                mTimeRunnable = null;
            }
            if (mHandler != null) {
                mHandler.removeCallbacksAndMessages(null);
                mHandler = null;
            }
            if (!mHaveHangUp) {//没调用过挂断
                LogUtil.i("onDestroy页面异常关闭挂断");
                pageCloseHangUp();
            }
            if (mFromType == Constant.FROM_USER || mFromType == Constant.FROM_ACTOR_INVITE) {
                //注销消息接收
                JMessageClient.unRegisterEventReceiver(this);
                cancelAutoTimer();
            }
            if (mSpreadView != null) {
                mSpreadView.stopAnim();
                mSpreadView = null;
            }
            if (mPlayer != null) {
                mPlayer.stop();
                mPlayer = null;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (task != null) {
            task.cancel();
        }
        super.onDestroy();
    }

    @OnClick({R.id.hang_up_iv, R.id.reward_iv, R.id.change_iv, R.id.content_fl, R.id.remote_fl,
            R.id.close_video_iv, R.id.message_iv, R.id.btn_send, R.id.hang_up_tv, R.id.camera_ll,
            R.id.close_micro_iv, R.id.text_cover_v})
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.reward_iv: {//打赏礼物
                showRewardDialog();
                break;
            }
            case R.id.change_iv: {//切换前后置
                if (mTttRtcEngine != null) {
                    mTttRtcEngine.switchCamera();
                }
                break;
            }
            case R.id.text_cover_v:
            case R.id.content_fl: {//显示或隐藏操作按钮
                if (mHeadLl.getVisibility() == View.VISIBLE) {
                    return;
                }
                if (mLittleLl.getVisibility() == View.VISIBLE) {
                    mLittleLl.setVisibility(View.INVISIBLE);
                    mTextListRv.setVisibility(View.GONE);
                    mInputLl.setVisibility(View.GONE);
                    mTextRecyclerAdapter.clearMessage();
                } else {
                    mLittleLl.setVisibility(View.VISIBLE);
                    mTextListRv.setVisibility(View.VISIBLE);
                }
                break;
            }
            case R.id.remote_fl: {//切换,点击小屏切换
                mRemoteFl.setEnabled(false);
                switchBigAndSmall(!mHaveSwitchBigToSmall);
                break;
            }
            case R.id.camera_ll: {//呼叫时 用户端操作摄像头按钮
                clickCameraCall();
                break;
            }
            case R.id.close_video_iv: {//操作摄像头(接通后)
                clickCamera();
                break;
            }
            case R.id.message_iv: {//文字消息
                if (mInputLl.getVisibility() != View.VISIBLE) {
                    mInputLl.setVisibility(View.VISIBLE);
                    showSpan();
                } else {
                    mInputLl.setVisibility(View.GONE);
                }
                break;
            }
            case R.id.btn_send: {//发送文字消息
                sendTextMessage();
                break;
            }
            case R.id.close_micro_iv: {//关闭麦克风
                clickMicro();
                break;
            }
            case R.id.hang_up_iv: {
                hangUp(mRoomId + "");
                break;
            }
            case R.id.hang_up_tv: {
                hangUp(mRoomId + "");
                break;
            }
        }
    }

    private void hangUp(String roomId) {
        Map<String, String> paramMap = new HashMap<>();
        paramMap.put("userId", getUserId());
        paramMap.put("type", String.valueOf(1));
        paramMap.put("roomId", String.valueOf(roomId));
        OkHttpUtils.post().url(ChatApi.HUANG_UP)
                .addParams("param", ParamUtil.getParam(paramMap))
                .build().execute(new AjaxCallback<BaseResponse>() {
            @Override
            public void onResponse(BaseResponse response, int id) {
                if (response != null) {
                    if (response.m_istatus == NetCode.SUCCESS) {
                        LogUtil.i("挂断成功", "response：" + response.toString());

                    } else if (response.m_istatus == -2) {//你拨打的用户正忙,请稍后再拨
                        String message = response.m_strMessage;
                        if (!TextUtils.isEmpty(message)) {
                            ToastUtil.showToast(getApplicationContext(), message);
                        } else {
                            ToastUtil.showToast(getApplicationContext(), R.string.busy_actor);
                        }
                    } else if (response.m_istatus == -1) {//对方不在线
                        String message = response.m_strMessage;
                        if (!TextUtils.isEmpty(message)) {
                            ToastUtil.showToast(getApplicationContext(), message);
                        } else {
                            ToastUtil.showToast(getApplicationContext(), R.string.not_online);
                        }
                    } else if (response.m_istatus == -3) {
                        String message = response.m_strMessage;
                        if (!TextUtils.isEmpty(message)) {
                            ToastUtil.showToast(getApplicationContext(), message);
                        } else {
                            ToastUtil.showToast(getApplicationContext(), R.string.not_bother);
                        }
                    } else if (response.m_istatus == -4) {

                    } else {
                        ToastUtil.showToast(getApplicationContext(), R.string.system_error);
                    }
                }
            }

            @Override
            public void onError(Call call, Exception e, int id) {
                super.onError(call, e, id);
                ToastUtil.showToast(getApplicationContext(), R.string.system_error);
            }


        });
    }
    private void hangUp(String roomId,String type) {
        Map<String, String> paramMap = new HashMap<>();
        paramMap.put("userId", getUserId());
        paramMap.put("type", type);
        paramMap.put("roomId", String.valueOf(roomId));
        OkHttpUtils.post().url(ChatApi.HUANG_UP)
                .addParams("param", ParamUtil.getParam(paramMap))
                .build().execute(new AjaxCallback<BaseResponse>() {
            @Override
            public void onResponse(BaseResponse response, int id) {
                finish();
                if (response != null) {
                    if (response.m_istatus == NetCode.SUCCESS) {
                        LogUtil.i("挂断成功", "response：" + response.toString());

                    } else if (response.m_istatus == -2) {//你拨打的用户正忙,请稍后再拨
                        String message = response.m_strMessage;
                        if (!TextUtils.isEmpty(message)) {
                            ToastUtil.showToast(getApplicationContext(), message);
                        } else {
                            ToastUtil.showToast(getApplicationContext(), R.string.busy_actor);
                        }
                    } else if (response.m_istatus == -1) {//对方不在线
                        String message = response.m_strMessage;
                        if (!TextUtils.isEmpty(message)) {
                            ToastUtil.showToast(getApplicationContext(), message);
                        } else {
                            ToastUtil.showToast(getApplicationContext(), R.string.not_online);
                        }
                    } else if (response.m_istatus == -3) {
                        String message = response.m_strMessage;
                        if (!TextUtils.isEmpty(message)) {
                            ToastUtil.showToast(getApplicationContext(), message);
                        } else {
                            ToastUtil.showToast(getApplicationContext(), R.string.not_bother);
                        }
                    } else if (response.m_istatus == -4) {

                    } else {
                        ToastUtil.showToast(getApplicationContext(), R.string.system_error);
                    }
                }
            }

            @Override
            public void onError(Call call, Exception e, int id) {
                super.onError(call, e, id);
                finish();
                ToastUtil.showToast(getApplicationContext(), R.string.system_error);
            }

        });
    }
    /**
     * 切换麦克风
     */
    private void clickMicro() {
        if (mCloseMicroIv.isSelected()) {//原来是关闭的
            mCloseMicroIv.setSelected(false);
            if (mTttRtcEngine != null) {
                //静音/取消静音。该方法用于允许/禁止往网络发送本地音频流。 	True麦克风静音，False取消静音
                mTttRtcEngine.muteLocalAudioStream(false);
            }
        } else {
            mCloseMicroIv.setSelected(true);
            if (mTttRtcEngine != null) {
                //静音/取消静音。该方法用于允许/禁止往网络发送本地音频流。 	True麦克风静音，False取消静音
                mTttRtcEngine.muteLocalAudioStream(true);
            }
        }
    }

    /**
     * 呼叫时 用户端操作摄像按钮
     */
    private void clickCameraCall() {
        LogUtil.i("挂断成功", "回调clickCameraCall：" + mCameraIv.isSelected());
        if (mCameraIv.isSelected()) {//如果摄像头是开启的
            mCameraIv.setSelected(false);
            mCameraTv.setText(getResources().getString(R.string.off_camera));
            mCloseVideoIv.setSelected(false);

            SharedPreferenceHelper.saveMute(getApplicationContext(), false);

            //如果大图显示的对方,小图是自己
            if (mHaveSwitchBigToSmall) {
                mLocalSurfaceView.setVisibility(View.INVISIBLE);
                mSmallCoverBlackV.setVisibility(View.VISIBLE);
                mBigCoverBlackV.setVisibility(View.GONE);
            } else {
                //大图是自己
                mBigCoverBlackV.setVisibility(View.VISIBLE);
                mSmallCoverBlackV.setVisibility(View.GONE);
            }

            //暂停本地视频流 True: 不发送本地视频流，False: 发送本地视频流
            if (mTttRtcEngine != null) {
                mTttRtcEngine.enableLocalVideo(false);
                mUserSelfMute = false;
            }
        } else {
            mCameraIv.setSelected(true);
            mCameraTv.setText(getResources().getString(R.string.open_camera));
            mCloseVideoIv.setSelected(true);

            SharedPreferenceHelper.saveMute(getApplicationContext(), true);

            //如果大图显示的是对方,小图是自己
            if (mHaveSwitchBigToSmall) {
                mBigCoverBlackV.setVisibility(View.GONE);
                mSmallCoverBlackV.setVisibility(View.GONE);
                mHaveOffCameraTv.setVisibility(View.GONE);
                mLocalSurfaceView.setVisibility(View.VISIBLE);
            } else {
                mBigCoverBlackV.setVisibility(View.GONE);
                mSmallCoverBlackV.setVisibility(View.GONE);
                mHaveOffCameraTv.setVisibility(View.GONE);
                mLocalSurfaceView.setVisibility(View.VISIBLE);
            }

            if (mTttRtcEngine != null) {
                mTttRtcEngine.enableLocalVideo(true);
                mUserSelfMute = true;
            }
        }
    }

    /**
     * 点击操作摄像头,只有用户可以操作
     */
    private void clickCamera() {
        Log.i("aaa", "回调clickCamera：" + mCloseVideoIv.isSelected());
        if (mCloseVideoIv.isSelected()) {//如果摄像头是开启的
            resetFulive();
            mCloseVideoIv.setSelected(false);
            SharedPreferenceHelper.saveMute(getApplicationContext(), false);
            //如果大图显示的对方,小图是自己
            if (mHaveSwitchBigToSmall) {
                mLocalSurfaceView.setVisibility(View.INVISIBLE);
                mSmallCoverBlackV.setVisibility(View.VISIBLE);
                mBigCoverBlackV.setVisibility(View.GONE);
            } else {
                //大图是自己
                mBigCoverBlackV.setVisibility(View.VISIBLE);
                mSmallCoverBlackV.setVisibility(View.GONE);
            }
            //暂停本地视频流  true是启用，false是禁用
            if (mTttRtcEngine != null) {
                mTttRtcEngine.enableLocalVideo(false);
                mUserSelfMute = false;
            }
        } else {
            mCloseVideoIv.setSelected(true);

            SharedPreferenceHelper.saveMute(getApplicationContext(), true);

            //如果大图显示的是对方,小图是自己
            if (mHaveSwitchBigToSmall) {
                mBigCoverBlackV.setVisibility(View.GONE);
                mSmallCoverBlackV.setVisibility(View.GONE);
                mHaveOffCameraTv.setVisibility(View.GONE);
                mLocalSurfaceView.setVisibility(View.VISIBLE);
            } else {
                mBigCoverBlackV.setVisibility(View.GONE);
                mSmallCoverBlackV.setVisibility(View.GONE);
                mHaveOffCameraTv.setVisibility(View.GONE);
                mLocalSurfaceView.setVisibility(View.VISIBLE);
            }

            //暂停本地视频流  true是启用，false是禁用
            if (mTttRtcEngine != null) {
                mTttRtcEngine.enableLocalVideo(true);
                mUserSelfMute = true;
            }
        }
    }

    /**
     * 页面异常或者自己杀死app关闭时挂断
     */
    private void pageCloseHangUp() {
        Map<String, String> paramMap = new HashMap<>();
        paramMap.put("userId", getUserId());
        paramMap.put("roomId", String.valueOf(mRoomId));
        OkHttpUtils.post().url(ChatApi.BREAK_LINK)
                .addParams("param", ParamUtil.getParam(paramMap))
                .build().execute(new AjaxCallback<BaseResponse>() {
            @Override
            public void onResponse(BaseResponse response, int id) {
                if (response != null && response.m_istatus == NetCode.SUCCESS) {
                    LogUtil.i("页面异常关闭挂断");
                }
            }
        });
    }


    //-----------------礼物相关----------------

    /**
     * 获取礼物列表
     */
    private void getGiftList() {
        Map<String, String> paramMap = new HashMap<>();
        paramMap.put("userId", getUserId());
        OkHttpUtils.post().url(ChatApi.GET_GIFT_LIST)
                .addParams("param", ParamUtil.getParam(paramMap))
                .build().execute(new AjaxCallback<BaseListResponse<GiftBean>>() {
            @Override
            public void onResponse(BaseListResponse<GiftBean> response, int id) {
                if (response != null && response.m_istatus == NetCode.SUCCESS) {
                    List<GiftBean> giftBeans = response.m_object;
                    if (giftBeans != null && giftBeans.size() > 0) {
                        mGiftBeans = giftBeans;
                    }
                }
            }
        });
    }

    /**
     * 显示打赏礼物Dialog
     */
    private void showRewardDialog() {
        final Dialog mDialog = new Dialog(this, R.style.DialogStyle);
        @SuppressLint("InflateParams")
        View view = LayoutInflater.from(this).inflate(R.layout.dialog_gift_layout, null);
        setGiftDialogView(view, mDialog);
        mDialog.setContentView(view);
        Point outSize = new Point();
        getWindowManager().getDefaultDisplay().getSize(outSize);
        Window window = mDialog.getWindow();
        if (window != null) {
            WindowManager.LayoutParams params = window.getAttributes();
            params.width = outSize.x;
            window.setGravity(Gravity.BOTTOM); // 此处可以设置dialog显示的位置
            window.setWindowAnimations(R.style.BottomPopupAnimation); // 添加动画
        }
        mDialog.setCanceledOnTouchOutside(true);
        if (!isFinishing()) {
            mDialog.show();
        }
    }

    /**
     * 礼物dialog view 初始化
     */
    private void setGiftDialogView(View view, final Dialog mDialog) {
        //-----------------初始化----------------
        final RecyclerView gift_rv = view.findViewById(R.id.gift_rv);
        final RecyclerView red_rv = view.findViewById(R.id.red_rv);
        final LinearLayout indicator_ll = view.findViewById(R.id.indicator_ll);
        final TextView gift_tv = view.findViewById(R.id.gift_tv);
        final TextView red_tv = view.findViewById(R.id.red_tv);
        final TextView gold_tv = view.findViewById(R.id.gold_tv);
        TextView charge_tv = view.findViewById(R.id.charge_tv);
        TextView reward_tv = view.findViewById(R.id.reward_tv);

        //如果是主播进来 就不显示打赏
        /*if (mFromType == Constant.FROM_ACTOR || mFromType == Constant.FROM_ACTOR_INVITE) {
            reward_tv.setVisibility(View.INVISIBLE);
        } else {
            reward_tv.setVisibility(View.VISIBLE);
        }*/

        //初始化显示礼物
        gift_tv.setSelected(true);
        red_tv.setSelected(false);
        gift_rv.setVisibility(View.VISIBLE);
        red_rv.setVisibility(View.GONE);
        indicator_ll.setVisibility(View.VISIBLE);

        //可用金币
        getMyGold(gold_tv);

        //处理list
        List<List<GiftBean>> giftListBeanList = new ArrayList<>();
        if (mGiftBeans != null && mGiftBeans.size() > 0) {
            int count = mGiftBeans.size() / 8;
            int left = mGiftBeans.size() % 8;
            if (count > 0) {//如果大于等于8个
                for (int i = 1; i <= count; i++) {
                    int start = (i - 1) * 8;
                    int end = i * 8;
                    List<GiftBean> subList = mGiftBeans.subList(start, end);
                    giftListBeanList.add(i - 1, subList);
                }
                if (left != 0) {//如果还剩余的话,那剩余的加进入
                    List<GiftBean> leftBeans = mGiftBeans.subList(count * 8, mGiftBeans.size());
                    giftListBeanList.add(count, leftBeans);
                }
            } else {
                giftListBeanList.add(0, mGiftBeans);
            }
        }

        //-----------------礼物---------------
        final List<ImageView> imageViews = new ArrayList<>();
        ViewPagerLayoutManager mLayoutManager = new ViewPagerLayoutManager(this, OrientationHelper.HORIZONTAL);
        gift_rv.setLayoutManager(mLayoutManager);
        final GiftViewPagerRecyclerAdapter giftAdapter = new GiftViewPagerRecyclerAdapter(VideoChatOneActivity.this);
        gift_rv.setAdapter(giftAdapter);
        if (giftListBeanList.size() > 0) {
            giftAdapter.loadData(giftListBeanList);
            //设置指示器
            for (int i = 0; i < giftListBeanList.size(); i++) {
                ImageView imageView = new ImageView(getApplicationContext());
                int width = DevicesUtil.dp2px(getApplicationContext(), 6);
                int height = DevicesUtil.dp2px(getApplicationContext(), 6);
                LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(width, height);
                params.leftMargin = 10;
                imageView.setLayoutParams(params);
                if (i == 0) {
                    imageView.setImageResource(R.drawable.shape_gift_indicator_white_back);
                } else {
                    imageView.setImageResource(R.drawable.shape_gift_indicator_gray_back);
                }
                imageViews.add(imageView);
                indicator_ll.addView(imageView);
            }
        }

        mLayoutManager.setOnViewPagerListener(new OnViewPagerListener() {
            @Override
            public void onInitComplete() {

            }

            @Override
            public void onPageRelease(boolean isNext, int position) {

            }

            @Override
            public void onPageSelected(int position, boolean isBottom) {
                if (imageViews.size() > 0) {
                    for (int i = 0; i < imageViews.size(); i++) {
                        if (i == position) {
                            imageViews.get(i).setImageResource(R.drawable.shape_gift_indicator_white_back);
                        } else {
                            imageViews.get(i).setImageResource(R.drawable.shape_gift_indicator_gray_back);
                        }
                    }
                }
            }
        });

        //红包
        GridLayoutManager gridLayoutManager = new GridLayoutManager(getApplicationContext(), 3);
        red_rv.setLayoutManager(gridLayoutManager);
        final GoldGridRecyclerAdapter goldGridRecyclerAdapter = new GoldGridRecyclerAdapter(VideoChatOneActivity.this);
        red_rv.setAdapter(goldGridRecyclerAdapter);
        goldGridRecyclerAdapter.loadData(getLocalRedList());

        //--------------处理切换----------------
        //礼物
        gift_tv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (gift_tv.isSelected()) {
                    return;
                }
                gift_tv.setSelected(true);
                red_tv.setSelected(false);
                gift_rv.setVisibility(View.VISIBLE);
                red_rv.setVisibility(View.GONE);
                indicator_ll.setVisibility(View.VISIBLE);
            }
        });
        //红包
        red_tv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (red_tv.isSelected()) {
                    return;
                }
                red_tv.setSelected(true);
                gift_tv.setSelected(false);
                red_rv.setVisibility(View.VISIBLE);
                gift_rv.setVisibility(View.GONE);
                indicator_ll.setVisibility(View.INVISIBLE);
            }
        });
        //充值
        charge_tv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), ChargeActivity.class);
                startActivity(intent);
                mDialog.dismiss();
            }
        });
        //dismiss的时候清空
        mDialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialog) {
                if (mGiftBeans != null && mGiftBeans.size() > 0) {
                    for (GiftBean bean : mGiftBeans) {
                        bean.isSelected = false;
                    }
                }
            }
        });
        //打赏
        reward_tv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //如果是礼物选中
                if (gift_tv.isSelected()) {
                    GiftBean giftBean = giftAdapter.getSelectBean();
                    if (giftBean == null) {
                        ToastUtil.showToast(getApplicationContext(), R.string.please_select_gift);
                        return;
                    }
                    //判断是否够
                    if (giftBean.t_gift_gold > mMyGoldNumber) {
                        ToastUtil.showToast(getApplicationContext(), R.string.gold_not_enough);
                        return;
                    }
                    reWardGift(giftBean);
                } else {//如果是红包选中
                    GoldBean goldBean = goldGridRecyclerAdapter.getSelectedBean();
                    if (goldBean == null) {
                        ToastUtil.showToast(getApplicationContext(), R.string.please_select_gold);
                        return;
                    }
                    if (goldBean.goldNumber > mMyGoldNumber) {
                        ToastUtil.showToast(getApplicationContext(), R.string.gold_not_enough);
                        return;
                    }
                    reWardGold(goldBean.goldNumber);
                }
                mDialog.dismiss();
            }
        });
    }

    /**
     * 获取本地红包集合
     */
    private List<GoldBean> getLocalRedList() {
        List<GoldBean> goldBeans = new ArrayList<>();
        // 99
        GoldBean one = new GoldBean();
        one.resourceId = R.drawable.reward_gold_one;
        one.goldNumber = 99;
        // 188
        GoldBean two = new GoldBean();
        two.resourceId = R.drawable.reward_gold_two;
        two.goldNumber = 188;
        // 520
        GoldBean three = new GoldBean();
        three.resourceId = R.drawable.reward_gold_three;
        three.goldNumber = 520;
        // 999
        GoldBean four = new GoldBean();
        four.resourceId = R.drawable.reward_gold_four;
        four.goldNumber = 999;
        // 1314
        GoldBean five = new GoldBean();
        five.resourceId = R.drawable.reward_gold_five;
        five.goldNumber = 1314;
        // 8888
        GoldBean six = new GoldBean();
        six.resourceId = R.drawable.reward_gold_six;
        six.goldNumber = 8888;
        goldBeans.add(0, one);
        goldBeans.add(1, two);
        goldBeans.add(2, three);
        goldBeans.add(3, four);
        goldBeans.add(4, five);
        goldBeans.add(5, six);
        return goldBeans;
    }

    /**
     * 打赏礼物
     */
    private void reWardGift(final GiftBean giftBean) {
        Map<String, String> paramMap = new HashMap<>();
        paramMap.put("userId", getUserId());
        paramMap.put("coverConsumeUserId", String.valueOf(mActorId));
        paramMap.put("giftId", String.valueOf(giftBean.t_gift_id));
        paramMap.put("giftNum", "1");
        OkHttpUtils.post().url(ChatApi.USER_GIVE_GIFT)
                .addParams("param", ParamUtil.getParam(paramMap))
                .build().execute(new AjaxCallback<BaseResponse>() {
            @Override
            public void onResponse(BaseResponse response, int id) {
                if (response != null) {
                    if (response.m_istatus == NetCode.SUCCESS) {
                        //发送自定义消息
                        CustomMessageBean bean = new CustomMessageBean();
                        bean.type = "1";
                        bean.gift_id = giftBean.t_gift_id;
                        bean.gift_name = giftBean.t_gift_name;
                        bean.gift_gif_url = giftBean.t_gift_still_url;
                        bean.gold_number = giftBean.t_gift_gold;
                        bean.t_gift_gif_url = giftBean.t_gift_gif_url;
                        bean.t_num = giftBean.t_num;

                        showGiftMessage(bean);

                        String mTargetId = String.valueOf(10000 + mActorId);
                        Conversation mConversation = JMessageClient.getSingleConversation(mTargetId, BuildConfig.jpushAppKey);
                        if (mConversation == null) {
                            mConversation = Conversation.createSingleConversation(mTargetId, BuildConfig.jpushAppKey);
                        }
                        if (mConversation != null) {
                            String json = JSON.toJSONString(bean);
                            CustomContent customContent = new CustomContent();
                            customContent.setStringValue("custom", json);
                            Message msg = mConversation.createSendMessage(customContent);
                            sendIMCustomMessage(msg);
                        }

                        startGiftInAnim(bean, true, false);
                    } else if (response.m_istatus == -1) {
                        ToastUtil.showToast(getApplicationContext(), R.string.gold_not_enough);
                    } else {
                        ToastUtil.showToast(getApplicationContext(), response.m_strMessage);
                    }
                } else {
                    ToastUtil.showToast(getApplicationContext(), R.string.pay_fail);
                }
            }
        });
    }

    /**
     * 发送Im自定义消息
     */
    private void sendIMCustomMessage(Message message) {
        if (message != null) {
            message.setOnSendCompleteCallback(new BasicCallback() {
                @Override
                public void gotResult(int i, String s) {
                    if (i == 0) {
                        LogUtil.i("发送礼物自定义消息成功");
                    } else {
                        LogUtil.i("发送礼物自定义消息失败");
                    }
                }
            });
            MessageSendingOptions options = new MessageSendingOptions();
            options.setShowNotification(false);
            JMessageClient.sendMessage(message, options);
        }
    }

    /**
     * 获取我的金币余额
     */
    private void getMyGold(final TextView can_use_iv) {
        Map<String, String> paramMap = new HashMap<>();
        paramMap.put("userId", getUserId());
        OkHttpUtils.post().url(ChatApi.GET_USER_BALANCE)
                .addParams("param", ParamUtil.getParam(paramMap))
                .build().execute(new AjaxCallback<BaseResponse<BalanceBean>>() {
            @Override
            public void onResponse(BaseResponse<BalanceBean> response, int id) {
                if (response != null && response.m_istatus == NetCode.SUCCESS) {
                    BalanceBean balanceBean = response.m_object;
                    if (balanceBean != null) {
                        mMyGoldNumber = balanceBean.amount;
                        String content = getResources().getString(R.string.can_use_gold) + mMyGoldNumber;
                        can_use_iv.setText(content);
                        can_use_iv.setVisibility(View.VISIBLE);
                    }
                }
            }
        });
    }

    /**
     * 打赏金币(红包)
     */
    private void reWardGold(final int gold) {
        Map<String, String> paramMap = new HashMap<>();
        paramMap.put("userId", getUserId());
        paramMap.put("coverConsumeUserId", String.valueOf(mActorId));
        paramMap.put("gold", String.valueOf(gold));
        OkHttpUtils.post().url(ChatApi.SEND_RED_ENVELOPE)
                .addParams("param", ParamUtil.getParam(paramMap))
                .build().execute(new AjaxCallback<BaseResponse>() {
            @Override
            public void onResponse(BaseResponse response, int id) {
                if (response != null) {
                    if (response.m_istatus == NetCode.SUCCESS) {
                        //发送自定义消息
                        CustomMessageBean bean = new CustomMessageBean();
                        bean.type = "0";//金币
                        bean.gold_number = gold;
                        bean.gift_name = getResources().getString(R.string.gold);

                        String mTargetId = String.valueOf(10000 + mActorId);
                        Conversation mConversation = JMessageClient.getSingleConversation(mTargetId, BuildConfig.jpushAppKey);
                        if (mConversation == null) {
                            mConversation = Conversation.createSingleConversation(mTargetId, BuildConfig.jpushAppKey);
                        }
                        if (mConversation != null) {
                            String json = JSON.toJSONString(bean);
                            CustomContent customContent = new CustomContent();
                            customContent.setStringValue("custom", json);
                            Message msg = mConversation.createSendMessage(customContent);
                            sendIMCustomMessage(msg);
                        }

                        startGiftInAnim(bean, true, true);
                    } else if (response.m_istatus == -1) {
                        ToastUtil.showToast(getApplicationContext(), R.string.gold_not_enough);
                    } else {
                        ToastUtil.showToast(getApplicationContext(), R.string.system_error);
                    }
                } else {
                    ToastUtil.showToast(getApplicationContext(), R.string.pay_fail);
                }
            }

            @Override
            public void onError(Call call, Exception e, int id) {
                super.onError(call, e, id);
                ToastUtil.showToast(getApplicationContext(), R.string.system_error);
            }
        });
    }

    /**
     * 发送文本消息
     */
    private void sendTextMessage() {
        String text = mInputEt.getText().toString().trim();
        if (TextUtils.isEmpty(text)) {
            ToastUtil.showToast(getApplicationContext(), R.string.please_input_text_message);
            return;
        }

        String mTargetId = String.valueOf(10000 + mActorId);
        Conversation mConversation = JMessageClient.getSingleConversation(mTargetId, BuildConfig.jpushAppKey);
        if (mConversation == null) {
            mConversation = Conversation.createSingleConversation(mTargetId, BuildConfig.jpushAppKey);
        }
        if (mConversation != null) {
            TextContent content = new TextContent(text);
            final Message message = mConversation.createSendMessage(content);
            if (message != null) {
                message.setOnSendCompleteCallback(new BasicCallback() {
                    @Override
                    public void gotResult(int i, String s) {
                        if (i == 0) {
                            LogUtil.i("发送文字消息成功");
                            mTextRecyclerAdapter.addData(message);
                            if (!Constant.hideChatOnTelephone()) {
                                if (mTextListRv != null) {
                                    if (mTextListRv.getVisibility() != View.VISIBLE) {
                                        mTextListRv.setVisibility(View.VISIBLE);
                                    }
                                    mTextListRv.scrollToPosition(mTextRecyclerAdapter.getPosition());
                                }
                            }
                        } else {
                            LogUtil.i("发送文字消息失败");
                        }
                    }
                });
                MessageSendingOptions options = new MessageSendingOptions();
                options.setShowNotification(false);
                JMessageClient.sendMessage(message, options);
            }
        }
        //清空输入框
        mInputEt.setText(null);
        //关闭软件盘
        closeSoft();
    }

    //-------------------权限---------------------
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
            if (permissions.size() != 0) {
                ActivityCompat.requestPermissions(this,
                        permissions.toArray(new String[0]),
                        100);
            }
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        return keyCode == KeyEvent.KEYCODE_BACK || super.onKeyDown(keyCode, event);
    }

    @Override
    protected void onHangUp() {
        try {
            if (!isFinishing()) {
                LogUtil.i("连接已挂断 30005");
                if (mTttRtcEngine != null && !mHaveHangUp) {
                    ToastUtil.showToast(getApplicationContext(), R.string.have_hang_up_one);
                    mHaveHangUp = true;
                    mTttRtcEngine.leaveChannel();
                    mTttRtcEngine = null;

                    if ((mFromType == Constant.FROM_USER || mFromType == Constant.FROM_USER_JOIN)
                            && (mRuleLl != null && mRuleLl.getVisibility() == View.GONE)) {
                        Intent intent = new Intent(getApplicationContext(), CommentActivity.class);
                        intent.putExtra(Constant.ACTOR_ID, mActorId);
                        startActivity(intent);
                    }

                    finish();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 在线消息
     */
    public void onEventMainThread(MessageEvent event) {
        Message message = event.getMessage();
        if (message.getTargetType() == ConversationType.single) {
            UserInfo fromUser = message.getFromUser();
            int mTargetId = 10000 + mActorId;
            if (fromUser != null && Integer.parseInt(fromUser.getUserName()) == mTargetId) {
                switch (message.getContentType()) {
                    case text: {//文本
                        final String content = ((TextContent) message.getContent()).getText();
                        LogUtil.i("新的文本消息: " + content);
                        mTextRecyclerAdapter.addData(message);
                        if (mTextListRv != null) {
                            if (mTextListRv.getVisibility() != View.VISIBLE) {
                                mTextListRv.setVisibility(View.VISIBLE);
                            }
                            mTextListRv.scrollToPosition(mTextRecyclerAdapter.getPosition());
                        }
                        break;
                    }
                    case custom: {//自定义
                        LogUtil.i("新的自定义消息: ");
                        CustomContent customContent = (CustomContent) message.getContent();
                        parseCustomMessage(customContent);


                        break;
                    }
                }
            }
        }
    }

    /**
     * 解析自定义消息
     */
    private void parseCustomMessage(CustomContent customContent) {
        try {
            String json = customContent.getStringValue("custom");
            CustomMessageBean bean = JSON.parseObject(json, CustomMessageBean.class);
            if (bean != null) {
                if (bean.type.equals("1")) {//礼物
                    LogUtil.i("接收到的礼物: " + bean.gift_name);
                    startGiftInAnim(bean, false, false);

                    tvGoldReward.setText((Float.parseFloat(tvGoldReward.getText().toString()) + bean.gold_number) + "");
                    float recharge_money = Float.parseFloat(tvGold.getText().toString());
                    recharge_money = recharge_money - bean.gold_number;
                    if (recharge_money <= 0) {
                        tvGold.setText("0.0");
                    } else {
                        tvGold.setText(recharge_money + "");
                    }

                    showGiftMessage(bean);
                } else if (bean.type.equals("0")) {//金币
                    bean.gift_name = getResources().getString(R.string.gold);
                    LogUtil.i("接收到的礼物: " + bean.gift_name);
                    startGiftInAnim(bean, false, true);

                    tvGoldReward.setText((Float.parseFloat(tvGoldReward.getText().toString()) + bean.gold_number) + "");
                    float recharge_money = Float.parseFloat(tvGold.getText().toString());
                    recharge_money = recharge_money - bean.gold_number;
                    if (recharge_money <= 0) {
                        tvGold.setText("0.0");
                    } else {
                        tvGold.setText(recharge_money + "");
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 开始礼物进来动画
     *
     * @param fromSend 是用户进来 还是主播
     */
    private void startGiftInAnim(CustomMessageBean bean, boolean fromSend, boolean isGold) {
        String lastDes = mGiftDesTv.getText().toString().trim();
        //判断需不需要重新开始一个,如果礼物不是重复,或则是金币
        if ((!TextUtils.isEmpty(lastDes) && !lastDes.contains(bean.gift_name)) || isGold) {
            mSingleTimeSendGiftCount = 0;
        }
        mSingleTimeSendGiftCount++;
        if (mSingleTimeSendGiftCount == 1) {//如果是第一次点击礼物
            //描述
            String des;
            if (fromSend) {//是送出
                des = getResources().getString(R.string.send_to) + bean.gift_name;
            } else {//是收到
                des = getResources().getString(R.string.send_you) + bean.gift_name;
            }
            mGiftDesTv.setText(des);
            //礼物类型
            if (isGold) {//是金币
                mGiftIv.setImageResource(R.drawable.ic_gold);
                String goldNumber = getResources().getString(R.string.multi) + bean.gold_number;
                mGiftNumberTv.setText(goldNumber);
            } else {
                ImageLoadHelper.glideShowImageWithUrl(this, bean.gift_gif_url, mGiftIv);
                String giftNumber = getResources().getString(R.string.multi) + mSingleTimeSendGiftCount;
                mGiftNumberTv.setText(giftNumber);


            }

            mGiftLl.setVisibility(View.VISIBLE);
            mGiftLl.clearAnimation();
            TranslateAnimation mGiftLayoutInAnim = (TranslateAnimation) AnimationUtils.loadAnimation(getApplicationContext(), R.anim.lp_gift_in);
            mGiftLl.setAnimation(mGiftLayoutInAnim);
            mGiftLayoutInAnim.start();
            mHandler.removeCallbacks(mGiftRunnable);
            mGiftLayoutInAnim.setAnimationListener(new Animation.AnimationListener() {
                @Override
                public void onAnimationStart(Animation animation) {

                }

                @Override
                public void onAnimationEnd(Animation animation) {
                    //2秒后消失
                    mHandler.postDelayed(mGiftRunnable, 3000);
                }

                @Override
                public void onAnimationRepeat(Animation animation) {

                }
            });
        } else {
            if (!isGold) {//礼物
                String giftNumber = getResources().getString(R.string.multi) + mSingleTimeSendGiftCount;
                mGiftNumberTv.setText(giftNumber);

                mHandler.removeCallbacks(mGiftRunnable);
                mHandler.postDelayed(mGiftRunnable, 3000);
                startComboAnim(mGiftNumberTv);
            }
        }

    }

    private void startGiftOutAnim() {
        mGiftLl.clearAnimation();
        Animation mGiftLayoutInAnim = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.lp_gift_out);
        mGiftLl.setAnimation(mGiftLayoutInAnim);
        mGiftLayoutInAnim.start();
        mGiftLayoutInAnim.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                if (mSingleTimeSendGiftCount != 1) {
                    mGiftLl.setVisibility(View.GONE);
                }
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
    }

    private void startComboAnim(final TextView giftNumView) {
        ObjectAnimator anim1 = ObjectAnimator.ofFloat(giftNumView, "scaleX", 1.8f, 1.0f);
        ObjectAnimator anim2 = ObjectAnimator.ofFloat(giftNumView, "scaleY", 1.8f, 1.0f);
        AnimatorSet animSet = new AnimatorSet();
        animSet.setDuration(300);
        animSet.setInterpolator(new OvershootInterpolator());
        animSet.playTogether(anim1, anim2);
        animSet.start();
    }

    /**
     * 礼物runnable
     */
    private Runnable mGiftRunnable = new Runnable() {
        @Override
        public void run() {
            mSingleTimeSendGiftCount = 0;
            startGiftOutAnim();
        }
    };

    /**
     * 处理奔溃情况
     */
    private void dealCrash() {
        Thread.setDefaultUncaughtExceptionHandler(new Thread.UncaughtExceptionHandler() {
            @Override
            public void uncaughtException(Thread t, Throwable e) {
                Log.i("aaa", e.toString());
                LogUtil.i("检测到奔溃");
                if (mHandler != null) {
                } else {
                    try {
                        Intent intent = new Intent(mContext, MainActivity.class);
                        @SuppressLint("WrongConstant") PendingIntent restartIntent = PendingIntent.getActivity(mContext,
                                0, intent, Intent.FLAG_ACTIVITY_NEW_TASK);
                        AlarmManager mgr = (AlarmManager) mContext.getSystemService(Context.ALARM_SERVICE);
                        if (mgr != null) {
                            mgr.set(AlarmManager.RTC, System.currentTimeMillis() + 100, restartIntent);
                        }
                        android.os.Process.killProcess(android.os.Process.myPid());
                    } catch (Exception ep) {
                        ep.printStackTrace();
                    }
                }
            }
        });
    }

    @Override
    protected void beenShutDown() {
        super.beenShutDown();
        if (mTttRtcEngine != null) {
            mTttRtcEngine.leaveChannel();
            mTttRtcEngine = null;
            finish();
        }
    }

    /**
     * 建造推流
     */
    private VideoCompositingLayout.Region[] buildRemoteLayoutLocation() {
        List<VideoCompositingLayout.Region> tempList = new ArrayList<>();
        //主播
        if (Long.parseLong(getUserId()) > 0) {
            VideoCompositingLayout.Region mRegion = new VideoCompositingLayout.Region();
            mRegion.mUserID = Long.parseLong(getUserId());
            //上下
            //1:1
            mRegion.x = 0;
            mRegion.y = 0;
            mRegion.width = 1;
            mRegion.height = 1;
            mRegion.zOrder = 0;
            tempList.add(mRegion);
        }
        VideoCompositingLayout.Region[] mRegions = new VideoCompositingLayout.Region[tempList.size()];
        for (int k = 0; k < tempList.size(); k++) {
            VideoCompositingLayout.Region region = tempList.get(k);
            mRegions[k] = region;
        }
        return mRegions;
    }

    @Override
    protected void moneyNotEnough() {
        if ((mFromType == Constant.FROM_USER || mFromType == Constant.FROM_USER_JOIN)
                && (mRuleLl != null && mRuleLl.getVisibility() == View.GONE) && !isFinishing()) {
            showGoldJustEnoughDialog();
        }
    }

    /**
     * 显示金币一分钟后挂断dialog
     */
    private void showGoldJustEnoughDialog() {
        final Dialog mDialog = new Dialog(this, R.style.DialogStyle);
        @SuppressLint("InflateParams")
        View view = LayoutInflater.from(this).inflate(R.layout.dialog_money_not_enough_layout, null);
        setGoldDialogView(view, mDialog);
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

    /**
     * 设置头像选择dialog的view
     */
    private void setGoldDialogView(View view, final Dialog mDialog) {
        //取消
        TextView ignore_tv = view.findViewById(R.id.ignore_tv);
        ignore_tv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mDialog.dismiss();
            }
        });
        //充值
        TextView charge_tv = view.findViewById(R.id.charge_tv);
        charge_tv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //清空房间
                Intent intent = new Intent(getApplicationContext(), ChargeActivity.class);
                startActivity(intent);
                mDialog.dismiss();
            }
        });
    }

    /**
     * 关闭软件盘
     */
    private void closeSoft() {
        try {
            if (mInputEt != null && mInputEt.hasFocus()) {
                InputMethodManager imm = (InputMethodManager) getApplicationContext().getSystemService(Context.INPUT_METHOD_SERVICE);
                if (imm != null && imm.isActive()) {
                    imm.hideSoftInputFromWindow(mInputEt.getWindowToken(), 0);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 调用键盘
     */
    private void showSpan() {
        getWindow().getDecorView().postDelayed(new Runnable() {
            @Override
            public void run() {
                if (mInputEt.requestFocus()) {
                    InputMethodManager imm = (InputMethodManager) getApplicationContext().getSystemService(Context.INPUT_METHOD_SERVICE);
                    if (imm != null) {
                        imm.showSoftInput(mInputEt, InputMethodManager.SHOW_IMPLICIT);
                    }
                }
            }
        }, 200);
    }

    //接通视频的时候,下发提示消息,主播用户都要提示
    @Override
    protected void onVideoStartSocketHint(String hintMessage) {
        if (mTextRecyclerAdapter != null && !TextUtils.isEmpty(hintMessage)) {
            mTextRecyclerAdapter.addHintData(hintMessage);
            if (!Constant.hideChatOnTelephone()) {
                if (mTextListRv != null) {
                    if (mTextListRv.getVisibility() != View.VISIBLE) {
                        mTextListRv.setVisibility(View.VISIBLE);
                    }
                    mTextListRv.scrollToPosition(mTextRecyclerAdapter.getPosition());
                }
            }
        }
    }

    /**
     * 获取金币数
     */
    private void getUserGold(int userId) {
        Map<String, String> paramMap = new HashMap<>();
        paramMap.put("userId", String.valueOf(userId));
        paramMap.put("anchorId", getUserId());
        OkHttpUtils.post().url(ChatApi.GET_USER_GOLD)
                .addParams("param", ParamUtil.getParam(paramMap))
                .build().execute(new AjaxCallback<BaseResponse<UserGoldBean>>() {
            @Override
            public void onResponse(BaseResponse<UserGoldBean> response, int id) {
                if (response != null && response.m_istatus == NetCode.SUCCESS) {
                    mUserGoldBean = response.m_object;
                    if (mUserGoldBean != null && mUserGoldBean.t_sex == 1) {
                        layoutGold.setVisibility(View.VISIBLE);
                        tvGold.setText(mUserGoldBean.t_recharge_money + "");
                        mDesTv.setVisibility(View.GONE);

                        if (task != null) {
                            task.cancel();
                        }

                        task = new TimerTask() {
                            @Override
                            public void run() {
                                android.os.Message message = new android.os.Message();
                                message.what = 1;
                                handler.sendMessage(message);
                            }
                        };

                        timer.schedule(task, 1000 * 60, 1000 * 60);
                    }
                }
            }
        });
    }

    void initXX() {
        FuliveSaveBean mFuliveSaveBean = SharedPreferenceHelper.getBeautyFulive(mContext);

        //filter_name 滤镜名称
        faceunity.fuItemSetParam(mItemsArray[ITEM_ARRAYS_FACE_BEAUTY_INDEX], BeautificationParams.FILTER_NAME, mFuliveSaveBean.mFilterName);
        //filter_level 滤镜强度 范围0~1 SDK默认为 1
        faceunity.fuItemSetParam(mItemsArray[ITEM_ARRAYS_FACE_BEAUTY_INDEX], BeautificationParams.FILTER_LEVEL, mFuliveSaveBean.mFilterLevel);

        //skin_detect 精准美肤（肤色检测开关） 0:关闭 1:开启 SDK默认为 0
        faceunity.fuItemSetParam(mItemsArray[ITEM_ARRAYS_FACE_BEAUTY_INDEX], BeautificationParams.SKIN_DETECT, mFuliveSaveBean.mSkinDetect);

        //heavy_blur 磨皮类型 0:清晰磨皮 1:重度磨皮 SDK默认为 1
        faceunity.fuItemSetParam(mItemsArray[ITEM_ARRAYS_FACE_BEAUTY_INDEX], BeautificationParams.HEAVY_BLUR, mFuliveSaveBean.mHeavyBlur);
        //blur_level 磨皮 范围0~6 SDK默认为 6
        faceunity.fuItemSetParam(mItemsArray[ITEM_ARRAYS_FACE_BEAUTY_INDEX], BeautificationParams.BLUR_LEVEL, 6 * mFuliveSaveBean.mBlurLevel);


        //nonskin_blur_scale 肤色检测之后，非肤色区域的融合程度，范围0-1，SDK默认为0.45
//            faceunity.fuItemSetParam(mItemsArray[ITEM_ARRAYS_FACE_BEAUTY_INDEX], BeautificationParams.NONSKIN_BLUR_SCALE, 0);
        //color_level 美白 范围0~1 SDK默认为 0.2
        faceunity.fuItemSetParam(mItemsArray[ITEM_ARRAYS_FACE_BEAUTY_INDEX], BeautificationParams.COLOR_LEVEL, mFuliveSaveBean.mColorLevel);


        //red_level 红润 范围0~1 SDK默认为 0.5
        faceunity.fuItemSetParam(mItemsArray[ITEM_ARRAYS_FACE_BEAUTY_INDEX], BeautificationParams.RED_LEVEL, mFuliveSaveBean.mRedLevel);
        //eye_bright 亮眼 范围0~1 SDK默认为 0
        faceunity.fuItemSetParam(mItemsArray[ITEM_ARRAYS_FACE_BEAUTY_INDEX], BeautificationParams.EYE_BRIGHT, mFuliveSaveBean.mEyeBright);
        //tooth_whiten 美牙 范围0~1 SDK默认为 0
        faceunity.fuItemSetParam(mItemsArray[ITEM_ARRAYS_FACE_BEAUTY_INDEX], BeautificationParams.TOOTH_WHITEN, mFuliveSaveBean.mToothWhiten);

    }

    /**
     * 重置相芯
     */
    private void resetFulive() {
        if (BuildConfig.beautySDK == 2) {
            faceunity.fuDestroyAllItems();
            faceunity.fuOnDeviceLost();
            faceunity.fuDone();
            mFrameId = 0;

            int itemBeauty = FuliveUtil.loadItem(mContext, Constant.BUNDLE_FACE_BEAUTIFICATION);
            if (itemBeauty <= 0) {
                return;
            }
            mItemsArray[ITEM_ARRAYS_FACE_BEAUTY_INDEX] = itemBeauty;
            initXX();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        resetFulive();
    }

    /**
     * 显示礼物动画
     */
    public void showGiftMessage(CustomMessageBean bean) {

        showGifGift(bean);
    }

    private int duration;

    /**
     * 显示gif礼物
     */
    private void showGifGift(CustomMessageBean bean) {

        String url = bean.t_gift_gif_url;
        if (url.endsWith(".gif")) {
            gift_svga.setVisibility(View.GONE);
            enter_room_gif.setVisibility(View.VISIBLE);
            enter_room_gif.setFocusable(true);
            enter_room_gif.setClickable(true);
            if (TextUtils.isEmpty(url)) {
                return;
            }
            enter_room_gif.setVisibility(View.VISIBLE);
            Glide.with(this).load(url).diskCacheStrategy(DiskCacheStrategy.SOURCE).listener(new RequestListener<String, GlideDrawable>() {
                @Override
                public boolean onException(Exception e, String model, Target<GlideDrawable> target, boolean isFirstResource) {

                    return false;
                }

                @Override
                public boolean onResourceReady(GlideDrawable resource, String model, Target<GlideDrawable> target, boolean isFromMemoryCache, boolean isFirstResource) {
                    duration = 0;
                    // 计算动画时长
                    GifDrawable drawable = (GifDrawable) resource;
                    GifDecoder decoder = drawable.getDecoder();
                    for (int i = 0; i < drawable.getFrameCount(); i++) {
                        duration += decoder.getDelay(i);
                    }
                    //发送延时消息，通知动画结束
                    avgahandler.sendEmptyMessageDelayed(1,
                            duration);
                    return false;
                }
            }).into(new GlideDrawableImageViewTarget(enter_room_gif, 1));
        } else if (url.endsWith(".svga")) {
            gift_svga.setVisibility(View.VISIBLE);
            enter_room_gif.setVisibility(View.GONE);
            gift_svga.setFocusable(true);
            gift_svga.setClickable(true);
            loadAnimation(url, "" + bean.gift_id);
        } else {
            gift_svga.setVisibility(View.GONE);
            enter_room_gif.setVisibility(View.GONE);
            gift_svga.setFocusable(false);
            gift_svga.setClickable(false);
            enter_room_gif.setFocusable(false);
            enter_room_gif.setClickable(false);
        }

    }

    private void loadAnimation(final String url, String id) {
        final SVGAParser parser = new SVGAParser(this);

        try {
            parser.decodeFromURL(new URL(url), new SVGAParser.ParseCompletion() {
                @Override
                public void onComplete(SVGAVideoEntity svgaVideoEntity) {
                    gift_svga.setVideoItem(svgaVideoEntity);
                    gift_svga.startAnimation();

                }

                @Override
                public void onError() {

                }
            });
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        /*GifCacheUtil.getFile(ChatApi.GIF_PATH + id, url, new CommonCallback<File>() {
            @Override
            public void callback(File bean) {
                if (bean!=null){
                    BufferedInputStream bis = null;
                    try {
                        bis = new BufferedInputStream(new FileInputStream(bean));
                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                    }

                    parser.decodeFromInputStream(bis, bean.getAbsolutePath(), new SVGAParser.ParseCompletion() {
                        @Override
                        public void onComplete(SVGAVideoEntity svgaVideoEntity) {
                            gift_svga.setVideoItem(svgaVideoEntity);
                            gift_svga.startAnimation();
                        }

                        @Override
                        public void onError() {

                        }
                    }, true);
                }

            }
        });
*/


    }

    private Handler avgahandler = new Handler() {
        @Override
        public void handleMessage(android.os.Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case 1:
                    if (enter_room_gif != null) {
                        enter_room_gif.setVisibility(View.GONE);
                    }

                    break;
            }
        }
    };

    /*
     * 充值回调
     * */

    @Override
    protected void onRecharge(String content) {
        super.onRecharge(content);
        try {
            JSONObject jc = new JSONObject(content);
//            Log.i("aaa","num:"+(Float.parseFloat(jc.getString("gold"))+Float.parseFloat(tvGold.getText().toString())));
//            Log.i("aaa","tvGoldConsume"+(Float.parseFloat(jc.getString("gold"))+Float.parseFloat(tvGold.getText().toString())) + "");
//             Log.i("aaa","tvGold"+tvGold.getText().toString());
            if ((Float.parseFloat(jc.getString("gold")) + Float.parseFloat(tvGold.getText().toString())) <= 0) {
                if (!mHaveHangUp) {
                    ToastUtil.show("对方余额不足");
                }
            }
            tvGold.setText(String.valueOf(Float.parseFloat(jc.getString("gold")) + Float.parseFloat(tvGold.getText().toString())));

        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

}
