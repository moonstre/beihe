package com.yiliao.chat.helper;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.graphics.Point;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;

import com.yiliao.chat.R;
import com.yiliao.chat.activity.ChargeActivity;
import com.yiliao.chat.activity.GoldNotEnoughActivity;
import com.yiliao.chat.activity.InviteEarnActivity;
import com.yiliao.chat.activity.VipCenterActivity;

/*
 * Copyright (C) 2018
 * 版权所有
 *
 * 功能描述：余额不足的情况下,弹出dialog
 * 作者：
 * 创建时间：2018/8/15
 *
 * 修改人：
 * 修改描述：
 * 修改日期
 */
public class ChargeHelper {

    /**
     * 金币不足
     */
    public static void showSetCoverDialog(Activity activity) {
        Intent intent = new Intent(activity, GoldNotEnoughActivity.class);
        activity.startActivity(intent);
    }

    public static void showSetCoverDialogWithoutVip(Activity activity) {
        Intent intent = new Intent(activity, GoldNotEnoughActivity.class);
        activity.startActivity(intent);
    }

    /**
     * 显示设为封面dialog
     */
    /*public static void showSetCoverDialog(Activity activity) {
        final Dialog mDialog = new Dialog(activity, R.style.DialogStyle_Dark_Background);
        View view = LayoutInflater.from(activity).inflate(R.layout.dialog_money_not_enough_layout, null);
        setChargeDialogView(view, mDialog, activity);
        mDialog.setContentView(view);
        Point outSize = new Point();
        activity.getWindowManager().getDefaultDisplay().getSize(outSize);
        Window window = mDialog.getWindow();
        if (window != null) {
            WindowManager.LayoutParams params = window.getAttributes();
            params.width = outSize.x;
            window.setGravity(Gravity.BOTTOM); // 此处可以设置dialog显示的位置
            window.setWindowAnimations(R.style.BottomPopupAnimation); // 添加动画
        }
        mDialog.setCanceledOnTouchOutside(false);
        if (!activity.isFinishing()) {
            mDialog.show();
        }
    }*/
    private static void setChargeDialogView(View view, final Dialog mDialog, final Activity activity) {
        //取消
        TextView cancel_tv = view.findViewById(R.id.cancel_tv);
        cancel_tv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mDialog.dismiss();
            }
        });
        //充值
        TextView charge_tv = view.findViewById(R.id.charge_tv);
        charge_tv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(activity, ChargeActivity.class);
                activity.startActivity(intent);
                mDialog.dismiss();
            }
        });
        //分享
        TextView share_tv = view.findViewById(R.id.share_tv);
        share_tv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(activity, InviteEarnActivity.class);
                activity.startActivity(intent);
                mDialog.dismiss();
            }
        });

    }

    public static void showInputInviteCodeDialog(Activity activity) {
        final Dialog mDialog = new Dialog(activity, R.style.DialogStyle_Dark_Background);
        @SuppressLint("InflateParams")
        View view = LayoutInflater.from(activity).inflate(R.layout.dialog_vip_info, null);
        setInviteCodeView(view, mDialog, activity);
        mDialog.setContentView(view);
        Point outSize = new Point();
        activity.getWindowManager().getDefaultDisplay().getSize(outSize);
        Window window = mDialog.getWindow();
        if (window != null) {
            WindowManager.LayoutParams params = window.getAttributes();
            params.width = outSize.x;
            window.setGravity(Gravity.CENTER); // 此处可以设置dialog显示的位置
        }
        mDialog.setCanceledOnTouchOutside(true);
        if (!activity.isFinishing()) {
            mDialog.show();
        }
    }

    private static void setInviteCodeView(View view, final Dialog mDialog, final Activity activity) {
        view.findViewById(R.id.tvCancel).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mDialog.dismiss();
            }
        });
        view.findViewById(R.id.tvConfirm).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                activity.startActivity(new Intent(activity, VipCenterActivity.class));
                mDialog.dismiss();
            }
        });
    }
}
