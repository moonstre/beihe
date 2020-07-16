package com.yiliao.chat.fulive.adapter;

import android.content.Context;
import android.widget.TextView;

import com.yiliao.chat.R;
import com.yiliao.chat.fulive.entity.FaceMakeup;

import java.util.List;

public class FaceMakeupAdapter extends BaseRecyclerAdapter<FaceMakeup> {
    private Context mContext;

    public FaceMakeupAdapter(Context context, List<FaceMakeup> data) {
        super(data, R.layout.layout_beauty_control_recycler);
        mContext = context;
    }

    @Override
    protected void bindViewHolder(BaseViewHolder viewHolder, FaceMakeup item) {
        viewHolder.setText(R.id.control_recycler_text, mContext.getResources().getString(item.getNameId()))
                .setImageResource(R.id.control_recycler_img, item.getIconId());
    }

    @Override
    protected void handleSelectedState(BaseViewHolder viewHolder, FaceMakeup data, boolean selected) {
        ((TextView) viewHolder.getViewById(R.id.control_recycler_text)).setTextColor(selected ?
                mContext.getResources().getColor(R.color.main_color) : mContext.getResources().getColor(R.color.colorWhite));
        viewHolder.setBackground(R.id.control_recycler_img, selected ? R.drawable.control_filter_select : 0);
    }
}