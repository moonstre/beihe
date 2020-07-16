package com.yiliao.chat.fulive;

public class BeautificationParams {
    // 滤镜名称，默认 origin
    public static final String FILTER_NAME = "filter_name";
    // 滤镜程度，0-1，默认 1
    public static final String FILTER_LEVEL = "filter_level";
    // 美白程度，0-1，默认 0.2
    public static final String COLOR_LEVEL = "color_level";
    // 红润程度，0-1，默认 0.5
    public static final String RED_LEVEL = "red_level";
    // 磨皮程度，0-6，默认 6
    public static final String BLUR_LEVEL = "blur_level";
    // 肤色检测开关，0 代表关，1 代表开，默认 0
    public static final String SKIN_DETECT = "skin_detect";
    // 肤色检测开启后，非肤色区域的融合程度，0-1，默认 0.45
    public static final String NONSKIN_BLUR_SCALE = "nonskin_blur_scale";
    // 磨皮类型，0 代表清晰磨皮，1 代表重度磨皮，默认 1
    public static final String HEAVY_BLUR = "heavy_blur";
    // 变形选择，0 代表女神，1 网红，2 自然，3 预设，4，精细变形，5 用户自定义，默认 3
    public static final String FACE_SHAPE = "face_shape";
    // 变形程度，0-1，默认 1
    public static final String FACE_SHAPE_LEVEL = "face_shape_level";
    // 大眼程度，0-1，默认 0.5
    public static final String EYE_ENLARGING = "eye_enlarging";
    // 瘦脸程度，0-1，默认 0
    public static final String CHEEK_THINNING = "cheek_thinning";
    // 窄脸程度，0-1，默认 0
    public static final String CHEEK_NARROW = "cheek_narrow";
    // 小脸程度，0-1，默认 0
    public static final String CHEEK_SMALL = "cheek_small";
    // V脸程度，0-1，默认 0
    public static final String CHEEK_V = "cheek_v";
    // 瘦鼻程度，0-1，默认 0
    public static final String INTENSITY_NOSE = "intensity_nose";
    // 嘴巴调整程度，0-1，默认 0.5
    public static final String INTENSITY_MOUTH = "intensity_mouth";
    // 额头调整程度，0-1，默认 0.5
    public static final String INTENSITY_FOREHEAD = "intensity_forehead";
    // 下巴调整程度，0-1，默认 0.5
    public static final String INTENSITY_CHIN = "intensity_chin";
    // 变形渐变调整参数，0 渐变关闭，大于 0 渐变开启，值为渐变需要的帧数
    public static final String CHANGE_FRAME = "change_frame";
    // 亮眼程度，0-1，默认 1
    public static final String EYE_BRIGHT = "eye_bright";
    // 美牙程度，0-1，默认 1
    public static final String TOOTH_WHITEN = "tooth_whiten";
    // 美颜参数全局开关，0 代表关，1 代表开
    public static final String IS_BEAUTY_ON = "is_beauty_on";

    // 女神
    public static final int FACE_SHAPE_GODDESS = 0;
    // 网红
    public static final int FACE_SHAPE_NET_RED = 1;
    // 自然
    public static final int FACE_SHAPE_NATURE = 2;
    // 默认
    public static final int FACE_SHAPE_DEFAULT = 3;
    // 精细变形
    public static final int FACE_SHAPE_CUSTOM = 4;
}
