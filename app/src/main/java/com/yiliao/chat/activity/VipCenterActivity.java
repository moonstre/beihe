package com.yiliao.chat.activity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.SparseArray;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.alipay.sdk.app.PayTask;
import com.tencent.mm.opensdk.modelpay.PayReq;
import com.tencent.mm.opensdk.openapi.IWXAPI;
import com.tencent.mm.opensdk.openapi.WXAPIFactory;
import com.yiliao.chat.BuildConfig;
import com.yiliao.chat.R;
import com.yiliao.chat.adapter.VipMoneyRecyclerAdapter;
import com.yiliao.chat.base.AppManager;
import com.yiliao.chat.base.BaseActivity;
import com.yiliao.chat.base.BaseListResponse;
import com.yiliao.chat.bean.PayTypeBean;
import com.yiliao.chat.bean.VipBean;
import com.yiliao.chat.constant.ChatApi;
import com.yiliao.chat.constant.Constant;
import com.yiliao.chat.helper.SharedPreferenceHelper;
import com.yiliao.chat.net.AjaxCallback;
import com.yiliao.chat.net.NetCode;
import com.yiliao.chat.pay.PayResult;
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
 * 功能描述   VIP会员页面
 * 作者：
 * 创建时间：2018/6/14
 *
 * 修改人：
 * 修改描述：
 * 修改日期
 */
public class VipCenterActivity extends BaseActivity {

    @BindView(R.id.money_rv)
    RecyclerView mMoneyRv;
    @BindView(R.id.alipay_check_iv)
    ImageView mAlipayCheckIv;
    @BindView(R.id.wechat_check_iv)
    ImageView mWechatCheckIv;
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

    //支付
    private IWXAPI mWxApi;
    //支付宝
    private static final int SDK_PAY_FLAG = 1;

    private VipMoneyRecyclerAdapter mVipMoneyRecyclerAdapter;

    @Override
    protected View getContentView() {
        return inflate(R.layout.activity_vip_center_layout);
    }

    @Override
    protected void onContentAdded() {
        setTitle(R.string.vip_title);
        mWxApi = WXAPIFactory.createWXAPI(mContext, BuildConfig.wechatAppId, true);
        mWxApi.registerApp(BuildConfig.wechatAppId);
        initView();
        getVipList();
    }

    private void initView() {
        String payType = SharedPreferenceHelper.getPayType(VipCenterActivity.this);
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

        LinearLayoutManager gridLayoutManager = new LinearLayoutManager(mContext);
        mMoneyRv.setLayoutManager(gridLayoutManager);
        mVipMoneyRecyclerAdapter = new VipMoneyRecyclerAdapter(this);
        mMoneyRv.setAdapter(mVipMoneyRecyclerAdapter);
    }

    /**
     * 获取VIP列表
     */
    private void getVipList() {
        Map<String, String> paramMap = new HashMap<>();
        paramMap.put("userId", getUserId());
        OkHttpUtils.post().url(ChatApi.GET_VIP_SET_MEAL_LIST)
                .addParams("param", ParamUtil.getParam(paramMap))
                .build().execute(new AjaxCallback<BaseListResponse<VipBean>>() {
            @Override
            public void onResponse(BaseListResponse<VipBean> response, int id) {
                if (response != null && response.m_istatus == NetCode.SUCCESS) {
                    List<VipBean> vipBeans = response.m_object;
                    if (vipBeans != null && vipBeans.size() > 0) {
                        if (vipBeans.size() >= 1) {
                            vipBeans.get(0).isSelected = true;
                        }
                        mVipMoneyRecyclerAdapter.loadData(vipBeans);
                    }
                }
            }
        });
    }

    @OnClick({R.id.alipay_rl, R.id.wechat_rl, R.id.youyun_rl, R.id.qiangui_rl, R.id.go_pay_tv})
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
            case R.id.go_pay_tv: {//去支付
                final VipBean mSelectVipBean = mVipMoneyRecyclerAdapter.getSelectBean();
                if (mSelectVipBean == null) {
                    ToastUtil.showToast(mContext, R.string.please_choose_vip);
                    return;
                }
                if (!mAlipayCheckIv.isSelected() && !mWechatCheckIv.isSelected() && !mYouyunCheckIv.isSelected() && !mQianguiCheckIv.isSelected()) {
                    ToastUtil.showToast(mContext, R.string.please_choose_pay_way);
                    return;
                }

                if (mAlipayCheckIv.isSelected()) {
                    payForVip(mSelectVipBean.t_id, 0, 0);
                } else if (mWechatCheckIv.isSelected()) {
                    payForVip(mSelectVipBean.t_id, 1, 0);
                } else if (mYouyunCheckIv.isSelected()) {
                    payForVip(mSelectVipBean.t_id, 2, 0);
//                    SparseArray<String> mSparseArray = new SparseArray<>();
//                    mSparseArray.put(100, getResources().getString(R.string.wechat_code_pay));
//                    mSparseArray.put(101, getResources().getString(R.string.alipay_code_pay));
//                    DialogUtil.showStringArrayDialog(VipCenterActivity.this, mSparseArray, new DialogUtil.StringArrayDialogCallback() {
//                        @Override
//                        public void onItemClick(String text, int tag) {
//                            if (tag == 101) {
//                                payForVip(mSelectVipBean.t_id, 2, 0);
//                            } else if (tag == 100) {
//                                payForVip(mSelectVipBean.t_id, 2, 1);
//                            }
//                        }
//                    });
                } else if (mQianguiCheckIv.isSelected()) {
                    payForVip(mSelectVipBean.t_id, 3, 0);
//                    SparseArray<String> mSparseArray = new SparseArray<>();
//                    mSparseArray.put(100, getResources().getString(R.string.wechat_code_pay));
//                    mSparseArray.put(101, getResources().getString(R.string.alipay_code_pay));
//                    DialogUtil.showStringArrayDialog(VipCenterActivity.this, mSparseArray, new DialogUtil.StringArrayDialogCallback() {
//                        @Override
//                        public void onItemClick(String text, int tag) {
//                            if (tag == 101) {
//                                payForVip(mSelectVipBean.t_id, 3, 0);
//                            } else if (tag == 100) {
//                                payForVip(mSelectVipBean.t_id, 3, 1);
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
     * VIP支付
     *
     * @param payType      0.支付宝 1.微信 2.优云宝 3.黔贵金服
     * @param proTypeChild 黔贵金服,优云宝时传入0支付宝1微信
     */
    private void payForVip(int vipId, int payType, int proTypeChild) {
        Map<String, String> paramMap = new HashMap<>();
        paramMap.put("userId", getUserId());
        paramMap.put("setMealId", String.valueOf(vipId));
        paramMap.put("payType", String.valueOf(payType));
        if (payType == 2 || payType == 3) {
            paramMap.put("proTypeChild", String.valueOf(proTypeChild));
        }
        OkHttpUtils.post().url(ChatApi.VIP_STORE_VALUE)
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
                            case 3:
                                Intent intent = new Intent(getApplicationContext(), CommonWebViewActivity.class);
                                intent.putExtra(Constant.TITLE, WordUtil.getString(R.string.code_pay_title));
                                intent.putExtra(Constant.URL, object.getString("m_object"));
                                startActivity(intent);
                                break;
                        }
                    }
                }
            }
        });
    }

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
                    AppManager.getInstance().setIsWeChatForVip(true);
                    finish();
                }
                LogUtil.i("res : " + res);
            } catch (Exception e) {
                e.printStackTrace();
                ToastUtil.showToast(mContext, R.string.pay_vip_fail);
            }
        } else {
            ToastUtil.showToast(mContext, R.string.not_install_we_chat);
        }
    }

    /**
     * 支付宝支付
     */
    private void payWithAlipay(final String orderInfo) {//订单信息
        Runnable payRunnable = new Runnable() {

            @Override
            public void run() {
                PayTask alipay = new PayTask(VipCenterActivity.this);
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
                default:
                    break;
            }
        }
    };

}
