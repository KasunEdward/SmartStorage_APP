package com.smartstorage.mobile.util;

import com.smartstorage.mobile.AppParams;

/**
 * Created by Irfad Hussain on 10/8/2017.
 */

public class CommonUtils {

    public static boolean isTempOrCacheFile(String absolutePath){
        return absolutePath.contains(AppParams.IGNORE_FILE_CACHE) ||
                absolutePath.contains(AppParams.IGNORE_FILE_TEMP) ||
                absolutePath.contains(AppParams.IGNORE_FILE_TMP);
    }

}
