package com.yiliao.chat.activity;

import android.content.Intent;
import android.net.Uri;
import android.os.CountDownTimer;
import android.text.InputFilter;
import android.text.InputType;
import android.text.TextUtils;
import android.text.method.DigitsKeyListener;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.tencent.cos.xml.exception.CosXmlClientException;
import com.tencent.cos.xml.exception.CosXmlServiceException;
import com.tencent.cos.xml.listener.CosXmlResultListener;
import com.tencent.cos.xml.model.CosXmlRequest;
import com.tencent.cos.xml.model.CosXmlResult;
import com.tencent.cos.xml.model.object.PutObjectRequest;
import com.yalantis.ucrop.UCrop;
import com.yiliao.chat.BuildConfig;
import com.yiliao.chat.R;
import com.yiliao.chat.base.BaseActivity;
import com.yiliao.chat.base.BaseResponse;
import com.yiliao.chat.bean.ChatUserInfo;
import com.yiliao.chat.constant.ChatApi;
import com.yiliao.chat.constant.Constant;
import com.yiliao.chat.helper.ImageHelper;
import com.yiliao.chat.helper.ImageLoadHelper;
import com.yiliao.chat.helper.SharedPreferenceHelper;
import com.yiliao.chat.listener.OnFileUploadListener;
import com.yiliao.chat.net.AjaxCallback;
import com.yiliao.chat.net.NetCode;
import com.yiliao.chat.oss.QServiceCfg;
import com.yiliao.chat.util.DevicesUtil;
import com.yiliao.chat.util.FileUtil;
import com.yiliao.chat.util.LogUtil;
import com.yiliao.chat.util.ParamUtil;
import com.yiliao.chat.util.SystemUtil;
import com.yiliao.chat.util.ToastUtil;
import com.yiliao.chat.util.VerifyUtils;
import com.yiliao.chat.util.WordUtil;
import com.zhihu.matisse.Matisse;
import com.zhy.http.okhttp.OkHttpUtils;

import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.OnClick;
import okhttp3.Call;
import okhttp3.Request;

/*
 * Copyright (C) 2018
 * 版权所有
 *
 * 功能描述：注册 忘记密码页面
 * 作者：
 * 创建时间：2018/6/22
 *
 * 修改人：
 * 修改描述：
 * 修改日期
 */
public class RegisterActivity extends BaseActivity {
    @BindView(R.id.tvRegisterTitle)
    TextView tvRegisterTitle;
    //获取验证码
    @BindView(R.id.send_verify_tv)
    TextView mSendVerifyTv;
    @BindView(R.id.mobile_et)
    EditText mMobileEt;
    @BindView(R.id.code_et)
    EditText mCodeEt;
    @BindView(R.id.pass_code_et)
    EditText mPassCodeEt;

    @BindView(R.id.headimage)
    ImageView headimage;

    private CountDownTimer mCountDownTimer;
    private int mJoinType;//进来的类型, 注册:  0  找回密码: 1
    private final int REGISTER = 0;


    private String mHeadImageHttpUrl = "";//头像url
    //头像本地路径
    private String mHeadImageLocalPath = "";


    //腾讯云
    private QServiceCfg mQServiceCfg;
    @Override
    protected View getContentView() {
        return inflate(R.layout.activity_register_layout);
    }

    @Override
    protected boolean supportFullScreen() {
        return true;
    }

    @Override
    protected void onContentAdded() {
        mQServiceCfg = QServiceCfg.instance(getApplicationContext());
        needHeader(false);
        mJoinType = getIntent().getIntExtra(Constant.JOIN_TYPE, REGISTER);
        initView();
    }

    /**
     * 初始化view
     */
    private void initView() {
        int FIND_BACK = 1;
        if (Constant.displayHeadImage()){
            if (mJoinType==0){
                headimage.setVisibility(View.VISIBLE);
            }else {
                headimage.setVisibility(View.GONE);
            }

        }else {
            headimage.setVisibility(View.GONE);
        }

        if (mJoinType == FIND_BACK) {//找回密码
            tvRegisterTitle.setText(R.string.forget_password_title);
            mPassCodeEt.setHint(getString(R.string.please_set_new_password));
        }

        if (SharedPreferenceHelper.getLoginTypeEmail(RegisterActivity.this)) {
            mMobileEt.setFilters(new InputFilter[]{new InputFilter.LengthFilter(50)});
            mMobileEt.setHint(R.string.please_input_phone_login_or_email);
        } else {
            mMobileEt.setKeyListener(DigitsKeyListener.getInstance("0123456789"));
            mMobileEt.setInputType(InputType.TYPE_CLASS_NUMBER);
        }
    }

    @OnClick({R.id.ivLoginClose, R.id.send_verify_tv, R.id.confirm_tv,R.id.headimage})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.ivLoginClose:
                finish();
                break;
            case R.id.send_verify_tv: {//获取验证码
                sendSmsVerifyCode();
                break;
            }
            case R.id.confirm_tv: {//确认
                if (mJoinType == REGISTER) {
                    if (Constant.displayHeadImage()){
                        if (mHeadImageHttpUrl.equals("")){
                            ToastUtil.show("请上传头像");
                        }else {
                            register();//注册
                        }
                    }else {
                        register();//注册
                    }

                } else {
                    updatePassword();//忘记密码
                }
                break;
            }
            case R.id.headimage:{
                //图片选择
                ImageHelper.openPictureChoosePage(RegisterActivity.this, Constant.REQUEST_CODE_CHOOSE);

                break;
            }
        }
    }

    /**
     * 更新密码
     */
    private void updatePassword() {
        final String phone = mMobileEt.getText().toString().trim();
        if (TextUtils.isEmpty(phone)) {
            if (SharedPreferenceHelper.getLoginTypeEmail(RegisterActivity.this)) {
                ToastUtil.showToast(getApplicationContext(), R.string.phone_number_or_email_null);
            } else {
                ToastUtil.showToast(getApplicationContext(), R.string.phone_number_null);
            }
            return;
        }
        if (!VerifyUtils.isPhoneNum(phone) && !VerifyUtils.isEmail(phone)) {
            if (SharedPreferenceHelper.getLoginTypeEmail(RegisterActivity.this)) {
                ToastUtil.showToast(getApplicationContext(), R.string.wrong_phone_or_email_number);
            } else {
                ToastUtil.showToast(getApplicationContext(), R.string.wrong_phone_number);
            }
            return;
        }
        String verifyCode = mCodeEt.getText().toString().trim();
        if (TextUtils.isEmpty(verifyCode)) {
            ToastUtil.showToast(getApplicationContext(), R.string.verify_code_number_null);
            return;
        }
        //密码
        String password = mPassCodeEt.getText().toString().trim();
        if (TextUtils.isEmpty(password)) {
            ToastUtil.showToast(getApplicationContext(), R.string.please_input_new_password);
            return;
        }
        if (password.length() < 6 || password.length() > 16) {
            ToastUtil.showToast(getApplicationContext(), R.string.new_length_wrong);
            return;
        }
        if (VerifyUtils.isPhoneNum(phone)) {
            Map<String, String> paramMap = new HashMap<>();
            paramMap.put("phone", phone);
            paramMap.put("password", password);
            paramMap.put("smsCode", verifyCode);
            OkHttpUtils.post().url(ChatApi.UP_PASSWORD)
                    .addParams("param", ParamUtil.getParam(paramMap))
                    .build().execute(new AjaxCallback<BaseResponse>() {
                @Override
                public void onResponse(BaseResponse response, int id) {
                    dismissLoadingDialog();
                    if (response != null) {//-2:验证码错误 -1:账号不存在 0:程序异常 1:修改成功
                        if (response.m_istatus == NetCode.SUCCESS) {
                            ToastUtil.showToastLong(getApplicationContext(), R.string.set_new_password_success);
//                        Intent intent = new Intent(getApplicationContext(), PhoneLoginActivity.class);
//                        startActivity(intent);
                            finish();
                        } else {
                            String message = response.m_strMessage;
                            if (!TextUtils.isEmpty(message)) {
                                ToastUtil.showToast(getApplicationContext(), message);
                            } else {
                                ToastUtil.showToast(getApplicationContext(), R.string.set_new_password_fail);
                            }
                        }
                    } else {
                        ToastUtil.showToast(getApplicationContext(), R.string.set_new_password_fail);
                    }

                }

                @Override
                public void onError(Call call, Exception e, int id) {
                    super.onError(call, e, id);
                    ToastUtil.showToast(getApplicationContext(), R.string.set_new_password_fail);
                    dismissLoadingDialog();
                }

                @Override
                public void onBefore(Request request, int id) {
                    super.onBefore(request, id);
                    showLoadingDialog();
                }

            });
        } else {
            Map<String, String> paramMap = new HashMap<>();
            paramMap.put("email", phone);
            paramMap.put("password", password);
            paramMap.put("smsCode", verifyCode);
            OkHttpUtils.post().url(ChatApi.UP_EMAIL_PASSWORD)
                    .addParams("param", ParamUtil.getParam(paramMap))
                    .build().execute(new AjaxCallback<BaseResponse>() {
                @Override
                public void onResponse(BaseResponse response, int id) {
                    dismissLoadingDialog();
                    if (response != null) {//-2:验证码错误 -1:账号不存在 0:程序异常 1:修改成功
                        if (response.m_istatus == NetCode.SUCCESS) {
                            ToastUtil.showToastLong(getApplicationContext(), R.string.set_new_password_success);
//                        Intent intent = new Intent(getApplicationContext(), PhoneLoginActivity.class);
//                        startActivity(intent);
                            finish();
                        } else {
                            String message = response.m_strMessage;
                            if (!TextUtils.isEmpty(message)) {
                                ToastUtil.showToast(getApplicationContext(), message);
                            } else {
                                ToastUtil.showToast(getApplicationContext(), R.string.set_new_password_fail);
                            }
                        }
                    } else {
                        ToastUtil.showToast(getApplicationContext(), R.string.set_new_password_fail);
                    }

                }

                @Override
                public void onError(Call call, Exception e, int id) {
                    super.onError(call, e, id);
                    ToastUtil.showToast(getApplicationContext(), R.string.set_new_password_fail);
                    dismissLoadingDialog();
                }

                @Override
                public void onBefore(Request request, int id) {
                    super.onBefore(request, id);
                    showLoadingDialog();
                }

            });
        }
    }

    /**
     * 注册
     */

    private void register() {
        final String phone = mMobileEt.getText().toString().trim();
        if (TextUtils.isEmpty(phone)) {
            if (SharedPreferenceHelper.getLoginTypeEmail(RegisterActivity.this)) {
                ToastUtil.showToast(getApplicationContext(), R.string.phone_number_or_email_null);
            } else {
                ToastUtil.showToast(getApplicationContext(), R.string.phone_number_null);
            }
            return;
        }
        if (!VerifyUtils.isPhoneNum(phone) && !VerifyUtils.isEmail(phone)) {
            if (SharedPreferenceHelper.getLoginTypeEmail(RegisterActivity.this)) {
                ToastUtil.showToast(getApplicationContext(), R.string.wrong_phone_or_email_number);
            } else {
                ToastUtil.showToast(getApplicationContext(), R.string.wrong_phone_number);
            }
            return;
        }
        String verifyCode = mCodeEt.getText().toString().trim();
        if (TextUtils.isEmpty(verifyCode)) {
            ToastUtil.showToast(getApplicationContext(), R.string.verify_code_number_null);
            return;
        }
        //密码
        String password = mPassCodeEt.getText().toString().trim();
        if (TextUtils.isEmpty(password)) {
            ToastUtil.showToast(getApplicationContext(), R.string.please_input_password);
            return;
        }
        if (password.length() < 6 || password.length() > 16) {
            ToastUtil.showToast(getApplicationContext(), R.string.length_wrong);
            return;
        }

        if (VerifyUtils.isPhoneNum(phone)) {
            //用于师徒
            String t_system_version = "Android " + SystemUtil.getSystemVersion();
            String deviceNumber = SystemUtil.getOnlyOneId(getApplicationContext());

            Map<String, String> paramMap = new HashMap<>();
            paramMap.put("phone", phone);
            paramMap.put("password", password);
            paramMap.put("smsCode", verifyCode);
            paramMap.put("t_phone_type", "Android");
            paramMap.put("t_system_version", TextUtils.isEmpty(t_system_version) ? "" : t_system_version);
            paramMap.put("deviceNumber", deviceNumber);
            paramMap.put("t_handImg",mHeadImageHttpUrl);
            OkHttpUtils.post().url(ChatApi.REGISTER)
                    .addParams("param", ParamUtil.getParam(paramMap))
                    .build().execute(new AjaxCallback<BaseResponse<ChatUserInfo>>() {
                @Override
                public void onResponse(BaseResponse<ChatUserInfo> response, int id) {
                    LogUtil.i("注册==--", JSON.toJSONString(response));
                    dismissLoadingDialog();
                    //状态码： -2：验证码错误-1:账号已存在,0:程序异常,1.注册成功
                    if (response != null) {
                        if (response.m_istatus == NetCode.SUCCESS) {
                            ToastUtil.showToastLong(getApplicationContext(), R.string.register_success);
                            Intent intent = new Intent(getApplicationContext(), PhoneLoginActivity.class);
                            startActivity(intent);
                            finish();
                        } else {
                            String message = response.m_strMessage;
                            if (!TextUtils.isEmpty(message)) {
                                ToastUtil.showToast(getApplicationContext(), message);
                            } else {
                                ToastUtil.showToast(getApplicationContext(), R.string.register_fail);
                            }
                        }
                    } else {
                        ToastUtil.showToast(getApplicationContext(), R.string.register_fail);
                    }
                }

                @Override
                public void onBefore(Request request, int id) {
                    super.onBefore(request, id);
                    showLoadingDialog();
                }

                @Override
                public void onError(Call call, Exception e, int id) {
                    super.onError(call, e, id);
                    dismissLoadingDialog();
                    ToastUtil.showToast(getApplicationContext(), R.string.system_error);
                }
            });
        } else {
            //用于师徒
            String t_system_version = "Android " + SystemUtil.getSystemVersion();
            String deviceNumber = SystemUtil.getOnlyOneId(getApplicationContext());

            Map<String, String> paramMap = new HashMap<>();
            paramMap.put("email", phone);
            paramMap.put("password", password);
            paramMap.put("smsCode", verifyCode);
            paramMap.put("t_phone_type", "Android");
            paramMap.put("t_system_version", TextUtils.isEmpty(t_system_version) ? "" : t_system_version);
            paramMap.put("deviceNumber", deviceNumber);
            paramMap.put("t_handImg",mHeadImageHttpUrl);
            OkHttpUtils.post().url(ChatApi.EMAIL_REGISTER)
                    .addParams("param", ParamUtil.getParam(paramMap))
                    .build().execute(new AjaxCallback<BaseResponse<ChatUserInfo>>() {
                @Override
                public void onResponse(BaseResponse<ChatUserInfo> response, int id) {
                    LogUtil.i("注册==--", JSON.toJSONString(response));
                    dismissLoadingDialog();
                    //状态码： -2：验证码错误-1:账号已存在,0:程序异常,1.注册成功
                    if (response != null) {
                        if (response.m_istatus == NetCode.SUCCESS) {
                            ToastUtil.showToastLong(getApplicationContext(), R.string.register_success);
                            Intent intent = new Intent(getApplicationContext(), PhoneLoginActivity.class);
                            startActivity(intent);
                            finish();
                        } else {
                            String message = response.m_strMessage;
                            if (!TextUtils.isEmpty(message)) {
                                ToastUtil.showToast(getApplicationContext(), message);
                            } else {
                                ToastUtil.showToast(getApplicationContext(), R.string.register_fail);
                            }
                        }
                    } else {
                        ToastUtil.showToast(getApplicationContext(), R.string.register_fail);
                    }
                }

                @Override
                public void onBefore(Request request, int id) {
                    super.onBefore(request, id);
                    showLoadingDialog();
                }

                @Override
                public void onError(Call call, Exception e, int id) {
                    super.onError(call, e, id);
                    dismissLoadingDialog();
                    ToastUtil.showToast(getApplicationContext(), R.string.system_error);
                }
            });
        }
    }

    /**
     * 发送短信验证码
     */
    private void sendSmsVerifyCode() {
        String phone = mMobileEt.getText().toString().trim();
        if (TextUtils.isEmpty(phone)) {
            if (SharedPreferenceHelper.getLoginTypeEmail(RegisterActivity.this)) {
                ToastUtil.showToast(getApplicationContext(), R.string.phone_number_or_email_null);
            } else {
                ToastUtil.showToast(getApplicationContext(), R.string.phone_number_null);
            }
            return;
        }
        if (!VerifyUtils.isPhoneNum(phone) && !VerifyUtils.isEmail(phone)) {
            if (SharedPreferenceHelper.getLoginTypeEmail(RegisterActivity.this)) {
                ToastUtil.showToast(getApplicationContext(), R.string.wrong_phone_or_email_number);
            } else {
                ToastUtil.showToast(getApplicationContext(), R.string.wrong_phone_number);
            }
            return;
        }

        if (VerifyUtils.isPhoneNum(phone)) {
            Map<String, String> paramMap = new HashMap<>();
            paramMap.put("phone", phone);
            paramMap.put("resType", "1");
            OkHttpUtils.post().url(ChatApi.SEND_SMS_CODE)
                    .addParams("param", ParamUtil.getParam(paramMap))
                    .build().execute(new AjaxCallback<BaseResponse>() {
                @Override
                public void onResponse(BaseResponse response, int id) {
                    LogUtil.i("获取短信验证码==--", JSON.toJSONString(response));
                    dismissLoadingDialog();
                    if (response != null && response.m_istatus == NetCode.SUCCESS) {
                        String message = response.m_strMessage;
                        if (!TextUtils.isEmpty(message) && message.contains(getResources().getString(R.string.send_success))) {
                            ToastUtil.showToast(getApplicationContext(), R.string.send_success_des);
                            startCountDown();
                        }
                    } else if (response != null && response.m_istatus == NetCode.FAIL) {
                        String message = response.m_strMessage;
                        if (!TextUtils.isEmpty(message)) {
                            ToastUtil.showToast(getApplicationContext(), message);
                        } else {
                            ToastUtil.showToast(getApplicationContext(), R.string.send_code_fail);
                        }
                    } else {
                        ToastUtil.showToast(getApplicationContext(), R.string.send_code_fail);
                    }
                }

                @Override
                public void onBefore(Request request, int id) {
                    super.onBefore(request, id);
                    showLoadingDialog();
                }

                @Override
                public void onError(Call call, Exception e, int id) {
                    super.onError(call, e, id);
                    dismissLoadingDialog();
                    ToastUtil.showToast(getApplicationContext(), R.string.system_error);
                }
            });
        } else {
            Map<String, String> paramMap = new HashMap<>();
            paramMap.put("email", phone);
            paramMap.put("resType", "1");
            OkHttpUtils.post().url(ChatApi.SEND_EMAIL_CODE)
                    .addParams("param", ParamUtil.getParam(paramMap))
                    .build().execute(new AjaxCallback<BaseResponse>() {
                @Override
                public void onResponse(BaseResponse response, int id) {
                    LogUtil.i("获取短信验证码==--", JSON.toJSONString(response));
                    dismissLoadingDialog();
                    if (response != null && response.m_istatus == NetCode.SUCCESS) {
                        String message = response.m_strMessage;
                        if (!TextUtils.isEmpty(message) && message.contains(getResources().getString(R.string.send_success))) {
                            ToastUtil.showToast(getApplicationContext(), R.string.send_success_des);
                            startCountDown();
                        }
                    } else if (response != null && response.m_istatus == NetCode.FAIL) {
                        String message = response.m_strMessage;
                        if (!TextUtils.isEmpty(message)) {
                            ToastUtil.showToast(getApplicationContext(), message);
                        } else {
                            ToastUtil.showToast(getApplicationContext(), R.string.send_code_fail);
                        }
                    } else {
                        ToastUtil.showToast(getApplicationContext(), R.string.send_code_fail);
                    }
                }

                @Override
                public void onBefore(Request request, int id) {
                    super.onBefore(request, id);
                    showLoadingDialog();
                }

                @Override
                public void onError(Call call, Exception e, int id) {
                    super.onError(call, e, id);
                    dismissLoadingDialog();
                    ToastUtil.showToast(getApplicationContext(), R.string.system_error);
                }
            });
        }
    }

    /**
     * 开始倒计时
     */
    private void startCountDown() {
        mSendVerifyTv.setClickable(false);
        mCountDownTimer = new CountDownTimer(60000, 1000) {
            @Override
            public void onTick(long l) {
                mSendVerifyTv.setTextColor(getResources().getColor(R.color.black_bbbbbb));
                String text = getResources().getString(R.string.re_send_one) + l / 1000 + getResources().getString(R.string.second);
                mSendVerifyTv.setText(text);
            }

            @Override
            public void onFinish() {
                mSendVerifyTv.setClickable(true);
                mSendVerifyTv.setTextColor(getResources().getColor(R.color.violet_d81aff));
                mSendVerifyTv.setText(R.string.get_code_one);
                if (mCountDownTimer != null) {
                    mCountDownTimer.cancel();
                    mCountDownTimer = null;
                }
            }
        }.start();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mCountDownTimer != null) {
            mCountDownTimer.cancel();
            mCountDownTimer = null;
        }
    }
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if ((requestCode == Constant.REQUEST_CODE_CHOOSE || requestCode == Constant.REQUEST_CODE_CHOOSE_HEAD_IMG)
                && resultCode == RESULT_OK) {//图库选择
            List<Uri> mSelectedUris = Matisse.obtainResult(data);
            if (mSelectedUris != null && mSelectedUris.size() > 0) {
                try {
                    Uri uri = mSelectedUris.get(0);
                    String filePath = FileUtil.getPathAbove19(this, uri);
                    if (!TextUtils.isEmpty(filePath)) {
                        File file = new File(filePath);
                        if (!file.exists()) {
                            LogUtil.i("文件不存在 ");
                        } else {
                            LogUtil.i("文件大小: " + file.length() / 1024);
                        }
                        //直接裁剪
                        if (requestCode == Constant.REQUEST_CODE_CHOOSE) {
                            cutWithUCrop(filePath, true);
                        } else {
                            cutWithUCrop(filePath, false);
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        } else if (resultCode == RESULT_OK
                && (requestCode == Constant.UCROP_REQUEST_CODE_COVER || requestCode == Constant.UCROP_REQUEST_CODE_HEAD)) {
            Uri resultUri = UCrop.getOutput(data);
            String filePath = FileUtil.getPathAbove19(this, resultUri);

                mHeadImageLocalPath = filePath;
                int width = DevicesUtil.dp2px(getApplicationContext(), 54);
                int height = DevicesUtil.dp2px(getApplicationContext(), 54);
                ImageLoadHelper.glideShowCircleImageWithUri(this, resultUri, headimage,
                        width, height);
            uploadHeadFileWithQQ(new OnFileUploadListener() {
                @Override
                public void onFileUploadSuccess() {

                }
            });
        }

    }
    /**
     * 使用u crop裁剪
     */
    private void cutWithUCrop(String sourceFilePath, boolean fromCover) {
        //计算 图片resize的大小
        int overWidth;
        int overHeight;
        if (fromCover) {
            overWidth = DevicesUtil.getScreenW(mContext);
            overHeight = DevicesUtil.dp2px(mContext, 435);
        } else {
            overWidth = DevicesUtil.getScreenW(getApplicationContext());
            overHeight = DevicesUtil.getScreenW(getApplicationContext());
        }
        //目录路径
        String dirPath;
        if (fromCover) {
            dirPath = Constant.COVER_AFTER_SHEAR_DIR;
        } else {
            dirPath = Constant.HEAD_AFTER_SHEAR_DIR;
        }
        File pFile = new File(FileUtil.YCHAT_DIR);
        if (!pFile.exists()) {
            pFile.mkdir();
        }
        File file = new File(dirPath);
        if (!file.exists()) {
            file.mkdir();
        }
        if (!fromCover) {
            FileUtil.deleteFiles(dirPath);
        }
        //文件名
        String filePath = file.getPath() + File.separator + System.currentTimeMillis() + ".png";
        //请求码
        int requestCode;
        if (fromCover) {
            requestCode = Constant.UCROP_REQUEST_CODE_COVER;
        } else {
            requestCode = Constant.UCROP_REQUEST_CODE_HEAD;
        }
        File sourceFile = new File(sourceFilePath);
        if (!sourceFile.exists()) {
            ToastUtil.showToast(getApplicationContext(), R.string.file_not_exist);
            return;
        }
        UCrop.of(Uri.fromFile(new File(sourceFilePath)), Uri.fromFile(new File(filePath)))
                .withAspectRatio(overWidth, overHeight)
                .withMaxResultSize(overWidth, overHeight)
                .start(this, requestCode);
    }

    /**
     * 上传头像
     */
    private void uploadHeadFileWithQQ(final OnFileUploadListener listener) {
        if (TextUtils.isEmpty(mHeadImageLocalPath)) {//如果头像为空
            listener.onFileUploadSuccess();
            return;
        }
        String fileName = mHeadImageLocalPath.substring(mHeadImageLocalPath.length() - 17, mHeadImageLocalPath.length());
        String cosPath = "/head/" + fileName;
        long signDuration = 600; //签名的有效期，单位为秒
        PutObjectRequest putObjectRequest = new PutObjectRequest(BuildConfig.tencentCloudBucket, cosPath, mHeadImageLocalPath);
        putObjectRequest.setSign(signDuration, null, null);
        mQServiceCfg.getCosCxmService().putObjectAsync(putObjectRequest, new CosXmlResultListener() {
            @Override
            public void onSuccess(CosXmlRequest request, CosXmlResult result) {
                LogUtil.i("腾讯云success 头像=  " + result.accessUrl);
                mHeadImageHttpUrl = result.accessUrl;
                if (!mHeadImageHttpUrl.contains("http") || !mHeadImageHttpUrl.contains("https")) {
                    mHeadImageHttpUrl = "https://" + mHeadImageHttpUrl;
                }
                if (listener != null) {
                    listener.onFileUploadSuccess();
                }
            }

            @Override
            public void onFail(CosXmlRequest cosXmlRequest, CosXmlClientException clientException, CosXmlServiceException serviceException) {
                String errorMsg = clientException != null ? clientException.toString() : serviceException.toString();
                LogUtil.i("腾讯云fail: " + errorMsg);
            }
        });
    }

}
