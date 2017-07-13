package com.lee.cordovawebview;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

//http://blog.csdn.net/u012422440/article/details/50276427
public class StoreCredentials {

    //微信类型
    public static final int WX_TYPE = 1;
    public static final int MINE_TYPE = 11;
    //验证方式
    private static final String API_KEY = "miles_api-key";//key
    private static final String USER_NAME = "miles_user_name";//用户名(微信昵称可能会带后缀以唯一,不适合显示)
    //判断标识
    private static final String LOGIN_TYPE = "miles_login_type";//登录类型
    private static final String LOGIN_STATE = "miles_is_login";//是否是登录状态
    //以下是显示字符
    private static final String USER_PHONE = "miles_user_phone";//手机号
    private static final String USER_NICK = "miles_wx_user_nick";//微信昵称
    private final SharedPreferences.Editor editor;
    private final SharedPreferences preferences;

    public StoreCredentials(Context context) {
        preferences = PreferenceManager.getDefaultSharedPreferences(context.getApplicationContext());
        editor = preferences.edit();
    }

    public SharedPreferences getPreferences() {
        return preferences;
    }

    public void store(String key, Object object) {
        if (object instanceof String) {
            editor.putString(key, (String) object);
        } else if (object instanceof Integer) {
            editor.putInt(key, (Integer) object);
        } else if (object instanceof Boolean) {
            editor.putBoolean(key, (Boolean) object);
        } else if (object instanceof Float) {
            editor.putFloat(key, (Float) object);
        } else if (object instanceof Long) {
            editor.putLong(key, (Long) object);
        } else {
            editor.putString(key, object.toString());
        }
        editor.apply();
    }

    public void storeUserName(String name) {
        editor.putString(USER_NAME, name);
        editor.apply();
    }

    public void storeWxNick(String nick) {
        editor.putString(USER_NICK, nick);
        editor.apply();
    }

    public void storeUserPhone(String phone) {
        editor.putString(USER_PHONE, phone);
        editor.apply();
    }

    public void storeLoginState(boolean state) {
        editor.putBoolean(LOGIN_STATE, state);
        editor.apply();
    }

    public void storeLoginType(int type) {
        editor.putInt(LOGIN_TYPE, type);
        editor.apply();
    }

    public void storeApiKey(String accessToken) {
        editor.putString(API_KEY, accessToken);
        editor.apply();
    }

    //获取用户名
    public String getUserName() {
        return preferences.getString(USER_NAME, "");
    }

    //获取api-key
    public String getApiKey() {
        return preferences.getString(API_KEY, "");
    }

    //获取登录类型
    public int getLoginType() {
        return preferences.getInt(LOGIN_TYPE, 0);
    }

    //获取登录状态
    public boolean getLoginState() {
        return preferences.getBoolean(LOGIN_STATE, false);
    }

    //获取手机号
    public String getUserPhone() {
        return preferences.getString(USER_PHONE, "");
    }

    //获取(微信)昵称
    public String getUserNick() {
        return preferences.getString(USER_NICK, "");
    }

    public void clear() {
        editor.remove(API_KEY);
        editor.remove(USER_NAME);
        editor.remove(LOGIN_TYPE);
        editor.remove(LOGIN_STATE);
        editor.remove(USER_PHONE);
        editor.remove(USER_NICK);
        editor.commit();
    }

    public void outLogin() {
        storeLoginState(false);
        editor.remove(API_KEY);
        editor.commit();
    }
}
