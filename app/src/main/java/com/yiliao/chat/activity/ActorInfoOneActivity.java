package com.yiliao.chat.activity;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.graphics.Point;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.OrientationHelper;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.yiliao.chat.BuildConfig;
import com.yiliao.chat.R;
import com.yiliao.chat.adapter.GiftViewPagerRecyclerAdapter;
import com.yiliao.chat.adapter.GoldGridRecyclerAdapter;
import com.yiliao.chat.banner.MZBannerView;
import com.yiliao.chat.banner.MZHolderCreator;
import com.yiliao.chat.banner.MZViewHolder;
import com.yiliao.chat.base.AppManager;
import com.yiliao.chat.base.BaseActivity;
import com.yiliao.chat.base.BaseListResponse;
import com.yiliao.chat.base.BaseResponse;
import com.yiliao.chat.bean.ActorInfoBean;
import com.yiliao.chat.bean.BalanceBean;
import com.yiliao.chat.bean.ChargeBean;
import com.yiliao.chat.bean.ChatUserInfo;
import com.yiliao.chat.bean.CoverUrlBean;
import com.yiliao.chat.bean.CustomMessageBean;
import com.yiliao.chat.bean.GiftBean;
import com.yiliao.chat.bean.GoldBean;
import com.yiliao.chat.bean.InfoRoomBean;
import com.yiliao.chat.bean.LabelBean;
import com.yiliao.chat.bean.RecivieBean;
import com.yiliao.chat.bean.ShareInformitionBean;
import com.yiliao.chat.bean.ShareLayoutBean;
import com.yiliao.chat.bean.VideoSignBean;
import com.yiliao.chat.constant.ChatApi;
import com.yiliao.chat.constant.Constant;
import com.yiliao.chat.fragment.ActorVideoFragment;
import com.yiliao.chat.fragment.InfoActiveFragment;
import com.yiliao.chat.fragment.PersonInfoOneFragment;
import com.yiliao.chat.helper.ChargeHelper;
import com.yiliao.chat.helper.ImageLoadHelper;
import com.yiliao.chat.helper.SharedPreferenceHelper;
import com.yiliao.chat.layoutmanager.ViewPagerLayoutManager;
import com.yiliao.chat.listener.OnItemClickListener;
import com.yiliao.chat.listener.OnViewPagerListener;
import com.yiliao.chat.mob.MobCallback;
import com.yiliao.chat.mob.MobConst;
import com.yiliao.chat.mob.MobShareUtil;
import com.yiliao.chat.mob.ShareData;
import com.yiliao.chat.net.AjaxCallback;
import com.yiliao.chat.net.NetCode;
import com.yiliao.chat.socket.ConnectManager;
import com.yiliao.chat.socket.domain.Mid;
import com.yiliao.chat.socket.domain.SocketResponse;
import com.yiliao.chat.util.DevicesUtil;
import com.yiliao.chat.util.DialogUtil;
import com.yiliao.chat.util.LogUtil;
import com.yiliao.chat.util.ParamUtil;
import com.yiliao.chat.util.ToastUtil;
import com.zhy.http.okhttp.OkHttpUtils;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;
import cn.jpush.im.android.api.JMessageClient;
import cn.jpush.im.android.api.content.CustomContent;
import cn.jpush.im.android.api.model.Conversation;
import cn.jpush.im.android.api.model.Message;
import cn.jpush.im.android.api.options.MessageSendingOptions;
import cn.jpush.im.api.BasicCallback;
import okhttp3.Call;
import okhttp3.Request;

/*
 * Copyright (C) 2018
 * 版权所有
 *
 * 功能描述：主播资料新页面
 * 作者：
 * 创建时间：2018/6/21
 *
 * 修改人：
 * 修改描述：
 * 修改日期
 */
public class ActorInfoOneActivity extends AppCompatActivity {

    @BindView(R.id.my_banner)
    MZBannerView<CoverUrlBean> mMZBannerView;
    @BindView(R.id.content_vp)
    ViewPager mContentVp;
    //昵称
    @BindView(R.id.nick_tv)
    TextView mNickTv;
    //职业
    @BindView(R.id.job_tv)
    TextView mJobTv;
    //粉丝数
    @BindView(R.id.fans_number_tv)
    TextView mFansNumberTv;
    //金币数
    @BindView(R.id.price_tv)
    TextView mPriceTv;
    //状态
    @BindView(R.id.status_tv)
    TextView mStatusTv;
    //性别
    @BindView(R.id.ivSex)
    ImageView ivSex;
    //年龄
    @BindView(R.id.age_tv)
    TextView mAgeTv;
    //签名
    @BindView(R.id.sign_tv)
    TextView mSignTv;
    //title的昵称
    @BindView(R.id.title_nick_tv)
    TextView mTitleNickTv;
    //关注
    @BindView(R.id.focus_iv)
    ImageView mFocusIv;

    //tabs
    @BindView(R.id.tabs)
    TabLayout mTabLayout;
    //ToolBar
    @BindView(R.id.title_tb)
    Toolbar mTitleTb;
    //ToolBar右边点
    @BindView(R.id.dian_white_iv)
    ImageView mDianWhiteIv;
    @BindView(R.id.dian_black_iv)
    ImageView mDianBlackIv;
    //ToolBar返回
    @BindView(R.id.back_white_iv)
    ImageView mBackWhiteIv;
    @BindView(R.id.back_black_iv)
    ImageView mBackBlackIv;
    //CollapsingToolbarLayout
    @BindView(R.id.toolbar_layout)
    CollapsingToolbarLayout mCollapsingToolbarLayout;
    //AppBarLayout
    @BindView(R.id.appbar_layout)
    AppBarLayout mAppBarLayout;
    //直播中
    @BindView(R.id.living_tv)
    TextView mLivingTv;
    @BindView(R.id.video_chat_tv)
    TextView mVideoChatTv;
    @BindView(R.id.price_layout)
    LinearLayout price_layout;
    @BindView(R.id.chat_free_layout)
    LinearLayout chat_free_layout;

//    @BindView(R.id.place_iv_gree)
//    ImageView place_iv_gree;

    @BindView(R.id.go_home)
    FrameLayout go_home;
    @BindView(R.id.is_go_to_home)
    ImageView is_go_to_home;

    //点击弹框
    @BindView(R.id.dian_fl)
    FrameLayout dian_fl;
    @BindView(R.id.video_chat_fl_yuebo)
    FrameLayout video_chat_fl_yuebo;
    @BindView(R.id.video_chat_fl)
    FrameLayout video_chat_fl;
    @BindView(R.id.chat_free_tv)
    TextView chat_free_tv;


    String glod_price; //服务价格
    private int mActorId;//对方id
    private ActorInfoBean<CoverUrlBean, LabelBean, ChargeBean, InfoRoomBean> mActorInfoBean;
    //粉丝数
    private int mFansNumber;
    private PersonInfoOneFragment mPersonInfoFragment;
    private ActorVideoFragment mActorVideoFragment;
    private InfoActiveFragment mInfoActiveFragment;
    //注解
    private Unbinder mUnbinder;
    //礼物相关
    private int mMyGoldNumber;
    private List<GiftBean> mGiftBeans = new ArrayList<>();
    private InfoRoomBean mInfoRoomBean;
    private String mIsBlack;//判断是否拉黑
    public String t_id;//主播id
    public String traffic;
    public String t_price;
    //接收socket 广播
    private MyBroadcastReceiver mMyBroadcastReceiver;
    Handler mHandler=new Handler();
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_info_one_layout);
        mUnbinder = ButterKnife.bind(this);
        mActorId = getIntent().getIntExtra(Constant.ACTOR_ID, 0);
        if (!Constant.hideHomeNearAndNew()) {
            video_chat_fl_yuebo.setVisibility(View.GONE);
            go_home.setVisibility(View.GONE);
            video_chat_fl.setVisibility(View.VISIBLE);
        } else {
            video_chat_fl.setVisibility(View.VISIBLE);
            go_home.setVisibility(View.VISIBLE);
            video_chat_fl.setVisibility(View.GONE);
            mPriceTv.setTextColor(Color.parseColor("#7275FF"));
            mFansNumberTv.setTextColor(Color.parseColor("#7275FF"));
            mTabLayout.setSelectedTabIndicatorColor(Color.parseColor("#7275FF"));
        }
        initBar();
        initFragment();
        getActorInfo(mActorId);
        getGiftList();
        //获取分享信息
        geShareInformaiton();

    }

    class MyBroadcastReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            String message = intent.getStringExtra(ConnectManager.MESSAGE);
            if (!TextUtils.isEmpty(message)) {
                try {
                    SocketResponse response = JSON.parseObject(message, SocketResponse.class);
                    if (response != null) {
                        if (response.mid == Mid.NOTICE_USER) {
                            JSONObject jc = new JSONObject(message);
                            RecivieBean mess = new RecivieBean();
                            mess.message = jc.getString("message");
                            ToastUtil.show(mess.message);
                        }
                        if (response.mid == Mid.payServerOrder) {
                            JSONObject jc = new JSONObject(message);
                            RecivieBean mess = new RecivieBean();
                            mess.message = jc.getString("message");
                            ToastUtil.show(mess.message);
                        }
                        if (response.mid == Mid.startServer) {
                            JSONObject jc = new JSONObject(message);
                            RecivieBean mess = new RecivieBean();
                            mess.message = jc.getString("message");
                            ToastUtil.show(mess.message);
                        }
                        if (response.mid == Mid.refuseServer) {
                            JSONObject jc = new JSONObject(message);
                            RecivieBean mess = new RecivieBean();
                            mess.message = jc.getString("message");
                            ToastUtil.show(mess.message);
                        }
                        if (response.mid == Mid.completServer) {
                            JSONObject jc = new JSONObject(message);
                            RecivieBean mess = new RecivieBean();
                            mess.message = jc.getString("message");
                            ToastUtil.show(mess.message);
                        }
                        if (response.mid == Mid.addServerTime) {
                            JSONObject jc = new JSONObject(message);
                            RecivieBean mess = new RecivieBean();
                            mess.message = jc.getString("message");
                            ToastUtil.show(mess.message);
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

    /**
     * 初始化bar
     */
    private void initBar() {
        mCollapsingToolbarLayout.setTitle(getString(R.string.no_text));
        setSupportActionBar(mTitleTb);
        mAppBarLayout.addOnOffsetChangedListener(new AppBarLayout.OnOffsetChangedListener() {
            @Override
            public void onOffsetChanged(AppBarLayout appBarLayout, int verticalOffset) {
                float fraction = Math.abs(verticalOffset * 1.0f) / appBarLayout.getTotalScrollRange();
                mTitleTb.setBackgroundColor(changeAlpha(getResources().getColor(R.color.white),
                        fraction));
                mTitleNickTv.setTextColor(changeAlpha(getResources().getColor(R.color.black_333333),
                        fraction));
                float imageAlpha = fraction * 255;
                mDianWhiteIv.setImageAlpha((int) (255 - imageAlpha));
                mDianBlackIv.setImageAlpha((int) imageAlpha);
                mBackWhiteIv.setImageAlpha((int) (255 - imageAlpha));
                mBackBlackIv.setImageAlpha((int) imageAlpha);
            }
        });

        //拉黑弹框初始化
        view = getLayoutInflater().inflate(R.layout.actotinfoonepopwind, null, false);
        Window_PullBlack = view.findViewById(R.id.Window_PullBlack);
    }

    /**
     * 初始化Fragment
     */
    private void initFragment() {
        Bundle bundle = new Bundle();
        bundle.putInt(Constant.ACTOR_ID, mActorId);
        mPersonInfoFragment = new PersonInfoOneFragment();
        mPersonInfoFragment.setArguments(bundle);
        mActorVideoFragment = new ActorVideoFragment();
        mActorVideoFragment.setArguments(bundle);
        mInfoActiveFragment = new InfoActiveFragment();
        mInfoActiveFragment.setArguments(bundle);
    }

    /**
     * 获取主播资料
     */
    private void getActorInfo(int actorId) {
        Map<String, String> paramMap = new HashMap<>();
        paramMap.put("userId", getUserId());//查看人
        paramMap.put("coverUserId", String.valueOf(actorId));//被查看人
        paramMap.put("t_id", String.valueOf(mActorId));
        OkHttpUtils.post().url(ChatApi.GET_ACTOR_INFO)
                .addParams("param", ParamUtil.getParam(paramMap))
                .build().execute(new AjaxCallback<BaseResponse<ActorInfoBean<CoverUrlBean, LabelBean, ChargeBean, InfoRoomBean>>>() {
            @Override
            public void onResponse(BaseResponse<ActorInfoBean<CoverUrlBean, LabelBean, ChargeBean, InfoRoomBean>> response, int id) {
                if (isFinishing()) {
                    return;
                }
                if (response != null && response.m_istatus == NetCode.SUCCESS) {
                    ActorInfoBean<CoverUrlBean, LabelBean, ChargeBean, InfoRoomBean> bean = response.m_object;
                    if (bean != null) {
                        mActorInfoBean = bean;
                        if (Constant.hideHomeNearAndNew()) {
                            if (mActorInfoBean.t_visit != 0 && mActorInfoBean.orderStatus == 0) {
                                chat_free_tv.setVisibility(View.GONE);

                                is_go_to_home.setBackgroundResource(R.mipmap.background_gree);
                                go_home.setEnabled(false);
                            } else {
                                chat_free_tv.setText("与TA视频前" + mActorInfoBean.freeChar + "分钟免费");
                                is_go_to_home.setVisibility(View.VISIBLE);
                                chat_free_tv.setVisibility(View.VISIBLE);
                            }
                        }
                        traffic = bean.traffic + "";
                        t_price = bean.t_price + "";
                        t_id = bean.t_id + "";
                        //昵称
                        String nick = bean.t_nickName;
                        if (!TextUtils.isEmpty(nick)) {
                            mNickTv.setText(nick);
                            mTitleNickTv.setText(nick);
                        }
                        //职业
                        String job = bean.t_vocation;
                        if (!TextUtils.isEmpty(job)) {
                            mJobTv.setText(job);
                        }
                        //粉丝数
                        mFansNumber = bean.totalCount;
                        if (mFansNumber > 0) {
                            mFansNumberTv.setText(String.valueOf(mFansNumber));
                        }
                        //性别
                        ivSex.setVisibility(View.VISIBLE);
                        if (bean.t_sex == 0) {//女
                            ivSex.setBackgroundResource(R.drawable.bg_women);
                            ivSex.setImageResource(R.mipmap.female_white_new);
                        } else {//男
                            ivSex.setBackgroundResource(R.drawable.bg_man);
                            ivSex.setImageResource(R.mipmap.male_white_new);
                        }

                        //岁数
                        if (bean.t_age > 0) {
                            mAgeTv.setText(String.valueOf(bean.t_age));
                            mAgeTv.setVisibility(View.VISIBLE);
                        }
                        //个性签名
                        if (!TextUtils.isEmpty(bean.t_autograph)) {
                            mSignTv.setText(bean.t_autograph);
                        } else {
                            mSignTv.setText(getString(R.string.lazy));
                        }
                        //关注  是否关注 0.未关注 1.已关注
                        int mIsFollowed = bean.isFollow;
                        if (mIsFollowed == 0) {
                            mFocusIv.setSelected(false);
                        } else {
                            mFocusIv.setSelected(true);
                        }
                        //视频聊天价格
                        if (bean.anchorSetup != null && bean.anchorSetup.size() > 0) {
                            ChargeBean chargeBean = bean.anchorSetup.get(0);
                            if (chargeBean != null && chargeBean.t_video_gold > 0) {
                                mPriceTv.setText(String.valueOf(chargeBean.t_video_gold));
                                glod_price = String.valueOf(chargeBean.t_video_gold);
                            }
                        }
                        //轮播图
                        List<CoverUrlBean> coverUrlBeanList = bean.lunbotu;
                        if (coverUrlBeanList != null && coverUrlBeanList.size() > 0) {
                            setBanner(coverUrlBeanList);
                        }
                        //状态
                        //处理状态 主播状态(0.空闲1.忙碌2.离线)
                        int state = bean.t_onLine;

                        if (state == 0) {//空闲
                            mStatusTv.setText(getString(R.string.free));
                        } else if (state == 1) {//忙碌
                            mStatusTv.setText(getString(R.string.busy));
                        } else if (state == 2) {//离线
                            mStatusTv.setText(getString(R.string.offline));
                        }
                        //处理大房间
                        mInfoRoomBean = bean.bigRoomData;
                        if (mInfoRoomBean != null && mInfoRoomBean.t_is_debut == 1 && mInfoRoomBean.t_room_id > 0
                                && mInfoRoomBean.t_chat_room_id > 0) {
                            mLivingTv.setVisibility(View.VISIBLE);
                            mVideoChatTv.setText(getString(R.string.enter_house));
                        }
                        //角色
                        bindViewPager(bean.t_role, bean);
                        if (bean.t_visit == 0) { //判断是否“可上门”
                            chat_free_layout.setVisibility(View.VISIBLE);
                        } else {
                            chat_free_layout.setVisibility(View.GONE);
                        }

                        //判断是否拉黑
                        mIsBlack = bean.isBlack;
                        if (bean.isBlack.equals("1")) {
                            Window_PullBlack.setText("已拉黑");
                        } else {
                            Window_PullBlack.setText("拉黑");
                        }
                    }
                }
            }

            @Override
            public void onError(Call call, Exception e, int id) {
                super.onError(call, e, id);
                ToastUtil.show(getResources().getString(R.string.system_error));
            }
        });
    }

    /**
     * 初始化下方资料ViewPager
     */
    private void bindViewPager(int role, ActorInfoBean<CoverUrlBean, LabelBean, ChargeBean, InfoRoomBean> bean) {
        //标题
        final List<String> mTitle = new ArrayList<>();
        mTitle.add(getString(R.string.info));
        mTitle.add(getString(R.string.video));
        if (!Constant.hideActorInfoVideo() && role == 1) {//主播
            mTitle.add(getString(R.string.active));
        }
        final List<Fragment> list = new ArrayList<>();
        list.add(0, mPersonInfoFragment);
        list.add(1, mActorVideoFragment);
        if (!Constant.hideActorInfoVideo() && role == 1) {
            list.add(2, mInfoActiveFragment);
        }
        mContentVp.setAdapter(new FragmentPagerAdapter(getSupportFragmentManager()) {
            @Override
            public Fragment getItem(int position) {
                return list.get(position);
            }

            @Override
            public int getCount() {
                return list.size();
            }

            @Nullable
            @Override
            public CharSequence getPageTitle(int position) {
                return mTitle.get(position);
            }
        });
        if (!Constant.hideActorInfoVideo() && role == 1) {
            mContentVp.setOffscreenPageLimit(3);
        } else {
            mContentVp.setOffscreenPageLimit(2);
        }
        mTabLayout.setupWithViewPager(mContentVp);
        //资料页加载数据
        if (mPersonInfoFragment != null && mPersonInfoFragment.mIsViewPrepared) {
            mPersonInfoFragment.loadData(bean);
        }
    }

    @OnClick({R.id.back_fl, R.id.dian_fl, R.id.message_iv, R.id.gift_iv, R.id.video_chat_fl, R.id.go_home, R.id.video_chat_fl_yuebo,
            R.id.focus_iv})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.back_fl: {//返回
                finish();
                break;
            }
            case R.id.dian_fl: {//分享
                getPoPwindow();
                mPoPwindow.showAsDropDown(dian_fl, 0, 0);


                break;
            }
            case R.id.message_iv: {//私信
                if (mActorInfoBean == null) {
                    return;
                }
                String mineUrl = SharedPreferenceHelper.getAccountInfo(getBaseContext()).headUrl;
                Intent intent = new Intent(getApplicationContext(), ChatActivity.class);
                intent.putExtra(Constant.TITLE, mActorInfoBean.t_nickName);
                intent.putExtra(Constant.ACTOR_ID, mActorId);
                intent.putExtra(Constant.USER_HEAD_URL, mActorInfoBean.t_handImg);
                intent.putExtra(Constant.MINE_HEAD_URL, mineUrl);
                intent.putExtra(Constant.MINE_ID, getUserId());
                startActivity(intent);
                break;
            }
            case R.id.gift_iv: {//礼物
                if (mActorInfoBean == null) {
                    return;
                }
                showRewardDialog();
                break;
            }

            case R.id.video_chat_fl: {//与TA视频
                video_chat_fl.setEnabled(false);
                if (mInfoRoomBean != null && mInfoRoomBean.t_is_debut == 1 && mInfoRoomBean.t_room_id > 0
                        && mInfoRoomBean.t_chat_room_id > 0) {//正在大房间直播
                    Intent intent = new Intent(getApplicationContext(), BigHouseActivity.class);
                    intent.putExtra(Constant.FROM_TYPE, Constant.FROM_USER);
                    intent.putExtra(Constant.ACTOR_ID, mActorId);
                    intent.putExtra(Constant.ROOM_ID, mInfoRoomBean.t_room_id);
                    intent.putExtra(Constant.CHAT_ROOM_ID, mInfoRoomBean.t_chat_room_id);
                    startActivity(intent);
                    video_chat_fl.setEnabled(true);
                } else {//正常连通
                    if (mActorInfoBean == null) {
                        video_chat_fl.setEnabled(true);
                        return;
                    }
                    //如果对方是主播,直接用户对主播发起,如果不是就主播对用户发起
                    if (getUserRole() == 0 && Constant.showExtremeCharge() && AppManager.getInstance().getUserInfo() != null && AppManager.getInstance().getUserInfo().t_is_extreme != 0) {
                        ChargeHelper.showInputInviteCodeDialog(ActorInfoOneActivity.this);
                        video_chat_fl.setEnabled(true);
                    } else {
                        getSign(mActorInfoBean.t_role == 1);
                    }
                }
                break;
            }
            case R.id.focus_iv: {//关注
                if (mActorInfoBean == null) {
                    return;
                }
                if (mActorId > 0) {
                    if (!mFocusIv.isSelected()) {//未关注
                        saveFollow(mActorId);
                    } else {//已关注
                        cancelFollow(mActorId);
                    }
                }
                break;
            }
        }
    }

    //----------------------关注  start-------------------------

    /**
     * 添加关注
     */
    private void saveFollow(int actorId) {
        Map<String, String> paramMap = new HashMap<>();
        paramMap.put("userId", getUserId());//关注人
        paramMap.put("coverFollowUserId", String.valueOf(actorId));//	被关注人
        OkHttpUtils.post().url(ChatApi.SAVE_FOLLOW)
                .addParams("param", ParamUtil.getParam(paramMap))
                .build().execute(new AjaxCallback<BaseResponse>() {
            @Override
            public void onResponse(BaseResponse response, int id) {
                if (response != null && response.m_istatus == NetCode.SUCCESS) {
                    String message = response.m_strMessage;
                    if (!TextUtils.isEmpty(message) && message.contains(getResources().getString(R.string.success_str))) {
                        ToastUtil.showToast(getApplicationContext(), message);
                        mFocusIv.setSelected(true);
                        mFansNumber = mFansNumber + 1;
                        if (mFansNumber > 0) {
                            mFansNumberTv.setText(String.valueOf(mFansNumber));
                        }
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
     * 取消关注
     */
    private void cancelFollow(int actorId) {
        Map<String, String> paramMap = new HashMap<>();
        paramMap.put("userId", getUserId());//关注人
        paramMap.put("coverFollow", String.valueOf(actorId));//	被关注人
        OkHttpUtils.post().url(ChatApi.DEL_FOLLOW)
                .addParams("param", ParamUtil.getParam(paramMap))
                .build().execute(new AjaxCallback<BaseResponse>() {
            @Override
            public void onResponse(BaseResponse response, int id) {
                if (response != null && response.m_istatus == NetCode.SUCCESS) {
                    String message = response.m_strMessage;
                    if (!TextUtils.isEmpty(message) && message.contains(getResources().getString(R.string.success_str))) {
                        ToastUtil.showToast(getApplicationContext(), message);
                        mFocusIv.setSelected(false);
                        mFansNumber = mFansNumber - 1;
                        if (mFansNumber >= 0) {
                            mFansNumberTv.setText(String.valueOf(mFansNumber));
                        }
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
    //----------------------关注  end---------------------------

    //----------------------发起视频 start----------------------

    /**
     * 获取签名,并登陆 然后创建房间,并加入
     */
    private void getSign(final boolean isUserCallActor) {
        String userId;
        String actorId;
        if (isUserCallActor) {
            userId = getUserId();
            actorId = String.valueOf(mActorId);
        } else {
            userId = String.valueOf(mActorId);
            actorId = getUserId();
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
                            video_chat_fl.setEnabled(true);
                        } else if (onlineState == -1) {
                            if (isUserCallActor) {//是用户call主播
                                userRequestChat(mRoomId);
                            } else {//主播call用户
                                requestChat(mRoomId);
                            }
                        } else {
                            if (isUserCallActor) {//是用户call主播
                                userRequestChat(mRoomId);
                            } else {//主播call用户
                                requestChat(mRoomId);
                            }
                        }
                    } else {
                        video_chat_fl.setEnabled(true);
                        ToastUtil.showToast(getApplicationContext(), R.string.system_error);
                    }
                } else if (response != null && !TextUtils.isEmpty(response.m_strMessage)) {
                    video_chat_fl.setEnabled(true);
                    ToastUtil.showToast(getApplicationContext(), response.m_strMessage);
                }
            }

            @Override
            public void onBefore(Request request, int id) {
                super.onBefore(request, id);
            }

            @Override
            public void onError(Call call, Exception e, int id) {
                super.onError(call, e, id);
                video_chat_fl.setEnabled(true);
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
        paramMap.put("userId", getUserId());
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
        paramMap.put("anchorUserId", getUserId());
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
                        intent.putExtra(Constant.ACTOR_ID, mActorId);//用户ID
                        intent.putExtra(Constant.NICK_NAME, mActorInfoBean.t_nickName);
                        intent.putExtra(Constant.USER_HEAD_URL, mActorInfoBean.t_handImg);
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
                        ChargeHelper.showSetCoverDialog(ActorInfoOneActivity.this);
                    }else if (response.m_istatus == -8) {
                        ToastUtil.showToast(getApplicationContext(), response.m_strMessage);
                    }  else {
                        ToastUtil.showToast(getApplicationContext(), R.string.system_error);
                    }
                }
                mHandler.postDelayed(new Runnable(){
                    @Override
                    public void run() {
                        video_chat_fl.setEnabled(true);
                    }
                },500);
            }

            @Override
            public void onError(Call call, Exception e, int id) {
                super.onError(call, e, id);
                ToastUtil.showToast(getApplicationContext(), R.string.system_error);
                video_chat_fl.setEnabled(true);
            }

        });
    }

    /**
     * 用户对主播发起聊天
     */
    private void userRequestChat(final int roomId) {
        Map<String, String> paramMap = new HashMap<>();
        paramMap.put("userId", getUserId());
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
                        intent.putExtra(Constant.ACTOR_ID, mActorId);
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
                        ChargeHelper.showSetCoverDialog(ActorInfoOneActivity.this);
                    } else if (response.m_istatus == -8) {
                        ToastUtil.showToast(getApplicationContext(), response.m_strMessage);
                    } else {
                        ToastUtil.showToast(getApplicationContext(), R.string.system_error);
                    }
                }
                mHandler.postDelayed(new Runnable(){
                    @Override
                    public void run() {
                        video_chat_fl.setEnabled(true);
                    }
                },500);

            }

            @Override
            public void onError(Call call, Exception e, int id) {
                super.onError(call, e, id);
                video_chat_fl.setEnabled(true);
                ToastUtil.showToast(getApplicationContext(), R.string.system_error);
            }

        });
    }

    //----------------------发起视频 end----------------------


    //-------------------------Banner------------------
    private void setBanner(List<CoverUrlBean> coverUrlBeans) {
        mMZBannerView.setPages(coverUrlBeans, new MZHolderCreator<BannerViewHolder>() {
            @Override
            public BannerViewHolder createViewHolder() {
                return new BannerViewHolder();
            }
        });
        if (coverUrlBeans.size() > 1) {
            mMZBannerView.setCanLoop(true);
            mMZBannerView.start();
        } else {
            mMZBannerView.setCanLoop(false);
        }
    }

    class BannerViewHolder implements MZViewHolder<CoverUrlBean> {

        private ImageView mImageView;

        @Override
        public View createView(Context context) {
            @SuppressLint("InflateParams")
            View view = LayoutInflater.from(context).inflate(R.layout.item_info_image_vp_layout, null);
            mImageView = view.findViewById(R.id.content_iv);
            return view;
        }

        @Override
        public void onBind(Context context, int i, CoverUrlBean bannerBean) {
            if (bannerBean != null) {
                String coverImg = bannerBean.t_img_url;
                if (!TextUtils.isEmpty(coverImg)) {
                    //计算 图片resize的大小
                    int overWidth = DevicesUtil.getScreenW(getApplicationContext());
                    int overHeight = DevicesUtil.dp2px(getApplicationContext(), 360);
                    if (overWidth > 800) {
                        overWidth = (int) (overWidth * 0.85);
                        overHeight = (int) (overHeight * 0.85);
                    }
                    ImageLoadHelper.glideShowImageWithUrl(getApplicationContext(), coverImg, mImageView, overWidth, overHeight);
                }
            }
        }
    }

    //-----------------------Banner end--------------------


    /**
     * 获取分享信息
     */
    private ShareInformitionBean mShareInformitionBean;

    private void geShareInformaiton() {
        Map<String, String> paramMap = new HashMap<>();
        paramMap.put("userId", getUserId());
        if (Constant.addType()) {
            paramMap.put("type", "1");
        }
        OkHttpUtils.post().url(ChatApi.SHARE_INFORMAOTION)
                .addParams("param", ParamUtil.getParam(paramMap))
                .build().execute(new AjaxCallback<BaseResponse<ShareInformitionBean>>() {
            @Override
            public void onResponse(BaseResponse<ShareInformitionBean> response, int id) {

                if (response != null && response.m_istatus == NetCode.SUCCESS) {
                    mShareInformitionBean = response.m_object;

                }
            }
        });
    }

    /**
     * 分享
     */
    private void share(String platType) {
        //图片地址
        String imgUrl = "";
        List<CoverUrlBean> coverUrlBeanList = mActorInfoBean.lunbotu;
        if (coverUrlBeanList != null && coverUrlBeanList.size() > 0) {
            imgUrl = coverUrlBeanList.get(0).t_img_url;
        }

        MobShareUtil mobShareUtil = new MobShareUtil();
        ShareData data = new ShareData();
        String title = mShareInformitionBean.t_share_title;
        data.setTitle(title);
        String des = mShareInformitionBean.t_share_details;
        data.setDes(des);
        data.setImgUrl(mShareInformitionBean.t_share_logo);
        String webUrl = ChatApi.SHARE_URL + getUserId();
        data.setWebUrl(mShareInformitionBean.t_share_url);
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

    //------------------------------分享end--------------------


    //----------------------------礼物 start-------------------

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
        final GiftViewPagerRecyclerAdapter giftAdapter = new GiftViewPagerRecyclerAdapter(ActorInfoOneActivity.this);
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
        final GoldGridRecyclerAdapter goldGridRecyclerAdapter = new GoldGridRecyclerAdapter(ActorInfoOneActivity.this);
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
                        ToastUtil.showToast(getApplicationContext(), R.string.reward_success);

                        //发送自定义消息
                        CustomMessageBean bean = new CustomMessageBean();
                        bean.type = "1";
                        bean.gift_id = giftBean.t_gift_id;
                        bean.gift_name = giftBean.t_gift_name;
                        bean.gift_gif_url = giftBean.t_gift_still_url;
                        bean.gold_number = giftBean.t_gift_gold;
                        bean.t_gift_gif_url=giftBean.t_gift_gif_url;
                        bean.t_num = giftBean.t_num;

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
     * 打赏金币(红包)
     */
    private void reWardGold(int gold) {
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
                        ToastUtil.showToast(getApplicationContext(), R.string.reward_success);
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

    //----------------------------礼物 end---------------------


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
     * 获取角色
     */
    public int getUserRole() {
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

    /**
     * 获取Vip
     */
    public int getUserVip() {
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

    protected void onResume() {
        super.onResume();
        try {
            //广播接收 自己socket消息
            IntentFilter filter = new IntentFilter(ConnectManager.BROADCAST_ACTION);
            if (mMyBroadcastReceiver == null) {
                mMyBroadcastReceiver = new MyBroadcastReceiver();
            }
            registerReceiver(mMyBroadcastReceiver, filter);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mUnbinder != null) {
            mUnbinder.unbind();
        }
    }

    /**
     * 根据百分比改变颜色透明度
     */
    public int changeAlpha(int color, float fraction) {
        int red = Color.red(color);
        int green = Color.green(color);
        int blue = Color.blue(color);
        int alpha = (int) (Color.alpha(color) * fraction);
        return Color.argb(alpha, red, green, blue);
    }

    /*
     * 分享和拉黑
     * */
    private PopupWindow mPoPwindow;
    private View view;
    private TextView Window_PullBlack;

    public void getPoPwindow() {

        mPoPwindow = new PopupWindow(view, ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT, false);

        mPoPwindow.setOutsideTouchable(true);//点击外部收起
        mPoPwindow.setFocusable(true);//设置焦点生效
        // popwindow监听
        mPoPwindow.setOnDismissListener(new PopupWindow.OnDismissListener() {
            @Override
            public void onDismiss() {

            }
        });
        view.setOnTouchListener(new View.OnTouchListener() {

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                // TODO Auto-generated method stub
                if (mPoPwindow != null && mPoPwindow.isShowing()) {
                    mPoPwindow.dismiss();
                }
                return false;
            }
        });

        //分享
        TextView Window_Share = view.findViewById(R.id.Window_Share);
        Window_Share.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DialogUtil.showShareDialog(ActorInfoOneActivity.this, new OnItemClickListener<ShareLayoutBean>() {
                    @Override
                    public void onItemClick(ShareLayoutBean bean, int position) {
                        switch (bean.id) {
                            case 1:
                                if (mActorInfoBean != null) {
                                    share(MobConst.Type.WX);
                                } else {
                                    ToastUtil.showToast(getApplicationContext(), R.string.system_error);
                                }
                                break;
                            case 2:
                                if (mActorInfoBean != null) {
                                    share(MobConst.Type.WX_PYQ);
                                } else {
                                    ToastUtil.showToast(getApplicationContext(), R.string.system_error);
                                }
                                break;
                            case 3:
                                share(MobConst.Type.QQ);
                                break;
                            case 4:
                                share(MobConst.Type.QZONE);
                                break;
                        }
                    }
                });

                if (mPoPwindow != null && mPoPwindow.isShowing()) {
                    mPoPwindow.dismiss();
                }
            }
        });

        //拉黑

        Window_PullBlack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mIsBlack.equals("1")) {
                    getCanlePullBack();
                } else {
                    getPullBack();
                }

            }
        });
    }

    //拉黑
    public void getPullBack() {
        Map<String, String> pullBack = new HashMap<>();
        pullBack.put("userId", getUserId());//查看人
        pullBack.put("fkId", String.valueOf(mActorId));

        OkHttpUtils.post().url(ChatApi.ADD_PULL_BACK)
                .addParams("param", ParamUtil.getParam(pullBack))
                .build().execute(new AjaxCallback<BaseListResponse<GiftBean>>() {
            @Override
            public void onResponse(BaseListResponse<GiftBean> response, int id) {
                if (response != null && response.m_istatus == NetCode.SUCCESS) {
                    mIsBlack = "1";
                    Window_PullBlack.setText("已拉黑");
                }
            }
        });
    }
    //取消拉黑

    public void getCanlePullBack() {
        Map<String, String> pullBack = new HashMap<>();
        pullBack.put("userId", getUserId());//查看人
        pullBack.put("fkId", String.valueOf(mActorId));

        OkHttpUtils.post().url(ChatApi.DELETE_PULL_BACK)
                .addParams("param", ParamUtil.getParam(pullBack))
                .build().execute(new AjaxCallback<BaseListResponse<GiftBean>>() {
            @Override
            public void onResponse(BaseListResponse<GiftBean> response, int id) {
                if (response != null && response.m_istatus == NetCode.SUCCESS) {
                    mIsBlack = "0";
                    Window_PullBlack.setText("拉黑");
                }
            }
        });
    }

}
