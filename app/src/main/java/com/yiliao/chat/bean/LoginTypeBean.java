package com.yiliao.chat.bean;

import com.yiliao.chat.base.BaseBean;

public class LoginTypeBean extends BaseBean {
    //登录方式：1 微信，2 QQ，3 whatsApp，4 Facebook，5 邮箱
    public String t_login_type;
    //分享方式：1 微信好友，2 朋友圈，3 QQ，4 QQ空间
    public String t_share_type;
    //支付方式：0 支付宝，1 微信，2 优云宝，3，黔贵金服
    public String t_pay_type;
}
