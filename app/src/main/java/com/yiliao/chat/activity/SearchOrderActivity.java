package com.yiliao.chat.activity;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.TextView;

import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.listener.OnLoadMoreListener;
import com.scwang.smartrefresh.layout.listener.OnRefreshListener;
import com.yiliao.chat.R;
import com.yiliao.chat.adapter.SearchOrderAdapter;
import com.yiliao.chat.adapter.SearchRecyclerAdapter;
import com.yiliao.chat.base.BaseActivity;
import com.yiliao.chat.base.BaseResponse;
import com.yiliao.chat.bean.PageBean;
import com.yiliao.chat.bean.SearchBean;
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
import okhttp3.Call;

public class SearchOrderActivity extends BaseActivity {
    @BindView(R.id.content_rv)
    RecyclerView mContentRv;
    @BindView(R.id.refreshLayout)
    SmartRefreshLayout mRefreshLayout;
    @BindView(R.id.search_et)
    EditText mSearchEt;
    @BindView(R.id.search_tv)
    TextView mSearchTv;
    int guildId;
    private int mCurrentPage = 1;
    private List<SearchBean> mFocusBeans = new ArrayList<>();
    SearchOrderAdapter adapter;

    @Override
    protected View getContentView() {
        return inflate(R.layout.activity_search_layout);
    }

    @Override
    protected void onContentAdded() {
        guildId = getIntent().getIntExtra("guildId", 0);
        needHeader(false);
        initRecycler();
        showSpan();
        adapter.setOnItemClickListener(new SearchOrderAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(SearchBean sortBean, int flag) {
                int id = sortBean.t_id;
                if (flag == 1) {
                    Intent intent = new Intent(mContext, ActorInfoOneActivity.class);
                    intent.putExtra(Constant.ACTOR_ID, id);
                    mContext.startActivity(intent);
                } else {
                    String isGuid = sortBean.inGuid;
                    if (isGuid.equals("0")) {
                        Invite(id);
                    }
                    if (isGuid.equals("1")) {
                        ToastUtil.show("该主播加入了其他公会");
                    }
                    if (isGuid.equals("2")) {
                        Intent intent = new Intent(SearchOrderActivity.this, ActorEarnDetailActivity.class);
                        intent.putExtra("guildId", guildId);
                        intent.putExtra("totalGold", sortBean.totalGold);
                        intent.putExtra(Constant.ACTOR_ID, id);
                        startActivity(intent);
                    }
                    if (isGuid.equals("3")) {
                        ToastUtil.show("该主播已经收到了公会的邀请");
                    }
                }

            }
        });
    }

    /**
     * 邀请加入公会
     */
    public void Invite(final int ids) {
        Map<String, String> paramMap = new HashMap<>();
        paramMap.put("userId", getUserId());
        paramMap.put("guildId", String.valueOf(guildId));
        paramMap.put("coverUserId", String.valueOf(ids));
        OkHttpUtils.post().url(ChatApi.ADD_GUILD)
                .addParams("param", ParamUtil.getParam(paramMap))
                .build().execute(new AjaxCallback<BaseResponse<PageBean<SearchBean>>>() {
            @Override
            public void onResponse(BaseResponse<PageBean<SearchBean>> response, int id) {

                if (response != null && response.m_istatus == NetCode.SUCCESS) {
                    ToastUtil.show(response.m_strMessage);
                } else {
                    ToastUtil.show(response.m_strMessage);
                }

            }

            @Override
            public void onError(Call call, Exception e, int id) {
                super.onError(call, e, id);
            }
        });

    }

    /**
     * 获取个人浏览记录
     */
    private void getSearchResult(final RefreshLayout refreshlayout, final boolean isRefresh, int page) {
        //搜索条件
        String condition = mSearchEt.getText().toString().trim();
        if (TextUtils.isEmpty(condition)) {
            ToastUtil.showToast(getApplicationContext(), R.string.search_hint);
            refreshlayout.finishRefresh();
            return;
        }

        Map<String, String> paramMap = new HashMap<>();
        paramMap.put("userId", getUserId());
        paramMap.put("page", String.valueOf(page));
        paramMap.put("guildId", String.valueOf(guildId));
        paramMap.put("condition", condition);
        OkHttpUtils.post().url(ChatApi.SEARCH_ORDER)
                .addParams("param", ParamUtil.getParam(paramMap))
                .build().execute(new AjaxCallback<BaseResponse<PageBean<SearchBean>>>() {
            @Override
            public void onResponse(BaseResponse<PageBean<SearchBean>> response, int id) {
                if (isFinishing()) {
                    return;
                }
                if (response != null && response.m_istatus == NetCode.SUCCESS) {
                    PageBean<SearchBean> pageBean = response.m_object;
                    if (pageBean != null) {
                        List<SearchBean> focusBeans = pageBean.data;
                        if (focusBeans != null) {
                            int size = focusBeans.size();
                            if (isRefresh) {
                                mCurrentPage = 1;
                                mFocusBeans.clear();
                                mFocusBeans.addAll(focusBeans);
                                adapter.loadData(mFocusBeans);
                                refreshlayout.finishRefresh();
                                if (size >= 10) {//如果是刷新,且返回的数据大于等于10条,就可以load more
                                    refreshlayout.setEnableLoadMore(true);
                                } else if (size <= 0) {
                                    ToastUtil.showToast(getApplicationContext(), R.string.no_one);
                                }
                            } else {
                                mCurrentPage++;
                                mFocusBeans.addAll(focusBeans);
                                adapter.loadData(mFocusBeans);
                                if (size >= 10) {
                                    refreshlayout.finishLoadMore();
                                }
                            }
                            if (size < 10) {//如果数据返回少于10了,那么说明就没数据了
                                refreshlayout.finishLoadMoreWithNoMoreData();
                            }
                        }
                    } else {
//                        ToastUtil.showToast(getApplicationContext(), "");
                        if (isRefresh) {
                            refreshlayout.finishRefresh();
                        } else {
                            refreshlayout.finishLoadMore();
                        }
                    }
                } else {
//                    ToastUtil.showToast(getApplicationContext(), R.string.system_error);
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
     * 初始化RecyclerView
     */
    private void initRecycler() {
        mRefreshLayout.setOnRefreshListener(new OnRefreshListener() {
            @Override
            public void onRefresh(@NonNull RefreshLayout refreshlayout) {
                getSearchResult(refreshlayout, true, 1);
            }
        });
        mRefreshLayout.setOnLoadMoreListener(new OnLoadMoreListener() {
            @Override
            public void onLoadMore(@NonNull RefreshLayout refreshlayout) {
                getSearchResult(refreshlayout, false, mCurrentPage + 1);
            }
        });
        mSearchEt.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (!TextUtils.isEmpty(s) && s.length() > 0) {
                    mSearchTv.setText(getResources().getString(R.string.search));
                } else {
                    mSearchTv.setText(getResources().getString(R.string.cancel));
                }
            }
        });
        mSearchEt.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if (keyCode == KeyEvent.KEYCODE_ENTER && event.getAction() == KeyEvent.ACTION_DOWN) {
                    String condition = mSearchEt.getText().toString().trim();
                    if (TextUtils.isEmpty(condition)) {
                        ToastUtil.showToast(getApplicationContext(), R.string.search_hint);
                        return true;
                    }
                    closeSoft();
                    mRefreshLayout.autoRefresh();
                    return true;
                }
                return false;
            }
        });

        LinearLayoutManager gridLayoutManager = new LinearLayoutManager(this);
        mContentRv.setLayoutManager(gridLayoutManager);
        adapter = new SearchOrderAdapter(this);
        mContentRv.setAdapter(adapter);
    }

    @OnClick({R.id.back_fl, R.id.search_tv})
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.back_fl: {
                closeSoft();
                finish();
                break;
            }
            case R.id.search_tv: {
                String des = mSearchTv.getText().toString().trim();
                if (des.equals(getResources().getString(R.string.search))) {
                    String condition = mSearchEt.getText().toString().trim();
                    if (TextUtils.isEmpty(condition)) {
                        ToastUtil.showToast(getApplicationContext(), R.string.search_hint);
                        return;
                    }
                    closeSoft();
                    mRefreshLayout.autoRefresh();
                } else {
                    closeSoft();
                    finish();
                }
                break;
            }
        }
    }

    /**
     * 调用键盘
     */
    private void showSpan() {
        if (mSearchEt != null) {
            mSearchEt.requestFocus();
            getWindow().getDecorView().postDelayed(new Runnable() {
                @Override
                public void run() {
                    if (mSearchEt != null && mSearchEt.requestFocus()) {
                        InputMethodManager imm = (InputMethodManager) getApplicationContext().getSystemService(Context.INPUT_METHOD_SERVICE);
                        if (imm != null) {
                            imm.showSoftInput(mSearchEt, InputMethodManager.SHOW_IMPLICIT);
                        }
                    }
                }
            }, 400);
        }
    }

    /**
     * 关闭软件盘
     */
    private void closeSoft() {
        try {
            if (mSearchEt != null && mSearchEt.hasFocus()) {
                InputMethodManager imm = (InputMethodManager) getApplicationContext().getSystemService(Context.INPUT_METHOD_SERVICE);
                if (imm != null && imm.isActive()) {
                    imm.hideSoftInputFromWindow(mSearchEt.getWindowToken(), 0);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
