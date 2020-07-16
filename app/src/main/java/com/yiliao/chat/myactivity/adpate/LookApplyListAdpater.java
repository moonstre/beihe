package com.yiliao.chat.myactivity.adpate;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.yiliao.chat.R;
import com.yiliao.chat.activity.ChatActivity;
import com.yiliao.chat.constant.Constant;
import com.yiliao.chat.helper.ImageLoadHelper;
import com.yiliao.chat.helper.SharedPreferenceHelper;
import com.yiliao.chat.myactivity.bean.ActivityApplyBean;

import java.util.ArrayList;
import java.util.List;

public class LookApplyListAdpater extends RecyclerView.Adapter<LookApplyListAdpater.LookApplyListViewHolder> {

    private Context mContext;
    private List<ActivityApplyBean> mList =new ArrayList<>();
    public LookApplyListAdpater(Context mContext){
        this.mContext=mContext;
    }

    public void loadData(List<ActivityApplyBean> data){
        this.mList=data;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public LookApplyListViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.look_apply_list_itme,null,false);
        LookApplyListViewHolder holder = new LookApplyListViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull LookApplyListViewHolder holder, final int position) {
            holder.LookApplyListItme_Name.setText(mList.get(position).t_nickName);
            holder.LookApplyListItme_Phone.setText("手机号 "+mList.get(position).t_phone);
            holder.LookApplyListItme_WChat.setText("微信号 "+mList.get(position).t_wx);
        ImageLoadHelper.glideShowCircleImageWithUrl(mContext, mList.get(position).t_handImg,holder.LookApplyListItme_HeadImage);

        holder.LookApplyListItme_ToWChat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String mineUrl = SharedPreferenceHelper.getAccountInfo(mContext).headUrl;
                Intent intent = new Intent(mContext, ChatActivity.class);
                intent.putExtra(Constant.TITLE, mList.get(position).t_nickName);
                intent.putExtra(Constant.ACTOR_ID, mList.get(position).t_uid);
                intent.putExtra(Constant.MINE_HEAD_URL, mineUrl);
                intent.putExtra(Constant.MINE_ID, String.valueOf(SharedPreferenceHelper.getAccountInfo(mContext).t_id));
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                mContext.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return mList.size();
    }

    public class LookApplyListViewHolder extends RecyclerView.ViewHolder {
        public ImageView LookApplyListItme_HeadImage;
        public TextView LookApplyListItme_Name;
        public TextView LookApplyListItme_Phone;
        public TextView LookApplyListItme_WChat;
        public ImageView LookApplyListItme_ToWChat;
        public LookApplyListViewHolder(View itemView) {
            super(itemView);
            LookApplyListItme_HeadImage =itemView.findViewById(R.id.LookApplyListItme_HeadImage);
            LookApplyListItme_Name=itemView.findViewById(R.id.LookApplyListItme_Name);
            LookApplyListItme_Phone=itemView.findViewById(R.id.LookApplyListItme_Phone);
            LookApplyListItme_WChat=itemView.findViewById(R.id.LookApplyListItme_WChat);
            LookApplyListItme_ToWChat=itemView.findViewById(R.id.LookApplyListItme_ToWChat);
        }
    }
}
