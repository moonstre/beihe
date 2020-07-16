package com.yiliao.chat.activity;

import android.support.constraint.ConstraintLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SimpleItemAnimator;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.HorizontalScrollView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.faceunity.wrapper.faceunity;
import com.wushuangtech.bean.TTTVideoFrame;
import com.wushuangtech.library.Constants;
import com.wushuangtech.wstechapi.TTTRtcEngine;
import com.wushuangtech.wstechapi.TTTRtcEngineEventHandler;
import com.wushuangtech.wstechapi.model.VideoCanvas;
import com.yiliao.chat.BuildConfig;
import com.yiliao.chat.R;
import com.yiliao.chat.base.BaseActivity;
import com.yiliao.chat.constant.Constant;
import com.yiliao.chat.fulive.BeautificationParams;
import com.yiliao.chat.fulive.FuliveSaveBean;
import com.yiliao.chat.fulive.FuliveUtil;
import com.yiliao.chat.fulive.adapter.FilterRecyclerAdapter;
import com.yiliao.chat.fulive.entity.Filter;
import com.yiliao.chat.fulive.entity.FilterEnum;
import com.yiliao.chat.helper.SharedPreferenceHelper;
import com.yiliao.chat.listener.OnItemClickListener;
import com.yiliao.chat.util.LogUtil;
import com.yiliao.chat.util.ToastUtil;
import com.yiliao.chat.view.BeautyBox;
import com.yiliao.chat.view.BeautyBoxGroup;
import com.yiliao.chat.view.CheckGroup;
import com.yiliao.chat.view.TextSeekBar;

import butterknife.BindView;
import butterknife.OnClick;
import cn.tillusory.sdk.TiSDKManager;
import cn.tillusory.tiui.TiPanelLayout;

import static com.wushuangtech.library.Constants.CHANNEL_PROFILE_LIVE_BROADCASTING;
import static com.wushuangtech.library.Constants.CLIENT_ROLE_ANCHOR;
import static com.wushuangtech.library.Constants.ERROR_ENTER_ROOM_BAD_VERSION;
import static com.wushuangtech.library.Constants.ERROR_ENTER_ROOM_TIMEOUT;
import static com.wushuangtech.library.Constants.ERROR_ENTER_ROOM_UNKNOW;
import static com.wushuangtech.library.Constants.ERROR_ENTER_ROOM_VERIFY_FAILED;
import static com.wushuangtech.library.Constants.LOG_FILTER_OFF;

/*
 * Copyright (C) 2018
 * 版权所有
 *
 * 功能描述：美颜
 * 作者：
 * 创建时间：2018/7/24
 *
 * 修改人：
 * 修改描述：
 * 修改日期
 */
public class SetBeautyActivity extends BaseActivity {

    @BindView(R.id.content_fl)
    ConstraintLayout mContentFl;
    @BindView(R.id.btnBeautySetting)
    TextView btnBeautySetting;
    @BindView(R.id.layoutGroupBeauty)
    LinearLayout layoutGroupBeauty;
    @BindView(R.id.seek_meibai)
    TextSeekBar seekMeiBai;
    @BindView(R.id.seek_mopi)
    TextSeekBar seekMoPi;
    private float mDefaultBeautyLevel;
    private float mDefaultBrightLevel;

    @BindView(R.id.seekBarFuLive)
    TextSeekBar seekBarFuLive;
    @BindView(R.id.beauty_group_skin_beauty)
    BeautyBoxGroup mSkinBeautyBoxGroup;
    @BindView(R.id.beauty_box_skin_detect)
    BeautyBox mBoxSkinDetect;
    @BindView(R.id.beauty_box_heavy_blur)
    BeautyBox mBoxHeavyBlur;
    @BindView(R.id.beauty_box_blur_level)
    BeautyBox mBoxBlurLevel;
    @BindView(R.id.beauty_box_color_level)
    BeautyBox mBoxColorLevel;
    @BindView(R.id.beauty_box_red_level)
    BeautyBox mBoxRedLevel;
    @BindView(R.id.beauty_box_eye_bright)
    BeautyBox mBoxEyeBright;
    @BindView(R.id.beauty_box_tooth_whiten)
    BeautyBox mBoxToothWhiten;

    @BindView(R.id.beauty_radio_group)
    CheckGroup mBottomCheckGroup;
    @BindView(R.id.layoutBeautySeek)
    LinearLayout layoutBeautySeek;
    @BindView(R.id.beauty_line)
    View beautyLine;
    @BindView(R.id.skin_beauty_select_block)
    HorizontalScrollView mSkinBeautySelect;
    @BindView(R.id.filter_recycle_view)
    RecyclerView mFilterRecyclerView;
    private boolean isShown;

    //美颜
    private TiSDKManager mTiSDKManager;
    //TTT视频聊天相关
    private TTTRtcEngine mTttRtcEngine;
    // 句柄索引
    private static final int ITEM_ARRAYS_FACE_BEAUTY_INDEX = 0;

    // 句柄数量
    private static final int ITEM_ARRAYS_COUNT = 12;
    //美颜和其他道具的handle数组
    private volatile int[] mItemsArray = new int[ITEM_ARRAYS_COUNT];

    private int mFrameId = 0;

    private FilterRecyclerAdapter mFilterRecyclerAdapter;

    public FuliveSaveBean mFuliveSaveBean;

    @Override
    protected View getContentView() {
        return inflate(R.layout.activity_set_beauty_layout);
    }

    @Override
    protected boolean supportFullScreen() {
        return true;
    }

    @Override
    protected void onContentAdded() {
        needHeader(false);
        initStart();

    }

    void initXX() {
        mFuliveSaveBean = SharedPreferenceHelper.getBeautyFulive(mContext);

        //filter_name 滤镜名称
        faceunity.fuItemSetParam(mItemsArray[ITEM_ARRAYS_FACE_BEAUTY_INDEX], BeautificationParams.FILTER_NAME, mFuliveSaveBean.mFilterName);
        //filter_level 滤镜强度 范围0~1 SDK默认为 1
        faceunity.fuItemSetParam(mItemsArray[ITEM_ARRAYS_FACE_BEAUTY_INDEX], BeautificationParams.FILTER_LEVEL, mFuliveSaveBean.mFilterLevel);

        //skin_detect 精准美肤（肤色检测开关） 0:关闭 1:开启 SDK默认为 0
        faceunity.fuItemSetParam(mItemsArray[ITEM_ARRAYS_FACE_BEAUTY_INDEX], BeautificationParams.SKIN_DETECT, mFuliveSaveBean.mSkinDetect);

        //heavy_blur 磨皮类型 0:清晰磨皮 1:重度磨皮 SDK默认为 1
        faceunity.fuItemSetParam(mItemsArray[ITEM_ARRAYS_FACE_BEAUTY_INDEX], BeautificationParams.HEAVY_BLUR, mFuliveSaveBean.mHeavyBlur);
        //blur_level 磨皮 范围0~6 SDK默认为 6
        faceunity.fuItemSetParam(mItemsArray[ITEM_ARRAYS_FACE_BEAUTY_INDEX], BeautificationParams.BLUR_LEVEL, 6 * mFuliveSaveBean.mBlurLevel);


        //nonskin_blur_scale 肤色检测之后，非肤色区域的融合程度，范围0-1，SDK默认为0.45
//            faceunity.fuItemSetParam(mItemsArray[ITEM_ARRAYS_FACE_BEAUTY_INDEX], BeautificationParams.NONSKIN_BLUR_SCALE, 0);
        //color_level 美白 范围0~1 SDK默认为 0.2
        faceunity.fuItemSetParam(mItemsArray[ITEM_ARRAYS_FACE_BEAUTY_INDEX], BeautificationParams.COLOR_LEVEL, mFuliveSaveBean.mColorLevel);


        //red_level 红润 范围0~1 SDK默认为 0.5
        faceunity.fuItemSetParam(mItemsArray[ITEM_ARRAYS_FACE_BEAUTY_INDEX], BeautificationParams.RED_LEVEL, mFuliveSaveBean.mRedLevel);
        //eye_bright 亮眼 范围0~1 SDK默认为 0
        faceunity.fuItemSetParam(mItemsArray[ITEM_ARRAYS_FACE_BEAUTY_INDEX], BeautificationParams.EYE_BRIGHT, mFuliveSaveBean.mEyeBright);
        //tooth_whiten 美牙 范围0~1 SDK默认为 0
        faceunity.fuItemSetParam(mItemsArray[ITEM_ARRAYS_FACE_BEAUTY_INDEX], BeautificationParams.TOOTH_WHITEN, mFuliveSaveBean.mToothWhiten);

      /*  //face_shape_level 美型程度 范围0~1 SDK默认为1
        faceunity.fuItemSetParam(mItemsArray[ITEM_ARRAYS_FACE_BEAUTY_INDEX], BeautificationParams.FACE_SHAPE_LEVEL, mFaceShapeLevel);
        //face_shape 脸型 0：女神 1：网红，2：自然，3：默认，4：精细变形，5 用户自定义，SDK默认为 3
        faceunity.fuItemSetParam(mItemsArray[ITEM_ARRAYS_FACE_BEAUTY_INDEX], BeautificationParams.FACE_SHAPE, mFaceShape);
        //eye_enlarging 大眼 范围0~1 SDK默认为 0
        faceunity.fuItemSetParam(mItemsArray[ITEM_ARRAYS_FACE_BEAUTY_INDEX], BeautificationParams.EYE_ENLARGING, mEyeEnlarging);
        //cheek_thinning 瘦脸 范围0~1 SDK默认为 0
        faceunity.fuItemSetParam(mItemsArray[ITEM_ARRAYS_FACE_BEAUTY_INDEX], BeautificationParams.CHEEK_THINNING, mCheekThinning);
        //cheek_narrow 窄脸 范围0~1 SDK默认为 0
        faceunity.fuItemSetParam(mItemsArray[ITEM_ARRAYS_FACE_BEAUTY_INDEX], BeautificationParams.CHEEK_NARROW, mCheekNarrow);
        //cheek_small 小脸 范围0~1 SDK默认为 0
        faceunity.fuItemSetParam(mItemsArray[ITEM_ARRAYS_FACE_BEAUTY_INDEX], BeautificationParams.CHEEK_SMALL, mCheekSmall);
        //cheek_v V脸 范围0~1 SDK默认为 0
        faceunity.fuItemSetParam(mItemsArray[ITEM_ARRAYS_FACE_BEAUTY_INDEX], BeautificationParams.CHEEK_V, mCheekV);
        //intensity_nose 鼻子 范围0~1 SDK默认为 0
        faceunity.fuItemSetParam(mItemsArray[ITEM_ARRAYS_FACE_BEAUTY_INDEX], BeautificationParams.INTENSITY_NOSE, mIntensityNose);
        //intensity_chin 下巴 范围0~1 SDK默认为 0.5    大于0.5变大，小于0.5变小
        faceunity.fuItemSetParam(mItemsArray[ITEM_ARRAYS_FACE_BEAUTY_INDEX], BeautificationParams.INTENSITY_CHIN, mIntensityChin);
        //intensity_forehead 额头 范围0~1 SDK默认为 0.5    大于0.5变大，小于0.5变小
        faceunity.fuItemSetParam(mItemsArray[ITEM_ARRAYS_FACE_BEAUTY_INDEX], BeautificationParams.INTENSITY_FOREHEAD, mIntensityForehead);
        //intensity_mouth 嘴型 范围0~1 SDK默认为 0.5   大于0.5变大，小于0.5变小
        faceunity.fuItemSetParam(mItemsArray[ITEM_ARRAYS_FACE_BEAUTY_INDEX], BeautificationParams.INTENSITY_MOUTH, mIntensityMouth);
        //change_frame 变形渐变调整参数，0 渐变关闭，大于 0 渐变开启，值为渐变需要的帧数
        faceunity.fuItemSetParam(mItemsArray[ITEM_ARRAYS_FACE_BEAUTY_INDEX], BeautificationParams.CHANGE_FRAME, mChangeFrame);
        */


        mBoxSkinDetect.updateImg(false, false);
//        mBoxHeavyBlur.updateImg(false, false);
        mBoxBlurLevel.updateImg(false, false);
        mBoxColorLevel.updateImg(false, false);
        mBoxRedLevel.updateImg(false, false);
        mBoxEyeBright.updateImg(false, false);
        mBoxToothWhiten.updateImg(false, false);

        mBottomCheckGroup.setOnCheckedChangeListener(new CheckGroup.OnCheckedChangeListener() {
            int checkedidOld = View.NO_ID;

            @Override
            public void onCheckedChanged(CheckGroup group, int checkedId) {
                clickViewBottomRadio(checkedId);
                if ((checkedId == View.NO_ID || checkedId == checkedidOld) && checkedidOld != View.NO_ID) {
                    layoutBeautySeek.setVisibility(View.GONE);
                    beautyLine.setVisibility(View.GONE);
                    isShown = false;
                } else if (checkedId != View.NO_ID && checkedidOld == View.NO_ID) {
                    layoutBeautySeek.setVisibility(View.VISIBLE);
                    beautyLine.setVisibility(View.VISIBLE);
                    isShown = true;
                }
                checkedidOld = checkedId;
            }
        });

        mSkinBeautyBoxGroup.setOnCheckedChangeListener(new BeautyBoxGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(BeautyBoxGroup group, int checkedId) {
//                mFaceShapeLayout.setVisibility(GONE);
//                mBeautySeekBar.setVisibility(GONE);
                if (checkedId != R.id.beauty_box_skin_detect) {
                    seekToSeekBar(checkedId);
//                    onChangeFaceBeautyLevel(checkedId);
                    seekBarFuLive.setVisibility(View.VISIBLE);
                } else {
                    seekBarFuLive.setVisibility(View.INVISIBLE);
                }
            }
        });

        mBoxSkinDetect.setOnOpenChangeListener(new BeautyBox.OnOpenChangeListener() {
            @Override
            public void onOpenChanged(BeautyBox beautyBox, boolean isOpen) {
                mFuliveSaveBean.mSkinDetect = isOpen ? 1 : 0;
                ToastUtil.show(mFuliveSaveBean.mSkinDetect == 0 ? R.string.beauty_box_skin_detect_close : R.string.beauty_box_skin_detect_open);
//                onChangeFaceBeautyLevel(R.id.beauty_box_skin_detect);
                faceunity.fuItemSetParam(mItemsArray[ITEM_ARRAYS_FACE_BEAUTY_INDEX], BeautificationParams.SKIN_DETECT, mFuliveSaveBean.mSkinDetect);
                SharedPreferenceHelper.saveBeautyFulive(mContext, mFuliveSaveBean);
            }
        });
    }

    /**
     * 滤镜
     */
    private void initViewFilterRecycler() {
        mFilterRecyclerView.setLayoutManager(new LinearLayoutManager(mContext, LinearLayoutManager.HORIZONTAL, false));
        mFilterRecyclerView.setAdapter(mFilterRecyclerAdapter = new FilterRecyclerAdapter(SetBeautyActivity.this));
        ((SimpleItemAnimator) mFilterRecyclerView.getItemAnimator()).setSupportsChangeAnimations(false);

        mFilterRecyclerAdapter.setItems(FilterEnum.getFiltersByFilterType());
        for (int i = 0; i < mFilterRecyclerAdapter.getItemCount(); i++) {
            if (mFilterRecyclerAdapter.getItem(i) != null && mFilterRecyclerAdapter.getItem(i).filterName().equals(mFuliveSaveBean.mFilterName)) {
                mFilterRecyclerAdapter.setSelectPosition(i);
                break;
            }
        }

        mFilterRecyclerAdapter.setOnItemClickListener(new OnItemClickListener<Filter>() {
            @Override
            public void onItemClick(Filter bean, int position) {
                mFuliveSaveBean.mFilterName = bean.filterName();
                faceunity.fuItemSetParam(mItemsArray[ITEM_ARRAYS_FACE_BEAUTY_INDEX], BeautificationParams.FILTER_NAME, mFuliveSaveBean.mFilterName);
                SharedPreferenceHelper.saveBeautyFulive(mContext, mFuliveSaveBean);
            }
        });
    }

    private void seekToSeekBar(int checkedId) {
        switch (checkedId) {
            case R.id.beauty_box_heavy_blur:
                //清晰磨皮
                seekBarFuLive.setProgress((int) (mFuliveSaveBean.mBlurLevel * 100));
                break;
            case R.id.beauty_box_color_level:
                //美白
                seekBarFuLive.setProgress((int) (mFuliveSaveBean.mColorLevel * 100));
                break;
            case R.id.beauty_box_red_level:
                //红润
                seekBarFuLive.setProgress((int) (mFuliveSaveBean.mRedLevel * 100));
                break;
            case R.id.beauty_box_eye_bright:
                //亮眼
                seekBarFuLive.setProgress((int) (mFuliveSaveBean.mEyeBright * 100));
                break;
            case R.id.beauty_box_tooth_whiten:
                //美牙
                seekBarFuLive.setProgress((int) (mFuliveSaveBean.mToothWhiten * 100));
                break;
        }
    }

    private void clickViewBottomRadio(int viewId) {
        if (viewId == R.id.beauty_radio_skin_beauty) {
            mSkinBeautySelect.setVisibility(View.VISIBLE);
            mFilterRecyclerView.setVisibility(View.GONE);

            if (mSkinBeautyBoxGroup.getCheckedBeautyBoxId() != R.id.beauty_box_skin_detect) {
                seekBarFuLive.setVisibility(View.VISIBLE);
                seekToSeekBar(mSkinBeautyBoxGroup.getCheckedBeautyBoxId());
            } else {
                seekBarFuLive.setVisibility(View.INVISIBLE);
            }
        } else if (viewId == R.id.beauty_radio_filter) {
            mSkinBeautySelect.setVisibility(View.GONE);
            mFilterRecyclerView.setVisibility(View.VISIBLE);

            seekBarFuLive.setVisibility(View.VISIBLE);
            seekBarFuLive.setProgress((int) (mFuliveSaveBean.mFilterLevel * 100));
        }
    }

    /**
     * 初始化
     */
    private void initStart() {
        if (BuildConfig.beautySDK == 2) {
            int itemBeauty = FuliveUtil.loadItem(mContext, Constant.BUNDLE_FACE_BEAUTIFICATION);
            if (itemBeauty <= 0) {
                return;
            }
            mItemsArray[ITEM_ARRAYS_FACE_BEAUTY_INDEX] = itemBeauty;

            initXX();
            initViewFilterRecycler();
        }

        mTttRtcEngine = TTTRtcEngine.create(getApplicationContext(), BuildConfig.tttAppId, true,
                new TTTRtcEngineEventHandler() {
                    @Override
                    public void onError(int errorType) {
                        super.onError(errorType);
                        if (errorType == ERROR_ENTER_ROOM_TIMEOUT) {
                            LogUtil.i("超时，10秒未收到服务器返回结果");
                        } else if (errorType == ERROR_ENTER_ROOM_UNKNOW) {
                            LogUtil.i("无法连接服务器");
                        } else if (errorType == ERROR_ENTER_ROOM_VERIFY_FAILED) {
                            LogUtil.i("验证码错误");
                        } else if (errorType == ERROR_ENTER_ROOM_BAD_VERSION) {
                            LogUtil.i("版本错误");
                        } else if (errorType == 6) {
                            LogUtil.i("该直播间不存在");
                        }
                    }

                    @Override
                    public void onLocalVideoFrameCaptured(TTTVideoFrame frame) {
                        //
                        if (BuildConfig.beautySDK == 0) {
                            super.onLocalVideoFrameCaptured(frame);
                        }
                        if (BuildConfig.beautySDK == 2) {
                            frame.textureID = faceunity.fuRenderToTexture(frame.textureID, frame.stride, frame.height, mFrameId++, mItemsArray, 0);
                        }
//                        if (mTiSDKManager != null) {
//                            frame.textureID = mTiSDKManager.renderTexture2D(frame.textureID, frame.stride,
//                                    frame.height, TiRotation.CLOCKWISE_ROTATION_0, true);
//                        }
                    }

                });
        if (mTttRtcEngine != null) {
            mTttRtcEngine.setLogFilter(LOG_FILTER_OFF);
            mTttRtcEngine.enableVideo();
            mTttRtcEngine.setChannelProfile(CHANNEL_PROFILE_LIVE_BROADCASTING);
            mTttRtcEngine.setClientRole(CLIENT_ROLE_ANCHOR);
            mTttRtcEngine.startPreview();

            delayShow();
        }


        if (BuildConfig.beautySDK == 0) {
            btnBeautySetting.setVisibility(View.VISIBLE);
        } else if (BuildConfig.beautySDK == 1) {
            mTttRtcEngine.setBeautyFaceStatus(false, 0, 0);
            mTiSDKManager = new TiSDKManager();
            addContentView(new TiPanelLayout(this).init(mTiSDKManager),
                    new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                            ViewGroup.LayoutParams.MATCH_PARENT));
        } else {
            mTttRtcEngine.setBeautyFaceStatus(true, 0f, 0.5f);


        }

        if (BuildConfig.beautySDK == 0) {
            seekMeiBai.setProgress(SharedPreferenceHelper.getDefaultBrightLevel(SetBeautyActivity.this));
            seekMoPi.setProgress(SharedPreferenceHelper.getDefaultBeautyLevel(SetBeautyActivity.this));
            mDefaultBeautyLevel = SharedPreferenceHelper.getDefaultBeautyLevel(SetBeautyActivity.this) / 100f;
            mDefaultBrightLevel = SharedPreferenceHelper.getDefaultBrightLevel(SetBeautyActivity.this) / 100f;

            btnBeautySetting.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    btnBeautySetting.setVisibility(View.GONE);
                    layoutGroupBeauty.setVisibility(View.VISIBLE);
                }
            });

            seekMeiBai.setOnSeekChangeListener(new TextSeekBar.OnSeekChangeListener() {
                @Override
                public void onProgressChanged(View view, int progress) {
                    SharedPreferenceHelper.saveDefaultBrightLevel(SetBeautyActivity.this, progress);
                    mDefaultBrightLevel = seekMeiBai.getFloatProgress();
                    mTttRtcEngine.setBeautyFaceStatus(true, mDefaultBeautyLevel, mDefaultBrightLevel);

                }
            });

            seekMoPi.setOnSeekChangeListener(new TextSeekBar.OnSeekChangeListener() {
                @Override
                public void onProgressChanged(View view, int progress) {
                    SharedPreferenceHelper.saveDefaultBeautyLevel(SetBeautyActivity.this, progress);
                    mDefaultBeautyLevel = seekMoPi.getFloatProgress();
                    mTttRtcEngine.setBeautyFaceStatus(true, mDefaultBeautyLevel, mDefaultBrightLevel);

                }
            });

            mContentFl.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (layoutGroupBeauty.getVisibility() == View.VISIBLE) {
                        layoutGroupBeauty.setVisibility(View.GONE);
                        btnBeautySetting.setVisibility(View.VISIBLE);
                    }
                }
            });
        }


        if (BuildConfig.beautySDK == 2) {
            mBottomCheckGroup.setVisibility(View.VISIBLE);
            seekBarFuLive.setOnSeekChangeListener(new TextSeekBar.OnSeekChangeListener() {
                @Override
                public void onProgressChanged(View view, int progress) {
                    SharedPreferenceHelper.saveDefaultBrightLevel(SetBeautyActivity.this, progress);
                    float pro = seekBarFuLive.getFloatProgress();
                    if (mSkinBeautySelect.getVisibility() == View.VISIBLE) {
                        switch (mSkinBeautyBoxGroup.getCheckedBeautyBoxId()) {
                            case R.id.beauty_box_heavy_blur://磨皮
                                //heavy_blur 磨皮类型 0:清晰磨皮 1:重度磨皮 SDK默认为 1
                                mFuliveSaveBean.mBlurLevel = pro;
                                faceunity.fuItemSetParam(mItemsArray[ITEM_ARRAYS_FACE_BEAUTY_INDEX], BeautificationParams.HEAVY_BLUR, mFuliveSaveBean.mHeavyBlur);
                                //blur_level 磨皮 范围0~6 SDK默认为 6
                                faceunity.fuItemSetParam(mItemsArray[ITEM_ARRAYS_FACE_BEAUTY_INDEX], BeautificationParams.BLUR_LEVEL, 6 * mFuliveSaveBean.mBlurLevel);
                                break;
                            case R.id.beauty_box_color_level://美白
                                mFuliveSaveBean.mColorLevel = pro;
                                faceunity.fuItemSetParam(mItemsArray[ITEM_ARRAYS_FACE_BEAUTY_INDEX], BeautificationParams.COLOR_LEVEL, mFuliveSaveBean.mColorLevel);
                                break;
                            case R.id.beauty_box_red_level://红润
                                mFuliveSaveBean.mRedLevel = pro;
                                faceunity.fuItemSetParam(mItemsArray[ITEM_ARRAYS_FACE_BEAUTY_INDEX], BeautificationParams.RED_LEVEL, mFuliveSaveBean.mRedLevel);
                                break;
                            case R.id.beauty_box_eye_bright://亮眼
                                mFuliveSaveBean.mEyeBright = pro;
                                faceunity.fuItemSetParam(mItemsArray[ITEM_ARRAYS_FACE_BEAUTY_INDEX], BeautificationParams.EYE_BRIGHT, mFuliveSaveBean.mEyeBright);
                                break;
                            case R.id.beauty_box_tooth_whiten://美牙
                                mFuliveSaveBean.mToothWhiten = pro;
                                faceunity.fuItemSetParam(mItemsArray[ITEM_ARRAYS_FACE_BEAUTY_INDEX], BeautificationParams.TOOTH_WHITEN, mFuliveSaveBean.mToothWhiten);
                                break;
                        }
                    } else {
                        mFuliveSaveBean.mFilterLevel = pro;
                        faceunity.fuItemSetParam(mItemsArray[ITEM_ARRAYS_FACE_BEAUTY_INDEX], BeautificationParams.FILTER_LEVEL, mFuliveSaveBean.mFilterLevel);
                    }
                    SharedPreferenceHelper.saveBeautyFulive(mContext, mFuliveSaveBean);

                }
            });

            mContentFl.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (isShown) {
                        isShown = false;
                        layoutBeautySeek.setVisibility(View.GONE);
                        beautyLine.setVisibility(View.GONE);
                        mBottomCheckGroup.clearCheck();
                    }
                }
            });
        }
    }

    /**
     * 延时显示
     */
    private void delayShow() {
        getWindow().getDecorView().postDelayed(new Runnable() {
            @Override
            public void run() {
                try {
                    SurfaceView mLocalSurfaceView = mTttRtcEngine.CreateRendererView(SetBeautyActivity.this);
                    mTttRtcEngine.setupLocalVideo(new VideoCanvas(Integer.parseInt(getUserId()),
                            Constants.RENDER_MODE_HIDDEN, mLocalSurfaceView), getRequestedOrientation());
                    mContentFl.addView(mLocalSurfaceView);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }, 10);
    }


    @OnClick({R.id.finish_iv, R.id.switch_iv})
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.finish_iv: {//关闭
                if (mTttRtcEngine != null) {
                    mTttRtcEngine.leaveChannel();
                    mTttRtcEngine = null;
                }
                finish();
                break;
            }
            case R.id.switch_iv: {//切换
                if (mTttRtcEngine != null) {
                    mTttRtcEngine.switchCamera();
                }
                break;
            }
        }
    }


    @Override
    protected void onDestroy() {
        if (mTiSDKManager != null) {
            mTiSDKManager.destroy();
        }
        faceunity.fuDestroyAllItems();
        faceunity.fuOnDeviceLost();
        faceunity.fuDone();
        super.onDestroy();
    }

}
