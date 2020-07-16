package com.yiliao.chat.fulive;

import android.content.Context;
import android.text.TextUtils;
import android.util.Log;

import com.faceunity.wrapper.faceunity;

import java.io.IOException;
import java.io.InputStream;

public class FuliveUtil {
    /**
     * fuCreateItemFromPackage 加载道具
     *
     * @param bundlePath 道具 bundle 的路径
     * @return 大于 0 时加载成功
     */
    public static int loadItem(Context context, String bundlePath) {
        int item = 0;
        try {
            if (!TextUtils.isEmpty(bundlePath)) {
                InputStream is = context.getAssets().open(bundlePath);
                byte[] itemData = new byte[is.available()];
                int len = is.read(itemData);
                is.close();
                item = faceunity.fuCreateItemFromPackage(itemData);
                Log.e("!!!", "bundle path: " + bundlePath + ", length: " + len + "Byte, handle:" + item);
            }
        } catch (IOException e) {
            Log.e("!!!", "loadItem error ", e);
        }
        return item;
    }
}
