package com.yiliao.chat.fragment;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.OrientationHelper;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.listener.OnLoadMoreListener;
import com.scwang.smartrefresh.layout.listener.OnRefreshListener;
import com.yiliao.chat.R;
import com.yiliao.chat.adapter.GirlRecyclerGridAdapter;
import com.yiliao.chat.adapter.ZBRecommendAdpater;
import com.yiliao.chat.base.BaseFragment;
import com.yiliao.chat.base.BaseListResponse;
import com.yiliao.chat.base.BaseResponse;
import com.yiliao.chat.bean.BannerBean;
import com.yiliao.chat.bean.BigRoomListBean;
import com.yiliao.chat.bean.GirlListBean;
import com.yiliao.chat.bean.PageBean;
import com.yiliao.chat.constant.ChatApi;
import com.yiliao.chat.constant.Constant;
import com.yiliao.chat.net.AjaxCallback;
import com.yiliao.chat.net.NetCode;
import com.yiliao.chat.util.ParamUtil;
import com.zhy.http.okhttp.OkHttpUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import okhttp3.Call;

public class ZBRecommendFragment extends BaseFragment {
    public ZBRecommendFragment() {

    }

    private ZBRecommendAdpater mZBRecommend;
    private List<GirlListBean> mGirlListBeans = new ArrayList<>();
    private int mCurrentPage = 1;
    private SmartRefreshLayout mRefreshLayout;
    public boolean mHaveFirstVisible = false;

    @Override
    protected int initLayout() {
        return R.layout.fragment_girl_layout;
    }

    @Override
    protected void initView(View view, Bundle savedInstanceState) {
        mRefreshLayout = view.findViewById(R.id.refreshLayout);
        RecyclerView mContentRv = view.findViewById(R.id.content_rv);
        mContentRv.setNestedScrollingEnabled(false);
        mRefreshLayout.setOnRefreshListener(new OnRefreshListener() {
            @Override
            public void onRefresh(@NonNull RefreshLayout refreshlayout) {
                if (Constant.showMainRecommendUI()) {
                    getBigRoomList();
                    getRecommendList();
                }
                getGirlList(refreshlayout, true, 1);
            }
        });
        mRefreshLayout.setOnLoadMoreListener(new OnLoadMoreListener() {
            @Override
            public void onLoadMore(@NonNull RefreshLayout refreshlayout) {
                getGirlList(refreshlayout, false, mCurrentPage + 1);
            }
        });
        //grid方式
        LinearLayoutManager manager = new LinearLayoutManager(getContext());
        manager.setOrientation(OrientationHelper.VERTICAL);
        GridLayoutManager gridLayoutManager = new GridLayoutManager(getContext(), 1);
        gridLayoutManager.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
            @Override
            public int getSpanSize(int position) {
                if (position == 0) {
                    return 1;
                } else {
                    return 1;
                }
            }
        });
        mContentRv.setLayoutManager(manager);
        mZBRecommend = new ZBRecommendAdpater(mContext);
        mContentRv.setAdapter(mZBRecommend);
        //先加一条空 用于上方推荐 banner  官方热门
        mGirlListBeans.add(0, null);
        mZBRecommend.loadData(mGirlListBeans);
        if (Constant.showMainRecommendUI()) {
            List<BigRoomListBean> girlListBeanList = new ArrayList<>();
            girlListBeanList.add(0, null);
            mZBRecommend.loadBigRoomData(girlListBeanList);
        }
    }

    @Override
    protected void onFirstVisible() {
        mHaveFirstVisible = true;
        if (Constant.showMainRecommendUI()) {
            getBigRoomList();
            getRecommendList();
        }
        getBannerList();
        getGirlList(mRefreshLayout, true, 1);
    }

    /**
     * 获取大房间正在直播主播
     */
    private void getBigRoomList() {
        Map<String, String> paramMap = new HashMap<>();
        paramMap.put("userId", mContext.getUserId());
        OkHttpUtils.post().url(ChatApi.GET_TOTAL_BIG_ROOM_LIST)
                .addParams("param", ParamUtil.getParam(paramMap))
                .build().execute(new AjaxCallback<BaseListResponse<BigRoomListBean>>() {
            @Override
            public void onResponse(BaseListResponse<BigRoomListBean> response, int id) {
                if (response != null && response.m_istatus == NetCode.SUCCESS) {
                    List<BigRoomListBean> pageBean = response.m_object;
                    if (pageBean != null && pageBean.size() > 0) {
                        pageBean.add(0, null);
                        mZBRecommend.loadBigRoomData(pageBean);
                    } else {
                        List<BigRoomListBean> girlListBeanList = new ArrayList<>();
                        girlListBeanList.add(0, null);
                        mZBRecommend.loadBigRoomData(girlListBeanList);
                    }
                }
            }
        });
    }

    /**
     * 获取推荐主播
     */
    private void getRecommendList() {
        Map<String, String> paramMap = new HashMap<>();
        paramMap.put("userId", mContext.getUserId());
        paramMap.put("page", "1");
        OkHttpUtils.post().url(ChatApi.GET_HOME_NOMINATE_LIST)
                .addParams("param", ParamUtil.getParam(paramMap))
                .build().execute(new AjaxCallback<BaseResponse<PageBean<GirlListBean>>>() {
            @Override
            public void onResponse(BaseResponse<PageBean<GirlListBean>> response, int id) {
                if (response != null && response.m_istatus == NetCode.SUCCESS) {
                    PageBean<GirlListBean> pageBean = response.m_object;
                    if (pageBean != null) {
                        List<GirlListBean> girlListBeans = pageBean.data;
                        if (girlListBeans != null && girlListBeans.size() > 0) {
                            mZBRecommend.loadRecommendData(girlListBeans);
                        } else {
                            List<GirlListBean> girlListBeanList = new ArrayList<>();
                            mZBRecommend.loadRecommendData(girlListBeanList);
                        }
                    }
                }
            }
        });
    }

    /**
     * 获取女神列表数据
     */
    private void getGirlList(final RefreshLayout refreshlayout, final boolean isRefresh, int page) {
        Map<String, String> paramMap = new HashMap<>();
        paramMap.put("userId", mContext.getUserId());
        paramMap.put("page", String.valueOf(page));
        paramMap.put("queryType", String.valueOf(0));
        OkHttpUtils.post().url(ChatApi.GET_HOME_PAGE_LIST)
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
                                mGirlListBeans.add(0, null);
                                mGirlListBeans.addAll(girlListBeans);
                                mZBRecommend.loadData(mGirlListBeans);
                                refreshlayout.finishRefresh();
                                if (size >= 10) {//如果是刷新,且返回的数据大于等于10条,就可以load more
                                    refreshlayout.setEnableLoadMore(true);
                                }
                            } else {
                                mCurrentPage++;
                                mGirlListBeans.addAll(girlListBeans);
                                mZBRecommend.loadData(mGirlListBeans);
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
                        mZBRecommend.loadBannerData(bannerBeans);
                    }
                }
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        if (mHaveFirstVisible && mZBRecommend != null) {
            mZBRecommend.resumeChange();
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        if (mHaveFirstVisible && mZBRecommend != null) {
            mZBRecommend.pauseChange();
        }
    }
}
