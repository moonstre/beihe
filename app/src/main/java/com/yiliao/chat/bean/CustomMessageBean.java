package com.yiliao.chat.bean;


import com.yiliao.chat.base.BaseBean;

/*
 * Copyright (C) 2018
 * 版权所有
 *
 * 功能描述：IM自定义消息
 * 作者：
 * 创建时间：2018/8/4
 *
 * 修改人：
 * 修改描述：
 * 修改日期
 */
public class CustomMessageBean extends BaseBean {

    public String type;//礼物类型  0-金币 1-礼物 2-图片 3-视频
    public int gift_id;//礼物id
    public String gift_name;//礼物名称
    public String gift_gif_url;//礼物静态图
    public int gold_number;//金币数量
    public String nickName;//昵称
    public String headUrl;//头像
    public String t_gift_gif_url;//礼物动态图
    public String t_num;


    public String picUrl;//图片地址

    public String videoURL;//视频地址
    public String coverURL;//视频缩略图地址
    public String videoDuration;//视频时长
}
