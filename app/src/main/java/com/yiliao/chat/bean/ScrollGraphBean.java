package com.yiliao.chat.bean;

import com.yiliao.chat.base.BaseBean;

import java.util.ArrayList;

public class ScrollGraphBean extends BaseBean {
    public int seconds ;//	轮播时间
    public ArrayList<ScrollGraphData> scrollgraph;
    public  class ScrollGraphData{
        public int t_id;
        public String t_messaage;
        public String t_money;
        public String t_type;
        public String t_userid;
        public String t_handImg;
        public String t_giftname;

    }

}
