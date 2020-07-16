package com.yiliao.chat.fragment.invite;

import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.yiliao.chat.R;
import com.yiliao.chat.adapter.InviteRecyclerAdapter;
import com.yiliao.chat.base.BaseCompactFragment;
import com.yiliao.chat.base.BaseListResponse;
import com.yiliao.chat.bean.RewardBean;
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

/*
 * Copyright (C) 2018
 * 版权所有
 *
 * 功能描述：奖励排行
 * 作者：
 * 创建时间：2018/10/19
 *
 * 修改人：
 * 修改描述：
 * 修改日期
 */
public class RewardFragment extends BaseCompactFragment {

    public RewardFragment() {

    }

    private InviteRecyclerAdapter mAdapter;
    private List<RewardBean> mFocusBeans = new ArrayList<>();

    @Override
    protected int initLayout() {
        return R.layout.fragment_reward_layout;
    }

    @Override
    protected void initView(View view, Bundle savedInstanceState) {
        RecyclerView mContentRv = view.findViewById(R.id.content_rv);
        LinearLayoutManager gridLayoutManager = new LinearLayoutManager(getContext());
        mContentRv.setLayoutManager(gridLayoutManager);
        mAdapter = new InviteRecyclerAdapter(mContext);
        mContentRv.setAdapter(mAdapter);
    }

    @Override
    protected void onFirstVisible() {
        getRankList();
    }

    /**
     * 获取推荐贡献排行榜
     */
    private void getRankList() {
        Map<String, String> paramMap = new HashMap<>();
        paramMap.put("userId", getUserId());
        OkHttpUtils.post().url(ChatApi.GET_SPREAD_BONUSES)
                .addParams("param", ParamUtil.getParam(paramMap))
                .build().execute(new AjaxCallback<BaseListResponse<RewardBean>>() {
            @Override
            public void onResponse(BaseListResponse<RewardBean> response, int id) {
                if (response != null && response.m_istatus == NetCode.SUCCESS) {
                    List<RewardBean> focusBeans = response.m_object;
                    if (focusBeans != null) {
                        mFocusBeans.clear();
                        mFocusBeans.addAll(focusBeans);
                        mAdapter.loadData(mFocusBeans);
                    }
                } else {
                    ToastUtil.showToast(getContext(), R.string.system_error);
                }
            }

            @Override
            public void onError(Call call, Exception e, int id) {
                super.onError(call, e, id);
                ToastUtil.showToast(getContext(), R.string.system_error);
            }
        });
    }

}
