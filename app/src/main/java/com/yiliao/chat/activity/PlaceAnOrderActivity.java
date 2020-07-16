package com.yiliao.chat.activity;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.os.CountDownTimer;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.TimePicker;

import com.yiliao.chat.R;
import com.yiliao.chat.adapter.LabelRecyclerAdapter;
import com.yiliao.chat.base.BaseActivity;
import com.yiliao.chat.base.BaseListResponse;
import com.yiliao.chat.base.BaseResponse;
import com.yiliao.chat.bean.BalanceBean;
import com.yiliao.chat.bean.CreateOrderBean;
import com.yiliao.chat.bean.LabelBean;
import com.yiliao.chat.bean.RecivieBean;
import com.yiliao.chat.bean.ShareInformitionBean;
import com.yiliao.chat.constant.ChatApi;
import com.yiliao.chat.constant.Constant;
import com.yiliao.chat.dialog.DialogUitl;
import com.yiliao.chat.helper.ChargeHelper;
import com.yiliao.chat.net.AjaxCallback;
import com.yiliao.chat.net.NetCode;
import com.yiliao.chat.util.ParamUtil;
import com.yiliao.chat.util.ToastUtil;
import com.zhy.http.okhttp.OkHttpUtils;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.DateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.Random;

import butterknife.BindView;
import butterknife.OnClick;
import okhttp3.Call;

/**
 * 填写订单
 */
public class PlaceAnOrderActivity extends BaseActivity {
    @BindView(R.id.go_home_time)
    TextView go_home_time;
    @BindView(R.id.go_home_time_hour)
    TextView go_home_time_hour;
    @BindView(R.id.sure_tv)
    TextView sure_tv;
    @BindView(R.id.to_home_name)
    TextView to_home_name;
    @BindView(R.id.service_price)
    TextView service_price;
    @BindView(R.id.service_time)
    EditText service_time;
    @BindView(R.id.phone_number)
    EditText phone_number;
    @BindView(R.id.go_home_address)
    EditText go_home_address;
    @BindView(R.id.agree_iv)
    ImageView mAgreeIv;
    @BindView(R.id.service_tag_ll)
    RecyclerView content_rv;
    String title, glod_price, t_id, total, message; //主播信息
    private CountDownTimer mCountDownTimer;
    DateFormat format = DateFormat.getDateTimeInstance();
    Calendar calendar = Calendar.getInstance(Locale.CHINA);
    //我的金币数量
    private int mMyGoldNumber;
    int code = 0;
    String traffics;
    int mealId;
    String anchorId, orderId, agree;

    @Override
    protected View getContentView() {
        return inflate(R.layout.place_order_layout);
    }

    public void initview() {

        mAgreeIv.setSelected(true);
        title = getIntent().getStringExtra(Constant.TITLE);
//            glod_price=getIntent().getStringExtra("glod_price");
        t_id = getIntent().getStringExtra("t_id");
        to_home_name.setText(title);


    }

    @Override
    protected void onContentAdded() {
        setTitle("填写订单信息");
        initview();
        getMyGold();
        QueryAnchorServer();
//        getQueryOrder();
    }

    private void getQueryOrder() {
        Map<String, String> paramMap = new HashMap<>();
        paramMap.put("userId", getUserId());
        OkHttpUtils.post().url(ChatApi.QUERY_SERVICE_ORDER)
                .addParams("param", ParamUtil.getParam(paramMap))
                .build().execute(new AjaxCallback<BaseResponse<ShareInformitionBean>>() {
            @Override
            public void onResponse(BaseResponse<ShareInformitionBean> response, int id) {
                if (response != null && response.m_istatus == NetCode.SUCCESS) {
                    ToastUtil.show(response.m_strMessage);
                    if (response.m_strMessage.contains("成功")) {
//                        startCountDown();
                    }
//                    mShareInformitionBean  =response.m_object;
                } else {
                    ToastUtil.show(response.m_strMessage);
                }

            }

            @Override
            public void onError(Call call, Exception e, int id) {
                super.onError(call, e, id);
                ToastUtil.showToast(mContext, R.string.system_error);
            }

        });

    }

    private void userPayNum(String total) {
        Map<String, String> paramMap = new HashMap<>();
        paramMap.put("userId", getUserId());
        paramMap.put("anchorId", t_id);
        paramMap.put("orderId", orderId);
        paramMap.put("money", total);
        OkHttpUtils.post().url(ChatApi.USER_PAY_NUM)
                .addParams("param", ParamUtil.getParam(paramMap))
                .build().execute(new AjaxCallback<BaseResponse>() {
            @Override
            public void onResponse(BaseResponse response, int id) {
                if (response != null && response.m_istatus == NetCode.SUCCESS) {
                    ToastUtil.show(response.m_strMessage);
                    if (response.m_istatus == 1) {
                        dialog.dismiss();
                        finish();
                    }
                } else {
                    ToastUtil.show(response.m_strMessage);
                }
            }

            @Override
            public void onError(Call call, Exception e, int id) {
                super.onError(call, e, id);
                ToastUtil.showToast(mContext, R.string.system_error);
            }

        });
    }

    /**
     * 生成订单
     */
    private void geShareInformaiton() {
        Map<String, String> paramMap = new HashMap<>();
        paramMap.put("userId", getUserId());
        paramMap.put("anchorId", t_id);
        paramMap.put("price", glod_price);
        paramMap.put("times", service_time.getText().toString());
        paramMap.put("phone", phone_number.getText().toString());
        paramMap.put("address", go_home_address.getText().toString());
        paramMap.put("mealId", String.valueOf(mealId));
        paramMap.put("serverTime", go_home_time.getText().toString() + " " + go_home_time_hour.getText().toString());
        OkHttpUtils.post().url(ChatApi.SERVICE_PRICE)
                .addParams("param", ParamUtil.getParam(paramMap))
                .build().execute(new AjaxCallback<BaseResponse<CreateOrderBean>>() {
            @Override
            public void onResponse(BaseResponse<CreateOrderBean> response, int id) {
                if (response != null && response.m_istatus == NetCode.SUCCESS) {
                    int time = response.m_object.waitTime;
                    ToastUtil.show(response.m_strMessage);
                    startCountDown(code, time);
                } else {
                    if (response.m_istatus == -4) {
                        ChargeHelper.showSetCoverDialog(PlaceAnOrderActivity.this);
                    }
                    ToastUtil.show(response.m_strMessage);
                }

            }

            @Override
            public void onError(Call call, Exception e, int id) {
                super.onError(call, e, id);
                ToastUtil.showToast(mContext, R.string.system_error);
            }

        });
    }

    /**
     * 将毫秒转化为 分钟：秒 的格式
     *
     * @param millisecond 毫秒
     * @return
     */
    public String formatTime(long millisecond) {
        int minute;//分钟
        int second;//秒数
        minute = (int) ((millisecond / 1000) / 60);
        second = (int) ((millisecond / 1000) % 60);
        if (minute < 10) {
            if (second < 10) {
                return "0" + minute + ":" + "0" + second;
            } else {
                return "0" + minute + ":" + second;
            }
        } else {
            if (second < 10) {
                return minute + ":" + "0" + second;
            } else {
                return minute + ":" + second;
            }
        }
    }

    /**
     * 开始倒计时
     */
    private void startCountDown(final int code, long time) {


        mCountDownTimer = new CountDownTimer(1000 * time, 1000) {
            @Override
            public void onTick(long l) {
                if (code == 2) {
                    sure_tv.setClickable(true);
                    sure_tv.setBackgroundResource(R.drawable.yuan_gree_save);
                } else {
                    sure_tv.setClickable(false);
                    sure_tv.setBackgroundResource(R.drawable.yuan_edit_garee);
                }
//                sure_tv.setTextColor(getResources().getColor(R.color.black_bbbbbb));
                String text = getResources().getString(R.string.go_pay) + "(" + formatTime(l) + ")";
                sure_tv.setText(text);
            }

            @Override
            public void onFinish() {
                sure_tv.setClickable(true);
                sure_tv.setTextColor(getResources().getColor(R.color.white));
                sure_tv.setText(R.string.sure_order);
                if (mCountDownTimer != null) {
                    mCountDownTimer.cancel();
                    mCountDownTimer = null;
                }
            }
        }.start();
    }

    @OnClick({R.id.agree_iv, R.id.go_home_time_hour, R.id.go_home_time, R.id.sure_tv, R.id.agree_tv})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.agree_iv: {
                if (mAgreeIv.isSelected()) {
                    mAgreeIv.setSelected(false);
                } else {
                    mAgreeIv.setSelected(true);
                }
                break;
            }
            case R.id.go_home_time:
                DialogUitl.showDatePickerDialog(mContext, new DialogUitl.DataPickerCallback() {
                    @Override
                    public void onConfirmClick(final String date) {
                        go_home_time.setText(date);
                    }
                });
//                    showDatePickerDialog(this,2,go_home_time,calendar);
                break;
            case R.id.go_home_time_hour:
                DialogUitl.showTimePickerDialog(mContext, new DialogUitl.DataPickerCallback() {
                    @Override
                    public void onConfirmClick(final String date) {
                        go_home_time_hour.setText(date);
                    }
                });
//                    showTimePickerDialog(this,2,go_home_time_hour,calendar);
                break;
            case R.id.sure_tv:
                if (code == 2) {
                    showToPayDialog(this, total);
                } else {
                    //调生成订单的接口
                    if (!TextUtils.isEmpty(service_time.getText().toString()) && !TextUtils.isEmpty(phone_number.getText().toString())
                            && !TextUtils.isEmpty(go_home_address.getText().toString())) {
                        geShareInformaiton();
                    } else {
                        ToastUtil.show("请填写完整的订单信息！");
                    }

                }
                break;
            case R.id.agree_tv: {//协议
                Intent intent = new Intent(getApplicationContext(), CommonWebViewActivity.class);
                intent.putExtra(Constant.TITLE, getResources().getString(R.string.chat_company_agree));
                intent.putExtra(Constant.URL, "file:///android_asset/company_agree.html");
                startActivity(intent);
                break;
            }
            default:
                break;
        }
    }

    Dialog dialog;

    /**
     * 确认支付
     */
    public void showToPayDialog(Context context, final String total) {
        dialog = new Dialog(context, R.style.dialog);
        View view = LayoutInflater.from(context).inflate(R.layout.dialog_to_pay, null);
        dialog.setContentView(view);
        dialog.setCancelable(true);
        dialog.setCanceledOnTouchOutside(true);
        TextView textView = view.findViewById(R.id.to_pay_num);
        String htmlString = "是否支付" + "<font color=\"#7275FF\">" + total + "</font>" + "金币";
        textView.setText(Html.fromHtml(htmlString));
        dialog.findViewById(R.id.btn_cancel).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        dialog.findViewById(R.id.btn_sure).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                userPayNum(total);
            }
        });
        dialog.show();
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
                if (response != null && response.m_istatus == NetCode.SUCCESS) {
                    BalanceBean balanceBean = response.m_object;
                    if (balanceBean != null) {
                        mMyGoldNumber = balanceBean.amount;
                    }
                }
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mCountDownTimer != null) {
            mCountDownTimer.cancel();
            mCountDownTimer = null;
        }
    }

    protected void onGetOrRe(String content) {
        super.onGetOrRe(content);
        try {
            JSONObject jc = new JSONObject(content);
            RecivieBean recivieBean = new RecivieBean();


            recivieBean.agree = jc.getString("agree");
            agree = recivieBean.agree;
            recivieBean.message = jc.getString("message");
            recivieBean.address = jc.getString("address");
            recivieBean.phone = jc.getString("phone");
            recivieBean.price = jc.getString("price");
            recivieBean.status = jc.getString("status");
            recivieBean.servertime = jc.getString("servertime");
            recivieBean.times = jc.getString("times");
            recivieBean.orderId = jc.getString("orderId");
            recivieBean.anchorId = jc.getString("anchorId");
            recivieBean.laveTime = jc.getString("laveTime");
            t_id = recivieBean.anchorId;
            recivieBean.userHandImg = jc.getString("userHandImg");
            recivieBean.username = jc.getString("username");
            recivieBean.total = jc.getString("total");

            if (TextUtils.equals(agree, "0")) {
                code = 2;
                if (mCountDownTimer != null) {
                    mCountDownTimer.cancel();
                    mCountDownTimer = null;
                }
                total = recivieBean.total;
                to_home_name.setText(recivieBean.username);
//            String status="<font color=\"#F8BE5E\">" + "未开始（待付款）" + "</font>";
//            order_state.setText(Html.fromHtml(status));
                String htmlString = "<font color=\"#F8BE5E\">" + recivieBean.price + "</font>" + "金币/小时";
                service_price.setText(Html.fromHtml(htmlString));
                service_time.setText(recivieBean.times);
                phone_number.setText(recivieBean.phone);
                go_home_time.setText(recivieBean.servertime);
                go_home_address.setText(recivieBean.address);
                anchorId = recivieBean.anchorId;
                orderId = recivieBean.orderId;
                go_home_time_hour.setVisibility(View.GONE);
                int time = Integer.parseInt(recivieBean.laveTime);
                startCountDown(code, time);
            } else if (TextUtils.equals(agree, "1")) {
                code = 1;
                if (mCountDownTimer != null) {
                    mCountDownTimer.cancel();
                    mCountDownTimer = null;
                }
                sure_tv.setClickable(true);
                sure_tv.setTextColor(getResources().getColor(R.color.white));
                sure_tv.setText(R.string.sure_order);
                sure_tv.setEnabled(true);
                sure_tv.setBackgroundResource(R.drawable.yuan_gree_save);
                ToastUtil.show(recivieBean.message);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void QueryAnchorServer() {
        Map<String, String> paramMap = new HashMap<>();
        paramMap.put("userId", getUserId());
        paramMap.put("anchorId", t_id);
        OkHttpUtils.post().url(ChatApi.QUERY_CHACK_SERVICE)
                .addParams("param", ParamUtil.getParam(paramMap))
                .build().execute(new AjaxCallback<BaseListResponse<LabelBean>>() {
            @Override
            public void onResponse(BaseListResponse<LabelBean> response, int id) {
                if (response != null && response.m_istatus == NetCode.SUCCESS) {
                    List<LabelBean> labelBeans = response.m_object;
                    int code = response.m_istatus;
                    if (code == 0) {
                        ToastUtil.showToast(getApplicationContext(), R.string.check_error);
                    } else {
                        setServiceLabelView(labelBeans);
                    }
                }
            }

            @Override
            public void onError(Call call, Exception e, int id) {
                super.onError(call, e, id);
                ToastUtil.showToast(getApplicationContext(), "服务异常");
            }
        });
    }

    /**
     * 设置标签View
     */
    private void setServiceLabelView(final List<LabelBean> labelBeans) {
        GridLayoutManager gridLayoutManager = new GridLayoutManager(getBaseContext(), 4);
        content_rv.setLayoutManager(gridLayoutManager);
        final LabelRecyclerAdapter adapter = new LabelRecyclerAdapter(getBaseContext(), 2);
        content_rv.setAdapter(adapter);
        labelBeans.get(0).selected = true;
        adapter.loadData(labelBeans);
        if (labelBeans != null) {
            glod_price = labelBeans.get(0).price + "";
            String htmlString = "<font color=\"#7275FF\">" + glod_price + "</font>" + "金币/小时";
            service_price.setText(Html.fromHtml(htmlString));
            mealId = labelBeans.get(0).t_id;
        }
        adapter.setOnItemClickListener(new LabelRecyclerAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(LabelBean sortBean) {
                mealId = sortBean.t_id;
                glod_price = sortBean.price + "";
                String htmlString = "<font color=\"#7275FF\">" + glod_price + "</font>" + "金币/小时";
                service_price.setText(Html.fromHtml(htmlString));
                for (int i = 0; i < labelBeans.size(); i++) {
                    if (labelBeans.get(i).t_id == sortBean.t_id) {
                        sortBean.selected = true;
                    } else {
                        labelBeans.get(i).selected = false;
                    }
                }

                adapter.notifyDataSetChanged();
            }
        });


    }
}
