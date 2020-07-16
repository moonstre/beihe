package com.yiliao.chat.bean;

import com.yiliao.chat.base.BaseBean;

import java.io.Serializable;
import java.util.List;

public class StartServiceBean extends BaseBean {
    public int times;
    public String laveTime;
    public int endPayTime;
    public int status;
    public int commentId;
    public String cancelStatus;
    public String traffic;
    public String isCheck;
    public String isStart;
    public List<continueClockList> continueClockList;
    public class continueClockList implements Serializable {
        public int id;
        public String creatime;
        public int price;
        public String serverContent;
        public int clolckTimes;
        public int  clolckPrice;
    }
}
