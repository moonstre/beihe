package com.yiliao.chat.activity;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.graphics.Point;
import android.os.CountDownTimer;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.lzy.okgo.OkGo;
import com.yiliao.chat.R;
import com.yiliao.chat.adapter.AddTimeLableAdapter;
import com.yiliao.chat.adapter.LabelRecyclerAdapter;
import com.yiliao.chat.base.BaseActivity;
import com.yiliao.chat.base.BaseBean;
import com.yiliao.chat.base.BaseListResponse;
import com.yiliao.chat.base.BaseResponse;
import com.yiliao.chat.bean.AddTimeBean;
import com.yiliao.chat.bean.BalanceBean;
import com.yiliao.chat.bean.LabelBean;
import com.yiliao.chat.bean.OrderSortBean;
import com.yiliao.chat.bean.QuiteTraffic;
import com.yiliao.chat.bean.RecivieBean;
import com.yiliao.chat.bean.StartServiceBean;
import com.yiliao.chat.constant.ChatApi;
import com.yiliao.chat.constant.Constant;
import com.yiliao.chat.net.AjaxCallback;
import com.yiliao.chat.net.NetCode;
import com.yiliao.chat.socket.ConnectManager;
import com.yiliao.chat.socket.domain.Mid;
import com.yiliao.chat.socket.domain.SocketResponse;
import com.yiliao.chat.util.ParamUtil;
import com.yiliao.chat.util.ToastUtil;
import com.yiliao.chat.view.SpacesItemDecoration;
import com.zhy.http.okhttp.OkHttpUtils;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

import butterknife.BindView;
import butterknife.OnClick;
import okhttp3.Call;

public class OrderDetailsActivity extends BaseActivity {
    @BindView(R.id.to_home_name)
    TextView to_home_name;
    @BindView(R.id.order_state)
    TextView order_state;
    @BindView(R.id.service_price)
    TextView service_price;
    @BindView(R.id.service_time)
    TextView service_time;
    @BindView(R.id.phone_number)
    TextView phone_number;
    @BindView(R.id.go_home_time)
    TextView go_home_time;
    @BindView(R.id.go_home_address)
    TextView go_home_address;
    @BindView(R.id.sure_tv)
    TextView sure_tv;
    @BindView(R.id.sure_tv_two)
    TextView sure_tv_two;
    @BindView(R.id.add_time)
    TextView add_time;
    @BindView(R.id.order_ll)
    LinearLayout order_ll;
    @BindView(R.id.quite_order)
    TextView quite_order;
    @BindView(R.id.order_value)
    TextView order_value;
    @BindView(R.id.add_time_ll)
    LinearLayout add_time_ll;
    //角色判断
    int role;
    //接单或拒绝
    String agree;
    //初始化数据
    OrderSortBean list;
    List<AddTimeBean> beans_list = new ArrayList<>();

    //续钟的时间和价格,id
    int add_times, add_prices, add_ids;
    //服务时间倒计时
    private CountDownTimer mCountDownTimer;
    //我的金币数量
    private int mMyGoldNumber;
    AddTimeLableAdapter adapter;
    private boolean selectMode;
    String status_order;
    int state, total;
    int orderId, anchorId;
    int flag = 0;
    String num;
    String type = "1";
    TextView right_text;
    List<StartServiceBean.continueClockList> lists = new ArrayList<>();

    String isSelf;//是否是自己的订单

    //接收socket 广播
//    private MyBroadcastReceiver mMyBroadcastReceiver;
    @Override
    protected View getContentView() {
        return inflate(R.layout.order_details_layout);
    }

    @Override
    protected void onContentAdded() {
        setTitle("订单详情");

        initview();
        getLabelList();
        getMyGold();
    }

    @SuppressLint("ResourceAsColor")
    public void initview() {
        list = (OrderSortBean) getIntent().getSerializableExtra("sortBean");
        isSelf = getIntent().getStringExtra("isSelf");
        role = getIntent().getExtras().getInt("role");
//        querySingleOrder();
        if (role == 0) {  // 0 用户
            order_ll.setVisibility(View.VISIBLE);
            if (list.status == 3) {  //未开始
                state = 3;
                sure_tv_two.setEnabled(false);
                sure_tv_two.setVisibility(View.VISIBLE);
                status_order = "<font color=\"#7275FF\">" + "未开始" + "</font>";
                order_state.setText(Html.fromHtml(status_order));
                quite_order.setVisibility(View.VISIBLE);
                querySingleOrder();
            }
            if (list.status == 4) {  //进行中
                state = 4;
                status_order = "<font color=\"#7275FF\">" + "进行中" + "</font>";
                order_state.setText(Html.fromHtml(status_order));
                add_time.setVisibility(View.VISIBLE);
                sure_tv.setVisibility(View.VISIBLE);
                querySingleOrder();
//                startCountDown(28000);
            }
            if (list.status == 5 && list.commentId != 0) {  //已完成
//                state=5;
                order_state.setText("已结束");
                querySingleOrder();
//                sure_tv.setVisibility(View.VISIBLE);
//                sure_tv.setText("立即评价");
            }
            if (list.status == 5 && list.commentId == 0) {
                state = 5;
                order_state.setText("已结束");
                sure_tv.setVisibility(View.VISIBLE);
                sure_tv.setText("立即评价");
            }
            if (list.status == 1) {

                state = 6;
                sure_tv.setText("立即付款");
                sure_tv.setVisibility(View.VISIBLE);
                sure_tv_two.setEnabled(false);
                sure_tv_two.setVisibility(View.VISIBLE);
                status_order = "<font color=\"#7275FF\">" + "未开始（待付款）" + "</font>";
                order_state.setText(Html.fromHtml(status_order));
                startCountDown(list.endPayTime);
//                querySingleOrder();
            }
            if (list.status == 7) {
                order_state.setText("已取消");
            }
            if (!TextUtils.isEmpty(list.username)) {
                to_home_name.setText(list.username);
            }
            if (TextUtils.equals(isSelf, "1")) {
                sure_tv.setVisibility(View.GONE);
                sure_tv_two.setVisibility(View.GONE);
                add_time.setVisibility(View.GONE);
                quite_order.setVisibility(View.GONE);
            }

        } else {     // 1 主播
            order_ll.setVisibility(View.GONE);
            if (list.status == 1) { //未开始,待付款
                state = 22;
                status_order = "<font color=\"#7275FF\">" + "未开始（待付款）" + "</font>";
                order_state.setText(Html.fromHtml(status_order));
                sure_tv_two.setText("开始服务");
                sure_tv_two.setVisibility(View.VISIBLE);
            }
            if (list.status == 3) {  //未开始
                state = 33;
                status_order = "<font color=\"#7275FF\">" + "未开始（已付款）" + "</font>";
                order_state.setText(Html.fromHtml(status_order));
                sure_tv.setText("开始服务");
                sure_tv.setVisibility(View.VISIBLE);
                querySingleOrder();
            }
            if (list.status == 4) { //进行中
                querySingleOrder();
                state = 44;
                status_order = "<font color=\"#7275FF\">" + "进行中" + "</font>";
                order_state.setText(Html.fromHtml(status_order));
                sure_tv_two.setVisibility(View.VISIBLE);
                startCountDown(list.laveTime);
            }
            if (list.status == 5) { //已完成
                order_state.setText("已结束");
                querySingleOrder();
            }
            if (list.status == 7) {
                order_state.setText("已取消");
            }
        }
        String price = "<font color=\"#7275FF\">" + list.price + "</font>" + "金币/小时";
        service_price.setText(Html.fromHtml(price));
        total = list.total;
        orderId = list.orderId;
        anchorId = list.anchorid;
        service_time.setText(list.times + "小时");
        phone_number.setText(list.phone);
        go_home_time.setText(list.servertime);
        go_home_address.setText(list.address);
        order_value.setText(list.serverContent);
        if (TextUtils.equals(isSelf, "1")) {
            sure_tv.setVisibility(View.GONE);
            sure_tv_two.setVisibility(View.GONE);
            add_time.setVisibility(View.GONE);
            quite_order.setVisibility(View.GONE);
        }
    }

    @OnClick({R.id.add_time, R.id.sure_tv, R.id.sure_tv_two, R.id.quite_order, R.id.right_text})
    public void OnClick(View view) {
        switch (view.getId()) {
            case R.id.add_time:
                showLabelListDialog();
                break;
            case R.id.sure_tv:
                if (state == 5) {
                    Intent intent = new Intent(getApplicationContext(), CommentActivity.class);
//                    intent.putExtra(Constant.ACTOR_ID, list.orderId);
                    intent.putExtra(Constant.ACTOR_ID, list.anchorid);
                    intent.putExtra("orderId", list.orderId);
                    startActivity(intent);
                    finish();
                }
                if (state == 4 && is_first == 4) {
                    completeService();
                }
                if (state == 33) {
                    StartService();
                }
                if (state == 6) {
                    showToPayDialog(this, total + "");
                }
                break;
            case R.id.quite_order:
                queryTraffic();

                break;
            case R.id.right_text:
                Intent intent = new Intent(this, AddTimeDetailActivity.class);
                intent.putExtra("lists", (Serializable) lists);
                startActivity(intent);
                break;
            default:
                break;
        }
    }

    int pay_time;
    int is_first = 0;

    /**
     * 获取主播交通费
     */
    public void queryTraffic() {
        Map<String, String> paramMap = new HashMap<>();
        paramMap.put("userId", getUserId());
        OkHttpUtils.post().url(ChatApi.QUERY_TRAFFIC)
                .addParams("param", ParamUtil.getParam(paramMap))
                .build().execute(new AjaxCallback<BaseResponse<QuiteTraffic>>() {
            @Override
            public void onResponse(BaseResponse<QuiteTraffic> response, int id) {
                if (response != null) {
                    if (response.m_istatus == NetCode.SUCCESS) {
                        QuiteTraffic quiteTraffic = response.m_object;
                        num = quiteTraffic.trafficRate;
                        showQuiteOrderDialog(OrderDetailsActivity.this);
                    }
                }
//
            }

            @Override
            public void onError(Call call, Exception e, int id) {
                super.onError(call, e, id);
                ToastUtil.showToast(getApplicationContext(), R.string.system_error);
            }
        });
    }

    /**
     * 用户退单
     */
    private void quiteOrder(String state) {
        Map<String, String> paramMap = new HashMap<>();
        paramMap.put("userId", getUserId());
        paramMap.put("orderId", String.valueOf(list.orderId));
        paramMap.put("anchorId", String.valueOf(list.anchorid));
        paramMap.put("isCheck", state);
        OkHttpUtils.post().url(ChatApi.CHARGEBACK)
                .addParams("param", ParamUtil.getParam(paramMap))
                .build().execute(new AjaxCallback<BaseResponse<BaseBean>>() {
            @Override
            public void onResponse(BaseResponse<BaseBean> response, int id) {
                if (response != null) {
                    if (response.m_istatus == NetCode.SUCCESS) {
                        ToastUtil.show(response.m_strMessage);
                        quite_order.setClickable(false);
                        quite_order.setBackgroundResource(R.drawable.yuan_gree_save_two);
                    }
                }
//
            }

            @Override
            public void onError(Call call, Exception e, int id) {
                super.onError(call, e, id);
                ToastUtil.showToast(getApplicationContext(), R.string.system_error);
            }
        });
    }

    /**
     * 主播确认退单
     */
    private void quiteOrder(String isCheck, final String state) {
        Map<String, String> paramMap = new HashMap<>();
        paramMap.put("userId", getUserId());
        paramMap.put("orderId", String.valueOf(list.orderId));
        paramMap.put("anchorId", String.valueOf(list.anchorid));
        paramMap.put("isCheck", isCheck);
        paramMap.put("status", state);
        OkHttpUtils.post().url(ChatApi.QUIET_ORDER)
                .addParams("param", ParamUtil.getParam(paramMap))
                .build().execute(new AjaxCallback<BaseResponse<BaseBean>>() {
            @Override
            public void onResponse(BaseResponse<BaseBean> response, int id) {
                if (response != null) {
                    if (response.m_istatus == NetCode.SUCCESS) {
                        if (state.equals("0")) {
                            sure_tv.setVisibility(View.GONE);
                        }
                        ToastUtil.show(response.m_strMessage);

//                        quite_order.setClickable(false);
//                        quite_order.setBackgroundResource(R.drawable.yuan_gree_save_two);
                    }
                }
//
            }

            @Override
            public void onError(Call call, Exception e, int id) {
                super.onError(call, e, id);
                ToastUtil.showToast(getApplicationContext(), R.string.system_error);
            }
        });
    }

    /**
     * 同意或拒绝主播开始服务
     */
    private void AgreeOrRefuse(String isAgree) {
        Map<String, String> map = new HashMap<>();
        map.put("userId", getUserId());
        map.put("orderId", String.valueOf(list.orderId));
        map.put("anchorId", String.valueOf(list.anchorid));
        map.put("isAgree", isAgree);
        OkHttpUtils.post().url(ChatApi.AGREE_OR_REFUSE)
                .addParams("param", ParamUtil.getParam(map))
                .build().execute(new AjaxCallback<BaseResponse<BaseBean>>() {
            @Override
            public void onResponse(BaseResponse<BaseBean> response, int id) {
                if (response != null && response.m_istatus == NetCode.SUCCESS) {
                    if (flag_sb == 0) {
                        querySingleOrder();
                    }
                    ToastUtil.show(response.m_strMessage);
                }
            }
        });
    }

    /**
     * 加钟刷新数据
     */
    private void querySingleOrder() {
        Map<String, String> paramMap = new HashMap<>();
        paramMap.put("userId", getUserId());
        paramMap.put("orderId", String.valueOf(list.orderId));
        OkHttpUtils.post().url(ChatApi.QUERY_SINGLE_ORDER)
                .addParams("param", ParamUtil.getParam(paramMap))
                .build().execute(new AjaxCallback<BaseResponse<StartServiceBean>>() {
            @Override
            public void onResponse(BaseResponse<StartServiceBean> response, int id) {
                if (response != null) {
                    if (response.m_istatus == NetCode.SUCCESS) {
                        try {
                            StartServiceBean bean = response.m_object;
                            if (bean != null) {
                                if (bean.continueClockList.size() > 0) {
                                    lists = bean.continueClockList;
                                    setRightText(R.string.yuebo_add_time);
                                }

                                if (mCountDownTimer != null) {
                                    mCountDownTimer.cancel();
                                    mCountDownTimer = null;
                                }
                                if (bean.isStart.equals("0")) {
                                    Log.i("ceshi isStart", bean.isStart);
                                    if (role == 0) {
                                        showSureDialog(OrderDetailsActivity.this);
                                    } else {
                                        sure_tv.setEnabled(false);
                                        sure_tv.setBackgroundResource(R.drawable.yuan_gree_save_two);
                                    }

                                }
                                if (bean.status == 1) {
                                    pay_time = bean.endPayTime;
                                    startCountDown(pay_time);
                                }
                                if (bean.status == 4) {
                                    is_first = 4;
                                    if (role == 0) {
                                        state = 4;
                                    }
                                    quite_order.setVisibility(View.GONE);
                                    startCountDown(Integer.valueOf(bean.laveTime));
                                }
                                if (bean.status == 5) {
                                    state = 5;
                                    order_state.setText("已结束");
                                    sure_tv_two.setVisibility(View.GONE);
                                    sure_tv.setVisibility(View.GONE);
//                                sure_tv.setText("立即评价");
                                }
                                if (TextUtils.equals(bean.cancelStatus, "98")) { //没勾选
                                    num = bean.traffic;
                                    state_order = bean.isCheck;
                                    showQuiteOrderDialog(OrderDetailsActivity.this);
                                }
                                if (TextUtils.equals(bean.cancelStatus, "99")) { //勾选
                                    num = bean.traffic;
                                    state_order = bean.isCheck;
                                    showQuiteOrderDialog(OrderDetailsActivity.this);
                                }
                                service_time.setText(String.valueOf(bean.times) + "小时");
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }


                    } else if (response.m_istatus != 1) {
                        ToastUtil.showToast(getApplicationContext(), response.m_strMessage);
                    }
                }
            }

            @Override
            public void onError(Call call, Exception e, int id) {
                super.onError(call, e, id);
                ToastUtil.showToast(getApplicationContext(), R.string.system_error);
            }
        });
    }

    /**
     * 主播开始服务
     */
    private void StartService() {
        Map<String, String> paramMap = new HashMap<>();
        paramMap.put("userId", getUserId());
        paramMap.put("orderId", String.valueOf(list.orderId));
        paramMap.put("anchorId", String.valueOf(list.anchorid));
        OkHttpUtils.post().url(ChatApi.START_SERVICE)
                .addParams("param", ParamUtil.getParam(paramMap))
                .build().execute(new AjaxCallback<BaseResponse<StartServiceBean>>() {
            @Override
            public void onResponse(BaseResponse<StartServiceBean> response, int id) {
                if (response != null) {
                    if (response.m_istatus == NetCode.SUCCESS) {
//                        to_to_pay_suss.setVisibility(View.GONE);
                        StartServiceBean bean = response.m_object;
                        ToastUtil.showToast(getApplicationContext(), "已通知用户确认开始服务！");
                        sure_tv.setBackgroundResource(R.drawable.yuan_gree_save_two);
                        sure_tv.setEnabled(false);
//                        if (bean!=null){
//                            startCountDown(bean.times);
//                            status_order="<font color=\"#7275FF\">"+"进行中"+"</font>";
//                            order_state.setText(Html.fromHtml(status_order));
//                            ToastUtil.showToast(getApplicationContext(), R.string.start_service);
//                        }

                    } else if (response.m_istatus != 1) {
                        ToastUtil.showToast(getApplicationContext(), response.m_strMessage);
                    }
                } else {
                    ToastUtil.showToast(getApplicationContext(), response.m_strMessage);
                }
            }

            @Override
            public void onError(Call call, Exception e, int id) {
                super.onError(call, e, id);
                ToastUtil.showToast(getApplicationContext(), R.string.system_error);
            }
        });
    }

    /**
     * 显示标签列表dialog
     */
    private void showLabelListDialog() {
        final Dialog mDialog = new Dialog(this, R.style.DialogStyle_Dark_Background_add);
        View view = LayoutInflater.from(this).inflate(R.layout.add_time_dialog, null);
        setLabelListView(view, mDialog);
        mDialog.setContentView(view);
        Point outSize = new Point();
        getWindowManager().getDefaultDisplay().getSize(outSize);
        Window window = mDialog.getWindow();
        if (window != null) {
            WindowManager.LayoutParams params = window.getAttributes();
            params.width = outSize.x;
            window.setGravity(Gravity.BOTTOM); // 此处可以设置dialog显示的位置
            window.setWindowAnimations(R.style.BottomPopupAnimation); // 添加动画
        }
        mDialog.setCanceledOnTouchOutside(true);
        if (!isFinishing()) {
            mDialog.show();
        }
    }

    /**
     * 设置label list view
     */
    private void setLabelListView(View view, final Dialog mDialog) {

        RecyclerView content_rv = view.findViewById(R.id.content_rv);
        GridLayoutManager gridLayoutManager = new GridLayoutManager(getBaseContext(), 3);
        content_rv.setLayoutManager(gridLayoutManager);
        HashMap<String, Integer> stringIntegerHashMap = new HashMap<>();
        stringIntegerHashMap.put(SpacesItemDecoration.TOP_DECORATION, 10);//上下间距
        content_rv.addItemDecoration(new SpacesItemDecoration(stringIntegerHashMap));
        adapter = new AddTimeLableAdapter(getBaseContext());
        content_rv.setAdapter(adapter);
        adapter.loadData(beans_list);
        adapter.setOnItemClickListener(new AddTimeLableAdapter.OnItemClickListener() {
            @Override
            public void onClick(View v, int pos, int times, int price, int id) {
                adapter.setDefSelect(pos);
                add_times = times;
                add_prices = price;
                add_ids = id;
            }
        });
        TextView confirm_tv = view.findViewById(R.id.go_to_pay);
        final TextView to_to_pay_suss = view.findViewById(R.id.to_to_pay_suss);
        TextView gold_num = view.findViewById(R.id.gold_num);
        confirm_tv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), ChargeActivity.class);
                startActivity(intent);
                mDialog.dismiss();

            }
        });
        to_to_pay_suss.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (add_prices > 0) {
                    if (add_prices > mMyGoldNumber) {
                        ToastUtil.showToast(getApplicationContext(), R.string.gold_not_enough);
                        return;
                    }
//                    reWardGift(add_prices);
                    Add_Time(to_to_pay_suss);
                } else {
                    ToastUtil.showToast(getApplicationContext(), R.string.place_chose_time);
                }
                mDialog.dismiss();
            }
        });
        getMyGold(gold_num);
    }

    /**
     * 用户加钟
     */
    private void Add_Time(final TextView to_to_pay_suss) {
        Map<String, String> paramMap = new HashMap<>();
        paramMap.put("userId", getUserId());
        paramMap.put("orderId", String.valueOf(list.orderId));
        paramMap.put("anchorId", String.valueOf(list.anchorid));
        paramMap.put("mealId", String.valueOf(add_ids));
        OkHttpUtils.post().url(ChatApi.CONTINUE_CLOCK)
                .addParams("param", ParamUtil.getParam(paramMap))
                .build().execute(new AjaxCallback<BaseResponse>() {
            @Override
            public void onResponse(BaseResponse response, int id) {
                if (response != null) {
                    if (response.m_istatus == NetCode.SUCCESS) {
                        to_to_pay_suss.setVisibility(View.GONE);
                        querySingleOrder();
                        ToastUtil.showToast(getApplicationContext(), response.m_strMessage);
                    } else if (response.m_istatus != 1) {
                        ToastUtil.showToast(getApplicationContext(), response.m_strMessage);
                    }
                } else {
                    ToastUtil.showToast(getApplicationContext(), R.string.pay_vip_fail);
                }
            }

            @Override
            public void onError(Call call, Exception e, int id) {
                super.onError(call, e, id);
                ToastUtil.showToast(getApplicationContext(), R.string.system_error);
            }
        });
    }

    /**
     * 获取我的金币余额
     */
    private void getMyGold(final TextView can_use_iv) {
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
                        can_use_iv.setText(String.valueOf(mMyGoldNumber));
                    }
                }
            }
        });
    }

    /**
     * 获取标签列表
     */
    private void getLabelList() {
        Map<String, String> paramMap = new HashMap<>();
        paramMap.put("userId", getUserId());
        paramMap.put("anchorId", String.valueOf(list.anchorid));
        OkHttpUtils.post().url(ChatApi.QUERY_CHACK_SERVICE)
                .addParams("param", ParamUtil.getParam(paramMap))
                .build().execute(new AjaxCallback<BaseListResponse<AddTimeBean>>() {
            @Override
            public void onResponse(BaseListResponse<AddTimeBean> response, int id) {
                if (response != null && response.m_istatus == NetCode.SUCCESS) {
                    List<AddTimeBean> beans = response.m_object;
                    if (beans != null && beans.size() > 0) {
                        beans_list = beans;
                    }
                }
            }
        });
    }

    private void completeService() {
        Map<String, String> paramMap = new HashMap<>();
        paramMap.put("userId", getUserId());
        paramMap.put("orderId", String.valueOf(list.orderId));
        OkHttpUtils.post().url(ChatApi.COMPLETE_SERVICE)
                .addParams("param", ParamUtil.getParam(paramMap))
                .build().execute(new AjaxCallback<BaseResponse>() {
            @Override
            public void onResponse(BaseResponse response, int id) {
                if (response != null && response.m_istatus == NetCode.SUCCESS) {
                    String beans = response.m_strMessage;
                    int code = response.m_istatus;
                    if (code == 1) {
                        if (mCountDownTimer != null) {
                            mCountDownTimer.cancel();
                            mCountDownTimer = null;
                        }
                        sure_tv.setText("立即评价");
                        state = 5;
                        add_time.setVisibility(View.GONE);
                        sure_tv_two.setVisibility(View.GONE);
                        ToastUtil.show(beans);
                    }
                }
            }

            @Override
            public void onError(Call call, Exception e, int id) {
                super.onError(call, e, id);
                ToastUtil.showToast(getApplicationContext(), R.string.system_error);
            }
        });
    }

    /**
     * 开始倒计时
     */
    private void startCountDown(long data) {
        mCountDownTimer = new CountDownTimer(data * 1000, 1000) {
            @Override
            public void onTick(long l) {
                try {
                    if (state == 33) {
                        sure_tv.setVisibility(View.GONE);
                        sure_tv_two.setVisibility(View.VISIBLE);
                        sure_tv_two.setClickable(false);
                        sure_tv_two.setTextColor(getResources().getColor(R.color.white));
                        String text = getResources().getString(R.string.service_over) + "(" + generateTime(l) + ")";
                        sure_tv_two.setText(text);
                    }
                    if (state == 44) {
                        sure_tv_two.setVisibility(View.VISIBLE);
                        sure_tv_two.setClickable(false);
                        sure_tv_two.setTextColor(getResources().getColor(R.color.white));
                        String text = getResources().getString(R.string.service_over) + "(" + generateTime(l) + ")";
                        sure_tv_two.setText(text);
                    }
                    if (state == 6) {
                        String text = getResources().getString(R.string.to_to_pay_suss) + "(" + generateTime(l) + ")";
                        sure_tv.setText(text);
                        return;
                    }
                    if (state == 4) {
                        status_order = "<font color=\"#7275FF\">" + "进行中" + "</font>";
                        order_state.setText(Html.fromHtml(status_order));
                        add_time.setVisibility(View.VISIBLE);
                        sure_tv.setVisibility(View.VISIBLE);
                        sure_tv_two.setVisibility(View.GONE);
                        sure_tv.setTextColor(getResources().getColor(R.color.black_bbbbbb));
                        String text = getResources().getString(R.string.service_over) + "(" + generateTime(l) + ")";
                        sure_tv.setText(text);
                    } else {
                        sure_tv.setTextColor(getResources().getColor(R.color.black_bbbbbb));
                        String text = getResources().getString(R.string.service_over) + "(" + generateTime(l) + ")";
                        sure_tv.setText(text);
                    }
                    if (TextUtils.equals(isSelf,"1")){
                        sure_tv.setVisibility(View.GONE);
                        sure_tv_two.setVisibility(View.GONE);
                        add_time.setVisibility(View.GONE);
                        quite_order.setVisibility(View.GONE);
                    }
                }catch (Exception e){
                    e.printStackTrace();
                }

            }

            @Override
            public void onFinish() {
                if (state == 33) {
                    sure_tv_two.setVisibility(View.GONE);
                }
                if (state == 44) {
                    sure_tv_two.setVisibility(View.GONE);
                }
                if (state == 6) {
                    sure_tv.setVisibility(View.GONE);
                } else {
                    sure_tv.setClickable(true);
                    sure_tv.setTextColor(getResources().getColor(R.color.white));
                    sure_tv.setText(R.string.service_over);
                    completeService();
                }
                if (mCountDownTimer != null) {
                    mCountDownTimer.cancel();
                    mCountDownTimer = null;
                }
                if (TextUtils.equals(isSelf,"1")){
                    sure_tv.setVisibility(View.GONE);
                    sure_tv_two.setVisibility(View.GONE);
                    add_time.setVisibility(View.GONE);
                    quite_order.setVisibility(View.GONE);
                }
            }
        }.start();
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
     * 将毫秒转时分秒
     *
     * @param time
     * @return
     */
    public static String generateTime(long time) {
        int totalSeconds = (int) (time / 1000);
        int seconds = totalSeconds % 60;
        int minutes = (totalSeconds / 60) % 60;
        int hours = totalSeconds / 3600;

        return hours > 0 ? String.format("%02d:%02d:%02d", hours, minutes, seconds) : String.format("%02d:%02d", minutes, seconds);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mCountDownTimer != null) {
            mCountDownTimer.cancel();
            mCountDownTimer = null;
        }
    }

    public void showQuiteOrderDialog(Context context) {
        dialog = new Dialog(context, R.style.dialog);
        View view = LayoutInflater.from(context).inflate(R.layout.refuse_dialog, null);
        dialog.setContentView(view);
        dialog.setCancelable(true);
        dialog.setCanceledOnTouchOutside(false);
        final TextView textView1 = view.findViewById(R.id.order_money);

        final String htmlString = "此订单需扣除" + "<font color=\"#7275FF\">" + num + "</font>" + "%的主播交通费";
        final String htmlString1 = "<font color=\"#7275FF\">" + "此订单需扣除" + num + "%的主播交通费" + "</font>";
        textView1.setText(Html.fromHtml(htmlString));
        final ImageView refuse_check = view.findViewById(R.id.refuse_check);
        if (!TextUtils.isEmpty(state_order)) {
            if (state_order.equals("1")) {
                refuse_check.setSelected(false);
            } else {
                refuse_check.setSelected(true);
            }
        }
        dialog.findViewById(R.id.btn_cancel).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!TextUtils.isEmpty(state_order)) {

                    quiteOrder(state_order, "1");
                }
                dialog.dismiss();
            }
        });
        refuse_check.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (TextUtils.isEmpty(state_order)) {
                    if (refuse_check.isSelected()) {
                        type = "1";
                        textView1.setText(Html.fromHtml(htmlString));
                        refuse_check.setSelected(false);
                    } else {
                        type = "0";
                        textView1.setText(Html.fromHtml(htmlString1));
                        refuse_check.setSelected(true);
                    }
                }

            }
        });
        dialog.findViewById(R.id.btn_sure).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (TextUtils.isEmpty(state_order)) {
                    if (refuse_check.isSelected()) {
                        type = "0";
                    } else {
                        type = "1";
                    }
                    quiteOrder(type);
                } else {
                    quiteOrder(state_order, "0");
                }
                dialog.dismiss();

            }
        });
        dialog.show();
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

    private void userPayNum(String total) {
        Map<String, String> paramMap = new HashMap<>();
        paramMap.put("userId", getUserId());
        paramMap.put("anchorId", String.valueOf(anchorId));
        paramMap.put("orderId", String.valueOf(orderId));
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
                        status_order = "<font color=\"#ffaa20\">" + "未开始（已付款）" + "</font>";
                        order_state.setText(Html.fromHtml(status_order));
                        sure_tv.setVisibility(View.GONE);
                        quite_order.setVisibility(View.VISIBLE);
//                        finish();
                    }
                } else {
                    ToastUtil.show(response.m_strMessage);
                }
            }

            @Override
            public void onError(Call call, Exception e, int id) {
                super.onError(call, e, id);
                ToastUtil.showToast(mContext, R.string.system_error);
                dialog.dismiss();
            }

        });
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

    protected void addTime(String content) {
        super.onRecharge(content);
        querySingleOrder();

    }

    protected void confirm(String content) {
        super.onRecharge(content);
        querySingleOrder();
//        sure_tv_two.setVisibility(View.GONE);
//        sure_tv.setVisibility(View.GONE);
//        add_time.setVisibility(View.GONE);

    }

    protected void start(String content) {
        super.onRecharge(content);
        showSureDialog(this);

    }

    String state_order;

    @Override
    protected void quite(String content) {
        super.quite(content);
        try {
            flag = 1;

            org.json.JSONObject jc = new org.json.JSONObject(content);
            num = jc.getString("traffic");
            state_order = jc.getString("trafficStatus");
            showQuiteOrderDialog(OrderDetailsActivity.this);


        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void order_quite(String content) {
        super.order_quite(content);
        try {
            JSONObject jc = new JSONObject(content);
            String mess = jc.getString("message");
            if (mess.contains("不同意")) {
                quite_order.setClickable(true);
                quite_order.setBackgroundResource(R.drawable.yuan_gree_save);
            } else {
                finish();
            }
            ToastUtil.show(jc.getString("message"));
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void startOrRefuse(String content) {
        super.startOrRefuse(content);
        try {
            JSONObject jc = new JSONObject(content);
            String isAgree = jc.getString("status");
            String laveTime = jc.getString("laveTime");
            if (isAgree.equals("4")) { //同意
                startCountDown(Integer.valueOf(laveTime));
                status_order = "<font color=\"#7275FF\">" + "进行中" + "</font>";
                order_state.setText(Html.fromHtml(status_order));
                ToastUtil.showToast(getApplicationContext(), R.string.start_service);
            } else {
                sure_tv.setEnabled(true);
                sure_tv.setBackgroundResource(R.drawable.yuan_gree_save);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    int flag_sb = 0;

    public void showSureDialog(Context context) {
        final Dialog  dialogs = new Dialog(context, R.style.dialog);
        View view = LayoutInflater.from(context).inflate(R.layout.sure_or_refuse_dialog, null);
        dialogs.setContentView(view);
        dialogs.setCancelable(true);
        dialogs.setCanceledOnTouchOutside(false);

        dialogs.findViewById(R.id.btn_cancel).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialogs.dismiss();
                AgreeOrRefuse("1");
                flag_sb = 1;

            }
        });
        dialogs.findViewById(R.id.btn_sure).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialogs.dismiss();
                AgreeOrRefuse("0");
                flag_sb = 0;

            }
        });
        dialogs.show();
    }

}
