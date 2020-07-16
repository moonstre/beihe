package com.yiliao.chat.adapter;

import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.yiliao.chat.R;

import java.util.List;

public class ImChatFaceAdapter extends RecyclerView.Adapter<ImChatFaceAdapter.Vh> {

    private List<String> mList;
    private LayoutInflater mInflater;
    private View.OnClickListener mOnClickListener;

    public ImChatFaceAdapter(List<String> list, LayoutInflater inflater, final ImChatFacePagerAdapter.OnFaceClickListener onFaceClickListener) {
        mList = list;
        mInflater = inflater;
        mOnClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Object tag = v.getTag();
                if (tag != null && onFaceClickListener != null) {
                    String str = (String) v.getTag();
                    if (!TextUtils.isEmpty(str)) {
                        if ("<".equals(str)) {
                            onFaceClickListener.onFaceDeleteClick();
                        } else {
                            onFaceClickListener.onFaceClick(str, v.getId());
                        }
                    }
                }
            }
        };
    }

    @Override
    public Vh onCreateViewHolder(ViewGroup parent, int viewType) {
        return new Vh(mInflater.inflate(R.layout.item_list_face, parent, false));
    }

    @Override
    public void onBindViewHolder(Vh vh, int position) {
        vh.setData(mList.get(position));
    }

    @Override
    public int getItemCount() {
        return mList.size();
    }

    class Vh extends RecyclerView.ViewHolder {
        RelativeLayout layoutFace;
        TextView tvFace;
        ImageView ivFace;

        public Vh(View itemView) {
            super(itemView);
            ivFace = itemView.findViewById(R.id.ivFace);
            layoutFace = itemView.findViewById(R.id.layoutFace);
            tvFace = itemView.findViewById(R.id.tvFace);
            layoutFace.setOnClickListener(mOnClickListener);
        }

        void setData(String str) {
            layoutFace.setTag(str);
            if (!TextUtils.isEmpty(str)) {
                layoutFace.setClickable(true);
                if ("<".equals(str)) {
                    ivFace.setVisibility(View.VISIBLE);
                    tvFace.setVisibility(View.GONE);
                } else {
                    ivFace.setVisibility(View.GONE);
                    tvFace.setVisibility(View.VISIBLE);
                    tvFace.setText(str);
                }
            } else {
                ivFace.setVisibility(View.GONE);
                tvFace.setVisibility(View.GONE);
                layoutFace.setClickable(false);
            }
        }
    }
}
