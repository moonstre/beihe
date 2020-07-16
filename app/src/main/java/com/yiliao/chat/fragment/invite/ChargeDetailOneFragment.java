package com.yiliao.chat.fragment.invite;

import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.yiliao.chat.R;
import com.yiliao.chat.adapter.UserCapitalRecyclerAdapter;
import com.yiliao.chat.base.BaseCompactFragment;
import com.yiliao.chat.base.BaseResponse;
import com.yiliao.chat.bean.PageBean;
import com.yiliao.chat.bean.UserCapitalBean;
import com.yiliao.chat.constant.ChatApi;
import com.yiliao.chat.net.AjaxCallback;
import com.yiliao.chat.net.NetCode;
import com.yiliao.chat.util.ParamUtil;
import com.yiliao.chat.util.ToastUtil;
import com.zhy.http.okhttp.OkHttpUtils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import okhttp3.Call;

public class ChargeDetailOneFragment extends BaseCompactFragment {
    private int t_id;
    private UserCapitalRecyclerAdapter adapter;

    public ChargeDetailOneFragment() {

    }

    public void setTid(int t_id) {
        this.t_id = t_id;
    }

    @Override
    protected int initLayout() {
        return R.layout.fragment_charge_detail_one;
    }

    @Override
    protected void initView(View view, Bundle savedInstanceState) {
        RecyclerView content_rv = view.findViewById(R.id.content_rv);
        content_rv.setLayoutManager(new LinearLayoutManager(getContext()));
        adapter = new UserCapitalRecyclerAdapter(mContext);
        content_rv.setAdapter(adapter);
    }

    @Override
    protected void onFirstVisible() {
        getShareUserCapitalList(t_id);
    }

    private void getShareUserCapitalList(int t_id) {
        Map<String, String> paramMap = new HashMap<>();
        paramMap.put("userId", String.valueOf(t_id));
        paramMap.put("type", "1");
        OkHttpUtils.post().url(ChatApi.GET_SHARE_USER_CAPITAL_LIST)
                .addParams("param", ParamUtil.getParam(paramMap))
                .build().execute(new AjaxCallback<BaseResponse<PageBean<UserCapitalBean>>>() {
            @Override
            public void onResponse(BaseResponse<PageBean<UserCapitalBean>> response, int id) {
                if (mContext.isFinishing()) {
                    return;
                }
                if (response != null && response.m_istatus == NetCode.SUCCESS) {
                    PageBean<UserCapitalBean> pageBean = response.m_object;
                    if (pageBean != null) {
                        List<UserCapitalBean> userCapitalBeans = pageBean.data;
                        if (userCapitalBeans != null) {
                            adapter.loadData(userCapitalBeans);
                        }
                    } else {
                        ToastUtil.showToast(mContext, R.string.system_error);
                    }
                } else {
                    ToastUtil.showToast(mContext, R.string.system_error);
                }
            }

            @Override
            public void onError(Call call, Exception e, int id) {
                super.onError(call, e, id);
                ToastUtil.showToast(mContext, R.string.system_error);
            }
        });
    }
}
