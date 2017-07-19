package com.hzkj.wdk.fra;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.SslErrorHandler;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.hzkj.wdk.R;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by chenweiqi on 2017/5/18.
 */

public class BrowserFragment extends BaseFragment {


    View rootView;
    @BindView(R.id.iv_left)
    ImageView ivLeft;
    @BindView(R.id.iv_right)
    ImageView ivRight;
    @BindView(R.id.title)
    TextView title;
    @BindView(R.id.head)
    RelativeLayout head;
    @BindView(R.id.webview)
    WebView webview;


    public String url;
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        if (rootView == null) {
            rootView = inflater.inflate(R.layout.fragment_browser, container, false);
            rootView.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    return true;
                }
            });
        }
        ButterKnife.bind(this, rootView);
        mActivity.FrameLayoutVisible(true);
        return rootView;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        WebSettings settings = webview.getSettings();
        settings.setSupportZoom(true);
        settings.setJavaScriptEnabled(true);
        settings.setJavaScriptCanOpenWindowsAutomatically(true);
        settings.setUseWideViewPort(true);// 设置此属性，可任意比例缩放
        settings.setLoadWithOverviewMode(true);
        settings.setCacheMode(WebSettings.LOAD_NO_CACHE);// 不使用缓存
        // settings.setCacheMode(WebSettings.LOAD_CACHE_ELSE_NETWORK);//缓存模式
        settings.setBuiltInZoomControls(true);// 显示缩放大小
        settings.setSupportZoom(true);// 设置可以缩放
        // /////
        settings.setDatabaseEnabled(true);
        String dir = mActivity.getApplicationContext()
                .getDir("database", Context.MODE_PRIVATE).getPath();
        settings.setGeolocationEnabled(true);
        settings.setGeolocationDatabasePath(dir);
        settings.setDomStorageEnabled(true);
        // ///
        webview.setInitialScale(100);// 默认缩放大小


        webview.setWebViewClient(new WebViewClient(){
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                webview.loadUrl(url);
                return true;
            }
            public void onReceivedSslError(WebView view, SslErrorHandler handler, android.net.http.SslError error) {
                handler.proceed();
            }

        });
        webview.setWebChromeClient(new WebChromeClient(){
            @Override
            public void onReceivedTitle(WebView view, String title) {
                super.onReceivedTitle(view, title);
//                if (BrowserFragment.this.title!=null)
//                    BrowserFragment.this.title.setText(title);
            }
        });

        if (!TextUtils.isEmpty(url)){
            webview.loadUrl(url);
        }

    }
    @OnClick(R.id.iv_left)
    public void onBackClick(){
        mActivity.backFragment();
    }

    @OnClick(R.id.back)
    public void onWebViewBackClick(){
        if (webview.canGoBack()){
            webview.goBack();
        }
    }
}
