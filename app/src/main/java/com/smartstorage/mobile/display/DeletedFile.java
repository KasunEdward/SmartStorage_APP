package com.smartstorage.mobile.display;

/**
 * Created by kasun on 9/25/17.
 */

public class DeletedFile {
    private String url;
    private String drive_link;
    private double size;
    public DeletedFile(){
    }
    public DeletedFile(String url,String drive_link,double size){
        this.setUrl(url);
        this.setDrive_link(drive_link);
        this.setSize(size);
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
}
