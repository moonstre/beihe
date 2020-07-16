package com.yiliao.chat.activity;

import android.content.Intent;
import android.net.Uri;
import android.text.Html;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

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
import com.yiliao.chat.util.VerifyUtils;
import com.zhihu.matisse.Matisse;
import com.zhy.http.okhttp.OkHttpUtils;

import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.OnClick;

public class NewApplyCompanyActivity extends BaseActivity {
    @BindView(R.id.company_name_et)
    EditText mCompanyNameEt;
    @BindView(R.id.contact_et)
    EditText mContactEt;
    @BindView(R.id.apply_count_et)
    EditText mApplyCountEt;
    @BindView(R.id.agree_iv)
    ImageView mAgreeIv;
    @BindView(R.id.agree_tv)
    TextView agree_tv;
    //腾讯云

    @Override
    protected View getContentView() {
        return inflate(R.layout.new_apply_company_layout);
    }

    @Override
    protected void onContentAdded() {
        setTitle(R.string.apply_company);
        mAgreeIv.setSelected(true);
        String agree_value="已阅读并同意"+"<font color=\"#FFE073\">" +"《同创公会协议书》" + "</font>";
        agree_tv.setText(Html.fromHtml(agree_value));
    }

    @OnClick({R.id.agree_iv, R.id.share_now_tv, R.id.agree_tv})
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.agree_iv: {
                if (mAgreeIv.isSelected()) {
                    mAgreeIv.setSelected(false);
                } else {
                    mAgreeIv.setSelected(true);
                }
                break;
            }
            case R.id.share_now_tv: {
                applyCompany();
                break;
            }
            case R.id.agree_tv: {//协议
                Intent intent = new Intent(getApplicationContext(), CommonWebViewActivity.class);
                intent.putExtra(Constant.TITLE, getResources().getString(R.string.chat_company_agree));
                intent.putExtra(Constant.URL, "file:///android_asset/company_agree.html");
                startActivity(intent);
                break;
            }
        }
    }

    /**
     * 申请工会
     */
    private void applyCompany() {
        //公会名
        String companyName = mCompanyNameEt.getText().toString().trim();
        if (TextUtils.isEmpty(companyName)) {
            ToastUtil.showToast(getApplicationContext(), R.string.please_input_company_name);
            return;
        }
        //申请人电话
        String contactNumber = mContactEt.getText().toString().trim();
        if (TextUtils.isEmpty(contactNumber)) {
            ToastUtil.showToast(getApplicationContext(), R.string.please_input_apply_contact);
            return;
        }
        if (!VerifyUtils.isPhoneNum(contactNumber)) {
            ToastUtil.showToast(getApplicationContext(), R.string.wrong_phone_number);
            return;
        }
        //主播人数
        String actorNumber = mApplyCountEt.getText().toString().trim();
        if (TextUtils.isEmpty(actorNumber)) {
            ToastUtil.showToast(getApplicationContext(), R.string.please_input_apply_count);
            return;
        }
        //协议
        if (!mAgreeIv.isSelected()) {
            ToastUtil.showToast(getApplicationContext(), R.string.not_agree);
            return;
        }
        uploadInfo();
    }





    @Override
    protected void onDestroy() {
        super.onDestroy();

    }



    /**
     * 上传信息到公司接口
     */
    private void uploadInfo() {
        //公会名
        String companyName = mCompanyNameEt.getText().toString().trim();
        //申请人电话
        String contactNumber = mContactEt.getText().toString().trim();
        //主播人数
        String actorNumber = mApplyCountEt.getText().toString().trim();

        Map<String, String> paramMap = new HashMap<>();
        paramMap.put("userId", getUserId());
        paramMap.put("guildName", companyName);
        paramMap.put("adminPhone", contactNumber);
        paramMap.put("anchorNumber", actorNumber);
        OkHttpUtils.post().url(ChatApi.APPLY_GUILD)
                .addParams("param", ParamUtil.getParam(paramMap))
                .build().execute(new AjaxCallback<BaseResponse>() {
            @Override
            public void onResponse(BaseResponse response, int id) {
                if (response != null && response.m_istatus == NetCode.SUCCESS) {
                    ToastUtil.showToast(getApplicationContext(), R.string.apply_success);
                    finish();
                } else {
                    ToastUtil.showToast(getApplicationContext(), response.m_strMessage);
                }
            }
        });
    }
}
