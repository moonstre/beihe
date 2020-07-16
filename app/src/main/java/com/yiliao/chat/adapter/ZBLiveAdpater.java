package com.yiliao.chat.adapter;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.TransitionDrawable;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.yiliao.chat.R;
import com.yiliao.chat.activity.BigHouseActivity;
import com.yiliao.chat.activity.QuickVideoChatActivity;
import com.yiliao.chat.activity.UserViewQuickActivity;
import com.yiliao.chat.base.BaseActivity;
import com.yiliao.chat.base.BaseResponse;
import com.yiliao.chat.bean.BigRoomListBean;
import com.yiliao.chat.constant.ChatApi;
import com.yiliao.chat.constant.Constant;
import com.yiliao.chat.helper.ImageLoadHelper;
import com.yiliao.chat.net.AjaxCallback;
import com.yiliao.chat.net.NetCode;
import com.yiliao.chat.util.ParamUtil;
import com.zhy.http.okhttp.OkHttpUtils;

import java.lang.ref.WeakReference;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

public class ZBLiveAdpater extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private BaseActivity mContext;
    private List<BigRoomListBean> mRecommendBeans = new ArrayList<>();
    private final int CHANGE_NEXT = 0x13;
    private ChangeHolder mChangeHolder;
    private MyHandler mChangeHandler = new MyHandler(ZBLiveAdpater.this);
    private int width;

    public ZBLiveAdpater(BaseActivity context) {
        mContext = context;
        //通过WindowManager获取
        WindowManager wm = (WindowManager) mContext.getSystemService(Context.WINDOW_SERVICE);
        width = wm.getDefaultDisplay().getWidth();

    }

    public void loadData(List<BigRoomListBean> beans) {
        mRecommendBeans = beans;
        notifyDataSetChanged();
    }



    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView;

            itemView = LayoutInflater.from(mContext).inflate(R.layout.item_big_house_recycler_layout,
                    parent, false);
            return new MyViewHolder(itemView);

    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        final BigRoomListBean bean = mRecommendBeans.get(position);

            MyViewHolder mHolder = (MyViewHolder) holder;
            //绑定数据
            if (bean != null) {
                //昵称
                mHolder.mNickTv.setText(bean.t_nickName);
                //显示封面图
                String coverImg = bean.t_cover_img;

                //FrameLayout
                FrameLayout.LayoutParams linearParams = (FrameLayout.LayoutParams) mHolder.mContentIv.getLayoutParams();
                linearParams.height = width;
                linearParams.width=width;
                mHolder.mContentIv.setLayoutParams(linearParams);
                if (!TextUtils.isEmpty(coverImg)) {
                    ImageLoadHelper.glideShowCornerImageWithUrl(mContext, coverImg, mHolder.mContentIv);
                } else {
                    mHolder.mContentIv.setImageResource(0);
                }
                //人数
                int number = bean.viewerCount;
                if (number > 0) {
                    String content;
                    if (number < 10000) {
                        content = number + mContext.getString(R.string.number_man);
                    } else {
                        BigDecimal old = new BigDecimal(number);
                        BigDecimal ten = new BigDecimal(10000);
                        BigDecimal res = old.divide(ten, 1, RoundingMode.UP);
                        content = res + mContext.getString(R.string.number_ten_thousand);
                    }
                    mHolder.mNumberTv.setText(content);
                }
                //是否开播 0.未开播
                if (bean.t_is_debut == 0) {
                    mHolder.mStatusTv.setVisibility(View.GONE);
                } else {
                    mHolder.mStatusTv.setVisibility(View.VISIBLE);
                }
                //点击
                mHolder.mContentFl.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (bean.t_user_id > 0 && bean.t_room_id > 0) {
                            int selfId = Integer.parseInt(mContext.getUserId());
                            if (selfId == bean.t_user_id) {//如果是自己看自己,就直接开播
                                Intent intent = new Intent(mContext, BigHouseActivity.class);
                                intent.putExtra(Constant.FROM_TYPE, Constant.FROM_ACTOR);
                                intent.putExtra(Constant.ACTOR_ID, Integer.parseInt(mContext.getUserId()));
                                mContext.startActivity(intent);
                            } else {
                                Intent intent = new Intent(mContext, BigHouseActivity.class);
                                intent.putExtra(Constant.FROM_TYPE, Constant.FROM_USER);
                                intent.putExtra(Constant.ACTOR_ID, bean.t_user_id);
                                intent.putExtra(Constant.ROOM_ID, bean.t_room_id);
                                intent.putExtra(Constant.CHAT_ROOM_ID, bean.t_chat_room_id);
                                mContext.startActivity(intent);
                            }
                        }
                    }
                });
            }

    }

    @Override
    public int getItemCount() {
        return mRecommendBeans != null ? mRecommendBeans.size() : 0;
    }

    class MyViewHolder extends RecyclerView.ViewHolder {

        ImageView mContentIv;
        TextView mNickTv;
        TextView mNumberTv;
        FrameLayout mContentFl;
        TextView mStatusTv;

        MyViewHolder(View itemView) {
            super(itemView);
            mContentIv = itemView.findViewById(R.id.content_iv);
            mNickTv = itemView.findViewById(R.id.nick_tv);
            mNumberTv = itemView.findViewById(R.id.number_tv);
            mContentFl = itemView.findViewById(R.id.content_fl);
            mStatusTv = itemView.findViewById(R.id.status_tv);
        }
    }

    class ChangeHolder extends RecyclerView.ViewHolder {

        ImageView mContentIv;
        FrameLayout mContentFl;

        ChangeHolder(View itemView) {
            super(itemView);
            mContentIv = itemView.findViewById(R.id.content_iv);
            mContentFl = itemView.findViewById(R.id.content_fl);
        }
    }

    /**
     * 更换图片
     */
    //头一张
    private int mLastImageIndex;

    private void changeImage() {
        if (mChangeHolder != null && mChangeHandler != null) {
            int[] res = {R.drawable.change_one_big, R.drawable.change_two_big, R.drawable.change_three_big,
                    R.drawable.change_four_big, R.drawable.change_five_big, R.drawable.change_six_big, R.drawable.change_seven_big,
                    R.drawable.change_eight_big, R.drawable.change_nine_big};
            Random random = new Random();
            int next = random.nextInt(res.length);
            //淡入淡出动画需要先设置一个Drawable数组，用于变换图片
            Drawable[] drawableArray = {mContext.getResources().getDrawable(res[mLastImageIndex]),
                    mContext.getResources().getDrawable(res[next])
            };
            TransitionDrawable transitionDrawable = new TransitionDrawable(drawableArray);
            mChangeHolder.mContentIv.setImageDrawable(transitionDrawable);
            transitionDrawable.startTransition(230);
            mLastImageIndex = next;
            mChangeHandler.removeCallbacksAndMessages(null);
            mChangeHandler.sendEmptyMessageDelayed(CHANGE_NEXT, 3000);
        }
    }

    private static class MyHandler extends Handler {
        private WeakReference<ZBLiveAdpater> mSettingActivityWeakReference;

        MyHandler(ZBLiveAdpater settingActivity) {
            mSettingActivityWeakReference = new WeakReference<>(settingActivity);
        }

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            ZBLiveAdpater settingActivity = mSettingActivityWeakReference.get();
            if (settingActivity != null && msg.what == settingActivity.CHANGE_NEXT) {
                settingActivity.changeImage();
            }
        }
    }






}
