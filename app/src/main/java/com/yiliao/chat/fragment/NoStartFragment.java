package com.yiliao.chat.fragment;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.listener.OnLoadMoreListener;
import com.scwang.smartrefresh.layout.listener.OnRefreshListener;
import com.yiliao.chat.R;
import com.yiliao.chat.activity.OrderDetailsActivity;
import com.yiliao.chat.adapter.OrderSortAdapter;
import com.yiliao.chat.base.AppManager;
import com.yiliao.chat.base.BaseFragment;
import com.yiliao.chat.base.BaseResponse;
import com.yiliao.chat.bean.ChatUserInfo;
import com.yiliao.chat.bean.OrderSortBean;
import com.yiliao.chat.bean.PageBean;
import com.yiliao.chat.constant.ChatApi;
import com.yiliao.chat.helper.SharedPreferenceHelper;
import com.yiliao.chat.net.AjaxCallback;
import com.yiliao.chat.net.NetCode;
import com.yiliao.chat.util.ParamUtil;
import com.yiliao.chat.util.ToastUtil;
import com.yiliao.chat.view.SpacesItemDecoration;
import com.zhy.http.okhttp.OkHttpUtils;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import okhttp3.Call;
@SuppressLint("ValidFragment")
public class NoStartFragment extends BaseFragment implements View.OnClickListener {
    String code_c;
    public NoStartFragment(String code){
        code_c=code;
    }
    private SmartRefreshLayout mRefreshLayout;
    private int mCurrentPage = 1;
    private RecyclerView content_rv;
    private OrderSortAdapter mAdapter;
    private List<OrderSortBean> mFocusBeans = new ArrayList<>();
    @Override
    protected int initLayout() {
        return R.layout.order_sort_fragment;
    }

    @Override
    protected void initView(View view, Bundle savedInstanceState) {
        content_rv=view.findViewById(R.id.content_rv);
        mRefreshLayout = view.findViewById(R.id.refreshLayout);
        mRefreshLayout.setOnRefreshListener(new OnRefreshListener() {
            @Override
            public void onRefresh(@NonNull RefreshLayout refreshlayout) {
                getQueryOrder(refreshlayout, true, 1,code_c);
            }
        });
        mRefreshLayout.setOnLoadMoreListener(new OnLoadMoreListener() {
            @Override
            public void onLoadMore(@NonNull RefreshLayout refreshlayout) {
                getQueryOrder(refreshlayout, false, mCurrentPage + 1,code_c);
            }
        });
        LinearLayoutManager gridLayoutManager = new LinearLayoutManager(getContext());
        content_rv.setLayoutManager(gridLayoutManager);
        mAdapter = new OrderSortAdapter(mContext, false);
        content_rv.setAdapter(mAdapter);
        if (mRefreshLayout != null) {
            mRefreshLayout.autoRefresh();
        }
        HashMap<String, Integer> stringIntegerHashMap = new HashMap<>();
        stringIntegerHashMap.put(SpacesItemDecoration.TOP_DECORATION,10);//上下间距
        content_rv.addItemDecoration(new SpacesItemDecoration(stringIntegerHashMap));


        mAdapter.setOnItemClickListener(new OrderSortAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(OrderSortBean sortBean) {
                int role=getUserRole();
                Intent intent=new Intent(getActivity(), OrderDetailsActivity.class);
                intent.putExtra("sortBean",(Serializable)sortBean);
                intent.putExtra("role",role);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
            }
        });

    }
    /**
     * 获取角色
     */
    private int getUserRole() {
        if (AppManager.getInstance() != null) {
            ChatUserInfo userInfo = AppManager.getInstance().getUserInfo();
            if (userInfo != null) {
                //1 主播 0 用户
                return userInfo.t_role;
            } else {
                return SharedPreferenceHelper.getAccountInfo(mContext.getApplicationContext()).t_role;
            }
        }
        return 0;
    }
    @Override
    protected void onFirstVisible() {

    }
    private void getQueryOrder(final RefreshLayout refreshlayout, final boolean isRefresh, int page,String code){
        Map<String, String> paramMap = new HashMap<>();
        paramMap.put("userId", mContext.getUserId());
        paramMap.put("status",code);
        paramMap.put("page",String.valueOf(page));
        OkHttpUtils.post().url(ChatApi.QUERY_SERVICE_ORDER)
                .addParams("param", ParamUtil.getParam(paramMap))
                .build().execute(new AjaxCallback<BaseResponse<PageBean<OrderSortBean>>>() {
            @Override
            public void onResponse(BaseResponse<PageBean<OrderSortBean>> response, int id) {
                if (response != null && response.m_istatus == NetCode.SUCCESS) {
                    PageBean<OrderSortBean> orderSortBean = response.m_object;
                    if (orderSortBean!=null){
                        List<OrderSortBean> orderSortBeanList=orderSortBean.data;
                        if (orderSortBeanList!=null){
                            int size=orderSortBeanList.size();
                            if (isRefresh){
                                mCurrentPage = 1;
                                mFocusBeans.clear();
                                mFocusBeans.addAll(orderSortBeanList);
                                mAdapter.loadData(mFocusBeans);
                                refreshlayout.finishRefresh();
                                if (size >= 10) {//如果是刷新,且返回的数据大于等于10条,就可以load more
                                    refreshlayout.setEnableLoadMore(true);
                                }

                            } else {
                                mCurrentPage++;
                                mFocusBeans.addAll(orderSortBeanList);
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
                }else {
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
                ToastUtil.showToast(mContext, R.string.system_error);
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
        switch (v.getId()){
            case R.id.go_to_detail:

                break;
        }
    }

    @Override
    protected void onFirstVisibleToUser() {
        Bundle bundle = getArguments();
        if (bundle != null) {
//            mActorId = bundle.getInt(Constant.ACTOR_ID);
//            getIntimateAndGift(mActorId);
//            getUserComment(mActorId);
        }
        mIsDataLoadCompleted = true;
    }
}
