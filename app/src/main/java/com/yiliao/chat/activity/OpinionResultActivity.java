package com.yiliao.chat.activity;

import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.yiliao.chat.R;
import com.yiliao.chat.adapter.OpinionResultAdapter;
import com.yiliao.chat.base.BaseActivity;
import com.yiliao.chat.base.BaseResponse;
import com.yiliao.chat.bean.OpinionResultBean;
import com.yiliao.chat.bean.PageBean;
import com.yiliao.chat.constant.ChatApi;
import com.yiliao.chat.net.AjaxCallback;
import com.yiliao.chat.net.NetCode;
import com.yiliao.chat.util.ParamUtil;
import com.zhy.http.okhttp.OkHttpUtils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import okhttp3.Call;

public class OpinionResultActivity extends BaseActivity {
    @BindView(R.id.content_rv)
    RecyclerView mContentRv;
    private OpinionResultAdapter opinionResultAdapter;

    @Override
    protected View getContentView() {
        return inflate(R.layout.activity_opinion_result_layout);
    }

    @Override
    protected void onContentAdded() {
        setTitle(R.string.opinion_feed_back_result);

        LinearLayoutManager gridLayoutManager = new LinearLayoutManager(this);
        mContentRv.setLayoutManager(gridLayoutManager);
        opinionResultAdapter = new OpinionResultAdapter(OpinionResultActivity.this);
        mContentRv.setAdapter(opinionResultAdapter);

        getFeedBackList();
    }

    private void getFeedBackList() {
        Map<String, String> paramMap = new HashMap<>();
        paramMap.put("userId", getUserId());
        paramMap.put("page", String.valueOf(1));
        OkHttpUtils.post().url(ChatApi.GET_FEED_BACK_LIST)
                .addParams("param", ParamUtil.getParam(paramMap))
                .build().execute(new AjaxCallback<BaseResponse<PageBean<OpinionResultBean>>>() {
            @Override
            public void onResponse(BaseResponse<PageBean<OpinionResultBean>> response, int id) {
                if (response != null && response.m_istatus == NetCode.SUCCESS) {
                    PageBean<OpinionResultBean> pageBean = response.m_object;
                    if (pageBean != null) {
                        List<OpinionResultBean> focusBeans = pageBean.data;
                        if (focusBeans != null) {
                            opinionResultAdapter.loadData(focusBeans);
                        }
                    }
                }
            }

            @Override
            public void onError(Call call, Exception e, int id) {
                super.onError(call, e, id);
            }
        });
    }
}
