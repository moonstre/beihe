package com.yiliao.chat.activity;

import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.Uri;
import android.os.Handler;
import android.os.Message;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.SparseArray;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.alipay.sdk.app.PayTask;
import com.tencent.mm.opensdk.modelpay.PayReq;
import com.tencent.mm.opensdk.openapi.IWXAPI;
import com.tencent.mm.opensdk.openapi.WXAPIFactory;
import com.yiliao.chat.BuildConfig;
import com.yiliao.chat.R;
import com.yiliao.chat.adapter.ChargeRecyclerAdapter;
import com.yiliao.chat.base.AppManager;
import com.yiliao.chat.base.BaseActivity;
import com.yiliao.chat.base.BaseListResponse;
import com.yiliao.chat.base.BaseResponse;
import com.yiliao.chat.bean.BalanceBean;
import com.yiliao.chat.bean.ChargeListBean;
import com.yiliao.chat.bean.ChatUserInfo;
import com.yiliao.chat.bean.PayTypeBean;
import com.yiliao.chat.bean.UserCenterBean;
import com.yiliao.chat.constant.ChatApi;
import com.yiliao.chat.constant.Constant;
import com.yiliao.chat.helper.SharedPreferenceHelper;
import com.yiliao.chat.net.AjaxCallback;
import com.yiliao.chat.net.NetCode;
import com.yiliao.chat.pay.PayResult;
import com.yiliao.chat.util.CodeUtil;
import com.yiliao.chat.util.DialogUtil;
import com.yiliao.chat.util.LogUtil;
import com.yiliao.chat.util.ParamUtil;
import com.yiliao.chat.util.ToastUtil;
import com.yiliao.chat.util.WordUtil;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.StringCallback;

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
 * 功能描述   充值页面
 * 作者：
 * 创建时间：2018/6/15
 *
 * 修改人：
 * 修改描述：
 * 修改日期
 */
public class ChargeActivity extends BaseActivity {

    @BindView(R.id.content_rv)
    RecyclerView mContentRv;
    @BindView(R.id.gold_number_tv)
    TextView mGoldNumberTv;
    @BindView(R.id.wechat_check_iv)
    ImageView mWechatCheckIv;
    @BindView(R.id.alipay_check_iv)
    ImageView mAlipayCheckIv;
    @BindView(R.id.youyun_check_iv)
    ImageView mYouyunCheckIv;
    @BindView(R.id.qiangui_check_iv)
    ImageView mQianguiCheckIv;

    @BindView(R.id.wechat_rl)
    RelativeLayout mWechatRl;
    @BindView(R.id.wechat_name)
    TextView mWechatName;
    @BindView(R.id.wechat_describe)
    TextView mWechatDescribe;

    @BindView(R.id.alipayDivider)
    View mAlipayDivider;
    @BindView(R.id.alipay_rl)
    RelativeLayout mAlipayRl;
    @BindView(R.id.alipay_name)
    TextView mAlipayName;
    @BindView(R.id.alipay_describe)
    TextView mAlipayDescribe;

    @BindView(R.id.youyunDivider)
    View mYouyunDivider;
    @BindView(R.id.youyun_rl)
    RelativeLayout mYouyunRl;
    @BindView(R.id.youyun_name)
    TextView mYouyunName;
    @BindView(R.id.youyun_describe)
    TextView mYouyunDescribe;

    @BindView(R.id.qianguiDivider)
    View mQianguiDivider;
    @BindView(R.id.qiangui_rl)
    RelativeLayout mQianguiRl;
    @BindView(R.id.qiangui_name)
    TextView mQianguiName;
    @BindView(R.id.qiangui_describe)
    TextView mQianguiDescribe;

    private ChargeRecyclerAdapter mAdapter;

    //支付宝
    private static final int SDK_PAY_FLAG = 1;
    private static final int SDK_AUTH_FLAG = 2;
    //微信
    private IWXAPI mWxApi;
    //接收关闭activity广播
    private MyFinishBroadcastReceiver mMyBroadcastReceiver;

    @Override
    protected View getContentView() {
        return inflate(R.layout.activity_charge_layout);
    }

    @Override
    protected void onContentAdded() {
        setTitle(R.string.charge_gold);
        setRightText(R.string.service);

        String payType = SharedPreferenceHelper.getPayType(ChargeActivity.this);
        if (!TextUtils.isEmpty(payType)) {
            List<PayTypeBean> payTypeBeanList = JSON.parseArray(payType, PayTypeBean.class);
            for (PayTypeBean bean : payTypeBeanList) {
                switch (bean.t_type) {
                    case "0":
                        if (bean.t_is_enable.equals("0")) {
                            mAlipayDivider.setVisibility(View.VISIBLE);
                            mAlipayRl.setVisibility(View.VISIBLE);
                            mAlipayName.setText(bean.t_name);
                            mAlipayDescribe.setText(bean.t_describe);
                        } else {
                            mAlipayDivider.setVisibility(View.GONE);
                            mAlipayRl.setVisibility(View.GONE);
                        }
                        break;
                    case "1":
                        if (bean.t_is_enable.equals("0")) {
                            mWechatRl.setVisibility(View.VISIBLE);
                            mWechatName.setText(bean.t_name);
                            mWechatDescribe.setText(bean.t_describe);
                        } else {
                            mWechatRl.setVisibility(View.GONE);
                        }
                        break;
                    case "2":
                        if (bean.t_is_enable.equals("0")) {
                            mYouyunDivider.setVisibility(View.VISIBLE);
                            mYouyunRl.setVisibility(View.VISIBLE);
                            mYouyunName.setText(bean.t_name);
                            mYouyunDescribe.setText(bean.t_describe);
                        } else {
                            mYouyunDivider.setVisibility(View.GONE);
                            mYouyunRl.setVisibility(View.GONE);
                        }
                        break;
                    case "3":
                        if (bean.t_is_enable.equals("0")) {
                            mQianguiDivider.setVisibility(View.VISIBLE);
                            mQianguiRl.setVisibility(View.VISIBLE);
                            mQianguiName.setText(bean.t_name);
                            mQianguiDescribe.setText(bean.t_describe);
                        } else {
                            mQianguiDivider.setVisibility(View.GONE);
                            mQianguiRl.setVisibility(View.GONE);
                        }
                        break;
                }
            }
        }
        if (mWechatRl.getVisibility() == View.VISIBLE) {
            mWechatCheckIv.setSelected(true);
        } else if (mAlipayRl.getVisibility() == View.VISIBLE) {
            mAlipayCheckIv.setSelected(true);
        } else if (mYouyunRl.getVisibility() == View.VISIBLE) {
            mYouyunCheckIv.setSelected(true);
        } else if (mQianguiRl.getVisibility() == View.VISIBLE) {
            mQianguiCheckIv.setSelected(true);
        }

        mWxApi = WXAPIFactory.createWXAPI(this, BuildConfig.wechatAppId, true);
        mWxApi.registerApp(BuildConfig.wechatAppId);
        mMyBroadcastReceiver = new MyFinishBroadcastReceiver();
        IntentFilter filter = new IntentFilter(Constant.FINISH_CHARGE_PAGE);
        registerReceiver(mMyBroadcastReceiver, filter);

        GridLayoutManager gridLayoutManager = new GridLayoutManager(getApplicationContext(), 3);
        mContentRv.setLayoutManager(gridLayoutManager);
        mAdapter = new ChargeRecyclerAdapter(this);
        mContentRv.setAdapter(mAdapter);


        getWindow().getDecorView().postDelayed(new Runnable() {
            @Override
            public void run() {
                getChargeList();
            }
        }, 20);
        getMyGold();
    }

    /**
     * 获取我的金币余额
     */
    private void getMyGold() {
        Map<String, String> paramMap = new HashMap<>();
        paramMap.put("userId", getUserId());
        OkHttpUtils.post().url(ChatApi.GET_USER_BALANCE)
                .addParams("param", ParamUtil.getParam(paramMap))
                .build().execute(new AjaxCallback<BaseResponse<BalanceBean>>() {
            @Override
            public void onResponse(BaseResponse<BalanceBean> response, int id) {
                if (isFinishing()) {
                    return;
                }
                if (response != null && response.m_istatus == NetCode.SUCCESS) {
                    BalanceBean balanceBean = response.m_object;
                    if (balanceBean != null) {
                        mGoldNumberTv.setText(String.valueOf(balanceBean.amount));
                    }
                }
            }
        });
    }

    /**
     * 获取充值列表
     */
    private void getChargeList() {
        Map<String, String> paramMap = new HashMap<>();
        paramMap.put("userId", getUserId());
        paramMap.put("t_end_type", "0");//端类型:0.Android 1.iPhone
        OkHttpUtils.post().url(ChatApi.GET_RECHARGE_DISCOUNT)
                .addParams("param", ParamUtil.getParam(paramMap))
                .build().execute(new AjaxCallback<BaseListResponse<ChargeListBean>>() {
            @Override
            public void onResponse(BaseListResponse<ChargeListBean> response, int id) {
                if (isFinishing()) {
                    return;
                }
                if (response != null && response.m_istatus == NetCode.SUCCESS) {
                    List<ChargeListBean> beans = response.m_object;
                    if (beans != null && beans.size() > 1) {
                        if (beans.size() >= 2) {
                            beans.get(1).isSelected = true;
                        }
                        mAdapter.loadData(beans);
                    }
                }
            }
        });
    }

    @OnClick({R.id.alipay_rl, R.id.wechat_rl, R.id.youyun_rl, R.id.qiangui_rl, R.id.account_detail_tv, R.id.right_text, R.id.go_pay_tv})
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.wechat_rl: {//微信支付
                if (mWechatCheckIv.isSelected()) {
                    return;
                }
                mWechatCheckIv.setSelected(true);
                mAlipayCheckIv.setSelected(false);
                mYouyunCheckIv.setSelected(false);
                mQianguiCheckIv.setSelected(false);
                break;
            }
            case R.id.alipay_rl: {//支付宝
                if (mAlipayCheckIv.isSelected()) {
                    return;
                }
                mWechatCheckIv.setSelected(false);
                mAlipayCheckIv.setSelected(true);
                mYouyunCheckIv.setSelected(false);
                mQianguiCheckIv.setSelected(false);
                break;
            }
            case R.id.youyun_rl: {//优云宝
                if (mYouyunCheckIv.isSelected()) {
                    return;
                }
                mWechatCheckIv.setSelected(false);
                mAlipayCheckIv.setSelected(false);
                mYouyunCheckIv.setSelected(true);
                mQianguiCheckIv.setSelected(false);
                break;
            }
            case R.id.qiangui_rl: {//黔贵金服
                if (mQianguiCheckIv.isSelected()) {
                    return;
                }
                mWechatCheckIv.setSelected(false);
                mAlipayCheckIv.setSelected(false);
                mYouyunCheckIv.setSelected(false);
                mQianguiCheckIv.setSelected(true);
                break;
            }
            case R.id.account_detail_tv: {//账单详情
                Intent intent = new Intent(getApplicationContext(), AccountBalanceActivity.class);
                startActivity(intent);
                break;
            }
            case R.id.right_text: {
                CodeUtil.jumpToQQ(ChargeActivity.this);
                break;
            }
            case R.id.go_pay_tv: {//去支付
                if (!mWechatCheckIv.isSelected() && !mAlipayCheckIv.isSelected() && !mYouyunCheckIv.isSelected() && !mQianguiCheckIv.isSelected()) {
                    ToastUtil.showToast(mContext, R.string.please_choose_pay_way);
                    return;
                }
                final ChargeListBean mSelectVipBean = mAdapter.getSelectBean();
                if (mSelectVipBean == null) {
                    ToastUtil.showToast(mContext, R.string.please_choose_money);
                    return;
                }

                if (mAlipayCheckIv.isSelected()) {
                    if (mSelectVipBean.t_id<12){
                        ToastUtil.show("请选择其他支付方式");
                        return;
                    }
                    payForGold(mSelectVipBean.t_id, 0, 0);
                } else if (mWechatCheckIv.isSelected()) {
                    payForGold(mSelectVipBean.t_id, 1, 0);
                } else if (mYouyunCheckIv.isSelected()) {
                    payForGold(mSelectVipBean.t_id, 2, 0);
//                    SparseArray<String> mSparseArray = new SparseArray<>();
//                    mSparseArray.put(100, getResources().getString(R.string.wechat_code_pay));
//                    mSparseArray.put(101, getResources().getString(R.string.alipay_code_pay));
//                    DialogUtil.showStringArrayDialog(ChargeActivity.this, mSparseArray, new DialogUtil.StringArrayDialogCallback() {
//                        @Override
//                        public void onItemClick(String text, int tag) {
//                            if (tag == 101) {
//                                payForGold(mSelectVipBean.t_id, 2, 0);
//                            } else if (tag == 100) {
//                                payForGold(mSelectVipBean.t_id, 2, 1);
//                            }
//                        }
//                    });
                } else if (mQianguiCheckIv.isSelected()) {

                    payForGold(mSelectVipBean.t_id, 3, 0);
//                    SparseArray<String> mSparseArray = new SparseArray<>();
//                    mSparseArray.put(100, getResources().getString(R.string.wechat_code_pay));
//                    mSparseArray.put(101, getResources().getString(R.string.alipay_code_pay));
//                    DialogUtil.showStringArrayDialog(ChargeActivity.this, mSparseArray, new DialogUtil.StringArrayDialogCallback() {
//                        @Override
//                        public void onItemClick(String text, int tag) {
//                            if (tag == 101) {
//                                payForGold(mSelectVipBean.t_id, 3, 0);
//                            } else if (tag == 100) {
//                                payForGold(mSelectVipBean.t_id, 3, 1);
//                            }
//                        }
//                    });
                }
                break;
            }

        }
    }

    //-------------------------支付-------------------------

    /**
     * 金币充值
     *
     * @param payType      0.支付宝 1.微信 2.优云宝 3.黔贵金服
     * @param proTypeChild 黔贵金服,优云宝时传入0支付宝1微信
     */
    private void payForGold(int goldId, int payType, int proTypeChild) {
        Map<String, String> paramMap = new HashMap<>();
        paramMap.put("userId", getUserId());
        paramMap.put("setMealId", String.valueOf(goldId));
        paramMap.put("payType", String.valueOf(payType));
        if (payType == 2 || payType == 3) {
            paramMap.put("proTypeChild", String.valueOf(proTypeChild));
        }
        OkHttpUtils.post().url(ChatApi.GOLD_STORE_VALUE)
                .addParams("param", ParamUtil.getParam(paramMap))
                .build().execute(new StringCallback() {
            @Override
            public void onError(Call call, Exception e, int id) {
                ToastUtil.showToast(getApplicationContext(), R.string.system_error);
            }

            @Override
            public void onResponse(String response, int id) {
                LogUtil.i("Vip支付: " + response);
                if (!TextUtils.isEmpty(response)) {
                    JSONObject object = JSON.parseObject(response);
                    if (object.containsKey("m_istatus") && object.getIntValue("m_istatus") == 1) {
                        switch (object.getIntValue("payType")) {
                            case 0://支付宝
                                String orderInfo = object.getString("m_object");
                                if (!TextUtils.isEmpty(orderInfo)) {
                                    payWithAlipay(orderInfo);
                                } else {
                                    ToastUtil.showToast(getApplicationContext(), R.string.pay_vip_fail);
                                }
                                break;
                            case 1://微信
                                JSONObject payObject = object.getJSONObject("m_object");
                                payWithWeChat(payObject);
                                break;
                            case 2:
                                Intent intent = new Intent(getApplicationContext(), CommonWebViewActivity.class);
                                intent.putExtra(Constant.TITLE, WordUtil.getString(R.string.code_pay_title));
                                intent.putExtra(Constant.URL, object.getString("m_object"));
                                startActivity(intent);
                            case 3:
//                                Intent intenturl= new Intent();
//                                intenturl.setAction("android.intent.action.VIEW");
//                                Uri content_url = Uri.parse(object.getString("m_object"));
//                                intenturl.setData(content_url);
//                                startActivity(intenturl);
                                Intent intenturl= new Intent(ChargeActivity.this,WebViewActivity.class);
                                intenturl.putExtra("url",object.getString("m_object"));
                                startActivity(intenturl);
                                break;
                        }
                    }
                }
            }
        });
    }

    /**
     * 支付宝支付
     *
     */
    private void payWithAlipay(final String orderInfo) {//订单信息
        Runnable payRunnable = new Runnable() {

            @Override
            public void run() {
                PayTask alipay = new PayTask(ChargeActivity.this);
                Map<String, String> result = alipay.payV2(orderInfo, true);

                Message msg = new Message();
                msg.what = SDK_PAY_FLAG;
                msg.obj = result;
                mHandler.sendMessage(msg);
            }
        };
        // 必须异步调用
        Thread payThread = new Thread(payRunnable);
        payThread.start();
    }

    @SuppressLint("HandlerLeak")
    private Handler mHandler = new Handler() {
        @SuppressWarnings("unused")
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case SDK_PAY_FLAG: {
                    @SuppressWarnings("unchecked")
                    PayResult payResult = new PayResult((Map<String, String>) msg.obj);
                    //对于支付结果，请商户依赖服务端的异步通知结果。同步通知结果，仅作为支付结束的通知。
                    String resultInfo = payResult.getResult();// 同步返回需要验证的信息
                    String resultStatus = payResult.getResultStatus();
                    // 判断resultStatus 为9000则代表支付成功
                    if (TextUtils.equals(resultStatus, "9000")) {
                        // 该笔订单是否真实支付成功，需要依赖服务端的异步通知。
                        ToastUtil.showToast(getApplicationContext(), R.string.pay_vip_success);
                        finish();
                    } else {
                        // 该笔订单真实的支付结果，需要依赖服务端的异步通知。
                        ToastUtil.showToast(getApplicationContext(), R.string.pay_vip_fail);
                    }
                    break;
                }
                case SDK_AUTH_FLAG: {
                    break;
                }
                default:
                    break;
            }
        }
    };

    /**
     * 微信支付
     */
    private void payWithWeChat(JSONObject payObject) {
        if (mWxApi != null && mWxApi.isWXAppInstalled()) {
            try {
                PayReq request = new PayReq();
                request.appId = payObject.getString("appid");
                request.partnerId = payObject.getString("partnerid");
                request.prepayId = payObject.getString("prepayid");
                request.packageValue = "Sign=WXPay";
                request.nonceStr = payObject.getString("noncestr");
                request.timeStamp = payObject.getString("timestamp");
                request.sign = payObject.getString("sign");
                boolean res = mWxApi.sendReq(request);
                if (res) {//设置是充值Vip
                    AppManager.getInstance().setIsWeChatForVip(false);
                }
                LogUtil.i("res : " + res);
            } catch (Exception e) {
                e.printStackTrace();
                ToastUtil.showToast(getApplicationContext(), R.string.pay_vip_fail);
            }
        } else {
            ToastUtil.showToast(getApplicationContext(), R.string.not_install_we_chat);
        }
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mMyBroadcastReceiver != null) {
            unregisterReceiver(mMyBroadcastReceiver);
        }
    }

    class MyFinishBroadcastReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            ChatUserInfo userInfo = AppManager.getInstance().getUserInfo();
            if (userInfo != null) {
                Map<String, String> paramMap = new HashMap<>();
                paramMap.put("userId", String.valueOf(userInfo.t_id));
                OkHttpUtils.post().url(ChatApi.INDEX)
                        .addParams("param", ParamUtil.getParam(paramMap))
                        .build().execute(new AjaxCallback<BaseResponse<UserCenterBean>>() {
                    @Override
                    public void onResponse(BaseResponse<UserCenterBean> response, int id) {
                        if (response != null && response.m_istatus == NetCode.SUCCESS) {
                            UserCenterBean bean = response.m_object;
                            if (bean != null) {
                                SharedPreferenceHelper.saveUserExtreme(getApplicationContext(), bean.t_is_extreme);
                            }
                        }
                        finish();
                    }
                });
            } else {
                finish();
            }
        }
    }

}
