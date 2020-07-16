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

public class SearchOrderAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private BaseActivity mContext;
    private List<SearchBean> mBeans = new ArrayList<>();
    public SearchOrderAdapter(BaseActivity context) {
        mContext = context;
    }
    public void loadData(List<SearchBean> beans) {
        mBeans = beans;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(mContext).inflate(R.layout.search_order_item,
                parent, false);
        return new SearchOrderAdapter.MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        final SearchBean bean = mBeans.get(position);
        final MyViewHolder mHolder = (MyViewHolder) holder;
        if (bean!=null){
            //头像
            String handImg = bean.t_handImg;
            if (!TextUtils.isEmpty(handImg)) {
                int width = DevicesUtil.dp2px(mContext, 50);
                int high = DevicesUtil.dp2px(mContext, 50);
                ImageLoadHelper.glideShowCircleImageWithUrl(mContext, handImg, mHolder.mHeadIv, width, high);
            } else {
                mHolder.mHeadIv.setImageResource(R.drawable.default_head_img);
            }
            final String isGlid=bean.inGuid;
            mHolder.ll_left.setEnabled(true);
            if (!TextUtils.isEmpty(isGlid)){
                switch (isGlid){
                    case "0":
                        mHolder.rl_state.setBackgroundResource(R.drawable.yuan_gree_save);
                        mHolder.user_head_iv.setVisibility(View.VISIBLE);
                        mHolder.invide_tv.setText("邀请");
                        break;
                    case "1":
                        mHolder.rl_state.setBackgroundResource(R.drawable.yuan_gree_save_two);
                        mHolder.invide_tv.setText("邀请");
                        break;
                    case "2":
                        mHolder.rl_state.setVisibility(View.GONE);
                        break;
                    case "3":
                        mHolder.rl_state.setBackgroundResource(R.drawable.yuan_gree_save_two);
                        mHolder.user_head_iv.setVisibility(View.GONE);
                        mHolder.invide_tv.setText("已邀请");
                        break;
                }
            }
            //昵称
            String nick = bean.t_nickName;
            if (!TextUtils.isEmpty(nick)) {
                mHolder.mTitleTv.setText(nick);
            }
            int toke=bean.t_idcard;
            if (!TextUtils.isEmpty(String.valueOf(bean.t_idcard))){
                mHolder.mIdTv.setText(toke+"");
            }
            mHolder.mHeadIv.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mOnItemClickListener.onItemClick(bean,1);
                }
            });
            mHolder.ll_left.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (mOnItemClickListener!=null){
                        if (isGlid.equals("0")){
                            mHolder.rl_state.setBackgroundResource(R.drawable.yuan_gree_save_two);
                            mHolder.user_head_iv.setVisibility(View.GONE);
                            mHolder.invide_tv.setText("已邀请");
//                            mHolder.ll_left.setEnabled(false);
                        }
                        mOnItemClickListener.onItemClick(bean,0);
                    }
                }
            });
//            mHolder.ll_state.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
//
//                }
//            });
        }
    }

    @Override
    public int getItemCount() {
        return mBeans != null ? mBeans.size() : 0;
    }
    class MyViewHolder extends RecyclerView.ViewHolder {

        View mContentLl;
        View rl_state;
        View ll_state;
        View ll_click;
        View ll_left;
        ImageView mHeadIv;
        TextView mTitleTv;
        TextView mIdTv;
        TextView invide_tv;
        ImageView user_head_iv;

        MyViewHolder(View itemView) {
            super(itemView);
            mContentLl = itemView.findViewById(R.id.content_ll);
            mHeadIv = itemView.findViewById(R.id.head_iv);
            ll_click=itemView.findViewById(R.id.ll_click);
            rl_state = itemView.findViewById(R.id.rl_state);
            ll_state=itemView.findViewById(R.id.ll_state);
            mTitleTv = itemView.findViewById(R.id.title_tv);
            mIdTv = itemView.findViewById(R.id.id_tv);
            invide_tv=itemView.findViewById(R.id.invide_tv);
            user_head_iv = itemView.findViewById(R.id.user_head_iv);
            ll_left=itemView.findViewById(R.id.ll_left);
        }
    }
    private OnItemClickListener mOnItemClickListener;
    public interface OnItemClickListener{
        void onItemClick(SearchBean sortBean,int flag);
    }
    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        mOnItemClickListener = onItemClickListener;
    }
}
