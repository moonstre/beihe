package com.yiliao.chat.activity;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.listener.OnLoadMoreListener;
import com.scwang.smartrefresh.layout.listener.OnRefreshListener;
import com.yiliao.chat.R;
import com.yiliao.chat.adapter.MyActorRecyclerAdapter;
import com.yiliao.chat.base.BaseActivity;
import com.yiliao.chat.base.BaseResponse;
import com.yiliao.chat.bean.CompanyBean;
import com.yiliao.chat.bean.GuildCountBean;
import com.yiliao.chat.bean.PageBean;
import com.yiliao.chat.constant.ChatApi;
import com.yiliao.chat.constant.Constant;
import com.yiliao.chat.net.AjaxCallback;
import com.yiliao.chat.net.NetCode;
import com.yiliao.chat.util.ParamUtil;
import com.yiliao.chat.util.ToastUtil;
import com.zhy.http.okhttp.OkHttpUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.OnClick;

/*
 * Copyright (C) 2018
 * 版权所有
 *
 * 功能描述：我的主播页面
 * 作者：
 * 创建时间：2018/9/11
 *
 * 修改人：
 * 修改描述：
 * 修改日期
 */
public class MyActorActivity extends BaseActivity {

    @BindView(R.id.content_rv)
    RecyclerView mContentRv;
    @BindView(R.id.refreshLayout)
    SmartRefreshLayout mRefreshLayout;
    @BindView(R.id.number_tv)
    TextView mNumberTv;
    @BindView(R.id.gold_tv)
    TextView mGoldTv;
    @BindView(R.id.category_iv)
    ImageView category_iv;
    private int mCurrentPage = 1;
    private List<CompanyBean> mFocusBeans = new ArrayList<>();
    private MyActorRecyclerAdapter mAdapter;
    int guildId;
    @Override
    protected View getContentView() {
        return inflate(R.layout.activity_my_actor_layout);
    }

    @Override
    protected int getStatusBarColor() {
        return getResources().getColor(R.color.pink_main);
    }

    @Override
    protected void onContentAdded() {
        guildId=getIntent().getIntExtra("guildId",0);
        if (Constant.showSearch()){
            category_iv.setVisibility(View.VISIBLE);
        }
        needHeader(false);
        initRecycler();
        getGuildCount();

        mAdapter.setOnItemClickListener(new MyActorRecyclerAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(CompanyBean sortBean) {
                Intent intent = new Intent(mContext, ActorEarnDetailActivity.class);
                intent.putExtra("guildId",guildId);
                intent.putExtra("totalGold",sortBean.totalGold);
                intent.putExtra(Constant.ACTOR_ID, sortBean.t_anchor_id);
                mContext.startActivity(intent);
            }
        });
    }

    /**
     * 统计公会主播数和贡献值
     */
    private void getGuildCount() {
        Map<String, String> paramMap = new HashMap<>();
        paramMap.put("userId", getUserId());
        OkHttpUtils.post().url(ChatApi.GET_GUILD_COUNT)
                .addParams("param", ParamUtil.getParam(paramMap))
                .build().execute(new AjaxCallback<BaseResponse<GuildCountBean>>() {
            @Override
            public void onResponse(BaseResponse<GuildCountBean> response, int id) {
                if (response != null && response.m_istatus == NetCode.SUCCESS) {
                    GuildCountBean bean = response.m_object;
                    if (bean != null) {
                        mNumberTv.setText(String.valueOf(bean.anchorCount));
                        mGoldTv.setText(String.valueOf(bean.totalGold));
                    }
                }
            }
        });
    }
    int g_id;
    /**
     * 获取个人浏览记录
     */
    private void getMyActor(final RefreshLayout refreshlayout, final boolean isRefresh, int page) {
        Map<String, String> paramMap = new HashMap<>();
        paramMap.put("userId", getUserId());
        paramMap.put("page", String.valueOf(page));
        OkHttpUtils.post().url(ChatApi.GET_CONTRIBUTION_LIST)
                .addParams("param", ParamUtil.getParam(paramMap))
                .build().execute(new AjaxCallback<BaseResponse<PageBean<CompanyBean>>>() {
            @Override
            public void onResponse(BaseResponse<PageBean<CompanyBean>> response, int id) {
                if (isFinishing()) {
                    return;
                }
                if (response != null && response.m_istatus == NetCode.SUCCESS) {
                    PageBean<CompanyBean> pageBean = response.m_object;
                    if (pageBean != null) {
                        List<CompanyBean> focusBeans = pageBean.data;

                        if (focusBeans != null) {

                            int size = focusBeans.size();
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
        });
    }

    /**
     * 初始化RecyclerView
     */
    private void initRecycler() {
        mRefreshLayout.setOnRefreshListener(new OnRefreshListener() {
            @Override
            public void onRefresh(@NonNull RefreshLayout refreshlayout) {
                getMyActor(refreshlayout, true, 1);
            }
        });
        mRefreshLayout.setOnLoadMoreListener(new OnLoadMoreListener() {
            @Override
            public void onLoadMore(@NonNull RefreshLayout refreshlayout) {
                getMyActor(refreshlayout, false, mCurrentPage + 1);
            }
        });

        LinearLayoutManager gridLayoutManager = new LinearLayoutManager(this);
        mContentRv.setLayoutManager(gridLayoutManager);
        mAdapter = new MyActorRecyclerAdapter(this);
        mContentRv.setAdapter(mAdapter);
    }

    @OnClick({R.id.back_iv,R.id.category_iv})
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.back_iv: {
                finish();
                break;
            }
            case R.id.category_iv:
                Intent intent=new Intent(this,SearchOrderActivity.class);
                intent.putExtra("guildId",guildId);
                startActivity(intent);
                break;
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        getMyActor(mRefreshLayout, true, 1);
    }
}
