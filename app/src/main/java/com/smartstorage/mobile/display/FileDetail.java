package com.smartstorage.mobile.display;

import android.graphics.drawable.Drawable;

/**
 * Created by kasun on 9/25/17.
 */

public class FileDetail {
    private String url;
    private String drive_link;
    private double size;
    private int icon_image;

    private boolean checkbox_value;
    public FileDetail(){
    }
    public FileDetail(String url, String drive_link, double size,int icon_image,boolean checkbox_value){
        this.setUrl(url);
        this.setDrive_link(drive_link);
        this.setSize(size);
        this.setIconImage(icon_image);
        this.setCheckboxValue(checkbox_value);
    }


    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getDrive_link() {
        return drive_link;
    }

    public void setDrive_link(String drive_link) {
        this.drive_link = drive_link;
    }

    public double getSize() {
        return size;
    }

    public void setSize(double size) {
        this.size = size;
    }

    public int getIconImage() {
        return icon_image;
    }

    public void setIconImage(int icon_image) {
        this.icon_image = icon_image;
    }

    public boolean getCheckboxValue() {
        return checkbox_value;
    }

    public void setCheckboxValue(boolean selected) {
        checkbox_value = selected;
    }
}
