package com.yiliao.chat.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.SparseBooleanArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.yiliao.chat.R;
import com.yiliao.chat.bean.LabelBean;
import com.yiliao.chat.constant.Constant;

import java.util.ArrayList;
import java.util.List;

/*
 * Copyright (C) 2018
 * 版权所有
 *
 * 功能描述：标签RecyclerView的Adapter
 * 作者：
 * 创建时间：2018/6/19
 *
 * 修改人：
 * 修改描述：
 * 修改日期
 */
public class LabelRecyclerAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private Context mContext;
    private List<LabelBean> mBeans = new ArrayList<>();
    private List<LabelBean> mSelectedLabels = new ArrayList<>();
    private int codes;
    private SparseBooleanArray mSelectArray;
    private boolean isFirst;
    public SparseBooleanArray getmSelectArray() {
        return mSelectArray;
    }

    public void setmSelectArray(SparseBooleanArray mSelectArray) {
        this.mSelectArray = mSelectArray;
    }

    public LabelRecyclerAdapter(Context context, int code) {
        mContext = context;
        codes=code;
    }

    public void loadData(List<LabelBean> beans) {
        mBeans = beans;
        notifyDataSetChanged();
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView;
        if (codes==2){
            itemView = LayoutInflater.from(mContext).inflate(R.layout.item_go_to_home_recycler_layout, parent, false);
        }else {
            itemView = LayoutInflater.from(mContext).inflate(R.layout.item_reward_gold_recycler_layout, parent, false);
        }

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, final int position) {
        final int mPosition = position;
        final LabelBean bean = mBeans.get(position);
        final MyViewHolder mHolder = (MyViewHolder) holder;
        mHolder.mGoldTv.setText(bean.t_label_name);
            if (bean.selected) {
                mHolder.mGoldTv.setSelected(true);
                if (Constant.hideHomeNearAndNew()){
                    if (codes==3){
                        if (mSelectedLabels.size() < 2) {
                            mSelectedLabels.add(bean);
                        }
                    }else {
                        if (mSelectedLabels.size() < 4) {
                            mSelectedLabels.add(bean);
                        }
                    }

                }else {
                    if (mSelectedLabels.size() < 2) {
                        mSelectedLabels.add(bean);
                    }
                }

            } else {
                mHolder.mGoldTv.setSelected(false);
            }
        if (codes==2){
            mHolder.mGoldTv.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (mOnItemClickListener!=null){
//                        mHolder.mGoldTv.setTag(position);
//                        if (v.isSelected()){
//                            v.setSelected(false);
//                        }
//                        mBeans.get(position).selected=true;
//                        mHolder.mGoldTv.setSelected(true);
//                        notifyDataSetChanged();
                        mOnItemClickListener.onItemClick(bean);
                    }
                }
            });
        }else {
            mHolder.mGoldTv.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    if (v.isSelected()) {
                        v.setSelected(false);
                        mSelectedLabels.remove(bean);
                    } else {
                        if (Constant.hideHomeNearAndNew()){
                            if (codes==3){
                                if (mSelectedLabels.size() < 2) {
                                    mSelectedLabels.add(bean);
                                    v.setSelected(true);
                                } else {
                                    changeData(mPosition);
                                }
                            }else {
                                if (mSelectedLabels.size() < 4) {
                                    mSelectedLabels.add(bean);
                                    v.setSelected(true);
                                } else {
                                    changeData(mPosition);
                                }
                            }

                        }else {
                            if (mSelectedLabels.size() < 2) {
                                mSelectedLabels.add(bean);
                                v.setSelected(true);
                            } else {
                                changeData(mPosition);
                            }
                        }
                    }

                }
            });
        }

    }

    /**
     * change data
     */
    private void changeData(int position) {
        List<Integer> positions = new ArrayList<>();
        for (int i = 0; i < mBeans.size(); i++) {
            LabelBean labelBean = mBeans.get(i);
            if (labelBean.selected) {
                positions.add(i);
            }
        }
        int firstPosition = positions.get(0);
        int secondPosition = positions.get(1);
        if (codes==3){
            //如果点击的比原来选中的都小,那么去掉后面大的
            if (position < firstPosition && position < secondPosition) {
                for (int j = mBeans.size() - 1; j >= 0; j--) {
                    LabelBean labelBean = mBeans.get(j);
                    if (labelBean.selected) {
                        labelBean.selected = false;
                        break;
                    }
                }

            } else {//都大或者中间
                for (int i = 0; i < mBeans.size(); i++) {
                    LabelBean labelBean = mBeans.get(i);
                    if (labelBean.selected) {
                        labelBean.selected = false;
                        break;
                    }
                }
            }
        }else {
            if (Constant.hideHomeNearAndNew()){
                int threePosition=positions.get(2);
                int forePosition=positions.get(3);
                //如果点击的比原来选中的都小,那么去掉后面大的
                if (position < firstPosition && position < secondPosition&&position<threePosition&&position<forePosition) {
                    for (int j = mBeans.size() - 1; j >= 0; j--) {
                        LabelBean labelBean = mBeans.get(j);
                        if (labelBean.selected) {
                            labelBean.selected = false;
                            break;
                        }
                    }

                } else {//都大或者中间
                    for (int i = 0; i < mBeans.size(); i++) {
                        LabelBean labelBean = mBeans.get(i);
                        if (labelBean.selected) {
                            labelBean.selected = false;
                            break;
                        }
                    }
                }
            }else {
                //如果点击的比原来选中的都小,那么去掉后面大的
                if (position < firstPosition && position < secondPosition) {
                    for (int j = mBeans.size() - 1; j >= 0; j--) {
                        LabelBean labelBean = mBeans.get(j);
                        if (labelBean.selected) {
                            labelBean.selected = false;
                            break;
                        }
                    }

                } else {//都大或者中间
                    for (int i = 0; i < mBeans.size(); i++) {
                        LabelBean labelBean = mBeans.get(i);
                        if (labelBean.selected) {
                            labelBean.selected = false;
                            break;
                        }
                    }
                }
            }
        }


        //将选中的position设为选中
        mBeans.get(position).selected = true;
        mSelectedLabels.clear();
        notifyDataSetChanged();

    }


    /**
     * 获取选中的标签列表
     */
    public List<LabelBean> getSelectedLabels() {
        return mSelectedLabels;
    }

    @Override
    public int getItemCount() {
        return mBeans != null ? mBeans.size() : 0;
    }

    class MyViewHolder extends RecyclerView.ViewHolder {

        TextView mGoldTv;

        MyViewHolder(View itemView) {
            super(itemView);
            mGoldTv = itemView.findViewById(R.id.gold_tv);
        }
    }
    private OnItemClickListener mOnItemClickListener;
    public interface OnItemClickListener{
        void onItemClick(LabelBean sortBean);
    }
    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        mOnItemClickListener = onItemClickListener;
    }
}
