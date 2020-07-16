package com.yiliao.chat.activity;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Point;
import android.graphics.Rect;
import android.net.Uri;
import android.os.AsyncTask;
import android.text.TextUtils;
import android.util.Base64;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.tencent.cos.xml.exception.CosXmlClientException;
import com.tencent.cos.xml.exception.CosXmlServiceException;
import com.tencent.cos.xml.listener.CosXmlProgressListener;
import com.tencent.cos.xml.listener.CosXmlResultListener;
import com.tencent.cos.xml.model.CosXmlRequest;
import com.tencent.cos.xml.model.CosXmlResult;
import com.tencent.cos.xml.model.object.PutObjectRequest;
import com.yalantis.ucrop.UCrop;
import com.yiliao.chat.BuildConfig;
import com.yiliao.chat.R;
import com.yiliao.chat.base.BaseActivity;
import com.yiliao.chat.base.BaseResponse;
import com.yiliao.chat.constant.ChatApi;
import com.yiliao.chat.constant.Constant;
import com.yiliao.chat.helper.ImageHelper;
import com.yiliao.chat.helper.ImageLoadHelper;
import com.yiliao.chat.net.AjaxCallback;
import com.yiliao.chat.net.NetCode;
import com.yiliao.chat.oss.QServiceCfg;
import com.yiliao.chat.util.DevicesUtil;
import com.yiliao.chat.util.FileUtil;
import com.yiliao.chat.util.LogUtil;
import com.yiliao.chat.util.ParamUtil;
import com.yiliao.chat.util.ToastUtil;
import com.zhihu.matisse.Matisse;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.StringCallback;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.lang.ref.WeakReference;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.OnClick;
import okhttp3.Call;

/*
 * Copyright (C) 2018
 * 版权所有
 *
 * 功能描述   申请认证页面
 * 作者：
 * 创建时间：2018/6/15
 *
 * 修改人：
 * 修改描述：
 * 修改日期
 */
public class ApplyVerifyActivity extends BaseActivity {
    @BindView(R.id.layoutName)
    LinearLayout layoutName;
    @BindView(R.id.layoutCard)
    LinearLayout layoutCard;
    @BindView(R.id.head_img_iv)
    ImageView mHeadImgIv;
    @BindView(R.id.real_name_et)
    EditText mRealNameEt;
    @BindView(R.id.id_card_et)
    EditText mIdCardEt;
    @BindView(R.id.scrollView)
    LinearLayout mScrollView;
    @BindView(R.id.we_chat_et)
    EditText mWeChatEt;

    @BindView(R.id.notice_four)
    TextView notice_four;
    @BindView(R.id.ohter_gone)
    TextView ohter_gone;
    @BindView(R.id.yuebo_gone)
    TextView yuebo_gone;
    @BindView(R.id.ll_gone)
    LinearLayout ll_gone;
    private String mSelectLocalPath;
    private Dialog mDialog;

    //腾讯云
    private QServiceCfg mQServiceCfg;
    private String mRealName;
    private String mIdNumber;
    private String mWeChat;

    @Override
    protected View getContentView() {
        return inflate(R.layout.activity_apply_verify_layout);
    }

    @Override
    protected void onContentAdded() {
        setTitle(R.string.apply_verify);
        controlKeyboardLayout();
        mQServiceCfg = QServiceCfg.instance(getApplicationContext());
        if (Constant.hideHomeNearAndNew()){
            ohter_gone.setVisibility(View.GONE);
            ll_gone.setVisibility(View.GONE);
            notice_four.setVisibility(View.GONE);
            layoutCard.setVisibility(View.GONE);
        }else {
            yuebo_gone.setVisibility(View.GONE);
        }
        if (Constant.hideApplyVerifyName()) {
            layoutName.setVisibility(View.GONE);
            layoutCard.setVisibility(View.GONE);
        }

        if (Constant.hideFour()){
            notice_four.setVisibility(View.GONE);
        }

    }

    @OnClick({R.id.upload_head_img_ll, R.id.submit_now_tv, R.id.agree_tv})
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.upload_head_img_ll: {//上传头像
                //图片选择
                ImageHelper.openPictureChoosePage(ApplyVerifyActivity.this, Constant.REQUEST_CODE_CHOOSE);
                break;
            }
            case R.id.submit_now_tv: {//提交认证
                submitVerify();
                break;
            }
            case R.id.agree_tv: {//绿色协议
                Intent intent = new Intent(getApplicationContext(), CommonWebViewActivity.class);
                intent.putExtra(Constant.TITLE, getResources().getString(R.string.green_agree));
                intent.putExtra(Constant.URL, "file:///android_asset/green.html");
                startActivity(intent);
                break;
            }
        }
    }

    /**
     * 提交认证
     */
    private void submitVerify() {
        //验证
        mRealName = mRealNameEt.getText().toString().trim();
        mIdNumber = mIdCardEt.getText().toString().trim();
        if (!Constant.hideApplyVerifyName()) {
            if (TextUtils.isEmpty(mRealName)) {
                ToastUtil.showToast(getApplicationContext(), R.string.please_input_real_name);
                return;
            }
            if(!Constant.hideHomeNearAndNew()){
                if (TextUtils.isEmpty(mIdNumber)) {
                    ToastUtil.showToast(getApplicationContext(), R.string.please_input_id_card_number);
                    return;
                }
            }

        }
        //微信号码
        mWeChat = mWeChatEt.getText().toString().trim();
        if (TextUtils.isEmpty(mWeChat)) {
            ToastUtil.showToast(getApplicationContext(), R.string.please_input_we_chat);
            return;
        }
        if (TextUtils.isEmpty(mSelectLocalPath)) {
            ToastUtil.showToast(getApplicationContext(), R.string.picture_not_choose);
            return;
        }
        File file = new File(mSelectLocalPath);
        if (!file.exists()) {
            ToastUtil.showToast(getApplicationContext(), R.string.file_not_exist);
            return;
        }
        //获取base 64 image
        showLoadingMessageDialog();
        BitmapTask bitmapTask = new BitmapTask(this, mSelectLocalPath);
        bitmapTask.execute();
    }

    /**
     * 调用接口
     */
    private void applyNet(String encodeImage) {
        OkHttpUtils.post().url(ChatApi.REAL_VERIFY)
                .addParams("key", Constant.XY_KEY)
                .addParams("secret", Constant.XY_SECRET)
                .addParams("trueName", mRealName)
                .addParams("idenNo", mIdNumber)
                .addParams("img", encodeImage)
                .addParams("typeId", "3013")
                .addParams("format", "json")
                .build().execute(new StringCallback() {
            @Override
            public void onError(Call call, Exception e, int id) {
//                ToastUtil.showToast(getApplicationContext(), R.string.verify_fail);
//                dismissLoadingMessageDialog();

//                ToastUtil.showToast(getApplicationContext(), "祥云跳过");
                uploadFileWithQQ(false);
            }

            @Override
            public void onResponse(String response, int id) {
                LogUtil.i(response);
                dismissLoadingMessageDialog();
                try {
                    if (!TextUtils.isEmpty(response)) {
                        JSONObject jsonObject = JSON.parseObject(response);
                        //验证message
                        JSONObject messageString = jsonObject.getJSONObject("messageString");
                        int status = messageString.getIntValue("status");
                        if (status == 0) {
                            JSONArray tnidInfo = jsonObject.getJSONArray("infoList");
                            JSONObject itemOne = tnidInfo.getJSONObject(0);
                            JSONArray veritem = itemOne.getJSONArray("veritem");
                            //验证 1.姓名及身份证号一致  2.身份为真
                            boolean user_check_desc = false;
                            boolean verify_result_desc = false;
                            for (int i = 0; i < veritem.size(); i++) {
                                JSONObject item = veritem.getJSONObject(i);
                                String desc = item.getString("desc");
                                String content = item.getString("content");
                                if (desc.equals("verify_result_status") && content.equals("1")) {
                                    user_check_desc = true;
                                } else if (desc.equals("verify_result_desc") && content.contains("判定为同一人")) {
                                    verify_result_desc = true;
                                }
                            }

                            if (user_check_desc && verify_result_desc) {
                                uploadFileWithQQ(true);
                            } else {
                                uploadFileWithQQ(false);
                            }
                        } else {
                            uploadFileWithQQ(false);
                        }
                    } else {
                        ToastUtil.showToast(getApplicationContext(), R.string.verify_fail);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    ToastUtil.showToast(getApplicationContext(), R.string.verify_fail);
                }
            }
        });
    }

    /**
     * 上传认证图到腾讯云
     */
    private void uploadFileWithQQ(final boolean passXY) {
        File file = new File(mSelectLocalPath);
        if (!file.exists()) {
            ToastUtil.showToast(getApplicationContext(), R.string.verify_fail);
            return;
        }
        String fileName = mSelectLocalPath.substring(mSelectLocalPath.length() - 17, mSelectLocalPath.length());
//        String bucket = "img-1256929999";
        String cosPath = "/verify/" + fileName;
        long signDuration = 600; //签名的有效期，单位为秒
        PutObjectRequest putObjectRequest = new PutObjectRequest(BuildConfig.tencentCloudBucket, cosPath, mSelectLocalPath);
        putObjectRequest.setSign(signDuration, null, null);
        putObjectRequest.setProgressListener(new CosXmlProgressListener() {
            @Override
            public void onProgress(long progress, long max) {
            }
        });
        mQServiceCfg.getCosCxmService().putObjectAsync(putObjectRequest, new CosXmlResultListener() {
            @Override
            public void onSuccess(CosXmlRequest request, CosXmlResult result) {
                LogUtil.i("腾讯云success 认证=  " + result.accessUrl);
                String mVerifyImageHttpUrl = result.accessUrl;
                if (!mVerifyImageHttpUrl.contains("http") || !mVerifyImageHttpUrl.contains("https")) {
                    mVerifyImageHttpUrl = "https://" + mVerifyImageHttpUrl;
                }
                uploadVerifyInfo(mVerifyImageHttpUrl, passXY);
            }

            @Override
            public void onFail(CosXmlRequest cosXmlRequest, CosXmlClientException clientException, CosXmlServiceException serviceException) {
                ToastUtil.showToast(getApplicationContext(), R.string.verify_fail);
            }
        });
    }

    /**
     * 上传认证信息到公司服务器
     */
    private void uploadVerifyInfo(String imageUrl, final boolean passXY) {
        String pass = "0";
        if (passXY) {
            pass = "1";
        }

        Map<String, String> paramMap = new HashMap<>();
        paramMap.put("userId", getUserId());
        paramMap.put("t_user_photo", imageUrl);
        paramMap.put("t_nam", mRealName);
        paramMap.put("t_id_card", mIdNumber);
        paramMap.put("t_wx", mWeChat);
        paramMap.put("realState", pass);
        OkHttpUtils.post().url(ChatApi.SUBMIT_VERIFY_DATA)
                .addParams("param", ParamUtil.getParam(paramMap))
                .build().execute(new AjaxCallback<BaseResponse>() {
            @Override
            public void onResponse(BaseResponse response, int id) {
                dismissLoadingMessageDialog();
                if (response != null) {
                    if (response.m_istatus == NetCode.SUCCESS) {
                        //看是不是分享完成了的
                        if (passXY) {
                            checkNewUser();
                        } else {
                            ToastUtil.showToast(getApplicationContext(), R.string.verify_fail_des);
                            Intent intent = new Intent(getApplicationContext(), ActorVerifyingActivity.class);
                            startActivity(intent);
                            finish();
                        }
                    } else {
                        String message = response.m_strMessage;
                        if (!TextUtils.isEmpty(message)) {
                            ToastUtil.showToast(getApplicationContext(), message);
                        } else {
                            ToastUtil.showToast(getApplicationContext(), R.string.verify_fail);
                        }
                    }
                } else {
                    ToastUtil.showToast(getApplicationContext(), R.string.verify_fail);
                }
            }

            @Override
            public void onError(Call call, Exception e, int id) {
                super.onError(call, e, id);
                dismissLoadingMessageDialog();
                ToastUtil.showToast(getApplicationContext(), R.string.verify_fail);
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == Constant.REQUEST_CODE_CHOOSE && resultCode == RESULT_OK) {
            List<Uri> mSelectedUris = Matisse.obtainResult(data);
            LogUtil.i("==--", "mSelected: " + mSelectedUris);
            dealFile(mSelectedUris);
        } else if (resultCode == RESULT_OK && requestCode == UCrop.REQUEST_CROP) {
            Uri resultUri = UCrop.getOutput(data);
            if (resultUri != null) {
                int resizeWidth = DevicesUtil.dp2px(getApplicationContext(), 96);
                int resizeHeight = DevicesUtil.dp2px(getApplicationContext(), 64);
                ImageLoadHelper.glideShowImageWithUri(ApplyVerifyActivity.this, resultUri,
                        mHeadImgIv, resizeWidth, resizeHeight);
                mSelectLocalPath = resultUri.getPath();
            }
            //用完之后应该删除
        } else if (resultCode == UCrop.RESULT_ERROR) {
            ToastUtil.showToast(getApplicationContext(), R.string.choose_picture_failed);
        }
    }

    /**
     * 处理返回的图片,过大的话 就压缩
     * 每次只允许选择一张,所以只处理第一个
     */
    private void dealFile(List<Uri> mSelectedUris) {
        if (mSelectedUris != null && mSelectedUris.size() > 0) {
            try {
                Uri uri = mSelectedUris.get(0);
                String filePath = FileUtil.getPathAbove19(this, uri);
                if (!TextUtils.isEmpty(filePath)) {
                    File file = new File(filePath);
                    LogUtil.i("==--", "file大小: " + file.length() / 1024);
                    //压缩图片
                    cutWithUCrop(uri);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 使用u crop裁剪
     */
    private void cutWithUCrop(Uri sourceUri) {
        //计算 图片裁剪的大小
        File pFile = new File(FileUtil.YCHAT_DIR);
        if (!pFile.exists()) {
            pFile.mkdir();
        }
        File file = new File(Constant.VERIFY_AFTER_RESIZE_DIR);
        if (!file.exists()) {
            file.mkdir();
        } else {
            FileUtil.deleteFiles(file.getPath());
        }
        String filePath = file.getPath() + File.separator + System.currentTimeMillis() + ".jpg";
        UCrop.of(sourceUri, Uri.fromFile(new File(filePath)))
                .withAspectRatio(1280, 960)
                .withMaxResultSize(1280, 960)
                .start(this);
    }

    /**
     * 进度条带message
     */
    private void showLoadingMessageDialog() {
        if (mDialog == null) {
            mDialog = new Dialog(ApplyVerifyActivity.this, R.style.DialogStyle_Dark_Background);
            @SuppressLint("InflateParams")
            View view = LayoutInflater.from(ApplyVerifyActivity.this).inflate(R.layout.dialog_progress_loading, null);
            TextView messageTv = view.findViewById(R.id.progress_txt);
            messageTv.setText(R.string.verify_ing);
            mDialog.setContentView(R.layout.dialog_progress_loading);
            mDialog.setCanceledOnTouchOutside(false);
        }
        mDialog.show();
    }

    /**
     * 关闭请求网络数据进度条
     */
    public void dismissLoadingMessageDialog() {
        try {
            if (!isFinishing() && mDialog != null && mDialog.isShowing()) {
                mDialog.dismiss();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        //删除REPORT目录中的图片
        FileUtil.deleteFiles(Constant.VERIFY_AFTER_RESIZE_DIR);
    }

    /**
     * 键盘
     */
    private void controlKeyboardLayout() {
        mScrollView.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {

            @Override
            public void onGlobalLayout() {
                Rect r = new Rect();
                mScrollView.getWindowVisibleDisplayFrame(r);
                //r.top 是状态栏高度
                int screenHeight = mScrollView.getRootView().getHeight();
                int softHeight = screenHeight - r.bottom;
                if (softHeight > 100) {//当输入法高度大于100判定为输入法打开了
                    mScrollView.scrollTo(0, 150);
                } else {//否则判断为输入法隐藏了
                    mScrollView.scrollTo(0, 10);
                }
            }
        });
    }

    static class BitmapTask extends AsyncTask<Integer, Void, String> {

        private WeakReference<ApplyVerifyActivity> mWeakAty;
        private String mPath;

        BitmapTask(ApplyVerifyActivity activity, String path) {
            mWeakAty = new WeakReference<>(activity);
            mPath = path;
        }

        @Override
        protected String doInBackground(Integer... integers) {
            final ApplyVerifyActivity activity = mWeakAty.get();
            if (activity != null) {
                try {
                    Bitmap bitmap = BitmapFactory.decodeFile(mPath);
                    ByteArrayOutputStream baos = new ByteArrayOutputStream();
                    //读取图片到ByteArrayOutputStream
                    bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos); //参数如果为100那么就不压缩
                    byte[] bytes = baos.toByteArray();
                    return Base64.encodeToString(bytes, Base64.NO_WRAP);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            return null;
        }

        @Override
        protected void onPostExecute(String bitmap) {
            if (mWeakAty.get() != null) {
                ApplyVerifyActivity activity = mWeakAty.get();
                if (!TextUtils.isEmpty(bitmap)) {
                    activity.applyNet(bitmap);
                } else {
                    activity.dismissLoadingDialog();
                    ToastUtil.showToast(activity, R.string.image_too_big);
                }
            }
        }
    }

    /**
     * 判断是不是新用户
     */
    private void checkNewUser() {
        Map<String, String> paramMap = new HashMap<>();
        paramMap.put("userId", getUserId());
        OkHttpUtils.post().url(ChatApi.GET_USER_NEW)
                .addParams("param", ParamUtil.getParam(paramMap))
                .build().execute(new AjaxCallback<BaseResponse<Integer>>() {
            @Override
            public void onResponse(BaseResponse<Integer> response, int id) {
                if (response != null && response.m_istatus == NetCode.SUCCESS) {
                    //0.不是新用户 1.是新用户
                    Integer mNewUser = response.m_object;
                    if (mNewUser == 2) {//是分享完成了的
                        showVerifySuccessDialog();
                    } else {
                        ToastUtil.showToast(getApplicationContext(), R.string.verify_success);
                        Intent intent = new Intent(getApplicationContext(), ActorVerifyingActivity.class);
                        startActivity(intent);
                        finish();
                    }
                } else {
                    ToastUtil.showToast(getApplicationContext(), R.string.verify_success);
                    Intent intent = new Intent(getApplicationContext(), ActorVerifyingActivity.class);
                    startActivity(intent);
                    finish();
                }
            }

            @Override
            public void onError(Call call, Exception e, int id) {
                super.onError(call, e, id);
                ToastUtil.showToast(getApplicationContext(), R.string.verify_success);
                Intent intent = new Intent(getApplicationContext(), ActorVerifyingActivity.class);
                startActivity(intent);
                finish();
            }
        });
    }

    /**
     * 显示分享到3个群dialog
     */
    private void showVerifySuccessDialog() {
        final Dialog mDialog = new Dialog(ApplyVerifyActivity.this, R.style.DialogStyle_Dark_Background);
        @SuppressLint("InflateParams")
        View view = LayoutInflater.from(ApplyVerifyActivity.this).inflate(R.layout.dialog_verify_success_layout, null);
        setVerifySuccessDialogView(view, mDialog);
        mDialog.setContentView(view);
        Point outSize = new Point();
        getWindowManager().getDefaultDisplay().getSize(outSize);
        Window window = mDialog.getWindow();
        if (window != null) {
            WindowManager.LayoutParams params = window.getAttributes();
            params.width = outSize.x;
            window.setGravity(Gravity.CENTER); // 此处可以设置dialog显示的位置
        }
        mDialog.setCanceledOnTouchOutside(false);
        mDialog.setCancelable(false);
        if (!isFinishing()) {
            mDialog.show();
        }
    }

    private void setVerifySuccessDialogView(View view, final Dialog mDialog) {
        //确定
        TextView confirm_tv = view.findViewById(R.id.confirm_tv);
        confirm_tv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mDialog.dismiss();
                Intent intent = new Intent(getApplicationContext(), ModifyUserInfoActivity.class);
                startActivity(intent);
                finish();
            }
        });
    }

}
