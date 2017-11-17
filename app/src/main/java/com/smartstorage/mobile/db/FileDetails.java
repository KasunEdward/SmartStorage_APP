package com.smartstorage.mobile.db;
/**
 * Created by kasun on 8/15/17.
 */

public class FileDetails {
    private int id;
    private String file_name;
    private String drive_link;
    private String drive_type;
    private double migration_value;
    private String deleted;
    private long size;  // size of file in bytes
    private long last_accessed;

    public FileDetails(String file_name, String drive_link, String drive_type, long size){
        this.file_name=file_name;
        this.drive_link=drive_link;
        this.drive_type=drive_type;
        this.size = size;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getFile_name() {
        return file_name;
    }

    public void setFile_name(String file_name) {
        this.file_name = file_name;
    }

    public String getDrive_link() {
        return drive_link;
    }

    public void setDrive_link(String drive_link) {
        this.drive_link = drive_link;
    }

    public String getDeleted() {
        return deleted;
    }

    public void setDeleted(String deleted) {
        this.deleted = deleted;
    }

    public String getDrive_type() {
        return drive_type;
    }

    public void setDrive_type(String drive_type) {
        this.drive_type = drive_type;
    }

    public double getMigration_value() {
        return migration_value;
    }

    public void setMigration_value(double migration_value) {
        this.migration_value = migration_value;
    }

    public long getSize() {
        return size;
    }

    public void setSize(long size) {
        this.size = size;
    }

    public long getLast_accessed() {
        return last_accessed;
    }

    public void setLast_accessed(long last_accessed) {
        this.last_accessed = last_accessed;
    }

}
