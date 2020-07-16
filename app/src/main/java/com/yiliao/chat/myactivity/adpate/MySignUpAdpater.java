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
import com.yiliao.chat.myactivity.bean.MySignUpBean;

import java.util.ArrayList;
import java.util.List;

public class MySignUpAdpater extends RecyclerView.Adapter<MySignUpAdpater.MySignUpViewHolder> {
    private Context mContext;
    private List<MySignUpBean> mList = new ArrayList<>();

    private LayoutInflater inflater;

    public MySignUpAdpater(Context mContext){
        this.mContext=mContext;
        inflater =LayoutInflater.from(mContext);
    }

    public void loadData(List<MySignUpBean> data){
        this.mList=data;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public MySignUpViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view =inflater.inflate(R.layout.itme_myestablish,null,false);
        MySignUpViewHolder holder = new MySignUpViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull MySignUpViewHolder holder, final int position) {
        if (mList.get(position).t_status.equals("1")){
            holder.MySignUp_State.setText("进行中");
        }else if(mList.get(position).t_status.equals("2")){
            holder.MySignUp_State.setText("结束");
        }

        holder.MySignUp_Time.setText(mList.get(position).t_activity_time);
        holder.MySignUp_Title.setText(mList.get(position).t_title);

        holder.MyEstablish.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mContext, ActivityDetalisActivity.class);
                intent.putExtra("ActivityID",String.valueOf(mList.get(position).t_activity_id));
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                mContext.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return mList.size();
    }

    public class MySignUpViewHolder extends RecyclerView.ViewHolder {

        public TextView MySignUp_State;
        public TextView MySignUp_Title;
        public TextView MySignUp_Time;
        public RelativeLayout MyEstablish;

        public MySignUpViewHolder(View itemView) {
            super(itemView);
            MySignUp_State =itemView.findViewById(R.id.MyEstablish_State);
            MySignUp_Title =itemView.findViewById(R.id.MyEstablish_Title);
            MySignUp_Time=itemView.findViewById(R.id.MyEstablish_Time);
            MyEstablish=itemView.findViewById(R.id.MyEstablish);
        }
    }
}
