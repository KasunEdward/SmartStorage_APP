package com.smartstorage.mobile;

/**
 * Created by Irfad Hussain on 8/23/2017.
 */

public class AppParams {

    public class PreferenceStr {

        public static final String SHARED_PREFERENCE_NAME = "com.smartstorage.mobile";
        public static final String FIRST_RUN = "firstRun";
        public static final String FILE_SYSTEM_MAPPED = "fileSystemMapped";

    }

    public static final String DRIVE_TYPE_GOOGLE = "GoogleDrive";
    public static final String DRIVE_TYPE_DROPBOX = "Dropbox";
    public static final String DRIVE_NO_LINK = "no_link_yet";

    // ignore monitoring files with name that contains the following
    public static final String IGNORE_FILE_CACHE = "cache";
    public static final String IGNORE_FILE_TEMP = "temp";
    public static final String IGNORE_FILE_TMP = "tmp";

    public static final int MIGRATION_X = 2048;
    public static final double MIGRATION_FACTOR = 0.9;

}
