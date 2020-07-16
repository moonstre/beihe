package com.yiliao.chat.myactivity.bean;

import com.yiliao.chat.base.BaseBean;

import java.util.List;

public class ActivtyDetalisInformationBean<T,K> extends BaseBean {
    public String is_apply;//是否报名活动0未报名1已报名
    public T data;//活动信息
    public List<K> applyList;//报名用户
}
