package com.yiliao.chat.activity;

import android.view.View;

import com.yiliao.chat.R;
import com.yiliao.chat.base.BaseActivity;

public class GoToHomeServiceActivity extends BaseActivity {
    @Override
    protected View getContentView() {
        return inflate(R.layout.go_to_home_service);
    }

    @Override
    protected void onContentAdded() {
        setTitle("上门服务");
    }
}
