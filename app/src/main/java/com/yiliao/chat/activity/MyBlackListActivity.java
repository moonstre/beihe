package com.yiliao.chat.activity;

import android.app.Dialog;
import android.support.annotation.NonNull;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.widget.TextView;

import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.listener.OnLoadMoreListener;
import com.scwang.smartrefresh.layout.listener.OnRefreshListener;
import com.yiliao.chat.R;
import com.yiliao.chat.adapter.BlackListAdpater;
import com.yiliao.chat.adapter.FocusRecyclerAdapter;
import com.yiliao.chat.base.BaseActivity;
import com.yiliao.chat.base.BaseListResponse;
import com.yiliao.chat.base.BaseResponse;
import com.yiliao.chat.bean.BlackListBean;
import com.yiliao.chat.bean.FocusBean;
import com.yiliao.chat.bean.GiftBean;
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

import butterknife.BindView;
import okhttp3.Call;

public class MyBlackListActivity extends BaseActivity {

    @BindView(R.id.MyBlackList)
    RecyclerView MyBlackList;

    @BindView(R.id.refreshLayout)
    SmartRefreshLayout mRefreshLayout;

    //页数
    private int mCurrentPage;

    private BlackListAdpater mAdapter;
    private List<BlackListBean> mBlacklist = new ArrayList<>();
    @Override
    protected View getContentView() {
        return inflate(R.layout.myblack);
    }

    @Override
    protected void onContentAdded() {
        setTitle(R.string.blacktitle);
        initRecycler();

    }

    @Override
    protected void onResume() {
        super.onResume();
        getBlackList(mRefreshLayout,true,1);
    }

    /**
     * 初始化RecyclerView
     */
    private void initRecycler() {
        mRefreshLayout.setOnRefreshListener(new OnRefreshListener() {
            @Override
            public void onRefresh(@NonNull RefreshLayout refreshlayout) {
                getBlackList(refreshlayout, true, 1);
            }
        });
        mRefreshLayout.setOnLoadMoreListener(new OnLoadMoreListener() {
            @Override
            public void onLoadMore(@NonNull RefreshLayout refreshlayout) {
                getBlackList(refreshlayout, false, mCurrentPage + 1);
            }
        });

        LinearLayoutManager gridLayoutManager = new LinearLayoutManager(this);
        MyBlackList.setLayoutManager(gridLayoutManager);
        mAdapter = new BlackListAdpater(this);
        MyBlackList.setAdapter(mAdapter);
        mAdapter.setOnClick(new BlackListAdpater.OnItmeCallBack() {
            @Override
            public void OnClick(int postion) {
                //移除黑名单
                getDeleteDialog(postion);

            }
        });
    }
    /**
     * 获取黑名单列表
     */
    private void getBlackList(final RefreshLayout refreshlayout, final boolean isRefresh, int page) {
        Map<String, String> paramMap = new HashMap<>();
        paramMap.put("userId", getUserId());
        paramMap.put("page", String.valueOf(page));
        OkHttpUtils.post().url(ChatApi.DELETE_LIST)
                .addParams("param", ParamUtil.getParam(paramMap))
                .build().execute(new AjaxCallback<BaseResponse<PageBean<BlackListBean>>>() {
            @Override
            public void onResponse(BaseResponse<PageBean<BlackListBean>> response, int id) {
                if (response != null && response.m_istatus == NetCode.SUCCESS) {
                    PageBean<BlackListBean> pageBean = response.m_object;
                    if (pageBean != null) {
                        List<BlackListBean> blacklist = pageBean.data;
                        if (blacklist != null) {
                            int size = blacklist.size();
                            if (isRefresh) {
                                mCurrentPage = 1;
                                mBlacklist.clear();
                                mBlacklist.addAll(blacklist);
                                mAdapter.loadData(mBlacklist);
                               /* if (mBlacklist.size() > 0) {
                                    mNoFocusTv.setVisibility(View.GONE);
                                } else {
                                    mNoFocusTv.setVisibility(View.VISIBLE);
                                }*/
                                refreshlayout.finishRefresh();
                                if (size >= 10) {//如果是刷新,且返回的数据大于等于10条,就可以load more
                                    refreshlayout.setEnableLoadMore(true);
                                }
                            } else {
                                mCurrentPage++;
                                mBlacklist.addAll(blacklist);
                                mAdapter.loadData(mBlacklist);
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
                    ToastUtil.showToast(getApplicationContext(), R.string.system_error);
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
                ToastUtil.showToast(getApplicationContext(), R.string.system_error);
                if (isRefresh) {
                    refreshlayout.finishRefresh();
                } else {
                    refreshlayout.finishLoadMore();
                }
            }
        });
    }

    private Dialog dialog;
    public void getDeleteDialog(final int poistion){
        dialog =new Dialog(MyBlackListActivity.this,R.style.dialog);
        dialog.setContentView(R.layout.black_list_detele);
        Window dialogWindow = dialog.getWindow();
        dialogWindow
                .setGravity(Gravity.CENTER | Gravity.CENTER);
        TextView Delete_Cancel = (TextView) dialog.findViewById(R.id.Delete_Cancel);
        TextView Delete_Sure = (TextView) dialog.findViewById(R.id.Delete_Sure);

        Delete_Sure.setOnClickListener(new View.OnClickListener() {//确定

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                getDeteleBlack(poistion);

            }
        });
        Delete_Cancel.setOnClickListener(new View.OnClickListener() {//取消

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                dialog.dismiss();
            }
        });
        dialog.show();
    }
    public void getDeteleBlack(int poistion){
        Map<String, String> pullBack = new HashMap<>();
        pullBack.put("userId", getUserId());//查看人
        pullBack.put("fkId", String.valueOf(mBlacklist.get(poistion).t_id));

        OkHttpUtils.post().url(ChatApi.DELETE_PULL_BACK)
                .addParams("param", ParamUtil.getParam(pullBack))
                .build().execute(new AjaxCallback<BaseListResponse<GiftBean>>() {
            @Override
            public void onResponse(BaseListResponse<GiftBean> response, int id) {
                if (response != null && response.m_istatus == NetCode.SUCCESS) {
                   if (dialog!=null&& dialog.isShowing()){
                       dialog.dismiss();

                       getBlackList(mRefreshLayout,true,mCurrentPage);
                   }
                }
            }
        });
    }
}
