package com.yiliao.chat.myactivity.adpate;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.yiliao.chat.R;
import com.yiliao.chat.myactivity.ActivityDetalisActivity;
import com.yiliao.chat.myactivity.bean.MyEstablishBean;

import java.util.ArrayList;
import java.util.List;

public class MyEstablishAdpater extends RecyclerView.Adapter<MyEstablishAdpater.MyMyEstablishViewHolder> {

    private Context mContext;
    private List<MyEstablishBean> mList = new ArrayList<>();

    private LayoutInflater inflater ;

    public MyEstablishAdpater(Context mContext){
        this.mContext=mContext;
        inflater =LayoutInflater.from(mContext);
    }

    public void loadData(List<MyEstablishBean> data){
        this.mList=data;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public MyMyEstablishViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view =inflater.inflate(R.layout.itme_myestablish,null,false);
        MyMyEstablishViewHolder holder = new MyMyEstablishViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull MyMyEstablishViewHolder holder, final int position) {

        if (mList.get(position).t_status==1){
            holder.MyEstablish_State.setText("进行中");
        }else if(mList.get(position).t_status==2){
            holder.MyEstablish_State.setText("结束");
        }else if(mList.get(position).t_status==3){
            holder.MyEstablish_State.setText("取消");
        }

        holder.MyEstablish_Time.setText(mList.get(position).t_activity_time);
        holder.MyEstablish_Title.setText(mList.get(position).t_title);

        holder.MyEstablish.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mContext, ActivityDetalisActivity.class);
                intent.putExtra("ActivityID",String.valueOf(mList.get(position).activityId));
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                mContext.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return mList.size();
    }

    public class MyMyEstablishViewHolder extends RecyclerView.ViewHolder {
        public TextView MyEstablish_State;
        public TextView MyEstablish_Title;
        public TextView MyEstablish_Time;
        public RelativeLayout MyEstablish;

        public MyMyEstablishViewHolder(View itemView) {
            super(itemView);
            MyEstablish_State =itemView.findViewById(R.id.MyEstablish_State);
            MyEstablish_Title =itemView.findViewById(R.id.MyEstablish_Title);
            MyEstablish_Time=itemView.findViewById(R.id.MyEstablish_Time);
            MyEstablish=itemView.findViewById(R.id.MyEstablish);
        }
    }
}
