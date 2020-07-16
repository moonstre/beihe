package com.yiliao.chat.fulive.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.yiliao.chat.R;
import com.yiliao.chat.fulive.entity.Filter;
import com.yiliao.chat.listener.OnItemClickListener;
import com.yiliao.chat.view.OnMultiClickListener;

import java.util.ArrayList;
import java.util.List;

public class FilterRecyclerAdapter extends RecyclerView.Adapter<FilterRecyclerAdapter.HomeRecyclerHolder> {
    private Context mContext;
    private List<Filter> filters = new ArrayList<>();
    private OnItemClickListener<Filter> onItemClickListener;
    // 默认选中第三个粉嫩
    private int mFilterPositionSelect = 2;

    public FilterRecyclerAdapter(Context context) {
        mContext = context;
    }

    public void setItems(List<Filter> filters) {
        this.filters.clear();
        this.filters.addAll(filters);
        notifyDataSetChanged();
    }

    public Filter getItem(int position) {
        if (position < filters.size()) {
            return filters.get(position);
        }
        return null;
    }

    public void setSelectPosition(int position) {
        mFilterPositionSelect = position;
        notifyDataSetChanged();
    }

    public void setOnItemClickListener(OnItemClickListener<Filter> onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }

    @Override
    public FilterRecyclerAdapter.HomeRecyclerHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new FilterRecyclerAdapter.HomeRecyclerHolder(LayoutInflater.from(mContext).inflate(R.layout.layout_beauty_control_recycler, parent, false));
    }

    @Override
    public void onBindViewHolder(FilterRecyclerAdapter.HomeRecyclerHolder holder, final int position) {
        holder.filterImg.setImageResource(filters.get(position).resId());
        holder.filterName.setText(filters.get(position).description());
        if (mFilterPositionSelect == position) {
            holder.filterImg.setBackgroundResource(R.drawable.control_filter_select);
        } else {
            holder.filterImg.setBackgroundResource(0);
        }
        holder.itemView.setOnClickListener(new OnMultiClickListener() {
            @Override
            protected void onMultiClick(View v) {
                mFilterPositionSelect = position;
//                mBeautySeekBar.setVisibility(position == 0 ? INVISIBLE : VISIBLE);
//                setFilterProgress();
                notifyDataSetChanged();
                if (onItemClickListener != null) {
//                    sFilter = filters.get(mFilterPositionSelect);
//                    mOnFUControlListener.onFilterNameSelected(sFilter.filterName());
                    onItemClickListener.onItemClick(filters.get(position), position);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return filters.size();
    }

    public void setFilterLevels(float filterLevels) {
        if (mFilterPositionSelect >= 0) {
//            setFilterLevel(filters.get(mFilterPositionSelect).filterName(), filterLevels);
        }
    }

    public void setFilter(Filter filter) {
        mFilterPositionSelect = filters.indexOf(filter);
    }

    public int indexOf(Filter filter) {
        for (int i = 0; i < filters.size(); i++) {
            if (filter.filterName().equals(filters.get(i).filterName())) {
                return i;
            }
        }
        return -1;
    }

    public void setFilterProgress() {
        if (mFilterPositionSelect >= 0) {
//            seekToSeekBar(getFilterLevel(filters.get(mFilterPositionSelect).filterName()));
        }
    }

    class HomeRecyclerHolder extends RecyclerView.ViewHolder {

        ImageView filterImg;
        TextView filterName;

        public HomeRecyclerHolder(View itemView) {
            super(itemView);
            filterImg = (ImageView) itemView.findViewById(R.id.control_recycler_img);
            filterName = (TextView) itemView.findViewById(R.id.control_recycler_text);
        }
    }
}
