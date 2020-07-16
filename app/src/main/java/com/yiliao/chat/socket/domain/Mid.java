package com.yiliao.chat.socket.domain;

import java.io.Serializable;

public class Mid implements Serializable {

    private static final long serialVersionUID = 1L;

    private Integer mid;

    public Integer getMid() {
        return mid;
    }

    public void setMid(Integer mid) {
        this.mid = mid;
    }

    /**
     * 发送模拟消息
     */
    public static final int SEND_VIRTUAL_MESSAGE = 30003;
    /**
     * 通知连线
     */
    public static final int CHAT_LINK = 30004;

    /**
     * 对方已挂断
     */
    public static final int HAVE_HANG_UP = 30005;

    /**
     * 被封号
     */
    public static final int BEAN_SUSPEND = 30006;

    public static final int SERVICE_RED=30007;
    public static final int USER_GET_INVITE = 30008;

    public static final int MONEY_NOT_ENOUGH = 30010;

    public static final int ACTIVE_NEW_COMMENT = 30009;

    public static final int QUICK_START_HINT_ANCHOR = 30013;
    public static final int VIDEO_CHAT_START_HINT = 30012;

    public static final int BIG_ROOM_COUNT_CHANGE = 30014;


    public static final int BIG_GIFT = 30015;



    public static final int EXIT = 30016;

    public static final int RECHARGE = 30017;
    public static final int HAVE_NEW_ORDER=30020;

    public static final int NOTICE_USER=30021;
    public static final int payServerOrder = 30022;
    public static final int startServer = 30023;
    public static final int chargeBack = 30027;
    public static final int completServer = 30025;
    public static final int addServerTime = 30026;
    public static final int refuseServer = 30024;
    public static final int  noticeAddGuild = 30031;
    public static final int  removeGuild = 30032;
    public static final int statrOrRefuse=30028;
}
