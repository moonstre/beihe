package com.yiliao.chat.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.yiliao.chat.R;
import com.yiliao.chat.bean.LoginBean;
import com.yiliao.chat.listener.OnItemClickListener;

import java.util.ArrayList;
import java.util.List;

public class LoginTypeAdapter extends RecyclerView.Adapter<LoginTypeAdapter.Vh> {
    private List<LoginBean> mList;
    private LayoutInflater mInflater;
    private OnItemClickListener<LoginBean> mOnItemClickListener;
    private View.OnClickListener mOnClickListener;

    public LoginTypeAdapter(Context context) {
        mList = new ArrayList<>();
        mInflater = LayoutInflater.from(context);
        mOnClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Object tag = v.getTag();
                if (tag != null) {
                    int position = (int) tag;
                    if (mOnItemClickListener != null) {
                        mOnItemClickListener.onItemClick(mList.get(position), position);
                    }
                }
            }
        };
    }

    public void setItems(List<LoginBean> list) {
        mList.clear();
        mList.addAll(list);
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public Vh onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new Vh(mInflater.inflate(R.layout.item_login_type, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull Vh holder, int position) {
        holder.setData(mList.get(position), position);
    }

    @Override
    public int getItemCount() {
        return mList.size();
    }

    class Vh extends RecyclerView.ViewHolder {

        ImageView mImg;

        public Vh(View itemView) {
            super(itemView);
            mImg = itemView.findViewById(R.id.img);
            mImg.setOnClickListener(mOnClickListener);
        }

        void setData(LoginBean bean, int position) {
            mImg.setTag(position);
            mImg.setImageResource(bean.getIcon());
        }
    }

    public void setOnItemClickListener(OnItemClickListener<LoginBean> onItemClickListener) {
        mOnItemClickListener = onItemClickListener;
    }
}
