package com.yiliao.chat.bean;


import com.yiliao.chat.base.BaseBean;

/*
 * Copyright (C) 2018
 * 版权所有
 *
 * 功能描述：大房间贡献值bean
 * 作者：
 * 创建时间：2019/3/5
 *
 * 修改人：
 * 修改描述：
 * 修改日期
 */
public class SendBean extends BaseBean {

    public int t_id;
    public String t_nickName;
    public String t_handImg;
    public int t_is_vip;//	是否VIP
    public int total;//	赠送金币
    public int goldfiles;//		金币档
    public int grade;//		充值级别

}
