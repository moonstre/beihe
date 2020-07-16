package com.yiliao.chat.adapter;

import android.app.Activity;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.yiliao.chat.R;
import com.yiliao.chat.activity.OpinionResultDetailActivity;
import com.yiliao.chat.bean.OpinionResultBean;
import com.yiliao.chat.constant.Constant;

import java.util.ArrayList;
import java.util.List;

public class OpinionResultAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private Activity mContext;
    private List<OpinionResultBean> mBeans = new ArrayList<>();

    public OpinionResultAdapter(Activity context) {
        mContext = context;
    }

    public void loadData(List<OpinionResultBean> beans) {
        mBeans = beans;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(mContext).inflate(R.layout.item_opinion_result, parent, false);
        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        final OpinionResultBean bean = mBeans.get(position);
        MyViewHolder mHolder = (MyViewHolder) holder;
        if (bean != null) {
            mHolder.tvContent.setText(bean.t_content);
            if (bean.t_is_handle == 0) {
                mHolder.tvHandle.setText(mContext.getResources().getString(R.string.opinion_ing));
            } else {
                mHolder.tvHandle.setText(mContext.getResources().getString(R.string.opinion_ed));
            }
            mHolder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(mContext, OpinionResultDetailActivity.class);
                    intent.putExtra(Constant.ACTOR_ID, bean.t_id);
                    mContext.startActivity(intent);
                }
            });
        }
    }

    @Override
    public int getItemCount() {
        return mBeans != null ? mBeans.size() : 0;
    }

    class MyViewHolder extends RecyclerView.ViewHolder {
        TextView tvContent;
        TextView tvHandle;

        MyViewHolder(View itemView) {
            super(itemView);
            tvContent = itemView.findViewById(R.id.tvContent);
            tvHandle = itemView.findViewById(R.id.tvHandle);
        }
    }
}
