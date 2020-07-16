package com.yiliao.chat.myactivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Looper;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.tencent.cos.xml.exception.CosXmlClientException;
import com.tencent.cos.xml.exception.CosXmlServiceException;
import com.tencent.cos.xml.listener.CosXmlResultListener;
import com.tencent.cos.xml.model.CosXmlRequest;
import com.tencent.cos.xml.model.CosXmlResult;
import com.tencent.cos.xml.model.object.PutObjectRequest;
import com.yalantis.ucrop.UCrop;
import com.yiliao.chat.BuildConfig;
import com.yiliao.chat.R;
import com.yiliao.chat.activity.ModifyUserInfoActivity;
import com.yiliao.chat.base.BaseActivity;
import com.yiliao.chat.base.BaseResponse;
import com.yiliao.chat.constant.ChatApi;
import com.yiliao.chat.constant.Constant;
import com.yiliao.chat.dialog.DialogUitl;
import com.yiliao.chat.helper.ImageHelper;
import com.yiliao.chat.helper.ImageLoadHelper;
import com.yiliao.chat.listener.OnFileUploadListener;
import com.yiliao.chat.myactivity.bean.ActivtyDetalisBean;
import com.yiliao.chat.net.AjaxCallback;
import com.yiliao.chat.net.NetCode;
import com.yiliao.chat.oss.QServiceCfg;
import com.yiliao.chat.util.DevicesUtil;
import com.yiliao.chat.util.FileUtil;
import com.yiliao.chat.util.LogUtil;
import com.yiliao.chat.util.ParamUtil;
import com.yiliao.chat.util.ToastUtil;
import com.yiliao.chat.util.WordUtil;
import com.zhihu.matisse.Matisse;
import com.zhy.http.okhttp.OkHttpUtils;

import java.io.File;
import java.util.HashMap;
import java.util.List;

import okhttp3.Call;

/*
* 发起活动
* */
public class EstablishActivity extends BaseActivity implements View.OnClickListener {
    //标题
    private EditText Establish_Title;
    //内容
    private EditText Establish_Content;
    //标题字数
    private TextView Establish_TitleNum;
    //背景图
    private ImageView Establish_Image;
    //活动地址
    private TextView Establish_Address;
    //活动发起日期
    private TextView Establish_SDate;
    //活动发起时间
    private TextView Establish_STime;
    //活动截止日期
    private TextView Establish_EDate;
    //活动报名截止时间
    private TextView Establish_ETime;
    //手机号码
    private EditText Establish_Phone;
    //参加人数
    private EditText Establish_JoinNum;
    //男性收费
    private EditText Establish_MalePrice;
    //女性收费
    private EditText Establish_FemalePrice;
    //微信
    private EditText Establish_WeChat;
    //提交
    private TextView Establish;
    //封面路径
    private String ImageLocalPath = "";
    //封面网络地址
    private String urlPath="";
    //活动id
    private String activityId="";
    //腾讯云
    private QServiceCfg mQServiceCfg;

    private String Address;
    private  String Lat;
    private String Lna;

    private String Type="";

    private ActivtyDetalisBean bean;
    @Override
    protected View getContentView() {
        return inflate(R.layout.establish_activity);
    }

    @Override
    protected void onContentAdded() {
        mQServiceCfg = QServiceCfg.instance(getApplicationContext());
        Type=getIntent().getStringExtra("TYPE");
        if (Type.equals("Edit")){
            activityId=getIntent().getStringExtra("ACTIVITYID");
            bean= (ActivtyDetalisBean) getIntent().getSerializableExtra("Data");
        }
        setTitle("发起活动");
        getInitView();
    }

    //初始化
    private void getInitView(){
        Establish_Title = findViewById(R.id.Establish_Title);
        Establish_Content=findViewById(R.id.Establish_Content);
        Establish_TitleNum =findViewById(R.id.Establish_TitleNum);
        Establish_Image=findViewById(R.id.Establish_Image);
        Establish_Image.setOnClickListener(this);
        Establish_Address=findViewById(R.id.Establish_Address);
        Establish_Address.setOnClickListener(this);
        Establish_SDate=findViewById(R.id.Establish_SDate);
        Establish_SDate.setOnClickListener(this);
        Establish_STime=findViewById(R.id.Establish_STime);
        Establish_STime.setOnClickListener(this);
        Establish_EDate=findViewById(R.id.Establish_EDate);
        Establish_EDate.setOnClickListener(this);
        Establish_ETime=findViewById(R.id.Establish_ETime);
        Establish_ETime.setOnClickListener(this);
        Establish_Phone=findViewById(R.id.Establish_Phone);
        Establish_JoinNum=findViewById(R.id.Establish_JoinNum);
        Establish_MalePrice=findViewById(R.id.Establish_MalePrice);
        Establish_FemalePrice=findViewById(R.id.Establish_FemalePrice);
        Establish_WeChat=findViewById(R.id.Establish_WeChat);
        Establish=findViewById(R.id.Establish);
        Establish.setOnClickListener(this);

        Establish_Content.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (s.toString().length()<100){
                    Establish_TitleNum.setText(s.toString().length()+"/100");
                }else {
                    ToastUtil.show("标题在100以内");
                }
            }
        });

        if (Type.equals("Edit")){
            Establish_Title.setText(bean.t_title);
            Establish_Content.setText(bean.t_content);
            Establish_Address.setText(bean.t_address);
            Establish_SDate.setText(bean.t_activity_time.substring(0,10));
            Establish_STime.setText(bean.t_activity_time.substring(11,bean.t_activity_time.length()));
            Establish_EDate.setText(bean.t_apply_time.substring(0,10));
            Establish_ETime.setText(bean.t_apply_time.substring(11,bean.t_apply_time.length()));
            Establish_Phone.setText(bean.t_phone);
            Establish_JoinNum.setText(""+bean.t_count);
            Establish_MalePrice.setText(bean.t_man_price);
            Establish_FemalePrice.setText(bean.t_woman_price);
            Establish_WeChat.setText(bean.t_wx);

            Lat=bean.t_lat;
            Lna=bean.t_lng;
            Address=bean.t_address;
            urlPath=bean.t_img;
            ImageLoadHelper.glideShowImageWithUrl(getApplicationContext(), bean.t_img,Establish_Image);
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.Establish_SDate://活动发起日期
                DialogUitl.showDatePickerDialog(mContext, new DialogUitl.DataPickerCallback() {
                    @Override
                    public void onConfirmClick(final String date) {
                        Establish_SDate.setText(date);
                    }
                });
                break;
            case R.id.Establish_STime://活动发起时间
                DialogUitl.showTimePickerDialog(mContext, new DialogUitl.DataPickerCallback() {
                    @Override
                    public void onConfirmClick(final String date) {
                        Establish_STime.setText(date);
                    }
                });
                break;
            case R.id.Establish_EDate://活动截止日期
                DialogUitl.showDatePickerDialog(mContext, new DialogUitl.DataPickerCallback() {
                    @Override
                    public void onConfirmClick(final String date) {
                        Establish_EDate.setText(date);
                    }
                });
                break;
            case R.id.Establish_ETime://活动截止时间
                DialogUitl.showTimePickerDialog(mContext, new DialogUitl.DataPickerCallback() {
                    @Override
                    public void onConfirmClick(final String date) {
                        Establish_ETime.setText(date);
                    }
                });
                break;
            case R.id.Establish_Image://上传封面
                //图片选择
                ImageHelper.openPictureChoosePage(EstablishActivity.this, Constant.REQUEST_CODE_CHOOSE);
                break;
            case R.id.Establish://提交
                if (urlPath==null||urlPath.equals("")){
                    ToastUtil.show("请上传封面");
                    return;
                }
                if (Establish_Title.getText().toString()==null||Establish_Title.getText().toString().equals("")){
                    ToastUtil.show("请活动输入标题");
                    return;
                }
                if (Establish_Content.getText().toString()==null||Establish_Content.getText().toString().equals("")){
                    ToastUtil.show("请输入活动内容");
                    return;
                }
                if (Establish_SDate.getText().toString()==null||Establish_SDate.getText().toString().equals("")){
                    ToastUtil.show("请输入活动开始日期");
                    return;
                }
                if (Establish_STime.getText().toString()==null||Establish_STime.getText().toString().equals("")){
                    ToastUtil.show("请输入活动开始时间");
                    return;
                }
                if (Establish_EDate.getText().toString()==null||Establish_EDate.getText().toString().equals("")){
                    ToastUtil.show("请输入活动结束日期");
                    return;
                }
                if (Establish_ETime.getText().toString()==null||Establish_ETime.getText().toString().equals("")){
                    ToastUtil.show("请输入活动结束时间");
                    return;
                }
                if (Establish_Phone.getText().toString()==null||Establish_Phone.getText().toString().equals("")){
                    ToastUtil.show("请输入手机号");
                    return;
                }
                if (Establish_WeChat.getText().toString()==null||Establish_WeChat.getText().toString().equals("")){
                    ToastUtil.show("请输入微信");
                    return;
                }
                if (Establish_JoinNum.getText().toString()==null||Establish_JoinNum.getText().toString().equals("")){
                    ToastUtil.show("请输入参加人数");
                    return;
                }
                if (Establish_MalePrice.getText().toString()==null||Establish_MalePrice.getText().toString().equals("")){
                    ToastUtil.show("请输入男性收费标准");
                    return;
                }
                if (Establish_FemalePrice.getText().toString()==null||Establish_FemalePrice.getText().toString().equals("")){
                    ToastUtil.show("请输入女性收费标准");
                    return;
                }
                if (Address==null||Address.equals("")){
                    ToastUtil.show("请输入活动地址");
                    return;
                }
//                if (Type.equals("Edit")){
                    Submit();
//                }else {

//                }

                break;
            case R.id.Establish_Address://添加地址
                Intent intent = new Intent(getApplicationContext(),AddLocationActivity.class);

                startActivityForResult(intent,10000);
                break;
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

                ImageLocalPath = filePath;
                int width = DevicesUtil.dp2px(getApplicationContext(), 146);
                int height = DevicesUtil.dp2px(getApplicationContext(), 56);
                ImageLoadHelper.glideShowImageWithUri(this, resultUri, Establish_Image,
                        width, height);

            uploadCoverFileWithQQ(new OnFileUploadListener() {
                @Override
                public void onFileUploadSuccess() {

                }
            });

        }else if(requestCode==10000&&resultCode==20000){
             Address = data.getStringExtra("ADDRESS");
             Lat =data.getStringExtra("LAT");
             Lna =data.getStringExtra("LNA");

            Log.i("aaa",Address);

            Establish_Address.setText(Address);
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
            overHeight = DevicesUtil.getScreenW(mContext)/2;
        } else {
            overWidth = DevicesUtil.getScreenW(getApplicationContext());
            overHeight = DevicesUtil.getScreenW(getApplicationContext())/2;
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
     * 使用腾讯云上传封面文件
     */
    private void uploadCoverFileWithQQ( final OnFileUploadListener listener) {
        String filePath = ImageLocalPath;

        //文件名
        String fileName;
        if (filePath.length() < 50) {
            fileName = filePath.substring(filePath.length() - 17, filePath.length());
        } else {
            String last = filePath.substring(filePath.length() - 4, filePath.length());
            if (last.contains("png")) {
                fileName = System.currentTimeMillis() + ".png";
            } else {
                fileName = System.currentTimeMillis() + ".jpg";
            }
        }

        String cosPath = "/cover/" + fileName;
        long signDuration = 600; //签名的有效期，单位为秒
        PutObjectRequest putObjectRequest = new PutObjectRequest(BuildConfig.tencentCloudBucket, cosPath, filePath);
        putObjectRequest.setSign(signDuration, null, null);
        mQServiceCfg.getCosCxmService().putObjectAsync(putObjectRequest, new CosXmlResultListener() {
            @Override
            public void onSuccess(CosXmlRequest request, CosXmlResult result) {
                LogUtil.i("腾讯云success =  " + result.accessUrl);
                String resultUrl = result.accessUrl;
                if (!resultUrl.contains("http") || !resultUrl.contains("https")) {
                    resultUrl = "https://" + resultUrl;
                }

                urlPath = resultUrl;


            }

            @Override
            public void onFail(CosXmlRequest cosXmlRequest, CosXmlClientException clientException, CosXmlServiceException serviceException) {
                String errorMsg = clientException != null ? clientException.toString() : serviceException.toString();
                LogUtil.i("腾讯云fail: " + errorMsg);
                dismissLoadingDialog();
                Looper.prepare();
                ToastUtil.showToast(EstablishActivity.this, R.string.upload_fail);
                Looper.loop();
            }
        });
    }


    //提交数据
    public void Submit(){
        HashMap<String,String> map = new HashMap<>();
        map.put("activityId",activityId);
        map.put("userId",getUserId());
        map.put("t_phone",Establish_Phone.getText().toString());
        map.put("t_wx",Establish_WeChat.getText().toString());
        map.put("t_img",urlPath);
        map.put("t_title",Establish_Title.getText().toString());
        map.put("t_content",Establish_Content.getText().toString());
        map.put("t_man_price",Establish_MalePrice.getText().toString());
        map.put("t_woman_price",Establish_FemalePrice.getText().toString());
        map.put("t_count",Establish_JoinNum.getText().toString());
        map.put("t_activity_time",Establish_SDate.getText().toString()+" "+Establish_STime.getText().toString());
        map.put("t_apply_time",Establish_EDate.getText().toString()+" "+Establish_ETime.getText().toString());
        map.put("t_address",Address);
        map.put("t_lng",Lna);
        map.put("t_lat",Lat);

        OkHttpUtils.post().url(ChatApi.ESTABLISH_ACTIVITY).
        addParams("param", ParamUtil.getParam(map))
                .build().execute(new AjaxCallback<BaseResponse>() {
            @Override
            public void onResponse(BaseResponse response, int id) {
                dismissLoadingDialog();
                if (response != null) {
                    if (response.m_istatus == NetCode.SUCCESS) {
                        String message = response.m_strMessage;
                        if (!TextUtils.isEmpty(message) && message.contains(getResources().getString(R.string.success_str))) {
                            ToastUtil.showToast(getApplicationContext(), message);
                            finish();
                        }
                    } else {
                        String message = response.m_strMessage;

                            ToastUtil.showToast(getApplicationContext(), message);

                    }
                } else {
                    ToastUtil.showToast(getApplicationContext(), "失败");
                }
            }

            @Override
            public void onError(Call call, Exception e, int id) {
                super.onError(call, e, id);
                dismissLoadingDialog();
                ToastUtil.showToast(getApplicationContext(), "发起活动失败");
            }

        });
    }
}
