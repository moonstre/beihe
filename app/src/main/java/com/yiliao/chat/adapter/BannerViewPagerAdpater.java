package com.yiliao.chat.adapter;

import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.mob.tools.gui.ViewPagerAdapter;
import com.yiliao.chat.R;
import com.yiliao.chat.activity.CommonWebViewActivity;
import com.yiliao.chat.activity.HelpCenterActivity;
import com.yiliao.chat.activity.InviteEarnActivity;
import com.yiliao.chat.activity.PhoneNaviActivity;
import com.yiliao.chat.base.BaseActivity;
import com.yiliao.chat.bean.BannerBean;
import com.yiliao.chat.constant.Constant;
import com.yiliao.chat.helper.ImageLoadHelper;
import com.yiliao.chat.helper.SharedPreferenceHelper;
import com.yiliao.chat.util.DevicesUtil;

import java.util.ArrayList;
import java.util.List;

public class BannerViewPagerAdpater extends PagerAdapter {
    private BaseActivity mContext;
    private List<BannerBean> mBannerBeans = new ArrayList<>();
    private ViewPager mPagerView;

    public BannerViewPagerAdpater(BaseActivity mContext,ViewPager mPagerView){
        this.mPagerView=mPagerView;
        this.mContext=mContext;


    }

    void loadBannerData(List<BannerBean> beans) {
        mBannerBeans = beans;
        notifyDataSetChanged();

    }
    @Override
    public int getCount() {
        return mBannerBeans.size();
    }


    /**
     * 判断是否使用缓存, 如果返回的是true, 使用缓存. 不去调用instantiateItem方法创建一个新的对象
     */
    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == object;
    }

    /**
     * 初始化一个条目
     * @param container
     * @param position 当前需要加载条目的索引
     * @return
     */
    @Override
    public Object instantiateItem(ViewGroup container, final int position) {
        // 把position对应位置的ImageView添加到ViewPager中
        View itemView = LayoutInflater.from(mContext).inflate(R.layout.item_top_banner_layout,
                null, false);
        ImageView iv = itemView.findViewById(R.id.content_iv);
        if (mBannerBeans.size()==0){
            iv.setVisibility(View.GONE);
        }


        int overWidth = DevicesUtil.getScreenW(mContext)-20;
        int overHeight = DevicesUtil.getScreenW(mContext)*19/32;
        ImageLoadHelper.glideShowCornerImageWithUrl(mContext, mBannerBeans.get(position).t_img_url, iv,
                5, overWidth, overHeight);
        final String url=mBannerBeans.get(position).t_link_url;
        iv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (Constant.hideMainTurntable()){
                    if (mBannerBeans.get(position).t_link_url.contains("zhuanpan")){
                        Intent intent = new Intent(mContext, CommonWebViewActivity.class);
//                        intent.putExtra(Constant.TITLE, "大转盘");
                        intent.putExtra(Constant.URL, mBannerBeans.get(position).t_link_url +"?userId="+ SharedPreferenceHelper.getAccountInfo(mContext).t_id);
                        mContext.startActivity(intent);
                    }

                }else {
                    if (!TextUtils.isEmpty(url)) {
                        if (url.contains("http")) {
                            Intent intent = new Intent();
                            intent.setAction("android.intent.action.VIEW");
                            Uri content_url = Uri.parse(url);
                            intent.setData(content_url);
                            mContext.startActivity(intent);
                        } else if (url.contains("InviteEarn")) {//跳转内部
                            Intent intent = new Intent(mContext, InviteEarnActivity.class);
                            mContext.startActivity(intent);
                        } else if (url.contains("PhoneNavi")) {//手机指南
                            Intent intent = new Intent(mContext, PhoneNaviActivity.class);
                            mContext.startActivity(intent);
                        } else if (url.contains("HelpCenter")) {//帮助中心
                            Intent intent = new Intent(mContext, HelpCenterActivity.class);
                            mContext.startActivity(intent);
                        }
                }
                }
            }
        });
//        iv.mContentIv.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                if (!TextUtils.isEmpty(url)) {
//                    if (url.contains("http")) {
//                        Intent intent = new Intent();
//                        intent.setAction("android.intent.action.VIEW");
//                        Uri content_url = Uri.parse(url);
//                        intent.setData(content_url);
//                        mContext.startActivity(intent);
//                    } else if (url.contains("InviteEarn")) {//跳转内部
//                        Intent intent = new Intent(mContext, InviteEarnActivity.class);
//                        mContext.startActivity(intent);
//                    } else if (url.contains("PhoneNavi")) {//手机指南
//                        Intent intent = new Intent(mContext, PhoneNaviActivity.class);
//                        mContext.startActivity(intent);
//                    } else if (url.contains("HelpCenter")) {//帮助中心
//                        Intent intent = new Intent(mContext, HelpCenterActivity.class);
//                        mContext.startActivity(intent);
//                    }
//                }
//            }
//        });
        container.addView(itemView);
        // 把当前添加ImageView返回回去.
        return itemView;
    }

    //刷新数据
    @Override
    public int getItemPosition(@NonNull Object object) {
        return POSITION_NONE;
    }

    /**
     * 销毁一个条目
     * position 就是当前需要被销毁的条目的索引
     */
    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        // 把ImageView从ViewPager中移除掉
        container.removeView((View) object);

    }

}
