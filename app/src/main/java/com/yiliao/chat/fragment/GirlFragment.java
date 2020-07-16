package com.yiliao.chat.fragment;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;

import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.listener.OnLoadMoreListener;
import com.scwang.smartrefresh.layout.listener.OnRefreshListener;
import com.yiliao.chat.R;
import com.yiliao.chat.activity.ZhuBoOrderActivity;
import com.yiliao.chat.adapter.GirlRecyclerGridAdapter;
import com.yiliao.chat.base.BaseActivity;
import com.yiliao.chat.base.BaseFragment;
import com.yiliao.chat.base.BaseListResponse;
import com.yiliao.chat.base.BaseResponse;
import com.yiliao.chat.bean.BannerBean;
import com.yiliao.chat.bean.BigRoomListBean;
import com.yiliao.chat.bean.GirlListBean;
import com.yiliao.chat.bean.PageBean;
import com.yiliao.chat.bean.RecivieBean;
import com.yiliao.chat.constant.ChatApi;
import com.yiliao.chat.constant.Constant;
import com.yiliao.chat.net.AjaxCallback;
import com.yiliao.chat.net.NetCode;
import com.yiliao.chat.util.DialogUtil;
import com.yiliao.chat.util.ParamUtil;
import com.yiliao.chat.util.ToastUtil;
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
 * 功能描述：女神页面Fragment
 * 作者：
 * 创建时间：2018/6/14
 *
 * 修改人：
 * 修改描述：
 * 修改日期
 */
public class GirlFragment extends BaseFragment {
    public GirlFragment() {

    }
    private GirlRecyclerGridAdapter mGridAdapter;//用于grid
    private List<GirlListBean> mGirlListBeans = new ArrayList<>();
    private int mCurrentPage = 1;
    private SmartRefreshLayout mRefreshLayout;
    public boolean mHaveFirstVisible = false;
    int code=0;
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

                }else if(Constant.hideHomeNearAndNew()){
                    getSortmmendList(refreshlayout, true, 1,code);
                    getBannerList();
                }else {
                    getGirlList(refreshlayout, true, 1);

                }

            }
        });
        mRefreshLayout.setOnLoadMoreListener(new OnLoadMoreListener() {
            @Override
            public void onLoadMore(@NonNull RefreshLayout refreshlayout) {
                if (Constant.showMainRecommendUI()) {
                    getBigRoomList();
                    getRecommendList();
//
                }else if(Constant.hideHomeNearAndNew()){
                    getSortmmendList(refreshlayout,false,mCurrentPage+1,code);
                }else {
                    getGirlList(refreshlayout, false, mCurrentPage + 1);

                }

            }
        });

        //grid方式
        GridLayoutManager gridLayoutManager = new GridLayoutManager(getContext(), 2);
//
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

        if (Constant.showMainRecommendUI()) {
            List<BigRoomListBean> girlListBeanList = new ArrayList<>();
            girlListBeanList.add(0, null);
            mGridAdapter.loadBigRoomData(girlListBeanList);
        }
        if (Constant.hideHomeNearAndNew()){
            if (mRefreshLayout != null) {
                mRefreshLayout.autoRefresh();
            }
        }
//        else {
//            //先加一条空 用于上方推荐 banner  官方热门
//        mGirlListBeans.add(0, null);
//        mGridAdapter.loadData(mGirlListBeans);
//        }
        //先加一条空 用于上方推荐 banner  官方热门
        mGirlListBeans.add(0, null);
        mGridAdapter.loadData(mGirlListBeans);
    }

    @Override
    protected void onFirstVisible() {
        mHaveFirstVisible = true;
        if (Constant.showMainRecommendUI()) {
            getBigRoomList();
            getRecommendList();
        }else if (Constant.hideHomeNearAndNew()){
            getSortmmendList(mRefreshLayout,true,1,code);
            getUserOrderState();
            getBannerList();
        }else {
            getBannerList();
            getGirlList(mRefreshLayout, true, 1);
        }


    }
    /**
     *
     *查询主播的订单信息详情
     */
    public void getUserOrderState(){
        Map<String, String> paramMap = new HashMap<>();
        paramMap.put("userId", mContext.getUserId());
        OkHttpUtils.post().url(ChatApi.QUERY_USER_ORDER)
                .addParams("param", ParamUtil.getParam(paramMap))
                .build().execute(new AjaxCallback<BaseResponse<RecivieBean>>() {
            @Override
            public void onResponse(BaseResponse<RecivieBean> response, int id) {
                if (response != null && response.m_istatus == NetCode.SUCCESS) {
                    RecivieBean pageBean = response.m_object;
                    if (pageBean != null) {
                        if ("0".equals(pageBean.status)&& !TextUtils.equals(pageBean.waitTime,"0")){
                            Intent intent=new Intent(mContext, ZhuBoOrderActivity.class);
                            intent.putExtra("content",(Serializable) pageBean);
                            intent.putExtra("flag","0");
                            startActivity(intent);
                        }
                    }
                }
            }
        });
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
                        mGridAdapter.loadBigRoomData(pageBean);
                    } else {
                        List<BigRoomListBean> girlListBeanList = new ArrayList<>();
                        girlListBeanList.add(0, null);
                        mGridAdapter.loadBigRoomData(girlListBeanList);
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
                            mGridAdapter.loadRecommendData(girlListBeans);
                        } else {
                            List<GirlListBean> girlListBeanList = new ArrayList<>();
                            mGridAdapter.loadRecommendData(girlListBeanList);
                        }
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
    /**
     * 获取推荐主播排序
     */
    private void getSortmmendListpx(String code) {
        Map<String, String> paramMap = new HashMap<>();
        paramMap.put("userId", mContext.getUserId());
        paramMap.put("page", "1");
        paramMap.put("queryType",code);
        OkHttpUtils.post().url(ChatApi.GET_SORT_PAGE_LIST)
                .addParams("param", ParamUtil.getParam(paramMap))
                .build().execute(new AjaxCallback<BaseResponse<PageBean<GirlListBean>>>() {
            @Override
            public void onResponse(BaseResponse<PageBean<GirlListBean>> response, int id) {
                if (response != null && response.m_istatus == NetCode.SUCCESS) {
                    PageBean<GirlListBean> pageBean = response.m_object;
                    if (pageBean != null) {
                        List<GirlListBean> girlListBeans = pageBean.data;
                        if (girlListBeans != null && girlListBeans.size() > 0) {
                            mGridAdapter.loadRecommendData(girlListBeans);
                        } else {
                            List<GirlListBean> girlListBeanList = new ArrayList<>();
                            mGridAdapter.loadRecommendData(girlListBeanList);
                        }
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
                        mGridAdapter.loadBannerData(bannerBeans);
                    }
                }
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        if (mHaveFirstVisible && mGridAdapter != null) {
            mGridAdapter.resumeChange();

        }
//        getUserOrderState();
    }

    @Override
    public void onPause() {
        super.onPause();
        if (mHaveFirstVisible && mGridAdapter != null) {
            mGridAdapter.pauseChange();
        }
    }

}
