package com.yiliao.chat.fulive;

import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SimpleItemAnimator;

import com.yiliao.chat.R;
import com.yiliao.chat.activity.SetBeautyActivity;
import com.yiliao.chat.fulive.adapter.FaceMakeupAdapter;
import com.yiliao.chat.fulive.entity.FaceMakeupEnum;

public class MakeUp {
//    <android.support.v7.widget.RecyclerView
//    android:id="@+id/rv_face_makeup"
//    android:layout_width="match_parent"
//    android:layout_height="wrap_content"
//    android:overScrollMode="never"
//    android:scrollbars="none"
//    android:visibility="visible" />

// private static final int ITEM_ARRAYS_LIGHT_MAKEUP_INDEX = 2;
//    private RecyclerView mRvMakeupItems;
//    private FaceMakeupAdapter mFaceMakeupAdapter;
//    // 轻美妆妆容集合
//    private Map<Integer, MakeupItem> mLightMakeupItemMap = new ConcurrentHashMap<>(64);
//    //用于和异步加载道具的线程交互
//    private HandlerThread mFuItemHandlerThread;
//    private Handler mFuItemHandler;
//    private List<Runnable> mEventQueue;
//    private double[] mLipStickColor;


//    mEventQueue = Collections.synchronizedList(new ArrayList<Runnable>());
//    mFuItemHandlerThread = new HandlerThread("FUItemHandlerThread");
//        mFuItemHandlerThread.start();
//    mFuItemHandler = new FUItemHandler(mFuItemHandlerThread.getLooper());


//    /**
//     * 质感美颜
//     */
//    private void initMakeupView() {
//        mRvMakeupItems = findViewById(R.id.rv_face_makeup);
//        mRvMakeupItems.setHasFixedSize(true);
//        ((SimpleItemAnimator) mRvMakeupItems.getItemAnimator()).setSupportsChangeAnimations(false);
//        mRvMakeupItems.setLayoutManager(new LinearLayoutManager(mContext, LinearLayoutManager.HORIZONTAL, false));
//        mFaceMakeupAdapter = new FaceMakeupAdapter(SetBeautyActivity.this, FaceMakeupEnum.getBeautyFaceMakeup());
//        SetBeautyActivity.OnFaceMakeupClickListener onMpItemClickListener = new SetBeautyActivity.OnFaceMakeupClickListener();
//        mFaceMakeupAdapter.setOnItemClickListener(onMpItemClickListener);
//        mRvMakeupItems.setAdapter(mFaceMakeupAdapter);
//        mFaceMakeupAdapter.setItemSelected(0);
//    }



//    // 美妆列表点击事件
//    private class OnFaceMakeupClickListener implements BaseRecyclerAdapter.OnItemClickListener<FaceMakeup> {
//
//        @Override
//        public void onItemClick(BaseRecyclerAdapter<FaceMakeup> adapter, View view, int position) {
//            FaceMakeup faceMakeup = adapter.getItem(position);
//            if (position == 0) {
//                // 卸妆
////                mBeautySeekBar.setVisibility(View.INVISIBLE);
////                int old = mFilterPositionSelect;
////                mFilterPositionSelect = -1;
////                mFilterRecyclerAdapter.notifyItemChanged(old);
//                onLightMakeupBatchSelected(faceMakeup.getMakeupItems());
//            } else {
//                // 效果妆容
////                mBeautySeekBar.setVisibility(View.VISIBLE);
////                String name = getResources().getString(faceMakeup.getNameId());
////                Float level = BeautyParameterModel.sBatchMakeupLevel.get(name);
////                boolean used = true;
////                if (level == null) {
////                    used = false;
////                    level = FaceMakeupEnum.MAKEUP_OVERALL_LEVEL.get(faceMakeup.getNameId());
////                    BeautyParameterModel.sBatchMakeupLevel.put(name, level);
////                }
////                seekToSeekBar(level);
//                onLightMakeupBatchSelected(faceMakeup.getMakeupItems());
////                mOnFUControlListener.onLightMakeupOverallLevelChanged(level);
//
////                Pair<Filter, Float> filterFloatPair = FaceMakeupEnum.MAKEUP_FILTERS.get(faceMakeup.getNameId());
////                if (filterFloatPair != null) {
////                    // 滤镜调整到对应的位置，没有就不做
////                    Filter filter = filterFloatPair.first;
////                    int i = mFilterRecyclerAdapter.indexOf(filter);
////                    if (i >= 0) {
////                        mFilterPositionSelect = i;
////                        mFilterRecyclerAdapter.notifyItemChanged(i);
////                        mFilterRecyclerView.scrollToPosition(i);
////                    } else {
////                        int old = mFilterPositionSelect;
////                        mFilterPositionSelect = -1;
////                        mFilterRecyclerAdapter.notifyItemChanged(old);
////                    }
////                    mOnFUControlListener.onFilterNameSelected(filter.filterName());
////                    Float filterLevel = used ? level : filterFloatPair.second;
////                    sFilter = filter;
////                    String filterName = filter.filterName();
////                    sFilterLevel.put(STR_FILTER_LEVEL + filterName, filterLevel);
////                    setFilterLevel(filterName, filterLevel);
////                }
//            }
//        }
//    }
//
//    private void onLightMakeupBatchSelected(List<MakeupItem> makeupItems) {
//        Set<Integer> keySet = mLightMakeupItemMap.keySet();
//        for (final Integer integer : keySet) {
//            queueEvent(new Runnable() {
//                @Override
//                public void run() {
//                    faceunity.fuItemSetParam(mItemsArray[ITEM_ARRAYS_LIGHT_MAKEUP_INDEX], getMakeupIntensityKeyByType(integer), 0);
//                }
//            });
//        }
//        mLightMakeupItemMap.clear();
//
//        if (makeupItems != null && makeupItems.size() > 0) {
//            for (int i = 0, size = makeupItems.size(); i < size; i++) {
//                MakeupItem makeupItem = makeupItems.get(i);
//                onLightMakeupSelected(makeupItem, makeupItem.getLevel());
//            }
//        } else {
//            queueEvent(new Runnable() {
//                @Override
//                public void run() {
//                    faceunity.fuItemSetParam(mItemsArray[ITEM_ARRAYS_LIGHT_MAKEUP_INDEX], "is_makeup_on", 0);
//                }
//            });
//        }
//    }
//
//    private void onLightMakeupSelected(final MakeupItem makeupItem, final float level) {
//        int type = makeupItem.getType();
//        MakeupItem mp = mLightMakeupItemMap.get(type);
//        if (mp != null) {
//            mp.setLevel(level);
//        } else {
//            // 复制一份
//            mLightMakeupItemMap.put(type, makeupItem.cloneSelf());
//        }
//        if (mFuItemHandler == null) {
//            queueEvent(new Runnable() {
//                @Override
//                public void run() {
//                    mFuItemHandler.sendMessage(Message.obtain(mFuItemHandler, ITEM_ARRAYS_LIGHT_MAKEUP_INDEX, makeupItem));
//                }
//            });
//        } else {
//            mFuItemHandler.sendMessage(Message.obtain(mFuItemHandler, ITEM_ARRAYS_LIGHT_MAKEUP_INDEX, makeupItem));
//        }
//    }
//
//    private String getMakeupIntensityKeyByType(int type) {
//        switch (type) {
//            case FaceMakeup.FACE_MAKEUP_TYPE_LIPSTICK:
//                return "makeup_intensity_lip";
//            case FaceMakeup.FACE_MAKEUP_TYPE_EYE_LINER:
//                return "makeup_intensity_eyeLiner";
//            case FaceMakeup.FACE_MAKEUP_TYPE_BLUSHER:
//                return "makeup_intensity_blusher";
//            case FaceMakeup.FACE_MAKEUP_TYPE_EYE_PUPIL:
//                return "makeup_intensity_pupil";
//            case FaceMakeup.FACE_MAKEUP_TYPE_EYEBROW:
//                return "makeup_intensity_eyeBrow";
//            case FaceMakeup.FACE_MAKEUP_TYPE_EYE_SHADOW:
//                return "makeup_intensity_eye";
//            case FaceMakeup.FACE_MAKEUP_TYPE_EYELASH:
//                return "makeup_intensity_eyelash";
//            default:
//                return "";
//        }
//    }
//
//    /**
//     * 类似GLSurfaceView的queueEvent机制
//     */
//    public void queueEvent(Runnable r) {
//        if (mEventQueue == null)
//            return;
//        mEventQueue.add(r);
//    }
//
//    /**
//     * 类似GLSurfaceView的queueEvent机制,保护在快速切换界面时进行的操作是当前界面的加载操作
//     */
//    private void queueEventItemHandle(Runnable r) {
//        if (mFuItemHandlerThread == null || Thread.currentThread().getId() != mFuItemHandlerThread.getId())
//            return;
//        queueEvent(r);
//    }
//
//    /**
//     * 从 assets 中读取颜色数据
//     *
//     * @param colorAssetPath
//     * @return rgba 数组
//     */
//    private double[] readMakeupLipColors(String colorAssetPath) throws IOException, JSONException {
//        if (TextUtils.isEmpty(colorAssetPath)) {
//            return null;
//        }
//        InputStream is = null;
//        try {
//            is = mContext.getAssets().open(colorAssetPath);
//            byte[] bytes = new byte[is.available()];
//            is.read(bytes);
//            String s = new String(bytes);
//            JSONObject jsonObject = new JSONObject(s);
//            JSONArray jsonArray = jsonObject.optJSONArray("rgba");
//            double[] colors = new double[4];
//            for (int i = 0, length = jsonArray.length(); i < length; i++) {
//                colors[i] = jsonArray.optDouble(i);
//            }
//            return colors;
//        } finally {
//            if (is != null) {
//                is.close();
//            }
//        }
//    }
//
//    /**
//     * 加载美妆资源数据
//     *
//     * @param path
//     * @return bytes, width and height
//     * @throws IOException
//     */
//    private Pair<byte[], Pair<Integer, Integer>> loadMakeupResource(String path) throws IOException {
//        if (TextUtils.isEmpty(path)) {
//            return null;
//        }
//        InputStream is = null;
//        try {
//            is = mContext.getAssets().open(path);
//            BitmapFactory.Options options = new BitmapFactory.Options();
//            Bitmap bitmap = BitmapFactory.decodeStream(is, null, options);
//            int bmpByteCount = bitmap.getByteCount();
//            int width = bitmap.getWidth();
//            int height = bitmap.getHeight();
//            byte[] bitmapBytes = new byte[bmpByteCount];
//            ByteBuffer byteBuffer = ByteBuffer.wrap(bitmapBytes);
//            bitmap.copyPixelsToBuffer(byteBuffer);
//            return Pair.create(bitmapBytes, Pair.create(width, height));
//        } finally {
//            if (is != null) {
//                is.close();
//            }
//        }
//    }
//
//    private String getFaceMakeupKeyByType(int type) {
//        switch (type) {
//            case FaceMakeup.FACE_MAKEUP_TYPE_LIPSTICK:
//                return "tex_lip";
//            case FaceMakeup.FACE_MAKEUP_TYPE_EYE_LINER:
//                return "tex_eyeLiner";
//            case FaceMakeup.FACE_MAKEUP_TYPE_BLUSHER:
//                return "tex_blusher";
//            case FaceMakeup.FACE_MAKEUP_TYPE_EYE_PUPIL:
//                return "tex_pupil";
//            case FaceMakeup.FACE_MAKEUP_TYPE_EYEBROW:
//                return "tex_brow";
//            case FaceMakeup.FACE_MAKEUP_TYPE_EYE_SHADOW:
//                return "tex_eye";
//            case FaceMakeup.FACE_MAKEUP_TYPE_EYELASH:
//                return "tex_eyeLash";
//            default:
//                return "";
//        }
//    }
//
//    class FUItemHandler extends Handler {
//
//        FUItemHandler(Looper looper) {
//            super(looper);
//        }
//
//        @Override
//        public void handleMessage(Message msg) {
//            super.handleMessage(msg);
//            switch (msg.what) {
//                //加载美颜bundle
//                case ITEM_ARRAYS_FACE_BEAUTY_INDEX: {
//                    final int itemBeauty = loadItem(Constant.BUNDLE_FACE_BEAUTIFICATION);
//                    if (itemBeauty <= 0) {
//                        return;
//                    }
//                    queueEventItemHandle(new Runnable() {
//                        @Override
//                        public void run() {
//                            mItemsArray[ITEM_ARRAYS_FACE_BEAUTY_INDEX] = itemBeauty;
////                            isNeedUpdateFaceBeauty = true;
//                        }
//                    });
//                }
//                break;
//                // 加载轻美妆bundle
//                case ITEM_ARRAYS_LIGHT_MAKEUP_INDEX: {
//                    final MakeupItem makeupItem = (MakeupItem) msg.obj;
//                    if (makeupItem == null) {
//                        return;
//                    }
//                    String path = makeupItem.getPath();
//                    if (!TextUtils.isEmpty(path)) {
//                        if (mItemsArray[ITEM_ARRAYS_LIGHT_MAKEUP_INDEX] <= 0) {
//                            int itemLightMakeup = loadItem(Constant.BUNDLE_LIGHT_MAKEUP);
//                            if (itemLightMakeup <= 0) {
//                                return;
//                            }
//                            mItemsArray[ITEM_ARRAYS_LIGHT_MAKEUP_INDEX] = itemLightMakeup;
//                        }
//                        final int itemHandle = mItemsArray[ITEM_ARRAYS_LIGHT_MAKEUP_INDEX];
//                        try {
//                            byte[] itemBytes = null;
//                            int width = 0;
//                            int height = 0;
//                            if (makeupItem.getType() == FaceMakeup.FACE_MAKEUP_TYPE_LIPSTICK) {
//                                mLipStickColor = readMakeupLipColors(path);
//                            } else {
//                                Pair<byte[], Pair<Integer, Integer>> pair = loadMakeupResource(path);
//                                itemBytes = pair.first;
//                                width = pair.second.first;
//                                height = pair.second.second;
//                            }
//                            final byte[] makeupItemBytes = itemBytes;
//                            final int finalHeight = height;
//                            final int finalWidth = width;
//                            queueEventItemHandle(new Runnable() {
//                                @Override
//                                public void run() {
//                                    String key = getFaceMakeupKeyByType(makeupItem.getType());
//                                    faceunity.fuItemSetParam(itemHandle, "is_makeup_on", 1);
//                                    faceunity.fuItemSetParam(itemHandle, "makeup_intensity", 1);
//                                    faceunity.fuItemSetParam(itemHandle, "reverse_alpha", 1);
//                                    if (mLipStickColor != null) {
//                                        if (makeupItem.getType() == FaceMakeup.FACE_MAKEUP_TYPE_LIPSTICK) {
//                                            faceunity.fuItemSetParam(itemHandle, "makeup_lip_color", mLipStickColor);
//                                            faceunity.fuItemSetParam(itemHandle, "makeup_lip_mask", 1);
//                                        }
//                                    } else {
//                                        faceunity.fuItemSetParam(itemHandle, "makeup_intensity_lip", 0);
//                                    }
//                                    if (makeupItemBytes != null) {
//                                        faceunity.fuCreateTexForItem(itemHandle, key, makeupItemBytes, finalWidth, finalHeight);
//                                    }
//                                    faceunity.fuItemSetParam(itemHandle, getMakeupIntensityKeyByType(
//                                            makeupItem.getType()), makeupItem.getLevel());
//                                }
//                            });
//                        } catch (IOException | JSONException e) {
//
//                        }
//                    } else {
//                        // 卸某个妆
//                        if (mItemsArray[ITEM_ARRAYS_LIGHT_MAKEUP_INDEX] > 0) {
//                            queueEventItemHandle(new Runnable() {
//                                @Override
//                                public void run() {
//                                    faceunity.fuItemSetParam(mItemsArray[ITEM_ARRAYS_LIGHT_MAKEUP_INDEX],
//                                            getMakeupIntensityKeyByType(makeupItem.getType()), 0);
//                                }
//                            });
//                        }
//                    }
//                }
//                break;
//                default:
//            }
//        }
//    }
}
