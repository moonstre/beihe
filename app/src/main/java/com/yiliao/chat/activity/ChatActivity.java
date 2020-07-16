package com.yiliao.chat.activity;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Path;
import android.graphics.Point;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.OrientationHelper;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.bumptech.glide.Glide;
import com.bumptech.glide.gifdecoder.GifDecoder;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.bumptech.glide.load.resource.gif.GifDrawable;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.GlideDrawableImageViewTarget;

import com.bumptech.glide.request.target.Target;
import com.opensource.svgaplayer.SVGACallback;
import com.opensource.svgaplayer.SVGAImageView;
import com.opensource.svgaplayer.SVGAParser;
import com.opensource.svgaplayer.SVGAVideoEntity;
import com.tencent.cos.xml.exception.CosXmlClientException;
import com.tencent.cos.xml.exception.CosXmlServiceException;
import com.tencent.cos.xml.listener.CosXmlResultListener;
import com.tencent.cos.xml.model.CosXmlRequest;
import com.tencent.cos.xml.model.CosXmlResult;
import com.tencent.cos.xml.model.object.PutObjectRequest;
import com.yiliao.chat.BuildConfig;
import com.yiliao.chat.R;
import com.yiliao.chat.adapter.ChatAdapter;
import com.yiliao.chat.adapter.GiftViewPagerRecyclerAdapter;
import com.yiliao.chat.adapter.GoldGridRecyclerAdapter;
import com.yiliao.chat.base.AppManager;
import com.yiliao.chat.base.BaseListResponse;
import com.yiliao.chat.base.BaseResponse;
import com.yiliao.chat.bean.BalanceBean;
import com.yiliao.chat.bean.ChargeBean;
import com.yiliao.chat.bean.ChatUserInfo;
import com.yiliao.chat.bean.CustomMessageBean;
import com.yiliao.chat.bean.GiftBean;
import com.yiliao.chat.bean.GoldBean;
import com.yiliao.chat.bean.UserCenterBean;
import com.yiliao.chat.bean.VideoRetrieverBean;
import com.yiliao.chat.bean.VideoSignBean;
import com.yiliao.chat.constant.ChatApi;
import com.yiliao.chat.constant.Constant;
import com.yiliao.chat.gif.CommonCallback;
import com.yiliao.chat.gif.DownloadUtil;
import com.yiliao.chat.gif.GifCacheUtil;
import com.yiliao.chat.helper.ChargeHelper;
import com.yiliao.chat.helper.ImageHelper;
import com.yiliao.chat.helper.SharedPreferenceHelper;
import com.yiliao.chat.im.ChatInput;
import com.yiliao.chat.im.ChatView;
import com.yiliao.chat.layoutmanager.ViewPagerLayoutManager;
import com.yiliao.chat.listener.OnLuCompressListener;
import com.yiliao.chat.listener.OnViewPagerListener;
import com.yiliao.chat.net.AjaxCallback;
import com.yiliao.chat.net.NetCode;
import com.yiliao.chat.oss.QServiceCfg;
import com.yiliao.chat.socket.ConnectManager;
import com.yiliao.chat.socket.domain.Mid;
import com.yiliao.chat.socket.domain.SocketResponse;
import com.yiliao.chat.util.DevicesUtil;
import com.yiliao.chat.util.DialogUtil;
import com.yiliao.chat.util.FileUtil;
import com.yiliao.chat.util.LogUtil;
import com.yiliao.chat.util.ParamUtil;
import com.yiliao.chat.util.ToastUtil;
import com.yiliao.chat.util.VideoFileUtils;
import com.yiliao.chat.videoupload.TXUGCPublish;
import com.yiliao.chat.videoupload.TXUGCPublishTypeDef;
import com.zhihu.matisse.Matisse;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.StringCallback;


import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.lang.ref.WeakReference;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
import okhttp3.Call;
import okhttp3.Request;
import pl.droidsonroids.gif.GifImageView;

/*
 * Copyright (C) 2018
 * 版权所有
 *
 * 功能描述：聊天页面
 * 作者：
 * 创建时间：2018/7/31
 *
 * 修改人：
 * 修改描述：
 * 修改日期
 */
public class ChatActivity extends FragmentActivity implements View.OnClickListener {
    private final int REQUEST_ALBUM_IMAGE_VIDEO = 0x1001;//相册请求图片和视频
    private final int CAMERA_REQUEST_CODE = 0x1006;

    private ChatInput mChatInput;
    private ListView mListView;
    private TextView mFirstDesTv;
    private View mVipLl;
    private TextView mFocusTv;

    private List<Message> mMessageList = new ArrayList<>();
    private ChatAdapter mChatAdapter;
    private boolean mIsGetMessage = false;
    private int mActorId;
    private String mMineId;
    //接收socket 广播
    private MyBroadcastReceiver mMyBroadcastReceiver;
    //礼物相关
    private List<GiftBean> mGiftBeans = new ArrayList<>();
    private int mMyGoldNumber;
    //极光im
    private Conversation mConversation;
    //对方是不是主播
    private UserCenterBean mUserCenterBean;
    //加载dialog
    private Dialog mDialogLoading;
    private TXUGCPublish mVideoPublish = null;
    private QServiceCfg mQServiceCfg;//图片
    private Uri mVideoUri;
    private String mVideoLocalPath;
    private String mVideoThumbnailUrl;
    private String mVideoDuration;

    private ImageView enter_room_gif;
    private SVGAImageView gift_svga;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat_layout);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
        // 禁止截屏
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_SECURE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {//如果不是沉浸式,就设置为黑色字体
            getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
        }

        mActorId = getIntent().getIntExtra(Constant.ACTOR_ID, 0);
        mMineId = getIntent().getStringExtra(Constant.MINE_ID);

        IntentFilter filter = new IntentFilter(ConnectManager.BROADCAST_ACTION);
        mMyBroadcastReceiver = new MyBroadcastReceiver();
        registerReceiver(mMyBroadcastReceiver, filter);

        mQServiceCfg = QServiceCfg.instance(getApplicationContext());

        initView();
        initConversation();
        initStart();

        //获取历史消息
        getMessage();
        getGiftList();
        getSpecifyUserFollow();
        if (mActorId > 0) {
            getActorInfo(mActorId);
        }
    }

    /**
     * 初始化conversation
     */
    private void initConversation() {
        //注册sdk的event用于接收各种event事件
        JMessageClient.registerEventReceiver(this);
        String mTargetId = String.valueOf(10000 + mActorId);
        mConversation = JMessageClient.getSingleConversation(mTargetId, BuildConfig.jpushAppKey);
        if (mConversation == null) {
            mConversation = Conversation.createSingleConversation(mTargetId, BuildConfig.jpushAppKey);
        }
    }

    /**
     * 初始化view
     */
    private void initView() {
        mDialogLoading = DialogUtil.showLoadingDialog(this);
        View left_fl = findViewById(R.id.left_fl);
        left_fl.setOnClickListener(this);
        mChatInput = findViewById(R.id.input_panel);
        mListView = findViewById(R.id.list);
        mFirstDesTv = findViewById(R.id.first_tv);
        enter_room_gif = findViewById(R.id.enter_room_gif);
        gift_svga = findViewById(R.id.gift_svga);

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


        TextView title_tv = findViewById(R.id.title_tv);
        TextView vip_tv = findViewById(R.id.vip_tv);
        vip_tv.setOnClickListener(this);
        if (Constant.hideVipCharge()) {
            vip_tv.setVisibility(View.GONE);
        }
        mVipLl = findViewById(R.id.vip_ll);
        mFocusTv = findViewById(R.id.focus_tv);
        mFocusTv.setOnClickListener(this);

        String title = getIntent().getStringExtra(Constant.TITLE);
        if (!TextUtils.isEmpty(title)) {
            title_tv.setText(title);
        } else {
            title_tv.setText(R.string.chat);
        }

        mDownloadGifCallback = new CommonCallback<File>() {
            @Override
            public void callback(File file) {
                if (file != null) {
//                    playHaoHuaGift(file);
                }
            }
        };
    }

    /**
     * 获取是否关注
     */
    private void getSpecifyUserFollow() {
        Map<String, String> paramMap = new HashMap<>();
        paramMap.put("userId", mMineId);
        paramMap.put("coverFollow", String.valueOf(mActorId));
        OkHttpUtils.post().url(ChatApi.GET_SPECIFY_USER_FOLLOW)
                .addParams("param", ParamUtil.getParam(paramMap))
                .build().execute(new AjaxCallback<BaseResponse<Integer>>() {
            @Override
            public void onResponse(BaseResponse<Integer> response, int id) {
                if (response != null && response.m_istatus == NetCode.SUCCESS) {
                    Integer foucs = response.m_object;
                    if (foucs == 0) {//0.为关注 1.已关注
                        mFocusTv.setVisibility(View.VISIBLE);
                    }
                }
            }
        });
    }

    /**
     * 获取主播收费设置
     */
    private void getActorSetCharge() {
        Map<String, String> paramMap = new HashMap<>();
        paramMap.put("userId", mMineId);
        paramMap.put("anchorId", String.valueOf(mActorId));
        OkHttpUtils.post().url(ChatApi.GET_ACTOR_CHARGE_SETUP)
                .addParams("param", ParamUtil.getParam(paramMap))
                .build().execute(new AjaxCallback<BaseResponse<ChargeBean>>() {
            @Override
            public void onResponse(BaseResponse<ChargeBean> response, int id) {
                if (isFinishing()) {
                    return;
                }
                if (response != null && response.m_istatus == NetCode.SUCCESS) {
                    ChargeBean bean = response.m_object;
                    if (bean != null) {
                        float textGold = bean.t_text_gold;
                        if (textGold > 0) {
                            String content = getResources().getString(R.string.private_chat_des) + textGold + getResources().getString(R.string.gold);
                            mFirstDesTv.setText(content);
                            mVipLl.setVisibility(View.VISIBLE);
                        } else {
                            mVipLl.setVisibility(View.GONE);
                        }
                    } else {
                        mVipLl.setVisibility(View.GONE);
                    }
                }
            }

            @Override
            public void onError(Call call, Exception e, int id) {
                super.onError(call, e, id);
                mVipLl.setVisibility(View.GONE);
            }
        });
    }

    boolean isSending;

    /**
     * 初始化
     */
    private void initStart() {
        String userHeadUrl = getIntent().getStringExtra(Constant.USER_HEAD_URL);
        String mineHeadUrl = getIntent().getStringExtra(Constant.MINE_HEAD_URL);

        mChatAdapter = new ChatAdapter(this, mMessageList);
        mChatAdapter.loadUrl(userHeadUrl, mineHeadUrl);
        mListView.setAdapter(mChatAdapter);
        mListView.setTranscriptMode(ListView.TRANSCRIPT_MODE_NORMAL);
        mListView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        mChatInput.setInputMode(ChatInput.InputMode.NONE);
                        break;
                }
                return false;
            }
        });
        mChatInput.setChatView(new ChatView() {
            @Override
            public void sendText() {
                //同性别不能交流
                if (mUserCenterBean == null) {
                    return;
                }
                String text = mChatInput.getText().toString().trim();
                if (TextUtils.isEmpty(text)) {
                    return;
                }
//                //非vip 普通用户要钱
//                if (getUserVip() == 1 && getUserRole() == 0) {//1 主播 0 用户
                payForText();
//                } else {
//                    if (TextUtils.isEmpty(text)) {
//                        return;
//                    }
//                    TextContent content = new TextContent(text);
//                    if (mConversation != null) {
//                        Message message = mConversation.createSendMessage(content);
//                        sendMessage(message);
//                        mChatInput.setText("");
//                    }
//                }
            }

            @Override
            public void sending() {

            }

            @Override
            public void showToast(String msg) {

            }

            @Override
            public void sendRed() {
                //发送红包
                LogUtil.i("点击了发红包");
                //同性别不能交流
                if (mUserCenterBean == null) {
                    return;
                }
                showRewardDialog();
            }

            @Override
            public void sendVideo() {
                if (mUserCenterBean != null) {
                    //同性别不能交流
                    //判断双方是不是都是用户
                    if (mUserCenterBean.t_role == 0 && getUserRole() == 0) {
                        ToastUtil.showToast(getApplicationContext(), R.string.can_not_communicate);
                        return;
                    }
                    //1 主播 0 用户
                    if (mActorId > 0 && Integer.parseInt(mMineId) > 0) {
                        //如果对方是主播,直接用户对主播发起,如果不是就主播对用户发起
                        if (Constant.showExtremeCharge() && AppManager.getInstance().getUserInfo() != null && AppManager.getInstance().getUserInfo().t_role == 0 && AppManager.getInstance().getUserInfo().t_is_extreme != 0) {
                            ChargeHelper.showInputInviteCodeDialog(ChatActivity.this);
                        } else {
                            if (!isSending) {
                                getSign(mUserCenterBean.t_role == 1);
                            }
                            isSending = true;
                        }
                    }
                }
            }

            @Override
            public void sendPhoto() {
                jumpToCamera();
            }

            @Override
            public void sendImage() {
                ImageHelper.openPictureVideoChoosePage(ChatActivity.this, REQUEST_ALBUM_IMAGE_VIDEO);
            }
        });

//        showSpan();
    }

    /**
     * 调用键盘
     */
    private void showSpan() {
        mChatInput.getEditText().requestFocus();
        getWindow().getDecorView().postDelayed(new Runnable() {
            @Override
            public void run() {
                if (mChatInput.getEditText().requestFocus()) {
                    InputMethodManager imm = (InputMethodManager) getApplicationContext().getSystemService(Context.INPUT_METHOD_SERVICE);
                    if (imm != null) {
                        imm.showSoftInput(mChatInput.getEditText(), InputMethodManager.SHOW_IMPLICIT);
                    }
                }
            }
        }, 400);
    }

    /**
     * 获取消息
     */
    public void getMessage() {
        if (!mIsGetMessage) {
            mIsGetMessage = true;

            if (mConversation != null) {
                List<Message> messages = mConversation.getAllMessage();
                if (messages != null && messages.size() > 0) {
                    mIsGetMessage = false;
                    mMessageList.addAll(messages);
                    mChatAdapter.notifyDataSetChanged();
                    mListView.setSelection(messages.size());
                } else {
                    mIsGetMessage = false;
                }
            } else {
                mIsGetMessage = false;
            }
        }
    }

    /**
     * 非VIP发送文本消息
     */
    private void payForText() {
        Map<String, String> paramMap = new HashMap<>();
        paramMap.put("userId", mMineId);
        paramMap.put("coverConsumeUserId", String.valueOf(mActorId));
        paramMap.put("text", mChatInput.getText().toString().trim());
        OkHttpUtils.post().url(ChatApi.SEND_TEXT_CONSUME)
                .addParams("param", ParamUtil.getParam(paramMap))
                .build().execute(new AjaxCallback<BaseResponse>() {
            @Override
            public void onResponse(BaseResponse response, int id) {
                if (response != null) {
                    if (response.m_istatus == NetCode.SUCCESS || response.m_istatus == 2) {
                        String text = (String) response.m_object;

                        if (mConversation != null && !TextUtils.isEmpty(text)) {
                            TextContent content = new TextContent(text);
                            Message message = mConversation.createSendMessage(content);
                            sendMessage(message);
                            mChatInput.setText("");
                        }

                    } else if (response.m_istatus == -1) {//余额不足
                        ChargeHelper.showSetCoverDialogWithoutVip(ChatActivity.this);
                    } else if (!TextUtils.isEmpty(response.m_strMessage)) {
                        ToastUtil.showToast(getApplicationContext(), response.m_strMessage);
                    } else {
                        ToastUtil.showToast(getApplicationContext(), R.string.system_error);
                    }
                } else {
                    ToastUtil.showToast(getApplicationContext(), R.string.system_error);
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
     * 发送消息
     */
    private void sendMessage(final Message message) {
        if (message != null) {
            message.setOnSendCompleteCallback(new BasicCallback() {
                @Override
                public void gotResult(int i, String s) {
                    if (i == 0) {
                        LogUtil.i("JIM发送消息成功");
                        mMessageList.add(message);
                        mChatAdapter.notifyDataSetChanged();
                        mListView.setSelection(mChatAdapter.getCount() - 1);
                    } else if (i == 898002) {
                        LogUtil.i("用户不存在");
                        mMessageList.add(message);
                        mChatAdapter.notifyDataSetChanged();
                        mListView.setSelection(mChatAdapter.getCount() - 1);
                    } else {
                        LogUtil.i("发送消息失败: " + i + "  描述: " + s);
                    }
                }
            });
            MessageSendingOptions options = new MessageSendingOptions();
            options.setShowNotification(false);
            JMessageClient.sendMessage(message, options);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        try {//全部设为已读
            if (mConversation != null) {
                mConversation.resetUnreadCount();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        try {
            //注销消息接收
            JMessageClient.unRegisterEventReceiver(this);
            unregisterReceiver(mMyBroadcastReceiver);
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
                LogUtil.i("新的文本消息ID: " + fromUser.getUserName());
                mMessageList.add(message);
                mChatAdapter.notifyDataSetChanged();

                CustomContent customContent = (CustomContent) message.getContent();
                if (message.getContentType() != ContentType.text) {
                    final CustomMessageBean customBean = parseCustomMessage(customContent);
                    showGiftMessage(customBean);
                }

                switch (message.getContentType()) {
                    case text: {//文本
                        final String content = ((TextContent) message.getContent()).getText();
                        LogUtil.i("新的文本消息: " + content);
                        break;
                    }
                    case custom: {//自定义
                        LogUtil.i("新的自定义消息: ");
                        break;
                    }
                    case eventNotification: {
                        LogUtil.i("新的notification: ");
                        break;
                    }
                }
            }
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.left_fl: {
                closeSoft();
                finish();
                break;
            }
            case R.id.vip_tv: {
                Intent intent = new Intent(getApplicationContext(), VipCenterActivity.class);
                startActivity(intent);
                break;
            }
            case R.id.focus_tv: {//关注
                saveFollow();
                break;

            }

        }
    }


    /**
     * 添加关注
     */
    private void saveFollow() {
        Map<String, String> paramMap = new HashMap<>();
        paramMap.put("userId", mMineId);//关注人
        paramMap.put("coverFollowUserId", String.valueOf(mActorId));//	被关注人
        OkHttpUtils.post().url(ChatApi.SAVE_FOLLOW)
                .addParams("param", ParamUtil.getParam(paramMap))
                .build().execute(new AjaxCallback<BaseResponse>() {
            @Override
            public void onResponse(BaseResponse response, int id) {
                if (response != null && response.m_istatus == NetCode.SUCCESS) {
                    String message = response.m_strMessage;
                    if (!TextUtils.isEmpty(message) && message.contains(getResources().getString(R.string.success_str))) {
                        ToastUtil.showToast(getApplicationContext(), message);
                        mFocusTv.setVisibility(View.GONE);
                    }
                } else {
                    ToastUtil.showToast(getApplicationContext(), R.string.system_error);
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
     * 获取Vip
     */
    private int getUserVip() {
        if (AppManager.getInstance() != null) {
            ChatUserInfo userInfo = AppManager.getInstance().getUserInfo();
            if (userInfo != null) {
                //是否VIP 0.是1.否
                return userInfo.t_is_vip;
            } else {
                return SharedPreferenceHelper.getAccountInfo(getApplicationContext()).t_is_vip;
            }
        }
        return 2;
    }

    /**
     * 获取角色
     */
    private int getUserRole() {
        if (AppManager.getInstance() != null) {
            ChatUserInfo userInfo = AppManager.getInstance().getUserInfo();
            if (userInfo != null) {
                //1 主播 0 用户
                return userInfo.t_role;
            } else {
                return SharedPreferenceHelper.getAccountInfo(getApplicationContext()).t_role;
            }
        }
        return 0;
    }

    class MyBroadcastReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            String message = intent.getStringExtra(ConnectManager.MESSAGE);
            if (!TextUtils.isEmpty(message)) {
                try {
                    SocketResponse response = JSON.parseObject(message, SocketResponse.class);
                    if (response != null) {
                        if (response.mid == Mid.CHAT_LINK) { //用户来视频了
                            int roomId = response.roomId;
                            int userId = response.connectUserId;
                            Intent videoIntent = new Intent(getApplicationContext(), WaitActorActivity.class);
                            videoIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                            videoIntent.putExtra(Constant.ROOM_ID, roomId);
                            videoIntent.putExtra(Constant.PASS_USER_ID, userId);
                            startActivity(videoIntent);
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }

    }

    @Override
    protected void onResume() {
        super.onResume();
        LogUtil.i("===========onResume");
        if (getUserVip() == 1 && getUserRole() == 0) {//是否VIP 0.是1.否
            getActorSetCharge();
        } else {
            mVipLl.setVisibility(View.GONE);
        }
    }

    /**
     * 关闭软件盘
     */
    private void closeSoft() {
        try {
            if (mChatInput.getEditText() != null && mChatInput.getEditText().hasFocus()) {
                InputMethodManager imm = (InputMethodManager) getApplicationContext().getSystemService(Context.INPUT_METHOD_SERVICE);
                if (imm != null && imm.isActive()) {
                    imm.hideSoftInputFromWindow(mChatInput.getEditText().getWindowToken(), 0);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    //-----------------------礼物相关---------------------

    /**
     * 获取礼物列表
     */
    private void getGiftList() {
        Map<String, String> paramMap = new HashMap<>();
        paramMap.put("userId", mMineId);
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
        /*if (getUserRole() == 1) {
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
        final GiftViewPagerRecyclerAdapter giftAdapter = new GiftViewPagerRecyclerAdapter(ChatActivity.this);
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
        final GoldGridRecyclerAdapter goldGridRecyclerAdapter = new GoldGridRecyclerAdapter(ChatActivity.this);
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
        paramMap.put("userId", mMineId);
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

                        if (mConversation != null) {

                            //动态图
                            showGiftMessage(bean);


                            String json = JSON.toJSONString(bean);
                            CustomContent customContent = new CustomContent();
                            customContent.setStringValue("custom", json);
                            Message msg = mConversation.createSendMessage(customContent);
                            sendMessage(msg);
                        }
                    } else if (response.m_istatus == -1) {
                        ToastUtil.showToast(getApplicationContext(), R.string.gold_not_enough);
                    } else {
                        ToastUtil.showToast(getApplicationContext(), R.string.pay_fail);
                    }
                } else {
                    ToastUtil.showToast(getApplicationContext(), R.string.pay_fail);
                }
            }
        });
    }

    /**
     * 打赏金币(红包)
     */
    private void reWardGold(final int gold) {
        Map<String, String> paramMap = new HashMap<>();
        paramMap.put("userId", mMineId);
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

                        if (mConversation != null) {
                            String json = JSON.toJSONString(bean);
                            CustomContent customContent = new CustomContent();
                            customContent.setStringValue("custom", json);
                            Message msg = mConversation.createSendMessage(customContent);
                            sendMessage(msg);
                        }
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
     * 获取我的金币余额
     */
    private void getMyGold(final TextView can_use_iv) {
        Map<String, String> paramMap = new HashMap<>();
        paramMap.put("userId", mMineId);
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
     * 获取签名,并登陆 然后创建房间,并加入
     */
    private void getSign(final boolean isUserCallActor) {
        String userId;
        String actorId;
        if (isUserCallActor) {
            userId = mMineId;
            actorId = String.valueOf(mActorId);
        } else {
            userId = String.valueOf(mActorId);
            actorId = mMineId;
        }
        Map<String, String> paramMap = new HashMap<>();
        paramMap.put("userId", userId);
        paramMap.put("anthorId", actorId);
        OkHttpUtils.post().url(ChatApi.GET_VIDEO_CHAT_SIGN)
                .addParams("param", ParamUtil.getParam(paramMap))
                .build().execute(new AjaxCallback<BaseResponse<VideoSignBean>>() {
            @Override
            public void onResponse(BaseResponse<VideoSignBean> response, int id) {
                if (response != null && response.m_istatus == NetCode.SUCCESS) {
                    VideoSignBean signBean = response.m_object;
                    if (signBean != null) {
                        int mRoomId = signBean.roomId;
                        int onlineState = signBean.onlineState;
                        if (onlineState == 1 && getUserRole() == 0) {//1.余额刚刚住够
                            showGoldJustEnoughDialog(mRoomId, isUserCallActor);
                            isSending = false;
                        } else {
                            if (isUserCallActor) {//是用户call主播
                                userRequestChat(mRoomId);
                            } else {//主播call用户
                                requestChat(mRoomId);
                            }
                        }
                    } else {
                        ToastUtil.showToast(getApplicationContext(), R.string.system_error);
                        isSending = false;
                    }
                } else if (response != null && !TextUtils.isEmpty(response.m_strMessage)) {
                    ToastUtil.showToast(getApplicationContext(), response.m_strMessage);
                    isSending = false;
                }
            }

            @Override
            public void onBefore(Request request, int id) {
                super.onBefore(request, id);
            }

            @Override
            public void onError(Call call, Exception e, int id) {
                super.onError(call, e, id);
                isSending = false;
                ToastUtil.showToast(getApplicationContext(), R.string.system_error);
            }
        });
    }

    /**
     * 显示金币刚好够dialog
     */
    private void showGoldJustEnoughDialog(int mRoomId, boolean isUserCallActor) {
        final Dialog mDialog = new Dialog(this, R.style.DialogStyle_Dark_Background);
        @SuppressLint("InflateParams")
        View view = LayoutInflater.from(this).inflate(R.layout.dialog_one_minute_layout, null);
        setGoldDialogView(view, mDialog, mRoomId, isUserCallActor);
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

    /**
     * 设置头像选择dialog的view
     */
    private void setGoldDialogView(View view, final Dialog mDialog, final int mRoomId,
                                   final boolean isUserCallActor) {
        //取消
        ImageView close_iv = view.findViewById(R.id.close_iv);
        close_iv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cleanRoom();
                mDialog.dismiss();
            }
        });
        //是 发起聊天
        TextView yes_tv = view.findViewById(R.id.yes_tv);
        yes_tv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isUserCallActor) {//是用户call主播
                    userRequestChat(mRoomId);
                } else {//主播call用户
                    requestChat(mRoomId);
                }
                mDialog.dismiss();
            }
        });
        //充值
        TextView charge_tv = view.findViewById(R.id.charge_tv);
        charge_tv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //清空房间
                cleanRoom();
                Intent intent = new Intent(getApplicationContext(), ChargeActivity.class);
                startActivity(intent);
                mDialog.dismiss();
            }
        });
    }

    /**
     * 清空房间
     */
    private void cleanRoom() {
        Map<String, String> paramMap = new HashMap<>();
        paramMap.put("userId", mMineId);
        OkHttpUtils.post().url(ChatApi.USER_HANG_UP_LINK)
                .addParams("param", ParamUtil.getParam(paramMap))
                .build().execute(new AjaxCallback<BaseResponse>() {
            @Override
            public void onResponse(BaseResponse response, int id) {
                if (response != null && response.m_istatus == NetCode.SUCCESS) {
                    LogUtil.i("清空房间成功");
                }
            }
        });
    }

    /**
     * 主播对用户发起聊天
     */
    private void requestChat(final int roomId) {
        Map<String, String> paramMap = new HashMap<>();
        paramMap.put("anchorUserId", mMineId);
        paramMap.put("userId", String.valueOf(mActorId));
        paramMap.put("roomId", String.valueOf(roomId));
        OkHttpUtils.post().url(ChatApi.ACTOR_LAUNCH_VIDEO_CHAT)
                .addParams("param", ParamUtil.getParam(paramMap))
                .build().execute(new AjaxCallback<BaseResponse>() {
            @Override
            public void onResponse(BaseResponse response, int id) {
                if (response != null) {
                    if (response.m_istatus == NetCode.SUCCESS) {
                        Intent intent = new Intent(getApplicationContext(), VideoChatOneActivity.class);
                        intent.putExtra(Constant.FROM_TYPE, Constant.FROM_ACTOR_INVITE);
                        intent.putExtra(Constant.ROOM_ID, roomId);
                        intent.putExtra(Constant.ACTOR_ID, mActorId);
                        intent.putExtra(Constant.NICK_NAME, mUserCenterBean.nickName);
                        intent.putExtra(Constant.USER_HEAD_URL, mUserCenterBean.handImg);
                        startActivity(intent);
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
                    } else if (response.m_istatus == -3) {//对方设置了勿扰
                        String message = response.m_strMessage;
                        if (!TextUtils.isEmpty(message)) {
                            ToastUtil.showToast(getApplicationContext(), message);
                        } else {
                            ToastUtil.showToast(getApplicationContext(), R.string.not_bother);
                        }
                    } else if (response.m_istatus == -4) {
                        ChargeHelper.showSetCoverDialog(ChatActivity.this);
                    } else if (response.m_istatus == -8) {
                        ToastUtil.showToast(getApplicationContext(), response.m_strMessage);
                    } else {
                        ToastUtil.showToast(getApplicationContext(), R.string.system_error);
                    }
                }
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        isSending = false;
                    }
                }, 500);
            }

            @Override
            public void onError(Call call, Exception e, int id) {
                super.onError(call, e, id);
                ToastUtil.showToast(getApplicationContext(), R.string.system_error);
                isSending = false;
            }

        });
    }

    Handler mHandler = new Handler();

    /**
     * 用户对主播发起聊天
     */
    private void userRequestChat(final int roomId) {
        Map<String, String> paramMap = new HashMap<>();
        paramMap.put("userId", mMineId);
        paramMap.put("coverLinkUserId", String.valueOf(mActorId));
        paramMap.put("roomId", String.valueOf(roomId));
        OkHttpUtils.post().url(ChatApi.LAUNCH_VIDEO_CHAT)
                .addParams("param", ParamUtil.getParam(paramMap))
                .build().execute(new AjaxCallback<BaseResponse>() {
            @Override
            public void onResponse(BaseResponse response, int id) {
                if (response != null) {
                    if (response.m_istatus == NetCode.SUCCESS) {
                        Intent intent = new Intent(getApplicationContext(), VideoChatOneActivity.class);
                        intent.putExtra(Constant.ROOM_ID, roomId);
                        intent.putExtra(Constant.FROM_TYPE, Constant.FROM_USER);
                        intent.putExtra(Constant.ACTOR_ID, mActorId);//主播ID
                        startActivity(intent);
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
                        ChargeHelper.showSetCoverDialog(ChatActivity.this);
                    } else if (response.m_istatus == -8) {
                        ToastUtil.showToast(getApplicationContext(), response.m_strMessage);
                    } else {
                        ToastUtil.showToast(getApplicationContext(), R.string.system_error);
                    }
                }
                mHandler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        isSending = false;
                    }
                }, 500);
            }

            @Override
            public void onError(Call call, Exception e, int id) {
                super.onError(call, e, id);
                ToastUtil.showToast(getApplicationContext(), R.string.system_error);
                isSending = false;
            }

        });
    }

    /**
     * 获取对方是不是主播
     */
    private void getActorInfo(final int userId) {
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
                        mUserCenterBean = bean;
                    }
                }
            }
        });
    }

    /**
     * 显示请求网络数据进度条
     */
    public void showLoadingDialog() {
        try {
            if (!isFinishing() && mDialogLoading != null && !mDialogLoading.isShowing()) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            mDialogLoading.show();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                });
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    /**
     * 关闭请求网络数据进度条
     */
    public void dismissLoadingDialog() {
        try {
            if (!isFinishing() && mDialogLoading != null && mDialogLoading.isShowing()) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            mDialogLoading.dismiss();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                });
            }
        } catch (Exception e) {
            e.printStackTrace();
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
            String filePath = FileUtil.getPathAbove19(this, uri);
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
     * 处理返回的图片,过大的话 就压缩
     * 每次只允许选择一张,所以只处理第一个
     */
    private void dealImageFile(Uri fileUri) {
        try {
            String filePath = FileUtil.getPathAbove19(this, fileUri);
            if (!TextUtils.isEmpty(filePath)) {
                //压缩图片
                compressImageWithLuBan(filePath);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 使用LuBan压缩图片
     */
    private void compressImageWithLuBan(String filePath) {
        ImageHelper.compressImageWithLuBanNotDelete(getApplicationContext(), filePath, Constant.ACTIVE_IMAGE_DIR,
                new OnLuCompressListener() {
                    @Override
                    public void onStart() {
                        showLoadingDialog();
                    }

                    @Override
                    public void onSuccess(File file) {
                        if (!TextUtils.isEmpty(file.getAbsolutePath())) {
                            uploadImageFileWithQQ(file);
                        } else {
                            ToastUtil.showToast(getApplicationContext(), R.string.choose_picture_failed);
                            dismissLoadingDialog();
                        }
                    }

                    @Override
                    public void onError(Throwable e) {
                        ToastUtil.showToast(getApplicationContext(), R.string.choose_picture_failed);
                        dismissLoadingDialog();
                    }
                });
    }

    /**
     * 使用腾讯云上传封面文件
     */
    private void uploadImageFileWithQQ(final File file) {
        String filePath = file.getAbsolutePath();
        //文件名
        String fileName;
        if (filePath.length() < 50) {
            fileName = filePath.substring(filePath.length() - 17, filePath.length());
        } else {
            String last = filePath.substring(filePath.length() - 4, filePath.length());
            if (last.contains("png")) {
                fileName = System.currentTimeMillis() + ".png";
            } else {
                fileName = System.currentTimeMillis() + ".jpg";
            }
        }

        String cosPath = "/active/" + fileName;
        long signDuration = 600; //签名的有效期，单位为秒
        PutObjectRequest putObjectRequest = new PutObjectRequest(BuildConfig.tencentCloudBucket, cosPath, filePath);
        putObjectRequest.setSign(signDuration, null, null);
        mQServiceCfg.getCosCxmService().putObjectAsync(putObjectRequest, new CosXmlResultListener() {
            @Override
            public void onSuccess(CosXmlRequest request, CosXmlResult result) {
                LogUtil.i("腾讯云动态success =  " + result.accessUrl);
                String resultUrl = result.accessUrl;
                if (!resultUrl.contains("http") || !resultUrl.contains("https")) {
                    resultUrl = "https://" + resultUrl;
                }

                dismissLoadingDialog();
                //发送自定义消息
                CustomMessageBean bean = new CustomMessageBean();
                bean.type = "2";
                bean.picUrl = resultUrl;
                if (mConversation != null) {
                    String json = JSON.toJSONString(bean);
                    CustomContent customContent = new CustomContent();
                    customContent.setStringValue("custom", json);
                    Message message = mConversation.createSendMessage(customContent);
                    sendMessage(message);
                }
            }

            @Override
            public void onFail(CosXmlRequest cosXmlRequest, CosXmlClientException clientException, CosXmlServiceException serviceException) {
                String errorMsg = clientException != null ? clientException.toString() : serviceException.toString();
                LogUtil.i("腾讯云fail: " + errorMsg);
                dismissLoadingDialog();
                ToastUtil.showToast(getApplicationContext(), R.string.upload_fail);
            }
        });
    }

    /**
     * ---------------------------处理视频文件-------------------------------------
     */
    private void dealVideoFile(Uri videoUri) {
        try {
            String filePath = VideoFileUtils.getRealPathFromUri(this, videoUri);
            if (!TextUtils.isEmpty(filePath)) {
                File file = new File(filePath);
                LogUtil.i("视频大小: " + file.length() / 1024 / 1024);
                double fileSize = (double) file.length() / 1024 / 1024;
                if (fileSize > 50) {
                    ToastUtil.showToast(getApplicationContext(), R.string.file_too_big);
                    return;
                }
                showLoadingDialog();

                //获取视频缩略图并显示
                VideoThumbTask task = new VideoThumbTask(ChatActivity.this, filePath);
                task.execute();
            } else {
                ToastUtil.showToast(getApplicationContext(), R.string.upload_fail);
                dismissLoadingDialog();
            }
        } catch (Exception e) {
            e.printStackTrace();
            dismissLoadingDialog();
        }
    }

    /**
     * 获取视频上传签名
     * fromCheck  是鉴黄用的签名 还是上传用的
     */
    private void getSign(final String filePath) {
        Map<String, String> paramMap = new HashMap<>();
        paramMap.put("userId", getUserId());
        OkHttpUtils.post().url(ChatApi.GET_VIDEO_SIGN)
                .addParams("param", ParamUtil.getParam(paramMap))
                .build().execute(new StringCallback() {
            @Override
            public void onError(Call call, Exception e, int id) {
                ToastUtil.showToast(getApplicationContext(), R.string.upload_fail);
            }

            @Override
            public void onResponse(String response, int id) {
                if (!TextUtils.isEmpty(response)) {
                    JSONObject jsonObject = JSON.parseObject(response);
                    int m_istatus = jsonObject.getInteger("m_istatus");
                    if (m_istatus == NetCode.SUCCESS) {
                        String m_object = jsonObject.getString("m_object");
                        if (!TextUtils.isEmpty(m_object)) {
                            //上传文件
                            beginUpload(m_object, filePath);
                        } else {
                            ToastUtil.showToast(getApplicationContext(), R.string.upload_fail);
                        }
                    } else {
                        ToastUtil.showToast(getApplicationContext(), R.string.upload_fail);
                    }
                } else {
                    ToastUtil.showToast(getApplicationContext(), R.string.upload_fail);
                }
            }
        });
    }

    /**
     * 开始上传
     */
    private void beginUpload(final String sign, final String filePath) {
        if (mVideoPublish == null) {
            mVideoPublish = new TXUGCPublish(this.getApplicationContext(), "carol_android");
            mVideoPublish.setListener(new TXUGCPublishTypeDef.ITXVideoPublishListener() {
                @Override
                public void onPublishProgress(long uploadBytes, long totalBytes) {
//                    mProcessPv.setProcess((int) (100 * uploadBytes / totalBytes));
                }

                @Override
                public void onPublishComplete(TXUGCPublishTypeDef.TXPublishResult result) {
                    dismissLoadingDialog();
                    if (result.retCode == 0) {//上传成功
                        LogUtil.i("视频文件id: " + result.videoId);
                        LogUtil.i("视频文件url: " + result.videoURL);

                        //发送自定义消息
                        CustomMessageBean bean = new CustomMessageBean();
                        bean.type = "3";
                        bean.videoDuration = mVideoDuration;
                        bean.videoURL = result.videoURL;
                        bean.coverURL = mVideoThumbnailUrl;
                        if (mConversation != null) {
                            String json = JSON.toJSONString(bean);
                            CustomContent customContent = new CustomContent();
                            customContent.setStringValue("custom", json);
                            Message message = mConversation.createSendMessage(customContent);
                            sendMessage(message);
                        }
                    } else if (result.retCode == 1015) {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                ToastUtil.showToast(getApplicationContext(), R.string.upload_fail_1015);
                            }
                        });
                    } else {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                ToastUtil.showToast(getApplicationContext(), R.string.upload_fail);
                            }
                        });
                    }
                }
            });
        }

        TXUGCPublishTypeDef.TXPublishParam param = new TXUGCPublishTypeDef.TXPublishParam();
        // signature计算规则可参考 https://www.qcloud.com/document/product/266/9221
        param.signature = sign;
        param.videoPath = filePath;
        int publishCode = mVideoPublish.publishVideo(param);
        if (publishCode != 0) {
            LogUtil.i("发布失败，错误码：" + publishCode);
        }
    }

    /**
     * 获取UserId
     */
    public String getUserId() {
        String sUserId = "";
        if (AppManager.getInstance() != null) {
            ChatUserInfo userInfo = AppManager.getInstance().getUserInfo();
            if (userInfo != null) {
                int userId = userInfo.t_id;
                if (userId >= 0) {
                    sUserId = String.valueOf(userId);
                }
            } else {
                int id = SharedPreferenceHelper.getAccountInfo(getApplicationContext()).t_id;
                sUserId = String.valueOf(id);
            }
        }
        return sUserId;
    }

    /**
     * 跳转到拍照
     */
    private void jumpToCamera() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager
                    .PERMISSION_GRANTED &&
                    ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.RECORD_AUDIO) == PackageManager
                            .PERMISSION_GRANTED &&
                    ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.CAMERA) == PackageManager
                            .PERMISSION_GRANTED) {
                Intent intent = new Intent(getApplicationContext(), CameraActivity.class);
                startActivityForResult(intent, CAMERA_REQUEST_CODE);
            } else {
                //不具有获取权限，需要进行权限申请
                ActivityCompat.requestPermissions(ChatActivity.this, new String[]{
                        Manifest.permission.WRITE_EXTERNAL_STORAGE,
                        Manifest.permission.RECORD_AUDIO,
                        Manifest.permission.CAMERA}, 100);
            }
        } else {
            Intent intent = new Intent(getApplicationContext(), CameraActivity.class);
            startActivityForResult(intent, CAMERA_REQUEST_CODE);
        }
    }

    /**
     * 处理拍摄的视频,包括传递的过来的
     */
    private void dealShootVideoFile(String videoUrl) {
        File file = new File(videoUrl);
        if (!file.exists()) {
            ToastUtil.showToast(getApplicationContext(), R.string.file_not_exist);
            return;
        }
        LogUtil.i("视频大小: " + file.length() / 1024 / 1024);
        double fileSize = (double) file.length() / 1024 / 1024;
        if (fileSize > 50) {
            ToastUtil.showToast(getApplicationContext(), R.string.file_too_big);
            return;
        }
        showLoadingDialog();
        //获取视频缩略图并显示
        VideoThumbTask task = new VideoThumbTask(ChatActivity.this, videoUrl);
        task.execute();
    }

    class VideoThumbTask extends AsyncTask<Integer, Void, Bitmap> {

        private WeakReference<ChatActivity> mWeakAty;
        private String mPath;

        VideoThumbTask(ChatActivity activity, String path) {
            mWeakAty = new WeakReference<>(activity);
            mPath = path;
        }

        @Override
        protected Bitmap doInBackground(Integer... integers) {
            final ChatActivity activity = mWeakAty.get();
            if (activity != null) {
                try {
                    VideoRetrieverBean retrieverBean = VideoFileUtils.getVideoInfo(mPath);
                    Bitmap bitmap = retrieverBean.bitmap;
                    activity.mVideoDuration = retrieverBean.videoDuration;
                    ByteArrayOutputStream baos = new ByteArrayOutputStream();
                    bitmap.compress(Bitmap.CompressFormat.PNG, 100, baos);
                    byte[] bytes = baos.toByteArray();
                    uploadImageFileWithQQ(bytes);
                    return bitmap;
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            return null;
        }

        @Override
        protected void onPostExecute(Bitmap bitmap) {

        }
    }

    /**
     * 使用腾讯云上传封面文件
     */
    private void uploadImageFileWithQQ(byte[] data) {
        String fileName = System.currentTimeMillis() + ".png";
        String cosPath = "/active/" + fileName;
        long signDuration = 600; //签名的有效期，单位为秒
        PutObjectRequest putObjectRequest = new PutObjectRequest(BuildConfig.tencentCloudBucket, cosPath, data);
        putObjectRequest.setSign(signDuration, null, null);
        mQServiceCfg.getCosCxmService().putObjectAsync(putObjectRequest, new CosXmlResultListener() {
            @Override
            public void onSuccess(CosXmlRequest request, CosXmlResult result) {
                LogUtil.i("腾讯云动态success =  " + result.accessUrl);
                mVideoThumbnailUrl = result.accessUrl;
                if (!mVideoThumbnailUrl.contains("http") || !mVideoThumbnailUrl.contains("https")) {
                    mVideoThumbnailUrl = "https://" + mVideoThumbnailUrl;
                }
                //获取签名
                if (mVideoUri != null) {
                    getSign(VideoFileUtils.getRealPathFromUri(ChatActivity.this, mVideoUri));
                } else {
                    getSign(mVideoLocalPath);
                }
            }

            @Override
            public void onFail(CosXmlRequest cosXmlRequest, CosXmlClientException clientException, CosXmlServiceException serviceException) {
                String errorMsg = clientException != null ? clientException.toString() : serviceException.toString();
                LogUtil.i("腾讯云fail: " + errorMsg);
                dismissLoadingDialog();
                ToastUtil.showToast(getApplicationContext(), R.string.upload_fail);
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            if (requestCode == REQUEST_ALBUM_IMAGE_VIDEO) {
                List<Uri> mSelectedUris = Matisse.obtainResult(data);
                if (mSelectedUris != null && mSelectedUris.size() > 0) {
                    if (checkUri(mSelectedUris)) {//判断通过
                        Uri fileUri = mSelectedUris.get(0);
                        if (fileUri != null) {
                            //如果是图片
                            if (!fileUri.toString().contains("video")) {
                                dealImageFile(fileUri);
                            } else {
                                //如果是视频
                                mVideoUri = fileUri;
                                dealVideoFile(fileUri);
                            }
                        }
                    }
                }
            }
        } else if (requestCode == CAMERA_REQUEST_CODE && data != null) {
            if (resultCode == 101) {//图片
                String imagePath = data.getStringExtra("imagePath");
                LogUtil.i("相机拍照图片:  " + imagePath);
                if (!TextUtils.isEmpty(imagePath)) {
                    compressImageWithLuBan(imagePath);
                } else {
                    ToastUtil.showToast(getApplicationContext(), R.string.file_invalidate);
                }
            } else if (resultCode == 102) {//视频
                String videoUrl = data.getStringExtra("videoUrl");
                LogUtil.i("相机录视频Url:  " + videoUrl);
                if (!TextUtils.isEmpty(videoUrl)) {
//                    mPostType = VIDEO;
                    mVideoUri = null;
                    mVideoLocalPath = videoUrl;
                    dealShootVideoFile(videoUrl);
                } else {
                    ToastUtil.showToast(getApplicationContext(), R.string.file_invalidate);
                }
            }
        }
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
                    handler.sendEmptyMessageDelayed(1,
                            duration);
                    return false;
                }
            }).into(new GlideDrawableImageViewTarget(enter_room_gif, 1));
        } else {
            gift_svga.setVisibility(View.VISIBLE);
            enter_room_gif.setVisibility(View.GONE);
            gift_svga.setFocusable(true);
            gift_svga.setClickable(true);
            loadAnimation(url, "" + bean.gift_id);
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

       /* GifCacheUtil.getFile(ChatApi.GIF_PATH + id, url, new CommonCallback<File>() {
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
            });*/


    }

    private Handler handler = new Handler() {
        @Override
        public void handleMessage(android.os.Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case 1:
                    enter_room_gif.setVisibility(View.GONE);
                    break;
            }
        }
    };

    private CommonCallback<File> mDownloadGifCallback;

    /**
     * 调整mGifImageView的大小
     */
    private void resizeGifImageView(Drawable drawable) {
        float w = drawable.getIntrinsicWidth();
        float h = drawable.getIntrinsicHeight();
        ViewGroup.LayoutParams params = enter_room_gif.getLayoutParams();
        params.height = (int) (enter_room_gif.getWidth() * h / w);
        enter_room_gif.setLayoutParams(params);
    }

//    /**
//     * 调整mSVGAImageView的大小
//     */
//    private void resizeSvgaImageView(double w, double h) {
//        ViewGroup.LayoutParams params = mSVGAImageView.getLayoutParams();
//        params.height = (int) (mSVGAImageView.getWidth() * h / w);
//        mSVGAImageView.setLayoutParams(params);
//    }
    /**
     * 播放豪华礼物
     */
//    private void playHaoHuaGift(File file) {
//        if (mTempGifGiftBean.getGitType() == 0) {//豪华礼物类型 0是gif  1是svga
//            if (mTempGifGiftBean != null) {
//                mGifGiftTip.setText(mTempGifGiftBean.getUserNiceName() + "  " + mSendString + mTempGifGiftBean.getGiftName());
//                mGifGiftTipGroup.setAlpha(1f);
//                mGifGiftTipShowAnimator.start();
//            }
//            playGift(file);
//        } else {
//            SVGAVideoEntity svgaVideoEntity = null;
//            if (mSVGAMap != null) {
//                SoftReference<SVGAVideoEntity> reference = mSVGAMap.get(mTempGifGiftBean.getGiftId());
//                if (reference != null) {
//                    svgaVideoEntity = reference.get();
//                }
//            }
//            if (svgaVideoEntity != null) {
//                playSVGA(svgaVideoEntity);
//            } else {
//                decodeSvga(file);
//            }
//        }
//    }

    /**
     * 解析自定义消息
     */
    private CustomMessageBean parseCustomMessage(CustomContent customElem) {
        try {
            String json = customElem.getStringValue("custom");
            return JSON.parseObject(json, CustomMessageBean.class);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }


}
