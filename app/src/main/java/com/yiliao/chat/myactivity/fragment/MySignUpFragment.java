package com.yiliao.chat.myactivity.fragment;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.OrientationHelper;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.listener.OnLoadMoreListener;
import com.scwang.smartrefresh.layout.listener.OnRefreshListener;
import com.yiliao.chat.R;
import com.yiliao.chat.base.AppManager;
import com.yiliao.chat.base.BaseFragment;
import com.yiliao.chat.base.BaseResponse;
import com.yiliao.chat.bean.ChatUserInfo;
import com.yiliao.chat.bean.PageBean;
import com.yiliao.chat.constant.ChatApi;
import com.yiliao.chat.helper.SharedPreferenceHelper;
import com.yiliao.chat.myactivity.adpate.MySignUpAdpater;
import com.yiliao.chat.myactivity.bean.MySignUpBean;
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
import butterknife.ButterKnife;
import okhttp3.Call;

public class MySignUpFragment extends Fragment {


    @BindView(R.id.refreshLayout)
    SmartRefreshLayout refreshLayout;
    private int mCurrentPage=1;

    @BindView(R.id.mRecy_MyEstablish)
     RecyclerView mRecy_MyEstablish;

    private MySignUpAdpater mAdpater;

    private List<MySignUpBean> mMySignupBean = new ArrayList<>();

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view =inflater.inflate(R.layout.myestablish,null,false);
        ButterKnife.bind(this,view);
        initView();
        return view;
    }


    public void initView() {



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


        LinearLayoutManager manager = new LinearLayoutManager(getActivity());
        manager.setOrientation(OrientationHelper.VERTICAL);
        mRecy_MyEstablish.setLayoutManager(manager);

        mAdpater = new MySignUpAdpater(getActivity());
        mRecy_MyEstablish.setAdapter(mAdpater);
        getActivityList(refreshLayout,false,1);
    }


    //获取数据列表
    public void getActivityList(final RefreshLayout refreshlayout, final boolean isRefresh, int page){

        Map<String, String> paramMap = new HashMap<>();
        paramMap.put("userId", getUserId());
        paramMap.put("page", String.valueOf(page));
        OkHttpUtils.post().url(ChatApi.ACTIVITY_LIST_SINGUP_CENTER)
                .addParams("param", ParamUtil.getParam(paramMap))
                .build().execute(new AjaxCallback<BaseResponse<PageBean<MySignUpBean>>>() {
            @Override
            public void onResponse(BaseResponse<PageBean<MySignUpBean>> response, int id) {
                if (response != null && response.m_istatus == NetCode.SUCCESS) {
                    PageBean<MySignUpBean> pageBean = response.m_object;
                    if (pageBean != null) {
                        List<MySignUpBean> focusBeans = pageBean.data;
                        if (focusBeans != null) {
                            int size = focusBeans.size();
                            if (isRefresh) {
                                mCurrentPage = 1;
                                mMySignupBean.clear();
                                mMySignupBean.addAll(focusBeans);
                                mAdpater.loadData(mMySignupBean);
                                if (mMySignupBean.size() > 0) {
                                    refreshLayout.setEnableRefresh(true);
                                }
                                refreshlayout.finishRefresh();
                                if (size >= 10) {//如果是刷新,且返回的数据大于等于10条,就可以load more
                                    refreshlayout.setEnableLoadMore(true);
                                }
                            } else {
                                mCurrentPage++;
                                mMySignupBean.addAll(focusBeans);
                                mAdpater.loadData(mMySignupBean);
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

    /**
     * 获取UserId
     */
    public String getUserId() {
        String sUserId = "";
        if (AppManager.getInstance() != null) {
            ChatUserInfo userInfo = AppManager.getInstance().getUserInfo();
            if (userInfo != null) {
                int userId = userInfo.t_id;
                if (userId >= 0) {
                    sUserId = String.valueOf(userId);
                }
            } else {
                int id = SharedPreferenceHelper.getAccountInfo(getActivity()).t_id;
                sUserId = String.valueOf(id);
            }
        }
        return sUserId;
    }
}
