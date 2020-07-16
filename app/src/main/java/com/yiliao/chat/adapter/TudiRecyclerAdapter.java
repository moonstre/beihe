package com.yiliao.chat.adapter;

import android.app.Activity;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.yiliao.chat.R;
import com.yiliao.chat.bean.TudiBean;
import com.yiliao.chat.constant.Constant;
import com.yiliao.chat.helper.ImageLoadHelper;
import com.yiliao.chat.listener.OnItemClickListener;
import com.yiliao.chat.util.DevicesUtil;

import java.util.ArrayList;
import java.util.List;

/*
 * Copyright (C) 2018
 * 版权所有
 *
 * 功能描述：我的徒弟 徒孙RecyclerView的Adapter
 * 作者：
 * 创建时间：2018/8/21
 *
 * 修改人：
 * 修改描述：
 * 修改日期
 */
public class TudiRecyclerAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private OnItemClickListener<TudiBean> onItemClickListener;
    private Activity mContext;
    private List<TudiBean> mBeans = new ArrayList<>();

    public TudiRecyclerAdapter(Activity context) {
        mContext = context;
    }

    public void loadData(List<TudiBean> beans) {
        mBeans = beans;
        notifyDataSetChanged();
    }

    public void setOnItemClickListener(OnItemClickListener<TudiBean> onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(mContext).inflate(R.layout.item_tudi_recycler_layout, parent, false);
        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, final int position) {
        final TudiBean bean = mBeans.get(position);
        MyViewHolder mHolder = (MyViewHolder) holder;
        if (bean != null) {
            //主播昵称
            String nick = bean.t_nickName;
            if (!TextUtils.isEmpty(nick)) {
                mHolder.mNameTv.setText(nick);
            } else {
                mHolder.mNameTv.setText("");
            }
            if (Constant.hideTusun()) {
                mHolder.tvId.setVisibility(View.VISIBLE);
                mHolder.tvId.setText("ID:" + bean.t_idcard);
            } else {
                mHolder.tvId.setVisibility(View.GONE);
            }
            //时间
            String time = bean.t_create_time;
            if (!TextUtils.isEmpty(time)) {
                mHolder.mTimeTv.setText(time);
            } else {
                mHolder.mTimeTv.setText(null);
            }
            //头像
            String headImg = bean.t_handImg;
            if (!TextUtils.isEmpty(headImg)) {
                int width = DevicesUtil.dp2px(mContext, 50);
                int high = DevicesUtil.dp2px(mContext, 50);
                ImageLoadHelper.glideShowCircleImageWithUrl(mContext, headImg, mHolder.mHeadIv, width, high);
            } else {
                mHolder.mHeadIv.setImageResource(R.drawable.default_head_img);
            }
            //钱
            mHolder.mMoneyTv.setText(String.valueOf(bean.spreadMoney));
            //是否认证 0.普通用户1.主播
            int t_role = bean.t_role;
            if (t_role == 1) {//主播
                mHolder.mHaveVerifyIv.setVisibility(View.VISIBLE);
            } else {
                mHolder.mHaveVerifyIv.setVisibility(View.GONE);
            }
            mHolder.mContentLl.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (Constant.showTudiMoney()) {
                        int actorId = bean.t_id;
                        if (actorId > 0) {
                            if (onItemClickListener != null) {
                                onItemClickListener.onItemClick(bean, position);
                            }
//                        Intent intent = new Intent(mContext, ActorInfoOneActivity.class);
//                        intent.putExtra(Constant.ACTOR_ID, actorId);
//                        mContext.startActivity(intent);
                        }
                    }
                }
            });

            if (Constant.showTudiMoney()) {
                mHolder.layoutRechargeMoney.setVisibility(View.VISIBLE);
                mHolder.tvRechargeMoney.setText(bean.t_recharge_money + "");
            } else {
                mHolder.layoutRechargeMoney.setVisibility(View.GONE);
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
        TextView tvId;
        TextView mTimeTv;
        TextView mMoneyTv;
        ImageView mHaveVerifyIv;
        View mContentLl;
        LinearLayout layoutRechargeMoney;
        TextView tvRechargeMoney;

        MyViewHolder(View itemView) {
            super(itemView);
            tvId = itemView.findViewById(R.id.tvId);
            mHeadIv = itemView.findViewById(R.id.header_iv);
            mNameTv = itemView.findViewById(R.id.name_tv);
            mTimeTv = itemView.findViewById(R.id.time_tv);
            mMoneyTv = itemView.findViewById(R.id.money_tv);
            mHaveVerifyIv = itemView.findViewById(R.id.have_verify_iv);
            mContentLl = itemView.findViewById(R.id.content_ll);
            layoutRechargeMoney = itemView.findViewById(R.id.layoutRechargeMoney);
            tvRechargeMoney = itemView.findViewById(R.id.tvRechargeMoney);
        }

    }

}
