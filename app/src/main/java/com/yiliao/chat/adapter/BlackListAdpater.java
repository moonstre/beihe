package com.yiliao.chat.adapter;

import android.app.Activity;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.yiliao.chat.R;
import com.yiliao.chat.bean.BlackListBean;
import com.yiliao.chat.helper.ImageLoadHelper;

import java.util.ArrayList;
import java.util.List;

public class BlackListAdpater extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private Activity mContext;
    private List<BlackListBean> mBeans = new ArrayList<>();

    public BlackListAdpater(Activity context) {
        mContext = context;
    }

    public void loadData(List<BlackListBean> beans) {
        mBeans = beans;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(mContext).inflate(R.layout.item_black_list, parent, false);
        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, final int position) {
        final BlackListBean bean = mBeans.get(position);
        MyViewHolder mHolder = (MyViewHolder) holder;
        if (bean != null) {
            if (!TextUtils.isEmpty(bean.t_nickName)) {
                mHolder.mNameTv.setText(bean.t_nickName);
            }
            //头像
            if (!TextUtils.isEmpty(bean.t_handImg)) {
                ImageLoadHelper.glideShowCircleImageWithUrl(mContext, bean.t_handImg, mHolder.mHeadIv);
            } else {
                mHolder.mHeadIv.setImageResource(R.drawable.default_head_img);
            }
            //个性签名
            if (!TextUtils.isEmpty(bean.t_autograph)) {
                mHolder.mIncTv.setText(bean.t_autograph);
            } else {
                mHolder.mIncTv.setText(mContext.getResources().getString(R.string.lazy));
            }

            //点击事件
            mHolder.mDeteleTv.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                   back.OnClick(position);
                }
            });
        }
    }

    @Override
    public int getItemCount() {
        return mBeans != null ? mBeans.size() : 0;
    }

    class MyViewHolder extends RecyclerView.ViewHolder {

        View mContentRl;
        ImageView mHeadIv;
        TextView mNameTv;
        TextView mIncTv;
        TextView mDeteleTv;


        MyViewHolder(View itemView) {
            super(itemView);
            mContentRl = itemView.findViewById(R.id.content_rl);
            mHeadIv = itemView.findViewById(R.id.BlackList_Head);
            mNameTv = itemView.findViewById(R.id.BlackList_Name);
            mIncTv = itemView.findViewById(R.id.BlackList_Intc);
            mDeteleTv = itemView.findViewById(R.id.BlackList_Detele);
        }

    }

    private OnItmeCallBack back;
    public void setOnClick(OnItmeCallBack back){
        this.back=back;
    }


    public interface OnItmeCallBack{
        public void OnClick(int postion);
    }
}
