package com.yiliao.chat.adapter;

import android.app.Activity;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.yiliao.chat.R;
import com.yiliao.chat.activity.ActorInfoOneActivity;
import com.yiliao.chat.bean.ManBean;
import com.yiliao.chat.constant.Constant;
import com.yiliao.chat.helper.ImageLoadHelper;
import com.yiliao.chat.util.DevicesUtil;

import java.util.ArrayList;
import java.util.List;

/*
 * Copyright (C) 2018
 * 版权所有
 *
 * 功能描述：邀请赚钱RecyclerView的Adapter
 * 作者：
 * 创建时间：2018/10/19
 *
 * 修改人：
 * 修改描述：
 * 修改日期
 */
public class InviteManRecyclerAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private Activity mContext;
    private List<ManBean> mBeans = new ArrayList<>();

    public InviteManRecyclerAdapter(Activity context) {
        mContext = context;
    }

    public void loadData(List<ManBean> beans) {
        mBeans = beans;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(mContext).inflate(R.layout.item_invite_man_recycler_layout, parent, false);
        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        final ManBean bean = mBeans.get(position);
        MyViewHolder mHolder = (MyViewHolder) holder;
        if (bean != null) {
            //数字
            mHolder.mNumberTv.setText(String.valueOf(position + 1));
            //头像
            String headImg = bean.t_handImg;
            if (!TextUtils.isEmpty(headImg)) {
                int width = DevicesUtil.dp2px(mContext, 50);
                int high = DevicesUtil.dp2px(mContext, 50);
                ImageLoadHelper.glideShowCircleImageWithUrl(mContext, headImg, mHolder.mHeadIv, width, high);
            } else {
                mHolder.mHeadIv.setImageResource(R.drawable.default_head_img);
            }
            //主播昵称
            String nick = bean.t_nickName;
            if (!TextUtils.isEmpty(nick)) {
                mHolder.mNameTv.setText(nick);
            }
            //金币
            mHolder.mGoldTv.setText(String.valueOf(bean.totalCount));
            mHolder.mContentRl.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int actorId = bean.t_id;
                    if (actorId > 0) {
                        Intent intent = new Intent(mContext, ActorInfoOneActivity.class);
                        intent.putExtra(Constant.ACTOR_ID, actorId);
                        mContext.startActivity(intent);
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

        TextView mNumberTv;
        ImageView mHeadIv;
        TextView mNameTv;
        TextView mGoldTv;
        View mContentRl;

        MyViewHolder(View itemView) {
            super(itemView);
            mNumberTv = itemView.findViewById(R.id.number_tv);
            mHeadIv = itemView.findViewById(R.id.head_iv);
            mNameTv = itemView.findViewById(R.id.name_tv);
            mGoldTv = itemView.findViewById(R.id.gold_tv);
            mContentRl = itemView.findViewById(R.id.content_rl);
        }

    }

}
