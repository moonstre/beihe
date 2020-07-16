package com.yiliao.chat.activity;

import android.graphics.BitmapFactory;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.yiliao.chat.R;
import com.yiliao.chat.adapter.ShareArticleRecyclerAdapter;
import com.yiliao.chat.base.BaseActivity;
import com.yiliao.chat.bean.ShareArticleBean;
import com.yiliao.chat.bean.ShareLayoutBean;
import com.yiliao.chat.constant.ChatApi;
import com.yiliao.chat.listener.OnItemClickListener;
import com.yiliao.chat.mob.MobCallback;
import com.yiliao.chat.mob.MobConst;
import com.yiliao.chat.mob.MobShareUtil;
import com.yiliao.chat.mob.ShareData;
import com.yiliao.chat.util.DialogUtil;
import com.yiliao.chat.util.ToastUtil;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;

/*
 * Copyright (C) 2018
 * 版权所有
 *
 * 功能描述：分享列表页面
 * 作者：
 * 创建时间：2018/8/27
 *
 * 修改人：
 * 修改描述：
 * 修改日期
 */
public class ShareArticleActivity extends BaseActivity {

    @BindView(R.id.content_rv)
    RecyclerView mContentRv;

    @Override
    protected View getContentView() {
        return inflate(R.layout.activity_share_article_layout);
    }

    @Override
    protected void onContentAdded() {
        setTitle(R.string.share_earn);
        initRecycler();
    }

    /**
     * 初始化RecyclerView
     */
    private void initRecycler() {
        List<ShareArticleBean> shareArticleBeans = new ArrayList<>();
        //1.小夫妻在家玩这个，半年后竟存款惊人！
        ShareArticleBean oneBean = new ShareArticleBean();
        oneBean.title = getResources().getString(R.string.little_couple);
        oneBean.des = getResources().getString(R.string.real_say);
        oneBean.resourceId = R.drawable.share_little_cuple;
        oneBean.targetUrl = ChatApi.JUMP_SPOUSE + getUserId();
        //2.在校女大学生兼职赚钱，三个月后竟给父母买车买房！   cps:  方便吗，你的好友【】邀请您视频私聊！  des: 点击接听
        ShareArticleBean twoBean = new ShareArticleBean();
        twoBean.title = getResources().getString(R.string.real_one);
        twoBean.des = getResources().getString(R.string.cps_des);
        twoBean.resourceId = R.drawable.icon_cps_friend;
        twoBean.targetUrl = ChatApi.CHAT_OFFICAL_URL;
        //3.APP招主播啦，无需才艺，轻松赚钱！收入高，提现快！
        ShareArticleBean threeBean = new ShareArticleBean();
        threeBean.title = getResources().getString(R.string.get_actor_title);
        threeBean.des = getResources().getString(R.string.get_actor_des);
        threeBean.resourceId = R.drawable.share_get_actor;
        threeBean.targetUrl = ChatApi.JUMP_ANCHORS + getUserId();
        //4.App【在家赚钱】盛大开业，任性发红包了！先到先得，领完为止！
        ShareArticleBean fourBean = new ShareArticleBean();
        fourBean.title = getResources().getString(R.string.share_qun_title_one);
        fourBean.des = getResources().getString(R.string.share_qun_des_one);
        fourBean.resourceId = R.drawable.share_game;
        fourBean.targetUrl = ChatApi.JUMP_GAME + getUserId();

        shareArticleBeans.add(oneBean);
        shareArticleBeans.add(twoBean);
        shareArticleBeans.add(threeBean);
        shareArticleBeans.add(fourBean);

        LinearLayoutManager gridLayoutManager = new LinearLayoutManager(this);
        mContentRv.setLayoutManager(gridLayoutManager);
        ShareArticleRecyclerAdapter mAdapter = new ShareArticleRecyclerAdapter(this);
        mContentRv.setAdapter(mAdapter);
        mAdapter.loadData(shareArticleBeans);
        mAdapter.setOnItemShareClickListener(new ShareArticleRecyclerAdapter.OnItemShareClickListener() {
            @Override
            public void onItemShareClick(final ShareArticleBean shareArticleBean) {
                DialogUtil.showShareDialog(ShareArticleActivity.this, new OnItemClickListener<ShareLayoutBean>() {
                    @Override
                    public void onItemClick(ShareLayoutBean bean, int position) {
                        switch (bean.id) {
                            case 1:
                                share(MobConst.Type.WX, shareArticleBean);
                                break;
                            case 2:
                                share(MobConst.Type.WX_PYQ, shareArticleBean);
                                break;
                            case 3:
                                share(MobConst.Type.QQ, shareArticleBean);
                                break;
                            case 4:
                                share(MobConst.Type.QZONE, shareArticleBean);
                                break;
                        }
                    }
                });
            }
        });
    }

    /**
     * 分享
     */
    private void share(String platType, ShareArticleBean shareArticleBean) {
//            String mineUrl = SharedPreferenceHelper.getAccountInfo(mContext).headUrl;

        MobShareUtil mobShareUtil = new MobShareUtil();
        ShareData data = new ShareData();
        data.setTitle(shareArticleBean.title);
        data.setDes(shareArticleBean.des);
        data.setImgData(BitmapFactory.decodeResource(getResources(), shareArticleBean.resourceId));
        data.setWebUrl(shareArticleBean.targetUrl);
        mobShareUtil.execute(platType, data, new MobCallback() {
            @Override
            public void onSuccess(Object data) {
                ToastUtil.showToast(getApplicationContext(), R.string.share_success);
            }

            @Override
            public void onError() {
//                ToastUtil.showToast(getApplicationContext(), R.string.share_fail);
            }

            @Override
            public void onCancel() {
//                ToastUtil.showToast(getApplicationContext(), R.string.share_cancel);
            }

            @Override
            public void onFinish() {

            }
        });
    }
}
