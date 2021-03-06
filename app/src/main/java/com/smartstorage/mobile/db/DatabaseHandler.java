package com.smartstorage.mobile.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.util.ArrayList;

/**
 * Created by kasun on 8/15/17.
 */

public class DatabaseHandler extends SQLiteOpenHelper {

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

    private DatabaseHandler(Context context){
        super(context,DATABASE_NAME,null,DATABASE_VERSION);
    }

    public static synchronized DatabaseHandler getDbInstance(Context context){
        if(dbInstance==null){
            dbInstance=new DatabaseHandler(context.getApplicationContext());
        }
        return dbInstance;
    }
    @Override
    public void onCreate(SQLiteDatabase db) {
        String CREATE_FILE_DETAILS_TABLE = "CREATE TABLE " + TABLE_FILE_DETAILS + "("
            + KEY_ID + " INTEGER PRIMARY KEY," + KEY_FILE_NAME + " TEXT," + DRIVE_TYPE + " TEXT," + MIGRATION_VALUE + " REAL,"
                + KEY_FILE_LINK + " TEXT," + KEY_DELETED +" TEXT"+ ")";
        db.execSQL(CREATE_FILE_DETAILS_TABLE);


    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS" + TABLE_FILE_DETAILS);
        onCreate(db);

    }

//    adding new file detail
    public void addFileDetails(FileDetails fileDetails){
        SQLiteDatabase db=this.getWritableDatabase();

        ContentValues values=new ContentValues();
        values.put(KEY_FILE_NAME,fileDetails.getFile_name());
        values.put(KEY_FILE_LINK,fileDetails.getDrive_link());
        values.put(DRIVE_TYPE,fileDetails.getDrive_type());
        values.put(MIGRATION_VALUE,0.0);
        values.put(KEY_DELETED,"false");

        db.insert(TABLE_FILE_DETAILS,null,values);
        db.close();
    }

    public void addMultipleFileDetails(ArrayList<FileDetails> fileDetails){
        SQLiteDatabase db = this.getWritableDatabase();
        db.beginTransaction();
        try {
            ContentValues values = new ContentValues();
            for (FileDetails fileDetail: fileDetails) {
                values.put(KEY_FILE_NAME,fileDetail.getFile_name());
                values.put(KEY_FILE_LINK,fileDetail.getDrive_link());
                values.put(KEY_FILE_LINK,fileDetail.getDrive_link());
                values.put(DRIVE_TYPE,fileDetail.getDrive_type());
                values.put(MIGRATION_VALUE,0.0);
                values.put(KEY_DELETED,"false");
                db.insert(TABLE_FILE_DETAILS,null,values);
            }
            db.setTransactionSuccessful();
        } finally {
            db.endTransaction();
            db.close();
        }
    }

    public String getFileDetails(String drive_link){
        String SELECT_QUERY="SELECT * FROM " + TABLE_FILE_DETAILS ;
        SQLiteDatabase db=this.getReadableDatabase();
        Cursor cursor=db.rawQuery(SELECT_QUERY,null);
        cursor.moveToFirst();
        return String.valueOf(cursor.getString(1));
    }

    public void updateFileLink(String fileUrl,String file_link){
        ContentValues cv=new ContentValues();
        cv.put(KEY_FILE_LINK,file_link);
        cv.put(DRIVE_TYPE,"GoogleDrive");

        SQLiteDatabase db=this.getWritableDatabase();
        db.update(TABLE_FILE_DETAILS,cv,"file_name=?",new String[]{fileUrl});
    }

    public String getFileLink(String fileUrl){
        String SELECT_QUERY="SELECT * from "+TABLE_FILE_DETAILS+ " WHERE file_name=?";
        SQLiteDatabase db=this.getReadableDatabase();
        Cursor cursor=db.rawQuery(SELECT_QUERY,new String[]{fileUrl});
        cursor.moveToFirst();
        return cursor.getString(4);

    }

    //TODO: use below method to update the deleted state of files when they are removed programmatically.

    public void updateDeletedState(String fileUrl){
        ContentValues cv=new ContentValues();
        cv.put(KEY_DELETED,"True");

        SQLiteDatabase db=this.getWritableDatabase();
        db.update(TABLE_FILE_DETAILS,cv,"file_name=?",new String[]{fileUrl});

    }




}
