package com.smartstorage.mobile.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;
import android.webkit.MimeTypeMap;

import com.smartstorage.mobile.AppParams;
import com.smartstorage.mobile.R;
import com.smartstorage.mobile.display.FileDetail;

import java.io.File;
import java.util.ArrayList;
import java.util.Random;

/**
 * Created by kasun on 8/15/17.
 */

public class DatabaseHandler extends SQLiteOpenHelper {

    private static final String LOG_TAG = "SS_DatabaseHandler";
    private static DatabaseHandler dbInstance;

    private static final int DATABASE_VERSION = 1;
    private static final String DATABASE_NAME = "smartStorage";
    private static final String TABLE_FILE_DETAILS = "file_details";

    // file_details Table Columns names
    private static final String KEY_ID = "id";
    private static final String KEY_FILE_NAME = "file_name";
    private static final String DRIVE_TYPE = "drive_type";
    private static final String KEY_FILE_LINK = "file_link";
    private static final String MIGRATION_VALUE = "migration_value";
    private static final String KEY_DELETED = "deleted";
    private static final String KEY_SIZE = "size";
    private static final String KEY_NEVER_DELETE = "never_delete";
    private static final String KEY_NEVER_COPY = "never_copy";
    private static final String KEY_LAST_ACCESS = "last_access";

    private Context context;

    private DatabaseHandler(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        this.context = context;
    }

    public static synchronized DatabaseHandler getDbInstance(Context context) {
        if (dbInstance == null) {
            dbInstance = new DatabaseHandler(context.getApplicationContext());
        }
        return dbInstance;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String CREATE_FILE_DETAILS_TABLE = "CREATE TABLE " + TABLE_FILE_DETAILS + "("
                + KEY_ID + " INTEGER PRIMARY KEY," + KEY_FILE_NAME + " TEXT," + DRIVE_TYPE + " TEXT," + MIGRATION_VALUE + " REAL,"
                + KEY_FILE_LINK + " TEXT," + KEY_DELETED + " TEXT," + KEY_SIZE + " INTEGER," + KEY_NEVER_DELETE + " TEXT," + KEY_NEVER_COPY + " TEXT, "
                + KEY_LAST_ACCESS + " INTEGER " + " )";
        db.execSQL(CREATE_FILE_DETAILS_TABLE);


    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS" + TABLE_FILE_DETAILS);
        onCreate(db);

    }

    //    adding new file detail
    public void addFileDetails(FileDetails fileDetails) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(KEY_FILE_NAME, fileDetails.getFile_name());
        values.put(KEY_FILE_LINK, fileDetails.getDrive_link());
        values.put(DRIVE_TYPE, fileDetails.getDrive_type());
        if (fileDetails.getSize() == 0) {
            values.put(MIGRATION_VALUE, 0.0);
        } else {
            values.put(MIGRATION_VALUE, ((AppParams.MIGRATION_X / fileDetails.getSize()) * AppParams.MIGRATION_FACTOR)*10);
        }
        values.put(KEY_DELETED, "false");
        values.put(KEY_SIZE, fileDetails.getSize());
        values.put(KEY_LAST_ACCESS, fileDetails.getLast_accessed());
        db.insert(TABLE_FILE_DETAILS, null, values);
        //db.close();
    }

    public void addMultipleFileDetails(ArrayList<FileDetails> fileDetails) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.beginTransaction();
        ContentValues values = new ContentValues();
        for (FileDetails fileDetail : fileDetails) {
            if (fileDetail.getFile_name().startsWith("/storage/emulated/0/Download")) {
                Log.i(LOG_TAG, "In downloads");
            }
            values.put(KEY_FILE_NAME, fileDetail.getFile_name());
            values.put(KEY_FILE_LINK, fileDetail.getDrive_link());
            values.put(DRIVE_TYPE, fileDetail.getDrive_type());
            if (fileDetail.getSize() == 0) {
                values.put(MIGRATION_VALUE, 0.0);
            } else {
                values.put(MIGRATION_VALUE, ((AppParams.MIGRATION_X / fileDetail.getSize()) * AppParams.MIGRATION_FACTOR)*10);
            }
            values.put(KEY_DELETED, "false");
            values.put(KEY_SIZE, fileDetail.getSize());
            values.put(KEY_LAST_ACCESS, fileDetail.getLast_accessed());
            db.insert(TABLE_FILE_DETAILS, null, values);
        }
        db.setTransactionSuccessful();
        db.endTransaction();
        //db.close();
    }

    public String getFileDetails(String drive_link) {
        String SELECT_QUERY = "SELECT * FROM " + TABLE_FILE_DETAILS;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(SELECT_QUERY, null);
        cursor.moveToFirst();
        return String.valueOf(cursor.getString(1));
    }

    public String getFileId(String drive_link){
        String SELECT_QUERY="SELECT * FROM " + TABLE_FILE_DETAILS + " WHERE file_name=?";
        SQLiteDatabase db=this.getReadableDatabase();
        Cursor cursor=db.rawQuery(SELECT_QUERY,new String[]{drive_link});
        cursor.moveToFirst();
        return String.valueOf(cursor.getString(0));
    }

    public String getFileName(String fileID){
        String SELECT_QUERY="SELECT * FROM " + TABLE_FILE_DETAILS + " WHERE id=?";
        SQLiteDatabase db=this.getReadableDatabase();
        Cursor cursor=db.rawQuery(SELECT_QUERY,new String[]{fileID});
        cursor.moveToFirst();
        String fileName = String.valueOf(cursor.getString(1));
        fileName = fileName.substring(fileName.lastIndexOf("/")+1,fileName.length());
        return fileName;
    }

    public boolean isDeleted(String fileID){
        String SELECT_QUERY="SELECT * FROM " + TABLE_FILE_DETAILS + " WHERE file_name=?";
        SQLiteDatabase db=this.getReadableDatabase();
        Cursor cursor=db.rawQuery(SELECT_QUERY,new String[]{fileID /*"/storage/emulated/0/Prefetch/Pic1.jpg"*/});
        cursor.moveToFirst();

        String fileName = String.valueOf(cursor.getString(5));
        if(fileName.equals("True"))
            return true;
        return false;
    }

    public ArrayList getAllFileNames(){
        ArrayList filesNames=new ArrayList();
        String SELECT_QUERY="SELECT * FROM " + TABLE_FILE_DETAILS + " WHERE deleted=?";
        SQLiteDatabase db=this.getReadableDatabase();
        Cursor cursor=db.rawQuery(SELECT_QUERY, new String[]{"false"});
        cursor.moveToFirst();
        filesNames.add(cursor.getString(1));
        while(cursor.moveToNext()){
            filesNames.add(cursor.getString(1));
        }
        return filesNames;
    }

    public ArrayList getAllFileSizes(){
        ArrayList filesSizes=new ArrayList();
        String SELECT_QUERY="SELECT * FROM " + TABLE_FILE_DETAILS + " WHERE deleted=?";
        SQLiteDatabase db=this.getReadableDatabase();
        Cursor cursor=db.rawQuery(SELECT_QUERY,new String[]{"false"});
        cursor.moveToFirst();
        filesSizes.add(cursor.getString(6));
        while (cursor.moveToNext()) {
            filesSizes.add(cursor.getString(6));
        }
        //db.close();
        return filesSizes;
    }

    public void updateFileLink(String fileUrl, String file_link, String drive_type) {
        ContentValues cv = new ContentValues();
        cv.put(KEY_FILE_LINK, file_link);
        cv.put(DRIVE_TYPE, drive_type);

        SQLiteDatabase db = this.getWritableDatabase();
        db.update(TABLE_FILE_DETAILS, cv, "file_name=?", new String[]{fileUrl});
    }

    public String getFileLink(String fileUrl) {
        String SELECT_QUERY = "SELECT * from " + TABLE_FILE_DETAILS + " WHERE file_name=?";
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(SELECT_QUERY, new String[]{fileUrl});
        cursor.moveToFirst();
        return cursor.getString(4);

    }

    //TODO: use below method to update the deleted state of files when they are removed programmatically.

    public void updateDeletedState(String fileUrl) {
        ContentValues cv = new ContentValues();
        cv.put(KEY_DELETED, "True");

        SQLiteDatabase db=this.getWritableDatabase();
        db.update(TABLE_FILE_DETAILS,cv,"file_name=?",new String[]{fileUrl});

    }
    public void updateDeletedStateToFalse(String fileUrl){
        ContentValues cv=new ContentValues();
        cv.put(KEY_DELETED,"False");

        SQLiteDatabase db=this.getWritableDatabase();
        db.update(TABLE_FILE_DETAILS,cv,"file_name=?",new String[]{fileUrl});

    }

    public int getNumOfTotalFiles() {
        String SELECT_QUERY = "SELECT * from " + TABLE_FILE_DETAILS;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(SELECT_QUERY, null);
        cursor.moveToFirst();
        int total = cursor.getCount();
        //db.close();
        return total;

    }

    public int getNumOfCopiedFiles() {
        String SELECT_QUERY = "SELECT * from " + TABLE_FILE_DETAILS + " WHERE " + KEY_FILE_LINK + "!='no_link_yet'";
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(SELECT_QUERY, null);
        cursor.moveToFirst();
        int total = cursor.getCount();
        return total;
    }

    public int[] getTypesAmountList(Context context) {
        int arr[] = {0, 0, 0, 0};
        String SELECT_QUERY = "SELECT * from " + TABLE_FILE_DETAILS;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(SELECT_QUERY, null);
        cursor.moveToFirst();
        while (cursor.moveToNext()) {
            String type = "other";
            String extension = MimeTypeMap.getFileExtensionFromUrl(cursor.getString(1));
            if (extension != null) {
                type = MimeTypeMap.getSingleton().getMimeTypeFromExtension(extension);
//                if(type_before!=null){
//                    String[] type_arr=type_before.split("/");
//                    type=type_arr[0];
//                }

            }
            if (type == null) {
                type = "other";
            }
            Log.i("db_test", type);
        }
        return arr;

    }

    public ArrayList getListOfFilesToBeDeleted() {
        ArrayList filesList = new ArrayList();
        String SELECT_QUERY = "select * from file_details where file_link !='no_link_yet' and deleted ='false'";
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(SELECT_QUERY, null);
        if(cursor!=null&&cursor.moveToFirst()){
            filesList.add(cursor.getString(1));
            while (cursor.moveToNext()) {
                filesList.add(cursor.getString(1));
            }
        }
//        filesList.add(cursor.getString(1));
//        while (cursor.moveToNext()) {
//            filesList.add(cursor.getString(1));
//        }
        return filesList;

    }

    public void updateMigrationValue(FileDetails fileDetails) {
        ContentValues cv = new ContentValues();
        cv.put(MIGRATION_VALUE, fileDetails.getMigration_value());
        cv.put(KEY_LAST_ACCESS, fileDetails.getLast_accessed());
        SQLiteDatabase db = this.getWritableDatabase();
        db.update(TABLE_FILE_DETAILS, cv, KEY_ID + "=?", new String[]{"" + fileDetails.getId()});
    }

    public ArrayList<String> getFilesToMigrate() {
        ArrayList<String> fileToMigrate = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        String query = "SELECT " + KEY_FILE_NAME + " FROM " + TABLE_FILE_DETAILS + " WHERE " + MIGRATION_VALUE + " < " + AppParams.MIGRATION_THRESHOLD + " AND NOT " + MIGRATION_VALUE + " = 0 AND " + KEY_SIZE + " < 20000000 AND "+KEY_NEVER_DELETE+" = 'false' AND "+KEY_NEVER_COPY+" = 'false'";
        Cursor cursor = db.rawQuery(query, null);
        if (cursor.moveToFirst()) {
            fileToMigrate.add(cursor.getString(0));
            while (cursor.moveToNext()) {
                fileToMigrate.add(cursor.getString(0));
            }
        }
        //db.close();
        Log.e(LOG_TAG, "num of files to migrate" + fileToMigrate.size());
        return fileToMigrate;
    }

    public void updateNeverDeleteStatus(String fileUrl) {
        ContentValues cv = new ContentValues();
        cv.put(KEY_NEVER_DELETE, "TRUE");

        SQLiteDatabase db = this.getWritableDatabase();
        db.update(TABLE_FILE_DETAILS, cv, "file_name=?", new String[]{fileUrl});
    }

    public void updateNeverCopyStatus(String fileUrl){
        ContentValues cv=new ContentValues();
        cv.put(KEY_NEVER_COPY,"TRUE");

        SQLiteDatabase db=this.getWritableDatabase();
        db.update(TABLE_FILE_DETAILS,cv,"file_name=?",new String[]{fileUrl});
    }
    public ArrayList<FileDetail> getMigrationFilesForDemo() {
        ArrayList<FileDetail> migrationFiles = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        String query = "SELECT " + KEY_FILE_NAME + "," + KEY_SIZE + "," + MIGRATION_VALUE + " FROM " + TABLE_FILE_DETAILS + " WHERE LOWER(" + KEY_FILE_NAME + ") LIKE '%/download/%' OR LOWER(" + KEY_FILE_NAME + ") LIKE '%/demo/%'";
        Cursor cursor = db.rawQuery(query, null);
        if (cursor.moveToFirst()) {
            String url = cursor.getString(0);
            double size = cursor.getDouble(1);
            double migration_val = cursor.getDouble(2);
            FileDetail fileDetail = new FileDetail(url, null, size, R.drawable.ic_file, false);
            fileDetail.setMigration_value(migration_val);
            migrationFiles.add(fileDetail);
            while (cursor.moveToNext()) {
                url = cursor.getString(0);
                size = cursor.getDouble(1);
                migration_val = cursor.getDouble(2);
                fileDetail = new FileDetail(url, null, size, R.drawable.ic_file, false);
                fileDetail.setMigration_value(migration_val);
                migrationFiles.add(fileDetail);
            }
        }
        //db.close();
        return migrationFiles;
    }

    public ArrayList<FileDetails> getFileDetails(String basePath, String[] children) {
        ArrayList<FileDetails> fileDetails = new ArrayList<>();
        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < children.length; i++) {
            children[i] = basePath + File.separator + children[i];
            builder.append("?, ");
        }
        if (builder.length() > 2) {
            builder.delete(builder.length() - 2, builder.length());
        } else {
            Log.e("ERRROR", "builder error" + builder.toString());
        }

        SQLiteDatabase db = this.getReadableDatabase();
        String query = "SELECT * FROM " + TABLE_FILE_DETAILS + " WHERE " + KEY_FILE_NAME + " IN (" + builder.toString() + ")";
        Cursor cursor = db.rawQuery(query, children);
        if (cursor.moveToFirst()) {
            int fileID = cursor.getInt(0);
            String url = cursor.getString(1);
            String drive_type = cursor.getString(2);
            double migration_val = cursor.getDouble(3);
            String driveLink = cursor.getString(4);
            long size = cursor.getLong(6);
            long last_accessed = cursor.getLong(9);
            FileDetails details = new FileDetails(url, driveLink, drive_type, size);
            details.setId(fileID);
            details.setLast_accessed(last_accessed);
            details.setMigration_value(migration_val);
            fileDetails.add(details);

            while (cursor.moveToNext()) {
                fileID = cursor.getInt(0);
                url = cursor.getString(1);
                drive_type = cursor.getString(2);
                migration_val = cursor.getDouble(3);
                driveLink = cursor.getString(4);
                size = cursor.getLong(6);
                last_accessed = cursor.getLong(9);
                details = new FileDetails(url, driveLink, drive_type, size);
                details.setId(fileID);
                details.setLast_accessed(last_accessed);
                details.setMigration_value(migration_val);
                fileDetails.add(details);
            }
        }
        //db.close();
        return fileDetails;
    }

    public void demoDecreaseMigration(double val, String path){
        ContentValues cv = new ContentValues();
        cv.put(MIGRATION_VALUE, val);
        SQLiteDatabase db = this.getWritableDatabase();
        db.update(TABLE_FILE_DETAILS, cv, "file_name=?", new String[]{path});
    }

    public void demoSimulateMigration(){
        SQLiteDatabase db = getWritableDatabase();
        String sql = "SELECT * FROM " + TABLE_FILE_DETAILS + " WHERE file_name like '/storage/emulated/0/Demo/%' OR file_name like '/storage/emulated/0/Download/%'";
        Cursor cursor = db.rawQuery(sql, null);
        if (cursor.moveToFirst()) {
            String fileName = cursor.getString(1);
            double migration_val = cursor.getDouble(3);
            long size = cursor.getLong(6);
            if (fileName.equals("/storage/emulated/0/Demo/Mini Case Study II.pdf")){
                migration_val +=  (AppParams.MIGRATION_X / size) * AppParams.MIGRATION_FACTOR;
                ContentValues cv = new ContentValues();
                cv.put(MIGRATION_VALUE, migration_val);
                db.update(TABLE_FILE_DETAILS, cv, "file_name=?", new String[]{fileName});
            }else{
                migration_val -=  (AppParams.MIGRATION_X / size) * AppParams.MIGRATION_FACTOR;
                ContentValues cv = new ContentValues();
                cv.put(MIGRATION_VALUE, migration_val);
                db.update(TABLE_FILE_DETAILS, cv, "file_name=?", new String[]{fileName});
            }
            while (cursor.moveToNext()){
                fileName = cursor.getString(1);
                migration_val = cursor.getDouble(3);
                size = cursor.getLong(6);
                if (fileName.equals("/storage/emulated/0/Demo/Mini Case Study II.pdf")){
                    migration_val +=  (AppParams.MIGRATION_X / size) * AppParams.MIGRATION_FACTOR;
                    ContentValues cv = new ContentValues();
                    cv.put(MIGRATION_VALUE, migration_val);
                    db.update(TABLE_FILE_DETAILS, cv, "file_name=?", new String[]{fileName});
                }else{
                    migration_val -=  (AppParams.MIGRATION_X / size) * AppParams.MIGRATION_FACTOR;
                    ContentValues cv = new ContentValues();
                    cv.put(MIGRATION_VALUE, migration_val);
                    db.update(TABLE_FILE_DETAILS, cv, "file_name=?", new String[]{fileName});
                }
            }
        }
    }

    public FileDetails[] getPredictedFileNames(String path, String filename){
        FileDetails[] filenames=new FileDetails[4];
        String SELECT_QUERY = "SELECT * FROM " + TABLE_FILE_DETAILS + " WHERE file_name like ? and not file_name = ?";
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(SELECT_QUERY, new String[]{path+"%",filename});
        cursor.moveToFirst();
        Random random = new Random();
        int fileLimit = 2 + random.nextInt(2);
        if(cursor.getCount() >0 && cursor.getCount()<= 10){
            filenames[0] = new FileDetails();
            filenames[0].setFile_name(cursor.getString(1));
            filenames[0].setDeleted(cursor.getString(5));
            for (int i=1; i<fileLimit;i++){
                if(cursor.moveToNext()){
                    filenames[i] = new FileDetails();
                    filenames[i].setFile_name(cursor.getString(1));
                    filenames[i].setDeleted(cursor.getString(5));
                }
            }
        } else {
            if (cursor.getCount() > 10) {
                ArrayList<FileDetails> fileList = new ArrayList<>();
                while (cursor.moveToNext()) {
                    FileDetails fileDetails = new FileDetails();
                    fileDetails.setFile_name(cursor.getString(1));
                    fileDetails.setDeleted(cursor.getString(5));
                    fileList.add(fileDetails);
                }
                for(int i=0; i<fileLimit;i++){
                    filenames[i]= fileList.get(random.nextInt(fileList.size()));
                }
            }
        }
        int mapfilesCount = context.getSharedPreferences(AppParams.PreferenceStr.SHARED_PREFERENCE_NAME, Context.MODE_PRIVATE)
                .getInt(AppParams.PreferenceStr.FILE_MAP_LENGTH, 500);
        Log.e(LOG_TAG, "mapfilescount:" + mapfilesCount);
        String ids = "(";
        for (int i=fileLimit; i<3; i++){
            ids += random.nextInt(mapfilesCount) + ",";
        }
        ids += random.nextInt(mapfilesCount) + ")";
        SELECT_QUERY = "SELECT * FROM " + TABLE_FILE_DETAILS + " WHERE id in " + ids;
        cursor = db.rawQuery(SELECT_QUERY, null);
        cursor.moveToFirst();
        Log.e(LOG_TAG, "IDs:" + ids + " Count:" + cursor.getCount()+ " limit:" + fileLimit);
        for (int i=fileLimit; i<4; i++){
            filenames[i] = new FileDetails();
            filenames[i].setFile_name(cursor.getString(1));
            filenames[i].setDeleted(cursor.getString(5));
            cursor.moveToNext();
        }
        return filenames;
    }

}
