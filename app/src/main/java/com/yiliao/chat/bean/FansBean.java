package com.yiliao.chat.bean;

import com.yiliao.chat.base.BaseBean;

/*
 * Copyright (C) 2018
 * 版权所有
 *
 * 功能描述：粉丝bean
 * 作者：
 * 创建时间：2018/8/29
 *
 * 修改人：
 * 修改描述：
 * 修改日期
 */
public class FansBean extends BaseBean {

    public int t_id;
    public int t_sex;//	性别：0.女 1.男
    public String t_handImg;//
    public String t_create_time;//创建时间
    public String t_nickName;
    public int t_is_vip = 1;//	是否VIP 0.是1.否
    public int goldfiles;//		金币档
    public int grade;//		充值档 1.第一档 星星 2.第二档 月亮 3.第三档 太阳

}
