package com.yiliao.chat.activity;

import android.view.View;

import com.yiliao.chat.R;
import com.yiliao.chat.base.BaseActivity;

/*
 * Copyright (C) 2018 
 * 版权所有
 *
 * 功能描述：魅族手机指南页面
 * 作者：
 * 创建时间：2018/11/13
 *
 * 修改人：
 * 修改描述：
 * 修改日期
 */
public class PhoneMeizuActivity extends BaseActivity {

    @Override
    protected View getContentView() {
        return inflate(R.layout.activity_phone_meizu_layout);
    }

    @Override
    protected void onContentAdded() {
        setTitle(R.string.meizu_des_one);
    }

}
