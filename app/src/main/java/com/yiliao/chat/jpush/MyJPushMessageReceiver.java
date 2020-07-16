package com.yiliao.chat.jpush;

import android.content.Context;

import com.yiliao.chat.base.AppManager;
import com.yiliao.chat.base.BaseResponse;
import com.yiliao.chat.bean.ChatUserInfo;
import com.yiliao.chat.constant.ChatApi;
import com.yiliao.chat.helper.SharedPreferenceHelper;
import com.yiliao.chat.net.AjaxCallback;
import com.yiliao.chat.util.LogUtil;
import com.yiliao.chat.util.ParamUtil;
import com.zhy.http.okhttp.OkHttpUtils;

import java.util.HashMap;
import java.util.Map;

import cn.jpush.android.api.JPushMessage;
import cn.jpush.android.service.JPushMessageReceiver;

/**
 * 自定义JPush message 接收器,包括操作tag/alias的结果返回(仅仅包含tag/alias新接口部分)
 */
public class MyJPushMessageReceiver extends JPushMessageReceiver {

    @Override
    public void onTagOperatorResult(Context context, JPushMessage jPushMessage) {
        super.onTagOperatorResult(context, jPushMessage);
    }

    @Override
    public void onCheckTagOperatorResult(Context context, JPushMessage jPushMessage) {
        super.onCheckTagOperatorResult(context, jPushMessage);
    }

    @Override
    public void onAliasOperatorResult(Context context, JPushMessage jPushMessage) {
        LogUtil.i("设置别名: code " + jPushMessage.getErrorCode());
        if (jPushMessage.getErrorCode() == 0) {
            LogUtil.i(" 极光别名alisa: " + jPushMessage.getAlias());
            SharedPreferenceHelper.saveJPushAlias(context, jPushMessage.getAlias());
            getWelcomeMessage(context);
        }
        super.onAliasOperatorResult(context, jPushMessage);
    }

    @Override
    public void onMobileNumberOperatorResult(Context context, JPushMessage jPushMessage) {
        super.onMobileNumberOperatorResult(context, jPushMessage);
    }

    /**
     * 获取欢迎消息
     */
    private void getWelcomeMessage(Context context) {
        Map<String, String> paramMap = new HashMap<>();
        paramMap.put("userId", getUserId(context));
        OkHttpUtils.post().url(ChatApi.GET_PUSH_MSG)
                .addParams("param", ParamUtil.getParam(paramMap))
                .build().execute(new AjaxCallback<BaseResponse>() {
            @Override
            public void onResponse(BaseResponse response, int id) {

            }
        });
    }

    /**
     * 获取UserId
     */
    public String getUserId(Context context) {
        String sUserId = "";
        if (AppManager.getInstance() != null) {
            ChatUserInfo userInfo = AppManager.getInstance().getUserInfo();
            if (userInfo != null) {
                int userId = userInfo.t_id;
                if (userId >= 0) {
                    sUserId = String.valueOf(userId);
                }
            } else {
                int id = SharedPreferenceHelper.getAccountInfo(context.getApplicationContext()).t_id;
                sUserId = String.valueOf(id);
            }
        }
        return sUserId;
    }
}
