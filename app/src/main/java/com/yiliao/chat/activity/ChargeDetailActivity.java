package com.yiliao.chat.activity;

import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import com.gyf.barlibrary.ImmersionBar;
import com.yiliao.chat.R;
import com.yiliao.chat.constant.Constant;
import com.yiliao.chat.fragment.invite.ChargeDetailOneFragment;
import com.yiliao.chat.fragment.invite.ChargeDetailTwoFragment;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;

public class ChargeDetailActivity extends AppCompatActivity {
    @BindView(R.id.content_vp)
    ViewPager mContentVp;
    //tabs
    @BindView(R.id.tabs)
    TabLayout mTabLayout;

    //注解
    private Unbinder mUnbinder;
    //沉浸式状态栏
    protected ImmersionBar mImmersionBar;

    private int t_id;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_charge_detail);
        mUnbinder = ButterKnife.bind(this);
        setStatusBarStyle();
        t_id = getIntent().getIntExtra(Constant.PASS_USER_ID, 0);
        initStart();
    }

    private void initStart() {
        final List<String> mTitle = new ArrayList<>();
        mTitle.add(getString(R.string.charge_detail_title_one));
        mTitle.add(getString(R.string.charge_detail_title_two));
        final List<Fragment> list = new ArrayList<>();
        ChargeDetailOneFragment oneFragment = new ChargeDetailOneFragment();
        oneFragment.setTid(t_id);
        ChargeDetailTwoFragment twoFragment = new ChargeDetailTwoFragment();
        twoFragment.setTid(t_id);
        list.add(0, oneFragment);
        list.add(1, twoFragment);
        mContentVp.setAdapter(new FragmentPagerAdapter(getSupportFragmentManager()) {
            @Override
            public Fragment getItem(int position) {
                return list.get(position);
            }

            @Override
            public int getCount() {
                return list.size();
            }

            @Nullable
            @Override
            public CharSequence getPageTitle(int position) {
                return mTitle.get(position);
            }
        });
        mTabLayout.setupWithViewPager(mContentVp);
        mContentVp.setOffscreenPageLimit(2);
    }

    @Override
    protected void onDestroy() {
        if (mUnbinder != null) {
            mUnbinder.unbind();
        }
        if (mImmersionBar != null) {
            mImmersionBar.destroy();
        }
        super.onDestroy();
    }

    protected void setStatusBarStyle() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {//如果不是沉浸式,就设置为黑色字体
            getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
            return;
        }
        mImmersionBar = ImmersionBar.with(this);
        mImmersionBar.statusBarDarkFont(true).navigationBarColor(R.color.black).init();
    }

    @OnClick({R.id.back_black_iv})
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.back_black_iv: {
                finish();
                break;
            }
        }
    }
}
