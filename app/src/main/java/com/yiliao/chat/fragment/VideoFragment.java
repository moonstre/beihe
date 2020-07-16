package com.yiliao.chat.fragment;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.listener.OnLoadMoreListener;
import com.scwang.smartrefresh.layout.listener.OnRefreshListener;
import com.yiliao.chat.R;
import com.yiliao.chat.adapter.HomeVideoRecyclerAdapter;
import com.yiliao.chat.base.BaseFragment;
import com.yiliao.chat.base.BaseResponse;
import com.yiliao.chat.bean.PageBean;
import com.yiliao.chat.bean.VideoBean;
import com.yiliao.chat.constant.ChatApi;
import com.yiliao.chat.net.AjaxCallback;
import com.yiliao.chat.net.NetCode;
import com.yiliao.chat.util.ParamUtil;
import com.yiliao.chat.util.ToastUtil;
import com.zhy.http.okhttp.OkHttpUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import okhttp3.Call;

/*
 * Copyright (C) 2018
 * 版权所有
 *
 * 功能描述：短视频页面Fragment
 * 作者：
 * 创建时间：2018/6/14
 *
 * 修改人：
 * 修改描述：
 * 修改日期
 */
public class VideoFragment extends BaseFragment implements View.OnClickListener {

    public VideoFragment() {

    }

    private boolean showTitle;
    private SmartRefreshLayout mRefreshLayout;
    private HomeVideoRecyclerAdapter mAdapter;
    private List<VideoBean> mFocusBeans = new ArrayList<>();
    private int mCurrentPage = 1;

    private TextView mAllTv;

    private TextView mFreeTv;

    private TextView mChargeTv;

    //请求类型 -1：全部 0.免费  1.私密
    private final int ALL = -1;
    private final int FREE = 0;
    private final int CHARGE = 1;
    private int mQueryType = -1;

    public void setShowTitle(boolean showTitle) {
        this.showTitle = showTitle;
    }

    @Override
    protected int initLayout() {
        return R.layout.fragment_date_layout;
    }

    @Override
    protected void initView(View view, Bundle savedInstanceState) {
        if (showTitle) {
            view.findViewById(R.id.layoutVideoTitle).setVisibility(View.VISIBLE);
        }
        RecyclerView mVideoRv = view.findViewById(R.id.video_rv);
        mRefreshLayout = view.findViewById(R.id.refreshLayout);
        mRefreshLayout.setOnRefreshListener(new OnRefreshListener() {
            @Override
            public void onRefresh(@NonNull RefreshLayout refreshlayout) {
                getVideoList(refreshlayout, true, 1);
            }
        });
        mRefreshLayout.setOnLoadMoreListener(new OnLoadMoreListener() {
            @Override
            public void onLoadMore(@NonNull RefreshLayout refreshlayout) {
                getVideoList(refreshlayout, false, mCurrentPage + 1);
            }
        });

        mAllTv = view.findViewById(R.id.all_tv);
        mFreeTv = view.findViewById(R.id.free_tv);
        mChargeTv = view.findViewById(R.id.charge_tv);

        mAllTv.setOnClickListener(this);
        mFreeTv.setOnClickListener(this);
        mChargeTv.setOnClickListener(this);

        GridLayoutManager gridLayoutManager = new GridLayoutManager(getContext(), 2);
        mVideoRv.setLayoutManager(gridLayoutManager);
        mAdapter = new HomeVideoRecyclerAdapter(mContext);
        mVideoRv.setAdapter(mAdapter);
    }

    @Override
    protected void onFirstVisible() {
        switchSelect(ALL);
    }

    /**
     * 获取主播视频照片
     */
    private void getVideoList(final RefreshLayout refreshlayout, final boolean isRefresh, int page) {
        Map<String, String> paramMap = new HashMap<>();
        paramMap.put("userId", mContext.getUserId());
        paramMap.put("page", String.valueOf(page));
        paramMap.put("queryType", String.valueOf(mQueryType));
        OkHttpUtils.post().url(ChatApi.GET_VIDEO_LIST)
                .addParams("param", ParamUtil.getParam(paramMap))
                .build().execute(new AjaxCallback<BaseResponse<PageBean<VideoBean>>>() {
            @Override
            public void onResponse(BaseResponse<PageBean<VideoBean>> response, int id) {
                if (response != null && response.m_istatus == NetCode.SUCCESS) {
                    PageBean<VideoBean> pageBean = response.m_object;
                    if (pageBean != null) {
                        List<VideoBean> focusBeans = pageBean.data;
                        if (focusBeans != null) {
                            int size = focusBeans.size();
                            if (isRefresh) {
                                mCurrentPage = 1;
                                mFocusBeans.clear();
                                mFocusBeans.addAll(focusBeans);
                                mAdapter.loadData(mFocusBeans);
                                if (mFocusBeans.size() > 0) {
                                    mRefreshLayout.setEnableRefresh(true);
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
                    }
                } else {
                    ToastUtil.showToast(getContext(), R.string.system_error);
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
                ToastUtil.showToast(getContext(), R.string.system_error);
                if (isRefresh) {
                    refreshlayout.finishRefresh();
                } else {
                    refreshlayout.finishLoadMore();
                }
            }
        });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.all_tv: {//全部
                switchSelect(ALL);
                break;
            }
            case R.id.free_tv: {//免费
                switchSelect(FREE);
                break;
            }
            case R.id.charge_tv: {//收费
                switchSelect(CHARGE);
                break;
            }
        }
    }

    /**
     * 切换选中
     */
    private void switchSelect(int position) {
        try {
            if (position == ALL) {
                if (mAllTv.isSelected()) {
                    return;
                }

                mAllTv.setSelected(true);

                mFreeTv.setSelected(false);

                mChargeTv.setSelected(false);

                mQueryType = ALL;
                if (mRefreshLayout != null) {
                    mRefreshLayout.autoRefresh();
                }
            } else if (position == FREE) {
                if (mFreeTv.isSelected()) {
                    return;
                }
                mFreeTv.setSelected(true);

                mAllTv.setSelected(false);

                mChargeTv.setSelected(false);

                mQueryType = FREE;
                if (mRefreshLayout != null) {
                    mRefreshLayout.autoRefresh();
                }
            } else if (position == CHARGE) {
                if (mChargeTv.isSelected()) {
                    return;
                }
                mChargeTv.setSelected(true);

                mFreeTv.setSelected(false);

                mAllTv.setSelected(false);

                mQueryType = CHARGE;
                if (mRefreshLayout != null) {
                    mRefreshLayout.autoRefresh();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
