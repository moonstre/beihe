package com.yiliao.chat.mob;

/**
 * Created by cxf on 2018/10/19.
 */

public class MobBean {

    private String mType;
    private int mIcon1;
    private int mIcon2;
    private int mName;
    private boolean mChecked;

    public String getType() {
        return mType;
    }

    public void setType(String type) {
        mType = type;
    }

    public int getName() {
        return mName;
    }

    public void setName(int name) {
        mName = name;
    }

    public int getIcon1() {
        return mIcon1;
    }

    public void setIcon1(int icon1) {
        mIcon1 = icon1;
    }

    public int getIcon2() {
        return mIcon2;
    }

    public void setIcon2(int icon2) {
        mIcon2 = icon2;
    }

    public boolean isChecked() {
        return mChecked;
    }

    public void setChecked(boolean checked) {
        mChecked = checked;
    }

}
