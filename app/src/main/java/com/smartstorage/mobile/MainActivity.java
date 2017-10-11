package com.smartstorage.mobile;

import android.Manifest;
import android.app.Activity;
import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.IntentSender;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.text.style.RelativeSizeSpan;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

import com.dropbox.client2.DropboxAPI;
import com.dropbox.client2.android.AndroidAuthSession;
import com.dropbox.client2.session.AppKeyPair;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.drive.Drive;
import com.google.android.gms.drive.DriveApi;
import com.google.android.gms.drive.DriveFolder;
import com.google.android.gms.drive.DriveId;
import com.google.android.gms.drive.Metadata;
import com.google.android.gms.drive.MetadataChangeSet;
import com.google.android.gms.drive.query.Filters;
import com.google.android.gms.drive.query.Query;
import com.google.android.gms.drive.query.SearchableField;
import com.hookedonplay.decoviewlib.DecoView;
import com.hookedonplay.decoviewlib.charts.SeriesItem;
import com.hookedonplay.decoviewlib.events.DecoEvent;
import com.smartstorage.mobile.db.DatabaseHandler;
import com.smartstorage.mobile.display.FilesActivity;
import com.smartstorage.mobile.display.FilesByTypeActivity;
import com.smartstorage.mobile.service.MigrationService;
import com.smartstorage.mobile.storage.StorageChecker;
import com.smartstorage.mobile.util.FileSystemMapper;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener{

    // TODO: 8/23/2017 Fix issue of re-appearing drive select window when back key press

    private static final  String APP_TAG="Smart_APP...:";

    private static final int REQUEST_CODE_ASK_MULTIPLE_PERMISSIONS = 148;
    private static final String GOOGLE_DRIVE_TAG = "Google Drive....:";
    private static final int REQUEST_CODE_RESOLUTION = 1;
    private static final int REQUEST_CODE_OPENER = 2;
    private static GoogleApiClient mGoogleApiClient;
    final Context context = this;

    private static final String DROP_BOX_TAG = "DropBox....";
    final static private String APP_KEY = "idq79rezmauppol";
    final static private String APP_SECRET = "33jvo64wa29qfmr";
    private static DropboxAPI<AndroidAuthSession> mDBApi;

    SharedPreferences prefs = null;
    SharedPreferences sp = null;
    SharedPreferences drivePrefs = null;
    static DriveId driveId;
    String driveId_str;

    private boolean bound;
    BroadcastReceiver receiver;
    static MainActivity instance;

//    variables for diplaying results in GUI
    String UI_TAG="SmartStorage_UI :";



    /**
     * DecoView animated arc based chart
     */
    private DecoView decoView;


    private int mBackIndex;
    private int mSeries1Index;
    private int mSeries2Index;
    private int mSeries3Index;

    private final float mSeriesMax = 50f;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        decoView = (DecoView) findViewById(R.id.dynamicArcView);
        final SeriesItem seriesItem = new SeriesItem.Builder(Color.parseColor("#870b39"))
                .setRange(0, 100, 0)
                .build();

        int series1Index = decoView.addSeries(seriesItem);
        final TextView textPercentage = (TextView) findViewById(R.id.textPercentage);
        seriesItem.addArcSeriesItemListener(new SeriesItem.SeriesItemListener() {
            @Override
            public void onSeriesItemAnimationProgress(float percentComplete, float currentPosition) {
                float percentFilled = ((currentPosition - seriesItem.getMinValue()) / (seriesItem.getMaxValue() - seriesItem.getMinValue()));
                String s= String.format("%.0f%%", percentFilled * 100f)+ " storage is full";
                SpannableString ss1=  new SpannableString(s);
                ss1.setSpan(new RelativeSizeSpan(0.5f), s.length()-15,s.length(), 0); // set size
                ss1.setSpan(new ForegroundColorSpan(Color.RED), 0, 3, 0);// set color
//                textPercentage.setText(String.format("%.0f%%", percentFilled * 100f)+ "is full");
                textPercentage.setText(ss1);
            }

            @Override
            public void onSeriesItemDisplayProgress(float percentComplete) {

            }
        });

        decoView.addEvent(new DecoEvent.Builder(StorageChecker.returnUsedPercentage())
                .setIndex(series1Index)
                .setDelay(10)
                .build());
/**  Add copied files details to the chart*/
//        final SeriesItem seriesItem2 = new SeriesItem.Builder(Color.parseColor("#110b87"))
//                .setRange(0, 100, 0)
//                .build();
//
//        int series1Index2 = decoView.addSeries(seriesItem2);
//        decoView.addEvent(new DecoEvent.Builder(30f)
//                .setIndex(series1Index2)
//                .setDelay(10)
//                .build());

        instance=this;
        GoogleClientHandler.googleApiClient = new GoogleApiClient.Builder(this)
                .addApi(Drive.API)
                .addScope(Drive.SCOPE_FILE)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .build();
        prefs = getSharedPreferences(AppParams.PreferenceStr.SHARED_PREFERENCE_NAME, MODE_PRIVATE);
        drivePrefs = getSharedPreferences("Drive_type", Activity.MODE_APPEND);
        if(drivePrefs.getString("type",null)==null){
            drivePrefs.edit().putString("type", "NoDrive").commit();
        }
        sp = getSharedPreferences("First_share_memory", Activity.MODE_APPEND);
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        /**Calling database handler to get total num of total num of files
         * */
        DatabaseHandler db=DatabaseHandler.getDbInstance(context);
        int total=db.getNumOfTotalFiles();
        Log.i(APP_TAG,String.valueOf(total));

//textview to display total num of files
        TextView txtMsg=(TextView)findViewById(R.id.textView6);
        String s= String.valueOf(total)+ " Total Files";
        SpannableString ss1=  new SpannableString(s);
        ss1.setSpan(new RelativeSizeSpan(2f), 0,s.length()-11, 0); // set size
        ss1.setSpan(new ForegroundColorSpan(Color.parseColor("#870b39")), 0, s.length()-11, 0);
        txtMsg.setText(ss1);

// textview to display num of copied files
        TextView copiedMsg=(TextView)findViewById(R.id.textView3);
        String s2= String.valueOf(total)+ " Copied Files";
        SpannableString ss2=  new SpannableString(s2);
        ss2.setSpan(new RelativeSizeSpan(2f), 0,s.length()-11, 0); // set size
        ss2.setSpan(new ForegroundColorSpan(Color.parseColor("#110b87")), 0, s.length()-11, 0);
        copiedMsg.setText(ss2);
    }

    @Override
    protected void onResume() {
        super.onResume();
//        TODO: only first run is checked.Must check the connectivity success/failure as well
        DatabaseHandler db_files=DatabaseHandler.getDbInstance(context);
        int total=db_files.getNumOfTotalFiles();
        Log.i(APP_TAG,String.valueOf(total));

        long total_size=StorageChecker.returnUsedSpace();

//textview to display total num of files
        TextView txtMsg=(TextView)findViewById(R.id.textView6);
        String s= String.valueOf(total)+ " Total Files";
        SpannableString ss1=  new SpannableString(s);
        ss1.setSpan(new RelativeSizeSpan(2f), 0,s.length()-11, 0); // set size
        ss1.setSpan(new ForegroundColorSpan(Color.parseColor("#870b39")), 0, s.length()-11, 0);
        txtMsg.setText(ss1);

        TextView totalSize=(TextView)findViewById(R.id.textView7);
        String totalSizeStr= String.valueOf(total_size)+ " GB";
        SpannableString ss3=  new SpannableString(totalSizeStr);
//        ss3.setSpan(new RelativeSizeSpan(2f), 0,totalSizeStr.length()-11, 0); // set size
//        ss3.setSpan(new ForegroundColorSpan(Color.parseColor("#870b39")), 0, t.length()-11, 0);
        totalSize.setText(ss3);



// textview to display num of copied files
        TextView copiedMsg=(TextView)findViewById(R.id.textView3);
        String s2= String.valueOf(total)+ " Copied Files";
        SpannableString ss2=  new SpannableString(s2);
        ss2.setSpan(new RelativeSizeSpan(2f), 0,s.length()-11, 0); // set size
        ss2.setSpan(new ForegroundColorSpan(Color.parseColor("#110b87")), 0, s.length()-11, 0);
        copiedMsg.setText(ss2);

//        Determine if there
        if (prefs.getBoolean(AppParams.PreferenceStr.FIRST_RUN, true)||(!drivePrefs.getString("type",null).equals("GoogleDrive")&&!drivePrefs.getString("type",null).equals("DropBox"))) {
            if (Build.VERSION.SDK_INT >= 23) {            prefs.edit().putBoolean(AppParams.PreferenceStr.FIRST_RUN, false).commit();

                requestRunTimePermission();
            } else {
                setDriveAccount();
                Intent serviceIntent = new Intent(getApplicationContext(), MigrationService.class);
                startService(serviceIntent);
            }

            new FileSystemMapper(this).execute();
            prefs.edit().putBoolean(AppParams.PreferenceStr.FIRST_RUN, false).commit();

        } else {
            Log.i("App...", "not first run");
            Log.i("App...", drivePrefs.getString("type", ""));


            if (drivePrefs.getString("type", "").equals("GoogleDrive")) {
                Log.i(GOOGLE_DRIVE_TAG, "GoogleDrive drive......");
                GoogleClientHandler.googleApiClient.connect();

            } else if (drivePrefs.getString("type", "").equals("DropBox")) {
                Log.i(DROP_BOX_TAG, "Dropbox drive......");
                AppKeyPair appKeys = new AppKeyPair(APP_KEY, APP_SECRET);
                AndroidAuthSession session = new AndroidAuthSession(appKeys);

                // Pass app key pair to the new DropboxAPI object.
                mDBApi = new DropboxAPI<AndroidAuthSession>(session);
                mDBApi.getSession().setOAuth2AccessToken(sp.getString("accesstoken", ""));
                Log.i(DROP_BOX_TAG, sp.getString("accesstoken", ""));
            }
        }
        if (mDBApi != null) {
            if (mDBApi.getSession().authenticationSuccessful()) {
                try {
                    Log.i(DROP_BOX_TAG, "Inside method...........");
                    // Required to complete auth, sets the access token on the session
                    mDBApi.getSession().finishAuthentication();

                    // retrieve access token
                    if (sp.getString("accesstoken", "").isEmpty()) {
                        String accessToken = mDBApi.getSession().getOAuth2AccessToken();
                        sp.edit().putString("accesstoken", accessToken).commit();
                        String savedAccessToken = sp.getString("accesstoken", "");
                        mDBApi.getSession().setOAuth2AccessToken(savedAccessToken);

                    }


                    Log.i(DROP_BOX_TAG, sp.getString("accesstoken", ""));
                } catch (IllegalStateException e) {
                    Log.i(DROP_BOX_TAG, "Error authenticating", e);
                }
            }

        }



        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.HOUR_OF_DAY, 20);
        calendar.set(Calendar.MINUTE, 41);
        calendar.set(Calendar.SECOND, 0);

//        TODO: uncomment this part to get CopyFileToGoogleDriveActivity to working state
        if(drivePrefs.getString("type",null).equals("GoogleDrive")||drivePrefs.getString("type",null).equals("DropBox")){
            if(drivePrefs.getString("type",null).equals("GoogleDrive")){
                Intent alarmReceiver = new Intent(this.getApplicationContext(),CopyFileToGoogleDriveActivity.class);
                ArrayList<String> fileList = getFiles();
//                GoogleClientHandler.googleApiClient=mGoogleApiClient;
//                alarmReceiver.putExtra("aa", (Parcelable) mGoogleApiClient);
//                alarmReceiver.putExtra("bb", driveId);
//                GoogleClientHandler.driveId=driveId;
                alarmReceiver.putStringArrayListExtra("copyingListToGD",fileList);
                alarmReceiver.setAction("com.smartStorage.copytoGD");


                //This is alarm manager
                PendingIntent pi = PendingIntent.getBroadcast(this, 0 , alarmReceiver, PendingIntent.FLAG_UPDATE_CURRENT);
                AlarmManager am = (AlarmManager) this.getSystemService(ALARM_SERVICE);
                am.setRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), AlarmManager.INTERVAL_DAY, pi);

//              Intent for check low storage





            }
            else{

            }
        }

        if(drivePrefs.getString("type",null).equals("GoogleDrive")){
            Log.i(APP_TAG,drivePrefs.getString("type",""));
        }

        DatabaseHandler db=DatabaseHandler.getDbInstance(context);

        int copiedTotal=db.getNumOfCopiedFiles();
        Log.i(APP_TAG,String.valueOf(copiedTotal));

//        DatabaseHandler ndb=DatabaseHandler.getDbInstance(context);
////        ArrayList al=ndb.getListOfFilesToBeDeleted();
////        String a=String.valueOf(al.size());
//        Log.i(APP_TAG,a);


        DatabaseHandler ndb= DatabaseHandler.getDbInstance(context);
//        int arr[]=ndb.getTypesAmountList(context);

    }
    //TODO: dummy method to create a list of files
    public ArrayList<String> getFiles() {
        ArrayList<String> fileList = new ArrayList<>();
        // Irfad A7 files
//        fileList.add("/storage/emulated/0/Documents/Batch 13 Student Details.xlsx");
//        fileList.add("/storage/emulated/0/DCIM/Facebook/FB_IMG_1502813011445.jpg");
        // Irfad Note 3 files
//        fileList.add("/storage/emulated/0/Download/Copy of pro pic.png");
//        fileList.add("/storage/emulated/0/DCIM/Camera/20170712_223552.jpg");
//        Kasun's files
//        fileList.add("/storage/emulated/0/Download/UoM-Virtual-Server-request-form-Final-Year-Projects.doc");
        fileList.add("/storage/emulated/0/DCIM/Camera/20170531_130539.jpg");
        fileList.add("/storage/emulated/0/DCIM/Camera/20170510_163111.mp4");
        fileList.add("/storage/emulated/0/Samsung/Music/Over the Horizon.mp3");
        return fileList;
    }



    private void setDriveAccount() {
        Log.e("Smart storge", "first run");
        CharSequence drivers[] = new CharSequence[]{"Google Drive", "DropBox"};
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("Pleas a choose a drive....")
                .setItems(drivers, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if (which == 0) {
                            Log.i(GOOGLE_DRIVE_TAG, "Connecting to Google Drive............");
                            googleDriveConnect();
                        } else if (which == 1) {
                            Log.i(DROP_BOX_TAG, "Connecting to DropBox............");
                            dropBoxConnect();
                        }
                    }
                });
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    private void dropBoxConnect() {
        // store app key and secret key
        AppKeyPair appKeys = new AppKeyPair(APP_KEY, APP_SECRET);
        AndroidAuthSession session = new AndroidAuthSession(appKeys);

        // Pass app key pair to the new DropboxAPI object.
        mDBApi = new DropboxAPI<AndroidAuthSession>(session);
        // MyActivity below should be your activity class name
        // start authentication.
        mDBApi.getSession().startOAuth2Authentication(MainActivity.this);
        drivePrefs.edit().putString("type", "DropBox").commit();
    }

    private void googleDriveConnect() {
        GoogleClientHandler.googleApiClient = new GoogleApiClient.Builder(this)
                .addApi(Drive.API)
                .addScope(Drive.SCOPE_FILE)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .build();
       GoogleClientHandler.googleApiClient.connect();
        drivePrefs.edit().putString("type", "GoogleDrive").commit();

    }


    @Override
    protected void onStop() {
        super.onStop();
//        if (mGoogleApiClient != null) {
//
//            // disconnect Google API client connection
//            mGoogleApiClient.disconnect();
//        }
//        super.onPause();
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
//TODO: change the settings action to switch between drive accounts
        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            Thread thread = new Thread(new Runnable() {

                @Override
                public void run() {
                    try  {

                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });

            thread.start();

        }
        else if(id==R.id.action_copyfile){
            Log.i("Settings","Deleting Files");
            Intent intent=new Intent();
            intent.setAction("com.smartStorage.deleteFile");
            ArrayList<String> strAL=new ArrayList<>();
            strAL.add("dddddddddd/sssss");
            strAL.add("df/gh/sssss");
            strAL.add("as/fg/hj/sssss");
            intent.putStringArrayListExtra("deletingList",strAL);
            sendBroadcast(intent);
        }
        else if(id==R.id.action_viewFilesDetails){
            Intent intent=new Intent(this,FilesActivity.class);
            startActivity(intent);
        }
        else if(id==R.id.action_viewPercentages){
            Intent intent=new Intent(this,FilesByTypeActivity.class);
            startActivity(intent);
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_camera) {
            // Handle the camera action
        } else if (id == R.id.nav_gallery) {

        } else if (id == R.id.nav_slideshow) {

        } else if (id == R.id.nav_manage) {

        } else if (id == R.id.nav_share) {

        } else if (id == R.id.nav_send) {

        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
    @Override
    protected void onPause(){
        super.onPause();
      //  unregisterReceiver(receiver);
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        Log.d(GOOGLE_DRIVE_TAG, "Connection failed");
        // Called whenever the API client fails to connect.
        Log.i(GOOGLE_DRIVE_TAG, "GoogleApiClient connection failed:" + connectionResult.toString());

        if (!connectionResult.hasResolution()) {

            // show the localized error dialog.
            GoogleApiAvailability.getInstance().getErrorDialog(this, connectionResult.getErrorCode(), 0).show();
            return;
        }

        /**
         *  The failure has a resolution. Resolve it.
         *  Called typically when the app is not yet authorized, and an  authorization
         *  dialog is displayed to the user.
         */

        try {

            connectionResult.startResolutionForResult(this, REQUEST_CODE_RESOLUTION);

        } catch (IntentSender.SendIntentException e) {

            Log.e(GOOGLE_DRIVE_TAG, "Exception while starting resolution activity&", e);
        }

    }

    @Override
    public void onConnected(Bundle bundle) {
        Query query =
                new Query.Builder().addFilter(Filters.and(Filters.eq(SearchableField.TITLE, "SmartApp"), Filters.eq(SearchableField.TRASHED, false)))
                        .build();
        Drive.DriveApi.query(GoogleClientHandler.googleApiClient, query).setResultCallback(new ResultCallback<DriveApi.MetadataBufferResult>() {
            @Override
            public void onResult(DriveApi.MetadataBufferResult result) {
                if (!result.getStatus().isSuccess()) {
                    Log.e(GOOGLE_DRIVE_TAG, "Cannot create folder in the root.");
                } else {
                    boolean isFound = false;
                    for (Metadata m : result.getMetadataBuffer()) {
                        if (m.getTitle().equals("SmartApp")) {
                            Log.e(GOOGLE_DRIVE_TAG, "Folder exists");
                            isFound = true;
                            GoogleClientHandler.driveId = m.getDriveId();
                            //create_file_in_folder(driveId);
                            break;
                        }
                    }
                    if (!isFound) {
                        Log.i(GOOGLE_DRIVE_TAG, "Folder not found; creating it.");
                        MetadataChangeSet changeSet = new MetadataChangeSet.Builder().setTitle("SmartApp").build();
                        Drive.DriveApi.getRootFolder(GoogleClientHandler.googleApiClient)
                                .createFolder(GoogleClientHandler.googleApiClient, changeSet)
                                .setResultCallback(new ResultCallback<DriveFolder.DriveFolderResult>() {
                                    @Override
                                    public void onResult(DriveFolder.DriveFolderResult result) {
                                        if (!result.getStatus().isSuccess()) {
                                            Log.e(GOOGLE_DRIVE_TAG, "U AR A MORON! Error while trying to create the folder");
                                        } else {
                                            Log.i(GOOGLE_DRIVE_TAG, "Created a folder");
                                            GoogleClientHandler.driveId = result.getDriveFolder().getDriveId();
//                                            create_file_in_folder(driveId);
                                        }
                                    }
                                });
                    }
                }
            }
        });
        Log.d(GOOGLE_DRIVE_TAG, "Connected");

    }

    @Override
    public void onConnectionSuspended(int i) {
        Log.i(GOOGLE_DRIVE_TAG, "suspended");

    }



    String fileName;





    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case REQUEST_CODE_ASK_MULTIPLE_PERMISSIONS:
                HashMap<String, Integer> permissionMap = new HashMap<>();
                for (int i = 0; i < permissions.length; i++) {
                    permissionMap.put(permissions[i], grantResults[i]);
                }
                Integer readExternalStoragePermission = permissionMap.get(Manifest.permission.READ_EXTERNAL_STORAGE);
                Integer getAccountsPermission = permissionMap.get(Manifest.permission.GET_ACCOUNTS);
                boolean criticalPermissionGranted = true;
                if (readExternalStoragePermission != null && readExternalStoragePermission != PackageManager.PERMISSION_GRANTED) {
                    criticalPermissionGranted = false;
                }
                if (getAccountsPermission != null && getAccountsPermission != PackageManager.PERMISSION_GRANTED) {
                    criticalPermissionGranted = false;
                }
                if (criticalPermissionGranted) {
                    setDriveAccount();
                    Intent serviceIntent = new Intent(getApplicationContext(), MigrationService.class);
                    startService(serviceIntent);
                } else {
                    new android.support.v7.app.AlertDialog.Builder(MainActivity.this)
                            .setMessage(Html.fromHtml("One or more required permission not granted. The app will not function correctly without the permission."))
                            .setPositiveButton("Open Settings", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    Intent intent = new Intent();
                                    intent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                                    Uri uri = Uri.fromParts("package", getPackageName(), null);
                                    intent.setData(uri);
                                    startActivity(intent);
                                }
                            })
                            .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.cancel();
                                }
                            })
                            .create()
                            .show();
                }
                break;
            default:
                super.onRequestPermissionsResult(requestCode, permissions, grantResults);
                break;
        }
    }

    private void requestRunTimePermission() {
        int readStoragePermission = ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE);
        int getAccountsPermission = ActivityCompat.checkSelfPermission(this, Manifest.permission.GET_ACCOUNTS);

        String alertMessage = "";
        ArrayList<String> permissionsNeeded = new ArrayList<>();
        if (readStoragePermission != PackageManager.PERMISSION_GRANTED) {
            permissionsNeeded.add(Manifest.permission.READ_EXTERNAL_STORAGE);
            alertMessage += "<p>&#8226; \"Read External Storage\" permission is required to manage storage <p>";
        }
        if (getAccountsPermission != PackageManager.PERMISSION_GRANTED) {
            permissionsNeeded.add(Manifest.permission.GET_ACCOUNTS);
            alertMessage += "<p>&#8226; \"Get Accounts\" permission is required to access drive accounts<p>";
        }
        if (!permissionsNeeded.isEmpty()) {
            final String[] reqPermissions = permissionsNeeded.toArray(new String[permissionsNeeded.size()]);
            if (getSharedPreferences(AppParams.PreferenceStr.SHARED_PREFERENCE_NAME, MODE_PRIVATE).getBoolean(AppParams.PreferenceStr.FIRST_RUN, true)) {
                new android.support.v7.app.AlertDialog.Builder(MainActivity.this)
                        .setMessage(Html.fromHtml(alertMessage))
                        .setPositiveButton("Grant Permission >", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                ActivityCompat.requestPermissions((Activity) context, reqPermissions
                                        , REQUEST_CODE_ASK_MULTIPLE_PERMISSIONS);
                            }
                        })
                        .create()
                        .show();
            } else
                ActivityCompat.requestPermissions(this, reqPermissions, REQUEST_CODE_ASK_MULTIPLE_PERMISSIONS);
        }


    }

//    static method to pass googleApiClient
    public static GoogleApiClient getGoogleApiClient() {
        return mGoogleApiClient;
    }

//    static method to pass driveId
    public static DriveId getDriveId(){
        return driveId;
    }

//    static method to pass dropbox api
    public static DropboxAPI getDropboxAPI(){
        return  mDBApi;
    }



}


