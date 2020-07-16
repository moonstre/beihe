package com.yiliao.chat.fragment.invite;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.graphics.Point;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;

import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.listener.OnLoadMoreListener;
import com.scwang.smartrefresh.layout.listener.OnRefreshListener;
import com.yiliao.chat.R;
import com.yiliao.chat.adapter.TudiRecyclerAdapter;
import com.yiliao.chat.adapter.UserCapitalRecyclerAdapter;
import com.yiliao.chat.base.BaseCompactFragment;
import com.yiliao.chat.base.BaseResponse;
import com.yiliao.chat.bean.PageBean;
import com.yiliao.chat.bean.TudiBean;
import com.yiliao.chat.bean.UserCapitalBean;
import com.yiliao.chat.constant.ChatApi;
import com.yiliao.chat.helper.ImageLoadHelper;
import com.yiliao.chat.listener.OnItemClickListener;
import com.yiliao.chat.net.AjaxCallback;
import com.yiliao.chat.net.NetCode;
import com.yiliao.chat.util.DevicesUtil;
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
 * 功能描述：徒孙
 * 作者：
 * 创建时间：2018/10/19
 *
 * 修改人：
 * 修改描述：
 * 修改日期
 */
public class TusunFragment extends BaseCompactFragment {

    public TusunFragment() {

    }

    private SmartRefreshLayout mRefreshLayout;
    private TudiRecyclerAdapter mAdapter;
    private List<TudiBean> mFocusBeans = new ArrayList<>();
    private int mCurrentPage = 1;

    @Override
    protected int initLayout() {
        return R.layout.fragment_tusun_layout;
    }

    @Override
    protected void initView(View view, Bundle savedInstanceState) {
        RecyclerView mContentRv = view.findViewById(R.id.content_rv);
        mRefreshLayout = view.findViewById(R.id.refreshLayout);
        mRefreshLayout.setOnRefreshListener(new OnRefreshListener() {
            @Override
            public void onRefresh(@NonNull RefreshLayout refreshlayout) {
                getMyTudi(refreshlayout, true, 1);
            }
        });
        mRefreshLayout.setOnLoadMoreListener(new OnLoadMoreListener() {
            @Override
            public void onLoadMore(@NonNull RefreshLayout refreshlayout) {
                getMyTudi(refreshlayout, false, mCurrentPage + 1);
            }
        });

        LinearLayoutManager gridLayoutManager = new LinearLayoutManager(getContext());
        mContentRv.setLayoutManager(gridLayoutManager);
        mAdapter = new TudiRecyclerAdapter(mContext);
        mContentRv.setAdapter(mAdapter);

        mAdapter.setOnItemClickListener(new OnItemClickListener<TudiBean>() {
            @Override
            public void onItemClick(TudiBean bean, int position) {

            }
        });
    }

    @Override
    protected void onFirstVisible() {
        getMyTudi(mRefreshLayout, true, 1);
    }

    /**
     * 获取我的徒弟 徒孙
     */
    private void getMyTudi(final RefreshLayout refreshlayout, final boolean isRefresh, int page) {
        Map<String, String> paramMap = new HashMap<>();
        paramMap.put("userId", getUserId());
        paramMap.put("page", String.valueOf(page));
        paramMap.put("type", String.valueOf("2"));//1.一级推广用户 2.二级推广用户
        OkHttpUtils.post().url(ChatApi.GET_SHARE_USER_LIST)
                .addParams("param", ParamUtil.getParam(paramMap))
                .build().execute(new AjaxCallback<BaseResponse<PageBean<TudiBean>>>() {
            @Override
            public void onResponse(BaseResponse<PageBean<TudiBean>> response, int id) {
                if (mContext.isFinishing()) {
                    return;
                }
                if (response != null && response.m_istatus == NetCode.SUCCESS) {
                    PageBean<TudiBean> pageBean = response.m_object;
                    if (pageBean != null) {
                        List<TudiBean> focusBeans = pageBean.data;
                        if (focusBeans != null) {
                            int size = focusBeans.size();
                            if (isRefresh) {
                                mCurrentPage = 1;
                                mFocusBeans.clear();
                                mFocusBeans.addAll(focusBeans);
                                mAdapter.loadData(mFocusBeans);
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
                        ToastUtil.showToast(mContext, R.string.system_error);
                        if (isRefresh) {
                            refreshlayout.finishRefresh();
                        } else {
                            refreshlayout.finishLoadMore();
                        }
                    }
                } else {
                    ToastUtil.showToast(mContext, R.string.system_error);
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
                ToastUtil.showToast(mContext, R.string.system_error);
            }
        });
    }

}
