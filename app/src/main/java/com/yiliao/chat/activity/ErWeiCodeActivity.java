package com.yiliao.chat.activity;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.net.Uri;
import android.text.TextUtils;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.signature.StringSignature;
import com.yiliao.chat.R;
import com.yiliao.chat.base.BaseActivity;
import com.yiliao.chat.base.BaseResponse;
import com.yiliao.chat.bean.InviteRewardBean;
import com.yiliao.chat.bean.ShareInformitionBean;
import com.yiliao.chat.bean.ShareLayoutBean;
import com.yiliao.chat.constant.ChatApi;
import com.yiliao.chat.constant.Constant;
import com.yiliao.chat.listener.OnItemClickListener;
import com.yiliao.chat.mob.MobCallback;
import com.yiliao.chat.mob.MobConst;
import com.yiliao.chat.mob.MobShareUtil;
import com.yiliao.chat.mob.ShareData;
import com.yiliao.chat.net.AjaxCallback;
import com.yiliao.chat.net.NetCode;
import com.yiliao.chat.util.DialogUtil;
import com.yiliao.chat.util.FileUtil;
import com.yiliao.chat.util.ParamUtil;
import com.yiliao.chat.util.ToastUtil;
import com.zhy.http.okhttp.OkHttpUtils;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.Map;

import butterknife.BindView;
import butterknife.OnClick;

/*
 * Copyright (C) 2018
 * 版权所有
 *
 * 功能描述：二维码页面
 * 作者：
 * 创建时间：2018/10/19
 *
 * 修改人：
 * 修改描述：
 * 修改日期
 */
public class ErWeiCodeActivity extends BaseActivity {

    @BindView(R.id.content_iv)
    ImageView mContentIv;
    @BindView(R.id.content_fl)
    FrameLayout mContentFl;


    @Override
    protected View getContentView() {
        return inflate(R.layout.activity_er_wei_code_layout);
    }

    @Override
    protected void onContentAdded() {
        setTitle(R.string.my_code);
        loadImage();
        geShareInformaiton();
    }
    public  boolean save(Bitmap src, File file, Bitmap.CompressFormat format, boolean recycle) {
        if (src==null)return false;
        System.out.println(src.getWidth() + ", " + src.getHeight());

        try {
            OutputStream os = null;
            boolean ret = false;
            os = new BufferedOutputStream(new FileOutputStream(file));
            ret = src.compress(format, 100, os);
            if (recycle && !src.isRecycled()) {
                src.recycle();
            }
            os.close();
            return true;
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
        }
        return false;
    }
    /**
     * 加载图片
     */
    private void loadImage() {
        showLoadingDialog();
       final File file=new File(FileUtil.CACHE_PATH+"code.png");

       try {
           if (file.exists()){
               Glide.with(ErWeiCodeActivity.this).load(FileUtil.CACHE_PATH+"code.png").into(mContentIv);
                dismissLoadingDialog();
           }else {
               file.createNewFile();
           }
       }catch (Exception ex){

       }

        final String url = ChatApi.ON_LOAD_GALANCE_OVER + getUserId();
        //加载

        Glide.with(ErWeiCodeActivity.this).load(url).asBitmap()
                .signature(new StringSignature(String.valueOf(System.currentTimeMillis())))
                .diskCacheStrategy(DiskCacheStrategy.NONE)
                .skipMemoryCache(true)
                .into(new SimpleTarget<Bitmap>() {
                    @Override
                    public void onResourceReady(Bitmap resource, GlideAnimation<? super Bitmap> glideAnimation) {
                        mContentIv.setImageBitmap(resource);
                        dismissLoadingDialog();

                        if (file.exists()){
                            save(resource,file, Bitmap.CompressFormat.PNG,false);
                        }

                    }
                });
    }

    @OnClick({R.id.invite_tv, R.id.copy_tv, R.id.share_tv})
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.invite_tv: {//邀请主播
                String path = viewSaveToImage(mContentFl);
                if (!TextUtils.isEmpty(path)) {
                    ToastUtil.showToast(getApplicationContext(), R.string.save_success);
                }
                break;
            }
            case R.id.copy_tv: {//分享链接
                DialogUtil.showShareDialog(ErWeiCodeActivity.this, new OnItemClickListener<ShareLayoutBean>() {
                    @Override
                    public void onItemClick(ShareLayoutBean bean, int position) {
                        switch (bean.id) {
                            case 1:
                                shareUrl(MobConst.Type.WX);
                                break;
                            case 2:
                                shareUrl(MobConst.Type.WX_PYQ);
                                break;
                            case 3:
                                shareUrl(MobConst.Type.QQ);
                                break;
                            case 4:
                                shareUrl(MobConst.Type.QZONE);
                                break;
                        }
                    }
                });
                break;
            }
            case R.id.share_tv: {//分享二维码
                DialogUtil.showShareDialog(ErWeiCodeActivity.this, new OnItemClickListener<ShareLayoutBean>() {
                    @Override
                    public void onItemClick(ShareLayoutBean bean, int position) {
                        switch (bean.id) {
                            case 1: {
                                String path = viewSaveToImage(mContentFl);
                                if (!TextUtils.isEmpty(path)) {
                                    shareImage(MobConst.Type.WX, path);
                                }
                            }
                            break;
                            case 2: {
                                String path = viewSaveToImage(mContentFl);
                                if (!TextUtils.isEmpty(path)) {
                                    shareImage(MobConst.Type.WX_PYQ, path);
                                }
                            }
                            break;
                            case 3: {
                                String path = viewSaveToImage(mContentFl);
                                if (!TextUtils.isEmpty(path)) {
                                    shareImage(MobConst.Type.QQ, path);
                                }
                            }
                            break;
                            case 4: {
                                String path = viewSaveToImage(mContentFl);
                                if (!TextUtils.isEmpty(path)) {
                                    shareImage(MobConst.Type.QZONE, path);
                                }
                            }
                            break;
                        }
                    }
                });
                break;
            }
        }
    }

    private String viewSaveToImage(View view) {
        view.setDrawingCacheEnabled(true);
        view.setDrawingCacheQuality(View.DRAWING_CACHE_QUALITY_HIGH);
        view.setDrawingCacheBackgroundColor(Color.WHITE);
        // 把一个View转换成图片
        Bitmap cachebmp = loadBitmapFromView(view);
        String res = saveImageToGallery(ErWeiCodeActivity.this, cachebmp);
        view.destroyDrawingCache();
        return res;
    }

    //保存文件到指定路径
    private String saveImageToGallery(Context context, Bitmap bmp) {
        // 首先保存图片
        File pFile = new File(FileUtil.YCHAT_DIR);
        if (!pFile.exists()) {
            boolean res = pFile.mkdir();
            if (!res) {
                return null;
            }
        }
        File dFile = new File(Constant.ER_CODE);
        if (!dFile.exists()) {
            boolean res = dFile.mkdir();
            if (!res) {
                return null;
            }
        } else {
            FileUtil.deleteFiles(dFile.getPath());
        }
        File file = new File(dFile, "erCode.jpg");
        try {
            FileOutputStream fos = new FileOutputStream(file);
            //通过io流的方式来压缩保存图片
            bmp.compress(Bitmap.CompressFormat.JPEG, 90, fos);
            fos.flush();
            fos.close();

            //保存图片后发送广播通知更新数据库
            Uri uri = Uri.fromFile(file);
            context.sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, uri));
            return file.getAbsolutePath();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    private Bitmap loadBitmapFromView(View v) {
        int w = v.getWidth();
        int h = v.getHeight();
        Bitmap bmp = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888);
        Canvas c = new Canvas(bmp);
        c.drawColor(Color.WHITE);
        // 如果不设置canvas画布为白色，则生成透明
        v.layout(0, 0, w, h);
        v.draw(c);
        return bmp;
    }

    /**
     * 分享Url
     */
    private void shareUrl(String platType) {
        MobShareUtil mobShareUtil = new MobShareUtil();
        ShareData data = new ShareData();
        String title = shareInformitionBean.t_share_title;
        data.setTitle(title);
        String des = shareInformitionBean.t_share_details;
        data.setDes(des);
//        data.setImgData(BitmapFactory.decodeResource(getResources(), R.mipmap.logo));
        data.setImgUrl(shareInformitionBean.t_share_logo);
//        String webUrl = ChatApi.SHARE_URL + getUserId();
        data.setWebUrl(shareInformitionBean.t_share_url);
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

    /**
     * 分享图片
     */
    private void shareImage(String platType, String imagePath) {
        MobShareUtil mobShareUtil = new MobShareUtil();
        ShareData data = new ShareData();
        data.setImgPath(imagePath);
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

    /**
     * 获取分享信息
     */
    private ShareInformitionBean shareInformitionBean;
    private void geShareInformaiton() {
        Map<String, String> paramMap = new HashMap<>();
        paramMap.put("userId", getUserId());
        if (Constant.addType()){
            paramMap.put("type","1");
        }
        OkHttpUtils.post().url(ChatApi.SHARE_INFORMAOTION)
                .addParams("param", ParamUtil.getParam(paramMap))
                .build().execute(new AjaxCallback<BaseResponse<ShareInformitionBean>>() {
            @Override
            public void onResponse(BaseResponse<ShareInformitionBean> response, int id) {

                if (response != null && response.m_istatus == NetCode.SUCCESS) {
                    shareInformitionBean  =response.m_object;

                }
            }
        });
    }
}
