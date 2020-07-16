package com.yiliao.chat.bean;

import com.yiliao.chat.base.BaseBean;

public class LoginBean extends BaseBean {
    private int type;
    private int icon;

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public int getIcon() {
        return icon;
    }

    public void setIcon(int icon) {
        this.icon = icon;
    }
}
