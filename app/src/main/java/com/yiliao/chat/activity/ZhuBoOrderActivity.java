package com.yiliao.chat.activity;


import android.annotation.SuppressLint;
import android.os.CountDownTimer;
import android.text.Html;
import android.view.View;
import android.widget.TextView;

import com.yiliao.chat.R;
import com.yiliao.chat.base.BaseActivity;
import com.yiliao.chat.base.BaseBean;
import com.yiliao.chat.base.BaseResponse;
import com.yiliao.chat.bean.RecivieBean;
import com.yiliao.chat.constant.ChatApi;
import com.yiliao.chat.net.AjaxCallback;
import com.yiliao.chat.net.NetCode;
import com.yiliao.chat.util.ParamUtil;
import com.yiliao.chat.util.ToastUtil;
import com.zhy.http.okhttp.OkHttpUtils;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.OnClick;
import okhttp3.Call;

public class ZhuBoOrderActivity extends BaseActivity {
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
    @BindView(R.id.re_tv)
            TextView re_tv;
    @BindView(R.id.order_value)
            TextView order_value;
    String content,anchorId,orderId;
    private CountDownTimer mCountDownTimer;
    RecivieBean bean_contetn;
    @Override
    protected View getContentView() {
        return inflate(R.layout.zhubo_order_layout);
    }

    @Override
    protected void onContentAdded() {
        initvieew();
    }
    public void initvieew(){
        String flag=getIntent().getStringExtra("flag");
        if ("0".equals(flag)){
            bean_contetn= (RecivieBean) getIntent().getSerializableExtra("content");
            String status="<font color=\"#7275FF\">" + "未开始（待付款）" + "</font>";
            order_state.setText(Html.fromHtml(status));
            String htmlString = "<font color=\"#7275FF\">" + bean_contetn.price + "</font>"+"金币/小时";
            service_price.setText(Html.fromHtml(htmlString));
            service_time.setText(bean_contetn.times+"小时");
            phone_number.setText(bean_contetn.phone);
            go_home_time.setText(bean_contetn.servertime);
            go_home_address.setText(bean_contetn.address);
            anchorId=bean_contetn.anchorId;
            orderId=bean_contetn.orderId;
            int time=Integer.parseInt(bean_contetn.waitTime);
            startCountDown(time);

        }else {
            content=getIntent().getStringExtra("content");
            try {
                JSONObject jc = new JSONObject(content);
                RecivieBean recivieBean=new RecivieBean();
                recivieBean.message=jc.getString("message");
                recivieBean.address=jc.getString("address");
                recivieBean.phone=jc.getString("phone");
                recivieBean.price=jc.getString("price");
                recivieBean.status=jc.getString("status");
                recivieBean.servertime=jc.getString("servertime");
                recivieBean.times=jc.getString("times");
                recivieBean.orderId=jc.getString("orderId");
                recivieBean.anchorId=jc.getString("anchorId");
                recivieBean.userHandImg=jc.getString("userHandImg");
                recivieBean.username=jc.getString("username");
                recivieBean.waitTime=jc.getString("waitTime");
                recivieBean.serverContent=jc.getString("serverContent");
                String status="<font color=\"#7275FF\">" + "未开始（待付款）" + "</font>";
                order_state.setText(Html.fromHtml(status));
                String htmlString = "<font color=\"#7275FF\">" + recivieBean.price + "</font>"+"金币/小时";
                service_price.setText(Html.fromHtml(htmlString));
                service_time.setText(recivieBean.times+"小时");
                phone_number.setText(recivieBean.phone);
                go_home_time.setText(recivieBean.servertime);
                go_home_address.setText(recivieBean.address);
                order_value.setText(recivieBean.serverContent);
                anchorId=recivieBean.anchorId;
                orderId=recivieBean.orderId;
                startCountDown(Integer.parseInt(recivieBean.waitTime));
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }


    }
    /**
     * 开始倒计时
     */
    private void startCountDown(int time ) {
        mCountDownTimer = new CountDownTimer(time*1000, 1000) {
            @Override
            public void onTick(long l) {
                String text = getResources().getString(R.string.go_to_save) +"("+generateTime(l)+")" ;
                sure_tv.setText(text);
            }

            @Override
            public void onFinish() {
                sure_tv.setText(R.string.go_to_save);
                if (mCountDownTimer != null) {
                    mCountDownTimer.cancel();
                    mCountDownTimer = null;
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
        }else {
            if (second < 10) {
                return minute + ":" + "0" + second;
            } else {
                return minute + ":" + second;
            }
        }
    }
    public void confrimOrRes(final int code){
        Map<String, String> paramMap = new HashMap<>();
        paramMap.put("userId", getUserId());
        paramMap.put("anchorId",anchorId);
        paramMap.put("orderId",orderId);
        paramMap.put("status",String.valueOf(code));
        OkHttpUtils.post().url(ChatApi.CONFRIM_OR_RES_ORDER)
                .addParams("param", ParamUtil.getParam(paramMap))
                .build().execute(new AjaxCallback<BaseResponse>() {
            @Override
            public void onResponse(BaseResponse response, int id) {
                if (response != null && response.m_istatus == NetCode.SUCCESS) {
                    ToastUtil.show(response.m_strMessage);
                }else {
                    ToastUtil.show(response.m_strMessage);
                }
                finish();
            }

            @Override
            public void onError(Call call, Exception e, int id) {
                super.onError(call, e, id);
                ToastUtil.showToast(mContext, R.string.system_error);
            }

        });
    }
    @OnClick({R.id.re_tv,R.id.sure_tv})
    public void OnClick(View view){
        switch (view.getId()){
            case R.id.re_tv:
                confrimOrRes(1);
                break;
            case R.id.sure_tv:
                confrimOrRes(0);
                break;
                default:
                    break;
        }
    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mCountDownTimer != null) {
            mCountDownTimer.cancel();
            mCountDownTimer = null;
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
}
