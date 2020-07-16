package com.yiliao.chat.adapter;

import android.app.Activity;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.yiliao.chat.R;
import com.yiliao.chat.bean.LabelBean;
import com.yiliao.chat.bean.SearchBean;

import java.util.ArrayList;
import java.util.List;

public class OpenOrCloseAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private Activity mContext;
    private List<LabelBean> mBeans = new ArrayList<>();
     String states="1";
     private List<LabelBean> list=new ArrayList<>();
     int types;
    public OpenOrCloseAdapter(Activity context){
        mContext=context;
    }
    public void loadData(List<LabelBean> beans,int type) {
        types=type;
        mBeans = beans;
        notifyDataSetChanged();
    }
    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(mContext).inflate(R.layout.open_or_close_item, parent, false);
        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        final LabelBean bean = mBeans.get(position);
        final   MyViewHolder mHolder = (MyViewHolder) holder;

        if (bean != null) {
            mHolder.sure_tv.setText(bean.t_label_name);
            mHolder.phone_number.setText(String.valueOf(bean.price)+"金币/小时");
            int state=bean.status;
            if (state==0){
                mHolder.refuse_check.setSelected(true);
            }else {
                mHolder.refuse_check.setSelected(false);
            }
            if (types!=1){
                mHolder.isChack.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (mOnItemClickListener!=null){
                            if (mHolder.refuse_check.isSelected()){
                                states="1";
                                if (list.size()>0){
                                    list.remove(bean);
                                }
                                mHolder.refuse_check.setSelected(false);
                            }else {
                                states="0";
                                list.add(bean);
                                mHolder.refuse_check.setSelected(true);
                            }
                            mOnItemClickListener.onItemClick(list);
                        }

                    }
                });
            }


        }
    }

    @Override
    public int getItemCount() {
        return mBeans != null ? mBeans.size() : 0;
    }
    class MyViewHolder extends RecyclerView.ViewHolder {
        TextView sure_tv;
        TextView phone_number;
        View refuse_check;
        View isChack;
        MyViewHolder(View itemView) {
            super(itemView);
            sure_tv = itemView.findViewById(R.id.sure_tv);
            phone_number = itemView.findViewById(R.id.phone_number);
            refuse_check=itemView.findViewById(R.id.refuse_check);
            isChack=itemView.findViewById(R.id.isChack);
        }
    }
    private OnItemClickListener mOnItemClickListener;
    public interface OnItemClickListener{
        void onItemClick(List<LabelBean> labelBeans);
    }
    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        mOnItemClickListener = onItemClickListener;
    }
}
