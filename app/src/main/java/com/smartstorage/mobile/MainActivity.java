package com.smartstorage.mobile;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.IntentSender;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.util.Log;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.webkit.MimeTypeMap;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.drive.Drive;
import com.google.android.gms.drive.DriveApi;
import com.google.android.gms.drive.DriveContents;
import com.google.android.gms.drive.DriveFile;
import com.google.android.gms.drive.DriveFolder;
import com.google.android.gms.drive.DriveId;
import com.google.android.gms.drive.Metadata;
import com.google.android.gms.drive.MetadataChangeSet;
import com.google.android.gms.drive.query.Filters;
import com.google.android.gms.drive.query.Query;
import com.google.android.gms.drive.query.SearchableField;
import com.smartstorage.mobile.db.DatabaseHandler;
import com.smartstorage.mobile.db.FileDetails;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener,GoogleApiClient.ConnectionCallbacks,GoogleApiClient.OnConnectionFailedListener {

    private static final String GOOGLE_DRIVE_TAG="Google Drive....:";
    private static final String DROP_BOX_TAG="DropBox....";
    private static final int REQUEST_CODE_RESOLUTION = 1;
    private static final  int REQUEST_CODE_OPENER = 2;
    private static GoogleApiClient mGoogleApiClient;
    final Context context=this;

    SharedPreferences prefs=null;
    DriveId driveId;
    String driveId_str;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        prefs=getSharedPreferences("com.smartstorage.mobile",MODE_PRIVATE);
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addApi(Drive.API)
                .addScope(Drive.SCOPE_FILE)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .build();
//        setDriveAccount();
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
    }
    @Override
    protected void onResume(){
        super.onResume();
//        TODO: only first run is checked.Must check the connectivity success/failure as well
        if(prefs.getBoolean("firstrun",true)){
            setDriveAccount();
            prefs.edit().putBoolean("firstrun",false).commit();

        }
        else{
            mGoogleApiClient.connect();
        }
//        setDriveAccount();

//        if (mGoogleApiClient == null) {
//
//            /**
//             * Create the API client and bind it to an instance variable.
//             * We use this instance as the callback for connection and connection failures.
//             * Since no account name is passed, the user is prompted to choose.
//             */
//            mGoogleApiClient = new GoogleApiClient.Builder(this)
//                    .addApi(Drive.API)
//                    .addScope(Drive.SCOPE_FILE)
//                    .addConnectionCallbacks(this)
//                    .addOnConnectionFailedListener(this)
//                    .build();
//        }
//
//        mGoogleApiClient.connect();
    }

    private void setDriveAccount(){
        Log.e("Smart storge","first run");
        CharSequence drivers[]=new CharSequence[]{"Google Drive","DropBox"};
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("Pleas a choose a drive....")
                .setItems(drivers, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if(which==0){
                            Log.i(GOOGLE_DRIVE_TAG,"Connecting to Google Drive............");
                            googleDriveConnect();
                        }
                        else if(which==1){
                            Log.i(DROP_BOX_TAG,"Connecting to DropBox............");
                            dropBoxConnect();
                        }
                    }
                });
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    private void dropBoxConnect() {

    }
    // Context context=getApplicationContext();
    private void googleDriveConnect(){
//        GoogleDriveActivity googleDriveActivity=GoogleDriveActivity.getInstance();
//        googleDriveActivity.connect(context);
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addApi(Drive.API)
                .addScope(Drive.SCOPE_FILE)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .build();
        mGoogleApiClient.connect();
    }


    @Override
    protected void onStop(){
        super.onStop();
        if (mGoogleApiClient != null) {

            // disconnect Google API client connection
            mGoogleApiClient.disconnect();
        }
        super.onPause();
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

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
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
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        Log.d(GOOGLE_DRIVE_TAG,"Connection failed");
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
        Drive.DriveApi.query(mGoogleApiClient, query).setResultCallback(new ResultCallback<DriveApi.MetadataBufferResult>() {
            @Override public void onResult(DriveApi.MetadataBufferResult result) {
                if (!result.getStatus().isSuccess()) {
                    Log.e(GOOGLE_DRIVE_TAG, "Cannot create folder in the root.");
                } else {
                    boolean isFound = false;
                    for (Metadata m : result.getMetadataBuffer()) {
                        if (m.getTitle().equals("SmartApp")) {
                            Log.e(GOOGLE_DRIVE_TAG, "Folder exists");
                            isFound = true;
                            driveId = m.getDriveId();
                            //create_file_in_folder(driveId);
                            break;
                        }
                    }
                    if (!isFound) {
                        Log.i(GOOGLE_DRIVE_TAG, "Folder not found; creating it.");
                        MetadataChangeSet changeSet = new MetadataChangeSet.Builder().setTitle("SmartApp").build();
                        Drive.DriveApi.getRootFolder(mGoogleApiClient)
                                .createFolder(mGoogleApiClient, changeSet)
                                .setResultCallback(new ResultCallback<DriveFolder.DriveFolderResult>() {
                                    @Override public void onResult(DriveFolder.DriveFolderResult result) {
                                        if (!result.getStatus().isSuccess()) {
                                            Log.e(GOOGLE_DRIVE_TAG, "U AR A MORON! Error while trying to create the folder");
                                        } else {
                                            Log.i(GOOGLE_DRIVE_TAG, "Created a folder");
                                            driveId = result.getDriveFolder().getDriveId();
//                                            create_file_in_folder(driveId);
                                        }
                                    }
                                });
                    }
                }
            }
        });
        Log.d(GOOGLE_DRIVE_TAG,"Connected");

    }

    @Override
    public void onConnectionSuspended(int i) {
        Log.i(GOOGLE_DRIVE_TAG,"suspended");

    }

    //TODO: dummy method to create a list of files
    public ArrayList<String> getFiles(){
        ArrayList<String> fileList=new ArrayList<>();
        fileList.add("/storage/emulated/0/DCIM/Screenshots/Screenshot_2017-08-08-18-29-01.png");
        fileList.add("/storage/emulated/0/Download/UoM-Virtual-Server-request-form-Final-Year-Projects.doc");
        fileList.add("/storage/emulated/0/Toucher/statistics/statistics/deviceId.txt");
        fileList.add("/storage/emulated/0/Samsung/Music/Over the Horizon.mp3");
        fileList.add("/storage/emulated/0/DCIM/Camera/20170531_130417.jpg");

        return fileList;
    }
    ArrayList<String> fileList=getFiles();

    public void copyFiles(View v){
        DatabaseHandler db=DatabaseHandler.getDbInstance(context);
        for(int i=0;i<fileList.size();i++) {
//  TODO : This must be removed.Writing to database must be done in onCreate,for all files in the app
            FileDetails fileDetails=new FileDetails(fileList.get(i),"no_link_yet","GoogleDrive");
            db.addFileDetails(fileDetails);
            copyFileToGoogleDrive(fileList.get(i));
        }
//        db.getFileDetails();




    }

    public void copyFileToGoogleDrive(final String fileUrl){
        Drive.DriveApi.newDriveContents(mGoogleApiClient).setResultCallback(new ResultCallback<DriveApi.DriveContentsResult>() {
            @Override
            public void onResult(@NonNull DriveApi.DriveContentsResult result) {
                 final DriveContents driveContents=result.getDriveContents();
                new Thread(){
                    @Override
                    public void run(){
                        // write content to DriveContents
                        OutputStream outputStream = driveContents.getOutputStream();
                        Uri resourceUri= Uri.fromFile(new File(fileUrl));

                        try {
                            InputStream inputStream=getContentResolver().openInputStream(resourceUri);
                            if (inputStream != null) {
                                byte[] data = new byte[1024];
                                while (inputStream.read(data) != -1) {
                                    outputStream.write(data);
                                }
                                inputStream.close();
                            }

                            outputStream.close();
                        } catch (IOException e) {
                            Log.e(GOOGLE_DRIVE_TAG, e.getMessage());
                        }
                        String extension= fileUrl.substring(fileUrl.indexOf(".")+1);
                        String fileType=MimeTypeMap.getSingleton().getMimeTypeFromExtension(extension);
                        Log.e(GOOGLE_DRIVE_TAG,fileType);
                        String[] arr= fileUrl.split("/");
                        String fileName=arr[arr.length-1].substring(0,arr[arr.length-1].indexOf("."));

                        MetadataChangeSet changeSet = new MetadataChangeSet.Builder()
                                .setTitle(fileName)
                                .setMimeType(fileType)
                                .setStarred(true).build();

                        // create a file in root folder
                        DriveFolder folder=driveId.asDriveFolder();
                        folder.createFile(mGoogleApiClient, changeSet, driveContents)
                                .setResultCallback(fileCallback);
                    }
                }.start();
            }
        });

    }
    private DriveId Did;
    final private ResultCallback<DriveFolder.DriveFileResult> fileCallback = new
            ResultCallback<DriveFolder.DriveFileResult>() {
                @Override
                public void onResult(DriveFolder.DriveFileResult result) {
                    if (result.getStatus().isSuccess()) {
                        Did=result.getDriveFile().getDriveId();
                        driveId_str=Did.encodeToString();
                        Log.e("Android exxx:",result.getDriveFile().getDriveId().toString());
                        Toast.makeText(getApplicationContext(), "file created:"+";"+
                                result.getDriveFile().getDriveId(), Toast.LENGTH_LONG).show();

                    }

                    return;

                }
            };


    public void downloadFiles(View v){
        DriveFile file=Drive.DriveApi.getFile(mGoogleApiClient,DriveId.decodeFromString(driveId_str));
        file.open(mGoogleApiClient,DriveFile.MODE_READ_ONLY,null).setResultCallback(contentsOpenedCallback);
    }
    ResultCallback<DriveApi.DriveContentsResult> contentsOpenedCallback =
            new ResultCallback<DriveApi.DriveContentsResult>() {
                @Override
                public void onResult(DriveApi.DriveContentsResult result) {
                    if (!result.getStatus().isSuccess()) {
                        Log.e("Error:","No such file");
                        return;
                    }
                    DriveContents contents = result.getDriveContents();
                    InputStream inputStream=contents.getInputStream();
                    try {
                        File targetFile=new File("/storage/emulated/0/Download/abcdefg.jpg");
                        OutputStream outputStream=new FileOutputStream(targetFile);
                        byte[] buffer = new byte[8 * 1024];
                        int bytesRead;
                        while ((bytesRead = inputStream.read(buffer)) != -1) {
                            outputStream.write(buffer, 0, bytesRead);
                        }
                        inputStream.close();
                        outputStream.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }


//                    BufferedReader reader = new BufferedReader(new InputStreamReader(contents.getInputStream()));
//                    StringBuilder builder = new StringBuilder();
//                    String line;
//                    try {
//                        while ((line = reader.readLine()) != null) {
//                            builder.append(line);
//                        }
//                    } catch (IOException e) {
//                        e.printStackTrace();
//                    }
//                    String contentsAsString = builder.toString();
//                    Log.e("RESULT:",contentsAsString);
                }
            };


}