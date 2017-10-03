package com.smartstorage.mobile.storage;

import android.os.Build;
import android.os.Environment;
import android.os.StatFs;

/**
 * Created by kasun on 9/22/17.
 */

public class StorageChecker {

    private static final long KILOBYTE = 1024;
//    Monitoring device storage
        static StatFs internalStatFs = new StatFs( Environment.getRootDirectory().getAbsolutePath() );
        static long internalTotal;
        static long internalFree;

        static StatFs externalStatFs = new StatFs( Environment.getExternalStorageDirectory().getAbsolutePath() );
        static long externalTotal;
        static long externalFree;
    public static long returnFreeStorage(){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2) {
            internalTotal = ( internalStatFs.getBlockCountLong() * internalStatFs.getBlockSizeLong() ) / ( KILOBYTE * KILOBYTE );
            internalFree = ( internalStatFs.getAvailableBlocksLong() * internalStatFs.getBlockSizeLong() ) / ( KILOBYTE * KILOBYTE );
            externalTotal = ( externalStatFs.getBlockCountLong() * externalStatFs.getBlockSizeLong() ) / ( KILOBYTE * KILOBYTE );
            externalFree = ( externalStatFs.getAvailableBlocksLong() * externalStatFs.getBlockSizeLong() ) / ( KILOBYTE * KILOBYTE );
        }
        else {
            internalTotal = ( (long) internalStatFs.getBlockCount() * (long) internalStatFs.getBlockSize() ) / ( KILOBYTE * KILOBYTE );
            internalFree = ( (long) internalStatFs.getAvailableBlocks() * (long) internalStatFs.getBlockSize() ) / ( KILOBYTE * KILOBYTE );
            externalTotal = ( (long) externalStatFs.getBlockCount() * (long) externalStatFs.getBlockSize() ) / ( KILOBYTE * KILOBYTE );
            externalFree = ( (long) externalStatFs.getAvailableBlocks() * (long) externalStatFs.getBlockSize() ) / ( KILOBYTE * KILOBYTE );
        }

        long total = internalTotal + externalTotal;
        long free = internalFree + externalFree;
        long used = total - free;
        long percentage=(long)((float)used/total*100);

        return percentage;

    }

}
