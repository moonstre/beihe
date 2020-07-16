package com.yiliao.chat.myactivity;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import com.gyf.barlibrary.ImmersionBar;
import com.yiliao.chat.R;
import com.yiliao.chat.base.BaseActivity;
import com.yiliao.chat.myactivity.fragment.MyEstablishFragment;
import com.yiliao.chat.myactivity.fragment.MySignUpFragment;
import com.yiliao.chat.util.WordUtil;
import com.yiliao.chat.view.ViewPagerIndicator;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/*
* 我的活动
* */

public class MyActivityActivity extends FragmentActivity {

    //沉浸式状态栏
    protected ImmersionBar mImmersionBar;

    @BindView(R.id.MyActivity_viewpager)
    ViewPager mViewPager;

    @BindView(R.id.indicator)
    ViewPagerIndicator mIndicator;

    private List<Fragment> mfragment = new ArrayList<>();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.myactivity);
        ButterKnife.bind(this);

        setStatusBarStyle();
        getInitView();
    }

    //初始化
    public void getInitView(){
        mIndicator.setTitles(new String[]{"我发起的","我报名的"
        });
        mIndicator.setViewPager(mViewPager);
        mfragment.add(new MyEstablishFragment());
        mfragment.add(new MySignUpFragment());

        mViewPager.setAdapter(new FragmentStatePagerAdapter(getSupportFragmentManager()) {
            @Override
            public Fragment getItem(int position) {
                return mfragment.get(position);
            }

            @Override
            public int getCount() {
                return mfragment.size();
            }
        });
    }

    //沉浸式状态栏
    protected void setStatusBarStyle() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {//如果不是沉浸式,就设置为黑色字体
            getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
            return;
        }
        mImmersionBar = ImmersionBar.with(this);
        mImmersionBar.statusBarDarkFont(true).navigationBarColor(R.color.black).init();


    }

    @OnClick({R.id.MyActivity_Establish,R.id.MyActivity_Back})
    public void OnClick(View view){
        switch (view.getId()){
            case R.id.MyActivity_Establish://发起
                Intent intent = new Intent(getApplicationContext(),EstablishActivity.class);
                intent.putExtra("TYPE","Add");
                startActivity(intent
                );
                break;
            case R.id.MyActivity_Back:
                finish();
                break;
        }
    }

}
