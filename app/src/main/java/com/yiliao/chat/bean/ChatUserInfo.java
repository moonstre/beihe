package com.yiliao.chat.bean;

import com.yiliao.chat.base.BaseBean;

/*
 * Copyright (C) 2018
 * 版权所有
 *
 * 功能描述：用户信息bean
 * 作者：
 * 创建时间：2018/6/27
 *
 * 修改人：
 * 修改描述：
 * 修改日期
 */
public class ChatUserInfo extends BaseBean {

    public int t_id;//用户编号
    public int t_is_vip = 1;//是否VIP 0.是1.否
    public int t_is_extreme = 1;//是否有至尊会员特权，0 有，1 无
    public int gold;//	金币
    public String phone;//电话号码
    public int t_sex = -1;//性别：0.女，1.男
    public int t_role;//用户角色  1 主播 0 用户
    public String nickName;//用户昵称
    public String headUrl;//用户头像地址
    public int is_phone;//是否需要绑定手机号；0 需要，1 不需要
    public String is_referee;//0没有1有
    public String t_token;//token
    public String logintype;//登录类型

}