package com.lee.cordovawebview.xutil;

/**
 * Created With Android Studio
 * Email: sielee@163.com
 * Auther: Lee Sie
 * CopyRight: CL
 *
 * @Description: TODO
 */
public class AppBar {

    //0->隐藏;-1->默认;其他->resId
    public int backId;//返回:icon
    //txt
    public String titleTxt;//标题
    //4->文字;5->icon
    public int optId;//opt:文字或者icon

    public int getBackId() {
        return backId;
    }

    public void setBackId(int backId) {
        this.backId = backId;
    }

    public String getTitleTxt() {
        return titleTxt;
    }

    public void setTitleTxt(String titleTxt) {
        this.titleTxt = titleTxt;
    }

    public int getOptId() {
        return optId;
    }

    public void setOptId(int optId) {
        this.optId = optId;
    }

    @Override
    public String toString() {
        return "AppBar{" +
                "backId=" + backId +
                ", titleTxt='" + titleTxt + '\'' +
                ", optId=" + optId +
                '}';
    }
}
