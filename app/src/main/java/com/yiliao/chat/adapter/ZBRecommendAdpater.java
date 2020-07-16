package com.yiliao.chat.adapter;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.support.annotation.NonNull;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.animation.AccelerateInterpolator;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.yiliao.chat.R;
import com.yiliao.chat.activity.ActorInfoOneActivity;
import com.yiliao.chat.activity.ActorVideoPlayActivity;
import com.yiliao.chat.base.BaseActivity;
import com.yiliao.chat.bean.BannerBean;
import com.yiliao.chat.bean.BigRoomListBean;
import com.yiliao.chat.bean.GirlListBean;
import com.yiliao.chat.constant.Constant;
import com.yiliao.chat.helper.ImageLoadHelper;
import com.yiliao.chat.util.DevicesUtil;
import com.yiliao.chat.util.FixedSpeedScroller;
import com.yiliao.chat.util.TimingUtil;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

public class ZBRecommendAdpater extends RecyclerView.Adapter<RecyclerView.ViewHolder>{

    private BaseActivity mContext;
    private List<GirlListBean> mBeans = new ArrayList<>();
    private List<GirlListBean> mRecommendBeans = new ArrayList<>();
    private List<BigRoomListBean> mBigRoomBeans = new ArrayList<>();
    private List<BannerBean> mBannerBeans = new ArrayList<>();
    private final int TOP = 0;
    private TopHolder mTopHolder;

    private int width;

    public ZBRecommendAdpater(BaseActivity context) {
        mContext = context;
        //通过WindowManager获取
        WindowManager wm = (WindowManager) mContext.getSystemService(Context.WINDOW_SERVICE);
        width = wm.getDefaultDisplay().getWidth();
    }

    public void loadData(List<GirlListBean> beans) {
        mBeans = beans;
        notifyDataSetChanged();
    }

    public void loadBigRoomData(List<BigRoomListBean> bigRoomListBeans) {
        mBigRoomBeans = bigRoomListBeans;
        notifyDataSetChanged();
    }

    public void loadBannerData(List<BannerBean> bannerBeans) {
        mBannerBeans = bannerBeans;
        notifyDataSetChanged();
    }

    public void loadRecommendData(List<GirlListBean> beans) {
        mRecommendBeans = beans;
        notifyDataSetChanged();
    }

    @Override
    public int getItemViewType(int position) {
        if (position == 0) {
            return TOP;
        } else {
            return 2;//主播
        }
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView;
        if (viewType == TOP) {
            itemView = LayoutInflater.from(mContext).inflate(R.layout.item_zbcommend_top,
                    parent, false);
            mTopHolder = new TopHolder(itemView);
            return mTopHolder;
        } else {
            itemView = LayoutInflater.from(mContext).inflate(R.layout.item_zbrecommend,
                    parent, false);
            return new MyViewHolder(itemView);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        final GirlListBean bean = mBeans.get(position);
        //主播
        if (holder instanceof MyViewHolder) {
            MyViewHolder mHolder = (MyViewHolder) holder;
            //绑定数据
            if (bean != null) {
                //昵称
                mHolder.mNameTv.setText(bean.t_nickName);
                //处理状态 主播状态(0.空闲1.忙碌2.离线)
                int state = bean.t_state;
                if (state == 0) {//在线
                    Drawable drawable = mContext.getResources().getDrawable(R.drawable.shape_free_indicator);
                    drawable.setBounds(0, 0, drawable.getMinimumWidth(), drawable.getMinimumHeight());
                    mHolder.mStatusTv.setVisibility(View.VISIBLE);
                    mHolder.mStatusTv.setCompoundDrawables(drawable, null, null, null);
                    mHolder.mStatusTv.setText(mContext.getString(R.string.free));
                    mHolder.mStatusTv.setTextColor(mContext.getResources().getColor(R.color.white));
                } else if (state == 1) {//忙碌
                    Drawable drawable = mContext.getResources().getDrawable(R.drawable.shape_busy_indicator);
                    drawable.setBounds(0, 0, drawable.getMinimumWidth(), drawable.getMinimumHeight());
                    mHolder.mStatusTv.setVisibility(View.VISIBLE);
                    mHolder.mStatusTv.setCompoundDrawables(drawable, null, null, null);
                    mHolder.mStatusTv.setText(mContext.getString(R.string.busy));
                    mHolder.mStatusTv.setTextColor(mContext.getResources().getColor(R.color.white));
                } else if (state == 2) {
                    Drawable drawable = mContext.getResources().getDrawable(R.drawable.shape_offline_indicator);
                    drawable.setBounds(0, 0, drawable.getMinimumWidth(), drawable.getMinimumHeight());
                    mHolder.mStatusTv.setVisibility(View.VISIBLE);
                    mHolder.mStatusTv.setCompoundDrawables(drawable, null, null, null);
                    mHolder.mStatusTv.setText(mContext.getString(R.string.offline));
                    mHolder.mStatusTv.setTextColor(mContext.getResources().getColor(R.color.gray_bcbcbc));
                }
                //等级图标
                if (Constant.showGradeOnMine()) {
                    if (!TextUtils.isEmpty(bean.t_level)) {
                        mHolder.ivLevel.setVisibility(View.VISIBLE);
                        ImageLoadHelper.glideShowImageWithUrl(mContext, bean.t_level, mHolder.ivLevel);
                    } else {
                        mHolder.ivLevel.setVisibility(View.GONE);
                    }
                }
                //性别
                if (Constant.showSexOnMain()) {
                    mHolder.ivSex.setVisibility(View.VISIBLE);
                    if (bean.t_sex == 0) {//女
                        mHolder.ivSex.setBackgroundResource(R.drawable.bg_women);
                        mHolder.ivSex.setImageResource(R.mipmap.female_white_new);
                    } else {//男
                        mHolder.ivSex.setBackgroundResource(R.drawable.bg_man);
                        mHolder.ivSex.setImageResource(R.mipmap.male_white_new);
                    }
                }
                if (!Constant.hideAgeOnMine()) {
                    //年龄
                    if (bean.t_age > 0) {
                        mHolder.mAgeTv.setVisibility(View.VISIBLE);
                        mHolder.mAgeTv.setText(String.valueOf(bean.t_age));
                    } else {
                        mHolder.mAgeTv.setVisibility(View.GONE);
                    }
                }
                //签名
                String sign = bean.t_autograph;
                if (!TextUtils.isEmpty(sign)) {
                    mHolder.mSignTv.setText(sign);
                } else {
                    mHolder.mSignTv.setText(mContext.getResources().getString(R.string.lazy));
                }
                //职业
                if (!TextUtils.isEmpty(bean.t_vocation)) {
                    mHolder.mJobTv.setVisibility(View.VISIBLE);
                    mHolder.mJobTv.setText(bean.t_vocation);
                } else {
                    mHolder.mJobTv.setVisibility(View.GONE);
                }
                //显示封面图
                FrameLayout.LayoutParams linearParams = (FrameLayout.LayoutParams) mHolder.mHeadIv.getLayoutParams();
                linearParams.height = width;
                linearParams.width=width;
                mHolder.mHeadIv.setLayoutParams(linearParams);
                String coverImg = bean.t_cover_img;
                if (!TextUtils.isEmpty(coverImg)) {
                    ImageLoadHelper.glideShowImageWithUrl(mContext, coverImg, mHolder.mHeadIv);
                } else {
                    mHolder.mHeadIv.setImageResource(0);
                }
                //金币
                int gold = bean.t_video_gold;
                if (gold > 0) {
                    String content = gold + mContext.getResources().getString(R.string.price);
                    mHolder.mPriceTv.setText(content);
                    mHolder.mPriceTv.setVisibility(View.VISIBLE);
                } else {
                    mHolder.mPriceTv.setVisibility(View.GONE);
                }

                //点击跳转
                mHolder.mContentLl.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (Constant.goToActorInfoFromHome()) {
                            Intent intent = new Intent(mContext, ActorInfoOneActivity.class);
                            intent.putExtra(Constant.ACTOR_ID, bean.t_id);
                            mContext.startActivity(intent);
                        } else {
                            //该主播是否存在免费视频0.不存在1.存在
                            int freeVideo = bean.t_is_public;
                            if (freeVideo == 0) {
                                Intent intent = new Intent(mContext, ActorInfoOneActivity.class);
                                intent.putExtra(Constant.ACTOR_ID, bean.t_id);
                                mContext.startActivity(intent);
                            } else {
                                Intent intent = new Intent(mContext, ActorVideoPlayActivity.class);
                                intent.putExtra(Constant.FROM_WHERE, Constant.FROM_GIRL);
                                intent.putExtra(Constant.ACTOR_ID, bean.t_id);
                                intent.putExtra(Constant.FILE_ID, bean.albumId);
                                mContext.startActivity(intent);
                            }
                        }
                    }
                });
                mHolder.mInfoLl.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(mContext, ActorInfoOneActivity.class);
                        intent.putExtra(Constant.ACTOR_ID, bean.t_id);
                        mContext.startActivity(intent);
                    }
                });
            }
        } else if (holder instanceof TopHolder) {
            TopHolder bannerHolder = (TopHolder) holder;
            if (mBigRoomBeans != null) {
                bannerHolder.loadBigRoomData(mBigRoomBeans);
            }
            if (mRecommendBeans != null) {
                bannerHolder.loadRecommendData(mRecommendBeans);
            }
            if (mBannerBeans != null && mBannerBeans.size() > 0) {
                bannerHolder.loadBannerData(mBannerBeans);
            }
        }
    }

    @Override
    public int getItemCount() {
        return mBeans != null ? mBeans.size() : 0;
    }


    class MyViewHolder extends RecyclerView.ViewHolder {

        ImageView mHeadIv;
        TextView mNameTv;
        TextView mPriceTv;
        TextView mStatusTv;
        TextView mSignTv;
        TextView mAgeTv;
        TextView mJobTv;
        View mContentLl;
        View mInfoLl;
        ImageView ivLevel;
        ImageView ivSex;

        MyViewHolder(View itemView) {
            super(itemView);
            mHeadIv = itemView.findViewById(R.id.head_iv);
            mNameTv = itemView.findViewById(R.id.name_tv);
            mPriceTv = itemView.findViewById(R.id.price_tv);
            mStatusTv = itemView.findViewById(R.id.status_tv);
            mContentLl = itemView.findViewById(R.id.content_ll);
            mInfoLl = itemView.findViewById(R.id.info_ll);
            mSignTv = itemView.findViewById(R.id.sign_tv);
            mAgeTv = itemView.findViewById(R.id.age_tv);
            mJobTv = itemView.findViewById(R.id.job_tv);
            ivLevel = itemView.findViewById(R.id.ivLevel);
            ivSex = itemView.findViewById(R.id.ivSex);
        }
    }

    class TopHolder extends RecyclerView.ViewHolder {

        BigHouseListRecyclerAdapter bigHouseListRecyclerAdapter;
        GirlTopRecyclerAdapter mGirlTopRecyclerAdapter;
        GirlTopBannerRecyclerAdapter mBannerRecyclerAdapter;
        BannerViewPagerAdpater mBannerViewPagerAdpater;
        RecyclerView mLiveRv;
        RecyclerView mRecommendRv;
        RecyclerView mBannerRv;
        ViewPager mBanner_vp;

        TopHolder(View itemView) {
            super(itemView);
            init(itemView);
        }

        /**
         * 初始化
         */
        private void init(View itemView) {
            mLiveRv = itemView.findViewById(R.id.live_rv);
            mRecommendRv = itemView.findViewById(R.id.recommend_rv);
            mBannerRv = itemView.findViewById(R.id.banner_rv);
            mBanner_vp=itemView.findViewById(R.id.banner_vp);
            mRecommendRv.setNestedScrollingEnabled(false);
            //大房间
            GridLayoutManager manager = new GridLayoutManager(mContext, 2);
            bigHouseListRecyclerAdapter = new BigHouseListRecyclerAdapter(mContext);
            mLiveRv.setLayoutManager(manager);
            mLiveRv.setAdapter(bigHouseListRecyclerAdapter);
            //推荐
            GridLayoutManager recommendManager = new GridLayoutManager(mContext, 2);
            mRecommendRv.setLayoutManager(recommendManager);
            mGirlTopRecyclerAdapter = new GirlTopRecyclerAdapter(mContext);
            mRecommendRv.setAdapter(mGirlTopRecyclerAdapter);

            //banner
            LinearLayoutManager linearLayoutManager = new LinearLayoutManager(mContext,
                    LinearLayoutManager.HORIZONTAL, false);
            mBannerRv.setLayoutManager(linearLayoutManager);
            mBannerRecyclerAdapter = new GirlTopBannerRecyclerAdapter(mContext);
            mBannerRv.setAdapter(mBannerRecyclerAdapter);


            //viepager滚动图片
            int overWidth = DevicesUtil.getScreenW(mContext)-20;
            int overHeight = DevicesUtil.getScreenW(mContext)*180/377;
            //用代码图片在视频的屏幕尺寸
            LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) mBanner_vp.getLayoutParams();
            params.width=overWidth;
            params.height=overHeight;
            mBanner_vp.setLayoutParams(params);


            //控制viewpager翻页速率
            try {
                Field mField =ViewPager.class.getDeclaredField("mScroller");
                mField.setAccessible(true);
                FixedSpeedScroller mScroller = new FixedSpeedScroller(mContext, new AccelerateInterpolator());
                mScroller.setmDuration(800);	//在这里设置时间单位毫秒
                mField.set(mBanner_vp, mScroller); //viewPager和FixedSpeedScrolle
            }catch(Exception e){

            }
            mBannerViewPagerAdpater = new BannerViewPagerAdpater(mContext,mBanner_vp);
            mBanner_vp.setAdapter(mBannerViewPagerAdpater);




        }

        void loadBigRoomData(List<BigRoomListBean> beans) {
            bigHouseListRecyclerAdapter.loadData(beans);
        }

        void loadRecommendData(List<GirlListBean> beans) {
            mGirlTopRecyclerAdapter.loadData(beans);
        }

        void loadBannerData(List<BannerBean> beans) {
            mBannerRecyclerAdapter.loadBannerData(beans);

            mBannerViewPagerAdpater.loadBannerData(beans);

            new TimingUtil(mBanner_vp,mBannerBeans.size()).Run();

        }

        void resumeChange() {
            if (bigHouseListRecyclerAdapter != null) {
                bigHouseListRecyclerAdapter.resumeChange();
            }
        }

        void pauseChange() {
            if (bigHouseListRecyclerAdapter != null) {
                bigHouseListRecyclerAdapter.pauseChange();
            }
        }


    }

    public void resumeChange() {
        if (mTopHolder != null) {
            mTopHolder.resumeChange();
        }
    }

    public void pauseChange() {
        if (mTopHolder != null) {
            mTopHolder.pauseChange();
        }
    }





}
