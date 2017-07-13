package com.lee.cordovawebview;

import java.util.ArrayList;
import java.util.Locale;

import org.apache.cordova.ConfigXmlParser;
import org.apache.cordova.CordovaInterfaceImpl;
import org.apache.cordova.CordovaPreferences;
import org.apache.cordova.CordovaWebView;
import org.apache.cordova.CordovaWebViewImpl;
import org.apache.cordova.LOG;
import org.apache.cordova.PluginEntry;
import org.apache.cordova.PluginManager;
import org.apache.cordova.engine.SystemWebView;
import org.apache.cordova.engine.SystemWebViewEngine;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Color;
import android.media.AudioManager;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebViewClient;
import android.widget.FrameLayout;

import com.lee.cordovawebview.xutil.SieConstant;

/**
 * 这个是我们自定义的activity 要使用cordova webView
 * 基本内容都是从CordovaActivity copy而来
 * 修改的地方会加注释，请搜索 changed
 */
public class WebH5Activity extends AppCompatActivity {

    public static String TAG = "CordovaActivity";
    protected CordovaWebView cordovaWebView;
    //JavaScript and native code在后台可继续运行
    protected boolean keepRunning = true;
    //仿真 if set to fullscreen
    protected boolean immersiveMode;
    // Read from config.xml:
    protected CordovaPreferences preferences;
    //other var
    protected ArrayList<PluginEntry> pluginEntries;
    protected String launchUrl;
    private String userAgentString = " Android-miles&version=" + SieConstant.appVersion;
    private LoadingDialogFragment loading;
    //todo CordovaInterfaceImpl不同于CordovaWebViewImpl
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

    protected void loadConfig() {
        ConfigXmlParser parser = new ConfigXmlParser();
        parser.parse(this);
        preferences = parser.getPreferences();
        preferences.setPreferencesBundle(getIntent().getExtras());
        launchUrl = parser.getLaunchUrl();
        pluginEntries = parser.getPluginEntries();
        // 内置白名单插件
        PluginEntry object = new PluginEntry("whiltelist", new ActivityPlugin(this) {
            @Override
            public boolean onOverrideUrlLoading(String url) {
                boolean ret = false;
                if (url.contains("http")) {
                    ret = true;
                    Intent intent = new Intent(WebH5Activity.this, WebH5Activity.class);
                    intent.putExtra("webH5Url", url);
                    startActivity(intent);
                }
                return ret;
            }
        });

        pluginEntries.add(object);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        LOG.i(TAG, "Apache Cordova native platform version " + CordovaWebView.CORDOVA_VERSION + " is starting");
        //must be called before adding content
        loadConfig();
        /*if (!preferences.getBoolean("ShowTitle", false)) {
            getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        }
        if (preferences.getBoolean("SetFullscreen", false)) {
            //新版不推荐使用,已经移除
            preferences.set("Fullscreen", true);
        }
        if (preferences.getBoolean("Fullscreen", false)) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                immersiveMode = true;
            } else {
                getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
            }
        } else {
            getWindow().setFlags(WindowManager.LayoutParams.FLAG_FORCE_NOT_FULLSCREEN, WindowManager.LayoutParams.FLAG_FORCE_NOT_FULLSCREEN);
        }*/
        super.onCreate(savedInstanceState);

        //cordovaInterface = makeCordovaInterface();
        /*if (savedInstanceState != null) {
            cordovaInterface.restoreInstanceState(savedInstanceState);
        }*/

        //设置自定义的布局 初始化 加载页面
        setContentView(R.layout.activity_re);
        loading = new LoadingDialogFragment();
        //newView,findViewId,addView
        initWebView();
        launchUrl = getIntent().getStringExtra("webH5Url");
        webLoadUrl(launchUrl);
    }

    protected void initWebView() {
        //new WebView
        newWebView();
        //添加到父布局
        addWebView();
        //插件初始化
        cordovaInterface.onCordovaInit(cordovaWebView.getPluginManager());
        // Wire the hardware volume controls to control media if desired.
        String volumePref = preferences.getString("DefaultVolumeStream", "");
        if ("media".equals(volumePref.toLowerCase(Locale.ENGLISH))) {
            setVolumeControlStream(AudioManager.STREAM_MUSIC);
        }
    }

    @SuppressWarnings("ResourceType")
    protected void addWebView() {
        //id用于审查
        cordovaWebView.getView().setId(100);
        cordovaWebView.getView().setLayoutParams(new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
        // 将SystemWebView加载到你自定义的布局中
        FrameLayout container = (FrameLayout) findViewById(R.id.container);
        container.addView(cordovaWebView.getView());

        if (preferences.contains("BackgroundColor")) {
            int backgroundColor = preferences.getInteger("BackgroundColor", Color.BLACK);
            //Background of activity
            cordovaWebView.getView().setBackgroundColor(backgroundColor);
        }
        cordovaWebView.getView().requestFocusFromTouch();
    }

    protected CordovaInterfaceImpl makeCordovaInterface() {
        return new CordovaInterfaceImpl(this) {
            @Override
            public Object onMessage(String id, Object data) {
                return this.onMessage(id, data);
            }
        };
    }

    public void webLoadUrl(String url) {
        if (this.cordovaWebView == null) {
            initWebView();
        }
        //If keepRunning
        this.keepRunning = preferences.getBoolean("KeepRunning", true);
        //加载URL
        this.getWebView().loadUrlIntoView(url, true);
    }

    @Override
    protected void onPause() {
        super.onPause();
        //转到后台 shouldn't stop WebView Javascript timers(定时器)
        this.getWebView().handlePause(this.keepRunning);
    }

    //Called when the activity receives a new intent
    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        //Forward to plugins
        this.getWebView().onNewIntent(intent);
    }

    public CordovaWebView getWebView() {
        newWebView();
        return this.cordovaWebView;
    }

    private void newWebView() {
        if (this.cordovaWebView == null) {
            SystemWebView webView = new SystemWebView(this);
            //或者preferences.getString("AppendUserAgent", null);
            webView.getSettings().setUserAgentString(userAgentString);
            //todo CordovaInterfaceImpl不同于CordovaWebViewImpl
            this.cordovaWebView = new CordovaWebViewImpl(new SystemWebViewEngine(webView, this.preferences));
            //new CordovaWebViewImpl(CordovaWebViewImpl.createEngine(this, preferences));
        }
        if (!cordovaWebView.isInitialized()) {
            //CordovaWebView初始化
            this.cordovaWebView.init(cordovaInterface, pluginEntries, preferences);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Force window to have focus, so application always receive user input.
        // Workaround for some devices (SamSung Galaxy Note 3 at least)
        this.getWindow().getDecorView().requestFocus();
        this.getWebView().handleResume(this.keepRunning);
    }

    /**
     * Called when the activity is no longer visible to the user.
     */
    @Override
    protected void onStop() {
        super.onStop();
        this.getWebView().handleStop();
    }

    /**
     * Called when the activity is becoming visible to the user.
     */
    @Override
    protected void onStart() {
        super.onStart();
        this.getWebView().handleStart();
    }

    /**
     * The final call you receive before your activity is destroyed.
     */
    @Override
    public void onDestroy() {
        super.onDestroy();
        this.getWebView().handleDestroy();
    }

    /**
     * Called when view focus is changed
     */
    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        if (hasFocus && immersiveMode) {
            final int uiOptions = View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                    | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                    | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                    | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                    | View.SYSTEM_UI_FLAG_FULLSCREEN
                    | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY;

            getWindow().getDecorView().setSystemUiVisibility(uiOptions);
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    @Override
    public void startActivityForResult(Intent intent, int requestCode, Bundle options) {
        cordovaInterface.setActivityResultRequestCode(requestCode);
        super.startActivityForResult(intent, requestCode, options);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
        super.onActivityResult(requestCode, resultCode, intent);
        cordovaInterface.onActivityResult(requestCode, resultCode, intent);
    }

    public void onReceivedError(final int errorCode, final String description, final String failingUrl) {
        final WebH5Activity me = this;
        final String errorUrl = preferences.getString("errorUrl", null);
        if ((errorUrl != null) && (!failingUrl.equals(errorUrl)) && (cordovaWebView != null)) {
            me.runOnUiThread(new Runnable() {
                public void run() {
                    me.cordovaWebView.showWebPage(errorUrl, false, true, null);
                }
            });
        } else {
            final boolean exit = !(errorCode == WebViewClient.ERROR_HOST_LOOKUP);
            me.runOnUiThread(new Runnable() {
                public void run() {
                    if (exit) {
                        me.cordovaWebView.getView().setVisibility(View.GONE);
                        me.displayError("Page Error", description + " (" + failingUrl + ")", "OK", exit);
                    }
                }
            });
        }
    }

    /**
     * Display an error dialog and optionally exit application.
     */
    public void displayError(final String title, final String message, final String button, final boolean exit) {
        final WebH5Activity me = this;
        me.runOnUiThread(new Runnable() {
            public void run() {
                try {
                    AlertDialog.Builder dlg = new AlertDialog.Builder(me);
                    dlg.setMessage(message);
                    dlg.setTitle(title);
                    dlg.setCancelable(false);
                    dlg.setPositiveButton(button,
                            new AlertDialog.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();
                                    if (exit) {
                                        finish();
                                    }
                                }
                            });
                    dlg.create();
                    dlg.show();
                } catch (Exception e) {
                    finish();
                    e.printStackTrace();
                }
            }
        });
    }

    /*
     * Hook in Cordova for menu plugins
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        if (cordovaWebView != null) {
            cordovaWebView.getPluginManager().postMessage("onCreateOptionsMenu", menu);
        }
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        if (cordovaWebView != null) {
            cordovaWebView.getPluginManager().postMessage("onPrepareOptionsMenu", menu);
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (cordovaWebView != null) {
            cordovaWebView.getPluginManager().postMessage("onOptionsItemSelected", item);
        }
        return true;
    }

    /**
     * Called when a message is sent to plugin.
     */
    public Object onMessage(String id, Object data) {
        if ("onReceivedError".equals(id)) {
            JSONObject d = (JSONObject) data;
            try {
                this.onReceivedError(d.getInt("errorCode"), d.getString("description"), d.getString("url"));
            } catch (JSONException e) {
                e.printStackTrace();
            }
        } else if ("exit".equals(id)) {
            finish();
        }
        return null;
    }

    protected void onSaveInstanceState(Bundle outState) {
        cordovaInterface.onSaveInstanceState(outState);
        super.onSaveInstanceState(outState);
    }

    /**
     * Called by the system when the device configuration changes while your activity is running.
     */
    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        PluginManager pm = this.getWebView().getPluginManager();
        if (pm != null) {
            pm.onConfigurationChanged(newConfig);
        }
    }
}
