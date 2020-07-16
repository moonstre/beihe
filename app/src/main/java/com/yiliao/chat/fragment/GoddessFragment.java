package com.yiliao.chat.fragment;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.listener.OnLoadMoreListener;
import com.scwang.smartrefresh.layout.listener.OnRefreshListener;
import com.yiliao.chat.R;
import com.yiliao.chat.adapter.NewManRecyclerAdapter;
import com.yiliao.chat.base.BaseFragment;
import com.yiliao.chat.base.BaseResponse;
import com.yiliao.chat.bean.GirlListBean;
import com.yiliao.chat.bean.PageBean;
import com.yiliao.chat.constant.ChatApi;
import com.yiliao.chat.net.AjaxCallback;
import com.yiliao.chat.net.NetCode;
import com.yiliao.chat.util.ParamUtil;
import com.zhy.http.okhttp.OkHttpUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import okhttp3.Call;

/**
 *
 */
public class GoddessFragment extends BaseFragment {
    //0 女神；1 男神
    private int sex = 0;

    public GoddessFragment() {

    }

    public void setSex(int sex) {
        this.sex = sex;
    }

    private SmartRefreshLayout mRefreshLayout;
    private NewManRecyclerAdapter mAdapter;
    private List<GirlListBean> mGirlListBeans = new ArrayList<>();
    private int mCurrentPage = 1;

    @Override
    protected int initLayout() {
        return R.layout.fragment_new_man_layout;
    }

    @Override
    protected void initView(View view, Bundle savedInstanceState) {
        RecyclerView mContentRv = view.findViewById(R.id.content_rv);
        mRefreshLayout = view.findViewById(R.id.refreshLayout);
        mRefreshLayout.setOnRefreshListener(new OnRefreshListener() {
            @Override
            public void onRefresh(@NonNull RefreshLayout refreshlayout) {
                getNewManList(refreshlayout, true, 1);
            }
        });
        mRefreshLayout.setOnLoadMoreListener(new OnLoadMoreListener() {
            @Override
            public void onLoadMore(@NonNull RefreshLayout refreshlayout) {
                getNewManList(refreshlayout, false, mCurrentPage + 1);
            }
        });

        GridLayoutManager gridLayoutManager = new GridLayoutManager(mContext, 2);
        mContentRv.setLayoutManager(gridLayoutManager);
        mAdapter = new NewManRecyclerAdapter(mContext);
        mContentRv.setAdapter(mAdapter);
    }

    @Override
    protected void onFirstVisible() {
        getNewManList(mRefreshLayout, true, 1);
    }

    /**
     * 获取新人列表
     */
    private void getNewManList(final RefreshLayout refreshlayout, final boolean isRefresh, int page) {
        Map<String, String> paramMap = new HashMap<>();
        paramMap.put("userId", mContext.getUserId());
        paramMap.put("page", String.valueOf(page));
        paramMap.put("sex", String.valueOf(sex));
        OkHttpUtils.post().url(ChatApi.GET_GODDESS_LIST)
                .addParams("param", ParamUtil.getParam(paramMap))
                .build().execute(new AjaxCallback<BaseResponse<PageBean<GirlListBean>>>() {
            @Override
            public void onResponse(BaseResponse<PageBean<GirlListBean>> response, int id) {
                if (response != null && response.m_istatus == NetCode.SUCCESS) {
                    PageBean<GirlListBean> pageBean = response.m_object;
                    if (pageBean != null) {
                        List<GirlListBean> girlListBeans = pageBean.data;
                        if (girlListBeans != null) {
                            int size = girlListBeans.size();
                            if (isRefresh) {
                                mCurrentPage = 1;
                                mGirlListBeans.clear();
                                mGirlListBeans.addAll(girlListBeans);
                                mAdapter.loadData(mGirlListBeans);
                                refreshlayout.finishRefresh();
                                if (size >= 10) {//如果是刷新,且返回的数据大于等于10条,就可以load more
                                    refreshlayout.setEnableLoadMore(true);
                                }
                            } else {
                                mCurrentPage++;
                                mGirlListBeans.addAll(girlListBeans);
                                mAdapter.loadData(mGirlListBeans);
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
                if (isRefresh) {
                    refreshlayout.finishRefresh();
                } else {
                    refreshlayout.finishLoadMore();
                }
            }
        });
    }

}