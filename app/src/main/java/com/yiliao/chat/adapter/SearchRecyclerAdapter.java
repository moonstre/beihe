package com.yiliao.chat.adapter;

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
import com.yiliao.chat.activity.ActorVideoPlayActivity;
import com.yiliao.chat.base.BaseActivity;
import com.yiliao.chat.bean.SearchBean;
import com.yiliao.chat.constant.Constant;
import com.yiliao.chat.helper.ImageLoadHelper;
import com.yiliao.chat.util.DevicesUtil;

import java.util.ArrayList;
import java.util.List;

/*
 * Copyright (C) 2018
 * 版权所有
 *
 * 功能描述：搜索RecyclerView的Adapter
 * 作者：
 * 创建时间：2018/8/29
 *
 * 修改人：
 * 修改描述：
 * 修改日期
 */
public class SearchRecyclerAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private BaseActivity mContext;
    private List<SearchBean> mBeans = new ArrayList<>();

    public SearchRecyclerAdapter(BaseActivity context) {
        mContext = context;
    }

    public void loadData(List<SearchBean> beans) {
        mBeans = beans;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(mContext).inflate(R.layout.item_search_recycler_layout,
                parent, false);
        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        final SearchBean bean = mBeans.get(position);
        final MyViewHolder mHolder = (MyViewHolder) holder;
        if (bean != null) {
            //头像
            String handImg = bean.t_handImg;
            if (!TextUtils.isEmpty(handImg)) {
                int width = DevicesUtil.dp2px(mContext, 80);
                int high = DevicesUtil.dp2px(mContext, 72);
                ImageLoadHelper.glideShowImageWithUrl(mContext, handImg, mHolder.mHeadIv, width, high);
            } else {
                mHolder.mHeadIv.setImageResource(R.drawable.default_head_img);
            }
            //状态
            //主播状态0.空闲1.在聊 2.离线(用户 0.在线 1.离线)
            int state = bean.t_online;
            //昵称
            String nick = bean.t_nickName;
            if (!TextUtils.isEmpty(nick)) {
                mHolder.mTitleTv.setText(nick);
            }
            //角色 0.用户 1.主播
            if (bean.t_role == 1) {
                mHolder.mVerifyIv.setVisibility(View.VISIBLE);
                if (state == 0) {
                    mHolder.mStateTv.setVisibility(View.VISIBLE);
                    mHolder.mStateTv.setText(mContext.getResources().getString(R.string.free));
                    mHolder.mStateTv.setBackgroundResource(R.drawable.shape_search_state_text_background);
                } else if (state == 1) {
                    mHolder.mStateTv.setVisibility(View.VISIBLE);
                    mHolder.mStateTv.setText(mContext.getResources().getString(R.string.busy));
                    mHolder.mStateTv.setBackgroundResource(R.drawable.shape_search_state_text_background);
                } else if (state == 2) {
                    mHolder.mStateTv.setVisibility(View.VISIBLE);
                    mHolder.mStateTv.setText(mContext.getResources().getString(R.string.offline));
                    mHolder.mStateTv.setBackgroundResource(R.drawable.shape_search_state_gray_text_background);
                } else {
                    mHolder.mStateTv.setVisibility(View.GONE);
                }
            } else {//用户
                mHolder.mVerifyIv.setVisibility(View.INVISIBLE);
                if (state == 0) {//用户 0.在线 1.离线
                    mHolder.mStateTv.setVisibility(View.VISIBLE);
                    mHolder.mStateTv.setText(mContext.getResources().getString(R.string.free));
                    mHolder.mStateTv.setBackgroundResource(R.drawable.shape_search_state_text_background);
                } else if (state == 1) {
                    mHolder.mStateTv.setVisibility(View.VISIBLE);
                    mHolder.mStateTv.setText(mContext.getResources().getString(R.string.offline));
                    mHolder.mStateTv.setBackgroundResource(R.drawable.shape_search_state_gray_text_background);
                } else {
                    mHolder.mStateTv.setVisibility(View.GONE);
                }
            }
            //ID
            String number = mContext.getResources().getString(R.string.chat_number_one) + bean.t_idcard;
            mHolder.mIdTv.setText(number);
            //全体
            mHolder.mContentLl.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (Constant.goToActorInfoFromHome()) {
                        Intent intent = new Intent(mContext, ActorInfoOneActivity.class);
                        intent.putExtra(Constant.ACTOR_ID, bean.t_id);
                        mContext.startActivity(intent);
                    } else {
                        //该主播是否存在免费视频 0.不存在1.存在
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
        }
    }

    @Override
    public int getItemCount() {
        return mBeans != null ? mBeans.size() : 0;
    }

    class MyViewHolder extends RecyclerView.ViewHolder {

        View mContentLl;
        ImageView mHeadIv;
        TextView mStateTv;
        TextView mTitleTv;
        TextView mIdTv;
        ImageView mVerifyIv;

        MyViewHolder(View itemView) {
            super(itemView);
            mContentLl = itemView.findViewById(R.id.content_ll);
            mHeadIv = itemView.findViewById(R.id.head_iv);
            mStateTv = itemView.findViewById(R.id.state_tv);
            mTitleTv = itemView.findViewById(R.id.title_tv);
            mIdTv = itemView.findViewById(R.id.id_tv);
            mVerifyIv = itemView.findViewById(R.id.verify_iv);
        }
    }

}
