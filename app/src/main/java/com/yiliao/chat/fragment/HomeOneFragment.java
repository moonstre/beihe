package com.yiliao.chat.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.widget.ImageView;

import com.yiliao.chat.R;
import com.yiliao.chat.activity.SearchActivity;
import com.yiliao.chat.adapter.HomeFragmentAdapter;
import com.yiliao.chat.base.AppManager;
import com.yiliao.chat.base.BaseFragment;
import com.yiliao.chat.bean.ChatUserInfo;
import com.yiliao.chat.constant.Constant;
import com.yiliao.chat.fragment.near.NearFragment;
import com.yiliao.chat.helper.SharedPreferenceHelper;
import com.yiliao.chat.util.WordUtil;
import com.yiliao.chat.view.ViewPagerIndicator;

import java.util.ArrayList;
import java.util.List;

/*
 * Copyright (C) 2018
 * 版权所有
 *
 * 功能描述：首页的Fragment改版One
 * 作者：
 * 创建时间：2019/3/5
 *
 * 修改人：
 * 修改描述：
 * 修改日期
 */
public class HomeOneFragment extends BaseFragment implements View.OnClickListener {

    public HomeOneFragment() {

    }

    //角色是用户还是主播
    private int mRole = 0;
    //ViewPager
    private ViewPager mContentVp;
    private ViewPagerIndicator mIndicator;

    @Override
    protected int initLayout() {
        return R.layout.fragment_home_one_layout;
    }

    @Override
    protected void initView(View view, Bundle savedInstanceState) {
        mIndicator = view.findViewById(R.id.indicator);
        //viewPager
        mContentVp = view.findViewById(R.id.content_vp);
        //搜索
        ImageView mCategoryIv = view.findViewById(R.id.category_iv);

        mCategoryIv.setOnClickListener(this);

        //初始化
        initViewPager();
    }

    /**
     * 初始化viewPager
     */
    private void initViewPager() {
        //设置主播 或用户能看到的项目
        mRole = getUserRole();

        List<Fragment> mFragmentList = new ArrayList<>();

        if (Constant.hideNearby()){
            //女神 新人 推荐 男神
            GoddessFragment mGoddessFragment = new GoddessFragment();
            mGoddessFragment.setSex(0);
            NewManFragment newManFragment = new NewManFragment();
            GirlFragment mGirlFragment = new GirlFragment();
            GoddessFragment mMalegodFragment = new GoddessFragment();
            mMalegodFragment.setSex(1);
            NearFragment mNearFragment = new NearFragment();
            mFragmentList.add(mGoddessFragment);
            mFragmentList.add(newManFragment);
            mFragmentList.add(mGirlFragment);
            mFragmentList.add(mMalegodFragment);
            mFragmentList.add(mNearFragment);
            mIndicator.setTitles(new String[]{
                    WordUtil.getString(R.string.goddess),
                    WordUtil.getString(R.string.new_man),
                    WordUtil.getString(R.string.recommend),
                    WordUtil.getString(R.string.mandess),
                    WordUtil.getString(R.string.near)
            });
        }else {
            if (Constant.showHomeForFenDie()) {
//            女神 新人 推荐 男神
                GoddessFragment mGoddessFragment = new GoddessFragment();
                mGoddessFragment.setSex(0);
                NewManFragment newManFragment = new NewManFragment();
                GirlFragment mGirlFragment = new GirlFragment();
                ActFragment mMalegodFragment = new ActFragment();
                mMalegodFragment.setSex(1);
                mFragmentList.add(mGoddessFragment);
                mFragmentList.add(newManFragment);
                mFragmentList.add(mGirlFragment);
                mFragmentList.add(mMalegodFragment);
                mIndicator.setTitles(new String[]{
                        WordUtil.getString(R.string.goddess),
                        WordUtil.getString(R.string.new_man),
                        WordUtil.getString(R.string.recommend),
                        WordUtil.getString(R.string.act)
                });
            }else {
                FocusFragment focusFragment = new FocusFragment();


                ZBRecommendFragment zbRecommendFragment = new ZBRecommendFragment();

                GirlFragment mGirlFragment = new GirlFragment();
                NewManFragment newManFragment = new NewManFragment();
                NearFragment mNearFragment = new NearFragment();
                VideoFragment mVideoFragment = new VideoFragment();
                ActivtyFragment mactivityFragment = new ActivtyFragment();
                //男神
                GoddessFragment mMalegodFragment = new GoddessFragment();
                mMalegodFragment.setSex(1);

                if (mRole == 1 && getUserSex() != 1) {//女主播
                    FansFragment mFansFragment = new FansFragment();

                    if (!Constant.hideFans()){
                        mFragmentList.add(mFansFragment);
                    }


                    mFragmentList.add(focusFragment);
                    if (Constant.showZBRecommend()){
                        mFragmentList.add(zbRecommendFragment);
                    }else{
                        mFragmentList.add(mGirlFragment);
                    }

                    mFragmentList.add(newManFragment);

                    if (Constant.showGoddess()){
                        mFragmentList.add(mMalegodFragment);
                    }
                    if (!Constant.hideHomeNear()) {
                        mFragmentList.add(mNearFragment);
                    }
                    if (!Constant.hideHomeVideo()) {
                        mFragmentList.add(mVideoFragment);
                    }

                    if (!Constant.hideHomeActivity()){
                        mFragmentList.add(mactivityFragment);
                    }

                    if (Constant.hideHomeNear()) {
                        if (Constant.hideHomeVideo()) {
                            mIndicator.setTitles(new String[]{
                                    WordUtil.getString(R.string.male_fan),
                                    WordUtil.getString(R.string.focus),
                                    WordUtil.getString(R.string.recommend),
                                    WordUtil.getString(R.string.new_man)
                            });

                        } else {
                            mIndicator.setTitles(new String[]{
                                    WordUtil.getString(R.string.male_fan),
                                    WordUtil.getString(R.string.focus),
                                    WordUtil.getString(R.string.recommend),
                                    WordUtil.getString(R.string.new_man),
                                    WordUtil.getString(R.string.video_des_one)
                            });
                        }
                    } else {
                        if (Constant.hideHomeVideo()) {
                            if (Constant.hideFans()){
                                if (Constant.showGoddess()){
                                    mIndicator.setTitles(new String[]{

                                            WordUtil.getString(R.string.focus),
                                            WordUtil.getString(R.string.recommend),
                                            WordUtil.getString(R.string.new_man),
                                            WordUtil.getString(R.string.mandess),
                                            WordUtil.getString(R.string.near)
                                    });
                                }else {
                                    mIndicator.setTitles(new String[]{

                                            WordUtil.getString(R.string.focus),
                                            WordUtil.getString(R.string.recommend),
                                            WordUtil.getString(R.string.new_man),
                                            WordUtil.getString(R.string.near)
                                    });
                                }

                            }else {
                                mIndicator.setTitles(new String[]{
                                        WordUtil.getString(R.string.male_fan),
                                        WordUtil.getString(R.string.focus),
                                        WordUtil.getString(R.string.recommend),
                                        WordUtil.getString(R.string.new_man),
                                        WordUtil.getString(R.string.near)
                                });
                            }

                        } else {
                            if (Constant.hideHomeActivity()){
                                mIndicator.setTitles(new String[]{
                                        WordUtil.getString(R.string.male_fan),
                                        WordUtil.getString(R.string.focus),
                                        WordUtil.getString(R.string.recommend),
                                        WordUtil.getString(R.string.new_man),
                                        WordUtil.getString(R.string.near),
                                        WordUtil.getString(R.string.video_des_one)
                                });

                            }

                        }
                    }

                } else {//用户
                    mFragmentList.add(focusFragment);
                    if (Constant.showZBRecommend()){
                        mFragmentList.add(zbRecommendFragment);
                    }else{
                        mFragmentList.add(mGirlFragment);
                    }
                    mFragmentList.add(newManFragment);

                    if (Constant.showGoddess()){
                        mFragmentList.add(mMalegodFragment);
                    }

                    if (!Constant.hideHomeNear()) {
                        mFragmentList.add(mNearFragment);
                    }
                    if (!Constant.hideHomeVideo()) {
                        mFragmentList.add(mVideoFragment);
                    }
                    if (!Constant.hideHomeActivity()){
                        mFragmentList.add(mactivityFragment);
                    }

                        if (Constant.hideHomeVideo()) {

                            if (Constant.showGoddess()){
                                mIndicator.setTitles(new String[]{
                                        WordUtil.getString(R.string.focus),
                                        WordUtil.getString(R.string.recommend),
                                        WordUtil.getString(R.string.new_man),
                                        WordUtil.getString(R.string.mandess),
                                        WordUtil.getString(R.string.near)
                                });
                            }else {
                                mIndicator.setTitles(new String[]{
                                        WordUtil.getString(R.string.focus),
                                        WordUtil.getString(R.string.recommend),
                                        WordUtil.getString(R.string.new_man),
                                        WordUtil.getString(R.string.near)
                                });
                            }
                        } else {
                            if (Constant.hideHomeActivity()){
                                mIndicator.setTitles(new String[]{
                                        WordUtil.getString(R.string.focus),
                                        WordUtil.getString(R.string.recommend),
                                        WordUtil.getString(R.string.new_man),
                                        WordUtil.getString(R.string.near),
                                        WordUtil.getString(R.string.video_des_one)
                                });
                            }
                        }
                }
            }
        }


        mIndicator.setViewPager(mContentVp);
        HomeFragmentAdapter mFragmentPagerAdapter = new HomeFragmentAdapter(getChildFragmentManager());
        mContentVp.setAdapter(mFragmentPagerAdapter);
        mFragmentPagerAdapter.loadData(mFragmentList);

        if (Constant.showHomeForFenDie()) {
            mContentVp.setOffscreenPageLimit(4);
            mContentVp.setCurrentItem(2);
        } else if (Constant.hideNearby()){
            mContentVp.setOffscreenPageLimit(5);
            mContentVp.setCurrentItem(2);
        }else {
            if (mRole == 1 && getUserSex() != 1) {//是主播
                mContentVp.setOffscreenPageLimit(4);
                mContentVp.setCurrentItem(1);
            } else {
                mContentVp.setOffscreenPageLimit(4);
                mContentVp.setCurrentItem(1);
            }
        }

    }

    @Override
    protected void onFirstVisible() {
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.category_iv: {//搜索
                Intent intent = new Intent(getContext(), SearchActivity.class);
                startActivity(intent);
                break;
            }
        }
    }

    /**
     * 获取角色
     */
    private int getUserRole() {
        if (AppManager.getInstance() != null) {
            ChatUserInfo userInfo = AppManager.getInstance().getUserInfo();
            if (userInfo != null) {
                //1 主播 0 用户
                return userInfo.t_role;
            } else {
                return SharedPreferenceHelper.getAccountInfo(mContext.getApplicationContext()).t_role;
            }
        }
        return 0;
    }

    /**
     * 获取用户性别
     */
    private int getUserSex() {
        if (AppManager.getInstance() != null) {
            ChatUserInfo userInfo = AppManager.getInstance().getUserInfo();
            if (userInfo != null) {
                //0.女，1.男
                int sex = userInfo.t_sex;
                return sex != 2 ? sex : 0;
            } else {
                int sex = SharedPreferenceHelper.getAccountInfo(mContext.getApplicationContext()).t_sex;
                return sex != 2 ? sex : 0;
            }
        }
        return 0;
    }

}
