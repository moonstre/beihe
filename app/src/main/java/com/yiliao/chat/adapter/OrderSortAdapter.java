package com.yiliao.chat.adapter;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.yiliao.chat.R;
import com.yiliao.chat.bean.OrderSortBean;
import com.yiliao.chat.bean.RankBean;
import com.yiliao.chat.helper.ImageLoadHelper;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class OrderSortAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>{
    private Activity mContext;
    private List<OrderSortBean> mBeans = new ArrayList<>();
    private boolean mFromCost = false;
    public OrderSortAdapter(Activity context,boolean fromCost) {
        mContext = context;
        mFromCost = fromCost;
    }
    public void loadData(List<OrderSortBean> beans) {
        mBeans = beans;
        notifyDataSetChanged();
    }
    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(mContext).inflate(R.layout.order_states_sort, parent, false);
        return new ContentViewHolder(itemView);
    }

    @SuppressLint("ResourceAsColor")
    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        final OrderSortBean bean=mBeans.get(position);
        ContentViewHolder mHolder=(ContentViewHolder)holder;
        if (bean!=null){
            if (!TextUtils.isEmpty(bean.username)){
                mHolder.name.setText(bean.username);
            }
            mHolder.go_to_detail.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (mOnItemClickListener!=null){
                        mOnItemClickListener.onItemClick(bean);
                    }
                }
            });
            if (!Objects.equals(bean.userHandImg,null)){
                ImageLoadHelper.glideShowCircleImageWithUrl(mContext, bean.userHandImg, mHolder.header_iv);
            }if (!TextUtils.isEmpty(bean.createtime)){
                mHolder.time.setText(bean.createtime);
            }if (bean.status==1){
                String strMsg = "<font color=\"#7275FF\">未开始</font>";
                mHolder.states.setText(Html.fromHtml(strMsg));
                mHolder.red_number_tv.setVisibility(View.VISIBLE);
            }if (bean.status==3){
                String strMsg = "<font color=\"#7275FF\">未开始</font>";
                mHolder.states.setText(Html.fromHtml(strMsg));
                mHolder.red_number_tv.setVisibility(View.VISIBLE);
                return;
            }else if (bean.status==4){
                String strMsg = "<font color=\"#7275FF\">进行中</font>";
                mHolder.states.setText(Html.fromHtml(strMsg));
                mHolder.red_number_tv.setVisibility(View.GONE);
                return;
            }else if (bean.status==5){
                mHolder.states.setText("已完成");
                mHolder.red_number_tv.setVisibility(View.GONE);
                return;
            }else if (bean.status==7){
                mHolder.states.setText("已取消");
                mHolder.red_number_tv.setVisibility(View.GONE);
            }

        }
    }

    @Override
    public int getItemCount() {
        return mBeans != null ? mBeans.size() : 0;
    }
    class ContentViewHolder extends RecyclerView.ViewHolder {

         View go_to_detail;

         ImageView header_iv;
         TextView name,time,states,red_number_tv;

        ContentViewHolder(View itemView) {
            super(itemView);
            go_to_detail = itemView.findViewById(R.id.go_to_detail);
            header_iv = itemView.findViewById(R.id.header_iv);
            name = itemView.findViewById(R.id.name);
            states = itemView.findViewById(R.id.states);
            time = itemView.findViewById(R.id.time);
            red_number_tv = itemView.findViewById(R.id.red_number_tv);

        }
    }
    //订单详情
    public interface OnItemClickListener {
        void onItemClick(OrderSortBean sortBean);
    }
    private OnItemClickListener mOnItemClickListener;

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        mOnItemClickListener = onItemClickListener;
    }
}
