package com.yiliao.chat.activity;

import android.view.View;

import com.yiliao.chat.R;
import com.yiliao.chat.base.BaseActivity;

/*
 * Copyright (C) 2018
 * 版权所有
 *
 * 功能描述：华为手机指南页面
 * 作者：
 * 创建时间：2018/11/13
 *
 * 修改人：
 * 修改描述：
 * 修改日期
 */
public class PhoneHuaweiActivity extends BaseActivity {

    @Override
    protected View getContentView() {
        return inflate(R.layout.activity_phone_huawei_layout);
    }

    @Override
    protected void onContentAdded() {
        setTitle(R.string.huawei_des_one);
    }

}
