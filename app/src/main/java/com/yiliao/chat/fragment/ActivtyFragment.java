package com.yiliao.chat.fragment;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.OrientationHelper;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;

import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.listener.OnLoadMoreListener;
import com.scwang.smartrefresh.layout.listener.OnRefreshListener;
import com.yiliao.chat.R;
import com.yiliao.chat.adapter.ActivityListAdpater;
import com.yiliao.chat.base.BaseFragment;
import com.yiliao.chat.base.BaseResponse;
import com.yiliao.chat.bean.ActivityBean;
import com.yiliao.chat.bean.PageBean;
import com.yiliao.chat.constant.ChatApi;
import com.yiliao.chat.helper.SharedPreferenceHelper;
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

public class ActivtyFragment extends BaseFragment {

    private SmartRefreshLayout mRefreshLayout;
    RecyclerView mRecy;
    //页数
    private int mCurrentPage;

    private ActivityListAdpater mAdpater;

    private List<ActivityBean> mActivityBean = new ArrayList<>();
    @Override
    protected int initLayout() {
        return R.layout.ctivtyfragment;
    }

    @Override
    protected void initView(View view, Bundle savedInstanceState) {
        mRefreshLayout=view.findViewById(R.id.refreshLayout);

        mRefreshLayout.setOnRefreshListener(new OnRefreshListener() {
            @Override
            public void onRefresh(@NonNull RefreshLayout refreshlayout) {
                getActivityList(refreshlayout, true, 1);
            }
        });
        mRefreshLayout.setOnLoadMoreListener(new OnLoadMoreListener() {
            @Override
            public void onLoadMore(@NonNull RefreshLayout refreshlayout) {
                getActivityList(refreshlayout, false, mCurrentPage + 1);
            }
        });

        mRecy= view.findViewById(R.id.mRecy_activity);

        LinearLayoutManager manager = new LinearLayoutManager(mContext);
        manager.setOrientation(OrientationHelper.VERTICAL);
        mRecy.setLayoutManager(manager);
        mAdpater = new ActivityListAdpater(mContext);
        mRecy.setAdapter(mAdpater);
    }

    @Override
    protected void onFirstVisible() {
        getActivityList(mRefreshLayout,false,1);
    }


    //获取数据列表
    public void getActivityList(final RefreshLayout refreshlayout, final boolean isRefresh, int page){
        Log.i("aaa",mContext.getUserId()+","+SharedPreferenceHelper.getCodeLng(mContext)+","+SharedPreferenceHelper.getCodeLat(mContext));
        Map<String, String> paramMap = new HashMap<>();
        paramMap.put("userId", mContext.getUserId());
        paramMap.put("page", String.valueOf(page));
        paramMap.put("lng", SharedPreferenceHelper.getCodeLng(mContext));
        paramMap.put("lat",SharedPreferenceHelper.getCodeLat(mContext));
        OkHttpUtils.post().url(ChatApi.ACTIVITY_LIST_MAIN)
                .addParams("param", ParamUtil.getParam(paramMap))
                .build().execute(new AjaxCallback<BaseResponse<PageBean<ActivityBean>>>() {
            @Override
            public void onResponse(BaseResponse<PageBean<ActivityBean>> response, int id) {
                if (response != null && response.m_istatus == NetCode.SUCCESS) {
                    PageBean<ActivityBean> pageBean = response.m_object;
                    if (pageBean != null) {
                        List<ActivityBean> focusBeans = pageBean.data;
                        if (focusBeans != null) {
                            int size = focusBeans.size();
                            if (isRefresh) {
                                mCurrentPage = 1;
                                mActivityBean.clear();
                                mActivityBean.addAll(focusBeans);
                                mAdpater.loadData(mActivityBean);
                                if (mActivityBean.size() > 0) {
                                    mRefreshLayout.setEnableRefresh(true);
                                }
                                refreshlayout.finishRefresh();
                                if (size >= 10) {//如果是刷新,且返回的数据大于等于10条,就可以load more
                                    refreshlayout.setEnableLoadMore(true);
                                }
                            } else {
                                mCurrentPage++;
                                mActivityBean.addAll(focusBeans);
                                mAdpater.loadData(mActivityBean);
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
}
