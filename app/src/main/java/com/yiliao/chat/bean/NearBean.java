package com.yiliao.chat.bean;


import com.yiliao.chat.base.BaseBean;

/*
 * Copyright (C) 2018
 * 版权所有
 *
 * 功能描述：距离的Recycler bean
 * 作者;
 * 创建时间：2018/11/16
 *
 * 修改人：
 * 修改描述：
 * 修改日期
 */
public class NearBean extends BaseBean {

    public int t_id;
    public String t_vocation;//职业
    public int t_role;//角色 0.普通用户 1.主播
    public String t_autograph;
    public String t_handImg;
    public int t_onLine;//在线状态 0.在线 1.离线
    public int t_age;
    public double distance;
    public String t_nickName;
    public int t_sex;//性别 0.女 1.男

}

