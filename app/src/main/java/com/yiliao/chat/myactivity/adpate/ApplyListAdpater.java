package com.yiliao.chat.myactivity.adpate;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.yiliao.chat.R;
import com.yiliao.chat.helper.ImageLoadHelper;
import com.yiliao.chat.myactivity.bean.ActivityApplyBean;

import java.util.List;

public class ApplyListAdpater extends RecyclerView.Adapter<ApplyListAdpater.ApplyListViewHolder> {
    private Context mContext;
    private List<ActivityApplyBean> mList;
    public ApplyListAdpater(Context mContext,List<ActivityApplyBean> data){
        this.mContext=mContext;
        this.mList=data;
    }

    @NonNull
    @Override
    public ApplyListViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.detalis_apply_list_itme,null,false);
        ApplyListViewHolder holder = new ApplyListViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull ApplyListViewHolder holder, int position) {
        ImageLoadHelper.glideShowCircleImageWithUrl(mContext, mList.get(position).t_handImg,holder.ApplyList_ItmeImage);
    }

    @Override
    public int getItemCount() {
        return mList.size();
    }

    public class ApplyListViewHolder extends RecyclerView.ViewHolder {
        public ImageView ApplyList_ItmeImage;
        public ApplyListViewHolder(View itemView) {
            super(itemView);
            ApplyList_ItmeImage=itemView.findViewById(R.id.ApplyList_ItmeImage);
        }
    }
}
