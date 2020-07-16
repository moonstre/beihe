package com.yiliao.chat.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.yiliao.chat.R;
import com.yiliao.chat.activity.OrderDetailsActivity;
import com.yiliao.chat.bean.AddTimeBean;

import java.util.ArrayList;
import java.util.List;

public class AddTimeLableAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private Context mContext;
    private List<AddTimeBean> mBeans = new ArrayList<>();
    private int defItem = -1;//默认值

    public AddTimeLableAdapter(Context context) {
        mContext = context;
    }

    public void loadData(List<AddTimeBean> beans) {
        mBeans = beans;
        notifyDataSetChanged();
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(mContext).inflate(R.layout.item_add_time_recycle_layout, parent, false);
        return new MyViewHolder(itemView);
    }
    public void setDefSelect(int position) {
        this.defItem = position;
        notifyDataSetChanged();
    }

    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder holder, final int position) {
        final int mPosition = position;
        final AddTimeBean bean = mBeans.get(position);
        final MyViewHolder mHolder = (MyViewHolder) holder;
        mHolder.gold_tv_time.setText(bean.times+"小时");
        mHolder.gold_tv_price.setText(bean.price+"金币");
        mHolder.gold_tv_type.setText(bean.t_label_name);
        if (defItem != -1) {
            if (defItem == mPosition) {
                mHolder.dianji_ll.setBackgroundResource(R.mipmap.chose_or_un);
            } else {
                mHolder.dianji_ll.setBackgroundResource(R.mipmap.chose_un_select);
            }
        }
        mHolder.dianji_ll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (onItemListener!=null){
                    onItemListener.onClick(v,position,bean.times,bean.price,bean.t_id);
                }

            }
        });

    }


    @Override
    public int getItemCount() {
        return mBeans != null ? mBeans.size() : 0;
    }

    class MyViewHolder extends RecyclerView.ViewHolder {
        View dianji_ll;
        TextView gold_tv_time,gold_tv_price,gold_tv_type;

        MyViewHolder(View itemView) {
            super(itemView);
            dianji_ll=itemView.findViewById(R.id.dianji_ll);
            gold_tv_time = itemView.findViewById(R.id.gold_tv_time);
            gold_tv_price=itemView.findViewById(R.id.gold_tv_price);
            gold_tv_type=itemView.findViewById(R.id.gold_tv_type);

        }
    }
    private OnItemClickListener onItemListener;
    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.onItemListener = onItemClickListener;
    }

    public interface OnItemClickListener {
        void onClick(View v, int pos,int time, int price,int id);
    }

}
