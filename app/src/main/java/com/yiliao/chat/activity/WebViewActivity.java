package com.yiliao.chat.activity;

import android.content.Intent;
import android.net.Uri;
import android.os.Handler;
import android.view.View;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.yiliao.chat.R;
import com.yiliao.chat.base.BaseActivity;

import butterknife.BindView;

public class WebViewActivity extends BaseActivity {

    @BindView(R.id.webView)
    WebView mWebView;

    Handler mHandler=new Handler();

//    @Override
//    public int getLayoutId() {
//        return R.layout.activity_web_view;
//    }

//    @Override
//    public void initView() {
//        mTitleView.setTitle(getIntent().getStringExtra("title"));
//        WebSettings webSettings = mWebView.getSettings();
//        /**
//         * LOAD_CACHE_ONLY: 不使用网络，只读取本地缓存数据
//         * LOAD_DEFAULT: （默认）根据cache-control决定是否从网络上取数据。
//         * LOAD_NO_CACHE: 不使用缓存，只从网络获取数据.
//         * LOAD_CACHE_ELSE_NETWORK，只要本地有，无论是否过期，或者no-cache，都使用缓存中的数据。
//         */
//        webSettings.setAppCacheEnabled(true);
//        webSettings.setDomStorageEnabled(true);//主要是这句
//        webSettings.setAllowContentAccess(true);
//        webSettings.setBlockNetworkImage(false);//解决图片不显示
//        webSettings.setAllowFileAccess(true);
//        webSettings.setJavaScriptEnabled(true);
//        webSettings.setJavaScriptCanOpenWindowsAutomatically(true);
//        webSettings.setLoadsImagesAutomatically(true);
//        webSettings.setCacheMode(WebSettings.LOAD_NO_CACHE);//不使用缓存，只从网络获取数据.
//        //支持屏幕缩放
//        webSettings.setSupportZoom(true);
//        webSettings.setBuiltInZoomControls(true);
//        //不显示webview缩放按钮
//        webSettings.setDisplayZoomControls(false);
//        mWebView.setWebChromeClient(new WebChromeClient());
//        mWebView.setWebViewClient(new WebViewClient() );
//
//
//    }

    @Override
    protected View getContentView() {
        return View.inflate(this, R.layout.activity_web_view,null);
    }

    @Override
    protected void onContentAdded() {
        WebSettings webSettings = mWebView.getSettings();
        /**
         * LOAD_CACHE_ONLY: 不使用网络，只读取本地缓存数据
         * LOAD_DEFAULT: （默认）根据cache-control决定是否从网络上取数据。
         * LOAD_NO_CACHE: 不使用缓存，只从网络获取数据.
         * LOAD_CACHE_ELSE_NETWORK，只要本地有，无论是否过期，或者no-cache，都使用缓存中的数据。
         */
        webSettings.setAppCacheEnabled(true);
        webSettings.setDomStorageEnabled(true);//主要是这句
        webSettings.setAllowContentAccess(true);
        webSettings.setBlockNetworkImage(false);//解决图片不显示
        webSettings.setAllowFileAccess(true);
        webSettings.setJavaScriptEnabled(true);
        webSettings.setJavaScriptCanOpenWindowsAutomatically(true);
        webSettings.setLoadsImagesAutomatically(true);
        webSettings.setCacheMode(WebSettings.LOAD_NO_CACHE);//不使用缓存，只从网络获取数据.
        //支持屏幕缩放
        webSettings.setSupportZoom(true);
        webSettings.setBuiltInZoomControls(true);
        //不显示webview缩放按钮
        webSettings.setDisplayZoomControls(false);
        mWebView.setWebChromeClient(new WebChromeClient());
        mWebView.setWebViewClient(new WebViewClient(){
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
//                Log.i("shouldOverrideUrlLoading",""+url);
                if (url.startsWith("weixin://") || url.startsWith("alipays://")) {
                    Intent intent = new Intent();
                    intent.setAction(Intent.ACTION_VIEW);
                    intent.setData(Uri.parse(url));
                    startActivity(intent);
                    finish();
                    return true;
                }
                view.loadUrl(url);
                return true;
            }
        } );
        mWebView.loadUrl(getIntent().getStringExtra("url"));//加载url
    }

//    @Override
//    public void initData() {
//        mWebView.loadUrl(getIntent().getStringExtra("url"));//加载url
//        mWebView.addJavascriptInterface(new WebAppInterface(this), "YaChang");
//
//    }


}
