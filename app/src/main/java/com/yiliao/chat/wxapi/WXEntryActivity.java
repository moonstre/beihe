package com.yiliao.chat.wxapi;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.graphics.Point;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.tencent.mm.opensdk.constants.ConstantsAPI;
import com.tencent.mm.opensdk.modelbase.BaseReq;
import com.tencent.mm.opensdk.modelbase.BaseResp;
import com.tencent.mm.opensdk.modelmsg.SendAuth;
import com.tencent.mm.opensdk.openapi.IWXAPI;
import com.tencent.mm.opensdk.openapi.IWXAPIEventHandler;
import com.tencent.mm.opensdk.openapi.WXAPIFactory;
import com.yiliao.chat.R;
import com.yiliao.chat.activity.ApplyVerifyActivity;
import com.yiliao.chat.base.AppManager;
import com.yiliao.chat.base.BaseResponse;
import com.yiliao.chat.bean.ChatUserInfo;
import com.yiliao.chat.constant.ChatApi;
import com.yiliao.chat.constant.Constant;
import com.yiliao.chat.helper.SharedPreferenceHelper;
import com.yiliao.chat.net.AjaxCallback;
import com.yiliao.chat.net.NetCode;
import com.yiliao.chat.util.LogUtil;
import com.yiliao.chat.util.ParamUtil;
import com.yiliao.chat.util.ToastUtil;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.StringCallback;

import java.util.HashMap;
import java.util.Map;

import okhttp3.Call;

/*
 * Copyright (C) 2018
 * 版权所有
 *
 * 功能描述：微信页面
 * 作者：
 * 创建时间：2018/6/28
 *
 * 修改人：
 * 修改描述：
 * 修改日期
 *
 *
 * 备注 ：暂时未用的此类 ，为了保证项目的运行，屏蔽了一些代码
 */
public class WXEntryActivity extends Activity implements IWXAPIEventHandler {

    private IWXAPI api;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //通过WXAPIFactory工厂获取IWXApI的示例
      /*  api = WXAPIFactory.createWXAPI(getApplicationContext(), Constant.WE_CHAT_APPID, true);
        //将应用的app id注册到微信
        api.registerApp(Constant.WE_CHAT_APPID);
        //注意：
        //第三方开发者如果使用透明界面来实现WXEntryActivity，需要判断handleIntent的返回值，如果返回值为false，则说明入参不合法未被SDK处理，应finish当前透明界面，避免外部通过传递非法参数的Intent导致停留在透明界面，引起用户的疑惑
        api.handleIntent(getIntent(), this);
        LogUtil.i("wxonCreate: ");*/
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        api.handleIntent(data, this);
        LogUtil.i("wxonActivityResult: ");
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        setIntent(intent);
        api.handleIntent(intent, this);
        finish();
    }

    @Override
    public void onReq(BaseReq baseReq) {
        LogUtil.i("baseReq:" + JSON.toJSONString(baseReq));
    }

    @Override
    public void onResp(BaseResp baseResp) {
        LogUtil.i("baseResp:" + JSON.toJSONString(baseResp));
        LogUtil.i("baseResp:" + baseResp.errStr + "," + baseResp.openId + "," + baseResp.transaction + "," + baseResp.errCode);
        switch (baseResp.errCode) {
            case BaseResp.ErrCode.ERR_OK:
                if (baseResp.getType() == ConstantsAPI.COMMAND_SENDAUTH) {//微信登录
                    SendAuth.Resp resp = (SendAuth.Resp) baseResp;
                    if (!TextUtils.isEmpty(resp.code)) {
                        getAccessToken(resp.code);
                    } else {
                        ToastUtil.showToast(getApplicationContext(), R.string.login_fail);
                        finish();
                    }
                } else if (baseResp.getType() == ConstantsAPI.COMMAND_SENDMESSAGE_TO_WX) {//微信分享
                    LogUtil.i("微信分享成功");
                    if (AppManager.getInstance().getIsMainPageShareQun()) {
                        //继续完成分享
                        addShareTime();
                    } else {
                        ToastUtil.showToast(getApplicationContext(), R.string.share_success);
                        finish();
                    }
                }
                break;
            case BaseResp.ErrCode.ERR_USER_CANCEL:
                if (baseResp.getType() == ConstantsAPI.COMMAND_SENDAUTH) {//微信登录
                    ToastUtil.showToast(getApplicationContext(), R.string.login_cancel);
                    finish();
                } else if (baseResp.getType() == ConstantsAPI.COMMAND_SENDMESSAGE_TO_WX) {//微信分享
                    ToastUtil.showToast(getApplicationContext(), R.string.share_cancel);
                    finish();
                }
                break;
            case BaseResp.ErrCode.ERR_AUTH_DENIED:
                finish();
                break;
            default:
                finish();
                break;
        }
    }

    /**
     * 获取Access_token
     */
    private void getAccessToken(String code) {
       /* OkHttpUtils.get().url(ChatApi.WX_GET_ACCESS_TOKEN)
                .addParams("appid", Constant.WE_CHAT_APPID)
                .addParams("secret", Constant.WE_CHAT_SECRET)
                .addParams("code", code)
                .addParams("grant_type", "authorization_code")
                .build().execute(new StringCallback() {
            @Override
            public void onError(Call call, Exception e, int id) {
                ToastUtil.showToast(getApplicationContext(), R.string.login_fail);
                finish();
            }

            @Override
            public void onResponse(String response, int id) {
                if (!TextUtils.isEmpty(response)) {
                    JSONObject jsonObject = JSON.parseObject(response);
                    String token = jsonObject.getString("access_token");
                    String openId = jsonObject.getString("openid");
                    if (!TextUtils.isEmpty(token) && !TextUtils.isEmpty(openId)) {
                        getUserInfo(token, openId);
                    } else {
                        ToastUtil.showToast(getApplicationContext(), R.string.login_fail);
                    }
                } else {
                    ToastUtil.showToast(getApplicationContext(), R.string.login_fail);
                    finish();
                }
            }
        });*/
    }

    /**
     * 获取UserInfo
     */
    private void getUserInfo(String token, String openId) {
        OkHttpUtils.get().url(ChatApi.WX_GET_USER_INFO)
                .addParams("access_token", token)
                .addParams("openid", openId)//openid:授权用户唯一标识
                .build().execute(new StringCallback() {
            @Override
            public void onError(Call call, Exception e, int id) {
                ToastUtil.showToast(getApplicationContext(), R.string.login_fail);
                finish();
            }

            @Override
            public void onResponse(String response, int id) {
                if (!TextUtils.isEmpty(response)) {
                    JSONObject object = JSON.parseObject(response);
                    if (object != null) {
                        if (!AppManager.getInstance().getIsWeChatBindAccount()) {//微信登录
//                            loginWx(object);
                        } else {//绑定微信号 提现
                            LogUtil.i("绑定微信号 提现");
                            //发送广播传递信息回去
                            String nickName = object.getString("nickname");
                            String handImg = object.getString("headimgurl");
                            String openId = object.getString("openid");
                            Intent intent = new Intent(Constant.WECHAT_WITHDRAW_ACCOUNT);
                            intent.putExtra(Constant.WECHAT_NICK_INFO, nickName);
                            intent.putExtra(Constant.WECHAT_HEAD_URL, handImg);
                            intent.putExtra(Constant.WECHAT_OPEN_ID, openId);
                            sendBroadcast(intent);
                            finish();
                        }
                    } else {
                        ToastUtil.showToast(getApplicationContext(), R.string.login_fail);
                        finish();
                    }
                } else {
                    ToastUtil.showToast(getApplicationContext(), R.string.login_fail);
                    finish();
                }
            }
        });
    }

    /**
     * 分享微信群完成后 添加分享次数
     */
    private void addShareTime() {
        Map<String, String> paramMap = new HashMap<>();
        paramMap.put("userId", getUserId());
        OkHttpUtils.post().url(ChatApi.ADD_SHARE_COUNT)
                .addParams("param", ParamUtil.getParam(paramMap))
                .build().execute(new AjaxCallback<BaseResponse<Integer>>() {
            @Override
            public void onResponse(BaseResponse<Integer> response, int id) {
                if (response != null && response.m_istatus == NetCode.SUCCESS) {
                    Integer m_object = response.m_object;
                    showThreeQunDialog(m_object);
                } else {
                    finish();
                }
            }

            @Override
            public void onError(Call call, Exception e, int id) {
                super.onError(call, e, id);
                finish();
            }
        });
    }

    /**
     * 显示分享到3个群dialog
     */
    private void showThreeQunDialog(int count) {
        final Dialog mDialog = new Dialog(WXEntryActivity.this, R.style.DialogStyle_Dark_Background);
        View view = LayoutInflater.from(WXEntryActivity.this).inflate(R.layout.dialog_share_qun_success_layout, null);
        setThreeQunDialogView(view, mDialog, count);
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

    private void setThreeQunDialogView(View view, final Dialog mDialog, int count) {
        //描述
        TextView des_tv = view.findViewById(R.id.des_tv);
        //按钮
        TextView confirm_tv = view.findViewById(R.id.confirm_tv);
        final int leftTime = 3 - count;
        if (leftTime > 0) {//还剩几次,继续分享
            String content = getResources().getString(R.string.need_one) + leftTime + getResources().getString(R.string.need_two);
            des_tv.setText(content);
            confirm_tv.setText(getResources().getString(R.string.continue_share));
        } else {//分享完成  开始认证
            des_tv.setText(getResources().getString(R.string.back_app));
            confirm_tv.setText(getResources().getString(R.string.back_and_verify));
        }
        confirm_tv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mDialog.dismiss();
                if (leftTime > 0) {
                    finish();
                } else {
                    Intent broadcastIntent = new Intent(Constant.QUN_SHARE_QUN_CLOSE);
                    sendBroadcast(broadcastIntent);
                    Intent intent = new Intent(getApplicationContext(), ApplyVerifyActivity.class);
                    startActivity(intent);
                    finish();
                }
            }
        });
    }

    /**
     * 获取UserId
     */
    private String getUserId() {
        String sUserId = "";
        if (AppManager.getInstance() != null) {
            ChatUserInfo userInfo = AppManager.getInstance().getUserInfo();
            if (userInfo != null) {
                int userId = userInfo.t_id;
                if (userId >= 0) {
                    sUserId = String.valueOf(userId);
                }
            } else {
                int id = SharedPreferenceHelper.getAccountInfo(getApplicationContext()).t_id;
                sUserId = String.valueOf(id);
            }
        }
        return sUserId;
    }

}
