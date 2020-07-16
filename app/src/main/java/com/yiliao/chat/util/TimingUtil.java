package com.yiliao.chat.util;

import android.os.Handler;
import android.support.v4.view.ViewPager;

public class TimingUtil {


    private static final int TIME = 3000;

    private Handler mHandler;

    private int itemPosition;


    private int mCount ;

    private ViewPager viewPager;

    public TimingUtil(ViewPager viewPager,int mCount){
        this.viewPager=viewPager;
        this.mCount=mCount;
    }

    public  void Run(){
        if (mHandler==null){
            mHandler = new Handler();
        }
        mHandler.postDelayed(runnableForViewPager,TIME);

    }

    /**
     * ViewPager的定时器
     */
    Runnable runnableForViewPager = new Runnable() {
        @Override
        public void run() {
            try {
                itemPosition++;
                mHandler.postDelayed(this, TIME);
                viewPager.setCurrentItem(itemPosition % mCount);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    };


}
