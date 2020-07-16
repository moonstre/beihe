package com.yiliao.chat.bean;

import com.yiliao.chat.base.BaseBean;

/*
 * Copyright (C) 2018
 * 版权所有
 *
 * 功能描述：榜单页面bean
 * 作者：
 * 创建时间：2018/9/26
 *
 * 修改人：
 * 修改描述：
 * 修改日期
 */
public class RankBean extends BaseBean {

    public int t_id;
    public int t_idcard;
    public int gold;
    public String t_handImg;
    public String t_nickName;
    public int t_role;//0 普通用户; 1 主播
    public int t_onLine;//普通用户 0.在线1.在聊2.离线; 主播 0.在线 1.离线

}
