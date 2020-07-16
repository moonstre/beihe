package com.yiliao.chat.activity;

import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;

import com.yiliao.chat.R;
import com.yiliao.chat.base.BaseActivity;
import com.yiliao.chat.constant.Constant;

import butterknife.BindView;

public class AddPhoneSuccessActivity extends BaseActivity {
    @BindView(R.id.tvPhone)
    TextView tvPhone;

    @Override
    protected View getContentView() {
        return inflate(R.layout.activity_add_phone_success);
    }

    @Override
    protected void onContentAdded() {
        setTitle(R.string.add_phone_success_title);
        String phone = getIntent().getStringExtra(Constant.USER_PHONE);
        if (!TextUtils.isEmpty(phone) && phone.length() == 11) {
            tvPhone.setText(phone.substring(0, 3) + "****" + phone.substring(7));
        }
    }
}
