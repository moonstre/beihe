package com.yiliao.chat.adapter;

import android.app.Activity;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.yiliao.chat.R;
import com.yiliao.chat.bean.UserCapitalBean;

import java.util.ArrayList;
import java.util.List;

public class UserCapitalRecyclerAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private Activity mContext;
    private List<UserCapitalBean> mBeans = new ArrayList<>();

    public UserCapitalRecyclerAdapter(Activity context) {
        mContext = context;
    }

    public void loadData(List<UserCapitalBean> beans) {
        mBeans = beans;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(mContext).inflate(R.layout.item_user_capital_recycler_layout, parent, false);
        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        UserCapitalBean bean = mBeans.get(position);
        MyViewHolder mHolder = (MyViewHolder) holder;
        if (bean != null) {
            mHolder.tvTime.setText(bean.time);
            mHolder.tvTotal.setText(bean.total + "");
        }
    }

    @Override
    public int getItemCount() {
        return mBeans != null ? mBeans.size() : 0;
    }

    class MyViewHolder extends RecyclerView.ViewHolder {
        TextView tvTime;
        TextView tvTotal;

        MyViewHolder(View itemView) {
            super(itemView);
            tvTime = itemView.findViewById(R.id.tvTime);
            tvTotal = itemView.findViewById(R.id.tvTotal);
        }
    }
}
