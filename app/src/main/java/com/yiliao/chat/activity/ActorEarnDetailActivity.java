package com.yiliao.chat.activity;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Intent;
import android.graphics.Point;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.shapes.OvalShape;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.listener.OnLoadMoreListener;
import com.scwang.smartrefresh.layout.listener.OnRefreshListener;
import com.yiliao.chat.R;
import com.yiliao.chat.adapter.ActorEarnDetailRecyclerAdapter;
import com.yiliao.chat.base.BaseActivity;
import com.yiliao.chat.base.BaseBean;
import com.yiliao.chat.base.BaseResponse;
import com.yiliao.chat.bean.ActorEarnDetailBean;
import com.yiliao.chat.bean.ActorEarnDetailListBean;
import com.yiliao.chat.bean.PageBean;
import com.yiliao.chat.constant.ChatApi;
import com.yiliao.chat.constant.Constant;
import com.yiliao.chat.dialog.DialogUitl;
import com.yiliao.chat.helper.ImageLoadHelper;
import com.yiliao.chat.myactivity.EstablishActivity;
import com.yiliao.chat.net.AjaxCallback;
import com.yiliao.chat.net.NetCode;
import com.yiliao.chat.util.DateUtil;
import com.yiliao.chat.util.DevicesUtil;
import com.yiliao.chat.util.ParamUtil;
import com.yiliao.chat.util.ToastUtil;
import com.zhy.http.okhttp.OkHttpUtils;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;

import butterknife.BindView;
import butterknife.OnClick;
import okhttp3.Call;

/*
 * Copyright (C) 2018
 * 版权所有
 *
 * 功能描述：主播贡献值详情
 * 作者：
 * 创建时间：2018/9/12
 *
 * 修改人：
 * 修改描述：
 * 修改日期
 */
public class ActorEarnDetailActivity extends BaseActivity {

    @BindView(R.id.content_rv)
    RecyclerView mContentRv;
    @BindView(R.id.refreshLayout)
    SmartRefreshLayout mRefreshLayout;
    @BindView(R.id.head_iv)
    ImageView mHeadIv;
    @BindView(R.id.nick_tv)
    TextView mNickTv;
    @BindView(R.id.total_tv)
    TextView mTotalTv;
    @BindView(R.id.today_tv)
    TextView mTodayTv;
    @BindView(R.id.right_text)
    TextView right_text;
    @BindView(R.id.time_start)
    EditText time_start;
    @BindView(R.id.time_end)
    EditText time_end;
    @BindView(R.id.weizhi)
    LinearLayout weizhi;
    @BindView(R.id.yuebo_total_tv)
    TextView yuebo_total_tv;
    private int mCurrentPage = 1;
    private List<ActorEarnDetailListBean> mFocusBeans = new ArrayList<>();
    private ActorEarnDetailRecyclerAdapter mAdapter;
    private int mActorId,guildId,totalGold;
    //popwindow的ui
    private View view;
    Dialog dialog;
    String times;
    @Override
    protected View getContentView() {


//        if (Constant.hideHomeNearAndNew()){
            return inflate(R.layout.yuebo_actor_earn_detail);
//        }else {
//            return inflate(R.layout.activity_actor_earn_detail_layout);
//        }

    }
    @OnClick( {R.id.right_text,R.id.time_start,R.id.time_end,R.id.sure_tv,R.id.onclick_ll})
    public void onClick(View view){
        switch (view.getId()){
            case R.id.sure_tv:
                showLoadingDialog();
                getEarnDetailList(mRefreshLayout, true, 1);
                break;
            case R.id.right_text:
                getPopwindow();
                break;
            case R.id.time_start:
                DialogUitl.showDatePickerDialog(mContext, new DialogUitl.DataPickerCallback() {
                    @Override
                    public void onConfirmClick(final String date) {
                        time_start.setText(date);
                    }
                });
                break;
            case R.id.time_end:
                DialogUitl.showDatePickerDialog(mContext, new DialogUitl.DataPickerCallback() {
                    @Override
                    public void onConfirmClick(final String date) {
                        time_end.setText(date);
                    }
                });
                break;
            case R.id.onclick_ll:
                Intent intent = new Intent(mContext, ActorInfoOneActivity.class);
                intent.putExtra(Constant.ACTOR_ID, mActorId);
                mContext.startActivity(intent);
//                Intent intent = new Intent(mContext, ModifyUserInfoActivity.class);
//                startActivity(intent);
                break;

        }
    }
    @Override
    protected void onContentAdded() {
        setTitle(R.string.actor_earn_detail_title);
        DateUtil date=new DateUtil();
        guildId=getIntent().getIntExtra("guildId",0);
        totalGold=getIntent().getIntExtra("totalGold",0);
        yuebo_total_tv.setText("总贡献值："+totalGold);
        time_start.setText(date.getDateToFormat("yyyy-MM-dd"));
        time_end.setText(date.getDateToFormat("yyyy-MM-dd"));

        if (Constant.hideHomeNearAndNew()){
            setRightImage(R.mipmap.icon_gengduo);
        }
        mActorId = getIntent().getIntExtra(Constant.ACTOR_ID, 0);
        initRecycler();
        if (mActorId > 0) {
            getActorInfo();
            getEarnDetailList(mRefreshLayout, true, 1);
        }
    }

    /**
     * 获取主播信息
     */
    private void getActorInfo() {
        Map<String, String> paramMap = new HashMap<>();
        paramMap.put("userId", getUserId());
        paramMap.put("anchorId", String.valueOf(mActorId));
        OkHttpUtils.post().url(ChatApi.GET_ANTHOR_TOTAL)
                .addParams("param", ParamUtil.getParam(paramMap))
                .build().execute(new AjaxCallback<BaseResponse<ActorEarnDetailBean>>() {
            @Override
            public void onResponse(BaseResponse<ActorEarnDetailBean> response, int id) {
                if (response != null && response.m_istatus == NetCode.SUCCESS) {
                    ActorEarnDetailBean detailBean = response.m_object;
                    if (detailBean != null) {

                        //头像
                        String handImg = detailBean.t_handImg;
                        if (!TextUtils.isEmpty(handImg)) {
                            int width = DevicesUtil.dp2px(mContext, 50);
                            int high = DevicesUtil.dp2px(mContext, 50);
                            ImageLoadHelper.glideShowCircleImageWithUrl(mContext, handImg, mHeadIv, width, high);
                        } else {
                            mHeadIv.setImageResource(R.drawable.default_head_img);
                        }
                        //昵称
                        String nick = detailBean.t_nickName;
                        if (!TextUtils.isEmpty(nick)) {
                            mNickTv.setText(nick);
                        }
                        //总共贡献值
                        String total = getResources().getString(R.string.earn_gold_des)
                                + detailBean.t_devote_value + getResources().getString(R.string.gold_des_one);
                        mTotalTv.setText(total);
                        //今日
                        String today = getResources().getString(R.string.today_earn_des)
                                + detailBean.toDay + getResources().getString(R.string.gold_des_one);
                        mTodayTv.setText(today);
                    }
                }
            }
        });
    }

    /**
     * 获取主播贡献明细列表
     */
    private void getEarnDetailList(final RefreshLayout refreshlayout, final boolean isRefresh, int page) {
        Map<String, String> paramMap = new HashMap<>();
        paramMap.put("userId", getUserId());
        paramMap.put("anchorId", String.valueOf(mActorId));
        paramMap.put("page", String.valueOf(page));
        paramMap.put("begin",time_start.getText().toString());
        paramMap.put("end",time_end.getText().toString());
        OkHttpUtils.post().url(ChatApi.GET_CONTRIBUTION_DETAIL)
                .addParams("param", ParamUtil.getParam(paramMap))
                .build().execute(new AjaxCallback<BaseResponse<PageBean<ActorEarnDetailListBean>>>() {
            @Override
            public void onResponse(BaseResponse<PageBean<ActorEarnDetailListBean>> response, int id) {
                if (isFinishing()) {
                    return;
                }
                dismissLoadingDialog();
                if (response != null && response.m_istatus == NetCode.SUCCESS) {
                    PageBean<ActorEarnDetailListBean> pageBean = response.m_object;
                    if (pageBean != null) {
                        List<ActorEarnDetailListBean> focusBeans = pageBean.data;
                        if (focusBeans != null) {
                            int size = focusBeans.size();
                            if (size>0){
                                //总共贡献值
                                String total = getResources().getString(R.string.earn_gold_des)
                                        + focusBeans.get(0).dayTotal + getResources().getString(R.string.gold_des_one);
                                mTotalTv.setText(total);
                            }else {
                                String total = getResources().getString(R.string.earn_gold_des)
                                        + 0 + getResources().getString(R.string.gold_des_one);
                                mTotalTv.setText(total);
                            }
                            if (isRefresh) {
                                mCurrentPage = 1;
                                mFocusBeans.clear();
                                mFocusBeans.addAll(focusBeans);

                                mAdapter.loadData(mFocusBeans);
                                if (mFocusBeans.size() > 0) {
                                    mRefreshLayout.setEnableRefresh(true);
                                } else {
                                    mRefreshLayout.setEnableRefresh(false);
                                }
                                refreshlayout.finishRefresh();
                                if (size >= 10) {//如果是刷新,且返回的数据大于等于10条,就可以load more
                                    refreshlayout.setEnableLoadMore(true);
                                }
                            } else {
                                mCurrentPage++;
                                mFocusBeans.addAll(focusBeans);
                                mAdapter.loadData(mFocusBeans);
                                if (size >= 10) {
                                    refreshlayout.finishLoadMore();
                                }
                            }
                            if (size < 10) {//如果数据返回少于10了,那么说明就没数据了
                                refreshlayout.finishLoadMoreWithNoMoreData();
                            }
                        }
                    } else {
                        ToastUtil.showToast(getApplicationContext(), R.string.system_error);
                        if (isRefresh) {
                            refreshlayout.finishRefresh();
                        } else {
                            refreshlayout.finishLoadMore();
                        }
                    }
                } else {
                    ToastUtil.showToast(getApplicationContext(), R.string.system_error);
                    if (isRefresh) {
                        refreshlayout.finishRefresh();
                    } else {
                        refreshlayout.finishLoadMore();
                    }
                }
            }

            @Override
            public void onError(Call call, Exception e, int id) {
                super.onError(call, e, id);
                dismissLoadingDialog();
                if (isRefresh) {
                    refreshlayout.finishRefresh();
                } else {
                    refreshlayout.finishLoadMore();
                }
            }
        });
    }

    /**
     * 初始化RecyclerView
     */
    private void initRecycler() {
        mRefreshLayout.setOnRefreshListener(new OnRefreshListener() {
            @Override
            public void onRefresh(@NonNull RefreshLayout refreshlayout) {
                getEarnDetailList(refreshlayout, true, 1);
            }
        });
        mRefreshLayout.setOnLoadMoreListener(new OnLoadMoreListener() {
            @Override
            public void onLoadMore(@NonNull RefreshLayout refreshlayout) {
                getEarnDetailList(refreshlayout, false, mCurrentPage + 1);
            }
        });

        LinearLayoutManager gridLayoutManager = new LinearLayoutManager(this);
        mContentRv.setLayoutManager(gridLayoutManager);
        mAdapter = new ActorEarnDetailRecyclerAdapter(this);
        mContentRv.setAdapter(mAdapter);
    }

    private Drawable getDrawable() {
        ShapeDrawable bgdrawable = new ShapeDrawable(new OvalShape());
        bgdrawable.getPaint().setColor(ActorEarnDetailActivity.this.getResources().getColor(android.R.color.transparent));
        return bgdrawable;
    }
    public void getPopwindow(){
        final Dialog mDialog = new Dialog(this, R.style.DialogStyle_new);
        @SuppressLint("InflateParams")
        View view = LayoutInflater.from(this).inflate(R.layout.remove_anchor_actor, null);
        mDialog.setContentView(view);
        Point outSize = new Point();
        getWindowManager().getDefaultDisplay().getSize(outSize);
        Window window = mDialog.getWindow();
        if (window != null) {
            WindowManager.LayoutParams params = window.getAttributes();
            params.width = outSize.x;
            window.setGravity(Gravity.BOTTOM); // 此处可以设置dialog显示的位置
            window.setWindowAnimations(R.style.BottomPopupAnimation); // 添加动画
        }
        TextView SignOut_Activity=view.findViewById(R.id.SignOut_Activity);
        TextView Cancel=view.findViewById(R.id.Cancel);
        mDialog.setCanceledOnTouchOutside(true);
        SignOut_Activity.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                removeAnchor();
                mDialog.dismiss();
            }
        });
        Cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mDialog.dismiss();
            }
        });
        if (!isFinishing()) {
            mDialog.show();
        }
    }
    public void removeAnchor(){
        Map<String, String> paramMap = new HashMap<>();
        paramMap.put("userId", getUserId());
        paramMap.put("guildId",String.valueOf(guildId));
        paramMap.put("coverUserId", String.valueOf(mActorId));
        OkHttpUtils.post().url(ChatApi.ROMOVE_ANCHOR)
                .addParams("param", ParamUtil.getParam(paramMap))
                .build().execute(new AjaxCallback<BaseResponse<BaseBean>>() {
            @Override
            public void onResponse(BaseResponse<BaseBean> response, int id) {
                if (isFinishing()) {
                    return;
                }
                if (response != null && response.m_istatus == NetCode.SUCCESS) {
                    ToastUtil.show(response.m_strMessage);
                    finish();
                }

            }

            @Override
            public void onError(Call call, Exception e, int id) {
                super.onError(call, e, id);
                ToastUtil.show("服务异常");
            }
        });
    }


}
