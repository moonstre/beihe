package com.yiliao.chat.activity;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Point;
import android.net.Uri;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.listener.OnLoadMoreListener;
import com.scwang.smartrefresh.layout.listener.OnRefreshListener;
import com.yiliao.chat.R;
import com.yiliao.chat.adapter.UserActiveRecyclerAdapter;
import com.yiliao.chat.base.BaseActivity;
import com.yiliao.chat.base.BaseResponse;
import com.yiliao.chat.bean.ActiveBean;
import com.yiliao.chat.bean.ActiveFileBean;
import com.yiliao.chat.bean.PageBean;
import com.yiliao.chat.constant.ChatApi;
import com.yiliao.chat.constant.Constant;
import com.yiliao.chat.helper.ImageHelper;
import com.yiliao.chat.net.AjaxCallback;
import com.yiliao.chat.net.NetCode;
import com.yiliao.chat.util.DevicesUtil;
import com.yiliao.chat.util.FileUtil;
import com.yiliao.chat.util.LogUtil;
import com.yiliao.chat.util.ParamUtil;
import com.yiliao.chat.util.ToastUtil;
import com.zhihu.matisse.Matisse;
import com.zhy.http.okhttp.OkHttpUtils;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindView;

/*
 * Copyright (C) 2018
 * 版权所有
 *
 * 功能描述：自己动态页面
 * 作者：
 * 创建时间：2018/12/18
 *
 * 修改人：
 * 修改描述：
 * 修改日期
 */
public class UserSelfActiveActivity extends BaseActivity {

    @BindView(R.id.content_rv)
    RecyclerView mContentRv;
    @BindView(R.id.refreshLayout)
    SmartRefreshLayout mRefreshLayout;

    private int mCurrentPage = 1;
    private List<ActiveBean<ActiveFileBean>> mFocusBeans = new ArrayList<>();
    private UserActiveRecyclerAdapter mAdapter;

    @Override
    protected View getContentView() {
        return inflate(R.layout.activity_user_self_active_layout);
    }

    @Override
    protected void onContentAdded() {
        setTitle(R.string.my_active);
        initRecycler();
        getActiveList(mRefreshLayout, true, 1);
    }

    /**
     * 1.4 版 获取通话记录
     */
    private void getActiveList(final RefreshLayout refreshlayout, final boolean isRefresh, int page) {
        Map<String, String> paramMap = new HashMap<>();
        paramMap.put("userId", getUserId());
        paramMap.put("page", String.valueOf(page));
        OkHttpUtils.post().url(ChatApi.GET_OWN_DYNAMIC_LIST)
                .addParams("param", ParamUtil.getParam(paramMap))
                .build().execute(new AjaxCallback<BaseResponse<PageBean<ActiveBean<ActiveFileBean>>>>() {
            @Override
            public void onResponse(BaseResponse<PageBean<ActiveBean<ActiveFileBean>>> response, int id) {
                if (isFinishing()) {
                    return;
                }
                if (response != null && response.m_istatus == NetCode.SUCCESS) {
                    PageBean<ActiveBean<ActiveFileBean>> pageBean = response.m_object;
                    if (pageBean != null) {
                        List<ActiveBean<ActiveFileBean>> focusBeans = pageBean.data;
                        if (focusBeans != null) {
                            int size = focusBeans.size();
                            if (isRefresh) {
                                mCurrentPage = 1;
                                mFocusBeans.clear();
                                mFocusBeans.addAll(focusBeans);
                                mAdapter.loadData(mFocusBeans);
                                if (mFocusBeans.size() > 0) {
                                    mRefreshLayout.setEnableRefresh(true);
                                } else {
                                    mRefreshLayout.setEnableRefresh(false);
                                }
                                refreshlayout.finishRefresh();
                                if (size >= 10) {//如果是刷新,且返回的数据大于等于10条,就可以load more
                                    refreshlayout.setEnableLoadMore(true);
                                }
                            } else {
                                mCurrentPage++;
                                mFocusBeans.addAll(focusBeans);
                                mAdapter.loadData(mFocusBeans);
                                if (size >= 10) {
                                    refreshlayout.finishLoadMore();
                                }
                            }
                            if (size < 10) {//如果数据返回少于10了,那么说明就没数据了
                                refreshlayout.finishLoadMoreWithNoMoreData();
                            }
                        }
                    } else {
                        ToastUtil.showToast(getApplicationContext(), R.string.system_error);
                        if (isRefresh) {
                            refreshlayout.finishRefresh();
                        } else {
                            refreshlayout.finishLoadMore();
                        }
                    }
                } else {
                    ToastUtil.showToast(getApplicationContext(), R.string.system_error);
                    if (isRefresh) {
                        refreshlayout.finishRefresh();
                    } else {
                        refreshlayout.finishLoadMore();
                    }
                }
            }
        });
    }

    /**
     * 初始化RecyclerView
     */
    private void initRecycler() {
        mRightTv.setBackgroundResource(R.drawable.active_camera);
        mRightTv.setVisibility(View.VISIBLE);
        RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) mRightTv.getLayoutParams();
        params.rightMargin = DevicesUtil.dp2px(getApplicationContext(), 15);
        mRightTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (Constant.hideApplyActivity()){
                    showPostDialog();
                }else {
                    Intent intent = new Intent(getApplicationContext(), PostActiveActivity.class);
                    startActivity(intent);
                }


            }
        });

        mRefreshLayout.setOnRefreshListener(new OnRefreshListener() {
            @Override
            public void onRefresh(@NonNull RefreshLayout refreshlayout) {
                getActiveList(refreshlayout, true, 1);
            }
        });
        mRefreshLayout.setOnLoadMoreListener(new OnLoadMoreListener() {
            @Override
            public void onLoadMore(@NonNull RefreshLayout refreshlayout) {
                getActiveList(refreshlayout, false, mCurrentPage + 1);
            }
        });

        LinearLayoutManager gridLayoutManager = new LinearLayoutManager(this);
        mContentRv.setLayoutManager(gridLayoutManager);
        mAdapter = new UserActiveRecyclerAdapter(this);
        mContentRv.setAdapter(mAdapter);
    }
    private final int CAMERA_REQUEST_CODE = 0x116;
    /**
     * 显示发布dialog
     */
    private void showPostDialog() {
        final Dialog mDialog = new Dialog(mContext, R.style.DialogStyle_Dark_Background);
        @SuppressLint("InflateParams")
        View view = LayoutInflater.from(mContext).inflate(R.layout.dialog_post_active_layout, null);
        setDialogView(view, mDialog);
        mDialog.setContentView(view);
        Point outSize = new Point();
        getWindowManager().getDefaultDisplay().getSize(outSize);
        Window window = mDialog.getWindow();
        if (window != null) {
            WindowManager.LayoutParams params = window.getAttributes();
            params.width = outSize.x;
            window.setGravity(Gravity.TOP); // 此处可以设置dialog显示的位置
        }
        mDialog.setCanceledOnTouchOutside(true);
        if (!isFinishing()) {
            mDialog.show();
        }
    }

    /**
     * 设置头像选择dialog的view
     */
    private void setDialogView(View view, final Dialog mDialog) {
        //取消
        ImageView close_iv = view.findViewById(R.id.close_iv);
        close_iv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mDialog.dismiss();
            }
        });
        //其他
        TextView other_tv = view.findViewById(R.id.other_tv);
        other_tv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mContext, PostActiveActivity.class);
                startActivity(intent);
                mDialog.dismiss();
            }
        });
        //拍摄
        TextView shoot_tv = view.findViewById(R.id.shoot_tv);
        shoot_tv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                jumpToCamera();
                mDialog.dismiss();
            }
        });
        //相册
        TextView album_tv = view.findViewById(R.id.album_tv);
        album_tv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ImageHelper.openPictureVideoChoosePage(UserSelfActiveActivity.this, Constant.REQUEST_ALBUM_IMAGE_AND_VIDEO);
                mDialog.dismiss();
            }
        });
    }
    /**
     * 跳转到拍照
     */
    private void jumpToCamera() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ContextCompat.checkSelfPermission(mContext, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager
                    .PERMISSION_GRANTED &&
                    ContextCompat.checkSelfPermission(mContext, Manifest.permission.RECORD_AUDIO) == PackageManager
                            .PERMISSION_GRANTED &&
                    ContextCompat.checkSelfPermission(mContext, Manifest.permission.CAMERA) == PackageManager
                            .PERMISSION_GRANTED) {
                Intent intent = new Intent(getApplicationContext(), CameraActivity.class);
                startActivityForResult(intent, CAMERA_REQUEST_CODE);
            } else {
                //不具有获取权限，需要进行权限申请
                ActivityCompat.requestPermissions(UserSelfActiveActivity.this, new String[]{
                        Manifest.permission.WRITE_EXTERNAL_STORAGE,
                        Manifest.permission.RECORD_AUDIO,
                        Manifest.permission.CAMERA}, 100);
            }
        } else {
            Intent intent = new Intent(mContext, CameraActivity.class);
            startActivityForResult(intent, CAMERA_REQUEST_CODE);
        }
    }
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == CAMERA_REQUEST_CODE && data != null) {//相机返回
            if (resultCode == 101) {//图片
                String imagePath = data.getStringExtra("imagePath");
                LogUtil.i("相机拍照图片:  " + imagePath);
                if (!TextUtils.isEmpty(imagePath)) {
                    //跳转到发布页面
                    int shoot = 1;//拍摄
                    Intent intent = new Intent(mContext, PostActiveActivity.class);
                    intent.putExtra(Constant.POST_FROM, shoot);
                    intent.putExtra(Constant.PASS_TYPE, Constant.TYPE_IMAGE);
                    intent.putExtra(Constant.POST_IMAGE_PATH, imagePath);
                    startActivity(intent);
                } else {
                    ToastUtil.showToast(mContext.getApplicationContext(), R.string.file_invalidate);
                }
            } else if (resultCode == 102) {//视频
                String videoUrl = data.getStringExtra("videoUrl");
                LogUtil.i("相机录视频Url:  " + videoUrl);
                if (!TextUtils.isEmpty(videoUrl)) {
                    //跳转到发布页面
                    int shoot = 1;//拍摄
                    Intent intent = new Intent(mContext, PostActiveActivity.class);
                    intent.putExtra(Constant.POST_FROM, shoot);
                    intent.putExtra(Constant.PASS_TYPE, Constant.TYPE_VIDEO);
                    intent.putExtra(Constant.POST_VIDEO_URL, videoUrl);
                    startActivity(intent);
                }
            }
        }else if (requestCode == Constant.REQUEST_ALBUM_IMAGE_AND_VIDEO){
            List<Uri> mSelectedUris = Matisse.obtainResult(data);
            if (mSelectedUris != null && mSelectedUris.size() > 0) {
                LogUtil.i("动态相册选择的: " + mSelectedUris.toString());
                if (checkUri(mSelectedUris)) {//判断通过
                    Uri fileUri = mSelectedUris.get(0);
                    if (fileUri != null) {
                        //跳转到发布页面
                        int album = 2;//拍摄
                        Intent intent = new Intent(mContext, PostActiveActivity.class);
                        intent.putExtra(Constant.POST_FROM, album);
                        //如果是视频
                        if (fileUri.toString().contains("video")) {
                            intent.putExtra(Constant.PASS_TYPE, Constant.TYPE_VIDEO);
                        } else {
                            //如果是图片
                            intent.putExtra(Constant.PASS_TYPE, Constant.TYPE_IMAGE);
                        }
                        intent.putExtra(Constant.POST_FILE_URI, fileUri.toString());
                        startActivity(intent);
                    }
                }
            }
        }
    }
    /**
     * 检查选择的
     */
    private boolean checkUri(List<Uri> mSelectedUris) {
        //判断文件是否无效
        Uri uri = mSelectedUris.get(0);
        if (!checkUriFileExist(uri)) {
            ToastUtil.showToast(mContext, R.string.file_invalidate);
            return false;
        }
        return true;
    }
    /**
     * 判断文件
     */
    private boolean checkUriFileExist(Uri uri) {
        if (uri != null) {
            String filePath = FileUtil.getPathAbove19(mContext, uri);
            if (!TextUtils.isEmpty(filePath)) {
                File file = new File(filePath);
                if (file.exists()) {
                    return true;
                } else {
                    LogUtil.i("文件不存在: " + uri.toString());
                    return false;
                }
            }
        }
        return false;
    }
}
