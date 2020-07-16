package com.yiliao.chat.myactivity;

import android.content.Intent;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.OrientationHelper;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.TextView;

import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationClient;
import com.amap.api.location.AMapLocationClientOption;
import com.amap.api.services.core.AMapException;
import com.amap.api.services.core.LatLonPoint;
import com.amap.api.services.core.PoiItem;
import com.amap.api.services.poisearch.PoiResult;
import com.amap.api.services.poisearch.PoiSearch;
import com.yiliao.chat.R;
import com.yiliao.chat.base.BaseActivity;
import com.yiliao.chat.helper.SharedPreferenceHelper;
import com.yiliao.chat.myactivity.adpate.AddLocationAdpater;
import com.yiliao.chat.myactivity.bean.LocationBean;
import com.yiliao.chat.util.SystemUtil;
import com.yiliao.chat.util.ToastUtil;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;

public class AddLocationActivity extends BaseActivity implements PoiSearch.OnPoiSearchListener {


    private List<LocationBean> mList = new ArrayList<>();

    @BindView(R.id.AddLocation)
    RecyclerView AddLocation;
    AddLocationAdpater adpater;
    PoiSearch.Query query;
    private String keyWord;

    @BindView(R.id.mEt_keyword)
    EditText mEt_keyword;

    @Override
    protected View getContentView() {
        return inflate(R.layout.addlocation);
    }

    @Override
    protected void onContentAdded() {

        setTitle("添加位置");

        LinearLayoutManager manager = new LinearLayoutManager(mContext);
        manager.setOrientation(OrientationHelper.VERTICAL);
        AddLocation.setLayoutManager(manager);




        mEt_keyword.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_SEARCH){//搜索按键action
                    doSearchQuery(v.getText().toString());
                    return true;
                }
                return false;
            }
        });
        doSearchQuery("");
    }





    private void doSearchQuery(String keyWord) {
        Log.i("aaa", "aaa" + SharedPreferenceHelper.getCity(mContext));
        query = new PoiSearch.Query(keyWord, "", "武汉");
        query.setPageSize(20);
        query.setPageNum(0);
        PoiSearch poiSearch = new PoiSearch(getApplicationContext(), query);
        poiSearch.setBound(new PoiSearch.SearchBound(new LatLonPoint(Double.parseDouble(SharedPreferenceHelper.getCodeLat(mContext)),
               Double.parseDouble( SharedPreferenceHelper.getCodeLng(mContext))), 2000));//设置周边搜索的中心点以及半径
        poiSearch.setOnPoiSearchListener(this);
        poiSearch.searchPOIAsyn();
    }


    private PoiResult poiResult; // poi返回的结果

    @Override
    public void onPoiSearched(PoiResult result, int error) {

        if (error == AMapException.CODE_AMAP_SUCCESS) {
            if (result != null && result.getQuery() != null) {  // 搜索poi的结果
                if (result.getQuery().equals(query)) {  // 是否是同一条
                    poiResult = result;
                    mList.clear();
                    // 取得搜索到的poiitems有多少页
                    List<PoiItem> pois = poiResult.getPois();// 取得第一页的poiitem数据，页数从数字0开始
                    for (int i = 0; i < pois.size(); i++) {
                        LocationBean locationBean = new LocationBean();
                        locationBean.Title = pois.get(i).getTitle();
                        locationBean.Address = pois.get(i).getSnippet();
                        locationBean.lat = pois.get(i).getLatLonPoint().getLatitude();
                        locationBean.lna = pois.get(i).getLatLonPoint().getLongitude();
                        mList.add(locationBean);
                    }

                    adpater = new AddLocationAdpater(mContext, mList);
                    AddLocation.setAdapter(adpater);
                    adpater.setItmeOnClack(new AddLocationAdpater.AddLoctionItmeCallBack() {
                        @Override
                        public void OnClcik(int position) {

                            Intent intent = new Intent();
                            //putExtra方法将数据以键值对的方式储存起来，第一个参数是“健”，第二个就是“值”。
                            //这里将回传数据作为值传进去。
                            intent.putExtra("ADDRESS",mList.get(position).Address);
                            intent.putExtra("LAT",String.valueOf(mList.get(position).lat));
                            intent.putExtra("LNA",String.valueOf(mList.get(position).lna));

                            //在这里将resultCode定义为2，并将intent作为内容传进去
                            setResult(20000,intent);
                            finish();
                        }
                    });
                }

            }
        }


    }

    @Override
    public void onPoiItemSearched(PoiItem poiItem, int i) {

    }


}
