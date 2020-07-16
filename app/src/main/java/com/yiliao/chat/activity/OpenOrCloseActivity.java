package com.yiliao.chat.activity;

import android.app.Dialog;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.tencent.cos.xml.utils.StringUtils;
import com.yiliao.chat.R;
import com.yiliao.chat.adapter.LabelRecyclerAdapter;
import com.yiliao.chat.adapter.OpenOrCloseAdapter;
import com.yiliao.chat.base.BaseActivity;
import com.yiliao.chat.base.BaseBean;
import com.yiliao.chat.base.BaseListResponse;
import com.yiliao.chat.base.BaseResponse;
import com.yiliao.chat.bean.AddTimeBean;
import com.yiliao.chat.bean.CloseServiceBean;
import com.yiliao.chat.bean.LabelBean;
import com.yiliao.chat.bean.OrderSortBean;
import com.yiliao.chat.constant.ChatApi;
import com.yiliao.chat.net.AjaxCallback;
import com.yiliao.chat.net.NetCode;
import com.yiliao.chat.util.DataUtil;
import com.yiliao.chat.util.ParamUtil;
import com.yiliao.chat.util.ToastUtil;
import com.zhy.http.okhttp.OkHttpUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import butterknife.BindView;
import okhttp3.Call;

/**
 *
 * 可上门开关
 * */
public class OpenOrCloseActivity extends BaseActivity {
    @BindView(R.id.content_rv)
    RecyclerView content_rv;
//    @BindView(R.id.open_or_close)
//    TextView open_or_close;
    @BindView(R.id.sure_tv)
    TextView sure_tv;

    OpenOrCloseAdapter adapter;
    //已选标签列表
    private List<LabelBean> mSelectedLabels = new ArrayList<>();
    //初始化数据
    OrderSortBean list;
    List<LabelBean> beans_list=new ArrayList<>();
    String serverContent;
    String t_visit;
    String price;
    int type=0;
    @Override
    protected View getContentView() {
        return inflate(R.layout.open_or_close);
    }

    @Override
    protected void onContentAdded() {
        setTitle("上门服务");
        LinearLayoutManager gridLayoutManager = new LinearLayoutManager(getBaseContext());
        content_rv.setLayoutManager(gridLayoutManager);
        adapter=new OpenOrCloseAdapter(this);
        content_rv.setAdapter(adapter);
        adapter.setOnItemClickListener(new OpenOrCloseAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(List<LabelBean> labelBeans) {
                beans_list.clear();
                beans_list.addAll(labelBeans);
            }
        });
        sure_tv.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public void onClick(View v) {
                if (type==1){
                    CheckOpenStates(type,"");
                }else {
                    if (beans_list.size()<=4){
                        List<String> list=new ArrayList<>();
                        for (int i=0;i<beans_list.size();i++){
                            list.add(beans_list.get(i).t_id+"");
                        }
                        serverContent=String.join(",",list);
                        if (type==0){
                            CheckOpenStates(type,serverContent);
                        }
                    }else{
                        ToastUtil.show("最多选择4项服务内容");
                    }
                }

            }
        });
         t_visit=getIntent().getStringExtra("t_visit");
         getLabelList();

    }

    private void CheckOpenStates(int visit,String serverContent){
        Map<String, String> paramMap = new HashMap<>();
        paramMap.put("userId", getUserId());
        paramMap.put("visit",String.valueOf(visit));
        paramMap.put("serverContent",serverContent);
        OkHttpUtils.post().url(ChatApi.UPDATE_USER_VISIT)
                .addParams("param", ParamUtil.getParam(paramMap))
                .build().execute(new AjaxCallback<BaseResponse>() {
            @Override
            public void onResponse(BaseResponse response, int id) {
                if (response != null && response.m_istatus == NetCode.SUCCESS) {
                    int code=response.m_istatus;
                    if (code==0){
                        ToastUtil.showToast(getApplicationContext(), R.string.check_error);
                    }else {
                        ToastUtil.showToast(getApplicationContext(), response.m_strMessage);
                        finish();
                    }
                }else {
                    ToastUtil.showToast(getApplicationContext(), "执行失败");
                }
            }

            @Override
            public void onError(Call call, Exception e, int id) {
                super.onError(call, e, id);
                ToastUtil.showToast(getApplicationContext(), "服务异常");
            }
        });
    }
    /**
     * 获取标签列表
     */
    private void getLabelList() {
        Map<String, String> paramMap = new HashMap<>();
        paramMap.put("userId", getUserId());
        paramMap.put("anchorId",getUserId());
        OkHttpUtils.post().url(ChatApi.QUERY_SERVICE_PRICE)
                .addParams("param", ParamUtil.getParam(paramMap))
                .build().execute(new AjaxCallback<BaseListResponse<LabelBean>>() {
            @Override
            public void onResponse(BaseListResponse<LabelBean> response, int id) {
                if (response != null && response.m_istatus == NetCode.SUCCESS) {
                    List<LabelBean> beans = response.m_object;
                    if (beans != null && beans.size() > 0) {
                        for (int i=0;i<beans.size();i++){
                            if (beans.get(i).status!=1){
                                type=1;
                                sure_tv.setText("关闭服务");
                            }
                        }
                        adapter.loadData(beans,type);
                    }
                }
            }

            @Override
            public void onError(Call call, Exception e, int id) {
                super.onError(call, e, id);
                ToastUtil.show("服务异常！");
            }
        });
    }

}
