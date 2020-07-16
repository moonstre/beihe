package com.yiliao.chat.adapter;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.yiliao.chat.R;
import com.yiliao.chat.bean.ActivityBean;
import com.yiliao.chat.helper.ImageLoadHelper;
import com.yiliao.chat.myactivity.ActivityDetalisActivity;

import java.util.ArrayList;
import java.util.List;

public class ActivityListAdpater extends RecyclerView.Adapter<ActivityListAdpater.ActivityListViewHolder> {
    private Context mContext;
    private LayoutInflater inflater;
    private List<ActivityBean> mList = new ArrayList<>();
    public ActivityListAdpater(Context mContext){
        this.mContext=mContext;
        inflater=LayoutInflater.from(mContext);
    }

    public void loadData(List<ActivityBean> data){
        this.mList=data;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ActivityListViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view =inflater.inflate(R.layout.item_activity_list,null,false);
        ActivityListViewHolder holder  = new ActivityListViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull ActivityListViewHolder holder, final int position) {
        holder.itme_title.setText(mList.get(position).t_title);
        holder.itme_nick.setText(mList.get(position).t_nickName);
        holder.itme_location.setText(mList.get(position).juli+"km");

        ImageLoadHelper.glideShowCornerImageWithUrl(mContext, mList.get(position).t_img,holder.item_bg);
        ImageLoadHelper.glideShowCircleImageWithUrl(mContext, mList.get(position).t_handImg,holder.itme_headimage);

        holder.itme_lin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mContext, ActivityDetalisActivity.class);
                intent.putExtra("ActivityID",mList.get(position).activityId);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                mContext.startActivity(intent);
            }
        });

        if (mList.get(position).t_sex.equals("0")){
            holder.itme_gender.setBackgroundResource(R.drawable.female_red);
        }else {
            holder.itme_gender.setBackgroundResource(R.drawable.male_blue);
        }
    }

    @Override
    public int getItemCount() {
        return mList.size();
    }

    public class ActivityListViewHolder extends RecyclerView.ViewHolder {
        public ImageView itme_headimage;
        public TextView itme_nick;
        public TextView itme_title;
        public TextView itme_location;
        public ImageView item_bg;
        public LinearLayout itme_lin;
        public ImageView itme_gender;

        public ActivityListViewHolder(View itemView) {
            super(itemView);
            item_bg = itemView.findViewById(R.id.item_bg);
            itme_headimage = itemView.findViewById(R.id.itme_headimage);
            itme_nick = itemView.findViewById(R.id.itme_nick);
            itme_title = itemView.findViewById(R.id.itme_title);
            itme_location = itemView.findViewById(R.id.itme_location);
            itme_lin=itemView.findViewById(R.id.itme_lin);
            itme_gender=itemView.findViewById(R.id.itme_gender);
        }
    }
}
