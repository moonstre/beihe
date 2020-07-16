package com.yiliao.chat.myactivity;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.shapes.OvalShape;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.yiliao.chat.R;
import com.yiliao.chat.base.AppManager;
import com.yiliao.chat.base.BaseListResponse;
import com.yiliao.chat.base.BaseResponse;
import com.yiliao.chat.bean.BalanceBean;
import com.yiliao.chat.bean.ChatUserInfo;
import com.yiliao.chat.bean.UserGoldBean;
import com.yiliao.chat.constant.ChatApi;
import com.yiliao.chat.helper.ChargeHelper;
import com.yiliao.chat.helper.ImageLoadHelper;
import com.yiliao.chat.helper.SharedPreferenceHelper;
import com.yiliao.chat.myactivity.adpate.ApplyListAdpater;
import com.yiliao.chat.myactivity.bean.ActivityApplyBean;
import com.yiliao.chat.myactivity.bean.ActivtyDetalisBean;
import com.yiliao.chat.myactivity.bean.ActivtyDetalisInformationBean;
import com.yiliao.chat.myactivity.bean.ApplyActivityBean;
import com.yiliao.chat.myactivity.bean.CancelActivityBean;
import com.yiliao.chat.net.AjaxCallback;
import com.yiliao.chat.net.NetCode;
import com.yiliao.chat.util.ParamUtil;
import com.yiliao.chat.util.ToastUtil;
import com.zhy.http.okhttp.OkHttpUtils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/*
* 活动详情
* */
public class ActivityDetalisActivity extends AppCompatActivity {

    @BindView(R.id.ActivityDetalis_BgImage)
    ImageView ActivityDetalis_BgImage;

    @BindView(R.id.ActivityDetalis_HeadImage)
    ImageView ActivityDetalis_HeadImage;

    //距离
    @BindView(R.id.ActivityDetalis_Distance)
    TextView ActivityDetalis_Distance;

    //昵称
    @BindView(R.id.ActivityDetalis_Name)
    TextView ActivityDetalis_Name;

    //性别
    @BindView(R.id.ActivityDetalis_Gender)
    ImageView ActivityDetalis_Gender;

    //标题
    @BindView(R.id.ActivityDetalis_Title)
    TextView ActivityDetalis_Title;

    //内容
    @BindView(R.id.ActivityDetalis_Content)
    TextView ActivityDetalis_Content;

    //时间
    @BindView(R.id.ActivityDetalis_Time)
    TextView ActivityDetalis_Time;

    //地址
    @BindView(R.id.ActivityDetalis_Address)
    TextView ActivityDetalis_Address;

    //金币
    @BindView(R.id.ActivityDetalis_Money)
    TextView ActivityDetalis_Money;

    //手机
    @BindView(R.id.ActivityDetalis_Phone)
    TextView ActivityDetalis_Phone;

    //微信
    @BindView(R.id.ActivityDetalis_Wchat)
    TextView ActivityDetalis_Wchat;

    @BindView(R.id.ActivityDetalis_ApplyList)
    RecyclerView ActivityDetalis_ApplyList;

    @BindView(R.id.ActivityDetalis_Button)
    TextView ActivityDetalis_Button;

    @BindView(R.id.ActivityDetalis_More)
    RelativeLayout ActivityDetalis_More;

    @BindView(R.id.ActivityDetalis)
    RelativeLayout ActivityDetalis;

    //已报名的信息
    @BindView(R.id.ActivityDetalis_Lin)
    LinearLayout ActivityDetalis_Lin;

    private String activityID;

    //popwindow的ui
    private View view;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activitydetalis);
        ButterKnife.bind(this);
        activityID =getIntent().getStringExtra("ActivityID");
        getInitView();

    }

    @Override
    protected void onResume() {
        super.onResume();
        getActivityDetalis();
    }

    /*
    * 初始化
    * */
    public void getInitView(){

        LinearLayoutManager ms= new LinearLayoutManager(getApplicationContext());
        ms.setOrientation(LinearLayoutManager.HORIZONTAL);// 设置 recyclerview 布局方式为横向布局//     LinearLayoutManager 种 含有3 种布局样式  第一个就是最常用的 1.横向 , 2. 竖向,3.偏移
        ActivityDetalis_ApplyList .setLayoutManager(ms);//给RecyClerView 添加设置好的布局样式
        view= LayoutInflater.from(getApplicationContext()).inflate(R.layout.activitydetalispopwindow,null,false);
    }


    /**
     * 获取活动信息
     */
    //活动详情数据
    private ActivtyDetalisBean detalisBean;
    private void getActivityDetalis() {
        Map<String, String> paramMap = new HashMap<>();
        paramMap.put("userId", getUserId());
        paramMap.put("activityId",activityID);
        OkHttpUtils.post().url(ChatApi.ACTIVITY_DETALIS)
                .addParams("param", ParamUtil.getParam(paramMap))
                .build().execute(new AjaxCallback<BaseListResponse<ActivtyDetalisInformationBean<ActivtyDetalisBean,ActivityApplyBean>>>() {
            @Override
            public void onResponse(BaseListResponse<ActivtyDetalisInformationBean<ActivtyDetalisBean,ActivityApplyBean>> response, int id) {
                if (response != null && response.m_istatus == NetCode.SUCCESS) {

                   List<ActivtyDetalisInformationBean<ActivtyDetalisBean,ActivityApplyBean>> beans= response.m_object;
                   if (beans.size()!=0){
                       Log.i("aaa",beans.get(0).is_apply);

                       detalisBean = beans.get(0).data;
                       if (getUserId().equals(detalisBean.t_uid)){
                           ActivityDetalis_More.setVisibility(View.VISIBLE);
                           ActivityDetalis_Lin.setVisibility(View.VISIBLE);
                           ActivityDetalis_Button.setVisibility(View.GONE);
                       }else {
                           if (beans.get(0).is_apply.equals("0")){
                               ActivityDetalis_Lin.setVisibility(View.GONE);
                               ActivityDetalis_Button.setVisibility(View.VISIBLE);
                               ActivityDetalis_More.setVisibility(View.GONE);
                           }else {
                               ActivityDetalis_More.setVisibility(View.VISIBLE);
                               ActivityDetalis_Lin.setVisibility(View.VISIBLE);
                               ActivityDetalis_Button.setVisibility(View.GONE);
                           }
                       }

                       getUIData(detalisBean);

                       if (beans.get(0).applyList.size()>0){
                           ApplyListAdpater adpater = new ApplyListAdpater(getApplicationContext(),beans.get(0).applyList);
                           ActivityDetalis_ApplyList.setAdapter(adpater);
                       }
                   }



                }
            }
        });
    }
    /**
     * 获取UserId
     */
    public String getUserId() {
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

    /*
    * ui赋值
    * */
    public void getUIData(ActivtyDetalisBean bean){

       /* if (bean.t_uid.equals(getUserId())){
            ActivityDetalis_Lin.setVisibility(View.VISIBLE);
            ActivityDetalis_Button.setVisibility(View.GONE);
        }else {
            ActivityDetalis_Lin.setVisibility(View.GONE);
            ActivityDetalis_Button.setVisibility(View.VISIBLE);

        }*/
        ImageLoadHelper.glideShowImageWithUrl(getApplicationContext(), bean.t_img,ActivityDetalis_BgImage);
        ImageLoadHelper.glideShowCircleImageWithUrl(getApplicationContext(), bean.t_handImg,ActivityDetalis_HeadImage);
        ActivityDetalis_Distance.setText(bean.juli+"km");
        ActivityDetalis_Name.setText(bean.t_nickName);
        if (bean.t_sex==0){
            ActivityDetalis_Money.setText(bean.t_woman_price+"金币");
            ActivityDetalis_Gender.setBackgroundResource(R.drawable.female_red);
        }else {
            ActivityDetalis_Money.setText(bean.t_man_price+"金币");
            ActivityDetalis_Gender.setBackgroundResource(R.drawable.male_blue);
        }
        ActivityDetalis_Title.setText(bean.t_title);
        ActivityDetalis_Content.setText(bean.t_content);
        ActivityDetalis_Time.setText(bean.t_activity_time);
        ActivityDetalis_Address.setText(bean.t_address);
        ActivityDetalis_Phone.setText(bean.t_phone);
        ActivityDetalis_Wchat.setText(bean.t_wx);

        if (bean.t_status==1){
            ActivityDetalis_Button.setText("立即报名");
        }else if(bean.t_status==2){
            ActivityDetalis_Button.setText("已结束");
        }else if(bean.t_status==3){
            ActivityDetalis_Button.setText("已取消");
        }

        payMoney=detalisBean.t_woman_price;


    }

    @OnClick({R.id.ActivityDetalis_Button,R.id.ActivityDetalis_Right,R.id.ActivityDetalis_More,R.id.ActivityDetalis_Back})
    public void Onlick(View view){
        switch (view.getId()){
            case R.id.ActivityDetalis_Button://报名
                ApplyDialog();
                break;
            case R.id.ActivityDetalis_Right://查看报名列表
                Intent intent = new Intent(getApplicationContext(),LookApplyListActivity.class);
                intent.putExtra("activityId",activityID);
                startActivity(intent);
                break;
            case R.id.ActivityDetalis_More:
                getPopwindow();
                mWindow.showAtLocation(ActivityDetalis, Gravity.BOTTOM, 0, 0);
                break;
            case R.id.ActivityDetalis_Back:
                finish();
                break;
        }
    }
    private Dialog dialog;
    private int sex=0;//报名现在性别
    private String payMoney;//支付的金币
    //报名填写信息
    public void ApplyDialog(){
        dialog = new Dialog(ActivityDetalisActivity.this, R.style.dialog);
        dialog.setContentView(R.layout.applyactivitydialog);
        dialog.setCancelable(true);
        dialog.setCanceledOnTouchOutside(true);

        TextView Apply_Dialog_Subimt  =dialog.findViewById(R.id.Apply_Dialog_Subimt);
        final EditText Apply_Dialog_WChat =dialog.findViewById(R.id.Apply_Dialog_WChat);
        final EditText Apply_Dialog_Phone =dialog.findViewById(R.id.Apply_Dialog_Phone);

        final ImageView Apply_Dialog_Male = dialog.findViewById(R.id.Apply_Dialog_Male);
        final ImageView Apply_Dialog_Female = dialog.findViewById(R.id.Apply_Dialog_Female);

        //选择男生
        LinearLayout Apply_Dialog_MaleLin = dialog.findViewById(R.id.Apply_Dialog_MaleLin);
        Apply_Dialog_MaleLin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sex=1;
                payMoney=detalisBean.t_man_price;
                Apply_Dialog_Male.setBackgroundResource(R.drawable.check_red);
                Apply_Dialog_Female.setBackgroundResource(R.drawable.check_gray);
            }
        });

        //选择女生
        LinearLayout Apply_Dialog_FemaleLin = dialog.findViewById(R.id.Apply_Dialog_FemaleLin);
        Apply_Dialog_FemaleLin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sex=0;
                payMoney=detalisBean.t_woman_price;
                Apply_Dialog_Male.setBackgroundResource(R.drawable.check_gray);
                Apply_Dialog_Female.setBackgroundResource(R.drawable.check_red);
            }
        });
        Apply_Dialog_Subimt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (dialog!=null&&dialog.isShowing()){
                    dialog.dismiss();
                }
                payMoney(sex,Apply_Dialog_Phone.getText().toString(),Apply_Dialog_WChat.getText().toString());

            }
        });
        dialog.show();
    }
    private Dialog paydialog;
    //判断是否支付金币弹框
    public void payMoney(final int sex, final String phone, final String wchat){

        paydialog =new Dialog(ActivityDetalisActivity.this,R.style.dialog);
        paydialog.setContentView(R.layout.paymoneydialog);
            Window dialogWindow = paydialog.getWindow();
            dialogWindow
                    .setGravity(Gravity.CENTER | Gravity.CENTER);
            TextView PayMoney_Cancel = (TextView) paydialog.findViewById(R.id.PayMoney_Cancel);
            TextView PayMoney_Sure = (TextView) paydialog.findViewById(R.id.PayMoney_Sure);
            TextView PayMoney_Text =paydialog.findViewById(R.id.PayMoney_Text);
        PayMoney_Text.setText("报名需要支付"+payMoney+"金币");
        PayMoney_Sure.setOnClickListener(new View.OnClickListener() {//确定

                @Override
                public void onClick(View v) {
                    if (paydialog!=null&&paydialog.isShowing()){
                        paydialog.dismiss();
                    }

                    getMyGold(sex,phone,wchat);


                }
            });
        PayMoney_Cancel.setOnClickListener(new View.OnClickListener() {//取消

                @Override
                public void onClick(View v) {
                    // TODO Auto-generated method stub
                    paydialog.dismiss();
                }
            });
        paydialog.show();
    }


    /*
    * 报名
    * */
    public void getSubmit(int sex,String phone,String wchat){
        Map<String, String> paramMap = new HashMap<>();
        paramMap.put("userId", getUserId());
        paramMap.put("activityId",activityID);
        paramMap.put("t_sex",String.valueOf(sex));
        paramMap.put("t_phone",phone);
        paramMap.put("t_wx",wchat);
        OkHttpUtils.post().url(ChatApi.APPLY_ACTIVITY)
                .addParams("param", ParamUtil.getParam(paramMap))
                .build().execute(new AjaxCallback<BaseListResponse<ApplyActivityBean>>() {
            @Override
            public void onResponse(BaseListResponse<ApplyActivityBean> response, int id) {
                if (response != null && response.m_istatus == NetCode.SUCCESS) {
                    ToastUtil.show(response.m_strMessage);

                    getActivityDetalis();

                }
            }
        });
    }
    private PopupWindow mWindow;
    public void getPopwindow(){
        backgroundAlpha(0.5f);

        mWindow = new PopupWindow(view, ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT, false);

        TextView Cancel_Activity = view.findViewById(R.id.Cancel_Activity);
        TextView Edit_Activity =view.findViewById(R.id.Edit_Activity);
        TextView SignOut_Activity = view.findViewById(R.id.SignOut_Activity);
        TextView Cancel = view.findViewById(R.id.Cancel);

        if (getUserId().equals(detalisBean.t_uid)){
            Cancel_Activity.setVisibility(View.VISIBLE);
            Edit_Activity.setVisibility(View.VISIBLE);
            SignOut_Activity.setVisibility(View.GONE);
        }else {
            Cancel_Activity.setVisibility(View.GONE);
            Edit_Activity.setVisibility(View.GONE);
            SignOut_Activity.setVisibility(View.VISIBLE);
        }

        //取消活动
        Cancel_Activity.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mWindow != null && mWindow.isShowing()) {
                    mWindow.dismiss();
                }
                CancelActivity();
            }
        });

        //修改活动
        Edit_Activity.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (mWindow != null && mWindow.isShowing()) {
                    mWindow.dismiss();
                }
                Intent intent = new Intent(getApplicationContext(),EstablishActivity.class);
                intent.putExtra("ACTIVITYID",activityID);
                intent.putExtra("TYPE","Edit");
                intent.putExtra("Data",detalisBean);
                startActivity(intent);
            }
        });
        //退出活动
        SignOut_Activity.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mWindow != null && mWindow.isShowing()) {
                    mWindow.dismiss();
                }
                SignOutActivity();
            }
        });
        Cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mWindow.dismiss();
            }
        });


        mWindow.setBackgroundDrawable(getDrawable());//设置背景透明以便点击外部消失
        mWindow.setOutsideTouchable(true);//点击外部收起
        mWindow.setFocusable(true);//设置焦点生效
        mWindow.setAnimationStyle(R.style.BottomPopupAnimation);
        // popwindow监听
        mWindow.setOnDismissListener(new PopupWindow.OnDismissListener() {
            @Override
            public void onDismiss() {
                backgroundAlpha(1f);
            }
        });
        view.setOnTouchListener(new View.OnTouchListener() {

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                // TODO Auto-generated method stub
                if (mWindow != null && mWindow.isShowing()) {
                    mWindow.dismiss();
//						backgroundAlpha(1f);

//                    popupWindow = null;
//                    IsChooseSpecifications = false;
                }

                return false;
            }
        });
    }


    private Handler handler = new Handler();
    public void backgroundAlpha(final float bgAlpha) {
        final WindowManager.LayoutParams lp = getWindow().getAttributes();
        handler.post(new Runnable() {
            @Override
            public void run() {
                lp.alpha = bgAlpha; //0.0-1.0
                getWindow().setAttributes(lp);
            }
        });

    }
    private Drawable getDrawable() {
        ShapeDrawable bgdrawable = new ShapeDrawable(new OvalShape());
        bgdrawable.getPaint().setColor(ActivityDetalisActivity.this.getResources().getColor(android.R.color.transparent));
        return bgdrawable;
    }

    //取消活动
    public void CancelActivity(){
        Map<String, String> paramMap = new HashMap<>();
        paramMap.put("userId", getUserId());
        paramMap.put("activityId",activityID);
        OkHttpUtils.post().url(ChatApi.CANCEL_ACTIVITY)
                .addParams("param", ParamUtil.getParam(paramMap))
                .build().execute(new AjaxCallback<BaseListResponse<CancelActivityBean>>() {
            @Override
            public void onResponse(BaseListResponse<CancelActivityBean> response, int id) {
                if (response != null && response.m_istatus == NetCode.SUCCESS) {
                    if (mWindow!=null&&mWindow.isShowing()){
                        mWindow.dismiss();
                    }
                    ToastUtil.show("取消成功");
                    finish();
                }else {
                    ToastUtil.show(response.m_strMessage);
                }
            }
        });
    }

    //退出活动
    public void SignOutActivity(){
        Map<String, String> paramMap = new HashMap<>();
        paramMap.put("userId", getUserId());
        paramMap.put("activityId",activityID);
        OkHttpUtils.post().url(ChatApi.SIGNOUT_ACTIVITY)
                .addParams("param", ParamUtil.getParam(paramMap))
                .build().execute(new AjaxCallback<BaseListResponse<CancelActivityBean>>() {
            @Override
            public void onResponse(BaseListResponse<CancelActivityBean> response, int id) {
                if (response != null && response.m_istatus == NetCode.SUCCESS) {
                    if (mWindow!=null&&mWindow.isShowing()){
                        mWindow.dismiss();
                    }
                    getActivityDetalis();
                }else {
                    ToastUtil.show(response.m_strMessage);
                }
            }
        });
    }
    /**
     * 获取我的金币余额
     * @param sex
     * @param phone
     * @param wchat
     */
    private void getMyGold(final int sex, final String phone, final String wchat) {
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
                       if (balanceBean.amount>=Integer.parseInt(payMoney)){
                           getSubmit(sex,phone,wchat);
                       }else {
                           ChargeHelper.showSetCoverDialogWithoutVip(ActivityDetalisActivity.this);
                       }
                    }
                }
            }
        });
    }
}
