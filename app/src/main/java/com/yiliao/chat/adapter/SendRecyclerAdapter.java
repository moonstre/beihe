package com.yiliao.chat.adapter;

import android.app.Activity;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.yiliao.chat.R;
import com.yiliao.chat.bean.SendBean;
import com.yiliao.chat.helper.ImageLoadHelper;

import java.util.ArrayList;
import java.util.List;

/*
 * Copyright (C) 2018
 * 版权所有
 *
 * 功能描述：大房间贡献值RecyclerView的Adapter
 * 作者：
 * 创建时间：2018/8/21
 *
 * 修改人：
 * 修改描述：
 * 修改日期
 */
public class SendRecyclerAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private Activity mContext;
    private List<SendBean> mBeans = new ArrayList<>();

    public SendRecyclerAdapter(Activity context) {
        mContext = context;
    }

    public void loadData(List<SendBean> beans) {
        mBeans = beans;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(mContext).inflate(R.layout.item_send_recycler_layout,
                parent, false);
        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        final SendBean bean = mBeans.get(position);
        MyViewHolder mHolder = (MyViewHolder) holder;
        if (bean != null) {
            //排名
            mHolder.mPositionTv.setText(String.valueOf(position + 1));
            if (position == 0 || position == 1 || position == 2) {
                mHolder.mPositionTv.setTextColor(mContext.getResources().getColor(R.color.red_fe2947));
                mHolder.mTotalTv.setTextColor(mContext.getResources().getColor(R.color.red_fe2947));
            } else {
                mHolder.mPositionTv.setTextColor(mContext.getResources().getColor(R.color.black_333333));
                mHolder.mTotalTv.setTextColor(mContext.getResources().getColor(R.color.black_333333));
            }
            //昵称
            mHolder.mNickTv.setText(bean.t_nickName);
            //头像
            String headImg = bean.t_handImg;
            if (!TextUtils.isEmpty(headImg)) {
                ImageLoadHelper.glideShowCircleImageWithUrl(mContext, headImg, mHolder.mHeadIv);
            } else {
                mHolder.mHeadIv.setImageResource(R.drawable.default_head_img);
            }
            //是否VIP 0.是 1.否
            if (bean.t_is_vip == 0) {//是VIP
                mHolder.mVipIv.setVisibility(View.VISIBLE);
            } else {
                mHolder.mVipIv.setVisibility(View.GONE);
            }
            //金币档
            int goldLevel = bean.goldfiles;
            if (goldLevel == 1) {
                mHolder.mLevelTv.setText(mContext.getString(R.string.level_one));
            } else if (goldLevel == 2) {
                mHolder.mLevelTv.setText(mContext.getString(R.string.level_two));
            } else if (goldLevel == 3) {
                mHolder.mLevelTv.setText(mContext.getString(R.string.level_three));
            } else if (goldLevel == 4) {
                mHolder.mLevelTv.setText(mContext.getString(R.string.level_four));
            } else if (goldLevel == 5) {
                mHolder.mLevelTv.setText(mContext.getString(R.string.level_five));
            }
            //充值档 1.第一档 星星 2.第二档 月亮 3.第三档 太阳
            int grade = bean.grade;
            if (grade == 1) {
                mHolder.mStarIv.setBackgroundResource(R.drawable.grade_star);
            } else if (grade == 2) {
                mHolder.mStarIv.setBackgroundResource(R.drawable.grade_moon);
            } else if (grade == 3) {
                mHolder.mStarIv.setBackgroundResource(R.drawable.grade_sun);
            }
            //贡献值
            String content = bean.total + mContext.getString(R.string.total_send);
            mHolder.mTotalTv.setText(content);
            //点击事件
            mHolder.mContentRl.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (mOnItemClickListener != null) {
                        mOnItemClickListener.onItemClick(bean);
                    }
                }
            });
        }
    }

    @Override
    public int getItemCount() {
        return mBeans != null ? mBeans.size() : 0;
    }

    class MyViewHolder extends RecyclerView.ViewHolder {

        TextView mPositionTv;
        ImageView mHeadIv;
        TextView mNickTv;
        TextView mLevelTv;
        ImageView mVipIv;
        ImageView mStarIv;
        TextView mTotalTv;
        RelativeLayout mContentRl;

        MyViewHolder(View itemView) {
            super(itemView);
            mPositionTv = itemView.findViewById(R.id.position_tv);
            mHeadIv = itemView.findViewById(R.id.head_iv);
            mNickTv = itemView.findViewById(R.id.nick_tv);
            mLevelTv = itemView.findViewById(R.id.level_tv);
            mVipIv = itemView.findViewById(R.id.vip_iv);
            mStarIv = itemView.findViewById(R.id.star_iv);
            mTotalTv = itemView.findViewById(R.id.total_tv);
            mContentRl = itemView.findViewById(R.id.content_rl);
        }
    }

    public interface OnItemClickListener {
        void onItemClick(SendBean bean);
    }

    private OnItemClickListener mOnItemClickListener;

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        mOnItemClickListener = onItemClickListener;
    }

}
