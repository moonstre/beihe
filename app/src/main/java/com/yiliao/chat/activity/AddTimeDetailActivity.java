package com.yiliao.chat.activity;

import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.yiliao.chat.R;
import com.yiliao.chat.adapter.AddTimeDetailAdapter;
import com.yiliao.chat.base.BaseActivity;
import com.yiliao.chat.bean.StartServiceBean;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;

public class AddTimeDetailActivity extends BaseActivity {
    @BindView(R.id.content_rv)
    RecyclerView content_rv;
    AddTimeDetailAdapter adapter;
    List<StartServiceBean.continueClockList> lists=new ArrayList<>();
    @Override
    protected View getContentView() {
        return inflate(R.layout.add_time_server_value_layout);
    }

    @Override
    protected void onContentAdded() {
        setTitle("加钟详情");
        lists= (List<StartServiceBean.continueClockList>) getIntent().getSerializableExtra("lists");
        LinearLayoutManager gridLayoutManager = new LinearLayoutManager(this);
        content_rv.setLayoutManager(gridLayoutManager);
        adapter=new AddTimeDetailAdapter(this);
        content_rv.setAdapter(adapter);
        adapter.loadData(lists);
    }
}
