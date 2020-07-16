package com.yiliao.chat.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.yiliao.chat.R;
import com.yiliao.chat.base.BaseActivity;
import com.yiliao.chat.bean.StartServiceBean;

import java.util.ArrayList;
import java.util.List;

public class AddTimeDetailAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private BaseActivity mContext;
    public AddTimeDetailAdapter(BaseActivity context){
        mContext=context;
    }
    List<StartServiceBean.continueClockList> mBeans=new ArrayList<>();
    public void loadData(List<StartServiceBean.continueClockList> beans) {
        mBeans = beans;
        notifyDataSetChanged();
    }
    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(mContext).inflate(R.layout.add_time_server_value_item,parent,false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        StartServiceBean.continueClockList list=mBeans.get(position);
        MyViewHolder mHolder=(MyViewHolder) holder;
        if (list!=null){
            mHolder.t_server_value.setText(list.serverContent);
            mHolder.t_server_time.setText(list.creatime+"");
            mHolder.t_server_time_houre.setText(list.clolckTimes+"小时");
            mHolder.t_server_time_price.setText(list.clolckPrice+"金币");
        }
    }

    @Override
    public int getItemCount() {
        return mBeans != null ? mBeans.size() : 0;
    }
    class MyViewHolder extends RecyclerView.ViewHolder{
        TextView t_server_value,t_server_time,t_server_time_houre,t_server_time_price;
        MyViewHolder(View view){
            super(view);
            t_server_value=view.findViewById(R.id.t_server_value);
            t_server_time=view.findViewById(R.id.t_server_time);
            t_server_time_houre=view.findViewById(R.id.t_server_time_houre);
            t_server_time_price=view.findViewById(R.id.t_server_time_price);
        }
    }
}
