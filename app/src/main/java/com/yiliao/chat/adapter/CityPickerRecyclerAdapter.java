package com.yiliao.chat.adapter;

import android.app.Activity;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.yiliao.chat.R;
import com.yiliao.chat.util.DevicesUtil;

import java.util.ArrayList;
import java.util.List;

/*
 * Copyright (C) 2018
 * 版权所有
 *
 * 功能描述:  设置收费项目RecyclerView的Adapter
 * 作者：
 * 创建时间：2018/6/30
 *
 * 修改人：
 * 修改描述：
 * 修改日期
 */
public class CityPickerRecyclerAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private Activity mContext;
    private List<String> mBeans = new ArrayList<>();

    public CityPickerRecyclerAdapter(Activity context) {
        mContext = context;
    }

    public void loadData(List<String> beans) {
        mBeans = beans;
        notifyDataSetChanged();
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(mContext).inflate(R.layout.item_city_picker_recycler_layout, parent, false);
        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        String bean = mBeans.get(position);
        MyViewHolder myViewHolder = (MyViewHolder) holder;
        if (myViewHolder != null) {
            myViewHolder.mContentTv.setText(bean);

            //设置点击事件
        }
    }

    @Override
    public int getItemCount() {
        return mBeans != null ? mBeans.size() : 0;
    }

    class MyViewHolder extends RecyclerView.ViewHolder {

        LinearLayout mContentLl;
        TextView mContentTv;

        MyViewHolder(View itemView) {
            super(itemView);
            mContentLl = itemView.findViewById(R.id.content_ll);
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(DevicesUtil.getScreenW(mContext) / 2,
                    ViewGroup.LayoutParams.WRAP_CONTENT);
            mContentLl.setLayoutParams(params);
            mContentTv = itemView.findViewById(R.id.content_tv);
        }

    }


}
