package com.yiliao.chat.bean;

import com.yiliao.chat.base.BaseBean;

/*
 * Copyright (C) 2018
 * 版权所有
 *
 * 功能描述：主页女神列表Bean
 * 作者：
 * 创建时间：2018/6/27
 *
 * 修改人：
 * 修改描述：
 * 修改日期
 */
public class GirlListBean extends BaseBean {

    public int t_id;//	用户编号
    public String t_handImg;//头像
    public int t_score;//评价
    public String t_cover_img;//封面图片
    public int t_age;//年龄
    public int t_state;//主播状态(0.空闲1.忙碌2.离线),默认都不是,以免出现问题
    public String t_nickName;//昵称
    public String t_city;//	所在地
    public String t_visit;
    public int t_is_public;//该主播是否存在免费视频0.不存在1.存在
    public int albumId;//视频ID
    public int t_video_gold;//	视频聊天金币数
    public String t_autograph;//签名
    public String t_vocation;//职业
    public String t_level;//等级图标
    public int t_sex;
}
