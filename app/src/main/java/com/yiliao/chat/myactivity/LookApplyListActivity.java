package com.yiliao.chat.myactivity;

import android.support.annotation.NonNull;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.OrientationHelper;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.listener.OnLoadMoreListener;
import com.scwang.smartrefresh.layout.listener.OnRefreshListener;
import com.yiliao.chat.R;
import com.yiliao.chat.base.BaseActivity;
import com.yiliao.chat.base.BaseResponse;
import com.yiliao.chat.bean.PageBean;
import com.yiliao.chat.constant.ChatApi;
import com.yiliao.chat.myactivity.adpate.LookApplyListAdpater;
import com.yiliao.chat.myactivity.bean.ActivityApplyBean;
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

public class LookApplyListActivity extends BaseActivity {

    private SmartRefreshLayout refreshLayout;

    private List<ActivityApplyBean> mApplyBean = new ArrayList<>();

    private int mCurrentPage=1;

    private RecyclerView mRecy_LookApply;

    private LookApplyListAdpater mAdpater;

    private String activityId;

    @Override
    protected View getContentView() {
        return inflate(R.layout.lookapplylist);
    }

    @Override
    protected void onContentAdded() {
        activityId=getIntent().getStringExtra("activityId");
        setTitle("已报名");
        getInitView();
        getActivityList(refreshLayout,false,1);
    }


    public void getInitView(){
        refreshLayout = findViewById(R.id.refreshLayout);


        refreshLayout.setOnRefreshListener(new OnRefreshListener() {
            @Override
            public void onRefresh(@NonNull RefreshLayout refreshlayout) {
                getActivityList(refreshlayout, true, 1);
            }
        });
        refreshLayout.setOnLoadMoreListener(new OnLoadMoreListener() {
            @Override
            public void onLoadMore(@NonNull RefreshLayout refreshlayout) {
                getActivityList(refreshlayout, false, mCurrentPage + 1);
            }
        });
        mRecy_LookApply=findViewById(R.id.mRecy_LookApply);
        LinearLayoutManager manager = new LinearLayoutManager(mContext);
        manager.setOrientation(OrientationHelper.VERTICAL);
        mRecy_LookApply.setLayoutManager(manager);
         mAdpater = new LookApplyListAdpater(getApplicationContext());

        mRecy_LookApply.setAdapter(mAdpater);
    }
    //获取数据列表
    public void getActivityList(final RefreshLayout refreshlayout, final boolean isRefresh, int page){

        Map<String, String> paramMap = new HashMap<>();

        paramMap.put("activityId", activityId);
        paramMap.put("page", String.valueOf(page));
        OkHttpUtils.post().url(ChatApi.ACTIVITY_APPLY_LIST)
                .addParams("param", ParamUtil.getParam(paramMap))
                .build().execute(new AjaxCallback<BaseResponse<PageBean<ActivityApplyBean>>>() {
            @Override
            public void onResponse(BaseResponse<PageBean<ActivityApplyBean>> response, int id) {
                if (response != null && response.m_istatus == NetCode.SUCCESS) {
                    PageBean<ActivityApplyBean> pageBean = response.m_object;
                    if (pageBean != null) {
                        List<ActivityApplyBean> Beans = pageBean.data;
                        if (Beans != null) {
                            int size = Beans.size();
                            if (isRefresh) {
                                mCurrentPage = 1;
                                mApplyBean.clear();
                                mApplyBean.addAll(Beans);
                                mAdpater.loadData(mApplyBean);
                                if (mApplyBean.size() > 0) {
                                    refreshLayout.setEnableRefresh(true);
                                }
                                refreshlayout.finishRefresh();
                                if (size >= 10) {//如果是刷新,且返回的数据大于等于10条,就可以load more
                                    refreshlayout.setEnableLoadMore(true);
                                }
                            } else {
                                mCurrentPage++;
                                mApplyBean.addAll(Beans);
                                mAdpater.loadData(mApplyBean);
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
}
