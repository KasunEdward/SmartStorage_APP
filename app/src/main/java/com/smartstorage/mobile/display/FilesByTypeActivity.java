package com.smartstorage.mobile.display;

import android.graphics.Color;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;

import com.smartstorage.mobile.R;

import org.eazegraph.lib.charts.PieChart;
import org.eazegraph.lib.models.PieModel;

public class FilesByTypeActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_files_by_type);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        PieChart mPieChart = (PieChart) findViewById(R.id.piechart);

        mPieChart.addPieSlice(new PieModel("Images", 15, Color.parseColor("#133926")));
        mPieChart.addPieSlice(new PieModel("Audio", 25, Color.parseColor("#26734d")));
        mPieChart.addPieSlice(new PieModel("Video", 35, Color.parseColor("#40bf80")));
        mPieChart.addPieSlice(new PieModel("Other", 9, Color.parseColor("#b3e6cc")));

        mPieChart.startAnimation();

    }

}
