package com.yiliao.chat.bean;

import com.yiliao.chat.base.BaseBean;

import java.util.List;

/*
 * Copyright (C) 2018
 * 版权所有
 *
 * 功能描述：个人资料bean
 * 作者：
 * 创建时间：2018/7/3
 *
 * 修改人：
 * 修改描述：
 * 修改日期
 */
public class PersonBean<T, K> extends BaseBean {

    public int t_height;//身高
    public String t_autograph;//个性签名
    public String t_handImg;//头像
    public String t_synopsis;//个人简介
    public String t_phone;//电话
    public int t_age;//	年龄
    public String t_weixin;//微信号
    public String t_constellation;//星座
    public List<T> lable;//	标签数组
    public double t_weight;//体重
    public String t_city;//	城市
    public int t_visit;
    public String t_nickName;//	昵称
    public String t_vocation;//	职业
    public List<K> coverList;//封面图
    public int goldLevel;//用户等级
    public String t_video_img;//视频封面图
    public String t_addres_url;//视频地址
    public String t_cup;


}
