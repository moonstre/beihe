package com.yiliao.chat.activity;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.widget.TextView;

import com.yiliao.chat.R;
import com.yiliao.chat.base.BaseActivity;
import com.yiliao.chat.bean.ActorInfoBean;
import com.yiliao.chat.bean.ChargeBean;
import com.yiliao.chat.bean.CoverUrlBean;
import com.yiliao.chat.bean.InfoRoomBean;
import com.yiliao.chat.bean.LabelBean;
import com.yiliao.chat.constant.Constant;
import com.yiliao.chat.fragment.AllOrderFragment;
import com.yiliao.chat.fragment.NoStartFragment;

import org.json.JSONException;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;

public class MyOrderActivity extends BaseActivity {
    public AllOrderFragment allOrderFragment,allOrderFragment1,allOrderFragment2,allOrderFragment3;
    @BindView(R.id.content_vp_order)
    ViewPager mContentVp;
    //tabs
    @BindView(R.id.tabs_order)
    TabLayout mTabLayout;
    @Override
    protected View getContentView() {
        return inflate(R.layout.my_order_layout);
    }

    @Override
    protected void onContentAdded() {
        setTitle("我的订单");
        bindViewPager();

    }

    /**
     * 初始化下方资料ViewPager
     */
    private void bindViewPager() {
        //标题
        final List<String> mTitle = new ArrayList<>();
        mTitle.add(getString(R.string.all));
        mTitle.add(getString(R.string.no_start));
        mTitle.add(getString(R.string.starting));
        mTitle.add(getString(R.string.over));
        final List<Fragment> list = new ArrayList<>();
        allOrderFragment=new AllOrderFragment("0");
        allOrderFragment1=new AllOrderFragment("1");
        allOrderFragment2=new AllOrderFragment("2");
        allOrderFragment3=new AllOrderFragment("3");
//        mContentVp.setOffscreenPageLimit(1);
        list.add(0, allOrderFragment);
        list.add(1, allOrderFragment1);
        list.add(2, allOrderFragment2);
        list.add(3, allOrderFragment3);
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
        mContentVp.setOffscreenPageLimit(4);

        //资料页加载数据
//        if (mPersonInfoFragment != null && mPersonInfoFragment.mIsViewPrepared) {
//            mPersonInfoFragment.loadData(bean);
//        }
    }

    @Override
    protected void onResume() {
        super.onResume();
//        bindViewPager();
    }

}
