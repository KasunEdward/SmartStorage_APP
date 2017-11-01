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

import com.smartstorage.mobile.R;
import com.smartstorage.mobile.db.DatabaseHandler;

import java.util.ArrayList;
import java.util.List;

public class DemoMigrationValActivity extends AppCompatActivity {

    private List<FileDetail> fileDetailList =new ArrayList<>();
    private RecyclerView recyclerView;
    private DemoMigrationFilesAdapter filesAdapter;
    private DatabaseHandler dbHandler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_demo_migration_val);

//        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
//        setSupportActionBar(toolbar);

        recyclerView = (RecyclerView) findViewById(R.id.migration_files_view);

        filesAdapter = new DemoMigrationFilesAdapter(fileDetailList);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(filesAdapter);

        getMigrationData();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu){
        getMenuInflater().inflate(R.menu.demo_migration_menu,menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.refresh_migration_val:
                Log.d("FILE MIGRATION", "refresh");
                getMigrationData();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void getMigrationData(){
        dbHandler = DatabaseHandler.getDbInstance(getApplicationContext());
        fileDetailList.clear();
        fileDetailList.addAll(dbHandler.getMigrationFilesForDemo());
        filesAdapter.notifyDataSetChanged();
    }

}
