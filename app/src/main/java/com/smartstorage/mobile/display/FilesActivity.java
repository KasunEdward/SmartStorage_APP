package com.smartstorage.mobile.display;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.smartstorage.mobile.R;
import com.smartstorage.mobile.db.DatabaseHandler;

import java.util.ArrayList;
import java.util.List;

public class FilesActivity extends AppCompatActivity {
    private List<FileDetail> fileDetailList =new ArrayList<>();
    private RecyclerView recyclerView;
    private FilesAdapter filesAdapter;
    private DatabaseHandler dbHandler;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_deleted_files);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        recyclerView = (RecyclerView) findViewById(R.id.deleted_files_view);

        filesAdapter = new FilesAdapter(fileDetailList);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(filesAdapter);

        prepareMovieData();

    }

    private void prepareMovieData() {
        dbHandler= DatabaseHandler.getDbInstance(this.getApplicationContext());
        ArrayList fileNames = dbHandler.getFilesToMigrate();
        ArrayList fileSizes = dbHandler.getAllFileSizes();
        FileDetail fileDetails;
        for (int i=0;i<fileNames.size();i++){
            String fileName = String.valueOf(fileNames.get(i));
//            fileName = fileName.substring(fileName.lastIndexOf('/')+1, fileName.length());
            double size = Double.valueOf(String.valueOf(fileSizes.get(i)));
            fileDetails =new FileDetail(fileName,"dddddddd",size,R.drawable.ic_file,false);
            fileDetailList.add(fileDetails);
            /*int begin = fileName.lastIndexOf('.');
            int end = fileName.length();
            String fileType = fileName.substring(begin,end);
            switch (fileType){
                case "mp3":
                    fileDetails =new FileDetail(fileName,"dddddddd",size,R.drawable.ic_mp3,false);
                    fileDetailList.add(fileDetails);
                    break;
                case "mp4":
                    fileDetails =new FileDetail(fileName,"dddddddd",size,R.drawable.ic_mp4,false);
                    fileDetailList.add(fileDetails);
                    break;
                case "pdf":
                    fileDetails =new FileDetail(fileName,"dddddddd",size,R.drawable.ic_pdf,false);
                    fileDetailList.add(fileDetails);
                    break;
                case "txt":
                    fileDetails =new FileDetail(fileName,"dddddddd",size,R.drawable.ic_doc,false);
                    fileDetailList.add(fileDetails);
                    break;
                case "jpg":
                    fileDetails =new FileDetail(fileName,"dddddddd",size,R.drawable.ic_jpg,false);
                    fileDetailList.add(fileDetails);
                    break;
                case "png":
                    fileDetails =new FileDetail(fileName,"dddddddd",size,R.drawable.ic_png,false);
                    fileDetailList.add(fileDetails);
                    break;
                default:
                    fileDetails =new FileDetail(fileName,"dddddddd",size,R.drawable.ic_file,false);
                    fileDetailList.add(fileDetails);
                    break;
            }*/
        }
        
        /*FileDetail fileDetail =new FileDetail("document 1","dddddddd",10.5,R.drawable.ic_doc,false);
        fileDetailList.add(fileDetail);
        fileDetail =new FileDetail("photo jpg","dddddddd",153.4,R.drawable.ic_jpg,false);
        fileDetailList.add(fileDetail);
        fileDetail =new FileDetail("music 1","dddddddd",200.2,R.drawable.ic_mp3,false);
        fileDetailList.add(fileDetail);
        fileDetail =new FileDetail("video 1","dddddddd",600.35,R.drawable.ic_mp4,false);
        fileDetailList.add(fileDetail);
        fileDetail =new FileDetail("document pdf 1","dddddddd",53.63,R.drawable.ic_pdf,false);
        fileDetailList.add(fileDetail);
        fileDetail =new FileDetail("document pdf 2","dddddddd",80.95,R.drawable.ic_pdf,false);
        fileDetailList.add(fileDetail);
        fileDetail =new FileDetail("music 2","dddddddd",163.23,R.drawable.ic_mp3,false);
        fileDetailList.add(fileDetail);
        fileDetail =new FileDetail("document 2","dddddddd",15.5,R.drawable.ic_doc,false);
        fileDetailList.add(fileDetail);
        fileDetail =new FileDetail("photo png","dddddddd",120.5,R.drawable.ic_png,false);
        fileDetailList.add(fileDetail);
*/
        filesAdapter.notifyDataSetChanged();
    }

/*    public void clickCheckBox(View v){
        Log.d("checkbox "," testing checkbox");
    }*/

    @Override
    public boolean onCreateOptionsMenu(Menu menu){
        getMenuInflater().inflate(R.menu.activity_files_list,menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.selectall:
                Log.d("file action","select all");
                filesAdapter.selectAll();
                return true;
            case R.id.copyfiles:
                Log.d("file action","Copy");
                filesAdapter.copyFiles();
                return true;
            case R.id.deletefiles:
                Log.d("file action","Delete");
                return true;
            case R.id.nevercopy:
                Log.d("file action","Never Copy");
                return true;
            case R.id.neverdelete:
                Log.d("file action","Never Delete");
                return true;
            case R.id.deselectall:
                Log.d("file action","Deselect All");
                filesAdapter.deSelectAll();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

}
