package com.smartstorage.mobile.display;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;

import com.smartstorage.mobile.R;

import java.util.ArrayList;
import java.util.List;

public class DeletedFilesActivity extends AppCompatActivity {
    private List<DeletedFile> deletedFileList=new ArrayList<>();
    private RecyclerView recyclerView;
    private DeletedFilesAdapter deletedFilesAdapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_deleted_files);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        recyclerView = (RecyclerView) findViewById(R.id.deleted_files_view);

        deletedFilesAdapter = new DeletedFilesAdapter(deletedFileList);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(deletedFilesAdapter);

        prepareMovieData();

    }

    private void prepareMovieData() {
        DeletedFile deletedFile=new DeletedFile("aaaaaaaaaaa","dddddddd",123.4);
        deletedFileList.add(deletedFile);
        deletedFile=new DeletedFile("aaaaaaaaaaa","dddddddd",153.4);
        deletedFileList.add(deletedFile);
        deletedFile=new DeletedFile("aaaaaaaaaaa","dddddddd",153.4);
        deletedFileList.add(deletedFile);
        deletedFile=new DeletedFile("aaaaaaaaaaa","dddddddd",153.4);
        deletedFileList.add(deletedFile);
        deletedFile=new DeletedFile("aaaaaaaaaaa","dddddddd",153.4);
        deletedFileList.add(deletedFile);
        deletedFile=new DeletedFile("aaaaaaaaaaa","dddddddd",153.4);
        deletedFileList.add(deletedFile);
        deletedFile=new DeletedFile("aaaaaaaaaaa","dddddddd",153.4);
        deletedFileList.add(deletedFile);
        deletedFile=new DeletedFile("aaaaaaaaaaa","dddddddd",153.4);
        deletedFileList.add(deletedFile);

        deletedFilesAdapter.notifyDataSetChanged();
    }

}
