package com.yiliao.chat.adapter;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.yiliao.chat.R;
import com.yiliao.chat.activity.ActiveCommentActivity;
import com.yiliao.chat.activity.ActorInfoOneActivity;
import com.yiliao.chat.activity.ActorVideoPlayActivity;
import com.yiliao.chat.activity.PhotoActivity;
import com.yiliao.chat.activity.PhotoViewActivity;
import com.yiliao.chat.base.BaseActivity;
import com.yiliao.chat.base.BaseResponse;
import com.yiliao.chat.bean.ActiveBean;
import com.yiliao.chat.bean.ActiveFileBean;
import com.yiliao.chat.constant.ChatApi;
import com.yiliao.chat.constant.Constant;
import com.yiliao.chat.helper.ImageLoadHelper;
import com.yiliao.chat.net.AjaxCallback;
import com.yiliao.chat.net.NetCode;
import com.yiliao.chat.util.DevicesUtil;
import com.yiliao.chat.util.ParamUtil;
import com.yiliao.chat.util.TimeUtil;
import com.yiliao.chat.util.ToastUtil;
import com.yiliao.chat.view.ExpandTextView;
import com.zhy.http.okhttp.OkHttpUtils;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import okhttp3.Call;

/*
 * Copyright (C) 2018
 * 版权所有
 *
 * 功能描述  个人中心动态RecyclerView的Adapter
 * 作者：
 * 创建时间：2018/8/29
 *
 * 修改人：
 * 修改描述：
 * 修改日期
 */
public class UserActiveRecyclerAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private BaseActivity mContext;
    private List<ActiveBean<ActiveFileBean>> mBeans = new ArrayList<>();

    public UserActiveRecyclerAdapter(BaseActivity context) {
        mContext = context;
    }

    public void loadData(List<ActiveBean<ActiveFileBean>> beans) {
        for (ActiveBean<ActiveFileBean> bean : beans) {
            List<ActiveFileBean> fileBeans = bean.dynamicFiles;
            for (ActiveFileBean fileBean : fileBeans) {
                if (fileBean.t_gold > 0) {
                    fileBean.t_gold = 0;
                }
            }
        }
        mBeans = beans;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(mContext).inflate(R.layout.item_user_active_recycler_layout,
                parent, false);
        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        final ActiveBean<ActiveFileBean> bean = mBeans.get(position);
        final int mPosition = position;
        final MyViewHolder mHolder = (MyViewHolder) holder;
        if (bean != null) {
            //头像
            final String handImg = bean.t_handImg;
            if (!TextUtils.isEmpty(handImg)) {
                //计算头像resize
                int smallOverWidth = DevicesUtil.dp2px(mContext, 40);
                int smallOverHeight = DevicesUtil.dp2px(mContext, 40);
                ImageLoadHelper.glideShowCircleImageWithUrl(mContext, handImg, mHolder.mHeadIv,
                        smallOverWidth, smallOverHeight);
            } else {
                mHolder.mHeadIv.setImageResource(R.drawable.default_head_img);
            }
            //昵称
            final String nick = bean.t_nickName;
            if (!TextUtils.isEmpty(nick)) {
                mHolder.mNickTv.setText(nick);
            }
            //时间
            long time = bean.t_create_time;
            if (time > 0) {
                mHolder.mTimeTv.setText(TimeUtil.getTimeStr(time));
                mHolder.mTimeTv.setVisibility(View.VISIBLE);
            } else {
                mHolder.mTimeTv.setVisibility(View.GONE);
            }
            //位置
            if (!TextUtils.isEmpty(bean.t_address)) {
                mHolder.mPositionTv.setText(bean.t_address);
                mHolder.mPositionTv.setVisibility(View.VISIBLE);
            } else {
                mHolder.mPositionTv.setVisibility(View.GONE);
            }
            //点赞
            mHolder.mHeartTv.setText(String.valueOf(bean.praiseCount));
            //是否点赞 0 未点赞 1 已点赞
            if (bean.isPraise == 1) {//已点赞
                mHolder.mHeartIv.setSelected(true);
            } else {
                mHolder.mHeartIv.setSelected(false);
            }
            //评论
            mHolder.mCommentTv.setText(String.valueOf(bean.commentCount));
            //-------处理图片、文字内容------------
            dealContent(mHolder, bean);
            //------------------点击事件---------------
            //评论
            mHolder.mCommentLl.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (bean.dynamicId > 0) {
                        Intent intent = new Intent(mContext, ActiveCommentActivity.class);
                        intent.putExtra(Constant.ACTIVE_ID, bean.dynamicId);
                        intent.putExtra(Constant.ACTOR_ID, bean.t_id);
                        intent.putExtra(Constant.COMMENT_NUMBER, bean.commentCount);
                        mContext.startActivity(intent);
                    }
                }
            });
            //点赞
            mHolder.mHeartLl.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (bean.dynamicId > 0 && !mHolder.mHeartIv.isSelected()) {
                        addHeart(mHolder.mHeartTv, mHolder.mHeartIv, bean.dynamicId);
                    }
                }
            });
            //跳转到信息
            mHolder.mHeadIv.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (bean.t_id > 0) {
                        Intent intent = new Intent(mContext, ActorInfoOneActivity.class);
                        intent.putExtra(Constant.ACTOR_ID, bean.t_id);
                        mContext.startActivity(intent);
                    }
                }
            });
            //删除
            mHolder.mDeleteTv.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mContext.showLoadingDialog();
                    deleteActive(mPosition, bean.dynamicId);
                }
            });
        }
    }

    /**
     * 删除
     */
    private void deleteActive(final int mPosition, int dynamicId) {
        Map<String, String> paramMap = new HashMap<>();
        paramMap.put("userId", mContext.getUserId());
        paramMap.put("dynamicId", String.valueOf(dynamicId));
        OkHttpUtils.post().url(ChatApi.DEL_DYNAMIC)
                .addParams("param", ParamUtil.getParam(paramMap))
                .build().execute(new AjaxCallback<BaseResponse>() {
            @Override
            public void onResponse(BaseResponse response, int id) {
                mContext.dismissLoadingDialog();
                if (response != null && response.m_istatus == NetCode.SUCCESS) {
                    ToastUtil.showToast(mContext, R.string.delete_success);
                    if (mBeans != null && mBeans.size() > mPosition) {
                        mBeans.remove(mPosition);
                        notifyDataSetChanged();
                    }
                } else {
                    ToastUtil.showToast(mContext, R.string.delete_fail);
                }
            }

            @Override
            public void onError(Call call, Exception e, int id) {
                super.onError(call, e, id);
                ToastUtil.showToast(mContext, R.string.delete_fail);
                mContext.dismissLoadingDialog();
            }
        });
    }

    /**
     * 添加点赞
     */
    private void addHeart(final TextView heart_tv, final ImageView heart_iv, int dynamicId) {
        Map<String, String> paramMap = new HashMap<>();
        paramMap.put("userId", mContext.getUserId());
        paramMap.put("dynamicId", String.valueOf(dynamicId));
        OkHttpUtils.post().url(ChatApi.GIVE_THE_THUMB_UP)
                .addParams("param", ParamUtil.getParam(paramMap))
                .build().execute(new AjaxCallback<BaseResponse>() {
            @Override
            public void onResponse(BaseResponse response, int id) {
                if (response != null && response.m_istatus == NetCode.SUCCESS) {
                    heart_iv.setSelected(true);
                    String number = heart_tv.getText().toString().trim();
                    String last = String.valueOf(Integer.parseInt(number) + 1);
                    heart_tv.setText(last);
                    ToastUtil.showToast(mContext, R.string.heart_success);
                }
            }
        });
    }

    /**
     * 处理图片
     */
    private void dealContent(final MyViewHolder mHolder, final ActiveBean<ActiveFileBean> bean) {
        //---------------文字---------------------------
        String contentText = bean.t_content;
        if (!TextUtils.isEmpty(contentText)) {
            mHolder.mTextContentLl.setVisibility(View.VISIBLE);
            mHolder.mExpandTv.setText(contentText, false, new ExpandTextView.Callback() {
                @Override
                public void onExpand() {
                    // 展开状态，比如：显示“收起”按钮
                    mHolder.mSeeMoreTv.setVisibility(View.VISIBLE);
                    mHolder.mSeeMoreTv.setText(mContext.getResources().getString(R.string.collapse));
                }

                @Override
                public void onCollapse() {
                    // 收缩状态，比如：显示“查看”按钮
                    mHolder.mSeeMoreTv.setVisibility(View.VISIBLE);
                    mHolder.mSeeMoreTv.setText(mContext.getResources().getString(R.string.see_all));
                }

                @Override
                public void onLoss() {
                    // 不满足展开的条件，比如：隐藏“全文”按钮
                    mHolder.mSeeMoreTv.setVisibility(View.GONE);
                }
            });
            //点击收起
            mHolder.mSeeMoreTv.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String text = mHolder.mSeeMoreTv.getText().toString().trim();
                    //查看全部
                    if (text.equals(mContext.getResources().getString(R.string.see_all))) {
                        //切换状态
                        mHolder.mExpandTv.setChanged(true);
                        mHolder.mSeeMoreTv.setText(mContext.getResources().getString(R.string.collapse));
                    } else {
                        mHolder.mExpandTv.setChanged(false);
                        mHolder.mSeeMoreTv.setText(mContext.getResources().getString(R.string.see_all));
                    }
                }
            });
        } else {
            mHolder.mTextContentLl.setVisibility(View.GONE);
        }
        //--------------图片视频-----------------------
        final List<ActiveFileBean> fileBeans = bean.dynamicFiles;
        if (fileBeans != null && fileBeans.size() > 0) {
            mHolder.mImageFl.setVisibility(View.VISIBLE);
            //-------------------如果只有一个文件-------------------
            if (fileBeans.size() == 1) {
                final ActiveFileBean fileBean = fileBeans.get(0);
                mHolder.mOneImageFl.setVisibility(View.VISIBLE);
                mHolder.mTwoImageLl.setVisibility(View.GONE);
                mHolder.mThreeRv.setVisibility(View.GONE);

                //显示图片
                String imgUrl;
                int overWidth = DevicesUtil.dp2px(mContext, 180);
                int overHeight = DevicesUtil.dp2px(mContext, 240);
                if (fileBean.t_file_type == 1) {//视频
                    imgUrl = fileBean.t_cover_img_url;
                } else {//图片
                    imgUrl = fileBean.t_file_url;
                }
                if (!TextUtils.isEmpty(imgUrl)) {//0.未消费 1.已消费
                    mHolder.mOneLockFl.setVisibility(View.GONE);
                    ImageLoadHelper.glideShowImageWithUrl(mContext, imgUrl, mHolder.mOneImageIv,
                            overWidth, overHeight);
                } else {
                    mHolder.mOneImageFl.setVisibility(View.GONE);
                }
                //视频时长
                if (!TextUtils.isEmpty(fileBean.t_video_time) && fileBean.t_file_type == 1) {
                    mHolder.mVideoTimeTv.setVisibility(View.VISIBLE);
                    mHolder.mVideoTimeTv.setText(fileBean.t_video_time);
                } else {
                    mHolder.mVideoTimeTv.setVisibility(View.GONE);
                }
                //点击事件
                mHolder.mOneImageFl.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (fileBean.t_file_type == 1) {//视频
                            Intent intent = new Intent(mContext, ActorVideoPlayActivity.class);
                            intent.putExtra(Constant.FROM_WHERE, Constant.FROM_ACTIVE);
                            intent.putExtra(Constant.VIDEO_URL, fileBean.t_file_url);
                            intent.putExtra(Constant.FILE_ID, fileBean.t_id);
                            intent.putExtra(Constant.ACTOR_ID, bean.t_id);
                            intent.putExtra(Constant.COVER_URL, fileBean.t_cover_img_url);
                            mContext.startActivity(intent);
                        } else {//图片
                            Intent intent = new Intent(mContext, PhotoActivity.class);
                            intent.putExtra(Constant.IMAGE_URL, fileBean.t_file_url);
                            mContext.startActivity(intent);
                        }
                    }
                });
            } else if (fileBeans.size() == 2) {//-------------------2个文件(图片)------------------
                mHolder.mOneImageFl.setVisibility(View.GONE);
                mHolder.mTwoImageLl.setVisibility(View.VISIBLE);
                mHolder.mThreeRv.setVisibility(View.GONE);
                //第一张-------
                final ActiveFileBean fileBean = fileBeans.get(0);
                int overWidth = DevicesUtil.dp2px(mContext, 126);
                int overHeight = DevicesUtil.dp2px(mContext, 135);
                if (!TextUtils.isEmpty(fileBean.t_file_url)) {
                    mHolder.mTwoImageOneIv.setVisibility(View.VISIBLE);
                    mHolder.mTwoLockOneIv.setVisibility(View.GONE);
                    ImageLoadHelper.glideShowImageWithUrl(mContext, fileBean.t_file_url, mHolder.mTwoImageOneIv,
                            overWidth, overHeight);
                } else {
                    mHolder.mTwoLockOneIv.setVisibility(View.GONE);
                    mHolder.mTwoImageOneIv.setVisibility(View.GONE);
                }
                //第二张---------
                final ActiveFileBean fileBeanTwo = fileBeans.get(1);
                if (!TextUtils.isEmpty(fileBeanTwo.t_file_url)) {
                    mHolder.mTwoImageTwoIv.setVisibility(View.VISIBLE);
                    mHolder.mTwoLockTwoIv.setVisibility(View.GONE);
                    ImageLoadHelper.glideShowImageWithUrl(mContext, fileBeanTwo.t_file_url, mHolder.mTwoImageTwoIv,
                            overWidth, overHeight);
                } else {
                    mHolder.mTwoLockTwoIv.setVisibility(View.GONE);
                    mHolder.mTwoImageTwoIv.setVisibility(View.GONE);
                }
                //点击事件
                //第一张
                mHolder.mTwoImageOneFl.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(mContext, PhotoViewActivity.class);
                        intent.putExtra(Constant.IMAGE_URL, (Serializable) fileBeans);
                        intent.putExtra(Constant.CLICK_POSITION, 0);
                        intent.putExtra(Constant.ACTOR_ID, bean.t_id);
                        mContext.startActivity(intent);
                    }
                });
                //第二张
                mHolder.mTwoImageTwoFl.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(mContext, PhotoViewActivity.class);
                        intent.putExtra(Constant.IMAGE_URL, (Serializable) fileBeans);
                        intent.putExtra(Constant.CLICK_POSITION, 1);
                        mContext.startActivity(intent);
                    }
                });
            } else {//-----------------大于等于3个文件(图片)-----------------
                mHolder.mOneImageFl.setVisibility(View.GONE);
                mHolder.mTwoImageLl.setVisibility(View.GONE);
                mHolder.mThreeRv.setVisibility(View.VISIBLE);
                GridLayoutManager layoutManager = new GridLayoutManager(mContext, 3);
                ActiveImagesRecyclerAdapter adapter = new ActiveImagesRecyclerAdapter(mContext);
                mHolder.mThreeRv.setLayoutManager(layoutManager);
                mHolder.mThreeRv.setAdapter(adapter);
                adapter.setOnImageItemClickListener(new ActiveImagesRecyclerAdapter.OnImageItemClickListener() {
                    @Override
                    public void onImageItemClick(int position, ActiveFileBean bean) {
                        Intent intent = new Intent(mContext, PhotoViewActivity.class);
                        intent.putExtra(Constant.IMAGE_URL, (Serializable) fileBeans);
                        intent.putExtra(Constant.CLICK_POSITION, position);
                        mContext.startActivity(intent);
                    }
                });
                adapter.loadData(fileBeans);
            }
        } else {
            mHolder.mImageFl.setVisibility(View.GONE);
        }
    }

    @Override
    public int getItemCount() {
        return mBeans != null ? mBeans.size() : 0;
    }

    class MyViewHolder extends RecyclerView.ViewHolder {

        ImageView mHeadIv;
        TextView mNickTv;
        ImageView mGenderIv;
        TextView mAgeTv;
        TextView mTimeTv;
        //内容相关
        TextView mContentTv;
        FrameLayout mImageFl;
        //一张图
        FrameLayout mOneLockFl;
        FrameLayout mOneImageFl;
        ImageView mOneImageIv;
        //2张图
        LinearLayout mTwoImageLl;
        FrameLayout mTwoImageOneFl;
        ImageView mTwoImageOneIv;
        ImageView mTwoLockOneIv;
        FrameLayout mTwoImageTwoFl;
        ImageView mTwoImageTwoIv;
        ImageView mTwoLockTwoIv;
        //大于等于三张
        RecyclerView mThreeRv;
        //点赞
        View mHeartLl;
        ImageView mHeartIv;
        TextView mHeartTv;
        //评论
        View mCommentLl;
        ImageView mCommentIv;
        TextView mCommentTv;
        ImageView mMoreIv;
        //位置
        TextView mPositionTv;
        //查看更多
        LinearLayout mTextContentLl;
        TextView mSeeMoreTv;
        ExpandTextView mExpandTv;
        //视频时长
        TextView mVideoTimeTv;
        //删除
        TextView mDeleteTv;

        MyViewHolder(View itemView) {
            super(itemView);
            mHeadIv = itemView.findViewById(R.id.head_iv);
            mNickTv = itemView.findViewById(R.id.nick_tv);
            mGenderIv = itemView.findViewById(R.id.gender_iv);
            mAgeTv = itemView.findViewById(R.id.age_tv);
            mTimeTv = itemView.findViewById(R.id.time_tv);
            mContentTv = itemView.findViewById(R.id.content_tv);
            mImageFl = itemView.findViewById(R.id.image_fl);
            mOneLockFl = itemView.findViewById(R.id.one_lock_fl);
            mOneImageFl = itemView.findViewById(R.id.one_image_fl);
            mOneImageIv = itemView.findViewById(R.id.one_image_iv);
            mTwoImageLl = itemView.findViewById(R.id.two_image_ll);
            mTwoImageOneIv = itemView.findViewById(R.id.two_image_one_iv);
            mTwoLockOneIv = itemView.findViewById(R.id.two_lock_one_iv);
            mTwoImageTwoIv = itemView.findViewById(R.id.two_image_two_iv);
            mTwoLockTwoIv = itemView.findViewById(R.id.two_lock_two_iv);
            mThreeRv = itemView.findViewById(R.id.three_rv);
            mHeartLl = itemView.findViewById(R.id.heart_ll);
            mHeartIv = itemView.findViewById(R.id.heart_iv);
            mHeartTv = itemView.findViewById(R.id.heart_tv);
            mCommentLl = itemView.findViewById(R.id.comment_ll);
            mCommentIv = itemView.findViewById(R.id.comment_iv);
            mCommentTv = itemView.findViewById(R.id.comment_tv);
            mMoreIv = itemView.findViewById(R.id.more_iv);
            mPositionTv = itemView.findViewById(R.id.position_tv);
            mTextContentLl = itemView.findViewById(R.id.text_content_ll);
            mSeeMoreTv = itemView.findViewById(R.id.see_more_tv);
            mExpandTv = itemView.findViewById(R.id.expand_tv);
            mVideoTimeTv = itemView.findViewById(R.id.video_time_tv);
            mTwoImageOneFl = itemView.findViewById(R.id.two_image_one_fl);
            mTwoImageTwoFl = itemView.findViewById(R.id.two_image_two_fl);
            mDeleteTv = itemView.findViewById(R.id.delete_tv);
        }
    }

}
