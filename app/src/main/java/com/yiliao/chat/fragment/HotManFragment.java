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
import com.yiliao.chat.adapter.GirlRecyclerGridAdapter;
import com.yiliao.chat.adapter.NewManRecyclerAdapter;
import com.yiliao.chat.base.BaseFragment;
import com.yiliao.chat.base.BaseListResponse;
import com.yiliao.chat.base.BaseResponse;
import com.yiliao.chat.bean.BannerBean;
import com.yiliao.chat.bean.GirlListBean;
import com.yiliao.chat.bean.PageBean;
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

public class HotManFragment extends BaseFragment {

    public HotManFragment(){}
    private SmartRefreshLayout mRefreshLayout;
    private GirlRecyclerGridAdapter mGridAdapter;
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
                getSortmmendList(refreshlayout, true, 1,2);
                getBannerList();
            }
        });
        mRefreshLayout.setOnLoadMoreListener(new OnLoadMoreListener() {
            @Override
            public void onLoadMore(@NonNull RefreshLayout refreshlayout) {
                getSortmmendList(refreshlayout, false, mCurrentPage + 1,2);
                getBannerList();
            }
        });

        GridLayoutManager gridLayoutManager = new GridLayoutManager(mContext, 2);
        gridLayoutManager.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
            @Override
            public int getSpanSize(int position) {
                if (position == 0) {
                    return 2;
                } else {
                    return 1;
                }
            }
        });
        mContentRv.setLayoutManager(gridLayoutManager);
        mGridAdapter = new GirlRecyclerGridAdapter(mContext);
        mContentRv.setAdapter(mGridAdapter);
        mGirlListBeans.add(0, null);
        mGridAdapter.loadData(mGirlListBeans);
    }

    @Override
    protected void onFirstVisible() {
        getSortmmendList(mRefreshLayout, true, 1,2);
        getBannerList();
    }
    /**
     * 获取banner
     */
    private void getBannerList() {
        Map<String, String> paramMap = new HashMap<>();
        paramMap.put("userId", mContext.getUserId());
        OkHttpUtils.post().url(ChatApi.GET_BANNER_LIST)
                .addParams("param", ParamUtil.getParam(paramMap))
                .build().execute(new AjaxCallback<BaseListResponse<BannerBean>>() {
            @Override
            public void onResponse(BaseListResponse<BannerBean> response, int id) {
                if (response != null && response.m_istatus == NetCode.SUCCESS) {
                    List<BannerBean> bannerBeans = response.m_object;
                    if (bannerBeans != null && bannerBeans.size() > 0) {
                        mGridAdapter.loadBannerData(bannerBeans);
                    }
                }
            }
        });
    }
    /**
     * 获取推荐主播
     */
    private void getSortmmendList(final RefreshLayout refreshlayout, final boolean isRefresh, int page,int codes) {
        Map<String, String> paramMap = new HashMap<>();
        paramMap.put("userId", mContext.getUserId());
        paramMap.put("page", String.valueOf(page));
        paramMap.put("queryType",String.valueOf(codes));
        OkHttpUtils.post().url(ChatApi.GET_SORT_PAGE_LIST)
                .addParams("param", ParamUtil.getParam(paramMap))
                .build().execute(new AjaxCallback<BaseResponse<PageBean<GirlListBean>>>() {
            @Override
            public void onResponse(BaseResponse<PageBean<GirlListBean>> response, int id) {
                if (response != null && response.m_istatus == NetCode.SUCCESS) {
                    PageBean<GirlListBean> pageBean = response.m_object;
                    if (pageBean != null) {
                        List<GirlListBean> girlListBeans = pageBean.data;
                        int size = girlListBeans.size();
                        if (isRefresh) {
                            mCurrentPage = 1;
                            mGirlListBeans.clear();
                            mGirlListBeans.add(0, null);
                            mGirlListBeans.addAll(girlListBeans);
                            mGridAdapter.loadData(mGirlListBeans);
                            refreshlayout.finishRefresh();
                            if (size >= 10) {//如果是刷新,且返回的数据大于等于10条,就可以load more
                                refreshlayout.setEnableLoadMore(true);
                            }

                        } else {
                            mCurrentPage++;
                            mGirlListBeans.addAll(girlListBeans);
                            mGridAdapter.loadData(mGirlListBeans);
                            if (size >= 10) {
                                refreshlayout.finishLoadMore();
                            }

                        }
                        if (size < 10) {//如果数据返回少于10了,那么说明就没数据了
                            refreshlayout.finishLoadMoreWithNoMoreData();
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
                ToastUtil.showToast(mContext, R.string.system_error);
            }
        });
    }
}
