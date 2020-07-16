package com.yiliao.chat.activity;

import android.content.Intent;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.yiliao.chat.R;
import com.yiliao.chat.base.BaseActivity;
import com.yiliao.chat.base.BaseResponse;
import com.yiliao.chat.bean.OpinionResultDetailBean;
import com.yiliao.chat.constant.ChatApi;
import com.yiliao.chat.constant.Constant;
import com.yiliao.chat.helper.ImageLoadHelper;
import com.yiliao.chat.net.AjaxCallback;
import com.yiliao.chat.net.NetCode;
import com.yiliao.chat.util.DevicesUtil;
import com.yiliao.chat.util.ParamUtil;
import com.zhy.http.okhttp.OkHttpUtils;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import okhttp3.Call;

public class OpinionResultDetailActivity extends BaseActivity {
    @BindView(R.id.tvContent)
    TextView tvContent;
    @BindView(R.id.evidence_ll)
    LinearLayout mEvidenceLl;
    @BindView(R.id.layoutResult)
    LinearLayout layoutResult;
    @BindView(R.id.tvRes)
    TextView tvRes;
    @BindView(R.id.img_ll)
    LinearLayout img_ll;

    @Override
    protected View getContentView() {
        return inflate(R.layout.activity_opinion_result_detail_layout);
    }

    @Override
    protected void onContentAdded() {
        setTitle(R.string.opinion_feed_back_result_detail);

        int t_id = getIntent().getIntExtra(Constant.ACTOR_ID, 0);

        getFeedBackById(t_id);
    }

    private void getFeedBackById(int t_id) {
        Map<String, String> paramMap = new HashMap<>();
        paramMap.put("userId", getUserId());
        paramMap.put("feedBackId", String.valueOf(t_id));
        OkHttpUtils.post().url(ChatApi.GET_FEED_BACK_DETAIL)
                .addParams("param", ParamUtil.getParam(paramMap))
                .build().execute(new AjaxCallback<BaseResponse<OpinionResultDetailBean>>() {
            @Override
            public void onResponse(BaseResponse<OpinionResultDetailBean> response, int id) {
                if (response != null && response.m_istatus == NetCode.SUCCESS) {
                    OpinionResultDetailBean bean = response.m_object;
                    if (bean != null) {
                        tvContent.setText(bean.t_content);
                        if (!TextUtils.isEmpty(bean.t_img_url)) {
                            List<String> urlList = Arrays.asList(bean.t_img_url.split(","));
                            for (String url : urlList) {
                                addImageToLinearLayout(mEvidenceLl, url);
                            }
                        }
                        if ("1".equals(bean.t_is_handle)) {
                            layoutResult.setVisibility(View.VISIBLE);
                            tvRes.setText(bean.t_handle_res);
                            if (!TextUtils.isEmpty(bean.t_handle_img)) {
                                List<String> urlList = Arrays.asList(bean.t_handle_img.split(","));
                                for (String url : urlList) {
                                    addImageToLinearLayout(img_ll, url);
                                }
                            }
                        } else {
                            layoutResult.setVisibility(View.GONE);
                        }
                    }
                }
            }

            @Override
            public void onError(Call call, Exception e, int id) {
                super.onError(call, e, id);
            }
        });
    }

    private void addImageToLinearLayout(LinearLayout layout,final String url) {
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(DevicesUtil.dp2px(getApplicationContext(), 50), DevicesUtil.dp2px(getApplicationContext(), 50));
        layoutParams.leftMargin = 5;
        layoutParams.rightMargin = 5;
        ImageView imageView = new ImageView(this);
        imageView.setLayoutParams(layoutParams);
        ImageLoadHelper.glideShowImageWithUrl(mContext, url, imageView);
        layout.addView(imageView);
        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mContext, PhotoActivity.class);
                intent.putExtra(Constant.IMAGE_URL, url);
                mContext.startActivity(intent);
            }
        });
    }
}
