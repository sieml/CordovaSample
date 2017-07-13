package com.lee.cordovawebview;

import android.content.Context;

import org.apache.cordova.ConfigXmlParser;
import org.apache.cordova.CordovaPlugin;
import org.apache.cordova.LOG;
import org.apache.cordova.Whitelist;
import org.xmlpull.v1.XmlPullParser;

/**
 * Created With Android Studio
 * Email: sielee@163.com
 * Auther: Lee Sie
 * CopyRight: CL
 *
 * @Description: TODO Demo: http://www.th7.cn/Program/Android/201507/500935.shtml
 */
public class ActivityPlugin extends CordovaPlugin {

    private static final String LOG_TAG = "WhitelistPlugin";
    static String TAG = "LifeCyclePlugin";
    private Whitelist allowedNavigations;
    private Whitelist allowedIntents;
    private Whitelist allowedRequests;
    /*//添加到白名单
    @Override
    public Boolean shouldAllowRequest(String url) {
        return true;//全部允许,true
        //return super.shouldAllowRequest(url);//全部允许,true
    }

    @Override
    public Boolean shouldAllowNavigation(String url) {
        return true;
        //return super.shouldAllowNavigation(url);
    }

    */

    // Used when instantiated via reflection by PluginManager
    public ActivityPlugin() {
    }

    // These can be used by embedders to allow Java-configuration of whitelists.
    public ActivityPlugin(Context context) {
        this(new Whitelist(), new Whitelist(), null);
        new CustomConfigXmlParser().parse(context);
    }
    public ActivityPlugin(XmlPullParser xmlParser) {
        this(new Whitelist(), new Whitelist(), null);
        new CustomConfigXmlParser().parse(xmlParser);
    }
    public ActivityPlugin(Whitelist allowedNavigations, Whitelist allowedIntents, Whitelist allowedRequests) {
        if (allowedRequests == null) {
            allowedRequests = new Whitelist();
            allowedRequests.addWhiteListEntry("file:///*", false);
            allowedRequests.addWhiteListEntry("data:*", false);
        }
        this.allowedNavigations = allowedNavigations;
        this.allowedIntents = allowedIntents;
        this.allowedRequests = allowedRequests;
    }

    @Override
    public void onStart() {
        LOG.d(TAG, "onStart");
    }

    @Override
    public void onPause(boolean multitasking) {
        LOG.d(TAG, "onPause");
    }

    @Override
    public void onResume(boolean multitasking) {
        LOG.d(TAG, "onResume");
    }

    @Override
    public void onStop() {
        LOG.d(TAG, "onStop");
    }

    /**
     * 允许二级访问:
     * onOverrideUrlLoading这个函数说明我们是否要自己处理这个url
     * shouldAllowNavigation这个函数说明是否允许这种导航
     * shouldOpenExternalUrl这个函数说明是否打开外部链接
     *
     * @param url The URL that is trying to be loaded in the Cordova webview.
     * @return
     *//*
    @Override
    public boolean onOverrideUrlLoading(String url) {
        return true;
        //return super.onOverrideUrlLoading(url);
    }
    */
    @Override
    public Boolean shouldAllowBridgeAccess(String url) {
        return true;
        //return super.shouldAllowBridgeAccess(url);
    }

    @Override
    public void pluginInitialize() {
        if (allowedNavigations == null) {
            allowedNavigations = new Whitelist();
            allowedIntents = new Whitelist();
            allowedRequests = new Whitelist();
            new CustomConfigXmlParser().parse(webView.getContext());
        }
    }

    @Override
    public Boolean shouldAllowNavigation(String url) {
        if (allowedNavigations.isUrlWhiteListed(url)) {
            return true;
        }
        return null; // Default policy
    }

    @Override
    public Boolean shouldAllowRequest(String url) {
        if (Boolean.TRUE == shouldAllowNavigation(url)) {
            return true;
        }
        if (allowedRequests.isUrlWhiteListed(url)) {
            return true;
        }
        return null; // Default policy
    }

    @Override
    public Boolean shouldOpenExternalUrl(String url) {
        if (allowedIntents.isUrlWhiteListed(url)) {
            return true;
        }
        return null; // Default policy
    }

    public Whitelist getAllowedNavigations() {
        return allowedNavigations;
    }

    public void setAllowedNavigations(Whitelist allowedNavigations) {
        this.allowedNavigations = allowedNavigations;
    }

    public Whitelist getAllowedIntents() {
        return allowedIntents;
    }

    public void setAllowedIntents(Whitelist allowedIntents) {
        this.allowedIntents = allowedIntents;
    }

    public Whitelist getAllowedRequests() {
        return allowedRequests;
    }

    public void setAllowedRequests(Whitelist allowedRequests) {
        this.allowedRequests = allowedRequests;
    }

    private class CustomConfigXmlParser extends ConfigXmlParser {
        @Override
        public void handleStartTag(XmlPullParser xml) {
            String strNode = xml.getName();
            if (strNode.equals("content")) {
                String startPage = xml.getAttributeValue(null, "src");
                allowedNavigations.addWhiteListEntry(startPage, false);
            } else if (strNode.equals("allow-navigation")) {
                String origin = xml.getAttributeValue(null, "href");
                if ("*".equals(origin)) {
                    allowedNavigations.addWhiteListEntry("http://*/*", false);
                    allowedNavigations.addWhiteListEntry("https://*/*", false);
                    allowedNavigations.addWhiteListEntry("data:*", false);
                } else {
                    allowedNavigations.addWhiteListEntry(origin, false);
                }
            } else if (strNode.equals("allow-intent")) {
                String origin = xml.getAttributeValue(null, "href");
                allowedIntents.addWhiteListEntry(origin, false);
            } else if (strNode.equals("access")) {
                String origin = xml.getAttributeValue(null, "origin");
                String subdomains = xml.getAttributeValue(null, "subdomains");
                boolean external = (xml.getAttributeValue(null, "launch-external") != null);
                if (origin != null) {
                    if (external) {
                        //Log.w(LOG_TAG, "Found <access launch-external> within config.xml. Please use <allow-intent> instead.");
                        allowedIntents.addWhiteListEntry(origin, (subdomains != null) && (subdomains.compareToIgnoreCase("true") == 0));
                    } else {
                        if ("*".equals(origin)) {
                            allowedRequests.addWhiteListEntry("http://*/*", false);
                            allowedRequests.addWhiteListEntry("https://*/*", false);
                        } else {
                            allowedRequests.addWhiteListEntry(origin, (subdomains != null) && (subdomains.compareToIgnoreCase("true") == 0));
                        }
                    }
                }
            }
        }

        @Override
        public void handleEndTag(XmlPullParser xml) {
        }
    }
}
