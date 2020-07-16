package com.yiliao.chat.base;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.yiliao.chat.bean.GirlListBean;
import com.yiliao.chat.bean.PageBean;
import com.yiliao.chat.constant.ChatApi;
import com.yiliao.chat.net.AjaxCallback;
import com.yiliao.chat.net.NetCode;
import com.yiliao.chat.util.ParamUtil;
import com.zhy.http.okhttp.OkHttpUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 懒加载
 */
public abstract class BaseFragment extends LazyFragment {

    public BaseActivity mContext;

    public BaseFragment() {

    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mContext = (BaseActivity) getActivity();
        View view = LayoutInflater.from(getContext()).inflate(initLayout(), container, false);
        initView(view, savedInstanceState);
        mIsViewPrepared = true;
        return view;
    }

    /**
     * 初始化layout
     */
    protected abstract int initLayout();

    /**
     * 初始化view
     */
    protected abstract void initView(View view, Bundle savedInstanceState);

    /**
     * 第一次可见的操作
     */
    protected abstract void onFirstVisible();

    @Override
    protected void onFirstVisibleToUser() {
        onFirstVisible();
        mIsDataLoadCompleted = true;
    }

}
