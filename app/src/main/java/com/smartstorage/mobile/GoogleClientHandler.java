package com.smartstorage.mobile;

import android.app.Application;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.drive.Drive;
import com.google.android.gms.drive.DriveId;

/**
 * Created by kasun on 10/10/17.
 */

public class GoogleClientHandler {
    public static GoogleApiClient googleApiClient;
    public static DriveId driveId;


}
