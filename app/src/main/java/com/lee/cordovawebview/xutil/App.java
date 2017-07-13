package com.lee.cordovawebview.xutil;

import android.support.multidex.MultiDexApplication;

import com.lee.cordovawebview.StoreCredentials;
import com.squareup.leakcanary.LeakCanary;

/**
 * Created With Android Studio
 * Email: sielee@163.com
 * Auther: Lee Sie
 * CopyRight: CL
 *
 * @Description: TODO
 */
public class App extends MultiDexApplication {

    //Application实例
    public static App self;
    //共享参数存储
    private StoreCredentials storeCredentials;

    @Override
    public void onCreate() {
        super.onCreate();
        LeakCanary.install(this);
        self = this;
    }

    public StoreCredentials getStoreCredentials() {
        if (storeCredentials == null) {
            storeCredentials = new StoreCredentials(this);
        }
        return storeCredentials;
    }
}
