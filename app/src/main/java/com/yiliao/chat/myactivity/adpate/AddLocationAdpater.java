package com.yiliao.chat.myactivity.adpate;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.amap.api.maps.AMapUtils;
import com.amap.api.maps.model.LatLng;
import com.amap.api.services.core.LatLonPoint;
import com.yiliao.chat.R;
import com.yiliao.chat.helper.SharedPreferenceHelper;
import com.yiliao.chat.myactivity.bean.LocationBean;

import java.util.ArrayList;
import java.util.List;

public class AddLocationAdpater extends RecyclerView.Adapter<AddLocationAdpater.AddLocationViewHolder> {
    private Context mContext;
    private List<LocationBean> mList ;

    public AddLocationAdpater(Context mContext,List<LocationBean> mList){
        this.mContext=mContext;
        this.mList=mList;
    }

    @NonNull
    @Override
    public AddLocationViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.addlocationitme,null,false);
        AddLocationViewHolder holder = new AddLocationViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull AddLocationViewHolder holder, final int position) {

        holder.AddLoction_Address.setText(mList.get(position).Address);
        holder.AddLoction_Title.setText(mList.get(position).Title);

        LatLng latLng1 = new LatLng(Double.parseDouble(SharedPreferenceHelper.getCodeLat(mContext)),Double.parseDouble(SharedPreferenceHelper.getCodeLng(mContext)));
        LatLng latLng2 = new LatLng(mList.get(position).lat,mList.get(position).lna);
        float distance = AMapUtils.calculateLineDistance(latLng1,latLng2);

        holder.AddLoction_Dis.setText((int)distance+"m");

        holder.AddLoction.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                back.OnClcik(position);
            }
        });
    }

    @Override
    public int getItemCount() {
        return mList.size();
    }

    public class AddLocationViewHolder extends RecyclerView.ViewHolder {

        public TextView AddLoction_Title;
        public TextView AddLoction_Address;
        public TextView AddLoction_Dis;
        public RelativeLayout AddLoction;

        public AddLocationViewHolder(View itemView) {
            super(itemView);
            AddLoction_Title =itemView.findViewById(R.id.AddLoction_Title);
            AddLoction_Dis =itemView.findViewById(R.id.AddLoction_Dis);
            AddLoction_Address =itemView.findViewById(R.id.AddLoction_Address);
            AddLoction=itemView.findViewById(R.id.AddLoction);
        }
    }
    AddLoctionItmeCallBack back;
    public void setItmeOnClack(AddLoctionItmeCallBack back){
        this.back=back;
    }

    public interface AddLoctionItmeCallBack{
        public void OnClcik(int position);
    }
}
