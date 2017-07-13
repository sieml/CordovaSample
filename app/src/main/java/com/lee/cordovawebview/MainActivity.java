package com.lee.cordovawebview;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.webkit.WebSettings;

import org.apache.cordova.ConfigXmlParser;
import org.apache.cordova.CordovaInterfaceImpl;
import org.apache.cordova.CordovaWebView;
import org.apache.cordova.CordovaWebViewImpl;
import org.apache.cordova.LOG;
import org.apache.cordova.PluginEntry;
import org.apache.cordova.engine.SystemWebView;
import org.apache.cordova.engine.SystemWebViewEngine;

import java.util.ArrayList;
import java.util.concurrent.ArrayBlockingQueue;

/**
 * js通信:
 * http://blog.csdn.net/crazyman2010/article/details/46694925
 */
public class MainActivity extends AppCompatActivity {

    public String START_URL;
    private CordovaWebView cordovaWebView;
    private LoadingDialogFragment loading;

    protected CordovaInterfaceImpl cordovaInterface = new CordovaInterfaceImpl(this) {
        private final String ERROR_URL = "file:///android_asset/files/404.html";

        @Override
        public Object onMessage(String id, Object data) {
            if ("onPageStarted".equals(id)) {
                loading.showLoadingDialog(getSupportFragmentManager());
                return true;
            }
            if ("onPageFinished".equals(id)) {
                loading.hideLoadingDialog(getSupportFragmentManager());
                return true;
            }
            if ("onReceivedError".equals(id)) {
                cordovaWebView.loadUrl(ERROR_URL);
                return true;
            }
            return super.onMessage(id, data);
        }
    };

    @Override
    protected void onStart() {
        super.onStart();
        if (cordovaWebView != null) {
            cordovaWebView.handleStart();
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (cordovaWebView != null) {
            cordovaWebView.handleStop();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (cordovaWebView != null) {
            cordovaWebView.handleResume(true);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (cordovaWebView != null) {
            cordovaWebView.handlePause(true);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (cordovaWebView != null) {
            cordovaWebView.handleDestroy();
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        loading = new LoadingDialogFragment();
        LOG.setLogLevel(LOG.DEBUG);
        //Set up the webview
        ConfigXmlParser configXmlParser = new ConfigXmlParser();
        configXmlParser.parse(this);
        START_URL = configXmlParser.getLaunchUrl() + "&language=zh";//"http://enjoy.ricebook.com";
        ArrayList<PluginEntry> pluginEntryList = configXmlParser.getPluginEntries();
        // 内置白名单插件
        PluginEntry object = new PluginEntry("whiltelist", new ActivityPlugin(this) {
            @Override
            public boolean onOverrideUrlLoading(String url) {
                boolean ret = false;
                if (url.contains("http")) {
                    ret = true;
                    Intent intent = new Intent(MainActivity.this, WebH5Activity.class);
                    intent.putExtra("webH5Url", url);
                    startActivity(intent);
                }
                return ret;
            }
        });
        pluginEntryList.add(object);
        //TODO 设置SystemWebView
        SystemWebView systemWebView = (SystemWebView) findViewById(R.id.cordovaWebView);
        systemWebView.getSettings().setUseWideViewPort(true);
        systemWebView.getSettings().setLoadWithOverviewMode(true);
        systemWebView.getSettings().setDomStorageEnabled(true);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            systemWebView.getSettings().setMixedContentMode(WebSettings.MIXED_CONTENT_ALWAYS_ALLOW);
        }
        systemWebView.getSettings().setUserAgentString("Android-miles&version=3.0.1");
        //TODO 构建systemWebView->systemWebViewEngine->CordovaWebViewImpl(实现cordovaWebView接口的实现类)
        SystemWebViewEngine systemWebViewEngine = new SystemWebViewEngine(systemWebView);
        cordovaWebView = new CordovaWebViewImpl(systemWebViewEngine);
        //初始化
        cordovaWebView.init(cordovaInterface, pluginEntryList, configXmlParser.getPreferences());

        //load URL
        if (BuildConfig.DEBUG) {
            //调试模式
            cordovaWebView.loadUrl("https://www.mileslife.com/staticpage/products/localproducts.html?city=上海&language=zh");
        } else {
            cordovaWebView.loadUrl(START_URL);
        }
    }
}