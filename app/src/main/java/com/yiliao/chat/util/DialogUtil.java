package com.yiliao.chat.util;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.graphics.Point;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.SparseArray;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.yiliao.chat.R;
import com.yiliao.chat.adapter.ShareRecyclerAdapter;
import com.yiliao.chat.bean.ShareLayoutBean;
import com.yiliao.chat.helper.SharedPreferenceHelper;
import com.yiliao.chat.listener.OnItemClickListener;

import java.util.ArrayList;
import java.util.List;

/*
 * Copyright (C) 2018
 * 版权所有
 *
 * 功能描述：Dialog工具类
 * 作者：
 * 创建时间：2018/6/14
 *
 * 修改人：
 * 修改描述：
 * 修改日期
 */
public class DialogUtil {

    /**
     * 进度条
     */
    public static Dialog showLoadingDialog(Context context) {
        Dialog dialog = new Dialog(context, R.style.DialogStyle);
        dialog.setContentView(R.layout.dialog_progress_loading);
        dialog.setCanceledOnTouchOutside(false);
        return dialog;
    }

    public static void showStringArrayDialog(Context context, SparseArray<String> array, final StringArrayDialogCallback callback) {
        final Dialog dialog = new Dialog(context, R.style.dialog);
        dialog.setContentView(R.layout.dialog_string_array);
        dialog.setCancelable(true);
        dialog.setCanceledOnTouchOutside(true);
        Window window = dialog.getWindow();
        window.setWindowAnimations(R.style.bottomToTopAnim);
        WindowManager.LayoutParams params = window.getAttributes();
        params.width = WindowManager.LayoutParams.MATCH_PARENT;
        params.height = WindowManager.LayoutParams.WRAP_CONTENT;
        params.gravity = Gravity.BOTTOM;
        window.setAttributes(params);
        LinearLayout container = (LinearLayout) dialog.findViewById(R.id.container);
        View.OnClickListener itemListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TextView textView = (TextView) v;
                if (callback != null) {
                    callback.onItemClick(textView.getText().toString(), (int) v.getTag());
                }
                dialog.dismiss();
            }
        };
        for (int i = 0, length = array.size(); i < length; i++) {
            TextView textView = new TextView(context);
            textView.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, DevicesUtil.dp2px(context, 54)));
            textView.setTextColor(0xff323232);
            textView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 18);
            textView.setGravity(Gravity.CENTER);
            textView.setText(array.valueAt(i));
            textView.setTag(array.keyAt(i));
            textView.setOnClickListener(itemListener);
            container.addView(textView);
            if (i != length - 1) {
                View v = new View(context);
                v.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, DevicesUtil.dp2px(context, 1)));
                v.setBackgroundColor(0xfff5f5f5);
                container.addView(v);
            }
        }
        dialog.findViewById(R.id.btn_cancel).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        dialog.show();
    }

    public static void showShareDialog(Activity context, OnItemClickListener<ShareLayoutBean> onItemClickListener) {
        final Dialog mDialog = new Dialog(context, R.style.DialogStyle_Dark_Background);
        @SuppressLint("InflateParams")
        View view = LayoutInflater.from(context).inflate(R.layout.dialog_share_layout, null);
        setShareDialogView(context, view, mDialog, onItemClickListener);
        mDialog.setContentView(view);
        Point outSize = new Point();
        context.getWindowManager().getDefaultDisplay().getSize(outSize);
        Window window = mDialog.getWindow();
        if (window != null) {
            WindowManager.LayoutParams params = window.getAttributes();
            params.width = outSize.x;
            window.setGravity(Gravity.BOTTOM); // 此处可以设置dialog显示的位置
            window.setWindowAnimations(R.style.BottomPopupAnimation); // 添加动画
        }
        mDialog.setCanceledOnTouchOutside(true);
        if (!context.isFinishing()) {
            mDialog.show();
        }
    }

    private static void setShareDialogView(Activity context, View view, final Dialog mDialog, final OnItemClickListener<ShareLayoutBean> onItemClickListener) {
        TextView cancel_tv = view.findViewById(R.id.cancel_tv);
        cancel_tv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mDialog.dismiss();
            }
        });

        RecyclerView content_rv = view.findViewById(R.id.content_rv);
        GridLayoutManager manager = new GridLayoutManager(context.getBaseContext(), 4);
        content_rv.setLayoutManager(manager);
        ShareRecyclerAdapter adapter = new ShareRecyclerAdapter(context);
        content_rv.setAdapter(adapter);
        adapter.setOnItemClickListener(new OnItemClickListener<ShareLayoutBean>() {
            @Override
            public void onItemClick(ShareLayoutBean bean, int position) {
                onItemClickListener.onItemClick(bean, position);
                mDialog.dismiss();
            }
        });

        List<ShareLayoutBean> beans = new ArrayList<>();
        String shareType = SharedPreferenceHelper.getShareType(context);
        if (!TextUtils.isEmpty(shareType)) {
            if (shareType.contains("1")) {
                beans.add(new ShareLayoutBean("微信", R.drawable.share_wechat, 1));
            }
            if (shareType.contains("2")) {
                beans.add(new ShareLayoutBean("朋友圈", R.drawable.share_wechatfriend, 2));
            }
            if (shareType.contains("3")) {
                beans.add(new ShareLayoutBean("QQ", R.drawable.share_qq, 3));
            }
            if (shareType.contains("4")) {
                beans.add(new ShareLayoutBean("QQ空间", R.drawable.share_qzone, 4));
            }
        }
        adapter.loadData(beans);
    }

    public interface StringArrayDialogCallback {
        void onItemClick(String text, int tag);
    }
}
