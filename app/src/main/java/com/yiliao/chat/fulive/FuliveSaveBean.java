package com.yiliao.chat.fulive;

public class FuliveSaveBean {
    public String mFilterName;//滤镜名称
    public float mFilterLevel;//滤镜强度 范围0~1 SDK默认为 1
    public int mSkinDetect; //精准美肤（肤色检测开关） 0:关闭 1:开启 SDK默认为 0
    public int mHeavyBlur;//磨皮类型 0:清晰磨皮 1:重度磨皮 SDK默认为 1，APP设置 0
    public float mBlurLevel;//磨皮 范围0~6 SDK默认为 6
    public float mColorLevel;//美白 范围0~1 SDK默认为 0.2
    public float mRedLevel;//红润 范围0~1 SDK默认为 0.5
    public float mEyeBright;//亮眼 范围0~1 SDK默认为 0
    public float mToothWhiten;//美牙 范围0~1 SDK默认为 0
}
